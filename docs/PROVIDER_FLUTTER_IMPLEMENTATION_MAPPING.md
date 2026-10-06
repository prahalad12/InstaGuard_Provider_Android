# PROVIDER_FLUTTER_IMPLEMENTATION_MAPPING.md — InstaGuard Provider

How to reproduce the Native Android provider app in Flutter (Provider + ChangeNotifier + Dio + Repository pattern + SharedPreferences). **This document maps architecture only — it does not contain Flutter code.**

## 1. Architecture mapping

| Android Native | Flutter |
|---|---|
| Activity | Screen (route / `MaterialPageRoute`) |
| Fragment | Screen or Widget |
| ViewModel *(none — logic lives in Activities/Fragments)* | `ChangeNotifier` Controller |
| Repository *(none — direct Retrofit calls)* | Repository class |
| Retrofit `APIInterface` | Dio-based `ApiService` |
| `APIClient` (OkHttp + interceptor) | Dio `BaseOptions` + `Interceptor` |
| Gson model | Dart model (`fromJson`/`toJson`) |
| `SharedPreferences` (`Utils.MY_PREFS_NAME`) | `shared_preferences` |
| `LiveData` / `ValueEventListener` | `ChangeNotifier` / Firebase `Stream` |
| RecyclerView `Adapter` | `ListView.builder` |
| `Intent` + `Utils.switchContent` | `Navigator` / named routes |
| Firebase Realtime DB + GeoFire | `firebase_database` + `geoflutterfire` |
| FCM `MyFirebaseMessagingService` | `firebase_messaging` |
| Twilio Voice/Conversations | `twilio_voice` / Twilio Conversations SDK (or plugin) |
| Stripe (WebView checkout) | `webview_flutter` + backend Checkout URL |
| Google Maps | `google_maps_flutter` |

> **Important:** the Native app has **no ViewModel/Repository layer** — networking is called directly inside Activities/Fragments, and success is decided by matching response `message` strings. In Flutter you should introduce the missing Repository + Controller layers, but keep the **exact endpoints, field names, and message-string gates**.

## 2. Suggested Flutter module structure

```
lib/
  core/
    api_client.dart          // Dio + auth interceptor (Bearer) + 401 refresh
    constants.dart           // base URL, SP keys, Firebase paths (mirror Utils.java)
    firebase_paths.dart      // providers/{id}/trips/service_status etc.
  models/                    // one Dart model per Gson model
  repositories/
    auth_repository.dart
    booking_repository.dart  // requestService, jobs lists, removeDriver, cancel
    availability_repository.dart
    profile_repository.dart
    finance_repository.dart
    subscription_repository.dart
  controllers/               // ChangeNotifier
    auth_controller.dart
    dispatch_controller.dart // listens to Firebase service_status → drives UI
    trip_controller.dart     // accept/start/reach/begin/end state machine
    jobs_controller.dart
    availability_controller.dart
    profile_controller.dart
    finance_controller.dart
  screens/                   // one per Fragment/Activity
  services/
    firebase_dispatch_service.dart // the RequestServices equivalent
    fcm_service.dart
    location_service.dart
```

## 3. Module-by-module rebuild notes

### 3.1 Auth (`auth_repository` / `auth_controller`)
- Endpoints: `auth/logind?`, `auth/registerDriver?`, `emailAvailable?`, `forgetpass`, `verifyotp`, `updatePassword?`, `auth/refreshd?`, `driver/socialAdd`, `driver/socialUpdate`, `auth/logoutd?`, `auth/passwordChangeDriver?`.
- Persist: `accessToken`, `tokenType`, `tokenExpTime`, `driverId`, `loginBoolean`. On login success also seed the `providers/{driverId}` Firebase node (register does this in native).
- Dio interceptor: on `401` call `auth/refreshd?`, re-save token, retry; second `401` → force logout.

