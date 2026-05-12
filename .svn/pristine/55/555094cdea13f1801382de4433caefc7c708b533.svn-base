$(document).ready(function() {
    var tabId = _budgetPreCopyPageTabId;
    var tabObj = $("#"+tabId);
    
    var gridScrollPosition = 0;
    var srcGridScrollPosition = 0;

    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        return ' style="vertical-align: top;"';
    };
    
    var upDgrcompoNmFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }

        var officeNm = isEmpty(rowObject.officeNm) == true ? "" : rowObject.officeNm;
        var deptNm = isEmpty(rowObject.deptNm) == true ? "" : rowObject.deptNm;
        var dbizNm = isEmpty(rowObject.dbizNm) == true ? "" : rowObject.dbizNm;
        var teMngMokNm = isEmpty(rowObject.teMngMokNm) == true ? "" : rowObject.teMngMokNm;
        
        return "실국: "+officeNm+"<br>"+"부서: "+deptNm+"<br>"+"세부: "+dbizNm+"<br>"+"통계목: "+teMngMokNm;
    };
    
    var colNames = ['', '[실국-부서-세부]', '구분', '기정액', '산출근거식', '증감액', '예산액', '산출근거식', '증감액', '예산액', '산출근거식', '예산액', '재원정보',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'dgrLevel', 'deptCd', 'dbizCd', 'teMngMokCd', 'teBgtCompoSeq', 'compoLevel', 'isLeaf'
                   ];
    
    var colModel = [
                        {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, frozen: true,
                            formatter: function (cellValue, option) {
                                return '<input type="radio" name="radio_' + option.gid + '"  />';
                            }
                        },
                        {name : 'upDgrcompoNm', index : 'upDgrcompoNm', width : 150, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:upDgrcompoNmFormatter
                        },
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 250, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'preAmt', index : 'preAmt', width : 80, sortable : false, fixed : true, align : 'right', cellattr: myCellattr, formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'demandCompFormular', index : 'demandCompFormular', width : 120, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'demandDiffAmt', index : 'demandDiffAmt', width : 80, sortable : false, fixed : true, align : 'right', cellattr: myCellattr, formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'demandBgtAmt', index : 'demandBgtAmt', width : 80, sortable : false, fixed : true, align : 'right', cellattr: myCellattr, formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'compFormular', index : 'compFormular', width : 120, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'diffAmt', index : 'diffAmt', width : 80, sortable : false, fixed : true, align : 'right', cellattr: myCellattr, formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'bgtAmt', index : 'bgtAmt', width : 80, sortable : false, fixed : true, align : 'right', cellattr: myCellattr, formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'preCompFormular', index : 'preCompFormular', width : 120, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'preBgtAmt', index : 'preBgtAmt', width : 80, sortable : false, fixed : true, align : 'right', cellattr: myCellattr, formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'frsces', index : 'frsces', width : 250, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
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
                        {name : 'isLeaf', index : 'isLeaf', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 300 > 150 ? $("#mainCenter", tabObj).height() - 300 : 150;
    };
    
    var mainBodyResize = function(){
        $("#subMainBody", tabObj).width($("#mainCenter", tabObj).width());
        $("#subMainBody", tabObj).layout().resizeAll();
    };
    
    var subMainBodyResize = function(){
        $("#subMainCenterCond", tabObj).width($("#subMainCenter", tabObj).width()-20);
        $("#subMainWestCond", tabObj).width($("#subMainWest", tabObj).width()-20);
        if(isEmpty($("#BUDGET_PRE_COPYPAGE_SRC_GRD", tabObj)) == false){
            $("#BUDGET_PRE_COPYPAGE_SRC_GRD", tabObj).setGridHeight(getGridHeight());
            $("#BUDGET_PRE_COPYPAGE_SRC_GRD", tabObj).setGridWidth($("#subMainWest", tabObj).width());
        }
        
        if(isEmpty($("#BUDGET_PRE_COPYPAGE_GRD", tabObj)) == false){
            $("#BUDGET_PRE_COPYPAGE_GRD", tabObj).setGridHeight(getGridHeight());
            $("#BUDGET_PRE_COPYPAGE_GRD", tabObj).setGridWidth($("#subMainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        center__onresize: mainBodyResize
    });
    
    $("#subMainBody", tabObj).layout({
        west__size : "50%",
        center__onresize: subMainBodyResize
    });
    
    subMainBodyResize();

    var budgetPreCopyPageSrcGrid = $("#BUDGET_PRE_COPYPAGE_SRC_GRD", tabObj);
    var budgetPreCopyPageGrid = $("#BUDGET_PRE_COPYPAGE_GRD", tabObj);
    
    var doSearchSrcCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        $("#BUDGET_PRE_COPYPAGE_SRC_GRD", tabObj).GridUnload();
        budgetPreCopyPageSrcGrid = $("#BUDGET_PRE_COPYPAGE_SRC_GRD", tabObj);
        budgetPreCopyPageSrcGrid.csTreeGrid({
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
        
        budgetPreCopyPageSrcGrid.jqGrid('setGroupHeaders', {
            useColSpanStyle : true,
            groupHeaders : [
                {startColumnName : 'demandCompFormular',numberOfColumns : 3, titleText : '요구'},
                {startColumnName : 'compFormular', numberOfColumns : 3, titleText : '조정'},
                {startColumnName : 'preCompFormular', numberOfColumns : 2, titleText : '전년도'} 
            ]
        });

        
        
        $("#BUDGET_PRE_COPYPAGE_SRC_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(srcGridScrollPosition);
        
        $("#BUDGET_PRE_COPYPAGE_SRC_PGR").addPagingData(data, "budgetPreCopyPageDoPageSearchSrc");
        $("#BUDGET_PRE_COPYPAGE_SRC_TOT").html("통계목 총건수 : " + addCommaStr(data.totalCount) + "건");
        
        data = null;
    };
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        $("#BUDGET_PRE_COPYPAGE_GRD", tabObj).jqGrid('GridUnload');
        budgetPreCopyPageGrid = $("#BUDGET_PRE_COPYPAGE_GRD", tabObj);
        budgetPreCopyPageGrid.csTreeGrid({
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
        
        budgetPreCopyPageGrid.jqGrid('setGroupHeaders', {
            useColSpanStyle : true,
            groupHeaders : [
                {startColumnName : 'demandCompFormular',numberOfColumns : 3, titleText : '요구'},
                {startColumnName : 'compFormular', numberOfColumns : 3, titleText : '조정'},
                {startColumnName : 'preCompFormular', numberOfColumns : 2, titleText : '전년도'} 
            ]
        });
        
        $("#BUDGET_PRE_COPYPAGE_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);

        $("#BUDGET_PRE_COPYPAGE_PGR").addPagingData(data, "budgetPreCopyPageDoPageSearch");
        $("#BUDGET_PRE_COPYPAGE_TOT").html("통계목 총건수 : " + addCommaStr(data.totalCount) + "건");
        
        data = null;
    };
    
    budgetPreCopyPageDoPageSearchSrc = function(page) {
        defaultSearchSrcParam.page = page;
        doSearchSrc();
    };
    
    budgetPreCopyPageDoPageSearch = function(page) {
        defaultSearchParam.page = page;
        doSearch();
    };

    var defaultSearchParam = {
        page : 1,
        rowNum : 3
    };

    var defaultSearchSrcParam = {
        page : 1,
        rowNum : 3
    };
    
    var getSearchSrcParam = function(){

        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condSrcBgtDgr option:selected", tabObj).val();
        var fisFgMstCd = $("#condSrcFisFgMstCd option:selected", tabObj).val();
        var fisFgCd = $("#condSrcFisFgCd option:selected", tabObj).val();
        var officeCd = $("#condSrcOfficeCd option:selected", tabObj).val();
        var deptRankFr = $("#condSrcDeptRankFr", tabObj).val();
        var deptRankTo = $("#condSrcDeptRankTo", tabObj).val();
        var teMngMokCdFr = $("#condSrcTeMngMokCdFr", tabObj).val();
        var teMngMokCdTo = $("#condSrcTeMngMokCdTo", tabObj).val();
        var frscFgCdFr = $("#condSrcFrscFgCdFr", tabObj).val();
        var frscFgCdTo = $("#condSrcFrscFgCdTo", tabObj).val();
        var frscFrCdYn = "N";
        if(isEmpty(frscFgCdFr) == false || isEmpty(frscFgCdTo) == false){
            frscFrCdYn = "Y";
        }
        var amtUnit = $("#condAmtUnit", tabObj).val();
        
        var param = {fisYear : fisYear,
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
                amtUnit : amtUnit
         };
        
        $.extend(defaultSearchSrcParam, param);
        
        return defaultSearchSrcParam;
    };
    
    var getSearchParam = function(){
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
        
        var param = {fisYear : fisYear,
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
                amtUnit : amtUnit
         };
        
        $.extend(defaultSearchParam, param);
        
        return defaultSearchParam;
    };
    
    var doSearchSrc = function(){
        srcGridScrollPosition = $("#BUDGET_PRE_COPYPAGE_SRC_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();

        $.csAjaxCall({
            url : "/budget/ajaxBudgetPreCopyPageList.do",
            data: getSearchSrcParam(),
            async : true,
            callBack : doSearchSrcCallBack
        });
        
    };
    
    var doSearch = function(){

        gridScrollPosition = $("#BUDGET_PRE_COPYPAGE_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        $.csAjaxCall({
            url : "/budget/ajaxBudgetPreCopyPageList.do",
            data: getSearchParam(),
            async : true,
            callBack : doSearchCallBack
        });
    };

    var doCondSrcInit = function(){
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
        
        condSrcTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condSrcTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
        
        condSrcFrscFgCdFrCreateCombo(fisYear, '');
        condSrcFrscFgCdToCreateCombo(fisYear, '');
        
        $("#condSrcDeptCdFr", tabObj).val("");
        $("#condSrcDeptNmFr", tabObj).val("");
        $("#condSrcDeptRankFr", tabObj).val("");
        $("#condSrcDeptCdTo", tabObj).val("");
        $("#condSrcDeptNmTo", tabObj).val("");
        $("#condSrcDeptRankTo", tabObj).val("");
        $("#condSrcTeMngMokCdFr", tabObj).val("");
        $("#condSrcTeMngMokCdTo", tabObj).val("");
        $("#condSrcFrscFgCdFr", tabObj).val("");
        $("#condSrcFrscFgCdTo", tabObj).val("");
    };
    
    $("#condSrcInitBtn", tabObj).click(function() {
        doCondSrcInit();
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
    };
    
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });
    
    $("#srcSearchBtn", tabObj).click(function() {
        var fisFgMstCd = $("#condSrcFisFgMstCd option:selected", tabObj).val();
        var officeCd = $("#condSrcOfficeCd option:selected", tabObj).val();
        /*
        if(fisFgMstCd == "100" && isEmpty(officeCd) == true){
            $.csAlert({
                msg : "일반회계는 실국을 선택하셔야 합니다.",
                callBack : function() {
                    $("#condSrcOfficeCd", tabObj).focus();
                }
            });
            
            return;
        }
        */
        
        srcGridScrollPosition = 0;
        
        doSearchSrc();
    });
    
    $("#searchBtn", tabObj).click(function() {
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        /*
        if(fisFgMstCd == "100" && isEmpty(officeCd) == true){
            $.csAlert({
                msg : "일반회계는 실국을 선택하셔야 합니다.",
                callBack : function() {
                    $("#condOfficeCd", tabObj).focus();
                }
            });
            
            return;
        }
        */
        
        gridScrollPosition = 0;
        
        doSearch();
    });

    var getSelectedRowId = function(gridNm){
        var $selRadio = $('input[name=radio_' + gridNm + '_GRD]:checked'), $tr;
        if ($selRadio.length > 0) {
            $tr = $selRadio.closest('tr');
            if ($tr.length > 0) {
                return $tr.attr('id');
            }
        }
            
        return "";
    };
    
    var applyParam = {};
    
    var doApply = function(params){
        if(params.confirmData != "Y"){
            return;
        }
        
        var data = $.csAjaxCall({
            url : "/budget/ajaxBudgetPreCopyCopyPreInfo.do",
            data : applyParam
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
    
    $("#applyBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        var srcRowId = getSelectedRowId('BUDGET_PRE_COPYPAGE_SRC');
        if(isEmpty(srcRowId) == true){
            $.csAlert({
                msg : "적용할 자료를 선택하여 주십시오."
            });
            
            return;
        }
        
        var rowId = getSelectedRowId('BUDGET_PRE_COPYPAGE');
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "적용할 대상을 선택하여 주십시오."
            });
            
            return;
        }

        var srcRowData = budgetPreCopyPageSrcGrid.getRowData(srcRowId);
        if(srcRowData.teBgtCompoId == "00000000000"){
            $.csAlert({
                msg : "세세목 이하 예산편성을 선택하여 주십시오."
            });
            
            return;
        }

        var rowData = budgetPreCopyPageGrid.getRowData(rowId);
        
        if(rowData.isLeaf !== "true"){
            $.csAlert({
                msg : "적용 대상은 최종 차수의 예산편성 자료만 선택하여 주십시오."
            });
            
            return;
        }
        
        if(rowData.teBgtCompoId == "00000000000"){
            $.csAlert({
                msg : "세세목 이하 예산편성을 선택하여 주십시오."
            });
            
            return;
        }
        
        applyParam = {};
        applyParam["srcFisYear"] = srcRowData.fisYear;
        applyParam["srcBgtDgr"] = srcRowData.bgtDgr;
        applyParam["srcTeBgtCompoId"] = srcRowData.teBgtCompoId;
        applyParam["fisYear"] = rowData.fisYear;
        applyParam["bgtDgr"] = rowData.bgtDgr;
        applyParam["teBgtCompoId"] = rowData.teBgtCompoId;
        
        $.csConfirm({
            msg : "적용하시겠습니까?",
            callBack : doApply
        });
    });
    
    var doChangeCondSrcFisYear = function(){
        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        condSrcBgtDgrCreateCombo(fisYear, '');
        condSrcFisFgMstCdCreateCombo(fisYear, '');
        doChageCondSrcBgtDgr();
        doChageCondSrcFisFgMstCd();
        
        condSrcFrscFgCdFrCreateCombo(fisYear, '');
        condSrcFrscFgCdToCreateCombo(fisYear, '');
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
    
    var doChageCondSrcBgtDgr = function(){
        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condSrcBgtDgr option:selected", tabObj).val();
        condSrcOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
        doChangeCondSrcOfficeCd();
        
        condSrcTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condSrcTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
    };
    
    var doChageCondBgtDgr = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
        doChangeCondOfficeCd();
        
        condTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
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
    
    $("#condSrcTeMngMokCdFr", tabObj).change(function(){
        $("#condSrcTeMngMokCdTo", tabObj).val($("#condSrcTeMngMokCdFr option:selected", tabObj).val());
    });
    
    $("#condTeMngMokCdFr", tabObj).change(function(){
        $("#condTeMngMokCdTo", tabObj).val($("#condTeMngMokCdFr option:selected", tabObj).val());
    });
    
    $("#condSrcFrscFgCdFr", tabObj).change(function(){
        $("#condSrcFrscFgCdTo", tabObj).val($("#condSrcFrscFgCdFr option:selected", tabObj).val());
    });
    
    $("#condFrscFgCdFr", tabObj).change(function(){
        $("#condFrscFgCdTo", tabObj).val($("#condFrscFgCdFr option:selected", tabObj).val());
    });
    
    var openDialogBgtDeptSrcSelt = function(seltFg){

        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condSrcBgtDgr option:selected", tabObj).val();
        var officeCd = $("#condSrcOfficeCd option:selected", tabObj).val();
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("budgetPreCopyPageDialogDgrDeptSrcSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    budgetPreCopyPageDialogDgrDeptSrcSeltCallBack = function(param){
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
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("budgetPreCopyPageDialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    budgetPreCopyPageDialogDgrDeptSeltCallBack = function(param){
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

    $("#condRowNum", tabObj).change(function() {
        defaultSearchParam.rowNum = $("#condRowNum option:selected", tabObj).val();
        defaultSearchParam.page = 1;
        
        $("#searchBtn", tabObj).click();
    });

    $("#condSrcRowNum", tabObj).change(function() {
        defaultSearchSrcParam.rowNum = $("#condSrcRowNum option:selected", tabObj).val();
        defaultSearchSrcParam.page = 1;
        
        $("#srcSearchBtn", tabObj).click();
    });
    
    var comboParam = [
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "bgtDgr", subQueryId : "BgtDgr"},
                      {id : "fisFgMstCd", subQueryId : "FisFgMstCd"},
                      {id : "fisFgCd", subQueryId : "FisFgCd"},
                      {id : "officeCd", subQueryId : "OfficeCd"},
                      {id : "teMngMokCd", subQueryId : "TeMngMokCd"},
                      {id : "frscFgCd", subQueryId : "FrscFgCd"},
                      {id : "rowNum", codeId : "CC001"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);
    
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
                , {id: 'officeCd'
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
    
    var condSrcTeMngMokCdFrCreateCombo = function(groupId, selectedValue){
        $("#condSrcTeMngMokCdFr", tabObj).csCreatCombo(comboData
                , {id: 'teMngMokCd'
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
    
    var condSrcTeMngMokCdToCreateCombo = function(groupId, selectedValue){
        $("#condSrcTeMngMokCdTo", tabObj).csCreatCombo(comboData
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
    
    var condSrcFrscFgCdFrCreateCombo = function(groupId, selectedValue){
        $("#condSrcFrscFgCdFr", tabObj).csCreatCombo(comboData
                , {id: 'frscFgCd'
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
    
    var condSrcFrscFgCdToCreateCombo = function(groupId, selectedValue){
        $("#condSrcFrscFgCdTo", tabObj).csCreatCombo(comboData
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
    
    $("#condRowNum", tabObj).csCreatCombo(comboData, {
        id : 'rowNum',
        groupId : 'ALL',
        selectedValue : '3',
        comboType : '',
        comboTypeValue : ''
    });
    
    $("#condSrcRowNum", tabObj).csCreatCombo(comboData, {
        id : 'rowNum',
        groupId : 'ALL',
        selectedValue : '3',
        comboType : '',
        comboTypeValue : ''
    });
    
    doCondSrcInit();
    doCondInit();
    
    $("#applyBtn", tabObj).btnChangeState(true);
});
