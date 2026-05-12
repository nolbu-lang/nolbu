$(document).ready(function() {
    var tabId = _manageUserTabId;
    var tabObj = $("#"+tabId);
    
    var manageUsercolNames = ['', 
                    '순번', 
                    '사용자ID', 
                    '사용자명',
                    '권한명',
                    '최종접속시각',
                    '최종접속IP',
                    'userId',
                    'powGrCd',
                    'pswd'
                   ];
    
    var manageUsercolModel = [
                        {name : 'selYn', index:'selYn', width: 50, align:'center', sortable : false, fixed : true, frozen: true,
                            formatter: function (cellValue, option) {
                                return '<input type="radio" name="radio_' + option.gid + '"  />';
                            }
                        },
                        {name : 'rowNum', index : 'rowNum', width : 83, sortable : false, fixed : true, align : 'center'},
                        {name : 'loginId', index : 'loginId', width : 100, sortable : false, fixed : true, align : 'left'},
                        {name : 'userNm', index : 'userNm', width : 80, sortable : false, fixed : true, align : 'left'},
                        {name : 'powGrNm', index : 'powGrNm', width : 80, sortable : false, fixed : true, align : 'left'},
                        {name : 'finalConnectDate', index : 'finalConnectDate', width : 150, sortable : false, fixed : true, align : 'left'},
                        {name : 'finalConnectIp', index : 'finalConnectIp', width : 150, sortable : false, fixed : true, align : 'left'},
                        {name : 'userId', index : 'userId', width : 0, sortable : false, fixed : true, hidden : true },
                        {name : 'powGrCd', index : 'powGrCd', width : 0, sortable : false, fixed : true, hidden : true },
                        {name : 'pswd', index : 'pswd', width : 0, sortable : false, fixed : true, hidden : true }
                    ];
   
    
    manageUserPageSearch = function(page) {
        manageUserSearch({
            page : page
        });
    };
    
    var manageUserSearchParam = {
            page : 1,
            rowNum : 10
    };
    
    $("#searchBtn", tabObj).click(function() {
        manageUserSearch({
            page : 1
        });
    });  
    
    manageUserModifyDialogSaveCallBackFunction = function(param){
        manageUserSearch();
    };
    
    manageUserRegiDialogSaveCallBackFunction = function(param){
        manageUserSearch();
    };
    
    var getManageUserSearchParam = function(params) {

        var searchParam = {
             userid : $("#condLoginId", tabObj).val(),
             usernm : $("#condUserNm", tabObj).val()
        };

        $.extend(manageUserSearchParam, searchParam);
        $.extend(manageUserSearchParam, params);

        return manageUserSearchParam;
    };
    
    var manageUsergridParam = {
            id : "MANAGE_USER_LIST",
            colNames : manageUsercolNames,
            colModel : manageUsercolModel,
            onSelectRow: function(rowId){
            }
    };
    
    var manageUserSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        manageUsergridGrid.addCsJsonData(data);
        
        $("#MANAGE_USER_LIST_PGR").addPagingData(data, "manageUserPageSearch");
        $("#MANAGE_USER_LIST_TOT").html("총건수 : " + addCommaStr(data.totalCount) + "건");
    };
    
    var manageUsergridGrid = $.csGrid(manageUsergridParam);
    
    var manageUserSearch = function(params) {
        $.csAjaxCall({
            url : "/manage/ajaxManageUserList.do",
            data : getManageUserSearchParam(params),
            async : true,
            callBack : manageUserSearchCallBack
        });
    };
    
    $("#mainBody", tabObj).layout({
        north__size : 105,
        center__onresize: function () {
            if(isEmpty($("#MANAGE_USER_LIST_GRD", $("#"+tabId))) == false){
                $("#MANAGE_USER_LIST_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            }
        }
    });
    
    var getSelectedRowId = function(){
        var $selRadio = $('input[name=radio_MANAGE_USER_LIST_GRD]:checked'), $tr;
        if ($selRadio.length > 0) {
            $tr = $selRadio.closest('tr');
            if ($tr.length > 0) {
                return $tr.attr('id');
            }
        }
            
        return "";
    };
    
    //등록
    $("#regiBtn", tabObj).click(function() {
        $("#dialogManageUserRegiCallBackFunction", $("#dialogManageUserRegiDiv")).val("manageUserRegiDialogSaveCallBackFunction");
        $("#checkLoginIdYn", $("#dialogManageUserRegiDiv")).val("N");
        $("#dialogManageUserRegiLoginId", $("#dialogManageUserRegiDiv")).val("");
        $("#dialogManageUserRegiUserPass", $("#dialogManageUserRegiDiv")).val("");
        $("#dialogManageUserRegiReUserPass", $("#dialogManageUserRegiDiv")).val("");
        $("#dialogManageUserRegiUserNm", $("#dialogManageUserRegiDiv")).val("");
        $("#dialogManageUserRegiPowGrCd", $("#dialogManageUserRegiDiv")).val("BC001");

        $("#dialogManageUserRegiReportSelectAllBtn").show();
        $("#dialogManageUserRegiReportUnSelectAllBtn").hide();

        $("#dialogManageUserRegiDeptSelectAllBtn").show();
        $("#dialogManageUserRegiDeptUnSelectAllBtn").hide();
        
        $("#dialogManageUserRegiDiv").dialog('open');
    });
    
    //수정
    $("#modifyBtn", tabObj).click(function() {
    
        var rowId = getSelectedRowId();
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "수정할 항목을 선택하여 주십시오."
            });
            
            return;
        }
        var rowData = manageUsergridGrid.getRowData(rowId);
        
        $("#dialogManageUserModifyCallBackFunction", $("#dialogManageUserModifyDiv")).val("manageUserModifyDialogSaveCallBackFunction");
        $("#dialogManageUserModifyUserId", $("#dialogManageUserModifyDiv")).val(rowData.userId);
        $("#dialogManageUserModifyLoginId", $("#dialogManageUserModifyDiv")).val(rowData.loginId);
        //$("#dialogManageUserModifyPass", $("#dialogManageUserModifyDiv")).val(rowData.pswd);
        //$("#dialogManageUserModifyRePass", $("#dialogManageUserModifyDiv")).val(rowData.pswd);
        $("#dialogManageUserModifyPass", $("#dialogManageUserModifyDiv")).val("");
        $("#dialogManageUserModifyRePass", $("#dialogManageUserModifyDiv")).val("");
        $("#dialogManageUserModifyUserNm", $("#dialogManageUserModifyDiv")).val(rowData.userNm);
        $("#dialogManageUserModifyPowGrCd", $("#dialogManageUserModifyDiv")).val(rowData.powGrCd);

        $("#dialogManageUserModifyReportSelectAllBtn").show();
        $("#dialogManageUserModifyReportUnSelectAllBtn").hide();

        $("#dialogManageUserModifyDeptSelectAllBtn").show();
        $("#dialogManageUserModifyDeptUnSelectAllBtn").hide();
        
        $("#dialogManageUserModifyDiv").dialog('open');
    });
    
    //삭제
    $("#deleteBtn", tabObj).click(function() {
        
        var rowId = getSelectedRowId();
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "삭제할 항목을 선택하여 주십시오."
            });
            
            return;
        }
       
        $.csConfirm({
            msg : "삭제하시겠습니까?",
            callBack : userModifyDoDelete
        });
    });
    
    userModifyDoDelete = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var rowId = getSelectedRowId();
        var rowData = manageUsergridGrid.getRowData(rowId);
        
        var data = $.csAjaxCall({
            url : "/manage/ajaxManageUserDeleteUserList.do",
            data : {
                deleteUserId : rowData.userId
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
                manageUserSearch();
            }
        });
    };
    
    $("#condLoginId,#condUserNm").keypress(function(event){
        if(event.which == 13){
            $("#searchBtn").click();    
        }
    });
    
    manageUserSearch();
    
});
