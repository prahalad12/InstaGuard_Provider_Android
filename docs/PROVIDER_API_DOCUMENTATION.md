# PROVIDER_API_DOCUMENTATION.md — InstaGuard Provider

Complete API reference reverse-engineered from `Helpers/Retrofit/APIInterface.java`, the Retrofit call sites, and the models. **Terminology:** Driver = Provider/Guard.

## Networking architecture

- **Client:** `Helpers/Retrofit/APIClient.java` — Retrofit 2.9 + Gson (lenient, `NullStringToEmptyAdapterFactory` maps null Strings → `""`). OkHttp with `HttpLoggingInterceptor` (BODY) in DEBUG only; connect/read timeout **1000 s**. There are 3 clients: `getClient()` (main), `getClient_chat()` (Twilio chat/call base), `getClient_linked()` (LinkedIn).
- **Base URL (`Utils.RETROFIT_URL`):** `https://backend.insta-guard.co.uk/api/v3/`
- **Chat/call base (`Utils.RETROFIT_URL_CALL_CHAT_ACCESS`):** `https://backend.insta-guard.co.uk/api/v3/`
- **All POST endpoints:** `@FormUrlEncoded` + `@FieldMap HashMap<String,Object>` (i.e. `application/x-www-form-urlencoded`). No JSON bodies.
- **Auth:** most secured calls pass `Authorization: "Bearer " + accessToken` as a `@Header`; a few pass `token` as a form field instead. `Accept`/`Content-Type` when present = `Utils.POST_ACCEPT_CONTENT_TYPE = "application/json"`.
- **Token refresh:** any HTTP `401` → `POST auth/refreshd?` (`doGetRefreshToken`) re-saves token and retries the original call; a 2nd 401 → forced re-login (`showTimeOut()`).
- **Response codes used:** 200, 201 (success), 401 (refresh), 500 (error toast).
- **Success is determined by matching `response.body().getMessage()` against exact strings** (documented per endpoint), NOT by HTTP status alone.

### Environments (`Utils.java`)
| Env | Value |
|---|---|
| Active build code | `BASE_CODE = "_Demo"` |
| Base URL (Demo & Live identical) | `https://backend.insta-guard.co.uk/` |
| API prefix | `api/v3/` |
| Calendar web URL | `http://insta-guardweb.uplogictech.in/calender/driver/` |
| Stripe test publishable fallback | `pk_test_WDP0fwVbtyJzXdGucvd901EJ` |
| Google Maps key | `${API_KEY}` from `local.properties` → **[SECRET/REDACTED]** |

---

## AUTH & ACCOUNT

### `auth/logind?` — Login
- **Method:** POST · **Auth:** none · **Retrofit:** `doGetLoginDetails` → `LoginResults`
- **Body:** `email` (lowercased), `password`, `device_token` (FCM token)
- **Response:** `{ result: LoginModel{access_token, token_type, expires_in, driver:DriverData}, message }`. Success: `"loggedin successfully"`.
- **Saves:** `SP_DRIVER_ACCESSTOKEN`, `SP_DRIVER_TOKENTYPE`, `SP_DRIVER_TOKENEXPTIME`, `SP_DRIVERID`, name/email/phone/currency, `SP_DRIVER_LOGIN=true`.
- **Used by:** `LoginFragment` (host `HomeActivity`). **Nav:** → `MainActivity` (or SubscriptionPlan when `service_ids` empty).

### `auth/registerDriver?` — Register
- POST · none · `doGetRegisterDetails` → `RegisterModel`
- **Body:** `first_name, last_name, email, phone_number, country_code(+), password, device_token, referral_code, provider_type(individual|company), company_name, city`
- Success: `"Registered Successfully"`. Writes `providers/{id}` Firebase node. **Nav:** → Login.

### `emailAvailable?` — Email/phone availability
- POST · none · `doGetEmailMobileChecking` → `EmailMobileCheckingModel`
- **Body:** `phone_number, email, mode:"driver"` · gate msg `"Email or Phone Number not exists in our db."` proceeds to register.

### `verifyotp` — Verify email OTP (forgot password)
- POST · none · `doVerifyOtp` → `VerifyOtpModel` · **Body:** `email, mode:"driver", otp` · Success: `"Code verified successfuly"` (returns `result.id`).

