# PROVIDER_SCREEN_FLOW.md — InstaGuard Provider

Navigation, screen inventory, and per-screen documentation. The app is **Activity + Fragment** based (no Jetpack Navigation graph). Fragment switching is centralized in `Utils.switchContent(containerId, TAG, activity, args, animation)`.

## Activities (from AndroidManifest)
| Activity | Role |
|---|---|
| `LaunchActivity` | Splash + auto-login route decision (LAUNCHER) |
| `HomeActivity` | Pre-login landing; hosts auth fragments |
| `MainActivity` | Map + live dispatch + job-in-progress + drawer host (post-login home) — **11,770 lines** |
| `DocumentActivity` | KYC document upload/view |
| `WebViewActivity` | Generic WebView (Stripe onboarding, etc.) |
| `SettingsFaceFingerscreen` | Biometric settings |
| `BlankDummyActivity` | Locale helper |

## Services / Receivers
`RequestServices` (Firebase request listener), `LocationUpdatesService`, `FloatingWidgetService`, `WidgetService`, `FetchAddressIntentService`, `MyFirebaseMessagingService` (FCM), `GeofenceBroadcastReceiver`.

---

## Navigation tree (actual)

```text
LaunchActivity (splash; fetch FCM token → regid; Firebase anon sign-in; optional biometric)
├─ SP_DRIVER_LOGIN==true && SP_DRIVERID!=null && providers/{id} exists → MainActivity
└─ else → HomeActivity
        ├─ LoginFragment ──(login success)──▶ MainActivity
        ├─ RegisterFragment ──▶ LoginFragment
        ├─ ForgotPasswordFragment (email OTP → reset)
        └─ SocialSignupFragment (FB / Google / LinkedIn)

MainActivity (drawer host, container R.id.frame_mainactivity)
   drawer (onNavigationItemSelected):
     nav_myprofile          → SettingsFragment
     nav_myavailability     → MyAvailabilityFragment
     nav_manageservices     → ManageServiceFragment (key="main") → SubCategoryFragment
     nav_managedocument     → DocumentActivity (from="vehicle")
     nav_yourjobs           → YourJobsFragment [Upcoming | Past] (+Pending internally)
     nav_bankdetails        → BankDetailsFragmentNew
     nav_userPayouts        → PayoutFragment [Withdrawal | Transaction]
     nav_mywallet           → MyWalletFragment → StripePaymentFragment
     nav_emergencycontact   → EmergencyContactsFragment
     nav_userfeedback       → UserFeedbackFragment (reviews received)
     nav_invitefriends      → InviteFriendsFragment
     nav_support            → SupportFragment → SupportSubFragment
     nav_notification       → YourAnnocementsOffersFragment [Offers | Announcements]
     nav_payout             → EarningsFragment
     nav_language           → language dialog
     nav_currency           → currency dialog
     menu_finger            → SettingsFaceFingerscreen
     nav_subscription_plans → SubscriptionPlanFragment (from="main") → SubscriptionPlanHistoryFragment
     nav_my_event_booking   → MyCalenderBookingFragment (WebView calendar)
```

No bottom navigation bar. There is a navigation drawer with a profile header (`txt_nav_user_name`, `txt_nav_walletbalance`, `txt_nav_user_id`, `img_nav_profileimage`, `rb_nav_user_rating`).

### Deep links / notification routing
`LaunchActivity` reads intent extras and forwards to `MainActivity` via extra `"Notify"`: `Offers` → offers screen, `Announcements` → announcements, `pending`→Pending jobs, `Reminder`→calendar. Twilio call invites route via `MainActivity.ACTION_INCOMING_CALL`.

---

## Screen-by-screen

### LaunchActivity
- **Path:** `Activities/LaunchActivity.java` · **Entry:** LAUNCHER · **Next:** MainActivity | HomeActivity
- **Logic:** fetch FCM token→`GLOBAL_STORED_ID/regid`; Firebase `signInAnonymously`; biometric gate (`MY_PREFS_NAME_NEW`); route on `SP_DRIVER_LOGIN` + `SP_DRIVERID` + `providers/{id}` existence.
- **Permissions:** location (requested downstream). **Local storage:** reads login flag, driver id, currency.

### HomeActivity (pre-login)
- **Path:** `Activities/HomeActivity.java` · **Prev:** Launch · **Next:** MainActivity, auth fragments
- **Actions:** Sign In / Sign Up / social login / language / currency. Calls `appsetting`.

### LoginFragment
- **Path:** `Fragments/LoginFragment.java` · **API:** `auth/logind?` · **Validation:** email/phone + password non-empty · **Saves:** token+profile SP · **Nav:** MainActivity.

### RegisterFragment
- **Path:** `Fragments/RegisterFragment.java` · **API:** `emailAvailable?` → `auth/registerDriver?` · **Validation:** name regex, libphonenumber, email, city, terms · **Nav:** Login.

### ForgotPasswordFragment
- **Path:** `Fragments/ForgotPasswordFragment.java` · **API:** `forgetpass` → `verifyotp` → `updatePassword?` · **Nav:** Login.

