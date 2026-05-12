$(document).ready(function() {
    var tabId = _pledgeManageTabId;
    var tabObj = $("#"+tabId);
    var gridScrollPosition = 0;

    
    var colNames = ['', '구분(민선-대-중-소)', 'pledgeId', 'pledgeInfoId', 'pledgeBizId', 'upPledgeBizId', 'pledgeBizFg'
                    , 'pledgeBizNm', 'pledgeBizLevel', 'pledgeBizSeq', 'pledgeInfoNm', 'pledgeBeginYmd', 'pledgeEndYmd', 'isLeaf'
                    ];
    
    var colModel = [
                        {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, frozen: true,
                            formatter: function (cellValue, option) {
                                return '<input type="radio" name="radio_' + option.gid + '"  />';
                            }
                        },
                        {name : 'pledgeInfo', index : 'pledgeInfo', width : 800, sortable : false, fixed : true, align : 'left'},
                        {name : 'pledgeId', index : 'pledgeId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'pledgeInfoId', index : 'pledgeInfoId', width : 0, sortable : false, hidden : true},
                        {name : 'pledgeBizId', index : 'pledgeBizId', width : 0, sortable : false, hidden : true},
                        {name : 'upPledgeBizId', index : 'upPledgeBizId', width : 0, sortable : false, hidden : true},
                        {name : 'pledgeBizFg', index : 'pledgeBizFg', width : 0, sortable : false, hidden : true},
                        {name : 'pledgeBizNm', index : 'pledgeBizNm', width : 0, sortable : false, hidden : true},
                        {name : 'pledgeBizLevel', index : 'pledgeBizLevel', width : 0, sortable : false, hidden : true},
                        {name : 'pledgeBizSeq', index : 'pledgeBizSeq', width : 0, sortable : false, hidden : true},
                        {name : 'pledgeInfoNm', index : 'pledgeInfoNm', width : 0, sortable : false, hidden : true},
                        {name : 'pledgeBeginYmd', index : 'pledgeBeginYmd', width : 0, sortable : false, hidden : true},
                        {name : 'pledgeEndYmd', index : 'pledgeEndYmd', width : 0, sortable : false, hidden : true},
                        {name : 'isLeaf', index : 'isLeaf', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 100 ? $("#mainCenter", tabObj).height() - 110 : 100;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#PLEDGE_MANAGE_GRD", $("#"+tabId))) == false){
            $("#PLEDGE_MANAGE_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#PLEDGE_MANAGE_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 120,
        center__onresize: mainBodyResize
    });
    
    var pledgeManageGrid = $("#PLEDGE_MANAGE_GRD", tabObj);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        $("#PLEDGE_MANAGE_GRD", tabObj).jqGrid('GridUnload');
        pledgeManageGrid = $("#PLEDGE_MANAGE_GRD", tabObj);
        pledgeManageGrid.csTreeGrid({
            datastr : data,
            height : getGridHeight(),
            colNames : colNames,
            colModel : colModel,
            ExpandColumn : "pledgeInfo",
            jsonReader : {
                repeatitems : false,
                root : "dataList"
            },
            onSelectRow: function(rowId){
            }
        });
        
        $("#PLEDGE_MANAGE_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
        $("#regiBizBtn", tabObj).btnChangeState(true);
        $("#modifyBtn", tabObj).btnChangeState(true);
        $("#deleteBtn", tabObj).btnChangeState(true);
        $("#sortBtn", tabObj).btnChangeState(true);

        data = null;
    };
    
    var getSelectedRowId = function(){
        var $selRadio = $('input[name=radio_PLEDGE_MANAGE_GRD]:checked'), $tr;
        if ($selRadio.length > 0) {
            $tr = $selRadio.closest('tr');
            if ($tr.length > 0) {
                return $tr.attr('id');
            }
        }
            
        return "";
    };
    
    var doSearch = function(){
        var pledgeInfoId = $("#condPledgeInfoId option:selected", tabObj).val();

        gridScrollPosition = $("#PLEDGE_MANAGE_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        $.csAjaxCall({
            url : "/pledge/ajaxPledgeManageSelectPledgeInfoList.do",
            data: {pledgeInfoId : pledgeInfoId
            },
            async : true,
            callBack : doSearchCallBack
        });
    };
    
    $("#searchBtn", tabObj).click(function() {
        var pledgeInfoId = $("#condPledgeInfoId option:selected", tabObj).val();
        if(isEmpty(pledgeInfoId) == true){
            $.csAlert({
                msg : "공약사업정보를 선택하여 주십시오.",
                callBack : function() {
                    $("#condPledgeInfoId", tabObj).focus();
                }
            });
            
            return;
        }
        
        gridScrollPosition = 0;
        
        doSearch();
    });
    
    var doCondInit = function(){
        $("#condPledgeInfoId", tabObj).val("");
        gridScrollPosition = 0;

    };
    
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });
    
    //공약정보등록
    pledgeManagePledgeInfoRegiDialogSaveCallBackFunction = function(param){
        doSearch();
    };
    
    $("#regiPledgeBtn", tabObj).click(function() {
        $("#dialogPledgeInfoRegiCallBackFunction", $("#dialogPledgeInfoRegiDiv")).val("pledgeManagePledgeInfoRegiDialogSaveCallBackFunction");
        $("#dialogPledgeInfoRegiDiv").dialog('open');
    });
    
    //공약사업등록
    pledgeManagePledgeBizRegiDialogSaveCallBackFunction = function(param){
        doSearch();
    };
    
    $("#regiBizBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        var rowId = getSelectedRowId();
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "상위항목을 선택하여 주십시오."
            });
            
            return;
        }
        
        var rowData = pledgeManageGrid.getRowData(rowId);
        if(rowData.pledgeBizLevel > 2){
            $.csAlert({
                msg : "최하위 사업입니다."
            });
            
            return;
        }
        
        var pledgeBizLevel = 1;
        if(isEmpty(rowData.pledgeBizLevel) == false){
            pledgeBizLevel = Number(rowData.pledgeBizLevel) + 1;
        }
        $("#dialogPledgeBizRegiCallBackFunction", $("#dialogPledgeBizRegiDiv")).val("pledgeManagePledgeBizRegiDialogSaveCallBackFunction");
        $("#dialogPledgeBizRegiPledgeInfo", $("#dialogPledgeBizRegiDiv")).val(rowData.pledgeInfo);
        $("#dialogPledgeBizRegiPledgeInfoId", $("#dialogPledgeBizRegiDiv")).val(rowData.pledgeInfoId);
        $("#dialogPledgeBizRegiUpPledgeBizId", $("#dialogPledgeBizRegiDiv")).val(rowData.pledgeBizId);
        $("#dialogPledgeBizRegiPledgeBizLevel", $("#dialogPledgeBizRegiDiv")).val(pledgeBizLevel);
        $("#dialogPledgeBizRegiDiv").dialog('open');
    });

    pledgeManagePledgeInfoModifyDialogSaveCallBackFunction = function(param){
        doSearch();
    };

    pledgeManagePledgeBizModifyDialogSaveCallBackFunction = function(param){
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

        var rowData = pledgeManageGrid.getRowData(rowId);
        if(rowData.pledgeBizLevel == "0"){
            $("#dialogPledgeInfoModifyCallBackFunction", $("#dialogPledgeInfoModifyDiv")).val("pledgeManagePledgeInfoModifyDialogSaveCallBackFunction");
            $("#dialogPledgeInfoModifyPledgeInfoId", $("#dialogPledgeInfoModifyDiv")).val(rowData.pledgeInfoId);
            $("#dialogPledgeInfoModifyPledgeInfoNm", $("#dialogPledgeInfoModifyDiv")).val(rowData.pledgeInfoNm);
            $("#dialogPledgeInfoModifyPledgeBeginYmd", $("#dialogPledgeInfoModifyDiv")).val(toDateFormat(rowData.pledgeBeginYmd));
            $("#dialogPledgeInfoModifyPledgeEndYmd", $("#dialogPledgeInfoModifyDiv")).val(toDateFormat(rowData.pledgeEndYmd));
            
            $("#dialogPledgeInfoModifyDiv").dialog('open');
        }else{
            $("#dialogPledgeBizModifyCallBackFunction", $("#dialogPledgeBizModifyDiv")).val("pledgeManagePledgeBizModifyDialogSaveCallBackFunction");
            $("#dialogPledgeBizModifyPledgeInfo", $("#dialogPledgeBizRegiDiv")).val(rowData.pledgeInfo);
            $("#dialogPledgeBizModifyPledgeBizId", $("#dialogPledgeBizModifyDiv")).val(rowData.pledgeBizId);
            $("#dialogPledgeBizModifyPledgeBizFg", $("#dialogPledgeBizModifyDiv")).val(rowData.pledgeBizFg);
            $("#dialogPledgeBizModifyPledgeBizNm", $("#dialogPledgeBizModifyDiv")).val(rowData.pledgeBizNm);
            
            $("#dialogPledgeBizModifyDiv").dialog('open');
        }
    });

    pledgeManagePledgeSortDialogSaveCallBackFunction = function(param){
        doSearch();
    };
    
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
        
        var rowData = pledgeManageGrid.getRowData(rowId);
        if(rowData.pledgeBizLevel > 2){
            $.csAlert({
                msg : "최하위 사업입니다."
            });
            
            return;
        }

        $("#dialogPledgeSortCallBackFunction", $("#dialogPledgeSortDiv")).val("pledgeManagePledgeSortDialogSaveCallBackFunction");
        $("#dialogPledgeSortUpPledgeBizId", $("#dialogPledgeSortDiv")).val(rowData.pledgeBizId);
        
        $("#dialogPledgeSortDiv").dialog('open');
    });

    var pledgeManageDoDelete = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var rowId = getSelectedRowId();
        var rowData = pledgeManageGrid.getRowData(rowId);
        
        var url = "/pledge/ajaxPledgeManageDeletePledgeBiz.do";
        if(rowData.pledgeBizLevel == "0"){
            url = "/pledge/ajaxPledgeManageDeletePledgeInfo.do";
        }
        
        var data = $.csAjaxCall({
            url : url,
            data : {
                pledgeInfoId : rowData.pledgeInfoId, 
                pledgeBizId : rowData.pledgeBizId
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

        $.csConfirm({
            msg : "삭제하시겠습니까?",
            callBack : pledgeManageDoDelete
        });
    });
    
    var comboParam = [
                      {id : "pledgeInfoId", subQueryId : "PledgeInfoId"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);
    
    $("#condPledgeInfoId", tabObj).csCreatCombo(comboData, {
        id : 'pledgeInfoId',
        groupId : 'ALL',
        selectedValue : '',
        comboType : 'S',
        comboTypeValue : ''
    });
    
    doCondInit();
});