### `forgetpass` — Send reset OTP (email)
- POST · none · `doForgotPassword` → `ForgetPassModel` · **Body:** `email, mode:"driver"` · Success: `"Mail sent successfully"`.

### `updatePassword?` — Reset password
- POST · none · `doUpdateResetPassword` → `ResetPasswordModel` · **Body:** `id, password, mode:"driver"` · Success: `"Driver password updated successfuly."`.

### `verifyMobile?` — Verify mobile (defined, inactive)
- POST · none · `doUpdateverifyMobile` → `VerifyMobileModel` · **Body:** `phone_number, mode:"driver"`.

### `forgot_password?` — (defined, unused) `doUpdateForgotPassword` → `ForgotPasswordModel`

### `auth/refreshd?` — Refresh token
- POST · Headers `Accept`+`Content-Type: application/json` · `doGetRefreshToken` → `RefreshTokenModel`
- **Body:** `id, token` · **Response:** `access_token, token_type, expires_in` (re-saved).

### `auth/SocialLogDriver?` — (defined, unused) `doGetSocialLoginDetailsdd`
### `driver/socialAdd` — Social check/login
- POST · none · `doGetSocialLoginDetails` → `SocialLoginModel` · **Body:** `social_id, social_type, device_id` · Branches on `msg`: `"New Registration"` / `"Driver login successful."` / `"Email already exists."`.

### `driver/socialUpdate` — Social signup complete
- POST · none · `doSocialLoginUpdate` → `SocialSignupModel` · **Body:** `driver_id, email, country_code, phone_number, device_id, name, last_name, referral_code, provider_type, company_name, city` · Success: `"Driver Details"`.

### `auth/logoutd?` — Logout
- POST · none (token as field) · `doGetLogoutDetails` → `LogoutModel` · **Body:** `id, token` · Success: `"Successfully logged out"` → clears `MY_PREFS_NAME`, sets `providers/{id}/status="0"`, → HomeActivity.

### `auth/passwordChangeDriver?` — Change password
- POST · `Authorization: Bearer` · `doUpdateChangePassword` → `ChangePasswordModel` · **Body:** `id, old_password, new_password` · Success: `"Password updated successfully."` · Used by `EditSettingsFragment`.

---

## PROFILE / DOCUMENTS / SERVICES

### `profileViewDriver?` — Get profile / earnings / current plan
- POST · none (token field) · `doGetprofileDetails` → `ProfileModel`
- **Body:** `id, token` · Success: `"Profile listed successfully."`
- **Response:** `{ data: ProfileData, rating, bonus_earning, message }`. `ProfileData` is the master provider record (see `PROVIDER_DATA_MODELS.md`).
- **Used by:** `EditSettingsFragment`, `EarningsFragment`, `MyWalletFragment`, `SubscriptionPlanFragment`.

### `updateprofileDriver?` — Update profile
- POST · none (token field) · `doGetprofileUpdateDetails` → `ProfileUpdateModel`
- **Body:** `id, first_name, last_name, email, phone_number, country_code, city, company_name, service_description, token` · Success: `"Driver updated successfully."` · Mirrors to `providers/{id}` Firebase.

### `picuploadDriver?` — Profile photo (Base64)
- POST · `Authorization: Bearer` · `doGetprofilePhotoDetails` → `ProfilePicModel`
- **Body:** `id, file_binary_data` (Base64 JPEG), `avatar` · Success: `"driver updated successfully."`.

### `CategoriesDriver?` / `AllServicesDriver?` — Category list
- POST · `Authorization: Bearer` + `Accept` + `Content-Type` · `doGetVehiclecategoryUpdateDetails` / `doGetVehiclecategoryAllServicesDriver` → `VehicleCategoryModel`
- **Response:** `data: List<VehicleCategoryData>{id, category_name, category_name_spanish, category_image, category_type, level_type, level_amount}`.

### `ServiceDriver?` — Services within a category
- POST · `Authorization: Bearer` · `doGetVehiclecategoryServicesDriver` → `VehicleSubCategoryModel`
- **Body:** `id` (category id) · **Response:** `data: List<VehicleSubCategoryData>` (service_name, base_fare, price_per_hour/km/min, admin_commission, has_level, … — rates read-only).

