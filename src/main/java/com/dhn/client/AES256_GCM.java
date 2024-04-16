package com.dhn.client;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AES256_GCM {
	
	public static String alg = "AES/GCM/NoPadding";
	private final String key = "9b4dabe9d4fed126a58f8639846143c7";
	private final SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(),"AES");
	
	public String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public String encrypt(String text,GCMParameterSpec nonce) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        GCMParameterSpec gcmParameterSpec = nonce;
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
       //String encodedEncryptedData = Base64.getEncoder().encodeToString(encrypted);
        
        return toHex(encrypted);
    }


}
