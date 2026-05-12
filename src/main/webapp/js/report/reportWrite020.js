$(document).ready(function() {
    var tabId = _reportWrite020TabId;
    var tabObj = $("#"+tabId);
    var gridScrollPosition = 0;
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        return ' style="vertical-align: top;"';
    };
    
    reportWirte020DialogDgrcompoModifyCallBackFunction = function(param){
        var rowId = param.dgrcompoId;
        if(isEmpty(rowId) == true){
            return;
        }
        
        reportWrite020Grid.jqGrid('setRowData', rowId, param);
       
        $("#preFrscAmt0"+"_"+rowId).val(addCommaStr(param.preFrscAmt0));
        $("#preFrscAmt1"+"_"+rowId).val(addCommaStr(param.preFrscAmt1));
        $("#preFrscAmt2"+"_"+rowId).val(addCommaStr(param.preFrscAmt2));
        $("#preFrscAmt3"+"_"+rowId).val(addCommaStr(param.preFrscAmt3));
        $("#preFrscAmt4"+"_"+rowId).val(addCommaStr(param.preFrscAmt4));
        $("#preFrscAmt5"+"_"+rowId).val(addCommaStr(param.preFrscAmt5));
        $("#preFrscAmt6"+"_"+rowId).val(addCommaStr(param.preFrscAmt6));
        $("#preCharAmt1"+"_"+rowId).val(addCommaStr(param.preCharAmt1));
        $("#preCharAmt2"+"_"+rowId).val(addCommaStr(param.preCharAmt2));
        $("#preCharAmt3"+"_"+rowId).val(addCommaStr(param.preCharAmt3));
       
        $("#dmnFrscAmt0"+"_"+rowId).val(addCommaStr(param.dmnFrscAmt0));
        $("#dmnFrscAmt1"+"_"+rowId).val(addCommaStr(param.dmnFrscAmt1));
        $("#dmnFrscAmt2"+"_"+rowId).val(addCommaStr(param.dmnFrscAmt2));
        $("#dmnFrscAmt3"+"_"+rowId).val(addCommaStr(param.dmnFrscAmt3));
        $("#dmnFrscAmt4"+"_"+rowId).val(addCommaStr(param.dmnFrscAmt4));
        $("#dmnFrscAmt5"+"_"+rowId).val(addCommaStr(param.dmnFrscAmt5));
        $("#dmnFrscAmt6"+"_"+rowId).val(addCommaStr(param.dmnFrscAmt6));
        $("#dmnCharAmt1"+"_"+rowId).val(addCommaStr(param.dmnCharAmt1));
        $("#dmnCharAmt2"+"_"+rowId).val(addCommaStr(param.dmnCharAmt2));
        $("#dmnCharAmt3"+"_"+rowId).val(addCommaStr(param.dmnCharAmt3));
       
        $("#frscAmt0"+"_"+rowId).val(addCommaStr(param.frscAmt0));
        $("#frscAmt1"+"_"+rowId).val(addCommaStr(param.frscAmt1));
        $("#frscAmt2"+"_"+rowId).val(addCommaStr(param.frscAmt2));
        $("#frscAmt3"+"_"+rowId).val(addCommaStr(param.frscAmt3));
        $("#frscAmt4"+"_"+rowId).val(addCommaStr(param.frscAmt4));
        $("#frscAmt5"+"_"+rowId).val(addCommaStr(param.frscAmt5));
        $("#frscAmt6"+"_"+rowId).val(addCommaStr(param.frscAmt6));
        $("#charAmt1"+"_"+rowId).val(addCommaStr(param.charAmt1));
        $("#charAmt2"+"_"+rowId).val(addCommaStr(param.charAmt2));
        $("#charAmt3"+"_"+rowId).val(addCommaStr(param.charAmt3));
        
        var dgrcompoNmView = param.dgrcompoNmView;
        if(isEmpty(dgrcompoNmView) == true){
            dgrcompoNmView = "";
        }
        
        $("#dgrcompoNmView_" + rowId, tabObj).html(dgrcompoNmView);
    };
    
    reportWirte020OpenDialogDgrcompoModify = function(rowId){  
        var rowData = reportWrite020Grid.getRowData(rowId);
        
        $("#dialogDgrcompoModifyCallBackFunction", $("#dialogDgrcompoModifyDiv")).val("reportWirte020DialogDgrcompoModifyCallBackFunction");
        $("#dialogDgrcompoModifyDgrcompoId", $("#dialogDgrcompoModifyDiv")).val(rowData.dgrcompoId);
        $("#dialogDgrcompoModifyFisYear", $("#dialogDgrcompoModifyDiv")).val(rowData.fisYear);
        $("#dialogDgrcompoModifyBgtDgr", $("#dialogDgrcompoModifyDiv")).val(rowData.bgtDgr);
        $("#dialogDgrcompoModifyTeBgtCompoId", $("#dialogDgrcompoModifyDiv")).val(rowData.teBgtCompoId);
        $("#dialogDgrcompoModifyIsLeaf", $("#dialogDgrcompoModifyDiv")).val(rowData.isLeaf);
        $("#dialogDgrcompoModifyAmtUnit", $("#dialogDgrcompoModifyDiv")).val($("#condAmtUnit", tabObj).val());
        
        $("#dialogDgrcompoModifyDiv").dialog('open');
    };
    
    var editFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(_mainNorthPowGrCd == "BC002"){
            return cellValue;
        }
        
        if(rowObject.teBgtCompoId == "00000000000"){
            return cellValue;
        }
        
        var demandCont = rowObject.demandCont; 
        if(isEmpty(demandCont) == true){
            demandCont = "";
        }
        
        var rVal = '<a href="javascript:reportWirte020OpenDialogDgrcompoModify(\''+options.rowId+'\');"><span class="ui-icon ui-icon-pencil"></span></a>';
        
        return rVal;
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
        
        var rVal = '<span id="dgrcompoNmView_'+rowObject.dgrcompoId+'" >' + cellValue + '</span><br>'
                 + '<textarea id="demandCont_'+rowObject.dgrcompoId+'" style="width:210px;ime-mode:active;height:128px;">'+demandCont+'</textarea>';
        
        return rVal;
    };
    
    var investPlanViewFormatter = function(cellValue, options, rowObject){

        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var preFrscAmt0 = rowObject.bgtDgr == '1' ? Number(rowObject.preFrscAmt0) : Number(rowObject.preDefFrscAmt0);
        var preFrscAmt1 = rowObject.bgtDgr == '1' ? Number(rowObject.preFrscAmt1) : Number(rowObject.preDefFrscAmt1);
        var preFrscAmt2 = rowObject.bgtDgr == '1' ? Number(rowObject.preFrscAmt2) : Number(rowObject.preDefFrscAmt2);
        var preFrscAmt3 = rowObject.bgtDgr == '1' ? Number(rowObject.preFrscAmt3) : Number(rowObject.preDefFrscAmt3);
        var preFrscAmt4 = rowObject.bgtDgr == '1' ? Number(rowObject.preFrscAmt4) : Number(rowObject.preDefFrscAmt4);
        var preFrscAmt5 = rowObject.bgtDgr == '1' ? Number(rowObject.preFrscAmt5) : Number(rowObject.preDefFrscAmt5);
        var preFrscAmt6 = rowObject.bgtDgr == '1' ? Number(rowObject.preFrscAmt6) : Number(rowObject.preDefFrscAmt6);
        var preCharAmt1 = rowObject.bgtDgr == '1' ? Number(rowObject.preCharAmt1) : Number(rowObject.preDefCharAmt1);
        var preCharAmt2 = rowObject.bgtDgr == '1' ? Number(rowObject.preCharAmt2) : Number(rowObject.preDefCharAmt2);
        var preCharAmt3 = rowObject.bgtDgr == '1' ? Number(rowObject.preCharAmt3) : Number(rowObject.preDefCharAmt3);
        
        //추가
        var tot_totFrscAmt1 = Number(rowObject.totFrscAmt1)+Number(rowObject.totFrscAmt4)+Number(rowObject.totFrscAmt5);
        var tot_preInvFrscAmt1= Number(rowObject.preInvFrscAmt1)+Number(rowObject.preInvFrscAmt4)+Number(rowObject.preInvFrscAmt5);
        var tot_preFrscAmt1 = preFrscAmt1+preFrscAmt4+preFrscAmt5;
        var tot_frscAmt1 = Number(rowObject.frscAmt1)+Number(rowObject.frscAmt4)+Number(rowObject.frscAmt5);
        
        
        var investPlanFrscAmt0 = Number(rowObject.totFrscAmt0) - Number(rowObject.preInvFrscAmt0) - preFrscAmt0 - Number(rowObject.frscAmt0);
        var investPlanFrscAmt1 = tot_totFrscAmt1 - tot_preInvFrscAmt1 - tot_preFrscAmt1 - tot_frscAmt1;
        var investPlanFrscAmt2 = Number(rowObject.totFrscAmt2) - Number(rowObject.preInvFrscAmt2) - preFrscAmt2 - Number(rowObject.frscAmt2);
        var investPlanFrscAmt3 = Number(rowObject.totFrscAmt3) - Number(rowObject.preInvFrscAmt3) - preFrscAmt3 - Number(rowObject.frscAmt3);
        //var investPlanFrscAmt4 = Number(rowObject.totFrscAmt4) - Number(rowObject.preInvFrscAmt4) - preFrscAmt4 - Number(rowObject.frscAmt4);
        //var investPlanFrscAmt5 = Number(rowObject.totFrscAmt5) - Number(rowObject.preInvFrscAmt5) - preFrscAmt5 - Number(rowObject.frscAmt5);
        var investPlanFrscAmt6 = Number(rowObject.totFrscAmt6) - Number(rowObject.preInvFrscAmt6) - preFrscAmt6 - Number(rowObject.frscAmt6);
        var investPlanCharAmt1 = Number(rowObject.totCharAmt1) - Number(rowObject.preInvCharAmt1) - preCharAmt1 - Number(rowObject.charAmt1);
        var investPlanCharAmt2 = Number(rowObject.totCharAmt2) - Number(rowObject.preInvCharAmt2) - preCharAmt2 - Number(rowObject.charAmt2);
        var investPlanCharAmt3 = Number(rowObject.totCharAmt3) - Number(rowObject.preInvCharAmt3) - preCharAmt3 - Number(rowObject.charAmt3);
        
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr>'
                 + '      <td><input id="investPlanFrscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(investPlanFrscAmt0)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="investPlanFrscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(investPlanFrscAmt1)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="investPlanFrscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(investPlanFrscAmt2)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="investPlanFrscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(investPlanFrscAmt3)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>'  
                 + '      </td>' 
                 + '    <tr>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>'  
                 + '      </td>'
                 + '    </tr>'
                /* 
                 + '      <td><input id="investPlanFrscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(investPlanFrscAmt4)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="investPlanFrscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(investPlanFrscAmt5)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 */
                 + '    <tr>'
                 + '      <td><input id="investPlanFrscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(investPlanFrscAmt6)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="investPlanCharAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(investPlanCharAmt1)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="investPlanCharAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(investPlanCharAmt2)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="investPlanCharAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(investPlanCharAmt3)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var examContFormatter = function(cellValue, options, rowObject){
        if(rowObject.teBgtCompoId == "00000000000"){
            return cellValue;
        }
        
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var styleStr = "";
        if(rowObject.compoLevel != 1){
            styleStr = 'style="display:none;width:240px;"';
        }else{
            styleStr = 'style="width:240px;"';
        }
        
        var investPlan = "";
        if(isEmpty(rowObject.investPlan) == false){
            investPlan = rowObject.investPlan;
        }
        
        var rVal = '<div>'
                 + '<select id="reflectFg_'+rowObject.dgrcompoId+'" title="반영구분" style="width:90px;">'
                 + reflectFgCreateCombo('RP003', rowObject.reflectFg)
                 + '</select>'
                 + '&nbsp;<input id="investPlan_'+rowObject.dgrcompoId+'" value="'+investPlan+'" maxlength="500" class="ui-state-enabled" '+styleStr +' />'+'<br>'
                 + '<textarea id="examCont_'+rowObject.dgrcompoId+'" style="width:340px;ime-mode:active;height:218px;">'+cellValue+'</textarea>'
                 + '</div>';

        return rVal;
    };
    
    var srchValFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.teBgtCompoId == "00000000000" ){
            return cellValue;
        }
        
        var styleStr = "";
        var styleChkStr = "";
        if(rowObject.compoLevel != 1){
            styleStr = 'style="display:none;"';
        }else{
            styleStr = 'style="width:240px;ime-mode:active;height:100px;"';
            styleChkStr = 'style="margin-top: 5px;"';
        }
        
        var rVal = '<div>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3250000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3250000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">중구</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3260000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3260000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">서구</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3270000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3270000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">동구</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3280000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3280000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">영도구</span><br>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3290000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3290000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">부산진구</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3300000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3300000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">동래구</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3310000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3310000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">남구</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3320000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3320000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">북구</span><br>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3330000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3330000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">해운대구</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3340000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3340000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">사하구</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3350000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3350000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">금정구</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3360000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3360000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">강서구</span><br>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3370000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3370000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">연제구</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3380000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3380000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">수영구</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3390000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3390000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">사상구</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn3400000_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.checkYn3400000 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">기장군</span>'
                 + '<textarea id="srchVal_'+rowObject.dgrcompoId+'" '+styleStr+'">'+cellValue+'</textarea>'
                 + '</div>';

        return rVal;
    };
    
    var reportSortSeqFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var styleStr = "";
        var styleChkStr = "";
        if(rowObject.compoLevel != 1){
            styleStr = 'style="display:none;width:15px;"';
            styleChkStr = 'style="display:none;"';
        }else{
            styleStr = 'style="width:40px;"';
            styleChkStr = 'style="margin-top: 5px;"';
        }
        
        var rVal = '<div>'
                 + '<input id="reportSortSeq_'+rowObject.dgrcompoId+'" value="'+cellValue+'" maxlength="10" class="amtInput020 ui-state-enabled" '+styleStr +' /><br>'
                 + '<input type="checkbox" id="mayorReportYn_'+rowObject.dgrcompoId+'" value="Y" class="chkReport020" '+styleChkStr +' '+(rowObject.mayorReportYn == 'Y' ? 'checked' : '')+' />보고'
                 + '</div>';

        return rVal;
    };
    
    var totFrscAmtFormatter = function(cellValue, options, rowObject){
        var readOnlyStr = "";
        var classStr = '';
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
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr>'
                 + '      <td colspan="2">계</td>'
                 + '      <td><input id="totFrscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td rowspan="6">재<br>원<br>별<br></td>'
                 + '      <td>시비</td>'
                 + '      <td><input id="totFrscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totFrscAmt1)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>국비</td>'
                 + '      <td><input id="totFrscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totFrscAmt2)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>교부세</td>'
                 + '      <td><input id="totFrscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totFrscAmt3)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>지방채</td>'
                 + '      <td><input id="totFrscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totFrscAmt4)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>채무</td>'
                 + '      <td><input id="totFrscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totFrscAmt5)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>기타</td>'
                 + '      <td><input id="totFrscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totFrscAmt6)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td rowspan="6">성<br>격<br>별<br></td>'
                 + '      <td>공사</td>'
                 + '      <td><input id="totCharAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totCharAmt1)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>보상</td>'
                 + '      <td><input id="totCharAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totCharAmt2)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>기타</td>'
                 + '      <td><input id="totCharAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.totCharAmt3)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';

        return rVal;
    };
    
    var preInvFrscAmtFormatter = function(cellValue, options, rowObject){
        var readOnlyStr = "";
        var classStr = '';
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
                 + '    <tr>'
                 + '      <td><input id="preInvFrscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preInvFrscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preInvFrscAmt1)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preInvFrscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preInvFrscAmt2)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preInvFrscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preInvFrscAmt3)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preInvFrscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preInvFrscAmt4)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preInvFrscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preInvFrscAmt5)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preInvFrscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preInvFrscAmt6)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preInvCharAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preInvCharAmt1)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preInvCharAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preInvCharAmt2)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preInvCharAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preInvCharAmt3)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
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
                 + '    <tr>'
                 + '      <td><input id="preFrscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preFrscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preFrscAmt1)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preFrscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preFrscAmt2)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preFrscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preFrscAmt3)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preFrscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preFrscAmt4)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preFrscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preFrscAmt5)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preFrscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preFrscAmt6)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preCharAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preCharAmt1)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preCharAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preCharAmt2)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="preCharAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.preCharAmt3)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
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
                 + '      <td id="preDefFrscAmt0_'+rowObject.dgrcompoId+'" class="amtView020Td">'+addCommaStr(cellValue)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td id="preDefFrscAmt1_'+rowObject.dgrcompoId+'" class="amtView020Td">'+addCommaStr(rowObject.preDefFrscAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td id="preDefFrscAmt2_'+rowObject.dgrcompoId+'" class="amtView020Td">'+addCommaStr(rowObject.preDefFrscAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td id="preDefFrscAmt3_'+rowObject.dgrcompoId+'" class="amtView020Td">'+addCommaStr(rowObject.preDefFrscAmt3)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td id="preDefFrscAmt4_'+rowObject.dgrcompoId+'" class="amtView020Td">'+addCommaStr(rowObject.preDefFrscAmt4)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td id="preDefFrscAmt5_'+rowObject.dgrcompoId+'" class="amtView020Td">'+addCommaStr(rowObject.preDefFrscAmt5)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td id="preDefFrscAmt6_'+rowObject.dgrcompoId+'" class="amtView020Td">'+addCommaStr(rowObject.preDefFrscAmt6)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td id="preDefCharAmt1_'+rowObject.dgrcompoId+'" class="amtView020Td">'+addCommaStr(rowObject.preDefCharAmt1)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td id="preDefCharAmt2_'+rowObject.dgrcompoId+'" class="amtView020Td">'+addCommaStr(rowObject.preDefCharAmt2)+'</td>'
                 + '    </tr>'
                 + '    <tr class="amtView020Tr">'
                 + '      <td id="preDefCharAmt3_'+rowObject.dgrcompoId+'" class="amtView020Td">'+addCommaStr(rowObject.preDefCharAmt3)+'</td>'
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
                 + '    <tr>'
                 + '      <td><input id="dmnFrscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnFrscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnFrscAmt1)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnFrscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnFrscAmt2)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnFrscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnFrscAmt3)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnFrscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnFrscAmt4)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnFrscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnFrscAmt5)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnFrscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnFrscAmt6)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnCharAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnCharAmt1)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnCharAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnCharAmt2)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnCharAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnCharAmt3)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
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
                 + '    <tr>'
                 + '      <td><input id="frscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="frscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.frscAmt1)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="frscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.frscAmt2)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="frscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.frscAmt3)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="frscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.frscAmt4)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="frscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.frscAmt5)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="frscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.frscAmt6)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="charAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.charAmt1)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="charAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.charAmt2)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="charAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.charAmt3)+'" class="amtInput020 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var colNames = ['', '구분(실국-세세목)', '정렬순서', '총사업비', '기투자', '본예산', '전년도 투자', '요구액', '조정액', '향후투자계획', '검토내용', '공약정보', '조건검색어',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'reportCd', 'reportDetlCd', 'dgrLevel', 'teBgtCompoId', 'teBgtCompoSeq', 'compoLevel', 'demandCont', 'examCont', 'reflectFg', 'srchVal', 'reportSortSeq', 'mayorReportYn', 
                    'totFrscAmt1', 'totFrscAmt2', 'totFrscAmt3', 'totFrscAmt4', 'totFrscAmt5', 'totFrscAmt6', 'totCharAmt1', 'totCharAmt2', 'totCharAmt3',
                    'preInvFrscAmt1', 'preInvFrscAmt2', 'preInvFrscAmt3', 'preInvFrscAmt4', 'preInvFrscAmt5', 'preInvFrscAmt6', 'preInvCharAmt1', 'preInvCharAmt2', 'preInvCharAmt3',
                    'frscAmt1', 'frscAmt2', 'frscAmt3', 'frscAmt4', 'frscAmt5', 'frscAmt6', 'charAmt1', 'charAmt2', 'charAmt3',
                    'dmnFrscAmt1', 'dmnFrscAmt2', 'dmnFrscAmt3', 'dmnFrscAmt4', 'dmnFrscAmt5', 'dmnFrscAmt6', 'dmnCharAmt1', 'dmnCharAmt2', 'dmnCharAmt3',
                    'preFrscAmt1', 'preFrscAmt2', 'preFrscAmt3', 'preFrscAmt4', 'preFrscAmt5', 'preFrscAmt6', 'preCharAmt1', 'preCharAmt2', 'preCharAmt3',
                    'checkYn3250000', 'checkYn3260000', 'checkYn3270000', 'checkYn3280000', 'checkYn3290000', 'checkYn3300000', 'checkYn3310000', 'checkYn3320000',
                    'checkYn3330000', 'checkYn3340000', 'checkYn3350000', 'checkYn3360000', 'checkYn3370000', 'checkYn3380000', 'checkYn3390000', 'checkYn3400000'
                   ];
    
    var colModel = [ {name : 'edit', index : 'edit', width : 20, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:editFormatter
                        },
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 320, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:dgrcompoNmFormatter
                        },
                        {name : 'reportSortSeqView', index : 'reportSortSeqView', width : 50, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : reportSortSeqFormatter
                        },
                        {name : 'totFrscAmt0', index : 'totFrscAmt0', width : 150, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
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
                        {name : 'examContView', index : 'examContView', width : 350, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:examContFormatter
                        },
                        {name : 'pledgeFgs', index : 'pledgeFgs', width : 70, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
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
                        {name : 'demandCont', index : 'demandCont', width : 0, sortable : false, hidden : true},
                        {name : 'examCont', index : 'examCont', width : 0, sortable : false, hidden : true},
                        {name : 'reflectFg', index : 'reflectFg', width : 0, sortable : false, hidden : true},
                        {name : 'srchVal', index : 'srchVal', width : 0, sortable : false, hidden : true},
                        {name : 'reportSortSeq', index : 'reportSortSeq', width : 0, sortable : false, hidden : true},
                        {name : 'mayorReportYn', index : 'mayorReportYn', width : 0, sortable : false, hidden : true},
                        {name : 'totFrscAmt1', index : 'totFrscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'totFrscAmt2', index : 'totFrscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'totFrscAmt3', index : 'totFrscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'totFrscAmt4', index : 'totFrscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'totFrscAmt5', index : 'totFrscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'totFrscAmt6', index : 'totFrscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'totCharAmt1', index : 'totCharAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'totCharAmt2', index : 'totCharAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'totCharAmt3', index : 'totCharAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'preInvFrscAmt1', index : 'preInvFrscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'preInvFrscAmt2', index : 'preInvFrscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'preInvFrscAmt3', index : 'preInvFrscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'preInvFrscAmt4', index : 'preInvFrscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'preInvFrscAmt5', index : 'preInvFrscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'preInvFrscAmt6', index : 'preInvFrscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'preInvCharAmt1', index : 'preInvCharAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'preInvCharAmt2', index : 'preInvCharAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'preInvCharAmt3', index : 'preInvCharAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt1', index : 'frscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt2', index : 'frscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt3', index : 'frscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt4', index : 'frscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt5', index : 'frscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt6', index : 'frscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'charAmt1', index : 'charAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'charAmt2', index : 'charAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'charAmt3', index : 'charAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'dmnFrscAmt1', index : 'dmnFrscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'dmnFrscAmt2', index : 'dmnFrscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'dmnFrscAmt3', index : 'dmnFrscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'dmnFrscAmt4', index : 'dmnFrscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'dmnFrscAmt5', index : 'dmnFrscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'dmnFrscAmt6', index : 'dmnFrscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'dmnCharAmt1', index : 'dmnCharAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'dmnCharAmt2', index : 'dmnCharAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'dmnCharAmt3', index : 'dmnCharAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'preFrscAmt1', index : 'preFrscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'preFrscAmt2', index : 'preFrscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'preFrscAmt3', index : 'preFrscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'preFrscAmt4', index : 'preFrscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'preFrscAmt5', index : 'preFrscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'preFrscAmt6', index : 'preFrscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'preCharAmt1', index : 'preCharAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'preCharAmt2', index : 'preCharAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'preCharAmt3', index : 'preCharAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3250000', index : 'checkYn3250000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3260000', index : 'checkYn3260000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3270000', index : 'checkYn3270000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3280000', index : 'checkYn328000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3290000', index : 'checkYn3290000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3300000', index : 'checkYn3300000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3310000', index : 'checkYn3310000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3320000', index : 'checkYn3320000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3330000', index : 'checkYn3330000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3340000', index : 'checkYn3340000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3350000', index : 'checkYn3350000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3360000', index : 'checkYn3360000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3370000', index : 'checkYn3370000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3380000', index : 'checkYn3380000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3390000', index : 'checkYn3390000', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn3400000', index : 'checkYn3400000', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 200 ? $("#mainCenter", tabObj).height() - 110 : 200;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#REPORT_WRITE020_GRD", $("#"+tabId))) == false){
            $("#REPORT_WRITE020_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#REPORT_WRITE020_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
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
        }else if(objId.indexOf("Char") > 0){
            setSumCharAmt(objId);
        }
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
        
        var preCharId = preFrscId.replaceAll("Frsc", "Char");
        
        var sumFrscAmt = Number($("#"+preFrscId+"1"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                       + Number($("#"+preFrscId+"2"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                       + Number($("#"+preFrscId+"3"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                       + Number($("#"+preFrscId+"4"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                       + Number($("#"+preFrscId+"5"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                       + Number($("#"+preFrscId+"6"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""));
        
        var sumCharAmt = Number($("#"+preCharId+"1"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                       + Number($("#"+preCharId+"2"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""));
        
        $("#"+preFrscId+"0"+"_"+dgrcompoId, tabObj).val(addCommaStr(sumFrscAmt));
        $("#"+preCharId+"3"+"_"+dgrcompoId, tabObj).val(addCommaStr((sumFrscAmt - sumCharAmt)));
    };
    
    var setSumCharAmt = function(objId){
        var idIndex = objId.indexOf("_");
        if(idIndex < 1){
            return;
        }
        
        var preCharId = objId.substring(0, idIndex - 1);
        var dgrcompoId = objId.substring(idIndex + 1);
        
        if(isEmpty(preCharId) == true || isEmpty(dgrcompoId) == true){
            return;
        }

        var preFrscId = preCharId.replaceAll("Char", "Frsc");
        
        var sumFrscAmt = Number($("#"+preFrscId+"0"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""));

        var sumCharAmt = Number($("#"+preCharId+"1"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                       + Number($("#"+preCharId+"2"+"_"+dgrcompoId, tabObj).val().replaceAll(",", ""));
        
        $("#"+preCharId+"3"+"_"+dgrcompoId, tabObj).val(addCommaStr((sumFrscAmt - sumCharAmt)));
    };
    
    var reportWrite020Grid = $("#REPORT_WRITE020_GRD", tabObj);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        var groupHeaders = [];
        if(data.data.bgtDgr == "1"){
            colModel[5].hidden = true;
            colModel[6].hidden = false;
            groupHeaders = [{startColumnName : 'dmnFrscAmt0',numberOfColumns : 2, titleText : '당해연도 투자계획'}];
        }else{
            colModel[5].hidden = false;
            colModel[6].hidden = true;
            groupHeaders = [{startColumnName : 'dmnFrscAmt0',numberOfColumns : 2, titleText : '금회추경 투자계획'}];
        }
        
        $("#REPORT_WRITE020_GRD", tabObj).jqGrid('GridUnload');
        reportWrite020Grid = $("#REPORT_WRITE020_GRD", tabObj);
        reportWrite020Grid.csTreeGrid({
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
                $(".amtInput020.ui-state-enabled", tabObj).autoNumeric({aPad: false, vMax:'99999999999999999'});
                $(".amtInput020.ui-state-enabled", tabObj).click(function () {
                    $(this).select();
                });
                $(".amtInput020.ui-state-enabled", tabObj).focusout(function(){
                    setSumAmt($(this));
                });
                $('textarea').maxlength({max: 1000, showFeedback: false});
            }
        });

        reportWrite020Grid.jqGrid('setGroupHeaders', {
            useColSpanStyle : true,
            groupHeaders : groupHeaders
        });
        
        $("#REPORT_WRITE020_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
        $("#saveBtn", $("#"+tabId)).btnChangeState(true);

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

        var reportDetlCd = $("#condReportDetlCd option:selected", tabObj).val();
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

        gridScrollPosition = $("#REPORT_WRITE020_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite020Report020List.do",
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
        
        $("#rankBtn", tabObj).btnChangeState(true);
    };
    
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });
    
    var getSaveDatas = function(gridObject, gridRows){
        var saveDatas = [];
        var saveData = {};
        var rowId;
        var rowData;
        var demandCont = "";
        var examCont = "";
        var reflectFg = "";
        var totFrscAmt1 = "";
        var totFrscAmt2 = "";
        var totFrscAmt3 = "";
        var totFrscAmt4 = "";
        var totFrscAmt5 = "";
        var totFrscAmt6 = "";
        var totCharAmt1 = "";
        var totCharAmt2 = "";
        var totCharAmt3 = "";
        var preInvFrscAmt1 = "";
        var preInvFrscAmt2 = "";
        var preInvFrscAmt3 = "";
        var preInvFrscAmt4 = "";
        var preInvFrscAmt5 = "";
        var preInvFrscAmt6 = "";
        var preInvCharAmt1 = "";
        var preInvCharAmt2 = "";
        var preInvCharAmt3 = "";
        var srchVal = "";
        var reportSortSeq = "";
        var mayorReportYn = "";
        var investPlan = "";
        var checkYn3250000 ="";
        var checkYn3260000 ="";
        var checkYn3270000 ="";
        var checkYn3280000 ="";
        var checkYn3290000 ="";
        var checkYn3300000 ="";
        var checkYn3310000 ="";
        var checkYn3320000 ="";
        var checkYn3330000 ="";
        var checkYn3340000 ="";
        var checkYn3350000 ="";
        var checkYn3360000 ="";
        var checkYn3370000 ="";
        var checkYn3380000 ="";
        var checkYn3390000 ="";
        var checkYn3400000 ="";
        
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);
            if(isEmpty(rowData.dgrcompoId) == false && rowData.teBgtCompoId != "00000000000"){
                demandCont = $('#demandCont_'+rowId, tabObj).val().trim();
                examCont = $('#examCont_'+rowId, tabObj).val().trim();
                reflectFg = $('#reflectFg_'+rowId, tabObj).val();
                totFrscAmt1 = $('#totFrscAmt1_'+rowId, tabObj).val().replaceAll(",", "");
                totFrscAmt2 = $('#totFrscAmt2_'+rowId, tabObj).val().replaceAll(",", "");
                totFrscAmt3 = $('#totFrscAmt3_'+rowId, tabObj).val().replaceAll(",", "");
                totFrscAmt4 = $('#totFrscAmt4_'+rowId, tabObj).val().replaceAll(",", "");
                totFrscAmt5 = $('#totFrscAmt5_'+rowId, tabObj).val().replaceAll(",", "");
                totFrscAmt6 = $('#totFrscAmt6_'+rowId, tabObj).val().replaceAll(",", "");
                totCharAmt1 = $('#totCharAmt1_'+rowId, tabObj).val().replaceAll(",", "");
                totCharAmt2 = $('#totCharAmt2_'+rowId, tabObj).val().replaceAll(",", "");
                totCharAmt3 = $('#totCharAmt3_'+rowId, tabObj).val().replaceAll(",", "");
                preInvFrscAmt1 = $('#preInvFrscAmt1_'+rowId, tabObj).val().replaceAll(",", "");
                preInvFrscAmt2 = $('#preInvFrscAmt2_'+rowId, tabObj).val().replaceAll(",", "");
                preInvFrscAmt3 = $('#preInvFrscAmt3_'+rowId, tabObj).val().replaceAll(",", "");
                preInvFrscAmt4 = $('#preInvFrscAmt4_'+rowId, tabObj).val().replaceAll(",", "");
                preInvFrscAmt5 = $('#preInvFrscAmt5_'+rowId, tabObj).val().replaceAll(",", "");
                preInvFrscAmt6 = $('#preInvFrscAmt6_'+rowId, tabObj).val().replaceAll(",", "");
                preInvCharAmt1 = $('#preInvCharAmt1_'+rowId, tabObj).val().replaceAll(",", "");
                preInvCharAmt2 = $('#preInvCharAmt2_'+rowId, tabObj).val().replaceAll(",", "");
                preInvCharAmt3 = $('#preInvCharAmt3_'+rowId, tabObj).val().replaceAll(",", "");
                srchVal = $('#srchVal_'+rowId, tabObj).val().trim();
                mayorReportYn = $('#mayorReportYn_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                if(rowData.compoLevel == 1){
                    reportSortSeq = $('#reportSortSeq_'+rowId, tabObj).val();
                }
                investPlan = $('#investPlan_'+rowId, tabObj).val();
                
                checkYn3250000 = $('#checkYn3250000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3260000 = $('#checkYn3260000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3270000 = $('#checkYn3270000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3280000 = $('#checkYn3280000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3290000 = $('#checkYn3290000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3300000 = $('#checkYn3300000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3310000 = $('#checkYn3310000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3320000 = $('#checkYn3320000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3330000 = $('#checkYn3330000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3340000 = $('#checkYn3340000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3350000 = $('#checkYn3350000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3360000 = $('#checkYn3360000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3370000 = $('#checkYn3370000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3380000 = $('#checkYn3380000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3390000 = $('#checkYn3390000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn3400000 = $('#checkYn3400000_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                
                if(rowData.demandCont != demandCont
                        || rowData.examCont != examCont
                        || rowData.reflectFg != reflectFg
                        || rowData.srchVal != srchVal
                        || rowData.reportSortSeq != reportSortSeq
                        || rowData.mayorReportYn != mayorReportYn
                        || rowData.investPlan != investPlan
                        || rowData.totFrscAmt1 != totFrscAmt1
                        || rowData.totFrscAmt2 != totFrscAmt2
                        || rowData.totFrscAmt3 != totFrscAmt3
                        || rowData.totFrscAmt4 != totFrscAmt4
                        || rowData.totFrscAmt5 != totFrscAmt5
                        || rowData.totFrscAmt6 != totFrscAmt6
                        || rowData.totCharAmt1 != totCharAmt1
                        || rowData.totCharAmt2 != totCharAmt2
                        || rowData.totCharAmt3 != totCharAmt3
                        || rowData.preInvFrscAmt1 != preInvFrscAmt1
                        || rowData.preInvFrscAmt2 != preInvFrscAmt2
                        || rowData.preInvFrscAmt3 != preInvFrscAmt3
                        || rowData.preInvFrscAmt4 != preInvFrscAmt4
                        || rowData.preInvFrscAmt5 != preInvFrscAmt5
                        || rowData.preInvFrscAmt6 != preInvFrscAmt6
                        || rowData.preInvCharAmt1 != preInvCharAmt1
                        || rowData.preInvCharAmt2 != preInvCharAmt2
                        || rowData.preInvCharAmt3 != preInvCharAmt3
                        || rowData.checkYn3250000 != checkYn3250000
                        || rowData.checkYn3260000 != checkYn3260000
                        || rowData.checkYn3270000 != checkYn3270000
                        || rowData.checkYn3280000 != checkYn3280000
                        || rowData.checkYn3290000 != checkYn3290000
                        || rowData.checkYn3300000 != checkYn3300000
                        || rowData.checkYn3310000 != checkYn3310000
                        || rowData.checkYn3320000 != checkYn3320000
                        || rowData.checkYn3330000 != checkYn3330000
                        || rowData.checkYn3340000 != checkYn3340000
                        || rowData.checkYn3350000 != checkYn3350000
                        || rowData.checkYn3360000 != checkYn3360000
                        || rowData.checkYn3370000 != checkYn3370000
                        || rowData.checkYn3380000 != checkYn3380000
                        || rowData.checkYn3390000 != checkYn3390000
                        || rowData.checkYn3400000 != checkYn3400000){
                    
                    saveData = {};
                    saveData["fisYear"] = rowData.fisYear;
                    saveData["bgtDgr"] = rowData.bgtDgr;
                    saveData["reportCd"] = rowData.reportCd;
                    saveData["reportDetlCd"] = rowData.reportDetlCd;
                    saveData["teBgtCompoId"] = rowData.teBgtCompoId;
                    saveData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
                    saveData["demandCont"] = demandCont;
                    saveData["examCont"] = examCont;
                    saveData["reflectFg"] = reflectFg;
                    saveData["srchVal"] = srchVal;
                    saveData["srchValYn"] = rowData.srchVal != srchVal ? "Y" : "N";
                    saveData["reportSortSeq"] = reportSortSeq;
                    saveData["reportSortSeqYn"] = rowData.reportSortSeq != reportSortSeq ? "Y" : "N";
                    saveData["mayorReportYn"] = mayorReportYn;
                    saveData["mayorReportChangeYn"] = rowData.mayorReportYn != mayorReportYn ? "Y" : "N";
                    saveData["investPlan"] = investPlan;
                    saveData["totFrscAmt1"] = totFrscAmt1;
                    saveData["totFrscAmt2"] = totFrscAmt2;
                    saveData["totFrscAmt3"] = totFrscAmt3;
                    saveData["totFrscAmt4"] = totFrscAmt4;
                    saveData["totFrscAmt5"] = totFrscAmt5;
                    saveData["totFrscAmt6"] = totFrscAmt6;
                    saveData["totCharAmt1"] = totCharAmt1;
                    saveData["totCharAmt2"] = totCharAmt2;
                    saveData["totCharAmt3"] = totCharAmt3;
                    saveData["preInvFrscAmt1"] = preInvFrscAmt1;
                    saveData["preInvFrscAmt2"] = preInvFrscAmt2;
                    saveData["preInvFrscAmt3"] = preInvFrscAmt3;
                    saveData["preInvFrscAmt4"] = preInvFrscAmt4;
                    saveData["preInvFrscAmt5"] = preInvFrscAmt5;
                    saveData["preInvFrscAmt6"] = preInvFrscAmt6;
                    saveData["preInvCharAmt1"] = preInvCharAmt1;
                    saveData["preInvCharAmt2"] = preInvCharAmt2;
                    saveData["preInvCharAmt3"] = preInvCharAmt3;   
                    
                    saveData["checkYn3250000"] = checkYn3250000;
                    saveData["checkYn3250000Yn"] = rowData.checkYn3250000 != checkYn3250000 ? "Y" : "N";
                    saveData["checkYn3260000"] = checkYn3260000;
                    saveData["checkYn3260000Yn"] = rowData.checkYn3260000 != checkYn3260000 ? "Y" : "N";
                    saveData["checkYn3270000"] = checkYn3270000;
                    saveData["checkYn3270000Yn"] = rowData.checkYn3270000 != checkYn3270000 ? "Y" : "N";
                    saveData["checkYn3280000"] = checkYn3280000;
                    saveData["checkYn3280000Yn"] = rowData.checkYn3280000 != checkYn3280000 ? "Y" : "N";
                    saveData["checkYn3290000"] = checkYn3290000;
                    saveData["checkYn3290000Yn"] = rowData.checkYn3290000 != checkYn3290000 ? "Y" : "N";
                    saveData["checkYn3300000"] = checkYn3300000;
                    saveData["checkYn3300000Yn"] = rowData.checkYn3300000 != checkYn3300000 ? "Y" : "N";
                    saveData["checkYn3310000"] = checkYn3310000;
                    saveData["checkYn3310000Yn"] = rowData.checkYn3310000 != checkYn3310000 ? "Y" : "N";
                    saveData["checkYn3320000"] = checkYn3320000;
                    saveData["checkYn3320000Yn"] = rowData.checkYn3320000 != checkYn3320000 ? "Y" : "N";
                    saveData["checkYn3330000"] = checkYn3330000;
                    saveData["checkYn3330000Yn"] = rowData.checkYn3330000 != checkYn3330000 ? "Y" : "N";
                    saveData["checkYn3340000"] = checkYn3340000;
                    saveData["checkYn3340000Yn"] = rowData.checkYn3340000 != checkYn3340000 ? "Y" : "N";
                    saveData["checkYn3350000"] = checkYn3350000;
                    saveData["checkYn3350000Yn"] = rowData.checkYn3350000 != checkYn3350000 ? "Y" : "N";
                    saveData["checkYn3360000"] = checkYn3360000;
                    saveData["checkYn3360000Yn"] = rowData.checkYn3360000 != checkYn3360000 ? "Y" : "N";
                    saveData["checkYn3370000"] = checkYn3370000;
                    saveData["checkYn3370000Yn"] = rowData.checkYn3370000 != checkYn3370000 ? "Y" : "N";
                    saveData["checkYn3380000"] = checkYn3380000;
                    saveData["checkYn3380000Yn"] = rowData.checkYn3380000 != checkYn3380000 ? "Y" : "N";
                    saveData["checkYn3390000"] = checkYn3390000;
                    saveData["checkYn3390000Yn"] = rowData.checkYn3390000 != checkYn3390000 ? "Y" : "N";
                    saveData["checkYn3400000"] = checkYn3400000;
                    saveData["checkYn3400000Yn"] = rowData.checkYn3400000 != checkYn3400000 ? "Y" : "N";                 
                    
                    if(rowData.reflectFg != reflectFg && reflectFg === "020"){
                        saveData["reflegFgYn"] = "Y";
                    }else{
                        saveData["reflegFgYn"] = "N";
                    }
                    
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
        
        var saveDatas = getSaveDatas(reportWrite020Grid, $("#REPORT_WRITE020_GRD", tabObj)[0].rows);
        if(isEmpty(saveDatas) == true || saveDatas.length < 1){
            $.csAlert({
                msg : "변경된 자료가 존재하지 않습니다."
            });
            
            return;
        }
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite020SaveReport020.do",
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
    
    $("#rankBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $("#dialogDgroffice020SortCallBackFunction", $("#dialogDgroffice020SortDiv")).val("reportWrite020DialogDgroffice020SortCallBackFunction");
        $("#dialogDgroffice020SortFisYear", $("#dialogDgroffice020SortDiv")).val($("#condFisYear option:selected", tabObj).val());
        $("#dialogDgroffice020SortBgtDgr", $("#dialogDgroffice020SortDiv")).val($("#condBgtDgr option:selected", tabObj).val());

        $("#dialogDgroffice020SortDiv").dialog('open');
    });
    
    reportWrite020DialogDgroffice020SortCallBackFunction = function(param){
        $.csAlert({
            msg : "다시 조회하시면 변경된 실국순서로 정렬됩니다."
        });
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
        
    $("#condReportDetlCd", tabObj).change(function(){
        condReportDetlCd();
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
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("reportWrite020DialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("020");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    reportWrite020DialogDgrDeptSeltCallBack = function(param){
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
                      {id : "officeCd", subQueryId : "OfficeCd", reportCd: "020"},
                      {id : "reflectFg", codeId : "RP003"},
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
    
    var reflectFgCreateCombo = function(groupId, selectedValue){
        return getCsComboStr(comboData
                , {id: 'reflectFg'
                    , groupId: groupId
                    , selectedValue: selectedValue
                    , comboType: 'S'
                    , comboTypeValue: ''
                    });
    };
    
    doCondInit();
});