### `ServiceSelection?` — Save selected services / availability
- POST · `Authorization: Bearer` · `doUpdateVehicleCategory` → `AvailabilityModel` · **Body:** `id, service_ids` (comma list) · Success: `"Driver Services updated successfully."`
- Same path also mapped as `doUpdateAvailable` (weekly slots): **Body:** `id, availble_slots, token`.

### `ServiceSelectionGet?` — Get selected services / weekly slots
- POST · none · `doGetAvailable` → `AvailabilityModel` · **Body:** `id, token`.

### Documents (active `*_e` flow — Base64, `DocumentActivity`)
| Endpoint | Retrofit | Body | Success |
|---|---|---|---|
| `manage_document` | `doCallViewManageDocument` → `DocumentListData` | `id, type:"driver", mode:"api"` | `"Documents listed successfully."` |
| `viewdocument_e` | `doDocumentViewDetails` → `DocumentViewModel` | `driver_id` | `"success"` |
| `imageupload_e` | `doImageUploadPathDetails` → `UploadModel` | `image`(Base64), `type` | `"image updated successfully"` (returns path) |
| `document_update_e` | `doDocumentUploadDetails` → `UploadDocModel` | `document`(path), `document_id, document_type, document_type_ar, driver_id, type, expiry_date, expiry_status[, licence_number]` | — |
| `proofreupload_e` | `doDocumentReUploadDetails` → `ReUploadModel` | `document, driver_id, document_id, id, type, expiry_date, expiry_status[, licence_number]` | — |

### Documents (legacy fragment flow)
| `viewdocument` | `doGetDocumentsviewDetails` → `DocumentViewModel_` | `id, token` (Auth = raw token) |
| `documentUpload` | `doUPdateDocumentDetails` → `UploadDocModel_` | Bearer · `id, file_binary_data, certificate, proof_name` |
| `proofreupload` | `doReupdateDocumentDetails` → `ReUploadModel_` | Bearer · `driver_id, id, type, document_proof, certificate` |

Approval lifecycle: `approved_status` `0`=pending, `1`=approved, `2/4`=rejected(+`reason`), `3`=not uploaded; `expiry_status`/`expiry_date`; SIA `licence_number` when `get_licence_number != "disable"`.

### Emergency contacts
| `emergencyContactDriver?` | `doAddemergencyContactCustomer` → `EmergencyAddModel` | Bearer · `driver_id, phone_number, status:"1", contact_name` |
| `emergencyContactGETDriver?` | `doGetViewEmergencyDetails` → `EmergencyViewModel` | Bearer+Accept · `driver_id` |
| `emergencyContactDeleteDriver?` | `doGetDeleteEmergencyDetails` → `EmergencyDeleteModel` | Bearer+Accept · `contact_id` |

### `cities` — City list (GET)
- GET · none · `callcitieslist(@Query device)` → `CitiesListModel{ cities:[{id, state_id, city_name}] }`.

---

## BOOKING / JOBS (see PROVIDER_BOOKING_FLOW.md for full flow)

### `requestService?` (shared endpoint, 3 Retrofit bindings)
- POST · `Authorization: Bearer` + `Accept` · Success (all): `"Request saved successfully."`
- `doAcceptRequestDetails` → `AcceptRequestModel` · **Accept:** `mode:"accept", customer_id, driver_id, customer_location, service_id, category, customer_lat, customer_lon, drop_lat, drop_lon, drop_location, payment_type, total_agent, total_days, promocode, special_instruction, language, customer_booking_id [, stripe_token, card_number][, booking_id, trip_type, start_date_time, end_date_time]`
- `doGetRequestStatusDetails` → `RequestStatusModel` · **Transitions:** `driver_id, booking_id, language` + `mode` ∈ {`start_driver`,`reached`,`start_service`}
- `doGetEndRequestStatusDetails` → `EndRequestModel` · **Complete:** `mode:"finish_service", driver_id, booking_id, customer_id, material_fee, misc_charge, driver_discount, distance, drop_location, drop_lat, drop_lon, payment_type, total_agent, total_days, language [, wallet_amount, total_amount]`

### `removeDriver` — Remove provider after reject
- POST · none · `docallRemoveDriver` → `BankModel` · **Body:** `booking_id, driver_id` · Success: `"success"`.

### `cancelJobs?` — Cancel in-progress job
- POST · Bearer+Accept · `doCancelJobDetails` → `CancelJobModel` · **Body:** `booking_id, driver_id, reason, mode:"driver", language` · Success: `"Cancelled successfully."`.

