$(document).ready(function() {
    var tabId = _budgetSelectTabId;
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
    
    var report030FgFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.teBgtCompoId == "00000000000" ){
            return cellValue;
        }
        
        var rVal = '<div>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn031_'+rowObject.dgrcompoId+'" value="Y" class="chkBudgetSelect" onclick="javascript:budgetSelectCheckYn(\'031\', \''+rowObject.dgrcompoId+'\');" style="margin-top: 5px;" '+(rowObject.checkYn031 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">국고</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn032_'+rowObject.dgrcompoId+'" value="Y" class="chkBudgetSelect" onclick="javascript:budgetSelectCheckYn(\'032\', \''+rowObject.dgrcompoId+'\');" style="margin-top: 5px;" '+(rowObject.checkYn032 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">균특</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn033_'+rowObject.dgrcompoId+'" value="Y" class="chkBudgetSelect" onclick="javascript:budgetSelectCheckYn(\'033\', \''+rowObject.dgrcompoId+'\');" style="margin-top: 5px;" '+(rowObject.checkYn033 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">기금</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYnTf1_'+rowObject.dgrcompoId+'" value="Y" class="chkBudgetSelect" onclick="javascript:budgetSelectCheckYn(\'TF1\', \''+rowObject.dgrcompoId+'\');" style="margin-top: 5px;" '+(rowObject.checkYnTf1 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">매칭펀드</span>'
                 + '</div>';

        return rVal;
    };
    
    budgetSelectCheckYn = function(fg, id){
        var checkYn = $('#checkYn'+fg+'_'+id, tabObj).is(':checked') == true ? "Y" : "N";
        if(checkYn != "Y"){
            return;
        }
        
        if(fg == '031'){
            $('#checkYn032'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn033'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYnTf1'+'_'+id, tabObj).removeAttr('checked');
        }else if(fg == '032'){
            $('#checkYn031'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn033'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYnTf1'+'_'+id, tabObj).removeAttr('checked');
        }else if(fg == '033'){
            $('#checkYn031'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn032'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYnTf1'+'_'+id, tabObj).removeAttr('checked');
        }else{
            $('#checkYn031'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn032'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn033'+'_'+id, tabObj).removeAttr('checked');
        }
    };
    
    var colNames = ['', '구분(회계-실-부서-세부-통계목)', '기정액', '산출근거식', '증감액', '예산액', '산출근거식', '증감액', '예산액', '재원정보', '선택정보', '국고보조사업(재원)',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'dgrLevel', 'teBgtCompoSeq', 'existYn', 'sel010Yn', 'sel020Yn',
                    'sel040Yn', 'sel050Yn', 'sel055Yn', 'sel060Yn', 'sel090Yn',
                    'checkYn031', 'checkYn032', 'checkYn033', 'checkYnTf1'
                   ];
    
    var colModel = [ 
                        {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, formatter:'checkbox', editoptions:{value:'Y:N'}, formatoptions:{disabled:false}},
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 500, sortable : false, fixed : true, align : 'left',
                            cellattr: myCellattr
                        },
                        {name : 'preAmt', index : 'preAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'demandCompFormular', index : 'demandCompFormular', width : 120, sortable : false, fixed : true, align : 'left'},
                        {name : 'demandDiffAmt', index : 'demandDiffAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'demandBgtAmt', index : 'demandBgtAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'compFormular', index : 'compFormular', width : 120, sortable : false, fixed : true, align : 'left'},
                        {name : 'diffAmt', index : 'diffAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'bgtAmt', index : 'bgtAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'frsces', index : 'frsces', width : 130, sortable : false, fixed : true, align : 'left'},
                        {name : 'selNames', index : 'selNames', width : 200, sortable : false, fixed : true, align : 'left'},
                        {name : 'report030FgView', index : 'report030FgView', width : 200, sortable : false, hidden : true, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:report030FgFormatter
                        },
                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key: true },
                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true},
                        {name : 'dgrLevel', index : 'dgrLevel', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'existYn', index : 'existYn', width : 0, sortable : false, hidden : true},
                        {name : 'sel010Yn', index : 'sel010Yn', width : 0, sortable : false, hidden : true},
                        {name : 'sel020Yn', index : 'sel020Yn', width : 0, sortable : false, hidden : true},
                        {name : 'sel040Yn', index : 'sel020Yn', width : 0, sortable : false, hidden : true},
                        {name : 'sel050Yn', index : 'sel020Yn', width : 0, sortable : false, hidden : true},
                        {name : 'sel055Yn', index : 'sel020Yn', width : 0, sortable : false, hidden : true},
                        {name : 'sel060Yn', index : 'sel020Yn', width : 0, sortable : false, hidden : true},
                        {name : 'sel090Yn', index : 'sel020Yn', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn031', index : 'checkYn031', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn032', index : 'checkYn032', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn033', index : 'checkYn033', width : 0, sortable : false, hidden : true},
                        {name : 'checkYnTf1', index : 'checkYnTf1', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 200 ? $("#mainCenter", tabObj).height() - 110 : 200;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#BUDGET_SELECT_GRD", $("#"+tabId))) == false){
            $("#BUDGET_SELECT_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#BUDGET_SELECT_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 220,
        center__onresize: mainBodyResize
    });
    
    var saveReportParam = {
            reportCd: "", 
            reportDetlCd: "", 
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
            amtUnit: "1",
            orderYmdSeq: ""
    };
    
    var budgetSelectGrid = $("#BUDGET_SELECT_GRD", tabObj);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        $("#saveBtn", $("#"+tabId)).btnChangeState(false);        

        if(saveReportParam.reportCd == "020" && (saveReportParam.reportDetlCd == "021" || saveReportParam.reportDetlCd == "022" || saveReportParam.reportDetlCd == "023")){
            colModel[11].hidden = false;
        }else{
            colModel[11].hidden = true;
        }

        $("#BUDGET_SELECT_GRD", tabObj).jqGrid('GridUnload');
        budgetSelectGrid = $("#BUDGET_SELECT_GRD", tabObj);
        budgetSelectGrid.csTreeGrid({
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
                        
                        setTreeGridChecked(e, budgetSelectGrid, $("#BUDGET_SELECT_GRD", tabObj)[0].rows, 'dgrLevel');
                        setUpTreeGridCheck(budgetSelectGrid, checkedRowId, 'upDgrcompoId');
                    });
                }
            }
        });

        budgetSelectGrid.jqGrid('setGroupHeaders', {
            useColSpanStyle : true,
            groupHeaders : [
               {startColumnName : 'selYn', numberOfColumns : 2, titleText : '구분'}, 
               {startColumnName : 'demandCompFormular',numberOfColumns : 3, titleText : '요구'},
               {startColumnName : 'compFormular', numberOfColumns : 3, titleText : '조정'} 
            ]
        });
        
        $("#BUDGET_SELECT_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
        $("#saveBtn", $("#"+tabId)).btnChangeState(true);
        $("#selectAllBtn", $("#"+tabId)).btnChangeState(true);
        $("#unSelectAllBtn", $("#"+tabId)).btnChangeState(true);

        data = null;
    };
    
    var doSearch = function(){
        var reportCd = $("#condReportCd option:selected", tabObj).val();
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
        var frscFgCdFr = $("#condFrscFgCdFr", tabObj).val();
        var frscFgCdTo = $("#condFrscFgCdTo", tabObj).val();
        var frscFrCdYn = "N";
        if(isEmpty(frscFgCdFr) == false || isEmpty(frscFgCdTo) == false){
            frscFrCdYn = "Y";
        }
        var amtUnit = $("#condAmtUnit", tabObj).val();
        var orderYmdSeq = $("#condOrderYmdSeq option:selected", tabObj).val();

        gridScrollPosition = $("#BUDGET_SELECT_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        saveReportParam.reportCd = reportCd;
        saveReportParam.reportDetlCd = reportDetlCd;
        saveReportParam.fisYear = fisYear;
        saveReportParam.bgtDgr = bgtDgr;
        saveReportParam.fisFgMstCd = fisFgMstCd;
        saveReportParam.fisFgCd = fisFgCd;
        saveReportParam.officeCd = officeCd;
        saveReportParam.deptRankFr = deptRankFr;
        saveReportParam.deptRankTo = deptRankTo;
        saveReportParam.teMngMokCdFr = teMngMokCdFr;
        saveReportParam.teMngMokCdTo = teMngMokCdTo;
        saveReportParam.frscFgCdFr = frscFgCdFr;
        saveReportParam.frscFgCdTo = frscFgCdTo;
        saveReportParam.frscFrCdYn = frscFrCdYn;
        saveReportParam.amtUnit = amtUnit;
        saveReportParam.orderYmdSeq = orderYmdSeq;
        
        $.csAjaxCall({
            url : "/budget/ajaxBudgetSelectDgrCompoList.do",
            data: {reportCd : reportCd,
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
                   frscFgCdFr : frscFgCdFr,
                   frscFgCdTo : frscFgCdTo,
                   frscFrCdYn : frscFrCdYn,
                   amtUnit : amtUnit,
                   orderYmdSeq : orderYmdSeq
            },
            async : true,
            callBack : doSearchCallBack
        });
    };
    
    $("#searchBtn", tabObj).click(function() {
        var reportCd = $("#condReportCd option:selected", tabObj).val();
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        var orderYmdSeq = $("#condOrderYmdSeq option:selected", tabObj).val();
        if(fisFgMstCd == "100" && isEmpty(officeCd) == true){
            $.csAlert({
                msg : "일반회계는 실국을 선택하셔야 합니다.",
                callBack : function() {
                    $("#condOfficeCd", tabObj).focus();
                }
            });
            
            return;
        }
        
        if(reportCd == "070" && isEmpty(orderYmdSeq) == true){
            $.csAlert({
                msg : "지시사항 조치계획 서식은 지시일자를 선택하셔야 합니다.",
                callBack : function() {
                    $("#condOrderYmdSeq", tabObj).focus();
                }
            });
            
            return;
        }
        
        gridScrollPosition = 0;
        
        doSearch();
    });

    var doCondInit = function(){
        $("#condReportCd", tabObj).csCreatCombo(comboData, {
            id : 'reportCd',
            groupId : 'ALL',
            selectedValue : '',
            comboType : '',
            comboTypeValue : ''
        });
        
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
        
        condTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
        
        condFrscFgCdFrCreateCombo(fisYear, '');
        condFrscFgCdToCreateCombo(fisYear, '');
        
        condOrderYmdSeqCreateCombo(fisYear + '_' + bgtDgr, '');
        
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
        $("#condOrderYmdSeq", tabObj).val("");
        
        saveReportParam.reportCd = "";
        saveReportParam.reportDetlCd = "";
        saveReportParam.fisYear = "";
        saveReportParam.bgtDgr = "";
        saveReportParam.fisFgMstCd = "";
        saveReportParam.fisFgCd = "";
        saveReportParam.officeCd = "";
        saveReportParam.deptRankFr = "";
        saveReportParam.deptRankTo = "";
        saveReportParam.teMngMokCdFr = "";
        saveReportParam.teMngMokCdTo = "";
        saveReportParam.frscFgCdFr = "";
        saveReportParam.frscFgCdTo = "";
        saveReportParam.frscFrCdYn = "";
        saveReportParam.orderYmdSeq = "";
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
                selectedData["sel010Yn"] = rowData.sel010Yn;
                selectedData["sel020Yn"] = rowData.sel020Yn;
                selectedData["sel040Yn"] = rowData.sel040Yn;
                selectedData["sel050Yn"] = rowData.sel050Yn;
                selectedData["sel055Yn"] = rowData.sel055Yn;
                selectedData["sel060Yn"] = rowData.sel060Yn;
                selectedData["sel090Yn"] = rowData.sel090Yn;
                selectedData["dgrcompoNm"] = rowData.dgrcompoNm;
                selectedData["dgrcompoId"] = rowData.dgrcompoId;
                selectedData["checkYn031"] = $('#checkYn031_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                selectedData["checkYn032"] = $('#checkYn032_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                selectedData["checkYn033"] = $('#checkYn033_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                selectedData["checkYnTf1"] = $('#checkYnTf1_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                
                selectedDatas.push(selectedData);
                cnt++;
            }
        }
        
        return selectedDatas;
    };
    
    var getSelectedData030 = function(gridObject, gridRows){
        var selectedDatas = [];
        var selectedData = {};
        var rowId;
        var rowData;
        var cnt = 0;

        var checkYn031 ="";
        var checkYn032 ="";
        var checkYn033 ="";
        var checkYnTf1 ="";
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);

            if(isEmpty(rowData.dgrcompoId) == false && rowData.teBgtCompoId != "00000000000"){
                checkYn031 = $('#checkYn031_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn032 = $('#checkYn032_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn033 = $('#checkYn033_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYnTf1 = $('#checkYnTf1_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                
                if(rowData.checkYn031 != checkYn031
                        || rowData.checkYn032 != checkYn032
                        || rowData.checkYn033 != checkYn033
                        || rowData.checkYnTf1 != checkYnTf1
                        ){
                    
                    selectedData = {};
                    selectedData["fisYear"] = rowData.fisYear;
                    selectedData["bgtDgr"] = rowData.bgtDgr;
                    selectedData["teBgtCompoId"] = rowData.teBgtCompoId;
                    selectedData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
                    selectedData["checkYn031"] = $('#checkYn031_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                    selectedData["checkYn031Yn"] = rowData.checkYn031 != checkYn031 ? "Y" : "N";
                    selectedData["checkYn032"] = $('#checkYn032_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                    selectedData["checkYn032Yn"] = rowData.checkYn032 != checkYn032 ? "Y" : "N";
                    selectedData["checkYn033"] = $('#checkYn033_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                    selectedData["checkYn033Yn"] = rowData.checkYn033 != checkYn033 ? "Y" : "N";
                    selectedData["checkYnTf1"] = $('#checkYnTf1_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                    selectedData["checkYnTf1Yn"] = rowData.checkYnTf1 != checkYnTf1 ? "Y" : "N";
                    
                    selectedDatas.push(selectedData);
                    cnt++;
                }
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
        
        if(isEmpty(saveReportParam) == true || isEmpty(saveReportParam.reportCd) == true || isEmpty(saveReportParam.reportDetlCd) == true){
            $.csAlert({
                msg : "조서구분 정보가 존재하지 않습니다."
            });
            
            return;
        }
        
        var selectedDatas = getSelectedData(budgetSelectGrid, $("#BUDGET_SELECT_GRD", tabObj)[0].rows);
        
        var temp = [];
        if(saveReportParam.reportCd == "010" 
                || saveReportParam.reportCd == "020" 
                || saveReportParam.reportCd == "040" 
                || saveReportParam.reportCd == "050" 
                || saveReportParam.reportCd == "055" 
                || saveReportParam.reportCd == "060" 
                || saveReportParam.reportCd == "090"
        ){
            for(var i = 0; i < selectedDatas.length; i++){
                temp = selectedDatas[i];
                if(temp.sel010Yn == "Y"){
                    if(saveReportParam.reportCd == "020" 
                            || saveReportParam.reportCd == "040" 
                            || saveReportParam.reportCd == "050" 
                            || saveReportParam.reportCd == "055" 
                            || saveReportParam.reportCd == "060" 
                            || saveReportParam.reportCd == "090"
                    ){
                        $.csAlert({
                            msg : "경상사업조서가 선택된 자료가 존재합니다.<Br>("+temp.dgrcompoNm+")"
                        });
                        
                        return;
                    }
                }
                
                if(temp.sel020Yn == "Y"){
                    if(saveReportParam.reportCd == "010" 
                            || saveReportParam.reportCd == "040" 
                            || saveReportParam.reportCd == "050" 
                            || saveReportParam.reportCd == "055" 
                            || saveReportParam.reportCd == "060" 
                            || saveReportParam.reportCd == "090"
                    ){
                        $.csAlert({
                            msg : "투자사업조서가 선택된 자료가 존재합니다.<Br>("+temp.dgrcompoNm+")"
                        });
                        
                        return;
                    }
                }
                
                if(temp.sel040Yn == "Y"){
                    if(saveReportParam.reportCd == "010" 
                            || saveReportParam.reportCd == "020" 
                            || saveReportParam.reportCd == "050" 
                            || saveReportParam.reportCd == "055" 
                            || saveReportParam.reportCd == "060" 
                            || saveReportParam.reportCd == "090"
                    ){
                        $.csAlert({
                            msg : "기본경비심사조서가 선택된 자료가 존재합니다.<Br>("+temp.dgrcompoNm+")"
                        });
                        
                        return;
                    }
                }
                
                if(temp.sel050Yn == "Y"){
                    if(saveReportParam.reportCd == "010"  
                            || saveReportParam.reportCd == "020"
                            || saveReportParam.reportCd == "040" 
                            || saveReportParam.reportCd == "055" 
                            || saveReportParam.reportCd == "060" 
                            || saveReportParam.reportCd == "090"
                    ){
                        $.csAlert({
                            msg : "시책업무추진비심사조서가 선택된 자료가 존재합니다.<Br>("+temp.dgrcompoNm+")"
                        });
                        
                        return;
                    }
                }
                
                if(temp.sel055Yn == "Y"){
                    if(saveReportParam.reportCd == "010" 
                            || saveReportParam.reportCd == "020" 
                            || saveReportParam.reportCd == "040" 
                            || saveReportParam.reportCd == "050" 
                            || saveReportParam.reportCd == "060" 
                            || saveReportParam.reportCd == "090"
                    ){
                        $.csAlert({
                            msg : "국외여비심사조서가 선택된 자료가 존재합니다.<Br>("+temp.dgrcompoNm+")"
                        });
                        
                        return;
                    }
                }
                
                if(temp.sel060Yn == "Y"){
                    if(saveReportParam.reportCd == "010" 
                            || saveReportParam.reportCd == "020" 
                            || saveReportParam.reportCd == "040" 
                            || saveReportParam.reportCd == "050" 
                            || saveReportParam.reportCd == "055" 
                            || saveReportParam.reportCd == "090"
                    ){
                        $.csAlert({
                            msg : "공통심사조서가 선택된 자료가 존재합니다.<Br>("+temp.dgrcompoNm+")"
                        });
                        
                        return;
                    }
                }
                
                if(temp.sel090Yn == "Y"){
                    if(saveReportParam.reportCd == "010" 
                            || saveReportParam.reportCd == "020"
                            || saveReportParam.reportCd == "040" 
                            || saveReportParam.reportCd == "050" 
                            || saveReportParam.reportCd == "055" 
                            || saveReportParam.reportCd == "060" 
                    ){
                        $.csAlert({
                            msg : "무기계약심사조서가 선택된 자료가 존재합니다.<Br>("+temp.dgrcompoNm+")"
                        });
                        
                        return;
                    }
                }
                
                if(saveReportParam.reportCd == "020" && (saveReportParam.reportDetlCd == "021" || saveReportParam.reportDetlCd == "022" || saveReportParam.reportDetlCd == "023")){
                    if(temp.checkYn031 != "Y" && temp.checkYn032 != "Y" && temp.checkYn033 != "Y" && temp.checkYnTf1 != "Y"){
                        $.csAlert({
                            msg : "투자사업심사조서(국고)는 국고보조사업(재원)을 선택해주세요.<Br>("+temp.dgrcompoNm+")"
                        });
                        
                        return;
                    }
                        
                }
            }
        }
        
        saveReportParam["saveReportDatas"] = selectedDatas;
        saveReportParam["saveReportDatas030"] = getSelectedData030(budgetSelectGrid, $("#BUDGET_SELECT_GRD", tabObj)[0].rows);
        
        $.csAjaxCall({
            url : "/budget/ajaxBudgetSelectSaveReport.do",
            data : saveReportParam,
            async : true,
            callBack : doSaveCallBack
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
            msg : "기존 자료 중 선택되지 않는 자료는 삭제됩니다.<br>저장하시겠습니까?",
            callBack : doSave
        });
    });
    
    var doChangeCondReportCd = function(){
        var reportCd = $("#condReportCd option:selected", tabObj).val();
        condReportDetlCdCreateCombo(reportCd, '');
        
        if(reportCd == "070"){
            $("#budgetSelectCondOrderYmdSeqSpan", tabObj).show();
            $("#budgetSelectCondOrderYmdSeqDiv", tabObj).show();
        }else{
            $("#budgetSelectCondOrderYmdSeqSpan", tabObj).hide();
            $("#budgetSelectCondOrderYmdSeqDiv", tabObj).hide();
        }
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
        
        condOrderYmdSeqCreateCombo(fisYear + '_' + bgtDgr, '');
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
    
    $("#condReportCd", tabObj).change(function(){
        doChangeCondReportCd();
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
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("budgetSelectDialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    budgetSelectDialogDgrDeptSeltCallBack = function(param){
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
        setGridCheckedAll(budgetSelectGrid, $("#BUDGET_SELECT_GRD", tabObj)[0].rows, "Y"); 
    });
    
    $("#unSelectAllBtn", tabObj).click(function(){
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $("#unSelectAllBtn", tabObj).hide();
        $("#selectAllBtn", tabObj).show();
        setGridCheckedAll(budgetSelectGrid, $("#BUDGET_SELECT_GRD", tabObj)[0].rows, "N");
    });
    
    var comboParam = [
                      {id : "reportCd", codeId : "RP001"},
                      {id : "reportDetlCd", codeId : "RP002"},
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "bgtDgr", subQueryId : "BgtDgr"},
                      {id : "fisFgMstCd", subQueryId : "FisFgMstCd"},
                      {id : "fisFgCd", subQueryId : "FisFgCd"},
                      {id : "officeCd", subQueryId : "OfficeCd"},
                      {id : "teMngMokCd", subQueryId : "TeMngMokCd"},
                      {id : "frscFgCd", subQueryId : "FrscFgCd"},
                      {id : "orderYmdSeq", subQueryId : "OrderYmdSeq"}
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
    
    var condOrderYmdSeqCreateCombo = function(groupId, selectedValue){
        $("#condOrderYmdSeq", tabObj).csCreatCombo(comboData
                , {id: 'orderYmdSeq'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'S'
                  , comboTypeValue: ''
                  }
        );
    };
    
    budgetSelectDialogReport070OrderYmdModifyCallBackFunction = function(param){        
        var comboOrderParam = [
                          {id : "orderYmdSeq", subQueryId : "OrderYmdSeq"}
                        ];

        var comboOrderData = jQuery.csComboAjaxCall(comboOrderParam);
        
        comboData.orderYmdSeq = comboOrderData.orderYmdSeq;

        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOrderYmdSeqCreateCombo(fisYear + '_' + bgtDgr, '');
    };
    
    $("#modifyOrderYmdSeqBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $("#dialogReport070OrderYmdModifyCallBackFunction", $("#dialogReport070OrderYmdModifyDiv")).val("budgetSelectDialogReport070OrderYmdModifyCallBackFunction");
        $("#dialogReport070OrderYmdModifyFisYear", $("#dialogReport070OrderYmdModifyDiv")).val($("#condFisYear option:selected", tabObj).val());
        $("#dialogReport070OrderYmdModifyBgtDgr", $("#dialogReport070OrderYmdModifyDiv")).val($("#condBgtDgr option:selected", tabObj).val());

        $("#dialogReport070OrderYmdModifyDiv").dialog('open');
    });
    
    doCondInit();
});
