package com.dhn.client.controller;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.dhn.client.bean.KAORequestBean;
import com.dhn.client.bean.SQLParameter;
import com.dhn.client.service.KAOService;
import com.dhn.client.service.RequestService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KAOSendRequest implements ApplicationListener<ContextRefreshedEvent> {

	public static boolean isStart = false;
	private boolean isProc = false;
	private SQLParameter param = new SQLParameter();
	private String dhnServer;
	private String userid;
	private String preGroupNo = "";
	private String crypto = "";

	@Autowired
	private RequestService reqService;

	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	private KAOService kaoService;


	@Autowired
	ScheduledAnnotationBeanPostProcessor posts;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		param.setMsg_table(appContext.getEnvironment().getProperty("dhnclient.msg_table"));
		param.setKakao(appContext.getEnvironment().getProperty("dhnclient.kakao"));
		param.setKakaobtn(appContext.getEnvironment().getProperty("dhnclient.kakaobtn"));
		param.setMsg_type("T");

		dhnServer = "http://" + appContext.getEnvironment().getProperty("dhnclient.server") + "/";
		userid = appContext.getEnvironment().getProperty("dhnclient.userid");

		HttpHeaders cheader = new HttpHeaders();
		
		cheader.setContentType(MediaType.APPLICATION_JSON);
		cheader.set("userid", userid);
		
		RestTemplate crt = new RestTemplate();
		HttpEntity<String> centity = new HttpEntity<String>(cheader);
		
		ResponseEntity<String> cresponse = crt.exchange( dhnServer + "get_crypto",HttpMethod.GET, centity, String.class );
		
		if(cresponse.getStatusCode()!=HttpStatus.OK) {
			log.info("암호화 컬럼 가져오기 오류 ");
		}
		
		if (param.getKakao() != null && param.getKakao().toUpperCase().equals("Y") && cresponse.getStatusCode() == HttpStatus.OK) {
			crypto = cresponse.getBody()!=null? cresponse.getBody().toString():"";
			log.info("KAO 초기화 완료");
			isStart = true;
		} else {
			posts.postProcessBeforeDestruction(this, null);
		}
		
	}

	@Scheduled(fixedDelay = 100)
	private void SendProcess() {
		if(isStart && !isProc) {
			isProc = true;
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
			LocalDateTime now = LocalDateTime.now();
			String group_no = now.format(formatter);
			
			if(!group_no.equals(preGroupNo)) {
				
				try {
					int cnt = reqService.selectKAORequestCount(param);
					
					if(cnt > 0) {
						
						HttpHeaders cheader = new HttpHeaders();
						
						cheader.setContentType(MediaType.APPLICATION_JSON);
						cheader.set("userid", userid);
						
						RestTemplate crt = new RestTemplate();
						HttpEntity<String> centity = new HttpEntity<String>(cheader);
						param.setGroup_no(group_no);

						reqService.updateKAOGroupNo(param);

						List<KAORequestBean> _list = reqService.selectKAORequests(param);


						for (KAORequestBean kaoRequestBean : _list) {
							if (kaoRequestBean.getButton1() != null) {
								kaoRequestBean = kaoService.Btn_form(kaoRequestBean);
							}
							kaoRequestBean = kaoService.encryption(kaoRequestBean, crypto);
						}

						StringWriter sw = new StringWriter();
						ObjectMapper om = new ObjectMapper();
						om.writeValue(sw, _list);

						HttpHeaders header = new HttpHeaders();

						header.setContentType(MediaType.APPLICATION_JSON);
						header.set("userid", userid);

						RestTemplate rt = new RestTemplate();
						HttpEntity<String> entity = new HttpEntity<String>(sw.toString(), header);

						try {
							ResponseEntity<String> response = rt.postForEntity(dhnServer + "req", entity, String.class);

							if (response.getStatusCode() == HttpStatus.OK) {
								reqService.updateKAOSendComplete(param);
								log.info("메세지 전송 완료 : " + group_no + " / " + _list.size() + " 건");
							} else {
								Map<String, String> res = om.readValue(response.getBody().toString(), Map.class);
								log.info("메세지 전송오류 : " + res.get("message"));
								reqService.updateKAOSendInit(param);
							}
						} catch (Exception e) {
							log.info("메세지 전송 오류 : " + e.toString());
							reqService.updateKAOSendInit(param);
						}

					}
					
				}catch (Exception e) {
					log.error("KAO Send Error : " + e.toString());
				}
				
				preGroupNo = group_no;
			}
			
			
			isProc = false;
		}
	}

}
