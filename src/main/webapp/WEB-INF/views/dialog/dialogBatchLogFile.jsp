<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogBatchLogFileDiv");
    
    var fileNmFormatter = function(cellValue, options, rowObject){
        
    	//var nm = encodeURIComponent(rowObject.logFile);
    	var nm = rowObject.logFile;
        return rVal = '<a href="javascript:batchLogFileDownload(\''+nm+'\');">' + nm + '</a>';
    };
    
    var dialogBatchLogFile = ['번호', '파일', 'logFile', 'logFilePath'];
    var dialogBatchLogFileColModel = [
                                     {name : 'rowNum', index : 'rowNum', width : 50, sortable : false, fixed : true, align : 'center'},
                                     {name : 'fileSn1', index : 'fileSn1', width : 300, sortable : false, fixed : true, align : 'center',
                                         formatter:fileNmFormatter
                                     },
                                     {name : 'logFile', index : 'logFile', width : 0, sortable : false, fixed : true, hidden : true },
                                     {name : 'logFilePath', index : 'logFilePath', width : 0, sortable : false, fixed : true, hidden : true }
    ];
    
    var dialogBatchLogFileGridParam = {
            id : "DIALOG_BATCH_LOG_FILE",
            colNames : dialogBatchLogFile,
            colModel : dialogBatchLogFileColModel,
            rowNum : 1000,
            autowidth:true,
            height: "auto"
        };
    
    var dialogBatchLogFileGrid = $.csGrid(dialogBatchLogFileGridParam);
    
    doDialogBatchLogFilePageSearch = function(page) {
        doDialogBatchLogFileSearch({
        });
    };
    
    var dialogBatchLogFileDefaultSearchParam = {
    };
    
    var getDialogBatchLogFileSearchParam = function(params){
        var searchParam = {
            };

            $.extend(dialogBatchLogFileDefaultSearchParam, searchParam);
            $.extend(dialogBatchLogFileDefaultSearchParam, params);

            return dialogBatchLogFileDefaultSearchParam;
    };
    
    var doDialogBatchLogFileSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        console.log(data);
        dialogBatchLogFileGrid.addCsJsonData(data);
    };
    
    var doDialogBatchLogFileSearch = function(params) {
        $.csAjaxCall({
            url : "/manage/ajaxManageBatchLogFileList.do",
            data : getDialogBatchLogFileSearchParam(params),
            async : true,
            callBack : doDialogBatchLogFileSearchCallBack
        });
    };
    
    var dialogBatchLogFileClose = function(){
        $("#dialogBatchLogFileCallBackFunction", dialogObj).val("");
        
        $("#dialogBatchLogFileDiv").dialog('close');
    };
    
    $("#dialogBatchLogFileSearchBtn", dialogObj).click(function() {
        doDialogBatchLogFileSearch({
        });
    });
    
    $("#dialogBatchLogFileCondDeptNm", dialogObj).keypress(function(event){
        if(event.which == 13){
            doDialogBatchLogFileSearch({
            });
        }
    });
    
    $("#dialogBatchLogFileDiv").dialog({
        title: "배치로그파일",
        autoOpen: false,
        width: 448,
        height: 510,
        modal: true,
        resizable: false,
        open: function(event, ui){
            doDialogBatchLogFileSearch({
            });
        },
        buttons : {
            "닫기" : function() {
                dialogBatchLogFileClose();
            }
        }
    });
    
    batchLogFileDownload = function(fileName) {
        //var url = ctx + "/manage/fileDown.do";
        var url = ctx + "/comm/batchFileLogDown.do";
        var formId = 'bcjisBatchLogFileDownForm';

        if ($("#" + formId) != null && $("#" + formId).attr("id") != null) {
            $("#" + formId).remove();
        }

        var form = $('<form action="' + url + '" method="post" name="' + formId + '" id="' + formId + '"></form>');
        $(form).appendTo('body');

        //console.log(filePath);
        //$('<input type="hidden" name="filePath" value="'+ encodeURIComponent(filePath) + '" />').appendTo(form);
        $('<input type="hidden" name="fileName" value="'+ encodeURIComponent(fileName) + '" />').appendTo(form);

        form.submit();
    };
   
});
</script>
<div id="dialogBatchLogFileDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogBatchLogFileCallBackFunction"/>
  <div class="btn">
    <div class="btnR">
      <a id="dialogBatchLogFileSearchBtn" href="#"><img src="<c:url value='/images/btn/btn_inquiry.gif'/>" alt="조회"/></a>
    </div>
  </div>
  <div id="DIALOG_BATCH_LOG_FILE_DIV" class="csGrid">
    <table id="DIALOG_BATCH_LOG_FILE_GRD" ></table>
  </div>
</div>