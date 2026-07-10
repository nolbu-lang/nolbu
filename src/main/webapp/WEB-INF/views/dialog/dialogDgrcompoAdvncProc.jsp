<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
var comboDataAdvnc;
$(document).ready(function (){
	var tabId = _budgetSelectTabId;
    var tabObj = $("#"+tabId);
    var dialogObj = $("#dialogDgrcompoAdvncProcDiv");
    
    var maxYear = 0;
    //공통코드 파라미터
      var comboParam = [
                        {id : "fisYear", subQueryId : "FisYear"},			//회계년도
                        {id : "bgtDgr", subQueryId : "BgtDgr"}			//예산차수
                      ];
    
      var comboData = jQuery.csComboAjaxCall(comboParam);
      comboDataAdvnc = comboData;
      var fisYearList = comboData.fisYear;
      var bgtDgrList = comboData.bgtDgr;
      
      var fisYearFormatter = function(cellValue, options, rowObject){
      	var groupId = rowObject.groupId;
      	var groupIdArr = groupId.split('|');
      	var fisYear = '';
      	if(groupIdArr.length == 2){
      		fisYear = groupIdArr[0];
      	}else if(groupId){
      		fisYear = groupId
      	}
      	
      	var rVal = '<select id="fisYear_' + rowObject.detlCd + '" name="fisYear_' + rowObject.detlCd + '" onchange="changeFisYear(this, \'' + rowObject.detlCd + '\')">';
      	for(var i=0 ; i<fisYearList.length ; i++){
      		var data = fisYearList[i];
      		var selected = '';
      		if(data.code == fisYear){
      			selected = 'selected="selected"';
      		}
      		rVal += '<option value="' + data.code + '" ' + selected + '>' + data.codeNm + '</option>';
      		
      		if(fisYear > maxYear){
          		maxYear = fisYear;
          	}
      	}

      	rVal += '</select>';
      	return rVal;
      	//return '<input id="groupId_'+rowObject.detlCd+'" value="'+rowObject.groupId+'" maxlength="20" class="ui-state-enabled" />';
      }
      
      var bgtDgrFormatter = function(cellValue, options, rowObject){
      	
      	var groupId = rowObject.groupId;
      	var groupIdArr = groupId.split('|');
      	var fisYear = '';
      	var groupFisYear = '';
      	var groupBgtDgr = '';
      	if(groupIdArr.length == 2){
      		groupFisYear = groupIdArr[0];
      		groupBgtDgr = groupIdArr[1];
      	}else if(groupId){
      		fisYear = groupId
      	}
      	
      	if(fisYear == ''){
      		fisYear = maxYear;
      	}
      	
      	
      	if(fisYear == 'RP015'){
      		fisYear = fisYearList[0].code;
      	}
      	
      	//groupId 에 선택된 fisYear 가져오기
      	for(var i=0 ; i<fisYearList.length ; i++){
      		var data = fisYearList[i];
      		var selected = '';
      		if(data.code == groupFisYear){
      			fisYear = data.code
      		}
      	}
      	
      	//선택된 연도가 없을경우 첫번째 값으로 설정
      	if(fisYear == ''){
  	    	fisYear = fisYearList[0].code;
      	}
      	
      	var rVal = '<select id="bgtDgr_' + rowObject.detlCd + '" name="bgtDgr_' + rowObject.detlCd + '">';
      	for(var i=0 ; i<bgtDgrList.length ; i++){
      		var data = bgtDgrList[i];
      		var selected = '';
      		if(data.groupId == fisYear){
  	    		if(data.groupId == groupFisYear && data.code == groupBgtDgr ){
  	    			selected = 'selected="selected"';
  	    		}
  	    		
  	    		rVal += '<option value="' + data.code + '" ' + selected + '>' + data.codeNm + '</option>';	
      		}
      		
      	}
      	
      	rVal += '</select>';
      	return rVal;
      	//return '<input id="groupId_'+rowObject.detlCd+'" value="'+rowObject.groupId+'" maxlength="20" class="ui-state-enabled" />';
      }
      
    var detlCdNmFormatter = function(cellValue, options, rowObject){
    	
    	return '<input id="detlCdNm_15'+rowObject.detlCd+'" value="'+rowObject.detlCdNm+'" maxlength="20" class="ui-state-enabled" />';
    }
    
	var dialogDgrcompoAdvncProcColNames = ['', '연도', '차수', '명칭', 'groupId','detlCd', 'lineUpOrd', 'addYn', 'groupCol', 'editYn'];
    
    var dialogDgrcompoAdvncProcColModel = [
						{name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, formatter:'checkbox', editoptions:{value:'Y:N'}, formatoptions:{disabled:false}}
                        , {name : 'fisYear', index : 'fisYear', width : 100, sortable : true, fixed : true, align : 'center',
                        	formatter:fisYearFormatter}
                        , {name : 'bgtDgr', index : 'bgtDgr', width : 100, sortable : true, fixed : true, align : 'center',
                        	formatter:bgtDgrFormatter}
                        , {name : 'detlCdNm', index : 'detlCdNm', width : 200, sortable : false, fixed : true, align : 'left',
                        	formatter:detlCdNmFormatter}
                        , {name : 'groupId', index : 'groupId', width : 0, sortable : false, hidden : true}
                        , {name : 'detlCd', index : 'detlCd', width : 0, sortable : false, hidden : true}
                        , {name : 'lineUpOrd', index : 'lineUpOrd', width : 0, sortable : false, hidden : true}
                        , {name : 'addYn', index : 'addYn', width : 0, sortable : false, hidden : true}
                        , {name : 'groupCol', index : 'groupCol', width : 0, sortable : false, hidden : true}
                        , {name : 'editYn', index : 'editYn', width : 0, sortable : false, hidden : true}
                    ]; 
    
    /* var dialogDgrcompoAdvncProcColNames = ['', '명칭', 'detlCd', 'groupId', 'lineUpOrd', 'addYn'];
    
    var dialogDgrcompoAdvncProcColModel = [
						{name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, formatter:'checkbox', editoptions:{value:'Y:N'}, formatoptions:{disabled:false}}
                        , {name : 'detlCdNm', index : 'detlCdNm', width : 400, sortable : false, fixed : true, align : 'left',
                        	formatter:detlCdNmFormatter}
                        , {name : 'detlCd', index : 'detlCd', width : 0, sortable : false, hidden : true}
                        , {name : 'groupId', index : 'groupId', width : 0, sortable : false, hidden : true}
                        , {name : 'lineUpOrd', index : 'lineUpOrd', width : 0, sortable : false, hidden : true}
                        , {name : 'addYn', index : 'addYn', width : 0, sortable : false, hidden : true}
                        
                    ]; */
    
    var setDataInit = function(elem){
    	$(elem).focus(function(){
    		$(this).select();
    	});
    }
                    
    var getGridHeight = function (){
    	var height = 290; 
    	$("#DIALOG_DGR_COMPO_ADVNC_PROC_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", height + 20);
        return height;
    };
    
    var dialogDgrcompoAdvncProcGridParam = {
            id : "DIALOG_DGR_COMPO_ADVNC_PROC",
            colNames : dialogDgrcompoAdvncProcColNames,
            colModel : dialogDgrcompoAdvncProcColModel,
            cellEdit: true,
            cellsubmit : "clientArray",
            defaultRows : 1,
            rowNum : 1000,
            width: "auto",
            height: getGridHeight(),
            sortname: 'groupCol',
            grouping:true,
           	groupingView : {
           		groupField : ['groupCol'],
           		groupColumnShow : [false],
           		groupText : ['<b>{0} - ({1})</b>'],
           		groupCollapse : true
           	},
            loadComplete:function(){
            	
            },
            //height: "auto",
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
    
    $("#DIALOG_DGR_COMPO_ADVNC_PROC_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", getGridHeight() + 20);
    
    var dialogDgrcompoAdvncProcGrid = $.csGrid(dialogDgrcompoAdvncProcGridParam);
    
    var dialogDgrcompoAdvncProcClose = function(){
    	
    	var dialogDgrcompoAdvncProcCallBackFunction = $("#dialogDgrcompoAdvncProcCallBackFunction", dialogObj).val();
        if(isEmpty(dialogDgrcompoAdvncProcCallBackFunction) == false){
            
            eval(dialogDgrcompoAdvncProcCallBackFunction + '()');
        }
        dialogDgrcompoAdvncProcGrid.trigger('reloadGrid');
        $("#dialogDgrcompoAdvncProcDiv").dialog("close");
    };
    
    $("#dialogDgrcompoAdvncProcDiv").dialog({
        title: "분류항목 관리",
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
            data: {codeId : "RP015", order: 'indi'},
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
            	var groupId = '';
            	var fisYear = $('#fisYear_' + rowData.detlCd + ' option:selected').val();
            	var bgtDgr = $('#bgtDgr_' + rowData.detlCd + ' option:selected').val();
            	groupId = fisYear + '|' + bgtDgr;
            	
                selectedData = {};
                selectedData["clCd"] = 'RP015';
                selectedData["detlCd"] = rowData.detlCd;
                selectedData["groupId"] = groupId;
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
    			groupId : maxYear,
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
                msg : '저장할 분류항목가 없습니다.'
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
            	dialogDgrcompoAdvncProcClose();
            	//doDialogDgrcompoAdvncProcSearchCallBack(data);
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
            var groupId = '';
        	var fisYear = $('#fisYear_' + rowData.detlCd + ' option:selected').val();
        	var bgtDgr = $('#bgtDgr_' + rowData.detlCd + ' option:selected').val();
        	groupId = fisYear + '|' + bgtDgr;
        	
        	var updateFlag = false;
        	
        	if(groupId != rowData.groupId || detlCd != rowData.detlCd || detlCdNm != detlCdNm){
        		updateFlag = true;
        	}
        	if(updateFlag){
	            if(isEmpty(detlCdNm) == false && detlCdNm != ''){
	            	saveData = {};
	                saveData["clCd"] = 'RP015';
	                saveData["detlCd"] = detlCd;
		            saveData["groupId"] = groupId;
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
    	var fisYear = $("#condFisYear option:selected", tabObj).val();
    	$("#condAdvncProc1", tabObj).csCreatCombo(comboData
    			, {id: 'advncProc'
    				, groupId: fisYear
	    			, selectedValue: selectedValue1
	    			, comboType: 'A'
	    			, comboTypeValue: ''
	    			}
    	);
    	
    	var selectedValue2 = $('#condAdvncProc2').val();
    	$("#condAdvncProc2", tabObj).csCreatCombo(comboData
    			, {id: 'advncProc'
    			, groupId: fisYear
    			, selectedValue: selectedValue2
    			, comboType: 'A'
    			, comboTypeValue: ''
    	});
    	
    	var selectedValue3 = $('#condAdvncProc3').val();
    	$("#condAdvncProc3", tabObj).csCreatCombo(comboData
    			, {id: 'advncProc'
    			, groupId: fisYear
    			, selectedValue: selectedValue3
    			, comboType: 'A'
    			, comboTypeValue: ''
    	});
    }
    
	var condBgtDgrCreateCombo = function(obj){
    	
    	//console.log(''  + $(obj).val());
        /* $("#groupId2_", tabObj).csCreatCombo(comboData
                , {id: 'bgtDgr'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: ''
                  , comboTypeValue: ''
                  }
        ); */
    };
});

function changeFisYear(obj, objId){
	
	var tabId = _budgetSelectTabId;
    var tabObj = $("#"+tabId);
	var fisYear = $('#fisYear_' + objId + ' option:selected').val();
	
	if(comboDataAdvnc){
		$("#bgtDgr_" + objId).csCreatCombo(comboDataAdvnc
	            , {id: 'bgtDgr'
	              , groupId: fisYear
	              , selectedValue: ''
	              , comboType: ''
	              , comboTypeValue: ''
	              } 
	    );	
	}
}

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
