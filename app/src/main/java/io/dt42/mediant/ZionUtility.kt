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
import org.json.JSONObject
import java.security.MessageDigest
import java.util.*
import kotlin.concurrent.thread

private const val TAG = "ZION"

class ZionUtility {
    private val ethereumType = 60
    private var htcWalletSdkManager: HtcWalletSdkManager = HtcWalletSdkManager.getInstance()
    private var uniqueId: Long = -1

    private fun createSha256Hash(str: String): String {
        val bytes = str.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { s, it -> s + "%02x".format(it) })
    }

    private fun createWalletSeed(walletName: String) {
        val sha256 = createSha256Hash(walletName)
        Log.i(TAG, "Wallet Name: $walletName")
        Log.i(TAG, "sha256: $sha256")
        this.uniqueId = this.htcWalletSdkManager.register(walletName, sha256)
        val createSeedResult = this.htcWalletSdkManager.createSeed(this.uniqueId)
        Log.i(TAG, "createSeedResult: $createSeedResult")

        val sendPublicKeyHolderEthereum =
            this.htcWalletSdkManager.getSendPublicKey(this.uniqueId, this.ethereumType)
        val receivePublicKeyHolderEthereum =
            this.htcWalletSdkManager.getReceivePublicKey(this.uniqueId, this.ethereumType)

        Log.i(TAG, "Eth sendPublicKey: ${sendPublicKeyHolderEthereum.key}")
        Log.i(TAG, "Eth receivePublicKey ${receivePublicKeyHolderEthereum.key}")
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
                    Log.i(TAG, "Zion SDK Version: $sdkVersion")
                    Log.i(TAG, "Zion API Version: $apiVersion")
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

    fun signMessage(hexData: String) {
        Log.d(TAG, hexData)
        val message = JSONObject()
        message.put("version", "45")
        message.put("data", hexData)

        val json = JSONObject()
        json.put("path", "m/44'/60'/0'/0/0")
        json.put("message", message)

        thread {
            val signature = ByteArrayHolder()
            val msgResult =
                this.htcWalletSdkManager.signMessage(this.uniqueId, this.ethereumType, json.toString(), signature)
            Log.i(TAG, "message result: $msgResult")
            Log.i(TAG, "signature: ${Arrays.toString(signature.byteArray)}")
        }
    }

    private fun createZionInitResultDialogHandler(activity: MainActivity): Handler {
        return object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(message: Message) {
                val builder = AlertDialog.Builder(activity)
                val msg: String = when (message.what) {
                    0 -> "Zion Initialization success."
                    1 -> "Please update your system in order to use Zion."
                    2 -> "Zion SDK can't support rooted device."
                    else -> "Zion initialization failed. Error code: ${message.arg1}"
                }
                builder.setMessage(msg)
                    .setNeutralButton("ok") { _, _ -> }
                val dialog = builder.create()
                dialog.show()
            }
        }
    }
}