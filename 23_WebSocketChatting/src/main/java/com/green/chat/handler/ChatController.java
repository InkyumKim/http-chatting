package com.green.chat.handler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ChatController {

	/*
	 * ä�ù�ȭ���� ǥ��
	 */
	@RequestMapping(value="/chat", method=RequestMethod.GET)
	public String chatView() {
		
		return "chat";
	}
}
