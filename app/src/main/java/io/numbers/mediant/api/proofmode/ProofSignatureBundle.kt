package io.numbers.mediant.api.proofmode

data class ProofSignatureBundle(
    val proof: String,
    val proofSignature: String,
    val mediaSignature: String
)