### `pendingJobs?` / `upcomingJobs?` / `pastjobs?` — Job lists
- POST · Bearer+Accept · `doCustomerPendingJobs`/`doCustomerUpcomingJobs`/`doCustomerPastJobs` → `PendingJobsModel`/`UpcomingJobsModel`/`PastJobsModel`
- **Body:** `user_id`(=driverId)`, mode:"driver"` (past also `language`) · Success: `"Fetching {pending|upcoming|past} jobs successful."`.

### `scheduleJobAccept?` — Accept pending scheduled job
- POST · Bearer+Accept · `doAcceptPendingJobs` → `DeclinePendingModel` · **Body:** `booking_id` · Success: `"successfully updated"`.

### `scheduledcancelJobs?` — Decline/cancel scheduled job
- POST · Bearer+Accept · `doDeclinePendingJobs` → `DeclinePendingModel` · **Body:** `booking_id[, driver_id], mode:"driver"` · Success: `"Cancelled successfully."`.

### `scheduletripStart?` — Start an upcoming job
- POST · Bearer+Accept · `doStartUpcomingJobs` → `StartUpcomingTripModel` · **Body:** `booking_id, driver_id, mode:"accept"` · Success: `"Request saved successfully."` → launches live trip.

### Transfer / backup (SOS) — `VolunteerTrip`
| `doBackupRequestSendDetails` → `SendRequestModel` | `mode:"request", booking_id, driver_id, transfer_status:"1"` |
| `doAcceptBackupRequestDetails` → `AcceptRequestModel` | `mode:"accept", booking_id, from_driver_id, to_driver_id, transfer_status` |
| `doGetBackupRequestStatusDetails` → `RequestStatusModel` | `booking_id, from_driver_id, to_driver_id, transfer_status, mode` |
| `doCancelBackupJobDetails` → `CancelJobModel` | `mode:"cancel", booking_id, from_driver_id, to_driver_id[, transfer_status]` |
| `volunteerTimeout` (`docallTimeout` → `BankModel`) | `booking_id, driver_id, type:"app", transfer_status:"1", reference_id` |

---

## AVAILABILITY / LOCATION

### `checkin?` — Online/offline toggle + location
- POST · Bearer+Accept · `doDriverUpdateLocation` → `DriverLocationUpdateModel`
- **Body:** `id, current_lat, current_lon, status("1"|"0"), language` · Success: `"driver check-in successful."` / `"driver check-out successful."`; denials `"Select city before checkIn"`, `"Please upload an expiry date for the document"`.

### `viewDriverDateSlot` / `updateDriverSlot` / `viewDriverSlots` — per-date slots
| `doGetNewAvailable` → `NewDateAvailListModel` | `driver_id, date` | success `"success"` |
| `doUpdateNewAvailable` → `UpdateAvailListModel` | `driver_id, slot("[1, 2, 5]"), date` | `"Slot added/upddated successfully."` |
| `doGetNewViewDriverAvailable` → `ViewAvailListModel` | `driver_id` | `"Driver slots listed successfully."` |

Slot ids are 1–24 (hourly), mapping to `Utils.timeArray` labels.

---

## FINANCE

### `TransactionDriver?` — Wallet transactions
- POST · Bearer+Accept · `doWalletTransactionAll` → `WalletAllModel` · **Body:** `driver_id, language` · Success: `"Transactions listed successfully."` · `data: List<WalletAllData>`.

### `driverAddMoney?` — Add wallet money (Stripe; call site commented)
- POST · Bearer · `doUpdateWalletAmount` → `WalletAddMoneyModel` · **Body:** `driver_id, transaction_amount, transaction_mode:"card", transaction_token, card_number` · Success: `"Money added in wallet successfully."`.

### `BonusTransactionList?` — Bonus history (paginated)
- POST · Bearer+Accept · `doBonusTransactionAll` → `RideHistoryModel` · **Body:** `driver_id, language, offset` · Success: `"Transactions listed successfully."`.

### `driverPayoutRequest` — Request payout
- POST · none · `callPayout` → `PayoutModel` · **Body:** `driver_id, amount` · On `success` → toast + EventBus `"payout added"`.

