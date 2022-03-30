package com.green.chat.handler;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


/*
 * 여러 클라이언트에서 연결 접속, 연결 해지, 메시지 전송등을 처리하는 프로그램
 */
@Component
public class EchoHandler extends TextWebSocketHandler {

	//웹 소켓 세션을 저장하는 맵 생성
	private HashMap<String, WebSocketSession> sessionMap = new HashMap<>();
	
	/*
	 * webSocket 연결 성공 시 실행하는 메소드
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// TODO Auto-generated method stub
		super.afterConnectionEstablished(session);
		sessionMap.put(session.getId(),session);
		
		JSONObject obj = new JSONObject();
		obj.put("type", "getId");
		obj.put("sessionId", session.getId());
		session.sendMessage(new TextMessage(obj.toJSONString()));  //화면에 소켓 세션ID를 전달(화면에서 내 메시지 구분하기 위함)
		
		System.out.println("세션 연결됨: " + session.getId());
	}

	/*
	 * 메시지를 수신 시 실행되는 메소드
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String msg = message.getPayload(); //수신한 웹소켓 메시지가 payload(사용자가 보낸 채팅)에 저장됨
		
		JSONObject obj = jsonToObjectParser(msg);
		//채팅방의 모든 사용자에게 메시지를 보낸다.
		for(String key: sessionMap.keySet()) {
			WebSocketSession wss = sessionMap.get(key);
			
			try {
				wss.sendMessage(new TextMessage(obj.toJSONString()));
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	/*
	 * webSocket 연결 종료 시 실행되는 메소드
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// TODO Auto-generated method stub
		super.afterConnectionClosed(session, status);
		
		sessionMap.remove(session.getId());
		
		System.out.println("연결 끊김: ");
	}
	
	/*
	 * JSON 형태의 문자열을 받아 JSONObject로 해석해주는 메소드
	 */
	private static JSONObject jsonToObjectParser(String jsonStr) {
		JSONParser parser = new JSONParser(); //json 문자열을 JSON Object로 분석해주는 처리기
		JSONObject obj = null;
		
		try {
			obj = (JSONObject)parser.parse(jsonStr); //json 문자열을 JSONObject 형태로 변환해서 저장
		}catch(ParseException e) {
			e.printStackTrace();
		}
		
		return obj;
	}

}
