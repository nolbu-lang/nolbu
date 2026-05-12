<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
	var tabId = _budgetSelectTabId;
    var tabObj = $("#"+tabId);
    var dialogObj = $("#dialogDgrcompoIndiAttrDiv");
    
    var detlCdNmFormatter = function(cellValue, options, rowObject){
    	
    	return '<input id="detlCdNm_14'+rowObject.detlCd+'" value="'+rowObject.detlCdNm+'" maxlength="20" class="ui-state-enabled" />';
    }
    
    var dialogDgrcompoIndiAttrColNames = ['', '명칭', 'detlCd', 'groupId', 'lineUpOrd', 'addYn'];
    
    var dialogDgrcompoIndiAttrColModel = [
						{name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, formatter:'checkbox', editoptions:{value:'Y:N'}, formatoptions:{disabled:false}}
                        , {name : 'detlCdNm', index : 'detlCdNm', width : 400, sortable : false, fixed : true, align : 'left',
                        	formatter:detlCdNmFormatter}
                        , {name : 'detlCd', index : 'detlCd', width : 0, sortable : false, hidden : true}
                        , {name : 'groupId', index : 'groupId', width : 0, sortable : false, hidden : true}
                        , {name : 'lineUpOrd', index : 'lineUpOrd', width : 0, sortable : false, hidden : true}
                        , {name : 'addYn', index : 'lineUpOrd', width : 0, sortable : false, hidden : true}
                    ];
    
    var setDataInit = function(elem){
    	$(elem).focus(function(){
    		$(this).select();
    	});
    }
    var dialogDgrcompoIndiAttrGridParam = {
            id : "DIALOG_DGR_COMPO_INDI_ATTR",
            colNames : dialogDgrcompoIndiAttrColNames,
            colModel : dialogDgrcompoIndiAttrColModel,
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
    var dialogDgrcompoIndiAttrGrid = $.csGrid(dialogDgrcompoIndiAttrGridParam);
    
    var dialogDgrcompoIndiAttrClose = function(){
    	
    	var dialogDgrcompoIndiAttrCallBackFunction = $("#dialogDgrcompoIndiAttrCallBackFunction", dialogObj).val();
        if(isEmpty(dialogDgrcompoIndiAttrCallBackFunction) == false){
            
            eval(dialogDgrcompoIndiAttrCallBackFunction + '()');
        }
        
        $("#dialogDgrcompoIndiAttrDiv").dialog("close");
    };
    
    $("#dialogDgrcompoIndiAttrDiv").dialog({
        title: "보고항목 관리",
        autoOpen: false,
        width: 'auto',
        height: 'auto',
        modal: true,
        resizable: true,
        open: function(event, ui){
        	doDialogDgrcompoIndiAttrSearch();
        },
        close: function(event, ui){
        	dialogDgrcompoIndiAttrClose();
        },
        buttons : {
            "저장" : function() {
            	dialogDgrcompoIndiAttrDoSave();
            },
            "닫기" : function() {
            	dialogDgrcompoIndiAttrClose();
            }
        }
    });
    
    function doDialogDgrcompoIndiAttrSearch(){
    	
    	$.csAjaxCall({
            url : "/budget/ajaxBudgetCommCdList.do",
            data: {codeId : "RP014"},
            async : true,
            callBack : doDialogDgrcompoIndiAttrSearchCallBack
        });
    }
    
    var doDialogDgrcompoIndiAttrSearchCallBack = function(data){
    	
    	if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
    	
    	dialogDgrcompoIndiAttrGrid.addCsJsonData(data);
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
                selectedData["clCd"] = 'RP014';
                selectedData["detlCd"] = rowData.detlCd;
                selectedData["useYn"] = 'N';
                selectedData["rowId"]	= rowId;
                selectedDatas.push(selectedData);
                cnt++;
            }
        }
        
        return selectedDatas;
    };
    
    $("#addRowBtn", dialogObj).click(function() {
    	var rowId = dialogDgrcompoIndiAttrGrid.getGridParam("reccount");
    	var trCnt = $('#DIALOG_DGR_COMPO_INDI_ATTR_GRD').find('input').length;
    	
    	if(rowId == 0 && trCnt == 0){
    		dialogDgrcompoIndiAttrGrid.delRowData(1);
    	}
    	
    	var maxDetlCd = getMaxDetlCd();

    	maxDetlCd++;
    	var addData = {
    			detlCdNm : '',
    			detlCd : maxDetlCd,
    			lineUpOrd : 0,
                groupId : 'RP014',
                addYn : 'Y'
        };
    	dialogDgrcompoIndiAttrGrid.jqGrid('addRowData', maxDetlCd, addData);
    });
    
    $("#delRowBtn", dialogObj).click(function() {
    	var selectedDatas = getSelectedData(dialogDgrcompoIndiAttrGrid, $("#DIALOG_DGR_COMPO_INDI_ATTR_GRD", dialogObj)[0].rows);

    	if(isEmpty(selectedDatas) == true || selectedDatas.length < 1){
    		$.csAlert({
                msg : '삭제할 속성을 선택해주세요.'
            });
        	return false;
    	}
    	
    	$.csConfirm({
            msg : "삭제하시겠습니까?",
            callBack : dialogDgrcompoIndiAttrDoDelete
        });
    });
    
    var dialogDgrcompoIndiAttrDoDelete = function(){
    	
    	//삭제 실행
    	var selectedDatas = getSelectedData(dialogDgrcompoIndiAttrGrid, $("#DIALOG_DGR_COMPO_INDI_ATTR_GRD", dialogObj)[0].rows);
    	var data = $.csAjaxCall({
            url : "/budget/ajaxDialogDgrcompoDelCommCd.do",
            data : {codeId : "RP014", delData: selectedDatas}
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                	//dialogDgrcompoIndiAttrClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : "삭제되었습니다.",
            callBack : function() {
            	doDialogDgrcompoIndiAttrSearchCallBack(data);
            	//setParentIndiAttrData(data);
            }
        });
    	
    }
    
  //저장실행
    var dialogDgrcompoIndiAttrDoSave = function(params){

        var saveData = getSaveData();
        
        if(isEmpty(saveData) == true || saveData.length < 1){
    		$.csAlert({
                msg : '저장할 보고항목이 없습니다.'
            });
        	return false;
    	}

        var data = $.csAjaxCall({
            url : "/budget/ajaxDialogDgrcompoSaveCommCd.do",
            data : {codeId : "RP014", saveData: saveData}
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
            	doDialogDgrcompoIndiAttrSearchCallBack(data);
            	//setParentIndiAttrData(data);
            }
        });
    };
    
    //저장할 데이터만 담기(text박스에 데이터가 입력되어있는경우)
    var getSaveData = function(){
    	
    	var gridRows = dialogDgrcompoIndiAttrGrid.jqGrid("getDataIDs");
    	var saveDatas = [];
        var saveData = {};
        var rowId;
        var rowData;
        var cnt = 0;
        var maxDetlCd = getMaxDetlCd();

        for(var i = 0; i < gridRows.length; i++) {
            rowData = dialogDgrcompoIndiAttrGrid.getRowData(gridRows[i]);
            var lineUpOrd = rowData.lineUpOrd;
            var detlCd = rowData.detlCd;
            var detlCdNm = $('#detlCdNm_14' + detlCd).val();
            
            if(isEmpty(detlCdNm) == false && detlCdNm != ''){
            	saveData = {};
                saveData["clCd"] = 'RP014';
                saveData["detlCd"] = detlCd;
                saveData["groupId"] = 'RP014';
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
    	var gridRows = dialogDgrcompoIndiAttrGrid.jqGrid("getDataIDs");
    	var maxDetlCd = 0;
    	for(var i = 0; i < gridRows.length; i++) {
            rowData = dialogDgrcompoIndiAttrGrid.getRowData(gridRows[i]);
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
    
    var setParentIndiAttrData = function (data){
    	
    	var codeData = convertCodeData(data);
    	comboData = {};
    	comboData["indiAttr"] = codeData;
    	var selectedValue1 = $('#condIndiAttr1').val();
    	$("#condIndiAttr1", tabObj).csCreatCombo(comboData
    			, {id: 'indiAttr'
	    			, groupId: 'RP014'
	    			, selectedValue: selectedValue1
	    			, comboType: 'A'
	    			, comboTypeValue: ''
	    			}
    	);
    	
    	var selectedValue2 = $('#condIndiAttr2').val();
    	$("#condIndiAttr2", tabObj).csCreatCombo(comboData
    			, {id: 'indiAttr'
	    			, groupId: 'RP014'
	    			, selectedValue: selectedValue2
	    			, comboType: 'A'
	    			, comboTypeValue: ''
    	});
    	
    	var selectedValue3 = $('#condIndiAttr3').val();
    	$("#condIndiAttr3", tabObj).csCreatCombo(comboData
    			, {id: 'indiAttr'
	    			, groupId: 'RP014'
	    			, selectedValue: selectedValue3
	    			, comboType: 'A'
	    			, comboTypeValue: ''
    	});
    }
});


</script>
<div id="dialogDgrcompoIndiAttrDiv" class="dialog" style="display:none;">
	<input type="hidden" id="dialogDgrcompoIndiAttrCallBackFunction"/>
  <div id="indiAttrBody">
  	<div class="btn">
        <div class="btnR">
          <!-- <a id="updateAllAmtBtn" class="btnDisabledClass" enabledYn="N" href="#">전체금액조정</a> -->
          <a id="addRowBtn" class="btnClass" href="#">추가</a>
          <a id="delRowBtn" class="btnClass" href="#">삭제</a>
        </div>
    </div>
  	<div id="DIALOG_DGR_COMPO_INDI_ATTR_DIV" class="csGrid">
		<table id="DIALOG_DGR_COMPO_INDI_ATTR_GRD"  style="border:0px;height:100%;"></table>
	</div>
  </div>
</div>