### SocialSignupFragment
- **Path:** `Fragments/SocialSignupFragment.java` · **API:** `driver/socialUpdate` · Completes social profile.

### MainActivity (post-login home / live trip)
- **Path:** `Activities/MainActivity.java` · **Prev:** Launch/Login · **Purpose:** Google Map, online/offline toggle (`checkin?`), **real-time request receive** (Firebase `providers/{id}/trips/service_status`), accept/reject, job-in-progress state machine, settlement, rating, drawer host, Twilio chat/voice, panic/SOS.
- **APIs:** requestService, removeDriver, cancelJobs, stripePayment, cashPayment, DriverFeedbackStore, checkin, savepanic, notifysend, profileViewDriver, appsetting, chat/call tokens.
- **Local storage:** trip state (`SP_MAP_TRIPSTATE`), booking id, FB category prefs, token, driver id.
- **Permissions:** ACCESS_FINE/COARSE/BACKGROUND_LOCATION, CAMERA, RECORD_AUDIO (calls), POST_NOTIFICATIONS, SYSTEM_ALERT_WINDOW (floating widget).
- **UI overlays:** `lytOnlineOffline`, `lytRequestReceive`, `lytJobInprogress`, `lytCardPayment`, trip summary, additional charge, cancel dialog, rating card.

### YourJobsFragment (+ Pending/Upcoming/Past)
- **Path:** `Fragments/YourJobsFragment.java` + `YourJobsPendingFragment/YourJobsUpcomingFragment/YourJobsPastFragment` · Tabs = Upcoming, Past (Pending internal).
- **APIs:** pendingJobs, upcomingJobs, pastjobs; actions scheduleJobAccept, scheduledcancelJobs, scheduletripStart, VolunteerTrip.
- **Adapters:** PendingJobsAdapter, UpcomingJobsAdapter, PastJobsAdapter.

### MyAvailabilityFragment / MyAvailabilityNewFragment / MyAvailabilitySubFragment
- **APIs:** ServiceSelectionGet/ServiceSelection (weekly), viewDriverDateSlot/updateDriverSlot/viewDriverSlots (per-date). Slots 1–24 hourly.

### MyCalenderBookingFragment
- **Path:** `Fragments/MyCalenderBookingFragment.java` · WebView loading `CALENDER_WEB_URL + driverId + "/" + Base64(dd-MM-yyyy)`.

### SettingsFragment / EditSettingsFragment
- **APIs:** profileViewDriver, updateprofileDriver, picuploadDriver (Base64), auth/passwordChangeDriver, delete_account_request/confirm, cities.

### ManageServiceFragment / SubCategoryFragment
- **APIs:** CategoriesDriver, ServiceDriver, ServiceSelection, ServiceSelectionGet · Adapters MapCategoryAdapter/MapCategorySubAdapter.

### DocumentActivity / ManageDocumentsFragment
- **APIs:** manage_document, viewdocument_e, imageupload_e, document_update_e, proofreupload_e (active); documentUpload/proofreupload/viewdocument (legacy). Base64 upload. Adapters DocumentAdapter/ManageDocumentAdapter.

### EmergencyContactsFragment
- **APIs:** emergencyContactDriver, emergencyContactGETDriver, emergencyContactDeleteDriver · Adapter EmergencyViewAdapter · Reads device contacts.

### EarningsFragment / MyWalletFragment / StripePaymentFragment / WalletTermsFragment
- **APIs:** profileViewDriver (earnings), TransactionDriver, BonusTransactionList, driverAddMoney, appsetting, Support.

### PayoutFragment / PayoutsFragment / PayoutTransctionsFragment
- **APIs:** driverPayoutRequest, payout_history · Adapter PayoutTransactionAdapter · Balance from Firebase `providers/{id}/wallet`.

### BankDetailsFragmentNew / BankDetailFragment
- **APIs:** bankDetailsGet, stripe/create_account (WebView onboarding), bankDetailsUpdate (legacy).

### SubscriptionPlanFragment / SubscriptionPlanHistoryFragment
- **APIs:** subscriptionList, subscriptionGetList, createCheckoutSession, checkSubscription, buySubscription, subscription_history, updateAutoRenew; result via Firebase `providers/{id}/subs_buy_status`.

### UserFeedbackFragment
- **API:** DriverFeedback? · Adapter UserFeedbackAdapter · Reviews the provider received.

### SupportFragment / SupportSubFragment
- **APIs:** pages?, Support? (page_name: About Us / Privacy Policy / Terms and Conditions / Contact Us / FAQ's).

### InviteFriendsFragment
- Referral share (FB/SMS/generic); uses `SP_DRIVER_INVITE_CODE`, `SP_DRIVER_REFERAL_CODE`. No dedicated endpoint.

### YourAnnocementsOffersFragment (+ YourOffersFragment / YourAnnocementFragment)
- **Data:** Firebase `Notifications/Offers|Announcements/provider` (status="1"). No REST.
