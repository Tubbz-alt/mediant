# Mediant

## Information

* [Project book](https://docs.google.com/spreadsheets/d/1Bnnn1sVpDvghmtKWnTXVusBP_wekR_1J38quPwPTqGQ/edit#gid=0)
* [Trello Index Card](https://trello.com/c/7Ccg3kam)

## ProofMode Library

### Minimal Sample Code to Generate Proof with Signature

> NOTE: `mediaUri` (file:URI) might be able to convert between `mediaPath` (String) or `mediaUri` (content:URI), and the `content://` URI would be a better choice for future compatibility.

``` kotlin
fun generateProof(mediaUri: Uri) {
  val mediaHash = generateFileSha256(mediaUri)

  // options include:
  // 1. show device IDs
  // 2. show geolocation
  // 3. show mobile network
  // 4. show SafetyNet API results and OpenTimeStamps
  writeProof(mediaUri, mediaHash, options)
}

fun writeProof(mediaUri: Uri, mediaHash: String, options) {
  val mediaSignature: File
  val proof: File
  val proofSignature: File

  // sign the media file and save the detached signature (by OpenPGP's PGPSignatureGenerator) to [mediaSignature]
  savePgpSignature(mediaUri, mediaSignature)
  // save CSV file to [proof] including media path, SHA256, generated time, lots of device info, SafetyNet results, OpenTimeStamps
  saveProofFile(mediaHash, options, proof)
  // sign the proof file and save the detached signature (by OpenPGP's PGPSignatureGenerator) to [proofSignature]
  savePgpSignature(proof, proofSignature)
}
```

### OpenPGP Details

In `PgpUtils#initCrypto()`, use `RSAKeyPairGenerator` from OpenPGP to create key ring by calling the following method.

``` java
/**
  * @param keyId defaults to "noone@proofmode.witness.org"
  * @param pass the password to generate private key from secret key
  * @return PGP key generator which can generate public and secret key
  * @throws PGPException
  */
public final static PGPKeyRingGenerator generateKeyRingGenerator(String keyId, char[] pass) throws PGPException {
  RSAKeyPairGenerator kpg = new RSAKeyPairGenerator();
  kpg.init(new RSAKeyGenerationParameters(BigInteger.valueOf(0x10001), new SecureRandom(), 4096, 12));
  PGPKeyPair rsakp_sign = new BcPGPKeyPair(PGPPublicKey.RSA_SIGN, kpg.generateKeyPair(), new Date());
  PGPKeyPair rsakp_enc = new BcPGPKeyPair(PGPPublicKey.RSA_ENCRYPT, kpg.generateKeyPair(), new Date());
  PGPSignatureSubpacketGenerator signhashgen = new PGPSignatureSubpacketGenerator();
  signhashgen.setKeyFlags(false, KeyFlags.SIGN_DATA | KeyFlags.CERTIFY_OTHER | KeyFlags.SHARED);
  signhashgen.setPreferredSymmetricAlgorithms(false, new int[]{SymmetricKeyAlgorithmTags.AES_256, SymmetricKeyAlgorithmTags.AES_192, SymmetricKeyAlgorithmTags.AES_128});
  signhashgen.setPreferredHashAlgorithms(false, new int[]{HashAlgorithmTags.SHA256, HashAlgorithmTags.SHA1, HashAlgorithmTags.SHA384, HashAlgorithmTags.SHA512, HashAlgorithmTags.SHA224});
  signhashgen.setFeature(false, Features.FEATURE_MODIFICATION_DETECTION);
  PGPSignatureSubpacketGenerator enchashgen = new PGPSignatureSubpacketGenerator();
  enchashgen.setKeyFlags(false, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);
  PGPDigestCalculator sha1Calc = new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA1);
  PGPDigestCalculator sha256Calc = new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA256);
  PBESecretKeyEncryptor pske = (new BcPBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha256Calc, 0xc0)).build(pass);
  PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(
    PGPSignature.POSITIVE_CERTIFICATION,
    rsakp_sign,
    keyId,
    sha1Calc,
    signhashgen.generate(),
    null,
    new BcPGPContentSignerBuilder(
      rsakp_sign.getPublicKey().getAlgorithm(),
      HashAlgorithmTags.SHA1
    ),
    pske
  );
  keyRingGen.addSubKey(rsakp_enc, enchashgen.generate(), null);
  return keyRingGen;
}
```
