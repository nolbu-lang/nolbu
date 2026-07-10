<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script>
// jQuery import 바로아래에 넣어 주면 됩니다.
// Cannot read property 'msie' of undefined 에러 나올때
jQuery.browser = {};
(function () {
    jQuery.browser.msie = false;
    jQuery.browser.version = 0;
    if (navigator.userAgent.match(/MSIE ([0-9]+)\./)) {
        jQuery.browser.msie = true;
        jQuery.browser.version = RegExp.$1;
    }
})();
</script>

<script type="text/javaScript" language="javascript" defer="defer">
    $(document).ready(function() {
        var dialogObj = $("#dialogPledgeExcelUploadDiv");
        
        dialogExcelUploadRegiValidataion = function(){
            
        	var fileCnt = $('#excelFile')[0].files.length;
        	
        	if(fileCnt == 0){
        		alert('첨부파일을 등록하여 주세요.');
        		return false;
        	}
            
            
            return true;
        };
        
        var dialogExcelUploadRegiClose = function() {
            /* $("#dialogPledgeExcelUploadCallBackFunction", dialogObj).val("");
            $("#dialogExcelUploadRegiExcelUploadId", dialogObj).val("");
            $("#dialogExcelUploadRegiExcelUploadNm", dialogObj).val("");
            $("#dialogExcelUploadRegiPledgeBeginYmd", dialogObj).val("");
            $("#dialogExcelUploadRegiPledgeEndYmd", dialogObj).val(""); */
            if ($.browser.msie) { // ie 일때 input[type=file] init. 
            	$("#excelFile").replaceWith( $("#excelFile").clone(true) ); 
            } else { // other browser 일때 input[type=file] init. 
            	$("#excelFile").val(""); 
            }

            $("#dialogPledgeExcelUploadDiv").dialog('close');
        };
        
        $("#dialogPledgeExcelUploadDiv").dialog({
            title : "공약정보엑셀업로드",
            autoOpen : false,
            width : 400,
            height : 200,
            modal : true,
            resizable : true,
            buttons : {
                "등록" : function() {
                    dialogExcelUploadRegiSaveBtnClick();
                },
               
                "닫기" : function() {
                    dialogExcelUploadRegiClose();
                }
            }
        });
        
        
        var dialogExcelUploadRegiSaveBtnClick = function(){
       
            if(dialogExcelUploadRegiValidataion() == false){
                return;
            }
            
            $.csConfirm({
                msg : "등록하시겠습니까?",
                callBack : dialogExcelUploadRegiDoSave
            });
        };
        
        var dialogExcelUploadRegiDoSaveCallBack = function(param){
            var dialogPledgeExcelUploadCallBackFunction = $("#dialogPledgeExcelUploadCallBackFunction", dialogObj).val();
            
            if(isEmpty(dialogPledgeExcelUploadCallBackFunction) == false){
                eval(dialogPledgeExcelUploadCallBackFunction + '()');
            }
        };
        
        dialogExcelUploadRegiDoSave = function(params){
            
            if(params.confirmData != "Y"){
                return;
            }
           
            var form = new FormData();
            form.append("file", $("#excelFile")[0].files[0]);
            
            jQuery.ajax({
	                url : "/dialog/ajaxDialogPledgeExcelUpload.do"
	              , type : "POST"
	              , processData : false
	              , contentType : false
	              , data : form
	              , success:function(response) {
	                  var json = JSON.parse(response);
	                  //console.log(response);
	                  var message = json.bcjisMessage;
	                  $.csAlert({
	                      msg : message,
	                      callBack : function() {
	                    	  dialogExcelUploadRegiDoSaveCallBack(response);
	                          dialogExcelUploadRegiClose();
	                      }
	                  });
	              }
	              ,error: function (jqXHR) 
	              { 
	                  alert(jqXHR.responseText); 
	              }
          	});
        };
        
    });
</script>
<div id="dialogPledgeExcelUploadDiv" class="dialog" style="display: none;">
<input type="hidden" id="dialogPledgeExcelUploadCallBackFunction"/>
  <div class="viewDiv" style="width: 370px;">
    <table>
      <colgroup>
        <col width="120px" />
        <col width="280px" />
      </colgroup>
      <tbody>
        <tr>
          <th>엑셀 업로드</th>
          <td>
            <input id="excelFile" type="file" name="excelFile" />
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>