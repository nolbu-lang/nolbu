<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogReport070OrderYmdModifyDiv");

    _dialogReport070OrderYmdModifyOrderYmdSeq = 0;
    
    var orderYmdFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        _dialogReport070OrderYmdModifyOrderYmdSeq = _dialogReport070OrderYmdModifyOrderYmdSeq > Number(rowObject.orderYmdSeq) ? _dialogReport070OrderYmdModifyOrderYmdSeq : Number(rowObject.orderYmdSeq);
        
        var rVal = '<input id="dialogReport070OrderYmdModifyOrderYmd_'+rowObject.orderYmdSeq+'" value="'+toDateFormat(cellValue)+'" maxlength="10" class="txtCal" />'; 
        
        return rVal;
    };
    
    var dialogReport070OrderYmdModifyColNames = ['지시일자', 'fisYear', 'bgtDgr', 'orderYmdSeq'];
    var dialogReport070OrderYmdModifyColModel = [
                                    {name : 'orderYmd', index : 'orderYmd', width : 110, sortable : false, fixed : true, align : 'center',
                                        formatter: orderYmdFormatter
                                    },
                                    {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                                    {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                                    {name : 'orderYmdSeq', index : 'orderYmdSeq', width : 0, sortable : false, hidden : true}

    ];
    
    var dialogReport070OrderYmdModifyGridParam = {
            id : "DIALOG_REPORT070_ORDERYMD_MODIFY",
            colNames : dialogReport070OrderYmdModifyColNames,
            colModel : dialogReport070OrderYmdModifyColModel,
            rowNum : 1000,
            multiselect: true,
            width : 160,
            height : 150,
            defaultRows: 0,
            beforeSelectRow: function(rowid, e){
                if(isEmpty($(e.target).closest('td')[0]) == true){
                    return;
                }
                
                var i = $.jgrid.getCellIndex($(e.target).closest('td')[0]);
                var cm = $(this).jqGrid('getGridParam', 'colModel');
                
                if(cm[i].name !== 'cb'){
                    return false;
                }
                
                var cbsdis = $("tr#"+rowid+".jqgrow > td > input.cbox:disabled", $(this)[0]);
                if(cbsdis.length === 0){
                    return true;
                }
                
                return false;
            },
            onSelectAll: function(aRowids,status){
                if(status){
                  var cbs = $("tr.jqgrow > td > input.cbox:disabled", $(this)[0]);
                  
                  cbs.removeAttr("checked");
                      
                  var selarrow = $(this).find("tr.jqgrow:has(td > input.cbox:checked)").map(function() { return this.id; }).get();
                  dialogReport070OrderYmdModifyGrid[0].p.selarrrow = selarrow;
                }
            },
            gridComplete: function() {
                $(".txtCal", dialogObj).csDatepicker();
            }
    };
    
    var dialogReport070OrderYmdModifyGrid = $.csGrid(dialogReport070OrderYmdModifyGridParam);
    
    var doDialogReport070OrderYmdModifySearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        dialogReport070OrderYmdModifyGrid.addCsJsonData(data);
    };
    
    var doDialogReport070OrderYmdModifySearch = function() {
        _dialogReport070OrderYmdModifyOrderYmdSeq = 0;
        
        $.csAjaxCall({
            url : "/dialog/ajaxDialogReport070OrderYmdModifyList.do",
            data : {
                    fisYear: $("#dialogReport070OrderYmdModifyFisYear", dialogObj).val(),
                    bgtDgr: $("#dialogReport070OrderYmdModifyBgtDgr", dialogObj).val()
                   },
            async : true,
            callBack : doDialogReport070OrderYmdModifySearchCallBack
        });
    };
    
    var dialogReport070OrderYmdModifyDoSaveCallBack = function(param){
        var dialogReport070OrderYmdModifyCallBackFunction = $("#dialogReport070OrderYmdModifyCallBackFunction", dialogObj).val();
        if(isEmpty(dialogReport070OrderYmdModifyCallBackFunction) == false){
            eval(dialogReport070OrderYmdModifyCallBackFunction+'()');
        }
    };
    
    var dialogReport070OrderYmdModifyDoSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var rowList = dialogReport070OrderYmdModifyGrid.getRowData();

        var saveDatas = [];
        var saveData = {};
        var orderYmd = "";
        for(var i = 0; i < rowList.length; i++){
        
            if(isEmpty(rowList[i].orderYmdSeq) == false){
                orderYmd = $('#dialogReport070OrderYmdModifyOrderYmd_'+rowList[i].orderYmdSeq, dialogObj).val().replaceAll("-", "");
                
                for(var j = 0; j < saveDatas.length; j++){                    
                    if(saveDatas[j].orderYmd == orderYmd){
                        $.csAlert({
                            msg : "동일한 지시일자가 존재합니다.",
                            callBack : function() {
                                $('#dialogReport070OrderYmdModifyOrderYmd_'+rowList[i].orderYmdSeq, dialogObj).focus();
                            }
                        });
                        
                        return;
                    }
                }
                
                saveData = {};
                saveData["fisYear"] = rowList[i].fisYear;
                saveData["bgtDgr"] = rowList[i].bgtDgr;
                saveData["orderYmdSeq"] = rowList[i].orderYmdSeq;
                saveData["orderYmd"] = orderYmd;
                
                saveDatas.push(saveData);
            }
        }
        
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogReport070OrderYmdModifySave.do",
            data : {
                fisYear: $("#dialogReport070OrderYmdModifyFisYear", dialogObj).val(),
                bgtDgr: $("#dialogReport070OrderYmdModifyBgtDgr", dialogObj).val(),
                report070Os: saveDatas
            }
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogReport070OrderYmdModifyClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                dialogReport070OrderYmdModifyDoSaveCallBack(data);

                dialogReport070OrderYmdModifyClose();
            }
        });
    };
    
    var dialogReport070OrderYmdModifySaveBtnClick = function(){
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : dialogReport070OrderYmdModifyDoSave
        });
    };
    
    var dialogReport070OrderYmdModifyClose = function(){

        $("#dialogReport070OrderYmdModifyCallBackFunction", dialogObj).val("");
        $("#dialogReport070OrderYmdModifyFisYear", dialogObj).val("");
        $("#dialogReport070OrderYmdModifyBgtDgr", dialogObj).val("");
        
        $("#dialogReport070OrderYmdModifyDiv").dialog("close");
    };
        
    $("#dialogReport070OrderYmdModifyAddBtn", dialogObj).click(function() {
        _dialogReport070OrderYmdModifyOrderYmdSeq++;
        var addData = {
                orderYmd : $.datepicker.formatDate('yy-mm-dd', new Date()),
                fisYear : $("#dialogReport070OrderYmdModifyFisYear", dialogObj).val(),
                bgtDgr : $("#dialogReport070OrderYmdModifyBgtDgr", dialogObj).val(),
                orderYmdSeq : _dialogReport070OrderYmdModifyOrderYmdSeq
        };
            
        dialogReport070OrderYmdModifyGrid.jqGrid('addRowData', _dialogReport070OrderYmdModifyOrderYmdSeq, addData);
    });
        
    $("#dialogReport070OrderYmdModifyDelBtn", dialogObj).click(function() {
        var selRows = dialogReport070OrderYmdModifyGrid.getGridParam('selarrrow');

        var rowIds = ("" + selRows).split(",");
        if(rowIds.length < 1){
            $.csAlert({
                msg : "삭제할 항목을 선택하여 주십시오."
            });
            
            return;
        }
        
        for(var i = 0; i < rowIds.length; i++){
            dialogReport070OrderYmdModifyGrid.delRowData(rowIds[i]);
        }
        
        dialogReport070OrderYmdModifyGrid.jqGrid('resetSelection');
    });
    
    $("#dialogReport070OrderYmdModifyDiv").dialog({
        title: "지시일자 추가",
        autoOpen: false,
        width: 200,
        height: 320,
        modal: true,
        resizable: false,
        open: function(event, ui){
            doDialogReport070OrderYmdModifySearch();
        },
        buttons : {
            "저장" : function() {
                dialogReport070OrderYmdModifySaveBtnClick();
            },
            "닫기" : function() {
                dialogReport070OrderYmdModifyClose();
            }
        }
    });
    
});
</script>
<div id="dialogReport070OrderYmdModifyDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogReport070OrderYmdModifyCallBackFunction"/>
  <input type="hidden" id="dialogReport070OrderYmdModifyFisYear"/>
  <input type="hidden" id="dialogReport070OrderYmdModifyBgtDgr"/>
  <div class="btn">
    <div class="btnR">
      <a id="dialogReport070OrderYmdModifyAddBtn" class="btnClass" href="#" enabledYn="Y">추가</a>
      <a id="dialogReport070OrderYmdModifyDelBtn" class="btnClass" href="#" enabledYn="Y">삭제</a>
    </div>
  </div>
  <div id="DIALOG_REPORT070_ORDERYMD_MODIFY_DIV" class="csGrid">
    <table id="DIALOG_REPORT070_ORDERYMD_MODIFY_GRD" ></table>
  </div>
</div>