$(document).ready(function() {
    var tabId = _pledgeMatchTabId;
    var tabObj = $("#"+tabId);
    
    var pledgeInfoIdFormatter = function(cellValue, options, rowObject){
        
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        rVal = '<div>'
            + '<select id="pledgeInfoId_'+rowObject.tId+'" title="공약정보" style="width:90px;">'
            + pledgeInfoIdCombo('ALL', rowObject.pledgeInfoId)
            + '</select>'
            + '</div>';
        
        return rVal;
    };
    
    var colNames = ['회계연도', '예산구분', '차수', '공약정보', 'tId', 'bgtDgr', 'addTimes', 'pledgeInfoId'
                   ];

    var colModel = [ 
                        {name : 'fisYear', index : 'fisYear', width : 50, sortable : false, fixed : true, align : 'center'},
                        {name : 'bgtDgrView', index : 'bgtDgrView', width : 50, sortable : false, fixed : true, align : 'center'},
                        {name : 'addTimesView', index : 'addTimesView', width : 50, sortable : false, fixed : true, align : 'center'},
                        {name : 'pledgeInfoIdView', index : 'pledgeInfoIdView', width : 540, sortable : false, fixed : true, align : 'left',
                            formatter:pledgeInfoIdFormatter
                        },
                        {name : 'tId', index : 'tId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'addTimes', index : 'addTimes', width : 0, sortable : false, hidden : true},
                        {name : 'pledgeInfoId', index : 'pledgeInfoId', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 100 ? $("#mainCenter", tabObj).height() - 110 : 100;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#PLEDGE_MATCH_GRD", tabObj)) == false){
            $("#PLEDGE_MATCH_GRD", tabObj).setGridHeight(getGridHeight());
            $("#PLEDGE_MATCH_GRD", tabObj).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 120,
        center__onresize: mainBodyResize
    });
    
    var pledgeMatchGridParam = {
            id : "PLEDGE_MATCH",
            colNames : colNames,
            colModel : colModel,
            rowNum : 1000,
            defaultRows: 0
    };
    
    var pledgeMatchGrid = $.csGrid(pledgeMatchGridParam);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        $("#saveBtn", tabObj).btnChangeState(true);
        pledgeMatchGrid.addCsJsonData(data);
    };
    
    var doSearch = function(){
        $.csAjaxCall({
            url : "/pledge/ajaxPledgeMatchSelectPledgeInfoList.do",
            data: {fisYear : $("#condFisYear option:selected", tabObj).val()},
            async : true,
            callBack : doSearchCallBack
        });
    };
    
    $("#searchBtn", tabObj).click(function() {
        doSearch();
    });

    var doCondInit = function(){
        //
    };
    
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });
    
    var getSaveDatas = function(gridObject, gridRows){
        var saveDatas = [];
        var saveData = {};
        var rowId;
        var rowData;
        var pledgeInfoId = "";
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);
            if(isEmpty(rowData.fisYear) == false){
                pledgeInfoId = $('#pledgeInfoId_'+rowId+' option:selected', tabObj).val();
                if(isEmpty(pledgeInfoId) == true){
                    pledgeInfoId = "           ";
                }
                
                if(rowData.pledgeInfoId != pledgeInfoId){
                    saveData = {};
                    saveData["fisYear"] = rowData.fisYear;
                    saveData["bgtDgr"] = rowData.bgtDgr;
                    saveData["pledgeInfoId"] = pledgeInfoId;
                    
                    saveDatas.push(saveData);
                }
            }
        }
        
        return saveDatas;
    };

    var doSaveCallBack = function(data){
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                doSearch();
            }
        });
    };
    
    var doSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }
        
        var saveDatas = getSaveDatas(pledgeMatchGrid, $("#PLEDGE_MATCH_GRD", tabObj)[0].rows);
        if(isEmpty(saveDatas) == true || saveDatas.length < 1){
            $.csAlert({
                msg : "변경된 자료가 존재하지 않습니다."
            });
            
            return;
        }
        
        $.csAjaxCall({
            url : "/pledge/ajaxPledgeMatchUpdatePledgeInfoId.do",
            data : {saveDatas: saveDatas},
            async : true,
            callBack : doSaveCallBack
        });
    };
    
    $("#saveBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : doSave
        });
    });
    
    var comboParam = [
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "pledgeInfoId", subQueryId : "PledgeInfoId"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);
    
    $("#condFisYear", tabObj).csCreatCombo(comboData, {
        id : 'fisYear',
        groupId : 'ALL',
        selectedValue : '',
        comboType : 'A',
        comboTypeValue : ''
    });
        
    var pledgeInfoIdCombo = function(groupId, selectedValue){
        return getCsComboStr(comboData
                , {id: 'pledgeInfoId'
                    , groupId: groupId
                    , selectedValue: selectedValue
                    , comboType: 'S'
                    , comboTypeValue: ''
                    });
    };
    
    doCondInit();
    
    doSearch();
});
