$(document).ready(function() {
    var tabId = _reportWrite0D0TabId;
    var tabObj = $("#"+tabId);
    var gridScrollPosition = 0;
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        if(rowObject.teBgtCompoId != "00000000000"){
            return ' style="vertical-align: top;"';
        }
    };
    
    reportWirte0D0DialogDgrcompoModifyCallBackFunction = function(param){
        var rowId = param.dgrcompoId;
        if(isEmpty(rowId) == true){
            return;
        }
        
        reportWrite0D0Grid.jqGrid('setRowData', rowId, param);
        
        var dgrcompoNmView = param.dgrcompoNmView;
        if(isEmpty(dgrcompoNmView) == true){
            dgrcompoNmView = "";
        }
        
        $("#dgrcompoNmView_" + rowId, tabObj).html(dgrcompoNmView);
    };
    
    reportWirte0D0OpenDialogDgrcompoModify = function(rowId){  
        var rowData = reportWrite0D0Grid.getRowData(rowId);
        
        $("#dialogDgrcompoModifyCallBackFunction", $("#dialogDgrcompoModifyDiv")).val("reportWirte0D0DialogDgrcompoModifyCallBackFunction");
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
        
        var rVal = '<a href="javascript:reportWirte0D0OpenDialogDgrcompoModify(\''+options.rowId+'\');"><span class="ui-icon ui-icon-pencil"></span></a>';
        
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
        
        var rVal = '<span id="dgrcompoNmView_'+rowObject.dgrcompoId+'" >' + cellValue + '</span><br>'
                 + '<textarea id="demandCont_'+rowObject.dgrcompoId+'" style="width:270px;ime-mode:active;height:200px;">'+demandCont+'</textarea>';
        
        return rVal;
    };
    
    var examContFormatter = function(cellValue, options, rowObject){
        if(rowObject.teBgtCompoId == "00000000000"){
            return cellValue;
        }
        
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var rVal = '<div>'
                 + '<select id="reflectFg_'+rowObject.dgrcompoId+'" title="반영구분" style="width:270px;">'
                 + reflectFgCreateCombo('RP003', rowObject.reflectFg)
                 + '</select>'+'<br>'
                 + '<textarea id="examCont_'+rowObject.dgrcompoId+'" style="width:270px;ime-mode:active;height:216px;">'+cellValue+'</textarea>'
                 + '</div>';

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
    
    var mayorReportYnFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }

        var styleChkStr = "";
        var chkStr = "";
        if(rowObject.compoLevel != 1){
            styleChkStr = 'style="display:none;"';
        }else{
            styleChkStr = 'style="margin-top: 5px;"';
            chkStr = '보고';
        }
        
        var rVal = '<div>'
                 + '<input type="checkbox" id="mayorReportYn_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.mayorReportYn == 'Y' ? 'checked' : '')+' />'+chkStr
                 + '</div>';

        return rVal;
    };
    
    var colNames = ['', '구분(부서-세부사업)', '보고', '본예산', '증감액', '전년도예산액', '요구액', '조정액', '비고', '재원정보', '조건검색어',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'reportCd', 'reportDetlCd', 'dgrLevel', 'teBgtCompoId', 'teBgtCompoSeq', 'compoLevel', 'demandCont', 'examCont', 'reflectFg', 'srchVal', 'mayorReportYn'
                   ];

    var colModel = [ {name : 'edit', index : 'edit', width : 20, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:editFormatter
                        },
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 330, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:dgrcompoNmFormatter
                        },
                        {name : 'mayorReportYnView', index : 'mayorReportYnView', width : 50, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : mayorReportYnFormatter
                        },
                        {name : 'preAmt', index : 'preAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'diffAmt', index : 'diffAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'preBgtAmt', index : 'preBgtAmt', width : 100, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'demandBgtAmt', index : 'demandBgtAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'bgtAmt', index : 'bgtAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'examContView', index : 'examContView', width : 280, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
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
                        {name : 'mayorReportYn', index : 'mayorReportYn', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 250 ? $("#mainCenter", tabObj).height() - 110 : 250;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#REPORT_WRITE0D0_GRD", $("#"+tabId))) == false){
            $("#REPORT_WRITE0D0_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#REPORT_WRITE0D0_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 225,
        center__onresize: mainBodyResize
    });
    
    var reportWrite0D0Grid = $("#REPORT_WRITE0D0_GRD", tabObj);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        var groupHeaders = [];
        if(data.data.bgtDgr == "1"){
            colModel[2].hidden = true;
            colModel[3].hidden = false;
            
            groupHeaders = [
                            {startColumnName : 'demandBgtAmt',numberOfColumns : 2, titleText : '당해연도'}
                         ];
        }else{
            colModel[2].hidden = false;
            colModel[3].hidden = true;
            
            groupHeaders = [
                            {startColumnName : 'demandBgtAmt',numberOfColumns : 2, titleText : '금회추경'}
                         ];
        }

        $("#REPORT_WRITE0D0_GRD", tabObj).jqGrid('GridUnload');
        reportWrite0D0Grid = $("#REPORT_WRITE0D0_GRD", tabObj);
        reportWrite0D0Grid.csTreeGrid({
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
                $('textarea').maxlength({max: 1000, showFeedback: false});
            }
        });

        reportWrite0D0Grid.jqGrid('setGroupHeaders', {
            useColSpanStyle : true,
            groupHeaders : groupHeaders
        });
        
        $("#REPORT_WRITE0D0_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
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
                amtUnit : amtUnit,
                frscFgCdFr : frscFgCdFr,
                frscFgCdTo : frscFgCdTo,
                frscFrCdYn : frscFrCdYn,
                mayorReportYn : "N"
         };
        
        return param;
    };
    
    var doSearch = function(){

        gridScrollPosition = $("#REPORT_WRITE0D0_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite0D0Report0D0List.do",
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
    };
    
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
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
        var mayorReportYn = "";
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);
            if(isEmpty(rowData.dgrcompoId) == false && rowData.teBgtCompoId != "00000000000"){
                demandCont = $('#demandCont_'+rowId, tabObj).val().trim();
                examCont = $('#examCont_'+rowId, tabObj).val().trim();
                reflectFg = $('#reflectFg_'+rowId, tabObj).val();
                srchVal = $('#srchVal_'+rowId, tabObj).val();
                mayorReportYn = $('#mayorReportYn_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                
                if(rowData.demandCont != demandCont
                        || rowData.examCont != examCont
                        || rowData.reflectFg != reflectFg
                        || rowData.srchVal != srchVal
                        || rowData.mayorReportYn != mayorReportYn){
                    
                    saveData = {};
                    saveData["fisYear"] = rowData.fisYear;
                    saveData["bgtDgr"] = rowData.bgtDgr;
                    saveData["reportCd"] = rowData.reportCd;
                    saveData["reportDetlCd"] = rowData.reportDetlCd;
                    saveData["teBgtCompoId"] = rowData.teBgtCompoId;
                    saveData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
                    saveData["demandCont"] = demandCont;
                    saveData["examCont"] = examCont;
                    saveData["reflectFg"] = reflectFg;
                    saveData["srchVal"] = srchVal;
                    saveData["srchValYn"] = rowData.srchVal != srchVal ? "Y" : "N";
                    saveData["mayorReportYn"] = mayorReportYn;
                    saveData["mayorReportChangeYn"] = rowData.mayorReportYn != mayorReportYn ? "Y" : "N";
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
        
        var saveDatas = getSaveDatas(reportWrite0D0Grid, $("#REPORT_WRITE0D0_GRD", tabObj)[0].rows);
        if(isEmpty(saveDatas) == true || saveDatas.length < 1){
            $.csAlert({
                msg : "변경된 자료가 존재하지 않습니다."
            });
            
            return;
        }
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite0D0SaveReport0D0.do",
            data : {saveDatas: saveDatas},
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
    
    $("#saveFileBtn", tabObj).click(function() {
        var param = getSearchParam();
        param["fileNm"] = "현안사업보고서식";
        param["amtUnit"] = "1000000";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite0D0SaveFile.do"
          , data: param
        });
    });
    
    $("#saveSheetBtn", tabObj).click(function() {
        var param = getSearchParam();
        param["fileNm"] = "현안사업보고서식";
        param["amtUnit"] = "1000000";
        param.mayorReportYn = "N";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite0D0SaveSheet.do"
          , data: param
        });
    });
    
    $("#saveSheetBtn2", tabObj).click(function() {
        var param = getSearchParam();
        param["fileNm"] = "현안사업보고서식_시장님보고";
        param["amtUnit"] = "1000000";
        param.mayorReportYn = "Y";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite0D0SaveSheet.do"
          , data: param
        });
    });
    
    $("#saveSheetBtn3", tabObj).click(function() {
        var param = getSearchParam();
        param["fileNm"] = "현안사업보고서식_시장님보고";
        param["amtUnit"] = "1000000";
        param.mayorReportYn = "Y";
        param.officeYn = "N";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite0D0SaveSheet.do"
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
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("reportWrite0D0DialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("0D0");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    reportWrite0D0DialogDgrDeptSeltCallBack = function(param){
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
                      {id : "officeCd", subQueryId : "OfficeCd", reportCd: "0D0"},
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
            
    var reflectFgCreateCombo = function(groupId, selectedValue){
        return getCsComboStr(comboData
                , {id: 'reflectFg'
                    , groupId: groupId
                    , selectedValue: selectedValue
                    , comboType: 'S'
                    , comboTypeValue: ''
                    });
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
