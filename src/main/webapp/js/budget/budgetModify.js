$(document).ready(function() {
    var tabId = _budgetModifyTabId;
    var tabObj = $("#"+tabId);
    var gridScrollPosition = 0;

    
    var colNames = ['', '구분(회계-실-부서-세부-통계목)', '기정액', '산출근거식', '증감액', '예산액', '산출근거식', '증감액', '예산액', '산출근거식', '예산액', '재원정보', '공약정보', 
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'dgrLevel', 'deptCd', 'dbizCd', 'teMngMokCd', 'teBgtCompoSeq', 'compoLevel', 'isLeaf', 'editableYn'
                   ];
    
    var colModel = [
                        {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, frozen: true,
                            formatter: function (cellValue, option, rowObject) {
                                if(rowObject.dgrLevel < 3){
                                    return "";
                                }
                                
                                if(rowObject.dgrLevel == 3 && _mainNorthPowGrCd != "BC001"){
                                    return "";
                                }
                                
                                if(_mainNorthPowGrCd != "BC001" && rowObject.editableYn != "Y"){
                                    return "";
                                }
                                
                                return '<input type="radio" name="radio_' + option.gid + '"  />';
                            }
                        },
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 400, sortable : false, fixed : true, align : 'left'},
                        {name : 'preAmt', index : 'preAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'demandCompFormular', index : 'demandCompFormular', width : 120, sortable : false, fixed : true, align : 'left'},
                        {name : 'demandDiffAmt', index : 'demandDiffAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'demandBgtAmt', index : 'demandBgtAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'compFormular', index : 'compFormular', width : 120, sortable : false, fixed : true, align : 'left'},
                        {name : 'diffAmt', index : 'diffAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'bgtAmt', index : 'bgtAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'preCompFormular', index : 'preCompFormular', width : 120, sortable : false, fixed : true, align : 'left'},
                        {name : 'preBgtAmt', index : 'preBgtAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'frsces', index : 'frsces', width : 250, sortable : false, fixed : true, align : 'left'},
                        {name : 'pledgeNames', index : 'pledgeNames', width : 250, sortable : false, fixed : true, align : 'left'},
                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true},
                        {name : 'dgrLevel', index : 'dgrLevel', width : 0, sortable : false, hidden : true},
                        {name : 'deptCd', index : 'deptCd', width : 0, sortable : false, hidden : true},
                        {name : 'dbizCd', index : 'dbizCd', width : 0, sortable : false, hidden : true},
                        {name : 'teMngMokCd', index : 'teMngMokCd', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'compoLevel', index : 'compoLevel', width : 0, sortable : false, hidden : true},
                        {name : 'isLeaf', index : 'isLeaf', width : 0, sortable : false, hidden : true},
                        {name : 'editableYn', index : 'editableYn', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 200 ? $("#mainCenter", tabObj).height() - 110 : 200;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#BUDGET_MODIFY_GRD", $("#"+tabId))) == false){
            $("#BUDGET_MODIFY_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#BUDGET_MODIFY_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 195,
        center__onresize: mainBodyResize
    });
    
    var budgetModifyGrid = $("#BUDGET_MODIFY_GRD", tabObj);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        $("#BUDGET_MODIFY_GRD", tabObj).jqGrid('GridUnload');
        budgetModifyGrid = $("#BUDGET_MODIFY_GRD", tabObj);
        budgetModifyGrid.csTreeGrid({
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
                $('textarea', tabObj).autogrow();
                $('textarea', tabObj).keyup();
            }
        });

        budgetModifyGrid.jqGrid('setGroupHeaders', {
            useColSpanStyle : true,
            groupHeaders : [
               {startColumnName : 'demandCompFormular',numberOfColumns : 3, titleText : '요구'},
               {startColumnName : 'compFormular', numberOfColumns : 3, titleText : '조정'},
               {startColumnName : 'preCompFormular', numberOfColumns : 2, titleText : '전년도'} 
            ]
        });
        
        $("#BUDGET_MODIFY_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
        $("#regiBtn", tabObj).btnChangeState(true);
        $("#modifyBtn", tabObj).btnChangeState(true);
        $("#deleteBtn", tabObj).btnChangeState(true);
        $("#sortBtn", tabObj).btnChangeState(true);

        data = null;
    };
    
    var getSelectedRowId = function(){
        var $selRadio = $('input[name=radio_BUDGET_MODIFY_GRD]:checked'), $tr;
        if ($selRadio.length > 0) {
            $tr = $selRadio.closest('tr');
            if ($tr.length > 0) {
                return $tr.attr('id');
            }
        }
            
        return "";
    };
    
    var doSearch = function(){
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

        gridScrollPosition = $("#BUDGET_MODIFY_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        $.csAjaxCall({
            url : "/budget/ajaxBudgetModifySelectDgrcompoList.do",
            data: {fisYear : fisYear,
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
                   userDeptYn : "N"
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
        

        gridScrollPosition = 0;
    };
    
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });

    $("#regiBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        var rowId = getSelectedRowId();
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "등록할 상위 항목을 선택하여 주십시오."
            });
            
            return;
        }

        var rowData = budgetModifyGrid.getRowData(rowId);
        
        if(rowData.dbizCd == "0000000000000000"){
            $.csAlert({
                msg : "세목 이하 예산편성을 등록할 수 있습니다.<BR>통계목 이하 항목을 선택하여 주십시오."
            });
            
            return;
        }
        
        if(rowData.compoLevel == "4"){
            $.csAlert({
                msg : "최종 차수 입니다. 상위 항목을 선택하여 주십시오."
            });
            
            return;
        }
        
        if(_mainNorthPowGrCd != "BC001" && rowData.editableYn != "Y"){
            $.csAlert({
                msg : "등록할 권한이 없는 부서 자료입니다."
            });
            
            return;
        }
        
        if(rowData.teMngMokCd == "00000"){
            if(_mainNorthPowGrCd != "BC001"){
                $.csAlert({
                    msg : "시스템관리자만 통계목을 등록하실 수 있습니다."
                });
                
                return;
            }
            
            $("#dialogDgrcompoTeMngRegiCallBackFunction", $("#dialogDgrcompoTeMngRegiDiv")).val("budgetModifyDialogDgrcompoTeMngRegiCallBackFunction");
            $("#dialogDgrcompoTeMngRegiFisYear", $("#dialogDgrcompoTeMngRegiDiv")).val(rowData.fisYear);
            $("#dialogDgrcompoTeMngRegiBgtDgr", $("#dialogDgrcompoTeMngRegiDiv")).val(rowData.bgtDgr);
            $("#dialogDgrcompoTeMngRegiDeptCd", $("#dialogDgrcompoTeMngRegiDiv")).val(rowData.deptCd);
            $("#dialogDgrcompoTeMngRegiDbizCd", $("#dialogDgrcompoTeMngRegiDiv")).val(rowData.dbizCd);
            $("#dialogDgrcompoTeMngRegiTeMngMokCd", $("#dialogDgrcompoTeMngRegiDiv")).val("");
            $("#dialogDgrcompoTeMngRegiUpTeBgtCompoId", $("#dialogDgrcompoTeMngRegiDiv")).val(rowData.teBgtCompoId);
            $("#dialogDgrcompoTeMngRegiUpTeBgtCompoSeq", $("#dialogDgrcompoTeMngRegiDiv")).val(rowData.teBgtCompoSeq);
            $("#dialogDgrcompoTeMngRegiCompoLevel", $("#dialogDgrcompoTeMngRegiDiv")).val("1");
            $("#dialogDgrcompoTeMngRegiDgrcompoNm", $("#dialogDgrcompoTeMngRegiDiv")).val(rowData.dgrcompoNm);
            $("#dialogDgrcompoTeMngRegiAmtUnit", $("#dialogDgrcompoTeMngRegiDiv")).val($("#condAmtUnit", tabObj).val());

            $("#dialogDgrcompoTeMngRegiDiv").dialog('open');
            
            return;
        }else{
            $("#dialogDgrcompoRegiCallBackFunction", $("#dialogDgrcompoRegiDiv")).val("budgetModifyDialogDgrcompoRegiCallBackFunction");
            $("#dialogDgrcompoRegiFisYear", $("#dialogDgrcompoRegiDiv")).val(rowData.fisYear);
            $("#dialogDgrcompoRegiBgtDgr", $("#dialogDgrcompoRegiDiv")).val(rowData.bgtDgr);
            $("#dialogDgrcompoRegiDeptCd", $("#dialogDgrcompoRegiDiv")).val(rowData.deptCd);
            $("#dialogDgrcompoRegiDbizCd", $("#dialogDgrcompoRegiDiv")).val(rowData.dbizCd);
            $("#dialogDgrcompoRegiTeMngMokCd", $("#dialogDgrcompoRegiDiv")).val(rowData.teMngMokCd);
            $("#dialogDgrcompoRegiUpTeBgtCompoId", $("#dialogDgrcompoRegiDiv")).val(rowData.teBgtCompoId);
            $("#dialogDgrcompoRegiUpTeBgtCompoSeq", $("#dialogDgrcompoRegiDiv")).val(rowData.teBgtCompoSeq);
            $("#dialogDgrcompoRegiCompoLevel", $("#dialogDgrcompoRegiDiv")).val("" + (Number(rowData.compoLevel) + 1));
            $("#dialogDgrcompoRegiDgrcompoNm", $("#dialogDgrcompoRegiDiv")).val(rowData.dgrcompoNm);
            $("#dialogDgrcompoRegiAmtUnit", $("#dialogDgrcompoRegiDiv")).val($("#condAmtUnit", tabObj).val());

            $("#dialogDgrcompoRegiDiv").dialog('open');
        }
        
    });
    
    budgetModifyDialogDgrcompoTeMngRegiCallBackFunction = function(param){
        doSearch();
    };
    
    budgetModifyDialogDgrcompoRegiCallBackFunction = function(param){
        doSearch();
    };

    $("#modifyBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        var rowId = getSelectedRowId();
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "수정할 항목을 선택하여 주십시오."
            });
            
            return;
        }

        var rowData = budgetModifyGrid.getRowData(rowId);
        if(rowData.teBgtCompoId == "00000000000"){
            $.csAlert({
                msg : "세세목 이하 예산편성을 수정할 수 있습니다."
            });
            
            return;
        }
        
        if(_mainNorthPowGrCd != "BC001" && rowData.editableYn != "Y"){
            $.csAlert({
                msg : "수정할 권한이 없는 부서 자료입니다."
            });
            
            return;
        }
        
        $("#dialogDgrcompoModifyCallBackFunction", $("#dialogDgrcompoModifyDiv")).val("budgetModifyDialogDgrcompoModifyCallBackFunction");
        $("#dialogDgrcompoModifyDgrcompoId", $("#dialogDgrcompoModifyDiv")).val(rowData.dgrcompoId);
        $("#dialogDgrcompoModifyFisYear", $("#dialogDgrcompoModifyDiv")).val(rowData.fisYear);
        $("#dialogDgrcompoModifyBgtDgr", $("#dialogDgrcompoModifyDiv")).val(rowData.bgtDgr);
        $("#dialogDgrcompoModifyTeBgtCompoId", $("#dialogDgrcompoModifyDiv")).val(rowData.teBgtCompoId);
        $("#dialogDgrcompoModifyIsLeaf", $("#dialogDgrcompoModifyDiv")).val(rowData.isLeaf);
        $("#dialogDgrcompoModifyAmtUnit", $("#dialogDgrcompoModifyDiv")).val($("#condAmtUnit", tabObj).val());
        
        $("#dialogDgrcompoModifyDiv").dialog('open');
    });

    var budgetModifyDoDelete = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var rowId = getSelectedRowId();
        var rowData = budgetModifyGrid.getRowData(rowId);
        
        var data = $.csAjaxCall({
            url : "/budget/ajaxBudgetModifyDeleteDgrcompoList.do",
            data : {
                fisYear : rowData.fisYear,
                bgtDgr : rowData.bgtDgr,
                teBgtCompoId : rowData.teBgtCompoId
            }
        });
        
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
    
    $("#deleteBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        var rowId = getSelectedRowId();
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "삭제할 항목을 선택하여 주십시오."
            });
            
            return;
        }

        var rowData = budgetModifyGrid.getRowData(rowId);
        if(rowData.isLeaf !== "true"){
            $.csAlert({
                msg : "최종 차수의 예산편성 자료만 삭제할 수 있습니다."
            });
            
            return;
        }
        
        if(_mainNorthPowGrCd != "BC001" && rowData.editableYn != "Y"){
            $.csAlert({
                msg : "삭제할 권한이 없는 부서 자료입니다."
            });
            
            return;
        }
        
        $.csConfirm({
            msg : "삭제하시겠습니까?",
            callBack : budgetModifyDoDelete
        });
    });
    
    $("#sortBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        var rowId = getSelectedRowId();
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "변경할 항목을 선택하여 주십시오."
            });
            
            return;
        }

        var rowData = budgetModifyGrid.getRowData(rowId);
        
        if(rowData.teBgtCompoId == "00000000000"){
            $.csAlert({
                msg : "세세목 이하 예산편성의 정렬 순서를 변경할 수 있습니다.<BR>세세목 이하 항목을 선택하여 주십시오."
            });
            
            return;
        }
        
        if(_mainNorthPowGrCd != "BC001" && rowData.editableYn != "Y"){
            $.csAlert({
                msg : "변경할 권한이 없는 부서 자료입니다."
            });
            
            return;
        }
        
        $("#dialogDgrcompoSortCallBackFunction", $("#dialogDgrcompoSortDiv")).val("budgetModifyDialogDgrcompoSortCallBackFunction");
        $("#dialogDgrcompoSortFisYear", $("#dialogDgrcompoSortDiv")).val(rowData.fisYear);
        $("#dialogDgrcompoSortBgtDgr", $("#dialogDgrcompoSortDiv")).val(rowData.bgtDgr);
        $("#dialogDgrcompoSortTeBgtCompoId", $("#dialogDgrcompoSortDiv")).val(rowData.teBgtCompoId);

        $("#dialogDgrcompoSortDiv").dialog('open');
    });
    
    budgetModifyDialogDgrcompoSortCallBackFunction = function(param){
        doSearch();
    };
    
    budgetModifyDialogDgrcompoModifyCallBackFunction = function(param){
        var rowId = param.dgrcompoId;
        if(isEmpty(rowId) == true){
            return;
        }

        var dgrcompoNmView = param.dgrcompoNmView;
        if(isEmpty(dgrcompoNmView) == true){
            dgrcompoNmView = "";
        }
        
        param["dgrcompoNm"] = dgrcompoNmView;
        
        budgetModifyGrid.jqGrid('setRowData', rowId, param);
        
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
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("budgetModifyDialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("N");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    budgetModifyDialogDgrDeptSeltCallBack = function(param){
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
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "bgtDgr", subQueryId : "BgtDgr"},
                      {id : "fisFgMstCd", subQueryId : "FisFgMstCd"},
                      {id : "fisFgCd", subQueryId : "FisFgCd"},
                      {id : "officeCd", subQueryId : "OfficeCd", userDeptYn : "N"},
                      {id : "teMngMokCd", subQueryId : "TeMngMokCd"},
                      {id : "frscFgCd", subQueryId : "FrscFgCd"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);
        
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
