<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%--
  AI 예산편성 도우미 챗 위젯 (예산편성 화면 중앙 하단)
  - 내부 심사정보시스템(CUBRID) 데이터만 사용
  - Gemini 로 자연어 질문 -> 조회 -> 결과 출력
--%>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/ai/aiChat.css?v=20260614c" />

<div id="aiChatDock" class="ai-collapsed">
  <div id="aiChatHeader">
    <span class="ai-title">AI 예산편성 도우미 <span class="ai-badge">Gemini</span></span>
    <span class="ai-toggle" id="aiChatToggle">▲</span>
  </div>
  <div id="aiChatBody">
    <div id="aiChatMessages">
      <div class="ai-msg ai-bot ai-guide-msg">
        <div class="ai-bubble ai-guide"><div class="ai-guide-line">안녕하세요. 심사정보시스템 데이터를 조회해 드리는 AI 예산편성 도우미입니다.</div><div class="ai-guide-line"><span class="ai-guide-mark">▸</span><span class="ai-guide-label">사업·분야</span> "2026년 경상사업 및 투자사업에서 <b>일상돌봄</b> 사업을 찾아줘"</div><div class="ai-guide-line"><span class="ai-guide-mark">▸</span><span class="ai-guide-label">비정형</span> [구분]·[검토내용] 목록을 명시해 주세요.</div><div class="ai-guide-line ai-guide-sub">"2026년 경상사업 및 투자사업에서 <b>[구분]</b>에 있는 내용 중 <b>테크노파크</b>가 시행처인 사업을 찾아서 정리해줘"</div><div class="ai-guide-line ai-guide-sub">"2026년 경상사업 및 투자사업에서 <b>[검토내용]</b>에 있는 내용 중 <b>마무리</b> 사업을 찾아서 정리해줘"</div></div>
      </div>
    </div>
    <div id="aiChatInputArea">
      <textarea id="aiChatInput" placeholder="질문을 입력하세요. (Enter 전송 / Shift+Enter 줄바꿈)"></textarea>
      <button type="button" id="aiChatSendBtn">전송</button>
    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/js/ai/aiChat.js"></script>
