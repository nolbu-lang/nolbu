<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogDgrModiListDiv");
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        /* if(rowObject.existYn == "N"){
            return ' style="color:#0000FF"';
        } */
        
        return '';
        
    };
    
    var dialogDgrModiListOnCellSelect = function(rowId, iCol){
    	console.log(rowId + '  ' + iCol);
        var rowData = dialogDgrModiListGrid.getRowData(rowId);
        console.log(rowData);
        if(isEmpty(rowData) == true || isEmpty(rowData.teBgtCompoId) == true){
            return;
        }

        
        openModi(rowData.teBgtCompoId);
        //dialogDgrCngHistoryListClose();
    };
    
    var cngTypeFomatter = function(cellValue, options, rowObject){
        
    	if(isEmpty(cellValue) == true){
            cellValue = "";
        }
    	var cngType = rowObject.cngType;
        var txt = '';
        if(cngType == 'CH01'){
        	txt = '병합';
        }else if(cngType == 'CH02'){
        	txt = '분리';
        }
        
        var rVal = txt;

        return rVal;
    };
    
    var btnFomatter = function(cellValue, options, rowObject){
    	
    	if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        var teBgtCompoId = rowObject.teBgtCompoId;
        
        var rVal = '<input type="button" onclick="openLog(\'' + teBgtCompoId + '\');" value="수정" />';

        return rVal;
    };
    
    var dialogDgrModiList = ['개별사업명', '선택', 'teBgtCompoId'];
    var dialogDgrModiListColModel = [
                                     {name : 'compGround', index : 'compGround', width : 350, sortable : false, fixed : true, align : 'left'},
                                     {name : 'teBgtCompoIdBtn', index : 'teBgtCompoId', width : 50, sortable : false, fixed : true, hidden : true, align : 'center', cellattr: myCellattr , formatter:btnFomatter},
                                     {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, fixed : true, hidden : true }
    ];
    
    var dialogDgrModiListGridParam = {
            id : "DIALOG_DGR_CNG_MODI_LIST",
            colNames : dialogDgrModiList,
            colModel : dialogDgrModiListColModel,
            rowNum : 1000,
            defaultRows : 1,
            width : 'auto',
            onCellSelect : function(rowId, iCol, cellcontent, e) {
            	dialogDgrModiListOnCellSelect(rowId, iCol);   
            }
        };
    
    var dialogDgrModiListGrid = $.csGrid(dialogDgrModiListGridParam);
    
    doDialogDgrModiListPageSearch = function(page) {
        doDialogDgrModiListSearch();
    };
    
    var dialogDgrModiListDefaultSearchParam = {
    };
    
    var getDialogDgrModiListSearchParam = function(){
        var searchParam = {
        		cngHistoryId: $("#dialogDgrModiListCngHistoryId", dialogObj).val(),
        		cngType: 'CH01',
                grpLvl: '2'
            };

            $.extend(dialogDgrModiListDefaultSearchParam, searchParam);

            return dialogDgrModiListDefaultSearchParam;
    };
    
    var doDialogDgrModiListSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        dialogDgrModiListGrid.addCsJsonData(data);
    };
    
    var doDialogDgrModiListSearch = function(params) {
    	
        $.csAjaxCall({
            url : "/dialog/ajaxDgrCngModiList.do",
            data : getDialogDgrModiListSearchParam(),
            async : true,
            callBack : doDialogDgrModiListSearchCallBack
        });
    };
    
    var dialogDgrModiListClose = function(){
        $("#dialogDgrModiListCallBackFunction", dialogObj).val("");
        $("#dialogDgrModiListFisYear", dialogObj).val("");
        $("#dialogDgrModiListBgtDgr", dialogObj).val("");
        $("#dialogDgrModiListOfficeCd", dialogObj).val("");
        $("#dialogDgrModiListSeltFg", dialogObj).val("");
        $("#dialogDgrModiListReportCd", dialogObj).val("");
        $("#dialogDgrModiListUserDeptYn", dialogObj).val("");
        
        $("#dialogDgrModiListDiv").dialog('close');
    };
    
    $("#dialogDgrModiListSearchBtn", dialogObj).click(function() {
        doDialogDgrModiListSearch();
    });
    
    $("#dialogDgrModiListCondDeptNm", dialogObj).keypress(function(event){
        if(event.which == 13){
            doDialogDgrModiListSearch();
        }
    });
    
    $("#dialogDgrModiListDiv").dialog({
        title: "개별사업수정",
        autoOpen: false,
        width: 'auto',
        height: 'auto',
        modal: true,
        resizable: false,
        open: function(event, ui){
            doDialogDgrModiListSearch();
        },
        buttons : {
            "닫기" : function() {
                dialogDgrModiListClose();
            }
        }
    });
    
    var dialogDgrModiListDoSaveCallBack = function(param){
        var dialogDgrModiListCallBackFunction = $("#dialogDgrModiListCallBackFunction", $("#dialogDgrcompoModifyMergeDiv")).val();

        dialogDgrModiListClose();
        if(isEmpty(param) == true){
            return;
        }
        
        //param.dgrcompo["dgrcompoId"] = $("#dialogDgrModiListDgrcompoId", $("#dialogDgrcompoModifyMergeDiv")).val();
        
        if(isEmpty(dialogDgrModiListCallBackFunction) == false){
            eval(dialogDgrModiListCallBackFunction + '('+ jsonToString(param.dgrcompo) + ')');
        }
    };
    
    function openModi(teBgtCompoId){
    	$("#dialogDgrcompoModifyMergeCallBackFunction", $("#dialogDgrcompoModifyMergeDiv")).val("budgetModifyDialogDgrcompoRegiCallBackFunction");
        //$("#dialogDgrcompoModifyMergeDgrcompoId", $("#dialogDgrcompoModifyMergeDiv")).val($('#dialogDgrModiListDgrcompoId', $('#dialogDgrModiListDiv')).val());
        $("#dialogDgrcompoModifyMergeCngHistoryId", $("#dialogDgrcompoModifyMergeDiv")).val($('#dialogDgrModiListCngHistoryId', $('#dialogDgrModiListDiv')).val());
        $("#dialogDgrcompoModifyMergeFisYear", $("#dialogDgrcompoModifyMergeDiv")).val($('#dialogDgrModiListFisYear', $('#dialogDgrModiListDiv')).val());
        $("#dialogDgrcompoModifyMergeBgtDgr", $("#dialogDgrcompoModifyMergeDiv")).val($('#dialogDgrModiListBgtDgr', $('#dialogDgrModiListDiv')).val());
        $("#dialogDgrcompoModifyMergeTeBgtCompoId", $("#dialogDgrcompoModifyMergeDiv")).val(teBgtCompoId);
        //$("#dialogDgrcompoModifyMergeIsLeaf", $("#dialogDgrcompoModifyMergeDiv")).val($('#dialogDgrModiListIsLeaf', $('#dialogDgrModiListDiv')).val());
        $("#dialogDgrcompoModifyMergeAmtUnit", $("#dialogDgrcompoModifyMergeDiv")).val($('#dialogDgrModiListAmtUnit', $('#dialogDgrModiListDiv')).val());
        
        //$("#dialogDgrcompoModiDgrcompoNm", $("#dialogDgrcompoModifyMergeDiv")).val($('#dialogDgrModiListDgrcompoNm', $('#dialogDgrModiListDiv')).val());
        //$("#dialogDgrcompoModiTeMngMokCdNm", $("#dialogDgrcompoModifyMergeDiv")).val($('#dialogDgrModiListTeMngMokCdNm', $('#dialogDgrModiListDiv')).val());
        //$("#dialogDgrcompoModiTeMngMokNm", $("#dialogDgrcompoModifyMergeDiv")).val($('#dialogDgrModiListTeMngMokNm', $('#dialogDgrModiListDiv')).val());
        
        $("#dialogDgrcompoModifyMergeDiv").dialog('open');
    }
   
});



