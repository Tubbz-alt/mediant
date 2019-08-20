package io.dt42.mediant

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.htc.htcwalletsdk.Export.HtcWalletSdkManager
import com.htc.htcwalletsdk.Export.RESULT
import com.htc.htcwalletsdk.Native.Type.ByteArrayHolder
import com.htc.htcwalletsdk.Security.Key.PublicKeyHolder
import java.security.MessageDigest
import java.util.*
import kotlin.concurrent.thread

class ZionUtility {
    private val ethereumType = 60
    private var htcWalletSdkManager: HtcWalletSdkManager = HtcWalletSdkManager.getInstance()
    private var uniqueId: Long = -1

    private fun createSha256Hash(str: String): String {
        val bytes = str.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    fun createWalletSeed(walletName: String) {
        val sha256 = createSha256Hash(walletName)
        Log.w("Wallet Name", walletName)
        Log.w("sha256", sha256)
        this.uniqueId = this.htcWalletSdkManager.register(walletName, sha256)
        val createSeedResult = this.htcWalletSdkManager.createSeed(this.uniqueId)
        Log.w("createSeedResult", createSeedResult.toString())

        val sendPublicKeyHolderEthereum =
            this.htcWalletSdkManager.getSendPublicKey(this.uniqueId, this.ethereumType)
        val receivePublicKeyHolderEthereum =
            this.htcWalletSdkManager.getReceivePublicKey(this.uniqueId, this.ethereumType)

        Log.w("Eth sendPublicKey", sendPublicKeyHolderEthereum.key)
        Log.w("Eth receivePublicKey", receivePublicKeyHolderEthereum.key)
    }

    fun initZion(activity: MainActivity, applicationContext: Context) {
        thread {
            val mHtcWalletSdkManager = HtcWalletSdkManager.getInstance()
            val zionInitResult: Int = mHtcWalletSdkManager.init(applicationContext)
            val mHandler: Handler = createZionInitResultDialogHandler(activity)
            val message: Message = mHandler.obtainMessage()
            when (zionInitResult) {
                RESULT.SUCCESS -> {
                    val sdkVersion: String = mHtcWalletSdkManager.moduleVersion
                    val apiVersion: String = mHtcWalletSdkManager.apiVersion
                    println("Zion SDK Version: $sdkVersion")
                    println("Zion API Version: $apiVersion")
                    message.what = 0
                    message.sendToTarget()
                    createWalletSeed("Foobar")
                }

                RESULT.E_SDK_ROM_TZAPI_TOO_OLD -> {
                    // App should prompt the user to update ROM
                    message.what = 1
                    message.sendToTarget()
                }

                RESULT.E_TEEKM_TAMPERED -> {
                    // App should prompt the user it’s rooted device
                    message.what = 2
                    message.sendToTarget()
                }

                else -> {
                    message.what = 3
                    message.arg1 = zionInitResult
                    message.sendToTarget()
                }
            }
        }
    }

    fun getUniqueId(): Long {
        return this.uniqueId
    }

    fun signMessage(jsonStr: String) {
        thread {
            val signature = ByteArrayHolder()
            val msgResult =
                this.htcWalletSdkManager.signMessage(this.uniqueId, this.ethereumType, jsonStr, signature)
            Log.w("msgResult", "" + msgResult)
            Log.w("signature", Arrays.toString(signature.byteArray))
        }
    }

    private fun createZionInitResultDialogHandler(activity: MainActivity): Handler {
        val mMessageHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(message: Message) {
                val builder = AlertDialog.Builder(activity)
                val msg: String
                when (message.what) {
                    0 -> msg = "Zion Initialization success."
                    1 -> msg = "Please update your system in order to use Zion."
                    2 -> msg = "Zion SDK can't support rooted device."
                    else -> msg = "Zion initialization failed. Error code: ${message.arg1}"
                }
                builder.setMessage(msg)
                    .setNeutralButton("ok") { _, _ -> }
                val dialog = builder.create()
                dialog.show()
            }
        }
        return mMessageHandler
    }
}