# PROVIDER_DATA_MODELS.md — InstaGuard Provider

All models live in `app/src/main/java/com/instaguard/provider/Models/`. They are Gson POJOs with `@SerializedName` JSON keys. Below are the fields that matter for the booking flow and rebuild. **Driver = Provider/Guard.**

---

## Booking / request models

### `AcceptRequestModel` (response of `requestService?` accept)
`{ Boolean success; AcceptRequestDataModel service_request; String message }`

### `AcceptRequestDataModel` (the booking record) — `Models/AcceptRequestDataModel.java`
| JSON key | Type | Meaning |
|---|---|---|
| `customer_id` | String | User id |
| `driver_id` | String | Provider id |
| `customer_location` | String | Pickup address |
| `service_id` | String | Service (sub-category) id |
| `category` | String | Category id |
| `customer_lat` / `customer_lon` | String | Pickup coords |
| `payment_type` | String | wallet/card/cash |
| `promocode` | Object | Promo |
| `special_instruction` | String | Notes |
| `mode` | String | Request mode |
| `job_number` | String | Human job no. |
| `customer_first_name` / `customer_last_name` | String | User name |
| `customer_email` / `customer_phone_number` | String | User contact |
| `job_date` | String | Booking date |
| `accept_time` | String | Accept timestamp |
| `service_status` | Integer | Live status |
| `cancel_service_status` | Integer | Cancel flag |
| `driver_first_name`/`driver_last_name`/`driver_email`/`driver_phone_number` | String | Provider info |
| `unit_price` / `site_commission` / `total_amount` | Integer | Money |
| `strip_id` | String | Stripe id |
| `created_at` / `updated_at` | String | Timestamps |
| `booking_id` | Integer | **Server booking id** |

### `RequestStatusModel` / `RequestStatusData`
`{ success, service_request:RequestStatusData, message }` — `RequestStatusData` carries `booking_id` (read via `getBookingId()`) plus the same booking record shape. Used for start_driver/reached/start_service transitions.

### `EndRequestModel` / `EndRequestDataModel`
Response of `requestService?` finish_service. Same booking record shape; `message` gate `"Request saved successfully."`.

### `StartUpcomingTripModel` / `StartUpcomingTripData`
`{ success, service_request:StartUpcomingTripData, message }`. `StartUpcomingTripData` is the full booking row (`id, job_number, job_date, customer_*, driver_*, service_status, payment_type, booking_id, cancel_service_status, current_satus, cancel_job_satus, payment_satus, status, …`).

### `CancelJobModel` / `CancelJobDataModel`
Response of `cancelJobs?`. `{ ... message }` gate `"Cancelled successfully."`.

### `DeclinePendingModel`
`{ Integer booking_id; String message }` — response of `scheduleJobAccept?`, `scheduledcancelJobs?`.

### `SendRequestModel` (`Models/Request/SendRequestModel.java`)
Response of `VolunteerTrip` transfer request.

---

## Job-list models

### `PendingJobsModel` → `List<PendingJobsData>`
### `UpcomingJobsModel` → `List<UpcomingJobsData>`
### `PastJobsModel` → `List<PastJobsData>`
All: `{ Boolean success; List<...> service_request; String message }`.

**Common list-row fields** (`PendingJobsData`, `UpcomingJobsData`, `PastJobsData`):
| JSON key | Type | Notes |
|---|---|---|
| `booking_id` | Integer | |
| `job_date` | String | |
| `category_name` / `service_name` / `category_ar` / `service_ar` | String | localized |
| `customer_location` | String | |
| `total_days` | String/Int | scheduled length |
| `total_agent` | String/Int | guards requested |
| `service_status` | Integer | 6/7/8/9/13/14/15 |
| `customer_name` / `customer_phone_number` | String | (upcoming shows these) |
| `start_date_time` / `end_date_time` | String | (upcoming) |
| `payment_type`, `total_amount`, `unit_price`, `site_commission`, `extra_charge`, `misc_charge`, `driver_discount`, `accept_time`, `rating` | mixed | detail |

`PastJobsData` additionally: `rating, rating_status, job_number, job_type, driver_name, customer_avatar, driver_avatar, customer_id, driver_id, promocode, promo_discount, emergency_msg, extended_amount, extended_vat, vat_amount, group_driver_id, group_driver_data:List<GroupDriverDatum>, total_hrs, price_per_hr, extended_status, extended_hrs, trip_type, price_per_agent, level_type, main_level_amount, sub_level_amount, visit_fare`.

---

## Provider / User master model

### `ProfileModel`
`{ ProfileData data; Object rating; Object bonus_earning; String message }`

### `ProfileData` (master provider record) — `Models/ProfileData.java` (839 lines)
Identity: `id, first_name, last_name, social_id, social_type, avatar, email, country_code, phone_number, birth_date, gender, driver_certificate, phone_verified, email_verified`.
Services/location: `service_ids, availble_slots, address, city, state_id, country_id, postal_code`.
Locale: `language, currency, currency_symbol`.
Provider type: `provider_type, company_name`.
Bio: `service_description, profile_description`.
Bank: `payment_email, account_holder_name, bank_name, bank_location, bank_code, bank_account_number`.
Money: `wallet_balance, wallet, earnings, earn_cash, earn_card, driver_paid, admin_paid`.
Subscription: `subscription_id, subscription_status, subscription_name, subscription_amount, subscription_purchase, subscription_expiry, plan_type, free_km, free_job, subscription_description, period_type, period, remeaning_job, subscription_code, maximum_discount_amount, tow_service_id, appointment_service_id, subscription_history_id, auto_renew, discount_percentage, free_appointment_services`.
Meta: `device_type, device_token, login_token, last_seen, login_status, status, created_at, updated_at, referral_code, invite_code, salt`.

