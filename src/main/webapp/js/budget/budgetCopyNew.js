$(document).ready(function() {
    var tabId = _budgetCopyTabId;
    var tabObj = $("#"+tabId);
    
    var gridScrollPosition = 0;
    var srcGridScrollPosition = 0;
    var gridHeight = 0;
    
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
        
        var rVal = '<span id="dgrcompoNmView_'+rowObject.dgrcompoId+'" >' + cellValue + '</span><br>'
                 + '<textarea id="demandCont_'+rowObject.dgrcompoId+'" class="invisible" style="width:280px;ime-mode:active;min-height:20px;" disabled>'+demandCont+'</textarea>'
                 + '';
        
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
                 + '<select id="reflectFg_'+rowObject.dgrcompoId+'" title="반영구분" class="invisible" style="width:280px;" disabled>'
                 + reflectFgCreateCombo('RP003', rowObject.reflectFg)
                 + '</select>'+'<br>'
                 + '<textarea id="examCont_'+rowObject.dgrcompoId+'" class="invisible" style="width:280px;ime-mode:active;min-height:20px;" disabled>'+cellValue+'</textarea>'
                 + '</div>';

        return rVal;
    };
    
  //대분류 그리드fomatter
    var reportMstrFormatter = function(cellValue, options, rowObject){
    	
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.teBgtCompoId == "00000000000" ){
            return cellValue;
        }
        var itemList = comboData['reportMstr'];
        
        if(cellValue == "" && isEmpty(rowObject.reportMstr) != true){
        	cellValue = rowObject.reportMstr;
        }
        
        var rVal = '<div>';
        for(var i=0 ; i<itemList.length ; i++){
        	var data = itemList[i];
        	if(cellValue.includes(data.code)){
    			rVal += '&nbsp;&nbsp;<span style="line-height:22px; vertical-align:top;">' + data.codeNm + '</span><br />';
    		}
        }
        
        rVal += '</div>';

        return rVal;
    };
    
  //중분류 그리드fomatter
    var reportCdFormatter = function(cellValue, options, rowObject){
    	
    	if(isEmpty(cellValue) == true){
    		cellValue = "";
    	}
    	
    	if(rowObject.teBgtCompoId == "00000000000" ){
    		return cellValue;
    	}
    	var itemList = comboData['reportCd'];

    	if(cellValue == "" && isEmpty(rowObject.reportCd) != true){
        	cellValue = rowObject.reportCd;
        }
    	
    	var rVal = '<div>';
    	for(var i=0 ; i<itemList.length ; i++){
    		var data = itemList[i];
    		if(cellValue.includes(data.code)){
    			rVal += '&nbsp;&nbsp;<span style="line-height:22px; vertical-align:top;">' + data.codeNm + '</span><br />';
    		}
    	}
    	
    	rVal += '</div>';
    	
    	return rVal;
    };
    
  //소분류 그리드fomatter
    var reportDetlCdFormatter = function(cellValue, options, rowObject){
    	
    	if(isEmpty(cellValue) == true){
    		cellValue = "";
    	}
    	
    	if(rowObject.teBgtCompoId == "00000000000" ){
    		return cellValue;
    	}
    	var itemList = comboData['reportDetlCd'];

    	if(cellValue == "" && isEmpty(rowObject.reportDetlCd) != true){
        	cellValue = rowObject.reportDetlCd;
        }
    	
    	var rVal = '<div>';
    	for(var i=0 ; i<itemList.length ; i++){
    		var data = itemList[i];
    		if(cellValue.includes(data.code)){
    			rVal += '&nbsp;&nbsp;<span style="line-height:22px; vertical-align:top;">' + data.codeNm + '</span><br />';
    		}
    	}
    	
    	rVal += '</div>';
    	
    	return rVal;
    };
    
    var colNames = ['', '구분(회계-실-부서-세부-통계목)','통계목', '증감액', '예산액', '증감액', '예산액', '예산액', '검토내용', '재원정보',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'reportCd', 'reportDetlCd', 'dgrLevel', 'teBgtCompoId', 'teBgtCompoSeq', 'demandCont', 'examCont', 'reflectFg', 'orderYmdSeq'
                   ];
    
    var colModel = [ {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, frozen: true,
                            formatter: function (cellValue, option, rowObject) {
                                if(rowObject.teBgtCompoId == "00000000000"){
                                    return '<input type="radio" name="radio_' + option.gid + '" style="display:none;" />';
                                }
                                
                                return '<input type="radio" name="radio_' + option.gid + '"  />';
                            }
                        },
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 400, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:dgrcompoNmFormatter
                        },
                        {name : 'teMngMokNm', index : 'teMngMokNm', width : 100, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'demandDiffAmt', index : 'demandDiffAmt', width : 80, sortable : false, fixed : true, align : 'right', cellattr: myCellattr, formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'demandBgtAmt', index : 'demandBgtAmt', width : 80, sortable : false, fixed : true, align : 'right', cellattr: myCellattr, formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'diffAmt', index : 'diffAmt', width : 80, sortable : false, fixed : true, align : 'right', cellattr: myCellattr, formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'bgtAmt', index : 'bgtAmt', width : 80, sortable : false, fixed : true, align : 'right', cellattr: myCellattr, formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'preBgtAmt', index : 'preBgtAmt', width : 80, sortable : false, fixed : true, align : 'right', cellattr: myCellattr, formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        
                        {name : 'examContView', index : 'examContView', width : 290, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:examContFormatter
                        },
                        {name : 'frsces', index : 'frsces', width : 250, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key: true},
                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'reportCd', index : 'reportCd', width : 0, sortable : false, hidden : true},
                        {name : 'reportDetlCd', index : 'reportDetlCd', width : 0, sortable : false, hidden : true},
                        {name : 'dgrLevel', index : 'dgrLevel', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'demandCont', index : 'demandCont', width : 0, sortable : false, hidden : true},
                        {name : 'examCont', index : 'examCont', width : 0, sortable : false, hidden : true},
                        {name : 'reflectFg', index : 'reflectFg', width : 0, sortable : false, hidden : true},
                        {name : 'orderYmdSeq', index : 'orderYmdSeq', width : 0, sortable : false, hidden : true}
                    ];
    
    var getGridHeight = function (){
    	var height = $("#mainCenter", tabObj).height() - 125 > 200 ? $("#mainCenter", tabObj).height() - 125 : 200;
    	if(isEmpty($("#BUDGET_COPY_SRC_GRD", tabObj)) == false){
	    	$("#BUDGET_COPY_SRC_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", height - 220);
    	}
    	if(isEmpty($("#BUDGET_COPY_GRD", tabObj)) == false){
    		$("#BUDGET_COPY_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", height - 220);
    	}
        return height;
    };
    
    var mainBodyResize = function(){
        $("#subMainBody", tabObj).width($("#mainCenter", tabObj).width());
        $("#subMainBody", tabObj).layout().resizeAll();
    };
    
    var subMainBodyResize = function(){
        $("#subMainCenterCond", tabObj).width($("#subMainCenter", tabObj).width()-20);
        $("#subMainWestCond", tabObj).width($("#subMainWest", tabObj).width()-20);
        if(isEmpty($("#BUDGET_COPY_SRC_GRD", tabObj)) == false){
            $("#BUDGET_COPY_SRC_GRD", tabObj).setGridHeight(getGridHeight());
            $("#BUDGET_COPY_SRC_GRD", tabObj).setGridWidth($("#subMainWest", tabObj).width());
        }
        
        if(isEmpty($("#BUDGET_COPY_GRD", tabObj)) == false){
            $("#BUDGET_COPY_GRD", tabObj).setGridHeight(getGridHeight());
            $("#BUDGET_COPY_GRD", tabObj).setGridWidth($("#subMainCenter", tabObj).width());
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

    var budgetCopySrcGrid = $("#BUDGET_COPY_SRC_GRD", tabObj);
    var budgetCopyGrid = $("#BUDGET_COPY_GRD", tabObj);
    
    var doSearchSrcCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        $("#BUDGET_COPY_SRC_GRD", tabObj).GridUnload();
        budgetCopySrcGrid = $("#BUDGET_COPY_SRC_GRD", tabObj);
        budgetCopySrcGrid.csTreeGrid({
            datastr : data,
            height : getGridHeight(),
            colNames : colNames,
            colModel : colModel,
            ExpandColumn : "dgrcompoNm",
            jsonReader : {
                repeatitems : false,
                root : "dataList"
            },
            onSelectRow: function(rowId){
            },
            loadComplete: function() {
                $('textarea', tabObj).autogrow();
                $('textarea', tabObj).keyup();
            }
        });
        
        //$("#BUDGET_COPY_SRC_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", 300);
        $("#BUDGET_COPY_SRC_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", getGridHeight() - 220);
        
        budgetCopySrcGrid.jqGrid('setGroupHeaders', {
            useColSpanStyle : true,
            groupHeaders : [
               {startColumnName : 'reportMstr',numberOfColumns : 3, titleText : '분류'},
               {startColumnName : 'demandDiffAmt', numberOfColumns : 2, titleText : '요구'}, 
               {startColumnName : 'diffAmt', numberOfColumns : 2, titleText : '조정'},
               {startColumnName : 'preBgtAmt', numberOfColumns : 1, titleText : '전년도'}
            ]
        });
        
        $("#BUDGET_COPY_SRC_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(srcGridScrollPosition);
        
        data = null;
    };
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        $("#BUDGET_COPY_GRD", tabObj).jqGrid('GridUnload');
        budgetCopyGrid = $("#BUDGET_COPY_GRD", tabObj);
        budgetCopyGrid.csTreeGrid({
            datastr : data,
            height : getGridHeight(),
            colNames : colNames,
            colModel : colModel,
            ExpandColumn : "dgrcompoNm",
            jsonReader : {
                repeatitems : false,
                root : "dataList"
            },
            onSelectRow: function(rowId){
            },
            loadComplete: function() {
                $('textarea', tabObj).autogrow();
                $('textarea', tabObj).keyup();
            }
        });
        
        $("#BUDGET_COPY_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", getGridHeight() - 220);
        
        budgetCopyGrid.jqGrid('setGroupHeaders', {
            useColSpanStyle : true,
            groupHeaders : [
               {startColumnName : 'reportMstr',numberOfColumns : 3, titleText : '분류'},
               {startColumnName : 'demandDiffAmt', numberOfColumns : 2, titleText : '요구'}, 
               {startColumnName : 'diffAmt', numberOfColumns : 2, titleText : '조정'},
               {startColumnName : 'preBgtAmt', numberOfColumns : 1, titleText : '전년도'} 
            ]
        });
        
        $("#BUDGET_COPY_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
        data = null;
    };
    
    var doSearchSrc = function(){
        var reportMstr = $("#condSrcReportMstr option:selected", tabObj).val();
        var reportCd = $("#condSrcReportCd option:selected", tabObj).val();
        var reportDetlCd = $("#condSrcReportDetlCd option:selected", tabObj).val();
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
        var orderYmdSeq = '';
        
        if(isEmpty(reportMstr) == true){
            $.csAlert({
                msg : "대분류를 선택해주세요.",
                callBack : function() {
                    $("#condSrcReportMstr", tabObj).focus();
                }
            });
            
            return;
        }
        
        if(isEmpty(reportCd) == true){
        	$.csAlert({
        		msg : "중분류를 선택해주세요.",
        		callBack : function() {
        			$("#condSrcReportCd", tabObj).focus();
        		}
        	});
        	
        	return;
        }
        
        if(isEmpty(reportDetlCd) == true){
        	reportDetlCd = '';
        }
        
        srcGridScrollPosition = $("#BUDGET_COPY_SRC_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();

        var tmp = {reportCd : reportCd,
                reportDetlCd : reportDetlCd,
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
                amtUnit : amtUnit,
                orderYmdSeq : orderYmdSeq,
                userDeptYn : "N"
         };
        
        $.csAjaxCall({
            url : "/budget/ajaxBudgetCopyNewList.do",
            data: {reportCd : reportCd,
                   reportDetlCd : reportDetlCd,
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
                   amtUnit : amtUnit,
                   orderYmdSeq : orderYmdSeq,
                   userDeptYn : "N"
            },
            async : true,
            callBack : doSearchSrcCallBack
        });
        
    };
    
    var doSearch = function(){
    	var reportMstr = $("#condReportMstr option:selected", tabObj).val();
        var reportCd = $("#condReportCd option:selected", tabObj).val();
        var reportDetlCd = $("#condReportDetlCd option:selected", tabObj).val();
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
        var orderYmdSeq = '';

        
        if(isEmpty(reportMstr) == true){
            $.csAlert({
                msg : "대분류를 선택해주세요.",
                callBack : function() {
                    $("#condReportMstr", tabObj).focus();
                }
            });
            
            return;
        }
        
        if(isEmpty(reportCd) == true){
        	$.csAlert({
        		msg : "중분류를 선택해주세요.",
        		callBack : function() {
        			$("#condReportCd", tabObj).focus();
        		}
        	});
        	
        	return;
        }
        
        if(isEmpty(reportDetlCd) == true){
        	reportDetlCd = '';
        }
        
        gridScrollPosition = $("#BUDGET_COPY_GRD", tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        $.csAjaxCall({
            url : "/budget/ajaxBudgetCopyNewList.do",
            data: {reportCd : reportCd,
                   reportDetlCd : reportDetlCd,
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
                   amtUnit : amtUnit,
                   orderYmdSeq : orderYmdSeq
            },
            async : true,
            callBack : doSearchCallBack
        });
    };

    var doCondSrcInit = function(){
    	//대분류 selectBox 세팅
        $("#condSrcReportMstr", tabObj).csCreatCombo(comboData, {
            id : 'reportMstr',
            groupId : 'ALL',
            selectedValue : '',
            comboType : 'S',
            comboTypeValue : ''
        });
        
        //중분류 selectBox 세팅
        var srcReportMstr = $("#condSrcReportMstr option:selected", tabObj).val();
        condSrcReportCdCreateCombo(srcReportMstr, '');
        
        //소분류 selectBox 세팅
        var srcReportCd = $("#condSrcReportCd option:selected", tabObj).val();
        condSrcReportDetlCdCreateCombo(srcReportCd, '');
        
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
        
        condSrcOrderYmdSeqCreateCombo(fisYear + '_' + bgtDgr, '');
        
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
        $("#condSrcOrderYmdSeq", tabObj).val("");
    };
    
    $("#condSrcInitBtn", tabObj).click(function() {
        doCondSrcInit();
    });

    var doCondInit = function(){
    	//대분류 selectBox 세팅
        $("#condReportMstr", tabObj).csCreatCombo(comboData, {
            id : 'reportMstr',
            groupId : 'ALL',
            selectedValue : '',
            comboType : 'S',
            comboTypeValue : ''
        });
        
        //중분류 selectBox 세팅
        var reportMstr = $("#condReportMstr option:selected", tabObj).val();
        condReportCdCreateCombo(reportMstr, '');
        
        //소분류 selectBox 세팅
        var reportCd = $("#condReportCd option:selected", tabObj).val();
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
        
        condTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
        
        condFrscFgCdFrCreateCombo(fisYear, '');
        condFrscFgCdToCreateCombo(fisYear, '');
        
        condOrderYmdSeqCreateCombo(fisYear + '_' + bgtDgr, '');
        
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
        $("#condOrderYmdSeq", tabObj).val("");
    };
    
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });
    
    $("#srcSearchBtn", tabObj).click(function() {
        srcGridScrollPosition = 0;
        
        var reportCd = $("#condReportCd option:selected", tabObj).val();
        var orderYmdSeq = $("#condSrcOrderYmdSeq option:selected", tabObj).val();
        if(reportCd == "070" && isEmpty(orderYmdSeq) == true){
            $.csAlert({
                msg : "지시사항 조치계획 서식은 지시일자를 선택하셔야 합니다.",
                callBack : function() {
                    $("#condSrcOrderYmdSeq", tabObj).focus();
                }
            });
            
            return;
        }
        
        doSearchSrc();
    });
    
    $("#searchBtn", tabObj).click(function() {
        gridScrollPosition = 0;
        
        var reportCd = $("#condReportCd option:selected", tabObj).val();
        var orderYmdSeq = $("#condOrderYmdSeq option:selected", tabObj).val();
        if(reportCd == "070" && isEmpty(orderYmdSeq) == true){
            $.csAlert({
                msg : "지시사항 조치계획 서식은 지시일자를 선택하셔야 합니다.",
                callBack : function() {
                    $("#condOrderYmdSeq", tabObj).focus();
                }
            });
            
            return;
        }
        
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
            url : "/budget/ajaxBudgetCopyCopyReport.do",
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
                //doSearch();
            }
        });
    };
    
    $("#applyBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        var srcRowId = getSelectedRowId('BUDGET_COPY_SRC');
        if(isEmpty(srcRowId) == true){
            $.csAlert({
                msg : "적용할 자료를 선택하여 주십시오."
            });
            
            return;
        }
        
        var rowId = getSelectedRowId('BUDGET_COPY');
        if(isEmpty(rowId) == true){
            $.csAlert({
                msg : "적용할 대상을 선택하여 주십시오."
            });
            
            return;
        }

        var srcRowData = budgetCopySrcGrid.getRowData(srcRowId);
        if(srcRowData.teBgtCompoId == "00000000000"){
            $.csAlert({
                msg : "세세목 이하 예산편성을 선택하여 주십시오."
            });
            
            return;
        }

        var rowData = budgetCopyGrid.getRowData(rowId);
        if(rowData.teBgtCompoId == "00000000000"){
            $.csAlert({
                msg : "세세목 이하 예산편성을 선택하여 주십시오."
            });
            
            return;
        }
        
        if(srcRowData.reportCd != rowData.reportCd){
            $.csAlert({
                msg : "동일한 조서만 적용 할 수 있습니다."
            });
            
            return;
        }
        
        applyParam = {};
        applyParam["srcReportCd"] = srcRowData.reportCd;
        applyParam["srcReportDetlCd"] = srcRowData.reportDetlCd;
        applyParam["srcFisYear"] = srcRowData.fisYear;
        applyParam["srcBgtDgr"] = srcRowData.bgtDgr;
        applyParam["srcTeBgtCompoId"] = srcRowData.teBgtCompoId;
        applyParam["srcOrderYmdSeq"] = srcRowData.orderYmdSeq;
        applyParam["reportCd"] = rowData.reportCd;
        applyParam["reportDetlCd"] = rowData.reportDetlCd;
        applyParam["fisYear"] = rowData.fisYear;
        applyParam["bgtDgr"] = rowData.bgtDgr;
        applyParam["teBgtCompoId"] = rowData.teBgtCompoId;
        applyParam["orderYmdSeq"] = rowData.orderYmdSeq;

        if(checkCloseYn(applyParam) == false){
            return;
        }
        
        $.csConfirm({
            msg : "적용하시겠습니까?",
            callBack : doApply
        });
    });
    
  //대분류 데이터 변경시
    var doChangeCondReportMstr = function(){
        var reportMstr = $("#condReportMstr option:selected", tabObj).val();
        condReportCdCreateCombo(reportMstr, '');
        doChangeCondReportCd();
    };
    
    //중분류 데이터 변경시
    var doChangeCondReportCd = function(){
    	var reportCd = $("#condReportCd option:selected", tabObj).val();
    	condReportDetlCdCreateCombo(reportCd, '');
    };
    
    //대분류 데이터 변경시
    var doChangeCondSrcReportMstr = function(){
    	var srcReportMstr = $("#condSrcReportMstr option:selected", tabObj).val();
    	condSrcReportCdCreateCombo(srcReportMstr, '');
    	doChangeCondSrcReportCd();
    };
    
    //중분류 데이터 변경시
    var doChangeCondSrcReportCd = function(){
    	var srcReportCd = $("#condSrcReportCd option:selected", tabObj).val();
    	condSrcReportDetlCdCreateCombo(srcReportCd, '');
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

        condSrcOrderYmdSeqCreateCombo(fisYear + '_' + bgtDgr, '');
    };
    
    var doChageCondBgtDgr = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
        doChangeCondOfficeCd();
        
        condTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
        
        condOrderYmdSeqCreateCombo(fisYear + '_' + bgtDgr, '');
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
    
    $("#condReportMstr", tabObj).change(function(){
        doChangeCondReportMstr();
    });
    
    $("#condReportCd", tabObj).change(function(){
    	doChangeCondReportCd();
    });
    
    $("#condSrcReportMstr", tabObj).change(function(){
    	doChangeCondSrcReportMstr();
    });
    
    $("#condSrcReportCd", tabObj).change(function(){
    	doChangeCondSrcReportCd();
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
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("budgetCopyDialogDgrDeptSrcSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("N");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    budgetCopyDialogDgrDeptSrcSeltCallBack = function(param){
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
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("budgetCopyDialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    budgetCopyDialogDgrDeptSeltCallBack = function(param){
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
					{id : "reportCd", codeId : "RP011"},
					{id : "reportDetlCd", codeId : "RP012"},
					{id : "reportMstr", codeId : "RP010"},
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "bgtDgr", subQueryId : "BgtDgr"},
                      {id : "fisFgMstCd", subQueryId : "FisFgMstCd"},
                      {id : "fisFgCd", subQueryId : "FisFgCd"},
                      {id : "officeCdAll", subQueryId : "OfficeCd", userDeptYn : "N"},
                      {id : "officeCd", subQueryId : "OfficeCd"},
                      {id : "teMngMokCd", subQueryId : "TeMngMokCd"},
                      {id : "frscFgCd", subQueryId : "FrscFgCd"},
                      {id : "reflectFg", codeId : "RP003"},
                      {id : "orderYmdSeq", subQueryId : "OrderYmdSeq"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);
        
  //대분류 데이터에 따라 중분류 새로 세팅
    var condReportCdCreateCombo = function(groupId, selectedValue){
        $("#condReportCd", tabObj).csCreatCombo(comboData
                , {id: 'reportCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'S'
                  , comboTypeValue: ''
                  }
        );
    };
    
  //중분류 데이터에 따라 소분류 새로 세팅
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
    
    //대분류 데이터에 따라 중분류 새로 세팅
    var condSrcReportCdCreateCombo = function(groupId, selectedValue){
    	$("#condSrcReportCd", tabObj).csCreatCombo(comboData
    			, {id: 'reportCd'
    				, groupId: groupId
    				, selectedValue: selectedValue
    				, comboType: 'S'
    					, comboTypeValue: ''
    	}
    	);
    };
    
    var condSrcReportDetlCdCreateCombo = function(groupId, selectedValue){
        $("#condSrcReportDetlCd", tabObj).csCreatCombo(comboData
                , {id: 'reportDetlCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
        
    /*var condReportDetlCdCreateCombo = function(groupId, selectedValue){
        $("#condReportDetlCd", tabObj).csCreatCombo(comboData
                , {id: 'reportDetlCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: ''
                  , comboTypeValue: ''
                  }
        );
    };*/
    
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
                , {id: 'officeCdAll'
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
    
    var condSrcOrderYmdSeqCreateCombo = function(groupId, selectedValue){
        $("#condSrcOrderYmdSeq", tabObj).csCreatCombo(comboData
                , {id: 'orderYmdSeq'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'S'
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condOrderYmdSeqCreateCombo = function(groupId, selectedValue){
        $("#condOrderYmdSeq", tabObj).csCreatCombo(comboData
                , {id: 'orderYmdSeq'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'S'
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
    
    doCondSrcInit();
    doCondInit();
    
    $("#applyBtn", tabObj).btnChangeState(true);
    
});
