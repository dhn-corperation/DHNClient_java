package com.dhn.client.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dhn.client.bean.KAORequestBean;
import com.dhn.client.bean.MMSImageBean;
import com.dhn.client.bean.Msg_Log;
import com.dhn.client.bean.RequestBean;
import com.dhn.client.bean.SQLParameter;

@Repository
public class RequestImpl implements RequestDAO{

	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int selectKAORequestCount(SQLParameter param) throws Exception {
		int cnt = 0;
		
		cnt = sqlSession.selectOne("com.dhn.client.kakao.mapper.SendRequest.req_kao_count",param);
		
		return cnt;
	}

	@Override
	public void updateKAOGroupNo(SQLParameter param) throws Exception {
		sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.req_kao_group_update",param);
	}

	@Override
	public List<KAORequestBean> selectKAORequests(SQLParameter param) throws Exception {

		return sqlSession.selectList("com.dhn.client.kakao.mapper.SendRequest.req_kao_select", param);
	}

	@Override
	public void updateKAOSendComplete(SQLParameter param) throws Exception {
		sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.req_sent_complete", param); 
	}

	@Override
	public void updateKAOSendInit(SQLParameter param) throws Exception {
		sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.req_sent_init", param); 
	}

	@Override
	public int selectSMSReqeustCount(SQLParameter param) throws Exception {
		int cnt = 0;
		
		cnt = sqlSession.selectOne("com.dhn.client.nkakao.mapper.SendRequest.req_sms_count",param);
		
		return cnt;
	}

	@Override
	public void updateSMSGroupNo(SQLParameter param) throws Exception {
		sqlSession.update("com.dhn.client.nkakao.mapper.SendRequest.req_sms_group_update",param);
	}

	@Override
	public List<RequestBean> selectSMSRequests(SQLParameter param) throws Exception {
		return sqlSession.selectList("com.dhn.client.nkakao.mapper.SendRequest.req_sms_select", param);
	}

	@Override
	public void updateSMSSendComplete(SQLParameter param) throws Exception {
		sqlSession.update("com.dhn.client.nkakao.mapper.SendRequest.req_sent_complete",param);
	}

	@Override
	public void updateSMSSendInit(SQLParameter param) throws Exception {
		sqlSession.update("com.dhn.client.nkakao.mapper.SendRequest.req_sent_init", param); 
	}

	@Override
	public int selectLMSReqeustCount(SQLParameter param) throws Exception {
		int cnt = 0;
		
		cnt = sqlSession.selectOne("com.dhn.client.nkakao.mapper.SendRequest.req_lms_count", param);
		
		return cnt;
	}

	@Override
	public void updateLMSGroupNo(SQLParameter param) throws Exception {
		sqlSession.update("com.dhn.client.nkakao.mapper.SendRequest.req_lms_group_update", param); 
	}

	@Override
	public List<RequestBean> selectLMSRequests(SQLParameter param) throws Exception {
		return sqlSession.selectList("com.dhn.client.nkakao.mapper.SendRequest.req_lms_select", param);
	}

	@Override
	public int selectMMSReqeustCount(SQLParameter param) throws Exception {
		int cnt ;

		cnt = sqlSession.selectOne("com.dhn.client.nkakao.mapper.SendRequest.req_mms_count", param); 

		return cnt;
	}

	@Override
	public void updateMMSGroupNo(SQLParameter param) throws Exception {
		sqlSession.update("com.dhn.client.nkakao.mapper.SendRequest.req_mms_group_update", param);
	}

	@Override
	public List<RequestBean> selectMMSRequests(SQLParameter param) throws Exception {
		return sqlSession.selectList("com.dhn.client.nkakao.mapper.SendRequest.req_mms_select", param);
	}

	@Override
	public List<MMSImageBean> selectMMSImage(SQLParameter param) throws Exception {
		return sqlSession.selectList("com.dhn.client.nkakao.mapper.SendRequest.req_mms_image", param);
	}

	@Override
	public void updateMMSImageGroup(SQLParameter param) throws Exception {
		sqlSession.update("com.dhn.client.nkakao.mapper.SendRequest.req_mms_key_update", param);
	}

	@Override
	public void Insert_msg_log(Msg_Log _ml) throws Exception {
		
		if(_ml.getMsg_type().equals("AT")) {
			sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.result_log_insert1", _ml);
			sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.result_log_insert2", _ml);
			sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.result_log_insert3", _ml);
			sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.result_log_insert4", _ml);
		}else {
			sqlSession.update("com.dhn.client.nkakao.mapper.SendRequest.result_log_insert1", _ml);
			sqlSession.update("com.dhn.client.nkakao.mapper.SendRequest.result_log_insert2", _ml);
			sqlSession.update("com.dhn.client.nkakao.mapper.SendRequest.result_log_insert3", _ml);
			sqlSession.update("com.dhn.client.nkakao.mapper.SendRequest.result_log_insert4", _ml);
		}
	}

}
