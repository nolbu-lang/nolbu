$(document).ready(function() {
    var tabId = _manageCommcdTabId;
    var tabObj = $("#"+tabId);
    
    var manageCommcdcolNames = ['', 
                    '순번', 
                    '그룹코드명', 
                    '코드명',
                    'clCd',
                    'detlCd'
                   ];
    
    var manageCommcdcolModel = [
                        {name : 'selYn', index:'selYn', width: 50, align:'center', sortable : false, fixed : true, frozen: true,
                            formatter: function (cellValue, option) {
                                return '<input type="radio" name="radio_' + option.gid + '"  />';
                            }
                        },
                        {name : 'rowNum', index : 'rowNum', width : 100, sortable : false, fixed : true, align : 'center'},
                        {name : 'clCdNm', index : 'clCdNm', width : 200, sortable : false, fixed : true, align : 'left'},
                        {name : 'detlCdNm', index : 'detlCdNm', width : 200, sortable : false, fixed : true, align : 'left'},
                        {name : 'clCd', index : 'clCd', width : 0, sortable : false, fixed : true, hidden : true },
                        {name : 'detlCd', index : 'detlCd', width : 0, sortable : false, fixed : true, hidden : true } 
                    ];
   
    manageCommcdPageSearch = function(page) {
        manageCommcdSearch({
            page : page
        });
    };
    
    var manageCommcdSearchParam = {
            page : 1,
            rowNum : 10
    };
    
    $("#searchBtn").click(function() {
        manageCommcdSearch({
            page : 1
        });
    });  
    
    manageCommcdModifyDialogSaveCallBackFunction = function(param){
        manageCommcdSearch();
    };
    
    var getManageCommcdSearchParam = function(params) {

        var searchParam = {
             detlcdnm : $("#condDetlCdNm", tabObj).val()
        };

        $.extend(manageCommcdSearchParam, searchParam);
        $.extend(manageCommcdSearchParam, params);

        return manageCommcdSearchParam;
    };
    
    var manageCommcdgridParam = {
            id : "MANAGE_COMMCD_LIST",
            colNames : manageCommcdcolNames,
            colModel : manageCommcdcolModel,
            onSelectRow: function(rowId){
            }
    };
    
    var manageCommcdSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        manageCommcdgridGrid.addCsJsonData(data);
        
        $("#MANAGE_COMMCD_LIST_PGR").addPagingData(data, "manageCommcdPageSearch");
        $("#MANAGE_COMMCD_LIST_TOT").html("총건수 : " + addCommaStr(data.totalCount) + "건");
    };
    
    var manageCommcdgridGrid = $.csGrid(manageCommcdgridParam);
    
    var manageCommcdSearch = function(params) {
        $.csAjaxCall({
            url : "/manage/ajaxManageCommcdList.do",
            data : getManageCommcdSearchParam(params),
            async : true,
            callBack : manageCommcdSearchCallBack
        });
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#MANAGE_COMMCD_LIST_GRD", $("#"+tabId))) == false){
            $("#MANAGE_COMMCD_LIST_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#MANAGE_COMMCD_LIST_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 105,
        center__onresize: mainBodyResize
    });
    
    var getSelectedRowId = function(){
        var $selRadio = $('input[name=radio_MANAGE_COMMCD_LIST_GRD]:checked'), $tr;
        if ($selRadio.length > 0) {
            $tr = $selRadio.closest('tr');
            if ($tr.length > 0) {
                return $tr.attr('id');
            }
        }
            
        return "";
    };
    
    $("#modifyBtn", tabObj).click(function() {
               
        var rowId = getSelectedRowId();
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "수정할 항목을 선택하여 주십시오."
            });
            
            return;
        }
        var rowData = manageCommcdgridGrid.getRowData(rowId);
        
       $("#dialogManageCommcdModifyCallBackFunction", $("#dialogManageCommcdModifyDiv")).val("manageCommcdModifyDialogSaveCallBackFunction");
       $("#dialogManageCommcdModifyClCd", $("#dialogManageCommcdModifyDiv")).val(rowData.clCd);
       $("#dialogManageCommcdModifyDetlCd", $("#dialogManageCommcdModifyDiv")).val(rowData.detlCd);
       $("#dialogManageCommcdModifyDetlCdNm", $("#dialogManageCommcdModifyDiv")).val(rowData.detlCdNm);
       $("#dialogManageCommcdModifyDiv").dialog('open');
    });
    
    $("#condDetlCdNm").keypress(function(event){
        if(event.which == 13){
            $("#searchBtn").click();    
        }
    });
    
    manageCommcdSearch();
    
});
