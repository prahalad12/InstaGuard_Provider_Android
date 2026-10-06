# InstaGuard Provider — Functionality Documentation

## Overview

**InstaGuard Provider** is the Android driver/guard-side application for the InstaGuard platform — a security-guard booking and management service based in the UK. Guards (referred to as "providers" or "drivers" in code) use this app to receive job requests, manage their schedule, track earnings, handle documents, and communicate with clients.

- **Package**: `com.instaguard.provider`
- **Version**: 1.3 (versionCode 4)
- **Min SDK**: 25 (Android 7.1 Nougat)
- **Target SDK**: 35 (Android 15)
- **Backend**: `https://backend.insta-guard.co.uk/api/v3/`
- **Languages supported**: English, Arabic, Finnish, Sinhala

---

## Core Features

### 1. Authentication

| Feature | Details |
|---|---|
| Email / Password Login | Standard credential login via `LoginFragment` |
| Registration | New guard account creation via `RegisterFragment` (email, mobile, name, city) |
| Social Login | Google Sign-In, Facebook Login, LinkedIn OAuth via `HomeActivity` |
| Social Signup Completion | `SocialSignupFragment` collects missing profile fields after social login |
| Forgot Password | OTP-based password reset flow via `ForgotPasswordFragment` |
| OTP Verification | Mobile number verification via SMS OTP |
| Remember Me | Credentials persisted in `SharedPreferences` |
| Token Refresh | JWT access token auto-refresh (`auth/refreshd`) |
| Logout | Clears local session and Firebase presence |
| Delete Account | Two-step deletion: request + confirm (`delete_account_request` / `delete_account_confirm`) |

### 2. Biometric Authentication

- Fingerprint and Face unlock presented at app launch via `LaunchActivity`
- Uses `androidx.biometric.BiometricPrompt`
- Settings managed in `SettingsFaceFingerscreen`
- Biometric status stored in `SharedPreferences` per user

### 3. Real-time Job Management

Accessed via `YourJobsFragment` (tabbed view):

#### Pending Jobs (`YourJobsPendingFragment`)
- Lists incoming job requests from clients
- Guard can **Accept** (`scheduleJobAccept`) or **Decline** (`scheduledcancelJobs`) each request

#### Upcoming Jobs (`YourJobsUpcomingFragment`)
- Lists confirmed/scheduled jobs
- Guard can **Start** a job (`scheduletripStart`) or **Cancel** (`cancelJobs`)

#### Past Jobs (`YourJobsPastFragment`)
- Full history of completed jobs
- Displays job date, time, client info, fare, and status

#### On-demand Jobs (live requests via `MainActivity`)
- Real-time job requests arrive via Firebase Realtime Database and FCM push notifications
- Guard has a countdown timer to Accept or Decline incoming requests
- On accept: live location tracking begins, map shows route to client
- Job states: Accepted → Started → Completed
- End-of-job triggers rating prompt and payment update

### 4. Availability Scheduling

| Screen | Purpose |
|---|---|
| `MyAvailabilityFragment` | Weekly recurring schedule — set available days and time slots |
| `MyAvailabilityNewFragment` | Date-specific availability slots |
| `MyAvailabilitySubFragment` | Sub-time slot management within a day |
| `MyCalenderBookingFragment` | Calendar WebView showing all bookings (`/calender/driver/`) |

API endpoints: `ServiceSelectionGet`, `ServiceSelection`, `viewDriverSlots`, `viewDriverDateSlot`, `updateDriverSlot`

### 5. Service / Category Management

- `ManageServiceFragment` — guard selects which service categories they offer (e.g., Event Security, Personal Protection)
- `SubCategoryFragment` — selects sub-categories within a service
- API: `CategoriesDriver`, `AllServicesDriver`, `ServiceDriver`, `ServiceSelection`

### 6. Location Services

- **FusedLocation** singleton (`FusedLocationSingleton`) for GPS fixes
- **Background location** via `LocationUpdatesService` (foreground service)
- Guard location streamed to Firebase Realtime Database under `providers/{id}`
- **GeoFire** used for proximity queries between clients and guards
- **Geofencing** (`GeofenceHelper` + `GeofenceBroadcastReceiver`): triggers push notification when guard enters/exits a defined radius (50 m, 90-second expiry)
- **Distance Matrix API** used for ETA and distance calculation
- **Google Maps SDK** renders live map with route overlay in `MainActivity`

### 7. Wallet & Earnings

| Screen | Purpose |
|---|---|
| `MyWalletFragment` | Wallet balance overview |
| `EarningsFragment` | Earnings summary |
| `YourWalletTransactionFragment` | Transaction tabs (All / Money In / Money Out) |
| `YourWalletTransAllFragment` | All wallet transactions |
| `YourWalletTransMoneyInFragment` | Credits only |
| `YourWalletTransMoneyOutFragment` | Debits only |
| `YourWalletTransAllBonusFragment` | Bonus credit history |

