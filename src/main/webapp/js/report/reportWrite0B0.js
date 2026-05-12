$(document).ready(function() {
    var tabId = _reportWrite0B0TabId;
    var tabObj = $("#"+tabId);
    var gridScrollPosition = 0;
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        if(rowObject.teBgtCompoId != "00000000000"){
            return ' style="vertical-align: top;"';
        }
    };
    
    reportWirte0B0DialogDgrcompoModifyCallBackFunction = function(param){
        var rowId = param.dgrcompoId;
        if(isEmpty(rowId) == true){
            return;
        }
        
        reportWrite0B0Grid.jqGrid('setRowData', rowId, param);
        
        var dgrcompoNmView = param.dgrcompoNmView;
        if(isEmpty(dgrcompoNmView) == true){
            dgrcompoNmView = "";
        }
        
        $("#dgrcompoNmView_" + rowId, tabObj).html(dgrcompoNmView);
    };
    
    reportWirte0B0OpenDialogDgrcompoModify = function(rowId){  
        var rowData = reportWrite0B0Grid.getRowData(rowId);
        
        $("#dialogDgrcompoModifyCallBackFunction", $("#dialogDgrcompoModifyDiv")).val("reportWirte0B0DialogDgrcompoModifyCallBackFunction");
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
        
        var rVal = '<a href="javascript:reportWirte0B0OpenDialogDgrcompoModify(\''+options.rowId+'\');"><span class="ui-icon ui-icon-pencil"></span></a>';
        
        return rVal;
    };
    
    var dgrcompoNmFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.teBgtCompoId == "00000000000"){
            return cellValue;
        }
        
        var bizNm = rowObject.bizNm; 
        if(isEmpty(bizNm) == true){
            bizNm = "";
        }
        
        var rVal = '<span id="dgrcompoNmView_'+rowObject.dgrcompoId+'" >' + cellValue + '</span><br>'
                 + '<textarea id="bizNm_'+rowObject.dgrcompoId+'" style="width:200px;ime-mode:active;height:40px;">'+bizNm+'</textarea>';
        
        return rVal;
    };
    
    var examContFormatter = function(cellValue, options, rowObject){
        if(rowObject.teBgtCompoId == "00000000000"){
            return "";
        }
        
        if(isEmpty(rowObject.frscAmt) == true || Number(rowObject.frscAmt) == 0){
            return "";
        }
        
        var rVal = '국비 ' + addCommaStr(rowObject.frscAmt) + ' 포함';

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
    
    var bizAmountFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.teBgtCompoId == "00000000000" ){
            return cellValue;
        }
        
        var rVal = '<textarea id="bizAmount_'+rowObject.dgrcompoId+'" style="width:130px;ime-mode:active;height:60px;">'+cellValue+'</textarea>';
        
        return rVal;
    };
    
    var bizPeriodFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.teBgtCompoId == "00000000000" ){
            return cellValue;
        }
        
        var rVal = '<textarea id="bizPeriod_'+rowObject.dgrcompoId+'" style="width:100px;ime-mode:active;height:60px;">'+cellValue+'</textarea>';
        
        return rVal;
    };
    
    var totBizAmtFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var readOnlyStr = "";
        var classStr = '';
        if(rowObject.teBgtCompoId == "00000000000" ){
            readOnlyStr = "readonly";
            classStr = 'class="amtInput0B0 ui-state-disabled"';
        }else{
            classStr = 'class="amtInput0B0 ui-state-enabled"';
        }
        
        var rVal = '<input id="totBizAmt_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" '+classStr+' '+readOnlyStr+' />';

        return rVal;
    };
    
    var preBizAmtFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var readOnlyStr = "";
        var classStr = '';
        if(rowObject.teBgtCompoId == "00000000000" ){
            readOnlyStr = "readonly";
            classStr = 'class="amtInput0B0 ui-state-disabled"';
        }else{
            classStr = 'class="amtInput0B0 ui-state-enabled"';
        }
        
        var rVal = '<input id="preBizAmt_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" '+classStr+' '+readOnlyStr+' />';
        return rVal;
    };
    
    var colNames = [/*'', */'', '사업명(조서사업명)', '사업량', '사업기간', '총사업비', '기투자', '기정액', '추경예산안', '예산액', '비고', '재원정보', '조건검색어',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'reportCd', 'reportDetlCd', 'dgrLevel', 'teBgtCompoId', 'teBgtCompoSeq', 'compoLevel', 'demandCont', 'examCont', 'reflectFg', 'srchVal', 
                    'bizNm', 'bizAmount', 'bizPeriod', 'totBizAmt', 'preBizAmt'
                   ];

    var colModel = [ /*{name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, formatter:'checkbox', editoptions:{value:'Y:N'}, formatoptions:{disabled:false}},*/
                        {name : 'edit', index : 'edit', width : 20, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:editFormatter
                        },
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 260, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:dgrcompoNmFormatter
                        },
                        {name : 'bizAmountView', index : 'bizAmount', width : 150, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : bizAmountFormatter
                        },
                        {name : 'bizPeriodView', index : 'bizPeriod', width : 120, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : bizPeriodFormatter
                        },
                        {name : 'totBizAmtView', index : 'totBizAmtView', width : 120, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:totBizAmtFormatter
                        },
                        {name : 'preBizAmtView', index : 'totBizAmtView', width : 120, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:preBizAmtFormatter
                        },
                        {name : 'preAmt', index : 'preAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'addBgtAmt', index : 'addBgtAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'bgtAmt', index : 'bgtAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'examContView', index : 'examContView', width : 100, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:examContFormatter
                        },
                        {name : 'frsces', index : 'frsces', width : 130, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
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
                        {name : 'bizNm', index : 'bizNm', width : 0, sortable : false, hidden : true},
                        {name : 'bizAmount', index : 'bizAmount', width : 0, sortable : false, hidden : true},
                        {name : 'bizPeriod', index : 'bizPeriod', width : 0, sortable : false, hidden : true},
                        {name : 'totBizAmt', index : 'totBizAmt', width : 0, sortable : false, hidden : true},
                        {name : 'preBizAmt', index : 'preBizAmt', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 250 ? $("#mainCenter", tabObj).height() - 110 : 250;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#REPORT_WRITE0B0_GRD", $("#"+tabId))) == false){
            $("#REPORT_WRITE0B0_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#REPORT_WRITE0B0_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 225,
        center__onresize: mainBodyResize
    });
    
    var reportWrite0B0Grid = $("#REPORT_WRITE0B0_GRD", tabObj);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        if(data.data.bgtDgr == "1"){
            colModel[6].hidden = true;
            colModel[7].hidden = true;
        }else{
            colModel[6].hidden = false;
            colModel[7].hidden = false;
        }

        $("#REPORT_WRITE0B0_GRD", tabObj).jqGrid('GridUnload');
        reportWrite0B0Grid = $("#REPORT_WRITE0B0_GRD", tabObj);
        reportWrite0B0Grid.csTreeGrid({
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
                var iColSelYn = getColumnIndexByName ($(this), 'selYn');
                var rows = this.rows;
                for(var i = 0; i < rows.length; i++) {
                    $(rows[i].cells[iColSelYn]).click(function (e) {
                        var checkedRowId = $(e.target).closest('tr')[0].id;
                        
                        setTreeGridChecked(e, reportWrite0B0Grid, $("#REPORT_WRITE0B0_GRD", tabObj)[0].rows, 'dgrLevel');
                        setUpTreeGridCheck(reportWrite0B0Grid, checkedRowId, 'upDgrcompoId');
                    });
                }
                
                $(".amtInput0B0.ui-state-enabled", tabObj).autoNumeric({aPad: false, vMax:'99999999999999999'});
                $(".amtInput0B0.ui-state-enabled", tabObj).click(function () {
                    $(this).select();
                });
                
                $("textarea[id^='bizNm_']").maxlength({max: 500, showFeedback: false});
                $("textarea[id^='bizAmount_']").maxlength({max: 500, showFeedback: false});
                $("textarea[id^='bizPeriod_']").maxlength({max: 500, showFeedback: false});
            }
        });
        
        $("#REPORT_WRITE0B0_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
        $("#saveBtn", $("#"+tabId)).btnChangeState(true);

        data = null;
    };
    
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
        var srchVal = $("#condSrchVal", tabObj).val();
        var frscFgCdFr = $("#condFrscFgCdFr", tabObj).val();
        var frscFgCdTo = $("#condFrscFgCdTo", tabObj).val();
        var frscFrCdYn = "N";
        if(isEmpty(frscFgCdFr) == false || isEmpty(frscFgCdTo) == false){
            frscFrCdYn = "Y";
        }
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
                srchVal : srchVal,
                frscFgCdFr : frscFgCdFr,
                frscFgCdTo : frscFgCdTo,
                frscFrCdYn : frscFrCdYn,
                amtUnit : amtUnit
         };
        
        return param;
    };
    
    var doSearch = function(){

        gridScrollPosition = $("#REPORT_WRITE0B0_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite0B0Report0B0List.do",
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
        condFisFgMstCdCreateCombo(fisYear, '');
        
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');

        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        condFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');
        
        condFrscFgCdFrCreateCombo(fisYear, '');
        condFrscFgCdToCreateCombo(fisYear, '');
        
        $("#condDeptCdFr", tabObj).val("");
        $("#condDeptNmFr", tabObj).val("");
        $("#condDeptRankFr", tabObj).val("");
        $("#condDeptCdTo", tabObj).val("");
        $("#condDeptNmTo", tabObj).val("");
        $("#condDeptRankTo", tabObj).val("");
        $("#condSrchVal", tabObj).val("");
        
        $("#insertBtn", tabObj).btnChangeState(true);
        //$("#deleteBtn", tabObj).btnChangeState(true);
    };
    
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });
    
    var getSaveDatas = function(gridObject, gridRows){
        var saveDatas = [];
        var saveData = {};
        var rowId;
        var rowData;
        //var demandCont = "";
        //var examCont = "";
        //var reflectFg = "";
        var srchVal = "";
        var bizNm = "";
        var bizAmount = "";
        var bizPeriod = "";
        var totBizAmt = "";
        var preBizAmt = "";
        //var reportSortSeq = "";
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);
            if(isEmpty(rowData.dgrcompoId) == false && rowData.teBgtCompoId != "00000000000"){
                bizNm = $('#bizNm_'+rowId, tabObj).val();
                bizAmount = $('#bizAmount_'+rowId, tabObj).val();
                bizPeriod = $('#bizPeriod_'+rowId, tabObj).val();
                totBizAmt = $('#totBizAmt_'+rowId, tabObj).val().replaceAll(",", "");
                preBizAmt = $('#preBizAmt_'+rowId, tabObj).val().replaceAll(",", "");
                srchVal = $('#srchVal_'+rowId, tabObj).val();

                if(rowData.bizNm != bizNm
                        || rowData.bizAmount != bizAmount
                        || rowData.bizPeriod != bizPeriod
                        || rowData.totBizAmt != totBizAmt
                        || rowData.preBizAmt != preBizAmt
                        || rowData.srchVal != srchVal){
                    
                    saveData = {};
                    saveData["fisYear"] = rowData.fisYear;
                    saveData["bgtDgr"] = rowData.bgtDgr;
                    saveData["reportCd"] = rowData.reportCd;
                    saveData["reportDetlCd"] = rowData.reportDetlCd;
                    saveData["teBgtCompoId"] = rowData.teBgtCompoId;
                    saveData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
                    saveData["demandCont"] = "";
                    saveData["examCont"] = "";
                    saveData["reflectFg"] = "";
                    saveData["srchVal"] = srchVal;
                    saveData["srchValYn"] = rowData.srchVal != srchVal ? "Y" : "N";
                    saveData["bizNm"] = bizNm;
                    saveData["bizAmount"] = bizAmount;
                    saveData["bizPeriod"] = bizPeriod;
                    saveData["totBizAmt"] = totBizAmt;
                    saveData["preBizAmt"] = preBizAmt;
                    saveDatas.push(saveData);
                }
            }
        }
        
        return saveDatas;
    };

    var doSaveCallBack = function(data){
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                doSearch();
            }
        });
    };
    
    var doSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }
        
        var saveDatas = getSaveDatas(reportWrite0B0Grid, $("#REPORT_WRITE0B0_GRD", tabObj)[0].rows);
        if(isEmpty(saveDatas) == true || saveDatas.length < 1){
            $.csAlert({
                msg : "변경된 자료가 존재하지 않습니다."
            });
            
            return;
        }
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite0B0SaveReport0B0.do",
            data : {saveDatas: saveDatas,
                    amtUnit:$("#condAmtUnit", tabObj).val()
            },
            async : true,
            callBack : doSaveCallBack
        });
    };
    
    $("#saveBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : doSave
        });
    });

    var doInsertCallBack = function(data){
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                doSearch();
            }
        });
    };
    
    var doInsert = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var param = getSearchParam();
        param["check030Amt"] = "2000000000";
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite0B0InsertReport0B0.do"
                , data: param
                , async : true
                , callBack : doInsertCallBack
        });
    };
    
    $("#insertBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        /*
        if(_mainNorthPowGrCd != "BC001" && _mainNorthPowGrCd != "BC002"){
            $.csAlert({
                msg : "관리자만 가져오기 하실 수 있습니다."
            });
            
            return;
        }
		*/
        
        $.csConfirm({
            msg : "주요사업내용을 가져오시겠습니까?",
            callBack : doInsert
        });
    });
    
    /*
    var getSelectedData = function(gridObject, gridRows){
        var selectedDatas = [];
        var selectedData = {};
        var rowId;
        var rowData;
        var cnt = 0;
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);

            if(rowData.selYn == "Y" && rowData.teBgtCompoId != "00000000000"){
                selectedData = {};
                selectedData["fisYear"] = rowData.fisYear;
                selectedData["bgtDgr"] = rowData.bgtDgr;
                selectedData["reportCd"] = rowData.reportCd;
                selectedData["reportDetlCd"] = rowData.reportDetlCd;
                selectedData["teBgtCompoId"] = rowData.teBgtCompoId;
                selectedData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
                selectedDatas.push(selectedData);
                cnt++;
            }
        }
        
        return selectedDatas;
    };
    */

    /*
    var doDeleteCallBack = function(data){
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                doSearch();
            }
        });
    };
    */
    
    /*
    var doDelete = function(params){
        if(params.confirmData != "Y"){
            return;
        }
        
        var selectedDatas = getSelectedData(reportWrite0B0Grid, $("#REPORT_WRITE0B0_GRD", tabObj)[0].rows);
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite0B0DeleteReport0B0.do",
            data : {deleteReportDatas : selectedDatas},
            async : true,
            callBack : doDeleteCallBack
        });
    };
    */
    
    /*
    $("#deleteBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $.csConfirm({
            msg : "선택한 사업을 삭제하시겠습니까?",
            callBack : doDelete
        });
    });
    */
    
    $("#saveFileBtn", tabObj).click(function() {
        var param = getSearchParam();
        param["fileNm"] = "주요사업내용";
        param["amtUnit"] = "1000000";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite0B0SaveFile.do"
          , data: param
        });
    });
    
    $("#saveSheetBtn", tabObj).click(function() {
        var param = getSearchParam();
        param["fileNm"] = "주요사업내용";
        param["amtUnit"] = "1000000";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite0B0SaveSheet.do"
          , data: param
        });
    });
    
    var doChangeCondFisYear = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
        condFisFgMstCdCreateCombo(fisYear, '');
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
    
    $("#condFrscFgCdFr", tabObj).change(function(){
        $("#condFrscFgCdTo", tabObj).val($("#condFrscFgCdFr option:selected", tabObj).val());
    });
    
    var openDialogBgtDeptSelt = function(seltFg){

        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("reportWrite0B0DialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("0B0");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    reportWrite0B0DialogDgrDeptSeltCallBack = function(param){
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
                      {id : "officeCd", subQueryId : "OfficeCd", reportCd: "0B0"},
                      {id : "frscFgCd", subQueryId : "FrscFgCd"},
                      {id : "reflectFg", codeId : "RP003"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);
        
    var condReportDetlCdCreateCombo = function(groupId, selectedValue){
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
    
    doCondInit();
});
