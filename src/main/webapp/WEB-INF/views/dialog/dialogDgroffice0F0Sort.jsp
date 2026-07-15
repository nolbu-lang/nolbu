<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogDgroffice0F0SortDiv");
    
    var dialogDgroffice0F0SortColNames = ['실국명', 'fisYear', 'bgtDgr', 'officeCd', 'officeRank0f0'];
    var dialogDgroffice0F0SortColModel = [
                                    {name : 'officeNm', index : 'officeNm', width : 250, sortable : false, fixed : true, align : 'left' },
                                    {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                                    {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                                    {name : 'officeCd', index : 'officeCd', width : 0, key: true, sortable : false, hidden : true},
                                    {name : 'officeRank0f0', index : 'officeRank0f0', width : 0, sortable : false, hidden : true}

    ];
    
    var dialogDgroffice0F0SortGridParam = {
            id : "DIALOG_DGROFFICE0F0_SORT",
            colNames : dialogDgroffice0F0SortColNames,
            colModel : dialogDgroffice0F0SortColModel,
            rowNum : 1000,
            width : "auto",
            height : "auto",
            defaultRows: 0
    };
    
    var dialogDgroffice0F0SortGrid = $.csGrid(dialogDgroffice0F0SortGridParam);
    
    var doDialogDgroffice0F0SortSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        dialogDgroffice0F0SortGrid.addCsJsonData(data);
    };
    
    var doDialogDgroffice0F0SortSearch = function() {
        $.csAjaxCall({
            url : "/dialog/ajaxDgroffice0F0SortDgroffice0F0List.do",
            data : {
                    fisYear: $("#dialogDgroffice0F0SortFisYear", dialogObj).val(),
                    bgtDgr: $("#dialogDgroffice0F0SortBgtDgr", dialogObj).val()
                   },
            async : true,
            callBack : doDialogDgroffice0F0SortSearchCallBack
        });
    };
    
    var dialogDgroffice0F0SortResort = function(rowId, sortFg){
        
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "변경할 항목을 선택하여 주십시오."
            });
            
            return;
        }
        
        var rowList = dialogDgroffice0F0SortGrid.getRowData();

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
        
        var srcData = dialogDgroffice0F0SortGrid.getRowData(rowId);
        var targetData = dialogDgroffice0F0SortGrid.getRowData(targetId);
        var tempOfficeRank0F0 = targetData.officeRank0f0;
        
        dialogDgroffice0F0SortGrid.jqGrid('setCell', targetId, 'officeRank0f0', srcData.officeRank0f0);
        srcData.officeRank0f0 = tempOfficeRank0F0;
        dialogDgroffice0F0SortGrid.delRowData(rowId);
        if(sortFg === "up"){
            dialogDgroffice0F0SortGrid.addRowData(rowId, srcData, "before", targetId);
        }else if(sortFg === "down"){
            dialogDgroffice0F0SortGrid.addRowData(rowId, srcData, "after", targetId);
        }
        
        dialogDgroffice0F0SortGrid.jqGrid('setSelection',rowId);
    };

    dialogDgroffice0F0SortGrid.jqGrid('sortableRows', {
        update : function() {
            var sortedIds = dialogDgroffice0F0SortGrid.jqGrid('getDataIDs');
            for(var i = 0; i < sortedIds.length; i++) {
                dialogDgroffice0F0SortGrid.jqGrid('setCell', sortedIds[i], 'officeRank0f0', i + 1);
            }
        }
    });

    $("#dialogDgroffice0F0SortUpBtn").click(function() {
        var selectedRowId = dialogDgroffice0F0SortGrid.jqGrid ('getGridParam', 'selrow');        
        dialogDgroffice0F0SortResort(selectedRowId, "up");
    });
    
    $("#dialogDgroffice0F0SortDownBtn").click(function() {
        var selectedRowId = dialogDgroffice0F0SortGrid.jqGrid ('getGridParam', 'selrow');        
        dialogDgroffice0F0SortResort(selectedRowId, "down");
    });
    
    var dialogDgroffice0F0SortDoSaveCallBack = function(param){
        var dialogDgroffice0F0SortCallBackFunction = $("#dialogDgroffice0F0SortCallBackFunction", dialogObj).val();
        if(isEmpty(dialogDgroffice0F0SortCallBackFunction) == false){
            eval(dialogDgroffice0F0SortCallBackFunction+'()');
        }
    };
    
    var dialogDgroffice0F0SortDoSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var rowList = dialogDgroffice0F0SortGrid.getRowData();

        var saveDatas = [];
        var saveData = {};
        for(var i = 0; i < rowList.length; i++){
        
            if(isEmpty(rowList[i].officeCd) == false){
                saveData = {};
                saveData["fisYear"] = rowList[i].fisYear;
                saveData["bgtDgr"] = rowList[i].bgtDgr;
                saveData["officeCd"] = rowList[i].officeCd;
                saveData["officeRank0F0"] = rowList[i].officeRank0f0;
                
                saveDatas.push(saveData);
            }
        }
        
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogDgroffice0F0SaveOfficeRank0F0s.do",
            data : {saveDatas: saveDatas}
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogDgroffice0F0SortClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                dialogDgroffice0F0SortDoSaveCallBack(data);

                dialogDgroffice0F0SortClose();
            }
        });
    };
    
    var dialogDgroffice0F0SortSaveBtnClick = function(){
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : dialogDgroffice0F0SortDoSave
        });
    };
    
    var dialogDgroffice0F0SortClose = function(){

        $("#dialogDgroffice0F0SortCallBackFunction", dialogObj).val("");
        $("#dialogDgroffice0F0SortFisYear", dialogObj).val("");
        $("#dialogDgroffice0F0SortBgtDgr", dialogObj).val("");
        
        $("#dialogDgroffice0F0SortDiv").dialog("close");
    };
    
    $("#dialogDgroffice0F0SortDiv").dialog({
        title: "정렬순서변경",
        autoOpen: false,
        width: 310,
        height: 430,
        modal: true,
        resizable: true,
        open: function(event, ui){
            doDialogDgroffice0F0SortSearch();
        },
        buttons : {
            "저장" : function() {
                dialogDgroffice0F0SortSaveBtnClick();
            },
            "닫기" : function() {
                dialogDgroffice0F0SortClose();
            }
        }
    });
    
});
</script>
<div id="dialogDgroffice0F0SortDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgroffice0F0SortCallBackFunction"/>
  <input type="hidden" id="dialogDgroffice0F0SortFisYear"/>
  <input type="hidden" id="dialogDgroffice0F0SortBgtDgr"/>
  <div class="btn">
    <div class="btnR">
      <span style="font-size:11px;color:#888;margin-right:6px;">행을 드래그하여 순서 변경 가능</span>
      <a id="dialogDgroffice0F0SortUpBtn" class="btnClass" href="#" enabledYn="Y">위</a>
      <a id="dialogDgroffice0F0SortDownBtn" class="btnClass" href="#" enabledYn="Y">아래</a>
    </div>
  </div>
  <div id="DIALOG_DGROFFICE0F0_SORT_DIV" class="csGrid">
    <table id="DIALOG_DGROFFICE0F0_SORT_GRD" ></table>
  </div>
</div>