<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogDgrcompoRegiDiv");
    
    var dialogDgrcompoRegiFrscColNames = ['재원명', '전년도예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'frscFgCd', 'standFrscCd'];
    var dialogDgrcompoRegiFrscColModel = [
                                    {name : 'frscFgNm', index : 'frscFgNm', width : 100, sortable : false, fixed : true, align : 'left'},
                                    {name : 'preFrscAmt', index : 'preFrscAmt', width : 100, sortable : false, fixed : true, align : 'right',
                                        editable : true, edittype:'text',
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'preDefFrscAmt', index : 'preDefFrscAmt', width : 100, sortable : false, fixed : true, align : 'right',
                                        editable : true, edittype:'text',
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'dmnDefFrscAmt', index : 'dmnDefFrscAmt', width : 100, sortable : false, fixed : true, align : 'right',
                                        editable : true, edittype:'text',
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'demandFrscAmt', index : 'demandFrscAmt', width : 100, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                    {name : 'adjDefFrscAmt', index : 'adjDefFrscAmt', width : 100, sortable : false, fixed : true, align : 'right',
                                        editable : true, edittype:'text',
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'frscAmt', index : 'frscAmt', width : 100, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                    {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'frscFgCd', index : 'frscFgCd', width : 0, sortable : false, fixed : true, hidden : true, key : true},
                                    {name : 'standFrscCd', index : 'standFrscCd', width : 0, sortable : false, fixed : true, hidden : true}
    ];
    
    var dialogDgrcompoRegiCharColNames = ['성격명', '전년도예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'charFgCd'];
    var dialogDgrcompoRegiCharColModel = [
                                    {name : 'charFgNm', index : 'charFgNm', width : 100, sortable : false, fixed : true, align : 'left'},
                                    {name : 'preCharAmt', index : 'preCharAmt', width : 100, sortable : false, fixed : true, align : 'right',
                                        editable : true, edittype:'text',
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'preDefCharAmt', index : 'preDefCharAmt', width : 100, sortable : false, fixed : true, align : 'right',
                                        editable : true, edittype:'text',
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'dmnDefCharAmt', index : 'dmnDefCharAmt', width : 100, sortable : false, fixed : true, align : 'right',
                                        editable : true, edittype:'text',
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'demandCharAmt', index : 'demandCharAmt', width : 100, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                    {name : 'adjDefCharAmt', index : 'adjDefCharAmt', width : 100, sortable : false, fixed : true, align : 'right',
                                        editable : true, edittype:'text',
                                        editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                        formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                    },
                                    {name : 'charAmt', index : 'charAmt', width : 100, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                    {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, fixed : true, hidden : true },
                                    {name : 'charFgCd', index : 'charFgCd', width : 0, sortable : false, fixed : true, hidden : true, key : true}
    ];
    
    var getSaveData = function(){
        var rowData;
        var frscIds = dialogDgrcompoRegiFrscGrid.jqGrid("getDataIDs");
        var frscs = [];
        var frscEach = {};

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
        
        var totPreDefFrscAmt = 0;
        var totDmnDefFrscAmt = 0;
        var totDemandFrscAmt = 0;
        var totAdjDefFrscAmt = 0;
        var totFrscAmt = 0;
        var totPreFrscAmt = 0;
        
        for(var i = 0; i < frscIds.length; i++){
            rowData = dialogDgrcompoRegiFrscGrid.jqGrid("getRowData", frscIds[i]);
            
            preDefFrscAmt = Number(rowData.preDefFrscAmt);
            dmnDefFrscAmt = Number(rowData.dmnDefFrscAmt);
            demandFrscAmt = preDefFrscAmt + dmnDefFrscAmt;
            adjDefFrscAmt = Number(rowData.adjDefFrscAmt);
            frscAmt = preDefFrscAmt + adjDefFrscAmt;
            preFrscAmt = Number(rowData.preFrscAmt);
            
            if(isEmpty(rowData.frscFgCd) == false){
                frscEach = {};
                frscEach["frscFgCd"] = rowData.frscFgCd;
                frscEach["preDefFrscAmt"] = preDefFrscAmt;
                frscEach["dmnDefFrscAmt"] = dmnDefFrscAmt;
                frscEach["adjDefFrscAmt"] = adjDefFrscAmt;
                frscEach["preFrscAmt"] = preFrscAmt;
                
                frscs.push(frscEach);

                if(rowData.frscFgCd < "200" || rowData.frscFgCd === "500"){
                    sumPreDefFrscAmt += preDefFrscAmt;
                    sumDmnDefFrscAmt += dmnDefFrscAmt;
                    sumDemandFrscAmt += demandFrscAmt;
                    sumAdjDefFrscAmt += adjDefFrscAmt;
                    sumFrscAmt += frscAmt;
                    sumPreFrscAmt += preFrscAmt;
                }
                
                totPreDefFrscAmt += preDefFrscAmt;
                totDmnDefFrscAmt += dmnDefFrscAmt;
                totDemandFrscAmt += demandFrscAmt;
                totAdjDefFrscAmt += adjDefFrscAmt;
                totFrscAmt += frscAmt;
                totPreFrscAmt += preFrscAmt;
            }
        }
        
        var demandBgtAmt = sumPreDefFrscAmt + sumDmnDefFrscAmt;
        var demandPreAmt = sumPreDefFrscAmt;
        var demandDiffAmt = sumDmnDefFrscAmt;
        var bgtAmt = sumPreDefFrscAmt + sumAdjDefFrscAmt;
        var preAmt = sumPreDefFrscAmt;
        var diffAmt = sumAdjDefFrscAmt;
        var preBgtAmt = sumPreFrscAmt;
        
        var charIds = dialogDgrcompoRegiCharGrid.jqGrid("getDataIDs");
        var chars = [];
        var charEach = {};
        
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
        
        for(var i = 0; i < charIds.length; i++){
            rowData = dialogDgrcompoRegiCharGrid.jqGrid("getRowData", charIds[i]);
            
            preDefCharAmt = Number(rowData.preDefCharAmt);
            dmnDefCharAmt = Number(rowData.dmnDefCharAmt);
            demandCharAmt = preDefCharAmt + dmnDefCharAmt;
            adjDefCharAmt = Number(rowData.adjDefCharAmt);
            charAmt = preDefCharAmt + adjDefCharAmt;
            preCharAmt = Number(rowData.preCharAmt);
        
            if(isEmpty(rowData.charFgCd) == false && rowData.charFgCd !== '090'){
                charEach = {};
                charEach["charFgCd"] = rowData.charFgCd;
                charEach["preDefCharAmt"] = preDefCharAmt;
                charEach["dmnDefCharAmt"] = dmnDefCharAmt;
                charEach["adjDefCharAmt"] = adjDefCharAmt;
                charEach["preCharAmt"] = preCharAmt;
                
                chars.push(charEach);
                
                sumPreDefCharAmt += preDefCharAmt;
                sumDmnDefCharAmt += dmnDefCharAmt;
                sumDemandCharAmt += demandCharAmt;
                sumAdjDefCharAmt += adjDefCharAmt;
                sumCharAmt += charAmt;
                sumPreCharAmt += preCharAmt;
            }
        }
        
        charEach = {};
        charEach["charFgCd"] = "090";
        charEach["preDefCharAmt"] = totPreDefFrscAmt - sumPreDefCharAmt;
        charEach["dmnDefCharAmt"] = totDmnDefFrscAmt - sumDmnDefCharAmt;
        charEach["adjDefCharAmt"] = totAdjDefFrscAmt - sumAdjDefCharAmt;
        charEach["preCharAmt"] = totPreFrscAmt - sumPreCharAmt;
        
        chars.push(charEach);
        
        var param = {
                fisYear: $("#dialogDgrcompoRegiFisYear", dialogObj).val(),
                bgtDgr: $("#dialogDgrcompoRegiBgtDgr", dialogObj).val(),
                deptCd: $("#dialogDgrcompoRegiDeptCd", dialogObj).val(),
                dbizCd: $("#dialogDgrcompoRegiDbizCd", dialogObj).val(),
                teMngMokCd: $("#dialogDgrcompoRegiTeMngMokCd", dialogObj).val(),
                upTeBgtCompoId: $("#dialogDgrcompoRegiUpTeBgtCompoId", dialogObj).val(),
                upTeBgtCompoSeq: $("#dialogDgrcompoRegiUpTeBgtCompoSeq", dialogObj).val(),
                compoLevel : $("#dialogDgrcompoRegiCompoLevel", dialogObj).val(),
                demandCompGround: $("#dialogDgrcompoRegiCompGround", dialogObj).val(), 
                //demandCompFormular: $("#dialogDgrcompoRegiDemandCompFormular", dialogObj).val(),
                demandCompFormular: '',
                demandBgtAmt: demandBgtAmt,
                demandPreAmt: demandPreAmt,
                demandDiffAmt: demandDiffAmt,
                compGround: $("#dialogDgrcompoRegiCompGround", dialogObj).val(), 
                //compFormular: $("#dialogDgrcompoRegiCompFormular", dialogObj).val(),
                compFormular: '',
                bgtAmt: bgtAmt, 
                //preFormular: $("#dialogDgrcompoRegiPreFormular", dialogObj).val(),
                preFormular: '',
                preAmt: preAmt, 
                diffAmt: diffAmt, 
                //preCompFormular: $("#dialogDgrcompoRegiPreCompFormular", dialogObj).val(),
                preCompFormular: '',
                preBgtAmt: preBgtAmt,
                amtUnit: $("#dialogDgrcompoRegiAmtUnit", dialogObj).val(),
                frscs: frscs,
                chars: chars
        };
        
        return param;
        
    };
    
    var afterSaveFrsc = function(gridObject, name){
        if(name !== "preDefFrscAmt" && name !== "dmnDefFrscAmt" && name !== "adjDefFrscAmt" && name !== "preFrscAmt"){
            return;
        }

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

            if(rowData.frscFgCd < "200" || rowData.frscFgCd === "500"){
                
                sumPreDefFrscAmt += preDefFrscAmt;
                sumDmnDefFrscAmt += dmnDefFrscAmt;
                sumDemandFrscAmt += demandFrscAmt;
                sumAdjDefFrscAmt += adjDefFrscAmt;
                sumFrscAmt += frscAmt;
                sumPreFrscAmt += preFrscAmt;
            }
        }
        
        $("#dialogDgrcompoRegiDemandBgtAmt", dialogObj).val(addCommaStr((sumPreDefFrscAmt + sumDmnDefFrscAmt)));
        $("#dialogDgrcompoRegiDemandPreAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoRegiDemandDiffAmt", dialogObj).val(addCommaStr(sumDmnDefFrscAmt));
        $("#dialogDgrcompoRegiBgtAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt + sumAdjDefFrscAmt));
        $("#dialogDgrcompoRegiPreAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoRegiDiffAmt", dialogObj).val(addCommaStr(sumAdjDefFrscAmt));
        $("#dialogDgrcompoRegiPreBgtAmt", dialogObj).val(addCommaStr(sumPreFrscAmt));
        
        afterSaveChar(dialogDgrcompoRegiCharGrid, "preDefCharAmt");
    };
    
    var afterSaveChar = function(gridObject, name){
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
        
        ids = dialogDgrcompoRegiFrscGrid.jqGrid("getDataIDs");
        for(var i = 0; i < ids.length; i++){
            rowData = dialogDgrcompoRegiFrscGrid.jqGrid("getRowData", ids[i]);
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
    
    var frscEditIRow = 0;
    var frscEditICol = 0;
    var dialogDgrcompoRegiFrscGridParam = {
            id : "DIALOG_DGR_COMPO_REGI_FRSC",
            colNames : dialogDgrcompoRegiFrscColNames,
            colModel : dialogDgrcompoRegiFrscColModel,
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
                afterSaveFrsc(dialogDgrcompoRegiFrscGrid, name);
                
                frscEditIRow = 0;
                frscEditICol = 0;
            }
    };
    
    var dialogDgrcompoRegiFrscGrid = $.csGrid(dialogDgrcompoRegiFrscGridParam);
    dialogDgrcompoRegiFrscGrid.jqGrid('setGroupHeaders', {
        useColSpanStyle : true,
        groupHeaders : [
           {startColumnName : 'dmnDefFrscAmt',numberOfColumns : 2, titleText : '요구'},
           {startColumnName : 'adjDefFrscAmt', numberOfColumns : 2, titleText : '조정'} 
        ]
    });

    var charEditIRow = 0;
    var charEditICol = 0;
    var dialogDgrcompoRegiCharGridParam = {
            id : "DIALOG_DGR_COMPO_REGI_CHAR",
            colNames : dialogDgrcompoRegiCharColNames,
            colModel : dialogDgrcompoRegiCharColModel,
            cellEdit : true,
            cellsubmit : "clientArray",
            defaultRows : 3,
            rowNum : 1000,
            width: "auto",
            height: "auto",
            beforeEditCell : function (owid, cellname, value, iRow, iCol){
                charEditIRow = iRow;
                charEditICol = iCol;
            },
            afterSaveCell : function(rowid,name,val,iRow,iCol) {
                afterSaveChar(dialogDgrcompoRegiCharGrid, name);
                
                charEditIRow = 0;
                charEditICol = 0;
            }
    };
    
    var dialogDgrcompoRegiCharGrid = $.csGrid(dialogDgrcompoRegiCharGridParam);
    dialogDgrcompoRegiCharGrid.jqGrid('setGroupHeaders', {
        useColSpanStyle : true,
        groupHeaders : [
           {startColumnName : 'dmnDefCharAmt',numberOfColumns : 2, titleText : '요구'},
           {startColumnName : 'adjDefCharAmt', numberOfColumns : 2, titleText : '조정'} 
        ]
    });
    
    var doDialogDgrcompoRegiSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        dialogDgrcompoRegiFrscGrid.addCsJsonData(data.frscInfo);
        dialogDgrcompoRegiCharGrid.addCsJsonData(data.charInfo);
        
        dialogDgrcompoRegiCharGrid.jqGrid("setCell", "090", "preDefCharAmt", "", "not-editable-cell");
        dialogDgrcompoRegiCharGrid.jqGrid("setCell", "090", "dmnDefCharAmt", "", "not-editable-cell");
        dialogDgrcompoRegiCharGrid.jqGrid("setCell", "090", "adjDefCharAmt", "", "not-editable-cell");
        dialogDgrcompoRegiCharGrid.jqGrid("setCell", "090", "preCharAmt", "", "not-editable-cell");
    };
    
    var doDialogDgrcompoRegiSearch = function() {
        var unitStr = "(단위:천원)";
        if($("#dialogDgrcompoRegiAmtUnitDiv", dialogObj).val() == "1"){
            unitStr = "(단위:원)";
        }else if($("#dialogDgrcompoRegiAmtUnitDiv", dialogObj).val() == "1000"){
            unitStr = "(단위:천원)";
        }else if($("#dialogDgrcompoRegiAmtUnitDiv", dialogObj).val() == "1000000"){
            unitStr = "(단위:백만원)";
        }
    
        $("#dialogDgrcompoRegiAmtUnitDiv", dialogObj).html(unitStr);
        
        $.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoRegiSelectDgrcompos.do",
            data : {
                    fisYear: $("#dialogDgrcompoRegiFisYear", dialogObj).val(),
                    bgtDgr: $("#dialogDgrcompoRegiBgtDgr", dialogObj).val(),
                    upTeBgtCompoId: $("#dialogDgrcompoRegiUpTeBgtCompoId", dialogObj).val(),
                    amtUnit: $("#dialogDgrcompoRegiAmtUnit", dialogObj).val()
                   },
            async : true,
            callBack : doDialogDgrcompoRegiSearchCallBack
        });
    };
    
    var dialogDgrcompoRegiDoSaveCallBack = function(param){
        var dialogDgrcompoRegiCallBackFunction = $("#dialogDgrcompoRegiCallBackFunction", dialogObj).val();
        
        if(isEmpty(dialogDgrcompoRegiCallBackFunction) == false){
            eval(dialogDgrcompoRegiCallBackFunction + '('+ jsonToString(param.dgrcompo) + ')');
        }
    };
    
    var dialogDgrcompoRegiDoSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var saveData = getSaveData();
        
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoRegiSaveDgrcompos.do",
            data : saveData
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogDgrcompoRegiClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                dialogDgrcompoRegiDoSaveCallBack(data);

                dialogDgrcompoRegiClose();
            }
        });
    };
    
    var dialogDgrcompoRegiSaveBtnClick = function(){
        if(frscEditIRow !== 0 && frscEditICol !== 0){
            dialogDgrcompoRegiFrscGrid.saveCell(frscEditIRow, frscEditICol);
        }
        
        if(charEditIRow !== 0 && charEditICol !== 0){
            dialogDgrcompoRegiCharGrid.saveCell(charEditIRow, charEditICol);
        }
        
        if(isEmpty($("#dialogDgrcompoRegiCompGround", dialogObj).val()) == true){
            $.csAlert({
                msg : "사업명을 입력하여 주십시오.",
                callBack : function() {
                    $("#dialogDgrcompoRegiCompGround", dialogObj).focus();
                }
            });
            
            return false;
        }
        
        $.csConfirm({
            msg : "등록하시겠습니까?",
            callBack : dialogDgrcompoRegiDoSave
        });
    };
    
    var dialogDgrcompoRegiClose = function(){
        $("#dialogDgrcompoRegiCallBackFunction", dialogObj).val("");
        $("#dialogDgrcompoRegiFisYear", dialogObj).val("");
        $("#dialogDgrcompoRegiBgtDgr", dialogObj).val("");
        $("#dialogDgrcompoRegiDeptCd", dialogObj).val("");
        $("#dialogDgrcompoRegiDbizCd", dialogObj).val("");
        $("#dialogDgrcompoRegiTeMngMokCd", dialogObj).val("");
        $("#dialogDgrcompoRegiUpTeBgtCompoId", dialogObj).val("");
        $("#dialogDgrcompoRegiUpTeBgtCompoSeq", dialogObj).val("");
        $("#dialogDgrcompoRegiCompoLevel", dialogObj).val("");
        $("#dialogDgrcompoRegiDgrcompoNm", dialogObj).val("");
        $("#dialogDgrcompoRegiDemandCompFormular", dialogObj).val("");
        $("#dialogDgrcompoRegiDemandBgtAmt", dialogObj).val("0");
        $("#dialogDgrcompoRegiDemandPreAmt", dialogObj).val("0");
        $("#dialogDgrcompoRegiDemandDiffAmt", dialogObj).val("0");
        $("#dialogDgrcompoRegiCompGround", dialogObj).val("");
        $("#dialogDgrcompoRegiCompFormular", dialogObj).val("");
        $("#dialogDgrcompoRegiBgtAmt", dialogObj).val("0");
        $("#dialogDgrcompoRegiPreFormular", dialogObj).val("");
        $("#dialogDgrcompoRegiPreAmt", dialogObj).val("0");
        $("#dialogDgrcompoRegiDiffAmt", dialogObj).val("0");
        $("#dialogDgrcompoRegiPreCompFormular", dialogObj).val("");
        $("#dialogDgrcompoRegiPreBgtAmt", dialogObj).val("0");
        $("#dialogDgrcompoRegiAmtUnit", dialogObj).val("1");
        
        $("#dialogDgrcompoRegiDiv").dialog("close");
    };
    
    $("#dialogDgrcompoRegiDiv").dialog({
        title: "예산편성등록",
        autoOpen: false,
        width: 780,
        height: 800,
        modal: true,
        resizable: true,
        open: function(event, ui){
            doDialogDgrcompoRegiSearch();
        },
        buttons : {
            "등록" : function() {
                dialogDgrcompoRegiSaveBtnClick();
            },
            "닫기" : function() {
                dialogDgrcompoRegiClose();
            }
        }
    });
});
</script>
<div id="dialogDgrcompoRegiDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgrcompoRegiCallBackFunction"/>
  <input type="hidden" id="dialogDgrcompoRegiFisYear"/>
  <input type="hidden" id="dialogDgrcompoRegiBgtDgr"/>
  <input type="hidden" id="dialogDgrcompoRegiDeptCd"/>
  <input type="hidden" id="dialogDgrcompoRegiDbizCd"/>
  <input type="hidden" id="dialogDgrcompoRegiTeMngMokCd"/>
  <input type="hidden" id="dialogDgrcompoRegiUpTeBgtCompoId"/>
  <input type="hidden" id="dialogDgrcompoRegiUpTeBgtCompoSeq"/>
  <input type="hidden" id="dialogDgrcompoRegiCompoLevel"/>
  <input type="hidden" id="dialogDgrcompoRegiAmtUnit" value="1"/>
  
  <div id="dialogDgrcompoRegiAmtUnitDiv" class="unitDiv" style="top: 0px;">
    (단위:천원)
  </div>
  <div class="viewDiv" style="width:735px;">
    <table>
      <colgroup>
        <col width="75px"/>
        <col width="90px"/>
        <col width="130px"/>
        <col width="90px"/>
        <col width="130px"/>
        <col width="90px"/>
        <col width="130px"/>
      </colgroup>
      <tbody>
        <tr>
          <th colspan="2">상위 항목</th>
          <td colspan="5">
            <input type="text" id="dialogDgrcompoRegiDgrcompoNm" class="readonly" style="width:100%;" readonly/>
          </td>
        </tr>
        <tr>
        	<th colspan="2">통계목코드</th>
        	<td>
	            <input type="text" id="dialogDgrcompoRegiTeMngMokCdNm" class="readonly" style="width:100%;" readonly/>
	        </td>
	        <th>통계목명</th>
	        <td colspan="3">
	          	<input type="text" id="dialogDgrcompoRegiTeMngMokNm" class="readonly" style="width:100%;" readonly/>
	        </td>
        </tr>
        <tr>
          <th colspan="2">사업명</th>
          <td colspan="5">
            <input type="text" id="dialogDgrcompoRegiCompGround" style="width:100%;"/>
          </td>
        </tr>
        <tr>
        	<th>요구</th>
          <th>기정액</th>
          <td>
            <input type="text" id="dialogDgrcompoRegiDemandPreAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
          <th>증감액</th>
          <td>
            <input type="text" id="dialogDgrcompoRegiDemandDiffAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
          <th>요구액</th>
          <td>
            <input type="text" id="dialogDgrcompoRegiDemandBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
        </tr>
        <tr>
        	<th>조정</th>
          <th>기정액</th>
          <td>
            <input type="text" id="dialogDgrcompoRegiPreAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
          <th>증감액</th>
          <td>
            <input type="text" id="dialogDgrcompoRegiDiffAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
          <th>조정액</th>
          <td>
            <input type="text" id="dialogDgrcompoRegiBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
        </tr>
        <tr>
        	<th>전년도</th>
          <th>예산액</th>
          <td>
            <input type="text" id="dialogDgrcompoRegiPreBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
        </tr>
      </tbody>
    </table>
  </div>
  <br>
  <div class="ui-widget-header" style="width:735px;">
    재원별 예산액
  </div>
  <div id="DIALOG_DGR_COMPO_REGI_FRSC_DIV" class="csGrid">
    <table id="DIALOG_DGR_COMPO_REGI_FRSC_GRD" ></table>
  </div>
  <BR>
  <div class="ui-widget-header" style="width:735px;">
    성격별 예산액
  </div>
  <div id="DIALOG_DGR_COMPO_REGI_CHAR_DIV" class="csGrid">
    <table id="DIALOG_DGR_COMPO_REGI_CHAR_GRD" ></table>
  </div>
</div>