package io.dt42.mediant

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.dt42.mediant.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

private const val CAMERA_REQUEST_CODE = 0

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewPager.adapter = SectionsPagerAdapter(this, supportFragmentManager)
        tabs.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                openCamera()
                true
            }
            R.id.actionSettings -> {
                openSettings()
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
                    imageView.setImageBitmap(data?.extras?.get("data") as Bitmap)
                }
            }
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            resolveActivity(packageManager)?.also {
                startActivityForResult(this, CAMERA_REQUEST_CODE)
            }
        }
    }

    private fun openSettings() {
        Intent(this, SettingsActivity::class.java).also {
            startActivity(it)
        }
    }
}