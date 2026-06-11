<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%--
  AI 예산편성 도우미 챗 위젯 (예산편성 화면 중앙 하단)
  - 내부 심사정보시스템(CUBRID) 데이터만 사용
  - Gemini 로 자연어 질문 -> 조회 -> 결과 출력
--%>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/ai/aiChat.css" />

<div id="aiChatDock" class="ai-collapsed">
  <div id="aiChatHeader">
    <span class="ai-title">AI 예산편성 도우미 <span class="ai-badge">Gemini</span></span>
    <span class="ai-toggle" id="aiChatToggle">▲</span>
  </div>
  <div id="aiChatBody">
    <div id="aiChatMessages">
      <div class="ai-msg ai-bot">
        <div class="ai-bubble">안녕하세요. 심사정보시스템 데이터를 조회해 드리는 AI 예산편성 도우미입니다.<br/>
          예) "2026년 경상 및 투자사업 중에서 <b>유가보조금, 유류비</b> 관련 사업을 찾아서 <b>차수별 예산 추이와 심사의견 요약</b>을 정리해줘"
        </div>
      </div>
    </div>
    <div id="aiChatInputArea">
      <textarea id="aiChatInput" placeholder="질문을 입력하세요. (Enter 전송 / Shift+Enter 줄바꿈)"></textarea>
      <button type="button" id="aiChatSendBtn">전송</button>
    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/js/ai/aiChat.js"></script>
