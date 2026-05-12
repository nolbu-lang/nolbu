<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogDgrCngHistoryListDiv");
    
    var dialogDgrCngHistoryListOnCellSelect = function(rowId, iCol){
        var rowData = dialogDgrCngHistoryListGrid.getRowData(rowId);
        if(isEmpty(rowData) == true || isEmpty(rowData.deptCd) == true){
            return;
        }

        var dialogDgrCngHistoryListCallBackFunction = $("#dialogDgrCngHistoryListCallBackFunction", dialogObj).val();
        if(isEmpty(dialogDgrCngHistoryListCallBackFunction) == false){
            var param = {
                    officeCd : rowData.officeCd,
                    officeNm : rowData.officeNm,
                    deptCd : rowData.deptCd,
                    deptNm : rowData.deptNm,
                    deptRank : rowData.deptRank
            };
            
            eval(dialogDgrCngHistoryListCallBackFunction + '('+ jsonToString(param) + ')');
        }

        dialogDgrCngHistoryListClose();
    };
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        /* if(rowObject.existYn == "N"){
            return ' style="color:#0000FF"';
        } */
        
        return '';
        
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
        }else if(cngType == 'CH03'){
        	txt = '병합해제';
        }
        
        var rVal = txt;

        return rVal;
    };
    
    var btnFomatter = function(cellValue, options, rowObject){
    	
    	if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        var cngType = rowObject.cngType;
        var cngHistoryId = rowObject.cngHistoryId;
        var regiDate = rowObject.regiDate;
        var title = rowObject.title;
        
        var rVal = '<input type="button" onclick="openLog(\'' + cngType + '\', \'' + cngHistoryId + '\', \'' + regiDate + '\', \'' + title + '\');" value="보기" />';

        return rVal;
    };
    
    var dialogDgrCngHistoryList = ['번호', '구분', '일자', '회계-실-부서-세부', '개별사업', '이력', 'cngHistoryId', 'cngType'];
    var dialogDgrCngHistoryListColModel = [
                                     {name : 'rowNum', index : 'rowNum', width : 40, sortable : false, fixed : true, align : 'center'},
                                     {name : 'cngTypeNm', index : 'cngType', width : 50, sortable : false, fixed : true, align : 'center', formatter: cngTypeFomatter},
                                     {name : 'regiDate', index : 'regiDate', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr },
                                     {name : 'title', index : 'title', width : 360, sortable : false, fixed : true, align : 'left', cellattr: myCellattr },
                                     {name : 'indiBns', index : 'indiBns', width : 120, sortable : false, fixed : true, align : 'left', cellattr: myCellattr },
                                     {name : 'cngHistoryIdBtn', index : 'cngHistoryId', width : 50, sortable : false, fixed : true, align : 'center', cellattr: myCellattr , formatter:btnFomatter},
                                     {name : 'cngHistoryId', index : 'cngHistoryId', width : 0, sortable : false, fixed : true, hidden : true },
                                     {name : 'cngType', index : 'cngType', width : 0, sortable : false, fixed : true, hidden : true }
    ];
    
    var dialogDgrCngHistoryListGridParam = {
            id : "DIALOG_DGR_CNG_HISTORY_LIST",
            colNames : dialogDgrCngHistoryList,
            colModel : dialogDgrCngHistoryListColModel,
            rowNum : 10,
            width : 'auto',
            caption: '<span id="DIALOG_DGR_CNG_HISTORY_LIST_TOT">총건수 : 0건</span>',
            onCellSelect : function(rowId, iCol, cellcontent, e) {
                dialogDgrCngHistoryListOnCellSelect(rowId, iCol);
            }
        };
    
    var dialogDgrCngHistoryListGrid = $.csGrid(dialogDgrCngHistoryListGridParam);
    
    doDialogDgrCngHistoryListPageSearch = function(page) {
        doDialogDgrCngHistoryListSearch({
            page : page
        });
    };
    
    var dialogDgrCngHistoryListDefaultSearchParam = {
            page : 1,
            rowNum : 10
    };
    
    var getDialogDgrCngHistoryListSearchParam = function(params){
        var searchParam = {
                cngType: $("#condCngType", dialogObj).val(),
                regiStartDate: $("#condRegiStartDate", dialogObj).val(),
                regiEndDate: $("#condRegiEndDate", dialogObj).val(),
                indiBns: $("#condIndiBns", dialogObj).val()
            };

            $.extend(dialogDgrCngHistoryListDefaultSearchParam, searchParam);
            $.extend(dialogDgrCngHistoryListDefaultSearchParam, params);

            return dialogDgrCngHistoryListDefaultSearchParam;
    };
    
    var doDialogDgrCngHistoryListSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        dialogDgrCngHistoryListGrid.addCsJsonData(data);
        $("#DIALOG_DGR_CNG_HISTORY_LIST_PGR").addPagingData(data, "doDialogDgrCngHistoryListPageSearch");
        $("#DIALOG_DGR_CNG_HISTORY_LIST_TOT").html("총건수 : " + addCommaStr(data.totalCount) + "건");
    };
    
    var doDialogDgrCngHistoryListSearch = function(params) {
    	
    	var regiStartDate = $("#condRegiStartDate", dialogObj).val();
    	var regiEndDate = $("#condRegiEndDate", dialogObj).val();
    	
    	if(isEmpty(regiStartDate) != true){
    		if(isEmpty(regiEndDate) == true){
    			$.csAlert({
                    msg : '종료일자를 선택해주세요.'
                });
    			return false;
    		}
    	}
    	
    	if(isEmpty(regiEndDate) != true){
    		if(isEmpty(regiStartDate) == true){
    			$.csAlert({
                    msg : '시작일자를 선택해주세요.'
                });
    			return false;
    		}
    	}
    	
    	if(isEmpty(regiStartDate) != true){
    		if(isEmpty(regiEndDate) != true){
    			var regiStartDateInt = parseInt(regiStartDate.replace(/-/gi, ''));
    			var regiEndDateInt = parseInt(regiEndDate.replace(/-/gi, ''));
    			
    			if(regiStartDateInt > regiEndDateInt){
    				$.csAlert({
                        msg : '시작일자가 종료일자보다 클수 없습니다.'
                    });
    				
    				return false;
    			}
    			
    		}
    	}
    	
    	
    	
        $.csAjaxCall({
            url : "/dialog/ajaxDgrCngHistoryListList.do",
            data : getDialogDgrCngHistoryListSearchParam(params),
            async : true,
            callBack : doDialogDgrCngHistoryListSearchCallBack
        });
    };
    
    var dialogDgrCngHistoryListClose = function(){
        $("#dialogDgrCngHistoryListCallBackFunction", dialogObj).val("");
        $("#dialogDgrCngHistoryListFisYear", dialogObj).val("");
        $("#dialogDgrCngHistoryListBgtDgr", dialogObj).val("");
        $("#dialogDgrCngHistoryListOfficeCd", dialogObj).val("");
        $("#dialogDgrCngHistoryListSeltFg", dialogObj).val("");
        $("#dialogDgrCngHistoryListReportCd", dialogObj).val("");
        $("#dialogDgrCngHistoryListUserDeptYn", dialogObj).val("");
        $("#condCngType", dialogObj).val("CH01");
        $("#condRegiStartDate", dialogObj).val("");
        $("#condRegiEndDate", dialogObj).val("");
        $("#condIndiBns", dialogObj).val("");
        
        $("#dialogDgrCngHistoryListDiv").dialog('close');
    };
    
    $("#dialogDgrCngHistoryListSearchBtn", dialogObj).click(function() {
        doDialogDgrCngHistoryListSearch({
            page : 1,
            rowNum : 10
        });
    });
    
    $("#dialogDgrCngHistoryListCondDeptNm", dialogObj).keypress(function(event){
        if(event.which == 13){
            doDialogDgrCngHistoryListSearch({
                page : 1,
                rowNum : 10
            });
        }
    });
    
    $("#dialogDgrCngHistoryListDiv").dialog({
        title: "병합/분리 이력관리",
        autoOpen: false,
        width: 'auto',
        height: 'auto',
        modal: true,
        resizable: false,
        open: function(event, ui){
            doDialogDgrCngHistoryListSearch({
                page : 1,
                rowNum : 10
            });
        },
        buttons : {
            "닫기" : function() {
                dialogDgrCngHistoryListClose();
            }
        }
    });
   
    $( "#condRegiStartDate" ).datepicker({
		inline: true,
		dateFormat: 'yy-mm-dd'
	});
    $( "#condRegiEndDate" ).datepicker({
		inline: true, 
		dateFormat: 'yy-mm-dd'
	});
    
    $('#dialogDgrCngHistoryListDiv').on('dialogclose', function(event) {
    	dialogDgrCngHistoryListClose();
    });
});

