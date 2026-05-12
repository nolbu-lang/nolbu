$(document).ready(function() {
    var tabId = _manageBatchLogTabId;
    var tabObj = $("#"+tabId);
    
    var manageBatchLogcolNames = ['순번', 
                    '배치실행시각', 
                    '배치종료시각'
                   ];
    
    var manageBatchLogcolModel = [
                        {name : 'rowNum', index : 'rowNum', width : 100, sortable : false, fixed : true, align : 'center'},
                        {name : 'beginDate', index : 'beginDate', width : 200, sortable : false, fixed : true, align : 'center'},
                        {name : 'endDate', index : 'endDate', width : 200, sortable : false, fixed : true, align : 'center'}
                    ];
   
    manageBatchLogPageSearch = function(page) {
        manageBatchLogSearch({
            page : page
        });
    };
    
    var manageBatchLogSearchParam = {
            page : 1,
            rowNum : 10
    };
    
    $("#searchBtn").click(function() {
        manageBatchLogSearch({
            page : 1
        });
    });
    
    var getManageBatchLogSearchParam = function(params) {

        var searchParam = {
                beginYmdFr: $("#manageBatchLogBeginYmdFr").val().replaceAll("-", ""),
                beginYmdTo: $("#manageBatchLogBeginYmdTo").val().replaceAll("-", "")
        };

        $.extend(manageBatchLogSearchParam, searchParam);
        $.extend(manageBatchLogSearchParam, params);

        return manageBatchLogSearchParam;
    };
    
    var manageBatchLoggridParam = {
            id : "MANAGE_BATCHLOG_LIST",
            colNames : manageBatchLogcolNames,
            colModel : manageBatchLogcolModel,
            onSelectRow: function(rowId){
            }
    };
    
    var manageBatchLoggridGrid = $.csGrid(manageBatchLoggridParam);
    
    var manageBatchLogSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        manageBatchLoggridGrid.addCsJsonData(data);
        
        $("#MANAGE_BATCHLOG_LIST_PGR").addPagingData(data, "manageBatchLogPageSearch");
        $("#MANAGE_BATCHLOG_LIST_TOT").html("총건수 : " + addCommaStr(data.totalCount) + "건");
    };
    
    var manageBatchLogSearch = function(params) {
        $.csAjaxCall({
            url : "/manage/ajaxManageBatchLogList.do",
            data : getManageBatchLogSearchParam(params),
            async : true,
            callBack : manageBatchLogSearchCallBack
        });
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#MANAGE_BATCHLOG_LIST_GRD", $("#"+tabId))) == false){
            $("#MANAGE_BATCHLOG_LIST_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#MANAGE_BATCHLOG_LIST_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 105,
        center__onresize: mainBodyResize
    });
    
    $("#condDetlCdNm").keypress(function(event){
        if(event.which == 13){
            $("#searchBtn").click();    
        }
    });

    $("#manageBatchLogBeginYmdFr").csDatepicker({yearRange:'2013:c+5>'});
    $("#manageBatchLogBeginYmdTo").csDatepicker({yearRange:'2013:c+5>'});
    
    manageBatchLogSearch();
    
});
