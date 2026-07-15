<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogDgroffice010SortDiv");
    
    var dialogDgroffice010SortColNames = ['실국명', 'fisYear', 'bgtDgr', 'officeCd', 'officeRank010'];
    var dialogDgroffice010SortColModel = [
                                    {name : 'officeNm', index : 'officeNm', width : 250, sortable : false, fixed : true, align : 'left' },
                                    {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                                    {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                                    {name : 'officeCd', index : 'officeCd', width : 0, key: true, sortable : false, hidden : true},
                                    {name : 'officeRank010', index : 'officeRank010', width : 0, sortable : false, hidden : true}

    ];
    
    var dialogDgroffice010SortGridParam = {
            id : "DIALOG_DGROFFICE010_SORT",
            colNames : dialogDgroffice010SortColNames,
            colModel : dialogDgroffice010SortColModel,
            rowNum : 1000,
            width : 290,
            height : 250,
            defaultRows: 0
    };
    
    var dialogDgroffice010SortGrid = $.csGrid(dialogDgroffice010SortGridParam);
    
    var doDialogDgroffice010SortSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        dialogDgroffice010SortGrid.addCsJsonData(data);
    };
    
    var doDialogDgroffice010SortSearch = function() {
        $.csAjaxCall({
            url : "/dialog/ajaxDgroffice010SortDgroffice010List.do",
            data : {
                    fisYear: $("#dialogDgroffice010SortFisYear", dialogObj).val(),
                    bgtDgr: $("#dialogDgroffice010SortBgtDgr", dialogObj).val()
                   },
            async : true,
            callBack : doDialogDgroffice010SortSearchCallBack
        });
    };
    
    var dialogDgroffice010SortResort = function(rowId, sortFg){
        
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "변경할 항목을 선택하여 주십시오."
            });
            
            return;
        }
        
        var rowList = dialogDgroffice010SortGrid.getRowData();

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
        
        var srcData = dialogDgroffice010SortGrid.getRowData(rowId);
        var targetData = dialogDgroffice010SortGrid.getRowData(targetId);
        var tempOfficeRank010 = targetData.officeRank010;
        
        dialogDgroffice010SortGrid.jqGrid('setCell', targetId, 'officeRank010', srcData.officeRank010);
        srcData.officeRank010 = tempOfficeRank010;
        dialogDgroffice010SortGrid.delRowData(rowId);
        if(sortFg === "up"){
            dialogDgroffice010SortGrid.addRowData(rowId, srcData, "before", targetId);
        }else if(sortFg === "down"){
            dialogDgroffice010SortGrid.addRowData(rowId, srcData, "after", targetId);
        }
        
        dialogDgroffice010SortGrid.jqGrid('setSelection',rowId);
    };

    dialogDgroffice010SortGrid.jqGrid('sortableRows', {
        update : function() {
            var sortedIds = dialogDgroffice010SortGrid.jqGrid('getDataIDs');
            for(var i = 0; i < sortedIds.length; i++) {
                dialogDgroffice010SortGrid.jqGrid('setCell', sortedIds[i], 'officeRank010', i + 1);
            }
        }
    });

    $("#dialogDgroffice010SortUpBtn").click(function() {
        var selectedRowId = dialogDgroffice010SortGrid.jqGrid ('getGridParam', 'selrow');        
        dialogDgroffice010SortResort(selectedRowId, "up");
    });
    
    $("#dialogDgroffice010SortDownBtn").click(function() {
        var selectedRowId = dialogDgroffice010SortGrid.jqGrid ('getGridParam', 'selrow');        
        dialogDgroffice010SortResort(selectedRowId, "down");
    });
    
    var dialogDgroffice010SortDoSaveCallBack = function(param){
        var dialogDgroffice010SortCallBackFunction = $("#dialogDgroffice010SortCallBackFunction", dialogObj).val();
        if(isEmpty(dialogDgroffice010SortCallBackFunction) == false){
            eval(dialogDgroffice010SortCallBackFunction+'()');
        }
    };
    
    var dialogDgroffice010SortDoSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var rowList = dialogDgroffice010SortGrid.getRowData();

        var saveDatas = [];
        var saveData = {};
        for(var i = 0; i < rowList.length; i++){
        
            if(isEmpty(rowList[i].officeCd) == false){
                saveData = {};
                saveData["fisYear"] = rowList[i].fisYear;
                saveData["bgtDgr"] = rowList[i].bgtDgr;
                saveData["officeCd"] = rowList[i].officeCd;
                saveData["officeRank010"] = rowList[i].officeRank010;
                
                saveDatas.push(saveData);
            }
        }
        
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogDgroffice010SaveOfficeRank010s.do",
            data : {saveDatas: saveDatas}
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogDgroffice010SortClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                dialogDgroffice010SortDoSaveCallBack(data);
            }
        });
    };
    
    var dialogDgroffice010SortDoInit = function(params){
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogDgroffice010InitOfficeRank010s.do",
            data : {
                fisYear: $("#dialogDgroffice010SortFisYear", dialogObj).val(),
                bgtDgr: $("#dialogDgroffice010SortBgtDgr", dialogObj).val()
               }
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogDgroffice010SortClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
            	doDialogDgroffice010SortSearch();
                dialogDgroffice010SortDoSaveCallBack(data);

            }
        });
    };
    
    var dialogDgroffice010SortSaveBtnClick = function(){
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : dialogDgroffice010SortDoSave
        });
    };
    
    var dialogDgroffice010SortClose = function(){

        $("#dialogDgroffice010SortCallBackFunction", dialogObj).val("");
        $("#dialogDgroffice010SortFisYear", dialogObj).val("");
        $("#dialogDgroffice010SortBgtDgr", dialogObj).val("");
        
        $("#dialogDgroffice010SortDiv").dialog("close");
    };
    
    $("#dialogDgroffice010SortDiv").dialog({
        title: "정렬순서변경",
        autoOpen: false,
        width: 310,
        height: 430,
        modal: true,
        resizable: false,
        open: function(event, ui){
            doDialogDgroffice010SortSearch();
        },
        buttons : {
            "저장" : function() {
                dialogDgroffice010SortSaveBtnClick();
            },
            "닫기" : function() {
                dialogDgroffice010SortClose();
            },
            "초기화" : function() {
            	dialogDgroffice010SortDoInit();
            }
        }
    });
    
});
</script>
<div id="dialogDgroffice010SortDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgroffice010SortCallBackFunction"/>
  <input type="hidden" id="dialogDgroffice010SortFisYear"/>
  <input type="hidden" id="dialogDgroffice010SortBgtDgr"/>
  <div class="btn">
    <div class="btnR">
      <span style="font-size:11px;color:#888;margin-right:6px;">행을 드래그하여 순서 변경 가능</span>
      <a id="dialogDgroffice010SortUpBtn" class="btnClass" href="#" enabledYn="Y">위</a>
      <a id="dialogDgroffice010SortDownBtn" class="btnClass" href="#" enabledYn="Y">아래</a>
    </div>
  </div>
  <div id="DIALOG_DGROFFICE010_SORT_DIV" class="csGrid">
    <table id="DIALOG_DGROFFICE010_SORT_GRD" ></table>
  </div>
</div>