function openLog(cngType, cngHistoryId, regiDate, title){
	var cngTypeNm = '';
	if(cngType == 'CH01'){
		cngTypeNm = '병합';
	}else if(cngType == 'CH02'){
		cngTypeNm = '분리';
	}else if(cngType == 'CH03'){
		cngTypeNm = '병합해제';
	}else{
		cngTypeNm = '';
	}
	
	$("#dialogDgrcompoCngLogCngType", $('#dialogDgrcompoCngLogDiv')).val(cngType);
	$("#dialogDgrcompoCngLogCngHistoryId", $('#dialogDgrcompoCngLogDiv')).val(cngHistoryId);
	$("#dialogDgrcompoCngLogCngTypeNm", $('#dialogDgrcompoCngLogDiv')).html(cngTypeNm);
	$("#dialogDgrcompoCngLogRegiDate", $('#dialogDgrcompoCngLogDiv')).html(regiDate);
	$("#dialogDgrcompoCngLogTitle", $('#dialogDgrcompoCngLogDiv')).html(title);
	$("#dialogDgrcompoCngLogAmtUnit", $('#dialogDgrcompoCngLogDiv')).val($('#dialogDgrCngHistoryListAmtUnit', $('#dialogDgrCngHistoryListDiv')).val());
    $("#dialogDgrcompoCngLogDiv").dialog('open');
}
</script>
<div id="dialogDgrCngHistoryListDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgrCngHistoryListCallBackFunction"/>
  <input type="hidden" id="dialogDgrCngHistoryListFisYear"/>
  <input type="hidden" id="dialogDgrCngHistoryListBgtDgr"/>
  <input type="hidden" id="dialogDgrCngHistoryListOfficeCd"/>
  <input type="hidden" id="dialogDgrCngHistoryListSeltFg"/>
  <input type="hidden" id="dialogDgrCngHistoryListReportCd"/>
  <input type="hidden" id="dialogDgrCngHistoryListUserDeptYn"/>
  <input type="hidden" id="dialogDgrCngHistoryListAmtUnit" value="1" />
  <div class="condition" style="margin-bottom:10px;">
    <table>
      <colgroup>
        <col width="30px"/>
        <col width="150px"/>
        <col width="30px"/>
        <col width="250px"/>
        <col width="50px"/>
        <col width="*"/>
        <col width="53px"/>
      </colgroup>
      <tbody>
        <tr>
        	
          <th>구분</th>
          <td>
            <select id="condCngType" name="condCngType" title="재원구분" style="width:100px;">
            	<option value="CH01">병합</option>
            	<option value="CH02">분리</option>
            	<option value="CH03">병합해제</option>
            </select>
          </td>
          <th>일자</th>
          <td>
          	<input type="text" id="condRegiStartDate" class="readonly" style="width:80px;" readonly/>
          	~
          	<input type="text" id="condRegiEndDate" class="readonly" style="width:80px;" readonly/>
          </td>
          <th>개별사업</th>
          <td>
            <input type="text" id="condIndiBns" style="width:95%;"/>
          </td>
          <td>
          	<a id="dialogDgrCngHistoryListSearchBtn" href="#"><img src="<c:url value='/images/btn/btn_inquiry.gif'/>" alt="조회"/></a>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  <div id="DIALOG_DGR_CNG_HISTORY_LIST_DIV" class="csGrid">
    <table id="DIALOG_DGR_CNG_HISTORY_LIST_GRD" ></table>
  </div>
  <!--page s-->
  <div id="DIALOG_DGR_CNG_HISTORY_LIST_PGR" class="paging">
  </div>
  <!--page e-->
</div>