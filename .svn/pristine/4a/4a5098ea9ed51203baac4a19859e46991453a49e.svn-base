<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
	var tabId = _budgetApplyTabId;
    var tabObj = $("#"+tabId);
    var dialogObj = $("#dialogDgrcompoDbizDiv");
    
    var dialogDgrcompoDbizColNames = ['세부사업', '선택', 'dbizCd'];
    
    var dialogDgrcompoDbizColModel = [
                        {name : 'dbizNm', index : 'dbizNm', width : 400, sortable : false, fixed : true, align : 'left'}
                        , {name:'selBtn', index:'selBtn', width:55, fixed:true,align:'center', formatter:makeBtn}
                        , {name : 'dbizCd', index : 'dbizCd', width : 0, sortable : false, hidden : true}
                        
                    ];
    function makeBtn(cellvalue, options, rowObject){
    	  var str = "";
    	  console.log(rowObject.dbizCd);
    	  console.log(rowObject);
    	  //str += "<a href=JavaScript:clickButton('"+val1+"','"+val2+"');>선택</a>"; 
    	  str += '<a class="btnClass" href="#none" onclick="javascript:selBtnFnc(\'' + rowObject.dbizCd + '\', \'' + rowObject.dbizNm + '\');" >선택</a>';
    	   
    	  return str;
   	}
    
    var dialogDgrcompoDbizGridParam = {
            id : "DIALOG_DGR_COMPO_DBIZ",
            colNames : dialogDgrcompoDbizColNames,
            colModel : dialogDgrcompoDbizColModel,
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
    var dialogDgrcompoDbizGrid = $.csGrid(dialogDgrcompoDbizGridParam);
    
    var dialogDgrcompoDbizClose = function(){
        $("#dialogDgrcompoDbizDiv").dialog("close");
    };
    
    $("#dialogDgrcompoDbizDiv").dialog({
        title: "세부사업선택",
        autoOpen: false,
        width: 'auto',
        height: 'auto',
        modal: true,
        resizable: true,
        open: function(event, ui){
        	doDialogDgrcompoDbizSearch();
        },
        buttons : {
            "닫기" : function() {
            	dialogDgrcompoDbizClose();
            }
        }
    });
    
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
  
    function doDialogDgrcompoDbizSearch(){
    	
    	$.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoDbizSelectDgrcompos.do",
            data : {
                    fisYear: $("#dialogDgrcompoDbizFisYear", dialogObj).val(),
                    bgtDgr: $("#dialogDgrcompoDbizBgtDgr", dialogObj).val(),
                    deptCd: $("#dialogDgrcompoDbizDeptCd", dialogObj).val(),
                    dbizCd: $("#dialogDgrcompoDbizDbizCd", dialogObj).val()
                   },
            async : true,
            callBack : doDialogDgrcompoDbizSearchCallBack
        });
    }
    
    var doDialogDgrcompoDbizSearchCallBack = function(data){
    	if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
    	
    	dialogDgrcompoDbizGrid.addCsJsonData(data);
    }    

});

function selBtnFnc(cd, nm){
	
	
	var dialogDgrcompoDbizCallBackFunction = $("#dialogDgrcompoDbizCallBackFunction", $("#dialogDgrcompoSeperateDiv")).val();
    var num = $('#dialogDgrcompoDbizSepNum').val();
    
    setDbizData(cd, nm, num);
    /* if(isEmpty(dialogDgrcompoDbizCallBackFunction) == false){
        eval(dialogDgrcompoDbizCallBackFunction + "('"+ cd + "', '" + nm + "', " + num + ")");
    } */
}


</script>
<div id="dialogDgrcompoDbizDiv" class="dialog" style="display:none;">
	<input type="hidden" id="dialogDgrcompoDbizCallBackFunction"/>
	<input type="hidden" id="dialogDgrcompoDbizFisYear"/>
	<input type="hidden" id="dialogDgrcompoDbizBgtDgr"/>
	<input type="hidden" id="dialogDgrcompoDbizDeptCd"/>
	<input type="hidden" id="dialogDgrcompoDbizDbizCd"/>
	<input type="hidden" id="dialogDgrcompoDbizSepNum"/>
  <div id="dbizBody">
  	<div id="DIALOG_DGR_COMPO_DBIZ_DIV" class="csGrid">
		<table id="DIALOG_DGR_COMPO_DBIZ_GRD"  style="border:0px;height:100%;"></table>
	</div>
  </div>
</div>
