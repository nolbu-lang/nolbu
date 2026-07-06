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

        // 연도+사업명+부서가 같은 차수 행들을 하나로 묶어(셀 병합) 표시할 컬럼
        var mergeCols = { "연도": true, "사업명": true, "소관부서": true };

        // 그룹(_grpKey) 단위 rowspan 계산
        var isGroupStart = {};
        var groupSpan = {};
        for (var g = 0; g < dataList.length; g++) {
            var key = dataList[g]["_grpKey"];
            if (typeof key === "undefined" || key === null) { key = "__row" + g; }
            if (g === 0 || key !== dataList[g - 1]["_grpKey"]) {
                isGroupStart[g] = true;
                var span = 1;
                for (var n = g + 1; n < dataList.length; n++) {
                    if (dataList[n]["_grpKey"] === dataList[g]["_grpKey"]) { span++; } else { break; }
                }
                groupSpan[g] = span;
            }
        }

        var html = '<div class="ai-result-table-wrap"><table class="ai-result-table"><thead><tr>';
        for (var c = 0; c < columns.length; c++) {
            html += "<th>" + htmlEscape(columns[c]) + "</th>";
        }
        html += "</tr></thead><tbody>";

        for (var r = 0; r < dataList.length; r++) {
            var row = dataList[r];
            var detail = row["_detail"];
            var title = row["_detailTitle"] || row["사업명"] || "";
            var clickable = detail && String(detail).length > 0;
            var cls = [];
            if (clickable) { cls.push("ai-row-clickable"); }
            if (isGroupStart[r]) { cls.push("ai-grp-start"); }
            var attrs = cls.length ? (' class="' + cls.join(" ") + '"') : "";
            if (clickable) {
                // 상세 HTML은 base64로 보관(긴 HTML·특수문자 깨짐 방지). 구버전 data-detail 도 호환.
                var detailB64 = encodeDetailB64(String(detail));
                attrs += ' title="클릭하면 연도별·차수별 상세를 새 창에서 봅니다"'
                    + ' data-detail-b64="' + htmlEscape(detailB64) + '"'
                    + ' data-title="' + htmlEscape(encodeURIComponent(String(title))) + '"';
            }
            html += "<tr" + attrs + ">";
            for (var k = 0; k < columns.length; k++) {
                var col = columns[k];
                if (mergeCols[col]) {
                    // 그룹 첫 행에서만 셀 출력(rowspan). 나머지 차수 행은 병합되어 생략.
                    if (isGroupStart[r]) {
                        var rs = (groupSpan[r] > 1) ? (' rowspan="' + groupSpan[r] + '"') : "";
                        var cellCls = (col === "사업명") ? "ai-grp-cell ai-biz-cell" : "ai-grp-cell";
                        html += '<td class="' + cellCls + '"' + rs + ">" + htmlEscape(row[col]) + "</td>";
                    }
                } else {
                    html += "<td>" + htmlEscape(row[col]) + "</td>";
                }
            }
            html += "</tr>";
        }
        html += "</tbody></table></div>";
        return html;
    }

    /** 사업 상세 창/모달 공통 표 스타일 */
    var DETAIL_TABLE_CSS = ''
        + '.ai-biz-detail-table{border-collapse:collapse;width:100%;font-size:13px;table-layout:fixed;}'
        + '.ai-biz-detail-table th,.ai-biz-detail-table td{border:1px solid #d5dbe3;padding:6px 8px;text-align:left;vertical-align:top;}'
        + '.ai-biz-detail-table th{background:#eef2f7;color:#1f4e79;font-weight:bold;}'
        + '.ai-biz-detail-table tr:nth-child(even) td{background:#fafbfc;}'
        + '.ai-biz-detail-table .ai-th-budget,.ai-biz-detail-table .ai-td-budget{width:16%;word-break:break-word;white-space:normal;line-height:1.45;}'
        + '.ai-biz-detail-table .ai-th-demand,.ai-biz-detail-table .ai-td-demand{width:34%;}'
        + '.ai-biz-detail-table .ai-th-exam,.ai-biz-detail-table .ai-td-exam{width:34%;}'
        + '.ai-biz-detail-table .ai-th-dept,.ai-biz-detail-table .ai-td-dept{width:16%;word-break:break-word;white-space:normal;line-height:1.45;}'
        + '.ai-biz-detail-table .ai-td-wrap{word-break:break-word;line-height:1.55;}';

    /** 상세 HTML을 data 속성에 안전하게 저장 */
    function encodeDetailB64(html) {
        try {
            return btoa(unescape(encodeURIComponent(String(html))));
        } catch (e) {
            return "";
        }
    }

    function decodeDetailB64(b64) {
        if (!b64) {
            return "";
        }
        try {
            return decodeURIComponent(escape(atob(String(b64))));
        } catch (e) {
            return "";
        }
    }

    /** 구버전(5열) 상세 표에서 [구분] 열 제거 — 캐시·미배포 WAR 대응 */
    function normalizeDetailTableHtml(html) {
        if (!html) {
            return "";
        }
        var s = String(html);
        if (s.indexOf("ai-th-gubun") < 0 && s.indexOf("[구분]") < 0) {
            return s;
        }
        try {
            var $h = $("<div>").html(s);
            $h.find("table.ai-biz-detail-table").each(function () {
                var $t = $(this);
                var idx = -1;
                $t.find("thead tr").first().children("th").each(function (i) {
                    var $th = $(this);
                    if ($th.hasClass("ai-th-gubun") || $.trim($th.text()) === "[구분]") {
                        idx = i;
                        return false;
                    }
                });
                if (idx >= 0) {
                    $t.find("tr").each(function () {
                        $(this).children("td,th").eq(idx).remove();
                    });
                }
            });
            return $h.html();
        } catch (e2) {
            return s
                .replace(/<th[^>]*class="[^"]*ai-th-gubun[^"]*"[^>]*>\s*\[구분\]\s*<\/th>/gi, "")
                .replace(/<td[^>]*class="[^"]*ai-td-gubun[^"]*"[^>]*>[\s\S]*?<\/td>/gi, "");
        }
    }

    function openDetailWindow(title, detailHtml) {
        var safeTitle = htmlEscape(title || "사업 상세");
        var bodyHtml = normalizeDetailTableHtml(detailHtml || "");

        // 1순위: 실제 새 창. 팝업 차단 등으로 실패하면 화면 내 모달로 대체(정부망 브라우저 대응)
        var win = null;
        try {
            win = window.open("", "_blank", "width=960,height=720,scrollbars=yes,resizable=yes");
        } catch (e) {
            win = null;
        }

        if (win && win.document) {
            var doc = win.document;
            doc.open();
            doc.write(
                '<!DOCTYPE html><html lang="ko"><head><meta charset="UTF-8">'
                + '<title>' + safeTitle + '</title>'
                + '<style>'
                + 'body{font-family:"맑은 고딕","Malgun Gothic",sans-serif;margin:0;background:#f4f6f8;color:#222;}'
                + '.hd{background:#1f4e79;color:#fff;padding:14px 20px;font-size:16px;font-weight:bold;}'
                + '.bd{padding:18px 22px;line-height:1.7;font-size:14px;}'
                + '.card{background:#fff;border:1px solid #dfe3e8;border-radius:8px;padding:18px 20px;'
                + 'box-shadow:0 1px 3px rgba(0,0,0,.06);overflow-x:auto;}'
                + DETAIL_TABLE_CSS
                + '</style></head><body>'
                + '<div class="hd">' + safeTitle + '</div>'
                + '<div class="bd"><div class="card">' + bodyHtml + '</div></div>'
                + '</body></html>');
            doc.close();
            win.focus();
            return;
        }

        openDetailModal(safeTitle, bodyHtml);
    }

    function openDetailModal(safeTitle, bodyHtml) {
        $(".ai-detail-modal").remove();
        var html = '<div class="ai-detail-modal">'
            + '<div class="ai-detail-dim"></div>'
            + '<div class="ai-detail-panel">'
            + '<div class="ai-detail-hd"><span>' + safeTitle + '</span>'
            + '<button type="button" class="ai-detail-close" aria-label="닫기">&times;</button></div>'
            + '<div class="ai-detail-bd"><div class="ai-detail-card">' + (bodyHtml || "") + '</div></div>'
            + '</div></div>';
        $("body").append(html);
    }

    function appendBotAnswer($loadingEl, data) {
        var answer = (data && data.answer) ? data.answer : "응답이 없습니다.";

        var tableHtml = "";
        if (data && data.columns && data.dataList) {
            tableHtml = buildResultTable(data.columns, data.dataList);
        }

        // 표가 있는 답변은 말풍선을 대화창 전체 폭으로 넓혀 가로 열이 모두 보이도록 한다.
        var bubbleClass = "ai-bubble ai-pre" + (tableHtml ? " ai-bubble-wide" : "");
        var inner = '<div class="' + bubbleClass + '">' + nl2br(answer);

        if (tableHtml) {
            inner += tableHtml;
            if (typeof data.rowCount !== "undefined") {
                inner += '<div style="font-size:11px;color:#888;margin-top:4px;">총 ' + data.rowCount + '건 (표시 행수 제한 적용)</div>';
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

        // 검색결과 표의 행 클릭 → 해당 사업 상세를 새 창에서 표시
        $(document).on("click.aichat", ".ai-result-table tbody tr.ai-row-clickable", function () {
            var detail = "";
            var title = "";
            var b64 = $(this).attr("data-detail-b64");
            if (b64) {
                detail = decodeDetailB64(b64);
            }
            if (!detail) {
                try {
                    detail = decodeURIComponent($(this).attr("data-detail") || "");
                } catch (e) {
                    detail = $(this).attr("data-detail") || "";
                }
            }
            try {
                title = decodeURIComponent($(this).attr("data-title") || "");
            } catch (e) {
                title = $(this).attr("data-title") || "";
            }
            openDetailWindow(title, detail);
        });

        // 상세 모달 닫기 (닫기 버튼 · 배경 클릭)
        $(document).on("click.aichat", ".ai-detail-close, .ai-detail-dim", function () {
            $(".ai-detail-modal").remove();
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
