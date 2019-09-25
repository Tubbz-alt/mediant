package io.dt42.mediant.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonParseException
import io.dt42.mediant.R
import io.dt42.mediant.models.ProofBundle
import kotlinx.android.synthetic.main.activity_proof.*

const val PROOF_BUNDLE_EXTRA = "PROOF_BUNDLE_EXTRA"

class ProofActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proof)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent.extras?.getString(PROOF_BUNDLE_EXTRA)?.also { proofBundleJson ->
            try {
                Gson().fromJson(proofBundleJson, ProofBundle::class.java)?.also {
                    proof.text = it.proof
                    mediaSignature.text = it.mediaSignature
                    proofSignature.text = it.proofSignature
                }
            } catch (e: JsonParseException) {
                otherDetails.text = proofBundleJson
            }
        }
    }
}
