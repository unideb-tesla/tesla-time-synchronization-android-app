package com.unideb.tesla.timesync;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class DigitalSignature {

    public static final String DIGITAL_SIGNATURE_ALGORITHM = "DSA";
    // public static final String PROVIDER_SUN = "SUN";
    public static final String SHA256_WITH_DSA = "SHA256withDSA";


    private DigitalSignature(){
    }

    public static boolean verify(byte[] message, byte[] signature, byte[] publicKey) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {

        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKey);
        // KeyFactory keyFactory = KeyFactory.getInstance(DIGITAL_SIGNATURE_ALGORITHM, PROVIDER_SUN);
        KeyFactory keyFactory = KeyFactory.getInstance(DIGITAL_SIGNATURE_ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

        Signature sig = Signature.getInstance(SHA256_WITH_DSA);
        sig.initVerify(pubKey);

        sig.update(message);

        boolean verifies = sig.verify(signature);

        return verifies;

    }


}
