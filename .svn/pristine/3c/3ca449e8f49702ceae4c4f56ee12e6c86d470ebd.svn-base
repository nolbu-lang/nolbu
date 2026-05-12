$(document).ready(function() {
    var tabId = _manageTeMngVeriTabId;
    var tabObj = $("#"+tabId);
    
    manageTeMngVeriDialogTeMngVeriCallBackFunction = function(param){
        manageTeMngVeriSearch({page : 1});
    };
    
    //등록
    manageTeMngVeriOpenDialogTeMngVeri = function(veriFg, rowId) {
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "항목을 선택하여 주십시오."
            });
            
            return;
        }
        
        var rowData = manageTeMngVeriGrid.getRowData(rowId);
        
        $("#dialogTeMngVeriCallBackFunction", $("#dialogTeMngVeriDiv")).val("manageTeMngVeriDialogTeMngVeriCallBackFunction");
        $("#dialogTeMngVeriFisYear", $("#dialogTeMngVeriDiv")).val(rowData.fisYear);
        $("#dialogTeMngVeriBgtDgr", $("#dialogTeMngVeriDiv")).val(rowData.bgtDgr);
        $("#dialogTeMngVeriTeMngMokCd", $("#dialogTeMngVeriDiv")).val(rowData.teMngMokCd);
        $("#dialogTeMngVeriVeriFg", $("#dialogTeMngVeriDiv")).val(veriFg);
        
        $("#dialogTeMngVeriDiv").dialog('open');
    };
    
    var veri010Formatter = function(cellValue, options, rowObject){
        var rVal = '<a href="javascript:manageTeMngVeriOpenDialogTeMngVeri(\'010\', \''+options.rowId+'\');"><img src="'+ctx+'/images/icon/edit.png" height="16" width="16" />'+cellValue+'</a>';
        return rVal;
    };
    
    var veri020Formatter = function(cellValue, options, rowObject){
        var rVal = '<a href="javascript:manageTeMngVeriOpenDialogTeMngVeri(\'020\', \''+options.rowId+'\');"><img src="'+ctx+'/images/icon/edit.png" height="16" width="16" />'+cellValue+'</a>';
        return rVal;
    };
    
    var veri030Formatter = function(cellValue, options, rowObject){
        var rVal = '<a href="javascript:manageTeMngVeriOpenDialogTeMngVeri(\'030\', \''+options.rowId+'\');"><img src="'+ctx+'/images/icon/edit.png" height="16" width="16" />'+cellValue+'</a>';
        return rVal;
    };
    
    var veri040Formatter = function(cellValue, options, rowObject){
        var rVal = '<a href="javascript:manageTeMngVeriOpenDialogTeMngVeri(\'040\', \''+options.rowId+'\');"><img src="'+ctx+'/images/icon/edit.png" height="16" width="16" />'+cellValue+'</a>';
        return rVal;
    };
    
    var manageTeMngVeriColNames = ['통계목명', '필수항목', '1개만선택', '1개이상선택', '조서기준오류', 'fisYear', 'bgtDgr', 'teMngMokCd'];
    
    var manageTeMngVeriColModel = [ 
                        {name : 'teMngMokNm', index : 'teMngMokNm', width : 200, sortable : false, fixed : true, align : 'left'},
                        {name : 'veri010', index : 'veri010', width : 150, sortable : false, fixed : true, align : 'left',
                            formatter:veri010Formatter
                        },
                        {name : 'veri020', index : 'veri020', width : 300, sortable : false, fixed : true, align : 'left',
                            formatter:veri020Formatter
                        },
                        {name : 'veri030', index : 'veri030', width : 300, sortable : false, fixed : true, align : 'left',
                            formatter:veri030Formatter
                        },
                        {name : 'veri040', index : 'veri040', width : 300, sortable : false, fixed : true, align : 'left',
                            formatter:veri040Formatter
                        },
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'teMngMokCd', index : 'dgrLevel', width : 0, sortable : false, hidden : true}
                    ];

    
    var manageTeMngVeriSearchParam = {
            page : 1,
            rowNum : 1000
    };
    
    $("#searchBtn", tabObj).click(function() {
        manageTeMngVeriSearch({
            page : 1
        });
    });  
    
    var getManageTeMngVeriSearchParam = function(params) {

        var searchParam = {
             fisYear : $("#condFisYear", tabObj).val(),
             bgtDgr : $("#condBgtDgr", tabObj).val()
        };

        $.extend(manageTeMngVeriSearchParam, searchParam);
        $.extend(manageTeMngVeriSearchParam, params);

        return manageTeMngVeriSearchParam;
    };
    
    var manageTeMngVeriGridParam = {
            id : "MANAGE_TEMNG_VERI_LIST",
            colNames : manageTeMngVeriColNames,
            colModel : manageTeMngVeriColModel,
            width: "auto",
            onSelectRow: function(rowId){
            }
    };
    
    var manageTeMngVeriGrid = $.csGrid(manageTeMngVeriGridParam);
    
    var manageTeMngVeriSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        manageTeMngVeriGrid.addCsJsonData(data);
        
        $("#MANAGE_TEMNG_VERI_LIST_TOT").html("총건수 : " + addCommaStr(data.totalCount) + "건");
    };
    
    var manageTeMngVeriSearch = function(params) {
        $.csAjaxCall({
            url : "/manage/ajaxManageTeMngVeriList.do",
            data : getManageTeMngVeriSearchParam(params),
            async : true,
            callBack : manageTeMngVeriSearchCallBack
        });
    };

    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 80 ? $("#mainCenter", tabObj).height() - 110 : 80;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#MANAGE_TEMNG_VERI_LIST_GRD", $("#"+tabId))) == false){
            $("#MANAGE_TEMNG_VERI_LIST_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#MANAGE_TEMNG_VERI_LIST_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
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
        comboType : '',
        comboTypeValue : ''
    });
    
    var condBgtDgrCreateCombo = function(groupId, selectedValue){
        $("#condBgtDgr", tabObj).csCreatCombo(comboData
                , {id: 'bgtDgr'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: ''
                  , comboTypeValue: ''
                  }
        );
    };
    
    condBgtDgrCreateCombo($("#condFisYear option:selected", tabObj).val(), '');
    
    $("#condFisYear", tabObj).change(function(){
        doChangeCondFisYear();
    });
    
    var doChangeCondFisYear = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
    };
    
    manageTeMngVeriSearch();
});