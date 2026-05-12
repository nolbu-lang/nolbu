<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
	var tabId = _budgetApplyTabId;
	var tabObj = $("#"+tabId);
	var dialogObj = $("#dialogDgrcompoMergeDiv");

	var dialogDgrcompoMergeColNames = ['', '구분(회계-실-부서-세부-통계목)', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'upDgrcompoNm', 'dbizCd', 'upTeBgtCompoId', 'compoLevel', 'teMngMokCd'];

	var dialogDgrcompoMergeColModel = [
									   {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, frozen: true,
											formatter: function (cellValue, option, rowObject) {
												return '<input type="radio" name="radio_' + option.gid + '"  />';
											}
									   },
									   {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 400, sortable : false, fixed : true, align : 'left'},
									   {name : 'fisYear', index : 'fisYear', width : 40, sortable : false, fixed : true, align : 'left', hidden : true},
									   {name : 'bgtDgr', index : 'bgtDgr', width : 40, sortable : false, fixed : true, align : 'left', hidden : true},
									   {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 40, sortable : false, fixed : true, align : 'left', hidden : true},
									   {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 40, sortable : false, fixed : true, align : 'left', hidden : true},
									   {name : 'upDgrcompoNm', index : 'upDgrcompoNm', width : 40, sortable : false, fixed : true, align : 'left', hidden : true},
									   {name : 'dbizCd', index : 'dbizCd', width : 40, sortable : false, fixed : true, align : 'left', hidden : true},
									   {name : 'upTeBgtCompoId', index : 'upTeBgtCompoId', width : 40, sortable : false, fixed : true, align : 'left', hidden : true},
									   {name : 'compoLevel', index : 'compoLevel', width : 40, sortable : false, fixed : true, align : 'left', hidden : true},
									   {name : 'teMngMokCd', index : 'teMngMokCd', width : 40, sortable : false, fixed : true, align : 'left', hidden : true}
									  ];

    var dialogDgrcompoMergeGridParam = {
            id : "DIALOG_DGR_COMPO_MERGE",
            colNames : dialogDgrcompoMergeColNames,
            colModel : dialogDgrcompoMergeColModel,
            cellEdit: true,
            cellsubmit : "clientArray",
            defaultRows : 5,
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
    var dialogDgrcompoMergeGrid = $.csGrid(dialogDgrcompoMergeGridParam);
    
    var dialogDgrcompoMergeClose = function(){
        $("#dialogDgrcompoMergeDiv").dialog("close");
    };
    
    $("#dialogDgrcompoMergeDiv").dialog({
        title: "예산병합",
        autoOpen: false,
        width: 'auto',
        height: 'auto',
        modal: true,
        resizable: true,
        open: function(event, ui){
        	doDialogDgrcompoMergeSearch();
        },
        buttons : {
            "저장" : function() {
            	//confirm창
            	dialogDgrcompoMergeDoSave();
            },
            "닫기" : function() {
            	dialogDgrcompoMergeClose();
            }
        }
    });
    
    var dialogDgrcompoMergeDoSave = function(){
    	var dgrcompoNm = "";
    	var rowId = getSelectedRowId();
    	var rowData = dialogDgrcompoMergeGrid.getRowData(rowId);
    	dgrcompoNm = rowData.dgrcompoNm;
    	if(dgrcompoNm){
    		$.csConfirm({
                msg : dgrcompoNm + " (으)로 병합하시겠습니까?",
                callBack : dialogDgrcompoMergeDoSaveProc
            });	
    	}else{
    		$.csAlert({
                msg : "개별사업을 선택하여주세요."
            });
    	}
    	
    }
    
    var dialogDgrcompoMergeDoSaveProcCallBack = function(data){
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        $.csAlert({
            msg : "병합이 완료되었습니다.",
            callBack : function() {
            	dialogDgrcompoMergeClose();
            	
            	var dialogDgrcompoMergeCallBackFunction = $("#dialogDgrcompoMergeCallBackFunction", dialogObj).val();

                if(isEmpty(data) == true){
                    return;
                }
                
                if(isEmpty(dialogDgrcompoMergeCallBackFunction) == false){
                    eval(dialogDgrcompoMergeCallBackFunction + '('+ jsonToString(data.dgrcompo) + ')');
                }
            }
        });
    };
    
	//confirm 결과
	var dialogDgrcompoMergeDoSaveProc = function(params){
		//취소 눌럿을시 return;
		if(params.confirmData != "Y"){
			return;
		}
		
		//여기 병합프로세스 추가
		var selRowId = getSelectedRowId();
		var gridRows = dialogDgrcompoMergeGrid[0].rows;
    	var selectedDatas = [];
        var selectedData = {};
        var oriDatas = [];
		var oriData = {};
    	for(var i = 0; i < gridRows.length; i++) {
	    	var rowId = gridRows[i].id;
	        
	        if(selRowId == rowId){
	        	var rowData = dialogDgrcompoMergeGrid.getRowData(rowId);
	        	console.log(rowData);
	        	saveData = {};
				saveData["fisYear"] = rowData.fisYear;
				saveData["bgtDgr"] = rowData.bgtDgr;
				saveData["dbizCd"] = rowData.dbizCd;
				saveData["upTeBgtCompoId"] = rowData.upTeBgtCompoId;
				saveData["compoLevel"] = rowData.compoLevel;
				saveData["teMngMokCd"] = rowData.teMngMokCd;
				saveData["teBgtCompoId"] = rowData.teBgtCompoId;
				saveData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
				saveData["rowId"] = (rowId==(i+1)) ? "Y" : "N";
				saveData["cngType"] = "CH01";
				saveData["title"] = rowData.upDgrcompoNm;	//회계-실-부서-세부 저장해야함
				saveData["indiBns"] = rowData.dgrcompoNm;
				saveData["note"] = '';
				
				oriDatas.push(rowData.teBgtCompoId);
	        }else{
	        	var rowData = dialogDgrcompoMergeGrid.getRowData(rowId);
	        	if(isEmpty(rowData.teBgtCompoId) != true){
					oriDatas.push(rowData.teBgtCompoId);	
				}
	        }
    	}
		
		saveData['oriDatas'] = oriDatas;

		var data = $.csAjaxCall({
			url : "/dialog/ajaxDialogDgrcompoCngDgrcompoMerge.do",
			data : {saveData : saveData},
			async : true,
	        callBack : dialogDgrcompoMergeDoSaveProcCallBack
		});

		/* $.csAlert({
			msg : "병합이 완료되었습니다."
		});

		dialogDgrcompoMergeClose(); */
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
  
    
    
    function doDialogDgrcompoMergeSearch(){
    	
    	var budgetApplyGrid = $("#BUDGET_APPLY_GRD", tabObj);
    	
    	var gridRows = budgetApplyGrid[0].rows;
    	var selectedDatas = [];
        var selectedData = {};
    	for(var i = 0; i < gridRows.length; i++) {
	    	var rowId = gridRows[i].id;
	        var rowData = budgetApplyGrid.getRowData(rowId);
	        if(rowData.selYn == "Y" && rowData.teBgtCompoSeq != 0){
	        	selectedData = {};
                selectedData["dgrcompoNm"]	= rowData.dgrcompoNm;	//구분값 추가
                selectedData["fisYear"]	= rowData.fisYear;	//구분값 추가
                selectedData["bgtDgr"]	= rowData.bgtDgr;	//구분값 추가
                selectedData["dbizCd"]	= rowData.dbizCd;	//구분값 추가
                selectedData["upTeBgtCompoId"]	= rowData.upTeBgtCompoId;	//구분값 추가
                selectedData["compoLevel"]	= rowData.compoLevel;	//구분값 추가
                selectedData["teMngMokCd"]	= rowData.teMngMokCd;	//구분값 추가
                selectedData["teBgtCompoId"]	= rowData.teBgtCompoId;	//구분값 추가
                selectedData["teBgtCompoSeq"]	= rowData.teBgtCompoSeq;	//구분값 추가
                selectedData["upDgrcompoNm"]	= rowData.upDgrcompoNm;	//구분값 추가
                selectedDatas.push(selectedData);
	        }
    	}
    	var dataArray = {"dataList": selectedDatas}; 
    	dialogDgrcompoMergeGrid.addCsJsonData(dataArray);
    }
    

});




</script>
<div id="dialogDgrcompoMergeDiv" class="dialog" style="display:none;">
	<input type="hidden" id="dialogDgrcompoMergeCallBackFunction"/>
  <div id="mergeBody">
  	<div id="DIALOG_DGR_COMPO_MERGE_DIV" class="csGrid">
		<table id="DIALOG_DGR_COMPO_MERGE_GRD"  style="border:0px;height:100%;"></table>
	</div>
  </div>
</div>