### `payout_history` — Payout history
- POST · none · `callPayoutTransaction` → `PayoutTransactionHistoryModel` · **Body:** `driver_id` · Returns `result[], current_status, current_payout_message`.

### `bankDetailsGet?` — Bank list (Stripe Connect)
- POST · none · `doGetBankDetails` → `BankModel{ stripe_document_status, data:[Datum], message }` · **Body:** `id`.

### `stripe/create_account` — Stripe onboarding URL
- POST · none · `createBankAccount` → `WebResModel{status,url,id}` · **Body:** `driver_id` · Opens `WebViewActivity` at `url`.

### `bankDetailsUpdate?` — Manual bank update (legacy)
- POST · Bearer · `doBankUpdateDetails` → `BankUpdateModel` · **Body:** `id, bank_email, account_holder_name, bank_name, bank_location, bank_code, bank_account_number` · Success: `"Driver bank details updated  successfully."`.

### `cashPayment` — Cash settlement at completion
- POST · Bearer+Accept · `doUpdatePaymentDetails` → `PaymentUpdateModel` · **Body:** `booking_id, driver_id, customer_id` · Success: `"success"`.

### `stripePayment` — Card/end-trip settlement
- POST · Bearer+Accept · `doStripUpdateEndTrip` → `StripeEndTModel` · **Body:** `booking_id, amount, driver_id, customer_id, mode:"0", material_fee, misc_charge, driver_discount, distance, type, language` · Success: `"Money debited successfully."`/`"success"`.

### Subscription
| `subscriptionList` | `doGetsubscriptionList` → `ListViewSubsModel` | `user_type:"2"` |
| `subscriptionGetList` | `doGetsubscriptionGetList` → `ViewSubsModel` | Bearer+Accept · `plan_id` |
| `buySubscription` | `doUpdatesubscriptionList` → `UpdateSubsModel` | `customer_id, plan_id` |
| `subscription_history` | `doSubscriptionHistory` → `SubsHistoryModel` | `cus_id, offset, user_type:"2"` |
| `updateAutoRenew?` | `doUpdateAutoRenew` → `CancelAutoRenewPlanModel` | `customer_id, subscription_id, user_type:"2", action(enable/disable), auto_renew(1/0)` · Success `"auto renew updated successfully"` |
| `checkSubscription?` | `doCheckSubscription` → `BuyPlanModel` | `customer_id` |
| `createCheckoutSession?` | `doCreateCheckoutSessionSubscription` → `BuyPlanWebHookModel` | `user_id, plan_id, auto_renew:"1", type:"2"` |
| `checkService?` | `doCheckSubscriptionSubCategory` → `CheckPlanModel` | **(defined, unused)** |

---

## NOTIFICATIONS / SUPPORT / MISC

### `notifysend?` — Push provider→user (e.g. reject)
- POST · `Authorization` (empty string at call site) · `doNotifySend` → `PushModel` · **Body:** `customer_id, driver_id, mode:"reject", language` · Success: `"notification send successfully"`.

### `savepanic?` — Panic / SOS
- POST · none · `doUpdateAddPanic` → `PanicAddAdminModel` · **Body:** `driver_id, mode:"driver", location_link` · Success: `"Panic Call details updated successfully."`.

### `DriverFeedback?` — Reviews the provider received
- POST · Bearer+Accept · `doGetUserFeedback` → `UserFeedbackModel` · **Body:** `driver_id`.

### `DriverFeedbackStore?` — Provider rates the user
- POST · Bearer+Accept · `doUpdateRatingUser` → `RatingUserModel` · **Body:** `from_id, to_id, job_id, rating, status:"0", feedback` · Success: `"Feedback received"`.

### `pages?` / `Support?` — Support pages
| `doGetSupportDetails` → `AppSupportPagesModel` | Bearer · (empty) | `"Pages listed successfully."` |
| `doGetAppSupportDetails` → `AppSupportModel` | Bearer · `page_name` | `"Pages listed successfully."` |

### `delete_account_request` / `delete_account_confirm` — Delete account
| `doGetDeleteDetails` → `DeleteModel` | `id, type:"driver", mobile_type:"android", token` | `"successfully unique code sent to mail"` |
| `doGetDeleteConfirmDetails` → `DeleteModel` | `id, type:"driver", unique_code` | `result_message == "success"` |

