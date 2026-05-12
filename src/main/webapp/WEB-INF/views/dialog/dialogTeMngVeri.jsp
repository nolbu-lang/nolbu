<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogTeMngVeriDiv");

    dialogTeMngMokVeriSelYn = function(veriClCd, veriDetlCd){
        if($("#dialogTeMngVeriVeriFg", dialogObj).val() != "010"){
            return;
        }
        
        var checkYn = $('#selYn_'+veriClCd+'_'+veriDetlCd, dialogObj).is(':checked') == true ? "Y" : "N";
        if(checkYn != "Y"){
            return;
        }
        
        var tId = "";
        $('#dialogTeMngVeriDiv input:checked').each(function() {
            tId = $(this).attr('id');
            if(tId != 'selYn_'+veriClCd+'_'+veriDetlCd){
                $(this).removeAttr('checked');
            }
        });
    };
    
    var selYnFormatter = function(cellValue, options, rowObject){

        var rVal = '<div>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="selYn_'+rowObject.veriClCd+'_'+rowObject.veriDetlCd+'" value="Y" class="chkBudgetSelect" onclick="javascript:dialogTeMngMokVeriSelYn(\''+rowObject.veriClCd+'\', \''+rowObject.veriDetlCd+'\');" style="margin-top: 5px;" '+(rowObject.selYn == 'Y' ? 'checked' : '')+' />'
                 + '</div>';

        return rVal;
    };
    
    var dialogTeMngVeriColNames = ['', '구분', '조서/집계명', '조서/집계상세명', 'veriClCd', 'veriDetlCd'];
    var dialogTeMngVeriColModel = [
                                    {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, 
                                        formatter : selYnFormatter
                                    },
                                    {name : 'fgNm', index : 'fgNm', width : 50, sortable : false, fixed : true, align : 'center' },
                                    {name : 'clCdNm', index : 'clCdNm', width : 150, sortable : false, fixed : true, align : 'left'},
                                    {name : 'detlCdNm', index : 'detlCdNm', width : 200, sortable : false, fixed : true, align : 'left'},
                                    {name : 'veriClCd', index : 'veriClCd', width : 0, sortable : false, hidden : true},
                                    {name : 'veriDetlCd', index : 'veriDetlCd', width : 0, sortable : false, hidden : true}
    ];
    
    var dialogTeMngVeriGridParam = {
            id : "DIALOG_TEMNG_VERI",
            colNames : dialogTeMngVeriColNames,
            colModel : dialogTeMngVeriColModel,
            rowNum : 1000,
            width : 500,
            height : 450,
            defaultRows: 0
    };
    
    var dialogTeMngVeriGrid = $.csGrid(dialogTeMngVeriGridParam);
    
    var doDialogTeMngVeriSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        dialogTeMngVeriGrid.addCsJsonData(data);
    };
    
    var doDialogTeMngVeriSearch = function() {
        $.csAjaxCall({
            url : "/dialog/ajaxTeMngVeriList.do",
            data : {
                    fisYear: $("#dialogTeMngVeriFisYear", dialogObj).val(),
                    bgtDgr: $("#dialogTeMngVeriBgtDgr", dialogObj).val(),
                    teMngMokCd: $("#dialogTeMngVeriTeMngMokCd", dialogObj).val(),
                    veriFg: $("#dialogTeMngVeriVeriFg", dialogObj).val()
                   },
            async : true,
            callBack : doDialogTeMngVeriSearchCallBack
        });
    };
    
    var dialogTeMngVeriDoSaveCallBack = function(param){
        var dialogTeMngVeriCallBackFunction = $("#dialogTeMngVeriCallBackFunction", dialogObj).val();
        if(isEmpty(dialogTeMngVeriCallBackFunction) == false){
            eval(dialogTeMngVeriCallBackFunction+ '('+ jsonToString(param.dgrcompo) + ')');
        }
    };
    
    var dialogTeMngVeriDoSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var rowList = dialogTeMngVeriGrid.getRowData();

        var saveDatas = [];
        var saveData = {};
        for(var i = 0; i < rowList.length; i++){            
            if($('#selYn_'+rowList[i].veriClCd+'_'+rowList[i].veriDetlCd, dialogObj).is(':checked') == true){
                saveData = {};
                saveData["veriClCd"] = rowList[i].veriClCd;
                saveData["veriDetlCd"] = rowList[i].veriDetlCd;
                
                saveDatas.push(saveData);
            }
        }
        
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogTeMngVeriInsertTeMngVeri.do",
            data : {
                fisYear : $("#dialogTeMngVeriFisYear", dialogObj).val(),
                bgtDgr : $("#dialogTeMngVeriBgtDgr", dialogObj).val(),
                teMngMokCd : $("#dialogTeMngVeriTeMngMokCd", dialogObj).val(),
                veriFg : $("#dialogTeMngVeriVeriFg", dialogObj).val(),
                saveDatas: saveDatas
            }
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogTeMngVeriClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                dialogTeMngVeriDoSaveCallBack(data);

                dialogTeMngVeriClose();
            }
        });
    };
    
    var dialogTeMngVeriSaveBtnClick = function(){
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : dialogTeMngVeriDoSave
        });
    };
    
    var dialogTeMngVeriClose = function(){

        $("#dialogTeMngVeriCallBackFunction", dialogObj).val("");
        $("#dialogTeMngVeriFisYear", dialogObj).val("");
        $("#dialogTeMngVeriBgtDgr", dialogObj).val("");
        $("#dialogTeMngVeriTeMngMokCd",dialogObj).val("");
        $("#dialogTeMngVeriVeriFg",dialogObj).val("");
        
        $("#dialogTeMngVeriDiv").dialog("close");
    };
    
    $("#dialogTeMngVeriDiv").dialog({
        title: "검증통계목등록",
        autoOpen: false,
        width: 530,
        height: 600,
        modal: true,
        resizable: false,
        open: function(event, ui){
            doDialogTeMngVeriSearch();
        },
        buttons : {
            "저장" : function() {
                dialogTeMngVeriSaveBtnClick();
            },
            "닫기" : function() {
                dialogTeMngVeriClose();
            }
        }
    });
    
});
</script>
<div id="dialogTeMngVeriDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogTeMngVeriCallBackFunction"/>
  <input type="hidden" id="dialogTeMngVeriFisYear"/>
  <input type="hidden" id="dialogTeMngVeriBgtDgr"/>
  <input type="hidden" id="dialogTeMngVeriTeMngMokCd"/>
  <input type="hidden" id="dialogTeMngVeriVeriFg"/>
  <div id="DIALOG_TEMNG_VERI_DIV" class="csGrid">
    <table id="DIALOG_TEMNG_VERI_GRD" ></table>
  </div>
</div>