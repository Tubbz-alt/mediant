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
import io.dt42.mediant.ui.main.model.Post
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_personal_thread.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "MEDIANT_MAIN"
private const val CAMERA_REQUEST_CODE = 0

class MainActivity : AppCompatActivity() {

    private lateinit var currentPhotoPath: String
    private val zion = ZionUtility()

    // TODO: after Textile server is built up, change [adapter] as local variable
    private var adapter = SectionsPagerAdapter(this, supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
        zion.initZion(this@MainActivity, applicationContext)

        TextileWrapper.initTextile(applicationContext)
        Log.i(TAG, TextileWrapper.getTimestamp())
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewPager.currentItem = 1

                    /**
                     * TODO
                     * after we can get photos from Textile server, we do not need to use [adapter] to reference
                     * the fragments.
                     */
                    adapter.personalThreadFragment.posts.add(
                        0,
                        Post("username", currentPhotoPath, "description")
                    )

                    personalRecyclerView.adapter?.notifyItemInserted(0)
                    personalRecyclerView.layoutManager?.scrollToPosition(0)

                    zion.signMessage(currentPhotoPath.toByteArray().joinToString("") { "%02x".format(it) })

                    // TODO: fix error caused by "E/ZKMALog: data field length=1256963 in JSON is too long."
                    // zion.signMessage(File(currentPhotoPath).readBytes().joinToString("") { "%02x".format(it) })
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
                    Log.i("main", "Error occurred while creating the File")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(this, "$packageName.provider", it)
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