package com.jurc._pay_demo.service;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
            throw new RuntimeException("Error generating signature", e);
        }
    }

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
