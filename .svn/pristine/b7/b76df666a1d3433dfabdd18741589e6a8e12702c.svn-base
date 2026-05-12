<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogDgrcompoTeMngRegiDiv");
    
    var dialogDgrcompoTeMngRegiDoSaveCallBack = function(param){
        var dialogDgrcompoTeMngRegiCallBackFunction = $("#dialogDgrcompoTeMngRegiCallBackFunction", dialogObj).val();
        
        if(isEmpty(dialogDgrcompoTeMngRegiCallBackFunction) == false){
            eval(dialogDgrcompoTeMngRegiCallBackFunction + '('+ jsonToString(param.dgrcompo) + ')');
        }
    };
    
    var dialogDgrcompoTeMngRegiDoSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var saveData = {
                fisYear: $("#dialogDgrcompoTeMngRegiFisYear", dialogObj).val(),
                bgtDgr: $("#dialogDgrcompoTeMngRegiBgtDgr", dialogObj).val(),
                deptCd: $("#dialogDgrcompoTeMngRegiDeptCd", dialogObj).val(),
                dbizCd: $("#dialogDgrcompoTeMngRegiDbizCd", dialogObj).val(),
                teMngMokCd: $("#dialogDgrcompoTeMngRegiTeMngMokCd", dialogObj).val(),
                teMngMokNm: $("#dialogDgrcompoTeMngRegiTeMngMokNm", dialogObj).val(),
                upTeBgtCompoId: $("#dialogDgrcompoTeMngRegiUpTeBgtCompoId", dialogObj).val(),
                upTeBgtCompoSeq: $("#dialogDgrcompoTeMngRegiUpTeBgtCompoSeq", dialogObj).val(),
                compoLevel : $("#dialogDgrcompoTeMngRegiCompoLevel", dialogObj).val(),
                demandCompGround: $("#dialogDgrcompoTeMngRegiCompGround", dialogObj).val(), 
                compGround: $("#dialogDgrcompoTeMngRegiCompGround", dialogObj).val()                
        };
        
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoTeMngRegiSaveDgrcompos.do",
            data : saveData
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogDgrcompoTeMngRegiClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                dialogDgrcompoTeMngRegiDoSaveCallBack(data);

                dialogDgrcompoTeMngRegiClose();
            }
        });
    };
    
    var dialogDgrcompoTeMngRegiSaveBtnClick = function(){
        
        if(isEmpty($("#dialogDgrcompoTeMngRegiTeMngMokCd", dialogObj).val()) == true){
            $.csAlert({
                msg : "통계목코드를 입력하여 주십시오.",
                callBack : function() {
                    $("#dialogDgrcompoTeMngRegiTeMngMokCd", dialogObj).focus();
                }
            });
            
            return false;
        }
        
        if($("#dialogDgrcompoTeMngRegiTeMngMokCd", dialogObj).val().length != 5){
            $.csAlert({
                msg : "통계목코드는 5자리 입니다.",
                callBack : function() {
                    $("#dialogDgrcompoTeMngRegiTeMngMokCd", dialogObj).focus();
                }
            });
            
            return false;
        }
        
        if(isEmpty($("#dialogDgrcompoTeMngRegiTeMngMokNm", dialogObj).val()) == true){
            $.csAlert({
                msg : "통계목명을 입력하여 주십시오.",
                callBack : function() {
                    $("#dialogDgrcompoTeMngRegiTeMngMokNm", dialogObj).focus();
                }
            });
            
            return false;
        }
        
        if(isEmpty($("#dialogDgrcompoTeMngRegiCompGround", dialogObj).val()) == true){
            $.csAlert({
                msg : "사업명을 입력하여 주십시오.",
                callBack : function() {
                    $("#dialogDgrcompoTeMngRegiCompGround", dialogObj).focus();
                }
            });
            
            return false;
        }
        
        $.csConfirm({
            msg : "등록하시겠습니까?",
            callBack : dialogDgrcompoTeMngRegiDoSave
        });
    };
    
    var dialogDgrcompoTeMngRegiClose = function(){
        $("#dialogDgrcompoTeMngRegiCallBackFunction", dialogObj).val("");
        $("#dialogDgrcompoTeMngRegiFisYear", dialogObj).val("");
        $("#dialogDgrcompoTeMngRegiBgtDgr", dialogObj).val("");
        $("#dialogDgrcompoTeMngRegiDeptCd", dialogObj).val("");
        $("#dialogDgrcompoTeMngRegiDbizCd", dialogObj).val("");
        $("#dialogDgrcompoTeMngRegiTeMngMokCd", dialogObj).val("");
        $("#dialogDgrcompoTeMngRegiUpTeBgtCompoId", dialogObj).val("");
        $("#dialogDgrcompoTeMngRegiUpTeBgtCompoSeq", dialogObj).val("");
        $("#dialogDgrcompoTeMngRegiCompoLevel", dialogObj).val("");
        $("#dialogDgrcompoTeMngRegiDgrcompoNm", dialogObj).val("");
        $("#dialogDgrcompoTeMngRegiCompGround", dialogObj).val("");
        
        $("#dialogDgrcompoTeMngRegiDiv").dialog("close");
    };
    
    $("#dialogDgrcompoTeMngRegiTeMngMokCd", dialogObj).autoNumeric({aPad: false, vMax:'99999', aSep:''});
    
    $("#dialogDgrcompoTeMngRegiDiv").dialog({
        title: "예산편성(통계목)등록",
        autoOpen: false,
        width: 780,
        height: 300,
        modal: true,
        resizable: true,
        open: function(event, ui){
            
        },
        buttons : {
            "등록" : function() {
                dialogDgrcompoTeMngRegiSaveBtnClick();
            },
            "닫기" : function() {
                dialogDgrcompoTeMngRegiClose();
            }
        }
    });
});
</script>
<div id="dialogDgrcompoTeMngRegiDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgrcompoTeMngRegiCallBackFunction"/>
  <input type="hidden" id="dialogDgrcompoTeMngRegiFisYear"/>
  <input type="hidden" id="dialogDgrcompoTeMngRegiBgtDgr"/>
  <input type="hidden" id="dialogDgrcompoTeMngRegiDeptCd"/>
  <input type="hidden" id="dialogDgrcompoTeMngRegiDbizCd"/>
  <input type="hidden" id="dialogDgrcompoTeMngRegiUpTeBgtCompoId"/>
  <input type="hidden" id="dialogDgrcompoTeMngRegiUpTeBgtCompoSeq"/>
  <input type="hidden" id="dialogDgrcompoTeMngRegiCompoLevel"/>
  <input type="hidden" id="dialogDgrcompoTeMngRegiAmtUnit" value="1"/>
  
  <div class="viewDiv" style="width:735px;">
    <table>
      <colgroup>
        <col width="75px"/>
        <col width="75px"/>
        <col width="75px"/>
        <col width="150px"/>
      </colgroup>
      <tbody>
        <tr>
          <th>상위 항목</th>
          <td colspan="3">
            <input type="text" id="dialogDgrcompoTeMngRegiDgrcompoNm" class="readonly" style="width:100%;" readonly/>
          </td>
        </tr>
        <tr>
          <th>통계목코드</th>
          <td>
            <input type="text" id="dialogDgrcompoTeMngRegiTeMngMokCd" style="width:50%;" maxlength="5" />
          </td>  
          <th>통계목명</th>
          <td>
            <input type="text" id="dialogDgrcompoTeMngRegiTeMngMokNm" style="width:100%;" />
          </td>
        </tr>
        <tr>
          <th colspan="1">사업명</th>
          <td colspan="3">
            <input type="text" id="dialogDgrcompoTeMngRegiCompGround" style="width:100%;"/>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  <br>
</div>