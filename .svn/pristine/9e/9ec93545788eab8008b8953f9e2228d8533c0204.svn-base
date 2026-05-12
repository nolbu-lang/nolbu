<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
    $(document).ready(function() {
        var dialogObj = $("#dialogPledgeBizModifyDiv");
        
        dialogPledgeBizModifyValidataion = function(){
            
            if(!isRequired($("#dialogPledgeBizModifyPledgeBizFg").val())){
                $.csAlert({
                    msg : "공약사업 분류를 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogPledgeBizModifyPledgeBizFg").focus();
                    }
                });
                
                return false;
            }
            
            if(!isRequired($("#dialogPledgeBizModifyPledgeBizNm").val())){
                $.csAlert({
                    msg : "공약사업명을 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogPledgeBizModifyPledgeBizNm").focus();
                    }
                });
                
                return false;
            }
            
            return true;
        };
        
        var dialogPledgeBizModifyClose = function() {
            $("#dialogPledgeBizModifyCallBackFunction", dialogObj).val("");
            $("#dialogPledgeBizModifyPledgeBizId", dialogObj).val("");
            $("#dialogPledgeBizModifyPledgeBizFg", dialogObj).val("");
            $("#dialogPledgeBizModifyPledgeBizNm", dialogObj).val("");
            $("#dialogPledgeBizModifyDiv").dialog('close');
        };
        
        $("#dialogPledgeBizModifyDiv").dialog({
            title : "공약사업수정",
            autoOpen : false,
            width : 500,
            height : 280,
            modal : true,
            resizable : true,
            buttons : {
                "수정" : function() {
                    dialogPledgeBizModifySaveBtnClick();
                },
               
                "닫기" : function() {
                    dialogPledgeBizModifyClose();
                }
            }
        });
        
        
        var dialogPledgeBizModifySaveBtnClick = function(){
       
            if(dialogPledgeBizModifyValidataion() == false){
                return;
            }
            
            $.csConfirm({
                msg : "수정하시겠습니까?",
                callBack : dialogPledgeBizModifyDoSave
            });
        };
        
        var dialogPledgeBizModifyDoSaveCallBack = function(param){
            var dialogPledgeBizModifyCallBackFunction = $("#dialogPledgeBizModifyCallBackFunction", dialogObj).val();
            
            if(isEmpty(dialogPledgeBizModifyCallBackFunction) == false){
                eval(dialogPledgeBizModifyCallBackFunction + '('+ jsonToString(param) + ')');
            }
        };
        
        dialogPledgeBizModifyDoSave = function(params){
            
            if(params.confirmData != "Y"){
                return;
            }

            var jsonParam = {
                    pledgeBizId: $("#dialogPledgeBizModifyPledgeBizId", dialogObj).val(),
                    pledgeBizFg: $("#dialogPledgeBizModifyPledgeBizFg", dialogObj).val(),
                    pledgeBizNm: $("#dialogPledgeBizModifyPledgeBizNm", dialogObj).val(),
                };
           
            var data = $.csAjaxCall({
                url : "/dialog/ajaxDialogPledgeBizModifyUpdatePledgeBiz.do",
                data : jsonParam
            });
            
            if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
                $.csAlert({
                    msg : data.bcjisMessage,
                    callBack : function() {
                        dialogPledgeBizModifyClose();
                    }
                });
                
                return;
            }
            
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogPledgeBizModifyDoSaveCallBack(data);
                    dialogPledgeBizModifyClose();
                }
            });
        };
        
    });
</script>
<div id="dialogPledgeBizModifyDiv" class="dialog" style="display: none;">
<input type="hidden" id="dialogPledgeBizModifyCallBackFunction"/>
<input type="hidden" id="dialogPledgeBizModifyPledgeBizId"/>
  <div class="viewDiv" style="width: 470px;">
    <table>
      <colgroup>
        <col width="120px" />
        <col width="280px" />
      </colgroup>
      <tbody>
        <tr>
          <th>공약사업 분류</th>
          <td>
            <input type="text" id="dialogPledgeBizModifyPledgeBizFg" style="width: 100%;" maxlength="50"/>
          </td>
        </tr>
        <tr>
          <th>공약사업 명</th>
          <td>
            <input type="text" id="dialogPledgeBizModifyPledgeBizNm" style="width: 100%; ime-mode: active;" maxlength="100"/>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>