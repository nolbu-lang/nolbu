<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogDgrDeptSeltDiv");
    
    var dialogDgrDeptSeltOnCellSelect = function(rowId, iCol){
        var rowData = dialogDgrDeptSeltGrid.getRowData(rowId);
        if(isEmpty(rowData) == true || isEmpty(rowData.deptCd) == true){
            return;
        }

        var dialogDgrDeptSeltCallBackFunction = $("#dialogDgrDeptSeltCallBackFunction", dialogObj).val();
        if(isEmpty(dialogDgrDeptSeltCallBackFunction) == false){
            var param = {
                    officeCd : rowData.officeCd,
                    officeNm : rowData.officeNm,
                    deptCd : rowData.deptCd,
                    deptNm : rowData.deptNm,
                    deptRank : rowData.deptRank
            };
            
            eval(dialogDgrDeptSeltCallBackFunction + '('+ jsonToString(param) + ')');
        }

        dialogDgrDeptSeltClose();
    };
    
    var dialogDgrDeptSelt = ['번호', '실국', '부서명', 'officeCd', 'deptCd', 'deptRank'];
    var dialogDgrDeptSeltColModel = [
                                     {name : 'rowNum', index : 'rowNum', width : 50, sortable : false, fixed : true, align : 'center'},
                                     {name : 'officeNm', index : 'officeNm', width : 140, sortable : false, fixed : true, align : 'center' },
                                     {name : 'deptNm', index : 'deptNm', width : 200, sortable : false, fixed : true, align : 'left'},
                                     {name : 'officeCd', index : 'officeCd', width : 0, sortable : false, fixed : true, hidden : true },
                                     {name : 'deptCd', index : 'deptCd', width : 0, sortable : false, fixed : true, hidden : true },
                                     {name : 'deptRank', index : 'deptRank', width : 0, sortable : false, fixed : true, hidden : true }
    ];
    
    var dialogDgrDeptSeltGridParam = {
            id : "DIALOG_DGR_DEPT_SELT",
            colNames : dialogDgrDeptSelt,
            colModel : dialogDgrDeptSeltColModel,
            rowNum : 10,
            width : 420,
            onCellSelect : function(rowId, iCol, cellcontent, e) {
                dialogDgrDeptSeltOnCellSelect(rowId, iCol);
            }
        };
    
    var dialogDgrDeptSeltGrid = $.csGrid(dialogDgrDeptSeltGridParam);
    
    doDialogDgrDeptSeltPageSearch = function(page) {
        doDialogDgrDeptSeltSearch({
            page : page
        });
    };
    
    var dialogDgrDeptSeltDefaultSearchParam = {
            page : 1,
            rowNum : 10
    };
    
    var getDialogDgrDeptSeltSearchParam = function(params){
        var searchParam = {
                fisYear: $("#dialogDgrDeptSeltFisYear", dialogObj).val(),
                bgtDgr: $("#dialogDgrDeptSeltBgtDgr", dialogObj).val(),
                officeCd: $("#dialogDgrDeptSeltOfficeCd", dialogObj).val(),
                deptNm: $("#dialogDgrDeptSeltCondDeptNm", dialogObj).val(),
                reportCd: $("#dialogDgrDeptSeltReportCd", dialogObj).val(),
                userDeptYn: $("#dialogDgrDeptSeltUserDeptYn", dialogObj).val()
            };

            $.extend(dialogDgrDeptSeltDefaultSearchParam, searchParam);
            $.extend(dialogDgrDeptSeltDefaultSearchParam, params);

            return dialogDgrDeptSeltDefaultSearchParam;
    };
    
    var doDialogDgrDeptSeltSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        dialogDgrDeptSeltGrid.addCsJsonData(data);
        $("#DIALOG_DGR_DEPT_SELT_PGR").addPagingData(data, "doDialogDgrDeptSeltPageSearch");
        $("#DIALOG_DGR_DEPT_SELT_TOT").html("총건수 : " + addCommaStr(data.totalCount) + "건");
    };
    
    var doDialogDgrDeptSeltSearch = function(params) {
        $.csAjaxCall({
            url : "/dialog/ajaxDgrDeptSeltList.do",
            data : getDialogDgrDeptSeltSearchParam(params),
            async : true,
            callBack : doDialogDgrDeptSeltSearchCallBack
        });
    };
    
    var dialogDgrDeptSeltClose = function(){
        $("#dialogDgrDeptSeltCallBackFunction", dialogObj).val("");
        $("#dialogDgrDeptSeltFisYear", dialogObj).val("");
        $("#dialogDgrDeptSeltBgtDgr", dialogObj).val("");
        $("#dialogDgrDeptSeltOfficeCd", dialogObj).val("");
        $("#dialogDgrDeptSeltSeltFg", dialogObj).val("");
        $("#dialogDgrDeptSeltReportCd", dialogObj).val("");
        $("#dialogDgrDeptSeltUserDeptYn", dialogObj).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('close');
    };
    
    $("#dialogDgrDeptSeltSearchBtn", dialogObj).click(function() {
        doDialogDgrDeptSeltSearch({
            page : 1,
            rowNum : 10
        });
    });
    
    $("#dialogDgrDeptSeltCondDeptNm", dialogObj).keypress(function(event){
        if(event.which == 13){
            doDialogDgrDeptSeltSearch({
                page : 1,
                rowNum : 10
            });
        }
    });
    
    $("#dialogDgrDeptSeltDiv").dialog({
        title: "부서선택",
        autoOpen: false,
        width: 448,
        height: 510,
        modal: true,
        resizable: false,
        open: function(event, ui){
            doDialogDgrDeptSeltSearch({
                page : 1,
                rowNum : 10
            });
        },
        buttons : {
            "닫기" : function() {
                dialogDgrDeptSeltClose();
            }
        }
    });
   
});
</script>
<div id="dialogDgrDeptSeltDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgrDeptSeltCallBackFunction"/>
  <input type="hidden" id="dialogDgrDeptSeltFisYear"/>
  <input type="hidden" id="dialogDgrDeptSeltBgtDgr"/>
  <input type="hidden" id="dialogDgrDeptSeltOfficeCd"/>
  <input type="hidden" id="dialogDgrDeptSeltSeltFg"/>
  <input type="hidden" id="dialogDgrDeptSeltReportCd"/>
  <input type="hidden" id="dialogDgrDeptSeltUserDeptYn"/>
  <div class="condition">
    <table>
      <colgroup>
        <col width="100px"/>
        <col width="200px"/>
      </colgroup>
      <tbody>
        <tr>
          <th>부서</th>
          <td>
            <input type="text" id="dialogDgrDeptSeltCondDeptNm" style="width:95%;"/>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  <div class="btn">
    <div class="btnR">
      <a id="dialogDgrDeptSeltSearchBtn" href="#"><img src="<c:url value='/images/btn/btn_inquiry.gif'/>" alt="조회"/></a>
    </div>
  </div>
  <div class="gridHeader">
    <span id="DIALOG_DGR_DEPT_SELT_TOT">총건수 : 0건</span>
  </div>
  <div id="DIALOG_DGR_DEPT_SELT_DIV" class="csGrid">
    <table id="DIALOG_DGR_DEPT_SELT_GRD" ></table>
  </div>
  <!--page s-->
  <div id="DIALOG_DGR_DEPT_SELT_PGR" class="paging">
  </div>
  <!--page e-->
</div>