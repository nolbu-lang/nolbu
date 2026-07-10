<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
var comboDataIndi;
$(document).ready(function (){
	var tabId = _budgetSelectTabId;
    var tabObj = $("#"+tabId);
    var dialogObj = $("#dialogDgrcompoIndiAttrDiv");
    var maxYear = 0;
  //공통코드 파라미터
    var comboParam = [
                      {id : "fisYear", subQueryId : "FisYear"},			//회계년도
                      {id : "bgtDgr", subQueryId : "BgtDgr"}			//예산차수
                    ];
  
    var comboData = jQuery.csComboAjaxCall(comboParam);
    comboDataIndi = comboData;
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
    	
    	return '<input id="detlCdNm_14'+rowObject.detlCd+'" value="'+rowObject.detlCdNm+'" maxlength="100" class="ui-state-enabled" />';
    }
    
    var dialogDgrcompoIndiAttrColNames = ['', '연도', '차수', '명칭', 'groupId','detlCd', 'lineUpOrd', 'addYn', 'groupCol', 'editYn'];
    
    var dialogDgrcompoIndiAttrColModel = [
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
    
    var setDataInit = function(elem){
    	$(elem).focus(function(){
    		$(this).select();
    	});
    }
    
    var getGridHeight = function (){
    	var height = 290; 
    	$("#DIALOG_DGR_COMPO_INDI_ATTR_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", height + 20);
        return height;
    };
    
    var dialogDgrcompoIndiAttrGridParam = {
            id : "DIALOG_DGR_COMPO_INDI_ATTR",
            colNames : dialogDgrcompoIndiAttrColNames,
            colModel : dialogDgrcompoIndiAttrColModel,
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
    
    $("#DIALOG_DGR_COMPO_INDI_ATTR_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", getGridHeight() + 20);
    
    var dialogDgrcompoIndiAttrGrid = $.csGrid(dialogDgrcompoIndiAttrGridParam);
    
    var dialogDgrcompoIndiAttrClose = function(){
    	
    	var dialogDgrcompoIndiAttrCallBackFunction = $("#dialogDgrcompoIndiAttrCallBackFunction", dialogObj).val();
        if(isEmpty(dialogDgrcompoIndiAttrCallBackFunction) == false){
            eval(dialogDgrcompoIndiAttrCallBackFunction + '()');
        }
        dialogDgrcompoIndiAttrGrid.trigger('reloadGrid');
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
    	//dialogDgrcompoIndiAttrGrid = $.csGrid(dialogDgrcompoIndiAttrGridParam);
    	
    	$.csAjaxCall({
            url : "/budget/ajaxBudgetCommCdList.do",
            data: {codeId : "RP014", order: 'indi'},
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
            	var groupId = '';
            	var fisYear = $('#fisYear_' + rowData.detlCd + ' option:selected').val();
            	var bgtDgr = $('#bgtDgr_' + rowData.detlCd + ' option:selected').val();
            	groupId = fisYear + '|' + bgtDgr;
            	
                selectedData = {};
                selectedData["clCd"] = 'RP014';
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
                groupId : maxYear,
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
            data : {codeId : "RP014", delData: selectedDatas, order: 'indi'}
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
            	dialogDgrcompoIndiAttrClose();
            	//doDialogDgrcompoIndiAttrSearchCallBack(data);
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
           	var groupId = '';
        	var fisYear = $('#fisYear_' + rowData.detlCd + ' option:selected').val();
        	var bgtDgr = $('#bgtDgr_' + rowData.detlCd + ' option:selected').val();
        	groupId = fisYear + '|' + bgtDgr;
        	
        	var updateFlag = false;
        	
        	if(groupId != rowData.groupId || detlCd != rowData.detlCd || detlCdNm != detlCdNm){
        		updateFlag = true;
        	}
        	
        	if(updateFlag){
        		if(isEmpty(detlCdNm) == false && detlCdNm != ''
		        		){
		        	saveData = {};
		            saveData["clCd"] = 'RP014';
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
    	var fisYear = $("#condFisYear option:selected", tabObj).val();
    	$("#condIndiAttr1", tabObj).csCreatCombo(comboData
    			, {id: 'indiAttr'
	    			, groupId: fisYear
	    			, selectedValue: selectedValue1
	    			, comboType: 'A'
	    			, comboTypeValue: ''
	    			}
    	);
    	
    	var selectedValue2 = $('#condIndiAttr2').val();
    	$("#condIndiAttr2", tabObj).csCreatCombo(comboData
    			, {id: 'indiAttr'
	    			, groupId: fisYear
	    			, selectedValue: selectedValue2
	    			, comboType: 'A'
	    			, comboTypeValue: ''
    	});
    	
    	var selectedValue3 = $('#condIndiAttr3').val();
    	$("#condIndiAttr3", tabObj).csCreatCombo(comboData
    			, {id: 'indiAttr'
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
	
	if(comboDataIndi){
		$("#bgtDgr_" + objId).csCreatCombo(comboDataIndi
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
