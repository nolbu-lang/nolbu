<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
    $(document).ready(function() {
        var dialogObj = $("#dialogManageUserRegiDiv");
        var dialogManageUserRegiReportColNames = ['', '구분명', 'reportCd'];
        var dialogManageUserRegiReportColModel = [
                                        {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, formatter:'checkbox', editoptions:{value:'Y:N'}, formatoptions:{disabled:false}},
                                        {name : 'reportNm', index : 'reportNm', width : 170, sortable : false, fixed : true, align : 'left'},
                                        {name : 'reportCd', index : 'reportCd', width : 0, sortable : false, fixed : true, hidden : true }
        ];
        
        var dialogManageUserRegiReportGridParam = {
                id : "DIALOG_MANAGE_USER_REGI_REPORT",
                colNames : dialogManageUserRegiReportColNames,
                colModel : dialogManageUserRegiReportColModel,
                defaultRows : 5,
                rowNum : 1000,
                width: "220",
                height: "350"
        };
        
        var dialogManageUserRegiReportGrid = $.csGrid(dialogManageUserRegiReportGridParam);

        var dialogManageUserRegiDeptColNames = ['', '구분명', 'officeCd', 'deptCd', 'dgrLevel', 'dgrcompoId', 'upDgrcompoId'];
        var dialogManageUserRegiDeptColModel = [
                                        {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, formatter:'checkbox', editoptions:{value:'Y:N'}, formatoptions:{disabled:false}},
                                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 280, sortable : false, fixed : true, align : 'left'},
                                        {name : 'officeCd', index : 'officeCd', width : 0, sortable : false, fixed : true, hidden : true },
                                        {name : 'deptCd', index : 'deptCd', width : 0, sortable : false, fixed : true, hidden : true },
                                        {name : 'dgrLevel', index : 'dgrLevel', width : 0, sortable : false, hidden : true},
                                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key: true },
                                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, fixed : true, hidden : true },
        ];
        
        var dialogManageUserRegiDeptGrid = $("#DIALOG_MANAGE_USER_REGI_DEPT_GRD", dialogObj);
        
        var doDialogManageRegiUserSearchCallBack = function(data) {
            if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
                return;
            }
            
            dialogManageUserRegiReportGrid.addCsJsonData(data.reportInfo);
            
            $("#DIALOG_MANAGE_USER_REGI_DEPT_GRD", dialogObj).jqGrid('GridUnload');
            dialogManageUserRegiDeptGrid = $("#DIALOG_MANAGE_USER_REGI_DEPT_GRD", dialogObj);
            dialogManageUserRegiDeptGrid.csTreeGrid({
                datastr : data.deptInfo,
                width: "300",
                height: "350",
                colNames : dialogManageUserRegiDeptColNames,
                colModel : dialogManageUserRegiDeptColModel,
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
                            
                            setTreeGridChecked(e, $("#DIALOG_MANAGE_USER_REGI_DEPT_GRD", dialogObj), $("#DIALOG_MANAGE_USER_REGI_DEPT_GRD", dialogObj)[0].rows, 'level');
                            setUpTreeGridCheck($("#DIALOG_MANAGE_USER_REGI_DEPT_GRD", dialogObj), checkedRowId, 'upDgrcompoId', 'level');
                        });
                    }
                }
            });

            data = null;
        };
        
        var doDialogManageUserSearch = function() {
            $.csAjaxCall({
                url : "/dialog/ajaxDialogManageUserRegiReportSearch.do",
                data : {userId: ""},
                async : true,
                callBack : doDialogManageRegiUserSearchCallBack
            });
        };
        
        dialogManageUserRegivalidataion = function(){
            
            if(!isRequired($("#dialogManageUserRegiLoginId").val())){
                $.csAlert({
                    msg : "사용자ID을 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogManageUserRegiLoginId").focus();
                    }
                });
                
                return false;
            }

            if($("#checkLoginIdYn").val() != "Y"){
                $.csAlert({
                    msg : "아이디 중복체크 하여 주십시오.",
                    callBack : function() {
                        $("#dialogManageUserRegicheckLoginIdBtn").focus();
                    }
                });
                
                return false;
            }
                              
            if(!isRequired($("#dialogManageUserRegiUserPass").val())){
                $.csAlert({
                    msg : "비밀번호을 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogManageUserRegiUserPass").focus();
                    }
                });
                
                return false;
            }
            
            if($("#dialogManageUserRegiUserPass").val().length < 2){
                $.csAlert({
                    msg : "비밀번호는 8자리 이상 입력하여야 합니다.",
                    callBack : function() {
                        $("#dialogManageUserRegiUserPass").focus();
                    }
                });
                
                return false;
            }
            
            if(!isRequired($("#dialogManageUserRegiReUserPass").val())){
                $.csAlert({
                    msg : "비밀번호 확인을 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogManageUserRegiReUserPass").focus();
                    }
                });
                
                return false;
            }
            
            if($("#dialogManageUserRegiUserPass").val() != $("#dialogManageUserRegiReUserPass").val()){
                $.csAlert({
                    msg : "비밀번호와 비밀번호 확인이 다릅니다.",
                    callBack : function() {
                        $("#dialogManageUserRegiUserPass").focus();
                    }
                });
                
                return false;
            }
            
            if(!isRequired($("#dialogManageUserRegiUserNm").val())){
                $.csAlert({
                    msg : "사용자명을 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogManageUserRegiUserNm").focus();
                    }
                });
                
                return false;
            }
            
            if(!isRequired($("#dialogManageUserRegiPowGrCd").val())){
                $.csAlert({
                    msg : "권한을 선택하여 주십시오.",
                    callBack : function() {
                        $("#dialogManageUserRegiPowGrCd").focus();
                    }
                });
                
                return false;
            }
            
            
            return true;
        };
        
        var dialogManageUserRegiClose = function() {
            $("#dialogManageUserRegiCallBackFunction", dialogObj).val("");
            $("#dialogManageUserRegiLoginId", dialogObj).val("");
            $("#dialogManageUserRegiUserPass", dialogObj).val("");
            $("#dialogManageUserRegiReUserPass", dialogObj).val("");
            $("#dialogManageUserRegiUserNm", dialogObj).val("");
            $("#dialogManageUserRegiPowGrCd", dialogObj).val("");
            $("#dialogManageUserRegiDiv").dialog('close');
        };
        
        $("#dialogManageUserRegiDiv").dialog({
            title : "사용자관리등록",
            autoOpen : false,
            width : 600,
            height : 720,
            modal : true,
            resizable : true,
            open : function(){
                
            },
            buttons : {
                "등록" : function() {
                    dialogManageUserRegiSaveBtnClick();
                },
               
                "닫기" : function() {
                    dialogManageUserRegiClose();
                }
            }
        });
        
        var dialogManageUserRegiSaveBtnClick = function(){
       
            if(dialogManageUserRegivalidataion() == false){
                return;
            }
            
            $.csConfirm({
                msg : "등록하시겠습니까?",
                callBack : dialogManageUserRegiDoSave
            });
        };
        
        var dialogManageUserRegiDoSaveCallBack = function(param){
            var dialogManageUserRegiCallBackFunction = $("#dialogManageUserRegiCallBackFunction", dialogObj).val();
            
            if(isEmpty(dialogManageUserRegiCallBackFunction) == false){
                eval(dialogManageUserRegiCallBackFunction + '('+ jsonToString(param) + ')');
            }
        };
                
        var getSelectedReportData = function(gridObject, gridRows){
            var selectedReportDatas = [];
            var selectedReportData = {};
            var rowId;
            var rowData;
            var cnt = 0;
            for(var i = 0; i < gridRows.length; i++) {
                rowId = gridRows[i].id;
                rowData = gridObject.getRowData(rowId);

                if(rowData.selYn == "Y"){
                    selectedReportData = {};
                    selectedReportData["reportCd"] = rowData.reportCd;
                    
                    selectedReportDatas.push(selectedReportData);
                    cnt++;
                }
            }
            
            return selectedReportDatas;
        };
                
        var getSelectedDeptData = function(gridObject, gridRows){
            var selectedDeptDatas = [];
            var selectedDeptData = {};
            var rowId;
            var rowData;
            var cnt = 0;
            for(var i = 0; i < gridRows.length; i++) {
                rowId = gridRows[i].id;
                rowData = gridObject.getRowData(rowId);

                if(rowData.selYn == "Y" && rowData.deptCd != "0000000"){
                    selectedDeptData = {};
                    selectedDeptData["deptCd"] = rowData.deptCd;;
                    selectedDeptData["officeCd"] = rowData.officeCd;
                    
                    selectedDeptDatas.push(selectedDeptData);
                    cnt++;
                }
            }
            
            return selectedDeptDatas;
        };
        
        dialogManageUserRegiDoSave = function(params){
            
            if(params.confirmData != "Y"){
                return;
            }

            var selectedReportDatas = getSelectedReportData(dialogManageUserRegiReportGrid, $("#DIALOG_MANAGE_USER_REGI_REPORT_GRD")[0].rows);
            var selectedDeptData = getSelectedDeptData(dialogManageUserRegiDeptGrid, $("#DIALOG_MANAGE_USER_REGI_DEPT_GRD")[0].rows);
            var jsonParam = {
                    loginId: $("#dialogManageUserRegiLoginId", dialogObj).val(),
                    userPass: $("#dialogManageUserRegiUserPass", dialogObj).val(),
                    userNm: $("#dialogManageUserRegiUserNm", dialogObj).val(),
                    powGrCd: $("#dialogManageUserRegiPowGrCd option:selected", dialogObj).val(),
                    reportDatas: selectedReportDatas,
                    deptDatas: selectedDeptData
            };
           
            var data = $.csAjaxCall({
                url : "/dialog/ajaxDialogManageUserRegiSaveUser.do",
                data : jsonParam
            });
            
            if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
                $.csAlert({
                    msg : data.bcjisMessage,
                    callBack : function() {
                        dialogManageUserRegiClose();
                    }
                });
                
                return;
            }
            
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogManageUserRegiDoSaveCallBack(data);
                    dialogManageUserRegiClose();
                }
            });
        };
        
        var comboParam = [
                          {id : "powGrCd", subQueryId : "PowGrCd"}
                         ];

        var comboData = jQuery.csComboAjaxCall(comboParam);
            
        var powGrCdCreateCombo = function(groupId, selectedValue){
            $("#dialogManageUserRegiPowGrCd", dialogObj).csCreatCombo(comboData
                    , {id: 'powGrCd'
                      , groupId: 'ALL'
                      , selectedValue: selectedValue
                      , comboType: ''
                      , comboTypeValue: ''
                      }
            );
        };
        
        powGrCdCreateCombo('ALL', '');
        
        $("#dialogManageUserRegicheckLoginIdBtn").click(function(){
            
            if(!isRequired($("#dialogManageUserRegiLoginId").val())){
                $.csAlert({
                    msg : "사용자ID을 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogManageUserRegiLoginId").focus();
                    }
                });
                
                return false;
            }

            var data = $.csAjaxCall({
                url : "/dialog/ajaxDialogManageUserCheckLoginId.do",
                data : {loginId: $("#dialogManageUserRegiLoginId").val()}
            });
            
            if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
                $.csAlert({
                    msg : data.bcjisMessage
                });
                
                return;
            }
            
            if(data.checkUserYn != "Y"){
                $.csAlert({
                    msg : "이미 사용중인 ID입니다.",
                    callBack : function() {
                        $("#dialogManageUserRegiLoginId").focus();
                    }
                });
                
                return;
            }else{
                $.csAlert({
                    msg : "사용 가능한 ID입니다.",
                    callBack : function() {
                        $("#dialogManageUserRegiLoginId").focus();
                    }
                });
                
                $("#checkLoginIdYn").val("Y");
            }
  
        });
        
        $("#dialogManageUserRegiLoginId").keypress(function(event){
            $("#checkLoginIdYn").val("N");
        });

        $("#checkLoginIdYn").val("N");
        $("#dialogManageUserRegiLoginId").isAlphaNumberic2();
        
        var doDialogManageUserRegiPowGrCd = function(){
            var powGrCd = $("#dialogManageUserRegiPowGrCd option:selected").val();
            if(powGrCd == "BC001" || powGrCd == "BC002"){
                $("#dialogManageUserRegiReportDiv").hide();
                $("#dialogManageUserRegiDeptDiv").hide();
            }else{
                $("#dialogManageUserRegiReportDiv").show();
                $("#dialogManageUserRegiDeptDiv").show();
            }
        };
        
        $("#dialogManageUserRegiPowGrCd").change(function(){
            doDialogManageUserRegiPowGrCd();
        });
        
        $("#dialogManageUserRegiReportSelectAllBtn").click(function(){
            $("#dialogManageUserRegiReportSelectAllBtn").hide();
            $("#dialogManageUserRegiReportUnSelectAllBtn").show();
            setGridCheckedAll(dialogManageUserRegiReportGrid, $("#DIALOG_MANAGE_USER_REGI_REPORT_GRD")[0].rows, "Y"); 
        });
        
        $("#dialogManageUserRegiReportUnSelectAllBtn").click(function(){
            $("#dialogManageUserRegiReportUnSelectAllBtn").hide();
            $("#dialogManageUserRegiReportSelectAllBtn").show();
            setGridCheckedAll(dialogManageUserRegiReportGrid, $("#DIALOG_MANAGE_USER_REGI_REPORT_GRD")[0].rows, "N");
        });
        
        $("#dialogManageUserRegiReportSelectAllBtn").btnChangeState(true);
        $("#dialogManageUserRegiReportUnSelectAllBtn").btnChangeState(true);
        
        $("#dialogManageUserRegiDeptSelectAllBtn").click(function(){
            $("#dialogManageUserRegiDeptSelectAllBtn").hide();
            $("#dialogManageUserRegiDeptUnSelectAllBtn").show();
            setGridCheckedAll(dialogManageUserRegiDeptGrid, $("#DIALOG_MANAGE_USER_REGI_DEPT_GRD", dialogObj)[0].rows, "Y"); 
        });
        
        $("#dialogManageUserRegiDeptUnSelectAllBtn").click(function(){
            $("#dialogManageUserRegiDeptUnSelectAllBtn").hide();
            $("#dialogManageUserRegiDeptSelectAllBtn").show();
            setGridCheckedAll(dialogManageUserRegiDeptGrid, $("#DIALOG_MANAGE_USER_REGI_DEPT_GRD", dialogObj)[0].rows, "N");
        });
        
        $("#dialogManageUserRegiDeptSelectAllBtn").btnChangeState(true);
        $("#dialogManageUserRegiDeptUnSelectAllBtn").btnChangeState(true);
        
        doDialogManageUserSearch();
    });
