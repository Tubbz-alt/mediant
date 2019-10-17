package io.numbers.mediant.util

import android.app.Application
import androidx.preference.PreferenceManager
import io.numbers.mediant.R
import javax.inject.Inject

class PreferenceHelper @Inject constructor(private val application: Application) {

    private val sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
    private val preferenceKeyPersonalId =
        application.applicationContext.resources.getString(R.string.key_personal_thread_id)
    private val preferenceKeyWalletRecoveryPhrase =
        application.applicationContext.resources.getString(R.string.key_wallet_recovery_phrase)

    var personalThreadId: String?
        get() = sharedPreferences.getString(preferenceKeyPersonalId, null)
        set(value) = sharedPreferences.edit().putString(preferenceKeyPersonalId, value).apply()
    var walletRecoveryPhrase: String?
        get() = sharedPreferences.getString(preferenceKeyWalletRecoveryPhrase, null)
        set(value) = sharedPreferences.edit().putString(
            preferenceKeyWalletRecoveryPhrase, value
        ).apply()
}