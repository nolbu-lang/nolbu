<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
    $(document).ready(function() {
        var dialogObj = $("#dialogPledgeInfoRegiDiv");
        
        dialogPledgeInfoRegiValidataion = function(){
            
            if(!isRequired($("#dialogPledgeInfoRegiPledgeInfoNm").val())){
                $.csAlert({
                    msg : "공약명을 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogPledgeInfoRegiPledgeInfoNm").focus();
                    }
                });
                
                return false;
            }
            
            return true;
        };
        
        var dialogPledgeInfoRegiClose = function() {
            $("#dialogPledgeInfoRegiCallBackFunction", dialogObj).val("");
            $("#dialogPledgeInfoRegiPledgeInfoId", dialogObj).val("");
            $("#dialogPledgeInfoRegiPledgeInfoNm", dialogObj).val("");
            $("#dialogPledgeInfoRegiPledgeBeginYmd", dialogObj).val("");
            $("#dialogPledgeInfoRegiPledgeEndYmd", dialogObj).val("");
            $("#dialogPledgeInfoRegiDiv").dialog('close');
        };
        
        $("#dialogPledgeInfoRegiDiv").dialog({
            title : "공약정보등록",
            autoOpen : false,
            width : 400,
            height : 200,
            modal : true,
            resizable : true,
            buttons : {
                "등록" : function() {
                    dialogPledgeInfoRegiSaveBtnClick();
                },
               
                "닫기" : function() {
                    dialogPledgeInfoRegiClose();
                }
            }
        });
        
        
        var dialogPledgeInfoRegiSaveBtnClick = function(){
       
            if(dialogPledgeInfoRegiValidataion() == false){
                return;
            }
            
            $.csConfirm({
                msg : "등록하시겠습니까?",
                callBack : dialogPledgeInfoRegiDoSave
            });
        };
        
        var dialogPledgeInfoRegiDoSaveCallBack = function(param){
            var dialogPledgeInfoRegiCallBackFunction = $("#dialogPledgeInfoRegiCallBackFunction", dialogObj).val();
            
            if(isEmpty(dialogPledgeInfoRegiCallBackFunction) == false){
                eval(dialogPledgeInfoRegiCallBackFunction + '('+ jsonToString(param) + ')');
            }
        };
        
        dialogPledgeInfoRegiDoSave = function(params){
            
            if(params.confirmData != "Y"){
                return;
            }

            var jsonParam = {
                    pledgeInfoNm: $("#dialogPledgeInfoRegiPledgeInfoNm", dialogObj).val(),
                    pledgeBeginYmd: $("#dialogPledgeInfoRegiPledgeBeginYmd", dialogObj).val().replaceAll("-", ""),
                    pledgeEndYmd: $("#dialogPledgeInfoRegiPledgeEndYmd", dialogObj).val().replaceAll("-", "")
                };
           
            var data = $.csAjaxCall({
                url : "/dialog/ajaxDialogPledgeInfoRegiInsertPledgeInfo.do",
                data : jsonParam
            });
            
            if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
                $.csAlert({
                    msg : data.bcjisMessage,
                    callBack : function() {
                        dialogPledgeInfoRegiClose();
                    }
                });
                
                return;
            }
            
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogPledgeInfoRegiDoSaveCallBack(data);
                    dialogPledgeInfoRegiClose();
                }
            });
        };
        

        $("#dialogPledgeInfoRegiPledgeBeginYmd").csDatepicker({yearRange:'2010:c+5>'});
        $("#dialogPledgeInfoRegiPledgeEndYmd").csDatepicker({yearRange:'2010:c+5>'});
    });
</script>
<div id="dialogPledgeInfoRegiDiv" class="dialog" style="display: none;">
<input type="hidden" id="dialogPledgeInfoRegiCallBackFunction"/>
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
            <input type="text" id="dialogPledgeInfoRegiPledgeInfoNm" style="width: 100%;" maxlength="100" />
          </td>
        </tr>
        <tr>
          <th>관리기간</th>
          <td>
            <input type="text" name="dialogPledgeInfoRegiPledgeBeginYmd" id="dialogPledgeInfoRegiPledgeBeginYmd" style="width: 85px; ime-mode: active;"/>
            ~
            <input type="text" name="dialogPledgeInfoRegiPledgeBeginYmd" id="dialogPledgeInfoRegiPledgeEndYmd" style="width: 85px; ime-mode: active;"/>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>