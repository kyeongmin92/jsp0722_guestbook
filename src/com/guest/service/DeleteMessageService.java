package com.guest.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.guest.dao.MessageDao;
import com.guest.jdbc.ConnectionProvider;
import com.guest.jdbc.JdbcUtil;
import com.guest.model.Message;

public class DeleteMessageService {
	
	private static DeleteMessageService instance = new DeleteMessageService();
	
	public static DeleteMessageService getInstance() {
		return instance;
	}
	
	private DeleteMessageService() {
		
	}
	
	public String deleteMessage(int messageId, String password) {
		Connection conn = null;
		
		try {
			conn = ConnectionProvider.getConnection();
			// 자동커밋모드가 false이면 JDBC 드라이버에서는 각 커밋 후에 새 트랜잭션을 암시적으로 시작한다.
			conn.setAutoCommit(false);
			
			MessageDao messageDao = MessageDao.getInstance();
			// 삭제할 메시지에 해당하는 Message 객체를 구한다
			Message message = messageDao.select(conn, messageId);
			
			// Message가 존해하지 않으면 MessageNotFoundException을 발생
			if(message == null) {
				return "메시지 없음";
			}
			// Message 객체의 matchPassword 메소드를 이용해서 암호 일치하는지 검사
			// 암호가 일치하지 않으면  InvaildPasswordException 발생
			if(!message.matchPassword(password)) {
				return "패스워드 불일치";					
			}
			
			// MessageDao의 delete 메소드를 이용해서 지정한 id에 맞는 메시지 삭제 
			messageDao.delete(conn, messageId);
			//트랜잭션 커밋
			conn.commit();
			
			return "삭제 성공";
		} catch(SQLException e) {
			// Exception 발생하면 트랜잭션 롤백
			JdbcUtil.rollback(conn);
			e.printStackTrace();
		} finally {
			JdbcUtil.close(conn);
		}
		return "삭제 실패";		
	}
}
