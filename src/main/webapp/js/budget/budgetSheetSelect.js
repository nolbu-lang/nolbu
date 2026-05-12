$(document).ready(function() {
    var tabId = _budgetSheetSelectTabId;
    var tabObj = $("#"+tabId);
    var gridScrollPosition = 0;
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        if(rowObject.sel010Yn == "Y"){
            return ' style="color:#0000FF"';
        }

        if(rowObject.sel020Yn == "Y"){
            return ' style="color:#FF0000"';
        }

        if(rowObject.sel030Yn == "Y"){
            return ' style="color:#00B8B8"';
        }

        if(rowObject.seletcYn == "Y"){
            return ' style="color:#FF9900"';
        }

        if(rowObject.selSheetYn == "Y"){
            return ' style="color:#FF99FF"';
        }
        
        return '';
    };
    
    var colNames = ['', '구분(회계-실-부서-세부-통계목)', '기정액', '산출근거식', '증감액', '예산액', '산출근거식', '증감액', '예산액', '재원정보', '선택정보',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'dgrLevel', 'teBgtCompoSeq', 'existYn'
                   ];
    
    var colModel = [ 
                        {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, formatter:'checkbox', editoptions:{value:'Y:N'}, formatoptions:{disabled:false}},
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 250, sortable : false, fixed : true, align : 'left',
                            cellattr: myCellattr
                        },
                        {name : 'preAmt', index : 'preAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'demandCompFormular', index : 'demandCompFormular', width : 120, sortable : false, fixed : true, align : 'left'},
                        {name : 'demandDiffAmt', index : 'demandDiffAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'demandBgtAmt', index : 'demandBgtAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'compFormular', index : 'compFormular', width : 120, sortable : false, fixed : true, align : 'left'},
                        {name : 'diffAmt', index : 'diffAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'bgtAmt', index : 'bgtAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'frsces', index : 'frsces', width : 250, sortable : false, fixed : true, align : 'left'},
                        {name : 'selNames', index : 'selNames', width : 250, sortable : false, fixed : true, align : 'left'},
                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key: true },
                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true},
                        {name : 'dgrLevel', index : 'dgrLevel', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'existYn', index : 'existYn', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 200 ? $("#mainCenter", tabObj).height() - 110 : 200;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#BUDGET_SHEET_SELECT_GRD", $("#"+tabId))) == false){
            $("#BUDGET_SHEET_SELECT_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#BUDGET_SHEET_SELECT_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 220,
        center__onresize: mainBodyResize
    });
    
    var saveSheetParam = {
            sheetCd: "", 
            sheetDetlCd: "", 
            fisYear: "", 
            bgtDgr: "", 
            fisFgMstCd: "", 
            fisFgCd: "", 
            officeCd: "", 
            deptRankFr: "", 
            deptRankTo: "",
            teMngMokCdFr: "",
            teMngMokCdTo: "",
            frscFgCdFr: "",
            frscFgCdTo: "",
            frscFrCdYn: "",
            minusDiffAmtYn: "",
            amtUnit: "1"
    };
    
    var budgetSheetSelectGrid = $("#BUDGET_SHEET_SELECT_GRD", tabObj);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        $("#BUDGET_SHEET_SELECT_GRD", tabObj).jqGrid('GridUnload');
        budgetSheetSelectGrid = $("#BUDGET_SHEET_SELECT_GRD", tabObj);
        budgetSheetSelectGrid.csTreeGrid({
            datastr : data,
            height : getGridHeight(),
            colNames : colNames,
            colModel : colModel,
            ExpandColumn : "dgrcompoNm",
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
                        
                        setTreeGridChecked(e, budgetSheetSelectGrid, $("#BUDGET_SHEET_SELECT_GRD", tabObj)[0].rows, 'dgrLevel');
                        setUpTreeGridCheck(budgetSheetSelectGrid, checkedRowId, 'upDgrcompoId');
                    });
                }
            }
        });

        budgetSheetSelectGrid.jqGrid('setGroupHeaders', {
            useColSpanStyle : true,
            groupHeaders : [
               {startColumnName : 'selYn', numberOfColumns : 2, titleText : '구분'}, 
               {startColumnName : 'demandCompFormular',numberOfColumns : 3, titleText : '요구'},
               {startColumnName : 'compFormular', numberOfColumns : 3, titleText : '조정'} 
            ]
        });
        
        $("#BUDGET_SHEET_SELECT_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
        $("#saveBtn", $("#"+tabId)).btnChangeState(true);
        $("#selectAllBtn", $("#"+tabId)).btnChangeState(true);
        $("#unSelectAllBtn", $("#"+tabId)).btnChangeState(true);

        data = null;
    };
    
    var doSearch = function(){
        var sheetCd = $("#condSheetCd option:selected", tabObj).val();
        var sheetDetlCd = $("#condSheetDetlCd option:selected", tabObj).val();
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        var fisFgCd = $("#condFisFgCd option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        var deptRankFr = $("#condDeptRankFr", tabObj).val();
        var deptRankTo = $("#condDeptRankTo", tabObj).val();
        var teMngMokCdFr = $("#condTeMngMokCdFr", tabObj).val();
        var teMngMokCdTo = $("#condTeMngMokCdTo", tabObj).val();
        var frscFgCdFr = $("#condFrscFgCdFr", tabObj).val();
        var frscFgCdTo = $("#condFrscFgCdTo", tabObj).val();
        var frscFrCdYn = "N";
        if(isEmpty(frscFgCdFr) == false || isEmpty(frscFgCdTo) == false){
            frscFrCdYn = "Y";
        }
        
        var minusDiffAmtYn = "N";
        if(sheetCd == "TI0"){
            minusDiffAmtYn = "Y";
        }
        
        var amtUnit = $("#condAmtUnit", tabObj).val();
        
        gridScrollPosition = $("#BUDGET_SHEET_SELECT_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        saveSheetParam.sheetCd = sheetCd;
        saveSheetParam.sheetDetlCd = sheetDetlCd;
        saveSheetParam.fisYear = fisYear;
        saveSheetParam.bgtDgr = bgtDgr;
        saveSheetParam.fisFgMstCd = fisFgMstCd;
        saveSheetParam.fisFgCd = fisFgCd;
        saveSheetParam.officeCd = officeCd;
        saveSheetParam.deptRankFr = deptRankFr;
        saveSheetParam.deptRankTo = deptRankTo;
        saveSheetParam.teMngMokCdFr = teMngMokCdFr;
        saveSheetParam.teMngMokCdTo = teMngMokCdTo;
        saveSheetParam.frscFgCdFr = frscFgCdFr;
        saveSheetParam.frscFgCdTo = frscFgCdTo;
        saveSheetParam.frscFrCdYn = frscFrCdYn;
        saveSheetParam.minusDiffAmtYn = minusDiffAmtYn;
        saveSheetParam.amtUnit = amtUnit;
        
        $.csAjaxCall({
            url : "/budget/ajaxBudgetSheetSelectDgrCompoList.do",
            data: {sheetCd : sheetCd,
                sheetDetlCd : sheetDetlCd,
                   fisYear : fisYear,
                   bgtDgr : bgtDgr,
                   fisFgMstCd : fisFgMstCd,
                   fisFgCd : fisFgCd,
                   officeCd : officeCd,
                   deptRankFr : deptRankFr,
                   deptRankTo : deptRankTo,
                   teMngMokCdFr : teMngMokCdFr,
                   teMngMokCdTo : teMngMokCdTo,
                   frscFgCdFr : frscFgCdFr,
                   frscFgCdTo : frscFgCdTo,
                   frscFrCdYn : frscFrCdYn,
                   minusDiffAmtYn : minusDiffAmtYn,
                   amtUnit : amtUnit
            },
            async : true,
            callBack : doSearchCallBack
        });
    };
    
    $("#searchBtn", tabObj).click(function() {
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        if(fisFgMstCd == "100" && isEmpty(officeCd) == true){
            $.csAlert({
                msg : "일반회계는 실국을 선택하셔야 합니다.",
                callBack : function() {
                    $("#condOfficeCd", tabObj).focus();
                }
            });
            
            return;
        }
        
        gridScrollPosition = 0;
        
        doSearch();
    });

    var doCondInit = function(){
        $("#condSheetCd", tabObj).csCreatCombo(comboData, {
            id : 'sheetCd',
            groupId : 'ALL',
            selectedValue : '',
            comboType : '',
            comboTypeValue : ''
        });
        
        var sheetCd = $("#condSheetCd option:selected", tabObj).val();
        condSheetDetlCdCreateCombo(sheetCd, '');
        
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
        
        condTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
        
        condFrscFgCdFrCreateCombo(fisYear, '');
        condFrscFgCdToCreateCombo(fisYear, '');
        
        $("#condDeptCdFr", tabObj).val("");
        $("#condDeptNmFr", tabObj).val("");
        $("#condDeptRankFr", tabObj).val("");
        $("#condDeptCdTo", tabObj).val("");
        $("#condDeptNmTo", tabObj).val("");
        $("#condDeptRankTo", tabObj).val("");
        $("#condTeMngMokCdFr", tabObj).val("");
        $("#condTeMngMokCdTo", tabObj).val("");
        $("#condFrscFgCdFr", tabObj).val("");
        $("#condFrscFgCdTo", tabObj).val("");
        
        saveSheetParam.sheetCd = "";
        saveSheetParam.sheetDetlCd = "";
        saveSheetParam.fisYear = "";
        saveSheetParam.bgtDgr = "";
        saveSheetParam.fisFgMstCd = "";
        saveSheetParam.fisFgCd = "";
        saveSheetParam.officeCd = "";
        saveSheetParam.deptRankFr = "";
        saveSheetParam.deptRankTo = "";
        saveSheetParam.teMngMokCdFr = "";
        saveSheetParam.teMngMokCdTo = "";
        saveSheetParam.frscFgCdFr = "";
        saveSheetParam.frscFgCdTo = "";
        saveSheetParam.frscFrCdYn = "";
        saveSheetParam.minusDiffAmtYn = "";
    };
    
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });
    
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
                selectedData["teBgtCompoId"] = rowData.teBgtCompoId;
                selectedData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
                selectedDatas.push(selectedData);
                cnt++;
            }
        }
        
        return selectedDatas;
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
        
        if(isEmpty(saveSheetParam) == true || isEmpty(saveSheetParam.sheetCd) == true || isEmpty(saveSheetParam.sheetDetlCd) == true){
            $.csAlert({
                msg : "집계표항목 구분 정보가 존재하지 않습니다."
            });
            
            return;
        }
        
        var selectedDatas = getSelectedData(budgetSheetSelectGrid, $("#BUDGET_SHEET_SELECT_GRD", tabObj)[0].rows);
        saveSheetParam["saveSheetDatas"] = selectedDatas;
        
        $.csAjaxCall({
            url : "/budget/ajaxBudgetSheetSelectSaveSheet.do",
            data : saveSheetParam,
            async : true,
            callBack : doSaveCallBack
        });
    };
    
    $("#saveBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        if(checkCloseYn(saveSheetParam) == false){
            return;
        }
        
        $.csConfirm({
            msg : "기존 자료 중 선택되지 않는 자료는 삭제됩니다.<br>저장하시겠습니까?",
            callBack : doSave
        });
    });
    
    var doChangeCondSheetCd = function(){
        var sheetCd = $("#condSheetCd option:selected", tabObj).val();
        condSheetDetlCdCreateCombo(sheetCd, '');
    };
    
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
    };
    
    $("#condSheetCd", tabObj).change(function(){
        doChangeCondSheetCd();
    });
    
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
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("budgetSheetSelectDialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    budgetSheetSelectDialogDgrDeptSeltCallBack = function(param){
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
    
    $("#selectAllBtn", tabObj).click(function(){
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $("#selectAllBtn", tabObj).hide();
        $("#unSelectAllBtn", tabObj).show();
        setGridCheckedAll(budgetSheetSelectGrid, $("#BUDGET_SHEET_SELECT_GRD", tabObj)[0].rows, "Y"); 
    });
    
    $("#unSelectAllBtn", tabObj).click(function(){
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $("#unSelectAllBtn", tabObj).hide();
        $("#selectAllBtn", tabObj).show();
        setGridCheckedAll(budgetSheetSelectGrid, $("#BUDGET_SHEET_SELECT_GRD", tabObj)[0].rows, "N");
    });
    
    var comboParam = [
                      {id : "sheetCd", codeId : "RT001"},
                      {id : "sheetDetlCd", codeId : "RT002"},
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "bgtDgr", subQueryId : "BgtDgr"},
                      {id : "fisFgMstCd", subQueryId : "FisFgMstCd"},
                      {id : "fisFgCd", subQueryId : "FisFgCd"},
                      {id : "officeCd", subQueryId : "OfficeCd"},
                      {id : "teMngMokCd", subQueryId : "TeMngMokCd"},
                      {id : "frscFgCd", subQueryId : "FrscFgCd"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);
        
    var condSheetDetlCdCreateCombo = function(groupId, selectedValue){
        $("#condSheetDetlCd", tabObj).csCreatCombo(comboData
                , {id: 'sheetDetlCd'
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
                  , comboType: ''
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
    
    doCondInit();
});
