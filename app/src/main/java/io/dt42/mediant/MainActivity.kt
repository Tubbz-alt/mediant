package io.dt42.mediant

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import io.dt42.mediant.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

private const val CAMERA_REQUEST_CODE = 0
private const val CURRENT_PHOTO_PATH = "CURRENT_PHOTO_PATH"
private const val TAG = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity() {
    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewPager.adapter = SectionsPagerAdapter(this, supportFragmentManager)
        tabs.setupWithViewPager(viewPager)

        TextileWrapper.initTextile(applicationContext)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(CURRENT_PHOTO_PATH, currentPhotoPath)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentPhotoPath = savedInstanceState.getString(CURRENT_PHOTO_PATH)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                dispatchTakePictureIntent()
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
                TextileWrapper.listThread()
                true
            }
            R.id.actionListImages -> {
                TextileWrapper.listImages()
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
                    currentPhotoPath?.apply {
                        TextileWrapper.addImage(this)
                        viewPager.currentItem = 1
                    }
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e(TAG, "Error occurred while creating the File")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri =
                        FileProvider.getUriForFile(this, "$packageName.provider", it)
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

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.TAIWAN).format(Date())
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", filesDir).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
}