<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
    $(document).ready(function() {
        var dialogObj = $("#dialogManageCloseHisDiv");
        
        var fileSnFormatter = function(cellValue, options, rowObject){
            if(isEmpty(cellValue) == true){
                return "";
            }
            
            return rVal = '<a href="javascript:attchFileDownload(\''+rowObject.atchFileId+'\', \''+cellValue+'\');"><img src="'+ctx+'/images/icon/excel.png" height="16" width="16"></a>';
        };
        
        var fileSnAllFormatter = function(cellValue, options, rowObject){
            if(cellValue <= 0){
                return "";
            }
            
            return rVal = '<a href="javascript:attchFileDownload(\''+rowObject.atchFileId+'\', \'0\');"><img src="'+ctx+'/images/icon/zip.png" height="16" width="16"></a>';
        };
        
        var dialogManageCloseHisColNames = ['마감일자', '마감취소일자',
                                            '전체','집계','인건','기초','경상','투자','국고','기본','시책','국외','공통','무기',
                                            'atchFileId'
                                            ];
        var dialogManageCloseHisColModel = [
                                            {name : 'closeDate', index : 'closeDate', width : 130, sortable : false, fixed : true, align : 'center'},
                                            {name : 'closeCancelDate', index : 'closeCancelDate', width : 130, sortable : false, fixed : true, align : 'center'},
                                            {name : 'fileSn0', index : 'fileSn0', width : 30, sortable : false, fixed : true, align : 'center',
                                                formatter:fileSnAllFormatter
                                            },
                                            {name : 'fileSn1', index : 'fileSn1', width : 30, sortable : false, fixed : true, align : 'center',
                                                formatter:fileSnFormatter
                                            },
                                            {name : 'fileSn2', index : 'fileSn2', width : 30, sortable : false, fixed : true, align : 'center',
                                                formatter:fileSnFormatter
                                            },
                                            {name : 'fileSn3', index : 'fileSn3', width : 30, sortable : false, fixed : true, align : 'center',
                                                formatter:fileSnFormatter
                                            },
                                            {name : 'fileSn110', index : 'fileSn110', width : 30, sortable : false, fixed : true, align : 'center',
                                                formatter:fileSnFormatter
                                            },
                                            {name : 'fileSn120', index : 'fileSn120', width : 30, sortable : false, fixed : true, align : 'center',
                                                formatter:fileSnFormatter
                                            },
                                            {name : 'fileSn130', index : 'fileSn130', width : 30, sortable : false, fixed : true, align : 'center',
                                                formatter:fileSnFormatter
                                            },
                                            {name : 'fileSn140', index : 'fileSn140', width : 30, sortable : false, fixed : true, align : 'center',
                                                formatter:fileSnFormatter
                                            },
                                            {name : 'fileSn150', index : 'fileSn150', width : 30, sortable : false, fixed : true, align : 'center',
                                                formatter:fileSnFormatter
                                            },
                                            {name : 'fileSn155', index : 'fileSn155', width : 30, sortable : false, fixed : true, align : 'center',
                                                formatter:fileSnFormatter
                                            },
                                            {name : 'fileSn160', index : 'fileSn160', width : 30, sortable : false, fixed : true, align : 'center',
                                                formatter:fileSnFormatter
                                            },
                                            {name : 'fileSn190', index : 'fileSn190', width : 30, sortable : false, fixed : true, align : 'center',
                                                formatter:fileSnFormatter
                                            },
                                            {name : 'atchFileId', index : 'atchFileId', width : 0, sortable : false, fixed : true, hidden : true }
        ];
        
        var dialogManageCloseHisGridParam = {
                id : "DIALOG_MANAGE_CLOSE_HIS",
                colNames : dialogManageCloseHisColNames,
                colModel : dialogManageCloseHisColModel,
                defaultRows : 5,
                rowNum : 1000,
                width: "auto",
                height: "350"
        };

        var dialogManageCloseHisGrid = $.csGrid(dialogManageCloseHisGridParam);
        
        var doDialogManageCloseHisSearchCallBack = function(data) {
            if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
                return;
            }
            
            dialogManageCloseHisGrid.addCsJsonData(data);
            
            data = null;
        };
        
        var doDialogManageCloseHisSearch = function() {
            $.csAjaxCall({
                url : "/dialog/ajaxDialogManageCloseHisSearch.do",
                data : {
                        fisYear: $("#dialogManageCloseHisFisYear", dialogObj).val(),
                        bgtDgr: $("#dialogManageCloseHisBgtDgr", dialogObj).val()
                       },
                async : true,
                callBack : doDialogManageCloseHisSearchCallBack
            });
        };
        
        dialogManageCloseHisClose = function() {
            $("#dialogManageCloseHisCallBackFunction", dialogObj).val("");
            $("#dialogManageCloseHisFisYear", dialogObj).val("");
            $("#dialogManageCloseHisBgtDgr", dialogObj).val("");
            $("#dialogManageCloseHisDiv").dialog('close');
        };
        
        $("#dialogManageCloseHisDiv").dialog({
            title : "마감관리이력",
            autoOpen : false,
            width : 710,
            height : 520,
            modal : true,
            resizable : true,
            open : function(){
                doDialogManageCloseHisSearch();
            },
            buttons : {
                "닫기" : function() {
                    dialogManageCloseHisClose();
                }
            }
        });
    });
</script>
<div id="dialogManageCloseHisDiv" class="dialog" style="display: none;">
  <input type="hidden" id="dialogManageCloseHisCallBackFunction"/>
  <input type="hidden" id="dialogManageCloseHisFisYear"/>
  <input type="hidden" id="dialogManageCloseHisBgtDgr"/>
  <div class="viewDiv" style="width: 680px;">
  </div>
  <div id="dialogManageHisCloseDiv" >
    <div id="DIALOG_MANAGE_CLOSE_HIS_DIV" class="csGrid">
      <table id="DIALOG_MANAGE_CLOSE_HIS_GRD"></table>
    </div>
  </div>
</div>