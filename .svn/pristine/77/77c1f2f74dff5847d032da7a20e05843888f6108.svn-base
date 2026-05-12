<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
    $(document).ready(function() {
        var dialogObj = $("#dialogManageUserModifyDiv");
        var dialogManageUserModifyReportColNames = ['', '조서명', 'reportCd'];
        var dialogManageUserModifyReportColModel = [
                                        {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, formatter:'checkbox', editoptions:{value:'Y:N'}, formatoptions:{disabled:false}},
                                        {name : 'reportNm', index : 'reportNm', width : 170, sortable : false, fixed : true, align : 'left'},
                                        {name : 'reportCd', index : 'reportCd', width : 0, sortable : false, fixed : true, hidden : true }
        ];
        
        var dialogManageUserModifyReportGridParam = {
                id : "DIALOG_MANAGE_USER_MODIFY_REPORT",
                colNames : dialogManageUserModifyReportColNames,
                colModel : dialogManageUserModifyReportColModel,
                defaultRows : 5,
                rowNum : 1000,
                width: "220",
                height: "350"
        };
        
        var dialogManageUserModifyReportGrid = $.csGrid(dialogManageUserModifyReportGridParam);
        
        var dialogManageUserModifyDeptColNames = ['', '구분명', 'officeCd', 'deptCd', 'dgrLevel', 'dgrcompoId', 'upDgrcompoId'];
        var dialogManageUserModifyDeptColModel = [
                                        {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, formatter:'checkbox', editoptions:{value:'Y:N'}, formatoptions:{disabled:false}},
                                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 260, sortable : false, fixed : true, align : 'left'},
                                        {name : 'officeCd', index : 'officeCd', width : 0, sortable : false, fixed : true, hidden : true },
                                        {name : 'deptCd', index : 'deptCd', width : 0, sortable : false, fixed : true, hidden : true },
                                        {name : 'dgrLevel', index : 'dgrLevel', width : 0, sortable : false, hidden : true},
                                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key: true },
                                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, fixed : true, hidden : true },
        ];
        
        var dialogManageUserModifyDeptGrid = $("#DIALOG_MANAGE_USER_MODIFY_DEPT_GRD", dialogObj);
        
        var doDialogManageUserModifySearchCallBack = function(data) {
            if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
                return;
            }
            
            dialogManageUserModifyReportGrid.addCsJsonData(data.reportInfo);
            
            $("#DIALOG_MANAGE_USER_MODIFY_DEPT_GRD", dialogObj).jqGrid('GridUnload');
            dialogManageUserModifyDeptGrid = $("#DIALOG_MANAGE_USER_MODIFY_DEPT_GRD", dialogObj);
            dialogManageUserModifyDeptGrid.csTreeGrid({
                datastr : data.deptInfo,
                width: "300",
                height: "350",
                colNames : dialogManageUserModifyDeptColNames,
                colModel : dialogManageUserModifyDeptColModel,
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
                            
                            setTreeGridChecked(e, $("#DIALOG_MANAGE_USER_MODIFY_DEPT_GRD", dialogObj), $("#DIALOG_MANAGE_USER_MODIFY_DEPT_GRD", dialogObj)[0].rows, 'level');
                            setUpTreeGridCheck($("#DIALOG_MANAGE_USER_MODIFY_DEPT_GRD", dialogObj), checkedRowId, 'upDgrcompoId', 'level');
                        });
                    }
                }
            });

            data = null;
        };
        
        var doDialogManageUserSearch = function() {
            $.csAjaxCall({
                url : "/dialog/ajaxDialogManageUserRegiReportSearch.do",
                data : {
                        userId: $("#dialogManageUserModifyUserId", dialogObj).val()
                       },
                async : true,
                callBack : doDialogManageUserModifySearchCallBack
            });
        };
        
        
        dialogManageUserModifyvalidataion = function(){
            if(isRequired($("#dialogManageUserModifyPass").val()) == true && !isRequired($("#dialogManageUserModifyRePass").val())){
                $.csAlert({
                    msg : "비밀번호 확인을 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogManageUserModifyRePass").focus();
                    }
                });
                
                return false;
            }
            
            if($("#dialogManageUserModifyPass").val() != $("#dialogManageUserModifyRePass").val()){
                $.csAlert({
                    msg : "비밀번호와 비밀번호 확인이 다릅니다.",
                    callBack : function() {
                        $("#dialogManageUserModifyPass").focus();
                    }
                });
                
                return false;
            }
            
            if(!isRequired($("#dialogManageUserModifyUserNm").val())){
                $.csAlert({
                    msg : "사용자명을 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogManageUserModifyUserNm").focus();
                    }
                });
                
                return false;
            }
              
            return true;
        };
        
        var dialogManageUserModifyClose = function() {
            $("#dialogManageUserModifyCallBackFunction", dialogObj).val("");
            $("#dialogManageUserModifyUserNm", dialogObj).val("");
            $("#dialogManageUserModifyDiv").dialog('close');
        };

        var dialogManageUserModifySaveBtnClick = function() {
            
            
            if(dialogManageUserModifyvalidataion() == false){
                return;
            }
            

            $.csConfirm({
                msg : "저장하시겠습니까?",
                callBack : dialogManageUserModifyDoSave
            });
        };
        
        var dialogManageUserModifyDoSaveCallBack = function(param){
            var dialogManageUserModifyCallBackFunction = $("#dialogManageUserModifyCallBackFunction", dialogObj).val();
           
            if(isEmpty(dialogManageUserModifyCallBackFunction) == false){
                eval(dialogManageUserModifyCallBackFunction + '('+ jsonToString(param) + ')');
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
                    selectedDeptData["deptCd"] = rowData.deptCd;
                    selectedDeptData["officeCd"] = rowData.officeCd;
                    
                    selectedDeptDatas.push(selectedDeptData);
                    cnt++;
                }
            }
            
            return selectedDeptDatas;
        };
        
        dialogManageUserModifyDoSave = function(params){
            if(params.confirmData != "Y"){
                return;
            }

            var selectedReportDatas = getSelectedReportData(dialogManageUserModifyReportGrid, $("#DIALOG_MANAGE_USER_MODIFY_REPORT_GRD")[0].rows);
            var selectedDeptData = getSelectedDeptData(dialogManageUserModifyDeptGrid, $("#DIALOG_MANAGE_USER_MODIFY_DEPT_GRD")[0].rows);
            var jsonParam = {
                    modifyuserId: $("#dialogManageUserModifyUserId", dialogObj).val(),
                    loginId: $("#dialogManageUserModifyLoginId", dialogObj).val(),
                    userPass: $("#dialogManageUserModifyPass", dialogObj).val(),
                    userNm: $("#dialogManageUserModifyUserNm", dialogObj).val(),
                    powGrCd: $("#dialogManageUserModifyPowGrCd option:selected", dialogObj).val(),
                    reportDatas: selectedReportDatas,
                    deptDatas: selectedDeptData
            };
            
            var data = $.csAjaxCall({
                url : "/dialog/ajaxDialogManageUserModifySave.do",
                data : jsonParam
            });
            
            if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
                $.csAlert({
                    msg : data.bcjisMessage,
                    callBack : function() {
                        dialogManageUserModifyClose();
                    }
                });
                
                return;
            }
            
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogManageUserModifyDoSaveCallBack(data);
                    
                    dialogManageUserModifyClose();
                }
            });
        };
        
        var comboParam = [
                          {id : "powGrCd", subQueryId : "PowGrCd"}
                         ];

        var comboData = jQuery.csComboAjaxCall(comboParam);
            
        var powGrCdCreateCombo = function(groupId, selectedValue){
            $("#dialogManageUserModifyPowGrCd", dialogObj).csCreatCombo(comboData
                    , {id: 'powGrCd'
                      , groupId: 'ALL'
                      , selectedValue: selectedValue
                      , comboType: ''
                      , comboTypeValue: ''
                      }
            );
        };
        
        powGrCdCreateCombo('ALL', '');
        
        var doDialogManageUserModifyPowGrCd = function(){
            var powGrCd = $("#dialogManageUserModifyPowGrCd option:selected").val();
            if(powGrCd == "BC001" || powGrCd == "BC002"){
                $("#dialogManageUserModifyReportDiv").hide();
                $("#dialogManageUserModifyDeptDiv").hide();
            }else{
                $("#dialogManageUserModifyReportDiv").show();
                $("#dialogManageUserModifyDeptDiv").show();
            }
        };
        
        $("#dialogManageUserModifyPowGrCd").change(function(){
            doDialogManageUserModifyPowGrCd();
        });
                
        $("#dialogManageUserModifyReportSelectAllBtn").click(function(){
            $("#dialogManageUserModifyReportSelectAllBtn").hide();
            $("#dialogManageUserModifyReportUnSelectAllBtn").show();
            setGridCheckedAll(dialogManageUserModifyReportGrid, $("#DIALOG_MANAGE_USER_MODIFY_REPORT_GRD")[0].rows, "Y"); 
        });
        
        $("#dialogManageUserModifyReportUnSelectAllBtn").click(function(){
            $("#dialogManageUserModifyReportUnSelectAllBtn").hide();
            $("#dialogManageUserModifyReportSelectAllBtn").show();
            setGridCheckedAll(dialogManageUserModifyReportGrid, $("#DIALOG_MANAGE_USER_MODIFY_REPORT_GRD")[0].rows, "N");
        });
        
        $("#dialogManageUserModifyReportSelectAllBtn").btnChangeState(true);
        $("#dialogManageUserModifyReportUnSelectAllBtn").btnChangeState(true);
        
        $("#dialogManageUserModifyDeptSelectAllBtn").click(function(){
            $("#dialogManageUserModifyDeptSelectAllBtn").hide();
            $("#dialogManageUserModifyDeptUnSelectAllBtn").show();
            setGridCheckedAll(dialogManageUserModifyDeptGrid, $("#DIALOG_MANAGE_USER_MODIFY_DEPT_GRD", dialogObj)[0].rows, "Y"); 
        });
        
        $("#dialogManageUserModifyDeptUnSelectAllBtn").click(function(){
            $("#dialogManageUserModifyDeptUnSelectAllBtn").hide();
            $("#dialogManageUserModifyDeptSelectAllBtn").show();
            setGridCheckedAll(dialogManageUserModifyDeptGrid, $("#DIALOG_MANAGE_USER_MODIFY_DEPT_GRD", dialogObj)[0].rows, "N");
        });
        
        $("#dialogManageUserModifyDeptSelectAllBtn").btnChangeState(true);
        $("#dialogManageUserModifyDeptUnSelectAllBtn").btnChangeState(true);
        
        $(dialogManageUserModifyDiv).dialog({
            title : "사용자관리수정",
            autoOpen : false,
            width : 600,
            height : 720,
            modal : true,
            resizable : true,
            open : function(){
                doDialogManageUserModifyPowGrCd();
                doDialogManageUserSearch();
            },
            buttons : {
                "저장" : function() {
                    dialogManageUserModifySaveBtnClick();
                },

                "닫기" : function() {
                    dialogManageUserModifyClose();
                }
            }
        });
    });
