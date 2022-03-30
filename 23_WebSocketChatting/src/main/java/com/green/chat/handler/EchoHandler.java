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
 * ���� Ŭ���̾�Ʈ���� ���� ����, ���� ����, �޽��� ���۵��� ó���ϴ� ���α׷�
 */
@Component
public class EchoHandler extends TextWebSocketHandler {

	//�� ���� ������ �����ϴ� �� ����
	private HashMap<String, WebSocketSession> sessionMap = new HashMap<>();
	
	/*
	 * webSocket ���� ���� �� �����ϴ� �޼ҵ�
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// TODO Auto-generated method stub
		super.afterConnectionEstablished(session);
		sessionMap.put(session.getId(),session);
		
		JSONObject obj = new JSONObject();
		obj.put("type", "getId");
		obj.put("sessionId", session.getId());
		session.sendMessage(new TextMessage(obj.toJSONString()));  //ȭ�鿡 ���� ����ID�� ����(ȭ�鿡�� �� �޽��� �����ϱ� ����)
		
		System.out.println("���� �����: " + session.getId());
	}

	/*
	 * �޽����� ���� �� ����Ǵ� �޼ҵ�
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String msg = message.getPayload(); //������ ������ �޽����� payload(����ڰ� ���� ä��)�� �����
		
		JSONObject obj = jsonToObjectParser(msg);
		//ä�ù��� ��� ����ڿ��� �޽����� ������.
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
	 * webSocket ���� ���� �� ����Ǵ� �޼ҵ�
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// TODO Auto-generated method stub
		super.afterConnectionClosed(session, status);
		
		sessionMap.remove(session.getId());
		
		System.out.println("���� ����: ");
	}
	
	/*
	 * JSON ������ ���ڿ��� �޾� JSONObject�� �ؼ����ִ� �޼ҵ�
	 */
	private static JSONObject jsonToObjectParser(String jsonStr) {
		JSONParser parser = new JSONParser(); //json ���ڿ��� JSON Object�� �м����ִ� ó����
		JSONObject obj = null;
		
		try {
			obj = (JSONObject)parser.parse(jsonStr); //json ���ڿ��� JSONObject ���·� ��ȯ�ؼ� ����
		}catch(ParseException e) {
			e.printStackTrace();
		}
		
		return obj;
	}

}
