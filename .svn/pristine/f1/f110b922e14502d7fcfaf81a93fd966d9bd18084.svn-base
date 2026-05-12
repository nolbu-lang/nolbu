<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogPledgeSortDiv");
    
    var dialogPledgeSortColNames = ['분류', '명', 'pledgeBizId', 'pledgeBizSeq'];
    var dialogPledgeSortColModel = [
                                    {name : 'pledgeBizFg', index : 'pledgeBizFg', width : 50, sortable : false, fixed : true, align : 'left' },
                                    {name : 'pledgeBizNm', index : 'pledgeBizNm', width : 300, sortable : false, fixed : true, align : 'left' },
                                    {name : 'pledgeBizId', index : 'pledgeBizId', width : 0, sortable : false, hidden : true, key: true},
                                    {name : 'pledgeBizSeq', index : 'pledgeBizSeq', width : 0, sortable : false, hidden : true}

    ];
    
    var dialogPledgeSortGridParam = {
            id : "DIALOG_PLEDGE_SORT",
            colNames : dialogPledgeSortColNames,
            colModel : dialogPledgeSortColModel,
            rowNum : 1000,
            width : 520,
            height : 250,
            defaultRows: 0
    };
    
    var dialogPledgeSortGrid = $.csGrid(dialogPledgeSortGridParam);
    
    var dodialogPledgeSortSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        dialogPledgeSortGrid.addCsJsonData(data);
    };
    
    var dodialogPledgeSortSearch = function() {
        $.csAjaxCall({
            url : "/dialog/ajaxDialogPledgeSortPledgeBizList.do",
            data : {
                    upPledgeBizId: $("#dialogPledgeSortUpPledgeBizId", dialogObj).val()
                   },
            async : true,
            callBack : dodialogPledgeSortSearchCallBack
        });
    };
    
    var dialogPledgeSortResort = function(rowId, sortFg){
        
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "변경할 항목을 선택하여 주십시오."
            });
            
            return;
        }
        
        var rowList = dialogPledgeSortGrid.getRowData();

        var targetId = "";
        for(var i = 0; i < rowList.length; i++) {
            if(rowId === rowList[i].pledgeBizId){
                if(sortFg === "up"){
                    if(i > 0){
                        targetId = rowList[i-1].pledgeBizId;
                    }
                }else if(sortFg === "down"){
                    targetId = rowList[i+1].pledgeBizId;
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
        
        var srcData = dialogPledgeSortGrid.getRowData(rowId);
        var targetData = dialogPledgeSortGrid.getRowData(targetId);
        var tempPledgeBizSeq = targetData.pledgeBizSeq;
        
        dialogPledgeSortGrid.jqGrid('setCell', targetId, 'pledgeBizSeq', srcData.pledgeBizSeq);
        srcData.pledgeBizSeq = tempPledgeBizSeq;
        dialogPledgeSortGrid.delRowData(rowId);
        if(sortFg === "up"){
            dialogPledgeSortGrid.addRowData(rowId, srcData, "before", targetId);
        }else if(sortFg === "down"){
            dialogPledgeSortGrid.addRowData(rowId, srcData, "after", targetId);
        }
        
        dialogPledgeSortGrid.jqGrid('setSelection',rowId);
    };
    
    $("#dialogPledgeSortUpBtn").click(function() {
        var selectedRowId = dialogPledgeSortGrid.jqGrid ('getGridParam', 'selrow');        
        dialogPledgeSortResort(selectedRowId, "up");
    });
    
    $("#dialogPledgeSortDownBtn").click(function() {
        var selectedRowId = dialogPledgeSortGrid.jqGrid ('getGridParam', 'selrow');        
        dialogPledgeSortResort(selectedRowId, "down");
    });
    
    var dialogPledgeSortDoSaveCallBack = function(param){
        var dialogPledgeSortCallBackFunction = $("#dialogPledgeSortCallBackFunction", dialogObj).val();
        if(isEmpty(dialogPledgeSortCallBackFunction) == false){
            eval(dialogPledgeSortCallBackFunction+ '('+ jsonToString(param) + ')');
        }
    };
    
    var dialogPledgeSortDoSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var rowList = dialogPledgeSortGrid.getRowData();

        var saveDatas = [];
        var saveData = {};
        for(var i = 0; i < rowList.length; i++){
        
            if(isEmpty(rowList[i].pledgeBizId) == false){
                saveData = {};
                saveData["pledgeBizId"] = rowList[i].pledgeBizId;
                saveData["pledgeBizSeq"] = rowList[i].pledgeBizSeq;
                
                saveDatas.push(saveData);
            }
        }
        
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogPledgeSortSavePledgeBizSorts.do",
            data : {saveDatas: saveDatas}
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogPledgeSortClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                dialogPledgeSortDoSaveCallBack(data);

                dialogPledgeSortClose();
            }
        });
    };
    
    var dialogPledgeSortSaveBtnClick = function(){
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : dialogPledgeSortDoSave
        });
    };
    
    var dialogPledgeSortClose = function(){

        $("#dialogPledgeSortCallBackFunction", dialogObj).val("");
        $("#dialogPledgeSortUpPledgeBizId",dialogObj).val("");
        
        $("#dialogPledgeSortDiv").dialog("close");
    };
    
    $("#dialogPledgeSortDiv").dialog({
        title: "정렬순서변경",
        autoOpen: false,
        width: 550,
        height: 430,
        modal: true,
        resizable: false,
        open: function(event, ui){
            dodialogPledgeSortSearch();
        },
        buttons : {
            "저장" : function() {
                dialogPledgeSortSaveBtnClick();
            },
            "닫기" : function() {
                dialogPledgeSortClose();
            }
        }
    });
    
});
</script>
<div id="dialogPledgeSortDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogPledgeSortCallBackFunction"/>
  <input type="hidden" id="dialogPledgeSortUpPledgeBizId"/>
  <div class="btn">
    <div class="btnR">
      <a id="dialogPledgeSortUpBtn" class="btnClass" href="#" enabledYn="Y">위</a>
      <a id="dialogPledgeSortDownBtn" class="btnClass" href="#" enabledYn="Y">아래</a>
    </div>
  </div>
  <div id="DIALOG_PLEDGE_SORT_DIV" class="csGrid">
    <table id="DIALOG_PLEDGE_SORT_GRD" ></table>
  </div>
</div>