### `appsetting` — App configuration
- POST · none · `doGetAppSettings` → `AppSettingWebModel{ data, app_setting:AppSetting, sms_templates, currency, language, message }` · Success: `"App settings listed successfully."`
- Key `AppSetting` fields cached: `payment_publisher_key`→`SP_COMMON_STRIPE_PUBLISH_KEY`, `payment_secret_key`→`SP_COMMON_STRIPE_SECRET_KEY`, `timedelay_accept`→`SP_COMMON_REQUEST_TIME_DELAY_ACCEPT`, `distance_unit`→`SP_COMMON_DISTANCE_UNIT`, `guard_subscription`→`SP_COMMON_SUBSCRIPTION_PAGE`, `wallet_minimum`, `referral_amount`, `tawk_url/property_url/property_id`, `police_number`, verification flags, matching params (`distance_limit`, `driver_search_radius`, `approximate_time`).

### Chat / Voice (Twilio)
| `chatAccessToken` (GET) | `doChatAccessToken(@Query device, @Query identity)` → `ChatAccessModel{identity, token}` | device=`driverId`, identity=`mIdentity` |
| `accesstoken` (GET) | `doCallAccessToken(@Query device, @Query identity)` → `ChatAccessModel` | device=`"android_driver"`, identity=`"dofloo"+driverId` |
| `message_notify` | `addSendMessageNotify` → `BankModel` | **(defined, unused)** |

### LinkedIn
| `userinfo` (GET) | `doGetLinkedInUserInfo(@Header Authorization)` → `LinkedInUserDetailsModel` | LinkedIn base URL |

### Defined-but-unused endpoints
`checkService?`, `message_notify`, `geofensPushNotification` (`doSendPushNotifyGeofence`), `auth/SocialLogDriver?`, `forgot_password?`, `verifyMobile?` (active flows use alternatives). Kept here for completeness.

---

## Master booking-API table

| # | API | Method | Endpoint | Auth | Purpose | Called From |
|---|---|---|---|---|---|---|
| 1 | Accept | POST | `requestService?` | Bearer | Accept on-demand request | `MainActivity.callAcceptRequest` |
| 2 | On-the-way | POST | `requestService?` | Bearer | mode=start_driver | `MainActivity.callRequestServicesStatus` |
| 3 | Reached | POST | `requestService?` | Bearer | mode=reached | `MainActivity` |
| 4 | Start service | POST | `requestService?` | Bearer | mode=start_service | `MainActivity` |
| 5 | Complete | POST | `requestService?` | Bearer | mode=finish_service | `MainActivity.callEndtrip` |
| 6 | Remove on reject | POST | `removeDriver` | none | drop from candidates | `MainActivity.callRemoveDriver` |
| 7 | Cancel job | POST | `cancelJobs?` | Bearer | cancel with reason | `MainActivity.callCancelJobUserUpdate` |
| 8 | Card settle | POST | `stripePayment` | Bearer | end-trip card | `MainActivity.callStripeUpdate` |
| 9 | Cash settle | POST | `cashPayment` | Bearer | end-trip cash | `MainActivity.updateCashonHandPayment` |
| 10 | Rate user | POST | `DriverFeedbackStore?` | Bearer | provider→user rating | `MainActivity.callUpdateRating` |
| 11 | Pending list | POST | `pendingJobs?` | Bearer | scheduled pending | `YourJobsPendingFragment` |
| 12 | Accept pending | POST | `scheduleJobAccept?` | Bearer | accept scheduled | `YourJobsPendingFragment` |
| 13 | Decline pending | POST | `scheduledcancelJobs?` | Bearer | decline scheduled | `YourJobsPendingFragment` |
| 14 | Upcoming list | POST | `upcomingJobs?` | Bearer | accepted scheduled | `YourJobsUpcomingFragment` |
| 15 | Start upcoming | POST | `scheduletripStart?` | Bearer | start scheduled | `YourJobsUpcomingFragment` |
| 16 | Cancel upcoming | POST | `scheduledcancelJobs?` | Bearer | cancel scheduled | `YourJobsUpcomingFragment` |
| 17 | Past list | POST | `pastjobs?` | Bearer | history | `YourJobsPastFragment` |
| 18 | Online + location | POST | `checkin?` | Bearer | availability toggle | `MainActivity` |
| 19 | Transfer send | POST | `VolunteerTrip` | none | transfer job | `YourJobsUpcomingFragment` |