</script>
<div id="dialogManageUserRegiDiv" class="dialog" style="display: none;">
<input type="hidden" id="dialogManageUserRegiCallBackFunction"/>
  <div class="viewDiv" style="width: 570px;">
    <table>
      <colgroup>
        <col width="120px" />
        <col width="200px" />
        <col width="80px" />
      </colgroup>
      <tbody>
        <tr>
          <th>ID</th>
          <td>
            <input type="hidden" id="checkLoginIdYn"/>
            <input type="text" id="dialogManageUserRegiLoginId" style="width: 100%;" />
          </td>
          <td>
              <a id="dialogManageUserRegicheckLoginIdBtn" href="#" style="text-decoration: none;font-weight: bold;color: #494949;">중복체크</a>
          </td>
        </tr>
        <tr>
          <th>비밀번호</th>
          <td>
            <input type="password" id="dialogManageUserRegiUserPass" style="width: 100%;" />
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <th>비밀번호확인</th>
          <td>
            <input type="password" id="dialogManageUserRegiReUserPass" style="width: 100%;" />
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <th>성명</th>
          <td>
            <input type="text" id="dialogManageUserRegiUserNm" style="width: 100%;" />
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <th>권한</th>
          <td>
            <select id="dialogManageUserRegiPowGrCd" name="dialogManageUserRegiPowGrCd" title="권한구분" style="width:100%;">
            </select>
          </td>
          <td>&nbsp;</td>
        </tr>
      </tbody>
    </table>
  </div>
  <div id="dialogManageUserRegiReportDiv" style="display:none; float:left;">
    <div class="btn">
      <div class="btnL">
        <a id="dialogManageUserRegiReportSelectAllBtn" class="btnDisabledClass" href="#" enabledYn="N">전체선택</a>
        <a id="dialogManageUserRegiReportUnSelectAllBtn" class="btnDisabledClass" href="#" enabledYn="N" style="display:none;">선택해제</a>
      </div>
    </div>
    <div class="ui-widget-header" style="width:220px;">
      조서구분
    </div>
    <div id="DIALOG_MANAGE_USER_REGI_REPORT_DIV" class="csGrid">
      <table id="DIALOG_MANAGE_USER_REGI_REPORT_GRD"></table>
    </div>
  </div>
  <div id="dialogManageUserRegiDeptDiv" style="display:none;float:left;margin-left: 20px;">
    <div class="btn">
      <div class="btnL">
        <a id="dialogManageUserRegiDeptSelectAllBtn" class="btnDisabledClass" href="#" enabledYn="N">전체선택</a>
        <a id="dialogManageUserRegiDeptUnSelectAllBtn" class="btnDisabledClass" href="#" enabledYn="N" style="display:none;">선택해제</a>
      </div>
    </div>
    <div class="ui-widget-header" style="width:320px;">
      부서구분
    </div>
    <div id="DIALOG_MANAGE_USER_REGI_DEPT_DIV" class="csGrid">
      <table id="DIALOG_MANAGE_USER_REGI_DEPT_GRD"></table>
    </div>
  </div>
</div>