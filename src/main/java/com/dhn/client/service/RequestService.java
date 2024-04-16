package com.dhn.client.service;

import java.util.List;

import com.dhn.client.bean.KAORequestBean;
import com.dhn.client.bean.MMSImageBean;
import com.dhn.client.bean.Msg_Log;
import com.dhn.client.bean.RequestBean;
import com.dhn.client.bean.SQLParameter;

public interface RequestService {

	public int selectKAORequestCount(SQLParameter param) throws Exception;

	public void updateKAOGroupNo(SQLParameter param) throws Exception;

	public List<KAORequestBean> selectKAORequests(SQLParameter param) throws Exception;

	public void updateKAOSendComplete(SQLParameter param) throws Exception;

	public void updateKAOSendInit(SQLParameter param) throws Exception;

	public int selectSMSReqeustCount(SQLParameter param) throws Exception;

	public void updateSMSGroupNo(SQLParameter param) throws Exception;

	public List<RequestBean> selectSMSRequests(SQLParameter param) throws Exception;

	public void updateSMSSendComplete(SQLParameter param) throws Exception;

	public void updateSMSSendInit(SQLParameter param) throws Exception;

	public int selectLMSReqeustCount(SQLParameter param) throws Exception;

	public void updateLMSGroupNo(SQLParameter param) throws Exception;

	public List<RequestBean> selectLMSRequests(SQLParameter param) throws Exception;

	public int selectMMSReqeustCount(SQLParameter param) throws Exception;

	public void updateMMSGroupNo(SQLParameter param) throws Exception;

	public List<RequestBean> selectMMSRequests(SQLParameter param) throws Exception;

	public List<MMSImageBean> selectMMSImage(SQLParameter param) throws Exception;

	public void updateMMSImageGroup(SQLParameter param) throws Exception;

	public void Insert_msg_log(Msg_Log _ml) throws Exception;

}
