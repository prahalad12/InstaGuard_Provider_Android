# PROVIDER_APP_DOCUMENTATION.md — InstaGuard Provider (Guard/Driver) — Master

Technical source of truth for rebuilding the **Provider/Guard side** of InstaGuard in Flutter, reverse-engineered from the Android Java project `instaguard-provider` (package `com.instaguard.provider`, versionName 1.3).

## Companion documents
- **`PROVIDER_BOOKING_FLOW.md`** — the complete User→Booking→Provider flow (**read first**).
- `PROVIDER_API_DOCUMENTATION.md` — every endpoint with request/response.
- `PROVIDER_DATA_MODELS.md` — all models & fields.
- `PROVIDER_SCREEN_FLOW.md` — navigation + screen-by-screen.
- `PROVIDER_FLUTTER_IMPLEMENTATION_MAPPING.md` — architecture mapping.

---

## 0. Executive answer

> **"If a User books a Guard from `instaguard-user`, exactly what happens inside `instaguard-provider`?"**

The backend writes the booking into Firebase Realtime Database at `providers/{driverId}/trips/...` and sets `providers/{driverId}/trips/service_status = "1"`. The provider app's background `RequestServices` and foreground `MainActivity` both listen to that path; when it becomes `"1"` they open the incoming-request screen, read the full booking payload straight from Firebase, and start a countdown. The provider **Accepts** (`POST requestService?` `mode=accept`, returns `booking_id`) or **Rejects** (Firebase `service_status="0"` + `POST removeDriver`). After accept, the backend advances `service_status` through `2→3→4→5→5.1`, and the provider drives it forward with `requestService?` (`start_driver`/`reached`/`start_service`) and finishes with `finish_service` + `stripePayment`/`cashPayment` + `DriverFeedbackStore?`. The booking then becomes a Past Job (status 6/7). Full detail: `PROVIDER_BOOKING_FLOW.md`.

---

## 1. Tech stack (from `app/build.gradle`)
- **Language:** Java. **UI:** ViewBinding + XML. minSdk 25, target/compile 35.
- **Networking:** Retrofit 2.9 + OkHttp 4.11 + Gson (+ Jackson present).
- **Realtime/dispatch:** Firebase Realtime Database 20.3.1 + GeoFire 3.2 + FirebaseUI DB.
- **Push:** Firebase Messaging 23.4.1. **Crash:** Crashlytics.
- **Maps/location:** play-services-maps 18.2, play-services-location 21.2, Google Direction lib.
- **Comms:** Twilio Voice + Conversations. **Payments:** Stripe (server-driven / WebView).
- **Auth SDKs:** Facebook 13.0, Google play-services-auth 21, Twitter4j, LinkedIn (WebView).
- **Images:** Fresco, Picasso, CircleImageView. **Misc:** Lottie, AwesomeValidation, libphonenumber, MaterialDateTimePicker.

## 2. Feature list

