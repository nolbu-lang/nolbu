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
        		fisYear : $("#dialogDgrModiListFisYear", dialogObj).val(),
        		bgtDgr : $("#dialogDgrModiListBgtDgr", dialogObj).val(),
        		teBgtCompoId : $("#dialogDgrModiListTeBgtCompoId", dialogObj).val(),
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
        
        $("#dialogDgrModiListCompGround", dialogObj).val(data.data.compGround);
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
            "사업명수정" : function() {
                doCompoGroundSave();
            },
            "닫기" : function() {
                dialogDgrModiListClose();
            }
        }
    });
    
  //저장 실행
    var doCompoGroundSave = function(){
        
    	$.csConfirm({
            msg : "사업명을 수정하시겠습니까?",
            callBack : doCompoGroundDoSave
        });
    	
    };
    var doCompoGroundDoSave = function(params){
    	var fisYear = $("#dialogDgrModiListFisYear", dialogObj).val();
        var bgtDgr = $("#dialogDgrModiListBgtDgr", dialogObj).val();
        var bgtCompoId = $("#dialogDgrModiListDgrcompoId", dialogObj).val();
        var teBgtCompoId = $("#dialogDgrModiListTeBgtCompoId", dialogObj).val();
        var compGround = $("#dialogDgrModiListCompGround", dialogObj).val();

        var param = {
                fisYear: fisYear,
                bgtDgr: bgtDgr,
                bgtCompoId: bgtCompoId,
                teBgtCompoId: teBgtCompoId,
                compGround: compGround
        };
        
        if(!fisYear){
        	alert('회계년도 정보가 없습니다.');
        	return;
        }
        if(!bgtDgr){
        	alert('예산차수 정보가 없습니다.');
        	return;
        }
        if(!bgtCompoId){
        	alert('사업 정보가 없습니다.');
        	return;
        }
        
       var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoModifySaveDgrcompoGround.do",
            data : param,
        });
       
       	if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
           $.csAlert({
               msg : data.bcjisMessage,
               callBack : function() {
            	   dialogDgrModiListClose();
               }
           });
           
           return;
       }
       
       $.csAlert({
           msg : "수정되었습니다.<BR>(상위 항목의 정보는 재 조회 후 확인하실 수 있습니다.)",
           callBack : function() {
               dialogDgrcompoModifyDoSaveCallBack(data);
               
               dialogDgrModiListClose();
           }
       });
    	
    };
    
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
    
    var dialogDgrcompoModifyDoSaveCallBack = function(param){
        var dialogDgrModiCompoGroundCallBackFunction = $("#dialogDgrModiCompoGroundCallBackFunction", dialogObj).val();

        if(isEmpty(param) == true){
            return;
        }
        
        if(isEmpty(dialogDgrModiCompoGroundCallBackFunction) == false){
        	eval(dialogDgrModiCompoGroundCallBackFunction + '('+ jsonToString(param) + ')');
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
  <input type="hidden" id="dialogDgrModiCompoGroundCallBackFunction"/>
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
  
  <div class="viewDiv" style="width:348px;">
    <table>
      <colgroup>
        <col width="75px"/>
        <col width="*"/>
      </colgroup>
      <tbody>
        <tr>
          <th colspan="2">사업명</th>
          <td colspan="5">
            <input type="text" id="dialogDgrModiListCompGround" style="width:100%;"/>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>