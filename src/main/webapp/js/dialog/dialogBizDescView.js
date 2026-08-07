/**
 * 사업명 클릭 → 사업설명서 매칭 결과를 별도 브라우저 창(window.open)으로 표시
 * - 부모(조서): 팝업 열기/닫기 + 사업명 하이라이트만
 * - 팝업: 후보 선택·요약 렌더·복사·매칭해제
 */
$(document).ready(function () {
    var isPopup = (window.BIZDESC_VIEW_MODE === "popup")
        || ($("#bizDescViewPopupFlag").length > 0);

    var findNameLinkByTeId = function (teId) {
        if (!teId) { return $(); }
        var $link = $('a.bizdesc-nm-link[data-te-id="' + teId + '"]');
        if ($link.length) { return $link.first(); }
        $link = $('a.bizdesc-nm-link').filter(function () {
            return String($(this).data("teId") || $(this).attr("data-te-id") || "") === String(teId);
        });
        return $link.first();
    };

    var clearMatchHighlight = function () {
        $("a.bizdesc-nm-link.bizdesc-matched").removeClass("bizdesc-matched");
    };

    var highlightMatchedByTeId = function (teId) {
        clearMatchHighlight();
        var $link = findNameLinkByTeId(teId);
        if ($link.length) {
            $link.addClass("bizdesc-matched");
        }
    };

    var readOfficeFromReportFilter = function () {
        var sel = $(".ui-tabs-panel:visible #condOfficeCd").get(0);
        if (!sel) {
            sel = $("#mainPane #condOfficeCd").filter(":visible").get(0) || $("#condOfficeCd").get(0);
        }
        var officeCd = "";
        var officeNm = "";
        if (sel && sel.options && sel.selectedIndex >= 0) {
            officeCd = sel.options[sel.selectedIndex].value;
            officeNm = sel.options[sel.selectedIndex].text;
        } else if (sel) {
            officeCd = sel.value || "";
        }
        officeCd = (officeCd == null ? "" : String(officeCd)).replace(/^\s+|\s+$/g, "");
        officeNm = (officeNm == null ? "" : String(officeNm)).replace(/^\s+|\s+$/g, "");
        if (officeCd === "" || officeCd === "null" || officeCd === "undefined" || officeNm === "전체") {
            var hid = $(".ui-tabs-panel:visible #bizDescOfficeCd").val()
                || $("#bizDescOfficeCd").val()
                || "";
            hid = String(hid).replace(/^\s+|\s+$/g, "");
            if (hid && hid !== "null" && hid !== "undefined") {
                return {
                    officeCd: hid,
                    officeNm: String($(".ui-tabs-panel:visible #bizDescOfficeNm").val()
                        || $("#bizDescOfficeNm").val() || hid).replace(/^\s+|\s+$/g, "")
                };
            }
            return { officeCd: "", officeNm: officeNm || "전체" };
        }
        return { officeCd: officeCd, officeNm: officeNm };
    };

    var BIZDESC_POS_KEY = "bizDescViewPopupPos";

    var normalizePopupPos = function (p) {
        if (!p) { return null; }
        var left = Number(p.left);
        var top = Number(p.top);
        if (isNaN(left) || isNaN(top)) { return null; }
        left = Math.round(left);
        top = Math.round(top);
        var width = Math.round(Number(p.width) > 200 ? Number(p.width) : 900);
        var height = Math.round(Number(p.height) > 200 ? Number(p.height) : 900);
        // 다중 모니터(음수 좌표 등)를 유지한다. 과도한 클램핑으로 원위치를 잃지 않도록 함.
        return { left: left, top: top, width: width, height: height };
    };

    var saveBizDescPopupPos = function (pos) {
        var n = normalizePopupPos(pos);
        if (!n) { return; }
        window.__bizDescPopupPos = n;
        try {
            localStorage.setItem(BIZDESC_POS_KEY, JSON.stringify(n));
        } catch (ignore) {}
    };

    var readBizDescPopupPos = function () {
        if (window.__bizDescPopupPos) {
            var mem = normalizePopupPos(window.__bizDescPopupPos);
            if (mem) { return mem; }
        }
        try {
            var raw = localStorage.getItem(BIZDESC_POS_KEY);
            if (raw) {
                return normalizePopupPos(JSON.parse(raw));
            }
        } catch (ignore) {}
        return null;
    };

    var capturePopupPosFromWin = function (w) {
        if (!w || w.closed) { return; }
        try {
            var left = (w.screenX != null) ? w.screenX : w.screenLeft;
            var top = (w.screenY != null) ? w.screenY : w.screenTop;
            var width = w.outerWidth || ((w.document && w.document.documentElement)
                ? w.document.documentElement.clientWidth : 900);
            var height = w.outerHeight || ((w.document && w.document.documentElement)
                ? w.document.documentElement.clientHeight : 900);
            if (typeof left === "number" && typeof top === "number") {
                saveBizDescPopupPos({ left: left, top: top, width: width, height: height });
            }
        } catch (ignore) {}
    };

    var applyPopupPos = function (win, pos) {
        if (!win || win.closed || !pos) { return; }
        var tryMove = function (n) {
            try {
                if (!win || win.closed) { return; }
                win.moveTo(pos.left, pos.top);
                if (pos.width && pos.height) {
                    try { win.resizeTo(pos.width, pos.height); } catch (ignoreR) {}
                }
            } catch (ignoreM) {}
            if (n < 8) {
                setTimeout(function () { tryMove(n + 1); }, 50 + n * 50);
            }
        };
        tryMove(0);
    };

    var closeBizDescPopup = function () {
        var w = window.__bizDescPopupWin;
        if (w && !w.closed) {
            capturePopupPosFromWin(w);
            try { w.close(); } catch (ignore) {}
        }
        window.__bizDescPopupWin = null;
    };

    var buildPopupUrl = function (params) {
        var q = [];
        var add = function (k, v) {
            if (v == null) { return; }
            q.push(encodeURIComponent(k) + "=" + encodeURIComponent(String(v)));
        };
        add("fisYear", params.fisYear);
        add("bgtDgr", params.bgtDgr);
        add("reportCd", params.reportCd);
        add("teBgtCompoId", params.teBgtCompoId);
        add("dgrcompoId", params.dgrcompoId);
        add("reportBizNm", params.reportBizNm);
        add("officeCd", params.officeCd);
        add("tabId", params.tabId);
        add("gridId", params.gridId);
        return (typeof ctx !== "undefined" ? ctx : "") + "/bizdesc/bizDescViewPopup.do?" + q.join("&");
    };

    // ---------- 부모(조서) 화면: 팝업 런처만 ----------
    if (!isPopup) {
        window.clearBizDescMatchHighlight = clearMatchHighlight;
        window.highlightBizDescMatch = highlightMatchedByTeId;
        window.closeBizDescPopup = closeBizDescPopup;

        window.openDialogBizDescView = function (params) {
            params = params || {};

            var officeCd = params.officeCd == null ? "" : String(params.officeCd).replace(/^\s+|\s+$/g, "");
            var live = readOfficeFromReportFilter();
            if (live.officeCd) {
                officeCd = live.officeCd;
            }
            if (!officeCd || officeCd === "null" || officeCd === "undefined") {
                $.csAlert({
                    msg: "조회조건 '실국'이 '" + (live.officeNm || "전체") + "' 입니다.\n"
                        + "특정 실국을 선택한 뒤 사업명을 클릭해 주세요.\n"
                        + "(해당 실국에 업로드된 사업설명서만 매칭합니다)"
                });
                return;
            }
            params.officeCd = officeCd;
            highlightMatchedByTeId(params.teBgtCompoId);

            var url = buildPopupUrl(params);
            var existing = window.__bizDescPopupWin;
            // 이미 열린 창이 있으면 닫지 않고 URL만 교체 → 사용자가 옮긴 위치 그대로 유지
            if (existing && !existing.closed) {
                try {
                    capturePopupPosFromWin(existing);
                    existing.location.href = url;
                    try { existing.focus(); } catch (ignoreF) {}
                    return;
                } catch (navErr) {
                    // 접근 불가 시 닫고 새로 연다
                    try { existing.close(); } catch (ignoreC) {}
                    window.__bizDescPopupWin = null;
                }
            }

            var pos = readBizDescPopupPos();
            var left = pos ? pos.left : 80;
            var top = pos ? pos.top : 40;
            var width = pos ? pos.width : 900;
            var height = pos ? pos.height : 900;
            var features = "width=" + width + ",height=" + height
                + ",left=" + left + ",top=" + top
                + ",scrollbars=yes,resizable=yes";
            var win = null;
            try {
                // 동일 창 이름으로 재사용 시도(브라우저가 위치를 유지하는 경우 있음)
                win = window.open(url, "bizDescViewPopup", features);
            } catch (e) {
                win = null;
            }
            window.__bizDescPopupWin = win;
            if (!win) {
                clearMatchHighlight();
                $.csAlert({
                    msg: "팝업이 차단되었습니다.\n브라우저에서 이 사이트의 팝업을 허용한 뒤 다시 시도해 주세요."
                });
                return;
            }
            try { win.focus(); } catch (ignore) {}
            if (pos) {
                applyPopupPos(win, pos);
            }
        };

        // 예전 인페이지 다이얼로그 HTML이 남아 있으면 숨김
        $("#dialogBizDescViewDiv").hide();
        $("#bizdescMatchLinkSvg").hide().remove();
        return;
    }

    // ---------- 팝업 창: 매칭/요약 UI ----------
    var dialogObj = $("#dialogBizDescViewDiv");
    if (dialogObj.length === 0) {
        return;
    }

    var fitBodyHeight = function () {
        var $wrap = $(".bizdesc-body-wrap", dialogObj);
        if (!$wrap.length || !$("#dialogBizDescViewSummaryPanel", dialogObj).is(":visible")) {
            return;
        }
        var metaH = $("#dialogBizDescViewMeta", dialogObj).outerHeight(true) || 0;
        var toolH = $(".bizdesc-toolbar", dialogObj).outerHeight(true) || 0;
        var headH = $("#dialogBizDescViewHead", dialogObj).outerHeight(true) || 0;
        var h = Math.max(200, $(window).height() - metaH - toolH - headH - 36);
        $wrap.css("max-height", h + "px");
    };

    var setPageTitle = function (title) {
        var t = title || "사업설명서";
        document.title = t;
        $("#dialogBizDescViewHeadTitle", dialogObj).text(t);
    };

    var notifyOpenerHighlight = function (teId) {
        try {
            if (window.opener && !window.opener.closed && typeof window.opener.highlightBizDescMatch === "function") {
                window.opener.highlightBizDescMatch(teId);
            }
        } catch (ignore) {}
    };

    var notifyOpenerClearHighlight = function () {
        try {
            if (window.opener && !window.opener.closed && typeof window.opener.clearBizDescMatchHighlight === "function") {
                window.opener.clearBizDescMatchHighlight();
            }
        } catch (ignore) {}
    };

    var baseParam = function () {
        return {
            fisYear: $("#dialogBizDescViewFisYear", dialogObj).val(),
            bgtDgr: $("#dialogBizDescViewBgtDgr", dialogObj).val(),
            reportCd: $("#dialogBizDescViewReportCd", dialogObj).val(),
            teBgtCompoId: $("#dialogBizDescViewTeBgtCompoId", dialogObj).val(),
            officeCd: $("#dialogBizDescViewOfficeCd", dialogObj).val()
        };
    };

    var flattenCellText = function (s, keepLines) {
        if (s == null) { return ""; }
        var raw = String(s);
        if (keepLines) {
            return raw.replace(/\r\n/g, "\n").replace(/\r/g, "\n")
                .split("\n")
                .map(function (line) {
                    return line.replace(/[\t]+/g, " ").replace(/ +/g, " ").replace(/^\s+|\s+$/g, "");
                })
                .filter(function (line) { return line.length > 0; })
                .join("\n");
        }
        return raw.replace(/[\r\n\t]+/g, " ").replace(/ +/g, " ").replace(/^\s+|\s+$/g, "");
    };

    var isLabelCell = function (text, colIdx, row, kind, rowIdx) {
        var t = flattenCellText(text);
        if (!t) { return false; }
        if (kind === "meta") {
            return colIdx === 0 || colIdx === 2;
        }
        if (kind === "procedure") {
            return (colIdx % 2) === 0;
        }
        if (kind === "yearly") {
            return colIdx === 0 || rowIdx === 0;
        }
        if (kind === "plan") {
            return rowIdx <= 1 || colIdx === 0;
        }
        return colIdx === 0 && t.length <= 20 && row.length <= 4;
    };

    var renderCellTable = function ($body, b) {
        var kind = b.kind || "";
        var $tbl = $('<table class="bd-table"></table>');
        if (kind === "plan") { $tbl.addClass("bd-plan"); }
        if (kind === "procedure") { $tbl.addClass("bd-procedure"); }
        if (kind === "yearly") { $tbl.addClass("bd-yearly"); }
        if (kind === "meta") { $tbl.addClass("bd-meta"); }
        var cells = b.cells || [];
        var keepLines = (kind === "plan");
        for (var r = 0; r < cells.length; r++) {
            var row = cells[r] || [];
            var $tr = $("<tr></tr>");
            for (var c = 0; c < row.length; c++) {
                var cell = row[c] || {};
                var cellText = flattenCellText(cell.text, keepLines);
                var cs = parseInt(cell.colSpan, 10) || 1;
                var rs = parseInt(cell.rowSpan, 10) || 1;
                var useTh = isLabelCell(cellText, c, row, kind, r);
                var $cell = $(useTh ? "<th></th>" : "<td></td>");
                if (cs > 1) { $cell.attr("colspan", cs); }
                if (rs > 1) { $cell.attr("rowspan", rs); }
                $cell.text(cellText);
                $tr.append($cell);
            }
            $tbl.append($tr);
        }
        $body.append($tbl);
    };

    var renderBlocks = function (blocks) {
        var $body = $("#dialogBizDescViewBody", dialogObj).empty();
        if (!blocks || !blocks.length) {
            $body.append('<div class="bd-para">표시할 내용이 없습니다.</div>');
            return;
        }
        for (var i = 0; i < blocks.length; i++) {
            var b = blocks[i] || {};
            var type = b.type || "para";
            if (type === "heading") {
                $body.append($('<div class="bd-heading"></div>').text(b.text || ""));
            } else if ((type === "table" || type === "meta") && b.cells && b.cells.length) {
                renderCellTable($body, b);
            } else if ((type === "table" || type === "meta") && b.rows && b.rows.length) {
                var kind = b.kind || (type === "meta" ? "meta" : "");
                var $tbl = $('<table class="bd-table"></table>');
                if (kind === "meta" || type === "meta") { $tbl.addClass("bd-meta"); }
                if (kind === "plan") { $tbl.addClass("bd-plan"); }
                if (kind === "procedure") { $tbl.addClass("bd-procedure"); }
                if (kind === "yearly") { $tbl.addClass("bd-yearly"); }
                for (var r = 0; r < b.rows.length; r++) {
                    var row = b.rows[r] || [];
                    var $tr = $("<tr></tr>");
                    for (var c = 0; c < row.length; c++) {
                        var cellText = flattenCellText(row[c]);
                        var tag = isLabelCell(cellText, c, row, kind, r) ? "<th></th>" : "<td></td>";
                        $tr.append($(tag).text(cellText));
                    }
                    $tbl.append($tr);
                }
                $body.append($tbl);
            } else {
                var paraText = flattenCellText(b.text || "");
                if (!paraText) { continue; }
                var $p = $('<div class="bd-para"></div>').text(paraText);
                if (b.kind === "line") { $p.addClass("bd-line"); }
                $body.append($p);
            }
        }
        fitBodyHeight();
    };

    var showSuggest = function (list) {
        $("#dialogBizDescViewSuggestPanel", dialogObj).show();
        $("#dialogBizDescViewSummaryPanel", dialogObj).hide();
        setPageTitle("사업설명서 매칭 후보");
        var $body = $("#dialogBizDescViewSuggestBody", dialogObj).empty();
        if (!list || !list.length) {
            $body.append('<tr><td colspan="5">유사도 60% 이상 후보가 없습니다. 조서 화면에서 「사업설명서불러오기」로 해당 실국 파일을 업로드해 주세요.</td></tr>');
            return;
        }
        for (var i = 0; i < list.length; i++) {
            var s = list[i];
            var tr = $("<tr></tr>");
            tr.append($("<td></td>").text((s.scorePct != null ? s.scorePct : Math.round((s.score || 0) * 100)) + "%"));
            tr.append($("<td></td>").text(s.deptNm || ""));
            tr.append($("<td></td>").text(s.bizNm || s.indivBiz || s.detailBiz || ""));
            tr.append($("<td></td>").text(s.orgFileNm || ""));
            var $btn = $('<a href="#" class="btnClass">매칭</a>');
            $btn.data("suggest", s);
            tr.append($("<td></td>").append($btn));
            $body.append(tr);
        }
    };

    var showSummary = function (data) {
        $("#dialogBizDescViewSuggestPanel", dialogObj).hide();
        $("#dialogBizDescViewSummaryPanel", dialogObj).show();
        var bizNm = data.bizNm || $("#dialogBizDescViewReportBizNm", dialogObj).val() || "사업설명서";
        setPageTitle(bizNm);
        var meta = "조서사업명: " + ($("#dialogBizDescViewReportBizNm", dialogObj).val() || "")
            + (data.deptNm ? " / 부서: " + data.deptNm : "")
            + (data.orgFileNm ? " ← " + data.orgFileNm : "");
        $("#dialogBizDescViewMeta", dialogObj).text(meta);
        renderBlocks(data.blocks || []);
        notifyOpenerHighlight($("#dialogBizDescViewTeBgtCompoId", dialogObj).val());
        setTimeout(fitBodyHeight, 50);
    };

    var loadSummary = function () {
        $.csAjaxCall({
            url: "/bizdesc/ajaxBizDescSummary.do",
            data: baseParam(),
            async: true,
            callBack: function (data) {
                if (isEmpty(data) || data[BCJIS_RETURN_CODE] != "SUCC") {
                    $.csAlert({ msg: (data && data.bcjisMessage) ? data.bcjisMessage : "요약 조회 실패" });
                    return;
                }
                showSummary(data.data || {});
            }
        });
    };

    var loadSuggestOrSummary = function () {
        var p = baseParam();
        p.reportBizNm = $("#dialogBizDescViewReportBizNm", dialogObj).val();
        $("#dialogBizDescViewMeta", dialogObj).text("조서사업명: " + (p.reportBizNm || "") + " — 조회 중...");
        $.csAjaxCall({
            url: "/bizdesc/ajaxBizDescSuggest.do",
            data: p,
            async: true,
            callBack: function (data) {
                if (isEmpty(data) || data[BCJIS_RETURN_CODE] != "SUCC") {
                    $.csAlert({ msg: (data && data.bcjisMessage) ? data.bcjisMessage : "후보 조회 실패" });
                    return;
                }
                var d = data.data || {};
                if (d.matched) {
                    loadSummary();
                } else {
                    showSuggest(d.suggestList || []);
                }
            }
        });
    };

    $("#dialogBizDescViewSuggestBody", dialogObj).on("click", "a", function (e) {
        e.preventDefault();
        var s = $(this).data("suggest");
        if (!s) { return; }
        var p = baseParam();
        p.bizdescFileId = s.bizdescFileId;
        p.bizSeq = s.bizSeq;
        p.bizNm = s.bizNm || s.indivBiz || s.detailBiz || "";
        p.deptNm = s.deptNm || "";
        p.matchScore = s.score;
        $.csAjaxCall({
            url: "/bizdesc/ajaxBizDescMatchSave.do",
            data: p,
            async: true,
            callBack: function (data) {
                if (isEmpty(data) || data[BCJIS_RETURN_CODE] != "SUCC") {
                    $.csAlert({ msg: (data && data.bcjisMessage) ? data.bcjisMessage : "매칭 저장 실패" });
                    return;
                }
                loadSummary();
            }
        });
    });

    var fallbackCopy = function (text, done) {
        var $ta = $('<textarea style="position:fixed;left:-9999px;top:0;"></textarea>').val(text).appendTo("body");
        $ta[0].select();
        try {
            document.execCommand("copy");
            done();
        } catch (err) {
            $.csAlert({ msg: "복사에 실패하였습니다. 직접 드래그하여 Ctrl+C 해 주세요." });
        }
        $ta.remove();
    };

    $("#dialogBizDescViewCopyBtn", dialogObj).click(function (e) {
        e.preventDefault();
        var sel = window.getSelection();
        var text = sel && !sel.isCollapsed ? String(sel) : "";
        if (!text) {
            text = $("#dialogBizDescViewBody", dialogObj).text() || "";
        }
        text = $.trim(text);
        if (!text) {
            $.csAlert({ msg: "복사할 내용이 없습니다. 영역을 드래그하여 선택해 주세요." });
            return;
        }
        var done = function () {
            $.csAlert({ msg: "클립보드에 복사되었습니다. 조서 입력칸에 Ctrl+V로 붙여넣기 하세요." });
        };
        if (navigator.clipboard && navigator.clipboard.writeText) {
            navigator.clipboard.writeText(text).then(done).catch(function () {
                fallbackCopy(text, done);
            });
        } else {
            fallbackCopy(text, done);
        }
    });

    $("#dialogBizDescViewClearBtn", dialogObj).click(function (e) {
        e.preventDefault();
        $.csConfirm({
            msg: "매칭을 해제하시겠습니까?",
            callBack: function (p) {
                if (p.confirmData != "Y") { return; }
                $.csAjaxCall({
                    url: "/bizdesc/ajaxBizDescMatchClear.do",
                    data: baseParam(),
                    async: true,
                    callBack: function (data) {
                        if (isEmpty(data) || data[BCJIS_RETURN_CODE] != "SUCC") {
                            $.csAlert({ msg: (data && data.bcjisMessage) ? data.bcjisMessage : "해제 실패" });
                            return;
                        }
                        notifyOpenerClearHighlight();
                        window.close();
                    }
                });
            }
        });
    });

    $("#dialogBizDescViewCloseBtn", dialogObj).click(function (e) {
        e.preventDefault();
        persistSelfPopupPos();
        window.close();
    });

    var persistSelfPopupPos = function () {
        try {
            var left = (window.screenX != null) ? window.screenX : window.screenLeft;
            var top = (window.screenY != null) ? window.screenY : window.screenTop;
            var width = window.outerWidth || document.documentElement.clientWidth || 900;
            var height = window.outerHeight || document.documentElement.clientHeight || 900;
            if (typeof left !== "number" || typeof top !== "number") { return; }
            var pos = { left: left, top: top, width: width, height: height };
            saveBizDescPopupPos(pos);
            if (window.opener && !window.opener.closed) {
                try { window.opener.__bizDescPopupPos = pos; } catch (ignore) {}
            }
        } catch (ignore2) {}
    };

    // 부모가 left/top을 무시하고 연 경우, 저장된 종료 위치로 스스로 이동
    var restoreSelfPopupPos = function () {
        try {
            var raw = localStorage.getItem(BIZDESC_POS_KEY);
            if (!raw) { return; }
            var pos = normalizePopupPos(JSON.parse(raw));
            if (!pos) { return; }
            var curLeft = (window.screenX != null) ? window.screenX : window.screenLeft;
            var curTop = (window.screenY != null) ? window.screenY : window.screenTop;
            if (typeof curLeft !== "number" || typeof curTop !== "number") { return; }
            if (Math.abs(curLeft - pos.left) > 15 || Math.abs(curTop - pos.top) > 15) {
                window.moveTo(pos.left, pos.top);
            }
            if (pos.width && pos.height
                    && (Math.abs((window.outerWidth || 0) - pos.width) > 20
                        || Math.abs((window.outerHeight || 0) - pos.height) > 20)) {
                try { window.resizeTo(pos.width, pos.height); } catch (ignoreR) {}
            }
        } catch (ignore3) {}
    };
    restoreSelfPopupPos();
    setTimeout(restoreSelfPopupPos, 50);
    setTimeout(restoreSelfPopupPos, 200);

    $(window).on("resize", function () {
        fitBodyHeight();
        persistSelfPopupPos();
    });
    $(window).on("beforeunload", persistSelfPopupPos);
    // 드래그 이동 중에도 위치를 남김 (beforeunload만으로는 부족할 수 있음)
    setInterval(persistSelfPopupPos, 800);

    // URL/hidden 파라미터로 초기 로드
    var reportBizNm = $("#dialogBizDescViewReportBizNm", dialogObj).val() || "";
    setPageTitle(reportBizNm || "사업설명서");
    notifyOpenerHighlight($("#dialogBizDescViewTeBgtCompoId", dialogObj).val());
    loadSuggestOrSummary();
});