| # | Feature | Screen/Class | Purpose | APIs | Models |
|---|---|---|---|---|---|
| 1 | Splash/auto-login | `LaunchActivity` | Route by login state | — (Firebase) | — |
| 2 | Landing | `HomeActivity` | Sign in/up, language, currency | appsetting | AppSettingWebModel |
| 3 | Login | `LoginFragment` | Email/phone login | auth/logind? | LoginResults |
| 4 | Register | `RegisterFragment` | Create provider | emailAvailable?, auth/registerDriver? | RegisterModel |
| 5 | Forgot/Reset password | `ForgotPasswordFragment` | Email OTP reset | forgetpass, verifyotp, updatePassword? | ForgetPassModel, VerifyOtpModel |
| 6 | Social login | `SocialSignupFragment` | FB/Google/LinkedIn | driver/socialAdd, driver/socialUpdate | SocialLoginModel |
| 7 | Change password | `EditSettingsFragment` | Update password | auth/passwordChangeDriver? | ChangePasswordModel |
| 8 | Logout | `MainActivity` | End session | auth/logoutd? | LogoutModel |
| 9 | Delete account | `SettingsFragment` | OTP delete | delete_account_request/confirm | DeleteModel |
| 10 | Online/offline + location | `MainActivity` | Availability toggle | checkin? | DriverLocationUpdateModel |
| 11 | **Receive booking (realtime)** | `RequestServices`, `MainActivity` | Live dispatch | Firebase `service_status` | (Firebase trip node) |
| 12 | **Accept booking** | `MainActivity` | Accept request | requestService? (accept) | AcceptRequestModel |
| 13 | **Reject booking** | `MainActivity` | Decline | Firebase + removeDriver | BankModel |
| 14 | Trip transitions | `MainActivity` | on-way/reach/start | requestService? | RequestStatusModel |
| 15 | **Complete booking** | `MainActivity` | Finish + settle | requestService? (finish), stripePayment, cashPayment | EndRequestModel, StripeEndTModel |
| 16 | Cancel job | `MainActivity` | Cancel w/ reason | cancelJobs? | CancelJobModel |
| 17 | Rate user | `MainActivity` | Provider→user rating | DriverFeedbackStore? | RatingUserModel |
| 18 | Pending jobs | `YourJobsPendingFragment` | Scheduled pending | pendingJobs?, scheduleJobAccept?, scheduledcancelJobs? | PendingJobsModel |
| 19 | Upcoming jobs | `YourJobsUpcomingFragment` | Accepted scheduled | upcomingJobs?, scheduletripStart?, scheduledcancelJobs?, VolunteerTrip | UpcomingJobsModel |
| 20 | Past jobs | `YourJobsPastFragment` | History | pastjobs? | PastJobsModel |
| 21 | Availability (per-date) | `MyAvailabilityNewFragment` | Hourly slots | viewDriverDateSlot, updateDriverSlot, viewDriverSlots | Availability/* |
| 22 | Availability (weekly) | `MyAvailabilityFragment`/`Sub` | Weekly slots | ServiceSelectionGet, ServiceSelection | AvailabilityModel |
| 23 | Calendar (web) | `MyCalenderBookingFragment` | Server calendar | WebView (CALENDER_WEB_URL) | — |
| 24 | Profile view/edit | `EditSettingsFragment` | Profile | profileViewDriver?, updateprofileDriver?, picuploadDriver? | ProfileModel |
| 25 | Manage services | `ManageServiceFragment`/`SubCategoryFragment` | Category/services | CategoriesDriver?, ServiceDriver?, ServiceSelection? | VehicleCategoryModel |
| 26 | Documents/KYC | `DocumentActivity`/`ManageDocumentsFragment` | Upload/verify | manage_document, imageupload_e, document_update_e, proofreupload_e | DocumentListData |
| 27 | Emergency contacts | `EmergencyContactsFragment` | SOS contacts | emergencyContact* | EmergencyViewModel |
| 28 | Earnings | `EarningsFragment` | Totals | profileViewDriver? | ProfileData |
| 29 | Wallet + transactions | `MyWalletFragment`, `YourWalletTrans*` | Wallet | TransactionDriver?, BonusTransactionList?, driverAddMoney? | WalletAllModel, RideHistoryModel |
| 30 | Payouts | `PayoutFragment`/`Payouts`/`PayoutTransctions` | Withdraw | driverPayoutRequest, payout_history | PayoutModel |
| 31 | Bank details | `BankDetailsFragmentNew` | Stripe Connect | bankDetailsGet?, stripe/create_account | BankModel, WebResModel |
| 32 | Subscription | `SubscriptionPlanFragment`/`History` | Plans | subscriptionList, createCheckoutSession, checkSubscription, subscription_history, updateAutoRenew | Subscription/* |
| 33 | Reviews received | `UserFeedbackFragment` | See ratings | DriverFeedback? | UserFeedbackModel |
| 34 | Chat | `MainActivity` (Twilio) | In-app chat | chatAccessToken | ChatAccessModel |
| 35 | Voice call | `MainActivity` (Twilio) | Call user | accesstoken | ChatAccessModel |
| 36 | Panic/SOS | `MainActivity` | Emergency | savepanic? | PanicAddAdminModel |
| 37 | Support pages | `SupportFragment`/`Sub` | About/terms/etc | pages?, Support? | AppSupportPagesModel |
| 38 | Invite friends | `InviteFriendsFragment` | Referral share | — | — |
| 39 | Offers/Announcements | `YourAnnocementsOffersFragment` | Promotions | Firebase | FirebaseModel |
| 40 | Biometric | `SettingsFaceFingerscreen` | Face/finger login | — | — |

---

## 3. Authentication summary
Full detail in `PROVIDER_API_DOCUMENTATION.md` (Auth section). Key points: form-encoded POST, token stored as `accessToken`/`tokenType`/`tokenExpTime`; `driverId` = `SP_DRIVERID`; role is implicit (`mode:"driver"`); OTP for forgot-password is **email-based** (`forgetpass`→`verifyotp`); logout clears all prefs. Admin OTP flag `SP_OTP_STATUS` is declared but unused.

## 4. Booking — see `PROVIDER_BOOKING_FLOW.md` (authoritative)

## 5. User information shown to the Provider
| Field | Source |
|---|---|
| Name | Firebase `trips/customer_name`; accept response `customer_first_name/last_name` |
| Phone | accept/booking response `customer_phone_number`; upcoming list `customer_phone_number` |
| Email | accept response `customer_email` |
| Pickup address | Firebase `trips/user_pickup`; `customer_location` |
| Pickup coords | Firebase `trips/pickup_lat/pickup_lng` |
| Drop | `trips/user_drop`, `drop_lat/drop_lon` |
| User rating | Firebase `users/{id}/rating`, `trips/user_rating` |
| User wallet | Firebase `users/{id}/wallet` |
| Special instructions | `trips/special_ins` |
| Guards requested / days | `trips/total_agent`, `trips/total_days` |

No dedicated user-profile REST endpoint exists on the provider side.

## 6. Provider availability & conflict handling
- **Availability = published slots** (per-date `updateDriverSlot`, or weekly `ServiceSelection`), plus a live **online toggle** (`checkin?` status 1/0) writing GeoFire location to `drivers_location/{driverId}`.
- **The booking system knows a provider is available** because the backend dispatches only to providers whose slots/online status qualify, then writes the request into that provider's Firebase `trips` node. The app itself does not run a matching algorithm.
- **Conflict prevention** (double-booking, overlap, out-of-hours) is **enforced server-side** — no client-side conflict logic exists. The on-demand request UI is single-slot (one `trips` node), which naturally serializes on-demand requests.

## 7. Booking status lifecycle — see `PROVIDER_BOOKING_FLOW.md` §9 (Firebase 0–5.2; REST 6–15).

## 8. Local storage (`SharedPreferences`)
Files: `MY_PREFS_NAME` (`Insta-Guard_Demo_Driver`), `GLOBAL_STORED_ID`, `PREFS_NAME_CURRENCY`, `MY_PREFS_FB_CATEGORY_TRIPSTATE`, `SP_MAP_TRIPSTATE`, `MY_PREFS_NAME_REMEMBER`, `MY_PREFS_NAME_NEW` (biometric).

| Key | Purpose | Written | Read |
|---|---|---|---|
| `accessToken`/`tokenType`/`tokenExpTime` | Auth token | login/refresh | all secured calls |
| `..._userId` (`SP_DRIVERID`) | Provider id | login | everywhere |
| `..._boolean` (`SP_DRIVER_LOGIN`) | Logged-in flag | login/logout | LaunchActivity |
| `regid` (`SP_DEVICE_ID`) | FCM token | LaunchActivity/onNewToken | login/register `device_token`, Twilio register |
| name/email/phone/CC/DOB/address | Profile cache | login/profile update | UI |
| `..._currency`/`..._currency_symbol`/`..._currency_common_ratio` | Currency | login/appsetting | amount formatting |
| `..._driver_wallet`/`..._driver_wallet_bonus` | Wallet cache | wallet/earnings | UI |
| `trip_state` (`SP_MAP_TRIPSTATE`) | Live trip state | trip listener | MainActivity |
| `trip_BOOKING_ID` | Current booking | trip listener | MainActivity |
| FB category prefs (`category_id`, `payment_mode`, etc.) | Accept context | request receive | accept/end |
| Stripe keys, timedelay_accept, distance_unit, subscription flag | appsetting cache | appsetting | various |
| remember-me email/pass/checked | Login convenience | login | login |
| face/finger user id + status | Biometric | biometric setup | LaunchActivity |

## 9. Permissions (AndroidManifest)
| Permission | Why | Where | If denied |
|---|---|---|---|
| ACCESS_FINE/COARSE_LOCATION | dispatch, map, checkin | MainActivity/Login | can't go online / receive jobs |
| ACCESS_BACKGROUND_LOCATION | tracking during trip | MainActivity | background tracking stops |
| CAMERA | profile pic, documents | Edit/Document | can't capture |
| READ_MEDIA_IMAGES / READ/WRITE_EXTERNAL_STORAGE (≤32) | gallery upload | Edit/Document | can't pick images |
| READ_CONTACTS | emergency contacts import | EmergencyContacts | manual entry only |
| RECORD_AUDIO | Twilio voice calls | MainActivity | can't call |
| POST_NOTIFICATIONS | FCM/alerts | app | no notifications |
| SYSTEM_ALERT_WINDOW / MANAGE_OVERLAY | floating request widget | FloatingWidgetService | no overlay |
| REQUEST_IGNORE_BATTERY_OPTIMIZATIONS / RECEIVE_BOOT_COMPLETED / FOREGROUND_SERVICE | keep dispatch alive | services | may miss jobs when backgrounded |
| READ_PHONE_STATE | device/telephony | app | minor |

## 10. Base URL / environment
- Active build: `BASE_CODE="_Demo"`, all URLs → `https://backend.insta-guard.co.uk/`, API prefix `api/v3/`.
- Chat/call base identical. Calendar WebView: `http://insta-guardweb.uplogictech.in/calender/driver/`.
- Secrets (`API_KEY` Maps, Stripe live/secret keys, Facebook/Google client ids, `io.fabric.ApiKey`) → **[SECRET/REDACTED]** (pulled from `local.properties`, `google-services.json`, `appsetting`).

## 11. Notifications
| Event | Type | Payload | Provider screen |
|---|---|---|---|
| New booking | **Firebase RTDB** (not FCM) | `providers/{id}/trips/*`, `service_status="1"` | MainActivity request UI |
| Generic push | FCM data | `a_data="pn_city"`, `title`, `body`, `image`, `status` | tray → LaunchActivity |
| Deep-link push | FCM/intent | `Notify` = Offers/Announcements/Pending/Reminder | respective screen |
| Incoming call | FCM (Twilio) | `twi_*` payload | MainActivity call dialog |
| Provider→user (reject) | REST `notifysend?` | `customer_id, driver_id, mode:"reject"` | — |

FCM token: fetched in `LaunchActivity`, stored at `GLOBAL_STORED_ID/regid`, refreshed in `MyFirebaseMessagingService.onNewToken`. No REST token-registration endpoint (token used in login/register `device_token` and Twilio `Voice.register`).

## 12. Reviews & ratings
- Provider **sees** reviews: `DriverFeedback?` `{driver_id}` → `UserFeedbackDataModel{from_id(user), to_id(this provider), job_id, rating, feedback, name, avatar}`.
- Provider **rates** user after job: `DriverFeedbackStore?` `{from_id:driver, to_id:user, job_id:booking_id, rating, feedback, status:"0"}` → `"Feedback received"` → Firebase `rating_status="1"`.
- Average rating shown in nav header (`rb_nav_user_rating`) and request/job cards (from Firebase `rating`).

## 13. Chat / communication
- **In-app chat:** Twilio **Conversations**, channel `Channel:{userId},{driverId},{bookingId}`, token from `chatAccessToken?device={driverId}&identity={mIdentity}`.
- **Voice:** Twilio **Voice**, token from `accesstoken?device=android_driver&identity=dofloo{driverId}`, register with FCM token; outgoing to identity `InstaGuard_user{userId}`.
- Both surface during job-in-progress.

---

## 14. UNKNOWN / REQUIRES BACKEND CONFIRMATION
1. **Exact Firebase write schema from the user app** — the provider *reads* `providers/{driverId}/trips/*`; the precise fields/format the user app/backend *writes* (and the on-demand matching/dispatch algorithm) are server-side and not visible here. What is confirmed is the read shape (Booking Flow §3.3).
2. **Response JSON for `requestService?` variants** — models define fields, but the live payloads (which of `unit_price`/`site_commission`/`total_amount` are populated, currency of amounts) need a captured response.
3. **`service_status` "5.2" (additional charge)** — referenced in code but the additional-charge settlement contract is partially commented; confirm backend behavior.
4. **Scheduled vs on-demand routing rules** — how backend chooses to push realtime (`service_status=1`) vs list as pending is server logic.
5. **`removeDriver` / candidate-list semantics** — how the backend re-offers a rejected booking to the next guard is not in the client.
6. **Unused endpoints** — `checkService?`, `message_notify`, `geofensPushNotification`, `auth/SocialLogDriver?`, `forgot_password?`, `verifyMobile?` are defined but not called; confirm whether the Flutter app should use them.
7. **Amounts as `Integer`** in `AcceptRequestDataModel` (`unit_price`, `total_amount`) vs `Double` elsewhere — confirm minor-units vs whole-currency with backend.
8. **Payout currency** hardcoded `£` in UI vs `currency` field in response — confirm.

---

## 15. FINAL AUDIT
- [x] Every provider screen inspected (Activities + all Fragments enumerated in `PROVIDER_SCREEN_FLOW.md`).
- [x] Every provider API inspected (all 60+ methods in `APIInterface.java` documented).
- [x] Every booking API traced to its call site in `MainActivity`.
- [x] All booking statuses identified (Firebase 0–5.2; REST 6–15).
- [x] Accept flow — `requestService?` mode=accept (MainActivity:3720-3897).
- [x] Reject flow — Firebase `service_status="0"` + `removeDriver` (MainActivity:1640-1667, 9904).
- [x] Cancel flow — `cancelJobs?` with reason (MainActivity:4432).
- [x] Start/Complete — `requestService?` start_driver/reached/start_service/finish_service.
- [x] Availability logic — slots (`updateDriverSlot`/`ServiceSelection`) + online `checkin?`.
- [x] Notification flow — Firebase dispatch + FCM generic/Twilio.
- [x] User data shown to provider — Firebase trip node + accept response.
- [x] Provider data — `ProfileData` master model.
- [x] Payment/earning flow — stripePayment/cashPayment/TransactionDriver/payouts.
- [x] Models documented (`PROVIDER_DATA_MODELS.md`).
- [x] Local storage documented (§8).
- [x] Permissions documented (§9).
- [x] Base URLs identified (§10).
- [x] Request/response structures documented (`PROVIDER_API_DOCUMENTATION.md`).
- [x] Navigation documented (`PROVIDER_SCREEN_FLOW.md`).

**No source files were modified. No Flutter code was written.**
