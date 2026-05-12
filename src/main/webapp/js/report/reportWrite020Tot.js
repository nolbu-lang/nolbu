$(document).ready(function() {
    var tabId = _reportWrite020TotTabId;
    var tabObj = $("#"+tabId);
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        return ' style="vertical-align: top; padding-right:0px;"';
    };
    
    var totFrscAmtFormatter = function(cellValue, options, rowObject){
        if(rowObject.teBgtCompoId == "00000000000"){
            readOnlyStr = "readonly";
            classStr = 'class="amtInput020 ui-state-disabled"';
        }else{
            classStr = 'class="amtInput020 ui-state-enabled"';
        }

        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="20px"/>'
                 + '    <col width="70px"/>'
                 + '    <col width="*"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td colspan="2" style="border-right-color:#AAAAAA;" >계</td>'
                 + '      <td class="amtView020Td">'+addCommaStr(cellValue)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td rowspan="6" style="border-right-color:#AAAAAA;" >재<br>원<br>별<br></td>'
                 + '      <td style="border-right-color:#AAAAAA;" >시비</td>'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.totFrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#AAAAAA;" >국비</td>'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.totFrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#AAAAAA;" >교부세</td>'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.totFrscAmt3)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#AAAAAA;" >지방채</td>'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.totFrscAmt4)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#AAAAAA;" >채무</td>'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.totFrscAmt5)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#AAAAAA;" >기타</td>'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.totFrscAmt6)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td rowspan="6" style="border-right-color:#AAAAAA; border-bottom-color:#ffffff !important; " >성<br>격<br>별<br></td>'
                 + '      <td style="border-right-color:#AAAAAA;" >공사</td>'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.totCharAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#AAAAAA;" >보상</td>'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.totCharAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#AAAAAA; border-bottom-color:#ffffff !important; " >기타</td>'
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.totCharAmt3)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';

        return rVal;
    };
    
    var preInvFrscAmtFormatter = function(cellValue, options, rowObject){
        if(rowObject.teBgtCompoId == "00000000000"){
            readOnlyStr = "readonly";
            classStr = 'class="amtInput020 ui-state-disabled"';
        }else{
            classStr = 'class="amtInput020 ui-state-enabled"';
        }

        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(cellValue)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preInvFrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preInvFrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preInvFrscAmt3)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preInvFrscAmt4)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preInvFrscAmt5)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preInvFrscAmt6)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preInvCharAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preInvCharAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.preInvCharAmt3)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var preDefFrscAmtFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }

        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(cellValue)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preDefFrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preDefFrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preDefFrscAmt3)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preDefFrscAmt4)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preDefFrscAmt5)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preDefFrscAmt6)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preDefCharAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preDefCharAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.preDefCharAmt3)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var preFrscAmtFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(cellValue)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preFrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preFrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preFrscAmt3)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preFrscAmt4)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preFrscAmt5)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preFrscAmt6)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preCharAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.preCharAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.preCharAmt3)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var dmnFrscAmtFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(cellValue)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.dmnFrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.dmnFrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.dmnFrscAmt3)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.dmnFrscAmt4)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.dmnFrscAmt5)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.dmnFrscAmt6)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.dmnCharAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.dmnCharAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.dmnCharAmt3)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var frscAmtFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(cellValue)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.frscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.frscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.frscAmt3)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.frscAmt4)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.frscAmt5)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.frscAmt6)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.charAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.charAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.charAmt3)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var colNames = ['구분(실국-사업구분)', '총사업비', '기투자', '본예산', '전년도 투자', '요구액', '조정액',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'reportCd', 'reportDetlCd', 'dgrLevel'
                   ];
    
    var colModel = [ {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 300, sortable : false, fixed : true, align : 'left'},
                        {name : 'totFrscAmt0', index : 'totFrscAmt0', width : 200, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : totFrscAmtFormatter
                        },
                        {name : 'preInvFrscAmt0', index : 'preInvFrscAmt0', width : 100, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : preInvFrscAmtFormatter
                        },
                        {name : 'preDefFrscAmt0', index : 'preDefFrscAmt0', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : preDefFrscAmtFormatter
                        },
                        {name : 'preFrscAmt0', index : 'preFrscAmt0', width : 100, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : preFrscAmtFormatter
                        },
                        {name : 'dmnFrscAmt0', index : 'dmnFrscAmt0', width : 100, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : dmnFrscAmtFormatter
                        },
                        {name : 'frscAmt0', index : 'frscAmt0', width : 100, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : frscAmtFormatter
                        },
                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'reportCd', index : 'reportCd', width : 0, sortable : false, hidden : true},
                        {name : 'reportDetlCd', index : 'reportDetlCd', width : 0, sortable : false, hidden : true},
                        {name : 'dgrLevel', index : 'dgrLevel', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 200 ? $("#mainCenter", tabObj).height() - 110 : 200;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#REPORT_WRITE020TOT_GRD", $("#"+tabId))) == false){
            $("#REPORT_WRITE020TOT_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#REPORT_WRITE020TOT_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 175,
        center__onresize: mainBodyResize
    });
    
    var reportWrite020TotGrid = $("#REPORT_WRITE020TOT_GRD", tabObj);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        if(data.data.bgtDgr == "1"){
            colModel[3].hidden = true;
            colModel[4].hidden = false;
        }else{
            colModel[3].hidden = false;
            colModel[4].hidden = true;
        }

        $("#REPORT_WRITE020TOT_GRD", tabObj).jqGrid('GridUnload');
        reportWrite020TotGrid = $("#REPORT_WRITE020TOT_GRD", tabObj);
        reportWrite020TotGrid.csTreeGrid({
            datastr : data,
            height : getGridHeight(),
            colNames : colNames,
            colModel : colModel,
            ExpandColumn : "dgrcompoNm",
            ExpandColClick: false,
            hoverrows: false,
            jsonReader : {
                repeatitems : false,
                root : "dataList"
            },
            onSelectRow: function(rowId){
            }
        });

        reportWrite020TotGrid.jqGrid('setGroupHeaders', {
            useColSpanStyle : true,
            groupHeaders : [
               {startColumnName : 'dmnFrscAmt0',numberOfColumns : 2, titleText : '당해연도 투자계획'}
            ]
        });

        data = null;
    };
    
    var getSearchParam = function(){
        var reportCd = $("#condReportCd", tabObj).val();
        var reportDetlCd = $("#condReportDetlCd option:selected", tabObj).val();
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        var deptRankFr = $("#condDeptRankFr", tabObj).val();
        var deptRankTo = $("#condDeptRankTo", tabObj).val();
        var teMngMokCdFr = $("#condTeMngMokCdFr", tabObj).val();
        var teMngMokCdTo = $("#condTeMngMokCdTo", tabObj).val();
        var srchVal = $("#condSrchVal", tabObj).val();
        var amtUnit = $("#condAmtUnit", tabObj).val();
        var localGovCd = $("#condLocalGovCd option:selected", tabObj).val();
        var fisFgCd = $("#condFisFgCd option:selected", tabObj).val();
        
        var param = {reportCd : reportCd,
                reportDetlCd : reportDetlCd,
                fisYear : fisYear,
                bgtDgr : bgtDgr,
                officeCd : officeCd,
                deptRankFr : deptRankFr,
                deptRankTo : deptRankTo,
                teMngMokCdFr : teMngMokCdFr,
                teMngMokCdTo : teMngMokCdTo,
                srchVal : srchVal,
                amtUnit : amtUnit,
                localGovCd : localGovCd,
                mayorReportYn : "N",
                fisFgCd : (reportDetlCd == "027" ? fisFgCd : "")
         };
        
        return param;
    };
    
    var doSearch = function(){
/*
        var reportDetlCd = $("#condReportCd option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        if(isEmpty(reportDetlCd) == true && isEmpty(officeCd) == true){
            $.csAlert({
                msg : "조서상세구분이 전체일 경우는 실국을 선택하셔야 합니다.",
                callBack : function() {
                    $("#condOfficeCd", tabObj).focus();
                }
            });
            
            return;
        }
*/        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite020Report020TotList.do",
            data: getSearchParam(),
            async : true,
            callBack : doSearchCallBack
        });
    };
    
    $("#searchBtn", tabObj).click(function() {
        
        doSearch();
    });

    var doCondInit = function(){
        var reportCd = $("#condReportCd", tabObj).val();
        condReportDetlCdCreateCombo(reportCd, '');
        
        condLocalGovCdCreateCombo();
        
        $("#condFisYear", tabObj).csCreatCombo(comboData, {
            id : 'fisYear',
            groupId : 'ALL',
            selectedValue : '',
            comboType : '',
            comboTypeValue : ''
        });
       
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
        
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');

        condFisFgCdCreateCombo(fisYear + '_200', '');
        
        condTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
        
        $("#condDeptCdFr", tabObj).val("");
        $("#condDeptNmFr", tabObj).val("");
        $("#condDeptRankFr", tabObj).val("");
        $("#condDeptCdTo", tabObj).val("");
        $("#condDeptNmTo", tabObj).val("");
        $("#condDeptRankTo", tabObj).val("");
        $("#condSrchVal", tabObj).val("");
        
    };
    
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });
    
    $("#saveFileBtn", tabObj).click(function() {
        var param = getSearchParam();
        param["fileNm"] = "투자사업심사조서";
        param["amtUnit"] = "1000000";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite020SaveFile.do"
          , data: param
        });
    });
    
    $("#saveSheetBtn", tabObj).click(function() {
        var param = getSearchParam();
        param.reportDetlCd = "";
        param.mayorReportYn = "N";
        param["fileNm"] = "투자사업심사조서";
        param["amtUnit"] = "1000000";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite020SaveSheet.do"
          , data: param
        });
    });
    
    $("#saveSheetBtn2", tabObj).click(function() {
        var param = getSearchParam();
        param.reportDetlCd = "";
        param.mayorReportYn = "Y";
        param["fileNm"] = "투자사업심사조서_시장님보고";
        param["amtUnit"] = "1000000";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite020SaveSheet2.do"
          , data: param
        });
    });
    
    $("#saveGuGunBtn", tabObj).click(function() {
        var param = getSearchParam();
        param.reportDetlCd = "";
        param.mayorReportYn = "N";
        param["fileNm"] = "구군별투자사업목록";
        param["amtUnit"] = "1000000";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite020SaveGuGun.do"
          , data: param
        });
    });
    
    var doChangeCondFisYear = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
        doChageCondBgtDgr();
        
    };
    
    var doChageCondBgtDgr = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
        doChangeCondOfficeCd();
        
        condTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
    };
    
    var condReportDetlCd = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condFisFgCdCreateCombo(fisYear + '_200', '');
    };
    
    var doChangeCondOfficeCd = function(){
        $("#condDeptCdFr", tabObj).val("");
        $("#condDeptNmFr", tabObj).val("");
        $("#condDeptRankFr", tabObj).val("");
        $("#condDeptCdTo", tabObj).val("");
        $("#condDeptNmTo", tabObj).val("");
        $("#condDeptRankTo", tabObj).val("");
    };
    
    $("#condFisYear", tabObj).change(function(){
        doChangeCondFisYear();
    });
    
    $("#condBgtDgr", tabObj).change(function(){
        doChageCondBgtDgr();
    });
    
    $("#condOfficeCd", tabObj).change(function(){
        doChangeCondOfficeCd();
    });
        
    $("#condReportDetlCd", tabObj).change(function(){
        condReportDetlCd();
    });
    
    $("#condTeMngMokCdFr", tabObj).change(function(){
        $("#condTeMngMokCdTo", tabObj).val($("#condTeMngMokCdFr option:selected", tabObj).val());
    });
    
    $("#openDialogBgtDeptBtnFr", tabObj).click(function(){
        openDialogBgtDeptSelt(1);
    });
    
    $("#openDialogBgtDeptBtnTo", tabObj).click(function(){
        openDialogBgtDeptSelt(2);
    });

    var comboParam = [
                      {id : "reportDetlCd", codeId : "RP002"},
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "bgtDgr", subQueryId : "BgtDgr"},
                      {id : "officeCd", subQueryId : "OfficeCd", reportCd: "020"},
                      {id : "teMngMokCd", subQueryId : "TeMngMokCd"},
                      {id : "localGovCd", subQueryId : "LocalGovCd"},
                      {id : "fisFgCd", subQueryId : "FisFgCd"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);
        
    var condReportDetlCdCreateCombo = function(groupId, selectedValue){
        $("#condReportDetlCd", tabObj).csCreatCombo(comboData
                , {id: 'reportDetlCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
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
    
    var condOfficeCdCreateCombo = function(groupId, selectedValue){
        $("#condOfficeCd", tabObj).csCreatCombo(comboData
                , {id: 'officeCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
        
    var condFisFgCdCreateCombo = function(groupId, selectedValue){
        $("#condFisFgCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
        
        var reportDetlCd = $("#condReportDetlCd option:selected", tabObj).val();
        if(reportDetlCd == "027"){
            $("#condFisFgCd", tabObj).show();
        }else{
            $("#condFisFgCd", tabObj).hide();
        }
    };
    
    var condTeMngMokCdFrCreateCombo = function(groupId, selectedValue){
        $("#condTeMngMokCdFr", tabObj).csCreatCombo(comboData
                , {id: 'teMngMokCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condTeMngMokCdToCreateCombo = function(groupId, selectedValue){
        $("#condTeMngMokCdTo", tabObj).csCreatCombo(comboData
                , {id: 'teMngMokCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
        
    var condLocalGovCdCreateCombo = function(){
        $("#condLocalGovCd", tabObj).csCreatCombo(comboData
                , {id: 'localGovCd'
                  , groupId: 'ALL'
                  , selectedValue: ''
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
    doCondInit();
});
