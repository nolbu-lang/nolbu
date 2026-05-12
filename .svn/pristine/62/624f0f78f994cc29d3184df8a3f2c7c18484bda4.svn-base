<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
	var tabId = _budgetSelectTabId;
    var tabObj = $("#"+tabId);
    var dialogObj = $("#dialogDgrcompoAdvncProcDiv");
    
    var detlCdNmFormatter = function(cellValue, options, rowObject){
    	
    	return '<input id="detlCdNm_15'+rowObject.detlCd+'" value="'+rowObject.detlCdNm+'" maxlength="20" class="ui-state-enabled" />';
    }
    
    var dialogDgrcompoAdvncProcColNames = ['', '명칭', 'detlCd', 'groupId', 'lineUpOrd', 'addYn'];
    
    var dialogDgrcompoAdvncProcColModel = [
						{name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, formatter:'checkbox', editoptions:{value:'Y:N'}, formatoptions:{disabled:false}}
                        , {name : 'detlCdNm', index : 'detlCdNm', width : 400, sortable : false, fixed : true, align : 'left',
                        	formatter:detlCdNmFormatter}
                        , {name : 'detlCd', index : 'detlCd', width : 0, sortable : false, hidden : true}
                        , {name : 'groupId', index : 'groupId', width : 0, sortable : false, hidden : true}
                        , {name : 'lineUpOrd', index : 'lineUpOrd', width : 0, sortable : false, hidden : true}
                        , {name : 'addYn', index : 'addYn', width : 0, sortable : false, hidden : true}
                        
                    ];
    
    var setDataInit = function(elem){
    	$(elem).focus(function(){
    		$(this).select();
    	});
    }
    var dialogDgrcompoAdvncProcGridParam = {
            id : "DIALOG_DGR_COMPO_ADVNC_PROC",
            colNames : dialogDgrcompoAdvncProcColNames,
            colModel : dialogDgrcompoAdvncProcColModel,
            cellEdit: true,
            cellsubmit : "clientArray",
            defaultRows : 1,
            rowNum : 1000,
            width: "auto",
            height: "auto",
            beforeEditCell : function (owid, cellname, value, iRow, iCol){
                //frscEditIRow = iRow;
                //frscEditICol = iCol;
            },
            afterSaveCell : function(rowid,name,val,iRow,iCol) {
                //afterSaveFrsc(dialogDgrcompoSeperateFrscGrid, name);
                
                //frscEditIRow = 0;
                //frscEditICol = 0;
            }
    };
    var dialogDgrcompoAdvncProcGrid = $.csGrid(dialogDgrcompoAdvncProcGridParam);
    
    var dialogDgrcompoAdvncProcClose = function(){
    	
    	var dialogDgrcompoAdvncProcCallBackFunction = $("#dialogDgrcompoAdvncProcCallBackFunction", dialogObj).val();
        if(isEmpty(dialogDgrcompoAdvncProcCallBackFunction) == false){
            
            eval(dialogDgrcompoAdvncProcCallBackFunction + '()');
        }
        
        $("#dialogDgrcompoAdvncProcDiv").dialog("close");
    };
    
    $("#dialogDgrcompoAdvncProcDiv").dialog({
        title: "사전절차 관리",
        autoOpen: false,
        width: 'auto',
        height: 'auto',
        modal: true,
        resizable: true,
        open: function(event, ui){
        	doDialogDgrcompoAdvncProcSearch();
        },
        close: function(event, ui){
        	dialogDgrcompoAdvncProcClose();
        },
        buttons : {
            "저장" : function() {
            	dialogDgrcompoAdvncProcDoSave();
            },
            "닫기" : function() {
            	dialogDgrcompoAdvncProcClose();
            }
        }
    });
    
    function doDialogDgrcompoAdvncProcSearch(){
    	
    	$.csAjaxCall({
            url : "/budget/ajaxBudgetCommCdList.do",
            data: {codeId : "RP015"},
            async : true,
            callBack : doDialogDgrcompoAdvncProcSearchCallBack
        });
    }
    
    var doDialogDgrcompoAdvncProcSearchCallBack = function(data){
    	
    	if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
    	//dialogDgrcompoAdvncProcClose();
    	dialogDgrcompoAdvncProcGrid.addCsJsonData(data);
    }    
    
    var getSelectedRowId = function(){
        var $selRadio = $('input[name=radio_DIALOG_DGR_COMPO_MERGE_GRD]:checked'), $tr;
        if ($selRadio.length > 0) {
            $tr = $selRadio.closest('tr');
            if ($tr.length > 0) {
                return $tr.attr('id');
            }
        }
            
        return "";
    };
    
    var getSelectedData = function(gridObject, gridRows){
        var selectedDatas = [];
        var selectedData = {};
        var rowId;
        var rowData;
        var cnt = 0;
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);

            if(rowData.selYn == "Y"){
                selectedData = {};
                selectedData["clCd"] = 'RP015';
                selectedData["detlCd"] = rowData.detlCd;
                selectedData["useYn"] = 'N';
                selectedData["rowId"]	= rowId;
                selectedDatas.push(selectedData);
                cnt++;
            }
        }
        
        return selectedDatas;
    };
    
    $("#addRowAdvncProcBtn", dialogObj).click(function() {
    	var rowId = dialogDgrcompoAdvncProcGrid.getGridParam("reccount");
    	var trCnt = $('#DIALOG_DGR_COMPO_ADVNC_PROC_GRD').find('input').length;
    	
    	if(rowId == 0 && trCnt == 0){
    		dialogDgrcompoAdvncProcGrid.delRowData(1);
    	}
    	
    	var maxDetlCd = getMaxDetlCd();

    	maxDetlCd++;
    	var addData = {
    			detlCdNm : '',
    			detlCd : maxDetlCd,
    			lineUpOrd : 0,
                groupId : 'RP015',
                addYn : 'Y'
        };
    	dialogDgrcompoAdvncProcGrid.jqGrid('addRowData', maxDetlCd, addData);
    });
    
    $("#delRowAdvncProcBtn", dialogObj).click(function() {
    	var selectedDatas = getSelectedData(dialogDgrcompoAdvncProcGrid, $("#DIALOG_DGR_COMPO_ADVNC_PROC_GRD", dialogObj)[0].rows);

    	if(isEmpty(selectedDatas) == true || selectedDatas.length < 1){
    		$.csAlert({
                msg : '삭제할 속성을 선택해주세요.'
            });
        	return false;
    	}
    	
    	$.csConfirm({
            msg : "삭제하시겠습니까?",
            callBack : dialogDgrcompoAdvncProcDoDelete
        });
    });
    
    var dialogDgrcompoAdvncProcDoDelete = function(){
    	
    	//삭제 실행
    	var selectedDatas = getSelectedData(dialogDgrcompoAdvncProcGrid, $("#DIALOG_DGR_COMPO_ADVNC_PROC_GRD", dialogObj)[0].rows);
    	var data = $.csAjaxCall({
            url : "/budget/ajaxDialogDgrcompoDelCommCd.do",
            data : {codeId : "RP015", delData: selectedDatas}
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
            msg : "삭제되었습니다.",
            callBack : function() {
            	doDialogDgrcompoAdvncProcSearchCallBack(data);
            	//setParentAdvncProcData(data);
            }
        });
    	
    }
    
  //저장실행
    var dialogDgrcompoAdvncProcDoSave = function(params){

        var saveData = getSaveData();
        
        if(isEmpty(saveData) == true || saveData.length < 1){
    		$.csAlert({
                msg : '저장할 사전절차가 없습니다.'
            });
        	return false;
    	}

        var data = $.csAjaxCall({
            url : "/budget/ajaxDialogDgrcompoSaveCommCd.do",
            data : {codeId : "RP015", saveData: saveData}
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        $.csAlert({
            msg : "수정되었습니다.",
            callBack : function() {
            	doDialogDgrcompoAdvncProcSearchCallBack(data);
            	//setParentAdvncProcData(data);
            }
        });
    };
    
    //저장할 데이터만 담기(text박스에 데이터가 입력되어있는경우)
    var getSaveData = function(){
    	
    	var gridRows = dialogDgrcompoAdvncProcGrid.jqGrid("getDataIDs");
    	var saveDatas = [];
        var saveData = {};
        var rowId;
        var rowData;
        var cnt = 0;
        var maxDetlCd = getMaxDetlCd();

        for(var i = 0; i < gridRows.length; i++) {
            rowData = dialogDgrcompoAdvncProcGrid.getRowData(gridRows[i]);
            var lineUpOrd = rowData.lineUpOrd;
            var detlCd = rowData.detlCd;
            var detlCdNm = $('#detlCdNm_15' + detlCd).val();
            
            if(isEmpty(detlCdNm) == false && detlCdNm != ''){
            	saveData = {};
                saveData["clCd"] = 'RP015';
                saveData["detlCd"] = detlCd;
                saveData["groupId"] = 'RP015';
                saveData["detlCdNm"] = detlCdNm;
                saveData["defaultValYn"] = '';
                saveData["lineUpOrd"] = i + 1;
                saveData["mngItemVal"] = '';
                saveData["useYn"] = 'Y';
                saveData["addYn"] = rowData.addYn;
                saveDatas.push(saveData);
                cnt++;
            }
            
        }
        
        return saveDatas;
    }
    
    //코드 최대값 가져오기
    var getMaxDetlCd = function(){
    	var gridRows = dialogDgrcompoAdvncProcGrid.jqGrid("getDataIDs");
    	var maxDetlCd = 0;
    	for(var i = 0; i < gridRows.length; i++) {
            rowData = dialogDgrcompoAdvncProcGrid.getRowData(gridRows[i]);
            var detlCd = parseInt(rowData.detlCd);
            if(isEmpty(detlCd) == false && detlCd > maxDetlCd){
            	maxDetlCd = detlCd;
            }
        }
    	
    	return maxDetlCd;
    }

    var convertCodeData = function(data){
    	var codeDatas = [];
        var codeData = {};
        var dataList = data.dataList;
        for(var i = 0; i < dataList.length; i++) {
            rowData = dataList[i];

            codeData = {};
            codeData["groupId"] = rowData.groupId;
            codeData["code"] = rowData.detlCd;
            codeData["codeNm"] = rowData.detlCdNm;
            codeDatas.push(codeData);
        }
        
        return codeDatas;
    }
    
    var setParentAdvncProcData = function (data){
    	
    	var codeData = convertCodeData(data);
    	comboData = {};
    	comboData["advncProc"] = codeData;
    	var selectedValue1 = $('#condAdvncProc1').val();
    	$("#condAdvncProc1", tabObj).csCreatCombo(comboData
    			, {id: 'advncProc'
	    			, groupId: 'RP015'
	    			, selectedValue: selectedValue1
	    			, comboType: 'A'
	    			, comboTypeValue: ''
	    			}
    	);
    	
    	var selectedValue2 = $('#condAdvncProc2').val();
    	$("#condAdvncProc2", tabObj).csCreatCombo(comboData
    			, {id: 'advncProc'
    			, groupId: 'RP015'
    			, selectedValue: selectedValue2
    			, comboType: 'A'
    			, comboTypeValue: ''
    	});
    	
    	var selectedValue3 = $('#condAdvncProc3').val();
    	$("#condAdvncProc3", tabObj).csCreatCombo(comboData
    			, {id: 'advncProc'
    			, groupId: 'RP015'
    			, selectedValue: selectedValue3
    			, comboType: 'A'
    			, comboTypeValue: ''
    	});
    }
});


</script>
<div id="dialogDgrcompoAdvncProcDiv" class="dialog" style="display:none;">
	<input type="hidden" id="dialogDgrcompoAdvncProcCallBackFunction"/>
  <div id="advncProcBody">
  	<div class="btn">
        <div class="btnR">
          <a id="addRowAdvncProcBtn" class="btnClass" href="#">추가</a>
          <a id="delRowAdvncProcBtn" class="btnClass" href="#">삭제</a>
        </div>
    </div>
  	<div id="DIALOG_DGR_COMPO_ADVNC_PROC_DIV" class="csGrid">
		<table id="DIALOG_DGR_COMPO_ADVNC_PROC_GRD"  style="border:0px;height:100%;"></table>
	</div>
  </div>
</div>
