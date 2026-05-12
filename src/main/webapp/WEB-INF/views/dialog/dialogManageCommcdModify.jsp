<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
    $(document).ready(function() {
        var dialogObj = $("#dialogManageCommcdModifyDiv");

        var dialogManageCommcdModifyClose = function() {
            $("#dialogManageCommcdModifyCallBackFunction", dialogObj).val("");
            $("#dialogManageCommcdModifyDetlCdNm", dialogObj).val("");
            $("#dialogManageCommcdModifyDiv").dialog('close');
        };

        var dialogManageCommcdModifySaveBtnClick = function() {
            if (isEmpty($("#dialogManageCommcdModifyDetlCdNm", dialogObj).val()) == true) {
                $.csAlert({
                    msg : "코드명명을 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogManageCommcdModifyDetlCdNm", dialogObj).focus();
                    }
                });

                return false;
            }

            $.csConfirm({
                msg : "저장하시겠습니까?",
                callBack : dialogManageCommcdModifyDoSave
            });
        };
        
        var dialogManageCommcdModifyDoSaveCallBack = function(param){
            var dialogManageCommcdModifyCallBackFunction = $("#dialogManageCommcdModifyCallBackFunction", dialogObj).val();
           
            if(isEmpty(dialogManageCommcdModifyCallBackFunction) == false){
                eval(dialogManageCommcdModifyCallBackFunction + '('+ jsonToString(param) + ')');
            }
        };
        
        dialogManageCommcdModifyDoSave = function(params){
            if(params.confirmData != "Y"){
                return;
            }

            var jsonParam = {
                detlCdNm : $("#dialogManageCommcdModifyDetlCdNm").val(),
                detlCd : $("#dialogManageCommcdModifyDetlCd").val(),
                clCd : $("#dialogManageCommcdModifyClCd").val()
            };
            
            var data = $.csAjaxCall({
                url : "/dialog/ajaxDialogManageCommcdModifySave.do",
                data : jsonParam
            });
            
            if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
                $.csAlert({
                    msg : data.bcjisMessage,
                    callBack : function() {
                        dialogManageCommcdModifyClose();
                    }
                });
                
                return;
            }
            
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogManageCommcdModifyDoSaveCallBack(data);
                    
                    dialogManageCommcdModifyClose();
                }
            });
        };
        
        $("#dialogManageCommcdModifyDiv").dialog({
            title : "코드관리수정",
            autoOpen : false,
            width : 400,
            height : 180,
            modal : true,
            resizable : true,

            buttons : {
                "저장" : function() {
                    dialogManageCommcdModifySaveBtnClick();
                },

                "닫기" : function() {
                    dialogManageCommcdModifyClose();
                }
            }
        });
    });
</script>
<div id="dialogManageCommcdModifyDiv" class="dialog" style="display: none;">
  <input type="hidden" id="dialogManageCommcdModifyCallBackFunction"/>
  <div class="viewDiv" style="width: 370px;">
    <table>
      <colgroup>
        <col width="100px" />
        <col width="200px" />
      </colgroup>
      <tbody>
        <tr>
          <th>코드명</th>
          <td>
            <input type="hidden" id="dialogManageCommcdModifyClCd" /> 
            <input type="hidden" id="dialogManageCommcdModifyDetlCd" /> 
            <input type="text" id="dialogManageCommcdModifyDetlCdNm" style="width: 100%;" />
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>