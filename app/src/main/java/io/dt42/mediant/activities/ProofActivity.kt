package io.dt42.mediant.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.dt42.mediant.R
import kotlinx.android.synthetic.main.activity_proof.*

class ProofActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proof)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
