package io.numbers.mediant.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import io.numbers.mediant.BuildConfig.LOG_TO_FILE
import io.numbers.mediant.R
import io.numbers.mediant.adapters.ThreadsPagerAdapter
import io.numbers.mediant.models.ProofBundle
import io.numbers.mediant.wrappers.TextileWrapper
import io.numbers.mediant.wrappers.ZionWrapper
import io.textile.pb.Model
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.witness.proofmode.ProofMode
import org.witness.proofmode.crypto.HashUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val TAG = "MEDIANT"

private enum class PermissionCode(val value: Int) { DEFAULT(0), LOGGING(1) }

private val DEFAULT_PERMISSIONS = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_FINE_LOCATION
)
private const val LOGGING_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

private const val CAMERA_REQUEST_CODE = 0
private const val CURRENT_PHOTO_PATH = "CURRENT_PHOTO_PATH"

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        @Suppress("ConstantConditionIf")
        if (LOG_TO_FILE) {
            if (hasPermissions(LOGGING_PERMISSION)) initFileLogger()
            else askPermissions(LOGGING_PERMISSION, code = PermissionCode.LOGGING)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        TextileWrapper.init(applicationContext, false)
        ZionWrapper.init(this, applicationContext)
        initTabs()
        handleIntent(intent)
    }

    private fun initFileLogger() {
        Log.d(TAG, "Initialize logger to file: ${getExternalFilesDir(null)}/log")
        getExternalFilesDir(null)?.also {
            val logDir = File(it.absolutePath + "/log")
            val logFile = File(logDir, "logcat${System.currentTimeMillis()}.txt")
            if (!logDir.exists()) logDir.mkdir()
            try {
                Runtime.getRuntime().exec("logcat -c")
                Runtime.getRuntime().exec("logcat -f $logFile")
            } catch (e: IOException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
        }
    }

    private fun initTabs() {
        ThreadsPagerAdapter(this, supportFragmentManager).also {
            viewPager.adapter = it
            tabs.apply {
                setupWithViewPager(viewPager)
                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {}
                    override fun onTabUnselected(tab: TabLayout.Tab) {}
                    override fun onTabReselected(tab: TabLayout.Tab) {
                        it.smoothScrollToTop(tab.position)
                    }
                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_PHOTO_PATH, currentPhotoPath)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentPhotoPath = savedInstanceState.getString(CURRENT_PHOTO_PATH)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.also {
                if (it.toString().startsWith("https://www.textile.photos/invites/new")) {
                    Toast.makeText(this, "Try to accept invitation", Toast.LENGTH_LONG).show()
                    TextileWrapper.checkCafeMessagesAsync()
                    acceptExternalInvite(it)
                } else Log.e(TAG, "Failed to parse invitation acceptance: $it")
            }
        }
    }

    private fun acceptExternalInvite(uri: Uri) = launch(Dispatchers.IO) {
        val uriWithoutFragment = Uri.parse(uri.toString().replaceFirst('#', '?'))
        TextileWrapper.apply {
            invokeWhenNodeOnline {
                try {
                    acceptExternalInvitation(
                        uriWithoutFragment.getQueryParameter("id")!!,
                        uriWithoutFragment.getQueryParameter("key")!!
                    ).also { updatePublicThreadId(it) }
                } catch (e: Exception) {
                    val msg = "Accepting invitation with an error. Try again might help."
                    Log.e(TAG, Log.getStackTraceString(e))
                    launch(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    private fun updatePublicThreadId(newThread: Model.Thread?) {
        if (newThread != null) {
            TextileWrapper.publicThreadId?.also { TextileWrapper.removeThread(it) }
            TextileWrapper.publicThreadId = newThread.id
            Log.i(TAG, "New public thread ID: ${TextileWrapper.publicThreadId}")
            TextileWrapper.snapshotAllThreads()
            launch(Dispatchers.Main) { showPublicThread() }
        } else launch(Dispatchers.Main) {
            val msg = "You have already joined the thread"
            Log.i(TAG, msg)
            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (hasPermissions(*DEFAULT_PERMISSIONS)) dispatchTakePictureIntent()
                else askPermissions(*DEFAULT_PERMISSIONS, code = PermissionCode.DEFAULT)
                true
            }
            R.id.actionSettings -> {
                dispatchSettingsActivityIntent()
                true
            }
            R.id.actionListThread -> {
                TextileWrapper.logThreads()
                true
            }
            R.id.actionShowTestingInfo -> {
                Log.d(TAG, "${TextileWrapper.publicThreadId}")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    currentPhotoPath?.also {
                        uploadFeed(it)
                        showPersonalThread()
                    }
                }
            }
        }
    }

    // TODO: retry uploading if catch exception during TextileWrapper.addFile
    private fun uploadFeed(imageFilePath: String) = launch {
        val proofBundle = if (ZionWrapper.useZion) {
            withContext(Dispatchers.IO) { generateProofWithZion(imageFilePath) }
        } else withContext(Dispatchers.IO) { generateProof(imageFilePath) }
        Log.i(TAG, "proof bundle: $proofBundle")
        Toast.makeText(this@MainActivity, "Uploading via Textile $proofBundle", Toast.LENGTH_SHORT)
            .show()
        TextileWrapper.apply {
            personalThreadId?.also {
                try {
                    addFile(imageFilePath, it, Gson().toJson(proofBundle))
                } catch (e: Exception) {
                    Log.e(TAG, Log.getStackTraceString(e))
                }
                snapshotAllThreads()
            }
        }
    }

    private fun generateProof(filePath: String): ProofBundle {
        var imageSignature: String? = null
        var proof: String? = null
        var proofSignature: String? = null
        ProofMode.generateProof(this, Uri.fromFile(File(filePath)))?.also { fileHash ->
            ProofMode.getProofDir(fileHash)?.apply {
                if (exists()) {
                    listFiles()?.forEach {
                        when {
                            it.name.endsWith(".jpg${ProofMode.OPENPGP_FILE_TAG}") -> // photo signature file
                                imageSignature = it.readText()
                            it.name.endsWith(".jpg${ProofMode.PROOF_FILE_TAG}") -> // proof file
                                proof = it.readText()
                            it.name.endsWith(".jpg${ProofMode.PROOF_FILE_TAG}${ProofMode.OPENPGP_FILE_TAG}") -> // proof signature file
                                proofSignature = it.readText()
                        }
                    }
                }
            }
        }
        return ProofBundle(imageSignature!!, proof!!, proofSignature!!)
    }

    private fun generateProofWithZion(filePath: String): ProofBundle {
        val proof = generateProof(filePath).proof
        val mediaHash = HashUtils.getSHA256FromFileContent(File(filePath))
        val proofHash = ZionWrapper.getHashFromString(proof)
        return ProofBundle(
            ZionWrapper.signMessage(mediaHash).contentToString(),
            proof,
            ZionWrapper.signMessage(proofHash).contentToString()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionCode.DEFAULT.value -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent()
                } else showPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            PermissionCode.LOGGING.value -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initFileLogger()
                } else showPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun dispatchTakePictureIntent() =
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile = try {
                    createImageFile()
                } catch (e: IOException) {
                    Log.e(TAG, Log.getStackTraceString(e))
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoUri = FileProvider.getUriForFile(this, "$packageName.provider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
            }
        }

    private fun dispatchSettingsActivityIntent() = Intent(this, SettingsActivity::class.java).also {
        startActivity(it)
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.TAIWAN).format(Date())
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", filesDir).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun hasPermissions(vararg permissions: String): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun askPermissions(
        vararg permissions: String,
        code: PermissionCode = PermissionCode.DEFAULT
    ) {
        permissions.forEach {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, it)) {
                showPermissionRationale(it)
            }
        }
        ActivityCompat.requestPermissions(this, permissions, code.value)
    }

    private fun showPermissionRationale(permission: String) {
        when (permission) {
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> showDefaultPermissionsRationale()
            Manifest.permission.ACCESS_FINE_LOCATION -> showDefaultPermissionsRationale()
        }
    }

    private fun showDefaultPermissionsRationale() = Toast.makeText(
        this, R.string.permission_rationale_default, Toast.LENGTH_LONG
    ).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }

    fun showPublicThread() {
        viewPager.currentItem = 0
    }

    fun showPersonalThread() {
        viewPager.currentItem = 1
    }
}