</script>
<div id="dialogManageUserModifyDiv" class="dialog" style="display: none;">
  <input type="hidden" id="dialogManageUserModifyCallBackFunction"/>
  <div class="viewDiv" style="width: 570px;">
    <table>
      <colgroup>
        <col width="100px" />
        <col width="200px" />
      </colgroup>
      <tbody>
        <tr>
          <th>사용자ID</th>
          <td>
            <input type="hidden" id="dialogManageUserModifyUserId" />
            <input type="text" id="dialogManageUserModifyLoginId" class="invisible" style="width: 100%;" READONLY/>
          </td> 
        </tr>
        <tr>
          <th>비밀번호</th>
          <td>
            <input type="password" id="dialogManageUserModifyPass" style="width: 100%;" />
          </td>
        </tr>
        <tr>
          <th>비밀번호확인</th>
          <td>
            <input type="password" id="dialogManageUserModifyRePass" style="width: 100%;" />
          </td>
        </tr>
        <tr>
          <th>사용자명</th>
          <td>
            <input type="text" id="dialogManageUserModifyUserNm" style="width: 100%;" />
          </td>
        </tr>
        <tr>
          <th>권한</th>
          <td>
            <select id="dialogManageUserModifyPowGrCd" name="dialogManageUserModifyPowGrCd" title="권한구분" style="width:100%;">
            </select>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  <div id="dialogManageUserModifyReportDiv" style="display:none; float:left;">
    <div class="btn">
      <div class="btnL">
        <a id="dialogManageUserModifyReportSelectAllBtn" class="btnDisabledClass" href="#" enabledYn="N">전체선택</a>
        <a id="dialogManageUserModifyReportUnSelectAllBtn" class="btnDisabledClass" href="#" enabledYn="N" style="display:none;">선택해제</a>
      </div>
    </div>
    <div class="ui-widget-header" style="width:220px;">
      조서구분
    </div>
    <div id="DIALOG_MANAGE_USER_MODIFY_REPORT_DIV" class="csGrid">
      <table id="DIALOG_MANAGE_USER_MODIFY_REPORT_GRD"></table>
    </div>
  </div>
  <div id="dialogManageUserModifyDeptDiv" style="display:none; float:left; margin-left: 20px;">
    <div class="btn">
      <div class="btnL">
        <a id="dialogManageUserModifyDeptSelectAllBtn" class="btnDisabledClass" href="#" enabledYn="N">전체선택</a>
        <a id="dialogManageUserModifyDeptUnSelectAllBtn" class="btnDisabledClass" href="#" enabledYn="N" style="display:none;">선택해제</a>
      </div>
    </div>
    <div class="ui-widget-header" style="width:320px;">
      부서구분
    </div>
    <div id="DIALOG_MANAGE_USER_MODIFY_DEPT_DIV" class="csGrid" style="width:320px;">
      <table id="DIALOG_MANAGE_USER_MODIFY_DEPT_GRD"></table>
    </div>
  </div>
</div>