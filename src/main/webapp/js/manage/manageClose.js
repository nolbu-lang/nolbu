$(document).ready(function() {

    var tabId = _manageCloseTabId;
    var tabObj = $("#"+tabId);    
    
    var closeViewFormatter = function(cellValue, options, rowObject){
        var demandCont = rowObject.demandCont; 
        if(isEmpty(demandCont) == true){
            demandCont = "";
        }
        
        var closeYn = rowObject.closeYn;
        
        var s = "";
        if(closeYn == "N"){
            s = "마감";
        }else{
            s = "마감취소";
        }
        
        var rVal = '<a href="javascript:manageCloseUpdateManageClose(\''+options.rowId+'\');">'+s+'</a>';
        
        return rVal;
    };
    
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
    
    var hisViewFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(isEmpty(rowObject.hisSeq) == true || rowObject.hisSeq == "0"){
            return "";
        }
        
        var rVal = '<a href="javascript:manageCloseOpenDialogManageCloseHis(\''+options.rowId+'\');"><img src="'+ctx+'/images/icon/history.png" alt="이력보기" height="16" width="16"></a>';
        
        return rVal;
    };

    var manageCloseColNames = ['순번', '회계연도','예산구분','마감여부','마감','마감자','마감시간','이력',
                              '전체','집계','인건','기초','경상','투자','국고','기본','시책','국외','공통','무기',
                              'bgtDgr',
                              'hisSeq',
                              'atchFileId'
                             ];
    
    var manageCloseColModel = [
                          {name : 'rowNum', index : 'rowNum', width : 40, sortable : false, fixed : true, align : 'center'},
                          {name : 'fisYear', index : 'fisYear', width : 55, sortable : false, fixed : true, align : 'center'},
                          {name : 'bgtDgrNm', index : 'bgtDgrNm', width : 60, sortable : false, fixed : true, align : 'center'},
                          {name : 'closeYn', index : 'closeYn', width : 60, sortable : false, fixed : true, align : 'center'},
                          {name : 'closeView', index : 'closeView', width : 60, sortable : false, fixed : true, align : 'center',
                              formatter:closeViewFormatter
                          },
                          {name : 'closeUserNm', index : 'closeUserNm', width : 70, sortable : false, fixed : true, align : 'center'},
                          {name : 'closeDate', index : 'closeDate', width : 150, sortable : false, fixed : true, align : 'center'},
                          {name : 'hisView', index : 'hisView', width : 30, sortable : false, fixed : true, align : 'center',
                              formatter:hisViewFormatter
                          },
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
                          {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, fixed : true, hidden : true },
                          {name : 'hisSeq', index : 'hisSeq', width : 0, sortable : false, fixed : true, hidden : true },
                          {name : 'atchFileId', index : 'atchFileId', width : 0, sortable : false, fixed : true, hidden : true }
                      ];
        
    manageClosePageSearch = function(page) {
        manageCloseSearch({
            page : page
        });
    };
    
    var manageCloseSearchParam = {
            page : 1,
            rowNum : 50
    };
    
    $("#searchBtn", tabObj).click(function() {
        manageCloseSearch({
            page : 1
        });
    });  
    
    var getManageCloseSearchParam = function(params) {

        var searchParam = {
             fisYear : $("#condFisYear", tabObj).val(),
             bgtDgr : $("#condBgtDgr", tabObj).val()
        };

        $.extend(manageCloseSearchParam, searchParam);
        $.extend(manageCloseSearchParam, params);

        return manageCloseSearchParam;
    };
    
    var manageCloseGridParam = {
            id : "MANAGE_CLOSE_LIST",
            colNames : manageCloseColNames,
            colModel : manageCloseColModel,
            onSelectRow: function(rowId){
            }
    };
    
    var manageCloseSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        manageCloseGridGrid.addCsJsonData(data);
        
        $("#MANAGE_CLOSE_LIST_PGR").addPagingData(data, "manageClosePageSearch");
        $("#MANAGE_Close_LIST_TOT").html("총건수 : " + addCommaStr(data.totalCount) + "건");
    };
    
    var manageCloseGridGrid = $.csGrid(manageCloseGridParam);
    
    var manageCloseSearch = function(params) {
        $.csAjaxCall({
            url : "/manage/ajaxManageCloseList.do",
            data : getManageCloseSearchParam(params),
            async : true,
            callBack : manageCloseSearchCallBack
        });
    };

    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 80 ? $("#mainCenter", tabObj).height() - 110 : 80;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#MANAGE_CLOSE_LIST_GRD", $("#"+tabId))) == false){
            $("#MANAGE_CLOSE_LIST_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#MANAGE_CLOSE_LIST_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;

    $("#mainBody", tabObj).layout({
        north__size : 100,
        center__onresize: mainBodyResize
    });
    
    var comboParam = [
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "bgtDgr", subQueryId : "BgtDgr"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);
    
    $("#condFisYear", tabObj).csCreatCombo(comboData, {
        id : 'fisYear',
        groupId : 'ALL',
        selectedValue : '',
        comboType : 'A',
        comboTypeValue : ''
    });
    
    var condBgtDgrCreateCombo = function(groupId, selectedValue){
        $("#condBgtDgr", tabObj).csCreatCombo(comboData
                , {id: 'bgtDgr'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
    $("#condFisYear", tabObj).change(function(){
        doChangeCondFisYear();
    });
    
    var doChangeCondFisYear = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
    };
    
    var doManageCloseUpdateManageClose = function(params){
        if(params.confirmData != "Y"){
            return;
        }
        
        var data = $.csAjaxCall({
            url : "/manage/ajaxManageCloseUpdateManageClose.do",
            data : params.closeParam
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
                
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                manageCloseSearch();
            }
        });
        
    };
    
    manageCloseSaveFile = function(fileName, filePath){
        alert(filePath);
        var param = {
                fileName : fileName,
                realFileName : filePath,
                fileDeleteYn : "N"
        };
        
        bcjisExeclDown(ctx + "/comm/excelFileDownload.do", param);
    };
    
    manageCloseOpenDialogManageCloseHis = function(rowId){
        var rowData = manageCloseGridGrid.getRowData(rowId);
        
        $("#dialogManageCloseHisCallBackFunction", $("#dialogManageCloseHisDiv")).val("");
        $("#dialogManageCloseHisFisYear", $("#dialogManageCloseHisDiv")).val(rowData.fisYear);
        $("#dialogManageCloseHisBgtDgr", $("#dialogManageCloseHisDiv")).val(rowData.bgtDgr);
        $("#dialogManageCloseHisDiv").dialog('open');
        
    };
    
    manageCloseUpdateManageClose = function(rowId){
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "변경할 항목을 선택하여 주십시오."
            });
            
            return;
        }

        var rowData = manageCloseGridGrid.getRowData(rowId);
        if(isEmpty(rowData) == true){
            $.csAlert({
                msg : "변경할 항목을 선택하여 주십시오."
            });
            
            return;
            
        }
        
        var closeParam = {
                "fisYear" : rowData.fisYear,
                "bgtDgr" : rowData.bgtDgr,
                "closeYn" : (rowData.closeYn == "Y" ? "N" : "Y"),
                "hisSeq" : rowData.hisSeq
        };

        var msg = rowData.fisYear + "년 " + rowData.bgtDgrNm + "을(를) 마감 하시겠습니까?";
        if(rowData.closeYn == "Y"){
            msg = rowData.fisYear + "년 " + rowData.bgtDgrNm + "을(를) 마감취소 하시겠습니까?";
        }
        
        $.csConfirm({
            msg : msg,
            closeParam : closeParam,
            callBack : doManageCloseUpdateManageClose
        });
    };
    
    condBgtDgrCreateCombo($("#condFisYear option:selected", tabObj).val(), '');
    
    mainBodyResize();
    manageCloseSearch();
});
