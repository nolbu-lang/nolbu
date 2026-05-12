<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    
    var dialogObj = $("#dialogDgrcompoCngLogDiv");
    
    var sepNowCnt = 0;
    var sepData;
    var tabNm;
    
    var dialogDgrcompoCngLogFrscColNames = ['재원명', '전년도예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'frscFgCd', 'standFrscCd'];
    var dialogDgrcompoCngLogFrscColModel = [
                                    {name : 'frscFgNm', index : 'frscFgNm', width : 80, sortable : false, fixed : true, align : 'left'},
                                    {name : 'preFrscAmt', index : 'preFrscAmt', width : 80, sortable : false, fixed : true, align : 'right'
                                    	,formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'preDefFrscAmt', index : 'preDefFrscAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                        editable : false, edittype:'text',
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'dmnDefFrscAmt', index : 'dmnDefFrscAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                        editable : false, edittype:'text', 
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'demandFrscAmt', index : 'demandFrscAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                    {name : 'adjDefFrscAmt', index : 'adjDefFrscAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                        editable : false, edittype:'text',
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'frscAmt', index : 'frscAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                    
                                    {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'frscFgCd', index : 'frscFgCd', width : 0, sortable : false, fixed : true, hidden : true, key : true},
                                    {name : 'standFrscCd', index : 'standFrscCd', width : 0, sortable : false, fixed : true, hidden : true}
    ];
    
    var dialogDgrcompoCngLogCharColNames = ['성격명', '전년도예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'charFgCd'];
    var dialogDgrcompoCngLogCharColModel = [
                                    {name : 'charFgNm', index : 'charFgNm', width : 80, sortable : false, fixed : true, align : 'left'},
                                    {name : 'preCharAmt', index : 'preCharAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                        editable : false, edittype:'text', 
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'preDefCharAmt', index : 'preDefCharAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                        editable : false, edittype:'text', 
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'dmnDefCharAmt', index : 'dmnDefCharAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                        editable : false, edittype:'text', 
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'demandCharAmt', index : 'demandCharAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                    {name : 'adjDefCharAmt', index : 'adjDefCharAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                        editable : false, edittype:'text', 
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'charAmt', index : 'charAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                    {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'charFgCd', index : 'charFgCd', width : 0, sortable : false, fixed : true, hidden : true, key : true}
    ];
    
    var dialogDgrcompoCngLogFrscColNamesEdit = ['재원명', '전년도예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'frscFgCd', 'standFrscCd', 'sepNum'];
    var dialogDgrcompoCngLogFrscColModelEdit = [
                                                  {name : 'frscFgNm', index : 'frscFgNm', width : 80, sortable : false, fixed : true, align : 'left'},
                                                  {name : 'preFrscAmt', index : 'preFrscAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : false, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'preDefFrscAmt', index : 'preDefFrscAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : false, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'dmnDefFrscAmt', index : 'dmnDefFrscAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : false, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'demandFrscAmt', index : 'demandFrscAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                                  {name : 'adjDefFrscAmt', index : 'adjDefFrscAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : false, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'frscAmt', index : 'frscAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                                  {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, fixed : true, hidden : true },
                                                  {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, fixed : true, hidden : true },
                                                  {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, fixed : true, hidden : true },
                                                  {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, fixed : true, hidden : true },
                                                  {name : 'frscFgCd', index : 'frscFgCd', width : 0, sortable : false, fixed : true, hidden : true, key : true},
                                                  {name : 'standFrscCd', index : 'standFrscCd', width : 0, sortable : false, fixed : true, hidden : true},
                                                  {name : 'sepNum', index : 'sepNum', width : 0, sortable : false, fixed : true, hidden : true}
                  ];
    
    var dialogDgrcompoCngLogCharColNamesEdit = ['성격명', '전년도예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'charFgCd', 'sepNum'];
    var dialogDgrcompoCngLogCharColModelEdit = [
                                                  {name : 'charFgNm', index : 'charFgNm', width : 80, sortable : false, fixed : true, align : 'left'},
                                                  {name : 'preCharAmt', index : 'preCharAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : false, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'preDefCharAmt', index : 'preDefCharAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : false, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'dmnDefCharAmt', index : 'dmnDefCharAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : false, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'demandCharAmt', index : 'demandCharAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                                  {name : 'adjDefCharAmt', index : 'adjDefCharAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : false, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'charAmt', index : 'charAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                                  {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, fixed : true, hidden : true },
                                                  {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, fixed : true, hidden : true },
                                                  {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, fixed : true, hidden : true },
                                                  {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, fixed : true, hidden : true },
                                                  {name : 'charFgCd', index : 'charFgCd', width : 0, sortable : false, fixed : true, hidden : true, key : true},
                                                  {name : 'sepNum', index : 'sepNum', width : 0, sortable : false, fixed : true, hidden : true}
                  ];
    
    //layoutResize
    var logBodyResize = function () {
    	
    	$('#sepWestDiv1 .viewDiv', dialogObj).width($("#sepWestDiv1", dialogObj).width());
        if(isEmpty($("#DIALOG_DGR_COMPO_CNG_LOG_FRSC_GRD", dialogObj)) == false){
            $("#DIALOG_DGR_COMPO_CNG_LOG_FRSC_GRD", dialogObj).setGridWidth($("#sepWestDiv2", dialogObj).width());
        }
        
        if(isEmpty($("#DIALOG_DGR_COMPO_CNG_LOG_CHAR_GRD", dialogObj)) == false){
            $("#DIALOG_DGR_COMPO_CNG_LOG_CHAR_GRD", dialogObj).setGridWidth($("#sepWestDiv3", dialogObj).width());
        }
        
        logBodySepResize();
    };
    
    //재원별 예산액 금액 수정시 실행
    var afterSaveFrsc = function(gridObject, name, cnt){
        if(name !== "preDefFrscAmt" && name !== "dmnDefFrscAmt" && name !== "adjDefFrscAmt" && name !== "preFrscAmt"){
            return;
        }

        gridObject = $('#DIALOG_DGR_COMPO_CNG_LOG_FRSC' + cnt + '_GRD', dialogObj);
        
        var rowData;
        var ids = gridObject.jqGrid("getDataIDs");
        var preDefFrscAmt = 0;
        var dmnDefFrscAmt = 0;
        var demandFrscAmt = 0;
        var adjDefFrscAmt = 0;
        var frscAmt = 0;
        var preFrscAmt = 0;
        
        var sumPreDefFrscAmt = 0;
        var sumDmnDefFrscAmt = 0;
        var sumDemandFrscAmt = 0;
        var sumAdjDefFrscAmt = 0;
        var sumFrscAmt = 0;
        var sumPreFrscAmt = 0;
        
        for(var i = 0; i < ids.length; i++){
            rowData = gridObject.jqGrid("getRowData", ids[i]);
            preDefFrscAmt = Number(rowData.preDefFrscAmt);
            dmnDefFrscAmt = Number(rowData.dmnDefFrscAmt);
            demandFrscAmt = preDefFrscAmt + dmnDefFrscAmt;
            adjDefFrscAmt = Number(rowData.adjDefFrscAmt);
            frscAmt = preDefFrscAmt + adjDefFrscAmt;
            preFrscAmt = Number(rowData.preFrscAmt);
            
            gridObject.jqGrid("setCell", ids[i], "demandFrscAmt", demandFrscAmt);
            gridObject.jqGrid("setCell", ids[i], "frscAmt", frscAmt);

            if(rowData.frscFgCd < "200"
                    || rowData.frscFgCd === "500"){
                sumPreDefFrscAmt += preDefFrscAmt;
                sumDmnDefFrscAmt += dmnDefFrscAmt;
                sumDemandFrscAmt += demandFrscAmt;
                sumAdjDefFrscAmt += adjDefFrscAmt;
                sumFrscAmt += frscAmt;
                sumPreFrscAmt += preFrscAmt;
            }
        }
        
        $("#dialogDgrcompoCngLogDemandBgtAmt" + cnt, dialogObj).val(addCommaStr((sumPreDefFrscAmt + sumDmnDefFrscAmt)));
        $("#dialogDgrcompoCngLogDemandPreAmt" + cnt, dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoCngLogDemandDiffAmt" + cnt, dialogObj).val(addCommaStr(sumDmnDefFrscAmt));
        $("#dialogDgrcompoCngLogBgtAmt" + cnt, dialogObj).val(addCommaStr(sumPreDefFrscAmt + sumAdjDefFrscAmt));
        $("#dialogDgrcompoCngLogPreAmt" + cnt, dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoCngLogDiffAmt" + cnt, dialogObj).val(addCommaStr(sumAdjDefFrscAmt));
        $("#dialogDgrcompoCngLogPreBgtAmt" + cnt, dialogObj).val(addCommaStr(sumPreFrscAmt));

        var tabCharGrid = $('#DIALOG_DGR_COMPO_CNG_LOG_CHAR' + cnt + '_GRD', dialogObj);
        
        afterSaveChar(tabCharGrid, "preDefCharAmt", cnt);
    };
    
    //성격별예산액 금액 수정시 실행
    var afterSaveChar = function(gridObject, name, cnt){
        if(name !== "preDefCharAmt" && name !== "dmnDefCharAmt" && name !== "adjDefCharAmt" && name !== "preCharAmt"){
            return;
        }

        var rowData;
        var ids = gridObject.jqGrid("getDataIDs");
        var preDefCharAmt = 0;
        var dmnDefCharAmt = 0;
        var demandCharAmt = 0;
        var adjDefCharAmt = 0;
        var charAmt = 0;
        var preCharAmt = 0;
        
        var sumPreDefCharAmt = 0;
        var sumDmnDefCharAmt = 0;
        var sumDemandCharAmt = 0;
        var sumAdjDefCharAmt = 0;
        var sumCharAmt = 0;
        var sumPreCharAmt = 0;
        
        for(var i = 0; i < ids.length; i++){
            if(ids[i] !== '090'){
                rowData = gridObject.jqGrid("getRowData", ids[i]);
                preDefCharAmt = Number(rowData.preDefCharAmt);
                dmnDefCharAmt = Number(rowData.dmnDefCharAmt);
                demandCharAmt = preDefCharAmt + dmnDefCharAmt;
                adjDefCharAmt = Number(rowData.adjDefCharAmt);
                charAmt = preDefCharAmt + adjDefCharAmt;
                preCharAmt = Number(rowData.preCharAmt);
                
                sumPreDefCharAmt += preDefCharAmt;
                sumDmnDefCharAmt += dmnDefCharAmt;
                sumDemandCharAmt += demandCharAmt;
                sumAdjDefCharAmt += adjDefCharAmt;
                sumCharAmt += charAmt;
                sumPreCharAmt += preCharAmt;
                
                gridObject.jqGrid("setCell", ids[i], "demandCharAmt", demandCharAmt);
                gridObject.jqGrid("setCell", ids[i], "charAmt", charAmt);
            }
        }

        var preDefFrscAmt = 0;
        var dmnDefFrscAmt = 0;
        var demandFrscAmt = 0;
        var adjDefFrscAmt = 0;
        var frscAmt = 0;
        var preFrscAmt = 0;
        
        var sumPreDefFrscAmt = 0;
        var sumDmnDefFrscAmt = 0;
        var sumDemandFrscAmt = 0;
        var sumAdjDefFrscAmt = 0;
        var sumFrscAmt = 0;
        var sumPreFrscAmt = 0;
        
        var frscGridObject = $('#DIALOG_DGR_COMPO_CNG_LOG_FRSC' + cnt + '_GRD', dialogObj);
        ids = frscGridObject.jqGrid("getDataIDs");
        for(var i = 0; i < ids.length; i++){
            rowData = frscGridObject.jqGrid("getRowData", ids[i]);
            preDefFrscAmt = Number(rowData.preDefFrscAmt);
            dmnDefFrscAmt = Number(rowData.dmnDefFrscAmt);
            demandFrscAmt = preDefFrscAmt + dmnDefFrscAmt;
            adjDefFrscAmt = Number(rowData.adjDefFrscAmt);
            frscAmt = preDefFrscAmt + adjDefFrscAmt;
            preFrscAmt = Number(rowData.preFrscAmt);
            
            sumPreDefFrscAmt += preDefFrscAmt;
            sumDmnDefFrscAmt += dmnDefFrscAmt;
            sumDemandFrscAmt += demandFrscAmt;
            sumAdjDefFrscAmt += adjDefFrscAmt;
            sumFrscAmt += frscAmt;
            sumPreFrscAmt += preFrscAmt;
        }

        var demandBgtAmt = sumPreDefFrscAmt + sumDmnDefFrscAmt;
        var demandDiffAmt = sumDmnDefFrscAmt;
        var bgtAmt = sumPreDefFrscAmt + sumAdjDefFrscAmt;
        var preAmt = sumPreDefFrscAmt;
        var diffAmt = sumAdjDefFrscAmt;
        var preBgtAmt = sumPreFrscAmt;
        
        var preDefCharAmt090 = preAmt - sumPreDefCharAmt;
        var dmnDefCharAmt090 = demandDiffAmt - sumDmnDefCharAmt;
        var demandCharAmt090 = demandBgtAmt - sumDemandCharAmt;
        var adjDefCharAmt090 = diffAmt - sumAdjDefCharAmt;
        var charAmt090 = bgtAmt - sumCharAmt;
        var preCharAmt090 = preBgtAmt - sumPreCharAmt;
        
        gridObject.jqGrid("setCell", "090", "preDefCharAmt", preDefCharAmt090);
        gridObject.jqGrid("setCell", "090", "dmnDefCharAmt", dmnDefCharAmt090);
        gridObject.jqGrid("setCell", "090", "demandCharAmt", demandCharAmt090);
        gridObject.jqGrid("setCell", "090", "adjDefCharAmt", adjDefCharAmt090);
        gridObject.jqGrid("setCell", "090", "charAmt", charAmt090);
        gridObject.jqGrid("setCell", "090", "preCharAmt", preCharAmt090);
    };
    
    //원본 재원별예산액 그리드 설정
    var frscEditIRow = 0;
    var frscEditICol = 0;
    var dialogDgrcompoCngLogFrscGridParam = {
            id : "DIALOG_DGR_COMPO_CNG_LOG_FRSC",
            colNames : dialogDgrcompoCngLogFrscColNames,
            colModel : dialogDgrcompoCngLogFrscColModel,
            cellEdit: true,
            cellsubmit : "clientArray",
            defaultRows : 5,
            rowNum : 1000,
            width: "auto",
            height: "auto",
            beforeEditCell : function (owid, cellname, value, iRow, iCol){
                frscEditIRow = iRow;
                frscEditICol = iCol;
            },
            afterSaveCell : function(rowid,name,val,iRow,iCol) {
                //afterSaveFrsc(dialogDgrcompoCngLogFrscGrid, name);
                
                frscEditIRow = 0;
                frscEditICol = 0;
            }
    };
    
    //원본 재원별예산액 그리드
    var dialogDgrcompoCngLogFrscGrid = $.csGrid(dialogDgrcompoCngLogFrscGridParam);
    dialogDgrcompoCngLogFrscGrid.jqGrid('setGroupHeaders', {
        useColSpanStyle : true,
        groupHeaders : [
           {startColumnName : 'dmnDefFrscAmt',numberOfColumns : 2, titleText : '요구'},
           {startColumnName : 'adjDefFrscAmt', numberOfColumns : 2, titleText : '조정'} 
        ]
    });

    //원본 성격별예산액 그리드 설정
    var charEditIRow = 0;
    var charEditICol = 0;
    var dialogDgrcompoCngLogCharGridParam = {
            id : "DIALOG_DGR_COMPO_CNG_LOG_CHAR",
            colNames : dialogDgrcompoCngLogCharColNames,
            colModel : dialogDgrcompoCngLogCharColModel,
            cellEdit : true,
            cellsubmit : "clientArray",
            defaultRows : 3,
            rowNum : 1000,
            autowidth:true,
            height: "auto",
            beforeEditCell : function (owid, cellname, value, iRow, iCol){
                charEditIRow = iRow;
                charEditICol = iCol;
            },
            afterSaveCell : function(rowid,name,val,iRow,iCol) {
                //afterSaveChar(dialogDgrcompoCngLogCharGrid, name);
                
                charEditIRow = 0;
                charEditICol = 0;
            }
    };
    
    //원본 성격별예산액 그리드
    var dialogDgrcompoCngLogCharGrid = $.csGrid(dialogDgrcompoCngLogCharGridParam);
    dialogDgrcompoCngLogCharGrid.jqGrid('setGroupHeaders', {
        useColSpanStyle : true,
        groupHeaders : [
           {startColumnName : 'dmnDefCharAmt',numberOfColumns : 2, titleText : '요구'},
           {startColumnName : 'adjDefCharAmt', numberOfColumns : 2, titleText : '조정'} 
        ]
    });
    
    //처음 데이터 로딩시 분리 탭 추가시 사용할 데이터 초기화
    function initData(data){
    	
    	data.dgrcompo.preAmt = 0;
    	data.dgrcompo.demandBgtAmt = 0;
        data.dgrcompo.demandDiffAmt = 0;
        data.dgrcompo.bgtAmt = 0;
        data.dgrcompo.preFormular = 0;
        data.dgrcompo.preAmt = 0;
        data.dgrcompo.diffAmt = 0;
        data.dgrcompo.preCompFormular = 0;
        data.dgrcompo.preBgtAmt = 0;
        
    	//재원별 예산액 금액 0원으로 초기화
    	var frscInfo = data.frscInfo.dataList;
    	for(var i=0 ; i<frscInfo.length ; i++){
    		frscInfo[i].adjDefFrscAmt = 0;
    		frscInfo[i].demandFrscAmt = 0;
    		frscInfo[i].dmnDefFrscAmt = 0;
    		frscInfo[i].frscAmt = 0;
    		frscInfo[i].preDefFrscAmt = 0;
    		frscInfo[i].preFrscAmt = 0;
    	}
    	//재원별 예산액 금액 0원으로 초기화
    	var charInfo = data.charInfo.dataList;
    	for(var i=0 ; i<charInfo.length ; i++){
    		charInfo[i].adjDefCharAmt = 0;
    		charInfo[i].charAmt = 0;
    		charInfo[i].demandCharAmt = 0;
    		charInfo[i].dmnDefCharAmt = 0;
    		charInfo[i].preCharAmt = 0;
    		charInfo[i].preDefCharAmt = 0;
    	}
    	
    	tabNm = data.dgrcompo.compGround; //탭명
    	
    	return data;
    }
    
    //원본 조회 완료
    var doDialogDgrcompoCngLogSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        var dataList = data.logData.dataList;
        for(var i=0 ; i<dataList.length ; i++){
        	if(i == 0){
        		var parentData = dataList[i];
        		$("#dialogDgrcompoCngLogTeBgtCompoSeq", dialogObj).val(parentData.dgrcompo.teBgtCompoSeq);
                //$("#dialogDgrcompoCngLogDgrcompoNm", dialogObj).val(parentData.dgrcompo.dgrcompoNm);
                $("#dialogDgrcompoCngLogDbizNm", dialogObj).val(parentData.dgrcompo.upDgrcompoNm);
                $("#dialogDgrcompoCngLogTeMngMokCd", dialogObj).val(parentData.dgrcompo.teMngMokCd);
                $("#dialogDgrcompoCngLogTeMngMokNm", dialogObj).val(parentData.dgrcompo.teMngMokNm);
                $("#dialogDgrcompoCngLogDemandCompFormular", dialogObj).val(parentData.dgrcompo.demandCompFormular);
                $("#dialogDgrcompoCngLogDemandBgtAmt", dialogObj).val(addCommaStr(parentData.dgrcompo.demandBgtAmt));
                $("#dialogDgrcompoCngLogDemandPreAmt", dialogObj).val(addCommaStr(parentData.dgrcompo.preAmt));
                $("#dialogDgrcompoCngLogDemandDiffAmt", dialogObj).val(addCommaStr(parentData.dgrcompo.demandDiffAmt));
                $("#dialogDgrcompoCngLogCompGround", dialogObj).val(parentData.dgrcompo.compGround);
                $("#dialogDgrcompoCngLogCompFormular", dialogObj).val(parentData.dgrcompo.compFormular);
                $("#dialogDgrcompoCngLogBgtAmt", dialogObj).val(addCommaStr(parentData.dgrcompo.bgtAmt));
                $("#dialogDgrcompoCngLogPreFormular", dialogObj).val(parentData.dgrcompo.preFormular);
                $("#dialogDgrcompoCngLogPreAmt", dialogObj).val(addCommaStr(parentData.dgrcompo.preAmt));
                $("#dialogDgrcompoCngLogDiffAmt", dialogObj).val(addCommaStr(parentData.dgrcompo.diffAmt));
                $("#dialogDgrcompoCngLogCompoLevel", dialogObj).val(parentData.dgrcompo.compoLevel);
                $("#dialogDgrcompoCngLogPreCompFormular", dialogObj).val(parentData.dgrcompo.preCompFormular);
                $("#dialogDgrcompoCngLogPreBgtAmt", dialogObj).val(addCommaStr(parentData.dgrcompo.preBgtAmt));
                
                dialogDgrcompoCngLogFrscGrid.addCsJsonData(parentData.frscInfo);
                dialogDgrcompoCngLogCharGrid.addCsJsonData(parentData.charInfo);
        	}else{
        		var childData = dataList[i];
        		addBtn(dataList[i]);
        	}
        }
        
    };
    
    //원본 조회시작
    var doDialogDgrcompoCngLogSearch = function() {
        var unitStr = "(단위:천원)";
        if($("#dialogDgrcompoCngLogAmtUnit", dialogObj).val() == "1"){
            unitStr = "(단위:원)";
        }else if($("#dialogDgrcompoCngLogAmtUnit", dialogObj).val() == "1000"){
            unitStr = "(단위:천원)";
        }else if($("#dialogDgrcompoCngLogAmtUnit", dialogObj).val() == "1000000"){
            unitStr = "(단위:백만원)";
        }
    
        var cngType = $('#dialogDgrcompoCngLogCngType').val();
        if(cngType == 'CH01'){
        	$('#parentLogTitle').html('선택한 개별사업');
        	$('#childLogTitle').html('병합된 개별사업');	
        }else if(cngType == 'CH02'){
        	$('#parentLogTitle').html('원본 개별사업');
        	$('#childLogTitle').html('분리된 개별사업');
        }else if(cngType == 'CH03'){
        	$('#parentLogTitle').html('선택한 개별사업');
        	$('#childLogTitle').html('병합된 개별사업');
        }
        
        $("#dialogDgrcompoCngLogAmtUnitDiv", dialogObj).html(unitStr);
        
        $.csAjaxCall({
            url : "/dialog/ajaxDgrCngLogData.do",
            data : {
                    cngHistoryId: $("#dialogDgrcompoCngLogCngHistoryId", dialogObj).val(),
                    cngType: $("#dialogDgrcompoCngLogCngType", dialogObj).val(),
                    amtUnit: $("#dialogDgrcompoCngLogAmtUnit", dialogObj).val()
                   },
            async : true,
            callBack : doDialogDgrcompoCngLogSearchCallBack
        });
    };
    
    //창닫기
    var dialogDgrcompoCngLogClose = function(){
    	var tabCnt = parseInt($('#dialogDgrcompoCngLogTabCnt').val()); 
    	for(var i=0 ; i<tabCnt ; i++){
    		var panelId = $("#cngTabTitle" + i).closest("li").remove().attr("aria-controls");
    		console.log(panelId);
            $("#" + panelId).remove();
            console.log(i);
    	}
    	
    	logTab.tabs("refresh");
    	$('#dialogDgrcompoCngLogTabCnt').val(1);
        $("#dialogDgrcompoCngLogCallBackFunction", dialogObj).val("");
        
        $("#dialogDgrcompoCngLogDiv").dialog("close");
    };
    
    //분리창 오픈시 세팅
    function openDialog(){
        
    	$("#logBody", dialogObj).layout({
            west__size : "50%",
            center__onresize: logBodyResize
        });
    	
    	logBodyResize();
    	
    	$('.ui-icon-close', dialogObj).each(function(){
    		$(this).trigger("click")
    	});
    	
    	$('#dialogDgrcompoCngLogTabCnt').val(1);
    	
    	$("#addBtn", dialogObj).btnChangeState(true);
    	
    }

    //다이얼로그 실행
    $("#dialogDgrcompoCngLogDiv").dialog({
        title: "이력관리",
        autoOpen: false,
        width: 1500,
        height: 925,
        modal: true,
        resizable: true,
        open: function(event, ui){
            doDialogDgrcompoCngLogSearch();
            openDialog();
        },
        buttons : {
            "목록" : function() {
                dialogDgrcompoCngLogClose();
            }
        }
    });

    $('#dialogDgrcompoCngLogDiv').on('dialogclose', function(event) {
    	dialogDgrcompoCngLogClose();
    });
    
    //분리 탭 세팅
    var logTab = $("#logTab").tabs();
    
    //추가 버튼
    function addBtn(data){
    	var tabCnt = parseInt($('#dialogDgrcompoCngLogTabCnt').val());
        sepNowCnt = tabCnt;
        
        var tabLength = $('div[id^=cngTab_]', $('#logTab')).length;
        
        if(tabLength > 9){
        	$.csAlert({
                msg : '추가는 10개까지만 가능합니다.'
            });
        	return false;
        }
        var id = 'cngTab_' + tabCnt;
        var label = tabNm + '_' + tabCnt;
        var tabTemplate = "<li><a href='!{href}' id='cngTabTitle" + tabCnt + "'>!{label}</a></li>";
        var li = $(tabTemplate.replace(/!\{href\}/g, "#" + id).replace(/!\{label\}/g, label));

        logTab.find(".ui-tabs-nav").append(li);
        logTab.append('<div id="' + id + '" style="height:100%;overflow: auto;">' + getSepHtml(tabCnt) + '</div>');
        
        logTab.tabs("refresh");
        
        $('#dialogDgrcompoCngLogTabCnt').val(tabCnt + 1);
        
        setSepGridData(tabCnt, data);
        
        $("[href='#"+id+"']", dialogObj).trigger("click");
    }
    
    //탭 닫기 실행
	logTab.delegate("span.ui-icon-close", "click", function() {
        var panelId = $(this).closest("li").remove().attr("aria-controls");
        $("#" + panelId).remove();
        logTab.tabs("refresh");
    });
	
    //탭추가시 내용 입력
	function getSepHtml(cnt){
		var sepHtml = '<div id="sepWestDiv1' + cnt + '"  style="padding-bottom:20px;margin-top:5px;">';
		sepHtml += '<div id="dialogDgrcompoCngLogAmtUnitDiv' + cnt + '" class="unitDiv" style="top: 0px;">';
		sepHtml += '(단위:천원)';
		sepHtml += '</div>';
		sepHtml += '<div class="viewDiv" style="width:690px;">';
		sepHtml += '<table>';
		sepHtml += '<colgroup>';
		sepHtml += '<col width="70px"/>';
		sepHtml += '<col width="80px"/>';
		sepHtml += '<col width="120px"/>';
		sepHtml += '<col width="90px"/>';
		sepHtml += '<col width="120px"/>';
		sepHtml += '<col width="90px"/>';
		sepHtml += '<col width="120px"/>';
		sepHtml += '</colgroup>';
		sepHtml += '<tbody>';
      
		sepHtml += '<tr>';
		sepHtml += '<th colspan="2">상위항목</th>';
		sepHtml += '<td colspan="5">';
		sepHtml += '<input type="hidden" id="dialogDgrcompoCngLogTeBgtCompoSeq' + cnt + '"/>';
		sepHtml += '<input type="hidden" id="dialogDgrcompoCngLogDbizCd' + cnt + '"/>';
		//sepHtml += "<a class='btnClass' href='#none' onclick='javascript:getDbizData(" + cnt + ");' enabledYn='N'>검색</a>";
		sepHtml += '<input type="text" id="dialogDgrcompoCngLogDbizNm' + cnt + '" class="readonly" style="width:90%;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '</tr>';
		sepHtml += '<tr>';
		sepHtml += '<th colspan="2">통계목코드</th>';
        sepHtml += '<td>';
        sepHtml += '<input type="text" id="dialogDgrcompoCngLogTeMngMokCd' + cnt + '" class="readonly" style="width:100%;" readonly/>';
        sepHtml += '</td>';
        sepHtml += '<th>통계목명</th>';
        sepHtml += '<td colspan="3">';
        sepHtml += '<input type="text" id="dialogDgrcompoCngLogTeMngMokNm' + cnt + '" class="readonly" style="width:100%;" readonly/>';
        sepHtml += '</td>';
        sepHtml += '</tr>';
		sepHtml += '<tr>';
		sepHtml += '<th colspan="2">사업명</th>';
		sepHtml += '<td colspan="5">';
		sepHtml += '<input type="text" id="dialogDgrcompoCngLogCompGround' + cnt + '" style="width:100%;" readonly />';
		sepHtml += '</td>';
		sepHtml += '</tr>';
		sepHtml += '<tr>';
		sepHtml += '<th>요구</th>';
		sepHtml += '<th>기정액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoCngLogDemandPreAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '<th>증감액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoCngLogDemandDiffAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '<th>요구액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoCngLogDemandBgtAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '</tr>';
		sepHtml += '<tr>';
		sepHtml += '<th>조정</th>';
		sepHtml += '<th>기정액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoCngLogPreAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '<th>증감액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoCngLogDiffAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '<th>조정액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoCngLogBgtAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '</tr>';
		sepHtml += '<tr>';
		sepHtml += '<th>전년도</th>';
		sepHtml += '<th>예산액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoCngLogPreBgtAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '<td>&nbsp;</td>';
		sepHtml += '<td>&nbsp;</td>';
		sepHtml += '<td>&nbsp;</td>';
		sepHtml += '<td>&nbsp;</td>';
		sepHtml += '</tr>';
		sepHtml += '</tbody>';
		sepHtml += '</table>';
		sepHtml += '</div>';
		sepHtml += '</div>';
		sepHtml += '<div id="sepWestDiv2' + cnt + '" style="padding-bottom:20px;">';
		sepHtml += '<div class="ui-widget-header">';
		sepHtml += '재원별 예산액';
		sepHtml += '</div>';
		sepHtml += '<div id="DIALOG_DGR_COMPO_CNG_LOG_FRSC' + cnt + '_DIV" class="csGrid">';
		sepHtml += '<table id="DIALOG_DGR_COMPO_CNG_LOG_FRSC' + cnt + '_GRD"  style="border:0px;height:100%;"></table>';
		sepHtml += '</div>';
		sepHtml += '</div>';
		sepHtml += '<div id="sepWestDiv3' + cnt + '">';
		sepHtml += '<div class="ui-widget-header">';
		sepHtml += '성격별 예산액';
		sepHtml += '</div>';
		sepHtml += '<div id="DIALOG_DGR_COMPO_CNG_LOG_CHAR' + cnt + '_DIV" class="csGrid">';
		sepHtml += '<table id="DIALOG_DGR_COMPO_CNG_LOG_CHAR' + cnt + '_GRD" ></table>';
		sepHtml += '</div>';
		sepHtml += '</div>';
		
		return sepHtml;
	}
	
    //탭 리사이즈
	var logBodySepResize = function () {
		var cntSize = parseInt($('#dialogDgrcompoCngLogTabCnt').val());
		
		var width = $("#logTab", dialogObj).width() - (13.2 * 2); // 2em padding 빼기
		for(var i=0 ; i<cntSize ; i++){
			
			$('#sepWestDiv1' + i + ' .viewDiv', dialogObj).width(width);
			if(isEmpty($("#DIALOG_DGR_COMPO_CNG_LOG_FRSC" + i + "_GRD", dialogObj)) == false){
	             $("#DIALOG_DGR_COMPO_CNG_LOG_FRSC" + i + "_GRD", dialogObj).setGridWidth(width);
	        }
	         
	        if(isEmpty($("#DIALOG_DGR_COMPO_CNG_LOG_CHAR" + i + "_GRD", dialogObj)) == false){
	             $("#DIALOG_DGR_COMPO_CNG_LOG_CHAR" + i + "_GRD", dialogObj).setGridWidth(width);
	        }
	        
		}
         
     };
     
     //탭 그리드 세팅
	function setSepGridData(cnt, data){
		
		var dialogDgrcompoCngLogFrscGridSep = $.csGrid( {
            id : "DIALOG_DGR_COMPO_CNG_LOG_FRSC" + cnt,
            colNames : dialogDgrcompoCngLogFrscColNamesEdit,
            colModel : dialogDgrcompoCngLogFrscColModelEdit,
            cellEdit: true,
            cellsubmit : "clientArray",
            defaultRows : 5,
            rowNum : 1000,
            width: "auto",
            height: "auto",
            beforeEditCell : function (owid, cellname, value, iRow, iCol){
                frscEditIRow = iRow;
                frscEditICol = iCol;
            },
            afterSaveCell : function(rowid,name,val,iRow,iCol) {
                afterSaveFrsc(dialogDgrcompoCngLogFrscGridSep, name, cnt);
                
                frscEditIRow = 0;
                frscEditICol = 0;
            },
            loadComplete: function (data) {
            	//logBodyResize();
            }
    	});
		
	    dialogDgrcompoCngLogFrscGridSep.jqGrid('setGroupHeaders', {
	        useColSpanStyle : true,
	        groupHeaders : [
	           {startColumnName : 'dmnDefFrscAmt',numberOfColumns : 2, titleText : '요구'},
	           {startColumnName : 'adjDefFrscAmt', numberOfColumns : 2, titleText : '조정'} 
	        ]
	    });
	    
	    var dialogDgrcompoCngLogCharGridSep = $.csGrid({
            id : "DIALOG_DGR_COMPO_CNG_LOG_CHAR" + cnt,
            colNames : dialogDgrcompoCngLogCharColNamesEdit,
            colModel : dialogDgrcompoCngLogCharColModelEdit,
            cellEdit : true,
            cellsubmit : "clientArray",
            defaultRows : 3,
            rowNum : 1000,
            autowidth:true,
            height: "auto",
            beforeEditCell : function (owid, cellname, value, iRow, iCol){
                charEditIRow = iRow;
                charEditICol = iCol;
            },
            afterSaveCell : function(rowid,name,val,iRow,iCol) {
                afterSaveChar(dialogDgrcompoCngLogCharGridSep, name, cnt);
                
                charEditIRow = 0;
                charEditICol = 0;
            },
            loadComplete: function (data) {
            	//logBodyResize();
            }
    	});

	    dialogDgrcompoCngLogCharGridSep.jqGrid('setGroupHeaders', {
	        useColSpanStyle : true,
	        groupHeaders : [
	           {startColumnName : 'dmnDefCharAmt',numberOfColumns : 2, titleText : '요구'},
	           {startColumnName : 'adjDefCharAmt', numberOfColumns : 2, titleText : '조정'} 
	        ]
	    });

	    var unitStr = "(단위:천원)";
        if($("#dialogDgrcompoCngLogAmtUnit" + cnt, dialogObj).val() == "1"){
            unitStr = "(단위:원)";
        }else if($("#dialogDgrcompoCngLogAmtUnit" + cnt, dialogObj).val() == "1000"){
            unitStr = "(단위:천원)";
        }else if($("#dialogDgrcompoCngLogAmtUnit" + cnt, dialogObj).val() == "1000000"){
            unitStr = "(단위:백만원)";
        }
	    
        
        $('#cngTabTitle' + cnt).html(data.dgrcompo.compGround);
        $("#dialogDgrcompoCngLogTeBgtCompoSeq" + cnt, dialogObj).val(data.dgrcompo.teBgtCompoSeq);
        //$("#dialogDgrcompoCngLogDgrcompoNm" + cnt, dialogObj).val(data.dgrcompo.dgrcompoNm + '_' + cnt);
        $("#dialogDgrcompoCngLogDbizCd" + cnt, dialogObj).val(data.dgrcompo.dbizCd);
        $("#dialogDgrcompoCngLogDbizNm" + cnt, dialogObj).val(data.dgrcompo.upDgrcompoNm);
        $("#dialogDgrcompoCngLogTeMngMokCd" + cnt, dialogObj).val(data.dgrcompo.teMngMokCd);
        $("#dialogDgrcompoCngLogTeMngMokNm" + cnt, dialogObj).val(data.dgrcompo.teMngMokNm);
        $("#dialogDgrcompoCngLogDemandCompFormular" + cnt, dialogObj).val(data.dgrcompo.demandCompFormular);
        $("#dialogDgrcompoCngLogDemandBgtAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.demandBgtAmt));
        $("#dialogDgrcompoCngLogDemandPreAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.preAmt));
        $("#dialogDgrcompoCngLogDemandDiffAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.demandDiffAmt));
        $("#dialogDgrcompoCngLogCompGround" + cnt, dialogObj).val(data.dgrcompo.compGround);
        $("#dialogDgrcompoCngLogCompFormular" + cnt, dialogObj).val(data.dgrcompo.compFormular);
        $("#dialogDgrcompoCngLogBgtAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.bgtAmt));
        $("#dialogDgrcompoCngLogPreFormular" + cnt, dialogObj).val(data.dgrcompo.preFormular);
        $("#dialogDgrcompoCngLogPreAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.preAmt));
        $("#dialogDgrcompoCngLogDiffAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.diffAmt));
        $("#dialogDgrcompoCngLogCompoLevel" + cnt, dialogObj).val(data.dgrcompo.compoLevel);
        $("#dialogDgrcompoCngLogPreCompFormular" + cnt, dialogObj).val(data.dgrcompo.preCompFormular);
        $("#dialogDgrcompoCngLogPreBgtAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.preBgtAmt));
        
        dialogDgrcompoCngLogFrscGridSep.addCsJsonData(data.frscInfo);
        dialogDgrcompoCngLogCharGridSep.addCsJsonData(data.charInfo);

        var isLeaf = $("#dialogDgrcompoCngLogIsLeaf" + cnt, dialogObj).val();
        dialogDgrcompoCngLogCharGridSep.jqGrid("setCell", "090", "preDefCharAmt", "", "not-editable-cell");
        dialogDgrcompoCngLogCharGridSep.jqGrid("setCell", "090", "dmnDefCharAmt", "", "not-editable-cell");
        dialogDgrcompoCngLogCharGridSep.jqGrid("setCell", "090", "adjDefCharAmt", "", "not-editable-cell");
        dialogDgrcompoCngLogCharGridSep.jqGrid("setCell", "090", "preCharAmt", "", "not-editable-cell");
        
        logBodyResize();
        
        logTab.tabs("refresh");
	}
	
    var budgetCngLogDialogDgrcompoDbizCallBackFunction = function(cd, nm, num){
    	$('#dialogDgrcompoCngLogDbizCd' + num, $("#dialogDgrcompoCngLogDiv")).val(cd);
    	$('#dialogDgrcompoCngLogDbizNm' + num, $("#dialogDgrcompoCngLogDiv")).val(nm);
    	$("#dialogDgrcompoDbizDiv").dialog("close");
    }
	
});


</script>
<div id="dialogDgrcompoCngLogDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgrcompoCngLogCngType"/>
  <input type="hidden" id="dialogDgrcompoCngLogCngHistoryId"/>
  <input type="hidden" id="dialogDgrcompoCngLogAmtUnit" value="1"/>
  <input type="hidden" id="dialogDgrcompoCngLogTabCnt" value="1"/>
  
  <br>
  
  <div id="logBody"  style="height:97%;border:0px;overflow:hidden;">
        <div id="logCenter" class="ui-layout-center" style="border:0px;overflow:auto;">
        	<div class="ui-widget-header">
		    	<span id="childLogTitle">병합된 개별사업</span>
		  	</div>
          	<div id="logTab" class="ui-layout-center" style="border:0px;">
				<UL>
				</UL>
			</div>
        </div>
        <div id="logWest" class="ui-layout-west" style="border:0px;overflow:auto;">
        	<div class="viewDiv" style="width:690px;margin-top:0px;margin-bottom:10px;">
		  		<table>
			      <colgroup>
			        <col width="70px"/>
			        <col width="150px"/>
			        <col width="*"/>
			      </colgroup>
			      <tbody>
			        <tr>
		        	  <th style="text-align:center;"><span id="dialogDgrcompoCngLogCngTypeNm">병합</span></th>
			          <th style="text-align:center;">
			            <span id="dialogDgrcompoCngLogRegiDate"></span>
			          </th>
			          <th>
			            <span id="dialogDgrcompoCngLogTitle"></span>
			          </th>
			        </tr>
			      </tbody>
				</table>
		  	</div>
        	<div id="sepWestDiv1"  style="padding-bottom:20px;">
	       		<div class="ui-widget-header">
			    	<span id="parentLogTitle">선택한 개별사업</span>
			  	</div>
			  	
        		<div id="dialogDgrcompoCngLogAmtUnitDiv" class="unitDiv" style="top: 0px;margin-top:5px;">
			    	(단위:천원)
			  	</div>
			  	<div class="viewDiv" style="width:690px;">
				    <table>
				      <colgroup>
				        <col width="70px"/>
				        <col width="80px"/>
				        <col width="120px"/>
				        <col width="90px"/>
				        <col width="120px"/>
				        <col width="90px"/>
				        <col width="120px"/>
				      </colgroup>
				      <tbody>
				        <tr>
				          <th colspan="2">상위항목</th>
				          <td colspan="5">
				            <input type="hidden" id="dialogDgrcompoCngLogTeBgtCompoSeq"/>
				            <input type="hidden" id="dialogDgrcompoCngLogDbizCd"/>
				            <input type="text" id="dialogDgrcompoCngLogDbizNm" class="readonly" style="width:100%;" readonly/>
				          </td>
				        </tr>
				        <tr>
				          <th colspan="2">통계목코드</th>
				          <td>
				            <input type="text" id="dialogDgrcompoCngLogTeMngMokCd" class="readonly" style="width:100%;" readonly/>
				          </td>
				          <th>통계목명</th>
				          <td colspan="3">
				          	<input type="text" id="dialogDgrcompoCngLogTeMngMokNm" class="readonly" style="width:100%;" readonly/>
				          </td>
				        </tr>
				        <tr>
				          <th colspan="2">사업명</th>
				          <td colspan="5">
				            <input type="text" id="dialogDgrcompoCngLogCompGround" style="width:100%;" readonly/>
				          </td>
				        </tr>
				        <tr>
				          <th>요구</th>
				          <th>기정액</th>
				          <td>
				            <input type="text" id="dialogDgrcompoCngLogDemandPreAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
				          </td>
				          <th>증감액</th>
				          <td>
				            <input type="text" id="dialogDgrcompoCngLogDemandDiffAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
				          </td>
				          <th>요구액</th>
				          <td>
				            <input type="text" id="dialogDgrcompoCngLogDemandBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
				          </td>
				        </tr>
				        <tr>
				          <th>조정</th>
				          <th>기정액</th>
				          <td>
				            <input type="text" id="dialogDgrcompoCngLogPreAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
				          </td>
				          <th>증감액</th>
				          <td>
				            <input type="text" id="dialogDgrcompoCngLogDiffAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
				          </td>
				          <th>조정액</th>
				          <td>
				            <input type="text" id="dialogDgrcompoCngLogBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
				          </td>
				        </tr>
				        <tr>
				          <th>전년도</th>
				          <th>예산액</th>
				          <td>
				            <input type="text" id="dialogDgrcompoCngLogPreBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
				          </td>
				          <td>&nbsp;</td>
				          <td>&nbsp;</td>
				          <td>&nbsp;</td>
				          <td>&nbsp;</td>
				        </tr>
				      </tbody>
				    </table>
				 </div>
        	</div>
        	<div id="sepWestDiv2" style="padding-bottom:20px;">
        		<div class="ui-widget-header">
				재원별 예산액
				</div>
				<div id="DIALOG_DGR_COMPO_CNG_LOG_FRSC_DIV" class="csGrid">
					<table id="DIALOG_DGR_COMPO_CNG_LOG_FRSC_GRD"  style="border:0px;height:100%;"></table>
				</div>
        	</div>
        	<div id="sepWestDiv3">
        		<div class="ui-widget-header">
			      성격별 예산액
			    </div>
			    <div id="DIALOG_DGR_COMPO_CNG_LOG_CHAR_DIV" class="csGrid">
			    	<table id="DIALOG_DGR_COMPO_CNG_LOG_CHAR_GRD" ></table>
			    </div>
        	</div>
		  
		  <br />
		  
       </div>
  </div>
</div>

