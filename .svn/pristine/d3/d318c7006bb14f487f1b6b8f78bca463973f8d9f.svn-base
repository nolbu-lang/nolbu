<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    
    var dialogObj = $("#dialogDgrcompoModifyReportDiv");
    var dialogDgrcompoModifyReportClose = function(){
    	
    	$('#dialogDgrcompoModifyReportCallBackFunction', dialogObj).val('');
    	$('#dialogDgrcompoModifyReportFisYear', dialogObj).val('');
    	$('#dialogDgrcompoModifyReportBgtDgr', dialogObj).val('');
    	$('#dialogDgrcompoModifyReportTeBgtCompoId', dialogObj).val('');
    	$('#dialogDgrcompoModifyReportReportCd', dialogObj).val('');
    	$('#dialogDgrcompoModifyReportReportDetlCd', dialogObj).val('');
    	$('#dialogDgrcompoModifyReportDemandCont', dialogObj).val('');
    	$('#dialogDgrcompoModifyReportReflectFg', dialogObj).val('');
    	$('#dialogDgrcompoModifyReportInvestPlan', dialogObj).val('');
    	$('#dialogDgrcompoModifyReportExamCont', dialogObj).val('');
    	$('#dialogDgrcompoModifyReportInvestPlan', dialogObj).hide();
        $("#dialogDgrcompoModifyReportDiv").dialog("close");
    };
    
    function openDialog(){
    	$('#dialogDgrcompoModifyReportInvestPlan', dialogObj).hide();
    	doSearch();
    }

    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        $('#dialogDgrcompoModifyReportDemandCont', dialogObj).val(data.reportData.demandCont);
        $('#dialogDgrcompoModifyReportReflectFg', dialogObj).val(data.reportData.reflectFg);
        $('#dialogDgrcompoModifyReportInvestPlan', dialogObj).val(data.reportData.investPlan);
        $('#dialogDgrcompoModifyReportExamCont', dialogObj).val(data.reportData.examCont);
        $('#dialogDgrcompoModifyReportReportCd', dialogObj).val(data.reportData.reportCd);
        $('#dialogDgrcompoModifyReportReportDetlCd', dialogObj).val(data.reportData.reportDetlCd);
        
        //경상, 투자심사만 노출
        if(data.reportData.reportCd == '010' || data.reportData.reportCd == '020'){
        	$('#dialogDgrcompoModifyReportInvestPlan', dialogObj).show();
        }else{
        	$('#dialogDgrcompoModifyReportInvestPlan', dialogObj).hide();
        }
        data = null;
    };
    
    var doSearch = function(){
        var fisYear = $("#dialogDgrcompoModifyReportFisYear", dialogObj).val();
        var bgtDgr = $("#dialogDgrcompoModifyReportBgtDgr", dialogObj).val();
        var teBgtCompoId = $("#dialogDgrcompoModifyReportTeBgtCompoId", dialogObj).val();

        $.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoModifyReportData.do",
            data: {fisYear : fisYear,
                   bgtDgr : bgtDgr,
                   teBgtCompoId : teBgtCompoId
            },
            async : true,
            callBack : doSearchCallBack
        });
    };
    
    var dialogDgrcompoModifyReportDoSave = function(){
    	var fisYear = $('#dialogDgrcompoModifyReportFisYear', dialogObj).val();
    	var bgtDgr = $('#dialogDgrcompoModifyReportBgtDgr', dialogObj).val();
    	var teBgtCompoId = $('#dialogDgrcompoModifyReportTeBgtCompoId', dialogObj).val();
    	var demandCont = $('#dialogDgrcompoModifyReportDemandCont', dialogObj).val();
    	var reflectFg = $('#dialogDgrcompoModifyReportReflectFg option:selected', dialogObj).val();
    	var investPlan = $('#dialogDgrcompoModifyReportInvestPlan', dialogObj).val();
    	var examCont = $('#dialogDgrcompoModifyReportExamCont', dialogObj).val();
    	var reportCd = $('#dialogDgrcompoModifyReportReportCd', dialogObj).val();
    	var reportDetlCd = $('#dialogDgrcompoModifyReportReportDetlCd', dialogObj).val();
    	
    	var param = {
    			fisYear: fisYear,
    			bgtDgr: bgtDgr,
    			teBgtCompoId: teBgtCompoId,
    			demandCont: demandCont,
    			reflectFg: reflectFg,
    			investPlan: investPlan,
    			examCont: examCont,
    			reportCd: reportCd,
    			reportDetlCd: reportDetlCd
    	}
    	
    	var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoModifyReportSaveData.do",
            data : param
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : "수정되었습니다.",
            callBack : function() {
            	dialogDgrcompoModifyReportClose();
            }
        });
    }
    
    var comboParam = [
                      {id : "reflectFg", codeId : "RP003"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);
    
    $("#dialogDgrcompoModifyReportReflectFg", dialogObj).csCreatCombo(comboData
    		, {id: 'reflectFg'
    		, groupId: 'RP003'
            , selectedValue: ''
            , comboType: 'S'
            , comboTypeValue: ''
              }
    );
    
    $("#dialogDgrcompoModifyReportDiv").dialog({
        title: "조서수정",
        autoOpen: false,
        width: 'auto',
        height: 'auto',
        modal: true,
        resizable: true,
        open: function(event, ui){
            openDialog();
        },
        buttons : {
            "저장" : function() {
            	dialogDgrcompoModifyReportDoSave();
            },
            "닫기" : function() {
                dialogDgrcompoModifyReportClose();
            }
        }
    });

    $('#dialogDgrcompoModifyReportDiv').on('dialogclose', function(event) {
    	dialogDgrcompoModifyReportClose();
    });
	
});




</script>
<div id="dialogDgrcompoModifyReportDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgrcompoModifyReportCallBackFunction"/>
  <input type="hidden" id="dialogDgrcompoModifyReportFisYear"/>
  <input type="hidden" id="dialogDgrcompoModifyReportBgtDgr"/>
  <input type="hidden" id="dialogDgrcompoModifyReportTeBgtCompoId"/>
  <br>
  
  <div id="modifyReportBody"  style="height:97%;border:0px;overflow:hidden;">
        <div id="modifyReportCenter" class="ui-layout-center" style="border:0px;overflow:auto;">
        	<div class="viewDiv" style="width:590px;">
			    <table>
			      <colgroup>
			        <col width="50%"/>
			        <col width="50%"/>
			      </colgroup>
			      <tbody>
			        <tr>
			          <th>조서내용</th>
			          <th>검토내용</th>
			        </tr>
			        <tr>
			        	<td>
			        		<input type="hidden" id="dialogDgrcompoModifyReportReportCd" value="" />
			        		<input type="hidden" id="dialogDgrcompoModifyReportReportDetlCd" value="" />
			        		<textarea id="dialogDgrcompoModifyReportDemandCont" style="width:270px;ime-mode:active;height:130px;"></textarea>
			        	</td>
			        	<td>
			        		<select id="dialogDgrcompoModifyReportReflectFg" title="반영구분" style="width:90px;">
			        		</select>
			        		<input id="dialogDgrcompoModifyReportInvestPlan" value="" maxlength="500" class="ui-state-enabled" style="width:170px;">
			        		<textarea id="dialogDgrcompoModifyReportExamCont" style="width:270px;ime-mode:active;height:130px;"></textarea>
			        	</td>
			        </tr>
			      </tbody>
			    </table>
			  </div>
        </div>
  </div>
</div>