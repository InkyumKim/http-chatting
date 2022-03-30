<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
<meta charset="UTF-8">
<title>Chating</title>
<style>
*{
	margin:0;
	padding:0;
}
.container{
	width: 500px;
	margin: 0 auto;
	padding: 25px
}
.container h1{
	text-align: left;
	padding: 5px 5px 5px 15px;
	color: #FFBB00;
	border-left: 3px solid #FFBB00;
	margin-bottom: 20px;
}
.chating{
	background-color: rgb(108,159,217);
	width: 500px;
	height: 500px;
	overflow: auto;
}
.chating .me{
	padding-right: 10px;
	color: #F6F6F6;
	text-align: right;
}
.chating .others{
	padding-left: 10px;
	color: #FFE400;
	text-align: left;
}
input{
	width: 330px;
	height: 25px;
}
#yourMsg{
	display: none;
}
</style>
</head>
<body>
	<div id="container" class="container">
		<h1>채팅</h1>
		<input type="hidden" id="sessionId" value="">
		<div id="chating" class="chating">
		</div>
		
		<div id="yourName">
			<table class="inputTable">
				<tr>
					<th>사용자명</th>
					<th><input type="text" name="userName" id="userName"></th>
					<th><button onclick="chatName()" id="startBtn">이름 등록</button></th>
				</tr>
			</table>
		</div>
		<div id="yourMsg">
			<table class="inputTable">
				<tr>
					<th>메시지</th>
					<th><input id="chatting" placeholder="보내실 메시지를 입력하세요."></th>
					<th><button onclick="send()" id="sendBtn">보내기</button></th>
					<th><button onclick="wsClose()" id="closeBtn">나가기</button></th>
				</tr>
			</table>
		</div>
	</div>

<script type="text/javascript">
	var ws;	// 웹소켓 객체 저장변수
	
	// 웹 소켓 연결수행
	function wsOpen() {
		ws = new WebSocket("ws://" + location.host + "/chatting");
		wsEvt();
	}

	// 웹소켓 이벤트 관련 콜백함수 정의
	function wsEvt() {
		ws.onopen = function(data) {
			// 웹소켓이 열리면 초기화하는 부분설정
		}
		
		// 메시지가 수신되면 수행할 함수
		ws.onmessage = function(data) {
			var msg = data.data;	// 수신된 메시지(JSON 문자열 형태)
			if (msg != null && msg.trim() != "") {
				var rcv_data = JSON.parse(msg)
				if (rcv_data.type == "getId") {
					var s_id = rcv_data.sessionId != null ? rcv_data.sessionId : "";
					
					if (s_id != '') {
						$("#sessionId").val(s_id); // 내 세션ID를 화면에 저장
					}
				} else if (rcv_data.type="message") { // 대화 메시지
					if (rcv_data.sessionId == $("#sessionId").val()) { // 메시지에 포함된 세션ID와 화면에 저장된 세션ID 비교
						$("#chating").append("<p class='me'>나: " + rcv_data.msg + "</p>");
					} else {
						$("#chating").append("<p class='others'> " + rcv_data.userName + ": " + rcv_data.msg + "</p>");
					}
				} else {
					console.warn("unknown type!")
				}
			}
		}
		
		document.addEventListener("keypress", function(e){
			if (e.keyCode == 13) {	// Enter 키가 눌려진 경우
				send();
			}
		});
	}
	
	// 사용자명을 입력하고 웹소켓에 연결
	function chatName() {
		var userName = $("#userName").val();
		if (userName == null || userName.trim() == "") {
			alert("사용자 이름을 입력해 주세요!");
			$("#userName").focus();
		} else {
			wsOpen();
			$("#yourName").hide();
			$("#yourMsg").show();	// 메시지창 오픈
		}
	}
	
	// 대화 메시지 전송
	function send() {
		var option = {
			type: "message",
			sessionId: $("#sessionId").val(),
			userName: $("#userName").val(),
			msg: $("#chatting").val()
		}

		// 웹소켓을 통해 메시지 전송
		ws.send(JSON.stringify(option)); // JSON 데이터를 문자열로 변환하여 전송
		$("#chatting").val("");	 // 대화 입력창 지우기
	}
	
	// 대화방 나가기 기능 구현
	function wsClose() {
		var option = {
				type: "message",
				sessionId: $("#sessionId").val(),
				userName: "",
				msg: $("#userName").val() + "님이 퇴장하셨습니다."
			}

		// 웹소켓을 통해 메시지 전송
		ws.send(JSON.stringify(option));
		
		ws.close();
	}
</script>	
	
    </body>
</html>