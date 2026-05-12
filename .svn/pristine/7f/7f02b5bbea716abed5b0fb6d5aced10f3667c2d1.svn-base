<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogDgroffice020SortDiv");
    
    var dialogDgroffice020SortColNames = ['실국명', 'fisYear', 'bgtDgr', 'officeCd', 'officeRank020'];
    var dialogDgroffice020SortColModel = [
                                    {name : 'officeNm', index : 'officeNm', width : 250, sortable : false, fixed : true, align : 'left' },
                                    {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                                    {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                                    {name : 'officeCd', index : 'officeCd', width : 0, key: true, sortable : false, hidden : true},
                                    {name : 'officeRank020', index : 'officeRank020', width : 0, sortable : false, hidden : true}

    ];
    
    var dialogDgroffice020SortGridParam = {
            id : "DIALOG_DGROFFICE020_SORT",
            colNames : dialogDgroffice020SortColNames,
            colModel : dialogDgroffice020SortColModel,
            rowNum : 1000,
            width : 290,
            height : 250,
            defaultRows: 0
    };
    
    var dialogDgroffice020SortGrid = $.csGrid(dialogDgroffice020SortGridParam);
    
    var doDialogDgroffice020SortSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        dialogDgroffice020SortGrid.addCsJsonData(data);
    };
    
    var doDialogDgroffice020SortSearch = function() {
        $.csAjaxCall({
            url : "/dialog/ajaxDgroffice020SortDgroffice020List.do",
            data : {
                    fisYear: $("#dialogDgroffice020SortFisYear", dialogObj).val(),
                    bgtDgr: $("#dialogDgroffice020SortBgtDgr", dialogObj).val()
                   },
            async : true,
            callBack : doDialogDgroffice020SortSearchCallBack
        });
    };
    
    var dialogDgroffice020SortResort = function(rowId, sortFg){
        
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "변경할 항목을 선택하여 주십시오."
            });
            
            return;
        }
        
        var rowList = dialogDgroffice020SortGrid.getRowData();

        var targetId = "";
        for(var i = 0; i < rowList.length; i++) {
            if(rowId === rowList[i].officeCd){
                if(sortFg === "up"){
                    if(i > 0){
                        targetId = rowList[i-1].officeCd;
                    }
                }else if(sortFg === "down"){
                    targetId = rowList[i+1].officeCd;
                }
                
                break;
            }
        }
        
        if(isEmpty(targetId) == true && sortFg === "up"){
            $.csAlert({
                msg : "최상위입니다."
            });
            
            return;
        }
        
        if(isEmpty(targetId) == true && sortFg === "down"){
            $.csAlert({
                msg : "최하위입니다."
            });
            
            return;
        }
        
        var srcData = dialogDgroffice020SortGrid.getRowData(rowId);
        var targetData = dialogDgroffice020SortGrid.getRowData(targetId);
        var tempOfficeRank020 = targetData.officeRank020;
        
        dialogDgroffice020SortGrid.jqGrid('setCell', targetId, 'officeRank020', srcData.officeRank020);
        srcData.officeRank020 = tempOfficeRank020;
        dialogDgroffice020SortGrid.delRowData(rowId);
        if(sortFg === "up"){
            dialogDgroffice020SortGrid.addRowData(rowId, srcData, "before", targetId);
        }else if(sortFg === "down"){
            dialogDgroffice020SortGrid.addRowData(rowId, srcData, "after", targetId);
        }
        
        dialogDgroffice020SortGrid.jqGrid('setSelection',rowId);
    };
    
    $("#dialogDgroffice020SortUpBtn").click(function() {
        var selectedRowId = dialogDgroffice020SortGrid.jqGrid ('getGridParam', 'selrow');        
        dialogDgroffice020SortResort(selectedRowId, "up");
    });
    
    $("#dialogDgroffice020SortDownBtn").click(function() {
        var selectedRowId = dialogDgroffice020SortGrid.jqGrid ('getGridParam', 'selrow');        
        dialogDgroffice020SortResort(selectedRowId, "down");
    });
    
    var dialogDgroffice020SortDoSaveCallBack = function(param){
        var dialogDgroffice020SortCallBackFunction = $("#dialogDgroffice020SortCallBackFunction", dialogObj).val();
        if(isEmpty(dialogDgroffice020SortCallBackFunction) == false){
            eval(dialogDgroffice020SortCallBackFunction+'()');
        }
    };
    
    var dialogDgroffice020SortDoSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var rowList = dialogDgroffice020SortGrid.getRowData();

        var saveDatas = [];
        var saveData = {};
        for(var i = 0; i < rowList.length; i++){
        
            if(isEmpty(rowList[i].officeCd) == false){
                saveData = {};
                saveData["fisYear"] = rowList[i].fisYear;
                saveData["bgtDgr"] = rowList[i].bgtDgr;
                saveData["officeCd"] = rowList[i].officeCd;
                saveData["officeRank020"] = rowList[i].officeRank020;
                
                saveDatas.push(saveData);
            }
        }
        
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogDgroffice020SaveOfficeRank020s.do",
            data : {saveDatas: saveDatas}
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogDgroffice020SortClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                dialogDgroffice020SortDoSaveCallBack(data);

                dialogDgroffice020SortClose();
            }
        });
    };
    
    var dialogDgroffice020SortSaveBtnClick = function(){
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : dialogDgroffice020SortDoSave
        });
    };
    
    var dialogDgroffice020SortClose = function(){

        $("#dialogDgroffice020SortCallBackFunction", dialogObj).val("");
        $("#dialogDgroffice020SortFisYear", dialogObj).val("");
        $("#dialogDgroffice020SortBgtDgr", dialogObj).val("");
        
        $("#dialogDgroffice020SortDiv").dialog("close");
    };
    
    $("#dialogDgroffice020SortDiv").dialog({
        title: "정렬순서변경",
        autoOpen: false,
        width: 310,
        height: 430,
        modal: true,
        resizable: false,
        open: function(event, ui){
            doDialogDgroffice020SortSearch();
        },
        buttons : {
            "저장" : function() {
                dialogDgroffice020SortSaveBtnClick();
            },
            "닫기" : function() {
                dialogDgroffice020SortClose();
            }
        }
    });
    
});
</script>
<div id="dialogDgroffice020SortDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgroffice020SortCallBackFunction"/>
  <input type="hidden" id="dialogDgroffice020SortFisYear"/>
  <input type="hidden" id="dialogDgroffice020SortBgtDgr"/>
  <div class="btn">
    <div class="btnR">
      <a id="dialogDgroffice020SortUpBtn" class="btnClass" href="#" enabledYn="Y">위</a>
      <a id="dialogDgroffice020SortDownBtn" class="btnClass" href="#" enabledYn="Y">아래</a>
    </div>
  </div>
  <div id="DIALOG_DGROFFICE020_SORT_DIV" class="csGrid">
    <table id="DIALOG_DGROFFICE020_SORT_GRD" ></table>
  </div>
</div>