<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
    $(document).ready(function() {
        var dialogObj = $("#dialogPledgeBizRegiDiv");
        
        dialogPledgeBizRegiValidataion = function(){
            
            if(!isRequired($("#dialogPledgeBizRegiPledgeBizFg").val())){
                $.csAlert({
                    msg : "공약사업 분류를 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogPledgeBizRegiPledgeBizFg").focus();
                    }
                });
                
                return false;
            }
            
            if(!isRequired($("#dialogPledgeBizRegiPledgeBizNm").val())){
                $.csAlert({
                    msg : "공약사업명을 입력하여 주십시오.",
                    callBack : function() {
                        $("#dialogPledgeBizRegiPledgeBizNm").focus();
                    }
                });
                
                return false;
            }
            
            return true;
        };
        
        var dialogPledgeBizRegiClose = function() {
            $("#dialogPledgeBizRegiCallBackFunction", dialogObj).val("");
            $("#dialogPledgeBizRegiPledgeInfoId", dialogObj).val("");
            $("#dialogPledgeBizRegiPledgeBizId", dialogObj).val("");
            $("#dialogPledgeBizRegiUpPledgeBizId", dialogObj).val("");
            $("#dialogPledgeBizRegiPledgeBizFg", dialogObj).val("");
            $("#dialogPledgeBizRegiPledgeBizNm", dialogObj).val("");
            $("#dialogPledgeBizRegiPledgeBizLevel", dialogObj).val("");
            $("#dialogPledgeBizRegiDiv").dialog('close');
        };
        
        $("#dialogPledgeBizRegiDiv").dialog({
            title : "공약사업등록",
            autoOpen : false,
            width : 500,
            height : 280,
            modal : true,
            resizable : true,
            buttons : {
                "등록" : function() {
                    dialogPledgeBizRegiSaveBtnClick();
                },
               
                "닫기" : function() {
                    dialogPledgeBizRegiClose();
                }
            }
        });
        
        
        var dialogPledgeBizRegiSaveBtnClick = function(){
       
            if(dialogPledgeBizRegiValidataion() == false){
                return;
            }
            
            $.csConfirm({
                msg : "등록하시겠습니까?",
                callBack : dialogPledgeBizRegiDoSave
            });
        };
        
        var dialogPledgeBizRegiDoSaveCallBack = function(param){
            var dialogPledgeBizRegiCallBackFunction = $("#dialogPledgeBizRegiCallBackFunction", dialogObj).val();
            
            if(isEmpty(dialogPledgeBizRegiCallBackFunction) == false){
                eval(dialogPledgeBizRegiCallBackFunction + '('+ jsonToString(param) + ')');
            }
        };
        
        dialogPledgeBizRegiDoSave = function(params){
            
            if(params.confirmData != "Y"){
                return;
            }

            var jsonParam = {
                    upPledgeBizId: $("#dialogPledgeBizRegiUpPledgeBizId", dialogObj).val(),
                    pledgeBizFg: $("#dialogPledgeBizRegiPledgeBizFg", dialogObj).val(),
                    pledgeBizNm: $("#dialogPledgeBizRegiPledgeBizNm", dialogObj).val(),
                    pledgeBizLevel: $("#dialogPledgeBizRegiPledgeBizLevel", dialogObj).val(),
                    pledgeInfoId: $("#dialogPledgeBizRegiPledgeInfoId", dialogObj).val()
                };
           
            var data = $.csAjaxCall({
                url : "/dialog/ajaxDialogPledgeBizRegiInsertPledgeBiz.do",
                data : jsonParam
            });
            
            if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
                $.csAlert({
                    msg : data.bcjisMessage,
                    callBack : function() {
                        dialogPledgeBizRegiClose();
                    }
                });
                
                return;
            }
            
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogPledgeBizRegiDoSaveCallBack(data);
                    dialogPledgeBizRegiClose();
                }
            });
        };
        
    });
</script>
<div id="dialogPledgeBizRegiDiv" class="dialog" style="display: none;">
<input type="hidden" id="dialogPledgeBizRegiCallBackFunction"/>
<input type="hidden" id="dialogPledgeBizRegiPledgeInfoId"/>
<input type="hidden" id="dialogPledgeBizRegiUpPledgeBizId"/>
<input type="hidden" id="dialogPledgeBizRegiPledgeBizLevel"/>
  <div class="viewDiv" style="width: 470px;">
    <table>
      <colgroup>
        <col width="120px" />
        <col width="280px" />
      </colgroup>
      <tbody>
        <tr>
          <th>상위공약사업</th>
          <td>
            <input type="text" id="dialogPledgeBizRegiPledgeInfo" class="readonly" style="width:100%;" readonly/>
          </td>
        </tr>
        <tr>
          <th>공약사업 분류</th>
          <td>
            <input type="text" id="dialogPledgeBizRegiPledgeBizFg" style="width: 100%;" maxlength="50"/>
          </td>
        </tr>
        <tr>
          <th>공약사업 명</th>
          <td>
            <input type="text" id="dialogPledgeBizRegiPledgeBizNm" style="width: 100%; ime-mode: active;" maxlength="100"/>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>