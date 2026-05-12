<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
var _dialogReport0C0DetlListSeq = 0;
$(document).ready(function (){
    var dialogObj = $("#dialogReport0C0DetlDiv");
    
    dialogReport0C0DetlAddList = function(detlData) {
        var detlDataTemp = {};
        if(isEmpty(detlData) == true || isEmpty(detlData.sn) == true){
            _dialogReport0C0DetlListSeq++;
            detlDataTemp = {
                    reportCd : "",
                    reportDetlCd : "",
                    fisYear : "",
                    bgtDgr : "",
                    teBgtCompoId : "",
                    sn : "",
                    teBgtCompoSeq : "",
                    fgTitle : "",
                    preCont : "",
                    demandCont : "",
                    examCont : "",
                    remark : ""
                    
            };
        }else{
            detlDataTemp = detlData;
            _dialogReport0C0DetlListSeq = detlData.sn;
        }
        
        var sn = _dialogReport0C0DetlListSeq;
        
        var tHtml = '<table id="dialogReport0C0DetlList_'+sn+'" width="100%" border="0" cellspacing="0" cellpadding="0" style="margin-top: 1px">'
                  + '  <colgroup>'
                  + '    <col width="170"/>'
                  + '    <col width="120"/>'
                  + '    <col width="120"/>'
                  + '    <col width="120"/>'
                  + '    <col width="220"/>'
                  + '    <col width="60"/>'
                  + '  </colgroup>'
                  + '  <tbody>'
                  + '    <tr>'
                  + '      <td style="vertical-align: top; text-align:center;padding: 2px 5px 2px 5px;">'
                  + '        <input type="hidden" id="dialogReport0C0DetlSn_'+sn+'" value="'+sn+'"/>'
                  + '        <textarea name="textarea" id="dialogReport0C0DetlFgTitle_'+sn+'" style="width:100%; ime-mode:active; height: 130px;">'+detlDataTemp.fgTitle+'</textarea>'
                  + '      </td>'
                  + '      <td style="vertical-align: top; text-align:center;padding: 2px 5px 2px 5px;">'
                  + '        <textarea name="textarea" id="dialogReport0C0DetlPreCont_'+sn+'" style="width:100%; ime-mode:active; height: 130px;">'+detlDataTemp.preCont+'</textarea>'
                  + '      </td>'
                  + '      <td style="vertical-align: top; text-align:center;padding: 2px 5px 2px 5px;">'
                  + '        <textarea name="textarea" id="dialogReport0C0DetlDemandCont_'+sn+'" style="width:100%; ime-mode:active; height: 130px;">'+detlDataTemp.demandCont+'</textarea>'
                  + '      </td>'
                  + '      <td style="vertical-align: top; text-align:center;padding: 2px 5px 2px 5px;">'
                  + '        <textarea name="textarea" id="dialogReport0C0DetlExamCont_'+sn+'" style="width:100%; ime-mode:active; height: 130px;">'+detlDataTemp.examCont+'</textarea>'
                  + '      </td>'
                  + '      <td style="vertical-align: top; text-align:center;padding: 2px 5px 2px 5px;">'
                  + '        <textarea name="textarea" id="dialogReport0C0DetlRemark_'+sn+'" style="width:100%; ime-mode:active; height: 130px;">'+detlDataTemp.remark+'</textarea>'
                  + '      </td>'
                  + '      <td style="vertical-align: center; text-align:center;padding: 2px 5px 2px 5px;"><a id="dialogReport0C0DetlDelList_'+sn+'" href="javascript:dialogReport0C0DetlDelList(\''+sn+'\');" ><img src="'+ctx+'/images/icon/icn_delete.gif" alt="삭제" style="vertical-align:middle;"/></a></td>'
                  + '    </tr>'
                  + '  </tbody>'
                  + '</table>';
        $("#dialogReport0C0DetlList", dialogObj).append(tHtml);
        $("#dialogReport0C0DetlFgTitle_"+sn, dialogObj).maxlength({max: 500, showFeedback: false});
        $("#dialogReport0C0DetlPreCont_"+sn, dialogObj).maxlength({max: 500, showFeedback: false});
        $("#dialogReport0C0DetlDemandCont_"+sn, dialogObj).maxlength({max: 500, showFeedback: false});
        $("#dialogReport0C0DetlExamCont_"+sn, dialogObj).maxlength({max: 500, showFeedback: false});
        $("#dialogReport0C0DetlRemark_"+sn, dialogObj).maxlength({max: 500, showFeedback: false});
    };
    
    $("#dialogReport0C0DetlAddListIcn").click(function(){
        dialogReport0C0DetlAddList();
    });

    dialogReport0C0DetlDelList = function(sn){
        $("#dialogReport0C0DetlList_"+sn, dialogObj).remove();
    };
    
    var getSaveData = function(){
        var detls = new Array();
        var sn = "";
        var detl = {};
        $("input[id^='dialogReport0C0DetlSn_']", dialogObj).each(function(index){
            sn = $(this).val();
            detl = {};
            detl["fgTitle"] = $("#dialogReport0C0DetlFgTitle_"+sn, dialogObj).val();
            detl["preCont"] = $("#dialogReport0C0DetlPreCont_"+sn, dialogObj).val();
            detl["demandCont"] = $("#dialogReport0C0DetlDemandCont_"+sn, dialogObj).val();
            detl["examCont"] = $("#dialogReport0C0DetlExamCont_"+sn, dialogObj).val();
            detl["remark"] = $("#dialogReport0C0DetlRemark_"+sn, dialogObj).val();
            
            detls.push(detl);
        });
        
        return detls;
        
    };
    
    
    var doDialogReport0C0DetlSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        var detls = data.dataList;
        var detl = {};
        for(var i = 0; i < detls.length; i++){
            detl = detls[i];
            
            if(isEmpty(detl) == true){
                continue;
            }
            
            dialogReport0C0DetlAddList(detl);
        }
    };
    
    var doDialogReport0C0DetlSearch = function() {
        $("#dialogReport0C0DetlList", dialogObj).html("");
        
        $.csAjaxCall({
            url : "/dialog/ajaxDialogReport0C0DetlSelectReport0C0D.do",
            data : {
                    reportCd: $("#dialogReport0C0DetlReportCd", dialogObj).val(),
                    reportDetlCd: $("#dialogReport0C0DetlReportDetlCd", dialogObj).val(),
                    fisYear: $("#dialogReport0C0DetlFisYear", dialogObj).val(),
                    bgtDgr: $("#dialogReport0C0DetlBgtDgr", dialogObj).val(),
                    teBgtCompoId: $("#dialogReport0C0DetlTeBgtCompoId", dialogObj).val()
                   },
            async : true,
            callBack : doDialogReport0C0DetlSearchCallBack
        });
    };
    
    var dialogReport0C0DetlDoSaveCallBack = function(param){
        var dialogReport0C0DetlCallBackFunction = $("#dialogReport0C0DetlCallBackFunction", dialogObj).val();

        if(isEmpty(param) == true){
            return;
        }
        
        param.dgrcompo["dgrcompoId"] = $("#dialogReport0C0DetlFisYear", dialogObj).val() + "_"
                                     + $("#dialogReport0C0DetlBgtDgr", dialogObj).val() + "_"
                                     + $("#dialogReport0C0DetlTeBgtCompoId", dialogObj).val();
        
        if(isEmpty(dialogReport0C0DetlCallBackFunction) == false){
            eval(dialogReport0C0DetlCallBackFunction + '('+ jsonToString(param.dgrcompo) + ')');
        }
    };
    
    var dialogReport0C0DetlDoSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var report0C0Ds = getSaveData();
        
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogReport0C0DetlSaveReport0C0D.do",
            data : {
                reportCd : $("#dialogReport0C0DetlReportCd", dialogObj).val(),
                reportDetlCd : $("#dialogReport0C0DetlReportDetlCd", dialogObj).val(),
                fisYear : $("#dialogReport0C0DetlFisYear", dialogObj).val(),
                bgtDgr : $("#dialogReport0C0DetlBgtDgr", dialogObj).val(),
                teBgtCompoId : $("#dialogReport0C0DetlTeBgtCompoId", dialogObj).val(),
                teBgtCompoSeq : $("#dialogReport0C0DetlTeBgtCompoSeq", dialogObj).val(),
                report0C0Ds : report0C0Ds
            }
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogReport0C0DetlClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : "저장되었습니다.",
            callBack : function() {
                dialogReport0C0DetlDoSaveCallBack(data);
                
                dialogReport0C0DetlClose();
            }
        });
    };
    
    var dialogReport0C0DetlSaveBtnClick = function(){
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : dialogReport0C0DetlDoSave
        });
    };
    
    var dialogReport0C0DetlClose = function(){
        $("#dialogReport0C0DetlCallBackFunction", dialogObj).val("");
        $("#dialogReport0C0DetlReportCd", dialogObj).val("");
        $("#dialogReport0C0DetlReportDetlCd", dialogObj).val("");
        $("#dialogReport0C0DetlFisYear", dialogObj).val("");
        $("#dialogReport0C0DetlBgtDgr", dialogObj).val("");
        $("#dialogReport0C0DetlTeBgtCompoId", dialogObj).val("");
        $("#dialogReport0C0DetlTeBgtCompoSeq", dialogObj).val("");
        
        $("#dialogReport0C0DetlList", dialogObj).html("");
        
        $("#dialogReport0C0DetlDiv").dialog("close");
    };
    
    $("#dialogReport0C0DetlDiv").dialog({
        title: "1장보고서상세",
        autoOpen: false,
        width: 800,
        height: 450,
        modal: true,
        resizable: true,
        open: function(event, ui){
            doDialogReport0C0DetlSearch();
        },
        buttons : {
            "저장" : function() {
                dialogReport0C0DetlSaveBtnClick();
            },
            "닫기" : function() {
                dialogReport0C0DetlClose();
            }
        }
    });
});
</script>
<div id="dialogReport0C0DetlDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogReport0C0DetlCallBackFunction"/>
  <input type="hidden" id="dialogReport0C0DetlReportCd"/>
  <input type="hidden" id="dialogReport0C0DetlReportDetlCd"/>
  <input type="hidden" id="dialogReport0C0DetlFisYear"/>
  <input type="hidden" id="dialogReport0C0DetlBgtDgr"/>
  <input type="hidden" id="dialogReport0C0DetlTeBgtCompoId"/>
  <input type="hidden" id="dialogReport0C0DetlTeBgtCompoSeq"/>
  <div class="viewDiv" style="width:755px;">
    <table width="100%" border="0" cellspacing="0" cellpadding="0" style="margin-top: 1px">
      <colgroup>
        <col width="170"/>
        <col width="120"/>
        <col width="120"/>
        <col width="120"/>
        <col width="220"/>
        <col width="60"/>
      </colgroup>
      <tbody>
        <tr>
          <th valign="middle" style="height:28px; text-align: center;" >구분</th>
          <th valign="middle" style="height:28px; text-align: center;" >기정</th>
          <th valign="middle" style="height:28px; text-align: center;" >요구</th>
          <th valign="middle" style="height:28px; text-align: center;" >심사안</th>
          <th valign="middle" style="height:28px; text-align: center;" >비고</th>
          <th valign="middle" style="height:28px; text-align: center;" ><a id="dialogReport0C0DetlAddListIcn" style="cursor:pointer;"><img src="${pageContext.request.contextPath}/images/btn/btn_add.png" alt="추가" style="vertical-align:middle;"/></a></th>
        </tr>
      </tbody>
    </table>
    <div id="dialogReport0C0DetlList">
    </div>
  </div>
</div>