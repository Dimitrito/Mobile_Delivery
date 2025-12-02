package com.mobiledelivery.data.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Моделі для PayPal API v2
 * PayPal API використовує snake_case для полів
 */

@Serializable
data class PayPalPaymentRequest(
    val intent: String = "CAPTURE",
    @SerialName("purchase_units")
    val purchase_units: List<PurchaseUnit>,
    @SerialName("application_context")
    val application_context: ApplicationContext? = null,
    @SerialName("payment_source")
    val payment_source: PaymentSource? = null
)

@Serializable
data class PaymentSource(
    @SerialName("paypal")
    val paypal: PayPalPaymentSource? = null
)

@Serializable
data class PayPalPaymentSource(
    @SerialName("experience_context")
    val experience_context: ExperienceContext? = null
)

@Serializable
data class ExperienceContext(
    @SerialName("payment_method_preference")
    val payment_method_preference: String? = "IMMEDIATE_PAYMENT_REQUIRED",
    @SerialName("brand_name")
    val brand_name: String? = null,
    @SerialName("locale")
    val locale: String? = null,
    @SerialName("landing_page")
    val landing_page: String? = null,
    @SerialName("shipping_preference")
    val shipping_preference: String? = null,
    @SerialName("user_action")
    val user_action: String? = "PAY_NOW",
    @SerialName("return_url")
    val return_url: String? = null,
    @SerialName("cancel_url")
    val cancel_url: String? = null
)

@Serializable
data class PurchaseUnit(
    val amount: Amount,
    val description: String? = null,
    @SerialName("reference_id")
    val reference_id: String? = null
)

@Serializable
data class Amount(
    @SerialName("currency_code")
    val currency_code: String,
    val value: String
)

@Serializable
data class ApplicationContext(
    @SerialName("return_url")
    val return_url: String? = null,
    @SerialName("cancel_url")
    val cancel_url: String? = null
)

@Serializable
data class PayPalPaymentResponse(
    val id: String,
    val status: String,
    val links: List<Link>? = null
)

@Serializable
data class Link(
    val href: String,
    val rel: String,
    val method: String? = null
)

@Serializable
data class PayPalCaptureRequest(
    @SerialName("payment_source")
    val payment_source: PaymentSource? = null
)

@Serializable
data class PayPalCaptureResponse(
    val id: String,
    val status: String,
    val payer: Payer? = null,
    @SerialName("purchase_units")
    val purchase_units: List<PurchaseUnitResponse>? = null
)

@Serializable
data class Payer(
    val name: PayerName? = null,
    @SerialName("email_address")
    val email_address: String? = null
)

@Serializable
data class PayerName(
    @SerialName("given_name")
    val given_name: String? = null,
    val surname: String? = null
)

@Serializable
data class PurchaseUnitResponse(
    val amount: Amount? = null,
    val payee: Payee? = null
)

@Serializable
data class Payee(
    @SerialName("email_address")
    val email_address: String? = null,
    @SerialName("merchant_id")
    val merchant_id: String? = null
)


