package com.guest.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.guest.dao.MessageDao;
import com.guest.jdbc.ConnectionProvider;
import com.guest.jdbc.JdbcUtil;
import com.guest.model.Message;

public class GetMessageListService {
	private static GetMessageListService instance = new GetMessageListService();
	
	public static GetMessageListService getInstance() {
		return instance;
	}
	
	private GetMessageListService() {
		
	}   
	// 싱글톤 패턴 적용
	
	// 페이지 당 보여줄 메시지 개수
	private static final int MESSAGE_COUNT_PER_PAGE = 3;
	
	public MessageListView getMessageList(int pageNumber) {
		Connection conn = null;
		int currentPageNumber = pageNumber;
		try {
			conn = ConnectionProvider.getConnection();
			MessageDao messageDao = MessageDao.getInstance();
			
			// 메시지 총 개수는 messageDao의 테이블에서 저장된 행의 개수를 리턴하는 selectCount를 이용
			int messageTotalCount = messageDao.selectCount(conn);
			
			List<Message> messageList = null;
			int firstRow =0;
			int endRow =0;
			// 메시지의 개수가 0보다 크면 요청한 페이지에 속하는 메시지의 시작 행과 끝 행을 구한다
			if(messageTotalCount > 0) {
				firstRow =
						(pageNumber - 1) * MESSAGE_COUNT_PER_PAGE + 1;
				endRow = firstRow + MESSAGE_COUNT_PER_PAGE - 1;
				
				// messageDao의 selectList를 이용해 시작 행과 끝행에 속한는 메시지 목록을 구한다
				messageList =
						messageDao.selectList(conn, firstRow, endRow);
			} else {
				// 메시지 개수가 0일 경우 빈 List를 messageList에 할당
				currentPageNumber = 0;
				messageList = Collections.emptyList();
			}
			// MessageViewList 객체 리턴
			return new MessageListView(messageList,
					messageTotalCount, currentPageNumber,
					MESSAGE_COUNT_PER_PAGE, firstRow, endRow);
			
		} catch (SQLException e) {
//			throw new ServiceException("목록 구하기 실패: " + e.getMessage(), e);
			e.printStackTrace();
		} finally {
			JdbcUtil.close(conn);
		}
		return null;
	}
}
