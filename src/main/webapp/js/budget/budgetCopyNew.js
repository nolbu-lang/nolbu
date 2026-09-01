$(document).ready(function() {
    var tabId = _budgetCopyTabId;
    var tabObj = $("#"+tabId);
    var comboData = null;

    // [경량] 평면 목록: 제목 + 조정액. 매칭용 통계목/산출근거/정책사업명 포함.
    var colNames = ['구분(부서 &gt; 사업 &gt; 통계목 &gt; 산출근거)', '조정액',
                    'teBgtCompoId', 'teBgtCompoSeq', 'reportCd', 'reportDetlCd', 'fisYear', 'bgtDgr', 'orderYmdSeq',
                    'teMngMokCd', 'teMngMokNm', 'compGround', 'dbizNm', 'pbizNm'
                   ];

    var escapeHtml = function(s){
        return String(s == null ? "" : s)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;");
    };

    var stripNmHtml = function(s){
        if(s == null){ return ""; }
        var t = String(s);
        if(t.indexOf("<") >= 0){
            t = $("<div>").html(t).find(".budget-copy-nm").first().text() || $("<div>").html(t).text();
        }
        return $.trim(t);
    };

    // 적용대상: 매핑된 사업만 이름 옆에 '해제' 표시 (색/밑줄 표시 없음)
    // ※ 매핑 키는 회계년도+예산차수+세세목ID (차수 간 표시 혼선 방지)
    // ※ 서버에 "적용됨" 여부를 별도로 저장하는 컬럼이 없어 클라이언트 메모리로만 관리하던 것을,
    //   메뉴 탭을 닫았다 다시 열거나 다른 화면을 갔다와도 유지되도록 sessionStorage에 함께 보관한다.
    //   (브라우저 탭을 완전히 닫으면 sessionStorage도 비워지지만, 그 경우는 재조회 대상이 아니다)
    var MAPPED_TGT_IDS_STORAGE_KEY = "bcjis_budgetCopyNew_mappedTgtIds";

    var loadMappedTgtIds = function(){
        try{
            var raw = window.sessionStorage ? window.sessionStorage.getItem(MAPPED_TGT_IDS_STORAGE_KEY) : null;
            var parsed = raw ? JSON.parse(raw) : {};
            return (parsed && typeof parsed === 'object') ? parsed : {};
        }catch(e){
            return {};
        }
    };

    var saveMappedTgtIds = function(){
        try{
            if(window.sessionStorage){
                window.sessionStorage.setItem(MAPPED_TGT_IDS_STORAGE_KEY, JSON.stringify(mappedTgtIds));
            }
        }catch(e){ /* storage 미지원/용량초과 시 조용히 무시(화면 표시는 기존처럼 메모리 값으로 동작) */ }
    };

    var mappedTgtIds = loadMappedTgtIds();

    var makeMappedKey = function(fisYear, bgtDgr, teBgtCompoId){
        return String(fisYear == null ? "" : fisYear)
            + "_" + String(bgtDgr == null ? "" : bgtDgr)
            + "_" + String(teBgtCompoId == null ? "" : teBgtCompoId);
    };

    var getMappedKeyFromRow = function(row, fallbackId){
        if(isEmpty(row) == true){
            return makeMappedKey(
                $("#condFisYear option:selected", tabObj).val(),
                $("#condBgtDgr option:selected", tabObj).val(),
                fallbackId
            );
        }
        return makeMappedKey(row.fisYear, row.bgtDgr, row.teBgtCompoId || fallbackId);
    };

    var isTgtMapped = function(row, fallbackId){
        var key = getMappedKeyFromRow(row, fallbackId);
        return key.length > 2 && mappedTgtIds[key] === true;
    };

    var tgtDgrcompoNmFormatter = function(cellvalue, options, rowObject){
        var nm = escapeHtml(cellvalue || "");
        var rid = options.rowId;
        var html = '<span class="budget-copy-nm">' + nm + '</span>';
        if(isTgtMapped(rowObject, rid)){
            html += ' <a href="#" class="budget-copy-act-unmap" data-rowid="' + rid + '">해제</a>';
        }
        return html;
    };

    var tgtDgrcompoNmUnformat = function(cellvalue, options, cell){
        var $nm = $("span.budget-copy-nm", cell);
        if($nm.length > 0){ return $nm.text(); }
        return stripNmHtml(cellvalue);
    };

    var srcColModel = [
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 400, sortable : false, align : 'left'},
                        {name : 'adjAmt', index : 'adjAmt', width : 121, sortable : false, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'reportCd', index : 'reportCd', width : 0, sortable : false, hidden : true},
                        {name : 'reportDetlCd', index : 'reportDetlCd', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'orderYmdSeq', index : 'orderYmdSeq', width : 0, sortable : false, hidden : true},
                        {name : 'teMngMokCd', index : 'teMngMokCd', width : 0, sortable : false, hidden : true},
                        {name : 'teMngMokNm', index : 'teMngMokNm', width : 0, sortable : false, hidden : true},
                        {name : 'compGround', index : 'compGround', width : 0, sortable : false, hidden : true},
                        {name : 'dbizNm', index : 'dbizNm', width : 0, sortable : false, hidden : true},
                        {name : 'pbizNm', index : 'pbizNm', width : 0, sortable : false, hidden : true}
                    ];

    var tgtColModel = [
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 480, sortable : false, align : 'left',
                         formatter : tgtDgrcompoNmFormatter, unformat : tgtDgrcompoNmUnformat},
                        {name : 'adjAmt', index : 'adjAmt', width : 121, sortable : false, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'reportCd', index : 'reportCd', width : 0, sortable : false, hidden : true},
                        {name : 'reportDetlCd', index : 'reportDetlCd', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'orderYmdSeq', index : 'orderYmdSeq', width : 0, sortable : false, hidden : true},
                        {name : 'teMngMokCd', index : 'teMngMokCd', width : 0, sortable : false, hidden : true},
                        {name : 'teMngMokNm', index : 'teMngMokNm', width : 0, sortable : false, hidden : true},
                        {name : 'compGround', index : 'compGround', width : 0, sortable : false, hidden : true},
                        {name : 'dbizNm', index : 'dbizNm', width : 0, sortable : false, hidden : true},
                        {name : 'pbizNm', index : 'pbizNm', width : 0, sortable : false, hidden : true}
                    ];

    var matchColNames = ['유사도', '구분(부서 &gt; 사업 &gt; 통계목 &gt; 산출근거)', '조정액',
                         'teBgtCompoId', 'teBgtCompoSeq', 'reportCd', 'reportDetlCd', 'fisYear', 'bgtDgr', 'orderYmdSeq',
                         'teMngMokCd', 'teMngMokNm', 'compGround', 'dbizNm', 'pbizNm', 'score'
                        ];
    var matchColModel = [
                        {name : 'scorePct', index : 'scorePct', width : 70, sortable : false, align : 'center'},
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 420, sortable : false, align : 'left'},
                        {name : 'adjAmt', index : 'adjAmt', width : 110, sortable : false, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'reportCd', index : 'reportCd', width : 0, sortable : false, hidden : true},
                        {name : 'reportDetlCd', index : 'reportDetlCd', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'orderYmdSeq', index : 'orderYmdSeq', width : 0, sortable : false, hidden : true},
                        {name : 'teMngMokCd', index : 'teMngMokCd', width : 0, sortable : false, hidden : true},
                        {name : 'teMngMokNm', index : 'teMngMokNm', width : 0, sortable : false, hidden : true},
                        {name : 'compGround', index : 'compGround', width : 0, sortable : false, hidden : true},
                        {name : 'dbizNm', index : 'dbizNm', width : 0, sortable : false, hidden : true},
                        {name : 'pbizNm', index : 'pbizNm', width : 0, sortable : false, hidden : true},
                        {name : 'score', index : 'score', width : 0, sortable : false, hidden : true}
                    ];

    // 매핑된 적용대상 집합 (키: fisYear_bgtDgr_teBgtCompoId)
    var suppressTgtSelectPopup = false;
    var matchDialogTgtRowId = "";

    var getListGridHeight = function(paneId){
        var pane = $("#" + paneId, tabObj);
        if(isEmpty(pane) == true || pane.height() < 80){
            return 150;
        }

        var overhead = 0;
        pane.children().each(function(){
            if($(this).hasClass("csGrid") == false){
                overhead += $(this).outerHeight(true) || 0;
            }
        });

        var h = pane.height() - overhead - 6;
        return h > 120 ? h : 120;
    };

    var resizeListGrid = function(gridSel, paneId){
        if(isEmpty($(gridSel, tabObj)) == true){
            return;
        }

        var bodyH = getListGridHeight(paneId);
        $(gridSel, tabObj).setGridHeight(bodyH);
        $(gridSel, tabObj).setGridWidth($("#" + paneId, tabObj).width());
        $(gridSel, tabObj).closest(".ui-jqgrid-bdiv").css({"max-height" : bodyH, "overflow-y" : "auto"});
    };

    var markMappedRows = function(){
        var grid = $("#BUDGET_COPY_GRD", tabObj);
        if(isEmpty(grid) == true || grid.length < 1){
            return;
        }
        // 매핑 여부 반영: 현재 행의 회계년도+예산차수 기준으로 '해제' 표시
        var ids = grid.jqGrid("getDataIDs") || [];
        for(var i = 0; i < ids.length; i++){
            var rid = ids[i];
            var row = grid.jqGrid("getRowData", rid);
            var nm = grid.jqGrid("getCell", rid, "dgrcompoNm");
            var plain = stripNmHtml(nm);
            var html = '<span class="budget-copy-nm">' + escapeHtml(plain) + '</span>';
            if(isTgtMapped(row, rid)){
                html += ' <a href="#" class="budget-copy-act-unmap" data-rowid="' + rid + '">해제</a>';
            }
            var $td = grid.find("#" + $.jgrid.jqID(rid) + " td[aria-describedby$='_dgrcompoNm']");
            if($td.length > 0){
                $td.html(html);
            }
        }
    };

    var mainBodyResize = function(){
        $("#subMainBody", tabObj).width($("#mainCenter", tabObj).width());
        $("#subMainBody", tabObj).height($("#mainCenter", tabObj).height());
        if($("#subMainBody", tabObj).layout){
            try{ $("#subMainBody", tabObj).layout().resizeAll(); }catch(e){}
        }
        subMainBodyResize();
    };

    var subMainBodyResize = function(){
        $("#subMainWestCond", tabObj).width($("#subMainWest", tabObj).width()-20);
        $("#subMainCenterCond", tabObj).width($("#subMainCenter", tabObj).width()-20);
        resizeListGrid("#BUDGET_COPY_SRC_GRD", "subMainWest");
        resizeListGrid("#BUDGET_COPY_GRD", "subMainCenter");
        markMappedRows();
    };

    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;

    // 하단 일괄적용 영역 제거 → 좌우 패널이 전체 높이 사용
    $("#mainBody", tabObj).layout({
        center__onresize: mainBodyResize
    });

    $("#subMainBody", tabObj).layout({
        west__size : "50%",
        center__onresize: subMainBodyResize
    });

    subMainBodyResize();

    // ===== 유사도 (띄어쓰기·대소문자 무시) =====
    var normalizeBizNm = function(s){
        if(s == null){ return ""; }
        return String(s).replace(/\s+/g, "").toLowerCase();
    };

    var bigrams = function(s){
        var out = [];
        if(!s || s.length < 2){ return out; }
        for(var i = 0; i < s.length - 1; i++){
            out.push(s.substring(i, i + 2));
        }
        return out;
    };

    var bizNameSimilarity = function(a, b){
        var na = normalizeBizNm(a);
        var nb = normalizeBizNm(b);
        if(na.length === 0 || nb.length === 0){ return 0; }
        if(na === nb){ return 1; }
        if(na.indexOf(nb) >= 0 || nb.indexOf(na) >= 0){
            var mn = Math.min(na.length, nb.length);
            var mx = Math.max(na.length, nb.length);
            return Math.min(0.95, 0.6 + (mn * 0.35 / mx));
        }
        var ba = bigrams(na);
        var bb = bigrams(nb);
        if(ba.length === 0 || bb.length === 0){
            return (na.length === 1 && nb.length === 1 && na === nb) ? 1 : 0;
        }
        var freq = {};
        for(var i = 0; i < bb.length; i++){
            freq[bb[i]] = (freq[bb[i]] || 0) + 1;
        }
        var hit = 0;
        for(var j = 0; j < ba.length; j++){
            if(freq[ba[j]] > 0){
                hit++;
                freq[ba[j]]--;
            }
        }
        return (2.0 * hit) / (ba.length + bb.length);
    };

    var isAdminOpExpense = function(row){
        var pbiz = normalizeBizNm(row && row.pbizNm);
        return pbiz.indexOf("행정운영경비") >= 0;
    };

    var getBizNmForMatch = function(row){
        // 사업명(산출근거): 산출근거 우선, 없으면 세부사업명
        if(row && row.compGround && String(row.compGround).replace(/\s+/g, "").length > 0){
            return row.compGround;
        }
        return (row && row.dbizNm) ? row.dbizNm : "";
    };

    var isSameMok = function(a, b){
        var ca = normalizeBizNm(a && a.teMngMokCd);
        var cb = normalizeBizNm(b && b.teMngMokCd);
        if(ca.length > 0 && cb.length > 0 && ca !== "00000" && cb !== "00000"){
            return ca === cb;
        }
        var na = normalizeBizNm(a && a.teMngMokNm);
        var nb = normalizeBizNm(b && b.teMngMokNm);
        return na.length > 0 && na === nb;
    };

    var collectGridRows = function(gridTableSel){
        var rows = [];
        var $g = $(gridTableSel, tabObj);
        if(isEmpty($g) == true || $g.length < 1){ return rows; }
        var ids = $g.jqGrid("getDataIDs") || [];
        for(var i = 0; i < ids.length; i++){
            var r = $g.jqGrid("getRowData", ids[i]);
            if(isEmpty(r) == false && isEmpty(r.teBgtCompoId) == false){
                r._rowId = ids[i];
                rows.push(r);
            }
        }
        return rows;
    };

    // 적용대상에 기정예산의 조서성질(분류)을 상속하므로 reportCd/reportDetlCd는 기정예산 기준
    var buildApplyParam = function(srcRow, tgtRow){
        return {
            srcReportCd : srcRow.reportCd,
            srcReportDetlCd : srcRow.reportDetlCd,
            srcFisYear : srcRow.fisYear,
            srcBgtDgr : srcRow.bgtDgr,
            srcTeBgtCompoId : srcRow.teBgtCompoId,
            srcOrderYmdSeq : srcRow.orderYmdSeq,
            reportCd : srcRow.reportCd,
            reportDetlCd : srcRow.reportDetlCd,
            fisYear : tgtRow.fisYear,
            bgtDgr : tgtRow.bgtDgr,
            teBgtCompoId : tgtRow.teBgtCompoId,
            teBgtCompoSeq : tgtRow.teBgtCompoSeq,
            orderYmdSeq : tgtRow.orderYmdSeq
        };
    };

    var applyMappings = function(mappings, doneMsg){
        if(isEmpty(mappings) == true || mappings.length < 1){
            $.csAlert({ msg : "적용할 매핑이 존재하지 않습니다." });
            return;
        }

        if(checkCloseYn(mappings[0]) == false){
            return;
        }

        $.csAjaxCall({
            url : "/budget/ajaxBudgetCopyNewCopyReportBatch.do",
            data : { mappings : mappings },
            async : true,
            callBack : function(data){
                if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
                    $.csAlert({ msg : (isEmpty(data) == true ? "적용 중 오류가 발생했습니다." : data.bcjisMessage) });
                    return;
                }

                for(var i = 0; i < mappings.length; i++){
                    if(mappings[i].teBgtCompoId){
                        mappedTgtIds[makeMappedKey(mappings[i].fisYear, mappings[i].bgtDgr, mappings[i].teBgtCompoId)] = true;
                    }
                }
                saveMappedTgtIds();
                markMappedRows();
                $.csAlert({ msg : doneMsg || data.bcjisMessage });
            }
        });
    };

    // 기정예산 / 적용대상 그리드
    var selectedSrcRowId = "";
    var selectedTgtRowId = "";

    var budgetCopySrcGrid = $.csGrid({
        id : "BUDGET_COPY_SRC",
        colNames : colNames,
        colModel : srcColModel,
        rowNum : 100000,
        defaultRows : 0,
        onSelectRow : function(rowId){ selectedSrcRowId = rowId; }
    });

    var openMatchDialogForTgt = function(rowId){
        var tgtRow = budgetCopyGrid.getRowData(rowId);
        if(isEmpty(tgtRow) == true || isEmpty(tgtRow.teBgtCompoId) == true){
            $.csAlert({ msg : "적용대상 사업을 선택하여 주십시오." });
            return;
        }

        var srcRows = collectGridRows("#BUDGET_COPY_SRC_GRD");
        if(srcRows.length < 1){
            $.csAlert({ msg : "기정예산 목록을 먼저 조회하여 주십시오." });
            return;
        }

        var tgtBiz = getBizNmForMatch(tgtRow);
        var candidates = [];
        for(var i = 0; i < srcRows.length; i++){
            var src = srcRows[i];
            // 조서성질 상속: 적용대상에 분류가 없어도 기정예산과 매핑 가능
            if(isEmpty(tgtRow.reportCd) == false && src.reportCd != tgtRow.reportCd){ continue; }
            var score = bizNameSimilarity(tgtBiz, getBizNmForMatch(src));
            if(score < 0.60){ continue; }
            var item = $.extend({}, src);
            item.score = score;
            item.scorePct = Math.round(score * 100) + "%";
            candidates.push(item);
        }

        candidates.sort(function(a, b){ return (b.score || 0) - (a.score || 0); });

        if(candidates.length < 1){
            $.csAlert({ msg : "유사도 60% 이상인 기정예산 사업이 없습니다." });
            return;
        }

        matchDialogTgtRowId = rowId;
        ensureMatchDialog();

        // dialog() 호출 후 DOM이 body로 이동하므로 tabObj 스코프 없이 조회
        var $dlg = $("#budgetCopyMatchDialog");
        var tgtNm = stripNmHtml(tgtRow.dgrcompoNm) || tgtBiz || "";
        $("#budgetCopyMatchTgtNm").text(tgtNm);

        $("#BUDGET_COPY_MATCH_GRD").clearGridData();
        budgetCopyMatchGrid.addCsJsonData({ dataList : candidates, bcjisReturnCode : "SUCC" });
        $("#BUDGET_COPY_MATCH_GRD").setGridWidth(740);
        $("#BUDGET_COPY_MATCH_GRD").setGridHeight(300);

        $dlg.dialog("open");
    };

    var doUnmap = function(rowId){
        if(isEmpty(rowId) == true){
            $.csAlert({ msg : "매핑된 사업이 아닙니다." });
            return;
        }
        var row = $("#BUDGET_COPY_GRD", tabObj).jqGrid("getRowData", rowId);
        var key = getMappedKeyFromRow(row, rowId);
        if(!mappedTgtIds[key]){
            $.csAlert({ msg : "매핑된 사업이 아닙니다." });
            return;
        }
        delete mappedTgtIds[key];
        saveMappedTgtIds();
        markMappedRows();
        $.csAlert({ msg : "매핑이 해제되었습니다. (자동매핑 대상에 다시 포함됩니다)" });
    };

    var budgetCopyGrid = $.csGrid({
        id : "BUDGET_COPY",
        colNames : colNames,
        colModel : tgtColModel,
        rowNum : 100000,
        defaultRows : 0,
        onSelectRow : function(rowId){
            selectedTgtRowId = rowId;
            if(suppressTgtSelectPopup){ return; }
            openMatchDialogForTgt(rowId);
            // 동일 행 재클릭 시에도 onSelectRow가 다시 발생하도록 선택 해제
            setTimeout(function(){
                try{ $("#BUDGET_COPY_GRD", tabObj).jqGrid("resetSelection"); }catch(e){}
            }, 0);
        }
    });

    // 적용대상 '해제' 클릭 (행 선택 팝업과 분리)
    $("#BUDGET_COPY_DIV", tabObj).on("click", "a.budget-copy-act-unmap", function(e){
        e.preventDefault();
        e.stopPropagation();
        doUnmap($(this).attr("data-rowid"));
        return false;
    });

    var budgetCopyMatchGrid = $.csGrid({
        id : "BUDGET_COPY_MATCH",
        colNames : matchColNames,
        colModel : matchColModel,
        rowNum : 100000,
        defaultRows : 0
    });

    // dialog는 최초 1회 초기화. 이후 body로 이동해도 전역 선택자로 접근.
    var matchDialogInited = false;
    var ensureMatchDialog = function(){
        if(matchDialogInited){ return; }
        var $dlg = $("#budgetCopyMatchDialog", tabObj);
        if($dlg.length < 1){
            $dlg = $("#budgetCopyMatchDialog");
        }
        $dlg.dialog({
            autoOpen : false,
            modal : true,
            width : 780,
            height : 480,
            resizable : true,
            appendTo : "body"
        });
        matchDialogInited = true;

        // 버튼도 dialog와 함께 body로 이동하므로 전역 바인딩
        $(document).off("click.budgetCopyMatchApply").on("click.budgetCopyMatchApply", "#budgetCopyMatchApplyBtn", function(e){
            e.preventDefault();
            var selId = $("#BUDGET_COPY_MATCH_GRD").jqGrid("getGridParam", "selrow");
            if(isEmpty(selId) == true){
                $.csAlert({ msg : "매핑할 기정예산 사업을 선택하여 주십시오." });
                return;
            }
            if(isEmpty(matchDialogTgtRowId) == true){
                $.csAlert({ msg : "적용대상이 없습니다." });
                return;
            }

            var srcRow = budgetCopyMatchGrid.getRowData(selId);
            var tgtRow = budgetCopyGrid.getRowData(matchDialogTgtRowId);
            if(isEmpty(srcRow) == true || isEmpty(tgtRow) == true){
                $.csAlert({ msg : "매핑 대상 자료가 올바르지 않습니다." });
                return;
            }
            if(isEmpty(tgtRow.reportCd) == false && srcRow.reportCd != tgtRow.reportCd){
                $.csAlert({ msg : "동일한 조서만 적용 할 수 있습니다." });
                return;
            }

            var mapping = buildApplyParam(srcRow, tgtRow);
            applyMappings([mapping], "매핑 적용되었습니다.");
            $("#budgetCopyMatchDialog").dialog("close");
        });

        $(document).off("click.budgetCopyMatchClose").on("click.budgetCopyMatchClose", "#budgetCopyMatchCloseBtn", function(e){
            e.preventDefault();
            $("#budgetCopyMatchDialog").dialog("close");
        });
    };
    var doSearchSrcCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({ msg : data.bcjisMessage });
            return;
        }

        selectedSrcRowId = "";
        $("#BUDGET_COPY_SRC_GRD", tabObj).clearGridData();
        budgetCopySrcGrid.addCsJsonData(data);
        resizeListGrid("#BUDGET_COPY_SRC_GRD", "subMainWest");
        data = null;
    };

    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({ msg : data.bcjisMessage });
            return;
        }

        selectedTgtRowId = "";
        suppressTgtSelectPopup = true;
        $("#BUDGET_COPY_GRD", tabObj).clearGridData();
        budgetCopyGrid.addCsJsonData(data);
        resizeListGrid("#BUDGET_COPY_GRD", "subMainCenter");
        markMappedRows();
        setTimeout(function(){ suppressTgtSelectPopup = false; }, 0);
        data = null;
    };

    var buildMapSearchData = function(condPrefix){
        var p = condPrefix || "";
        var reportDetlCd = $("#cond" + p + "ReportDetlCd option:selected", tabObj).val();
        if(isEmpty(reportDetlCd) == true){
            reportDetlCd = "";
        }

        var data = {
            reportCd : $("#cond" + p + "ReportCd option:selected", tabObj).val(),
            reportDetlCd : reportDetlCd,
            fisYear : $("#cond" + p + "FisYear option:selected", tabObj).val(),
            bgtDgr : $("#cond" + p + "BgtDgr option:selected", tabObj).val(),
            fisFgMstCd : $("#cond" + p + "FisFgMstCd option:selected", tabObj).val(),
            fisFgCd : $("#cond" + p + "FisFgCd option:selected", tabObj).val(),
            officeCd : $("#cond" + p + "OfficeCd option:selected", tabObj).val(),
            deptRankFr : $("#cond" + p + "DeptRankFr", tabObj).val(),
            deptRankTo : $("#cond" + p + "DeptRankTo", tabObj).val(),
            amtUnit : $("#condAmtUnit", tabObj).val(),
            orderYmdSeq : ""
        };

        if(p === "Src"){
            data.userDeptYn = "N";
        }

        return data;
    };

    var validateMapSearchCond = function(condPrefix){
        var p = condPrefix || "";

        // 적용대상(p="")은 분류 없이 조회 가능 — 조서성질 상속 매핑용
        // 기정예산(p="Src")만 대/중분류 필수
        if(p !== "Src"){
            return true;
        }

        if(isEmpty($("#cond" + p + "ReportMstr option:selected", tabObj).val()) == true){
            $.csAlert({
                msg : "대분류를 선택해주세요.",
                callBack : function() {
                    $("#cond" + p + "ReportMstr", tabObj).focus();
                }
            });
            return false;
        }

        if(isEmpty($("#cond" + p + "ReportCd option:selected", tabObj).val()) == true){
            $.csAlert({
                msg : "중분류를 선택해주세요.",
                callBack : function() {
                    $("#cond" + p + "ReportCd", tabObj).focus();
                }
            });
            return false;
        }

        return true;
    };

    var doSearchSrc = function(){
        if(validateMapSearchCond("Src") == false){
            return;
        }

        $.csAjaxCall({
            url : "/budget/ajaxBudgetCopyNewMapList.do",
            data : buildMapSearchData("Src"),
            async : true,
            callBack : doSearchSrcCallBack
        });
    };

    var doSearch = function(){
        if(validateMapSearchCond("") == false){
            return;
        }

        $.csAjaxCall({
            url : "/budget/ajaxBudgetCopyNewMapList.do",
            data : buildMapSearchData(""),
            async : true,
            callBack : doSearchCallBack
        });
    };

    // ===== 자동매핑: 행정운영경비 제외, 통계목+사업명(산출근거) 100% =====
    var doAutoMap = function(){
        var tgtRows = collectGridRows("#BUDGET_COPY_GRD");
        var srcRows = collectGridRows("#BUDGET_COPY_SRC_GRD");

        if(tgtRows.length < 1){
            $.csAlert({ msg : "적용대상 목록을 먼저 조회하여 주십시오." });
            return;
        }
        if(srcRows.length < 1){
            $.csAlert({ msg : "기정예산 목록을 먼저 조회하여 주십시오." });
            return;
        }

        var usedSrc = {};
        var mappings = [];

        for(var i = 0; i < tgtRows.length; i++){
            var tgt = tgtRows[i];
            if(isTgtMapped(tgt, tgt.teBgtCompoId)){ continue; }
            if(isAdminOpExpense(tgt)){ continue; }

            var tgtBiz = getBizNmForMatch(tgt);
            if(normalizeBizNm(tgtBiz).length < 1){ continue; }

            var best = null;
            for(var j = 0; j < srcRows.length; j++){
                var src = srcRows[j];
                if(usedSrc[src.teBgtCompoId]){ continue; }
                if(isAdminOpExpense(src)){ continue; }
                // 조서성질 상속: 적용대상 분류가 비어 있으면 기정예산 분류와 무관하게 매칭
                if(isEmpty(tgt.reportCd) == false && src.reportCd != tgt.reportCd){ continue; }
                if(!isSameMok(tgt, src)){ continue; }
                var score = bizNameSimilarity(tgtBiz, getBizNmForMatch(src));
                if(score < 1){ continue; }
                best = src;
                break;
            }

            if(best != null){
                usedSrc[best.teBgtCompoId] = true;
                mappings.push(buildApplyParam(best, tgt));
            }
        }

        if(mappings.length < 1){
            $.csAlert({ msg : "자동매핑 대상(통계목·사업명 100% 일치)이 없습니다." });
            return;
        }

        $.csConfirm({
            msg : "유사도 100% " + mappings.length + "건을 자동매핑 적용하시겠습니까?",
            callBack : function(params){
                if(params.confirmData != "Y"){ return; }
                applyMappings(mappings, mappings.length + "건 자동매핑 적용되었습니다.");
            }
        });
    };

    $("#autoMapBtn", tabObj).click(function() {
        doAutoMap();
    });

    var doCondSrcInit = function(){
    	if(!comboData){ return; }
    	$("#condSrcReportMstr", tabObj).csCreatCombo(comboData, {
            id : 'reportMstr',
            groupId : 'ALL',
            selectedValue : '',
            comboType : 'S',
            comboTypeValue : ''
        });

        var srcReportMstr = $("#condSrcReportMstr option:selected", tabObj).val();
        condSrcReportCdCreateCombo(srcReportMstr, '');

        var srcReportCd = $("#condSrcReportCd option:selected", tabObj).val();
        condSrcReportDetlCdCreateCombo(srcReportCd, '');

        $("#condSrcFisYear", tabObj).csCreatCombo(comboData, {
            id : 'fisYear',
            groupId : 'ALL',
            selectedValue : '',
            comboType : '',
            comboTypeValue : ''
        });

        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        condSrcBgtDgrCreateCombo(fisYear, '');
        condSrcFisFgMstCdCreateCombo(fisYear, '');

        var bgtDgr = $("#condSrcBgtDgr option:selected", tabObj).val();
        condSrcOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');

        var fisFgMstCd = $("#condSrcFisFgMstCd option:selected", tabObj).val();
        condSrcFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');

        $("#condSrcDeptCdFr", tabObj).val("");
        $("#condSrcDeptNmFr", tabObj).val("");
        $("#condSrcDeptRankFr", tabObj).val("");
        $("#condSrcDeptCdTo", tabObj).val("");
        $("#condSrcDeptNmTo", tabObj).val("");
        $("#condSrcDeptRankTo", tabObj).val("");
    };

    $("#condSrcInitBtn", tabObj).click(function() {
        doCondSrcInit();
    });

    var doCondInit = function(){
    	if(!comboData){ return; }
    	$("#condReportMstr", tabObj).csCreatCombo(comboData, {
            id : 'reportMstr',
            groupId : 'ALL',
            selectedValue : '',
            comboType : 'S',
            comboTypeValue : ''
        });

        var reportMstr = $("#condReportMstr option:selected", tabObj).val();
        condReportCdCreateCombo(reportMstr, '');

        var reportCd = $("#condReportCd option:selected", tabObj).val();
        condReportDetlCdCreateCombo(reportCd, '');

        $("#condFisYear", tabObj).csCreatCombo(comboData, {
            id : 'fisYear',
            groupId : 'ALL',
            selectedValue : '',
            comboType : '',
            comboTypeValue : ''
        });

        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
        condFisFgMstCdCreateCombo(fisYear, '');

        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');

        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        condFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');

        $("#condDeptCdFr", tabObj).val("");
        $("#condDeptNmFr", tabObj).val("");
        $("#condDeptRankFr", tabObj).val("");
        $("#condDeptCdTo", tabObj).val("");
        $("#condDeptNmTo", tabObj).val("");
        $("#condDeptRankTo", tabObj).val("");
    };

    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });

    $("#srcSearchBtn", tabObj).click(function() {
        doSearchSrc();
    });

    $("#searchBtn", tabObj).click(function() {
        doSearch();
    });

    var doChangeCondReportMstr = function(){
        var reportMstr = $("#condReportMstr option:selected", tabObj).val();
        condReportCdCreateCombo(reportMstr, '');
        doChangeCondReportCd();
    };

    var doChangeCondReportCd = function(){
    	var reportCd = $("#condReportCd option:selected", tabObj).val();
    	condReportDetlCdCreateCombo(reportCd, '');
    };

    var doChangeCondSrcReportMstr = function(){
    	var srcReportMstr = $("#condSrcReportMstr option:selected", tabObj).val();
    	condSrcReportCdCreateCombo(srcReportMstr, '');
    	doChangeCondSrcReportCd();
    };

    var doChangeCondSrcReportCd = function(){
    	var srcReportCd = $("#condSrcReportCd option:selected", tabObj).val();
    	condSrcReportDetlCdCreateCombo(srcReportCd, '');
    };

    var doChangeCondSrcFisYear = function(){
        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        condSrcBgtDgrCreateCombo(fisYear, '');
        condSrcFisFgMstCdCreateCombo(fisYear, '');
        doChageCondSrcBgtDgr();
        doChageCondSrcFisFgMstCd();
    };

    var doChangeCondFisYear = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
        condFisFgMstCdCreateCombo(fisYear, '');
        doChageCondBgtDgr();
        doChageCondFisFgMstCd();
    };

    var doChageCondSrcBgtDgr = function(){
        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condSrcBgtDgr option:selected", tabObj).val();
        condSrcOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
        doChangeCondSrcOfficeCd();
    };

    var doChageCondBgtDgr = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
        doChangeCondOfficeCd();
    };

    var doChageCondSrcFisFgMstCd = function(){
        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        var fisFgMstCd = $("#condSrcFisFgMstCd option:selected", tabObj).val();
        condSrcFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');
    };

    var doChageCondFisFgMstCd = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        condFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');
    };

    var doChangeCondSrcOfficeCd = function(){
        $("#condSrcDeptCdFr", tabObj).val("");
        $("#condSrcDeptNmFr", tabObj).val("");
        $("#condSrcDeptRankFr", tabObj).val("");
        $("#condSrcDeptCdTo", tabObj).val("");
        $("#condSrcDeptNmTo", tabObj).val("");
        $("#condSrcDeptRankTo", tabObj).val("");
    };

    var doChangeCondOfficeCd = function(){
        $("#condDeptCdFr", tabObj).val("");
        $("#condDeptNmFr", tabObj).val("");
        $("#condDeptRankFr", tabObj).val("");
        $("#condDeptCdTo", tabObj).val("");
        $("#condDeptNmTo", tabObj).val("");
        $("#condDeptRankTo", tabObj).val("");
    };

    $("#condReportMstr", tabObj).change(function(){ doChangeCondReportMstr(); });
    $("#condReportCd", tabObj).change(function(){ doChangeCondReportCd(); });
    $("#condSrcReportMstr", tabObj).change(function(){ doChangeCondSrcReportMstr(); });
    $("#condSrcReportCd", tabObj).change(function(){ doChangeCondSrcReportCd(); });
    $("#condSrcFisYear", tabObj).change(function(){ doChangeCondSrcFisYear(); });
    $("#condFisYear", tabObj).change(function(){ doChangeCondFisYear(); });
    $("#condSrcBgtDgr", tabObj).change(function(){ doChageCondSrcBgtDgr(); });
    $("#condBgtDgr", tabObj).change(function(){ doChageCondBgtDgr(); });
    $("#condSrcFisFgMstCd", tabObj).change(function(){ doChageCondSrcFisFgMstCd(); });
    $("#condFisFgMstCd", tabObj).change(function(){ doChageCondFisFgMstCd(); });
    $("#condSrcOfficeCd", tabObj).change(function(){ doChangeCondSrcOfficeCd(); });
    $("#condOfficeCd", tabObj).change(function(){ doChangeCondOfficeCd(); });

    var openDialogBgtDeptSrcSelt = function(seltFg){
        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condSrcBgtDgr option:selected", tabObj).val();
        var officeCd = $("#condSrcOfficeCd option:selected", tabObj).val();

        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("budgetCopyDialogDgrDeptSrcSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("N");

        $("#dialogDgrDeptSeltDiv").dialog('open');
    };

    budgetCopyDialogDgrDeptSrcSeltCallBack = function(param){
        if($("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val() == 1){
            $("#condSrcDeptCdFr", tabObj).val(param.deptCd);
            $("#condSrcDeptNmFr", tabObj).val(param.deptNm);
            $("#condSrcDeptRankFr", tabObj).val(param.deptRank);
            $("#condSrcDeptCdTo", tabObj).val(param.deptCd);
            $("#condSrcDeptNmTo", tabObj).val(param.deptNm);
            $("#condSrcDeptRankTo", tabObj).val(param.deptRank);
        }else{
            $("#condSrcDeptCdTo", tabObj).val(param.deptCd);
            $("#condSrcDeptNmTo", tabObj).val(param.deptNm);
            $("#condSrcDeptRankTo", tabObj).val(param.deptRank);
        }
    };

    var openDialogBgtDeptSelt = function(seltFg){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();

        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("budgetCopyDialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");

        $("#dialogDgrDeptSeltDiv").dialog('open');
    };

    budgetCopyDialogDgrDeptSeltCallBack = function(param){
        if($("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val() == 1){
            $("#condDeptCdFr", tabObj).val(param.deptCd);
            $("#condDeptNmFr", tabObj).val(param.deptNm);
            $("#condDeptRankFr", tabObj).val(param.deptRank);
            $("#condDeptCdTo", tabObj).val(param.deptCd);
            $("#condDeptNmTo", tabObj).val(param.deptNm);
            $("#condDeptRankTo", tabObj).val(param.deptRank);
        }else{
            $("#condDeptCdTo", tabObj).val(param.deptCd);
            $("#condDeptNmTo", tabObj).val(param.deptNm);
            $("#condDeptRankTo", tabObj).val(param.deptRank);
        }
    };

    $("#openDialogBgtDeptSrcBtnFr", tabObj).click(function(){ openDialogBgtDeptSrcSelt(1); });
    $("#openDialogBgtDeptSrcBtnTo", tabObj).click(function(){ openDialogBgtDeptSrcSelt(2); });
    $("#openDialogBgtDeptBtnFr", tabObj).click(function(){ openDialogBgtDeptSelt(1); });
    $("#openDialogBgtDeptBtnTo", tabObj).click(function(){ openDialogBgtDeptSelt(2); });

    var comboParam = [
					{id : "reportCd", codeId : "RP011"},
					{id : "reportDetlCd", codeId : "RP012"},
					{id : "reportMstr", codeId : "RP010"},
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "bgtDgr", subQueryId : "BgtDgr"},
                      {id : "fisFgMstCd", subQueryId : "FisFgMstCd"},
                      {id : "fisFgCd", subQueryId : "FisFgCd"},
                      {id : "officeCdAll", subQueryId : "OfficeCd", userDeptYn : "N"},
                      {id : "officeCd", subQueryId : "OfficeCd"}
                    ];

    var condReportCdCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
        $("#condReportCd", tabObj).csCreatCombo(comboData
                , {id: 'reportCd', groupId: groupId, selectedValue: selectedValue, comboType: 'S', comboTypeValue: ''}
        );
    };

    var condReportDetlCdCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
    	$("#condReportDetlCd", tabObj).csCreatCombo(comboData
    			, {id: 'reportDetlCd', groupId: groupId, selectedValue: selectedValue, comboType: 'A', comboTypeValue: ''}
    	);
    };

    var condSrcReportCdCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
    	$("#condSrcReportCd", tabObj).csCreatCombo(comboData
    			, {id: 'reportCd', groupId: groupId, selectedValue: selectedValue, comboType: 'S', comboTypeValue: ''}
    	);
    };

    var condSrcReportDetlCdCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
        $("#condSrcReportDetlCd", tabObj).csCreatCombo(comboData
                , {id: 'reportDetlCd', groupId: groupId, selectedValue: selectedValue, comboType: 'A', comboTypeValue: ''}
        );
    };

    var condSrcBgtDgrCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
        $("#condSrcBgtDgr", tabObj).csCreatCombo(comboData
                , {id: 'bgtDgr', groupId: groupId, selectedValue: selectedValue, comboType: '', comboTypeValue: ''}
        );
    };

    var condBgtDgrCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
        $("#condBgtDgr", tabObj).csCreatCombo(comboData
                , {id: 'bgtDgr', groupId: groupId, selectedValue: selectedValue, comboType: '', comboTypeValue: ''}
        );
    };

    var condSrcFisFgMstCdCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
        $("#condSrcFisFgMstCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgMstCd', groupId: groupId, selectedValue: selectedValue, comboType: '', comboTypeValue: ''}
        );
    };

    var condFisFgMstCdCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
        $("#condFisFgMstCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgMstCd', groupId: groupId, selectedValue: selectedValue, comboType: '', comboTypeValue: ''}
        );
    };

    var condSrcFisFgCdCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
        $("#condSrcFisFgCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgCd', groupId: groupId, selectedValue: selectedValue, comboType: 'A', comboTypeValue: ''}
        );
    };

    var condFisFgCdCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
        $("#condFisFgCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgCd', groupId: groupId, selectedValue: selectedValue, comboType: 'A', comboTypeValue: ''}
        );
    };

    var condSrcOfficeCdCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
        $("#condSrcOfficeCd", tabObj).csCreatCombo(comboData
                , {id: 'officeCdAll', groupId: groupId, selectedValue: selectedValue, comboType: 'A', comboTypeValue: ''}
        );
    };

    var condOfficeCdCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
        $("#condOfficeCd", tabObj).csCreatCombo(comboData
                , {id: 'officeCd', groupId: groupId, selectedValue: selectedValue, comboType: 'A', comboTypeValue: ''}
        );
    };

    // 화면 골격 표시 후 콤보 로드 (메뉴 오픈 체감속도)
    setTimeout(function(){
        comboData = jQuery.csComboAjaxCall(comboParam);
        doCondSrcInit();
        doCondInit();
    }, 0);
});
