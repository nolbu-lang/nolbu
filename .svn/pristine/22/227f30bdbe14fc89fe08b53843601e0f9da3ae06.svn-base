$(document).ready(function() {
    var tabId = _pledgeCopyTabId;
    var tabObj = $("#"+tabId);
    
    var gridScrollPosition = 0;
    var srcGridScrollPosition = 0;
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        return ' style="vertical-align: top;"';
    };
    
    var upDgrcompoNmFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }

        var officeNm = isEmpty(rowObject.officeNm) == true ? "" : rowObject.officeNm;
        var deptNm = isEmpty(rowObject.deptNm) == true ? "" : rowObject.deptNm;
        var dbizNm = isEmpty(rowObject.dbizNm) == true ? "" : rowObject.dbizNm;
        
        return "실국: "+officeNm+"<br>"+"부서: "+deptNm+"<br>"+"세부: "+dbizNm;
    };
    
    var totFrscAmtFormatter = function(cellValue, options, rowObject){
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
                 + '      <td rowspan="6" style="border-right-color:#AAAAAA; border-bottom-color:#ffffff !important;" >재<br>원<br>별<br></td>'
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
                 + '      <td style="border-right-color:#AAAAAA; border-bottom-color:#ffffff !important; " >기타</td>'
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.totFrscAmt6)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';

        return rVal;
    };
    
    var year1FrscAmtFormatter = function(cellValue, options, rowObject){

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
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year1FrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year1FrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year1FrscAmt3)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year1FrscAmt4)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year1FrscAmt5)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.year1FrscAmt6)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var year2FrscAmtFormatter = function(cellValue, options, rowObject){

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
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year2FrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year2FrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year2FrscAmt3)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year2FrscAmt4)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year2FrscAmt5)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.year2FrscAmt6)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var year3FrscAmtFormatter = function(cellValue, options, rowObject){

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
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year3FrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year3FrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year3FrscAmt3)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year3FrscAmt4)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year3FrscAmt5)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.year3FrscAmt6)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var year4FrscAmtFormatter = function(cellValue, options, rowObject){

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
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year4FrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year4FrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year4FrscAmt3)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year4FrscAmt4)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year4FrscAmt5)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.year4FrscAmt6)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var year5FrscAmtFormatter = function(cellValue, options, rowObject){
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
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year5FrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year5FrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year5FrscAmt3)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year5FrscAmt4)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td">'+addCommaStr(rowObject.year5FrscAmt5)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.year5FrscAmt6)+'</td>'
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
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.dmnFrscAmt6)+'</td>'
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
                 + '    <tr class="amtView020Tr" >'
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.frscAmt6)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var colNames = ['', '[실국-부서-세부]', '구분', '총계', '1년차', '2년차', '3년차', '4년차', '5년차', '요구액', '조정액',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'pledgeBizId', 'teBgtCompoId', 'teBgtCompoSeq',
                    'frscAmt1', 'frscAmt2', 'frscAmt3', 'frscAmt4', 'frscAmt5', 'frscAmt6',
                    'totFrscAmt1', 'totFrscAmt2', 'totFrscAmt3', 'totFrscAmt4', 'totFrscAmt5', 'totFrscAmt6',
                    'year1FrscAmt1', 'year1FrscAmt2', 'year1FrscAmt3', 'year1FrscAmt4', 'year1FrscAmt5', 'year1FrscAmt6',
                    'year2FrscAmt1', 'year2FrscAmt2', 'year2FrscAmt3', 'year2FrscAmt4', 'year2FrscAmt5', 'year2FrscAmt6',
                    'year3FrscAmt1', 'year3FrscAmt2', 'year3FrscAmt3', 'year3FrscAmt4', 'year3FrscAmt5', 'year3FrscAmt6',
                    'year4FrscAmt1', 'year4FrscAmt2', 'year4FrscAmt3', 'year4FrscAmt4', 'year4FrscAmt5', 'year4FrscAmt6',
                    'year5FrscAmt1', 'year5FrscAmt2', 'year5FrscAmt3', 'year5FrscAmt4', 'year5FrscAmt5', 'year5FrscAmt6',
                    'pledgeBeginYmd'
                   ];
    
    var colModel = [ {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, frozen: true,
                            formatter: function (cellValue, option, rowObject) {
                                if(rowObject.teBgtCompoId == "00000000000"){
                                    return '<input type="radio" name="radio_' + option.gid + '" style="display:none;" />';
                                }
                                
                                return '<input type="radio" name="radio_' + option.gid + '"  />';
                            }
                        },
                        {name : 'upDgrcompoNm', index : 'upDgrcompoNm', width : 150, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:upDgrcompoNmFormatter
                        },
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 150, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'totFrscAmt0', index : 'totFrscAmt0', width : 200, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : totFrscAmtFormatter
                        },
                        {name : 'year1FrscAmt0', index : 'totFrscAmt0', width : 100, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : year1FrscAmtFormatter
                        },
                        {name : 'year2FrscAmt0', index : 'totFrscAmt0', width : 100, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : year2FrscAmtFormatter
                        },
                        {name : 'year3FrscAmt0', index : 'totFrscAmt0', width : 100, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : year3FrscAmtFormatter
                        },
                        {name : 'year4FrscAmt0', index : 'totFrscAmt0', width : 100, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : year4FrscAmtFormatter
                        },
                        {name : 'year5FrscAmt0', index : 'totFrscAmt0', width : 100, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : year5FrscAmtFormatter
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
                        {name : 'pledgeBizId', index : 'pledgeBizId', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt1', index : 'frscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt2', index : 'frscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt3', index : 'frscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt4', index : 'frscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt5', index : 'frscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt6', index : 'frscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'totFrscAmt1', index : 'totFrscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'totFrscAmt2', index : 'totFrscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'totFrscAmt3', index : 'totFrscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'totFrscAmt4', index : 'totFrscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'totFrscAmt5', index : 'totFrscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'totFrscAmt6', index : 'totFrscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'year1FrscAmt1', index : 'year1FrscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'year1FrscAmt2', index : 'year1FrscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'year1FrscAmt3', index : 'year1FrscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'year1FrscAmt4', index : 'year1FrscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'year1FrscAmt5', index : 'year1FrscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'year1FrscAmt6', index : 'year1FrscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'year2FrscAmt1', index : 'year2FrscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'year2FrscAmt2', index : 'year2FrscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'year2FrscAmt3', index : 'year2FrscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'year2FrscAmt4', index : 'year2FrscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'year2FrscAmt5', index : 'year2FrscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'year2FrscAmt6', index : 'year2FrscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'year3FrscAmt1', index : 'year3FrscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'year3FrscAmt2', index : 'year3FrscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'year3FrscAmt3', index : 'year3FrscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'year3FrscAmt4', index : 'year3FrscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'year3FrscAmt5', index : 'year3FrscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'year3FrscAmt6', index : 'year3FrscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'year4FrscAmt1', index : 'year4FrscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'year4FrscAmt2', index : 'year4FrscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'year4FrscAmt3', index : 'year4FrscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'year4FrscAmt4', index : 'year4FrscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'year4FrscAmt5', index : 'year4FrscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'year4FrscAmt6', index : 'year4FrscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'year5FrscAmt1', index : 'year5FrscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'year5FrscAmt2', index : 'year5FrscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'year5FrscAmt3', index : 'year5FrscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'year5FrscAmt4', index : 'year5FrscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'year5FrscAmt5', index : 'year5FrscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'year5FrscAmt6', index : 'year5FrscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'pledgeBeginYmd', index : 'year5FrscAmt6', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 300 > 150 ? $("#mainCenter", tabObj).height() - 300 : 150;
    };
    
    var mainBodyResize = function(){
        $("#subMainBody", tabObj).width($("#mainCenter", tabObj).width());
        $("#subMainBody", tabObj).layout().resizeAll();
    };
    
    var subMainBodyResize = function(){
        $("#subMainCenterCond", tabObj).width($("#subMainCenter", tabObj).width()-20);
        $("#subMainWestCond", tabObj).width($("#subMainWest", tabObj).width()-20);
        if(isEmpty($("#PLEDGE_COPY_SRC_GRD", tabObj)) == false){
            $("#PLEDGE_COPY_SRC_GRD", tabObj).setGridHeight(getGridHeight());
            $("#PLEDGE_COPY_SRC_GRD", tabObj).setGridWidth($("#subMainWest", tabObj).width());
        }
        
        if(isEmpty($("#PLEDGE_COPY_GRD", tabObj)) == false){
            $("#PLEDGE_COPY_GRD", tabObj).setGridHeight(getGridHeight());
            $("#PLEDGE_COPY_GRD", tabObj).setGridWidth($("#subMainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 100,
        center__onresize: mainBodyResize
    });
    
    $("#subMainBody", tabObj).layout({
        west__size : "50%",
        center__onresize: subMainBodyResize
    });

    subMainBodyResize();
    
    var pledgeCopySrcGridParam = {
            id : "PLEDGE_COPY_SRC",
            colNames : colNames,
            colModel : colModel,
            rowNum : 1000,
            defaultRows: 10
    };
    
    var pledgeCopyGridParam = {
            id : "PLEDGE_COPY",
            colNames : colNames,
            colModel : colModel,
            rowNum : 1000,
            defaultRows: 10
    };
    
    var pledgeCopySrcGrid = $.csGrid(pledgeCopySrcGridParam);
    var pledgeCopyGrid = $.csGrid(pledgeCopyGridParam);
    
    var doSearchSrcCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        pledgeCopySrcGrid.addCsJsonData(data);
        
        $("#PLEDGE_COPY_SRC_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(srcGridScrollPosition);
        subMainBodyResize();
        
        data = null;
    };
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        pledgeCopyGrid.addCsJsonData(data);
        $("#PLEDGE_COPY_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        subMainBodyResize();
        
        data = null;
    };
    
    var doSearchSrc = function(){
        var pledgeBizId = $("#condPledgeBizId option:selected", tabObj).val();
        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condSrcBgtDgr option:selected", tabObj).val();
        var fisFgMstCd = $("#condSrcFisFgMstCd option:selected", tabObj).val();
        var fisFgCd = $("#condSrcFisFgCd option:selected", tabObj).val();
        var officeCd = $("#condSrcOfficeCd option:selected", tabObj).val();
        var deptRankFr = $("#condSrcDeptRankFr", tabObj).val();
        var deptRankTo = $("#condSrcDeptRankTo", tabObj).val();
        var teMngMokCdFr = $("#condSrcTeMngMokCdFr", tabObj).val();
        var teMngMokCdTo = $("#condSrcTeMngMokCdTo", tabObj).val();
        var frscFgCdFr = $("#condSrcFrscFgCdFr", tabObj).val();
        var frscFgCdTo = $("#condSrcFrscFgCdTo", tabObj).val();
        var frscFrCdYn = "N";
        if(isEmpty(frscFgCdFr) == false || isEmpty(frscFgCdTo) == false){
            frscFrCdYn = "Y";
        }
        var amtUnit = $("#condAmtUnit", tabObj).val();
        
        srcGridScrollPosition = $("#PLEDGE_COPY_SRC_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();

        $.csAjaxCall({
            url : "/pledge/ajaxPledgeCopyPledgeList.do",
            data: {pledgeBizId : pledgeBizId,
                   fisYear : fisYear,
                   bgtDgr : bgtDgr,
                   fisFgMstCd : fisFgMstCd,
                   fisFgCd : fisFgCd,
                   officeCd : officeCd,
                   deptRankFr : deptRankFr,
                   deptRankTo : deptRankTo,
                   teMngMokCdFr : teMngMokCdFr,
                   teMngMokCdTo : teMngMokCdTo,
                   frscFgCdFr : frscFgCdFr,
                   frscFgCdTo : frscFgCdTo,
                   frscFrCdYn : frscFrCdYn,
                   amtUnit : amtUnit
            },
            async : true,
            callBack : doSearchSrcCallBack
        });
        
    };
    
    var doSearch = function(){
        var pledgeBizId = $("#condPledgeBizId option:selected", tabObj).val();
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        var fisFgCd = $("#condFisFgCd option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        var deptRankFr = $("#condDeptRankFr", tabObj).val();
        var deptRankTo = $("#condDeptRankTo", tabObj).val();
        var teMngMokCdFr = $("#condTeMngMokCdFr", tabObj).val();
        var teMngMokCdTo = $("#condTeMngMokCdTo", tabObj).val();
        var frscFgCdFr = $("#condFrscFgCdFr", tabObj).val();
        var frscFgCdTo = $("#condFrscFgCdTo", tabObj).val();
        var frscFrCdYn = "N";
        if(isEmpty(frscFgCdFr) == false || isEmpty(frscFgCdTo) == false){
            frscFrCdYn = "Y";
        }
        var amtUnit = $("#condAmtUnit", tabObj).val();

        gridScrollPosition = $("#PLEDGE_COPY_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        $.csAjaxCall({
            url : "/pledge/ajaxPledgeCopyPledgeList.do",
            data: {pledgeBizId : pledgeBizId,
                   fisYear : fisYear,
                   bgtDgr : bgtDgr,
                   fisFgMstCd : fisFgMstCd,
                   fisFgCd : fisFgCd,
                   officeCd : officeCd,
                   deptRankFr : deptRankFr,
                   deptRankTo : deptRankTo,
                   teMngMokCdFr : teMngMokCdFr,
                   teMngMokCdTo : teMngMokCdTo,
                   frscFgCdFr : frscFgCdFr,
                   frscFgCdTo : frscFgCdTo,
                   frscFrCdYn : frscFrCdYn,
                   amtUnit : amtUnit
            },
            async : true,
            callBack : doSearchCallBack
        });
    };

    var doCondSrcInit = function(){
        $("#condSrcFisYear", tabObj).csCreatCombo(comboData, {
            id : 'fisYear',
            groupId : 'ALL',
            selectedValue : '',
            comboType : '',
            comboTypeValue : ''
        });
       
        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        condSrcBgtDgrCreateCombo(fisYear, '');
        condSrcFisFgMstCdCreateCombo(fisYear, '');
        
        var bgtDgr = $("#condSrcBgtDgr option:selected", tabObj).val();
        condSrcOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
        
        var fisFgMstCd = $("#condSrcFisFgMstCd option:selected", tabObj).val();
        condSrcFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');
        
        condSrcTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condSrcTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
        
        condSrcFrscFgCdFrCreateCombo(fisYear, '');
        condSrcFrscFgCdToCreateCombo(fisYear, '');
        
        $("#condSrcDeptCdFr", tabObj).val("");
        $("#condSrcDeptNmFr", tabObj).val("");
        $("#condSrcDeptRankFr", tabObj).val("");
        $("#condSrcDeptCdTo", tabObj).val("");
        $("#condSrcDeptNmTo", tabObj).val("");
        $("#condSrcDeptRankTo", tabObj).val("");
        $("#condSrcTeMngMokCdFr", tabObj).val("");
        $("#condSrcTeMngMokCdTo", tabObj).val("");
        $("#condSrcFrscFgCdFr", tabObj).val("");
        $("#condSrcFrscFgCdTo", tabObj).val("");
    };
    
    $("#condSrcInitBtn", tabObj).click(function() {
        doCondSrcInit();
    });

    var doCondInit = function(){
        $("#condPledgeInfoId", tabObj).csCreatCombo(comboData, {
            id : 'pledgeInfoId',
            groupId : 'ALL',
            selectedValue : '',
            comboType : '',
            comboTypeValue : ''
        });
        
        var condPledgeInfoId = $("#condPledgeInfoId option:selected", tabObj).val();
        condPledgeBizId1CreateCombo(condPledgeInfoId, '');
        
        var condPledgeBizId1 = $("#condPledgeBizId1 option:selected", tabObj).val();
        condPledgeBizId2CreateCombo(condPledgeBizId1, '');
        
        var condPledgeBizId2 = $("#condPledgeBizId2 option:selected", tabObj).val();
        condPledgeBizIdCreateCombo(condPledgeBizId2, '');
        
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
        
        condTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
        
        condFrscFgCdFrCreateCombo(fisYear, '');
        condFrscFgCdToCreateCombo(fisYear, '');
        
        $("#condDeptCdFr", tabObj).val("");
        $("#condDeptNmFr", tabObj).val("");
        $("#condDeptRankFr", tabObj).val("");
        $("#condDeptCdTo", tabObj).val("");
        $("#condDeptNmTo", tabObj).val("");
        $("#condDeptRankTo", tabObj).val("");
        $("#condTeMngMokCdFr", tabObj).val("");
        $("#condTeMngMokCdTo", tabObj).val("");
        $("#condFrscFgCdFr", tabObj).val("");
        $("#condFrscFgCdTo", tabObj).val("");
    };
    
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });
    
    $("#srcSearchBtn", tabObj).click(function() {
        var pledgeBizId1 = $("#condPledgeBizId1 option:selected", tabObj).val();
        if(isEmpty(pledgeBizId1) == true){
            $.csAlert({
                msg : "공약사업구분(대)을 선택하셔야 합니다.",
                callBack : function() {
                    $("#condPledgeBizId1", tabObj).focus();
                }
            });
            
            return;
        }
        
        var pledgeBizId2 = $("#condPledgeBizId2 option:selected", tabObj).val();
        if(isEmpty(pledgeBizId2) == true){
            $.csAlert({
                msg : "공약사업구분(중)을 선택하셔야 합니다.",
                callBack : function() {
                    $("#condPledgeBizId2", tabObj).focus();
                }
            });
            
            return;
        }
        
        var pledgeBizId = $("#condPledgeBizId option:selected", tabObj).val();
        if(isEmpty(pledgeBizId) == true){
            $.csAlert({
                msg : "공약사업구분(소)을 선택하셔야 합니다.",
                callBack : function() {
                    $("#condPledgeBizId", tabObj).focus();
                }
            });
            
            return;
        }

        srcGridScrollPosition = 0;
        
        doSearchSrc();
    });
    
    $("#searchBtn", tabObj).click(function() {
        var pledgeBizId = $("#condPledgeBizId option:selected", tabObj).val();
        if(isEmpty(pledgeBizId) == true){
            $.csAlert({
                msg : "공약사업구분을선택하셔야 합니다.",
                callBack : function() {
                    $("#condPledgeBizId", tabObj).focus();
                }
            });
            
            return;
        }

        gridScrollPosition = 0;
        
        doSearch();
    });

    var getSelectedRowId = function(gridNm){
        var $selRadio = $('input[name=radio_' + gridNm + '_GRD]:checked'), $tr;
        if ($selRadio.length > 0) {
            $tr = $selRadio.closest('tr');
            if ($tr.length > 0) {
                return $tr.attr('id');
            }
        }
            
        return "";
    };
    
    var applyParam = {};
    
    var doApply = function(params){
        if(params.confirmData != "Y"){
            return;
        }
        
        var data = $.csAjaxCall({
            url : "/pledge/ajaxPledgeCopyCopyPledge.do",
            data : applyParam
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
                doSearch();
            }
        });
    };
    
    $("#applyBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        var srcRowId = getSelectedRowId('PLEDGE_COPY_SRC');
        if(isEmpty(srcRowId) == true){
            $.csAlert({
                msg : "적용할 자료를 선택하여 주십시오."
            });
            
            return;
        }
        
        var rowId = getSelectedRowId('PLEDGE_COPY');
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "적용할 대상을 선택하여 주십시오."
            });
            
            return;
        }
        
        var srcRowData = pledgeCopySrcGrid.getRowData(srcRowId);
        var rowData = pledgeCopyGrid.getRowData(rowId);
        if(srcRowData.pledgeBizId != rowData.pledgeBizId){
            $.csAlert({
                msg : "동일한 공약사항만 적용 할 수 있습니다."
            });
            
            return;
        }
        
        var srcFisYear = srcRowData.fisYear;
        var srcBgtDgr = srcRowData.bgtDgr;
        var fisYear = rowData.fisYear;
        var bgtDgr = rowData.bgtDgr;
        var pledgeBeginYmd = rowData.pledgeBeginYmd;
        var pledgeBeginYear = pledgeBeginYmd.substring(0, 4);
        
        var diffYear = Number(fisYear) - Number(srcFisYear);
        if(diffYear < 0){
            $.csAlert({
                msg : "대상회계연도가 자료 회계연도 보다 이전입니다.",
                callBack : function() {
                    $("#condFisYear", tabObj).focus();
                }
            });
            
            return;
        }
        
        if(srcFisYear == fisYear && srcBgtDgr == bgtDgr){
            $.csAlert({
                msg : "동일 회계연도와 예산차수 입니다.",
                callBack : function() {
                    $("#condFisYear", tabObj).focus();
                }
            });
            
            return;
        }
        
        applyParam = {};
        applyParam["srcPledgeBizId"] = srcRowData.pledgeBizId;
        applyParam["srcFisYear"] = srcRowData.fisYear;
        applyParam["srcBgtDgr"] = srcRowData.bgtDgr;
        applyParam["srcTeBgtCompoId"] = srcRowData.teBgtCompoId;
        applyParam["pledgeBizId"] = rowData.pledgeBizId;
        applyParam["fisYear"] = rowData.fisYear;
        applyParam["bgtDgr"] = rowData.bgtDgr;
        applyParam["teBgtCompoId"] = rowData.teBgtCompoId;
        applyParam["amtUnit"] = $("#condAmtUnit", tabObj).val();
        
        applyParam["totFrscAmt1"] = srcRowData.totFrscAmt1;
        applyParam["totFrscAmt2"] = srcRowData.totFrscAmt2;
        applyParam["totFrscAmt3"] = srcRowData.totFrscAmt3;
        applyParam["totFrscAmt4"] = srcRowData.totFrscAmt4;
        applyParam["totFrscAmt5"] = srcRowData.totFrscAmt5;
        applyParam["totFrscAmt6"] = srcRowData.totFrscAmt6;
        
        applyParam["year1FrscAmt1"] = srcRowData.year1FrscAmt1;
        applyParam["year1FrscAmt2"] = srcRowData.year1FrscAmt2;
        applyParam["year1FrscAmt3"] = srcRowData.year1FrscAmt3;
        applyParam["year1FrscAmt4"] = srcRowData.year1FrscAmt4;
        applyParam["year1FrscAmt5"] = srcRowData.year1FrscAmt5;
        applyParam["year1FrscAmt6"] = srcRowData.year1FrscAmt6;
        
        applyParam["year2FrscAmt1"] = srcRowData.year2FrscAmt1;
        applyParam["year2FrscAmt2"] = srcRowData.year2FrscAmt2;
        applyParam["year2FrscAmt3"] = srcRowData.year2FrscAmt3;
        applyParam["year2FrscAmt4"] = srcRowData.year2FrscAmt4;
        applyParam["year2FrscAmt5"] = srcRowData.year2FrscAmt5;
        applyParam["year2FrscAmt6"] = srcRowData.year2FrscAmt6;
        
        applyParam["year3FrscAmt1"] = srcRowData.year3FrscAmt1;
        applyParam["year3FrscAmt2"] = srcRowData.year3FrscAmt2;
        applyParam["year3FrscAmt3"] = srcRowData.year3FrscAmt3;
        applyParam["year3FrscAmt4"] = srcRowData.year3FrscAmt4;
        applyParam["year3FrscAmt5"] = srcRowData.year3FrscAmt5;
        applyParam["year3FrscAmt6"] = srcRowData.year3FrscAmt6;
        
        applyParam["year4FrscAmt1"] = srcRowData.year4FrscAmt1;
        applyParam["year4FrscAmt2"] = srcRowData.year4FrscAmt2;
        applyParam["year4FrscAmt3"] = srcRowData.year4FrscAmt3;
        applyParam["year4FrscAmt4"] = srcRowData.year4FrscAmt4;
        applyParam["year4FrscAmt5"] = srcRowData.year4FrscAmt5;
        applyParam["year4FrscAmt6"] = srcRowData.year4FrscAmt6;
        
        applyParam["year5FrscAmt1"] = srcRowData.year5FrscAmt1;
        applyParam["year5FrscAmt2"] = srcRowData.year5FrscAmt2;
        applyParam["year5FrscAmt3"] = srcRowData.year5FrscAmt3;
        applyParam["year5FrscAmt4"] = srcRowData.year5FrscAmt4;
        applyParam["year5FrscAmt5"] = srcRowData.year5FrscAmt5;
        applyParam["year5FrscAmt6"] = srcRowData.year5FrscAmt6;
        
        //var diffYear = Number(fisYear) - Number(srcFisYear);
        var dgr =  Number(fisYear) - Number(pledgeBeginYear) + 1;
        if(dgr > 0){
            if(fisYear == srcFisYear){
                applyParam["year"+dgr+"FrscAmt1"] = Number(applyParam["year"+dgr+"FrscAmt1"]) + Number(rowData.frscAmt1);
                applyParam["year"+dgr+"FrscAmt2"] = Number(applyParam["year"+dgr+"FrscAmt2"]) + Number(rowData.frscAmt2);
                applyParam["year"+dgr+"FrscAmt3"] = Number(applyParam["year"+dgr+"FrscAmt3"]) + Number(rowData.frscAmt3);
                applyParam["year"+dgr+"FrscAmt4"] = Number(applyParam["year"+dgr+"FrscAmt4"]) + Number(rowData.frscAmt4);
                applyParam["year"+dgr+"FrscAmt5"] = Number(applyParam["year"+dgr+"FrscAmt5"]) + Number(rowData.frscAmt5);
                applyParam["year"+dgr+"FrscAmt6"] = Number(applyParam["year"+dgr+"FrscAmt6"]) + Number(rowData.frscAmt6);
            }else{
                applyParam["year"+dgr+"FrscAmt1"] = rowData.frscAmt1;
                applyParam["year"+dgr+"FrscAmt2"] = rowData.frscAmt2;
                applyParam["year"+dgr+"FrscAmt3"] = rowData.frscAmt3;
                applyParam["year"+dgr+"FrscAmt4"] = rowData.frscAmt4;
                applyParam["year"+dgr+"FrscAmt5"] = rowData.frscAmt5;
                applyParam["year"+dgr+"FrscAmt6"] = rowData.frscAmt6;
            }
        }
        
        $.csConfirm({
            msg : "적용하시겠습니까?",
            callBack : doApply
        });
    });
    
    var doChangeCondPledgeInfoId = function(){
        var pledgeInfoId = $("#condPledgeInfoId option:selected", tabObj).val();
        condPledgeBizId1CreateCombo(pledgeInfoId, '');
    };
    
    var doChangeCondPledgeBizId1 = function(){
        var pledgeBizId1 = $("#condPledgeBizId1 option:selected", tabObj).val();
        condPledgeBizId2CreateCombo(pledgeBizId1, '');
    };
    
    var doChangeCondPledgeBizId2 = function(){
        var pledgeBizId2 = $("#condPledgeBizId2 option:selected", tabObj).val();
        condPledgeBizIdCreateCombo(pledgeBizId2, '');
    };
    
    var doChangeCondSrcFisYear = function(){
        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        condSrcBgtDgrCreateCombo(fisYear, '');
        condSrcFisFgMstCdCreateCombo(fisYear, '');
        doChageCondSrcBgtDgr();
        doChageCondSrcFisFgMstCd();
        
        condSrcFrscFgCdFrCreateCombo(fisYear, '');
        condSrcFrscFgCdToCreateCombo(fisYear, '');
    };
    
    var doChangeCondFisYear = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
        condFisFgMstCdCreateCombo(fisYear, '');
        doChageCondBgtDgr();
        doChageCondFisFgMstCd();
        
        condFrscFgCdFrCreateCombo(fisYear, '');
        condFrscFgCdToCreateCombo(fisYear, '');
    };
    
    var doChageCondSrcBgtDgr = function(){
        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condSrcBgtDgr option:selected", tabObj).val();
        condSrcOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
        doChangeCondSrcOfficeCd();
        
        condSrcTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condSrcTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');

    };
    
    var doChageCondBgtDgr = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
        doChangeCondOfficeCd();
        
        condTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');

    };
    
    var doChageCondSrcFisFgMstCd = function(){
        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        var fisFgMstCd = $("#condSrcFisFgMstCd option:selected", tabObj).val();
        condSrcFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');
    };
    
    var doChageCondFisFgMstCd = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        condFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');
    };
    
    var doChangeCondSrcOfficeCd = function(){
        $("#condSrcDeptCdFr", tabObj).val("");
        $("#condSrcDeptNmFr", tabObj).val("");
        $("#condSrcDeptRankFr", tabObj).val("");
        $("#condSrcDeptCdTo", tabObj).val("");
        $("#condSrcDeptNmTo", tabObj).val("");
        $("#condSrcDeptRankTo", tabObj).val("");
    };
    
    var doChangeCondOfficeCd = function(){
        $("#condDeptCdFr", tabObj).val("");
        $("#condDeptNmFr", tabObj).val("");
        $("#condDeptRankFr", tabObj).val("");
        $("#condDeptCdTo", tabObj).val("");
        $("#condDeptNmTo", tabObj).val("");
        $("#condDeptRankTo", tabObj).val("");
    };
    
    $("#condPledgeInfoId", tabObj).change(function(){
        doChangeCondPledgeInfoId();
    });
    
    $("#condPledgeBizId1", tabObj).change(function(){
        doChangeCondPledgeBizId1();
    });
    
    $("#condPledgeBizId2", tabObj).change(function(){
        doChangeCondPledgeBizId2();
    });
    
    $("#condSrcFisYear", tabObj).change(function(){
        doChangeCondSrcFisYear();
    });
    
    $("#condFisYear", tabObj).change(function(){
        doChangeCondFisYear();
    });
    
    $("#condSrcBgtDgr", tabObj).change(function(){
        doChageCondSrcBgtDgr();
    });
    
    $("#condBgtDgr", tabObj).change(function(){
        doChageCondBgtDgr();
    });
    
    $("#condSrcFisFgMstCd", tabObj).change(function(){
        doChageCondSrcFisFgMstCd();
    });
    
    $("#condFisFgMstCd", tabObj).change(function(){
        doChageCondFisFgMstCd();
    });
    
    $("#condSrcOfficeCd", tabObj).change(function(){
        doChangeCondSrcOfficeCd();
    });
    
    $("#condOfficeCd", tabObj).change(function(){
        doChangeCondOfficeCd();
    });
    
    $("#condSrcTeMngMokCdFr", tabObj).change(function(){
        $("#condSrcTeMngMokCdTo", tabObj).val($("#condSrcTeMngMokCdFr option:selected", tabObj).val());
    });
    
    $("#condTeMngMokCdFr", tabObj).change(function(){
        $("#condTeMngMokCdTo", tabObj).val($("#condTeMngMokCdFr option:selected", tabObj).val());
    });
    
    $("#condSrcFrscFgCdFr", tabObj).change(function(){
        $("#condSrcFrscFgCdTo", tabObj).val($("#condSrcFrscFgCdFr option:selected", tabObj).val());
    });
    
    $("#condFrscFgCdFr", tabObj).change(function(){
        $("#condFrscFgCdTo", tabObj).val($("#condFrscFgCdFr option:selected", tabObj).val());
    });
    
    var openDialogBgtDeptSrcSelt = function(seltFg){

        var fisYear = $("#condSrcFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condSrcBgtDgr option:selected", tabObj).val();
        var officeCd = $("#condSrcOfficeCd option:selected", tabObj).val();
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("pledgeCopyDialogDgrDeptSrcSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("N");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    pledgeCopyDialogDgrDeptSrcSeltCallBack = function(param){
        if($("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val() == 1){
            $("#condSrcDeptCdFr", tabObj).val(param.deptCd);
            $("#condSrcDeptNmFr", tabObj).val(param.deptNm);
            $("#condSrcDeptRankFr", tabObj).val(param.deptRank);
            $("#condSrcDeptCdTo", tabObj).val(param.deptCd);
            $("#condSrcDeptNmTo", tabObj).val(param.deptNm);
            $("#condSrcDeptRankTo", tabObj).val(param.deptRank);
        }else{
            $("#condSrcDeptCdTo", tabObj).val(param.deptCd);
            $("#condSrcDeptNmTo", tabObj).val(param.deptNm);
            $("#condSrcDeptRankTo", tabObj).val(param.deptRank);
        }
    };
    
    var openDialogBgtDeptSelt = function(seltFg){

        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("pledgeCopyDialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("N");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    pledgeCopyDialogDgrDeptSeltCallBack = function(param){
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
    
    $("#openDialogBgtDeptSrcBtnFr", tabObj).click(function(){
        openDialogBgtDeptSrcSelt(1);
    });
    
    $("#openDialogBgtDeptSrcBtnTo", tabObj).click(function(){
        openDialogBgtDeptSrcSelt(2);
    });
    
    $("#openDialogBgtDeptBtnFr", tabObj).click(function(){
        openDialogBgtDeptSelt(1);
    });
    
    $("#openDialogBgtDeptBtnTo", tabObj).click(function(){
        openDialogBgtDeptSelt(2);
    });
    
    var comboParam = [
                      {id : "pledgeInfoId", subQueryId : "PledgeInfoId"},
                      {id : "pledgeBizId", subQueryId : "PledgeBizId"},
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "bgtDgr", subQueryId : "BgtDgr"},
                      {id : "fisFgMstCd", subQueryId : "FisFgMstCd"},
                      {id : "fisFgCd", subQueryId : "FisFgCd"},
                      {id : "officeCd", subQueryId : "OfficeCd", userDeptYn : "N"},
                      {id : "teMngMokCd", subQueryId : "TeMngMokCd"},
                      {id : "frscFgCd", subQueryId : "FrscFgCd"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);
    
    var condPledgeBizId1CreateCombo = function(groupId, selectedValue){
        $("#condPledgeBizId1", tabObj).csCreatCombo(comboData
                , {id: 'pledgeBizId'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'S'
                  , comboTypeValue: ''
                  }
        );
        
        var condPledgeBizId1 = $("#condPledgeBizId1 option:selected", tabObj).val();
        condPledgeBizId2CreateCombo(condPledgeBizId1, '');
    };
        
    var condPledgeBizId2CreateCombo = function(groupId, selectedValue){
        $("#condPledgeBizId2", tabObj).csCreatCombo(comboData
                , {id: 'pledgeBizId'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'S'
                  , comboTypeValue: ''
                  }
        );
        
        var condPledgeBizId2 = $("#condPledgeBizId2 option:selected", tabObj).val();
        condPledgeBizIdCreateCombo(condPledgeBizId2, '');
    };
        
    var condPledgeBizIdCreateCombo = function(groupId, selectedValue){
        $("#condPledgeBizId", tabObj).csCreatCombo(comboData
                , {id: 'pledgeBizId'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'S'
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condSrcBgtDgrCreateCombo = function(groupId, selectedValue){
        $("#condSrcBgtDgr", tabObj).csCreatCombo(comboData
                , {id: 'bgtDgr'
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
    
    var condSrcFisFgMstCdCreateCombo = function(groupId, selectedValue){
        $("#condSrcFisFgMstCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgMstCd'
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
                  , comboType: ''
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condSrcFisFgCdCreateCombo = function(groupId, selectedValue){
        $("#condSrcFisFgCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgCd'
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
    
    var condSrcOfficeCdCreateCombo = function(groupId, selectedValue){
        $("#condSrcOfficeCd", tabObj).csCreatCombo(comboData
                , {id: 'officeCd'
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
    
    var condSrcTeMngMokCdFrCreateCombo = function(groupId, selectedValue){
        $("#condSrcTeMngMokCdFr", tabObj).csCreatCombo(comboData
                , {id: 'teMngMokCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
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
    
    var condSrcTeMngMokCdToCreateCombo = function(groupId, selectedValue){
        $("#condSrcTeMngMokCdTo", tabObj).csCreatCombo(comboData
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
    
    var condSrcFrscFgCdFrCreateCombo = function(groupId, selectedValue){
        $("#condSrcFrscFgCdFr", tabObj).csCreatCombo(comboData
                , {id: 'frscFgCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
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
    
    var condSrcFrscFgCdToCreateCombo = function(groupId, selectedValue){
        $("#condSrcFrscFgCdTo", tabObj).csCreatCombo(comboData
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
    
    doCondSrcInit();
    doCondInit();
    
    $("#applyBtn", tabObj).btnChangeState(true);
});
