package com.dhn.client.bean;

import lombok.Data;

@Data
public class SQLParameter {
	private String msg_table;
	private String log_table;
	private String kakao;
	private String group_no;
	private String msg_type;
	private String log_mv_flag;
	private String bktable;
	private String kakaobtn;
	private String newagent;
	private String dist_proc_option;
	private String dist_value;
	private String att_file_path;
	private String file1;
	private String file2;
	private String file3;
	private String mms_key;

	public void setDist_proc_option(String dist_proc_option) {
		if(dist_proc_option != null && dist_proc_option.length() > 0) {
			this.dist_proc_option = dist_proc_option;
		} else {
			this.dist_proc_option = "N";
		}
	}

	public void setNewagent(String newagent) {
		if(newagent != null && newagent.length() > 0) {
			this.newagent = newagent;
		} else {
			this.newagent = "N";
		}		 
	}

	public void setKakaobtn(String kakaobtn) {
		if(kakaobtn != null && kakaobtn.length() > 0) {
			this.kakaobtn = kakaobtn.toUpperCase();
		} else {
			this.kakaobtn = "N";
		}
	}	

}
