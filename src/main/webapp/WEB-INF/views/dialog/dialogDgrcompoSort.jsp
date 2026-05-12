<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogDgrcompoSortDiv");
    
    var dialogDgrcompoSortColNames = ['사업명', '요구액', '예산액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'sortSeq'];
    var dialogDgrcompoSortColModel = [
                                    {name : 'compGround', index : 'dgrcompoNm', width : 300, sortable : false, fixed : true, align : 'left' },
                                    {name : 'demandBgtAmt', index : 'demandBgtAmt', width : 100, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                    {name : 'bgtAmt', index : 'bgtAmt', width : 100, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                    {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                                    {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                                    {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, key: true, sortable : false, hidden : true},
                                    {name : 'sortSeq', index : 'sortSeq', width : 0, sortable : false, hidden : true}

    ];
    
    var dialogDgrcompoSortGridParam = {
            id : "DIALOG_DGRCOMPO_SORT",
            colNames : dialogDgrcompoSortColNames,
            colModel : dialogDgrcompoSortColModel,
            rowNum : 1000,
            width : "auto",
            height : "auto",
            resizable: true,
            defaultRows: 0
    };
    
    var dialogDgrcompoSortGrid = $.csGrid(dialogDgrcompoSortGridParam);
    
    var doDialogDgrcompoSortSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        dialogDgrcompoSortGrid.addCsJsonData(data);
    };
    
    var doDialogDgrcompoSortSearch = function() {
    	var dbizCd = $("#dialogDgrcompoSortDbizCd", dialogObj).val();
    	console.log('dbizCd : ' + dbizCd);
    	if(isEmpty(dbizCd) == true){
    		$.csAjaxCall({
                url : "/dialog/ajaxDgrcompoSortDgrcompoList.do",
                data : {
                        fisYear: $("#dialogDgrcompoSortFisYear", dialogObj).val(),
                        bgtDgr: $("#dialogDgrcompoSortBgtDgr", dialogObj).val(),
                        teBgtCompoId: $("#dialogDgrcompoSortTeBgtCompoId", dialogObj).val()
                       },
                async : true,
                callBack : doDialogDgrcompoSortSearchCallBack
            });
    	}else{
    		$.csAjaxCall({
                url : "/dialog/ajaxDgrcompoSortDgrcompoListNew.do",
                data : {
                        fisYear: $("#dialogDgrcompoSortFisYear", dialogObj).val(),
                        bgtDgr: $("#dialogDgrcompoSortBgtDgr", dialogObj).val(),
                        deptCd: $("#dialogDgrcompoSortDeptCd", dialogObj).val(),
                        dbizCd: $("#dialogDgrcompoSortDbizCd", dialogObj).val()
                        //teBgtCompoId: $("#dialogDgrcompoSortTeBgtCompoId", dialogObj).val()
                       },
                async : true,
                callBack : doDialogDgrcompoSortSearchCallBack
            });
    	}
        
    };
    
    var dialogDgrcompoSortResort = function(rowId, sortFg){
        
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "변경할 항목을 선택하여 주십시오."
            });
            
            return;
        }
        
        var rowList = dialogDgrcompoSortGrid.getRowData();

        var targetId = "";
        for(var i = 0; i < rowList.length; i++) {
            if(rowId === rowList[i].teBgtCompoId){
                if(sortFg === "up"){
                    if(i > 0){
                        targetId = rowList[i-1].teBgtCompoId;
                    }
                }else if(sortFg === "down"){
                    targetId = rowList[i+1].teBgtCompoId;
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
        
        var srcData = dialogDgrcompoSortGrid.getRowData(rowId);
        var targetData = dialogDgrcompoSortGrid.getRowData(targetId);
        var tempSortSeq = targetData.sortSeq;
        
        dialogDgrcompoSortGrid.jqGrid('setCell', targetId, 'sortSeq', srcData.sortSeq);
        srcData.sortSeq = tempSortSeq;
        dialogDgrcompoSortGrid.delRowData(rowId);
        if(sortFg === "up"){
            dialogDgrcompoSortGrid.addRowData(rowId, srcData, "before", targetId);
        }else if(sortFg === "down"){
            dialogDgrcompoSortGrid.addRowData(rowId, srcData, "after", targetId);
        }
        
        dialogDgrcompoSortGrid.jqGrid('setSelection',rowId);
    };
    
    $("#dialogDgrcompoSortUpBtn").click(function() {
        var selectedRowId = dialogDgrcompoSortGrid.jqGrid ('getGridParam', 'selrow');        
        dialogDgrcompoSortResort(selectedRowId, "up");
    });
    
    $("#dialogDgrcompoSortDownBtn").click(function() {
        var selectedRowId = dialogDgrcompoSortGrid.jqGrid ('getGridParam', 'selrow');        
        dialogDgrcompoSortResort(selectedRowId, "down");
    });
    
    var dialogDgrcompoSortDoSaveCallBack = function(param){
        var dialogDgrcompoSortCallBackFunction = $("#dialogDgrcompoSortCallBackFunction", dialogObj).val();
        if(isEmpty(dialogDgrcompoSortCallBackFunction) == false){
            eval(dialogDgrcompoSortCallBackFunction+ '('+ jsonToString(param.dgrcompo) + ')');
        }
    };
    
    var dialogDgrcompoSortDoSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var rowList = dialogDgrcompoSortGrid.getRowData();

        var saveDatas = [];
        var saveData = {};
        for(var i = 0; i < rowList.length; i++){
        
            if(isEmpty(rowList[i].teBgtCompoId) == false){
                saveData = {};
                saveData["fisYear"] = rowList[i].fisYear;
                saveData["bgtDgr"] = rowList[i].bgtDgr;
                saveData["teBgtCompoId"] = rowList[i].teBgtCompoId;
                saveData["sortSeq"] = (i + 1) * 1000;
                //saveData["sortSeq"] = rowList[i].sortSeq;
                
                saveDatas.push(saveData);
            }
        }
        
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoSortSaveDgrcompoSorts.do",
            data : {saveDatas: saveDatas}
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogDgrcompoSortClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                dialogDgrcompoSortDoSaveCallBack(data);

                dialogDgrcompoSortClose();
            }
        });
    };
    
    var dialogDgrcompoSortSaveBtnClick = function(){
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : dialogDgrcompoSortDoSave
        });
    };
    
    var dialogDgrcompoSortClose = function(){

        $("#dialogDgrcompoSortCallBackFunction", dialogObj).val("");
        $("#dialogDgrcompoSortFisYear", dialogObj).val("");
        $("#dialogDgrcompoSortBgtDgr", dialogObj).val("");
        $("#dialogDgrcompoSortTeBgtCompoId",dialogObj).val("");
        $("#dialogDgrcompoSortDeptCd",dialogObj).val("");
        $("#dialogDgrcompoSortDbizCd",dialogObj).val("");
        
        $("#dialogDgrcompoSortDiv").dialog("close");
    };
    
    $("#dialogDgrcompoSortDiv").dialog({
        title: "정렬순서변경",
        autoOpen: false,
        width: 'auto',
        height: 'auto',
        modal: true,
        resizable: false,
        open: function(event, ui){
            doDialogDgrcompoSortSearch();
        },
        buttons : {
            "저장" : function() {
                dialogDgrcompoSortSaveBtnClick();
            },
            "닫기" : function() {
                dialogDgrcompoSortClose();
            }
        }
    });
    
});
</script>
<div id="dialogDgrcompoSortDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgrcompoSortCallBackFunction"/>
  <input type="hidden" id="dialogDgrcompoSortFisYear"/>
  <input type="hidden" id="dialogDgrcompoSortBgtDgr"/>
  <input type="hidden" id="dialogDgrcompoSortDeptCd"/>
  <input type="hidden" id="dialogDgrcompoSortDbizCd"/>
  <input type="hidden" id="dialogDgrcompoSortTeBgtCompoId"/>
  <div class="btn">
    <div class="btnR">
      <a id="dialogDgrcompoSortUpBtn" class="btnClass" href="#" enabledYn="Y">위</a>
      <a id="dialogDgrcompoSortDownBtn" class="btnClass" href="#" enabledYn="Y">아래</a>
    </div>
  </div>
  <div id="DIALOG_DGRCOMPO_SORT_DIV" class="csGrid">
    <table id="DIALOG_DGRCOMPO_SORT_GRD" ></table>
  </div>
</div>