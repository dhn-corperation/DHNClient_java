package com.dhn.client.controller;

import java.io.File;
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
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.dhn.client.bean.MMSImageBean;
import com.dhn.client.bean.RequestBean;
import com.dhn.client.bean.SQLParameter;
import com.dhn.client.service.RequestService;
import com.dhn.client.service.SMSService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
@Slf4j
public class MMSSendRequest implements ApplicationListener<ContextRefreshedEvent>{
	
	public static boolean isStart = false;
	private boolean isProc = false;
	private SQLParameter param = new SQLParameter();
	private String dhnServer;
	private String userid;
	private String basepath;
	private String preGroupNo = "";
	private String crypto = "";
	
	@Autowired
	private RequestService reqService;
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private ApplicationContext appContext;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		param.setMsg_table( appContext.getEnvironment().getProperty("dhnclient.msg_table") ); 
		param.setMsg_type("M");
		

		dhnServer = "http://" + appContext.getEnvironment().getProperty("dhnclient.server") + "/";
		userid = appContext.getEnvironment().getProperty("dhnclient.userid");
		
		// 풀 경로를 DB에 담는듯.
		basepath = appContext.getEnvironment().getProperty("dhnclient.file_base_path")==null?"":appContext.getEnvironment().getProperty("dhnclient.file_base_path");
		
		
		HttpHeaders cheader = new HttpHeaders();
		
		cheader.setContentType(MediaType.APPLICATION_JSON);
		cheader.set("userid", userid);
		
		RestTemplate crt = new RestTemplate();
		HttpEntity<String> centity = new HttpEntity<String>(cheader);

		
		try {
			ResponseEntity<String> cresponse = crt.exchange( dhnServer + "get_crypto",HttpMethod.GET, centity, String.class );
			
			if(cresponse.getStatusCode()==HttpStatus.OK) {
				crypto = cresponse.getBody()!=null? cresponse.getBody().toString():"";
				log.info("MMS 초기화 완료");
				isStart = true;
			}else {
				log.info("암호화 컬럼 가져오기 오류 ");			
			}
			
		}catch (HttpClientErrorException e) {
			log.error("crypto 가져오기 오류 : " + e.getStatusCode() + ", " + e.toString());
		}catch (RestClientException e) {
			log.error("기타 오류 : " + dhnServer + ", " + e.toString());
		}
	}
	
	@Scheduled(fixedDelay = 100)
	private void SendProcess() {
		if(isStart && !isProc) {
			isProc = true;
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
			LocalDateTime now = LocalDateTime.now();
			String group_no = "M" + now.format(formatter);
			
			if(!group_no.equals(preGroupNo)) {
				
				try {
					
					int cnt = reqService.selectMMSReqeustCount(param);
					
					if(cnt > 0) {
						
						param.setGroup_no(group_no);
						
						reqService.updateMMSGroupNo(param);
						
						List<RequestBean> _list = reqService.selectMMSRequests(param);
						
						for (RequestBean requestBean : _list) {
							requestBean = smsService.encryption(requestBean,crypto);
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
							//log.info(response.getStatusCode() + " / " + response.getBody());
													
							if(response.getStatusCode() == HttpStatus.OK)
							{
								reqService.updateSMSSendComplete(param);
								log.info("MMS 메세지 전송 완료 : " + group_no + " / " + _list.size() + " 건");
							} else {
								Map<String, String> res = om.readValue(response.getBody().toString(), Map.class);
								log.info("MMS 메세지 전송오류 : " + res.get("message"));
								reqService.updateSMSSendInit(param);
							}
						} catch(Exception ex) {
							log.info("MMS 메세지 전송 오류 : " + ex.toString());
							
							reqService.updateSMSSendInit(param);
						}
						
					}
					
					
				}catch (Exception e) {
					log.error("MMS Send Error : " + e.toString());
				}
				preGroupNo = group_no;
			}
			isProc = false;
		}
	}
	
	@Scheduled(fixedDelay = 100)
	private void GETImageKey() {
		if(isStart && !isProc) {
			isProc = true;
			
			try {
				List<MMSImageBean> imgList = reqService.selectMMSImage(param);
				
				if(imgList.size() > 0) {
					for (MMSImageBean mmsImageBean : imgList) {
						param.setFile1("X");
						param.setFile2("X");
						param.setFile3("X");
						
						MultipartBody.Builder builder = new MultipartBody.Builder();
						builder.addFormDataPart("userid", userid);
						if(mmsImageBean.getFile1() != null && mmsImageBean.getFile1().length() > 0) {
							File file = new File(basepath + mmsImageBean.getFile1());
							param.setFile1(mmsImageBean.getFile1());
							builder.addFormDataPart("image1", mmsImageBean.getFile1(), RequestBody.create(MultipartBody.FORM,file));
						}
						if(mmsImageBean.getFile2() != null && mmsImageBean.getFile2().length() > 0) {
							File file = new File(basepath + mmsImageBean.getFile2());
							param.setFile2(mmsImageBean.getFile2());
							builder.addFormDataPart("image2", mmsImageBean.getFile2(), RequestBody.create(MultipartBody.FORM, file));
						}
						if(mmsImageBean.getFile3() != null && mmsImageBean.getFile3().length() > 0) {
							File file = new File(basepath + mmsImageBean.getFile3());
							param.setFile3(mmsImageBean.getFile3());
							builder.addFormDataPart("image3", mmsImageBean.getFile3(), RequestBody.create(MultipartBody.FORM, file));
						}
						
						builder.setType(MultipartBody.FORM);
						
						RequestBody reqbody = builder.build();
						
						Request request = new Request.Builder()
								.url(dhnServer + "mms/image")
								.post(reqbody)
								.build();
						
						try {
							OkHttpClient client = new OkHttpClient();
							Response response = client.newCall(request).execute();
							
							if(response.code() == 200) {
								ObjectMapper mapper = new ObjectMapper();
								Map<String, String> res = mapper.readValue(response.body().string(), Map.class);
								log.info("MMS Image Key : " + res.get("image group"));
								if(res.get("image group") != null && res.get("image group").length() > 0) {
									param.setMms_key(res.get("image group"));
									reqService.updateMMSImageGroup(param);
								}
							}
							response.close();
						} catch (Exception e) {
							log.info("MMS Image Key 등록 오류 : ", e.toString());
						}
						
					}
				}
				
			} catch (Exception e) {
				log.error("MMS Image 등록 오류 : " + e.toString());
			}
		}
		isProc = false;
	}

}
