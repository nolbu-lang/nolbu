/*
 * AI 예산편성 도우미
 * - 상단 상태바 사각버튼 → 독립 팝업창 (/ai/aiChatPopup.do)
 * - 팝업: 회계년도·내부검색·일반자료검색 체크 + 매뉴얼 업로드 + 지우기/닫기
 */
(function () {
    var meta = {
        minFisYear: 2013,
        latestFisYear: String(new Date().getFullYear()),
        manualCanManage: true,
        manualFiles: []
    };
    var aiPopupWin = null;
    var AI_POPUP_NAME = "aiBudgetChatPopup";
    var AI_POPUP_POS_KEY = "bcjis.aiChatPopup.pos";
    var AI_POPUP_DEFAULT_W = 1020;
    var AI_POPUP_DEFAULT_H = 760;
    var aiOpenerWatchTimer = null;
    var GUIDE_HTML = ''
        + '<div class="ai-msg ai-bot ai-guide-msg">'
        + '<div class="ai-bubble ai-guide">'
        + '<div class="ai-guide-line">안녕하세요. AI 예산편성 도우미입니다.</div>'
        + '<div class="ai-guide-line"><span class="ai-guide-mark">▸</span><span class="ai-guide-label">내부검색</span> 사업명·구분·검토내용·조건검색어 체크 후 검색</div>'
        + '<div class="ai-guide-line"><span class="ai-guide-mark">▸</span><span class="ai-guide-label">일반검색</span> 법령·조례 / 보도자료,고시공고 / 예산운용지침 중 <b>하나만</b> 선택</div>'
        + '<div class="ai-guide-line"><span class="ai-guide-mark">▸</span>내부검색과 일반검색은 <b>동시에 선택불가</b></div>'
        + '<div class="ai-guide-line ai-guide-sub">키워드 조건: AND는 &amp; / OR는 , &nbsp;·&nbsp; 띄어쓰기·영문 대소문자 무시</div>'
        + '<div class="ai-guide-line ai-guide-sub">예) 투자심사&amp;40억원,예비타당성 / 세출예산 절차별 이행사항</div>'
        + '</div></div>';

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
        $("#aiChatMessages").append(
            '<div class="ai-msg ai-user"><div class="ai-bubble ai-pre">' + nl2br(text) + '</div></div>'
        );
        scrollToBottom();
    }

    function appendBotLoading() {
        var $el = $('<div class="ai-msg ai-bot ai-loading"><div class="ai-bubble">답변을 생성하고 있습니다...</div></div>');
        $("#aiChatMessages").append($el);
        scrollToBottom();
        return $el;
    }

    /** 긴 파일명 축약 (확장자 유지). 전체명은 title 속성에 둠. */
    function shortenDisplayName(name, maxLen) {
        var s = name == null ? "" : String(name);
        var lim = maxLen || 28;
        if (s.length <= lim) {
            return s;
        }
        var ext = "";
        var dot = s.lastIndexOf(".");
        if (dot > 0 && s.length - dot <= 8) {
            ext = s.substring(dot);
            s = s.substring(0, dot);
        }
        var keep = lim - ext.length - 1;
        if (keep < 10) {
            keep = 10;
        }
        if (s.length <= keep) {
            return s + ext;
        }
        return s.substring(0, keep) + "…" + ext;
    }

    /** title/sub 에서 파일명과 페이지 문구 분리 */
    function splitManualTitlePages(title, sub) {
        var name = title == null ? "" : String(title);
        var pages = "";
        var m = name.match(/^(.*?)\s*\(p\.([^)]+)\)\s*$/);
        if (m) {
            name = m[1];
            pages = "p." + m[2];
        }
        if (!pages && sub) {
            var sm = String(sub).match(/(?:관련\s*페이지\s*:\s*)?(.+)/);
            if (sm) {
                var p = sm[1].replace(/^\s*p\.\s*/i, "").trim();
                pages = p.indexOf("p.") === 0 ? p : ("p." + p);
            }
        }
        if (pages && pages.indexOf("관련") === 0) {
            pages = pages.replace(/^관련\s*페이지\s*:\s*/i, "p.");
        }
        return { name: name, pages: pages };
    }

    function buildGeneralItemsHtml(items, sourcesOnly) {
        if (!items || !items.length) {
            return "";
        }
        var html = '<div class="ai-general-result">';
        if (sourcesOnly) {
            html += '<div class="ai-general-src-hd">관련자료출처</div>';
        }
        html += '<ol class="ai-general-ol">';
        for (var i = 0; i < items.length; i++) {
            var it = items[i] || {};
            var kind = it.kind ? String(it.kind) : "";
            var title = it.title ? String(it.title) : "(제목 없음)";
            var sub = it.sub ? String(it.sub) : "";
            var body = sourcesOnly ? "" : (it.body ? String(it.body) : "");
            var url = it.url ? String(it.url) : "";
            var date = it.date ? String(it.date) : "";
            var isManual = (kind === "매뉴얼" || kind === "예산운용지침" || !!it.manualId);
            var displayTitle = title;
            var pagesText = "";
            // 예산운용지침: 파일명·페이지 분리 — 긴 파일명 때문에 페이지가 잘리지 않게
            if (isManual) {
                if (it.pages) {
                    pagesText = "p." + String(it.pages);
                    displayTitle = shortenDisplayName(title, 28);
                } else {
                    var parsed = splitManualTitlePages(title, sub);
                    displayTitle = shortenDisplayName(parsed.name, 28);
                    pagesText = parsed.pages;
                }
            }
            html += '<li class="ai-general-li' + (sourcesOnly ? " ai-general-src" : "") + '">';
            if (kind) {
                var kindLabel = (kind === "매뉴얼") ? "예산운용지침" : kind;
                html += '<span class="ai-general-kind">[' + htmlEscape(kindLabel) + ']</span> ';
            }
            if (url) {
                html += '<a class="ai-general-link" href="' + htmlEscape(url)
                    + '" target="_blank" rel="noopener noreferrer" title="' + htmlEscape(title) + '">'
                    + htmlEscape(displayTitle) + '</a>';
            } else {
                html += '<span class="ai-general-title" title="' + htmlEscape(title) + '">'
                    + htmlEscape(displayTitle) + '</span>';
            }
            if (pagesText) {
                html += ' <span class="ai-general-pages">' + htmlEscape(pagesText) + '</span>';
            }
            // 게시일(시홈페이지 등)은 출처 목록에도 표시
            if (date && !isManual) {
                html += ' <span class="ai-general-date">' + htmlEscape(date) + '</span>';
            }
            if (sub && !sourcesOnly && !isManual) {
                html += '<div class="ai-general-sub">' + htmlEscape(sub) + '</div>';
            } else if (sub && sourcesOnly && !isManual && !pagesText) {
                html += ' <span class="ai-general-pages">' + htmlEscape(sub) + '</span>';
            }
            if (body) {
                html += '<div class="ai-general-body">' + nl2br(body) + '</div>';
            }
            html += '</li>';
        }
        html += '</ol></div>';
        return html;
    }

    function buildResultTable(columns, dataList) {
        if (!columns || columns.length === 0 || !dataList || dataList.length === 0) {
            return "";
        }
        var mergeCols = { "연도": true, "사업명": true, "소관부서": true };
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
        var detailByKey = {};
        for (var d = 0; d < dataList.length; d++) {
            var dr = dataList[d];
            if (dr["_detailRows"] && dr["_detailRows"].length > 0 && dr["_detailKey"]) {
                detailByKey[dr["_detailKey"]] = dr["_detailRows"];
            }
        }
        var html = '<div class="ai-result-table-wrap"><table class="ai-result-table"><thead><tr>';
        for (var c = 0; c < columns.length; c++) {
            html += "<th>" + htmlEscape(columns[c]) + "</th>";
        }
        html += "</tr></thead><tbody>";
        for (var r = 0; r < dataList.length; r++) {
            var row = dataList[r];
            var detailKey = row["_detailKey"] || row["사업명"] || "";
            var detailRows = row["_detailRows"] || detailByKey[detailKey];
            var title = row["_detailTitle"] || row["사업명"] || "";
            var hasRows = detailRows && detailRows.length > 0;
            var cls = [];
            if (hasRows) { cls.push("ai-row-clickable"); }
            if (isGroupStart[r]) { cls.push("ai-grp-start"); }
            var attrs = cls.length ? (' class="' + cls.join(" ") + '"') : "";
            if (hasRows) {
                attrs += ' title="클릭하면 연도별·차수별 상세를 새 창에서 봅니다"'
                    + ' data-detail-rows="' + htmlEscape(encodeURIComponent(JSON.stringify(detailRows))) + '"'
                    + ' data-title="' + htmlEscape(encodeURIComponent(String(title))) + '"';
            }
            html += "<tr" + attrs + ">";
            for (var k = 0; k < columns.length; k++) {
                var col = columns[k];
                if (mergeCols[col]) {
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

    var DETAIL_TABLE_CSS = ''
        + '.ai-biz-detail-table{border-collapse:collapse;width:100%;font-size:12px;table-layout:fixed;}'
        + '.ai-biz-detail-table th,.ai-biz-detail-table td{border:1px solid #d5dbe3;padding:6px 7px;text-align:left;vertical-align:top;}'
        + '.ai-biz-detail-table th{background:#eef2f7;color:#1f4e79;font-weight:bold;}'
        + '.ai-biz-detail-table tr:nth-child(even) td{background:#fafbfc;}'
        + '.ai-biz-detail-table .ai-td-wrap{word-break:break-word;line-height:1.55;}'
        + '.ai-biz-detail-table .ai-amt{font-weight:bold;}'
        + '.ai-biz-detail-table .ai-frsc{color:#444;font-size:11px;margin-top:3px;line-height:1.4;}'
        + '.ai-biz-detail-table .ai-dept-sub{color:#555;font-size:11px;margin-top:3px;}';

    var CLICK_HINT = "사업명을 클릭하면 해당 사업의 연도별·차수별 상세 내용을 새 창에서 확인할 수 있습니다.";

    function cellPlainText(el) {
        var t = (el && (el.innerText || el.textContent)) ? String(el.innerText || el.textContent) : "";
        return t.replace(/\u00a0/g, " ").replace(/\r?\n+/g, " ").replace(/\t/g, " ").replace(/\s+/g, " ").trim();
    }

    /** 셀 내용을 화면 줄바꿈 유지한 HTML로 (한글·엑셀 붙여넣기용) */
    function cellToPasteInnerHtml(el) {
        var t = (el && (el.innerText || el.textContent)) ? String(el.innerText || el.textContent) : "";
        t = t.replace(/\u00a0/g, " ").replace(/\r\n/g, "\n").replace(/\r/g, "\n");
        t = t.replace(/[ \t]+\n/g, "\n").replace(/\n[ \t]+/g, "\n").replace(/\n{3,}/g, "\n\n").trim();
        if (!t) {
            return "&nbsp;";
        }
        return htmlEscape(t).replace(/\n/g, "<br>");
    }

    function countTableCols(table) {
        if (!table) {
            return 1;
        }
        var first = table.querySelector("tr");
        if (!first) {
            return 1;
        }
        var n = 0;
        var cells = first.children;
        for (var i = 0; i < cells.length; i++) {
            n += parseInt(cells[i].getAttribute("colspan") || "1", 10) || 1;
        }
        return Math.max(1, n);
    }

    /**
     * 화면 표 모양(rowspan/colspan·줄바꿈·테두리)을 살린 붙여넣기용 HTML.
     * 한글(HWP)·엑셀이 인식하기 쉬운 inline style + border 표.
     */
    function buildOfficePasteHtml(table, title) {
        if (!table) {
            return "";
        }
        var clone = table.cloneNode(true);
        var cells = clone.querySelectorAll("th, td");
        for (var i = 0; i < cells.length; i++) {
            var cell = cells[i];
            var isTh = String(cell.tagName).toUpperCase() === "TH";
            var rs = cell.getAttribute("rowspan");
            var cs = cell.getAttribute("colspan");
            cell.innerHTML = cellToPasteInnerHtml(cell);
            cell.removeAttribute("class");
            cell.removeAttribute("style");
            cell.removeAttribute("width");
            var st = "border:1px solid #000000;padding:4px 6px;vertical-align:top;"
                + "font-size:10pt;font-family:'맑은 고딕','Malgun Gothic',sans-serif;";
            if (isTh) {
                st += "background-color:#D9E2F3;font-weight:bold;text-align:center;";
            } else {
                st += "background-color:#FFFFFF;";
            }
            cell.setAttribute("style", st);
            if (rs) { cell.setAttribute("rowspan", rs); }
            if (cs) { cell.setAttribute("colspan", cs); }
        }
        var parts = [];
        parts.push('<table border="1" cellspacing="0" cellpadding="4" '
            + 'style="border-collapse:collapse;border:1px solid #000000;">');
        parts.push("<tbody>");
        if (title) {
            var cols = countTableCols(table);
            parts.push('<tr><td colspan="' + cols + '" style="border:1px solid #000000;padding:6px 8px;'
                + "font-size:12pt;font-weight:bold;font-family:'맑은 고딕','Malgun Gothic',sans-serif;"
                + 'background-color:#1F4E79;color:#FFFFFF;">'
                + htmlEscape(String(title)) + "</td></tr>");
        }
        var rows = clone.querySelectorAll("tr");
        for (var r = 0; r < rows.length; r++) {
            parts.push(rows[r].outerHTML);
        }
        parts.push("</tbody></table>");
        return parts.join("");
    }

    /** Windows 한글·엑셀용 CF_HTML 래핑 */
    function wrapCfHtml(fragment) {
        var start = "<html><body><!--StartFragment-->";
        var end = "<!--EndFragment--></body></html>";
        var html = start + fragment + end;
        function pad(n) {
            var s = String(n);
            while (s.length < 10) { s = "0" + s; }
            return s;
        }
        var header =
            "Version:0.9\r\n"
            + "StartHTML:<<<<<<<1\r\n"
            + "EndHTML:<<<<<<<2\r\n"
            + "StartFragment:<<<<<<<3\r\n"
            + "EndFragment:<<<<<<<4\r\n";
        var startHTML = header.length;
        var startFragment = startHTML + start.length;
        var endFragment = startFragment + fragment.length;
        var endHTML = startHTML + html.length;
        return header
            .replace("<<<<<<<1", pad(startHTML))
            .replace("<<<<<<<2", pad(endHTML))
            .replace("<<<<<<<3", pad(startFragment))
            .replace("<<<<<<<4", pad(endFragment))
            + html;
    }

    /** rowspan/colspan 반영 TSV (텍스트 폴백) */
    function tableToTsv(table) {
        if (!table) {
            return "";
        }
        var trs = table.querySelectorAll("tr");
        var matrix = [];
        for (var r = 0; r < trs.length; r++) {
            if (!matrix[r]) {
                matrix[r] = [];
            }
            var col = 0;
            var cells = trs[r].children;
            for (var c = 0; c < cells.length; c++) {
                while (matrix[r][col] !== undefined) {
                    col++;
                }
                var cell = cells[c];
                var text = (cell.innerText || cell.textContent || "")
                    .replace(/\u00a0/g, " ").replace(/\r\n/g, "\n").replace(/\r/g, "\n").trim();
                text = text.replace(/\t/g, " ").replace(/\n/g, " / ");
                var rs = parseInt(cell.getAttribute("rowspan") || "1", 10) || 1;
                var cs = parseInt(cell.getAttribute("colspan") || "1", 10) || 1;
                for (var i = 0; i < rs; i++) {
                    if (!matrix[r + i]) {
                        matrix[r + i] = [];
                    }
                    for (var j = 0; j < cs; j++) {
                        // 병합 영역은 첫 칸만 값, 나머지는 빈칸 → 화면 병합 느낌
                        matrix[r + i][col + j] = (i === 0 && j === 0) ? text : "";
                    }
                }
                col += cs;
            }
        }
        var lines = [];
        for (var mr = 0; mr < matrix.length; mr++) {
            var row = matrix[mr] || [];
            var max = 0;
            for (var k = 0; k < row.length; k++) {
                if (row[k] !== undefined) {
                    max = k + 1;
                }
            }
            var parts = [];
            for (var p = 0; p < max; p++) {
                parts.push(row[p] !== undefined ? row[p] : "");
            }
            lines.push(parts.join("\t"));
        }
        return lines.join("\r\n");
    }

    function fallbackCopyText(text, done, fail) {
        var ta = document.createElement("textarea");
        ta.value = text;
        ta.setAttribute("readonly", "readonly");
        ta.style.cssText = "position:fixed;left:-9999px;top:0;";
        document.body.appendChild(ta);
        ta.select();
        var ok = false;
        try {
            ok = document.execCommand("copy");
        } catch (e) {
            ok = false;
        }
        document.body.removeChild(ta);
        if (ok) {
            if (done) { done(); }
        } else if (fail) {
            fail();
        }
    }

    /**
     * 한글(HWP)·엑셀용 표 복사.
     * 1) 화면과 같은 HTML 표를 선택 후 execCommand('copy') — Office가 가장 잘 인식
     * 2) 실패 시 CF_HTML + text/plain Clipboard API
     */
    function copyTableForOffice(table, title, doneMsg) {
        if (!table) {
            alert("복사할 표가 없습니다.");
            return;
        }
        var htmlTable = buildOfficePasteHtml(table, title || "");
        if (!htmlTable) {
            alert("복사할 표 내용이 없습니다.");
            return;
        }
        var plain = (title ? String(title) + "\r\n\r\n" : "") + tableToTsv(table);
        var msg = doneMsg || "표가 복사되었습니다. 한글(HWP) 또는 엑셀에 Ctrl+V로 붙여넣기 하세요.";
        var done = function () { alert(msg); };
        var fail = function () {
            alert("복사에 실패했습니다. 표를 드래그하여 Ctrl+C로 복사해 주세요.");
        };

        var host = document.createElement("div");
        host.setAttribute("contenteditable", "true");
        host.setAttribute("aria-hidden", "true");
        host.style.cssText = "position:fixed;left:0;top:0;width:1px;height:1px;opacity:0;z-index:-1;overflow:hidden;";
        host.innerHTML = htmlTable;
        document.body.appendChild(host);

        var sel = window.getSelection();
        var ok = false;
        try {
            sel.removeAllRanges();
            var range = document.createRange();
            range.selectNodeContents(host);
            sel.addRange(range);
            ok = document.execCommand("copy");
        } catch (e) {
            ok = false;
        }
        try { sel.removeAllRanges(); } catch (e2) { /* ignore */ }
        document.body.removeChild(host);

        if (ok) {
            done();
            return;
        }

        var cfHtml = wrapCfHtml(htmlTable);
        if (navigator.clipboard && window.ClipboardItem) {
            try {
                var item = new ClipboardItem({
                    "text/plain": new Blob([plain], { type: "text/plain" }),
                    "text/html": new Blob([cfHtml], { type: "text/html" })
                });
                navigator.clipboard.write([item]).then(done).catch(function () {
                    fallbackCopyText(plain, done, fail);
                });
                return;
            } catch (e3) { /* fall through */ }
        }
        if (navigator.clipboard && navigator.clipboard.writeText) {
            navigator.clipboard.writeText(plain).then(done).catch(function () {
                fallbackCopyText(plain, done, fail);
            });
            return;
        }
        fallbackCopyText(plain, done, fail);
    }

    function copyResultTableFromBtn($btn) {
        var $bubble = $btn.closest(".ai-bubble");
        var table = $bubble.find(".ai-result-table").get(0);
        copyTableForOffice(table, "", "결과 표가 복사되었습니다. 한글(HWP) 또는 엑셀에 Ctrl+V로 붙여넣기 하세요.");
    }

    function copyDetailTable(title, table) {
        copyTableForOffice(table, title || "사업 상세",
            "상세 표가 복사되었습니다. 한글(HWP) 또는 엑셀에 Ctrl+V로 붙여넣기 하세요.");
    }

    function buildDetailCopyScript() {
        // 상세 팝업 창용: 부모와 동일한 Office 표 붙여넣기 로직(인라인)
        return ""
            + "function aiEsc(s){return String(s||'').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/\"/g,'&quot;');}"
            + "function aiCellHtml(el){var t=(el&&(el.innerText||el.textContent))?String(el.innerText||el.textContent):'';"
            + "t=t.replace(/\\u00a0/g,' ').replace(/\\r\\n/g,'\\n').replace(/\\r/g,'\\n');"
            + "t=t.replace(/[ \\t]+\\n/g,'\\n').replace(/\\n[ \\t]+/g,'\\n').replace(/\\n{3,}/g,'\\n\\n').trim();"
            + "if(!t)return'&nbsp;';return aiEsc(t).replace(/\\n/g,'<br>');}"
            + "function aiCols(table){var tr=table.querySelector('tr');if(!tr)return 1;var n=0,cells=tr.children,i;"
            + "for(i=0;i<cells.length;i++){n+=parseInt(cells[i].getAttribute('colspan')||'1',10)||1;}return Math.max(1,n);}"
            + "function aiBuildHtml(table,title){var clone=table.cloneNode(true),cells=clone.querySelectorAll('th,td'),i,cell,isTh,rs,cs,st,parts=[],rows,r;"
            + "for(i=0;i<cells.length;i++){cell=cells[i];isTh=String(cell.tagName).toUpperCase()==='TH';"
            + "rs=cell.getAttribute('rowspan');cs=cell.getAttribute('colspan');"
            + "cell.innerHTML=aiCellHtml(cell);cell.removeAttribute('class');cell.removeAttribute('style');"
            + "st='border:1px solid #000000;padding:4px 6px;vertical-align:top;font-size:10pt;"
            + "font-family:Malgun Gothic,sans-serif;';"
            + "st+=isTh?'background-color:#D9E2F3;font-weight:bold;text-align:center;':'background-color:#FFFFFF;';"
            + "cell.setAttribute('style',st);if(rs)cell.setAttribute('rowspan',rs);if(cs)cell.setAttribute('colspan',cs);}"
            + "parts.push('<table border=\"1\" cellspacing=\"0\" cellpadding=\"4\" style=\"border-collapse:collapse;border:1px solid #000000;\"><tbody>');"
            + "if(title){parts.push('<tr><td colspan=\"'+aiCols(table)+'\" style=\"border:1px solid #000000;padding:6px 8px;"
            + "font-size:12pt;font-weight:bold;font-family:Malgun Gothic,sans-serif;"
            + "background-color:#1F4E79;color:#FFFFFF;\">'+aiEsc(title)+'</td></tr>');}"
            + "rows=clone.querySelectorAll('tr');for(r=0;r<rows.length;r++){parts.push(rows[r].outerHTML);}"
            + "parts.push('</tbody></table>');return parts.join('');}"
            + "function aiCopyDetail(){var titleEl=document.getElementById('aiDetailTitle');"
            + "var table=document.querySelector('.ai-biz-detail-table');"
            + "var title=titleEl?(titleEl.innerText||titleEl.textContent||'사업 상세'):'사업 상세';"
            + "if(!table){alert('복사할 표가 없습니다.');return;}"
            + "var htmlTable=aiBuildHtml(table,title);"
            + "var host=document.createElement('div');host.contentEditable='true';"
            + "host.style.cssText='position:fixed;left:0;top:0;width:1px;height:1px;opacity:0;overflow:hidden;';"
            + "host.innerHTML=htmlTable;document.body.appendChild(host);"
            + "var sel=window.getSelection(),ok=false;try{sel.removeAllRanges();var rg=document.createRange();"
            + "rg.selectNodeContents(host);sel.addRange(rg);ok=document.execCommand('copy');}catch(e){ok=false;}"
            + "try{sel.removeAllRanges();}catch(e2){}document.body.removeChild(host);"
            + "if(ok){alert('상세 표가 복사되었습니다. 한글(HWP) 또는 엑셀에 Ctrl+V로 붙여넣기 하세요.');return;}"
            + "alert('복사에 실패했습니다. 표를 드래그하여 Ctrl+C로 복사해 주세요.');}"
            + "var btn=document.getElementById('aiDetailCopyBtn');if(btn){btn.onclick=function(e){e.preventDefault();aiCopyDetail();};}";
    }

    function amtWithFrscHtml(amt, frsc) {
        var a = amt ? String(amt) : "-";
        var f = frsc ? String(frsc) : "";
        var html = '<div class="ai-amt">' + htmlEscape(a) + '</div>';
        if (f) {
            html += '<div class="ai-frsc">' + nl2br(f) + '</div>';
        }
        return html;
    }

    /** 상세 표: 연도/차수(+소관부서) | 구분 | 총사업비 | 기정액 | 요구액 | 조정액 | 검토내용 */
    function buildDetailTableFromRows(rows) {
        if (!rows || !rows.length) {
            return "";
        }
        var isLegacy = rows[0] && typeof rows[0].yearDgr === "undefined"
            && typeof rows[0].demandAmt === "undefined"
            && typeof rows[0].budget !== "undefined";
        if (isLegacy) {
            return '<table class="ai-biz-detail-table"><tbody><tr><td>'
                + htmlEscape(JSON.stringify(rows[0]).substring(0, 80))
                + '...</td></tr></tbody></table>';
        }

        var showTot = false;
        for (var s = 0; s < rows.length; s++) {
            if (rows[s].totAmt && String(rows[s].totAmt) !== "-" && String(rows[s].totAmt).length > 0
                && String(rows[s].totAmt).indexOf("0백만원") !== 0) {
                var raw = String(rows[s].totAmt).replace(/[^0-9-]/g, "");
                if (raw && raw !== "0") { showTot = true; break; }
            }
            if (rows[s].hasTot === true || rows[s].hasTot === "Y") {
                showTot = true;
                break;
            }
        }

        // 금액열 ~30% 축소(12→8), 구분·검토내용 ~15% 확대
        var html = '<table class="ai-biz-detail-table"><thead><tr>'
            + '<th style="width:11%;">연도/차수</th>'
            + '<th style="width:21%;">구분</th>';
        if (showTot) {
            html += '<th style="width:8%;">총사업비</th>';
        }
        html += '<th style="width:8%;">기정액</th>'
            + '<th style="width:8%;">요구액</th>'
            + '<th style="width:8%;">조정액</th>'
            + '<th style="width:' + (showTot ? '28' : '36') + '%;">검토내용</th>'
            + '</tr></thead><tbody>';

        for (var i = 0; i < rows.length; i++) {
            var line = rows[i] || {};
            var yearCell = htmlEscape(line.yearDgr || "");
            if (line.fisFgNm) {
                yearCell += '<div class="ai-dept-sub">' + htmlEscape(String(line.fisFgNm)) + '</div>';
            }
            if (line.dept) {
                yearCell += '<div class="ai-dept-sub">' + htmlEscape(String(line.dept)) + '</div>';
            }
            var gubun = line.gubun ? String(line.gubun) : "";
            var exam = line.exam ? String(line.exam) : "";
            html += "<tr>"
                + '<td class="ai-td-wrap">' + yearCell + "</td>"
                + '<td class="ai-td-wrap">' + (gubun ? nl2br(gubun) : "-") + "</td>";
            if (showTot) {
                html += '<td class="ai-td-wrap">' + amtWithFrscHtml(line.totAmt, line.totFrsc) + "</td>";
            }
            html += '<td class="ai-td-wrap">' + amtWithFrscHtml(line.preAmt, line.preFrsc) + "</td>"
                + '<td class="ai-td-wrap">' + amtWithFrscHtml(line.demandAmt, line.demandFrsc) + "</td>"
                + '<td class="ai-td-wrap">' + amtWithFrscHtml(line.adjAmt, line.adjFrsc) + "</td>"
                + '<td class="ai-td-wrap">' + (exam ? nl2br(exam) : "-") + "</td>"
                + "</tr>";
        }
        return html + "</tbody></table>";
    }

    function parseDetailRowsAttr(attrVal) {
        if (!attrVal) {
            return null;
        }
        try {
            return JSON.parse(decodeURIComponent(String(attrVal)));
        } catch (e) {
            try {
                return JSON.parse(String(attrVal));
            } catch (e2) {
                return null;
            }
        }
    }

    function resolveDetailHtml($row) {
        var parsed = parseDetailRowsAttr($row.attr("data-detail-rows"));
        if (parsed && parsed.length > 0) {
            return buildDetailTableFromRows(parsed);
        }
        return "";
    }

    function openDetailWindow(title, detailHtml) {
        var safeTitle = htmlEscape(title || "사업 상세");
        var rawTitle = title || "사업 상세";
        var win = null;
        try {
            win = window.open("", "_blank", "width=1200,height=720,scrollbars=yes,resizable=yes");
        } catch (e) {
            win = null;
        }
        if (win && win.document) {
            win.document.open();
            win.document.write(
                '<!DOCTYPE html><html lang="ko"><head><meta charset="UTF-8">'
                + '<title>' + safeTitle + '</title><style>'
                + 'body{font-family:"맑은 고딕","Malgun Gothic",sans-serif;margin:0;background:#f4f6f8;color:#222;}'
                + '.hd{background:#1f4e79;color:#fff;padding:12px 16px;display:flex;align-items:center;gap:12px;}'
                + '.hd-title{flex:1 1 auto;font-size:16px;font-weight:bold;min-width:0;word-break:break-word;}'
                + '.hd-copy{flex:0 0 auto;background:#fff;color:#1f4e79;border:1px solid #c5d4e8;border-radius:4px;'
                + 'padding:5px 12px;font-size:12px;font-weight:bold;cursor:pointer;font-family:inherit;}'
                + '.hd-copy:hover{background:#eef4fb;}'
                + '.bd{padding:18px 22px;}.card{background:#fff;border:1px solid #dfe3e8;border-radius:8px;padding:18px;overflow-x:auto;}'
                + DETAIL_TABLE_CSS
                + '</style></head><body><div class="hd">'
                + '<span class="hd-title" id="aiDetailTitle">' + safeTitle + '</span>'
                + '<button type="button" class="hd-copy" id="aiDetailCopyBtn">표복사하기</button></div>'
                + '<div class="bd"><div class="card">' + (detailHtml || "") + '</div></div>'
                + '<script>' + buildDetailCopyScript() + '<\/script>'
                + '</body></html>'
            );
            win.document.close();
            win.focus();
            return;
        }
        openDetailModal(rawTitle, detailHtml);
    }

    function openDetailModal(title, bodyHtml) {
        var safeTitle = htmlEscape(title || "사업 상세");
        $(".ai-detail-modal").remove();
        $("body").append(
            '<div class="ai-detail-modal"><div class="ai-detail-dim"></div>'
            + '<div class="ai-detail-panel"><div class="ai-detail-hd">'
            + '<span class="ai-detail-title" id="aiDetailTitle">' + safeTitle + '</span>'
            + '<button type="button" class="ai-tool-btn ai-detail-copy-btn">표복사하기</button>'
            + '<button type="button" class="ai-detail-close" aria-label="닫기">&times;</button></div>'
            + '<div class="ai-detail-bd"><div class="ai-detail-card">' + (bodyHtml || "") + '</div></div>'
            + '</div></div>'
        );
    }

    function appendBotAnswer($loadingEl, data) {
        var answer = (data && data.answer) ? data.answer : "응답이 없습니다.";
        var tableHtml = "";
        if (data && data.columns && data.dataList) {
            tableHtml = buildResultTable(data.columns, data.dataList);
        }
        var sourcesOnly = !!(data && (data.generalSourcesOnly || data.answerHighlight));
        var generalHtml = "";
        if (data && data.generalItems) {
            generalHtml = buildGeneralItemsHtml(data.generalItems, sourcesOnly);
        }
        var bubbleClass = "ai-bubble ai-pre" + ((tableHtml || generalHtml || sourcesOnly) ? " ai-bubble-wide" : "");
        var hintRowHtml = "";
        if (tableHtml) {
            var hasHint = answer.indexOf(CLICK_HINT) >= 0;
            if (hasHint) {
                answer = answer.split(CLICK_HINT).join("").replace(/\n{2,}/g, "\n").replace(/\n+$/, "");
            }
            hintRowHtml = '<div class="ai-table-hint-row">'
                + '<span class="ai-table-hint">'
                + htmlEscape(hasHint ? CLICK_HINT : "결과 표 목록을 복사할 수 있습니다.")
                + '</span>'
                + '<button type="button" class="ai-tool-btn ai-copy-result-table-btn" title="결과 표만 복사">표복사하기</button>'
                + '</div>';
        }
        var answerHtml = nl2br(answer);
        if (data && data.answerHighlight) {
            answerHtml = '<div class="ai-summary-text">' + answerHtml + '</div>';
        }
        var inner = '<div class="' + bubbleClass + '">' + answerHtml;
        if (hintRowHtml) {
            inner += hintRowHtml;
        }
        if (generalHtml) {
            inner += generalHtml;
        }
        if (tableHtml) {
            inner += tableHtml;
            if (typeof data.rowCount !== "undefined") {
                inner += '<div style="font-size:11px;color:#888;margin-top:4px;">총 ' + data.rowCount + '건 (표시 행수 제한 적용)</div>';
            }
        } else if (generalHtml && typeof data.rowCount !== "undefined") {
            inner += '<div style="font-size:11px;color:#888;margin-top:4px;">총 ' + data.rowCount + '건</div>';
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

    function applyYearDefaults() {
        var y = meta.latestFisYear || String(new Date().getFullYear());
        if ($("#aiFisYearFrom").length) {
            $("#aiFisYearFrom").val(y);
            $("#aiFisYearTo").val(y);
        }
    }

    function loadMeta() {
        if (!$("#aiFisYearFrom").length) {
            return;
        }
        try {
            $.csAjaxCall({
                url: "/ai/ajaxAiChatMeta.do",
                data: { _: String(new Date().getTime()) },
                async: true,
                callBack: function (rtnData) {
                    if (rtnData && rtnData.data) {
                        if (rtnData.data.latestFisYear) {
                            meta.latestFisYear = String(rtnData.data.latestFisYear);
                        }
                        if (rtnData.data.minFisYear) {
                            meta.minFisYear = parseInt(rtnData.data.minFisYear, 10) || 2013;
                        }
                        meta.manualCanManage = !!rtnData.data.manualCanManage;
                        meta.manualFiles = rtnData.data.manualFiles || [];
                        applyManualUi();
                    }
                    applyYearDefaults();
                },
                error: function () { applyYearDefaults(); applyManualUi(); }
            });
        } catch (e) {
            applyYearDefaults();
            applyManualUi();
        }
    }

    function applyManualUi() {
        var can = meta.manualCanManage !== false;
        if ($("#aiManualManageBox").length) {
            if (can) {
                $("#aiManualManageBox").show();
                $("#aiManualHint").text(meta.manualAdminOnly
                    ? "(관리권한자만 업로드·삭제)"
                    : "(테스트: 업로드 허용)");
            } else {
                $("#aiManualManageBox").hide();
                $("#aiManualHint").text("(매뉴얼 업로드는 관리권한자만 가능)");
            }
        }
        renderManualFileList(meta.manualFiles || []);
    }

    function renderManualFileList(files) {
        var $el = $("#aiManualFileList");
        if (!$el.length) {
            return;
        }
        if (!files || !files.length) {
            $el.text("업로드된 매뉴얼 없음").attr("title", "");
            return;
        }
        var names = [];
        for (var i = 0; i < files.length; i++) {
            names.push(files[i].name || files[i].id);
        }
        $el.text("파일 " + files.length + "개 (목록 클릭)").attr("title", names.join("\n"));
    }

    function formatFileSize(bytes) {
        var n = parseInt(bytes, 10) || 0;
        if (n < 1024) {
            return n + " B";
        }
        if (n < 1024 * 1024) {
            return (n / 1024).toFixed(1) + " KB";
        }
        return (n / (1024 * 1024)).toFixed(1) + " MB";
    }

    function openManualListModal(files) {
        $(".ai-manual-modal").remove();
        var list = files || meta.manualFiles || [];
        var can = meta.manualCanManage !== false;
        var rows = "";
        if (!list.length) {
            rows = '<div class="ai-manual-empty">업로드된 매뉴얼이 없습니다.</div>';
        } else {
            rows = '<ul class="ai-manual-ul">';
            for (var i = 0; i < list.length; i++) {
                var f = list[i] || {};
                var id = f.id ? String(f.id) : "";
                var name = f.name ? String(f.name) : id;
                var size = formatFileSize(f.size);
                var date = f.uploadedAt ? String(f.uploadedAt) : (f.date ? String(f.date) : "");
                rows += '<li class="ai-manual-li" data-id="' + htmlEscape(id) + '">'
                    + '<div class="ai-manual-li-main">'
                    + '<div class="ai-manual-li-name" title="' + htmlEscape(name) + '">' + htmlEscape(name) + '</div>'
                    + '<div class="ai-manual-li-meta">' + htmlEscape(size)
                    + (date ? (" · " + htmlEscape(date)) : "") + '</div>'
                    + '</div>';
                if (can && id) {
                    rows += '<button type="button" class="ai-tool-btn ai-manual-del-btn" data-id="'
                        + htmlEscape(id) + '" data-name="' + htmlEscape(name) + '">삭제</button>';
                }
                rows += '</li>';
            }
            rows += '</ul>';
        }
        $("body").append(
            '<div class="ai-manual-modal"><div class="ai-manual-dim"></div>'
            + '<div class="ai-manual-panel">'
            + '<div class="ai-manual-hd"><span>매뉴얼 파일 목록</span>'
            + '<button type="button" class="ai-manual-close" aria-label="닫기">&times;</button></div>'
            + '<div class="ai-manual-bd">' + rows + '</div>'
            + '<div class="ai-manual-ft">'
            + (can ? '<span class="ai-manual-ft-hint">삭제는 관리권한자만 가능합니다.</span>' : '<span class="ai-manual-ft-hint">조회만 가능합니다.</span>')
            + '<button type="button" class="ai-tool-btn ai-manual-close">닫기</button>'
            + '</div></div></div>'
        );
    }

    function refreshManualList(openModal) {
        $.csAjaxCall({
            url: "/ai/ajaxAiManualList.do",
            data: { _: String(new Date().getTime()) },
            async: true,
            callBack: function (rtnData) {
                if (rtnData && rtnData.data) {
                    meta.manualCanManage = !!rtnData.data.canManage;
                    meta.manualFiles = rtnData.data.files || [];
                    applyManualUi();
                    if (openModal) {
                        openManualListModal(meta.manualFiles);
                    }
                } else if (openModal) {
                    openManualListModal([]);
                }
            },
            error: function () {
                if (openModal) {
                    alert("매뉴얼 목록을 불러오지 못했습니다.");
                }
            }
        });
    }

    function deleteManualFile(id, name) {
        if (!meta.manualCanManage) {
            alert("매뉴얼 삭제 권한이 없습니다.");
            return;
        }
        if (!id) {
            return;
        }
        if (!confirm("매뉴얼을 삭제할까요?\n" + (name || id))) {
            return;
        }
        $.csAjaxCall({
            url: "/ai/ajaxAiManualDelete.do",
            data: { id: id },
            async: true,
            callBack: function (rtnData) {
                var code = rtnData ? (rtnData.bcjisRtnCode || rtnData[BCJIS_RETURN_CODE]) : "";
                if (!rtnData || code != "SUCC") {
                    alert((rtnData && rtnData.bcjisMessage) ? rtnData.bcjisMessage : "삭제에 실패했습니다.");
                    return;
                }
                alert((rtnData.bcjisMessage) ? rtnData.bcjisMessage : "삭제되었습니다.");
                refreshManualList(true);
            },
            error: function () {
                alert("매뉴얼 삭제 중 오류가 발생했습니다.");
            }
        });
    }

    function uploadManual() {
        if (!meta.manualCanManage) {
            alert("예산운용지침 업로드 권한이 없습니다.");
            return;
        }
        var fileInput = document.getElementById("aiManualFile");
        if (!fileInput || !fileInput.value) {
            alert("PDF 파일을 선택해 주세요. (여러 개 선택 가능)");
            return;
        }
        // 선택 파일 확장자 사전 검사 (가능하면)
        try {
            if (fileInput.files && fileInput.files.length) {
                for (var i = 0; i < fileInput.files.length; i++) {
                    var nm = fileInput.files[i].name || "";
                    if (!/\.pdf$/i.test(nm)) {
                        alert("PDF만 업로드할 수 있습니다: " + nm);
                        return;
                    }
                }
            }
        } catch (ignore) { /* IE 등 */ }

        $("#aiManualHint").text("업로드 중... (텍스트 추출 포함, 대용량은 수분 소요)");
        $("#aiManualUploadBtn").prop("disabled", true);

        // 시스템 공통 iframe 업로드 (사업설명서 등과 동일) — FormData/$.ajax 는 필터·세션 환경에서 실패 사례 있음
        if (typeof $.bcjisFileAjaxCall === "function" && typeof $.ajaxFileUpload === "function") {
            $.bcjisFileAjaxCall({
                url: "/ai/ajaxAiManualUpload.do",
                fileElementId: "aiManualFile",
                dataType: "json",
                data: {},
                timeout: 600000,
                async: true,
                callBack: function (data) {
                    $("#aiManualUploadBtn").prop("disabled", false);
                    try { $("#aiManualFile").val(""); } catch (ignore2) {}
                    var code = data ? (data.bcjisRtnCode || data[BCJIS_RETURN_CODE]) : "";
                    if (!data || code != "SUCC") {
                        alert((data && data.bcjisMessage) ? data.bcjisMessage : "업로드에 실패했습니다.");
                        applyManualUi();
                        return;
                    }
                    alert((data.bcjisMessage) ? data.bcjisMessage : "업로드되었습니다.");
                    refreshManualList(false);
                },
                error: function (xhr, st, err) {
                    $("#aiManualUploadBtn").prop("disabled", false);
                    alert("예산운용지침 업로드 중 오류가 발생했습니다.\n" + (err || st || ""));
                    applyManualUi();
                }
            });
            return;
        }

        // 폴백: FormData (ajaxfileupload 미로드 시)
        if (typeof FormData === "undefined") {
            $("#aiManualUploadBtn").prop("disabled", false);
            alert("이 브라우저는 파일 업로드를 지원하지 않습니다. Chrome/Edge를 사용해 주세요.");
            return;
        }
        var fd = new FormData();
        var count = 0;
        for (var j = 0; j < fileInput.files.length; j++) {
            fd.append("file", fileInput.files[j]);
            count++;
        }
        var base = (typeof ctx !== "undefined" && ctx) ? ctx : "";
        $.ajax({
            url: base + "/ai/ajaxAiManualUpload.do",
            type: "POST",
            data: fd,
            processData: false,
            contentType: false,
            dataType: "json",
            timeout: 600000,
            xhrFields: { withCredentials: true },
            success: function (data) {
                try { $("#aiManualFile").val(""); } catch (ignore3) {}
                var code2 = data ? (data.bcjisRtnCode || data[BCJIS_RETURN_CODE]) : "";
                if (!data || code2 != "SUCC") {
                    alert((data && data.bcjisMessage) ? data.bcjisMessage : "업로드에 실패했습니다.");
                    applyManualUi();
                    return;
                }
                alert((data.bcjisMessage) ? data.bcjisMessage : "업로드되었습니다.");
                refreshManualList(false);
            },
            error: function (xhr, st, err) {
                var detail = "";
                try {
                    if (xhr && xhr.responseText) {
                        detail = String(xhr.responseText).substring(0, 200);
                    }
                } catch (e2) { /* ignore */ }
                alert("예산운용지침 업로드 중 오류가 발생했습니다.\n"
                    + (err || st || "")
                    + (detail ? ("\n" + detail) : ""));
                applyManualUi();
            },
            complete: function () {
                $("#aiManualUploadBtn").prop("disabled", false);
            }
        });
    }

    function isChecked(id) {
        return $("#" + id).is(":checked");
    }

    function clearInternalChecks() {
        $("#aiSearchBizNm,#aiSearchGubun,#aiSearchExam,#aiSearchSrchVal").prop("checked", false);
    }

    function clearGeneralChecks() {
        $("#aiSearchLaw,#aiSearchCity,#aiSearchManual").prop("checked", false);
    }

    function onInternalCheckChange() {
        if ($(this).is(":checked")) {
            clearGeneralChecks();
        }
    }

    function onGeneralCheckChange() {
        if ($(this).is(":checked")) {
            clearInternalChecks();
            // 일반검색은 단일 선택
            $(".ai-general-chk").not(this).prop("checked", false);
        }
    }

    function validateYears(fromY, toY) {
        var minY = meta.minFisYear || 2013;
        var maxY = parseInt(meta.latestFisYear, 10) || new Date().getFullYear();
        if (!/^\d{4}$/.test(fromY) || !/^\d{4}$/.test(toY)) {
            return "회계년도는 4자리 숫자로 입력해 주세요.";
        }
        var fromN = parseInt(fromY, 10);
        var toN = parseInt(toY, 10);
        if (fromN < minY || toN < minY) {
            return "회계년도는 " + minY + "년 이상이어야 합니다.";
        }
        if (fromN > maxY || toN > maxY) {
            return "회계년도는 최근 회계년도(" + maxY + ") 이하여야 합니다.";
        }
        if (fromN > toN) {
            return "시작 회계년도가 종료 회계년도보다 클 수 없습니다.";
        }
        return "";
    }

    /** 상단 회계년도(시작=종료, 1년) 내부자료를 JSON 파일로 저장 */
    function exportInternalJson() {
        var fromY = $.trim($("#aiFisYearFrom").val() || "");
        var toY = $.trim($("#aiFisYearTo").val() || "");
        var yearErr = validateYears(fromY, toY);
        if (yearErr) {
            alert(yearErr);
            return;
        }
        if (fromY !== toY) {
            alert("JSON 내보내기는 회계년도를 1년 단위로 맞춰 주세요.\n(시작 연도와 종료 연도를 같게 입력)");
            return;
        }
        var $btn = $("#aiChatExportBtn");
        if ($btn.length && $btn.prop("disabled")) {
            return;
        }
        if (!window.confirm(fromY + "년 내부자료(심사조서)를 JSON으로 내보내시겠습니까?\n"
                + "(모바일 뷰어용 · 데이터량에 따라 수십 초 걸릴 수 있습니다)")) {
            return;
        }
        $btn.prop("disabled", true).text("내보내는중…");
        var $loadingEl = appendBotLoading();
        $loadingEl.find(".ai-bubble").text(fromY + "년 내부자료 JSON을 생성하는 중…");
        $.csAjaxCall({
            url: "/ai/ajaxAiInternalExport.do",
            data: { fisYear: fromY },
            async: true,
            timeout: 300000,
            callBack: function (rtnData) {
                $btn.prop("disabled", false).text("JSON내보내기");
                if (!rtnData) {
                    appendBotError($loadingEl, "서버 응답을 받지 못했습니다.");
                    return;
                }
                var msg = rtnData.message || "";
                var data = rtnData.data || null;
                if (!data || data.ok === false) {
                    appendBotError($loadingEl, msg || (data && data.error) || "내보내기에 실패했습니다.");
                    return;
                }
                try {
                    downloadJsonFile(data, "bcjis-ai-internal-" + fromY + ".json");
                    var note = "○ " + fromY + "년 내부자료 JSON 내보내기 완료\n"
                        + "○ 사업 " + (data.bizCount || 0) + "개 / 행 " + (data.rowCount || 0) + "건\n"
                        + "○ 파일: bcjis-ai-internal-" + fromY + ".json";
                    if (data.truncated) {
                        note += "\n○ 참고: 상한으로 일부만 포함됨(AiInternalExportMaxRows/MaxBiz)";
                    }
                    appendBotAnswer($loadingEl, { answer: note, aiProvider: "internal-export" });
                } catch (e) {
                    appendBotError($loadingEl, "파일 저장 실패: " + (e && e.message ? e.message : e));
                }
            },
            error: function () {
                $btn.prop("disabled", false).text("JSON내보내기");
                appendBotError($loadingEl, "내보내기 요청 중 오류가 발생했습니다.");
            }
        });
    }

    function downloadJsonFile(obj, filename) {
        var text = typeof obj === "string" ? obj : JSON.stringify(obj, null, 2);
        var blob;
        try {
            blob = new Blob([text], { type: "application/json;charset=utf-8" });
        } catch (e1) {
            if (window.navigator && window.navigator.msSaveOrOpenBlob) {
                window.navigator.msSaveOrOpenBlob(
                    new Blob([text], { type: "application/json;charset=utf-8" }),
                    filename
                );
                return;
            }
            throw e1;
        }
        if (window.navigator && window.navigator.msSaveOrOpenBlob) {
            window.navigator.msSaveOrOpenBlob(blob, filename);
            return;
        }
        var url = (window.URL || window.webkitURL).createObjectURL(blob);
        var a = document.createElement("a");
        a.href = url;
        a.download = filename;
        a.style.display = "none";
        document.body.appendChild(a);
        a.click();
        setTimeout(function () {
            document.body.removeChild(a);
            try { (window.URL || window.webkitURL).revokeObjectURL(url); } catch (e2) { /* */ }
        }, 200);
    }

    function clearChat() {
        $("#aiChatInput").val("");
        $("#aiChatMessages").html(GUIDE_HTML);
        clearInternalChecks();
        clearGeneralChecks();
        applyYearDefaults();
        scrollToBottom();
    }

    function closeChatWindow() {
        persistAiPopupPos();
        try {
            window.close();
        } catch (e) { /* ignore */ }
        // 팝업 차단/정책으로 close 실패 시 안내
        setTimeout(function () {
            if (!window.closed) {
                appendBotError(
                    $('<div class="ai-msg ai-bot"></div>').appendTo("#aiChatMessages"),
                    "브라우저 정책으로 창을 자동 닫을 수 없습니다. 창의 × 버튼으로 닫아 주세요."
                );
            }
        }, 200);
    }

    function normalizeAiPopupPos(pos) {
        if (!pos || typeof pos !== "object") {
            return null;
        }
        var left = parseInt(pos.left, 10);
        var top = parseInt(pos.top, 10);
        var width = parseInt(pos.width, 10);
        var height = parseInt(pos.height, 10);
        if (isNaN(left) || isNaN(top)) {
            return null;
        }
        if (isNaN(width) || width < 640) {
            width = AI_POPUP_DEFAULT_W;
        }
        if (isNaN(height) || height < 480) {
            height = AI_POPUP_DEFAULT_H;
        }
        return { left: left, top: top, width: width, height: height };
    }

    function loadAiPopupPos() {
        try {
            var raw = localStorage.getItem(AI_POPUP_POS_KEY);
            if (!raw) {
                return null;
            }
            return normalizeAiPopupPos(JSON.parse(raw));
        } catch (e) {
            return null;
        }
    }

    function saveAiPopupPos(pos) {
        var normalized = normalizeAiPopupPos(pos);
        if (!normalized) {
            return;
        }
        try {
            localStorage.setItem(AI_POPUP_POS_KEY, JSON.stringify(normalized));
        } catch (e) { /* ignore */ }
        try {
            window.__aiChatPopupPos = normalized;
        } catch (e2) { /* ignore */ }
    }

    function persistAiPopupPos() {
        try {
            var left = (window.screenX != null) ? window.screenX : window.screenLeft;
            var top = (window.screenY != null) ? window.screenY : window.screenTop;
            var width = window.outerWidth || document.documentElement.clientWidth || AI_POPUP_DEFAULT_W;
            var height = window.outerHeight || document.documentElement.clientHeight || AI_POPUP_DEFAULT_H;
            if (typeof left !== "number" || typeof top !== "number") {
                return;
            }
            saveAiPopupPos({ left: left, top: top, width: width, height: height });
            if (window.opener && !window.opener.closed) {
                try { window.opener.__aiChatPopupPos = { left: left, top: top, width: width, height: height }; } catch (e) { /* ignore */ }
            }
        } catch (e2) { /* ignore */ }
    }

    function restoreAiPopupPosSelf() {
        try {
            var pos = loadAiPopupPos();
            if (!pos) {
                return;
            }
            var curLeft = (window.screenX != null) ? window.screenX : window.screenLeft;
            var curTop = (window.screenY != null) ? window.screenY : window.screenTop;
            if (typeof curLeft !== "number" || typeof curTop !== "number") {
                return;
            }
            if (Math.abs(curLeft - pos.left) > 15 || Math.abs(curTop - pos.top) > 15) {
                window.moveTo(pos.left, pos.top);
            }
            if (pos.width && pos.height
                    && (Math.abs((window.outerWidth || 0) - pos.width) > 20
                        || Math.abs((window.outerHeight || 0) - pos.height) > 20)) {
                try { window.resizeTo(pos.width, pos.height); } catch (e) { /* ignore */ }
            }
        } catch (e2) { /* ignore */ }
    }

    function focusExistingAiPopup(win) {
        if (!win) {
            return false;
        }
        try {
            if (win.closed) {
                return false;
            }
        } catch (e0) {
            return false;
        }
        // 기존 창: 위치·크기·내용을 건드리지 않고 앞으로만 가져온다.
        // (moveTo/resizeTo/location 재지정 금지 — 첫 열림 좌표로 되돌아가거나 대화가 초기화됨)
        try { win.focus(); } catch (e) { /* ignore */ }
        try {
            if (win.document && typeof win.document.hasFocus === "function" && !win.document.hasFocus()) {
                win.focus();
            }
        } catch (e2) { /* ignore */ }
        return true;
    }

    /** 기존 창 여부 — document 접근이 막혀도 closed 만으로 판정 (재로드 방지) */
    function getOpenAiPopupWin() {
        var candidates = [aiPopupWin, window.__aiChatPopupWin];
        for (var i = 0; i < candidates.length; i++) {
            var w = candidates[i];
            if (!w) {
                continue;
            }
            try {
                if (!w.closed) {
                    aiPopupWin = w;
                    try { window.__aiChatPopupWin = w; } catch (e1) { /* ignore */ }
                    return w;
                }
            } catch (e2) { /* ignore */ }
        }
        return null;
    }

    function registerAiPopupToOpener() {
        try {
            if (window.opener && !window.opener.closed) {
                window.opener.__aiChatPopupWin = window;
                if (typeof window.opener.__aiChatPopupOpened === "function") {
                    try { window.opener.__aiChatPopupOpened(window); } catch (e0) { /* ignore */ }
                }
            }
        } catch (e1) { /* ignore */ }
    }

    function buildAiPopupFeatures() {
        var pos = null;
        try { pos = window.__aiChatPopupPos || loadAiPopupPos(); } catch (e) { pos = loadAiPopupPos(); }
        var width = (pos && pos.width) ? pos.width : AI_POPUP_DEFAULT_W;
        var height = (pos && pos.height) ? pos.height : AI_POPUP_DEFAULT_H;
        var features = "width=" + width + ",height=" + height
            + ",scrollbars=yes,resizable=yes,menubar=no,toolbar=no,location=no,status=no";
        if (pos && typeof pos.left === "number" && typeof pos.top === "number") {
            features += ",left=" + pos.left + ",top=" + pos.top;
        }
        return features;
    }

    function closeOwnedAiPopup() {
        var w = getOpenAiPopupWin();
        if (w) {
            try { w.close(); } catch (e) { /* ignore */ }
        }
        aiPopupWin = null;
        try { window.__aiChatPopupWin = null; } catch (e2) { /* ignore */ }
    }

    function bindMainWindowCloseAiPopup() {
        $(window).off("beforeunload.aichatpopup unload.aichatpopup pagehide.aichatpopup");
        $(window).on("beforeunload.aichatpopup unload.aichatpopup pagehide.aichatpopup", function () {
            closeOwnedAiPopup();
        });
        // 심사정보시스템 로그아웃 시에도 함께 종료
        $(document).off("click.aichatpopuplogout");
        $(document).on("click.aichatpopuplogout", "#mainNorthLogoutBtn, a[href*='logout'], a[href*='Logout']", function () {
            closeOwnedAiPopup();
        });
    }

    function watchOpenerAndCloseSelf() {
        if (aiOpenerWatchTimer) {
            try { clearInterval(aiOpenerWatchTimer); } catch (e) { /* ignore */ }
        }
        registerAiPopupToOpener();
        aiOpenerWatchTimer = setInterval(function () {
            registerAiPopupToOpener();
            try {
                if (!window.opener || window.opener.closed) {
                    persistAiPopupPos();
                    try { window.close(); } catch (e2) { /* ignore */ }
                }
            } catch (e3) {
                try { window.close(); } catch (e4) { /* ignore */ }
            }
        }, 800);
    }

    function send() {
        var $input = $("#aiChatInput");
        var $sendBtn = $("#aiChatSendBtn");
        var question = $.trim($input.val());
        if (question === "") {
            return;
        }
        var fromY = $.trim($("#aiFisYearFrom").val());
        var toY = $.trim($("#aiFisYearTo").val());
        var searchBizNm = isChecked("aiSearchBizNm");
        var searchGubun = isChecked("aiSearchGubun");
        var searchExam = isChecked("aiSearchExam");
        var searchSrchVal = isChecked("aiSearchSrchVal");
        var searchLaw = isChecked("aiSearchLaw");
        var searchCity = isChecked("aiSearchCity");
        var searchManual = isChecked("aiSearchManual");
        var anyInternal = searchBizNm || searchGubun || searchExam || searchSrchVal;
        var anyGeneral = searchLaw || searchCity || searchManual;

        if (anyInternal && anyGeneral) {
            appendUser(question);
            $input.val("");
            appendBotError(appendBotLoading(), "내부자료 검색과 일반자료 검색은 동시에 선택할 수 없습니다.");
            return;
        }
        if ((searchLaw ? 1 : 0) + (searchCity ? 1 : 0) + (searchManual ? 1 : 0) > 1) {
            appendUser(question);
            $input.val("");
            appendBotError(appendBotLoading(), "일반자료 검색은 법령·조례 / 보도자료,고시공고 / 예산운용지침 중 하나만 선택해 주세요.");
            return;
        }

        // 내부검색만 회계년도 필수 검증
        if (anyInternal) {
            var yearErr = validateYears(fromY, toY);
            if (yearErr) {
                appendUser(question);
                $input.val("");
                appendBotError(appendBotLoading(), yearErr);
                return;
            }
        }

        var userLabel = question;
        if (anyInternal) {
            var tags = [];
            if (searchBizNm) { tags.push("사업명"); }
            if (searchGubun) { tags.push("구분"); }
            if (searchExam) { tags.push("검토내용"); }
            if (searchSrchVal) { tags.push("조건검색어"); }
            userLabel = "[" + fromY + "~" + toY + "][" + tags.join(",") + "] " + question;
        } else if (anyGeneral) {
            var gtag = searchLaw ? "법령·조례" : (searchCity ? "보도자료,고시공고" : "예산운용지침");
            userLabel = "[일반자료:" + gtag + "] " + question;
        } else {
            userLabel = "[외부자료] " + question;
        }
        appendUser(userLabel);
        $input.val("");
        $input.prop("disabled", true);
        $sendBtn.prop("disabled", true);
        var $loadingEl = appendBotLoading();
        $.csAjaxCall({
            url: "/ai/ajaxAiChat.do",
            data: {
                question: question,
                fisYearFrom: fromY,
                fisYearTo: toY,
                searchBizNm: searchBizNm ? "Y" : "N",
                searchGubun: searchGubun ? "Y" : "N",
                searchExam: searchExam ? "Y" : "N",
                searchSrchVal: searchSrchVal ? "Y" : "N",
                searchLaw: searchLaw ? "Y" : "N",
                searchCity: searchCity ? "Y" : "N",
                searchManual: searchManual ? "Y" : "N"
            },
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

    /**
     * 이름만으로 기존 팝업을 찾는다 (URL 미지정).
     * - 이미 aiChatPopup 이 떠 있으면 참조만 돌려받고 내용은 유지된다.
     * - 없으면 about:blank 가 생기므로 호출측에서 닫고 URL로 새로 연다.
     * ※ window.open(실제URL, name) 은 Chrome 등에서 기존 창을 재로드해 대화가 사라지므로 쓰지 않는다.
     */
    function probeNamedAiPopup() {
        var probe = null;
        try {
            probe = window.open("", AI_POPUP_NAME);
        } catch (e) {
            return null;
        }
        if (!probe) {
            return null;
        }
        try {
            if (probe.closed) {
                return null;
            }
        } catch (e1) {
            return null;
        }
        var href = "";
        try {
            href = String(probe.location && probe.location.href ? probe.location.href : "");
        } catch (e2) {
            // cross-origin 등으로 href 를 못 읽으면 기존 창으로 간주하고 focus 만
            return probe;
        }
        if (href && href.indexOf("aiChatPopup") >= 0) {
            return probe;
        }
        // 방금 연 빈 창 → 닫고 null (아래에서 URL로 정식 open)
        try { probe.close(); } catch (e3) { /* ignore */ }
        return null;
    }

    /**
     * AI 예산도우미 열기.
     * - 이미 열린 창이 있으면 URL을 다시 넣지 않고 focus 만 한다 (대화·현재 위치 유지).
     * - 참조가 끊겨도 같은 이름 창이 살아 있으면 probe 로 찾아 focus 한다.
     */
    function openAiPopup() {
        var existing = getOpenAiPopupWin();
        if (!existing) {
            existing = probeNamedAiPopup();
            if (existing) {
                aiPopupWin = existing;
                try { window.__aiChatPopupWin = existing; } catch (e0) { /* ignore */ }
            }
        }
        if (existing) {
            focusExistingAiPopup(existing);
            return false;
        }

        var base = (typeof ctx !== "undefined" && ctx) ? ctx : "";
        var url = base + "/ai/aiChatPopup.do";
        var win = null;
        try {
            // 최초 1회만 URL+features 로 연다. 이후 재클릭은 위 existing 분기.
            win = window.open(url, AI_POPUP_NAME, buildAiPopupFeatures());
        } catch (eOpen) {
            win = null;
        }
        if (!win) {
            alert("팝업이 차단되었습니다. 브라우저에서 팝업을 허용해 주세요.");
            return false;
        }
        aiPopupWin = win;
        try { window.__aiChatPopupWin = win; } catch (e1) { /* ignore */ }
        try { win.focus(); } catch (e2) { /* ignore */ }
        return false;
    }

    // 런처(FAB 복제 버튼)에서도 동일 함수를 호출할 수 있도록 노출
    window.openAiBudgetHelper = openAiPopup;
    window.__aiChatPopupOpened = function (win) {
        if (win) {
            aiPopupWin = win;
            try { window.__aiChatPopupWin = win; } catch (e) { /* ignore */ }
        }
    };

    function bindPopup() {
        $(document).off(".aichat");
        $(document).on("input.aichat change.aichat", "#aiFisYearFrom", function () {
            var v = $.trim($(this).val());
            if (/^\d{4}$/.test(v)) {
                $("#aiFisYearTo").val(v);
            }
        });
        $(document).on("change.aichat", ".ai-internal-chk", onInternalCheckChange);
        $(document).on("change.aichat", ".ai-general-chk", onGeneralCheckChange);
        $(document).on("click.aichat", "#aiChatSendBtn", function () { send(); });
        $(document).on("keydown.aichat", "#aiChatInput", function (e) {
            if (e.keyCode === 13 && !e.shiftKey) {
                e.preventDefault();
                send();
            }
        });
        $(document).on("click.aichat", "#aiChatClearBtn", function () { clearChat(); });
        $(document).on("click.aichat", "#aiChatExportBtn", function () { exportInternalJson(); });
        $(document).on("click.aichat", "#aiChatCloseBtn", function () { closeChatWindow(); });
        $(document).on("click.aichat", "#aiManualUploadBtn", function () { uploadManual(); });
        $(document).on("click.aichat", "#aiManualRefreshBtn, #aiManualFileList", function () {
            refreshManualList(true);
        });
        $(document).on("click.aichat", ".ai-manual-close, .ai-manual-dim", function () {
            $(".ai-manual-modal").remove();
        });
        $(document).on("click.aichat", ".ai-manual-del-btn", function (e) {
            e.preventDefault();
            e.stopPropagation();
            deleteManualFile($(this).attr("data-id"), $(this).attr("data-name"));
        });
        $(document).on("click.aichat", ".ai-copy-result-table-btn", function (e) {
            e.preventDefault();
            e.stopPropagation();
            copyResultTableFromBtn($(this));
        });
        $(document).on("click.aichat", ".ai-detail-copy-btn", function (e) {
            e.preventDefault();
            e.stopPropagation();
            var $modal = $(this).closest(".ai-detail-modal");
            var title = $.trim($modal.find("#aiDetailTitle").text() || "사업 상세");
            var table = $modal.find(".ai-biz-detail-table").get(0);
            copyDetailTable(title, table);
        });
        $(document).on("click.aichat", ".ai-result-table tbody tr.ai-row-clickable", function () {
            var $tr = $(this);
            var detail = resolveDetailHtml($tr);
            var title = "";
            try {
                title = decodeURIComponent($tr.attr("data-title") || "");
            } catch (e) {
                title = $tr.attr("data-title") || "";
            }
            openDetailWindow(title, detail);
        });
        $(document).on("click.aichat", ".ai-detail-close, .ai-detail-dim", function () {
            $(".ai-detail-modal").remove();
        });
        window.aiChatClear = clearChat;

        // 위치 기억 + opener 등록 + 심사정보시스템 종료 시 함께 닫힘
        registerAiPopupToOpener();
        restoreAiPopupPosSelf();
        setTimeout(restoreAiPopupPosSelf, 50);
        setTimeout(restoreAiPopupPosSelf, 200);
        $(window).off("resize.aichatpos beforeunload.aichatpos unload.aichatpos pagehide.aichatpos");
        $(window).on("resize.aichatpos", persistAiPopupPos);
        $(window).on("beforeunload.aichatpos unload.aichatpos pagehide.aichatpos", persistAiPopupPos);
        setInterval(persistAiPopupPos, 800);
        watchOpenerAndCloseSelf();
    }

    function bindLauncher() {
        $(document).off("click.aichatopen");
        $(document).on("click.aichatopen", "#aiChatOpenBtn, #aiChatOpenBtnSlot, a.ai-chat-fab", function (e) {
            e.preventDefault();
            e.stopPropagation();
            openAiPopup();
            return false;
        });
        bindMainWindowCloseAiPopup();
    }

    function init() {
        if ($("#aiChatPopupRoot").length > 0) {
            applyYearDefaults();
            bindPopup();
            loadMeta();
            return;
        }
        // FAB가 나중에 복제되어도 동작하도록 항상 런처 바인딩
        bindLauncher();
    }

    if (window.jQuery) {
        $(init);
    } else {
        document.addEventListener("DOMContentLoaded", init);
    }
})();
