$(document).ready(function() {
    var tabId = _budgetCopyTabId;
    var tabObj = $("#"+tabId);
    
    // [경량] 평면 목록: 제목 + 조정액. 적용에 필요한 키는 hidden 으로 보관.
    var colNames = ['구분(부서 &gt; 사업 &gt; 통계목 &gt; 산출근거)', '조정액',
                    'teBgtCompoId', 'teBgtCompoSeq', 'reportCd', 'reportDetlCd', 'fisYear', 'bgtDgr', 'orderYmdSeq'
                   ];

    var colModel = [
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 520, sortable : false, align : 'left'},
                        {name : 'adjAmt', index : 'adjAmt', width : 110, sortable : false, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'reportCd', index : 'reportCd', width : 0, sortable : false, hidden : true},
                        {name : 'reportDetlCd', index : 'reportDetlCd', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'orderYmdSeq', index : 'orderYmdSeq', width : 0, sortable : false, hidden : true}
                    ];

    // 매핑 목록(전년도 -> 적용대상) 그리드 컬럼
    var mapColNames = ['전년도 제목', '전년도조정액', '적용대상', '적용대상 조정액',
                       'srcReportCd', 'srcReportDetlCd', 'srcFisYear', 'srcBgtDgr', 'srcTeBgtCompoId', 'srcOrderYmdSeq',
                       'reportCd', 'reportDetlCd', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'orderYmdSeq'
                      ];

    var mapColModel = [
                        {name : 'srcDgrcompoNm', index : 'srcDgrcompoNm', width : 480, sortable : false, align : 'left'},
                        {name : 'srcAdjAmt', index : 'srcAdjAmt', width : 110, sortable : false, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'tgtDgrcompoNm', index : 'tgtDgrcompoNm', width : 480, sortable : false, align : 'left'},
                        {name : 'tgtAdjAmt', index : 'tgtAdjAmt', width : 110, sortable : false, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'srcReportCd', index : 'srcReportCd', width : 0, sortable : false, hidden : true},
                        {name : 'srcReportDetlCd', index : 'srcReportDetlCd', width : 0, sortable : false, hidden : true},
                        {name : 'srcFisYear', index : 'srcFisYear', width : 0, sortable : false, hidden : true},
                        {name : 'srcBgtDgr', index : 'srcBgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'srcTeBgtCompoId', index : 'srcTeBgtCompoId', width : 0, sortable : false, hidden : true},
                        {name : 'srcOrderYmdSeq', index : 'srcOrderYmdSeq', width : 0, sortable : false, hidden : true},
                        {name : 'reportCd', index : 'reportCd', width : 0, sortable : false, hidden : true},
                        {name : 'reportDetlCd', index : 'reportDetlCd', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'orderYmdSeq', index : 'orderYmdSeq', width : 0, sortable : false, hidden : true}
                      ];
    
    // 전년도/적용대상 목록 그리드 높이: 패널 실제 높이에서 제목·조건·버튼 영역을 제외
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
    
    // 매핑 그리드: 남쪽 영역 너비를 채우고, 행이 늘면 세로 스크롤되도록 높이 제한
    var mapGridResize = function(){
        if(isEmpty($("#BUDGET_MAP_GRD", tabObj)) == true){
            return;
        }

        var southW = $("#mainSouth", tabObj).width();
        var southH = $("#mainSouth", tabObj).height();
        if(isEmpty(southW) == false && southW > 0){
            $("#BUDGET_MAP_GRD", tabObj).setGridWidth(southW - 4);
        }
        // 헤더/버튼 영역(약 70px)을 제외한 높이만큼만 표시하고 초과 시 스크롤
        var bodyH = (southH > 0 ? southH : 320) - 70;
        if(bodyH < 80){ bodyH = 80; }
        $("#BUDGET_MAP_GRD", tabObj).closest(".ui-jqgrid-bdiv").css({"max-height" : bodyH, "overflow-y" : "auto"});
    };

    var mainBodyResize = function(){
        $("#subMainBody", tabObj).width($("#mainCenter", tabObj).width());
        $("#subMainBody", tabObj).layout().resizeAll();
        subMainBodyResize();
        mapGridResize();
    };
    
    var subMainBodyResize = function(){
        $("#subMainCenterCond", tabObj).width($("#subMainCenter", tabObj).width()-20);
        $("#subMainWestCond", tabObj).width($("#subMainWest", tabObj).width()-20);
        resizeListGrid("#BUDGET_COPY_SRC_GRD", "subMainWest");
        resizeListGrid("#BUDGET_COPY_GRD", "subMainCenter");
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        south__size : 240,
        south__minSize : 160,
        south__maxSize : 400,
        south__resizable : true,
        south__onresize: mainBodyResize,
        center__onresize: mainBodyResize
    });
    
    $("#subMainBody", tabObj).layout({
        west__size : "50%",
        center__onresize: subMainBodyResize
    });
    
    subMainBodyResize();

    // 선택된 행 추적 (전년도/올해)
    var selectedSrcRowId = "";
    var selectedTgtRowId = "";

    // 평면 그리드 1회 생성 (조회 시 데이터만 갱신)
    var budgetCopySrcGrid = $.csGrid({
        id : "BUDGET_COPY_SRC",
        colNames : colNames,
        colModel : colModel,
        rowNum : 100000,
        defaultRows : 0,
        onSelectRow : function(rowId){ selectedSrcRowId = rowId; }
    });

    var budgetCopyGrid = $.csGrid({
        id : "BUDGET_COPY",
        colNames : colNames,
        colModel : colModel,
        rowNum : 100000,
        defaultRows : 0,
        onSelectRow : function(rowId){ selectedTgtRowId = rowId; }
    });

    var budgetMapGrid = $.csGrid({
        id : "BUDGET_MAP",
        colNames : mapColNames,
        colModel : mapColModel,
        rowNum : 100000,
        defaultRows : 0
    });

    mapGridResize();

    var doSearchSrcCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
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
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        selectedTgtRowId = "";
        $("#BUDGET_COPY_GRD", tabObj).clearGridData();
        budgetCopyGrid.addCsJsonData(data);
        resizeListGrid("#BUDGET_COPY_GRD", "subMainCenter");

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

    var doCondSrcInit = function(){
    	//대분류 selectBox 세팅
        $("#condSrcReportMstr", tabObj).csCreatCombo(comboData, {
            id : 'reportMstr',
            groupId : 'ALL',
            selectedValue : '',
            comboType : 'S',
            comboTypeValue : ''
        });
        
        //중분류 selectBox 세팅
        var srcReportMstr = $("#condSrcReportMstr option:selected", tabObj).val();
        condSrcReportCdCreateCombo(srcReportMstr, '');
        
        //소분류 selectBox 세팅
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
    	//대분류 selectBox 세팅
        $("#condReportMstr", tabObj).csCreatCombo(comboData, {
            id : 'reportMstr',
            groupId : 'ALL',
            selectedValue : '',
            comboType : 'S',
            comboTypeValue : ''
        });
        
        //중분류 selectBox 세팅
        var reportMstr = $("#condReportMstr option:selected", tabObj).val();
        condReportCdCreateCombo(reportMstr, '');
        
        //소분류 selectBox 세팅
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

    // ===== 매핑(전년도 -> 올해) 추가/삭제/일괄적용 =====

    // 매핑추가: 선택된 전년도 1건 + 올해 1건을 매핑 목록에 담는다.
    $("#mapAddBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }

        if(isEmpty(selectedSrcRowId) == true){
            $.csAlert({ msg : "전년도 예산(왼쪽)에서 적용할 자료를 선택하여 주십시오." });
            return;
        }

        if(isEmpty(selectedTgtRowId) == true){
            $.csAlert({ msg : "적용대상(올해)에서 적용할 대상을 선택하여 주십시오." });
            return;
        }

        var srcRowData = budgetCopySrcGrid.getRowData(selectedSrcRowId);
        var tgtRowData = budgetCopyGrid.getRowData(selectedTgtRowId);

        if(isEmpty(srcRowData) == true || isEmpty(srcRowData.teBgtCompoId) == true
           || isEmpty(tgtRowData) == true || isEmpty(tgtRowData.teBgtCompoId) == true){
            $.csAlert({ msg : "세세목 이하 예산편성을 선택하여 주십시오." });
            return;
        }

        if(srcRowData.reportCd != tgtRowData.reportCd){
            $.csAlert({ msg : "동일한 조서만 적용 할 수 있습니다." });
            return;
        }

        // 동일 대상(올해 세세목)이 이미 매핑되어 있으면 중복 추가 방지
        var existRow = budgetMapGrid.getRowData(tgtRowData.teBgtCompoId);
        if(isEmpty(existRow) == false && isEmpty(existRow.teBgtCompoId) == false){
            $.csAlert({ msg : "이미 매핑된 적용대상입니다. (대상은 한 번만 매핑할 수 있습니다)" });
            return;
        }

        var applyParam = {
            srcReportCd : srcRowData.reportCd,
            srcReportDetlCd : srcRowData.reportDetlCd,
            srcFisYear : srcRowData.fisYear,
            srcBgtDgr : srcRowData.bgtDgr,
            srcTeBgtCompoId : srcRowData.teBgtCompoId,
            srcOrderYmdSeq : srcRowData.orderYmdSeq,
            reportCd : tgtRowData.reportCd,
            reportDetlCd : tgtRowData.reportDetlCd,
            fisYear : tgtRowData.fisYear,
            bgtDgr : tgtRowData.bgtDgr,
            teBgtCompoId : tgtRowData.teBgtCompoId,
            orderYmdSeq : tgtRowData.orderYmdSeq
        };

        // 마감여부 확인(기존 단건 적용과 동일한 검증)
        if(checkCloseYn(applyParam) == false){
            return;
        }

        var mapRow = $.extend({}, applyParam);
        mapRow.srcDgrcompoNm = srcRowData.dgrcompoNm;
        mapRow.srcAdjAmt = srcRowData.adjAmt;
        mapRow.tgtDgrcompoNm = tgtRowData.dgrcompoNm;
        mapRow.tgtAdjAmt = tgtRowData.adjAmt;

        budgetMapGrid.insertData(tgtRowData.teBgtCompoId, mapRow);
        mapGridResize();

        $("#batchApplyBtn", tabObj).btnChangeState(true);
    });

    // 매핑삭제: 선택된 매핑 1건 삭제
    $("#mapDelBtn", tabObj).click(function() {
        var selRowId = budgetMapGrid.getGridParam("selrow");
        if(isEmpty(selRowId) == true){
            $.csAlert({ msg : "삭제할 매핑을 선택하여 주십시오." });
            return;
        }

        budgetMapGrid.jqGrid("delRowData", selRowId);
        mapGridResize();
    });

    // 전체삭제
    $("#mapClearBtn", tabObj).click(function() {
        $("#BUDGET_MAP_GRD", tabObj).clearGridData();
        mapGridResize();
    });

    // 매핑 목록을 적용 파라미터 배열로 수집
    var getMappings = function(){
        var mappings = [];
        var rows = $("#BUDGET_MAP_GRD", tabObj)[0].rows;
        var rowData;
        for(var i = 0; i < rows.length; i++){
            rowData = budgetMapGrid.getRowData(rows[i].id);
            if(isEmpty(rowData) == true || isEmpty(rowData.teBgtCompoId) == true){
                continue;
            }

            mappings.push({
                srcReportCd : rowData.srcReportCd,
                srcReportDetlCd : rowData.srcReportDetlCd,
                srcFisYear : rowData.srcFisYear,
                srcBgtDgr : rowData.srcBgtDgr,
                srcTeBgtCompoId : rowData.srcTeBgtCompoId,
                srcOrderYmdSeq : rowData.srcOrderYmdSeq,
                reportCd : rowData.reportCd,
                reportDetlCd : rowData.reportDetlCd,
                fisYear : rowData.fisYear,
                bgtDgr : rowData.bgtDgr,
                teBgtCompoId : rowData.teBgtCompoId,
                orderYmdSeq : rowData.orderYmdSeq
            });
        }

        return mappings;
    };

    var doBatchApply = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var mappings = getMappings();
        if(isEmpty(mappings) == true || mappings.length < 1){
            $.csAlert({ msg : "적용할 매핑이 존재하지 않습니다." });
            return;
        }

        var data = $.csAjaxCall({
            url : "/budget/ajaxBudgetCopyNewCopyReportBatch.do",
            data : { mappings : mappings }
        });

        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({ msg : (isEmpty(data) == true ? "적용 중 오류가 발생했습니다." : data.bcjisMessage) });
            return;
        }

        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                $("#BUDGET_MAP_GRD", tabObj).clearGridData();
                mapGridResize();
            }
        });
    };

    // 일괄적용: 매핑 목록 전체를 한 번에 적용
    $("#batchApplyBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }

        var mappings = getMappings();
        if(isEmpty(mappings) == true || mappings.length < 1){
            $.csAlert({ msg : "적용할 매핑이 존재하지 않습니다." });
            return;
        }

        $.csConfirm({
            msg : mappings.length + "건을 일괄 적용하시겠습니까?",
            callBack : doBatchApply
        });
    });
    
  //대분류 데이터 변경시
    var doChangeCondReportMstr = function(){
        var reportMstr = $("#condReportMstr option:selected", tabObj).val();
        condReportCdCreateCombo(reportMstr, '');
        doChangeCondReportCd();
    };
    
    //중분류 데이터 변경시
    var doChangeCondReportCd = function(){
    	var reportCd = $("#condReportCd option:selected", tabObj).val();
    	condReportDetlCdCreateCombo(reportCd, '');
    };
    
    //대분류 데이터 변경시
    var doChangeCondSrcReportMstr = function(){
    	var srcReportMstr = $("#condSrcReportMstr option:selected", tabObj).val();
    	condSrcReportCdCreateCombo(srcReportMstr, '');
    	doChangeCondSrcReportCd();
    };
    
    //중분류 데이터 변경시
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
    
    $("#condReportMstr", tabObj).change(function(){
        doChangeCondReportMstr();
    });
    
    $("#condReportCd", tabObj).change(function(){
    	doChangeCondReportCd();
    });
    
    $("#condSrcReportMstr", tabObj).change(function(){
    	doChangeCondSrcReportMstr();
    });
    
    $("#condSrcReportCd", tabObj).change(function(){
    	doChangeCondSrcReportCd();
    });
    
    $("#condSrcFisYear", tabObj).change(function(){
        doChangeCondSrcFisYear();
    });
    
    $("#condFisYear", tabObj).change(function(){
        doChangeCondFisYear();
    });
    
    $("#condSrcBgtDgr", tabObj).change(function(){
        doChageCondSrcBgtDgr();
    });
    
    $("#condBgtDgr", tabObj).change(function(){
        doChageCondBgtDgr();
    });
    
    $("#condSrcFisFgMstCd", tabObj).change(function(){
        doChageCondSrcFisFgMstCd();
    });
    
    $("#condFisFgMstCd", tabObj).change(function(){
        doChageCondFisFgMstCd();
    });
    
    $("#condSrcOfficeCd", tabObj).change(function(){
        doChangeCondSrcOfficeCd();
    });
    
    $("#condOfficeCd", tabObj).change(function(){
        doChangeCondOfficeCd();
    });
    
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
    
    $("#openDialogBgtDeptSrcBtnFr", tabObj).click(function(){
        openDialogBgtDeptSrcSelt(1);
    });
    
    $("#openDialogBgtDeptSrcBtnTo", tabObj).click(function(){
        openDialogBgtDeptSrcSelt(2);
    });
    
    $("#openDialogBgtDeptBtnFr", tabObj).click(function(){
        openDialogBgtDeptSelt(1);
    });
    
    $("#openDialogBgtDeptBtnTo", tabObj).click(function(){
        openDialogBgtDeptSelt(2);
    });
    
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

    var comboData = jQuery.csComboAjaxCall(comboParam);
        
  //대분류 데이터에 따라 중분류 새로 세팅
    var condReportCdCreateCombo = function(groupId, selectedValue){
        $("#condReportCd", tabObj).csCreatCombo(comboData
                , {id: 'reportCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'S'
                  , comboTypeValue: ''
                  }
        );
    };
    
  //중분류 데이터에 따라 소분류 새로 세팅
    var condReportDetlCdCreateCombo = function(groupId, selectedValue){
    	$("#condReportDetlCd", tabObj).csCreatCombo(comboData
    			, {id: 'reportDetlCd'
    				, groupId: groupId
    				, selectedValue: selectedValue
    				, comboType: 'A'
    				, comboTypeValue: ''
    	}
    	);
    };
    
    //대분류 데이터에 따라 중분류 새로 세팅
    var condSrcReportCdCreateCombo = function(groupId, selectedValue){
    	$("#condSrcReportCd", tabObj).csCreatCombo(comboData
    			, {id: 'reportCd'
    				, groupId: groupId
    				, selectedValue: selectedValue
    				, comboType: 'S'
    					, comboTypeValue: ''
    	}
    	);
    };
    
    var condSrcReportDetlCdCreateCombo = function(groupId, selectedValue){
        $("#condSrcReportDetlCd", tabObj).csCreatCombo(comboData
                , {id: 'reportDetlCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
        
    var condSrcBgtDgrCreateCombo = function(groupId, selectedValue){
        $("#condSrcBgtDgr", tabObj).csCreatCombo(comboData
                , {id: 'bgtDgr'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: ''
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condBgtDgrCreateCombo = function(groupId, selectedValue){
        $("#condBgtDgr", tabObj).csCreatCombo(comboData
                , {id: 'bgtDgr'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: ''
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condSrcFisFgMstCdCreateCombo = function(groupId, selectedValue){
        $("#condSrcFisFgMstCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgMstCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: ''
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condFisFgMstCdCreateCombo = function(groupId, selectedValue){
        $("#condFisFgMstCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgMstCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: ''
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condSrcFisFgCdCreateCombo = function(groupId, selectedValue){
        $("#condSrcFisFgCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condFisFgCdCreateCombo = function(groupId, selectedValue){
        $("#condFisFgCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condSrcOfficeCdCreateCombo = function(groupId, selectedValue){
        $("#condSrcOfficeCd", tabObj).csCreatCombo(comboData
                , {id: 'officeCdAll'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condOfficeCdCreateCombo = function(groupId, selectedValue){
        $("#condOfficeCd", tabObj).csCreatCombo(comboData
                , {id: 'officeCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
    doCondSrcInit();
    doCondInit();
    
    $("#mapAddBtn", tabObj).btnChangeState(true);
    
});
