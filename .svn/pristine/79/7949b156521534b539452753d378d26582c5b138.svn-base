$(document).ready(function() {
    var tabId = _pledgeReportTabId;
    var tabObj = $("#"+tabId);
    var gridScrollPosition = 0;
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        return ' style="vertical-align: top;"';
    };
    
    var dgrcompoNmFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.teBgtCompoId == "00000000000"){
            return cellValue;
        }
        
        var demandCont = rowObject.demandCont; 
        if(isEmpty(demandCont) == true){
            demandCont = "";
        }
        
        var rVal = '<span id="dgrcompoNmView_'+rowObject.dgrcompoId+'" >' + cellValue + '</span>';
        
        return rVal;
    };
    
    var examContFormatter = function(cellValue, options, rowObject){
        if(rowObject.teBgtCompoId == "00000000000"){
            return cellValue;
        }
        
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var rVal = '<div>'
                 + '<textarea id="examCont_'+rowObject.dgrcompoId+'" style="width:270px;ime-mode:active;height:10px;">'+cellValue+'</textarea>'
                 + '</div>';
        
        return rVal;
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
                 + '      <td style="border-right-color:#ffffff !important; "><input id="totFrscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td rowspan="6" style="border-right-color:#AAAAAA; border-bottom-color:#ffffff !important;" >재<br>원<br>별<br></td>'
                 + '      <td style="border-right-color:#AAAAAA;" >시비</td>'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="totFrscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totFrscAmt1)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#AAAAAA;" >국비</td>'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="totFrscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totFrscAmt2)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#AAAAAA;" >교부세</td>'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="totFrscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totFrscAmt3)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#AAAAAA;" >지방채</td>'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="totFrscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totFrscAmt4)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#AAAAAA;" >채무</td>'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="totFrscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totFrscAmt5)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#AAAAAA; border-bottom-color:#ffffff !important; " >기타</td>'
                 + '      <td style="border-right-color:#ffffff !important; border-bottom-color:#ffffff !important; "><input id="totFrscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totFrscAmt6)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';

        return rVal;
    };
    
    var year1FrscAmtFormatter = function(cellValue, options, rowObject){
        var readOnlyStr = "";
        var classStr = "";
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
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year1FrscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year1FrscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year1FrscAmt1)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year1FrscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year1FrscAmt2)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year1FrscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year1FrscAmt3)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year1FrscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year1FrscAmt4)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year1FrscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year1FrscAmt5)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; border-bottom-color:#ffffff !important; "><input id="year1FrscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year1FrscAmt6)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var year2FrscAmtFormatter = function(cellValue, options, rowObject){
        var readOnlyStr = "";
        var classStr = "";
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
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year2FrscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year2FrscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year2FrscAmt1)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year2FrscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year2FrscAmt2)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year2FrscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year2FrscAmt3)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year2FrscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year2FrscAmt4)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year2FrscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year2FrscAmt5)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; border-bottom-color:#ffffff !important; "><input id="year2FrscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year2FrscAmt6)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var year3FrscAmtFormatter = function(cellValue, options, rowObject){
        var readOnlyStr = "";
        var classStr = "";
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
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year3FrscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year3FrscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year3FrscAmt1)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year3FrscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year3FrscAmt2)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year3FrscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year3FrscAmt3)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year3FrscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year3FrscAmt4)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year3FrscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year3FrscAmt5)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; border-bottom-color:#ffffff !important; "><input id="year3FrscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year3FrscAmt6)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var year4FrscAmtFormatter = function(cellValue, options, rowObject){
        var readOnlyStr = "";
        var classStr = "";
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
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year4FrscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year4FrscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year4FrscAmt1)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year4FrscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year4FrscAmt2)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year4FrscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year4FrscAmt3)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year4FrscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year4FrscAmt4)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year4FrscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year4FrscAmt5)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; border-bottom-color:#ffffff !important; "><input id="year4FrscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year4FrscAmt6)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var year5FrscAmtFormatter = function(cellValue, options, rowObject){
        var readOnlyStr = "";
        var classStr = "";
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
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year5FrscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year5FrscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year5FrscAmt1)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year5FrscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year5FrscAmt2)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year5FrscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year5FrscAmt3)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year5FrscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year5FrscAmt4)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; "><input id="year5FrscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year5FrscAmt5)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td style="border-right-color:#ffffff !important; border-bottom-color:#ffffff !important; "><input id="year5FrscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.year5FrscAmt6)+'" '+classStr+' '+readOnlyStr+' /></td>'
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
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.dmnFrscAmt6)+'</td>'
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
                 + '      <td class="amtView020Td" style="border-bottom-color:#ffffff !important; border-bottom-color:#ffffff !important; ">'+addCommaStr(rowObject.frscAmt6)+'</td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var colNames = ['', 'dgrLevel', '[실국-세세목]', '', '총계', '1년차', '2년차', '3년차', '4년차', '5년차', '요구액', '조정액', '비고',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'pledgeBizId', 'teBgtCompoId', 'teBgtCompoSeq', 'compoLevel', 'examCont',
                    'frscAmt1', 'frscAmt2', 'frscAmt3', 'frscAmt4', 'frscAmt5', 'frscAmt6',
                    'totFrscAmt1', 'totFrscAmt2', 'totFrscAmt3', 'totFrscAmt4', 'totFrscAmt5', 'totFrscAmt6',
                    'year1FrscAmt1', 'year1FrscAmt2', 'year1FrscAmt3', 'year1FrscAmt4', 'year1FrscAmt5', 'year1FrscAmt6',
                    'year2FrscAmt1', 'year2FrscAmt2', 'year2FrscAmt3', 'year2FrscAmt4', 'year2FrscAmt5', 'year2FrscAmt6',
                    'year3FrscAmt1', 'year3FrscAmt2', 'year3FrscAmt3', 'year3FrscAmt4', 'year3FrscAmt5', 'year3FrscAmt6',
                    'year4FrscAmt1', 'year4FrscAmt2', 'year4FrscAmt3', 'year4FrscAmt4', 'year4FrscAmt5', 'year4FrscAmt6',
                    'year5FrscAmt1', 'year5FrscAmt2', 'year5FrscAmt3', 'year5FrscAmt4', 'year5FrscAmt5', 'year5FrscAmt6',
                    'pledgeBeginYmd'
                   ];
    
    var colModel = [ {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, formatter:'checkbox', editoptions:{value:'Y:N'}, formatoptions:{disabled:false}},
                        {name : 'dgrLevel', index : 'dgrLevel', width : 0, sortable : false, hidden : true},
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 260, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:dgrcompoNmFormatter
                        },
                        {name : 'sheetReportYn', index : 'sheetReportYn', width : 20, sortable : false, fixed : true, align : 'center', cellattr: myCellattr},
                        {name : 'totFrscAmt0', index : 'totFrscAmt0', width : 160, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : totFrscAmtFormatter
                        },
                        {name : 'year1FrscAmt0', index : 'totFrscAmt0', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : year1FrscAmtFormatter
                        },
                        {name : 'year2FrscAmt0', index : 'totFrscAmt0', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : year2FrscAmtFormatter
                        },
                        {name : 'year3FrscAmt0', index : 'totFrscAmt0', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : year3FrscAmtFormatter
                        },
                        {name : 'year4FrscAmt0', index : 'totFrscAmt0', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : year4FrscAmtFormatter
                        },
                        {name : 'year5FrscAmt0', index : 'totFrscAmt0', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : year5FrscAmtFormatter
                        },
                        {name : 'dmnFrscAmt0', index : 'dmnFrscAmt0', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : dmnFrscAmtFormatter
                        },
                        {name : 'frscAmt0', index : 'frscAmt0', width : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : frscAmtFormatter
                        },
                        {name : 'examContView', index : 'examContView', width : 290, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:examContFormatter
                        },
                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'pledgeBizId', index : 'pledgeBizId', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'compoLevel', index : 'compoLevel', width : 0, sortable : false, hidden : true},
                        {name : 'examCont', index : 'examCont', width : 0, sortable : false, hidden : true},
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
        return $("#mainCenter", tabObj).height() - 110 > 200 ? $("#mainCenter", tabObj).height() - 110 : 200;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#PLEDGE_REPORT_GRD", $("#"+tabId))) == false){
            $("#PLEDGE_REPORT_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#PLEDGE_REPORT_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 175,
        center__onresize: mainBodyResize
    });
    
    var setSumAmt = function(amtObj){
        var objId = amtObj.attr("id");
        
        if(isEmpty(objId) == true){
            return;
        }
        
        if(objId.indexOf("Frsc") > 0){
            setSumFrscAmt(objId);
        }
    };
    
    var setTotFrscAmt = function(dgrcompoId){
        var totFrscAmt0 = Number($("#year1FrscAmt0"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year2FrscAmt0"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year3FrscAmt0"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year4FrscAmt0"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year5FrscAmt0"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""));
        $("#totFrscAmt0"+"_"+dgrcompoId, tabObj).val(addCommaStr(totFrscAmt0));
        
        var totFrscAmt1 = Number($("#year1FrscAmt1"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year2FrscAmt1"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year3FrscAmt1"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year4FrscAmt1"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year5FrscAmt1"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""));
        $("#totFrscAmt1"+"_"+dgrcompoId, tabObj).val(addCommaStr(totFrscAmt1));
        
        var totFrscAmt2 = Number($("#year1FrscAmt2"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year2FrscAmt2"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year3FrscAmt2"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year4FrscAmt2"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year5FrscAmt2"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""));
        $("#totFrscAmt2"+"_"+dgrcompoId, tabObj).val(addCommaStr(totFrscAmt2));
        
        var totFrscAmt3 = Number($("#year1FrscAmt3"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year2FrscAmt3"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year3FrscAmt3"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year4FrscAmt3"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year5FrscAmt3"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""));
        $("#totFrscAmt3"+"_"+dgrcompoId, tabObj).val(addCommaStr(totFrscAmt3));
        
        var totFrscAmt4 = Number($("#year1FrscAmt4"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year2FrscAmt4"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year3FrscAmt4"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year4FrscAmt4"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year5FrscAmt4"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""));
        $("#totFrscAmt4"+"_"+dgrcompoId, tabObj).val(addCommaStr(totFrscAmt4));
        
        var totFrscAmt5 = Number($("#year1FrscAmt5"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year2FrscAmt5"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year3FrscAmt5"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year4FrscAmt5"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year5FrscAmt5"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""));
        $("#totFrscAmt5"+"_"+dgrcompoId, tabObj).val(addCommaStr(totFrscAmt5));
        
        var totFrscAmt6 = Number($("#year1FrscAmt6"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year2FrscAmt6"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year3FrscAmt6"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year4FrscAmt6"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                        + Number($("#year5FrscAmt6"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""));
        $("#totFrscAmt6"+"_"+dgrcompoId, tabObj).val(addCommaStr(totFrscAmt6));
    };
    
    var setSumFrscAmt = function(objId){
        var idIndex = objId.indexOf("_");
        if(idIndex < 1){
            return;
        }
        
        var preFrscId = objId.substring(0, idIndex - 1);
        var dgrcompoId = objId.substring(idIndex + 1);
        
        if(isEmpty(preFrscId) == true || isEmpty(dgrcompoId) == true){
            return;
        }
        
        var sumFrscAmt = Number($("#"+preFrscId+"1"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                       + Number($("#"+preFrscId+"2"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                       + Number($("#"+preFrscId+"3"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                       + Number($("#"+preFrscId+"4"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                       + Number($("#"+preFrscId+"5"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                       + Number($("#"+preFrscId+"6"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""));
        $("#"+preFrscId+"0"+"_"+dgrcompoId, tabObj).val(addCommaStr(sumFrscAmt));
        
        setTotFrscAmt(dgrcompoId);
        
    };
    
    var pledgeReportGrid = $("#PLEDGE_REPORT_GRD", tabObj);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        $("#PLEDGE_REPORT_GRD", tabObj).jqGrid('GridUnload');
        pledgeReportGrid = $("#PLEDGE_REPORT_GRD", tabObj);
        pledgeReportGrid.csTreeGrid({
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
            },
            loadComplete: function() {
                $('textarea', tabObj).autogrow();
                $('textarea', tabObj).keyup();
                $('textarea').maxlength({max: 1000, showFeedback: false});
                
                $(".amtInput020.ui-state-enabled", tabObj).autoNumeric({aPad: false, vMax:'99999999999999999', vMin:'-99999999999999999'});
                $(".amtInput020.ui-state-enabled", tabObj).click(function () {
                    $(this).select();
                });
                $(".amtInput020.ui-state-enabled", tabObj).focusout(function(){
                    setSumAmt($(this));
                });

                var iColSelYn = getColumnIndexByName ($(this), 'selYn');
                var rows = this.rows;
                for(var i = 0; i < rows.length; i++) {
                    $(rows[i].cells[iColSelYn]).click(function (e) {
                        var checkedRowId = $(e.target).closest('tr')[0].id;
                        
                        setTreeGridChecked(e, pledgeReportGrid, $("#PLEDGE_REPORT_GRD", tabObj)[0].rows, 'dgrLevel');
                        setUpTreeGridCheck(pledgeReportGrid, checkedRowId, 'upDgrcompoId');
                    });
                }
            }
        });
        
        $("#PLEDGE_REPORT_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
        $("#setBtn", $("#"+tabId)).btnChangeState(true);
        $("#resetBtn", $("#"+tabId)).btnChangeState(true);
        $("#saveBtn", $("#"+tabId)).btnChangeState(true);

        data = null;
    };
    
    var getSearchParam = function(){
        var pledgeInfoId = $("#condPledgeInfoId option:selected", tabObj).val();
        var pledgeBizId1 = $("#condPledgeBizId1 option:selected", tabObj).val();
        var pledgeBizId2 = $("#condPledgeBizId2 option:selected", tabObj).val();
        var pledgeBizId = $("#condPledgeBizId option:selected", tabObj).val();
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var sheetReportYn = $("#condSheetReportYn option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        var deptRankFr = $("#condDeptRankFr", tabObj).val();
        var deptRankTo = $("#condDeptRankTo", tabObj).val();
        var teMngMokCdFr = $("#condTeMngMokCdFr", tabObj).val();
        var teMngMokCdTo = $("#condTeMngMokCdTo", tabObj).val();
        var amtUnit = $("#condAmtUnit", tabObj).val();
        
        var param = {pledgeInfoId : pledgeInfoId,
                pledgeBizId1 : pledgeBizId1,
                pledgeBizId2 : pledgeBizId2,
                pledgeBizId : pledgeBizId,
                fisYear : fisYear,
                bgtDgr : bgtDgr,
                sheetReportYn : sheetReportYn, 
                officeCd : officeCd,
                deptRankFr : deptRankFr,
                deptRankTo : deptRankTo,
                teMngMokCdFr : teMngMokCdFr,
                teMngMokCdTo : teMngMokCdTo,
                amtUnit : amtUnit,
                pledgeYn : "Y"
         };
        
        return param;
    };
    
    var doSearch = function(){

        var pledgeBizId1 = $("#condPledgeBizId1 option:selected", tabObj).val();
        var pledgeBizId2 = $("#condPledgeBizId2 option:selected", tabObj).val();
        var pledgeBizId = $("#condPledgeBizId option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        
        if(isEmpty(pledgeBizId1) == true && isEmpty(officeCd) == true){
            $.csAlert({
                msg : "공약사업구분(대)이 전체일 경우는 실국을 선택하셔야 합니다.",
                callBack : function() {
                    $("#condOfficeCd", tabObj).focus();
                }
            });
            
            return;
        }
        
        if(isEmpty(pledgeBizId2) == true && isEmpty(officeCd) == true){
            $.csAlert({
                msg : "공약사업구분(중)이 전체일 경우는 실국을 선택하셔야 합니다.",
                callBack : function() {
                    $("#condOfficeCd", tabObj).focus();
                }
            });
            
            return;
        }
        
        if(isEmpty(pledgeBizId) == true && isEmpty(officeCd) == true){
            $.csAlert({
                msg : "공약사업구분(소)이 전체일 경우는 실국을 선택하셔야 합니다.",
                callBack : function() {
                    $("#condOfficeCd", tabObj).focus();
                }
            });
            
            return;
        }

        gridScrollPosition = $("#PLEDGE_REPORT_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        $.csAjaxCall({
            url : "/pledge/ajaxPledgeReportPledgeList.do",
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
        
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
        
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
    
    var doSetData = function(params){
        if(params.confirmData != "Y"){
            return;
        }
        
        var gridObject = pledgeReportGrid;
        var gridRows = $("#PLEDGE_REPORT_GRD", tabObj)[0].rows;

        var rowId;
        var rowData;

        var yearFg = $("#yearFg option:selected", tabObj).val();
        var frscAmt0 = "0";
        var frscAmt1 = "0";
        var frscAmt2 = "0";
        var frscAmt3 = "0";
        var frscAmt4 = "0";
        var frscAmt5 = "0";
        var frscAmt6 = "0";

        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);

            if(rowData.selYn == "Y"){
                frscAmt1 = rowData.frscAmt1.replaceAll(",", "");
                frscAmt2 = rowData.frscAmt2.replaceAll(",", "");
                frscAmt3 = rowData.frscAmt3.replaceAll(",", "");
                frscAmt4 = rowData.frscAmt4.replaceAll(",", "");
                frscAmt5 = rowData.frscAmt5.replaceAll(",", "");
                frscAmt6 = rowData.frscAmt6.replaceAll(",", "");
                frscAmt0 = Number(frscAmt1) + Number(frscAmt2) + Number(frscAmt3) + Number(frscAmt4) + Number(frscAmt5) + Number(frscAmt6);

                if(yearFg == "all" || yearFg == "1"){
                    $("#year1FrscAmt0"+"_"+rowId, tabObj).val(addCommaStr(frscAmt0));
                    $("#year1FrscAmt1"+"_"+rowId, tabObj).val(addCommaStr(frscAmt1));
                    $("#year1FrscAmt2"+"_"+rowId, tabObj).val(addCommaStr(frscAmt2));
                    $("#year1FrscAmt3"+"_"+rowId, tabObj).val(addCommaStr(frscAmt3));
                    $("#year1FrscAmt4"+"_"+rowId, tabObj).val(addCommaStr(frscAmt4));
                    $("#year1FrscAmt5"+"_"+rowId, tabObj).val(addCommaStr(frscAmt5));
                    $("#year1FrscAmt6"+"_"+rowId, tabObj).val(addCommaStr(frscAmt6));
                }
                
                if(yearFg == "all" || yearFg == "2"){
                    $("#year2FrscAmt0"+"_"+rowId, tabObj).val(addCommaStr(frscAmt0));
                    $("#year2FrscAmt1"+"_"+rowId, tabObj).val(addCommaStr(frscAmt1));
                    $("#year2FrscAmt2"+"_"+rowId, tabObj).val(addCommaStr(frscAmt2));
                    $("#year2FrscAmt3"+"_"+rowId, tabObj).val(addCommaStr(frscAmt3));
                    $("#year2FrscAmt4"+"_"+rowId, tabObj).val(addCommaStr(frscAmt4));
                    $("#year2FrscAmt5"+"_"+rowId, tabObj).val(addCommaStr(frscAmt5));
                    $("#year2FrscAmt6"+"_"+rowId, tabObj).val(addCommaStr(frscAmt6));
                }
                
                if(yearFg == "all" || yearFg == "3"){
                    $("#year3FrscAmt0"+"_"+rowId, tabObj).val(addCommaStr(frscAmt0));
                    $("#year3FrscAmt1"+"_"+rowId, tabObj).val(addCommaStr(frscAmt1));
                    $("#year3FrscAmt2"+"_"+rowId, tabObj).val(addCommaStr(frscAmt2));
                    $("#year3FrscAmt3"+"_"+rowId, tabObj).val(addCommaStr(frscAmt3));
                    $("#year3FrscAmt4"+"_"+rowId, tabObj).val(addCommaStr(frscAmt4));
                    $("#year3FrscAmt5"+"_"+rowId, tabObj).val(addCommaStr(frscAmt5));
                    $("#year3FrscAmt6"+"_"+rowId, tabObj).val(addCommaStr(frscAmt6));
                }
                
                if(yearFg == "all" || yearFg == "4"){
                    $("#year4FrscAmt0"+"_"+rowId, tabObj).val(addCommaStr(frscAmt0));
                    $("#year4FrscAmt1"+"_"+rowId, tabObj).val(addCommaStr(frscAmt1));
                    $("#year4FrscAmt2"+"_"+rowId, tabObj).val(addCommaStr(frscAmt2));
                    $("#year4FrscAmt3"+"_"+rowId, tabObj).val(addCommaStr(frscAmt3));
                    $("#year4FrscAmt4"+"_"+rowId, tabObj).val(addCommaStr(frscAmt4));
                    $("#year4FrscAmt5"+"_"+rowId, tabObj).val(addCommaStr(frscAmt5));
                    $("#year4FrscAmt6"+"_"+rowId, tabObj).val(addCommaStr(frscAmt6));
                }
                
                if(yearFg == "all" || yearFg == "5"){
                    $("#year5FrscAmt0"+"_"+rowId, tabObj).val(addCommaStr(frscAmt0));
                    $("#year5FrscAmt1"+"_"+rowId, tabObj).val(addCommaStr(frscAmt1));
                    $("#year5FrscAmt2"+"_"+rowId, tabObj).val(addCommaStr(frscAmt2));
                    $("#year5FrscAmt3"+"_"+rowId, tabObj).val(addCommaStr(frscAmt3));
                    $("#year5FrscAmt4"+"_"+rowId, tabObj).val(addCommaStr(frscAmt4));
                    $("#year5FrscAmt5"+"_"+rowId, tabObj).val(addCommaStr(frscAmt5));
                    $("#year5FrscAmt6"+"_"+rowId, tabObj).val(addCommaStr(frscAmt6));
                }
                
                setTotFrscAmt(rowId);
            }
            
        }
    };
    

    
    var doResetData = function(params){
        
        if(params.confirmData != "Y"){
            return;
        }
        
        var gridObject = pledgeReportGrid;
        var gridRows = $("#PLEDGE_REPORT_GRD", tabObj)[0].rows;

        var rowId;
        var rowData;

        var yearFg = $("#yearFg option:selected", tabObj).val();
        var frscAmt0 = "0";
        var frscAmt1 = "0";
        var frscAmt2 = "0";
        var frscAmt3 = "0";
        var frscAmt4 = "0";
        var frscAmt5 = "0";
        var frscAmt6 = "0";

        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);

            if(rowData.selYn == "Y"){

                if(yearFg == "all" || yearFg == "1"){
                    $("#year1FrscAmt0"+"_"+rowId, tabObj).val(addCommaStr(frscAmt0));
                    $("#year1FrscAmt1"+"_"+rowId, tabObj).val(addCommaStr(frscAmt1));
                    $("#year1FrscAmt2"+"_"+rowId, tabObj).val(addCommaStr(frscAmt2));
                    $("#year1FrscAmt3"+"_"+rowId, tabObj).val(addCommaStr(frscAmt3));
                    $("#year1FrscAmt4"+"_"+rowId, tabObj).val(addCommaStr(frscAmt4));
                    $("#year1FrscAmt5"+"_"+rowId, tabObj).val(addCommaStr(frscAmt5));
                    $("#year1FrscAmt6"+"_"+rowId, tabObj).val(addCommaStr(frscAmt6));
                }
                
                if(yearFg == "all" || yearFg == "2"){
                    $("#year2FrscAmt0"+"_"+rowId, tabObj).val(addCommaStr(frscAmt0));
                    $("#year2FrscAmt1"+"_"+rowId, tabObj).val(addCommaStr(frscAmt1));
                    $("#year2FrscAmt2"+"_"+rowId, tabObj).val(addCommaStr(frscAmt2));
                    $("#year2FrscAmt3"+"_"+rowId, tabObj).val(addCommaStr(frscAmt3));
                    $("#year2FrscAmt4"+"_"+rowId, tabObj).val(addCommaStr(frscAmt4));
                    $("#year2FrscAmt5"+"_"+rowId, tabObj).val(addCommaStr(frscAmt5));
                    $("#year2FrscAmt6"+"_"+rowId, tabObj).val(addCommaStr(frscAmt6));
                }
                
                if(yearFg == "all" || yearFg == "3"){
                    $("#year3FrscAmt0"+"_"+rowId, tabObj).val(addCommaStr(frscAmt0));
                    $("#year3FrscAmt1"+"_"+rowId, tabObj).val(addCommaStr(frscAmt1));
                    $("#year3FrscAmt2"+"_"+rowId, tabObj).val(addCommaStr(frscAmt2));
                    $("#year3FrscAmt3"+"_"+rowId, tabObj).val(addCommaStr(frscAmt3));
                    $("#year3FrscAmt4"+"_"+rowId, tabObj).val(addCommaStr(frscAmt4));
                    $("#year3FrscAmt5"+"_"+rowId, tabObj).val(addCommaStr(frscAmt5));
                    $("#year3FrscAmt6"+"_"+rowId, tabObj).val(addCommaStr(frscAmt6));
                }
                
                if(yearFg == "all" || yearFg == "4"){
                    $("#year4FrscAmt0"+"_"+rowId, tabObj).val(addCommaStr(frscAmt0));
                    $("#year4FrscAmt1"+"_"+rowId, tabObj).val(addCommaStr(frscAmt1));
                    $("#year4FrscAmt2"+"_"+rowId, tabObj).val(addCommaStr(frscAmt2));
                    $("#year4FrscAmt3"+"_"+rowId, tabObj).val(addCommaStr(frscAmt3));
                    $("#year4FrscAmt4"+"_"+rowId, tabObj).val(addCommaStr(frscAmt4));
                    $("#year4FrscAmt5"+"_"+rowId, tabObj).val(addCommaStr(frscAmt5));
                    $("#year4FrscAmt6"+"_"+rowId, tabObj).val(addCommaStr(frscAmt6));
                }
                
                if(yearFg == "all" || yearFg == "5"){
                    $("#year5FrscAmt0"+"_"+rowId, tabObj).val(addCommaStr(frscAmt0));
                    $("#year5FrscAmt1"+"_"+rowId, tabObj).val(addCommaStr(frscAmt1));
                    $("#year5FrscAmt2"+"_"+rowId, tabObj).val(addCommaStr(frscAmt2));
                    $("#year5FrscAmt3"+"_"+rowId, tabObj).val(addCommaStr(frscAmt3));
                    $("#year5FrscAmt4"+"_"+rowId, tabObj).val(addCommaStr(frscAmt4));
                    $("#year5FrscAmt5"+"_"+rowId, tabObj).val(addCommaStr(frscAmt5));
                    $("#year5FrscAmt6"+"_"+rowId, tabObj).val(addCommaStr(frscAmt6));
                }
                
                setTotFrscAmt(rowId);
            }
            
        }
    };
    
    $("#setBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $.csConfirm({
            msg : "저장하셔야 최종 적용됩니다. 적용하시겠습니까?",
            callBack : doSetData
        });
    });
    
    $("#resetBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $.csConfirm({
            msg : "저장하셔야 최종 적용됩니다. 초기화하시겠습니까?",
            callBack : doResetData
        });
    });
    
    var getSaveDatas = function(gridObject, gridRows){
        var saveDatas = [];
        var saveData = {};
        var rowId;
        var rowData;
        var examCont = "";
        var totFrscAmt1 = "";
        var totFrscAmt2 = "";
        var totFrscAmt3 = "";
        var totFrscAmt4 = "";
        var totFrscAmt5 = "";
        var totFrscAmt6 = "";
        var year1FrscAmt1 = "";
        var year1FrscAmt2 = "";
        var year1FrscAmt3 = "";
        var year1FrscAmt4 = "";
        var year1FrscAmt5 = "";
        var year1FrscAmt6 = "";
        var year2FrscAmt1 = "";
        var year2FrscAmt2 = "";
        var year2FrscAmt3 = "";
        var year2FrscAmt4 = "";
        var year2FrscAmt5 = "";
        var year2FrscAmt6 = "";
        var year3FrscAmt1 = "";
        var year3FrscAmt2 = "";
        var year3FrscAmt3 = "";
        var year3FrscAmt4 = "";
        var year3FrscAmt5 = "";
        var year3FrscAmt6 = "";
        var year4FrscAmt1 = "";
        var year4FrscAmt2 = "";
        var year4FrscAmt3 = "";
        var year4FrscAmt4 = "";
        var year4FrscAmt5 = "";
        var year4FrscAmt6 = "";
        var year5FrscAmt1 = "";
        var year5FrscAmt2 = "";
        var year5FrscAmt3 = "";
        var year5FrscAmt4 = "";
        var year5FrscAmt5 = "";
        var year5FrscAmt6 = "";
        
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);
            if(isEmpty(rowData.dgrcompoId) == false && rowData.teBgtCompoId != "00000000000"){
                examCont = $('#examCont_'+rowId, tabObj).val().trim();
                totFrscAmt1 = $('#totFrscAmt1_'+rowId, tabObj).val().replaceAll(",", "");
                totFrscAmt2 = $('#totFrscAmt2_'+rowId, tabObj).val().replaceAll(",", "");
                totFrscAmt3 = $('#totFrscAmt3_'+rowId, tabObj).val().replaceAll(",", "");
                totFrscAmt4 = $('#totFrscAmt4_'+rowId, tabObj).val().replaceAll(",", "");
                totFrscAmt5 = $('#totFrscAmt5_'+rowId, tabObj).val().replaceAll(",", "");
                totFrscAmt6 = $('#totFrscAmt6_'+rowId, tabObj).val().replaceAll(",", "");
                year1FrscAmt1 = $('#year1FrscAmt1_'+rowId, tabObj).val().replaceAll(",", "");
                year1FrscAmt2 = $('#year1FrscAmt2_'+rowId, tabObj).val().replaceAll(",", "");
                year1FrscAmt3 = $('#year1FrscAmt3_'+rowId, tabObj).val().replaceAll(",", "");
                year1FrscAmt4 = $('#year1FrscAmt4_'+rowId, tabObj).val().replaceAll(",", "");
                year1FrscAmt5 = $('#year1FrscAmt5_'+rowId, tabObj).val().replaceAll(",", "");
                year1FrscAmt6 = $('#year1FrscAmt6_'+rowId, tabObj).val().replaceAll(",", "");
                year2FrscAmt1 = $('#year2FrscAmt1_'+rowId, tabObj).val().replaceAll(",", "");
                year2FrscAmt2 = $('#year2FrscAmt2_'+rowId, tabObj).val().replaceAll(",", "");
                year2FrscAmt3 = $('#year2FrscAmt3_'+rowId, tabObj).val().replaceAll(",", "");
                year2FrscAmt4 = $('#year2FrscAmt4_'+rowId, tabObj).val().replaceAll(",", "");
                year2FrscAmt5 = $('#year2FrscAmt5_'+rowId, tabObj).val().replaceAll(",", "");
                year2FrscAmt6 = $('#year2FrscAmt6_'+rowId, tabObj).val().replaceAll(",", "");
                year3FrscAmt1 = $('#year3FrscAmt1_'+rowId, tabObj).val().replaceAll(",", "");
                year3FrscAmt2 = $('#year3FrscAmt2_'+rowId, tabObj).val().replaceAll(",", "");
                year3FrscAmt3 = $('#year3FrscAmt3_'+rowId, tabObj).val().replaceAll(",", "");
                year3FrscAmt4 = $('#year3FrscAmt4_'+rowId, tabObj).val().replaceAll(",", "");
                year3FrscAmt5 = $('#year3FrscAmt5_'+rowId, tabObj).val().replaceAll(",", "");
                year3FrscAmt6 = $('#year3FrscAmt6_'+rowId, tabObj).val().replaceAll(",", "");
                year4FrscAmt1 = $('#year4FrscAmt1_'+rowId, tabObj).val().replaceAll(",", "");
                year4FrscAmt2 = $('#year4FrscAmt2_'+rowId, tabObj).val().replaceAll(",", "");
                year4FrscAmt3 = $('#year4FrscAmt3_'+rowId, tabObj).val().replaceAll(",", "");
                year4FrscAmt4 = $('#year4FrscAmt4_'+rowId, tabObj).val().replaceAll(",", "");
                year4FrscAmt5 = $('#year4FrscAmt5_'+rowId, tabObj).val().replaceAll(",", "");
                year4FrscAmt6 = $('#year4FrscAmt6_'+rowId, tabObj).val().replaceAll(",", "");
                year5FrscAmt1 = $('#year5FrscAmt1_'+rowId, tabObj).val().replaceAll(",", "");
                year5FrscAmt2 = $('#year5FrscAmt2_'+rowId, tabObj).val().replaceAll(",", "");
                year5FrscAmt3 = $('#year5FrscAmt3_'+rowId, tabObj).val().replaceAll(",", "");
                year5FrscAmt4 = $('#year5FrscAmt4_'+rowId, tabObj).val().replaceAll(",", "");
                year5FrscAmt5 = $('#year5FrscAmt5_'+rowId, tabObj).val().replaceAll(",", "");
                year5FrscAmt6 = $('#year5FrscAmt6_'+rowId, tabObj).val().replaceAll(",", "");
                
                if(rowData.examCont != examCont
                        || rowData.totFrscAmt1 != totFrscAmt1
                        || rowData.totFrscAmt2 != totFrscAmt2
                        || rowData.totFrscAmt3 != totFrscAmt3
                        || rowData.totFrscAmt4 != totFrscAmt4
                        || rowData.totFrscAmt5 != totFrscAmt5
                        || rowData.totFrscAmt6 != totFrscAmt6
                        || rowData.year1FrscAmt1 != year1FrscAmt1
                        || rowData.year1FrscAmt2 != year1FrscAmt2
                        || rowData.year1FrscAmt3 != year1FrscAmt3
                        || rowData.year1FrscAmt4 != year1FrscAmt4
                        || rowData.year1FrscAmt5 != year1FrscAmt5
                        || rowData.year1FrscAmt6 != year1FrscAmt6
                        || rowData.year2FrscAmt1 != year2FrscAmt1
                        || rowData.year2FrscAmt2 != year2FrscAmt2
                        || rowData.year2FrscAmt3 != year2FrscAmt3
                        || rowData.year2FrscAmt4 != year2FrscAmt4
                        || rowData.year2FrscAmt5 != year2FrscAmt5
                        || rowData.year2FrscAmt6 != year2FrscAmt6
                        || rowData.year3FrscAmt1 != year3FrscAmt1
                        || rowData.year3FrscAmt2 != year3FrscAmt2
                        || rowData.year3FrscAmt3 != year3FrscAmt3
                        || rowData.year3FrscAmt4 != year3FrscAmt4
                        || rowData.year3FrscAmt5 != year3FrscAmt5
                        || rowData.year3FrscAmt6 != year3FrscAmt6
                        || rowData.year4FrscAmt1 != year4FrscAmt1
                        || rowData.year4FrscAmt2 != year4FrscAmt2
                        || rowData.year4FrscAmt3 != year4FrscAmt3
                        || rowData.year4FrscAmt4 != year4FrscAmt4
                        || rowData.year4FrscAmt5 != year4FrscAmt5
                        || rowData.year4FrscAmt6 != year4FrscAmt6
                        || rowData.year5FrscAmt1 != year5FrscAmt1
                        || rowData.year5FrscAmt2 != year5FrscAmt2
                        || rowData.year5FrscAmt3 != year5FrscAmt3
                        || rowData.year5FrscAmt4 != year5FrscAmt4
                        || rowData.year5FrscAmt5 != year5FrscAmt5
                        || rowData.year5FrscAmt6 != year5FrscAmt6){
                    
                    saveData = {};
                    saveData["fisYear"] = rowData.fisYear;
                    saveData["bgtDgr"] = rowData.bgtDgr;
                    saveData["pledgeBizId"] = rowData.pledgeBizId;
                    saveData["teBgtCompoId"] = rowData.teBgtCompoId;
                    saveData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
                    saveData["examCont"] = examCont;
                    saveData["totFrscAmt1"] = totFrscAmt1;
                    saveData["totFrscAmt2"] = totFrscAmt2;
                    saveData["totFrscAmt3"] = totFrscAmt3;
                    saveData["totFrscAmt4"] = totFrscAmt4;
                    saveData["totFrscAmt5"] = totFrscAmt5;
                    saveData["totFrscAmt6"] = totFrscAmt6;
                    saveData["year1FrscAmt1"] = year1FrscAmt1;
                    saveData["year1FrscAmt2"] = year1FrscAmt2;
                    saveData["year1FrscAmt3"] = year1FrscAmt3;
                    saveData["year1FrscAmt4"] = year1FrscAmt4;
                    saveData["year1FrscAmt5"] = year1FrscAmt5;
                    saveData["year1FrscAmt6"] = year1FrscAmt6;
                    saveData["year2FrscAmt1"] = year2FrscAmt1;
                    saveData["year2FrscAmt2"] = year2FrscAmt2;
                    saveData["year2FrscAmt3"] = year2FrscAmt3;
                    saveData["year2FrscAmt4"] = year2FrscAmt4;
                    saveData["year2FrscAmt5"] = year2FrscAmt5;
                    saveData["year2FrscAmt6"] = year2FrscAmt6;
                    saveData["year3FrscAmt1"] = year3FrscAmt1;
                    saveData["year3FrscAmt2"] = year3FrscAmt2;
                    saveData["year3FrscAmt3"] = year3FrscAmt3;
                    saveData["year3FrscAmt4"] = year3FrscAmt4;
                    saveData["year3FrscAmt5"] = year3FrscAmt5;
                    saveData["year3FrscAmt6"] = year3FrscAmt6;
                    saveData["year4FrscAmt1"] = year4FrscAmt1;
                    saveData["year4FrscAmt2"] = year4FrscAmt2;
                    saveData["year4FrscAmt3"] = year4FrscAmt3;
                    saveData["year4FrscAmt4"] = year4FrscAmt4;
                    saveData["year4FrscAmt5"] = year4FrscAmt5;
                    saveData["year4FrscAmt6"] = year4FrscAmt6;
                    saveData["year5FrscAmt1"] = year5FrscAmt1;
                    saveData["year5FrscAmt2"] = year5FrscAmt2;
                    saveData["year5FrscAmt3"] = year5FrscAmt3;
                    saveData["year5FrscAmt4"] = year5FrscAmt4;
                    saveData["year5FrscAmt5"] = year5FrscAmt5;
                    saveData["year5FrscAmt6"] = year5FrscAmt6;
                    
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
        
        var saveDatas = getSaveDatas(pledgeReportGrid, $("#PLEDGE_REPORT_GRD", tabObj)[0].rows);
        if(isEmpty(saveDatas) == true || saveDatas.length < 1){
            $.csAlert({
                msg : "변경된 자료가 존재하지 않습니다."
            });
            
            return;
        }
        
        $.csAjaxCall({
            url : "/pledge/ajaxPledgeReportSavePledge.do",
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
    
    $("#savePledgeInfoBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }

        var param = getSearchParam();
        param["fileNm"] = "공약사업";
        param["amtUnit"] = "1000000";
        
        $.bcjisExcelAjaxCall({
            url : "/pledge/ajaxPledgeReportSaveSheet1.do"
          , data: param
        });
    });

    $("#saveSheetBtn2", tabObj).click(function() {
        var param = getSearchParam();
        param["fileNm"] = "공약사업 집계표";
        param["reportCd"] = "000";
        param["reportDetlCd"] = "000";
        param["amtUnit"] = "1000000";
        
        $.bcjisExcelAjaxCall({
            url : "/pledge/ajaxPledgeReportSaveSheet2.do"
          , data: param
        });
    });
    
    $("#saveSheetBtn3", tabObj).click(function() {
        var param = getSearchParam();
        param["reportCd"] = "P30";
        param["reportDetlCd"] = "P31";
        param["fileNm"] = "공약사항 현황";
        param["amtUnit"] = "1000000";
        param["pledgeInfNm"] = $("#condPledgeInfoId option:selected", tabObj).text();
        
        $.bcjisExcelAjaxCall({
            url : "/pledge/ajaxPledgeReportSaveSheet3.do"
          , data: param
        });
    });
    
    $("#saveSheetBtn4", tabObj).click(function() {
        var param = getSearchParam();
        param["reportCd"] = "P40";
        param["reportDetlCd"] = "P41";
        param["fileNm"] = "공약사항 현황(구군별)";
        param["amtUnit"] = "1000000";
        
        $.bcjisExcelAjaxCall({
            url : "/pledge/ajaxPledgeReportSaveSheet4.do"
          , data: param
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
    
    $("#condFisYear", tabObj).change(function(){
        doChangeCondFisYear();
    });
    
    $("#condBgtDgr", tabObj).change(function(){
        doChageCondBgtDgr();
    });
    
    $("#condOfficeCd", tabObj).change(function(){
        doChangeCondOfficeCd();
    });
    
    $("#condTeMngMokCdFr", tabObj).change(function(){
        $("#condTeMngMokCdTo", tabObj).val($("#condTeMngMokCdFr option:selected", tabObj).val());
    });
    
    var openDialogBgtDeptSelt = function(seltFg){

        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("pledgeReportDialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("N");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    pledgeReportDialogDgrDeptSeltCallBack = function(param){
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
                      {id : "pledgeInfoId", subQueryId : "PledgeInfoId"},
                      {id : "pledgeBizId", subQueryId : "PledgeBizId"},
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "bgtDgr", subQueryId : "BgtDgr"},
                      {id : "officeCd", subQueryId : "OfficeCd", userDeptYn : "N"},
                      {id : "reflectFg", codeId : "RP003"},
                      {id : "teMngMokCd", subQueryId : "TeMngMokCd"},
                      {id : "localGovCd", subQueryId : "LocalGovCd"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);
        
    var condPledgeBizId1CreateCombo = function(groupId, selectedValue){
        $("#condPledgeBizId1", tabObj).csCreatCombo(comboData
                , {id: 'pledgeBizId'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
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
                  , comboType: 'A'
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
    
    doCondInit();
});
