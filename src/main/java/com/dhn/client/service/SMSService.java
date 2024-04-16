package com.dhn.client.service;

import java.security.SecureRandom;

import javax.crypto.spec.GCMParameterSpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhn.client.AES256_GCM;
import com.dhn.client.bean.KAORequestBean;
import com.dhn.client.bean.RequestBean;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SMSService {
	
	@Autowired
	private AES256_GCM aes256;
	
	public RequestBean encryption(RequestBean requestBean, String column) {
		GCMParameterSpec nonce = generateGCMParameterSpec();
		//String noncestd = Base64.getEncoder().encodeToString(nonce.getIV());
		String nonceHex = aes256.toHex(nonce.getIV());
		requestBean.setCrypto(nonceHex+","+column);
		try {
			requestBean.setPhn(aes256.encrypt(requestBean.getPhn(), nonce));
			if(column.contains("MSG")) {
				requestBean.setMsg(aes256.encrypt(requestBean.getMsg(), nonce));
			}
			if(column.contains("PROFILE")) {
				requestBean.setProfile(aes256.encrypt(requestBean.getProfile(), nonce));
			}
			if(column.contains("MESSAGETYPE")) {
				requestBean.setMessagetype(aes256.encrypt(requestBean.getMessagetype(), nonce));
			}
			if(column.contains("MSGSMS")) {
				requestBean.setMsgsms(aes256.encrypt(requestBean.getMsgsms(), nonce));
			}
			if(column.contains("SMSSENDER")) {
				requestBean.setSmssender(aes256.encrypt(requestBean.getSmssender(), nonce));
			}
		}catch (Exception e) {
			log.info("데이터 암호화 오류 : ",e.getMessage());
		}
		return requestBean;
	}
	
	public GCMParameterSpec generateGCMParameterSpec() {
        byte[] iv = new byte[12]; // 12바이트 nonce
        new SecureRandom().nextBytes(iv);
        return new GCMParameterSpec(128, iv);
   }

}
