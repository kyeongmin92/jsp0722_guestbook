package com.guest.dao;

import java.sql.Connection; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.guest.jdbc.JdbcUtil;
import com.guest.model.Message;

public class MessageDao {
	private static MessageDao messageDao = new MessageDao();
	public static MessageDao getInstance() {
		return messageDao;
	}
	
	private MessageDao() {}
	
	public int insert(Connection conn, Message message) throws SQLException {
		// 1. 클래스 로딩 : listener 에서 이미 로딩됨
		// 2. 연결 생성 : 파라미터로 받음
		// 3. statement 생성 : 메소드 내
		// 4. 쿼리 실행 : 메소드 내
		// 5. 결과 처리 : 호출한 곳에서 처리
		// 6. 자원 닫기 
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("insert into guestbook_message" 
					+ "(guest_name, password, message) values (?,?,?)");
			pstmt.setString(1, message.getGuestName());
			pstmt.setString(2, message.getPassword());
			pstmt.setString(3, message.getMessage());
			return pstmt.executeUpdate();
		} finally {
			/*try {
			pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			JdbcUtil.close(pstmt);
		}
	}
	
	public Message select(Connection conn, int messageId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("select * from guestbook_message "
										+"where message_id=?");
			pstmt.setInt(1, messageId);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return makeMessageFromResultSet(rs);
			} else {
				return null;
			}
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
	
	// makeMessageFromResultSet에서는 ResultSet에서 값을 가져와서 모델에 하나씩 넣는 과정
	private Message makeMessageFromResultSet(ResultSet rs) throws SQLException {
		Message message = new Message();
		message.setId(rs.getInt("message_id"));
		message.setGuestName(rs.getString("guest_name"));
		message.setPassword(rs.getString("password"));
		message.setMessage(rs.getString("message"));
		
		return message;
	}
	
	// selectCount는 테이블에서 저장된 행의 개수를 리턴
	public int selectCount(Connection conn) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select count(*) from guestbook_message");
			rs.next();
			return rs.getInt(1);			
		} finally {
			JdbcUtil.close(rs, stmt);
		}
	}
	
	// 시작행과 끝행에 해당되는 메세지 목록을 읽어온다
	public List<Message> selectList(Connection conn, int firstRow, int endRow) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("select * from guestbook_message "
									+ "order by message_id desc limit ?, ?");
			pstmt.setInt(1, firstRow - 1);
			pstmt.setInt(2, endRow - firstRow + 1);
			rs = pstmt.executeQuery();
			
				List<Message> messageList = new ArrayList<Message>();
				
				while (rs.next()) {
					messageList.add(makeMessageFromResultSet(rs));
				}
				return messageList;
			} finally {
				JdbcUtil.close(rs, pstmt);
			}		
		}
	
	// 지정한 주요키에 해당하는 행의 데이터를 삭제하는 쿼리를 실행하고 삭제된 행의 갯수를 리턴
	public int delete(Connection conn, int messageId) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("delete from guestbook_message "
					+ "where message_id=?");
			pstmt.setInt(1, messageId);
			return pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
	
}
















