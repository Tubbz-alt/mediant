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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.security.MessageDigest
import java.util.*

private const val TAG = "ZION"
private const val DEV_WALLET_NAME = "DEV_WALLET_NAME"
private const val ETHEREUM_TYPE = 60

object ZionWrapper : CoroutineScope by MainScope() {
    private val zkma: HtcWalletSdkManager
        get() = HtcWalletSdkManager.getInstance()
    private var uniqueId: Long? = null

    fun init(context: Context, applicationContext: Context) = launch(Dispatchers.IO) {
        val result = zkma.init(applicationContext)
        val dialogHandler = createZionInitResultDialogHandler(context)
        val message = dialogHandler.obtainMessage()
        when (result) {
            RESULT.SUCCESS -> {
                Log.i(TAG, "Zion SDK Version: ${zkma.moduleVersion}")
                Log.i(TAG, "Zion API Version: ${zkma.apiVersion}")
                message.what = 0
                message.sendToTarget()
                createWalletSeed(DEV_WALLET_NAME)
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
                message.arg1 = result
                message.sendToTarget()
            }
        }
    }

    private fun createWalletSeed(walletName: String) {
        val sha256 = getHashFromString(walletName)
        Log.i(TAG, "Wallet Name: $walletName")
        Log.i(TAG, "sha256: $sha256")
        uniqueId = zkma.register(walletName, sha256)
        uniqueId?.also {
            when (val result = zkma.createSeed(it)) {
                RESULT.SUCCESS -> {
                    Log.i(
                        TAG,
                        "Eth sendPublicKey: ${zkma.getSendPublicKey(it, ETHEREUM_TYPE).key}"
                    )
                    Log.i(
                        TAG,
                        "Eth receivePublicKey ${zkma.getReceivePublicKey(it, ETHEREUM_TYPE).key}"
                    )
                }
                RESULT.E_TEEKM_SEED_EXISTS -> {
                    Log.i(TAG, "Seed has been already generated.")
                }
                else -> {
                    Log.e(TAG, "Create seed result: $result")
                }
            }
        }
    }

    fun getHashFromString(str: String): String {
        val bytes = str.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digest = messageDigest.digest(bytes)
        return digest.fold("", { s, it -> s + "%02x".format(it) })
    }

    private fun createZionInitResultDialogHandler(context: Context): Handler {
        return object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                val builder = AlertDialog.Builder(context)
                val text = when (msg.what) {
                    0 -> "Zion Initialization success."
                    1 -> "Please update your system in order to use Zion."
                    2 -> "Zion SDK can't support rooted device."
                    else -> "Zion initialization failed. Error code: ${msg.arg1}"
                }
                builder.setMessage(text).setNeutralButton("ok") { _, _ -> }
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    fun signMessage(hexData: String): ByteArray {
        Log.d(TAG, hexData)
        val message = JSONObject()
        message.put("version", "45")
        message.put("data", hexData)

        val json = JSONObject()
        json.put("path", "m/44'/60'/0'/0/0")
        json.put("message", message)

        val signature = ByteArrayHolder()
        val msgResult = zkma.signMessage(uniqueId!!, ETHEREUM_TYPE, json.toString(), signature)
        Log.i(TAG, "zkma.signMessage result: $msgResult")
        Log.i(TAG, "signature: ${Arrays.toString(signature.byteArray)}")
        return signature.byteArray
    }
}