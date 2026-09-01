$(document).ready(function() {
    var tabId = _reportWrite010PageTabId;
    var tabObj = $("#"+tabId);
    
    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        if(rowObject.teBgtCompoId != "00000000000"){
            return ' style="vertical-align: top;"';
        }
    };
    
    reportWirte010DialogDgrcompoModifyCallBackFunction = function(param){
        var rowId = param.dgrcompoId;
        if(isEmpty(rowId) == true){
            return;
        }
        
        reportWrite010PageGrid.jqGrid('setRowData', rowId, param);
        
        var dgrcompoNmView = param.dgrcompoNmView;
        if(isEmpty(dgrcompoNmView) == true){
            dgrcompoNmView = "";
        }
        
        $("#dgrcompoNmView_" + rowId, tabObj).html(dgrcompoNmView);
    };
    
    reportWirte010OpenDialogDgrcompoModify = function(rowId){  
        var rowData = reportWrite010PageGrid.getRowData(rowId);
        
        $("#dialogDgrcompoModifyCallBackFunction", $("#dialogDgrcompoModifyDiv")).val("reportWirte010DialogDgrcompoModifyCallBackFunction");
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
        
        var rVal = '<a href="javascript:reportWirte010OpenDialogDgrcompoModify(\''+options.rowId+'\');"><span class="ui-icon ui-icon-pencil"></span></a>';
        
        return rVal;
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
    
    var bizDescAttrEsc = function(v) {
        return String(v == null ? "" : v)
            .replace(/&/g, "&amp;")
            .replace(/"/g, "&quot;")
            .replace(/</g, "&lt;");
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

        var rVal = '<a href="#" class="bizdesc-nm-link" style="color:#06c;text-decoration:underline;"'
                 + ' data-te-id="'+rowObject.teBgtCompoId+'"'
                 + ' data-tebgtcompoid="'+rowObject.teBgtCompoId+'"'
                 + ' data-dgrcompoid="'+rowObject.dgrcompoId+'"'
                 + ' data-fisyear="'+(rowObject.fisYear||'')+'"'
                 + ' data-bgtdgr="'+(rowObject.bgtDgr||'')+'"'
                 + ' data-reportcd="'+(rowObject.reportCd||'010')+'"'
                 + ' data-biznm="'+bizDescAttrEsc(cellValue)+'"'
                 + ' data-dbiznm="'+bizDescAttrEsc(rowObject.dbizNm||'')+'">' + cellValue + '</a><br>'
                 + '<textarea id="demandCont_'+rowObject.dgrcompoId+'" style="width:210px;ime-mode:active;resize:none;" rows="12" cols="22" >'+demandCont+'</textarea>';

        return rVal;
    };
    
    var examContFormatter = function(cellValue, options, rowObject){
        if(rowObject.teBgtCompoId == "00000000000"){
            return cellValue;
        }
        
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        var rVal = '';
        if(rowObject.compoLevel != 1){
            rVal = '<div>'
                + '<textarea id="examCont_'+rowObject.dgrcompoId+'" style="width:270px;ime-mode:active;resize:none;"  rows="12" cols="22" >'+cellValue+'</textarea>'
                + '</div>';
        }else{
            
            var investPlan = "";
            var styleStr = 'style="width:150px;"';
            if(isEmpty(rowObject.investPlan) == false){
                investPlan = rowObject.investPlan;
            }
            
            rVal = '<div>'
                + '<select id="reflectFg_'+rowObject.dgrcompoId+'" title="반영구분" style="width:90px;">'
                + reflectFgCreateCombo('RP003', rowObject.reflectFg)
                + '</select>'
                + '&nbsp;<input id="investPlan_'+rowObject.dgrcompoId+'" value="'+investPlan+'" maxlength="20" class="ui-state-enabled" '+styleStr +' maxlength="20" />'+'<br>'
                + '<textarea id="examCont_'+rowObject.dgrcompoId+'" style="width:245px;ime-mode:active;resize:none;" rows="12" cols="22" >'+cellValue+'</textarea>'
                + '</div>';
        }
        

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
            styleStr = 'style="width:240px;ime-mode:active;"';
        }
        
        var rVal = '<div>'
                 + '<textarea id="srchVal_'+rowObject.dgrcompoId+'" '+styleStr+'" rows="12" cols="22">'+cellValue+'</textarea>'
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
    
    
    var govSubFormatter = function(cellValue, options, rowObject){
    	
    	if(isEmpty(cellValue) == true){
    		cellValue = "";
    	}
    	
    	if(rowObject.teBgtCompoId == "00000000000" ){
    		return cellValue;
    	}
    	var itemList = comboData['govSub'];
    	
    	var rVal = '<div>';
    	for(var i=0 ; i<itemList.length ; i++){
    		var data = itemList[i];
    		if(cellValue.includes(data.code)){
    			rVal += '&nbsp;&nbsp;<span style="line-height:22px; vertical-align:top;">' + data.codeNm + '</span><br />';
    		}
    		//rVal += '<p><input type="checkbox" id="checkYnGovSub_'+data.code+'" value="Y" class="chkBudgetSelect" onclick="javascript:budgetSelectCheckYn(\'031\', \''+rowObject.dgrcompoId+'\');" style="margin-top: 5px;" '+(rowObject.checkYn031 == 'Y' ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">' + data.codeNm + '</span></p>';
    	}
    	
    	rVal += '</div>';
    	
    	return rVal;
    };
    
    //분류항목 그리드fomatter
    var advncProcFgFormatter = function(cellValue, options, rowObject){
    	if(isEmpty(cellValue) == true){
    		cellValue = "";
    	}
    	
    	if(rowObject.teBgtCompoId == "00000000000" ){
    		return cellValue;
    	}
    	
    	if(cellValue == "" && isEmpty(rowObject.advncProc) != true){
        	cellValue = rowObject.advncProc;
        }
    	
    	var itemList = comboData['advncProc'];
    	var rVal = '<div>';
        for(var i=0 ; i<itemList.length ; i++){
        	var data = itemList[i];
        	var checked = '';
        	
        	if(cellValue.includes(data.code)){
        		//checked = 'checked';
        		rVal += '&nbsp;&nbsp;<span style="line-height:22px; vertical-align:top;"><label for="checkYnAdvncProc_' + rowObject.dgrcompoId + '_' +data.code + '">' + data.codeNm + '</label></span><br />';
        	}
        	//rVal += '&nbsp;&nbsp;<span style="line-height:22px; vertical-align:top;"><input type="checkbox" id="checkYnAdvncProc_' + rowObject.dgrcompoId + '_' +data.code + '" value="' + data.code + '" class="chkBudgetSelect" style="margin-top: 5px;" ' + checked + ' /><label for="checkYnAdvncProc_' + rowObject.dgrcompoId + '_' +data.code + '">' + data.codeNm + '</label></span><br />';
        }
        rVal += '</div>';
    	
    	return rVal;
    };
    
    var colNames = ['', '[실국-부서-세부]', '구분', '통계목', '본예산', '증감액', '전년도예산액', '요구액', '조정액', '검토내용', '재원정보', '공약정보', '조건검색어',
                    '대분류', '중분류', '소분류', '국고보조', '분류항목',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'reportCd', 'reportDetlCd', 'dgrLevel', 'teBgtCompoId', 'teBgtCompoSeq', 'compoLevel', 'demandCont', 'examCont', 'reflectFg', 'srchVal', 'investPlan',
                    'indiAttr','advncProc'
                   ];

    var colModel = [ {name : 'edit', index : 'edit', width : 20, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
                            formatter:editFormatter
                        },
                        {name : 'upDgrcompoNm', index : 'upDgrcompoNm', width : 150, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:upDgrcompoNmFormatter
                        },
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 250, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:dgrcompoNmFormatter
                        },
                        {name : 'teMngMokNm', index : 'teMngMokNm', width : 100, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'preAmt', index : 'preAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'diffAmt', index : 'diffAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'preBgtAmt', index : 'preBgtAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'demandBgtAmt', index : 'demandBgtAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'bgtAmt', index : 'bgtAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
                        {name : 'examContView', index : 'examContView', width : 290, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:examContFormatter
                        },
                        {name : 'frsces', index : 'frsces', width : 130, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'pledgeFgs', index : 'pledgeFgs', width : 70, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
                        {name : 'srchValView', index : 'srchValView', width : 250, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter:srchValFormatter
                        },
                        {name : 'reportMstr', index : 'reportMstr', width : 100, sortable : false, hidden : false, fixed : true, align : 'left', cellattr: myCellattr,
                        	formatter:reportMstrFormatter
                        },
                        {name : 'reportCd', index : 'reportCd', width : 100, sortable : false, hidden : false, fixed : true, align : 'left', cellattr: myCellattr,
                        	formatter:reportCdFormatter
                        },
                        {name : 'reportDetlCd', index : 'reportDetlCd', width : 100, sortable : false, hidden : false, fixed : true, align : 'left', cellattr: myCellattr,
                        	formatter:reportDetlCdFormatter
                        },
                        {name : 'govSub', index : 'govSub', width : 100, sortable : false, hidden : false, fixed : true, align : 'left', cellattr: myCellattr,
                        	formatter:govSubFormatter
                        },
                        {name : 'advncProcChk', index : 'advncProc', width : 100, sortable : false, hidden : false, fixed : true, align : 'left', cellattr: myCellattr,
                        	formatter:advncProcFgFormatter
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
                        {name : 'investPlan', index : 'investPlan', width : 0, sortable : false, hidden : true},
                        {name : 'indiAttr', index : 'indiAttr', width : 0, sortable : false, hidden : true},
                        {name : 'advncProc', index : 'advncProc', width : 0, sortable : false, hidden : true}
                    ];
/*    var colNames = ['', '[실국-부서-세부]', '구분', '통계목', '본예산', '증감액', '전년도예산액', '산출근거식', '요구액', '산출근거식', '조정액', '검토내용', '재원정보', '공약정보', '조건검색어',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'reportCd', 'reportDetlCd', 'dgrLevel', 'teBgtCompoId', 'teBgtCompoSeq', 'compoLevel', 'demandCont', 'examCont', 'reflectFg', 'srchVal', 'investPlan'
                    ];
    
    var colModel = [ {name : 'edit', index : 'edit', width : 20, sortable : false, fixed : true, align : 'center', cellattr: myCellattr,
    	formatter:editFormatter
    },
    {name : 'upDgrcompoNm', index : 'upDgrcompoNm', width : 150, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
    	formatter:upDgrcompoNmFormatter
    },
    {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 250, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
    	formatter:dgrcompoNmFormatter
    },
    {name : 'teMngMokNm', index : 'teMngMokNm', width : 100, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
    {name : 'preAmt', index : 'preAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
    {name : 'diffAmt', index : 'diffAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
    {name : 'preBgtAmt', index : 'preBgtAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
    {name : 'demandCompFormular', index : 'demandCompFormular', width : 80, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
    {name : 'demandBgtAmt', index : 'demandBgtAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
    {name : 'compFormular', index : 'compFormular', width : 80, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
    {name : 'bgtAmt', index : 'bgtAmt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, cellattr: myCellattr},
    {name : 'examContView', index : 'examContView', width : 290, sortable : false, fixed : true, align : 'left', cellattr: myCellattr,
    	formatter:examContFormatter
    },
    {name : 'frsces', index : 'frsces', width : 130, sortable : false, fixed : true, align : 'left', cellattr: myCellattr},
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
    {name : 'investPlan', index : 'investPlan', width : 0, sortable : false, hidden : true}
    ];
*/    
    var getGridHeight = function (){
    	var height = $("#mainCenter", tabObj).height() - 125 > 200 ? $("#mainCenter", tabObj).height() - 125 : 200; 
    	$("#REPORT_WRITE010PAGE_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", height - 10);
        return height;
    };
    
    var mainBodyResize = function(){
    	
        if(isEmpty($("#REPORT_WRITE010PAGE_GRD", tabObj)) == false){
            $("#REPORT_WRITE010PAGE_GRD", tabObj).setGridHeight(getGridHeight());
            $("#REPORT_WRITE010PAGE_GRD", tabObj).setGridWidth($("#mainCenter", tabObj).width());
            
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    $("#mainBody", tabObj).layout({
        north__size : 245,
        center__onresize: mainBodyResize
    });
    
    var reportWrite010PageGrid = $("#REPORT_WRITE010PAGE_GRD", tabObj);
    
    var doSearchCallBack = function(data){
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }

        if(typeof clearDirtyRows === "function"){ clearDirtyRows(); }

        if(data.data.bgtDgr == "1"){
            colModel[4].hidden = true;
            colModel[5].hidden = false;
        }else{
            colModel[4].hidden = false; 
            colModel[5].hidden = true;
        }
        
        $("#REPORT_WRITE010PAGE_GRD", tabObj).jqGrid('GridUnload');
        reportWrite010PageGrid = $("#REPORT_WRITE010PAGE_GRD", tabObj);
        reportWrite010PageGrid.csTreeGrid({
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
                //$('textarea', tabObj).autogrow();
                $('textarea', tabObj).keyup();
                $('textarea').maxlength({max: 1000, showFeedback: false});
            }
        });
        
        $("#REPORT_WRITE010PAGE_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", getGridHeight() - 10);

        reportWrite010PageGrid.jqGrid('setGroupHeaders', {
            useColSpanStyle : true,
            groupHeaders : [
               /*{startColumnName : 'demandCompFormular',numberOfColumns : 2, titleText : '요구'},
               {startColumnName : 'compFormular', numberOfColumns : 2, titleText : '조정'}*/ 
               {startColumnName : 'demandBgtAmt',numberOfColumns : 1, titleText : '요구'},
               {startColumnName : 'bgtAmt', numberOfColumns : 1, titleText : '조정'},
               {startColumnName : 'reportMstr', numberOfColumns : 3, titleText : '분류'}
            ]
        });

        if(data.data.bgtDgr == "1"){
        	$("#REPORT_WRITE010PAGE_GRD", tabObj).jqGrid("setLabel", colModel[4]['name'], '본예산');
            $("#REPORT_WRITE010PAGE_GRD", tabObj).jqGrid("setLabel", colModel[6]['name'], '전년도<br/>최종예산');
        }else{
            $("#REPORT_WRITE010PAGE_GRD", tabObj).jqGrid("setLabel", colModel[4]['name'], '기정액');
            $("#REPORT_WRITE010PAGE_GRD", tabObj).jqGrid("setLabel", colModel[6]['name'], '전년도<br />최종예산');
        }
        
        $("#REPORT_WRITE010PAGE_PGR").addPagingData(data, "reportWrite010PageDoPageSearch");
        $("#REPORT_WRITE010PAGE_TOT").html("통계목 총건수 : " + addCommaStr(data.totalCount) + "건");
        
        $("#saveBtn", $("#"+tabId)).btnChangeState(true);

        data = null;
        
    };
    
    reportWrite010PageDoPageSearch = function(page) {        
        var saveDatas = getSaveDatas(reportWrite010PageGrid, $("#REPORT_WRITE010PAGE_GRD", tabObj)[0].rows);
        if(saveDatas != null && saveDatas != undefined && saveDatas.length > 0){
            $.csAlert({
                msg : "저장되지 않은 변경사항이 있습니다.<br>먼저 저장 후 페이지를 이동해 주세요."
            });
            return;
        }
        
        defaultSearchParam.page = page;
        doSearch();
    };

    var defaultSearchParam = {
        page : 1,
        rowNum : 3
    };
    
    var saveReportParam = null;
    
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
        var teMngMokCdFr = $("#condTeMngMokCdFr", tabObj).val();
        var teMngMokCdTo = $("#condTeMngMokCdTo", tabObj).val();
        var srchVal = $("#condSrchVal", tabObj).val();
        var frscFgCdFr = $("#condFrscFgCdFr", tabObj).val();
        var frscFgCdTo = $("#condFrscFgCdTo", tabObj).val();
        var frscFrCdYn = "N";
        var advncProc = $("#condAdvncProc option:selected", tabObj).val();
        if(isEmpty(frscFgCdFr) == false || isEmpty(frscFgCdTo) == false){
            frscFrCdYn = "Y";
        }
        var amtUnit = $("#condAmtUnit", tabObj).val();
        
        var param = {reportCd : reportCd,
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
                srchVal : srchVal,
                frscFgCdFr : frscFgCdFr,
                frscFgCdTo : frscFgCdTo,
                frscFrCdYn : frscFrCdYn,
                advncProc	: advncProc,
                amtUnit : amtUnit
        };
        
        saveReportParam = {};
        $.extend(saveReportParam, param);
        
        $.extend(defaultSearchParam, param);

        return defaultSearchParam;
    };
    
    var doSearch = function(){
        $.csAjaxCall({
            url : "/report/ajaxReportWrite010Report010PageList.do",
            data: getSearchParam(),
            async : true,
            callBack : doSearchCallBack
        });
    };
    
    $("#searchBtn", tabObj).click(function() {
        defaultSearchParam.page = 1;
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
        
      //분류항목(RP015) selectBox 세팅
        $("#condAdvncProc", tabObj).csCreatCombo(comboData, {
        	id : 'advncProc',
        	groupId : 'ALL',
        	selectedValue : '',
        	comboType : 'A',
        	comboTypeValue : ''
        });
       
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
        condFisFgMstCdCreateCombo(fisYear, 'T00');
        
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
        $("#condSrchVal", tabObj).val("");
        $("#rankBtn", tabObj).btnChangeState(true);
        updateBizDescFileBtnState();
    };

    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });
    
    // 수정된 행만 저장 대상으로 좁히기 위한 dirty 추적
    var dirtyRowIds = {};
    var markDirtyRow = function(rowId){
        if(isEmpty(rowId) == false){
            dirtyRowIds[rowId] = true;
        }
    };
    var clearDirtyRows = function(){
        dirtyRowIds = {};
    };
    $(tabObj).off("change.report010PageSave input.report010PageSave").on("change.report010PageSave input.report010PageSave", "#REPORT_WRITE010PAGE_GRD :input", function(){
        markDirtyRow($(this).closest("tr.jqgrow").attr("id"));
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
        var investPlan = "";
        var indiAttr = "";
        var advncProc = "";
        var rowIds = [];
        var dirtyKeys = Object.keys(dirtyRowIds);
        if(dirtyKeys.length > 0){
            rowIds = dirtyKeys;
        }else if(gridRows && gridRows.length){
            for(var r = 0; r < gridRows.length; r++){
                if(gridRows[r] && gridRows[r].id){ rowIds.push(gridRows[r].id); }
            }
        }
        for(var i = 0; i < rowIds.length; i++) {
            rowId = rowIds[i];
            if(isEmpty(rowId) == true || !$("#" + rowId, tabObj).length){ continue; }
            rowData = gridObject.getRowData(rowId);
            if(isEmpty(rowData.dgrcompoId) == false && rowData.teBgtCompoId != "00000000000"){
                demandCont = $('#demandCont_'+rowId, tabObj).val().trim();
                examCont = $('#examCont_'+rowId, tabObj).val().trim();
                reflectFg = $('#reflectFg_'+rowId, tabObj).val();
                srchVal = $('#srchVal_'+rowId, tabObj).val().trim();
                investPlan = $('#investPlan_'+rowId, tabObj).val();
                indiAttr = rowData.indiAttr; //getIndiAttrCheckVal(rowData.dgrcompoId);
                advncProc = rowData.advncProc; //getAdvncProcCheckVal(rowData.dgrcompoId);
                
                if(rowData.demandCont.trim() != demandCont
                        || rowData.examCont.trim() != examCont
                        || rowData.reflectFg != reflectFg
                        || rowData.srchVal.trim() != srchVal
                        || rowData.investPlan != investPlan
                        || rowData.indiAttr != indiAttr
                        || rowData.advncProc != advncProc
                        ){
                    
                    saveData = {};
                    saveData["fisYear"] = rowData.fisYear;
                    saveData["bgtDgr"] = rowData.bgtDgr;
                    saveData["reportCd"] = rowData.reportCd;
                    saveData["reportDetlCd"] = rowData.reportDetlCd;
                    saveData["teBgtCompoId"] = rowData.teBgtCompoId;
                    saveData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
                    saveData["demandCont"] = demandCont;
                    saveData["examCont"] = examCont;
                    saveData["reflectFg"] = isEmpty(reflectFg) == true ? "" : reflectFg;
                    saveData["srchVal"] = srchVal;
                    saveData["srchValYn"] = rowData.srchVal != srchVal ? "Y" : "N";
                    saveData["investPlan"] = isEmpty(investPlan) == true ? "" : investPlan;
                    saveData["indiAttr"] = indiAttr;
                    saveData["advncProc"] = advncProc;
                    
                    if(rowData.indiAttr != indiAttr
                            || rowData.advncProc != advncProc){
                    	saveData["updateReportFlag"] = 'Y';
                    }else{
                    	saveData["updateReportFlag"] = 'N';
                    }
                    
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

    var doSaveCallBack = function(data, saveDatas){
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });
            return;
        }

        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                var needReload = false;
                if(isEmpty(saveDatas) == false){
                    for(var i = 0; i < saveDatas.length; i++){
                        if(saveDatas[i].srchValYn === "Y" || saveDatas[i].reflegFgYn === "Y"){
                            needReload = true;
                            break;
                        }
                    }
                }
                clearDirtyRows();
                if(needReload){
                    doSearch();
                }else if(isEmpty(saveDatas) == false){
                    for(var j = 0; j < saveDatas.length; j++){
                        var sd = saveDatas[j];
                        var rid = sd.teBgtCompoId;
                        if(isEmpty(rid) == true){ continue; }
                        try{
                            reportWrite010PageGrid.setRowData(rid, {
                                demandCont : sd.demandCont,
                                examCont : sd.examCont,
                                reflectFg : sd.reflectFg,
                                srchVal : sd.srchVal,
                                investPlan : sd.investPlan,
                                indiAttr : sd.indiAttr,
                                advncProc : sd.advncProc
                            });
                        }catch(e){}
                    }
                }
            }
        });
    };
    
    var doSave = function(params){
        if(!params || params.confirmData != "Y"){
            return;
        }
        
        var saveDatas = getSaveDatas(reportWrite010PageGrid, $("#REPORT_WRITE010PAGE_GRD", tabObj)[0].rows);
        if(isEmpty(saveDatas) == true || saveDatas.length < 1){
            $.csAlert({
                msg : "변경된 자료가 존재하지 않습니다."
            });
            return;
        }

        $.csAjaxCall({
            url : "/report/ajaxReportWrite010SaveReport010.do",
            data : {saveDatas: saveDatas},
            async : true,
            callBack : function(data){
                doSaveCallBack(data, saveDatas);
            }
        });
    };

  //보고항목 체크된 코드 가져오기
    var getIndiAttrCheckVal = function(dgrcompoId){
    	var itemList = comboData['indiAttr'];
    	var rtnData = '';
    	for(var i=0 ; i<itemList.length ; i++){
        	var data = itemList[i];
        	var checkObj = $('#checkYnIndiAttr_' + dgrcompoId + '_' + data.code);
        	var checked = checkObj.is(':checked');
        	if(checked){
        		if(rtnData == ''){
        			rtnData = checkObj.val();
        		}else{
        			rtnData = rtnData + ',' + checkObj.val();
        		}
        	}
        }
    	return rtnData;
    }
    
    //사전절차 체크된 코드 가져오기
    var getAdvncProcCheckVal = function(dgrcompoId){
    	var itemList = comboData['advncProc'];
    	var rtnData = '';
    	for(var i=0 ; i<itemList.length ; i++){
        	var data = itemList[i];
        	var checkObj = $('#checkYnAdvncProc_' + dgrcompoId + '_' + data.code);
        	var checked = checkObj.is(':checked');
        	if(checked){
        		if(rtnData == ''){
        			rtnData = checkObj.val();
        		}else{
        			rtnData = rtnData + ',' + checkObj.val();
        		}
        	}
        }
    	return rtnData;
    }
    
    $("#saveBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        if(checkCloseYn(saveReportParam) == false){
            return;
        }
        
        $.csConfirm({
            msg : "저장하시겠습니까?",
            callBack : doSave
        });
    });

    /**
     * 조서 조회조건 실국(#condOfficeCd) 현재값.
     * native select 기준으로 읽고 hidden(#bizDescOfficeCd/Nm)에 동기화한다.
     */
    var getSelectedOffice = function(){
        var sel = tabObj.find("#condOfficeCd").get(0);
        if (!sel) {
            sel = $(".ui-tabs-panel:visible #condOfficeCd").get(0);
        }
        if (!sel) {
            sel = document.getElementById("condOfficeCd");
        }
        var officeCd = "";
        var officeNm = "";
        if (sel && sel.options && sel.selectedIndex >= 0) {
            officeCd = sel.options[sel.selectedIndex].value;
            officeNm = sel.options[sel.selectedIndex].text;
        } else if (sel) {
            officeCd = sel.value || "";
        }
        officeCd = (officeCd == null ? "" : String(officeCd)).replace(/^\s+|\s+$/g, "");
        officeNm = (officeNm == null ? "" : String(officeNm)).replace(/^\s+|\s+$/g, "");
        if (officeCd === "" || officeCd === "null" || officeCd === "undefined" || officeNm === "전체") {
            tabObj.find("#bizDescOfficeCd").val("ALL");
            tabObj.find("#bizDescOfficeNm").val("전체");
            return { officeCd: "ALL", officeNm: "전체", allOffice: true };
        }
        tabObj.find("#bizDescOfficeCd").val(officeCd);
        tabObj.find("#bizDescOfficeNm").val(officeNm);
        return { officeCd: officeCd, officeNm: officeNm, allOffice: false };
    };

    var getBizDescFilter = function(){
        var office = getSelectedOffice();
        var fisYear = String(tabObj.find("#condFisYear").val() || "").replace(/^\s+|\s+$/g, "");
        var bgtDgr = String(tabObj.find("#condBgtDgr").val() || "").replace(/^\s+|\s+$/g, "");
        if (bgtDgr === "null" || bgtDgr === "undefined") { bgtDgr = ""; }
        return {
            fisYear: fisYear,
            bgtDgr: bgtDgr,
            officeCd: office.officeCd,
            officeNm: office.officeNm,
            allOffice: !!office.allOffice,
            ready: !!(fisYear && bgtDgr)
        };
    };

    var updateBizDescFileBtnState = function(){
        var f = getBizDescFilter();
        var $btn = $("#bizDescFileBtn", tabObj);
        if (!$btn.length) { return; }
        if (f.ready) {
            $btn.attr("enabledYn", "Y").removeClass("btnDisabledClass").addClass("btnClass");
        } else {
            $btn.attr("enabledYn", "N").removeClass("btnClass").addClass("btnDisabledClass");
        }
    };

    $("#bizDescFileBtn", tabObj).click(function(e){
        e.preventDefault();
        var f = getBizDescFilter();
        updateBizDescFileBtnState();
        if (typeof openDialogBizDescMatch !== "function") {
            $.csAlert({ msg: "사업설명서 화면이 준비되지 않았습니다. 메인 화면을 새로고침(F5) 후 다시 시도해 주세요." });
            return;
        }
        if (!f.ready) {
            var missing = [];
            if (!f.fisYear) { missing.push("회계년도"); }
            if (!f.bgtDgr) { missing.push("예산차수"); }
            $.csAlert({
                msg: "조회조건 '" + missing.join("', '") + "'을(를) 선택한 뒤 사업설명서를 불러와 주세요."
            });
            return;
        }
        var sp = getSearchParam();
        openDialogBizDescMatch({
            fisYear: sp.fisYear || f.fisYear,
            bgtDgr: sp.bgtDgr || f.bgtDgr,
            reportCd: sp.reportCd || "010",
            officeCd: f.officeCd,
            officeNm: f.officeNm
        });
    });

    $(tabObj).on("click", "a.bizdesc-nm-link", function(e){
        e.preventDefault();
        var $a = $(this);
        var office = getSelectedOffice();
        if (typeof openDialogBizDescView !== "function") {
            $.csAlert({ msg: "사업설명서 화면이 준비되지 않았습니다. 메인 화면을 새로고침(F5) 후 다시 시도해 주세요." });
            return;
        }
        openDialogBizDescView({
            fisYear: $a.data("fisyear") || tabObj.find("#condFisYear").val(),
            bgtDgr: $a.data("bgtdgr") || tabObj.find("#condBgtDgr").val(),
            reportCd: $a.data("reportcd") || "010",
            teBgtCompoId: $a.data("tebgtcompoid"),
            dgrcompoId: $a.data("dgrcompoid"),
            reportBizNm: $a.attr("data-biznm") || "",
            dbizNm: $a.attr("data-dbiznm") || "",
            officeCd: office.officeCd,
            officeNm: office.officeNm,
            tabId: tabId
        });
    });
    
    $("#saveFileBtn", tabObj).click(function() {
        var param = getSearchParam();
        param["fileNm"] = "경상사업심사조서";

        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite010SaveFile.do"
          , data: param
        });
    });

    $("#saveFileTotalBtn", tabObj).click(function() {
    	var param = getSearchParam();
    	param["fileNm"] = "경상사업심사조서(통합)";
    	param["flag"] = "total";
    	$.bcjisExcelAjaxCall({
    		url : "/report/ajaxReportWrite010SaveFile.do"
    			, data: param
    	});
    });
    
    $("#saveSheetBtn", tabObj).click(function() {
        var param = getSearchParam();
        param["fileNm"] = "경상사업심사조서";

        $.bcjisExcelAjaxCall({
            url : "/report/ajaxReportWrite010SaveSheet.do"
          , data: param
        });
    });

    $("#rankBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }

        $("#dialogDgroffice010SortCallBackFunction", $("#dialogDgroffice010SortDiv")).val("reportWrite010PageDialogDgroffice010SortCallBackFunction");
        $("#dialogDgroffice010SortFisYear", $("#dialogDgroffice010SortDiv")).val($("#condFisYear option:selected", tabObj).val());
        $("#dialogDgroffice010SortBgtDgr", $("#dialogDgroffice010SortDiv")).val($("#condBgtDgr option:selected", tabObj).val());

        $("#dialogDgroffice010SortDiv").dialog('open');
    });

    reportWrite010PageDialogDgroffice010SortCallBackFunction = function(param){
        $.csAlert({
            msg : "다시 조회하시면 변경된 실국순서로 정렬됩니다."
        });
    };

    var doChangeCondFisYear = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
        condFisFgMstCdCreateCombo(fisYear, 'T00');
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
        
        condTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');
        condTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');
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
        updateBizDescFileBtnState();
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
    
    $("#condTeMngMokCdFr", tabObj).change(function(){
        $("#condTeMngMokCdTo", tabObj).val($("#condTeMngMokCdFr option:selected", tabObj).val());
    });
    
    $("#condFrscFgCdFr", tabObj).change(function(){
        $("#condFrscFgCdTo", tabObj).val($("#condFrscFgCdFr option:selected", tabObj).val());
    });
    
    var openDialogBgtDeptSelt = function(seltFg){

        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("reportWrite010PageDialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("010");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    reportWrite010PageDialogDgrDeptSeltCallBack = function(param){
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

    $("#condRowNum", tabObj).change(function() {
        defaultSearchParam.rowNum = $("#condRowNum option:selected", tabObj).val();
        defaultSearchParam.page = 1;
        
        $("#searchBtn", tabObj).click();
    });

    var comboParam = [
                      {id : "reportCd", codeId : "RP011"},
                      {id : "reportDetlCd", codeId : "RP012"},
                      {id : "reportMstr", codeId : "RP010"},
                      {id : "govSub", codeId : "RP013"},
                      {id : "indiAttr", codeId : "RP014"},
                      {id : "advncProc", codeId : "RP015"},
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "bgtDgr", subQueryId : "BgtDgr"},
                      {id : "fisFgMstCd", subQueryId : "FisFgMstCd"},
                      {id : "fisFgCd", subQueryId : "FisFgCd"},
                      {id : "officeCd", subQueryId : "OfficeCd", reportCd: "010"},
                      {id : "reflectFg", codeId : "RP003"},
                      {id : "teMngMokCd", subQueryId : "TeMngMokCd"},
                      {id : "frscFgCd", subQueryId : "FrscFgCd"},
                      {id : "rowNum", codeId : "CC001"}
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
    
    var condFisFgMstCdCreateCombo = function(groupId, selectedValue){
        $("#condFisFgMstCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgMstCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  , beforeAdd : [{groupId: groupId, code: 'T00', codeNm: "전체(기금제외)"}]
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
        
    var reflectFgCreateCombo = function(groupId, selectedValue){
        return getCsComboStr(comboData
                , {id: 'reflectFg'
                    , groupId: groupId
                    , selectedValue: selectedValue
                    , comboType: 'S' //수정
                    , comboTypeValue: ''
                    });
    };
    
    $("#condRowNum", tabObj).csCreatCombo(comboData, {
        id : 'rowNum',
        groupId : 'ALL',
        selectedValue : '3',
        comboType : '',
        comboTypeValue : ''
    });
    
    doCondInit();
    updateBizDescFileBtnState();
});
