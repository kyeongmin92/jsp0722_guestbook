package com.guest.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.guest.dao.MessageDao;
import com.guest.jdbc.ConnectionProvider;
import com.guest.jdbc.JdbcUtil;
import com.guest.model.Message;

public class WriteMessageService {
	private static WriteMessageService instance = new WriteMessageService();
	
	public static WriteMessageService getInstance() {
		return instance;
	}
	
	private WriteMessageService() {
		
	}
	// 싱글톤 패턴
	
	public boolean write(Message message) {
		Connection conn = null;
		
		try {
			conn = ConnectionProvider.getConnection();
			MessageDao messageDao = MessageDao.getInstance();
			// MessageDao의 insert 메소드를 이용해서 메시지를 테이블에 추가 
			messageDao.insert(conn, message);
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			JdbcUtil.close(conn);
		}
		
		return true;
	}
}
