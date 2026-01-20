package com.example.scicalc.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BillingManager(private val context: Context) : PurchasesUpdatedListener {
    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private val _billingState = MutableStateFlow(BillingState())
    val billingState: StateFlow<BillingState> = _billingState

    fun startConnection() {
        if (billingClient.isReady) return
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                _billingState.value = _billingState.value.copy(
                    isReady = billingResult.responseCode == BillingClient.BillingResponseCode.OK,
                    message = billingResult.debugMessage
                )
            }

            override fun onBillingServiceDisconnected() {
                _billingState.value = _billingState.value.copy(isReady = false)
            }
        })
    }

    fun queryDonationProduct(productId: String) {
        if (!billingClient.isReady) return
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()
        billingClient.queryProductDetailsAsync(params) { result, detailsList ->
            _billingState.value = _billingState.value.copy(
                productDetails = detailsList.firstOrNull(),
                message = result.debugMessage
            )
        }
    }

    fun launchPurchase(activity: Activity, productDetails: ProductDetails?) {
        if (!billingClient.isReady || productDetails == null) return
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            )
            .build()
        billingClient.launchBillingFlow(activity, params)
    }

    fun restorePurchases() {
        if (!billingClient.isReady) return
        billingClient.queryPurchasesAsync(BillingClient.ProductType.INAPP) { _, purchases ->
            purchases.forEach { acknowledgeIfNeeded(it) }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { acknowledgeIfNeeded(it) }
            _billingState.value = _billingState.value.copy(message = "Thanks for your support!")
        } else {
            _billingState.value = _billingState.value.copy(message = billingResult.debugMessage)
        }
    }

    private fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.isAcknowledged) return
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) {}
    }
}

data class BillingState(
    val isReady: Boolean = false,
    val productDetails: ProductDetails? = null,
    val message: String = ""
)
