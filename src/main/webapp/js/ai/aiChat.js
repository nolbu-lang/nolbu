/*
 * AI 예산편성 도우미 챗 위젯
 * - /ai/ajaxAiChat.do 호출 (csAjaxCall, 비동기)
 * - 내부 심사정보시스템(CUBRID) 데이터 조회 결과를 챗 창에 출력
 *
 * 예산편성 화면은 탭으로 ajax 로드되므로, 핸들러는 document 위임 + 네임스페이스로
 * 바인딩하여 여러 번 로드되어도 중복 동작하지 않도록 한다.
 */
(function () {
    function htmlEscape(str) {
        if (str === null || str === undefined) {
            return "";
        }
        return String(str)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function nl2br(str) {
        return htmlEscape(str).replace(/\n/g, "<br/>");
    }

    function scrollToBottom() {
        var el = document.getElementById("aiChatMessages");
        if (el) {
            el.scrollTop = el.scrollHeight;
        }
    }

    function appendUser(text) {
        var html = '<div class="ai-msg ai-user"><div class="ai-bubble ai-pre">' + nl2br(text) + '</div></div>';
        $("#aiChatMessages").append(html);
        scrollToBottom();
    }

    function appendBotLoading() {
        var $el = $('<div class="ai-msg ai-bot ai-loading"><div class="ai-bubble">답변을 생성하고 있습니다...</div></div>');
        $("#aiChatMessages").append($el);
        scrollToBottom();
        return $el;
    }

    function buildResultTable(columns, dataList) {
        if (!columns || columns.length === 0 || !dataList || dataList.length === 0) {
            return "";
        }

        var html = '<div class="ai-result-table-wrap"><table class="ai-result-table"><thead><tr>';
        for (var c = 0; c < columns.length; c++) {
            html += "<th>" + htmlEscape(columns[c]) + "</th>";
        }
        html += "</tr></thead><tbody>";

        for (var r = 0; r < dataList.length; r++) {
            var row = dataList[r];
            html += "<tr>";
            for (var k = 0; k < columns.length; k++) {
                html += "<td>" + htmlEscape(row[columns[k]]) + "</td>";
            }
            html += "</tr>";
        }
        html += "</tbody></table></div>";
        return html;
    }

    function appendBotAnswer($loadingEl, data) {
        var answer = (data && data.answer) ? data.answer : "응답이 없습니다.";
        var inner = '<div class="ai-bubble ai-pre">' + nl2br(answer);

        if (data && data.columns && data.dataList) {
            var tableHtml = buildResultTable(data.columns, data.dataList);
            if (tableHtml) {
                inner += tableHtml;
                if (typeof data.rowCount !== "undefined") {
                    inner += '<div style="font-size:11px;color:#888;margin-top:4px;">총 ' + data.rowCount + '건 (표시 행수 제한 적용)</div>';
                }
            }
        }

        if (data && data.sql) {
            inner += '<div class="ai-sql-toggle">실행된 조회문 보기</div>';
            inner += '<div class="ai-sql-box">' + htmlEscape(data.sql) + '</div>';
        }

        if (data && data.aiProvider) {
            inner += '<div class="ai-provider-hint">연결: ' + htmlEscape(data.aiProvider) + '</div>';
        }

        inner += "</div>";

        $loadingEl.removeClass("ai-loading").html(inner);
        scrollToBottom();
    }

    function appendBotError($loadingEl, msg) {
        $loadingEl.removeClass("ai-loading").html('<div class="ai-bubble ai-pre">' + nl2br(msg) + '</div>');
        scrollToBottom();
    }

    function send() {
        var $input = $("#aiChatInput");
        var $sendBtn = $("#aiChatSendBtn");
        var question = $.trim($input.val());
        if (question === "") {
            return;
        }

        appendUser(question);
        $input.val("");
        $input.prop("disabled", true);
        $sendBtn.prop("disabled", true);

        var $loadingEl = appendBotLoading();

        $.csAjaxCall({
            url: "/ai/ajaxAiChat.do",
            data: { question: question },
            async: true,
            callBack: function (rtnData) {
                $("#aiChatInput").prop("disabled", false).focus();
                $("#aiChatSendBtn").prop("disabled", false);

                if (!rtnData) {
                    appendBotError($loadingEl, "서버 응답을 받지 못했습니다. 잠시 후 다시 시도해 주세요.");
                    return;
                }
                appendBotAnswer($loadingEl, rtnData.data);
            }
        });
    }

    function bind() {
        // 네임스페이스(.aichat)로 중복 바인딩 방지
        $(document).off(".aichat");

        $(document).on("click.aichat", "#aiChatHeader", function () {
            var $dock = $("#aiChatDock");
            $dock.toggleClass("ai-collapsed");
            var collapsed = $dock.hasClass("ai-collapsed");
            $("#aiChatToggle").text(collapsed ? "▲" : "▼");
            if (!collapsed) {
                setTimeout(function () {
                    $("#aiChatInput").focus();
                    scrollToBottom();
                }, 50);
            }
        });

        $(document).on("click.aichat", "#aiChatSendBtn", function () {
            send();
        });

        $(document).on("keydown.aichat", "#aiChatInput", function (e) {
            if (e.keyCode === 13 && !e.shiftKey) {
                e.preventDefault();
                send();
            }
        });

        $(document).on("click.aichat", "#aiChatDock .ai-chip", function () {
            $("#aiChatInput").val($(this).text()).focus();
        });

        $(document).on("click.aichat", ".ai-sql-toggle", function () {
            $(this).next(".ai-sql-box").toggle();
        });
    }

    function init() {
        if ($("#aiChatDock").length === 0) {
            return;
        }
        bind();
    }

    if (window.jQuery) {
        $(init);
    } else {
        document.addEventListener("DOMContentLoaded", init);
    }
})();
