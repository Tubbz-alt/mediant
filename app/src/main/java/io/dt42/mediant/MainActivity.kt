package io.dt42.mediant

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
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import io.dt42.mediant.model.ProofBundle
import io.dt42.mediant.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.witness.proofmode.ProofMode
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

private enum class PermissionCode(val value: Int) { DEFAULT(0) }

private val DEFAULT_PERMISSIONS = listOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_FINE_LOCATION
)

private const val CAMERA_REQUEST_CODE = 0
private const val CURRENT_PHOTO_PATH = "CURRENT_PHOTO_PATH"
private const val TAG = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity() {
    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initTabs()

        TextileWrapper.init(applicationContext, true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_PHOTO_PATH, currentPhotoPath)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentPhotoPath = savedInstanceState.getString(CURRENT_PHOTO_PATH)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (hasPermissions(DEFAULT_PERMISSIONS)) {
                    dispatchTakePictureIntent()
                }
                true
            }
            R.id.actionSettings -> {
                dispatchSettingsActivityIntent()
                true
            }
            R.id.actionAcceptExternalInvitation -> {
                // https://www.textile.photos/invites/new#id=QmTnxH2U5CXZ2NT1JZQFdU5n8capSXTDiXm5evYrBMZMXe&key=9X6obPAc4Gm3HqBRJXqFW6ayyxudSYC2fpTGmgKGQhfh71TQHgoSN1MTSjH9&inviter=P4ibDYs2oa2mz9unQaPrJRtuso83NUSAebxVtQuniUjUqe4K&name=nbsdev&referral=MSCES
                thread {
                    Log.d(TAG, "========== accepting external invitation started ===========")
                    TextileWrapper.acceptExternalInvitation(
                        "QmTnxH2U5CXZ2NT1JZQFdU5n8capSXTDiXm5evYrBMZMXe",
                        "9X6obPAc4Gm3HqBRJXqFW6ayyxudSYC2fpTGmgKGQhfh71TQHgoSN1MTSjH9"
                    )
                    Log.d(TAG, "========= accepting external invitation finished ===========")
                }
                true
            }
            R.id.actionListThread -> {
                TextileWrapper.logThreads()
                true
            }
            R.id.actionShowTestingInfo -> {
                PreferenceManager.getDefaultSharedPreferences(this).apply {
                    Log.d(TAG, "autoNotarize ${getBoolean("autoNotarize", false)}")
                    Log.d(TAG, "trackLocation ${getBoolean("trackLocation", false)}")
                    Log.d(TAG, "trackDeviceId ${getBoolean("trackDeviceId", false)}")
                    Log.d(TAG, "trackMobileNetwork ${getBoolean("trackMobileNetwork", false)}")
                }
                true
            }
            R.id.actionInitPrivateThread -> {
                TextileWrapper.initPersonalThread()
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
                        thread {
                            val proofBundle = generateProof(it)
                            Log.d(TAG, "proof bundle: $proofBundle")
                            runOnUiThread {
                                Toast.makeText(
                                    this,
                                    "Uploading via Textile $proofBundle",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            TextileWrapper.addImage(
                                it,
                                TextileWrapper.profileAddress,
                                //"${proofBundle.proof}\n${proofBundle.imageSignature}\n${proofBundle.proofSignature}"
                                proofBundle.proof
                            )
                        }
                        viewPager.currentItem = 1
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionCode.DEFAULT.value -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    dispatchTakePictureIntent()
                } else {
                    showPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun initTabs() {
        val adapter = SectionsPagerAdapter(this, supportFragmentManager)
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                adapter.getItem(tab.position).apply {
                    view?.findViewById<RecyclerView>(R.id.recyclerView)?.smoothScrollToPosition(0)
                }
            }

        })
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e(TAG, "Error occurred while creating the File")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(this, "$packageName.provider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
            }
        }
    }

    private fun dispatchSettingsActivityIntent() {
        Intent(this, SettingsActivity::class.java).also {
            startActivity(it)
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

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.TAIWAN).format(Date())
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", filesDir).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun hasPermissions(permissions: List<String>): Boolean {
        return if (permissions.all {
                ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }) {
            true
        } else {
            askPermissions(permissions)
            false
        }
    }

    private fun askPermissions(permissions: List<String>) {
        permissions.forEach {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, it)) {
                showPermissionRationale(it)
            }
        }
        ActivityCompat.requestPermissions(
            this,
            permissions.toTypedArray(),
            PermissionCode.DEFAULT.value
        )
    }

    private fun showPermissionRationale(permission: String) {
        when (permission) {
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> showDefaultPermissionsRationale()
            Manifest.permission.ACCESS_FINE_LOCATION -> showDefaultPermissionsRationale()
        }
    }

    private fun showDefaultPermissionsRationale() {
        Toast.makeText(
            this,
            R.string.permission_rationale_default,
            Toast.LENGTH_LONG
        ).apply {
            setGravity(Gravity.CENTER, 0, 0)
            show()
        }
    }
}