$(document).ready(function() {
    var tabId = _reportWrite010TabId;
    var tabObj = $("#"+tabId);
    var gridScrollPosition = 0;
    var comboData = null;
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        if(rowObject.teBgtCompoId != "00000000000"){
            return ' style="vertical-align: top;"';
        }
    };
    
    reportWirte010DialogDgrcompoModifyCallBackFunction = function(param){
        var rowId = param.dgrcompoId;
        if(isEmpty(rowId) == true){
            return;
        }
        
        reportWrite010Grid.jqGrid('setRowData', rowId, param);
        
        var dgrcompoNmView = param.dgrcompoNmView;
        if(isEmpty(dgrcompoNmView) == true){
            dgrcompoNmView = "";
        }
        
        $("#dgrcompoNmView_" + rowId, tabObj).html(dgrcompoNmView);
    };
    
    reportWirte010OpenDialogDgrcompoModify = function(rowId){  
        var rowData = reportWrite010Grid.getRowData(rowId);
        
        $("#dialogDgrcompoModifyCallBackFunction", $("#dialogDgrcompoModifyDiv")).val("reportWirte010DialogDgrcompoModifyCallBackFunction");
        $("#dialogDgrcompoModifyDgrcompoId", $("#dialogDgrcompoModifyDiv")).val(rowData.dgrcompoId);
        $("#dialogDgrcompoModifyFisYear", $("#dialogDgrcompoModifyDiv")).val(rowData.fisYear);
        $("#dialogDgrcompoModifyBgtDgr", $("#dialogDgrcompoModifyDiv")).val(rowData.bgtDgr);
        $("#dialogDgrcompoModifyTeBgtCompoId", $("#dialogDgrcompoModifyDiv")).val(rowData.teBgtCompoId);
        $("#dialogDgrcompoModifyIsLeaf", $("#dialogDgrcompoModifyDiv")).val(rowData.isLeaf);
        $("#dialogDgrcompoModifyAmtUnit", $("#dialogDgrcompoModifyDiv")).val($("#condAmtUnit", tabObj).val());
        
        $("#dialogDgrcompoModifyDiv").dialog('open');
    };
    
    var editFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(_mainNorthPowGrCd == "BC002"){
            return cellValue;
        }
        
        if(rowObject.teBgtCompoId == "00000000000"){
            return cellValue;
        }
        
        var demandCont = rowObject.demandCont; 
        if(isEmpty(demandCont) == true){
            demandCont = "";
        }
        
        var rVal = '<a href="javascript:reportWirte010OpenDialogDgrcompoModify(\''+options.rowId+'\');"><span class="ui-icon ui-icon-pencil"></span></a>';
        
        return rVal;
    };
    
    var dgrcompoNmFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.teBgtCompoId == "00000000000"){
            return cellValue;
        }
        
        var demandCont = rowObject.demandCont; 
        if(isEmpty(demandCont) == true){
            demandCont = "";
        }
        
        var encNm = encodeURIComponent(cellValue);
        var rVal = '<a href="#" class="bizdesc-nm-link" style="color:#06c;text-decoration:underline;"'
                 + ' data-te-id="'+rowObject.teBgtCompoId+'"'
                 + ' data-tebgtcompoid="'+rowObject.teBgtCompoId+'"'
                 + ' data-dgrcompoid="'+rowObject.dgrcompoId+'"'
                 + ' data-fisyear="'+(rowObject.fisYear||'')+'"'
                 + ' data-bgtdgr="'+(rowObject.bgtDgr||'')+'"'
                 + ' data-reportcd="'+(rowObject.reportCd||'010')+'"'
                 + ' data-biznm="'+encNm+'">' + cellValue + '</a><br>'
                 + '<textarea id="demandCont_'+rowObject.dgrcompoId+'" style="width:230px;ime-mode:active;height:10px;">'+demandCont+'</textarea>';
        
        return rVal;
    };
    
    var examContFormatter = function(cellValue, options, rowObject){
        if(rowObject.teBgtCompoId == "00000000000"){
            return cellValue;
        }
        
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var rVal = '';
        if(rowObject.compoLevel != 1){
            rVal = '<div>'
                + '<textarea id="examCont_'+rowObject.dgrcompoId+'" style="width:270px;ime-mode:active;height:10px;">'+cellValue+'</textarea>'
                + '</div>';
        }else{
            
            var investPlan = "";
            var styleStr = 'style="width:170px;"';
            if(isEmpty(rowObject.investPlan) == false){
                investPlan = rowObject.investPlan;
            }
            
            rVal = '<div>'
                + '<select id="reflectFg_'+rowObject.dgrcompoId+'" title="반영구분" style="width:90px;">'
                + reflectFgCreateCombo('RP003', rowObject.reflectFg)
                + '</select>'
                + '&nbsp;<input id="investPlan_'+rowObject.dgrcompoId+'" value="'+investPlan+'" maxlength="500" class="ui-state-enabled" '+styleStr +' />'+'<br>'
                + '<textarea id="examCont_'+rowObject.dgrcompoId+'" style="width:270px;ime-mode:active;height:10px;">'+cellValue+'</textarea>'
                + '</div>';
        }
        return rVal;
    };
    
    var srchValFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.teBgtCompoId == "00000000000" ){
            return cellValue;
        }
        
        var styleStr = "";
        if(rowObject.compoLevel != 1){
            styleStr = 'style="display:none;"';
        }else{
            styleStr = 'style="width:240px;ime-mode:active;height:50px;"';
        }
        
        var rVal = '<div>'
                 + '<textarea id="srchVal_'+rowObject.dgrcompoId+'" '+styleStr+'">'+cellValue+'</textarea>'
                 + '</div>';

        return rVal;
    };
    
    var colNames = ['', '구분(실-부서-세부)', '통계목', '기정액', '증감액', '전년도예산액', '산출근거식', '요구액', '산출근거식', '조정액', '검토내용', '재원정보', '공약정보', '조건검색어',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'reportCd', 'reportDetlCd', 'dgrLevel', 'teBgtCompoId', 'teBgtCompoSeq', 'compoLevel', 'demandCont', 'examCont', 'reflectFg', 'srchVal', 'investPlan'
                   ];

    var colModel = [ {name : 'edit', index : 'edit', width : 20, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:editFormatter
                        },
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 340, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:dgrcompoNmFormatter
                        },
                        {name : 'teMngMokNm', index : 'teMngMokNm', width : 100, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'preAmt', index : 'preAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'diffAmt', index : 'diffAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'preBgtAmt', index : 'preBgtAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'demandCompFormular', index : 'demandCompFormular', width : 80, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'demandBgtAmt', index : 'demandBgtAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'compFormular', index : 'compFormular', width : 80, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'bgtAmt', index : 'bgtAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'examContView', index : 'examContView', width : 290, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:examContFormatter
                        },
                        {name : 'frsces', index : 'frsces', width : 130, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'pledgeFgs', index : 'pledgeFgs', width : 70, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'srchValView', index : 'srchValView', width : 250, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:srchValFormatter
                        },
                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'reportCd', index : 'reportCd', width : 0, sortable : false, hidden : true},
                        {name : 'reportDetlCd', index : 'reportDetlCd', width : 0, sortable : false, hidden : true},
                        {name : 'dgrLevel', index : 'dgrLevel', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'compoLevel', index : 'compoLevel', width : 0, sortable : false, hidden : true},
                        {name : 'demandCont', index : 'demandCont', width : 0, sortable : false, hidden : true},
                        {name : 'examCont', index : 'examCont', width : 0, sortable : false, hidden : true},
                        {name : 'reflectFg', index : 'reflectFg', width : 0, sortable : false, hidden : true},
                        {name : 'srchVal', index : 'srchVal', width : 0, sortable : false, hidden : true},
                        {name : 'investPlan', index : 'investPlan', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 200 ? $("#mainCenter", tabObj).height() - 110 : 200;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#REPORT_WRITE010_GRD", tabObj)) == false){
            $("#REPORT_WRITE010_GRD", tabObj).setGridHeight(getGridHeight());
            $("#REPORT_WRITE010_GRD", tabObj).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 220,
        center__onresize: mainBodyResize
    });
    
    var reportWrite010Grid = $("#REPORT_WRITE010_GRD", tabObj);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        if(typeof clearDirtyRows === "function"){ clearDirtyRows(); }

        if(data.data.bgtDgr == "1"){
            colModel[3].hidden = true;
            colModel[4].hidden = false;
            colModel[5].hidden = false;
        }else{
            colModel[3].hidden = false;
            colModel[4].hidden = true;
            colModel[5].hidden = true;
        }
        
        $("#REPORT_WRITE010_GRD", tabObj).jqGrid('GridUnload');
        reportWrite010Grid = $("#REPORT_WRITE010_GRD", tabObj);
        reportWrite010Grid.csTreeGrid({
            datastr : data,
            height : getGridHeight(),
            colNames : colNames,
            colModel : colModel,
            ExpandColumn : "dgrcompoNm",
            ExpandColClick: false,
            jsonReader : {
                repeatitems : false,
                root : "dataList"
            },
            onSelectRow: function(rowId){
            },
            loadComplete: function() {
                $('textarea', tabObj).autogrow();
                $('textarea', tabObj).keyup();
                $('textarea').maxlength({max: 1000, showFeedback: false});
            }
        });

        reportWrite010Grid.jqGrid('setGroupHeaders', {
            useColSpanStyle : true,
            groupHeaders : [
               {startColumnName : 'demandCompFormular',numberOfColumns : 2, titleText : '요구'},
               {startColumnName : 'compFormular', numberOfColumns : 2, titleText : '조정'} 
            ]
        });
        
        $("#REPORT_WRITE010_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
        $("#saveBtn", $("#"+tabId)).btnChangeState(true);

        data = null;
    };
    

    var saveReportParam = null;
    
    var getSearchParam = function(){
        var reportCd = $("#condReportCd", tabObj).val();
        var reportDetlCd = $("#condReportDetlCd option:selected", tabObj).val();
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        var fisFgCd = $("#condFisFgCd option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        var deptRankFr = $("#condDeptRankFr", tabObj).val();
        var deptRankTo = $("#condDeptRankTo", tabObj).val();
        var teMngMokCdFr = $("#condTeMngMokCdFr", tabObj).val();
        var teMngMokCdTo = $("#condTeMngMokCdTo", tabObj).val();
        var srchVal = $("#condSrchVal", tabObj).val();
        var frscFgCdFr = $("#condFrscFgCdFr", tabObj).val();
        var frscFgCdTo = $("#condFrscFgCdTo", tabObj).val();
        var frscFrCdYn = "N";
        if(isEmpty(frscFgCdFr) == false || isEmpty(frscFgCdTo) == false){
            frscFrCdYn = "Y";
        }
        // 심사조서 보고항목선택(분류항목)에서 지정한 ADVNC_PROC 로 조회
        var advncProc = $("#condAdvncProc option:selected", tabObj).val();
        var amtUnit = $("#condAmtUnit", tabObj).val();
        
        var param = {reportCd : reportCd,
                reportDetlCd : reportDetlCd,
                fisYear : fisYear,
                bgtDgr : bgtDgr,
                fisFgMstCd : fisFgMstCd,
                fisFgCd : fisFgCd,
                officeCd : officeCd,
                deptRankFr : deptRankFr,
                deptRankTo : deptRankTo,
                teMngMokCdFr : teMngMokCdFr,
                teMngMokCdTo : teMngMokCdTo,
                srchVal : srchVal,
                frscFgCdFr : frscFgCdFr,
                frscFgCdTo : frscFgCdTo,
                frscFrCdYn : frscFrCdYn,
                advncProc : advncProc,
                amtUnit : amtUnit
         };
        
        saveReportParam = {};
        $.extend(saveReportParam, param);
        
        return param;
    };
    
    var doSearch = function(){
        gridScrollPosition = $("#REPORT_WRITE010_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite010Report010List.do",
            data: getSearchParam(),
            async : true,
            callBack : doSearchCallBack
        });
    };
    
    $("#searchBtn", tabObj).click(function() {
        
        gridScrollPosition = 0;
        
        doSearch();
    });

    var doCondInit = function(){
        if(!comboData){ return; }
        var reportCd = $("#condReportCd", tabObj).val();
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
        condFisFgMstCdCreateCombo(fisYear, 'T00');
        
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
        
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        condFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');
        
        condTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
        
        condFrscFgCdFrCreateCombo(fisYear, '');
        condFrscFgCdToCreateCombo(fisYear, '');

        // 분류항목(RP015) — 심사조서 보고항목선택에서 지정한 값으로 조회
        $("#condAdvncProc", tabObj).csCreatCombo(comboData, {
            id : 'advncProc',
            groupId : 'ALL',
            selectedValue : '',
            comboType : 'A',
            comboTypeValue : ''
        });
        
        $("#condDeptCdFr", tabObj).val("");
        $("#condDeptNmFr", tabObj).val("");
        $("#condDeptRankFr", tabObj).val("");
        $("#condDeptCdTo", tabObj).val("");
        $("#condDeptNmTo", tabObj).val("");
        $("#condDeptRankTo", tabObj).val("");
        $("#condSrchVal", tabObj).val("");
        updateBizDescFileBtnState();
    };
    
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });
    
    // 수정된 행만 저장 대상으로 좁히기 위한 dirty 추적
    var dirtyRowIds = {};
    var markDirtyRow = function(rowId){
        if(isEmpty(rowId) == false){
            dirtyRowIds[rowId] = true;
        }
    };
    var clearDirtyRows = function(){
        dirtyRowIds = {};
    };
    $(tabObj).off("change.report010Save input.report010Save").on("change.report010Save input.report010Save", "#REPORT_WRITE010_GRD :input", function(){
        markDirtyRow($(this).closest("tr.jqgrow").attr("id"));
    });

    var getSaveDatas = function(gridObject, gridRows){
        var saveDatas = [];
        var saveData = {};
        var rowId;
        var rowData;
        var demandCont = "";
        var examCont = "";
        var reflectFg = "";
        var srchVal = "";
        var investPlan = "";
        var rowIds = [];
        var dirtyKeys = Object.keys(dirtyRowIds);
        if(dirtyKeys.length > 0){
            rowIds = dirtyKeys;
        }else if(gridRows && gridRows.length){
            for(var r = 0; r < gridRows.length; r++){
                if(gridRows[r] && gridRows[r].id){ rowIds.push(gridRows[r].id); }
            }
        }
        for(var i = 0; i < rowIds.length; i++) {
            rowId = rowIds[i];
            if(isEmpty(rowId) == true || !$("#" + rowId, tabObj).length){ continue; }
            rowData = gridObject.getRowData(rowId);
            if(isEmpty(rowData.dgrcompoId) == false && rowData.teBgtCompoId != "00000000000"){
                demandCont = $('#demandCont_'+rowId, tabObj).val().trim();
                examCont = $('#examCont_'+rowId, tabObj).val().trim();
                reflectFg = $('#reflectFg_'+rowId, tabObj).val();
                srchVal = $('#srchVal_'+rowId, tabObj).val().trim();
                investPlan = $('#investPlan_'+rowId, tabObj).val();

                if(rowData.demandCont.trim() != demandCont
                        || rowData.examCont.trim() != examCont
                        || rowData.reflectFg != reflectFg
                        || rowData.srchVal.trim() != srchVal
                        || rowData.investPlan != investPlan){
                    
                    saveData = {};
                    saveData["fisYear"] = rowData.fisYear;
                    saveData["bgtDgr"] = rowData.bgtDgr;
                    saveData["reportCd"] = rowData.reportCd;
                    saveData["reportDetlCd"] = rowData.reportDetlCd;
                    saveData["teBgtCompoId"] = rowData.teBgtCompoId;
                    saveData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
                    saveData["demandCont"] = demandCont;
                    saveData["examCont"] = examCont;
                    saveData["reflectFg"] = isEmpty(reflectFg) == true ? "" : reflectFg;
                    saveData["srchVal"] = srchVal;
                    saveData["srchValYn"] = rowData.srchVal != srchVal ? "Y" : "N";
                    saveData["investPlan"] = isEmpty(investPlan) == true ? "" : investPlan;
                    if(rowData.reflectFg != reflectFg && reflectFg === "020"){
                        saveData["reflegFgYn"] = "Y";
                    }else{
                        saveData["reflegFgYn"] = "N";
                    }
                    
                    saveDatas.push(saveData);
                }
            }
        }
        
        return saveDatas;
    };

    var doSaveCallBack = function(data, saveDatas){
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });
            return;
        }

        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                // 하위전파/금액재계산이 없으면 전체 재조회 생략(체감 속도 개선)
                var needReload = false;
                if(isEmpty(saveDatas) == false){
                    for(var i = 0; i < saveDatas.length; i++){
                        if(saveDatas[i].srchValYn === "Y" || saveDatas[i].reflegFgYn === "Y"){
                            needReload = true;
                            break;
                        }
                    }
                }
                clearDirtyRows();
                if(needReload){
                    doSearch();
                }else if(isEmpty(saveDatas) == false){
                    for(var j = 0; j < saveDatas.length; j++){
                        var sd = saveDatas[j];
                        var rid = sd.teBgtCompoId;
                        if(isEmpty(rid) == true){ continue; }
                        try{
                            reportWrite010Grid.setRowData(rid, {
                                demandCont : sd.demandCont,
                                examCont : sd.examCont,
                                reflectFg : sd.reflectFg,
                                srchVal : sd.srchVal,
                                investPlan : sd.investPlan
                            });
                        }catch(e){}
                    }
                }
            }
        });
    };
    
    var doSave = function(params){
        if(!params || params.confirmData != "Y"){
            return;
        }
        
        var saveDatas = getSaveDatas(reportWrite010Grid, $("#REPORT_WRITE010_GRD", tabObj)[0].rows);
        if(isEmpty(saveDatas) == true || saveDatas.length < 1){
            $.csAlert({
                msg : "변경된 자료가 존재하지 않습니다."
            });
            return;
        }

        $.csAjaxCall({
            url : "/report/ajaxReportWrite010SaveReport010.do",
            data : {saveDatas: saveDatas},
            async : true,
            callBack : function(data){
                doSaveCallBack(data, saveDatas);
            }
        });
    };
    
    $("#saveBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        if(checkCloseYn(saveReportParam) == false){
            return;
        }
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : doSave
        });
    });

    /**
     * 조서 조회조건 실국(#condOfficeCd) 현재값.
     * native select 기준으로 읽고 hidden(#bizDescOfficeCd/Nm)에 동기화한다.
     */
    var getSelectedOffice = function(){
        var sel = tabObj.find("#condOfficeCd").get(0);
        if (!sel) {
            sel = $(".ui-tabs-panel:visible #condOfficeCd").get(0);
        }
        if (!sel) {
            sel = document.getElementById("condOfficeCd");
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
        // '전체' 옵션(value='') 또는 표시명이 전체면 미선택 처리
        if (officeCd === "" || officeCd === "null" || officeCd === "undefined" || officeNm === "전체") {
            officeCd = "";
            officeNm = "";
        }
        tabObj.find("#bizDescOfficeCd").val(officeCd);
        tabObj.find("#bizDescOfficeNm").val(officeNm);
        return { officeCd: officeCd, officeNm: officeNm };
    };

    /** 회계년도·예산차수·실국 선택 시 사업설명서불러오기 활성화 */
    var getBizDescFilter = function(){
        var office = getSelectedOffice();
        var fisYear = String(tabObj.find("#condFisYear").val() || "").replace(/^\s+|\s+$/g, "");
        var bgtDgr = String(tabObj.find("#condBgtDgr").val() || "").replace(/^\s+|\s+$/g, "");
        if (bgtDgr === "null" || bgtDgr === "undefined") { bgtDgr = ""; }
        return {
            fisYear: fisYear,
            bgtDgr: bgtDgr,
            officeCd: office.officeCd,
            officeNm: office.officeNm,
            ready: !!(fisYear && bgtDgr && office.officeCd)
        };
    };

    var updateBizDescFileBtnState = function(){
        var f = getBizDescFilter();
        var $btn = $("#bizDescFileBtn", tabObj);
        if (!$btn.length) { return; }
        if (f.ready) {
            $btn.attr("enabledYn", "Y").removeClass("btnDisabledClass").addClass("btnClass");
        } else {
            $btn.attr("enabledYn", "N").removeClass("btnClass").addClass("btnDisabledClass");
        }
    };

    $("#bizDescFileBtn", tabObj).click(function(e){
        e.preventDefault();
        var f = getBizDescFilter();
        updateBizDescFileBtnState();
        if (typeof openDialogBizDescMatch !== "function") {
            $.csAlert({ msg: "사업설명서 화면이 준비되지 않았습니다. 메인 화면을 새로고침(F5) 후 다시 시도해 주세요." });
            return;
        }
        if (!f.ready) {
            var missing = [];
            if (!f.fisYear) { missing.push("회계년도"); }
            if (!f.bgtDgr) { missing.push("예산차수"); }
            if (!f.officeCd) { missing.push("실국"); }
            $.csAlert({
                msg: "조회조건 '" + missing.join("', '") + "'을(를) 선택한 뒤 사업설명서를 불러와 주세요.\n"
                    + "(회계년도·예산차수·실국 단위 업로드로 매칭 속도와 정확도를 높입니다)"
            });
            return;
        }
        var sp = getSearchParam();
        openDialogBizDescMatch({
            fisYear: sp.fisYear || f.fisYear,
            bgtDgr: sp.bgtDgr || f.bgtDgr,
            reportCd: sp.reportCd || "010",
            officeCd: f.officeCd,
            officeNm: f.officeNm
        });
    });

    $(tabObj).on("click", "a.bizdesc-nm-link", function(e){
        e.preventDefault();
        var $a = $(this);
        // 실국은 클릭 시점 조회조건에서 다시 읽고, 뷰 다이얼로그에서도 재확인
        var office = getSelectedOffice();
        if (typeof openDialogBizDescView !== "function") {
            $.csAlert({ msg: "사업설명서 화면이 준비되지 않았습니다. 메인 화면을 새로고침(F5) 후 다시 시도해 주세요." });
            return;
        }
        openDialogBizDescView({
            fisYear: $a.data("fisyear") || tabObj.find("#condFisYear").val(),
            bgtDgr: $a.data("bgtdgr") || tabObj.find("#condBgtDgr").val(),
            reportCd: $a.data("reportcd") || "010",
            teBgtCompoId: $a.data("tebgtcompoid"),
            dgrcompoId: $a.data("dgrcompoid"),
            reportBizNm: decodeURIComponent($a.data("biznm") || ""),
            officeCd: office.officeCd,
            officeNm: office.officeNm,
            tabId: tabId
        });
    });
    
    $("#saveFileBtn", tabObj).click(function() {
        var param = getSearchParam();
        param["fileNm"] = "경상사업심사조서";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite010SaveFile.do"
          , data: param
        });
    });
    
    $("#saveSheetBtn", tabObj).click(function() {
        var param = getSearchParam();
        param["fileNm"] = "경상사업심사조서";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite010SaveSheet.do"
          , data: param
        });
    });
    
    var doChangeCondFisYear = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
        condFisFgMstCdCreateCombo(fisYear, 'T00');
        doChageCondBgtDgr();
        doChageCondFisFgMstCd();
        
        condFrscFgCdFrCreateCombo(fisYear, '');
        condFrscFgCdToCreateCombo(fisYear, '');
    };
    
    var doChageCondBgtDgr = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
        doChangeCondOfficeCd();
        
        condTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
    };
    
    var doChageCondFisFgMstCd = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        condFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');
    };
    
    var doChangeCondOfficeCd = function(){
        $("#condDeptCdFr", tabObj).val("");
        $("#condDeptNmFr", tabObj).val("");
        $("#condDeptRankFr", tabObj).val("");
        $("#condDeptCdTo", tabObj).val("");
        $("#condDeptNmTo", tabObj).val("");
        $("#condDeptRankTo", tabObj).val("");
        updateBizDescFileBtnState();
    };
    
    $("#condFisYear", tabObj).change(function(){
        doChangeCondFisYear();
    });
    
    $("#condBgtDgr", tabObj).change(function(){
        doChageCondBgtDgr();
    });
    
    $("#condFisFgMstCd", tabObj).change(function(){
        doChageCondFisFgMstCd();
    });
    
    $("#condOfficeCd", tabObj).change(function(){
        doChangeCondOfficeCd();
    });
    
    $("#condTeMngMokCdFr", tabObj).change(function(){
        $("#condTeMngMokCdTo", tabObj).val($("#condTeMngMokCdFr option:selected", tabObj).val());
    });
    
    $("#condFrscFgCdFr", tabObj).change(function(){
        $("#condFrscFgCdTo", tabObj).val($("#condFrscFgCdFr option:selected", tabObj).val());
    });
    
    var openDialogBgtDeptSelt = function(seltFg){

        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("reportWrite010DialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("010");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    reportWrite010DialogDgrDeptSeltCallBack = function(param){
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
    
    $("#openDialogBgtDeptBtnFr", tabObj).click(function(){
        openDialogBgtDeptSelt(1);
    });
    
    $("#openDialogBgtDeptBtnTo", tabObj).click(function(){
        openDialogBgtDeptSelt(2);
    });

    var comboParam = [
                      {id : "reportDetlCd", codeId : "RP002"},
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "bgtDgr", subQueryId : "BgtDgr"},
                      {id : "fisFgMstCd", subQueryId : "FisFgMstCd"},
                      {id : "fisFgCd", subQueryId : "FisFgCd"},
                      {id : "officeCd", subQueryId : "OfficeCd", reportCd: "010"},
                      {id : "reflectFg", codeId : "RP003"},
                      {id : "teMngMokCd", subQueryId : "TeMngMokCd"},
                      {id : "frscFgCd", subQueryId : "FrscFgCd"},
                      {id : "advncProc", codeId : "RP015"}
                    ];

    var condReportDetlCdCreateCombo = function(groupId, selectedValue){
        if(!comboData){ return; }
        $("#condReportDetlCd", tabObj).csCreatCombo(comboData
                , {id: 'reportDetlCd'
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
    
    var condFisFgMstCdCreateCombo = function(groupId, selectedValue){
        $("#condFisFgMstCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgMstCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  , beforeAdd : [{groupId: groupId, code: 'T00', codeNm: "전체(기금제외)"}]
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
    
    var condTeMngMokCdFrCreateCombo = function(groupId, selectedValue){
        $("#condTeMngMokCdFr", tabObj).csCreatCombo(comboData
                , {id: 'teMngMokCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condTeMngMokCdToCreateCombo = function(groupId, selectedValue){
        $("#condTeMngMokCdTo", tabObj).csCreatCombo(comboData
                , {id: 'teMngMokCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condFrscFgCdFrCreateCombo = function(groupId, selectedValue){
        $("#condFrscFgCdFr", tabObj).csCreatCombo(comboData
                , {id: 'frscFgCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condFrscFgCdToCreateCombo = function(groupId, selectedValue){
        $("#condFrscFgCdTo", tabObj).csCreatCombo(comboData
                , {id: 'frscFgCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
        
    var reflectFgCreateCombo = function(groupId, selectedValue){
        return getCsComboStr(comboData
                , {id: 'reflectFg'
                    , groupId: groupId
                    , selectedValue: selectedValue
                    , comboType: 'S'
                    , comboTypeValue: ''
                    });
    };
    
    setTimeout(function(){
        comboData = jQuery.csComboAjaxCall(comboParam);
        doCondInit();
        updateBizDescFileBtnState();
    }, 0);
});
