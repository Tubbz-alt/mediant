package io.numbers.mediant.api.proofmode

import android.app.Application
import android.net.Uri
import io.numbers.mediant.util.deleteDirectory
import org.witness.proofmode.ProofMode
import java.io.File
import java.io.IOException
import javax.inject.Inject

const val proofFileSuffix = ProofMode.PROOF_FILE_TAG
const val mediaSignatureFileSuffix = ProofMode.OPENPGP_FILE_TAG
const val proofSignatureFileSuffix = "${ProofMode.PROOF_FILE_TAG}${ProofMode.OPENPGP_FILE_TAG}"

// TODO: catch throws by showing error message on snackbar
class ProofModeService @Inject constructor(private val application: Application) {

    fun generateProofAndSignatures(filePath: String): ProofSignatureBundle {
        val mediaFileHash =
            ProofMode.generateProof(application.applicationContext, Uri.fromFile(File(filePath)))
        if (mediaFileHash.isNullOrEmpty()) throw IOException("Cannot generate proof.")
        else {
            return getProofSignatureBundle(mediaFileHash).also {
                removeProofSignatureFiles(mediaFileHash)
            }
        }
    }

    private fun getProofSignatureBundle(mediaFileHash: String): ProofSignatureBundle {
        val proofDir = ProofMode.getProofDir(mediaFileHash)
        if (proofDir == null || !proofDir.exists()) throw IOException("Cannot locate proof directory: $mediaFileHash")
        else {
            var proof = ""
            var proofSignature = ""
            var mediaSignature = ""
            proofDir.listFiles()?.forEach {
                when {
                    it.name.endsWith(proofSignatureFileSuffix) -> proofSignature = it.readText()
                    it.name.endsWith(proofFileSuffix) -> proof = it.readText()
                    it.name.endsWith(mediaSignatureFileSuffix) -> mediaSignature =
                        it.readText()
                }
            }
            checkProofSignatureBundleCompletion(proof, proofSignature, mediaSignature)
            return ProofSignatureBundle(proof, proofSignature, mediaSignature)
        }
    }

    private fun checkProofSignatureBundleCompletion(
        proof: String,
        proofSignature: String,
        mediaSignature: String
    ) {
        if (proof.isEmpty()) throw IOException("Cannot read proof from file.")
        if (proofSignature.isEmpty()) throw IOException("Cannot read proof signature from file.")
        if (mediaSignature.isEmpty()) throw IOException("Cannot read media signature from file.")
    }

    private fun removeProofSignatureFiles(mediaFileHash: String) {
        val proofDir = ProofMode.getProofDir(mediaFileHash)
        if (proofDir == null || !proofDir.exists()) throw IOException("Cannot locate proof directory: $mediaFileHash")
        else proofDir.deleteDirectory()
    }
}