<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
    $(document).ready(function() {
        var dialogObj = $("#dialogPledgeInfoModifyDiv");
        
        dialogPledgeInfoModifyValidataion = function(){
            
            if(!isRequired($("#dialogPledgeInfoModifyPledgeInfoNm").val())){
                $.csAlert({
                    msg : "공약명을 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogPledgeInfoModifyPledgeInfoNm").focus();
                    }
                });
                
                return false;
            }
            
            return true;
        };
        
        var dialogPledgeInfoModifyClose = function() {
            $("#dialogPledgeInfoModifyCallBackFunction", dialogObj).val("");
            $("#dialogPledgeInfoModifyPledgeInfoId", dialogObj).val("");
            $("#dialogPledgeInfoModifyPledgeInfoNm", dialogObj).val("");
            $("#dialogPledgeInfoModifyPledgeBeginYmd", dialogObj).val("");
            $("#dialogPledgeInfoModifyPledgeEndYmd", dialogObj).val("");
            $("#dialogPledgeInfoModifyDiv").dialog('close');
        };
        
        $("#dialogPledgeInfoModifyDiv").dialog({
            title : "공약정보수정",
            autoOpen : false,
            width : 400,
            height : 200,
            modal : true,
            resizable : true,
            buttons : {
                "저장" : function() {
                    dialogPledgeInfoModifySaveBtnClick();
                },
               
                "닫기" : function() {
                    dialogPledgeInfoModifyClose();
                }
            }
        });
        
        
        var dialogPledgeInfoModifySaveBtnClick = function(){
       
            if(dialogPledgeInfoModifyValidataion() == false){
                return;
            }
            
            $.csConfirm({
                msg : "수정하시겠습니까?",
                callBack : dialogPledgeInfoModifyDoSave
            });
        };
        
        var dialogPledgeInfoModifyDoSaveCallBack = function(param){
            var dialogPledgeInfoModifyCallBackFunction = $("#dialogPledgeInfoModifyCallBackFunction", dialogObj).val();
            
            if(isEmpty(dialogPledgeInfoModifyCallBackFunction) == false){
                eval(dialogPledgeInfoModifyCallBackFunction + '('+ jsonToString(param) + ')');
            }
        };
        
        dialogPledgeInfoModifyDoSave = function(params){
            
            if(params.confirmData != "Y"){
                return;
            }

            var jsonParam = {
                    pledgeInfoId: $("#dialogPledgeInfoModifyPledgeInfoId", dialogObj).val(),
                    pledgeInfoNm: $("#dialogPledgeInfoModifyPledgeInfoNm", dialogObj).val(),
                    pledgeBeginYmd: $("#dialogPledgeInfoModifyPledgeBeginYmd", dialogObj).val().replaceAll("-", ""),
                    pledgeEndYmd: $("#dialogPledgeInfoModifyPledgeEndYmd", dialogObj).val().replaceAll("-", "")
                };
           
            var data = $.csAjaxCall({
                url : "/dialog/ajaxDialogPledgeInfoModifyUpdatePledgeInfo.do",
                data : jsonParam
            });
            
            if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
                $.csAlert({
                    msg : data.bcjisMessage,
                    callBack : function() {
                        dialogPledgeInfoModifyClose();
                    }
                });
                
                return;
            }
            
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogPledgeInfoModifyDoSaveCallBack(data);
                    dialogPledgeInfoModifyClose();
                }
            });
        };
        

        $("#dialogPledgeInfoModifyPledgeBeginYmd").csDatepicker({yearRange:'2010:c+5>'});
        $("#dialogPledgeInfoModifyPledgeEndYmd").csDatepicker({yearRange:'2010:c+5>'});
    });
</script>
<div id="dialogPledgeInfoModifyDiv" class="dialog" style="display: none;">
<input type="hidden" id="dialogPledgeInfoModifyCallBackFunction"/>
  <div class="viewDiv" style="width: 370px;">
    <table>
      <colgroup>
        <col width="120px" />
        <col width="280px" />
      </colgroup>
      <tbody>
        <tr>
          <th>공약정보명</th>
          <td>
            <input type="hidden" id="dialogPledgeInfoModifyPledgeInfoId" />
            <input type="text" id="dialogPledgeInfoModifyPledgeInfoNm" style="width: 100%; ime-mode: active;" maxlength="100" />
          </td>
        </tr>
        <tr>
          <th>관리기간</th>
          <td>
            <input type="text" name="dialogPledgeInfoModifyPledgeBeginYmd" id="dialogPledgeInfoModifyPledgeBeginYmd" style="width: 85px; "/>
            ~
            <input type="text" name="dialogPledgeInfoModifyPledgeEndYmd" id="dialogPledgeInfoModifyPledgeEndYmd" style="width: 85px; "/>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>