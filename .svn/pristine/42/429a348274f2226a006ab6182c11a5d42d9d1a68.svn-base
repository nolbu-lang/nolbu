<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogDgrcompoModifyMergeDiv");
    
    var dialogDgrcompoModifyMergeFrscColNames = ['재원명', '전년도예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'frscFgCd', 'standFrscCd'];
    var dialogDgrcompoModifyMergeFrscColModel = [
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
    
    var dialogDgrcompoModifyMergeCharColNames = ['성격명', '전년도예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'charFgCd'];
    var dialogDgrcompoModifyMergeCharColModel = [
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
        var frscIds = dialogDgrcompoModifyMergeFrscGrid.jqGrid("getDataIDs");
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
            rowData = dialogDgrcompoModifyMergeFrscGrid.jqGrid("getRowData", frscIds[i]);
            
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
        
        var charIds = dialogDgrcompoModifyMergeCharGrid.jqGrid("getDataIDs");
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
            rowData = dialogDgrcompoModifyMergeCharGrid.jqGrid("getRowData", charIds[i]);
            
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

        $("#dialogDgrcompoModifyMergeDemandBgtAmt", dialogObj).val(addCommaStr((sumPreDefFrscAmt + sumDmnDefFrscAmt)));
        $("#dialogDgrcompoModifyMergeDemandPreAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoModifyMergeDemandDiffAmt", dialogObj).val(addCommaStr(sumDmnDefFrscAmt));
        $("#dialogDgrcompoModifyMergeBgtAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt + sumAdjDefFrscAmt));
        $("#dialogDgrcompoModifyMergePreAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoModifyMergeDiffAmt", dialogObj).val(addCommaStr(sumAdjDefFrscAmt));
        $("#dialogDgrcompoModifyMergePreBgtAmt", dialogObj).val(addCommaStr(sumPreFrscAmt));
        
        var param = {
                fisYear: $("#dialogDgrcompoModifyMergeFisYear", dialogObj).val(),
                bgtDgr: $("#dialogDgrcompoModifyMergeBgtDgr", dialogObj).val(),
                teBgtCompoId: $("#dialogDgrcompoModifyMergeTeBgtCompoId", dialogObj).val(),
                teBgtCompoSeq: $("#dialogDgrcompoModifyMergeTeBgtCompoSeq", dialogObj).val(),
                demandCompGround: $("#dialogDgrcompoModifyMergeCompGround", dialogObj).val(), 
                demandCompFormular: '',
                demandBgtAmt: demandBgtAmt,
                demandPreAmt: demandPreAmt,
                demandDiffAmt: demandDiffAmt,
                compGround: $("#dialogDgrcompoModifyMergeCompGround", dialogObj).val(), 
                teMngMokCd: $("#dialogDgrcompoModifyMergeTeMngMokCdNm", dialogObj).val().replace(/-/gi, ''), 
                teMngMokNm: $("#dialogDgrcompoModifyMergeTeMngMokNm", dialogObj).val(), 
                compFormular: '',
                bgtAmt: bgtAmt, 
                preFormular: '',
                preAmt: preAmt, 
                diffAmt: diffAmt, 
                preCompFormular: '',
                preBgtAmt: preBgtAmt,
                amtUnit: $("#dialogDgrcompoModifyMergeAmtUnit", dialogObj).val(),
                isLeaf: $("#dialogDgrcompoModifyMergeIsLeaf", dialogObj).val(),
                frscs: frscs,
                chars: chars,
                cngHistoryId: $("#dialogDgrcompoModifyMergeCngHistoryId", dialogObj).val()
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
        
        $("#dialogDgrcompoModifyMergeDemandBgtAmt", dialogObj).val(addCommaStr((sumPreDefFrscAmt + sumDmnDefFrscAmt)));
        $("#dialogDgrcompoModifyMergeDemandPreAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoModifyMergeDemandDiffAmt", dialogObj).val(addCommaStr(sumDmnDefFrscAmt));
        $("#dialogDgrcompoModifyMergeBgtAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt + sumAdjDefFrscAmt));
        $("#dialogDgrcompoModifyMergePreAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoModifyMergeDiffAmt", dialogObj).val(addCommaStr(sumAdjDefFrscAmt));
        $("#dialogDgrcompoModifyMergePreBgtAmt", dialogObj).val(addCommaStr(sumPreFrscAmt));
        
        afterSaveChar(dialogDgrcompoModifyMergeCharGrid, "preDefCharAmt");
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
        
        ids = dialogDgrcompoModifyMergeFrscGrid.jqGrid("getDataIDs");
        for(var i = 0; i < ids.length; i++){
            rowData = dialogDgrcompoModifyMergeFrscGrid.jqGrid("getRowData", ids[i]);
            console.log(rowData);
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
    var dialogDgrcompoModifyMergeFrscGridParam = {
            id : "DIALOG_DGR_COMPO_MODIFY_MERGE_FRSC",
            colNames : dialogDgrcompoModifyMergeFrscColNames,
            colModel : dialogDgrcompoModifyMergeFrscColModel,
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
                afterSaveFrsc(dialogDgrcompoModifyMergeFrscGrid, name);
                
                frscEditIRow = 0;
                frscEditICol = 0;
            }
    };
    
    var dialogDgrcompoModifyMergeFrscGrid = $.csGrid(dialogDgrcompoModifyMergeFrscGridParam);
    dialogDgrcompoModifyMergeFrscGrid.jqGrid('setGroupHeaders', {
        useColSpanStyle : true,
        groupHeaders : [
           {startColumnName : 'dmnDefFrscAmt',numberOfColumns : 2, titleText : '요구'},
           {startColumnName : 'adjDefFrscAmt', numberOfColumns : 2, titleText : '조정'} 
        ]
    });

    var charEditIRow = 0;
    var charEditICol = 0;
    var dialogDgrcompoModifyMergeCharGridParam = {
            id : "DIALOG_DGR_COMPO_MODIFY_MERGE_CHAR",
            colNames : dialogDgrcompoModifyMergeCharColNames,
            colModel : dialogDgrcompoModifyMergeCharColModel,
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
                afterSaveChar(dialogDgrcompoModifyMergeCharGrid, name);
                
                charEditIRow = 0;
                charEditICol = 0;
            }
    };
    
    var dialogDgrcompoModifyMergeCharGrid = $.csGrid(dialogDgrcompoModifyMergeCharGridParam);
    dialogDgrcompoModifyMergeCharGrid.jqGrid('setGroupHeaders', {
        useColSpanStyle : true,
        groupHeaders : [
           {startColumnName : 'dmnDefCharAmt',numberOfColumns : 2, titleText : '요구'},
           {startColumnName : 'adjDefCharAmt', numberOfColumns : 2, titleText : '조정'} 
        ]
    });
    
    var doDialogDgrcompoModifyMergeSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        $("#dialogDgrcompoModifyMergeTeBgtCompoSeq", dialogObj).val(data.dgrcompo.teBgtCompoSeq);
        $("#dialogDgrcompoModifyMergeDgrcompoNm", dialogObj).val(data.dgrcompo.upDgrcompoNm);
        $("#dialogDgrcompoModifyMergeTeMngMokCdNm", dialogObj).val(data.dgrcompo.teMngMokCd);
        $("#dialogDgrcompoModifyMergeTeMngMokNm", dialogObj).val(data.dgrcompo.teMngMokNm);
        $("#dialogDgrcompoModifyMergeDemandCompFormular", dialogObj).val(data.dgrcompo.demandCompFormular);
        $("#dialogDgrcompoModifyMergeDemandBgtAmt", dialogObj).val(addCommaStr(data.dgrcompo.demandBgtAmt));
        $("#dialogDgrcompoModifyMergeDemandPreAmt", dialogObj).val(addCommaStr(data.dgrcompo.preAmt));
        $("#dialogDgrcompoModifyMergeDemandDiffAmt", dialogObj).val(addCommaStr(data.dgrcompo.demandDiffAmt));
        $("#dialogDgrcompoModifyMergeCompGround", dialogObj).val(data.dgrcompo.compGround);
        $("#dialogDgrcompoModifyMergeCompFormular", dialogObj).val(data.dgrcompo.compFormular);
        $("#dialogDgrcompoModifyMergeBgtAmt", dialogObj).val(addCommaStr(data.dgrcompo.bgtAmt));
        $("#dialogDgrcompoModifyMergePreFormular", dialogObj).val(data.dgrcompo.preFormular);
        $("#dialogDgrcompoModifyMergePreAmt", dialogObj).val(addCommaStr(data.dgrcompo.preAmt));
        $("#dialogDgrcompoModifyMergeDiffAmt", dialogObj).val(addCommaStr(data.dgrcompo.diffAmt));
        $("#dialogDgrcompoModifyMergeCompoLevel", dialogObj).val(data.dgrcompo.compoLevel);
        $("#dialogDgrcompoModifyMergePreCompFormular", dialogObj).val(data.dgrcompo.preCompFormular);
        $("#dialogDgrcompoModifyMergePreBgtAmt", dialogObj).val(addCommaStr(data.dgrcompo.preBgtAmt));
        
        dialogDgrcompoModifyMergeFrscGrid.addCsJsonData(data.frscInfo);
        dialogDgrcompoModifyMergeCharGrid.addCsJsonData(data.charInfo);
        
    };
    
    var doDialogDgrcompoModifyMergeSearch = function() {
    	
        var unitStr = "(단위:천원)";
        if($("#dialogDgrcompoModifyMergeAmtUnit", dialogObj).val() == "1"){
            unitStr = "(단위:원)";
        }else if($("#dialogDgrcompoModifyMergeAmtUnit", dialogObj).val() == "1000"){
            unitStr = "(단위:천원)";
        }else if($("#dialogDgrcompoModifyMergeAmtUnit", dialogObj).val() == "1000000"){
            unitStr = "(단위:백만원)";
        }
    
        $("#dialogDgrcompoModifyMergeAmtUnitDiv", dialogObj).html(unitStr);
        
        $.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoModifySelectDgrcompos.do",
            data : {
                    fisYear: $("#dialogDgrcompoModifyMergeFisYear", dialogObj).val(),
                    bgtDgr: $("#dialogDgrcompoModifyMergeBgtDgr", dialogObj).val(),
                    teBgtCompoId: $("#dialogDgrcompoModifyMergeTeBgtCompoId", dialogObj).val(),
                    amtUnit: $("#dialogDgrcompoModifyMergeAmtUnit", dialogObj).val()
                   },
            async : true,
            callBack : doDialogDgrcompoModifyMergeSearchCallBack
        });
    };
    
    var dialogDgrcompoModifyMergeDoSaveCallBack = function(param){
        var dialogDgrcompoModifyMergeCallBackFunction = $("#dialogDgrcompoModifyMergeCallBackFunction", dialogObj).val();

        $("#dialogDgrModiListCallBackFunction", $("#dialogDgrModiListDiv")).val("");
        $("#dialogDgrModiListFisYear", $("#dialogDgrModiListDiv")).val("");
        $("#dialogDgrModiListBgtDgr", $("#dialogDgrModiListDiv")).val("");
        $("#dialogDgrModiListOfficeCd", $("#dialogDgrModiListDiv")).val("");
        $("#dialogDgrModiListSeltFg", $("#dialogDgrModiListDiv")).val("");
        $("#dialogDgrModiListReportCd", $("#dialogDgrModiListDiv")).val("");
        $("#dialogDgrModiListUserDeptYn", $("#dialogDgrModiListDiv")).val("");
        
        $("#dialogDgrModiListDiv").dialog('close');
        
        if(isEmpty(param) == true){
            return;
        }
        
        param.dgrcompo["dgrcompoId"] = $("#dialogDgrcompoModifyMergeDgrcompoId", dialogObj).val();
        
        if(isEmpty(dialogDgrcompoModifyMergeCallBackFunction) == false){
            eval(dialogDgrcompoModifyMergeCallBackFunction + '('+ jsonToString(param.dgrcompo) + ')');
        }
    };
    
    var dialogDgrcompoModifyMergeDoSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var saveData = getSaveData();
        
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoModifyMergeSaveDgrcompos.do",
            data : saveData
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogDgrcompoModifyMergeClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : "수정되었습니다.<BR>(상위 항목의 정보는 재 조회 후 확인하실 수 있습니다.)",
            callBack : function() {
                dialogDgrcompoModifyMergeDoSaveCallBack(data);
                
                dialogDgrcompoModifyMergeClose();
            }
        });
    };
    
    var dialogDgrcompoModifyMergeSaveBtnClick = function(){
        if(frscEditIRow !== 0 && frscEditICol !== 0){
            dialogDgrcompoModifyMergeFrscGrid.saveCell(frscEditIRow, frscEditICol);
        }
        
        if(charEditIRow !== 0 && charEditICol !== 0){
            dialogDgrcompoModifyMergeCharGrid.saveCell(charEditIRow, charEditICol);
        }
        
        if(isEmpty($("#dialogDgrcompoModifyMergeCompGround", dialogObj).val()) == true){
            $.csAlert({
                msg : "사업명을 입력하여 주십시오.",
                callBack : function() {
                    $("#dialogDgrcompoModifyMergeCompGround", dialogObj).focus();
                }
            });
            
            return false;
        }
        
        if(isEmpty($("#dialogDgrcompoModifyMergeTeMngMokCdNm", dialogObj).val()) == true){
            $.csAlert({
                msg : "통계목코드를 입력하여 주십시오.",
                callBack : function() {
                    $("#dialogDgrcompoModifyMergeTeMngMokCdNm", dialogObj).focus();
                }
            });
            
            return false;
        }
        
        if(isEmpty($("#dialogDgrcompoModifyMergeTeMngMokNm", dialogObj).val()) == true){
            $.csAlert({
                msg : "통계목명을 입력하여 주십시오.",
                callBack : function() {
                    $("#dialogDgrcompoModifyMergeTeMngMokNm", dialogObj).focus();
                }
            });
            
            return false;
        }
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : dialogDgrcompoModifyMergeDoSave
        });
    };
    
    var dialogDgrcompoModifyMergeClose = function(){
        $("#dialogDgrcompoModifyMergeCallBackFunction", dialogObj).val("");
        $("#dialogDgrcompoModifyMergeDgrcompoId", dialogObj).val("");
        $("#dialogDgrcompoModifyMergeFisYear", dialogObj).val("");
        $("#dialogDgrcompoModifyMergeBgtDgr", dialogObj).val("");
        $("#dialogDgrcompoModifyMergeTeBgtCompoId", dialogObj).val("");
        $("#dialogDgrcompoModifyMergeIsLeaf", dialogObj).val("");
        $("#dialogDgrcompoModifyMergeAmtUnit", dialogObj).val("1");
        
        $("#dialogDgrcompoModifyMergeTeBgtCompoSeq", dialogObj).val("");
        $("#dialogDgrcompoModifyMergeDgrcompoNm", dialogObj).val("");
        $("#dialogDgrcompoModifyMergeDemandCompFormular", dialogObj).val("");
        $("#dialogDgrcompoModifyMergeDemandBgtAmt", dialogObj).val("0");
        $("#dialogDgrcompoModifyMergeDemandPreAmt", dialogObj).val("0");
        $("#dialogDgrcompoModifyMergeDemandDiffAmt", dialogObj).val("0");
        $("#dialogDgrcompoModifyMergeCompGround", dialogObj).val("");
        $("#dialogDgrcompoModifyMergeCompFormular", dialogObj).val("");
        $("#dialogDgrcompoModifyMergeBgtAmt", dialogObj).val("0");
        $("#dialogDgrcompoModifyMergePreFormular", dialogObj).val("");
        $("#dialogDgrcompoModifyMergePreAmt", dialogObj).val("0");
        $("#dialogDgrcompoModifyMergeDiffAmt", dialogObj).val("0");
        $("#dialogDgrcompoModifyMergeCompoLevel", dialogObj).val("");
        $("#dialogDgrcompoModifyMergePreCompFormular", dialogObj).val("");
        $("#dialogDgrcompoModifyMergePreBgtAmt", dialogObj).val("0");
        
        $("#dialogDgrcompoModifyMergeDiv").dialog("close");
    };
    
    $("#dialogDgrcompoModifyMergeDiv").dialog({
        title: "예산편성수정",
        autoOpen: false,
        width: 780,
        height: 820,
        modal: true,
        resizable: true,
        open: function(event, ui){
            doDialogDgrcompoModifyMergeSearch();
        },
        buttons : {
            "저장" : function() {
                dialogDgrcompoModifyMergeSaveBtnClick();
            },
            "닫기" : function() {
                dialogDgrcompoModifyMergeClose();
            }
        }
    });
});
</script>
<div id="dialogDgrcompoModifyMergeDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgrcompoModifyMergeCallBackFunction"/>
  <input type="hidden" id="dialogDgrcompoModifyMergeCngHistoryId"/>
  <input type="hidden" id="dialogDgrcompoModifyMergeDgrcompoId"/>
  <input type="hidden" id="dialogDgrcompoModifyMergeFisYear"/>
  <input type="hidden" id="dialogDgrcompoModifyMergeBgtDgr"/>
  <input type="hidden" id="dialogDgrcompoModifyMergeTeBgtCompoId"/>
  <input type="hidden" id="dialogDgrcompoModifyMergeIsLeaf"/>
  <input type="hidden" id="dialogDgrcompoModifyMergeCompoLevel"/>
  <input type="hidden" id="dialogDgrcompoModifyMergeAmtUnit" value="1"/>
  
  <div id="dialogDgrcompoModifyMergeAmtUnitDiv" class="unitDiv" style="top: 0px;">
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
          	<input type="hidden" id="dialogDgrcompoModifyMergeTeBgtCompoSeq"/>
            <input type="text" id="dialogDgrcompoModifyMergeDgrcompoNm" class="readonly" style="width:100%;" readonly/>
          </td>
        </tr>
        <tr>
        	<th colspan="2">통계목코드</th>
        	<td>
	            <input type="text" id="dialogDgrcompoModifyMergeTeMngMokCdNm" class="readonly" style="width:100%;" readonly/>
	        </td>
	        <th>통계목명</th>
	        <td colspan="3">
	          	<input type="text" id="dialogDgrcompoModifyMergeTeMngMokNm" class="readonly" style="width:100%;" readonly/>
	        </td>
        </tr>
        <tr>
          <th colspan="2">사업명</th>
          <td colspan="5">
            <input type="text" id="dialogDgrcompoModifyMergeCompGround" style="width:100%;"/>
          </td>
        </tr>
        <tr>
        	<th>요구</th>
          <th>기정액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyMergeDemandPreAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
          <th>증감액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyMergeDemandDiffAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
          <th>요구액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyMergeDemandBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
        </tr>
        <tr>
        	<th>조정</th>
          <th>기정액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyMergePreAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
          <th>증감액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyMergeDiffAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
          <th>조정액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyMergeBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
        </tr>
        <tr>
        	<th>전년도</th>
          <th>예산액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyMergePreBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
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
  <div id="DIALOG_DGR_COMPO_MODIFY_MERGE_FRSC_DIV" class="csGrid">
    <table id="DIALOG_DGR_COMPO_MODIFY_MERGE_FRSC_GRD" ></table>
  </div>
  <BR>
  <div class="ui-widget-header" style="width:735px;">
    성격별 예산액
  </div>
  <div id="DIALOG_DGR_COMPO_MODIFY_MERGE_CHAR_DIV" class="csGrid">
    <table id="DIALOG_DGR_COMPO_MODIFY_MERGE_CHAR_GRD" ></table>
  </div>
</div>