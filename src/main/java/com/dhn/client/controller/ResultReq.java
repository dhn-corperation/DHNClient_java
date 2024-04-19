package com.dhn.client.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.dhn.client.bean.Msg_Log;
import com.dhn.client.service.RequestService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ResultReq implements ApplicationListener<ContextRefreshedEvent>{
	
	public static boolean isStart = false;
	private boolean isProc = false;
	//private SQLParameter param = new SQLParameter();
	private String dhnServer;
	private String userid;
	//private Map<String, String> _rsltCode = new HashMap<String, String>();
	private static int procCnt = 0;
	private String msgTable = "";
	private String logTable = "";
	
	@Autowired
	private RequestService reqService;
	
	@Autowired
	private ApplicationContext appContext;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		msgTable = appContext.getEnvironment().getProperty("dhnclient.msg_table");
		logTable = appContext.getEnvironment().getProperty("dhnclient.log_table");
		
		dhnServer = "http://" + appContext.getEnvironment().getProperty("dhnclient.server") + "/";
		userid = appContext.getEnvironment().getProperty("dhnclient.userid");
		
		isStart = true;
	}
	
	@Scheduled(fixedDelay = 100)
	private void SendProcess() {
		if(isStart && !isProc && procCnt < 10) {
			isProc = true;
			procCnt++;
			try {
				ObjectMapper om = new ObjectMapper();
				HttpHeaders header = new HttpHeaders();
				
				header.setContentType(MediaType.APPLICATION_JSON);
				header.set("userid", userid);
				
				RestTemplate rt = new RestTemplate();
				HttpEntity<String> entity = new HttpEntity<String>(null, header);
				
				try {
					ResponseEntity<String> response = rt.postForEntity(dhnServer + "result", entity, String.class);
											
					if(response.getStatusCode() ==  HttpStatus.OK)
					{
						JSONArray json = new JSONArray(response.getBody().toString());
						if(json.length()>0) {
							Thread res = new Thread(() ->ResultProc(json, procCnt) );
							res.start();
						} else {
							procCnt--;
						}
					} else {
						procCnt--;
					}
				} catch(Exception ex) {
					log.info("결과 수신 오류 : " + ex.toString());
					procCnt--;
				}
				
			}catch (Exception e) {
				log.info("결과 수신 오류 : " + e.toString());
				procCnt--;
			}
			isProc = false;
		}
	}
	
	private void ResultProc(JSONArray json, int _pc) {
		
		for(int i=0; i<json.length(); i++) {
			JSONObject ent = json.getJSONObject(i);
			
			Msg_Log _ml = new Msg_Log(msgTable, logTable);
			_ml.setMsgid(ent.getString("msgid"));
			
			String rscode = "";
			
			_ml.setMsg_type(ent.getString("message_type").toUpperCase());
			
			if(ent.getString("message_type").equalsIgnoreCase("AT")) { // message_type = AT 즉, 1차 알림톡 성공
				_ml.setMsg_err_code(ent.getString("code")); // 알림톡 코드
				rscode = "K"; // 재발송된 문자 결과값 (K : 알림톡 성공)
				_ml.setSndg_cpee_dt(ent.getString("res_dt")); // 단말기 수신 시각 (알림톡이 성공하면 remark2가 없어 Center에서 AT테이블에 넣는 시각)
				
				if(ent.getString("code").equals("0000")) { // 알림톡 성공 여부
					_ml.setStatus("2");		
				}else {
					_ml.setStatus("4");							
				}
			}else { // message_type = PH
				if (ent.has("s_code") && !ent.isNull("s_code") && ent.getString("s_code").length() > 1){// 알림톡 실패 -> 문자처리
					_ml.setMsg_err_code(ent.getString("s_code")); // 알림톡 실패 코드
					rscode = ent.getString("code").substring(2); // 문자 코드
					_ml.setAgan_sms_type(ent.getString("sms_kind")); // 재발송된 문자 타입
					
					if(ent.getString("remark1").equalsIgnoreCase("SKT")) {// 재발송된 문자 통신사값
						_ml.setAgan_tel_info("1");
					}else if(ent.getString("remark1").equalsIgnoreCase("KTF")) {
						_ml.setAgan_tel_info("2");
					}else if(ent.getString("remark1").equalsIgnoreCase("LGT")) {
						_ml.setAgan_tel_info("3");
					}else if(ent.getString("remark1").equalsIgnoreCase("ETC")) {
						_ml.setAgan_tel_info("4");
					}
					
					if(ent.getString("code").equals("0000")) { // 문자 성공 여부
						_ml.setStatus("2");		
					}else {
						_ml.setStatus("4");							
					}
					_ml.setSndg_cpee_dt(ent.getString("remark2")); // 단말기 수신 시각
					
				}else { // 일반 문자
					_ml.setMsg_err_code(ent.getString("code").substring(2)); // 문자 코드
					_ml.setAgan_sms_type(ent.getString("sms_kind")); // 문자 타입
					if(ent.getString("code").equals("0000")) {
						_ml.setStatus("2");		
					}else {
						_ml.setStatus("4");							
					}
					_ml.setSndg_cpee_dt(ent.getString("remark2")); // 단말기 수신 시각
				}
			}
			
			_ml.setAgan_code(rscode);
			
			
			try {
				reqService.Insert_msg_log(_ml);
			}catch (Exception e) {
				log.info("결과 처리 오류 [ " + _ml.getMsgid() + " ] - " + e.toString());
			}
		}
		log.info("결과 수신 완료 : " + json.length() + " 건");		
		procCnt--;
		
	}

}
