package com.dhn.client.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhn.client.bean.KAORequestBean;
import com.dhn.client.bean.MMSImageBean;
import com.dhn.client.bean.Msg_Log;
import com.dhn.client.bean.RequestBean;
import com.dhn.client.bean.SQLParameter;
import com.dhn.client.dao.RequestDAO;

@Service
public class RequestServiceImpl implements RequestService {

	@Autowired
	private RequestDAO req;

	@Override
	public int selectKAORequestCount(SQLParameter param) throws Exception {
		return req.selectKAORequestCount(param);
	}

	@Override
	public void updateKAOGroupNo(SQLParameter param) throws Exception {
		req.updateKAOGroupNo(param);
	}

	@Override
	public List<KAORequestBean> selectKAORequests(SQLParameter param) throws Exception {
		return req.selectKAORequests(param);
	}

	@Override
	public void updateKAOSendComplete(SQLParameter param) throws Exception {
		req.updateKAOSendComplete(param);
	}

	@Override
	public void updateKAOSendInit(SQLParameter param) throws Exception {
		req.updateKAOSendInit(param);
	}

	@Override
	public int selectSMSReqeustCount(SQLParameter param) throws Exception {
		return req.selectSMSReqeustCount(param);
	}

	@Override
	public void updateSMSGroupNo(SQLParameter param) throws Exception {
		req.updateSMSGroupNo(param);
	}

	@Override
	public List<RequestBean> selectSMSRequests(SQLParameter param) throws Exception {
		return req.selectSMSRequests(param);
	}

	@Override
	public void updateSMSSendComplete(SQLParameter param) throws Exception {
		req.updateSMSSendComplete(param);
	}

	@Override
	public void updateSMSSendInit(SQLParameter param) throws Exception {
		req.updateSMSSendInit(param);
	}

	@Override
	public int selectLMSReqeustCount(SQLParameter param) throws Exception {
		return req.selectLMSReqeustCount(param);
	}

	@Override
	public void updateLMSGroupNo(SQLParameter param) throws Exception {
		req.updateLMSGroupNo(param);
	}

	@Override
	public List<RequestBean> selectLMSRequests(SQLParameter param) throws Exception {
		return req.selectLMSRequests(param);
	}

	@Override
	public int selectMMSReqeustCount(SQLParameter param) throws Exception {
		return req.selectMMSReqeustCount(param);
	}

	@Override
	public void updateMMSGroupNo(SQLParameter param) throws Exception {
		req.updateMMSGroupNo(param);
	}

	@Override
	public List<RequestBean> selectMMSRequests(SQLParameter param) throws Exception {
		return req.selectMMSRequests(param);
	}

	@Override
	public List<MMSImageBean> selectMMSImage(SQLParameter param) throws Exception {
		return req.selectMMSImage(param);
	}

	@Override
	public void updateMMSImageGroup(SQLParameter param) throws Exception {
		req.updateMMSImageGroup(param);
	}

	@Override
	public void Insert_msg_log(Msg_Log _ml) throws Exception {
		req.Insert_msg_log(_ml);
	}

}