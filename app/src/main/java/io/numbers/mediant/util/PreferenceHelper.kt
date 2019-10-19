package io.numbers.mediant.util

import android.app.Application
import androidx.preference.PreferenceManager
import io.numbers.mediant.R
import org.witness.proofmode.crypto.PgpUtils
import javax.inject.Inject

class PreferenceHelper @Inject constructor(application: Application) {

    private val sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
    private val preferenceKeyPersonalId =
        application.applicationContext.resources.getString(R.string.key_personal_thread_id)
    private val preferenceKeyWalletRecoveryPhrase =
        application.applicationContext.resources.getString(R.string.key_wallet_recovery_phrase)
    private val preferenceKeyProofModePgpPassword =
        application.applicationContext.resources.getString(R.string.key_proofmode_pgp_password)
    private val preferenceKeyProofModePgpPublicKey =
        application.applicationContext.resources.getString(R.string.key_proofmode_pgp_public_key)

    var personalThreadId: String?
        get() = sharedPreferences.getString(preferenceKeyPersonalId, null)
        set(value) = sharedPreferences.edit().putString(preferenceKeyPersonalId, value).apply()
    var walletRecoveryPhrase: String?
        get() = sharedPreferences.getString(preferenceKeyWalletRecoveryPhrase, null)
        set(value) = sharedPreferences.edit().putString(
            preferenceKeyWalletRecoveryPhrase, value
        ).apply()

    init {
        sharedPreferences.edit().putString(
            preferenceKeyProofModePgpPassword,
            PgpUtils.DEFAULT_PASSWORD
        ).apply()
        sharedPreferences.edit().putString(
            preferenceKeyProofModePgpPublicKey,
            PgpUtils.getInstance(application.applicationContext).publicKey
        ).apply()
    }
}