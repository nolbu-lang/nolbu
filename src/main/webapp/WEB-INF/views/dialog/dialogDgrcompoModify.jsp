<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    var dialogObj = $("#dialogDgrcompoModifyDiv");
    
    var dialogDgrcompoModifyFrscColNames = ['재원명', '전년도최종예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'frscFgCd', 'standFrscCd'];
    var dialogDgrcompoModifyFrscColModel = [
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
    
    var dialogDgrcompoModifyCharColNames = ['성격명', '전년도최종예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'charFgCd'];
    var dialogDgrcompoModifyCharColModel = [
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
        var frscIds = dialogDgrcompoModifyFrscGrid.jqGrid("getDataIDs");
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
            rowData = dialogDgrcompoModifyFrscGrid.jqGrid("getRowData", frscIds[i]);
            
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
        
        var charIds = dialogDgrcompoModifyCharGrid.jqGrid("getDataIDs");
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
            rowData = dialogDgrcompoModifyCharGrid.jqGrid("getRowData", charIds[i]);
            
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

        $("#dialogDgrcompoModifyDemandBgtAmt", dialogObj).val(addCommaStr((sumPreDefFrscAmt + sumDmnDefFrscAmt)));
        $("#dialogDgrcompoModifyDemandPreAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoModifyDemandDiffAmt", dialogObj).val(addCommaStr(sumDmnDefFrscAmt));
        $("#dialogDgrcompoModifyBgtAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt + sumAdjDefFrscAmt));
        $("#dialogDgrcompoModifyPreAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoModifyDiffAmt", dialogObj).val(addCommaStr(sumAdjDefFrscAmt));
        $("#dialogDgrcompoModifyPreBgtAmt", dialogObj).val(addCommaStr(sumPreFrscAmt));
        
        var param = {
                fisYear: $("#dialogDgrcompoModifyFisYear", dialogObj).val(),
                bgtDgr: $("#dialogDgrcompoModifyBgtDgr", dialogObj).val(),
                teBgtCompoId: $("#dialogDgrcompoModifyTeBgtCompoId", dialogObj).val(),
                teBgtCompoSeq: $("#dialogDgrcompoModifyTeBgtCompoSeq", dialogObj).val(),
                demandCompGround: $("#dialogDgrcompoModifyCompGround", dialogObj).val(), 
                demandCompFormular: '',
                //demandCompFormular: $("#dialogDgrcompoModifyDemandCompFormular", dialogObj).val(),
                demandBgtAmt: demandBgtAmt,
                demandPreAmt: demandPreAmt,
                demandDiffAmt: demandDiffAmt,
                compGround: $("#dialogDgrcompoModifyCompGround", dialogObj).val(), 
                //compFormular: $("#dialogDgrcompoModifyCompFormular", dialogObj).val(),
                compFormular: '',
                bgtAmt: bgtAmt, 
                preFormular: '',
                //preFormular: $("#dialogDgrcompoModifyPreFormular", dialogObj).val(),
                preAmt: preAmt, 
                diffAmt: diffAmt, 
                preCompFormular: '',
                //preCompFormular: $("#dialogDgrcompoModifyPreCompFormular", dialogObj).val(),
                preBgtAmt: preBgtAmt,
                amtUnit: $("#dialogDgrcompoModifyAmtUnit", dialogObj).val(),
                isLeaf: $("#dialogDgrcompoModifyIsLeaf", dialogObj).val(),
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
        
        $("#dialogDgrcompoModifyDemandBgtAmt", dialogObj).val(addCommaStr((sumPreDefFrscAmt + sumDmnDefFrscAmt)));
        $("#dialogDgrcompoModifyDemandPreAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoModifyDemandDiffAmt", dialogObj).val(addCommaStr(sumDmnDefFrscAmt));
        $("#dialogDgrcompoModifyBgtAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt + sumAdjDefFrscAmt));
        $("#dialogDgrcompoModifyPreAmt", dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoModifyDiffAmt", dialogObj).val(addCommaStr(sumAdjDefFrscAmt));
        $("#dialogDgrcompoModifyPreBgtAmt", dialogObj).val(addCommaStr(sumPreFrscAmt));
        
        afterSaveChar(dialogDgrcompoModifyCharGrid, "preDefCharAmt");
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
        
        ids = dialogDgrcompoModifyFrscGrid.jqGrid("getDataIDs");
        for(var i = 0; i < ids.length; i++){
            rowData = dialogDgrcompoModifyFrscGrid.jqGrid("getRowData", ids[i]);
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
    var dialogDgrcompoModifyFrscGridParam = {
            id : "DIALOG_DGR_COMPO_MODIFY_FRSC",
            colNames : dialogDgrcompoModifyFrscColNames,
            colModel : dialogDgrcompoModifyFrscColModel,
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
                afterSaveFrsc(dialogDgrcompoModifyFrscGrid, name);
                
                frscEditIRow = 0;
                frscEditICol = 0;
            }
    };
    
    var dialogDgrcompoModifyFrscGrid = $.csGrid(dialogDgrcompoModifyFrscGridParam);
    dialogDgrcompoModifyFrscGrid.jqGrid('setGroupHeaders', {
        useColSpanStyle : true,
        groupHeaders : [
           {startColumnName : 'dmnDefFrscAmt',numberOfColumns : 2, titleText : '요구'},
           {startColumnName : 'adjDefFrscAmt', numberOfColumns : 2, titleText : '조정'} 
        ]
    });

    var charEditIRow = 0;
    var charEditICol = 0;
    var dialogDgrcompoModifyCharGridParam = {
            id : "DIALOG_DGR_COMPO_MODIFY_CHAR",
            colNames : dialogDgrcompoModifyCharColNames,
            colModel : dialogDgrcompoModifyCharColModel,
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
                afterSaveChar(dialogDgrcompoModifyCharGrid, name);
                
                charEditIRow = 0;
                charEditICol = 0;
            }
    };
    
    var dialogDgrcompoModifyCharGrid = $.csGrid(dialogDgrcompoModifyCharGridParam);
    dialogDgrcompoModifyCharGrid.jqGrid('setGroupHeaders', {
        useColSpanStyle : true,
        groupHeaders : [
           {startColumnName : 'dmnDefCharAmt',numberOfColumns : 2, titleText : '요구'},
           {startColumnName : 'adjDefCharAmt', numberOfColumns : 2, titleText : '조정'} 
        ]
    });
    
    var doDialogDgrcompoModifySearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        //dialogDgrcompoModiDgrcompoNm
        console.log(data.dgrcompo);
        $("#dialogDgrcompoModifyTeBgtCompoSeq", dialogObj).val(data.dgrcompo.teBgtCompoSeq);
        $("#dialogDgrcompoModiDgrcompoNm", dialogObj).val(data.dgrcompo.upDgrcompoNm);
        $("#dialogDgrcompoModiTeMngMokCdNm", dialogObj).val(data.dgrcompo.teMngMokCd);
        $("#dialogDgrcompoModiTeMngMokNm", dialogObj).val(data.dgrcompo.teMngMokNm);
        $("#dialogDgrcompoModifyDgrcompoNm", dialogObj).val(data.dgrcompo.dgrcompoNm);
        $("#dialogDgrcompoModifyDemandCompFormular", dialogObj).val(data.dgrcompo.demandCompFormular);
        $("#dialogDgrcompoModifyDemandBgtAmt", dialogObj).val(addCommaStr(data.dgrcompo.demandBgtAmt));
        $("#dialogDgrcompoModifyDemandPreAmt", dialogObj).val(addCommaStr(data.dgrcompo.preAmt));
        $("#dialogDgrcompoModifyDemandDiffAmt", dialogObj).val(addCommaStr(data.dgrcompo.demandDiffAmt));
        $("#dialogDgrcompoModifyCompGround", dialogObj).val(data.dgrcompo.compGround);
        $("#dialogDgrcompoModifyCompFormular", dialogObj).val(data.dgrcompo.compFormular);
        $("#dialogDgrcompoModifyBgtAmt", dialogObj).val(addCommaStr(data.dgrcompo.bgtAmt));
        $("#dialogDgrcompoModifyPreFormular", dialogObj).val(data.dgrcompo.preFormular);
        $("#dialogDgrcompoModifyPreAmt", dialogObj).val(addCommaStr(data.dgrcompo.preAmt));
        $("#dialogDgrcompoModifyDiffAmt", dialogObj).val(addCommaStr(data.dgrcompo.diffAmt));
        $("#dialogDgrcompoModifyCompoLevel", dialogObj).val(data.dgrcompo.compoLevel);
        $("#dialogDgrcompoModifyPreCompFormular", dialogObj).val(data.dgrcompo.preCompFormular);
        $("#dialogDgrcompoModifyPreBgtAmt", dialogObj).val(addCommaStr(data.dgrcompo.preBgtAmt));
        
        dialogDgrcompoModifyFrscGrid.addCsJsonData(data.frscInfo);
        dialogDgrcompoModifyCharGrid.addCsJsonData(data.charInfo);
        
        var isLeaf = $("#dialogDgrcompoModifyIsLeaf", dialogObj).val();
        if(isLeaf === "true"){
            dialogDgrcompoModifyCharGrid.jqGrid("setCell", "090", "preDefCharAmt", "", "not-editable-cell");
            dialogDgrcompoModifyCharGrid.jqGrid("setCell", "090", "dmnDefCharAmt", "", "not-editable-cell");
            dialogDgrcompoModifyCharGrid.jqGrid("setCell", "090", "adjDefCharAmt", "", "not-editable-cell");
            dialogDgrcompoModifyCharGrid.jqGrid("setCell", "090", "preCharAmt", "", "not-editable-cell");
        }else{
            var ids = dialogDgrcompoModifyFrscGrid.jqGrid('getDataIDs');
            for (var i=0; i < ids.length; i++){
                dialogDgrcompoModifyFrscGrid.jqGrid("setCell", ids[i], "preDefFrscAmt", "", "not-editable-cell");
                dialogDgrcompoModifyFrscGrid.jqGrid("setCell", ids[i], "dmnDefFrscAmt", "", "not-editable-cell");
                dialogDgrcompoModifyFrscGrid.jqGrid("setCell", ids[i], "adjDefFrscAmt", "", "not-editable-cell");
                dialogDgrcompoModifyFrscGrid.jqGrid("setCell", ids[i], "preFrscAmt", "", "not-editable-cell");
            }
            
            ids = dialogDgrcompoModifyCharGrid.jqGrid('getDataIDs');
            for (var i=0; i < ids.length; i++){
                dialogDgrcompoModifyCharGrid.jqGrid("setCell", ids[i], "preDefCharAmt", "", "not-editable-cell");
                dialogDgrcompoModifyCharGrid.jqGrid("setCell", ids[i], "dmnDefCharAmt", "", "not-editable-cell");
                dialogDgrcompoModifyCharGrid.jqGrid("setCell", ids[i], "adjDefCharAmt", "", "not-editable-cell");
                dialogDgrcompoModifyCharGrid.jqGrid("setCell", ids[i], "preCharAmt", "", "not-editable-cell");
            }
        }
        
    };
    
    var doDialogDgrcompoModifySearch = function() {
        var unitStr = "(단위:천원)";
        if($("#dialogDgrcompoModifyAmtUnit", dialogObj).val() == "1"){
            unitStr = "(단위:원)";
        }else if($("#dialogDgrcompoModifyAmtUnit", dialogObj).val() == "1000"){
            unitStr = "(단위:천원)";
        }else if($("#dialogDgrcompoModifyAmtUnit", dialogObj).val() == "1000000"){
            unitStr = "(단위:백만원)";
        }
    
        $("#dialogDgrcompoModifyAmtUnitDiv", dialogObj).html(unitStr);
        
        $.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoModifySelectDgrcompos.do",
            data : {
                    fisYear: $("#dialogDgrcompoModifyFisYear", dialogObj).val(),
                    bgtDgr: $("#dialogDgrcompoModifyBgtDgr", dialogObj).val(),
                    teBgtCompoId: $("#dialogDgrcompoModifyTeBgtCompoId", dialogObj).val(),
                    amtUnit: $("#dialogDgrcompoModifyAmtUnit", dialogObj).val()
                   },
            async : true,
            callBack : doDialogDgrcompoModifySearchCallBack
        });
    };
    
    var dialogDgrcompoModifyDoSaveCallBack = function(param){
        var dialogDgrcompoModifyCallBackFunction = $("#dialogDgrcompoModifyCallBackFunction", dialogObj).val();

        if(isEmpty(param) == true){
            return;
        }
        
        param.dgrcompo["dgrcompoId"] = $("#dialogDgrcompoModifyDgrcompoId", dialogObj).val();
        
        if(isEmpty(dialogDgrcompoModifyCallBackFunction) == false){
            eval(dialogDgrcompoModifyCallBackFunction + '('+ jsonToString(param.dgrcompo) + ')');
        }
    };
    
    var dialogDgrcompoModifyDoSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var saveData = getSaveData();
        
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoModifySaveDgrcompos.do",
            data : saveData
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage,
                callBack : function() {
                    dialogDgrcompoModifyClose();
                }
            });
            
            return;
        }
        
        $.csAlert({
            msg : "수정되었습니다.<BR>(상위 항목의 정보는 재 조회 후 확인하실 수 있습니다.)",
            callBack : function() {
                dialogDgrcompoModifyDoSaveCallBack(data);
                
                dialogDgrcompoModifyClose();
            }
        });
    };
    
    var dialogDgrcompoModifySaveBtnClick = function(){
        if(frscEditIRow !== 0 && frscEditICol !== 0){
            dialogDgrcompoModifyFrscGrid.saveCell(frscEditIRow, frscEditICol);
        }
        
        if(charEditIRow !== 0 && charEditICol !== 0){
            dialogDgrcompoModifyCharGrid.saveCell(charEditIRow, charEditICol);
        }
        
        if(isEmpty($("#dialogDgrcompoModifyCompGround", dialogObj).val()) == true){
            $.csAlert({
                msg : "사업명을 입력하여 주십시오.",
                callBack : function() {
                    $("#dialogDgrcompoModifyCompGround", dialogObj).focus();
                }
            });
            
            return false;
        }
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : dialogDgrcompoModifyDoSave
        });
    };
    
    var dialogDgrcompoModifyClose = function(){
        $("#dialogDgrcompoModifyCallBackFunction", dialogObj).val("");
        $("#dialogDgrcompoModifyDgrcompoId", dialogObj).val("");
        $("#dialogDgrcompoModifyFisYear", dialogObj).val("");
        $("#dialogDgrcompoModifyBgtDgr", dialogObj).val("");
        $("#dialogDgrcompoModifyTeBgtCompoId", dialogObj).val("");
        $("#dialogDgrcompoModifyIsLeaf", dialogObj).val("");
        $("#dialogDgrcompoModifyAmtUnit", dialogObj).val("1");
        
        $("#dialogDgrcompoModifyTeBgtCompoSeq", dialogObj).val("");
        $("#dialogDgrcompoModifyDgrcompoNm", dialogObj).val("");
        $("#dialogDgrcompoModifyDemandCompFormular", dialogObj).val("");
        $("#dialogDgrcompoModifyDemandBgtAmt", dialogObj).val("0");
        $("#dialogDgrcompoModifyDemandPreAmt", dialogObj).val("0");
        $("#dialogDgrcompoModifyDemandDiffAmt", dialogObj).val("0");
        $("#dialogDgrcompoModifyCompGround", dialogObj).val("");
        $("#dialogDgrcompoModifyCompFormular", dialogObj).val("");
        $("#dialogDgrcompoModifyBgtAmt", dialogObj).val("0");
        $("#dialogDgrcompoModifyPreFormular", dialogObj).val("");
        $("#dialogDgrcompoModifyPreAmt", dialogObj).val("0");
        $("#dialogDgrcompoModifyDiffAmt", dialogObj).val("0");
        $("#dialogDgrcompoModifyCompoLevel", dialogObj).val("");
        $("#dialogDgrcompoModifyPreCompFormular", dialogObj).val("");
        $("#dialogDgrcompoModifyPreBgtAmt", dialogObj).val("0");
        
        $("#dialogDgrcompoModifyDiv").dialog("close");
    };
    
    $("#dialogDgrcompoModifyDiv").dialog({
        title: "예산편성수정",
        autoOpen: false,
        width: 780,
        height: 800,
        modal: true,
        resizable: true,
        open: function(event, ui){
            doDialogDgrcompoModifySearch();
        },
        buttons : {
            "저장" : function() {
                dialogDgrcompoModifySaveBtnClick();
            },
            "닫기" : function() {
                dialogDgrcompoModifyClose();
            }
        }
    });
});
</script>
<div id="dialogDgrcompoModifyDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgrcompoModifyCallBackFunction"/>
  <input type="hidden" id="dialogDgrcompoModifyDgrcompoId"/>
  <input type="hidden" id="dialogDgrcompoModifyFisYear"/>
  <input type="hidden" id="dialogDgrcompoModifyBgtDgr"/>
  <input type="hidden" id="dialogDgrcompoModifyTeBgtCompoId"/>
  <input type="hidden" id="dialogDgrcompoModifyIsLeaf"/>
  <input type="hidden" id="dialogDgrcompoModifyCompoLevel"/>
  <input type="hidden" id="dialogDgrcompoModifyAmtUnit" value="1"/>
  
  <div id="dialogDgrcompoModifyAmtUnitDiv" class="unitDiv" style="top: 0px;">
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
          	<input type="hidden" id="dialogDgrcompoModifyTeBgtCompoSeq"/>
            <input type="text" id="dialogDgrcompoModiDgrcompoNm" class="readonly" style="width:100%;" readonly/>
          </td>
        </tr>
        <tr>
        	<th colspan="2">통계목코드</th>
        	<td>
	            <input type="text" id="dialogDgrcompoModiTeMngMokCdNm" class="readonly" style="width:100%;" readonly/>
	        </td>
	        <th>통계목명</th>
	        <td colspan="3">
	          	<input type="text" id="dialogDgrcompoModiTeMngMokNm" class="readonly" style="width:100%;" readonly/>
	        </td>
        </tr>
        <tr>
          <th colspan="2">사업명</th>
          <td colspan="5">
            <input type="text" id="dialogDgrcompoModifyCompGround" style="width:100%;"/>
          </td>
        </tr>
        <tr>
        	<th>요구</th>
          <th>기정액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyDemandPreAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
          <th>증감액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyDemandDiffAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
          <th>요구액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyDemandBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
        </tr>
        <tr>
        	<th>조정</th>
          <th>기정액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyPreAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
          <th>증감액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyDiffAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
          <th>조정액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
          </td>
        </tr>
        <tr>
        	<th>전년도</th>
          <th>예산액</th>
          <td>
            <input type="text" id="dialogDgrcompoModifyPreBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
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
  <div id="DIALOG_DGR_COMPO_MODIFY_FRSC_DIV" class="csGrid">
    <table id="DIALOG_DGR_COMPO_MODIFY_FRSC_GRD" ></table>
  </div>
  <BR>
  <div class="ui-widget-header" style="width:735px;">
    성격별 예산액
  </div>
  <div id="DIALOG_DGR_COMPO_MODIFY_CHAR_DIV" class="csGrid">
    <table id="DIALOG_DGR_COMPO_MODIFY_CHAR_GRD" ></table>
  </div>
</div>