### 3.2 Dispatch (`firebase_dispatch_service` / `dispatch_controller`) — **critical**
- Subscribe to `providers/{driverId}/trips/service_status` (`firebase_database` `onValue` stream) — replaces `RequestServices`.
- On `"1"`: read the full `providers/{driverId}/trips` snapshot into a `BookingRequest` model (fields in `PROVIDER_BOOKING_FLOW.md` §3.3), start a countdown (`appsetting.timedelay_accept`, default 30 s), play a tone, show the request screen.
- Also stream `users/{customer_id}` for `wallet` + `rating` + `trips/accept_status`.
- Map `service_status` values (`0,1,2,3,4,5,5.1,5.2`) to trip-state UI exactly as `callTripStatusFB`.

### 3.3 Trip (`booking_repository` / `trip_controller`)
- Accept → `requestService?` `{mode:"accept",...}`; capture `booking_id`.
- Transitions → `requestService?` with `mode` `start_driver`/`reached`/`start_service`.
- Complete → `requestService?` `{mode:"finish_service",...}` then `stripePayment`/`cashPayment`, then `DriverFeedbackStore?`.
- Reject → set Firebase `service_status="0"` + `removeDriver`.
- Cancel (in-progress) → `cancelJobs?` with reason.
- Success gates by exact message strings (`"Request saved successfully."`, `"Cancelled successfully."`, etc.).

### 3.4 Jobs lists (`jobs_controller`)
- `pendingJobs?` / `upcomingJobs?` / `pastjobs?`; actions `scheduleJobAccept?`, `scheduledcancelJobs?`, `scheduletripStart?`.
- Status integers 6/7/8/9/13/14/15 for labels/colors.

### 3.5 Availability (`availability_controller`)
- Per-date slots: `viewDriverDateSlot`/`updateDriverSlot`/`viewDriverSlots` (slot ids 1–24).
- Online toggle + location: `checkin?` (`status` "1"/"0") + write GeoFire location to `drivers_location/{driverId}`.

### 3.6 Profile / Documents / Services
- `profileViewDriver?`, `updateprofileDriver?`, `picuploadDriver?` (**Base64**, not multipart), category (`CategoriesDriver?`/`ServiceDriver?`/`ServiceSelection?`), documents (`manage_document`/`imageupload_e`/`document_update_e`/`proofreupload_e`, Base64), emergency contacts.

### 3.7 Finance / Subscription
- Earnings from `profileViewDriver?`; wallet `TransactionDriver?`; bonus `BonusTransactionList?`; payout `driverPayoutRequest`/`payout_history`; bank via Stripe onboarding WebView (`stripe/create_account`/`bankDetailsGet?`); subscription checkout WebView (`createCheckoutSession?`) with result from Firebase `providers/{id}/subs_buy_status`.

### 3.8 Notifications / Chat / Support
- FCM for generic + Twilio voice invites. In-app chat via Twilio Conversations (channel `Channel:{userId},{driverId},{bookingId}`), tokens from `chatAccessToken`/`accesstoken`. Support pages via `pages?`/`Support?`. Offers/Announcements from Firebase `Notifications/Offers|Announcements/provider`.

## 4. Constants to port verbatim (from `Utils.java`)
- Base URL `https://backend.insta-guard.co.uk/api/v3/`
- Firebase headers: `providers`, `users`, `trips`, `service_status`, `accept_status`, `backup_request`, `transfer_request`, `Notifications/Offers/provider`, `Notifications/Announcements/provider`.
- SP keys: `accessToken`, `tokenType`, `tokenExpTime`, `..._userId`, `..._boolean`, `regid`, currency/wallet keys.
- Slot arrays: `dayArray` (note misspelling `Saterday`), `timeArray` (24 hourly labels ↔ slot ids 1..24).
- Request timeout default `countDownTimeLimit = 30`.

## 5. Behaviors to preserve
1. **Firebase is the dispatch channel** — do not attempt a REST "get new bookings" poll; there isn't one.
2. **Message-string success gates** — replicate exact strings.
3. **Base64 image/document uploads** in form-encoded bodies.
4. **Two status spaces** — Firebase live `service_status` (0..5.2) vs REST list integers (6..15).
5. **Reject sends `removeDriver`**, timeout behaves identically; no reason for on-demand reject; reason required only for in-progress `cancelJobs?`.
6. **`user_id` field actually carries the driver id** in job-list calls; `customer_id`/`from_id`/`to_id` map user↔provider carefully.
