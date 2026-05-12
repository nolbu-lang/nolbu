$(document).ready(function() {
    var tabId = _reportWrite0C0TabId;
    var tabObj = $("#"+tabId);
    var gridScrollPosition = 0;
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        if(rowObject.teBgtCompoId != "00000000000"){
            return ' style="vertical-align: top;"';
        }
    };
    
    reportWirte0C0DialogReport0C0DetlCallBackFunction = function(param){
        var rowId = param.dgrcompoId;
        if(isEmpty(rowId) == true){
            return;
        }
        
        var detlNm = isEmpty(param.detlNm) == true ? "" : param.detlNm;
        $("#detlNm_" + rowId, tabObj).html(detlNm);
    };
    
    reportWirte0C0OpenDialogReport0C0Detl = function(rowId){  
        var rowData = reportWrite0C0Grid.getRowData(rowId);
        
        $("#dialogReport0C0DetlCallBackFunction", $("#dialogReport0C0DetlDiv")).val("reportWirte0C0DialogReport0C0DetlCallBackFunction");
        $("#dialogReport0C0DetlReportCd", $("#dialogReport0C0DetlDiv")).val(rowData.reportCd);
        $("#dialogReport0C0DetlReportDetlCd", $("#dialogReport0C0DetlDiv")).val(rowData.reportDetlCd);
        $("#dialogReport0C0DetlFisYear", $("#dialogReport0C0DetlDiv")).val(rowData.fisYear);
        $("#dialogReport0C0DetlBgtDgr", $("#dialogReport0C0DetlDiv")).val(rowData.bgtDgr);
        $("#dialogReport0C0DetlTeBgtCompoId", $("#dialogReport0C0DetlDiv")).val(rowData.teBgtCompoId);
        $("#dialogReport0C0DetlTeBgtCompoSeq", $("#dialogReport0C0DetlDiv")).val(rowData.teBgtCompoSeq);
        
        $("#dialogReport0C0DetlDiv").dialog('open');
    };
    
    var editFormatter = function(cellValue, options, rowObject){
        if(_mainNorthPowGrCd == "BC002"){
            return "";
        }
        
        var rVal = '<a href="javascript:reportWirte0C0OpenDialogReport0C0Detl(\''+options.rowId+'\');"><span class="ui-icon ui-icon-pencil"></span></a>';
        
        return rVal;
    };
    
    var dgrcompoNmFormatter = function(cellValue, options, rowObject){
        var bizNm = isEmpty(rowObject.bizNm) == true ? "" : rowObject.bizNm;
        
        var rVal = '<span id="dgrcompoNmView_'+rowObject.dgrcompoId+'" >' + cellValue + '</span><br>'
                 + '<textarea id="bizNm_'+rowObject.dgrcompoId+'" style="width:200px;ime-mode:active;height:40px;">'+bizNm+'</textarea>';
        
        return rVal;
    };
    
    var bizSmryFormatter = function(cellValue, options, rowObject){
        var bizSmryTitle = isEmpty(rowObject.bizSmryTitle) == true ? "" : rowObject.bizSmryTitle;
        var bizSmryCont = isEmpty(rowObject.bizSmryCont) == true ? "" : rowObject.bizSmryCont;
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="20px"/>'
                 + '    <col width="*"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr style="border-right-width: 0px;">'
                 + '      <td>제목</td><td style="border-right: 0px !important;"><input id="bizSmryTitle_'+rowObject.dgrcompoId+'" value="'+bizSmryTitle+'" maxlength="300" style="width:98%;" /></td>'
                 + '    </tr>'
                 + '    <tr style="border-right-width: 0px;">'
                 + '      <td style="border-bottom-width: 0px !important;">내용</td><td style="border-right: 0px !important; border-bottom-width: 0px !important; padding-top: 1px; padding-bottom: 1px;"><textarea id="bizSmryCont_'+rowObject.dgrcompoId+'" style="width:415px;ime-mode:active;height:83px;">'+bizSmryCont+'</textarea></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
        
        return rVal;
    };
    
    var promCorpFormatter = function(cellValue, options, rowObject){
        var promCorpTitle = isEmpty(rowObject.promCorpTitle) == true ? "" : rowObject.promCorpTitle;
        var promCorpCont = isEmpty(rowObject.promCorpCont) == true ? "" : rowObject.promCorpCont;
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="20px"/>'
                 + '    <col width="*"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr style="border-right-width: 0px;">'
                 + '      <td>제목</td><td style="border-right: 0px !important;"><input id="promCorpTitle_'+rowObject.dgrcompoId+'" value="'+promCorpTitle+'" maxlength="300" style="width:98%;" /></td>'
                 + '    </tr>'
                 + '    <tr style="border-right-width: 0px;">'
                 + '      <td style="border-bottom-width: 0px !important;">내용</td><td style="border-right: 0px !important; border-bottom-width: 0px !important; padding-top: 1px; padding-bottom: 1px;"><textarea id="promCorpCont_'+rowObject.dgrcompoId+'" style="width:415px;ime-mode:active;height:83px;">'+promCorpCont+'</textarea></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
        
        return rVal;
    };
    
    var demandFormatter = function(cellValue, options, rowObject){
        var demandTitle = isEmpty(rowObject.demandTitle) == true ? "" : rowObject.demandTitle;
        var demandCont = isEmpty(rowObject.demandCont) == true ? "" : rowObject.demandCont;
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="20px"/>'
                 + '    <col width="*"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr style="border-right-width: 0px;">'
                 + '      <td>제목</td><td style="border-right: 0px !important;"><input id="demandTitle_'+rowObject.dgrcompoId+'" value="'+demandTitle+'" maxlength="300" style="width:98%;" /></td>'
                 + '    </tr>'
                 + '    <tr style="border-right-width: 0px;">'
                 + '      <td style="border-bottom-width: 0px !important;">내용</td><td style="border-right: 0px !important; border-bottom-width: 0px !important; padding-top: 1px; padding-bottom: 1px;"><textarea id="demandCont_'+rowObject.dgrcompoId+'" style="width:415px;ime-mode:active;height:83px;">'+demandCont+'</textarea></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
        
        return rVal;
    };
    
    var explainContFormatter = function(cellValue, options, rowObject){
        var explainCont = isEmpty(rowObject.explainCont) == true ? "" : rowObject.explainCont;
        var rVal = '<div>'
                 + '<textarea id="explainCont_'+rowObject.dgrcompoId+'" style="width:415px;ime-mode:active;height:100px;">'+explainCont+'</textarea>'
                 + '</div>';
        
        return rVal;
    };
    
    var examContFormatter = function(cellValue, options, rowObject){
        var examCont = isEmpty(rowObject.examCont) == true ? "" : rowObject.examCont;
        var rVal = '<div>'
                 + '<textarea id="examCont_'+rowObject.dgrcompoId+'" style="width:415px;ime-mode:active;height:100px;">'+examCont+'</textarea>'
                 + '</div>';
        
        return rVal;
    };
    
    var reportFgFormatter = function(cellValue, options, rowObject){
        var reportFg = isEmpty(rowObject.reportFg) == true ? "" : rowObject.reportFg;
        
        var rVal = '<div>'
                 + '<select id="reportFg_'+rowObject.dgrcompoId+'" title="보고서구분" style="width:90px;">'
                 + reportFgCreateCombo('RP009', reportFg)
                 + '</select>'
                 + '</div>';

        return rVal;
    };
    
    var totFrscAmtFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }

        var totFrscCont = isEmpty(rowObject.totFrscCont) == true ? "" : rowObject.totFrscCont;
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="20px"/>'
                 + '    <col width="70px"/>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr>'
                 + '      <td colspan="2">사업규모</td>'
                 + '      <td style="border-right: 0px !important;"><input id="totFrscCont_'+rowObject.dgrcompoId+'" value="'+totFrscCont+'" maxlength="300" style="width:98%;" /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td colspan="2">계</td>'
                 + '      <td id="totFrscAmt0_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(cellValue)+'</td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td rowspan="6">재<br>원<br>별<br></td>'
                 + '      <td>시비</td>'
                 + '      <td id="totFrscAmt1_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.totFrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>국비</td>'
                 + '      <td id="totFrscAmt2_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.totFrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>기타</td>'
                 + '      <td id="totFrscAmt6_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.totFrscAmt6)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';

        return rVal;
    };
    
    var preInvFrscAmtFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }

        var preInvFrscCont = isEmpty(rowObject.preInvFrscCont) == true ? "" : rowObject.preInvFrscCont;
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr>'
                 + '      <td style="border-right: 0px !important;"><input id="preInvFrscCont_'+rowObject.dgrcompoId+'" value="'+preInvFrscCont+'" maxlength="300" style="width:98%;" /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td id="preInvFrscAmt0_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(cellValue)+'</td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td id="preInvFrscAmt1_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.preInvFrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td id="preInvFrscAmt2_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.preInvFrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td id="preInvFrscAmt6_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.preInvFrscAmt6)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var preFrscAmtFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }

        var preFrscCont = isEmpty(rowObject.preFrscCont) == true ? "" : rowObject.preFrscCont;
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr>'
                 + '      <td style="border-right: 0px !important;"><input id="preFrscCont_'+rowObject.dgrcompoId+'" value="'+preFrscCont+'" maxlength="300" style="width:98%;" /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="preFrscAmt0_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(cellValue)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="preFrscAmt1_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.preFrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="preFrscAmt2_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.preFrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="preFrscAmt6_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.preFrscAmt6)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var preDefFrscAmtFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }

        var preDefFrscCont = isEmpty(rowObject.preDefFrscCont) == true ? "" : rowObject.preDefFrscCont;
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr>'
                 + '      <td style="border-right: 0px !important;"><input id="preDefFrscCont_'+rowObject.dgrcompoId+'" value="'+preDefFrscCont+'" maxlength="300" style="width:98%;" /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="preDefFrscAmt0_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(cellValue)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="preDefFrscAmt1_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.preDefFrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="preDefFrscAmt2_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.preDefFrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="preDefFrscAmt6_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.preDefFrscAmt6)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var dmnFrscAmtFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }

        var dmnFrscCont = isEmpty(rowObject.dmnFrscCont) == true ? "" : rowObject.dmnFrscCont;
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr>'
                 + '      <td style="border-right: 0px !important;"><input id="dmnFrscCont_'+rowObject.dgrcompoId+'" value="'+dmnFrscCont+'" maxlength="300" style="width:98%;" /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="dmnFrscAmt0_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(cellValue)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="dmnFrscAmt1_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.dmnFrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="dmnFrscAmt2_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.dmnFrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="dmnFrscAmt6_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.dmnFrscAmt6)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var frscAmtFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }

        var frscCont = isEmpty(rowObject.frscCont) == true ? "" : rowObject.frscCont;
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr>'
                 + '      <td style="border-right: 0px !important;"><input id="frscCont_'+rowObject.dgrcompoId+'" value="'+frscCont+'" maxlength="300" style="width:98%;" /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="frscAmt0_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(cellValue)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="frscAmt1_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.frscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="frscAmt2_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.frscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView0C0Tr">'
                 + '      <td id="frscAmt6_'+rowObject.dgrcompoId+'" class="amtView0C0Td">'+addCommaStr(rowObject.frscAmt6)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var investPlanViewFormatter = function(cellValue, options, rowObject){

        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var frscAmt0 = rowObject.bgtDgr == '1' ? Number(rowObject.preFrscAmt0) : Number(rowObject.preDefFrscAmt0);
        var frscAmt1 = rowObject.bgtDgr == '1' ? Number(rowObject.preFrscAmt1) : Number(rowObject.preDefFrscAmt1);
        var frscAmt2 = rowObject.bgtDgr == '1' ? Number(rowObject.preFrscAmt2) : Number(rowObject.preDefFrscAmt2);
        var frscAmt6 = rowObject.bgtDgr == '1' ? Number(rowObject.preFrscAmt6) : Number(rowObject.preDefFrscAmt6);

        var investPlan0 = addCommaStr(Number(rowObject.totFrscAmt0) - Number(rowObject.preInvFrscAmt0) - frscAmt0 - Number(rowObject.frscAmt0));
        var investPlan1 = addCommaStr(Number(rowObject.totFrscAmt1) - Number(rowObject.preInvFrscAmt1) - frscAmt1 - Number(rowObject.frscAmt1));
        var investPlan2 = addCommaStr(Number(rowObject.totFrscAmt2) - Number(rowObject.preInvFrscAmt2) - frscAmt2 - Number(rowObject.frscAmt2));
        var investPlan6 = addCommaStr(Number(rowObject.totFrscAmt6) - Number(rowObject.preInvFrscAmt6) - frscAmt6 - Number(rowObject.frscAmt6));
        var investPlanCont = isEmpty(rowObject.investPlanCont) == true ? "" : rowObject.investPlanCont;

        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr>'
                 + '      <td style="border-right: 0px !important;"><input id="investPlanCont_'+rowObject.dgrcompoId+'" value="'+investPlanCont+'" maxlength="300" style="width:98%;" /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td style="border-right: 0px !important;"><input id="investPlan0_'+rowObject.dgrcompoId+'" value="'+investPlan0+'" maxlength="300" class="amtInput0C0 ui-state-disabled" style="width:98%;" readonly/></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td style="border-right: 0px !important;"><input id="investPlan1_'+rowObject.dgrcompoId+'" value="'+investPlan1+'" maxlength="300" class="amtInput0C0 ui-state-disabled" style="width:98%;" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td style="border-right: 0px !important;"><input id="investPlan2_'+rowObject.dgrcompoId+'" value="'+investPlan2+'" maxlength="300" class="amtInput0C0 ui-state-disabled" style="width:98%;" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td style="border-right: 0px !important;"><input id="investPlan6_'+rowObject.dgrcompoId+'" value="'+investPlan6+'" maxlength="300" class="amtInput0C0 ui-state-disabled" style="width:98%;" readonly /></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var srchValFormatter = function(cellValue, options, rowObject){
        var srchVal = isEmpty(rowObject.srchVal) == true ? "" : rowObject.srchVal;
        var detlNm = isEmpty(rowObject.detlNm) == true ? "" : rowObject.detlNm;
        
        var rVal = '<div>'
                 + '<textarea id="srchVal_'+rowObject.dgrcompoId+'" value="'+srchVal+'" style="width:240px;ime-mode:active;height:50px;" ></textarea>'
                 + '<div id="detlNm_'+rowObject.dgrcompoId+'" style="width:98%;" >'+detlNm+'</div>'
                 + '</div>';

        return rVal;
    };
    
    var colNames = ['', '사업명(조서사업명)', '총사업비', '기투자', '본예산', '전년도 투자', '요구액', '조정액', '향후투자', '사업개요', '추진상황', '예산요구', '설명', '검토의견', '보고서구분', '조건검색어',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'reportCd', 'reportDetlCd', 'dgrLevel', 'teBgtCompoId', 'teBgtCompoSeq', 'compoLevel', 'bizNm', 
                    'bizSmryTitle', 'bizSmryCont', 'promCorpTitle', 'promCorpCont', 'demandTitle', 'demandCont', 'explainCont', 'examCont',
                    'totFrscCont', 'preInvFrscCont', 'preDefFrscCont', 'preFrscCont', 'dmnFrscCont', 'frscCont',
                    'reportFg', 'srchVal'
                   ];

    var colModel = [{name : 'edit', index : 'edit', width : 20, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:editFormatter
                        },
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 210, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:dgrcompoNmFormatter
                        },
                        {name : 'totFrscAmt0', index : 'totFrscAmt0', width : 180, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : totFrscAmtFormatter
                        },
                        {name : 'preInvFrscAmt0', index : 'preInvFrscAmt0', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : preInvFrscAmtFormatter
                        },
                        {name : 'preDefFrscAmt0', index : 'preDefFrscAmt0', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : preDefFrscAmtFormatter
                        },
                        {name : 'preFrscAmt0', index : 'preFrscAmt0', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : preFrscAmtFormatter
                        },
                        {name : 'dmnFrscAmt0', index : 'dmnFrscAmt0', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : dmnFrscAmtFormatter
                        },
                        {name : 'frscAmt0', index : 'frscAmt0', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : frscAmtFormatter
                        },
                        {name : 'investPlanView', index : 'investPlanView', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : investPlanViewFormatter
                        },
                        {name : 'bizSmryView', index : 'bizSmryView', width : 450, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : bizSmryFormatter
                        },
                        {name : 'promCorpView', index : 'promCorpView', width : 450, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : promCorpFormatter
                        },
                        {name : 'demandView', index : 'demandView', width : 450, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : demandFormatter
                        },
                        {name : 'explainContView', index : 'bizPeriod', width : 420, sortable : false, fixed : true, align : 'center',
                            formatter : explainContFormatter
                        },
                        {name : 'examContView', index : 'examContView', width : 420, sortable : false, fixed : true, align : 'center',
                            formatter:examContFormatter
                        },
                        {name : 'reportFgView', index : 'reportFgView', width : 100, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:reportFgFormatter
                        },
                        {name : 'srchValView', index : 'srchValView', width : 250, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:srchValFormatter
                        },
                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'reportCd', index : 'reportCd', width : 0, sortable : false, hidden : true},
                        {name : 'reportDetlCd', index : 'reportDetlCd', width : 0, sortable : false, hidden : true},
                        {name : 'dgrLevel', index : 'dgrLevel', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'compoLevel', index : 'compoLevel', width : 0, sortable : false, hidden : true},
                        {name : 'bizNm', index : 'bizNm', width : 0, sortable : false, hidden : true},
                        {name : 'bizSmryTitle', index : 'bizSmryTitle', width : 0, sortable : false, hidden : true},
                        {name : 'bizSmryCont', index : 'bizSmryCont', width : 0, sortable : false, hidden : true},
                        {name : 'promCorpTitle', index : 'promCorpTitle', width : 0, sortable : false, hidden : true},
                        {name : 'promCorpCont', index : 'promCorpCont', width : 0, sortable : false, hidden : true},
                        {name : 'demandTitle', index : 'demandTitle', width : 0, sortable : false, hidden : true},
                        {name : 'demandCont', index : 'demandCont', width : 0, sortable : false, hidden : true},
                        {name : 'explainCont', index : 'explainCont', width : 0, sortable : false, hidden : true},
                        {name : 'examCont', index : 'examCont', width : 0, sortable : false, hidden : true},
                        {name : 'totFrscCont', index : 'totFrscCont', width : 0, sortable : false, hidden : true},
                        {name : 'preInvFrscCont', index : 'preInvFrscCont', width : 0, sortable : false, hidden : true},
                        {name : 'preDefFrscCont', index : 'preDefFrscCont', width : 0, sortable : false, hidden : true},
                        {name : 'preFrscCont', index : 'preFrscCont', width : 0, sortable : false, hidden : true},
                        {name : 'dmnFrscCont', index : 'dmnFrscCont', width : 0, sortable : false, hidden : true},
                        {name : 'frscCont', index : 'frscCont', width : 0, sortable : false, hidden : true},
                        {name : 'reportFg', index : 'reportFg', width : 0, sortable : false, hidden : true},
                        {name : 'srchVal', index : 'srchVal', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 250 ? $("#mainCenter", tabObj).height() - 110 : 250;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#REPORT_WRITE0C0_GRD", $("#"+tabId))) == false){
            $("#REPORT_WRITE0C0_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#REPORT_WRITE0C0_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 225,
        center__onresize: mainBodyResize
    });
    
    var reportWrite0C0Grid = $("#REPORT_WRITE0C0_GRD", tabObj);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        if(data.data.bgtDgr == "1"){
            colModel[4].hidden = true;
            colModel[5].hidden = false;
        }else{
            colModel[4].hidden = false;
            colModel[5].hidden = true;
        }

        $("#REPORT_WRITE0C0_GRD", tabObj).jqGrid('GridUnload');
        reportWrite0C0Grid = $("#REPORT_WRITE0C0_GRD", tabObj);
        reportWrite0C0Grid.csTreeGrid({
            datastr : data,
            height : getGridHeight(),
            colNames : colNames,
            colModel : colModel,
            ExpandColumn : "dgrcompoNm",
            ExpandColClick: false,
            jsonReader : {
                repeatitems : false,
                root : "dataList"
            },
            onSelectRow: function(rowId){
            },
            loadComplete: function() {
                var iColSelYn = getColumnIndexByName ($(this), 'selYn');
                var rows = this.rows;
                for(var i = 0; i < rows.length; i++) {
                    $(rows[i].cells[iColSelYn]).click(function (e) {
                        var checkedRowId = $(e.target).closest('tr')[0].id;
                        
                        setTreeGridChecked(e, reportWrite0C0Grid, $("#REPORT_WRITE0C0_GRD", tabObj)[0].rows, 'dgrLevel');
                        setUpTreeGridCheck(reportWrite0C0Grid, checkedRowId, 'upDgrcompoId');
                    });
                }
                
                $(".amtInput0C0.ui-state-enabled", tabObj).autoNumeric({aPad: false, vMax:'99999999999999999'});
                $(".amtInput0C0.ui-state-enabled", tabObj).click(function () {
                    $(this).select();
                });
                
                $("textarea[id^='bizNm_']").maxlength({max: 500, showFeedback: false});
                $("textarea[id^='bizAmount_']").maxlength({max: 500, showFeedback: false});
                $("textarea[id^='bizPeriod_']").maxlength({max: 500, showFeedback: false});
            }
        });
        
        $("#REPORT_WRITE0C0_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
        $("#saveBtn", $("#"+tabId)).btnChangeState(true);

        data = null;
    };
    
    var getSearchParam = function(){
        var reportCd = $("#condReportCd", tabObj).val();
        var reportDetlCd = $("#condReportDetlCd option:selected", tabObj).val();
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        var fisFgCd = $("#condFisFgCd option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        var deptRankFr = $("#condDeptRankFr", tabObj).val();
        var deptRankTo = $("#condDeptRankTo", tabObj).val();
        var srchVal = $("#condSrchVal", tabObj).val();
        var frscFgCdFr = $("#condFrscFgCdFr", tabObj).val();
        var frscFgCdTo = $("#condFrscFgCdTo", tabObj).val();
        var frscFrCdYn = "N";
        if(isEmpty(frscFgCdFr) == false || isEmpty(frscFgCdTo) == false){
            frscFrCdYn = "Y";
        }
        var amtUnit = $("#condAmtUnit", tabObj).val();
        
        var param = {reportCd : reportCd,
                reportDetlCd : reportDetlCd,
                fisYear : fisYear,
                bgtDgr : bgtDgr,
                fisFgMstCd : fisFgMstCd,
                fisFgCd : fisFgCd,
                officeCd : officeCd,
                deptRankFr : deptRankFr,
                deptRankTo : deptRankTo,
                srchVal : srchVal,
                frscFgCdFr : frscFgCdFr,
                frscFgCdTo : frscFgCdTo,
                frscFrCdYn : frscFrCdYn,
                amtUnit : amtUnit
         };
        
        return param;
    };
    
    var doSearch = function(){

        gridScrollPosition = $("#REPORT_WRITE0C0_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite0C0Report0C0List.do",
            data: getSearchParam(),
            async : true,
            callBack : doSearchCallBack
        });
    };
    
    $("#searchBtn", tabObj).click(function() {
        
        gridScrollPosition = 0;
        
        doSearch();
    });

    var doCondInit = function(){
        var reportCd = $("#condReportCd", tabObj).val();
        condReportDetlCdCreateCombo(reportCd, '');
        
        $("#condFisYear", tabObj).csCreatCombo(comboData, {
            id : 'fisYear',
            groupId : 'ALL',
            selectedValue : '',
            comboType : '',
            comboTypeValue : ''
        });
       
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
        condFisFgMstCdCreateCombo(fisYear, '');
        
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');

        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        condFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');
        
        condFrscFgCdFrCreateCombo(fisYear, '');
        condFrscFgCdToCreateCombo(fisYear, '');
        
        $("#condDeptCdFr", tabObj).val("");
        $("#condDeptNmFr", tabObj).val("");
        $("#condDeptRankFr", tabObj).val("");
        $("#condDeptCdTo", tabObj).val("");
        $("#condDeptNmTo", tabObj).val("");
        $("#condDeptRankTo", tabObj).val("");
        $("#condSrchVal", tabObj).val("");
        
        $("#insertBtn", tabObj).btnChangeState(true);
    };
    
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });
    
    var getSaveDatas = function(gridObject, gridRows){
        var saveDatas = [];
        var saveData = {};
        var rowId;
        var rowData;
        var bizNm = "";
        var bizSmryTitle = "";
        var bizSmryCont = "";
        var promCorpTitle = "";
        var promCorpCont = "";
        var demandTitle = "";
        var demandCont = "";
        var explainCont = "";
        var examCont = "";
        var totFrscCont = "";
        var preInvFrscCont = "";
        var preDefFrscCont = "";
        var preFrscCont = "";
        var dmnFrscCont = "";
        var frscCont = "";
        var investPlan0 = "";
        var investPlan1 = "";
        var investPlan2 = "";
        var investPlan6 = "";
        var investPlanCont = "";
        var reportFg = "";
        var srchVal = "";
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);
            if(isEmpty(rowData.dgrcompoId) == false && rowData.teBgtCompoId != "00000000000"){
                bizNm = $('#bizNm_'+rowId, tabObj).val();
                bizSmryTitle = $('#bizSmryTitle_'+rowId, tabObj).val();
                bizSmryCont = $('#bizSmryCont_'+rowId, tabObj).val();
                promCorpTitle = $('#promCorpTitle_'+rowId, tabObj).val();
                promCorpCont = $('#promCorpCont_'+rowId, tabObj).val();
                demandTitle = $('#demandTitle_'+rowId, tabObj).val();
                demandCont = $('#demandCont_'+rowId, tabObj).val();
                explainCont = $('#explainCont_'+rowId, tabObj).val();
                examCont = $('#examCont_'+rowId, tabObj).val();
                totFrscCont = $('#totFrscCont_'+rowId, tabObj).val();
                preInvFrscCont = $('#preInvFrscCont_'+rowId, tabObj).val();
                preDefFrscCont = $('#preDefFrscCont_'+rowId, tabObj).val();
                preFrscCont = $('#preFrscCont_'+rowId, tabObj).val();
                dmnFrscCont = $('#dmnFrscCont_'+rowId, tabObj).val();
                frscCont = $('#frscCont_'+rowId, tabObj).val();
                investPlan0 = $('#investPlan0_'+rowId, tabObj).val();
                investPlan1 = $('#investPlan1_'+rowId, tabObj).val();
                investPlan2 = $('#investPlan2_'+rowId, tabObj).val();
                investPlan6 = $('#investPlan6_'+rowId, tabObj).val();
                investPlanCont = $('#investPlanCont_'+rowId, tabObj).val();
                reportFg = $('#reportFg_'+rowId, tabObj).val();
                srchVal = $('#srchVal_'+rowId, tabObj).val();

                if(rowData.bizNm != bizNm
                        || rowData.bizSmryTitle != bizSmryTitle
                        || rowData.bizSmryCont != bizSmryCont
                        || rowData.promCorpTitle != promCorpTitle
                        || rowData.promCorpCont != promCorpCont
                        || rowData.demandTitle != demandTitle
                        || rowData.demandCont != demandCont
                        || rowData.explainCont != explainCont
                        || rowData.examCont != examCont
                        || rowData.totFrscCont != totFrscCont
                        || rowData.preInvFrscCont != preInvFrscCont
                        || rowData.preDefFrscCont != preDefFrscCont
                        || rowData.preFrscCont != preFrscCont
                        || rowData.dmnFrscCont != dmnFrscCont
                        || rowData.frscCont != frscCont
                        || rowData.investPlan0 != investPlan0
                        || rowData.investPlan1 != investPlan1
                        || rowData.investPlan2 != investPlan2
                        || rowData.investPlan6 != investPlan6
                        || rowData.investPlanCont != investPlanCont
                        || rowData.reportFg != reportFg
                        || rowData.srchVal != srchVal){
                    
                    saveData = {};
                    saveData["fisYear"] = rowData.fisYear;
                    saveData["bgtDgr"] = rowData.bgtDgr;
                    saveData["reportCd"] = rowData.reportCd;
                    saveData["reportDetlCd"] = rowData.reportDetlCd;
                    saveData["teBgtCompoId"] = rowData.teBgtCompoId;
                    saveData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
                    saveData["bizNm"] = bizNm;
                    saveData["bizSmryTitle"] = bizSmryTitle;
                    saveData["bizSmryCont"] = bizSmryCont;
                    saveData["promCorpTitle"] = promCorpTitle;
                    saveData["promCorpCont"] = promCorpCont;
                    saveData["demandTitle"] = demandTitle;
                    saveData["demandCont"] = demandCont;
                    saveData["explainCont"] = explainCont;
                    saveData["examCont"] = examCont;
                    saveData["totFrscCont"] = totFrscCont;
                    saveData["preInvFrscCont"] = preInvFrscCont;
                    saveData["preDefFrscCont"] = preDefFrscCont;
                    saveData["preFrscCont"] = preFrscCont;
                    saveData["dmnFrscCont"] = dmnFrscCont;
                    saveData["frscCont"] = frscCont;
                    saveData["investPlan0"] = investPlan0;
                    saveData["investPlan1"] = investPlan1;
                    saveData["investPlan2"] = investPlan2;
                    saveData["investPlan6"] = investPlan6;
                    saveData["investPlanCont"] = investPlanCont;
                    saveData["reportFg"] = reportFg;
                    saveData["srchVal"] = srchVal;
                    saveData["srchValYn"] = rowData.srchVal != srchVal ? "Y" : "N";

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
        
        var saveDatas = getSaveDatas(reportWrite0C0Grid, $("#REPORT_WRITE0C0_GRD", tabObj)[0].rows);
        if(isEmpty(saveDatas) == true || saveDatas.length < 1){
            $.csAlert({
                msg : "변경된 자료가 존재하지 않습니다."
            });
            
            return;
        }
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite0C0SaveReport0C0.do",
            data : {saveDatas: saveDatas,
                    amtUnit:$("#condAmtUnit", tabObj).val()
            },
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

    var doInsertCallBack = function(data){
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
    
    var doInsert = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var param = getSearchParam();
        $.csAjaxCall({
            url : "/report/ajaxReportWrite0C0InsertReport0C0.do"
                , data: param
                , async : true
                , callBack : doInsertCallBack
        });
    };
    
    $("#insertBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $.csConfirm({
            msg : "1장보고서를 가져오시겠습니까?",
            callBack : doInsert
        });
    });
    
    $("#saveFileBtn", tabObj).click(function() {

        var param = getSearchParam();
        param["fileNm"] = "1장보고서";
        param["amtUnit"] = "1000000";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite0C0SaveHwp.do"
          , data: param
        });
    });
    
    var doChangeCondFisYear = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
        condFisFgMstCdCreateCombo(fisYear, '');
        doChageCondBgtDgr();
        doChageCondFisFgMstCd();

        condFrscFgCdFrCreateCombo(fisYear, '');
        condFrscFgCdToCreateCombo(fisYear, '');

    };
    
    var doChageCondBgtDgr = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
        doChangeCondOfficeCd();
        
    };
    
    var doChageCondFisFgMstCd = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        condFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');
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
    
    $("#condFisFgMstCd", tabObj).change(function(){
        doChageCondFisFgMstCd();
    });
    
    $("#condOfficeCd", tabObj).change(function(){
        doChangeCondOfficeCd();
    });
    
    $("#condFrscFgCdFr", tabObj).change(function(){
        $("#condFrscFgCdTo", tabObj).val($("#condFrscFgCdFr option:selected", tabObj).val());
    });
    
    var openDialogBgtDeptSelt = function(seltFg){

        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("reportWrite0C0DialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("0C0");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    reportWrite0C0DialogDgrDeptSeltCallBack = function(param){
        if($("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val() == 1){
            $("#condDeptCdFr", tabObj).val(param.deptCd);
            $("#condDeptNmFr", tabObj).val(param.deptNm);
            $("#condDeptRankFr", tabObj).val(param.deptRank);
            $("#condDeptCdTo", tabObj).val(param.deptCd);
            $("#condDeptNmTo", tabObj).val(param.deptNm);
            $("#condDeptRankTo", tabObj).val(param.deptRank);
        }else{
            $("#condDeptCdTo", tabObj).val(param.deptCd);
            $("#condDeptNmTo", tabObj).val(param.deptNm);
            $("#condDeptRankTo", tabObj).val(param.deptRank);
        }
    };
    
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
                      {id : "fisFgMstCd", subQueryId : "FisFgMstCd"},
                      {id : "fisFgCd", subQueryId : "FisFgCd"},
                      {id : "officeCd", subQueryId : "OfficeCd", reportCd: "0C0"},
                      {id : "frscFgCd", subQueryId : "FrscFgCd"},
                      {id : "reportFg", codeId : "RP009"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);
        
    var condReportDetlCdCreateCombo = function(groupId, selectedValue){
        $("#condReportDetlCd", tabObj).csCreatCombo(comboData
                , {id: 'reportDetlCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: ''
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
    
    var condFisFgMstCdCreateCombo = function(groupId, selectedValue){
        $("#condFisFgMstCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgMstCd'
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
    
    var reportFgCreateCombo = function(groupId, selectedValue){
        return getCsComboStr(comboData
                , {id: 'reportFg'
                    , groupId: groupId
                    , selectedValue: selectedValue
                    , comboType: 'S'
                    , comboTypeValue: ''
                    });
    };
    
    var condFrscFgCdFrCreateCombo = function(groupId, selectedValue){
        $("#condFrscFgCdFr", tabObj).csCreatCombo(comboData
                , {id: 'frscFgCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condFrscFgCdToCreateCombo = function(groupId, selectedValue){
        $("#condFrscFgCdTo", tabObj).csCreatCombo(comboData
                , {id: 'frscFgCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
    doCondInit();
});