API: `TransactionDriver`, `BonusTransactionList`, `driverAddMoney`

### 8. Payouts

- `PayoutFragment` / `PayoutsFragment` — request a payout to bank account
- `PayoutTransctionsFragment` — history of payout requests
- Stripe Connect bank account creation via `stripe/create_account`
- `StripePaymentFragment` handles Stripe card input
- API: `driverPayoutRequest`, `payout_history`

### 9. Payment Processing

- Cash payment confirmation: `cashPayment`
- Stripe end-of-trip payment: `stripePayment`
- `PaymentFragment` for payment method selection

### 10. Document Management

| Feature | Details |
|---|---|
| Upload Documents | `ManageDocumentsFragment` — photo upload for ID, licenses, certificates |
| View Documents | `ManageViewDocumentsFragment` — view submitted documents |
| Re-upload | Re-submit rejected documents (`proofreupload`) |
| Document Activity | `DocumentActivity` — full-screen document viewer |
| Status | Documents can be pending/approved/rejected (displayed in list) |

API: `documentUpload`, `proofreupload`, `viewdocument`, `manage_document`, `imageupload_e`, `document_update_e`

### 11. Profile & Settings

- `SettingsFragment` — view profile, navigate to sub-settings
- `EditSettingsFragment` — edit name, email, phone, city, address
- Profile photo upload (`picuploadDriver`) with image compression
- Change password (`auth/passwordChangeDriver`)
- City selection from server-side list (`cities`)

### 12. Bank Details

- `BankDetailFragment` / `BankDetailsFragmentNew` — view and update bank account info
- API: `bankDetailsGet`, `bankDetailsUpdate`
- Stripe Connect account creation for payouts

### 13. Emergency Contacts

- `EmergencyContactsFragment` — add, view, and delete emergency contacts
- API: `emergencyContactDriver`, `emergencyContactGETDriver`, `emergencyContactDeleteDriver`

### 14. In-App Communication (Twilio)

- **Voice calls**: Twilio Voice SDK 4.4 — `doCallAccessToken` fetches token
- **Chat/messaging**: Twilio Conversations SDK 6.0 — `doChatAccessToken` fetches token
- Message notifications sent via `message_notify` API
- `FloatingWidgetService` shows an overlay call/chat widget during active trips

### 15. Subscriptions

| Screen | Purpose |
|---|---|
| `SubscriptionPlanFragment` | Browse available subscription plans |
| `SubscriptionPlanHistoryFragment` | View purchase history |

API: `subscriptionList`, `subscriptionGetList`, `buySubscription`, `createCheckoutSession`, `checkSubscription`, `subscription_history`, `updateAutoRenew`, `checkService`

- Stripe Checkout used for subscription purchase
- Auto-renew can be disabled

### 16. Push Notifications (FCM)

- `MyFirebaseMessagingService` handles all FCM messages
- `NotificationHelper` / `NotificationUtils` build local notifications
- Notification types: new job request, job update, message, offer, announcement, geofence alert
- `notifysend` API endpoint for server-triggered pushes

### 17. Offers & Announcements

- `YourOffersFragment` / `YourAnnocementFragment` / `YourAnnocementsOffersFragment`
- Data sourced from Firebase Realtime Database nodes: `Offers`, `Announcements`
- Separate streams for user-targeted and provider-targeted offers

### 18. User Ratings & Feedback

- `UserFeedbackFragment` — guard views ratings left by clients
- Post-trip rating submission by guard for the client (`DriverFeedbackStore`)
- API: `DriverFeedback`, `DriverFeedbackStore`
- `SimpleRatingBar` / `ColorRatingBar` custom rating UI components

### 19. Multi-language Support

- Runtime locale switching without restart using `LocalizationActivity`
- Supported locales: `en`, `ar`, `fi`, `si`
- Language picker in `HomeActivity` (before login)
- Currency picker in `HomeActivity`
- `LanguageAdapter` / `CurrencyAdapter` drive the selection UI

### 20. Support / Help

- `SupportFragment` — lists help topics fetched from `pages` API
- `SupportSubFragment` — displays article content from `Support` API
- `WebViewActivity` — renders any full web page (terms, policies)

### 21. Invite Friends / Referral

- `InviteFriendsFragment` — share referral link or code

### 22. Backup / Volunteer Trip

- Guard can send or accept **volunteer backup trip** requests (`VolunteerTrip`)
- `TripFirebaseBackupGuardListAdapter` displays available backup guards in Firebase
- Volunteer timeout handled by `volunteerTimeout` API

### 23. App Settings (Server-driven)

