package com.olivetti.club.repositories

import android.content.Context

class MerchantRepository(context: Context) {

    companion object {
        private const val PREFERENCES_KEY = "OLIVETTI_CLUB_PREFERENCES"
        private const val MERCHANT_KEY = "MERCHANT_KEY"
    }

    val preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)

    fun loadMerchant(): String? {
        return preferences.getString(MERCHANT_KEY, null)
    }

    fun saveMerchant(merchant: String) {
        preferences.edit().putString(MERCHANT_KEY, merchant).apply()
    }
}