</script>
<div id="dialogDgrModiListDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgrModiListCallBackFunction"/>
  <input type="hidden" id="dialogDgrModiListCngHistoryId"/>
  <input type="hidden" id="dialogDgrModiListFisYear"/>
  <input type="hidden" id="dialogDgrModiListBgtDgr"/>
  <input type="hidden" id="dialogDgrModiListOfficeCd"/>
  <input type="hidden" id="dialogDgrModiListSeltFg"/>
  <input type="hidden" id="dialogDgrModiListReportCd"/>
  <input type="hidden" id="dialogDgrModiListUserDeptYn"/>
  <input type="hidden" id="dialogDgrModiListDgrcompoId"/>
  <input type="hidden" id="dialogDgrModiListTeBgtCompoId"/>
  <input type="hidden" id="dialogDgrModiListIsLeaf"/>
  <input type="hidden" id="dialogDgrModiListAmtUnit"/>
  <input type="hidden" id="dialogDgrModiListDgrcompoNm"/>
  <input type="hidden" id="dialogDgrModiListTeMngMokCdNm"/>
  <input type="hidden" id="dialogDgrModiListTeMngMokNm"/>
  <div class="ui-widget-header">
   	<span id="parentLogTitle">수정할 개별사업을 선택해주세요.</span>
  </div>
  <div id="DIALOG_DGR_CNG_MODI_LIST_DIV" class="csGrid">
    <table id="DIALOG_DGR_CNG_MODI_LIST_GRD" ></table>
  </div>
  <!--page s-->
  <div id="DIALOG_DGR_CNG_MODI_LIST_PGR" class="paging">
  </div>
  <!--page e-->
</div>