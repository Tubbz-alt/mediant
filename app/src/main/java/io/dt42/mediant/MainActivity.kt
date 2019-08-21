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
import kotlin.concurrent.thread

private const val TAG = "MEDIANT_MAIN"
private const val CAMERA_REQUEST_CODE = 0

class MainActivity : AppCompatActivity() {

    //private val zion = ZionUtility()

    private var tag: String = "mediant"
    private lateinit var currentPhotoPath: String

    // TODO: after Textile server is built up, change [adapter] as local variable
    private var adapter = SectionsPagerAdapter(this, supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
        //zion.initZion(this@MainActivity, applicationContext)

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
            R.id.actionAcceptExternalInvitation -> {
                thread {
                    Log.d(TAG, "actionAcceptExternalInvitation started!")
                    // the following id and key are used in the Exodus
                    TextileWrapper.acceptExternalInvitation(
                        "QmY399pXbrFCkminbh5oYu9wypBYkaM5UM34H3AfJt2vbP",
                        "2BphD5Yy8gS6wd1hKrWeY2yvaKBvGyJKeBe8itasqqJ72hQ5noKJjxW1H1mdg"
                    )
                    Log.d(TAG, "actionAcceptExternalInvitation finished!")
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
            R.id.actionGetProfile -> {
                TextileWrapper.getProfile()
                true
            }
            R.id.actionAddImage -> {
                TextileWrapper.addImageDev()
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

                    Log.i(tag, "========== Upload image start ==========")
                    TextileWrapper.addImage(currentPhotoPath, "SWENC" + TextileWrapper.getTimestamp() + "SWENC")
                    Log.i(tag, "========== Upload image completed ==========")

                    /*
                    zion.signMessage(
                        TextileWrapper.getTimestamp().toByteArray().joinToString("") { "%02x".format(it) }
                    ) { TextileWrapper.addImage(currentPhotoPath, it) }
                    */

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