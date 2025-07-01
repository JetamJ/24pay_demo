package com.jurc._pay_demo.service;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Arrays;

@Service
public class SignService {

    private static final Logger logger = LoggerFactory.getLogger(SignService.class);

    /**
     * Generates a signature by hashing the input message using SHA-1,
     * then encrypting the hash with AES/CBC/PKCS7Padding using a key and
     * an initialization vector (IV) derived from the provided merchant ID (mid).
     *
     * @param message the input message to be signed
     * @param key the hexadecimal representation of the AES encryption key
     * @param mid the merchant ID used to create the initialization vector (IV)
     * @return the first 16 bytes of the encrypted hash, encoded as a hexadecimal string
     * @throws RuntimeException if any error occurs during the signature generation process
     */
    public String generateSign(String message, String key, String mid) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            byte[] keyBytes = Hex.decodeHex(key.toCharArray());
            String iv = this.createIv(mid);
            byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);

            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

            byte[] sha1Hash = DigestUtils.sha1(message.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedData = encryptCipher.doFinal(sha1Hash);

            return Hex.encodeHexString(Arrays.copyOf(encryptedData, 16));
        } catch (Exception e) {
            logger.error("Error generating signature", e);
            throw new RuntimeException("Error generating signature", e);
        }
    }

    /**
     * Creates an initialization vector (IV) for AES encryption by concatenating
     * the merchant ID (mid) with its reverse, and trimming or padding the result
     * to ensure it is exactly 16 characters long.
     *
     * @param mid the merchant ID to be used for IV creation
     * @return a 16-character string serving as the initialization vector
     */
    public String createIv(String mid) {
        String reversedMid = new StringBuilder(mid).reverse().toString();
        String combined = mid + reversedMid;
        if (combined.length() > 16) {
            return combined.substring(0, 16);
        } else {
            return String.format("%-16s", combined).substring(0, 16);
        }
    }
}