- `appsetting` API fetches global config: currency, language options, fee structures, map keys
- Settings cached in `SharedPreferences`

---

## Architecture & Tech Stack

### Language & Build
- **Java** (Java 8 source/target compatibility)
- **Gradle** 8.7 with `viewBinding` and `buildConfig` enabled
- **MultiDex** enabled

### UI
- Fragment-based navigation via `FragmentManager` (utility class: `Utils`)
- `ViewBinding` for all UI bindings
- Custom Views: `CustomTextView`, `CustomEditText`, `CustomButton` (Rubik/Calibri/Segoe fonts via `FontManager`)
- Lottie animations (loading, location, security lock, biometric icons)
- SDP dimension library for responsive sizing
- `SimpleRatingBar` / `ColorRatingBar` for star ratings
- `ExpansionPanelLayout` for collapsible sections
- `FloatingWidgetView` for system overlay during active trips

### Networking
- **Retrofit 2.9** + **OkHttp 4.11** for all REST API calls
- `CallbackWithRetry` for automatic retry on failure
- **GeoFire 3.2** for Firebase location queries

### Firebase
| Service | Usage |
|---|---|
| Realtime Database | Live location, trip state, offers, announcements, backup requests |
| Firebase Auth | Anonymous/token auth |
| Firebase Messaging (FCM) | Push notifications |
| Firebase Crashlytics | Crash reporting |
| Firebase Analytics | Usage analytics |

### Third-party SDKs
| Library | Purpose |
|---|---|
| Twilio Voice 4.4 | In-app voice calls |
| Twilio Conversations 6.0 | In-app messaging |
| Facebook SDK 13 | Facebook login, Audience Network ads |
| Google Sign-In 21 | Google OAuth |
| Google Maps SDK 18.2 | Map display |
| Google Play Location 21.2 | Fused location provider |
| Stripe (Jackson-based) | Payment processing |
| Lottie 6.0 | JSON animations |
| Fresco 3.1 | Image loading/caching |
| Picasso 2.71 | Image loading |
| Biometric 1.1 | Fingerprint/Face auth |
| EventBus 3.3 | In-process event delivery |
| Google Ads 22.2 | AdMob integration |

---

## Background Services

| Service | Purpose |
|---|---|
| `LocationUpdatesService` | Foreground service for continuous GPS tracking |
| `FloatingWidgetService` | System overlay widget during active trip |
| `WidgetService` | App widget service |
| `RequestServices` | Background job/request state polling |
| `MyFirebaseMessagingService` | FCM push message handler |
| `FetchAddressIntentService` | Reverse geocoding (address from coordinates) |

---

## Permissions Required

| Permission | Reason |
|---|---|
| `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` | Real-time location tracking |
| `ACCESS_BACKGROUND_LOCATION` | Background GPS during trips |
| `FOREGROUND_SERVICE` | Location & floating widget services |
| `CAMERA` | Document photo capture |
| `READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE` | Image picker |
| `WRITE_EXTERNAL_STORAGE` | File saving (≤ API 32) |
| `SYSTEM_ALERT_WINDOW` | Floating widget overlay |
| `POST_NOTIFICATIONS` | FCM push notifications |
| `RECEIVE_BOOT_COMPLETED` | Restart services after device reboot |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Keep location service alive |
| `READ_CONTACTS` | Emergency contact picker |

---

## App Flow Summary

```
LaunchActivity (splash + biometric check)
    │
    ├─► HomeActivity (unauthenticated)
    │       ├─ LoginFragment
    │       ├─ RegisterFragment
    │       ├─ ForgotPasswordFragment
    │       └─ SocialSignupFragment
    │
    └─► MainActivity (authenticated — main hub)
            ├─ Live job request screen (map + accept/decline)
            ├─ YourJobsFragment (Pending / Upcoming / Past tabs)
            ├─ MyAvailabilityFragment / MyAvailabilityNewFragment
            ├─ MyWalletFragment → transaction sub-fragments
            ├─ EarningsFragment
            ├─ PayoutFragment / PayoutsFragment
            ├─ SettingsFragment → EditSettingsFragment
            ├─ BankDetailsFragmentNew
            ├─ ManageDocumentsFragment → ManageViewDocumentsFragment
            ├─ ManageServiceFragment → SubCategoryFragment
            ├─ SubscriptionPlanFragment → SubscriptionPlanHistoryFragment
            ├─ EmergencyContactsFragment
            ├─ UserFeedbackFragment
            ├─ YourAnnocementsOffersFragment
            ├─ SupportFragment → SupportSubFragment
            └─ InviteFriendsFragment

DocumentActivity  — standalone document upload/view screen
WebViewActivity   — in-app browser for web pages
SettingsFaceFingerscreen — biometric configuration
```
