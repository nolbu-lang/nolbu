<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    
    var dialogObj = $("#dialogDgrcompoSeperateDiv");
    
    var sepNowCnt = 0;
    var sepData;
    var tabNm;
    
    var dialogDgrcompoSeperateFrscColNames = ['재원명', '전년도예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'frscFgCd', 'standFrscCd'];
    var dialogDgrcompoSeperateFrscColModel = [
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
    
    var dialogDgrcompoSeperateCharColNames = ['성격명', '전년도예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'charFgCd'];
    var dialogDgrcompoSeperateCharColModel = [
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
    
    var dialogDgrcompoSeperateFrscColNamesEdit = ['재원명', '전년도예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'frscFgCd', 'standFrscCd', 'sepNum'];
    var dialogDgrcompoSeperateFrscColModelEdit = [
                                                  {name : 'frscFgNm', index : 'frscFgNm', width : 80, sortable : false, fixed : true, align : 'left'},
                                                  {name : 'preFrscAmt', index : 'preFrscAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : true, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'preDefFrscAmt', index : 'preDefFrscAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : true, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'dmnDefFrscAmt', index : 'dmnDefFrscAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : true, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'demandFrscAmt', index : 'demandFrscAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                                  {name : 'adjDefFrscAmt', index : 'adjDefFrscAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : true, edittype:'text',
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
    
    var dialogDgrcompoSeperateCharColNamesEdit = ['성격명', '전년도예산액', '기정액', '증감액', '요구액', '증감액', '조정액', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'teBgtCompoSeq', 'charFgCd', 'sepNum'];
    var dialogDgrcompoSeperateCharColModelEdit = [
                                                  {name : 'charFgNm', index : 'charFgNm', width : 80, sortable : false, fixed : true, align : 'left'},
                                                  {name : 'preCharAmt', index : 'preCharAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : true, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'preDefCharAmt', index : 'preDefCharAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : true, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'dmnDefCharAmt', index : 'dmnDefCharAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : true, edittype:'text',
                                                      editoptions:{maxlength:17, dataInit: function (elem) {$(elem).numeric();$(elem).focus(function(){$(this).select();});}}, 
                                                      formatter : 'integer', formatoptions : {thousandsSeparator : ","}
                                                  },
                                                  {name : 'demandCharAmt', index : 'demandCharAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                                                  {name : 'adjDefCharAmt', index : 'adjDefCharAmt', width : 80, sortable : false, fixed : true, align : 'right',
                                                      editable : true, edittype:'text',
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
    var sepBodyResize = function () {
    	
    	$('#sepWestDiv1 .viewDiv', dialogObj).width($("#sepWestDiv1", dialogObj).width());
        if(isEmpty($("#DIALOG_DGR_COMPO_SEPERATE_FRSC_GRD", dialogObj)) == false){
            $("#DIALOG_DGR_COMPO_SEPERATE_FRSC_GRD", dialogObj).setGridWidth($("#sepWestDiv2", dialogObj).width());
        }
        
        if(isEmpty($("#DIALOG_DGR_COMPO_SEPERATE_CHAR_GRD", dialogObj)) == false){
            $("#DIALOG_DGR_COMPO_SEPERATE_CHAR_GRD", dialogObj).setGridWidth($("#sepWestDiv3", dialogObj).width());
        }
        
        sepBodySepResize();
    };

    //저장 데이터 반환
    var getSaveData = function(){
    	var saveData = {};
    	var tabDatas = [];
    	var tabData = {};	//저장데이터(총)
    	var frscDatas = [];	//재원별분리데이터 리스트
    	var frscData = {};	//재원별분리 데이터
    	var charDatas = [];	//성격별분리데이터 리스트
    	var charData = {};	//성격별분리 데이터
    	
    	//재원별 예산액 그리드 데이터 확인
    	var sepFrscGrid = $("#DIALOG_DGR_COMPO_SEPERATE_FRSC_GRD", dialogObj);
    	var sepFrscGridData = sepFrscGrid.jqGrid('getRowData');
    	var tabCnt = parseInt($('#dialogDgrcompoSeperateTabCnt').val());
    	var teBgtCompoId = $('#dialogDgrcompoSeperateTeBgtCompoId', dialogObj).val();        	
    	var fisYear = $('#dialogDgrcompoSeperateFisYear', dialogObj).val();        	
    	var bgtDgr = $('#dialogDgrcompoSeperateBgtDgr', dialogObj).val();        	
    	var compoLevel = $('#dialogDgrcompoSeperateCompoLevel', dialogObj).val();        	
    	var deptCd = $('#dialogDgrcompoSeperateDeptCd', dialogObj).val();
    	var amtUnit = $('#dialogDgrcompoSeperateAmtUnit', dialogObj).val();
    	var title = $('#dialogDgrcompoSeperateTitle', dialogObj).val();
    	var indiBns = $('#dialogDgrcompoSeperateIndiBns', dialogObj).val();
    	
    	saveData['teBgtCompoId'] = teBgtCompoId;
    	saveData['fisYear'] = fisYear;
    	saveData['bgtDgr'] = bgtDgr;
    	saveData['compoLevel'] = compoLevel;
    	saveData['deptCd'] = deptCd;
    	saveData['amtUnit'] = amtUnit;
    	saveData['title'] = title;
    	saveData['indiBns'] = indiBns;
    	saveData['cngType'] = 'CH02';
    	
       	//j : 탭번호
       	for(var j=0 ; j<tabCnt ; j++){
       		
       		var dbizCd = $('#dialogDgrcompoSeperateDbizCd' + j, dialogObj).val();
       		var teMngMokCd = $('#dialogDgrcompoSeperateTeMngMokCd' + j, dialogObj).val().replace(/-/gi, '');
       		var teMngMokNm = $('#dialogDgrcompoSeperateTeMngMokNm' + j, dialogObj).val();
       		var compGround = $('#dialogDgrcompoSeperateCompGround' + j, dialogObj).val();
       		var demandPreAmt = $('#dialogDgrcompoSeperateDemandPreAmt' + j, dialogObj).val();
       		var demandDiffAmt = $('#dialogDgrcompoSeperateDemandDiffAmt' + j, dialogObj).val();
       		var demandBgtAmt = $('#dialogDgrcompoSeperateDemandBgtAmt' + j, dialogObj).val();
       		var preAmt = $('#dialogDgrcompoSeperatePreAmt' + j, dialogObj).val();
       		var diffAmt = $('#dialogDgrcompoSeperateDiffAmt' + j, dialogObj).val();
       		var bgtAmt = $('#dialogDgrcompoSeperateBgtAmt' + j, dialogObj).val();
       		var preBgtAmt = $('#dialogDgrcompoSeperatePreBgtAmt' + j, dialogObj).val();
       		
       		if(isEmpty(dbizCd) != true){
       			tabData = getTabSaveData(j);
       			tabDatas.push(tabData);
       		}
       	}
    	
       	saveData['saveDatas'] = tabDatas;
        return saveData;
    };
    
    var getTabSaveData = function(tabNum){
        var rowData;
        var dialogDgrcompoRegiFrscGrid = $("#DIALOG_DGR_COMPO_SEPERATE_FRSC" + tabNum + "_GRD", dialogObj);
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
        
        var dialogDgrcompoRegiCharGrid = $("#DIALOG_DGR_COMPO_SEPERATE_CHAR" + tabNum + "_GRD", dialogObj);
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
                fisYear: $("#dialogDgrcompoSeperateFisYear", dialogObj).val(),
                bgtDgr: $("#dialogDgrcompoSeperateBgtDgr", dialogObj).val(),
                deptCd: $('#dialogDgrcompoSeperateDeptCd', dialogObj).val(),
                dbizCd: $('#dialogDgrcompoSeperateDbizCd' + tabNum, dialogObj).val(),
                teMngMokCd: $('#dialogDgrcompoSeperateTeMngMokCd' + tabNum, dialogObj).val().replace(/-/gi, ''),
                upTeBgtCompoId: $('#dialogDgrcompoSeperateUpTeBgtCompoId', dialogObj).val(),
                upTeBgtCompoSeq: $('#dialogDgrcompoSeperateUpTeBgtCompoSeq', dialogObj).val(),
                compoLevel : $('#dialogDgrcompoSeperateCompoLevel', dialogObj).val(),
                demandCompGround: $('#dialogDgrcompoSeperateCompGround' + tabNum, dialogObj).val(), 
                demandCompFormular: '',
                demandBgtAmt: demandBgtAmt,
                demandPreAmt: demandPreAmt,
                demandDiffAmt: demandDiffAmt,
                compGround: $('#dialogDgrcompoSeperateCompGround' + tabNum, dialogObj).val(), 
                compFormular: '',
                bgtAmt: bgtAmt, 
                preFormular: '',
                preAmt: preAmt, 
                diffAmt: diffAmt, 
                preCompFormular: '',
                preBgtAmt: preBgtAmt,
                amtUnit: $('#dialogDgrcompoSeperateAmtUnit', dialogObj).val(),
                frscs: frscs,
                chars: chars,
                cngType: 'CH02',
                grpLvl: '2'
        };
        
        return param;
        
    };
    
    var checkStr = function(str){
    	if(isEmpty(str) == true){
    		return "";
    	}else{
    		return str;
    	}
    }
    
    //재원별 예산액 금액 수정시 실행
    var afterSaveFrsc = function(gridObject, name, cnt){
        if(name !== "preDefFrscAmt" && name !== "dmnDefFrscAmt" && name !== "adjDefFrscAmt" && name !== "preFrscAmt"){
            return;
        }

        gridObject = $('#DIALOG_DGR_COMPO_SEPERATE_FRSC' + cnt + '_GRD', dialogObj);
        
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
        
        $("#dialogDgrcompoSeperateDemandBgtAmt" + cnt, dialogObj).val(addCommaStr((sumPreDefFrscAmt + sumDmnDefFrscAmt)));
        $("#dialogDgrcompoSeperateDemandPreAmt" + cnt, dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoSeperateDemandDiffAmt" + cnt, dialogObj).val(addCommaStr(sumDmnDefFrscAmt));
        $("#dialogDgrcompoSeperateBgtAmt" + cnt, dialogObj).val(addCommaStr(sumPreDefFrscAmt + sumAdjDefFrscAmt));
        $("#dialogDgrcompoSeperatePreAmt" + cnt, dialogObj).val(addCommaStr(sumPreDefFrscAmt));
        $("#dialogDgrcompoSeperateDiffAmt" + cnt, dialogObj).val(addCommaStr(sumAdjDefFrscAmt));
        $("#dialogDgrcompoSeperatePreBgtAmt" + cnt, dialogObj).val(addCommaStr(sumPreFrscAmt));

        var tabFrscGrid = $('#DIALOG_DGR_COMPO_SEPERATE_CHAR' + cnt + '_GRD', dialogObj);
        
        afterSaveChar(tabFrscGrid, "preDefCharAmt", cnt);
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
        
        var frscGridObject = $('#DIALOG_DGR_COMPO_SEPERATE_FRSC' + cnt + '_GRD', dialogObj);
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
    var dialogDgrcompoSeperateFrscGridParam = {
            id : "DIALOG_DGR_COMPO_SEPERATE_FRSC",
            colNames : dialogDgrcompoSeperateFrscColNames,
            colModel : dialogDgrcompoSeperateFrscColModel,
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
                //afterSaveFrsc(dialogDgrcompoSeperateFrscGrid, name);
                
                frscEditIRow = 0;
                frscEditICol = 0;
            }
    };
    
    //원본 재원별예산액 그리드
    var dialogDgrcompoSeperateFrscGrid = $.csGrid(dialogDgrcompoSeperateFrscGridParam);
    dialogDgrcompoSeperateFrscGrid.jqGrid('setGroupHeaders', {
        useColSpanStyle : true,
        groupHeaders : [
           {startColumnName : 'dmnDefFrscAmt',numberOfColumns : 2, titleText : '요구'},
           {startColumnName : 'adjDefFrscAmt', numberOfColumns : 2, titleText : '조정'} 
        ]
    });

    //원본 성격별예산액 그리드 설정
    var charEditIRow = 0;
    var charEditICol = 0;
    var dialogDgrcompoSeperateCharGridParam = {
            id : "DIALOG_DGR_COMPO_SEPERATE_CHAR",
            colNames : dialogDgrcompoSeperateCharColNames,
            colModel : dialogDgrcompoSeperateCharColModel,
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
                //afterSaveChar(dialogDgrcompoSeperateCharGrid, name);
                
                charEditIRow = 0;
                charEditICol = 0;
            }
    };
    
    //원본 성격별예산액 그리드
    var dialogDgrcompoSeperateCharGrid = $.csGrid(dialogDgrcompoSeperateCharGridParam);
    dialogDgrcompoSeperateCharGrid.jqGrid('setGroupHeaders', {
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
    var doDialogDgrcompoSeperateSearchCallBack = function(data) {
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        $("#dialogDgrcompoSeperateTeBgtCompoSeq", dialogObj).val(data.dgrcompo.teBgtCompoSeq);
        //$("#dialogDgrcompoSeperateDgrcompoNm", dialogObj).val(data.dgrcompo.dgrcompoNm);
        $("#dialogDgrcompoSeperateDbizNm", dialogObj).val(data.dgrcompo.upDgrcompoNm);
        $("#dialogDgrcompoSeperateTeMngMokCd", dialogObj).val(data.dgrcompo.teMngMokCd);
        $("#dialogDgrcompoSeperateTeMngMokNm", dialogObj).val(data.dgrcompo.teMngMokNm);
        $("#dialogDgrcompoSeperateDemandCompFormular", dialogObj).val(data.dgrcompo.demandCompFormular);
        $("#dialogDgrcompoSeperateDemandBgtAmt", dialogObj).val(addCommaStr(data.dgrcompo.demandBgtAmt));
        $("#dialogDgrcompoSeperateDemandPreAmt", dialogObj).val(addCommaStr(data.dgrcompo.preAmt));
        $("#dialogDgrcompoSeperateDemandDiffAmt", dialogObj).val(addCommaStr(data.dgrcompo.demandDiffAmt));
        $("#dialogDgrcompoSeperateCompGround", dialogObj).val(data.dgrcompo.compGround);
        $("#dialogDgrcompoSeperateCompFormular", dialogObj).val(data.dgrcompo.compFormular);
        $("#dialogDgrcompoSeperateBgtAmt", dialogObj).val(addCommaStr(data.dgrcompo.bgtAmt));
        $("#dialogDgrcompoSeperatePreFormular", dialogObj).val(data.dgrcompo.preFormular);
        $("#dialogDgrcompoSeperatePreAmt", dialogObj).val(addCommaStr(data.dgrcompo.preAmt));
        $("#dialogDgrcompoSeperateDiffAmt", dialogObj).val(addCommaStr(data.dgrcompo.diffAmt));
        $("#dialogDgrcompoSeperateCompoLevel", dialogObj).val(data.dgrcompo.compoLevel);
        $("#dialogDgrcompoSeperatePreCompFormular", dialogObj).val(data.dgrcompo.preCompFormular);
        $("#dialogDgrcompoSeperatePreBgtAmt", dialogObj).val(addCommaStr(data.dgrcompo.preBgtAmt));
        
        dialogDgrcompoSeperateFrscGrid.addCsJsonData(data.frscInfo);
        dialogDgrcompoSeperateCharGrid.addCsJsonData(data.charInfo);
        
        sepData = initData(data);
        
        var isLeaf = $("#dialogDgrcompoSeperateIsLeaf", dialogObj).val();
        if(isLeaf === "true"){
            dialogDgrcompoSeperateCharGrid.jqGrid("setCell", "090", "preDefCharAmt", "", "not-editable-cell");
            dialogDgrcompoSeperateCharGrid.jqGrid("setCell", "090", "dmnDefCharAmt", "", "not-editable-cell");
            dialogDgrcompoSeperateCharGrid.jqGrid("setCell", "090", "adjDefCharAmt", "", "not-editable-cell");
            dialogDgrcompoSeperateCharGrid.jqGrid("setCell", "090", "preCharAmt", "", "not-editable-cell");
        }else{
            var ids = dialogDgrcompoSeperateFrscGrid.jqGrid('getDataIDs');
            for (var i=0; i < ids.length; i++){
                dialogDgrcompoSeperateFrscGrid.jqGrid("setCell", ids[i], "preDefFrscAmt", "", "not-editable-cell");
                dialogDgrcompoSeperateFrscGrid.jqGrid("setCell", ids[i], "dmnDefFrscAmt", "", "not-editable-cell");
                dialogDgrcompoSeperateFrscGrid.jqGrid("setCell", ids[i], "adjDefFrscAmt", "", "not-editable-cell");
                dialogDgrcompoSeperateFrscGrid.jqGrid("setCell", ids[i], "preFrscAmt", "", "not-editable-cell");
            }
            
            ids = dialogDgrcompoSeperateCharGrid.jqGrid('getDataIDs');
            for (var i=0; i < ids.length; i++){
                dialogDgrcompoSeperateCharGrid.jqGrid("setCell", ids[i], "preDefCharAmt", "", "not-editable-cell");
                dialogDgrcompoSeperateCharGrid.jqGrid("setCell", ids[i], "dmnDefCharAmt", "", "not-editable-cell");
                dialogDgrcompoSeperateCharGrid.jqGrid("setCell", ids[i], "adjDefCharAmt", "", "not-editable-cell");
                dialogDgrcompoSeperateCharGrid.jqGrid("setCell", ids[i], "preCharAmt", "", "not-editable-cell");
            }
        }
        
       
        addBtn();
    };
    
    //원본 조회시작
    var doDialogDgrcompoSeperateSearch = function() {
        var unitStr = "(단위:천원)";
        if($("#dialogDgrcompoSeperateAmtUnit", dialogObj).val() == "1"){
            unitStr = "(단위:원)";
        }else if($("#dialogDgrcompoSeperateAmtUnit", dialogObj).val() == "1000"){
            unitStr = "(단위:천원)";
        }else if($("#dialogDgrcompoSeperateAmtUnit", dialogObj).val() == "1000000"){
            unitStr = "(단위:백만원)";
        }
    
        $("#dialogDgrcompoSeperateAmtUnitDiv", dialogObj).html(unitStr);
        
        $.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoModifySelectDgrcompos.do",
            data : {
                    fisYear: $("#dialogDgrcompoSeperateFisYear", dialogObj).val(),
                    bgtDgr: $("#dialogDgrcompoSeperateBgtDgr", dialogObj).val(),
                    teBgtCompoId: $("#dialogDgrcompoSeperateTeBgtCompoId", dialogObj).val(),
                    amtUnit: $("#dialogDgrcompoSeperateAmtUnit", dialogObj).val()
                   },
            async : true,
            callBack : doDialogDgrcompoSeperateSearchCallBack
        });
    };
    
    
    //저장실행 후 
    var dialogDgrcompoSeperateDoSaveCallBack = function(data){
    	if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        $.csAlert({
            msg : "분리가 완료되었습니다.",
            callBack : function() {
            	dialogDgrcompoSeperateClose();
            	
            	var dialogDgrcompoSeperateCallBackFunction = $("#dialogDgrcompoSeperateCallBackFunction", dialogObj).val();

                if(isEmpty(data) == true){
                    return;
                }
                
                if(isEmpty(dialogDgrcompoSeperateCallBackFunction) == false){
                    eval(dialogDgrcompoSeperateCallBackFunction + '('+ jsonToString(data.dgrcompo) + ')');
                }
            }
        });
    };
    
    //저장실행
    var dialogDgrcompoSeperateDoSave = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var saveDatas = getSaveData();
        
        //console.log(saveDatas);
        var data = $.csAjaxCall({
            url : "/dialog/ajaxDialogDgrcompoCngDgrcompoSep.do",
            data : saveDatas,
            async : true,
	        callBack : dialogDgrcompoSeperateDoSaveCallBack
        });
        
    };
    
    
  //금액체크
    var checkAmt = function(){
    	var flag = true;
    	
    	//재원별 예산액 그리드 데이터 확인
    	var sepFrscGrid = $("#DIALOG_DGR_COMPO_SEPERATE_FRSC_GRD", dialogObj);
    	var sepFrscGridData = sepFrscGrid.jqGrid('getRowData');
    	var tabCnt = parseInt($('#dialogDgrcompoSeperateTabCnt').val());
    	
    	if(tabCnt < 1){
    		$.csAlert({
                msg : "탭을 추가하여 주세요."
            });
        	$("#bcjisLoading").fadeOut(300);
        	return false;
    	}
    	//원본 재원별 예산액 확인
    	for(var i=0 ; i<sepFrscGridData.length ; i++){
    		
    		//i : 원번 row번호
    		var rawData = sepFrscGridData[i];
        	var preFrscAmt = parseInt(rawData.preFrscAmt);
        	var preDefFrscAmt = parseInt(rawData.preDefFrscAmt);
        	var dmnDefFrscAmt = parseInt(rawData.dmnDefFrscAmt);
        	var demandFrscAmt = parseInt(rawData.demandFrscAmt);
        	var adjDefFrscAmt = parseInt(rawData.adjDefFrscAmt);
        	var frscAmt = parseInt(rawData.frscAmt);
        	
        	var preFrscAmtTab = 0;
        	var preDefFrscAmtTab = 0;
        	var dmnDefFrscAmtTab = 0;
        	var demandFrscAmtTab = 0;
        	var adjDefFrscAmtTab = 0;
        	var frscAmtTab = 0;
        	
        	//j : 탭번호
        	for(var j=0 ; j<tabCnt ; j++){
        		//첫행 재원명 클릭하여 input box 초기화
           		//이런 방법말고 제대로 된 방법은 없을까?
           		$("#DIALOG_DGR_COMPO_SEPERATE_FRSC" + j + "_GRD", $("#dialogDgrcompoSeperateDiv")).find('tr').eq(1).find('td').eq(0).trigger("click");
        		
        		var tabFrscGrid = $("#DIALOG_DGR_COMPO_SEPERATE_FRSC" + j + "_GRD", dialogObj);
        		var tabFrscGridData = tabFrscGrid.jqGrid('getRowData');
           		var tabData = tabFrscGridData[i];  //원본과 같은 라인의 data 가져오기
           		if(tabData){
           			preFrscAmtTab += parseInt(tabData.preFrscAmt);
               		preDefFrscAmtTab += parseInt(tabData.preDefFrscAmt);
               		dmnDefFrscAmtTab += parseInt(tabData.dmnDefFrscAmt);
               		demandFrscAmtTab += parseInt(tabData.demandFrscAmt);
               		adjDefFrscAmtTab += parseInt(tabData.adjDefFrscAmt);
               		frscAmtTab += parseInt(tabData.frscAmt);	
           		}
           		
        	}
        	
        	//원본 금액과 탭금액의 합이 하나라도 다를경우 return false;
        	if(preFrscAmt != preFrscAmtTab ||
        			preDefFrscAmt != preDefFrscAmtTab ||
        			dmnDefFrscAmt != dmnDefFrscAmtTab ||
        			demandFrscAmt != demandFrscAmtTab ||
        			adjDefFrscAmt != adjDefFrscAmtTab ||
        			frscAmt != frscAmtTab
        			){
        		
        		$.csAlert({
                    msg : rawData.frscFgNm + "의 예산액이 맞지 않습니다. 다시 확인해 주세요."
                });
            	$("#bcjisLoading").fadeOut(300);
        		return false;
        	}
        	
        	
    	}
    	
    	//성격별 예산액 그리드 데이터 확인
    	var sepCharGrid = $("#DIALOG_DGR_COMPO_SEPERATE_CHAR_GRD", dialogObj);
    	var sepCharGridData = sepCharGrid.jqGrid('getRowData');
    	var tabCnt = parseInt($('#dialogDgrcompoSeperateTabCnt').val());
    	//원본 재원별 예산액 확인
    	for(var i=0 ; i<sepCharGridData.length ; i++){
    		
    		//i : 원번 row번호
    		var rawData = sepCharGridData[i];
        	var preCharAmt = parseInt(rawData.preCharAmt);
        	var preDefCharAmt = parseInt(rawData.preDefCharAmt);
        	var dmnDefCharAmt = parseInt(rawData.dmnDefCharAmt);
        	var demandCharAmt = parseInt(rawData.demandCharAmt);
        	var adjDefCharAmt = parseInt(rawData.adjDefCharAmt);
        	var charAmt = parseInt(rawData.charAmt);
        	
        	var preCharAmtTab = 0;
        	var preDefCharAmtTab = 0;
        	var dmnDefCharAmtTab = 0;
        	var demandCharAmtTab = 0;
        	var adjDefCharAmtTab = 0;
        	var charAmtTab = 0;
        	
        	//j : 탭번호
        	for(var j=0 ; j<tabCnt ; j++){
        		var tabGrid = $("#DIALOG_DGR_COMPO_SEPERATE_CHAR" + j + "_GRD", dialogObj);
        		var tabGridData = tabGrid.jqGrid('getRowData');
           		var tabData = tabGridData[i];  //원본과 같은 라인의 data 가져오기
           		
           		//첫행 재원명 클릭하여 input box 초기화
           		//이런 방법말고 제대로 된 방법은 없을까?
           		$("#DIALOG_DGR_COMPO_SEPERATE_CHAR" + j + "_GRD", $("#dialogDgrcompoSeperateDiv")).find('tr').eq(1).find('td').eq(0).trigger("click");
           		
           		if(tabData){
           			preCharAmtTab += parseInt(tabData.preCharAmt);
           			preDefCharAmtTab += parseInt(tabData.preDefCharAmt);
           			dmnDefCharAmtTab += parseInt(tabData.dmnDefCharAmt);
               		demandCharAmtTab += parseInt(tabData.demandCharAmt);
               		adjDefCharAmtTab += parseInt(tabData.adjDefCharAmt);
               		charAmtTab += parseInt(tabData.charAmt);	
           		}
           		
        	}
        	
        	//원본 금액과 탭금액의 합이 하나라도 다를경우 return false;
        	if(preCharAmt != preCharAmtTab ||
        			preDefCharAmt != preDefCharAmtTab ||
        			dmnDefCharAmt != dmnDefCharAmtTab ||
        			demandCharAmt != demandCharAmtTab ||
        			adjDefCharAmt != adjDefCharAmtTab ||
        			charAmt != charAmtTab
        			){
        		
        		$.csAlert({
                    msg : rawData.frscFgNm + "의 금액을 확인하여 주세요."
                });
            	$("#bcjisLoading").fadeOut(300);
        		return false;
        		
        		/* if(preCharAmt != preCharAmtTab){
        			console.log('preCharAmt : ' + preCharAmt + ' : ' + preCharAmtTab);
        		}
        		if(preDefCharAmt != preDefCharAmtTab){
        			console.log('preDefCharAmt : ' + preDefCharAmt + ' : ' + preDefCharAmtTab);
        		}
        		if(dmnDefCharAmt != dmnDefCharAmtTab){
        			console.log('dmnDefCharAmt : ' + dmnDefCharAmt + ' : ' + dmnDefCharAmtTab);
        		}
        		if(demandCharAmt != demandCharAmtTab){
        			console.log('demandCharAmt : ' + demandCharAmt + ' : ' + demandCharAmtTab);
        		}
        		if(adjDefCharAmt != adjDefCharAmtTab){
        			console.log('adjDefCharAmt : ' + adjDefCharAmt + ' : ' + adjDefCharAmtTab);
        		}
        		if(charAmt != charAmtTab){
        			console.log('charAmt : ' + charAmt + ' : ' + charAmtTab);
        		} */
        		
        		return false;
        	}
    	}
    	
    	return flag;
    }
  
    //저장버튼 클릭
    var dialogDgrcompoSeperateSaveBtnClick = function(){
    	$("#bcjisLoading").show();
        if(frscEditIRow !== 0 && frscEditICol !== 0){
            dialogDgrcompoSeperateFrscGrid.saveCell(frscEditIRow, frscEditICol);
        }
        
        if(charEditIRow !== 0 && charEditICol !== 0){
            dialogDgrcompoSeperateCharGrid.saveCell(charEditIRow, charEditICol);
        }
        
        if(isEmpty($("#dialogDgrcompoSeperateCompGround", dialogObj).val()) == true){
            $.csAlert({
                msg : "사업명을 입력하여 주십시오.",
                callBack : function() {
                    $("#dialogDgrcompoSeperateCompGround", dialogObj).focus();
                }
            });
            
            return false;
        }
        
        
        //금액 확인
        if(checkAmt()){
        	
	        $("#bcjisLoading").fadeOut(300);
	        $.csConfirm({
	            msg : "저장하시겠습니까?",
	            callBack : dialogDgrcompoSeperateDoSave
	        });
        }
    };
    
    //창닫기
    var dialogDgrcompoSeperateClose = function(){
        $("#dialogDgrcompoSeperateCallBackFunction", dialogObj).val("");
        $("#dialogDgrcompoSeperateDgrcompoId", dialogObj).val("");
        $("#dialogDgrcompoSeperateFisYear", dialogObj).val("");
        $("#dialogDgrcompoSeperateBgtDgr", dialogObj).val("");
        $("#dialogDgrcompoSeperateTeBgtCompoId", dialogObj).val("");
        $("#dialogDgrcompoSeperateIsLeaf", dialogObj).val("");
        $("#dialogDgrcompoSeperateAmtUnit", dialogObj).val("1");
        
        $("#dialogDgrcompoSeperateTeBgtCompoSeq", dialogObj).val("");
        $("#dialogDgrcompoSeperateDgrcompoNm", dialogObj).val("");
        $("#dialogDgrcompoSeperateDbizNm", dialogObj).val("");
        $("#dialogDgrcompoSeperateTeMngMokCd", dialogObj).val("");
        $("#dialogDgrcompoSeperateTeMngMokNm", dialogObj).val("");
        $("#dialogDgrcompoSeperateDemandCompFormular", dialogObj).val("");
        $("#dialogDgrcompoSeperateDemandBgtAmt", dialogObj).val("0");
        $("#dialogDgrcompoSeperateDemandPreAmt", dialogObj).val("0");
        $("#dialogDgrcompoSeperateDemandDiffAmt", dialogObj).val("0");
        $("#dialogDgrcompoSeperateCompGround", dialogObj).val("");
        $("#dialogDgrcompoSeperateCompFormular", dialogObj).val("");
        $("#dialogDgrcompoSeperateBgtAmt", dialogObj).val("0");
        $("#dialogDgrcompoSeperatePreFormular", dialogObj).val("");
        $("#dialogDgrcompoSeperatePreAmt", dialogObj).val("0");
        $("#dialogDgrcompoSeperateDiffAmt", dialogObj).val("0");
        $("#dialogDgrcompoSeperateCompoLevel", dialogObj).val("");
        $("#dialogDgrcompoSeperatePreCompFormular", dialogObj).val("");
        $("#dialogDgrcompoSeperatePreBgtAmt", dialogObj).val("0");
        
        $("#dialogDgrcompoSeperateDiv").dialog("close");
    };
    
    //분리창 오픈시 세팅
    function openDialog(){
        
    	$("#sepBody", dialogObj).layout({
            west__size : "50%",
            center__onresize: sepBodyResize
        });
    	
    	sepBodyResize();
    	
    	$('.ui-icon-close', dialogObj).each(function(){
    		$(this).trigger("click")
    	});
    	
    	$('#dialogDgrcompoSeperateTabCnt').val(0);
    	
    	$("#addBtn", dialogObj).btnChangeState(true);
    }

    //다이얼로그 실행
    $("#dialogDgrcompoSeperateDiv").dialog({
        title: "예산분리",
        autoOpen: false,
        width: 1500,
        height: 800,
        modal: true,
        resizable: true,
        open: function(event, ui){
            doDialogDgrcompoSeperateSearch();
            openDialog();
        },
        buttons : {
        	"추가" : function() {
            	addBtn();
            },
            "저장" : function() {
                dialogDgrcompoSeperateSaveBtnClick();
            },
            "닫기" : function() {
                dialogDgrcompoSeperateClose();
            }
        }
    });

    //분리 탭 세팅
    var sepTab = $("#sepTab").tabs();
    
    //추가 버튼
    function addBtn(){
    	var tabCnt = parseInt($('#dialogDgrcompoSeperateTabCnt').val());
        sepNowCnt = tabCnt;
        
        var tabLength = $('div[id^=tab_]', $('#sepTab')).length;
        
        if(tabLength > 9){
        	$.csAlert({
                msg : '추가는 10개까지만 가능합니다.'
            });
        	return false;
        }
        
        
        var id = 'tab_' + tabCnt;
        var label = tabNm + '_' + tabCnt;
        var tabTemplate = "<li><a href='!{href}' id='tabTitle" + tabCnt + "'>!{label}</a> <span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>";
        var li = $(tabTemplate.replace(/!\{href\}/g, "#" + id).replace(/!\{label\}/g, label));

        sepTab.find(".ui-tabs-nav").append(li);
        sepTab.append('<div id="' + id + '" style="height:100%;overflow: auto;">' + getSepHtml(tabCnt) + '</div>');
        
        sepTab.tabs("refresh");
        
        $('#dialogDgrcompoSeperateTabCnt').val(tabCnt + 1);
        
        setSepGridData(tabCnt);
        
        $("[href='#"+id+"']", dialogObj).trigger("click");
    }
    
    //탭 닫기 실행
	sepTab.delegate("span.ui-icon-close", "click", function() {
        var panelId = $(this).closest("li").remove().attr("aria-controls");
        $("#" + panelId).remove();
        sepTab.tabs("refresh");
    });
	
    //탭추가시 내용 입력
	function getSepHtml(cnt){
		var sepHtml = '<div id="sepWestDiv1' + cnt + '"  style="padding-bottom:20px;">';
		sepHtml += '<div id="dialogDgrcompoSeperateAmtUnitDiv' + cnt + '" class="unitDiv" style="top: 0px;">';
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
		sepHtml += '<input type="hidden" id="dialogDgrcompoSeperateTeBgtCompoSeq' + cnt + '"/>';
		sepHtml += '<input type="hidden" id="dialogDgrcompoSeperateDbizCd' + cnt + '"/>';
		sepHtml += "<a class='btnClass' href='#none' onclick='javascript:getDbizData(" + cnt + ");' enabledYn='N'>검색</a>";
		sepHtml += '<input type="text" id="dialogDgrcompoSeperateDbizNm' + cnt + '" class="readonly" style="width:90%;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '</tr>';
		sepHtml += '<tr>';
		sepHtml += '<th colspan="2">통계목코드</th>';
        sepHtml += '<td>';
        sepHtml += '<input type="text" id="dialogDgrcompoSeperateTeMngMokCd' + cnt + '" class="readonly" style="width:100%;" />';
        sepHtml += '</td>';
        sepHtml += '<th>통계목명</th>';
        sepHtml += '<td colspan="3">';
        sepHtml += '<input type="text" id="dialogDgrcompoSeperateTeMngMokNm' + cnt + '" class="readonly" style="width:100%;" />';
        sepHtml += '</td>';
        sepHtml += '</tr>';
		sepHtml += '<tr>';
		sepHtml += '<th colspan="2">사업명</th>';
		sepHtml += '<td colspan="5">';
		sepHtml += '<input type="text" id="dialogDgrcompoSeperateCompGround' + cnt + '" style="width:100%;" onchange="javascript:changeTabTitle(\'' + cnt + '\');" />';
		sepHtml += '</td>';
		sepHtml += '</tr>';
		sepHtml += '<tr>';
		sepHtml += '<th>요구</th>';
		sepHtml += '<th>기정액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoSeperateDemandPreAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '<th>증감액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoSeperateDemandDiffAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '<th>요구액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoSeperateDemandBgtAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '</tr>';
		sepHtml += '<tr>';
		sepHtml += '<th>조정</th>';
		sepHtml += '<th>기정액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoSeperatePreAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '<th>증감액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoSeperateDiffAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '<th>조정액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoSeperateBgtAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '</tr>';
		sepHtml += '<tr>';
		sepHtml += '<th>전년도</th>';
		sepHtml += '<th>예산액</th>';
		sepHtml += '<td>';
		sepHtml += '<input type="text" id="dialogDgrcompoSeperatePreBgtAmt' + cnt + '" class="readonly" style="width:100%;text-align:right;" readonly/>';
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
		sepHtml += '<div id="DIALOG_DGR_COMPO_SEPERATE_FRSC' + cnt + '_DIV" class="csGrid">';
		sepHtml += '<table id="DIALOG_DGR_COMPO_SEPERATE_FRSC' + cnt + '_GRD"  style="border:0px;height:100%;"></table>';
		sepHtml += '</div>';
		sepHtml += '</div>';
		sepHtml += '<div id="sepWestDiv3' + cnt + '">';
		sepHtml += '<div class="ui-widget-header">';
		sepHtml += '성격별 예산액';
		sepHtml += '</div>';
		sepHtml += '<div id="DIALOG_DGR_COMPO_SEPERATE_CHAR' + cnt + '_DIV" class="csGrid">';
		sepHtml += '<table id="DIALOG_DGR_COMPO_SEPERATE_CHAR' + cnt + '_GRD" ></table>';
		sepHtml += '</div>';
		sepHtml += '</div>';
		
		return sepHtml;
	}
	
    //탭 리사이즈
	var sepBodySepResize = function () {
		var cntSize = parseInt($('#dialogDgrcompoSeperateTabCnt').val());
		
		var width = $("#sepTab", dialogObj).width() - (13.2 * 2); // 2em padding 빼기
		for(var i=0 ; i<cntSize ; i++){
			
			$('#sepWestDiv1' + i + ' .viewDiv', dialogObj).width(width);
			if(isEmpty($("#DIALOG_DGR_COMPO_SEPERATE_FRSC" + i + "_GRD", dialogObj)) == false){
	             $("#DIALOG_DGR_COMPO_SEPERATE_FRSC" + i + "_GRD", dialogObj).setGridWidth(width);
	        }
	         
	        if(isEmpty($("#DIALOG_DGR_COMPO_SEPERATE_CHAR" + i + "_GRD", dialogObj)) == false){
	             $("#DIALOG_DGR_COMPO_SEPERATE_CHAR" + i + "_GRD", dialogObj).setGridWidth(width);
	        }
	        
		}
         
     };
     
     //탭 그리드 세팅
	function setSepGridData(cnt){
		
		var dialogDgrcompoSeperateFrscGridSep = $.csGrid( {
            id : "DIALOG_DGR_COMPO_SEPERATE_FRSC" + cnt,
            colNames : dialogDgrcompoSeperateFrscColNamesEdit,
            colModel : dialogDgrcompoSeperateFrscColModelEdit,
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
                afterSaveFrsc(dialogDgrcompoSeperateFrscGridSep, name, cnt);
                
                frscEditIRow = 0;
                frscEditICol = 0;
            },
            loadComplete: function (data) {
            	//sepBodyResize();
            }
    	});
	    dialogDgrcompoSeperateFrscGridSep.jqGrid('setGroupHeaders', {
	        useColSpanStyle : true,
	        groupHeaders : [
	           {startColumnName : 'dmnDefFrscAmt',numberOfColumns : 2, titleText : '요구'},
	           {startColumnName : 'adjDefFrscAmt', numberOfColumns : 2, titleText : '조정'} 
	        ]
	    });
	    
	    var dialogDgrcompoSeperateCharGridSep = $.csGrid({
            id : "DIALOG_DGR_COMPO_SEPERATE_CHAR" + cnt,
            colNames : dialogDgrcompoSeperateCharColNamesEdit,
            colModel : dialogDgrcompoSeperateCharColModelEdit,
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
                afterSaveChar(dialogDgrcompoSeperateCharGridSep, name, cnt);
                
                charEditIRow = 0;
                charEditICol = 0;
            },
            loadComplete: function (data) {
            	//sepBodyResize();
            }
    	});
	    dialogDgrcompoSeperateCharGridSep.jqGrid('setGroupHeaders', {
	        useColSpanStyle : true,
	        groupHeaders : [
	           {startColumnName : 'dmnDefCharAmt',numberOfColumns : 2, titleText : '요구'},
	           {startColumnName : 'adjDefCharAmt', numberOfColumns : 2, titleText : '조정'} 
	        ]
	    });
	    
	    var unitStr = "(단위:천원)";
        if($("#dialogDgrcompoSeperateAmtUnit" + cnt, dialogObj).val() == "1"){
            unitStr = "(단위:원)";
        }else if($("#dialogDgrcompoSeperateAmtUnit" + cnt, dialogObj).val() == "1000"){
            unitStr = "(단위:천원)";
        }else if($("#dialogDgrcompoSeperateAmtUnit" + cnt, dialogObj).val() == "1000000"){
            unitStr = "(단위:백만원)";
        }
	    
	    var data = sepData;
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            return;
        }
        
        $("#dialogDgrcompoSeperateTeBgtCompoSeq" + cnt, dialogObj).val(data.dgrcompo.teBgtCompoSeq);
        //$("#dialogDgrcompoSeperateDgrcompoNm" + cnt, dialogObj).val(data.dgrcompo.dgrcompoNm + '_' + cnt);
        $("#dialogDgrcompoSeperateDbizCd" + cnt, dialogObj).val(data.dgrcompo.dbizCd);
        $("#dialogDgrcompoSeperateDbizNm" + cnt, dialogObj).val(data.dgrcompo.upDgrcompoNm);
        $("#dialogDgrcompoSeperateTeMngMokCd" + cnt, dialogObj).val(data.dgrcompo.teMngMokCd);
        $("#dialogDgrcompoSeperateTeMngMokNm" + cnt, dialogObj).val(data.dgrcompo.teMngMokNm);
        $("#dialogDgrcompoSeperateDemandCompFormular" + cnt, dialogObj).val(data.dgrcompo.demandCompFormular);
        $("#dialogDgrcompoSeperateDemandBgtAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.demandBgtAmt));
        $("#dialogDgrcompoSeperateDemandPreAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.preAmt));
        $("#dialogDgrcompoSeperateDemandDiffAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.demandDiffAmt));
        $("#dialogDgrcompoSeperateCompGround" + cnt, dialogObj).val(data.dgrcompo.compGround + '_' + cnt);
        $("#dialogDgrcompoSeperateCompFormular" + cnt, dialogObj).val(data.dgrcompo.compFormular);
        $("#dialogDgrcompoSeperateBgtAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.bgtAmt));
        $("#dialogDgrcompoSeperatePreFormular" + cnt, dialogObj).val(data.dgrcompo.preFormular);
        $("#dialogDgrcompoSeperatePreAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.preAmt));
        $("#dialogDgrcompoSeperateDiffAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.diffAmt));
        $("#dialogDgrcompoSeperateCompoLevel" + cnt, dialogObj).val(data.dgrcompo.compoLevel);
        $("#dialogDgrcompoSeperatePreCompFormular" + cnt, dialogObj).val(data.dgrcompo.preCompFormular);
        $("#dialogDgrcompoSeperatePreBgtAmt" + cnt, dialogObj).val(addCommaStr(data.dgrcompo.preBgtAmt));
        
        dialogDgrcompoSeperateFrscGridSep.addCsJsonData(data.frscInfo);
        dialogDgrcompoSeperateCharGridSep.addCsJsonData(data.charInfo);

        var isLeaf = $("#dialogDgrcompoSeperateIsLeaf" + cnt, dialogObj).val();
        dialogDgrcompoSeperateCharGridSep.jqGrid("setCell", "090", "preDefCharAmt", "", "not-editable-cell");
        dialogDgrcompoSeperateCharGridSep.jqGrid("setCell", "090", "dmnDefCharAmt", "", "not-editable-cell");
        dialogDgrcompoSeperateCharGridSep.jqGrid("setCell", "090", "adjDefCharAmt", "", "not-editable-cell");
        dialogDgrcompoSeperateCharGridSep.jqGrid("setCell", "090", "preCharAmt", "", "not-editable-cell");
        /* if(isLeaf === "true"){
            dialogDgrcompoSeperateCharGridSep.jqGrid("setCell", "090", "preDefCharAmt", "", "not-editable-cell");
            dialogDgrcompoSeperateCharGridSep.jqGrid("setCell", "090", "dmnDefCharAmt", "", "not-editable-cell");
            dialogDgrcompoSeperateCharGridSep.jqGrid("setCell", "090", "adjDefCharAmt", "", "not-editable-cell");
            dialogDgrcompoSeperateCharGridSep.jqGrid("setCell", "090", "preCharAmt", "", "not-editable-cell");
        }else{
            var ids = dialogDgrcompoSeperateFrscGridSep.jqGrid('getDataIDs');
            for (var i=0; i < ids.length; i++){
                dialogDgrcompoSeperateFrscGridSep.jqGrid("setCell", ids[i], "preDefFrscAmt", "", "not-editable-cell");
                dialogDgrcompoSeperateFrscGridSep.jqGrid("setCell", ids[i], "dmnDefFrscAmt", "", "not-editable-cell");
                dialogDgrcompoSeperateFrscGridSep.jqGrid("setCell", ids[i], "adjDefFrscAmt", "", "not-editable-cell");
                dialogDgrcompoSeperateFrscGridSep.jqGrid("setCell", ids[i], "preFrscAmt", "", "not-editable-cell");
            }
            
            ids = dialogDgrcompoSeperateCharGridSep.jqGrid('getDataIDs');
            for (var i=0; i < ids.length; i++){
                dialogDgrcompoSeperateCharGridSep.jqGrid("setCell", ids[i], "preDefCharAmt", "", "not-editable-cell");
                dialogDgrcompoSeperateCharGridSep.jqGrid("setCell", ids[i], "dmnDefCharAmt", "", "not-editable-cell");
                dialogDgrcompoSeperateCharGridSep.jqGrid("setCell", ids[i], "adjDefCharAmt", "", "not-editable-cell");
                dialogDgrcompoSeperateCharGridSep.jqGrid("setCell", ids[i], "preCharAmt", "", "not-editable-cell");
            }
        } */
        
        sepBodyResize();
        
        sepTab.tabs("refresh");
	}
	
    var budgetSeperateDialogDgrcompoDbizCallBackFunction = function(cd, nm, num){
    	$('#dialogDgrcompoSeperateDbizCd' + num, $("#dialogDgrcompoSeperateDiv")).val(cd);
    	$('#dialogDgrcompoSeperateDbizNm' + num, $("#dialogDgrcompoSeperateDiv")).val(nm);
    	$("#dialogDgrcompoDbizDiv").dialog("close");
    }
	
});

//사업명 수정시 탭 타이틀 수정
function changeTabTitle(num){
		var title = $('#dialogDgrcompoSeperateCompGround' + num, $('#dialogDgrcompoSeperateDiv')).val();
		$('#tabTitle' + num).html(title);
}

function getDbizData(num){
	
	$("#dialogDgrcompoDbizCallBackFunction", $("#dialogDgrcompoDbizDiv")).val("budgetSeperateDialogDgrcompoDbizCallBackFunction");
    $("#dialogDgrcompoDbizFisYear", $("#dialogDgrcompoDbizDiv")).val($("#dialogDgrcompoSeperateFisYear", $("#dialogDgrcompoSeperateDiv")).val());
    $("#dialogDgrcompoDbizBgtDgr", $("#dialogDgrcompoDbizDiv")).val($("#dialogDgrcompoSeperateBgtDgr", $("#dialogDgrcompoSeperateDiv")).val());
    $("#dialogDgrcompoDbizDeptCd", $("#dialogDgrcompoDbizDiv")).val($("#dialogDgrcompoSeperateDeptCd", $("#dialogDgrcompoSeperateDiv")).val());
    $("#dialogDgrcompoDbizSepNum", $("#dialogDgrcompoDbizDiv")).val(num);
    
    $("#dialogDgrcompoDbizDiv").dialog('open');	
}

function setDbizData(cd, nm, num){
	$('#dialogDgrcompoSeperateDbizCd' + num, $("#dialogDgrcompoSeperateDiv")).val(cd);
	$('#dialogDgrcompoSeperateDbizNm' + num, $("#dialogDgrcompoSeperateDiv")).val(nm);
	$("#dialogDgrcompoDbizDiv").dialog("close");
}

</script>
<div id="dialogDgrcompoSeperateDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgrcompoSeperateCallBackFunction"/>
  <input type="hidden" id="dialogDgrcompoSeperateDgrcompoId"/>
  <input type="hidden" id="dialogDgrcompoSeperateFisYear"/>
  <input type="hidden" id="dialogDgrcompoSeperateBgtDgr"/>
  <input type="hidden" id="dialogDgrcompoSeperateTeBgtCompoId"/>
  <input type="hidden" id="dialogDgrcompoSeperateUpTeBgtCompoId"/>
  <input type="hidden" id="dialogDgrcompoSeperateUpTeBgtCompoSeq"/>
  <input type="hidden" id="dialogDgrcompoSeperateIsLeaf"/>
  <input type="hidden" id="dialogDgrcompoSeperateCompoLevel"/>
  <input type="hidden" id="dialogDgrcompoSeperateDeptCd"/>
  <input type="hidden" id="dialogDgrcompoSeperateTitle"/>
  <input type="hidden" id="dialogDgrcompoSeperateIndiBns"/>
  <input type="hidden" id="dialogDgrcompoSeperateAmtUnit" value="1"/>
  <input type="hidden" id="dialogDgrcompoSeperateTabCnt" value="0"/>
  
  <br>
  
  <div id="sepBody"  style="height:97%;border:0px;overflow:hidden;">
        <div id="sepCenter" class="ui-layout-center" style="border:0px;overflow:auto;">
          	<div id="sepTab" class="ui-layout-center">
				<UL>
				</UL>
				<div class="ui-layout-content"><!--  ui-widget-content -->
				</div>
			</div>
        </div>
        <div id="sepWest" class="ui-layout-west" style="border:0px;overflow:auto;">
        	<div id="sepWestDiv1"  style="padding-bottom:20px;">
        		<div id="dialogDgrcompoSeperateAmtUnitDiv" class="unitDiv" style="top: 0px;">
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
			            <input type="hidden" id="dialogDgrcompoSeperateTeBgtCompoSeq"/>
			            <input type="hidden" id="dialogDgrcompoSeperateDbizCd"/>
			            <input type="text" id="dialogDgrcompoSeperateDbizNm" class="readonly" style="width:100%;" readonly/>
			          </td>
			        </tr>
			        <tr>
			          <th colspan="2">통계목코드</th>
			          <td>
			            <input type="text" id="dialogDgrcompoSeperateTeMngMokCd" class="readonly" style="width:100%;" readonly/>
			          </td>
			          <th>통계목명</th>
			          <td colspan="3">
			          	<input type="text" id="dialogDgrcompoSeperateTeMngMokNm" class="readonly" style="width:100%;" readonly/>
			          </td>
			        </tr>
			        <tr>
			          <th colspan="2">사업명</th>
			          <td colspan="5">
			            <input type="text" id="dialogDgrcompoSeperateCompGround" style="width:100%;" readonly/>
			          </td>
			        </tr>
			        <tr>
			          <th>요구</th>
			          <th>기정액</th>
			          <td>
			            <input type="text" id="dialogDgrcompoSeperateDemandPreAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
			          </td>
			          <th>증감액</th>
			          <td>
			            <input type="text" id="dialogDgrcompoSeperateDemandDiffAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
			          </td>
			          <th>요구액</th>
			          <td>
			            <input type="text" id="dialogDgrcompoSeperateDemandBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
			          </td>
			        </tr>
			        <tr>
			          <th>조정</th>
			          <th>기정액</th>
			          <td>
			            <input type="text" id="dialogDgrcompoSeperatePreAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
			          </td>
			          <th>증감액</th>
			          <td>
			            <input type="text" id="dialogDgrcompoSeperateDiffAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
			          </td>
			          <th>조정액</th>
			          <td>
			            <input type="text" id="dialogDgrcompoSeperateBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
			          </td>
			        </tr>
			        <tr>
			          <th>전년도</th>
			          <th>예산액</th>
			          <td>
			            <input type="text" id="dialogDgrcompoSeperatePreBgtAmt" class="readonly" style="width:100%;text-align:right;" readonly/>
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
				<div id="DIALOG_DGR_COMPO_SEPERATE_FRSC_DIV" class="csGrid">
					<table id="DIALOG_DGR_COMPO_SEPERATE_FRSC_GRD"  style="border:0px;height:100%;"></table>
				</div>
        	</div>
        	<div id="sepWestDiv3">
        		<div class="ui-widget-header">
			      성격별 예산액
			    </div>
			    <div id="DIALOG_DGR_COMPO_SEPERATE_CHAR_DIV" class="csGrid">
			    	<table id="DIALOG_DGR_COMPO_SEPERATE_CHAR_GRD" ></table>
			    </div>
        	</div>
		  
		  <br />
		  
       </div>
  </div>
</div>

