<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogBgtFrscAmtColNames = ['구분코드', '재원명', '기정액', '요구액', '예산액', 'fisYear', 'bgtDgr', 'frscFgCd', 'teBgtCompoSeq'];
    var dialogBgtFrscAmtColModel = [
                                    {name : 'frscFgCd', index : 'govOfficeNm', width : 50, sortable : false, fixed : true, align : 'center' },
                                    {name : 'frscFgNm', index : 'deptNm', width : 130, sortable : false, fixed : true, align : 'left'},
                                    {name : 'preDefFrscAmt', index : 'preDefFrscAmt', width : 100, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                    {name : 'demandFrscAmt', index : 'demandFrscAmt', width : 100, sortable : false, fixed : true, hidden : true, align : 'right',
                                        editable : true, edittype:'text', 
                                        editoptions:{dataInit: function (elem) {$(elem).autoNumeric({aPad: false, vMax:'99999999999999999'});}},
                                        formatter : function(c) {return addCommaStr(c); }
                                    },
                                    {name : 'adjDefFrscAmt', index : 'adjDefFrscAmt', width : 100, sortable : false, fixed : true, hidden : true, align : 'right',
                                        editable : true, edittype:'text', 
                                        editoptions:{dataInit: function (elem) {$(elem).autoNumeric({aPad: false, vMax:'99999999999999999'});}}, 
                                        formatter : function(c) {return addCommaStr(c); }
                                    },
                                    {name : 'fisYear', index : 'locGovCd', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'bgtDgr', index : 'govOfficeCd', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'frscFgCd', index : 'frscFgCd', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, fixed : true, hidden : true }
    ];
    
    var dialogBgtFrscAmtGridParam = {
            id : "DIALOG_BGT_FRSC_AMT",
            colNames : dialogBgtFrscAmtColNames,
            colModel : dialogBgtFrscAmtColModel,
            cellEdit: true,
            cellsubmit : 'clientArray',
            rowNum : 10,
            defaultRows: 15,
            width : 420,
            height : 350
        };
    
    var dialogBgtFrscAmtGrid = $.csGrid(dialogBgtFrscAmtGridParam);
    
    var doDialogBgtFrscAmtInit = function(){
        $("#dialogBgtFrscAmtFisYear", $("#dialogBgtFrscAmtDiv")).val("");
        $("#dialogBgtFrscAmtBgtDgr", $("#dialogBgtFrscAmtDiv")).val("");
        $("#dialogBgtFrscAmtTeBgtCompoSeq", $("#dialogBgtFrscAmtDiv")).val("");
        $("#dialogBgtFrscAmtFg", $("#dialogBgtFrscAmtDiv")).val("");
    };

    var doDialogBgtFrscAmtSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        if($("#dialogBgtFrscAmtFg", $("#dialogBgtFrscAmtDiv")).val() == "1"){
            dialogBgtFrscAmtGrid.showCol("demandFrscAmt");
            dialogBgtFrscAmtGrid.hideCol("adjDefFrscAmt");
        }else if($("#dialogBgtFrscAmtFg", $("#dialogBgtFrscAmtDiv")).val() == "2"){
            dialogBgtFrscAmtGrid.hideCol("demandFrscAmt");
            dialogBgtFrscAmtGrid.showCol("adjDefFrscAmt");
        }else{
            dialogBgtFrscAmtGrid.hideCol("demandFrscAmt");
            dialogBgtFrscAmtGrid.hideCol("adjDefFrscAmt");
        }
        
        dialogBgtFrscAmtGrid.addCsJsonData(data);
    };
    
    var doDialogBgtFrscAmtSearch = function() {
        $.csAjaxCall({
            url : "/dialog/selectDgrfrscCompoSeqList.do",
            data : {
                    fisYear: $("#dialogBgtFrscAmtFisYear", $("#dialogBgtFrscAmtDiv")).val(),
                    bgtDgr: $("#dialogBgtFrscAmtBgtDgr", $("#dialogBgtFrscAmtDiv")).val(),
                    teBgtCompoSeq: $("#dialogBgtFrscAmtTeBgtCompoSeq", $("#dialogBgtFrscAmtDiv")).val()
                   },
            async : true,
            callBack : doDialogBgtFrscAmtSearchCallBack
        });
    };
    
    $("#dialogBgtFrscAmtDiv").dialog({
        title: "예산액수정",
        autoOpen: false,
        width: 448,
        height: 490,
        modal: true,
        resizable: false,
        open: function(event, ui){
            doDialogBgtFrscAmtSearch();
        },
        buttons : {
            "저장" : function() {
                $(this).dialog("close");
                
                if($.isFunction(eval('dialogBgtFrscAmtCallBack'))){
                    eval('dialogBgtFrscAmtCallBack("1")');
                }
            },
            "닫기" : function() {
                $(this).dialog("close");
                doDialogBgtFrscAmtInit();
            }
        }
    });
    
    $("#dialogBgtFrscAmtCloseBtn", $("#dialogBgtFrscAmtDiv")).click(function(){
        $("#dialogBgtFrscAmtDiv").dialog('close');
    });

});
</script>
<div id="dialogBgtFrscAmtDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogBgtFrscAmtFisYear"/>
  <input type="hidden" id="dialogBgtFrscAmtBgtDgr"/>
  <input type="hidden" id="dialogBgtFrscAmtTeBgtCompoSeq"/>
  <input type="hidden" id="dialogBgtFrscAmtFg"/>
  <div id="DIALOG_BGT_FRSC_AMT_DIV" class="csGrid">
    <table id="DIALOG_BGT_FRSC_AMT_GRD" ></table>
  </div>
</div>