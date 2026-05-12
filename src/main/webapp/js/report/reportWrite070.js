$(document).ready(function() {
    var tabId = _reportWrite070TabId;
    var tabObj = $("#"+tabId);
    var gridScrollPosition = 0;
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        if(rowObject.teBgtCompoId != "00000000000"){
            return ' style="vertical-align: top;"';
        }
    };
    
    reportWirte070DialogDgrcompoModifyCallBackFunction = function(param){
        var rowId = param.dgrcompoId;
        if(isEmpty(rowId) == true){
            return;
        }
        
        reportWrite070Grid.jqGrid('setRowData', rowId, param);
        
        var dgrcompoNmView = param.dgrcompoNmView;
        if(isEmpty(dgrcompoNmView) == true){
            dgrcompoNmView = "";
        }
        
        $("#dgrcompoNmView_" + rowId, tabObj).html(dgrcompoNmView);
    };
    
    reportWirte070OpenDialogDgrcompoModify = function(rowId){  
        var rowData = reportWrite070Grid.getRowData(rowId);
        
        $("#dialogDgrcompoModifyCallBackFunction", $("#dialogDgrcompoModifyDiv")).val("reportWirte070DialogDgrcompoModifyCallBackFunction");
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
        
        var rVal = '<a href="javascript:reportWirte070OpenDialogDgrcompoModify(\''+options.rowId+'\');"><span class="ui-icon ui-icon-pencil"></span></a>';
        
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
                 + '<textarea id="demandCont_'+rowObject.dgrcompoId+'" style="width:225px;ime-mode:active;height:216px;">'+demandCont+'</textarea>';
        
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
                 + '<select id="reflectFg_'+rowObject.dgrcompoId+'" title="반영구분" style="width:295px;">'
                 + reflectFgCreateCombo('RP003', rowObject.reflectFg)
                 + '</select>'+'<br>'
                 + '<textarea id="examCont_'+rowObject.dgrcompoId+'" style="width:295px;ime-mode:active;height:234px;">'+cellValue+'</textarea>'
                 + '</div>';

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
                 + '      <td><input id="dmnFrscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnFrscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnFrscAmt1)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnFrscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnFrscAmt2)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnFrscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnFrscAmt3)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnFrscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnFrscAmt4)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnFrscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnFrscAmt5)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="dmnFrscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.dmnFrscAmt6)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
        
        return rVal;
    };
    
    var considerAmtFormatter = function(cellValue, options, rowObject){        
        var readOnlyStr = "";
        var classStr = '';
        if(rowObject.teBgtCompoId == "00000000000"){
            readOnlyStr = "readonly";
            classStr = 'class="amtInput070 ui-state-disabled consider"';
        }else{
            classStr = 'class="amtInput070 ui-state-enabled consider"';
        }

        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr>'
                 + '      <td>계</td>'
                 + '      <td><input id="considerAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>시비</td>'
                 + '      <td><input id="considerAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.considerAmt1)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>국비</td>'
                 + '      <td><input id="considerAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.considerAmt2)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>교부세</td>'
                 + '      <td><input id="considerAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.considerAmt3)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>지방채</td>'
                 + '      <td><input id="considerAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.considerAmt4)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>채무</td>'
                 + '      <td><input id="considerAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.considerAmt5)+'" '+classStr+' '+readOnlyStr+' /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td>기타</td>'
                 + '      <td><input id="considerAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.considerAmt6)+'" '+classStr+' '+readOnlyStr+' /></td>'
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
                 + '      <td><input id="frscAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="frscAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.frscAmt1)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="frscAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.frscAmt2)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="frscAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.frscAmt3)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="frscAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.frscAmt4)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="frscAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.frscAmt5)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="frscAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.frscAmt6)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
    
       return rVal;
    };
    
    var diffAmtFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var rVal = '<table class="jqgridSubTable">'
                 + '  <colgroup>'
                 + '    <col width="70px"/>'
                 + '  </colgroup>'
                 + '  <tbody>'
                 + '    <tr>'
                 + '      <td><input id="diffAmt0_'+rowObject.dgrcompoId+'" value="'+addCommaStr(cellValue)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="diffAmt1_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.diffAmt1)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="diffAmt2_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.diffAmt2)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="diffAmt3_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.diffAmt3)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="diffAmt4_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.diffAmt4)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="diffAmt5_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.diffAmt5)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '    <tr>'
                 + '      <td><input id="diffAmt6_'+rowObject.dgrcompoId+'" value="'+addCommaStr(rowObject.diffAmt6)+'" class="amtInput070 ui-state-disabled" readonly /></td>'
                 + '    </tr>'
                 + '  </tbody>'
                 + '</table>';
        
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
        if(rowObject.compoLevel != 1){
            styleStr = 'style="display:none;"';
        }else{
            styleStr = 'style="width:240px;ime-mode:active;height:50px;"';
        }
        
        var rVal = '<div>'
                 + '<textarea id="srchVal_'+rowObject.dgrcompoId+'" '+styleStr+'">'+cellValue+'</textarea>'
                 + '</div>';

        return rVal;
    };
    
    var reportSortSeqFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        var styleStr = "";
        if(rowObject.compoLevel != 1){
            styleStr = 'style="display:none;width:15px;"';
        }else{

            styleStr = 'style="width:40px;"';
        }
        
        var rVal = '<div>'
                 + '<input id="reportSortSeq_'+rowObject.dgrcompoId+'" value="'+cellValue+'" maxlength="10" class="amtInput070 ui-state-enabled" '+styleStr +' />'
                 + '</div>';

        return rVal;
    };
    
    var colNames = ['', '구분(사업개요)', '정렬순서', '요구액', '심의안', '조정액', '증감액', '검토내용', '조건검색어',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'reportCd', 'reportDetlCd', 'dgrLevel', 'teBgtCompoId', 'orderYmdSeq', 'teBgtCompoSeq', 'compoLevel', 'demandCont', 'examCont', 'reflectFg', 'srchVal', 'reportSortSeq',
                    'considerAmt1', 'considerAmt2', 'considerAmt3', 'considerAmt4', 'considerAmt5', 'considerAmt6',
                    'frscAmt1', 'frscAmt2', 'frscAmt3', 'frscAmt4', 'frscAmt5', 'frscAmt6',
                    'dmnFrscAmt1', 'dmnFrscAmt2', 'dmnFrscAmt3', 'dmnFrscAmt4', 'dmnFrscAmt5', 'dmnFrscAmt6',
                    'diffAmt1', 'diffAmt2', 'diffAmt3', 'diffAmt4', 'diffAmt5', 'diffAmt6'
                   ];

    var colModel = [ {name : 'edit', index : 'edit', width : 20, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:editFormatter
                        },
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 270, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:dgrcompoNmFormatter
                        },
                        {name : 'reportSortSeqView', index : 'reportSortSeqView', width : 50, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter : reportSortSeqFormatter
                        },
                        {name : 'dmnFrscAmt0', index : 'dmnFrscAmt0', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr,
                            formatter : dmnFrscAmtFormatter
                        },
                        {name : 'considerAmt0', index : 'considerAmt0', width : 130, sortable : false, fixed : true, align : 'center', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr,
                            formatter:considerAmtFormatter
                        },
                        {name : 'frscAmt0', index : 'frscAmt0', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr,
                            formatter:frscAmtFormatter
                        },
                        {name : 'diffAmt0', index : 'diffAmt0', width : 80, sortable : false, fixed : true, align : 'center', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr,
                            formatter:diffAmtFormatter
                        },
                        {name : 'examContView', index : 'examContView', width : 300, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:examContFormatter
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
                        {name : 'orderYmdSeq', index : 'orderYmdSeq', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'compoLevel', index : 'compoLevel', width : 0, sortable : false, hidden : true},
                        {name : 'demandCont', index : 'demandCont', width : 0, sortable : false, hidden : true},
                        {name : 'examCont', index : 'examCont', width : 0, sortable : false, hidden : true},
                        {name : 'reflectFg', index : 'reflectFg', width : 0, sortable : false, hidden : true},
                        {name : 'srchVal', index : 'srchVal', width : 0, sortable : false, hidden : true},
                        {name : 'reportSortSeq', index : 'reportSortSeq', width : 0, sortable : false, hidden : true},
                        {name : 'considerAmt1', index : 'considerAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'considerAmt2', index : 'considerAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'considerAmt3', index : 'considerAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'considerAmt4', index : 'considerAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'considerAmt5', index : 'considerAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'considerAmt6', index : 'considerAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt1', index : 'frscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt2', index : 'frscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt3', index : 'frscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt4', index : 'frscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt5', index : 'frscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'frscAmt6', index : 'frscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'dmnFrscAmt1', index : 'dmnFrscAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'dmnFrscAmt2', index : 'dmnFrscAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'dmnFrscAmt3', index : 'dmnFrscAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'dmnFrscAmt4', index : 'dmnFrscAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'dmnFrscAmt5', index : 'dmnFrscAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'dmnFrscAmt6', index : 'dmnFrscAmt6', width : 0, sortable : false, hidden : true},
                        {name : 'diffAmt1', index : 'diffAmt1', width : 0, sortable : false, hidden : true},
                        {name : 'diffAmt2', index : 'diffAmt2', width : 0, sortable : false, hidden : true},
                        {name : 'diffAmt3', index : 'diffAmt3', width : 0, sortable : false, hidden : true},
                        {name : 'diffAmt4', index : 'diffAmt4', width : 0, sortable : false, hidden : true},
                        {name : 'diffAmt5', index : 'diffAmt5', width : 0, sortable : false, hidden : true},
                        {name : 'diffAmt6', index : 'diffAmt6', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 250 ? $("#mainCenter", tabObj).height() - 110 : 250;
    };
    
    var mainBodyResize = function(){
        if(isEmpty($("#REPORT_WRITE070_GRD", $("#"+tabId))) == false){
            $("#REPORT_WRITE070_GRD", $("#"+tabId)).setGridHeight(getGridHeight());
            $("#REPORT_WRITE070_GRD", $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 225,
        center__onresize: mainBodyResize
    });
    
    var reportWrite070Grid = $("#REPORT_WRITE070_GRD", tabObj);

    var setSumAmt = function(amtObj){
        var objId = amtObj.attr("id");
        
        if(isEmpty(objId) == true){
            return;
        }
        
        var idIndex = objId.indexOf("_");
        if(idIndex < 1){
            return;
        }
        
        var dgrcompoId = objId.substring(idIndex + 1);
        
        var frscObjId = objId.replaceAll("consider", "frsc");
        var diffObjId = objId.replaceAll("consider", "diff");
        
        var diffAmt = Number($("#"+frscObjId, tabObj).val().replaceAll(",", "")) - Number($("#"+objId, tabObj).val().replaceAll(",", ""));
        $("#"+diffObjId, tabObj).val(addCommaStr(diffAmt));
        
        var sumConsiderAmt = Number($("#considerAmt1_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                           + Number($("#considerAmt2_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                           + Number($("#considerAmt3_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                           + Number($("#considerAmt4_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                           + Number($("#considerAmt5_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                           + Number($("#considerAmt6_"+dgrcompoId, tabObj).val().replaceAll(",", ""));
        
        var sumDiffAmt = Number($("#diffAmt1_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                           + Number($("#diffAmt2_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                           + Number($("#diffAmt3_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                           + Number($("#diffAmt4_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                           + Number($("#diffAmt5_"+dgrcompoId, tabObj).val().replaceAll(",", ""))
                           + Number($("#diffAmt6_"+dgrcompoId, tabObj).val().replaceAll(",", ""));
        
        $("#considerAmt0_"+dgrcompoId, tabObj).val(addCommaStr(sumConsiderAmt));
        $("#diffAmt0_"+dgrcompoId, tabObj).val(addCommaStr(sumDiffAmt));
    };
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        $("#REPORT_WRITE070_GRD", tabObj).jqGrid('GridUnload');
        reportWrite070Grid = $("#REPORT_WRITE070_GRD", tabObj);
        reportWrite070Grid.csTreeGrid({
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
                $(".amtInput070.ui-state-enabled", tabObj).autoNumeric({aPad: false, vMax:'99999999999999999'});
                $(".amtInput070.ui-state-enabled", tabObj).click(function () {
                    $(this).select();
                });
                $(".amtInput070.ui-state-enabled.consider", tabObj).focusout(function(){
                    setSumAmt($(this));
                });
                
                $('textarea').maxlength({max: 1000, showFeedback: false});
            }
        });
        
        $("#REPORT_WRITE070_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
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
        var orderYmdSeq = $("#condOrderYmdSeq option:selected", tabObj).val();
        
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
                amtUnit : amtUnit,
                frscFgCdFr : frscFgCdFr,
                frscFgCdTo : frscFgCdTo,
                frscFrCdYn : frscFrCdYn,
                orderYmdSeq : orderYmdSeq
         };
        
        return param;
    };
    
    var doSearch = function(){

        gridScrollPosition = $("#REPORT_WRITE070_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite070Report070List.do",
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
        
        condOrderYmdSeqCreateCombo(fisYear + '_' + bgtDgr, '');
        
        $("#condDeptCdFr", tabObj).val("");
        $("#condDeptNmFr", tabObj).val("");
        $("#condDeptRankFr", tabObj).val("");
        $("#condDeptCdTo", tabObj).val("");
        $("#condDeptNmTo", tabObj).val("");
        $("#condDeptRankTo", tabObj).val("");
        $("#condSrchVal", tabObj).val("");
        $("#condOrderYmdSeq", tabObj).val("");
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
        var srchVal = "";
        var considerAmt1 = "";
        var considerAmt2 = "";
        var considerAmt3 = "";
        var considerAmt4 = "";
        var considerAmt5 = "";
        var considerAmt6 = "";
        var reportSortSeq = "";
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);
            if(isEmpty(rowData.dgrcompoId) == false && rowData.teBgtCompoId != "00000000000"){
                demandCont = $('#demandCont_'+rowId, tabObj).val().trim();
                examCont = $('#examCont_'+rowId, tabObj).val().trim();
                reflectFg = $('#reflectFg_'+rowId, tabObj).val();
                srchVal = $('#srchVal_'+rowId, tabObj).val();
                if(rowData.compoLevel == 1){
                    reportSortSeq = $('#reportSortSeq_'+rowId, tabObj).val();
                }
                considerAmt1 = $('#considerAmt1_'+rowId, tabObj).val().replaceAll(",", "");
                considerAmt2 = $('#considerAmt2_'+rowId, tabObj).val().replaceAll(",", "");
                considerAmt3 = $('#considerAmt3_'+rowId, tabObj).val().replaceAll(",", "");
                considerAmt4 = $('#considerAmt4_'+rowId, tabObj).val().replaceAll(",", "");
                considerAmt5 = $('#considerAmt5_'+rowId, tabObj).val().replaceAll(",", "");
                considerAmt6 = $('#considerAmt6_'+rowId, tabObj).val().replaceAll(",", "");
                
                if(rowData.demandCont != demandCont
                        || rowData.examCont != examCont
                        || rowData.reflectFg != reflectFg
                        || rowData.srchVal != srchVal
                        || rowData.reportSortSeq != reportSortSeq
                        || rowData.considerAmt1 != considerAmt1
                        || rowData.considerAmt2 != considerAmt2
                        || rowData.considerAmt3 != considerAmt3
                        || rowData.considerAmt4 != considerAmt4
                        || rowData.considerAmt5 != considerAmt5
                        || rowData.considerAmt6 != considerAmt6){
                    
                    saveData = {};
                    saveData["fisYear"] = rowData.fisYear;
                    saveData["bgtDgr"] = rowData.bgtDgr;
                    saveData["reportCd"] = rowData.reportCd;
                    saveData["reportDetlCd"] = rowData.reportDetlCd;
                    saveData["teBgtCompoId"] = rowData.teBgtCompoId;
                    saveData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
                    saveData["orderYmdSeq"] = rowData.orderYmdSeq;
                    saveData["demandCont"] = demandCont;
                    saveData["examCont"] = examCont;
                    saveData["reflectFg"] = reflectFg;
                    saveData["srchVal"] = srchVal;
                    saveData["srchValYn"] = rowData.srchVal != srchVal ? "Y" : "N";
                    saveData["reportSortSeq"] = reportSortSeq;
                    saveData["reportSortSeqYn"] = rowData.reportSortSeq != reportSortSeq ? "Y" : "N";
                    saveData["considerAmt1"] = considerAmt1;
                    saveData["considerAmt2"] = considerAmt2;
                    saveData["considerAmt3"] = considerAmt3;
                    saveData["considerAmt4"] = considerAmt4;
                    saveData["considerAmt5"] = considerAmt5;
                    saveData["considerAmt6"] = considerAmt6;
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
        
        var saveDatas = getSaveDatas(reportWrite070Grid, $("#REPORT_WRITE070_GRD", tabObj)[0].rows);
        if(isEmpty(saveDatas) == true || saveDatas.length < 1){
            $.csAlert({
                msg : "변경된 자료가 존재하지 않습니다."
            });
            
            return;
        }
        
        $.csAjaxCall({
            url : "/report/ajaxReportWrite070SaveReport070.do",
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
        param["fileNm"] = "지시사항조치계획";
        param["amtUnit"] = "1000000";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite070SaveFile.do"
          , data: param
        });
    });
    
    $("#saveSheetBtn", tabObj).click(function() {
        var param = getSearchParam();
        param.orderYmdSeq = "";
        param["fileNm"] = "지시사항조치계획";
        param["amtUnit"] = "1000000";
        
        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite070SaveSheet.do"
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
        
        condOrderYmdSeqCreateCombo(fisYear + '_' + bgtDgr, '');
        
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
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("reportWrite070DialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("070");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    reportWrite070DialogDgrDeptSeltCallBack = function(param){
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
                      {id : "officeCd", subQueryId : "OfficeCd", reportCd: "070"},
                      {id : "frscFgCd", subQueryId : "FrscFgCd"},
                      {id : "reflectFg", codeId : "RP003"},
                      {id : "orderYmdSeq", subQueryId : "OrderYmdSeq"}
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
    
    var reflectFgCreateCombo = function(groupId, selectedValue){
        return getCsComboStr(comboData
                , {id: 'reflectFg'
                    , groupId: groupId
                    , selectedValue: selectedValue
                    , comboType: 'S'
                    , comboTypeValue: ''
                    });
    };
    
    var condOrderYmdSeqCreateCombo = function(groupId, selectedValue){
        $("#condOrderYmdSeq", tabObj).csCreatCombo(comboData
                , {id: 'orderYmdSeq'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: ''
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
