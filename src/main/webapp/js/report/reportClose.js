$(document).ready(function() {

    var tabId = _reportCloseTabId;
    var tabObj = $("#"+tabId);    
    
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
        
        var rVal = '<a href="javascript:reportCloseOpenDialogManageCloseHis(\''+options.rowId+'\');"><img src="'+ctx+'/images/icon/history.png" alt="이력보기" height="16" width="16"></a>';
        
        return rVal;
    };

    var reportCloseColNames = ['순번', '회계연도','예산구분','마감여부','마감자','마감시간','이력',
                              '전체','집계','인건','기초','경상','투자','국고','기본','시책','국외','공통','무기',
                              'bgtDgr',
                              'hisSeq',
                              'atchFileId'
                             ];
    
    var reportCloseColModel = [
                          {name : 'rowNum', index : 'rowNum', width : 40, sortable : false, fixed : true, align : 'center'},
                          {name : 'fisYear', index : 'fisYear', width : 55, sortable : false, fixed : true, align : 'center'},
                          {name : 'bgtDgrNm', index : 'bgtDgrNm', width : 60, sortable : false, fixed : true, align : 'center'},
                          {name : 'closeYn', index : 'closeYn', width : 60, sortable : false, fixed : true, align : 'center'},
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
        
    reportClosePageSearch = function(page) {
        reportCloseSearch({
            page : page
        });
    };
    
    var reportCloseSearchParam = {
            page : 1,
            rowNum : 50
    };
    
    $("#errExcelBtn", tabObj).click(function() {
        attchFileDownload('FILA_000000000000000', '1');
    });  
    
    $("#searchBtn", tabObj).click(function() {
        reportCloseSearch({
            page : 1
        });
    });  
    
    var getReportCloseSearchParam = function(params) {

        var searchParam = {
             fisYear : $("#condFisYeaer", tabObj).val(),
             bgtDgr : $("#condBgtDgr", tabObj).val()
        };

        $.extend(reportCloseSearchParam, searchParam);
        $.extend(reportCloseSearchParam, params);

        return reportCloseSearchParam;
    };
    
    var reportCloseGridParam = {
            id : "REPORT_CLOSE_LIST",
            colNames : reportCloseColNames,
            colModel : reportCloseColModel,
            onSelectRow: function(rowId){
            }
    };
    
    var reportCloseSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        reportCloseGridGrid.addCsJsonData(data);
        
        $("#REPORT_CLOSE_LIST_PGR").addPagingData(data, "reportClosePageSearch");
        $("#REPORT_Close_LIST_TOT").html("총건수 : " + addCommaStr(data.totalCount) + "건");
    };
    
    var reportCloseGridGrid = $.csGrid(reportCloseGridParam);
    
    var reportCloseSearch = function(params) {
        $.csAjaxCall({
            url : "/manage/ajaxManageCloseList.do",
            data : getReportCloseSearchParam(params),
            async : true,
            callBack : reportCloseSearchCallBack
        });
    };

    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 80 ? $("#mainCenter", tabObj).height() - 110 : 80;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#REPORT_CLOSE_LIST_GRD", $("#"+tabId))) == false){
            $("#REPORT_CLOSE_LIST_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#REPORT_CLOSE_LIST_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
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
    
    reportCloseSaveFile = function(fileName, filePath){
        alert(filePath);
        var param = {
                fileName : fileName,
                realFileName : filePath,
                fileDeleteYn : "N"
        };
        
        bcjisExeclDown(ctx + "/comm/excelFileDownload.do", param);
    };
    
    reportCloseOpenDialogManageCloseHis = function(rowId){
        var rowData = reportCloseGridGrid.getRowData(rowId);
        
        $("#dialogManageCloseHisCallBackFunction", $("#dialogManageCloseHisDiv")).val("");
        $("#dialogManageCloseHisFisYear", $("#dialogManageCloseHisDiv")).val(rowData.fisYear);
        $("#dialogManageCloseHisBgtDgr", $("#dialogManageCloseHisDiv")).val(rowData.bgtDgr);
        $("#dialogManageCloseHisDiv").dialog('open');
        
    };
    
    
    condBgtDgrCreateCombo($("#condFisYear option:selected", tabObj).val(), '');

    mainBodyResize();
    
    reportCloseSearch();
});
