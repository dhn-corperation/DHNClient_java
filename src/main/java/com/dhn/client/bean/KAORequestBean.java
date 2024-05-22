package com.dhn.client.bean;

import lombok.Data;

@Data
public class KAORequestBean {
	private String msgid;
	private String adflag;
	private String button1;
	private String button2;
	private String button3;
	private String button4;
	private String button5;
	private String imagelink;
	private String imageurl;
	private String messagetype;
	private String msg;
	private String msgsms;
	private String onlysms;
	private String pcom;
	private String pinvoice;
	private String phn;
	private String profile;
	private String regdt;
	private String remark1;
	private String remark2;
	private String remark3;
	private String remark4;
	private String remark5;
	private String reservedt;
	private String scode;
	private String smskind;
	private String smslmstit;
	private String smssender;
	private String tmplid;
	private String wide;
	private String supplement;
	private String price;
	private String currencytype;
	private String title;
	private String header;
	private String carousel;
	private String att_items;
	private String att_coupon;
	private String crypto;
	
	public void addUrlToMsgsms(String url) {
		if(url != null && !url.isEmpty()) {
			this.msgsms = (this.msgsms == null? "" : this.msgsms + "\n") + url;
		}
	}
	  
}
