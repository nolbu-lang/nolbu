$(document).ready(function() {
    var tabId = _reportWrite030TabId;
    var tabObj = $("#"+tabId);
    var gridScrollPosition = 0;
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        if(rowObject.sel010Yn == "Y"){
            return ' style="color:#0000FF"';
        }

        if(rowObject.sel020Yn == "Y"){
            return ' style="color:#FF0000"';
        }
        
    };
    
    var examContFormatter = function(cellValue, options, rowObject){
        if(rowObject.dbizCd == "0000000000000000" ){
            return cellValue;
        }
        
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var rVal = '<input id="examCont_'+rowObject.dgrcompoId+'" value="'+cellValue+'" maxlength="100" style="width:90%" />';

        return rVal;
    };
    
    var guGoonExpFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var readOnlyStr = "";
        var classStr = '';
        if(rowObject.dbizCd == "0000000000000000"){
            readOnlyStr = "readonly";
            classStr = 'class="amtInput030 ui-state-disabled"';
        }else{
            classStr = 'class="amtInput030 ui-state-enabled"';
        }
        
        var rVal = '<input id="guGoonExp_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" '+classStr+' '+readOnlyStr+' />';
        
        return rVal;
    };
    
    var natnBizNmFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.dbizCd == "0000000000000000" ){
            return cellValue;
        }
        
        var rVal = '<input id="natnBizNm_'+rowObject.dgrcompoId+'" value="'+cellValue+'" maxlength="40" style="width:90%" />';

        return rVal;
    };
    
    var mnstryNmFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.dbizCd == "0000000000000000" ){
            return cellValue;
        }
        
        var rVal = '<input id="mnstryNm_'+rowObject.dgrcompoId+'" value="'+cellValue+'" maxlength="40" style="width:90%" />';

        return rVal;
    };
    
    var natnBudnRateFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.dbizCd == "0000000000000000" ){
            return cellValue;
        }
        
        var rVal = '<input id="natnBudnRate_'+rowObject.dgrcompoId+'" value="'+cellValue+'" maxlength="10" style="width:90%"; text-align:center;/>';

        return rVal;
    };
    
    var siBudnRateFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.dbizCd == "0000000000000000" ){
            return cellValue;
        }
        
        var rVal = '<input id="siBudnRate_'+rowObject.dgrcompoId+'" value="'+cellValue+'" maxlength="10" style="width:90%"; text-align:center; />';

        return rVal;
    };
    
    var guBudnRateFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.dbizCd == "0000000000000000" ){
            return cellValue;
        }
        
        var rVal = '<input id="guBudnRate_'+rowObject.dgrcompoId+'" value="'+cellValue+'" maxlength="10" style="width:90%"; text-align:center; />';

        return rVal;
    };
    
    var srchValFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.dbizCd == "0000000000000000" ){
            return cellValue;
        }
        
        var rVal = '<div>'
                 + '<input id="srchVal_'+rowObject.dgrcompoId+'" value="'+cellValue+'" maxlength="40" style="width:98%;" />'
                 + '</div>';

        return rVal;
    };
    
    var colNames = ['구분(부서-사업)', '통계목', '계', '국비', '시비', '계', '국비', '시비', '계', '국비', '시비', '구·군비(※별도)', '국가사업명', '부처명', '국비', '시비', '구비', '비고', '조건검색어',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'reportCd', 'reportDetlCd', 'dgrLevel', 'deptCd', 'dbizCd', 'demandCont', 'examCont', 'reflectFg', 'srchVal',
                    'guGoonExp', 'natnBizNm', 'mnstryNm', 'natnBudnRate', 'siBudnRate', 'guBudnRate'
                   ];

    var colModel = [ {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 260, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'teMngMokNm', index : 'teMngMokNm', width : 60, sortable : false, fixed : true, align : 'center', cellattr: myCellattr},
                        {name : 'preDefSumExp', index : 'preDefSumExp', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'preDefNatnExp', index : 'preDefNatnExp', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'preDefSiExp', index : 'preDefSiExp', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'preSumExp', index : 'preSumExp', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'preNatnExp', index : 'preNatnExp', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'preSiExp', index : 'preSiExp', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'sumExp', index : 'sumExp', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'natnExp', index : 'natnExp', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'siExp', index : 'siExp', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'guGoonExpView', index : 'guGoonExpView', width : 90, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:guGoonExpFormatter
                        },
                        {name : 'natnBizNmView', index : 'natnBizNmView', width : 120, sortable : false, fixed : true, align : 'center', cellattr: myCellattr, 
                            formatter:natnBizNmFormatter
                        },
                        {name : 'mnstryNmView', index : 'mnstryNmView', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr, 
                            formatter:mnstryNmFormatter
                        },
                        {name : 'natnBudnRateView', index : 'natnBudnRateView', width : 30, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:natnBudnRateFormatter
                        },
                        {name : 'siBudnRateView', index : 'natnBudnRateView', width : 30, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:siBudnRateFormatter
                        },
                        {name : 'guBudnRateView', index : 'natnBudnRateView', width : 30, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:guBudnRateFormatter
                        },
                        {name : 'examContView', index : 'examContView', width : 150, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:examContFormatter
                        },
                        {name : 'srchValView', index : 'srchValView', width : 150, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:srchValFormatter
                        },
                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'reportCd', index : 'reportCd', width : 0, sortable : false, hidden : true},
                        {name : 'reportDetlCd', index : 'reportDetlCd', width : 0, sortable : false, hidden : true},
                        {name : 'dgrLevel', index : 'dgrLevel', width : 0, sortable : false, hidden : true},
                        {name : 'deptCd', index : 'deptCd', width : 0, sortable : false, hidden : true},
                        {name : 'dbizCd', index : 'dbizCd', width : 0, sortable : false, hidden : true},
                        {name : 'demandCont', index : 'demandCont', width : 0, sortable : false, hidden : true},
                        {name : 'examCont', index : 'examCont', width : 0, sortable : false, hidden : true},
                        {name : 'reflectFg', index : 'reflectFg', width : 0, sortable : false, hidden : true},
                        {name : 'srchVal', index : 'srchVal', width : 0, sortable : false, hidden : true},
                        {name : 'guGoonExp', index : 'guGoonExp', width : 0, sortable : false, hidden : true},
                        {name : 'natnBizNm', index : 'natnBizNm', width : 0, sortable : false, hidden : true},
                        {name : 'mnstryNm', index : 'mnstryNm', width : 0, sortable : false, hidden : true},
                        {name : 'natnBudnRate', index : 'natnBudnRate', width : 0, sortable : false, hidden : true},
                        {name : 'siBudnRate', index : 'siBudnRate', width : 0, sortable : false, hidden : true},
                        {name : 'guBudnRate', index : 'guBudnRate', width : 0, sortable : false, hidden : true}
                    ];

    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 250 ? $("#mainCenter", tabObj).height() - 110 : 250;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#REPORT_WRITE030_GRD_2", $("#"+tabId))) == false){
            $("#REPORT_WRITE030_GRD_2", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#REPORT_WRITE030_GRD_2", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 225,
        center__onresize: mainBodyResize
    });
    
    var reportWrite030Grid = $("#REPORT_WRITE030_GRD_2", tabObj);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        if(data.data.bgtDgr == "1"){
            colModel[2].hidden = true;
            colModel[3].hidden = true;
            colModel[4].hidden = true;
            colModel[5].hidden = false;
            colModel[6].hidden = false;
            colModel[7].hidden = false;
        }else{
            colModel[2].hidden = false;
            colModel[3].hidden = false;
            colModel[4].hidden = false;
            colModel[5].hidden = true;
            colModel[6].hidden = true;
            colModel[7].hidden = true;
        }

        $("#REPORT_WRITE030_GRD_2", tabObj).jqGrid('GridUnload');
        reportWrite030Grid = $("#REPORT_WRITE030_GRD_2", tabObj);
        reportWrite030Grid.csTreeGrid({
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
                $(".amtInput030.ui-state-enabled", tabObj).autoNumeric({aPad: false, vMax:'99999999999999999'});
                $(".amtInput030.ui-state-enabled", tabObj).click(function () {
                    $(this).select();
                });
                
                $('textarea', tabObj).autogrow();
                $('textarea', tabObj).keyup();
                $('textarea').maxlength({max: 1000, showFeedback: false});
            }
        });

        reportWrite030Grid.jqGrid('setGroupHeaders', {
            useColSpanStyle : true,
            groupHeaders : [
                {startColumnName : 'preDefSumExp',numberOfColumns : 3, titleText : '기정액'},
                {startColumnName : 'preSumExp',numberOfColumns : 3, titleText : '전년도 예산(추경포함)'},
                {startColumnName : 'sumExp',numberOfColumns : 3, titleText : '조정 예산'},
                {startColumnName : 'natnBudnRateView',numberOfColumns : 3, titleText : '부담비율(%)'},
            ]
        });
        
        $("#REPORT_WRITE030_GRD_2", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
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

        gridScrollPosition = $("#REPORT_WRITE030_GRD_2", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite030Report030List_2.do",
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
        
        //2018 이후 년도 제외
        $('#condFisYear').children('option').each(function(){
        	if($(this).val() > 2018){
        		$(this).remove();
        	}
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
        var examCont = "";
        var srchVal = "";
        var guGoonExp = "";
        var natnBizNm = "";
        var mnstryNm = "";
        var natnBudnRate = "";
        var siBudnRate = "";
        var guBudnRate = "";
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);
            if(isEmpty(rowData.dgrcompoId) == false && rowData.dbizCd != "0000000000000000"){
                examCont = $('#examCont_'+rowId, tabObj).val().trim();
                srchVal = $('#srchVal_'+rowId, tabObj).val();
                guGoonExp = $('#guGoonExp_'+rowId, tabObj).val().replaceAll(",", "");
                natnBizNm = $('#natnBizNm_'+rowId, tabObj).val();
                mnstryNm = $('#mnstryNm_'+rowId, tabObj).val();
                natnBudnRate = $('#natnBudnRate_'+rowId, tabObj).val();
                siBudnRate = $('#siBudnRate_'+rowId, tabObj).val();
                guBudnRate = $('#guBudnRate_'+rowId, tabObj).val();
                
                if(rowData.examCont != examCont
                        || rowData.srchVal != srchVal
                        || rowData.guGoonExp != guGoonExp
                        || rowData.natnBizNm != natnBizNm
                        || rowData.mnstryNm != mnstryNm
                        || rowData.natnBudnRate != natnBudnRate
                        || rowData.siBudnRate != siBudnRate
                        || rowData.guBudnRate != guBudnRate){
                    
                    saveData = {};
                    saveData["fisYear"] = rowData.fisYear;
                    saveData["bgtDgr"] = rowData.bgtDgr;
                    saveData["reportCd"] = rowData.reportCd;
                    saveData["reportDetlCd"] = rowData.reportDetlCd;
                    saveData["deptCd"] = rowData.deptCd;
                    saveData["dbizCd"] = rowData.dbizCd;
                    saveData["examCont"] = examCont;
                    saveData["srchVal"] = srchVal;
                    saveData["srchValYn"] = rowData.srchVal != srchVal ? "Y" : "N";
                    saveData["preNatnExp"] = rowData.preNatnExp;
                    saveData["preSiExp"] = rowData.preSiExp;
                    saveData["guGoonExp"] = guGoonExp;
                    saveData["natnBizNm"] = natnBizNm;
                    saveData["mnstryNm"] = mnstryNm;
                    saveData["natnBudnRate"] = natnBudnRate;
                    saveData["siBudnRate"] = siBudnRate;
                    saveData["guBudnRate"] = guBudnRate;
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
        
        var saveDatas = getSaveDatas(reportWrite030Grid, $("#REPORT_WRITE030_GRD_2", tabObj)[0].rows);
        if(isEmpty(saveDatas) == true || saveDatas.length < 1){
            $.csAlert({
                msg : "변경된 자료가 존재하지 않습니다."
            });
            
            return;
        }
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite030SaveReport030_2.do",
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
        
        $.csAlert({
            msg : "2018년도 이전자료 조회용이므로 저장할수 없습니다."
        });
        
        return;
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : doSave
        });
    });
    
    $("#saveFileBtn", tabObj).click(function() {
        var param = getSearchParam();
        param["fileNm"] = "국고보조사업심사조서";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite030SaveFile_2.do"
          , data: param
        });
    });
    
    $("#saveSheetBtn", tabObj).click(function() {
        var param = getSearchParam();
        param.reportDetlCd = "";
        param["fileNm"] = "국고보조사업심사조서";

        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite030SaveSheet_2.do"
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
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("reportWrite030DialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("030");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    reportWrite030DialogDgrDeptSeltCallBack = function(param){
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
                      {id : "officeCd", subQueryId : "OfficeCd", reportCd: "030"},
                      {id : "frscFgCd", subQueryId : "FrscFgCd"}
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