> **User (customer) info the provider sees comes from two places:** (1) the Firebase `trips` node at request time (`customer_id, customer_name, user_pickup, pickup_lat/lng, user_rating, special_ins`), and (2) the accept/booking response (`customer_first_name, customer_last_name, customer_email, customer_phone_number`). There is no dedicated "user profile" endpoint on the provider side.

### `LoginResults` → `LoginModel{access_token, token_type, expires_in, driver:DriverData}`
`DriverData`: `id, first_name, last_name, email, avatar, birth_date, state_id, country_id, address, phone_number, country_code, wallet_balance, referral_code, invite_code, currency, currency_symbol, language, service_ids`.

### `RegisterModel` → `RegisterData` (id, first_name, last_name, email, avatar, phone_number, country_code + others)

---

## Availability models
- `AvailabilityModel{ AvailabilityData data; String message }`; `AvailabilityData{ Object service_ids; AvailbleSlots availble_slots }`; `AvailbleSlots{ List<Integer> Monday..Sunday }` (Saturday field `saterday`).
- `Models/Availability/`: `NewDateAvailListModel→NewDateAvailData`, `UpdateAvailListModel→UpdateAvailListData`, `ViewAvailListModel→List<ViewAvailListData>`. Each `*Data`: `id, driver_id, date, slot:List<String>, status, created_at, updated_at`.

## Document models
- `DocumentListData{ List<DocumentList> data; message }`; `DocumentViewModel{ message; List<DocumentList> result; reject_status }`.
- `DocumentList`: `id, document_name, document_name_ar, status, country_id, expire_on, car_id, driver_id, document, document_proof, approved_status, expiry_date, expiry_status, reason, document_priority, get_licence_number, licence_number`.
- `UploadModel{result(path), message}`, `UploadDocModel{result, message}`, `ReUploadModel{message, result:ReUploadData}`.

## Finance models
- `WalletAllModel{ List<WalletAllData> data; message }`; `WalletAllData`: `id, transaction_token, customer_id, driver_id, transaction_type(credit|debit), transaction_date, transaction_amount(Double), transaction_mode, card_number, transaction_notes(+_ar), strip_response, transaction_status, created_at, updated_at`.
- `RideHistoryModel{ List<RideHistoryData> result; message; page; next_page }`; `RideHistoryData` bonus fields: `id, job_id, driver_id, bonus_amount, bonus_status, bonus_interval, deducation_*, created_at, updated_at`.
- `PayoutModel{ success, message, data:{id, stripe_payout_id, driver_id, email, arrival_date, amount, currency, destination, status, transfer_id, transfer_status} }`.
- `PayoutTransactionHistoryModel{ message, result:List<Result>{id, email, driver_id, description, amount, currency, paid_date, paid_time, request_date, request_time, status}, current_status, current_payout_message }`.
- `BankModel{ Integer stripe_document_status; List<Datum> data; message }`; `Datum{ bank_name, account_holder_name, routing_number, currency, last_digit, bank_status, is_default }`.
- `StripeEndTModel{ success, message }`, `PaymentUpdateModel{ message }`, `WalletAddMoneyModel{ success, service_request, wallet_balance, message }`, `WebResModel{ status, url, id }`.

## Subscription models (`Models/Subscription/`)
- `ListViewSubsModel→List<ListViewSubsData>{id, name, description, amount, period, period_type, free_km, free_job_request, status, plan_type}`.
- `SubsHistoryModel→List<SubsHistoryData>{id, order_id, cus_id, plan_id, purchase_date, expiry_date, plan_period, amount, status, name, auto_renew}`.
- `BuyPlanWebHookModel→BuyPlanWebHookData{id, url}`, `BuyPlanModel{result, message}`, `CancelAutoRenewPlanModel{message}`, `CheckPlanModel→CheckPlanData` (unused).

## Notification / feedback / chat models
- `PushModel{result, message}`, `GeofencePushNotificationModel{message, result:{booking_id, current_status, driver_id}}` (unused).
- `UserFeedbackModel{ List<UserFeedbackDataModel> data; message }`; `UserFeedbackDataModel{id, from_id, to_id, job_id, rating(Float), feedback, status, created_at, updated_at, avatar, name}`.
- `RatingUserModel{message}`.
- `ChatAccessModel{identity, token}`.
- `FirebaseModel` — POJO for Firebase offers/announcements + trip backup nodes.
- `PanicAddAdminModel{ PanicAddAdminDataModel response; message }`.

## App settings
- `AppSettingWebModel{ List<AppSupportDataModel> data; AppSetting app_setting; List sms_templates; List currency; List language; message }`.
- `AppSetting` (717 lines) — config incl. `payment_publisher_key, payment_secret_key, payment_mode, timedelay_accept, distance_unit, guard_subscription, wallet_minimum, referral_amount, phone_verification_driver, email_verification_driver, distance_limit, driver_search_radius, approximate_time, tawk_url, property_url, property_id, police_number, customer_manage_document, document_norms` + social/SMTP/SMS keys.
