package com.dhn.client.bean;

import lombok.Data;

@Data
public class Msg_Log {
	private String msg_table;
	private String log_table;
	private String msgid;
	private String msg_err_code;
	private String agan_code;
	private String agan_tel_info;
	private String agan_sms_type;
	private String status;
	private String end_status;
	private String msg_type;
	private String sndg_cpee_dt;
	
	public Msg_Log(String msg_table, String log_table) {
		this.msg_table = msg_table;
		this.log_table = log_table;
	}

}
