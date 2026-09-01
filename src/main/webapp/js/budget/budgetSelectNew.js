$(document).ready(function() {
    var tabId = _budgetSelectTabId;
    var tabObj = $("#"+tabId);
    // 탭별 독립 그리드 ID — 조서·집계 / 보고·분류 동시 오픈 시 jqGrid ID 충돌 방지
    var gridId = "BUDGET_SELECT_NEW_GRD_" + tabId;
    var gridScrollPosition = 0;
    var searchDetl = '';
    var comboData = null;
    // 사용안함(삭제) 코드까지 포함한 이름조회 전용 맵(선택목록에는 노출 안함) — attr 모드에서만 사용
    var codeNmAll = { indiAttr: {}, advncProc: {} };
    // class: 조서·집계 항목선택 / attr: 보고항목·분류항목
    // data-view-mode를 우선 사용해 전역변수 덮어쓰기 충돌 방지
    var viewMode = tabObj.find("[data-view-mode]").attr("data-view-mode")
        || ((typeof _budgetSelectViewMode !== 'undefined' && _budgetSelectViewMode) ? _budgetSelectViewMode : 'class');
    var isAttrMode = (viewMode === 'attr');
    // 직전 취소 스냅샷 (탭 닫으면 삭제)
    var cancelUndoSnapshot = null;
    var undoMemoryAction = null;
    var isSysAdmin = (typeof _mainNorthPowGrCd !== 'undefined' && _mainNorthPowGrCd === 'BC001');

    if (isAttrMode) {
        $(".viewModeClassOnly", tabObj).hide();
        $(".viewModeAttrOnly", tabObj).show();
    } else {
        $(".viewModeClassOnly", tabObj).show();
        $(".viewModeAttrOnly", tabObj).hide();
    }

    // 선택코드 문자열(쉼표/파이프)에서 코드 정확히 포함 여부 — includes() 부분일치로 전부 체크되는 문제 방지
    var hasSelectedCode = function(cellValue, code){
        if(isEmpty(cellValue) == true || isEmpty(code) == true){
            return false;
        }
        var parts = String(cellValue).split(/[,|]/);
        for(var i = 0; i < parts.length; i++){
            if($.trim(parts[i]) === String(code)){
                return true;
            }
        }
        return false;
    };

    var firstCode = function(cellValue){
        if(isEmpty(cellValue) == true){
            return "";
        }
        var parts = String(cellValue).split(/[,|]/);
        for(var i = 0; i < parts.length; i++){
            var p = $.trim(parts[i]);
            if(p.length > 0){
                return p;
            }
        }
        return "";
    };

    var getCodeNm = function(comboId, code){
        if(isEmpty(code) || !comboData || !comboData[comboId]){ return ""; }
        var list = comboData[comboId];
        var codeVal = firstCode(code);
        for(var i=0;i<list.length;i++){
            if(String(list[i].code) === String(codeVal)){
                return list[i].codeNm;
            }
        }
        // 활성목록에 없으면(사용안함 처리된 코드) 이름조회 전용 맵에서 이름만 찾아옴
        if(codeNmAll[comboId] && codeNmAll[comboId][codeVal]){
            return codeNmAll[comboId][codeVal];
        }
        return codeVal || "";
    };
    var resolveCodeNames = function(comboId, cellValue){
        if(isEmpty(cellValue)){ return ""; }
        var parts = String(cellValue).split(/[,|]/);
        var names = [];
        for(var i=0;i<parts.length;i++){
            var c = $.trim(parts[i]);
            if(!c) continue;
            var nm = c;
            var found = false;
            if(comboData && comboData[comboId]){
                for(var j=0;j<comboData[comboId].length;j++){
                    if(String(comboData[comboId][j].code)===c){ nm = comboData[comboId][j].codeNm; found = true; break; }
                }
            }
            if(!found && codeNmAll[comboId] && codeNmAll[comboId][c]){
                nm = codeNmAll[comboId][c];
            }
            names.push(nm);
        }
        return names.join(',');
    };

    // 사용안함(삭제) 코드도 포함한 이름조회 맵 로드 — 조회조건/체크박스 선택목록에는 반영하지 않음
    var loadAttrCodeNmAll = function(){
        if(!isAttrMode){ return; }
        var fillMap = function(comboId){
            return function(data){
                if(isEmpty(data) === true || data[BCJIS_RETURN_CODE] != "SUCC"){ return; }
                var list = data.dataList || [];
                var map = {};
                for(var i=0;i<list.length;i++){
                    map[String(list[i].detlCd)] = list[i].detlCdNm;
                }
                codeNmAll[comboId] = map;
            };
        };
        $.csAjaxCall({
            url : "/budget/ajaxBudgetCommCdListAll.do",
            data: { codeId : "RP014" },
            async : true,
            callBack : fillMap('indiAttr')
        });
        $.csAjaxCall({
            url : "/budget/ajaxBudgetCommCdListAll.do",
            data: { codeId : "RP015" },
            async : true,
            callBack : fillMap('advncProc')
        });
    };

    // 조서·집계 항목선택 글자색 — 기존 budgetSelect.js 기준 + 화면 범례
    // 1.경상(#0000FF) 2.투자(#FF0000) 3.기본경비(#00B8B8) 4.공통경비(#FF9900) 5.그외(#FF99FF)
    var resolveBizColor = function(rowObject){
        if(!rowObject){ return ''; }
        // 부모행(회계/실국/부서/세부사업)은 색상 미적용
        if(String(rowObject.teBgtCompoId) == "00000000000"){
            return '';
        }
        var reportCd = firstCode(rowObject.reportCd);
        var reportDetlCd = firstCode(rowObject.reportDetlCd);
        if(rowObject.sel010Yn == "Y" || reportCd == "010" || hasSelectedCode(rowObject.reportCd, "010")){
            return '#0000FF';
        }
        if(rowObject.sel020Yn == "Y" || reportCd == "020" || hasSelectedCode(rowObject.reportCd, "020")){
            return '#FF0000';
        }
        if(rowObject.sel040Yn == "Y" || reportCd == "040" || hasSelectedCode(rowObject.reportCd, "040")){
            return '#00B8B8';
        }
        if(rowObject.sel060Yn == "Y" || reportCd == "060" || hasSelectedCode(rowObject.reportCd, "060")){
            return '#FF9900';
        }
        if(rowObject.sel050Yn == "Y" || rowObject.sel055Yn == "Y" || rowObject.sel090Yn == "Y"
                || rowObject.seletcYn == "Y" || rowObject.selSheetYn == "Y"
                || reportCd == "050" || reportCd == "055" || reportCd == "070" || reportCd == "080" || reportCd == "090"
                || (isEmpty(reportDetlCd) == false && String(reportDetlCd) !== '')){
            return '#FF99FF';
        }
        return '';
    };

    var myCellattr = function (rowId, tv, rowObject, cm, rdata) {
        var color = resolveBizColor(rowObject);
        return color ? (' style="color:' + color + '"') : '';
    };

    // tree ExpandColumn 등으로 cellattr이 누락되는 경우 대비 — 행 단위로 색 재적용
    var BIZ_COLOR_COLS = ['dgrcompoNm', 'teMngMokNm', 'reportMstrNm', 'reportCdNm', 'reportDetlCdNm', 'govSubNm'];
    var applyBizRowColors = function(){
        if(isAttrMode){ return; }
        var gridEl = $("#"+gridId, tabObj)[0];
        if(!gridEl || !gridEl.rows){ return; }
        // 컬럼 index 1회만 계산 — 행마다 jQuery 셀렉터 사용 금지(실국 조회 시 수천행 지연 방지)
        var colIdx = [];
        for(var c = 0; c < BIZ_COLOR_COLS.length; c++){
            colIdx.push(getColumnIndexByName(budgetSelectGrid, BIZ_COLOR_COLS[c]));
        }
        var rows = gridEl.rows;
        for(var i = 0; i < rows.length; i++){
            var tr = rows[i];
            var rowId = tr.id;
            if(!rowId){ continue; }
            var rowData = null;
            try{ rowData = budgetSelectGrid.jqGrid('getLocalRow', rowId); }catch(e0){}
            if(!rowData){ continue; }
            if(String(rowData.teBgtCompoId) == "00000000000"){ continue; }
            var color = resolveBizColor(rowData);
            if(!color){ continue; }
            for(var c = 0; c < colIdx.length; c++){
                var idx = colIdx[c];
                if(idx >= 0 && tr.cells && tr.cells[idx]){
                    tr.cells[idx].style.color = color;
                }
            }
        }
    };

    // 적용 후 재조회 없이 글자색용 sel*Yn 동기화
    var syncSelFlagsFromReportCd = function(rowId, rowData){
        var rc = firstCode(rowData.reportCd);
        var flagMap = {
            sel010Yn: "N", sel020Yn: "N", sel040Yn: "N",
            sel050Yn: "N", sel055Yn: "N", sel060Yn: "N", sel090Yn: "N", seletcYn: "N"
        };
        if(rc == "010"){ flagMap.sel010Yn = "Y"; }
        else if(rc == "020"){ flagMap.sel020Yn = "Y"; }
        else if(rc == "040"){ flagMap.sel040Yn = "Y"; flagMap.seletcYn = "Y"; }
        else if(rc == "050"){ flagMap.sel050Yn = "Y"; flagMap.seletcYn = "Y"; }
        else if(rc == "055"){ flagMap.sel055Yn = "Y"; flagMap.seletcYn = "Y"; }
        else if(rc == "060"){ flagMap.sel060Yn = "Y"; flagMap.seletcYn = "Y"; }
        else if(rc == "090"){ flagMap.sel090Yn = "Y"; flagMap.seletcYn = "Y"; }
        else if(rc == "070" || rc == "080"){ flagMap.seletcYn = "Y"; }
        for(var k in flagMap){
            if(flagMap.hasOwnProperty(k)){
                budgetSelectGrid.jqGrid("setCell", rowId, k, flagMap[k]);
            }
        }
    };

    var isSelYnChecked = function(v){
        return v === true || v === "Y" || v === "Yes" || v === "true" || v === "TRUE";
    };

    var uncheckSelYnRow = function(rowId){
        budgetSelectGrid.jqGrid("setCell", rowId, "selYn", "N");
        try{
            $("#"+$.jgrid.jqID(rowId)+" td[aria-describedby='"+gridId+"_selYn'] input[type=checkbox]", tabObj).prop("checked", false);
        }catch(e2){
            $("#"+rowId+" td[aria-describedby='"+gridId+"_selYn'] input[type=checkbox]", tabObj).prop("checked", false);
        }
    };
    
    //국고보조사업(재원) 그리드 fomatter
    var report030FgFormatter = function(cellValue, options, rowObject){
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.teBgtCompoId == "00000000000" ){
            return cellValue;
        }

        // 서버가 checkYn031~035를 내려주지 않는 행(현 조회 SQL)에서는 govSub 값으로 최초 체크상태를 보정
        // (govSub만 보고 판단 -> 실제 030 미등록 상태라도 무방: 안 건드리면 그대로, 다시 적용하면 정상 등록됨)
        var chk031 = rowObject.checkYn031 == 'Y' || (isEmpty(rowObject.checkYn031) && rowObject.govSub == '031');
        var chk032 = rowObject.checkYn032 == 'Y' || (isEmpty(rowObject.checkYn032) && rowObject.govSub == '032');
        var chk033 = rowObject.checkYn033 == 'Y' || (isEmpty(rowObject.checkYn033) && rowObject.govSub == '033');
        var chk034 = rowObject.checkYn034 == 'Y' || (isEmpty(rowObject.checkYn034) && rowObject.govSub == '034');
        var chk035 = rowObject.checkYn035 == 'Y' || (isEmpty(rowObject.checkYn035) && rowObject.govSub == '035');

        var rVal = '<div>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn031_'+rowObject.dgrcompoId+'" value="Y" class="chkBudgetSelect" onclick="javascript:budgetSelectCheckYn(\'031\', \''+rowObject.dgrcompoId+'\');" style="margin-top: 5px;" '+(chk031 ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">국고-일반</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn032_'+rowObject.dgrcompoId+'" value="Y" class="chkBudgetSelect" onclick="javascript:budgetSelectCheckYn(\'032\', \''+rowObject.dgrcompoId+'\');" style="margin-top: 5px;" '+(chk032 ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">국고-균특</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn033_'+rowObject.dgrcompoId+'" value="Y" class="chkBudgetSelect" onclick="javascript:budgetSelectCheckYn(\'033\', \''+rowObject.dgrcompoId+'\');" style="margin-top: 5px;" '+(chk033 ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">국고-기금</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn034_'+rowObject.dgrcompoId+'" value="Y" class="chkBudgetSelect" onclick="javascript:budgetSelectCheckYn(\'034\', \''+rowObject.dgrcompoId+'\');" style="margin-top: 5px;" '+(chk034 ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">국고-기타특별</span>'
                 + '&nbsp;&nbsp;<input type="checkbox" id="checkYn035_'+rowObject.dgrcompoId+'" value="Y" class="chkBudgetSelect" onclick="javascript:budgetSelectCheckYn(\'035\', \''+rowObject.dgrcompoId+'\');" style="margin-top: 5px;" '+(chk035 ? 'checked' : '')+' /><span style="line-height:22px; vertical-align:top;">국비직접지원</span>'
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
        if(!itemList || !itemList.length){
            return firstCode(cellValue) || cellValue || "";
        }
        
        if(cellValue == "" && isEmpty(rowObject.reportMstr) != true){
        	cellValue = rowObject.reportMstr;
        }
        
        //중분류데이터가 있는데 대분류 데이터가 없을경우
        if(cellValue == "" && isEmpty(rowObject.reportCd) != true){
        	var reportCdList = comboData['reportCd'];
        	var reportCd = rowObject.reportCd;
        	var grpId = '';
        	for(var i=0 ; i<reportCdList.length ; i++){
        		var reportCdData = reportCdList[i];
        		if(reportCd == reportCdData.code){
        			grpId = reportCdData.groupId;
        		}
        	}
        	
        	if(grpId != ''){
        		cellValue = grpId;
        	}
        }
        
        var rVal = '<div>';
        for(var i=0 ; i<itemList.length ; i++){
        	var data = itemList[i];
        	if(hasSelectedCode(cellValue, data.code)){
    			rVal += '<span style="line-height:22px; vertical-align:top;">' + data.codeNm + '</span><br />';
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
    	if(!itemList || !itemList.length){
    		return firstCode(cellValue) || cellValue || "";
    	}

    	if(cellValue == "" && isEmpty(rowObject.reportCd) != true){
        	cellValue = rowObject.reportCd;
        }
    	
    	var rVal = '<div>';
    	for(var i=0 ; i<itemList.length ; i++){
    		var data = itemList[i];
    		if(hasSelectedCode(cellValue, data.code)){
    			rVal += '<span style="line-height:22px; vertical-align:top;">' + data.codeNm + '</span><br />';
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
    	if(!itemList || !itemList.length){
    		return firstCode(cellValue) || cellValue || "";
    	}

    	if(cellValue == "" && isEmpty(rowObject.reportDetlCd) != true){
        	cellValue = rowObject.reportDetlCd;
        }
    	
    	var rVal = '<div>';
    	if(hasSelectedCode(cellValue, '024') || hasSelectedCode(cellValue, '025') || hasSelectedCode(cellValue, '026')){
    		rVal += '<span style="line-height:22px; vertical-align:top;">자체투자</span><br />';
    	}else if(hasSelectedCode(cellValue, '021') || hasSelectedCode(cellValue, '022') || hasSelectedCode(cellValue, '023')){
    		rVal += '<span style="line-height:22px; vertical-align:top;">국고투자</span><br />';
    	}else{
    		for(var i=0 ; i<itemList.length ; i++){
        		var data = itemList[i];
        		if(hasSelectedCode(cellValue, data.code)){
        			rVal += '<span style="line-height:22px; vertical-align:top;">' + data.codeNm + '</span><br />';
        		}
        	}
    	}
    	
    	rVal += '</div>';
    	
    	return rVal;
    };
    
  //국고보조 그리드fomatter
    var govSubFormatter = function(cellValue, options, rowObject){
    	
        if(isEmpty(cellValue) == true){
            cellValue = "";
        }
        
        if(rowObject.teBgtCompoId == "00000000000" ){
            return cellValue;
        }
        var govSubList = comboData['govSub'];
        if(!govSubList || !govSubList.length){
            return firstCode(cellValue) || cellValue || "";
        }

        if(cellValue == "" && isEmpty(rowObject.govSub) != true){
        	cellValue = rowObject.govSub;
        }
        
        var rVal = '<div>';
        for(var i=0 ; i<govSubList.length ; i++){
        	var data = govSubList[i];
        	
        	if(hasSelectedCode(cellValue, data.code)){
    			rVal += '&nbsp;&nbsp;<span style="line-height:22px; vertical-align:top;">' + data.codeNm + '</span><br />';
    		}
        }
        
        rVal += '</div>';

        return rVal;
    };
    
    //보고항목 그리드fomatter
    var indiAttrFormatter = function(cellValue, options, rowObject){
    	if(isEmpty(cellValue) == true){
    		cellValue = "";
    	}
    	
    	if(rowObject.teBgtCompoId == "00000000000" ){
    		return cellValue;
    	}
    	
    	if(cellValue == "" && isEmpty(rowObject.indiAttr) != true){
        	cellValue = rowObject.indiAttr;
        }
    	
    	var itemList = comboData['indiAttr'];
    	
    	var rVal = '<div>';
        for(var i=0 ; i<itemList.length ; i++){
        	var data = itemList[i];
        	var checked = '';
        	
        	if(hasSelectedCode(cellValue, data.code)){
        		checked = 'checked';
        	}
        	
        	rVal += '&nbsp;&nbsp;<span style="line-height:22px; vertical-align:top;"><input type="checkbox" id="checkYnIndiAttr_' + rowObject.dgrcompoId + '_' +data.code + '" value="' + data.code + '" class="chkBudgetSelect" onchange="javascript:onChangeFlag(\'' + rowObject.dgrcompoId + '\', this);" style="margin-top: 5px;" ' + checked + ' /><label for="checkYnIndiAttr_' + rowObject.dgrcompoId + '_' +data.code + '">' + data.codeNm + '</label></span><br />';
        }
        rVal += '</div>';
        
    	return rVal;
    };
    
    //사전절차 그리드fomatter
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
        	
        	if(hasSelectedCode(cellValue, data.code)){
        		checked = 'checked';
        	}
        	
        	rVal += '&nbsp;&nbsp;<span style="line-height:22px; vertical-align:top;"><input type="checkbox" id="checkYnAdvncProc_' + rowObject.dgrcompoId + '_' +data.code + '" value="' + data.code + '" class="chkBudgetSelect" onchange="javascript:onChangeFlag(\'' + rowObject.dgrcompoId + '\', this);" style="margin-top: 5px;" ' + checked + ' /><label for="checkYnAdvncProc_' + rowObject.dgrcompoId + '_' +data.code + '">' + data.codeNm + '</label></span>';
        	if((i % 4) == 3){
        		rVal += '<br />';
        	}
        }
        rVal += '</div>';
    	
    	return rVal;
    };

    // attr: 대/중/소/국고 명칭 병합 표시
    var reportClassNmFormatter = function(cellValue, options, rowObject){
        if(rowObject.teBgtCompoId == "00000000000"){
            return cellValue || "";
        }
        var parts = [];
        var mstr = getCodeNm('reportMstr', rowObject.reportMstr);
        var cd = getCodeNm('reportCd', rowObject.reportCd);
        var detl = getCodeNm('reportDetlCd', rowObject.reportDetlCd);
        var gov = getCodeNm('govSub', rowObject.govSub);
        if(mstr){ parts.push(mstr); }
        if(cd){ parts.push(cd); }
        if(detl){ parts.push(detl); }
        if(gov){ parts.push(gov); }
        return parts.join('/');
    };

    // attr: 보고항목·분류항목 — 한줄 '선택' 토글 + 체크 다중선택
    var buildAttrCheckPanel = function(kind, dgrcompoId, comboId, selectedVal){
        var list = (comboData && comboData[comboId]) ? comboData[comboId] : [];
        var panelId = 'attrDrop_'+kind+'_'+dgrcompoId;
        var html = '<div class="attrDropWrap" style="position:relative;display:inline-block;width:48%;vertical-align:top;margin-right:2%;">';
        html += '<div class="attrDropToggle" id="attrToggle_'+kind+'_'+dgrcompoId+'" '
             + 'onclick="toggleAttrDrop(\''+kind+'\',\''+dgrcompoId+'\');return false;" '
             + 'style="height:22px;line-height:22px;border:1px solid #aaa;background:#fff;padding:0 6px;cursor:pointer;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;">선택</div>';
        html += '<div id="'+panelId+'" class="attrDropPanel" style="display:none;position:absolute;left:0;top:23px;z-index:2000;background:#fff;border:1px solid #888;max-height:160px;overflow:auto;width:220px;box-shadow:1px 1px 4px rgba(0,0,0,.15);">';
        for(var i=0;i<list.length;i++){
            var data = list[i];
            var chk = hasSelectedCode(selectedVal, data.code) ? ' checked' : '';
            var cid = 'chkAttr_'+kind+'_'+dgrcompoId+'_'+data.code;
            html += '<label for="'+cid+'" style="display:block;padding:2px 6px;line-height:20px;white-space:nowrap;cursor:pointer;">'
                 + '<input type="checkbox" id="'+cid+'" value="'+data.code+'"'+chk
                 + ' onclick="event.stopPropagation();" onchange="onAttrCheckChange(\''+dgrcompoId+'\');" /> '
                 + data.codeNm + '</label>';
        }
        html += '</div></div>';
        return html;
    };

    var attrSelectFormatter = function(cellValue, options, rowObject){
        if(rowObject.teBgtCompoId == "00000000000"){
            return "";
        }
        var dgrcompoId = rowObject.dgrcompoId;
        var indiVal = isEmpty(rowObject.indiAttr) ? "" : rowObject.indiAttr;
        var advVal = isEmpty(rowObject.advncProc) ? "" : rowObject.advncProc;
        return '<div style="white-space:nowrap;">'
             + buildAttrCheckPanel('indi', dgrcompoId, 'indiAttr', indiVal)
             + buildAttrCheckPanel('advn', dgrcompoId, 'advncProc', advVal)
             + '</div>';
    };

    // attr: 사업정보 — 투자사업유형(미선택) + 분류항목(항목별 삭제)
    // indiAttr=투자사업유형, advncProc=분류항목
    var buildBizInfoHtml = function(rowObject){
        if(!rowObject || rowObject.teBgtCompoId == "00000000000"){
            return "";
        }
        var dgrId = rowObject.dgrcompoId;
        var parts = [];
        var indiNm = resolveCodeNames('indiAttr', rowObject.indiAttr);
        // 투자사업만 투자사업유형 표시(미지정 시 '미선택'). 그 외 사업은 표시하지 않음
        if(isInvestReportRow(rowObject)){
            parts.push('<span class="bizIndiLabel">' + (indiNm ? indiNm : '미선택') + '</span>');
        }
        var advRaw = (rowObject.advncProc == null) ? '' : String(rowObject.advncProc);
        if(advRaw && advRaw !== 'null'){
            var codes = advRaw.split(/[,|]/);
            for(var i = 0; i < codes.length; i++){
                var code = $.trim(codes[i]);
                if(!code){ continue; }
                var nm = getCodeNm('advncProc', code) || code;
                parts.push(
                    '<span class="bizAdvnItem" style="white-space:nowrap;">'
                    + nm
                    + '<a href="#" class="bizAdvnDel" title="분류항목 삭제" style="color:#c00;text-decoration:underline;margin-left:4px;"'
                    + ' data-dgr="'+dgrId+'" data-code="'+code+'">삭제</a>'
                    + '</span>'
                );
            }
        }
        if(parts.length < 1){ return ''; }
        return '<span id="bizInfo_'+dgrId+'" style="display:block;white-space:normal;word-break:break-all;line-height:1.5;">'
             + parts.join(' <span style="color:#999;">/</span> ')
             + '</span>';
    };

    var bizInfoFormatter = function(cellValue, options, rowObject){
        return buildBizInfoHtml(rowObject);
    };

    var isInvestReportRow = function(rowObject){
        if(!rowObject){ return false; }
        var mstr = firstCode(rowObject.reportMstr);
        var nm = getCodeNm('reportMstr', mstr) || '';
        if(nm.indexOf('투자') >= 0){ return true; }
        if(mstr && (String(mstr).toUpperCase().indexOf('I') === 0)){ return true; }
        // reportCd 020 = 투자사업조서
        var rc = firstCode(rowObject.reportCd);
        return rc === '020';
    };

    var buildAdvncProcColHtml = function(rowObject){
        if(!rowObject || rowObject.teBgtCompoId == "00000000000"){
            return "";
        }
        var dgrId = rowObject.dgrcompoId;
        var parts = [];
        var advRaw = (rowObject.advncProc == null) ? '' : String(rowObject.advncProc);
        if(advRaw && advRaw !== 'null'){
            var codes = advRaw.split(/[,|]/);
            for(var i = 0; i < codes.length; i++){
                var code = $.trim(codes[i]);
                if(!code){ continue; }
                var nm = getCodeNm('advncProc', code) || code;
                parts.push(
                    '<span class="bizAdvnItem" style="white-space:nowrap;">'
                    + nm
                    + '<a href="#" class="bizAdvnDel" title="분류항목 삭제" style="color:#c00;text-decoration:underline;margin-left:4px;"'
                    + ' data-dgr="'+dgrId+'" data-code="'+code+'">삭제</a>'
                    + '</span>'
                );
            }
        }
        return '<span id="advnCol_'+dgrId+'" style="display:block;white-space:normal;word-break:break-all;line-height:1.5;">'
             + (parts.length ? parts.join('<br/>') : '')
             + '</span>';
    };

    var buildIndiAttrColHtml = function(rowObject){
        if(!rowObject || rowObject.teBgtCompoId == "00000000000"){
            return "";
        }
        if(!isInvestReportRow(rowObject)){
            return "";
        }
        var dgrId = rowObject.dgrcompoId;
        var indiNm = resolveCodeNames('indiAttr', rowObject.indiAttr);
        return '<span id="indiCol_'+dgrId+'" style="display:block;white-space:normal;word-break:break-all;line-height:1.5;">'
             + (indiNm ? indiNm : '미선택')
             + '</span>';
    };

    var advncProcColFormatter = function(cellValue, options, rowObject){
        return buildAdvncProcColHtml(rowObject);
    };
    var indiAttrColFormatter = function(cellValue, options, rowObject){
        return buildIndiAttrColHtml(rowObject);
    };

    var refreshBizInfoCell = function(rowId, rowData){
        if(!rowData){ rowData = budgetSelectGrid.jqGrid('getLocalRow', rowId) || budgetSelectGrid.getRowData(rowId); }
        var dgrId = rowData.dgrcompoId || rowId;
        var setTdHtml = function(colName, html){
            var $td = null;
            try{
                $td = $("#"+$.jgrid.jqID(rowId)+" td[aria-describedby='"+gridId+"_"+colName+"']", tabObj);
            }catch(e0){
                $td = $("#"+rowId+" td[aria-describedby='"+gridId+"_"+colName+"']", tabObj);
            }
            if($td && $td.length){
                $td.html(html || '');
            }
        };
        setTdHtml('advncProcCol', buildAdvncProcColHtml(rowData));
        setTdHtml('indiAttrCol', buildIndiAttrColHtml(rowData));
        // 구 단일 컬럼 호환
        setTdHtml('bizInfo', buildBizInfoHtml(rowData));
    };

    toggleAttrDrop = function(kind, dgrcompoId){
        var $toggle = $('#attrToggle_'+kind+'_'+dgrcompoId);
        if($toggle.length < 1){ return; }
        var $scope = $toggle.closest('[id^="mainTabs01-"]');
        if($scope.length < 1){ $scope = $(document); }
        var $panel = $('#attrDrop_'+kind+'_'+dgrcompoId, $scope);
        if($panel.length < 1){ $panel = $('#attrDrop_'+kind+'_'+dgrcompoId); }
        if($panel.length < 1){ return; }
        var opening = !$panel.is(':visible');
        $('.attrDropPanel').hide();
        if(opening){
            if($panel.data('attrDropHost') !== 'body'){
                $panel.appendTo($scope.length ? $scope : $('body'));
                $panel.data('attrDropHost', 'body');
            }
            var rect = $toggle[0].getBoundingClientRect();
            $panel.css({
                position: 'fixed',
                left: rect.left + 'px',
                top: (rect.bottom + 1) + 'px',
                width: Math.max(rect.width, 200) + 'px',
                zIndex: 10000,
                display: 'block'
            });
        }
    };

    onAttrCheckChange = function(dgrcompoId){
        onChangeFlag(dgrcompoId);
        var indiNms = [], advNms = [];
        $('input[id^="chkAttr_indi_'+dgrcompoId+'_"]:checked').each(function(){
            indiNms.push($.trim($(this).closest('label').text()));
        });
        $('input[id^="chkAttr_advn_'+dgrcompoId+'_"]:checked').each(function(){
            advNms.push($.trim($(this).closest('label').text()));
        });
        var txt = indiNms.concat(advNms).join(', ');
        $('#bizInfo_'+dgrcompoId).text(txt);
    };

    // 그리드 스크롤/외부 클릭 시 패널 닫기
    $(document).off('mousedown.attrDrop_'+tabId).on('mousedown.attrDrop_'+tabId, function(e){
        if($(e.target).closest('.attrDropWrap,.attrDropPanel').length < 1){
            $('.attrDropPanel').hide();
        }
    });
    $("#"+gridId, tabObj).closest(".ui-jqgrid-bdiv").off('scroll.attrDrop').on('scroll.attrDrop', function(){
        $('.attrDropPanel').hide();
    });
    
    //국고보조사업(재원) 체크박스 클릭시 이벤트
    budgetSelectCheckYn = function(fg, id){
        var checkYn = $('#checkYn'+fg+'_'+id, tabObj).is(':checked') == true ? "Y" : "N";
        if(checkYn != "Y"){
            return;
        }
        
        if(fg == '031'){
            $('#checkYn032'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn033'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn034'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn035'+'_'+id, tabObj).removeAttr('checked');
        }else if(fg == '032'){
            $('#checkYn031'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn033'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn034'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn035'+'_'+id, tabObj).removeAttr('checked');
        }else if(fg == '033'){
            $('#checkYn031'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn032'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn034'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn035'+'_'+id, tabObj).removeAttr('checked');
        }else if(fg == '034'){
            $('#checkYn031'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn032'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn033'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn035'+'_'+id, tabObj).removeAttr('checked');
        }else{
        	$('#checkYn031'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn032'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn033'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn034'+'_'+id, tabObj).removeAttr('checked');
        }
    };
    
    var colNames = ['', isAttrMode ? '구분(실국-부서-세부사업-사업)' : '구분(회계-실국-부서-세부사업-사업)',
                    '통계목', isAttrMode ? '대분류/중분류/소분류/국고보조' : '대분류', '중분류', '소분류', '국고보조', 
                    '기정액', '증감액', '예산액', '증감액', '예산액', '재원정보', '선택정보', '국고보조사업(재원)',
                    isAttrMode ? '분류항목' : '보고항목', isAttrMode ? '투자사업유형' : '사업정보',
                    'indiAttr', 'advncProc',
                    'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'teBgtCompoId', 'dgrLevel', 'teBgtCompoSeq', 'existYn', 'sel010Yn', 'sel020Yn',
                    'sel040Yn', 'sel050Yn', 'sel055Yn', 'sel060Yn', 'sel090Yn', 'seletcYn', 'selSheetYn',
                    'checkYn031', 'checkYn032', 'checkYn033', 'checkYnTf1',
                    'reportMstr', 'reportCd', 'reportDetlCd', 'govSub', 'changeFlag', 'indiAttrOrg'
                   ];
    
    var colModel = [ 
                        {name : 'selYn', index:'selYn', width: 30, align:'center', sortable : false, fixed : true, hidden: false, formatter:'checkbox', editoptions:{value:'Y:N'}, formatoptions:{disabled:false}},
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : isAttrMode ? 360 : 300, sortable : false, fixed : true, align : 'left',
                            cellattr: myCellattr
                        },
                        {name : 'teMngMokNm', index : 'teMngMokNm', width : isAttrMode ? 70 : 80, sortable : false, fixed : true, align : 'center', cellattr: myCellattr, hidden: false},
                        {name : 'reportMstrNm', index : 'reportMstr', width : isAttrMode ? 240 : 90, sortable : false, hidden : false, fixed : true, align : 'center', cellattr: myCellattr,
                        	formatter: isAttrMode ? reportClassNmFormatter : reportMstrFormatter
                        },
                        {name : 'reportCdNm', index : 'reportCd', width : 90, sortable : false, hidden : isAttrMode, fixed : true, align : 'center', cellattr: myCellattr,
                        	formatter:reportCdFormatter
                        },
                        {name : 'reportDetlCdNm', index : 'reportDetlCd', width : 90, sortable : false, hidden : isAttrMode, fixed : true, align : 'center', cellattr: myCellattr,
                        	formatter:reportDetlCdFormatter
                        },
                        {name : 'govSubNm', index : 'govSub', width : 90, sortable : false, hidden : isAttrMode, fixed : true, align : 'center', cellattr: myCellattr,
                        	formatter:govSubFormatter
                        },
                        
                        {name : 'preAmt', index : 'preAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, hidden: isAttrMode},
                        {name : 'demandDiffAmt', index : 'demandDiffAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, hidden: isAttrMode},
                        {name : 'demandBgtAmt', index : 'demandBgtAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, hidden: isAttrMode},
                        {name : 'diffAmt', index : 'diffAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, hidden: isAttrMode},
                        {name : 'bgtAmt', index : 'bgtAmt', width : 80, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}, hidden: isAttrMode},
                        {name : 'frsces', index : 'frsces', width : 130, sortable : false, fixed : true, align : 'left', hidden: isAttrMode },
                        {name : 'selNames', index : 'selNames', width : 200, sortable : false, hidden : true, fixed : true, align : 'left'},
                        {name : 'report030FgView', index : 'report030FgView', width : 200, sortable : false, hidden : true, fixed : true, align : 'left', cellattr: myCellattr,
                            formatter: report030FgFormatter
                        },
                        {name : 'advncProcCol', index : 'advncProcCol', width : isAttrMode ? 200 : 120, sortable : false, hidden : !isAttrMode, fixed : true, align : 'left',
                        	cellattr: function(){ return ' style="white-space:normal;word-break:break-all;vertical-align:top;"'; },
                        	formatter: isAttrMode ? advncProcColFormatter : function(v){ return (v==null?'':v); }
                        },
                        {name : 'indiAttrCol', index : 'indiAttrCol', width : isAttrMode ? 140 : 120, sortable : false, hidden : !isAttrMode, fixed : true, align : 'left',
                        	cellattr: function(){ return ' style="white-space:normal;word-break:break-all;vertical-align:top;"'; },
                        	formatter: isAttrMode ? indiAttrColFormatter : function(v){ return (v==null?'':v); }
                        },
                        {name : 'indiAttr', index : 'indiAttr', width : 120, sortable : false, hidden : true, fixed : true, align : 'left'},
                        {name : 'advncProc', index : 'advncProc', width : 120, sortable : false, hidden : true, fixed : true, align : 'left'},
                        
                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key: true },
                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true},
                        {name : 'dgrLevel', index : 'dgrLevel', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'existYn', index : 'existYn', width : 0, sortable : false, hidden : true},
                        {name : 'sel010Yn', index : 'sel010Yn', width : 0, sortable : false, hidden : true},
                        {name : 'sel020Yn', index : 'sel020Yn', width : 0, sortable : false, hidden : true},
                        {name : 'sel040Yn', index : 'sel040Yn', width : 0, sortable : false, hidden : true},
                        {name : 'sel050Yn', index : 'sel050Yn', width : 0, sortable : false, hidden : true},
                        {name : 'sel055Yn', index : 'sel055Yn', width : 0, sortable : false, hidden : true},
                        {name : 'sel060Yn', index : 'sel060Yn', width : 0, sortable : false, hidden : true},
                        {name : 'sel090Yn', index : 'sel090Yn', width : 0, sortable : false, hidden : true},
                        {name : 'seletcYn', index : 'seletcYn', width : 0, sortable : false, hidden : true},
                        {name : 'selSheetYn', index : 'selSheetYn', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn031', index : 'checkYn031', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn032', index : 'checkYn032', width : 0, sortable : false, hidden : true},
                        {name : 'checkYn033', index : 'checkYn033', width : 0, sortable : false, hidden : true},
                        {name : 'checkYnTf1', index : 'checkYnTf1', width : 0, sortable : false, hidden : true},
                        {name : 'reportMstr', index : 'reportMstr', width : 0, sortable : false, hidden : true},
                        {name : 'reportCd', index : 'reportCd', width : 0, sortable : false, hidden : true},
                        {name : 'reportDetlCd', index : 'reportDetlCd', width : 0, sortable : false, hidden : true},
                        {name : 'govSub', index : 'govSub', width : 0, sortable : false, hidden : true},
                        {name : 'changeFlag', index : 'changeFlag', width : 0, sortable : false, hidden : true},
                        {name : 'indiAttrOrg', index : 'indiAttrOrg', width : 0, sortable : false, hidden : true},
                    ];
    
    //그리드 높이 가져오기
    /*var getGridHeight = function (){
        return $("#mainCenter", tabObj).height() - 110 > 200 ? $("#mainCenter", tabObj).height() - 110 : 200;
    };*/
    
    var getGridHeight = function (){
    	var height = $("#mainCenter", tabObj).height() - 125 > 200 ? $("#mainCenter", tabObj).height() - 125 : 200; 
    	$("#"+gridId, tabObj).closest(".ui-jqgrid-bdiv").css("max-height", height + 20);
        return height;
    };
    
    //mainBody 리사이즈
    var mainBodyResize = function(){ 
        if(isEmpty($("#"+gridId, $("#"+tabId))) == false){
            $("#"+gridId, $("#"+tabId)).setGridHeight(getGridHeight());
            $("#"+gridId, $("#"+tabId)).setGridWidth($("#mainCenter", tabObj).width());
        }
    };
    
    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;
    
    //화면 레이아웃 설정 (attr 모드는 상단 필터 행이 더 많아 north 높이 확대)
    $("#mainBody", tabObj).layout({
        north__size : isAttrMode ? 280 : 240,
        center__onresize: mainBodyResize
    });

    // 상단/하단 겹침 방지: 실제 조건영역 높이에 맞게 north 재조정
    var adjustNorthPane = function(){
        try{
            var $north = $("#mainNorth", tabObj);
            var need = ($north.find(".condition").outerHeight(true) || 0)
                     + ($north.find(".btn").outerHeight(true) || 0)
                     + ($north.find(".unitDiv").outerHeight(true) || 0) + 16;
            if(need < (isAttrMode ? 260 : 220)){
                need = isAttrMode ? 260 : 220;
            }
            $("#mainBody", tabObj).layout().sizePane("north", need);
            mainBodyResize();
        }catch(e){}
    };
    setTimeout(adjustNorthPane, 50);
    setTimeout(adjustNorthPane, 300);
    
    //조회 데이터 보관
    var saveReportParam = {
    		reportMstl: "", 
            reportCd: "", 
            reportDetlCd: "", 
            indiAttr: "",
            advncProc: "", 
            fisYear: "", 
            bgtDgr: "", 
            fisFgMstCd: "", 
            fisFgCd: "", 
            officeCd: "", 
            deptRankFr: "", 
            deptRankTo: "",
            teMngMokCdFr: "",
            teMngMokCdTo: "",
            frscFgCdFr: "",
            frscFgCdTo: "",
            frscFrCdYn: "",
            amtUnit: "1",
            orderYmdSeq: ""
    };
    
    //그리드 object
    var budgetSelectGrid = $("#"+gridId, tabObj);
    
    //조회 callback 이벤트
    var doSearchCallBack = function(data){
    	//데이터 이상없이 불러왔는지 체크
        if (isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC") {
            $.csAlert({		//경고창(alert)
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        //저장 버튼 활성화
        $("#saveBtn", $("#"+tabId)).btnChangeState(false);        

        //국고보조사업(재원) 보이기 _임시
        //colModel[16].hidden = false;

        //그리드 세팅
        $("#"+gridId, tabObj).jqGrid('GridUnload');
        budgetSelectGrid = $("#"+gridId, tabObj);
        budgetSelectGrid.csTreeGrid({
            datastr : data,				
            height : getGridHeight(),	
            colNames : colNames,		
            colModel : colModel,		
            ExpandColumn : "dgrcompoNm",	//확장컬럼(tree구조)
            jsonReader : {
                repeatitems : false,
                root : "dataList"
            },
            onSelectRow: function(rowId){
            },
            loadComplete: function() {
                var iColSelYn = getColumnIndexByName ($(this), 'selYn');
                var rows = this.rows;
                for(var i = 0; i < rows.length; i++) {
                    $(rows[i].cells[iColSelYn]).click(function (e) {
                        var checkedRowId = $(e.target).closest('tr')[0].id;
                        
                        setTreeGridChecked(e, budgetSelectGrid, $("#"+gridId, tabObj)[0].rows, 'dgrLevel');
                        setUpTreeGridCheck(budgetSelectGrid, checkedRowId, 'upDgrcompoId');
                    });
                }
                // 조회 후 사업명 글자색 적용 (tree ExpandColumn 대비)
                applyBizRowColors();
                // 조회 후 그리드 높이/폭 재맞춤 (목록이 안 보이는 레이아웃 이슈 방지)
                try{ mainBodyResize(); }catch(e){}
            }
        });
        
        $("#"+gridId, tabObj).closest(".ui-jqgrid-bdiv").css("max-height", getGridHeight() + 20);

        budgetSelectGrid.jqGrid('setGroupHeaders', {	//상단명 설정
            useColSpanStyle : true,
            groupHeaders : isAttrMode ? [
               {startColumnName : 'selYn', numberOfColumns : 2, titleText : '구분'},
               {startColumnName : 'reportMstrNm', numberOfColumns : 1, titleText : '분류'}, 
               {startColumnName : 'advncProcCol', numberOfColumns : 2, titleText : '보고항목'} 
            ] : [
               {startColumnName : 'selYn', numberOfColumns : 2, titleText : '구분'}, 
               {startColumnName : 'reportMstrNm', numberOfColumns : 3, titleText : '분류'}, 
               {startColumnName : 'demandDiffAmt',numberOfColumns : 2, titleText : '요구'},
               {startColumnName : 'diffAmt', numberOfColumns : 2, titleText : '조정'} 
            ]
        });
        
        $("#"+gridId, tabObj).closest(".ui-jqgrid-bdiv").scrollTop(gridScrollPosition);
        
        $("#saveBtn", $("#"+tabId)).btnChangeState(true);			//저장버튼 활성화
        if (!isAttrMode) {
            $("#selectAllBtn", $("#"+tabId)).btnChangeState(true);		//전체선택 버튼 활성화
            $("#unSelectAllBtn", $("#"+tabId)).btnChangeState(true);	//전체해제 버튼 활성화
            $("#saveAllBtn", $("#"+tabId)).btnChangeState(true);		//일괄적용 버튼 활성화
            $("#cancelClassBtn", $("#"+tabId)).btnChangeState(true);	//분류취소 버튼 활성화
            if(cancelUndoSnapshot && cancelUndoSnapshot.items && cancelUndoSnapshot.items.length > 0){
                showUndoCancelBtn();
            }
        }

        //$('#jqgh_BUDGET_SELECT_NEW_GRD_indiAttr').css('color', '#f26c4f'); //보고항목
        //$('#jqgh_BUDGET_SELECT_NEW_GRD_advncProc').css('color', '#f26c4f'); //사전절차
        data = null;
    };
    
    function Val_Chk_Han(num){
    	if(num.replace('-', '').replace(',','') >= 0 || num.replace('-', '').replace(',','') < 0){
    		return true;
    	}else{
    		return false;
    	}
    	
    }
    
    //조회 이벤트
    var doSearch = function(){
        var reportMstl = '';
        var reportCd = '';
        var reportDetlCd = '';
        var govSub = '';
        var indiAttr = '';
        var advncProc = '';
        // class 모드에서는 보고/분류 필터 미사용 (숨은 콤보 값이 조회를 오염시키지 않도록)
        if(isAttrMode){
            indiAttr = $("#condIndiAttr option:selected", tabObj).val();
            advncProc = $("#condAdvncProc option:selected", tabObj).val();
        }
        var fisYear = $("#condFisYear option:selected", tabObj).val() || '';
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val() || '';
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val() || '';
        var fisFgCd = $("#condFisFgCd option:selected", tabObj).val() || '';
        var officeCd = $("#condOfficeCd option:selected", tabObj).val() || '';
        var deptRankFr = $("#condDeptRankFr", tabObj).val() || '';
        var deptRankTo = $("#condDeptRankTo", tabObj).val() || '';
        var teMngMokCdFr = $("#condTeMngMokCdFr", tabObj).val() || '';
        var teMngMokCdTo = $("#condTeMngMokCdTo", tabObj).val() || '';
        var frscFgCdFr = $("#condFrscFgCdFr", tabObj).val() || '';
        var frscFgCdTo = $("#condFrscFgCdTo", tabObj).val() || '';
        var condAmtFr = ($("#condAmtFr", tabObj).val() || '').replace(/,/gi,'');
        var condAmtTo = ($("#condAmtTo", tabObj).val() || '').replace(/,/gi,'');
        var frscFrCdYn = "N";
        // 사업명 키워드: 공백·대소문자 무시 (서버 LIKE용 정규화)
        var bizNmKw = String($("#condBizNm", tabObj).val() || '')
            .replace(/[\s\u00A0\u3000]+/g, '')
            .toLowerCase();
        
        if(searchDetl == 'detl'){
        	reportMstl = $("#reportMstrSel option:selected", tabObj).val() || '';
            reportCd = $("#reportCdSel option:selected", tabObj).val() || '';
            reportDetlCd = $("#reportDetlCdSel option:selected", tabObj).val() || '';
            govSub = $("#govSubSel option:selected", tabObj).val() || '';
        }else{
            // 상단 조회: 분류(대/중/소/국고) 필터는 적용하지 않음 — 상단 조건만 사용
            reportMstl = '';
            reportCd = '';
            reportDetlCd = '';
            govSub = '';
        }
        
        if(condAmtFr != "" || condAmtFr != ""){
	        if(condAmtFr != ""){
	        	if(isNaN(condAmtFr)){
	            	$.csAlert({
	                    msg : "숫자를 입력해주세요."
	                });
	            	
	            	$("#condAmtFr", tabObj).focus();
	            	return false;
	            }
	        	
	        }
	        
	        if(condAmtTo != ""){
	        	if(isNaN(condAmtFr)){
	            	$.csAlert({
	                    msg : "숫자를 입력해주세요."
	                });
	            	$("#condAmtTo", tabObj).focus();
	            	return false;
	            }
	        	
	        }
        
        }
        
        if(condAmtTo != "" && condAmtFr != ""){
        	if(condAmtFr > condAmtTo){
	        	$.csAlert({
	                msg : "최소 금액이 최대 금액보다 큽니다.."
	            });
	        	$("#condAmtTo", tabObj).focus();
	        	return false;
	        }
        }
        
        if(isEmpty(frscFgCdFr) == false || isEmpty(frscFgCdTo) == false){
            frscFrCdYn = "Y";
        }
        var amtUnit = $("#condAmtUnit", tabObj).val();
        //var orderYmdSeq = $("#condOrderYmdSeq option:selected", tabObj).val();
        var orderYmdSeq = '';

        // class 툴바 조회만 분류완료 사업 제한.
        // attr(보고·분류)은 TB_REPORT 분류가 없어도 사업목록이 나와야 하므로 강제하지 않음.
        var classOnlyYn = (!isAttrMode && searchDetl == 'detl') ? 'Y' : 'N';

        var condGovSub = $("#condGovSub option:selected", tabObj).val();
    	
        gridScrollPosition = $("#"+gridId, tabObj).closest(".ui-jqgrid-bdiv").scrollTop();
        
        saveReportParam.reportMstl = reportMstl;
        saveReportParam.reportCd = reportCd;
        saveReportParam.reportDetlCd = reportDetlCd;
        saveReportParam.govSub = govSub;
        saveReportParam.indiAttr = indiAttr;
        saveReportParam.advncProc = advncProc;
        saveReportParam.fisYear = fisYear;
        saveReportParam.bgtDgr = bgtDgr;
        saveReportParam.fisFgMstCd = fisFgMstCd;
        saveReportParam.fisFgCd = fisFgCd;
        saveReportParam.officeCd = officeCd;
        saveReportParam.deptRankFr = deptRankFr;
        saveReportParam.deptRankTo = deptRankTo;
        saveReportParam.teMngMokCdFr = teMngMokCdFr;
        saveReportParam.teMngMokCdTo = teMngMokCdTo;
        saveReportParam.frscFgCdFr = frscFgCdFr;
        saveReportParam.frscFgCdTo = frscFgCdTo;
        saveReportParam.frscFrCdYn = frscFrCdYn;
        saveReportParam.condAmtFr = condAmtFr;
        saveReportParam.condAmtTo = condAmtTo;
        saveReportParam.amtUnit = amtUnit;
        saveReportParam.orderYmdSeq = orderYmdSeq;
       
        $.csAjaxCall({
            url : "/budget/ajaxBudgetSelectNewDgrCompoList.do",
            data: {reportMstl : reportMstl,
            	   reportCd : reportCd,
                   reportDetlCd : reportDetlCd,
                   govSub : govSub,
                   indiAttr : indiAttr,
                   advncProc : advncProc,
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
                   bizNmKw : bizNmKw,
                   condAmtFr : condAmtFr,
                   condAmtTo : condAmtTo,
                   amtUnit : amtUnit,
                   orderYmdSeq : orderYmdSeq,
                   classOnlyYn : classOnlyYn,
                   viewMode : viewMode
            },
            async : true,
            callBack : doSearchCallBack
        });
    };
    
    //조회 버튼 클릭 이벤트
    $("#searchBtn", tabObj).click(function() {
    	searchDetl = ''; //상세조회 여부
        var reportCd = '';
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        var orderYmdSeq = $("#condOrderYmdSeq option:selected", tabObj).val();
        var condTeMngMokCdFr = $("#condTeMngMokCdFr option:selected", tabObj).val();
        var condTeMngMokCdTo = $("#condTeMngMokCdTo option:selected", tabObj).val();
        /*if(fisFgMstCd == "100" && isEmpty(officeCd) == true){
        	
        	if(isEmpty(condTeMngMokCdFr) == true && isEmpty(condTeMngMokCdTo) == true){
        		$.csAlert({
                    msg : "일반회계는 실국을 선택하셔야 합니다.",
                    callBack : function() {
                        $("#condOfficeCd", tabObj).focus();
                    }
                });
        		return;
        	}
        }*/
        
        gridScrollPosition = 0;
        
        doSearch();
    });
    
  //조회 버튼 클릭 상세 이벤트
    $("#searchDetlBtn", tabObj).click(function() {
    	searchDetl = 'detl';
        var reportCd = '';
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        var orderYmdSeq = $("#condOrderYmdSeq option:selected", tabObj).val();
        var condTeMngMokCdFr = $("#condTeMngMokCdFr option:selected", tabObj).val();
        var condTeMngMokCdTo = $("#condTeMngMokCdTo option:selected", tabObj).val();
        /*if(fisFgMstCd == "100" && isEmpty(officeCd) == true){
        	
        	if(isEmpty(condTeMngMokCdFr) == true && isEmpty(condTeMngMokCdTo) == true){
        		$.csAlert({
                    msg : "일반회계는 실국을 선택하셔야 합니다.",
                    callBack : function() {
                        $("#condOfficeCd", tabObj).focus();
                    }
                });
        		return;
        	}
        }*/
        
        gridScrollPosition = 0;
        
        doSearch();
    });
    
    
    $("#saveFileBtn", tabObj).click(function() {
    	
    	var reportMstl = '';
        var reportCd = '';
        var reportDetlCd = '';
        var govSub = '';
        var indiAttr = '';
        var advncProc = '';
        if(isAttrMode){
            indiAttr = $("#condIndiAttr option:selected", tabObj).val();
            advncProc = $("#condAdvncProc option:selected", tabObj).val();
        }
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
        var condAmtFr = $("#condAmtFr", tabObj).val().replace(/,/gi,'');
        var condAmtTo = $("#condAmtTo", tabObj).val().replace(/,/gi,'');
        var frscFrCdYn = "N";
        var bizNmKw = String($("#condBizNm", tabObj).val() || '')
            .replace(/[\s\u00A0\u3000]+/g, '')
            .toLowerCase();
        
    	reportMstl = $("#reportMstrSel option:selected", tabObj).val();
        reportCd = $("#reportCdSel option:selected", tabObj).val();
        reportDetlCd = $("#reportDetlCdSel option:selected", tabObj).val();
        govSub = $("#govSubSel option:selected", tabObj).val();
        
        if(condAmtFr != "" || condAmtFr != ""){
	        if(condAmtFr != ""){
	        	if(isNaN(condAmtFr)){
	            	$.csAlert({
	                    msg : "숫자를 입력해주세요."
	                });
	            	
	            	$("#condAmtFr", tabObj).focus();
	            	return false;
	            }
	        	
	        }
	        
	        if(condAmtTo != ""){
	        	if(isNaN(condAmtFr)){
	            	$.csAlert({
	                    msg : "숫자를 입력해주세요."
	                });
	            	$("#condAmtTo", tabObj).focus();
	            	return false;
	            }
	        	
	        }
        
        }
        
        if(condAmtTo != "" && condAmtFr != ""){
        	if(condAmtFr > condAmtTo){
	        	$.csAlert({
	                msg : "최소 금액이 최대 금액보다 큽니다.."
	            });
	        	$("#condAmtTo", tabObj).focus();
	        	return false;
	        }
        }
        
        if(isEmpty(frscFgCdFr) == false || isEmpty(frscFgCdTo) == false){
            frscFrCdYn = "Y";
        }
        var amtUnit = $("#condAmtUnit", tabObj).val();
        //var orderYmdSeq = $("#condOrderYmdSeq option:selected", tabObj).val();
        var orderYmdSeq = '';

        var condGovSub = $("#condGovSub option:selected", tabObj).val();
    	
        $.bcjisExcelAjaxCall({
            url : "/budget/ajaxBudgetSelectNewSaveFile.do"
          , data: {reportMstl : reportMstl,
		       	   reportCd : reportCd,
		           reportDetlCd : reportDetlCd,
		           govSub : govSub,
		           indiAttr : indiAttr,
		           advncProc : advncProc,
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
		           bizNmKw : bizNmKw,
		           condAmtFr : condAmtFr,
		           condAmtTo : condAmtTo,
		           amtUnit : amtUnit,
		           viewMode : viewMode,
		           orderYmdSeq : orderYmdSeq,
		           fileNm : "예산심사조서_집계표항목"
		    }
        });
    });

    // 초기 세팅
    var doCondInit = function(){
        if(!comboData){ return; }
        searchDetl = '';
        
        //대분류 selectBox 세팅
        $("#reportMstrSel", tabObj).csCreatCombo(comboData, {
            id : 'reportMstr',
            groupId : 'ALL',
            selectedValue : '',
            comboType : 'TS',
            comboTypeValue : ''
        });
        
        //중분류 selectBox 세팅
        var reportMstrSel = $("#reportMstrSel option:selected", tabObj).val();
        reportCdSelCreateCombo(reportMstrSel, '');
        
        //소분류 selectBox 세팅
        var reportCdSel = $("#reportCdSel option:selected", tabObj).val();
        reportDetlCdSelCreateCombo(reportCdSel, '');
        
        //국고보조 selectBox 세팅
        $("#govSubSel", tabObj).csCreatCombo(comboData, {
            id : 'govSub',
            groupId : 'ALL',
            selectedValue : '',
            comboType : 'TS',
            comboTypeValue : ''
        });
        
        //사전절차(분류항목) selectBox 세팅 — 설정 목록(RP015)과 동일하게 groupId ALL
        $("#condAdvncProc", tabObj).csCreatCombo(comboData, {
        	id : 'advncProc',
        	groupId : 'ALL',
        	selectedValue : '',
        	comboType : 'A',
        	comboTypeValue : ''
        });

        //보고항목(투자사업유형) selectBox 세팅 — 설정 목록(RP014)과 동일하게 groupId ALL
        $("#condIndiAttr", tabObj).csCreatCombo(comboData, {
        	id : 'indiAttr',
        	groupId : 'ALL',
        	selectedValue : '',
        	comboType : 'A',
        	comboTypeValue : ''
        });
        buildToolbarAdvncCombo();
        buildToolbarIndiAttrCombo();
        
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
        $("#condBizNm", tabObj).val("");
        $("#condOrderYmdSeq", tabObj).val("");
        $("#condAmtFr", tabObj).val("");
        $("#condAmtTo", tabObj).val("");
        
        saveReportParam.reportMstl = "";
        saveReportParam.reportCd = "";
        saveReportParam.reportDetlCd = "";
        saveReportParam.govSub = "";
        saveReportParam.indiAttr = "";
        saveReportParam.advncProc = "";
        saveReportParam.fisYear = "";
        saveReportParam.bgtDgr = "";
        saveReportParam.fisFgMstCd = "";
        saveReportParam.fisFgCd = "";
        saveReportParam.officeCd = "";
        saveReportParam.deptRankFr = "";
        saveReportParam.deptRankTo = "";
        saveReportParam.teMngMokCdFr = "";
        saveReportParam.teMngMokCdTo = "";
        saveReportParam.frscFgCdFr = "";
        saveReportParam.frscFgCdTo = "";
        saveReportParam.frscFrCdYn = "";
        saveReportParam.orderYmdSeq = "";

        // 하단 대~국고·툴바 조건 및 조회 결과 초기화
        if(isAttrMode){
            try{ resetToolbarAttrControls(); }catch(eRst){}
        }
        try{ clearCancelUndoMemory(); }catch(eMem){}
        gridScrollPosition = 0;
        try{
            var emptyData = {};
            emptyData[BCJIS_RETURN_CODE] = "SUCC";
            emptyData.dataList = [];
            doSearchCallBack(emptyData);
            $("#saveBtn", $("#"+tabId)).btnChangeState(false);
            if(!isAttrMode){
                $("#selectAllBtn", $("#"+tabId)).btnChangeState(false);
                $("#unSelectAllBtn", $("#"+tabId)).btnChangeState(false);
                $("#saveAllBtn", $("#"+tabId)).btnChangeState(false);
                $("#cancelClassBtn", $("#"+tabId)).btnChangeState(false);
                $("#undoCancelClassBtn", $("#"+tabId)).btnChangeState(false).hide();
            }
        }catch(eClr){
            try{
                if($("#"+gridId, tabObj).length && $("#"+gridId, tabObj)[0].grid){
                    $("#"+gridId, tabObj).jqGrid('clearGridData', true);
                }
            }catch(eClr2){}
        }
    };
    
    //초기화 버튼 클릭 이벤트
    $("#condInitBtn", tabObj).click(function() {
        doCondInit();
    });
    
    $("#condAmtInitBtn", tabObj).click(function() {
    	$("#condAmtFr", tabObj).val("");
        $("#condAmtTo", tabObj).val("");
    });
    
    //보고항목·분류항목 관리 — 상단 툴바(추가/저장/삭제) 통일
    var applyAttrCodeAdminUi = function(){
        if(isSysAdmin){
            $("#attrCodeToolbar").show();
            $("#attrCodeAddBtn,#attrCodeSaveBtn,#attrCodeDelBtn").show();
            $("input[id^=detlCdNm_14], input[id^=detlCdNm_15]").prop('readonly', false).prop('disabled', false).css('background-color', '#fff');
        }else{
            $("#attrCodeToolbar").hide();
            $("input[id^=detlCdNm_14], input[id^=detlCdNm_15]").prop('readonly', true).prop('disabled', true).css('background-color', '#f0f0f0');
        }
        // 탭 내부 중복 버튼은 숨김
        $(".attrCodeInnerBtn").hide();
    };
    window.applyAttrCodeAdminUi = applyAttrCodeAdminUi;

    var getAttrCodeActiveTab = function(){
        try{
            var idx = $("#attrCodeTabs").tabs("option","active");
            return (idx === null || typeof idx === 'undefined') ? 0 : idx;
        }catch(e){
            return 0;
        }
    };

    $("#attrCodeBtn", tabObj).click(function() {
        if($("#attrCodeTabIndi").children().length < 1){
            $("#indiAttrBody").appendTo("#attrCodeTabIndi");
            $("#advncProcBody").appendTo("#attrCodeTabClass");
        }
        if(!$("#attrCodeTabs").data("ui-tabs")){
            $("#attrCodeTabs").tabs();
        }
        applyAttrCodeAdminUi();
        $("#dialogDgrcompoAttrCodeDiv").dialog('open');
    });

    $("#attrCodeAddBtn").off("click.attrCode").on("click.attrCode", function(e){
        e.preventDefault();
        if(!isSysAdmin){ return; }
        if(getAttrCodeActiveTab() === 0){
            $("#addRowBtn").trigger("click");
        }else{
            $("#addRowAdvncProcBtn").trigger("click");
        }
    });
    $("#attrCodeSaveBtn").off("click.attrCode").on("click.attrCode", function(e){
        e.preventDefault();
        if(!isSysAdmin){ return; }
        if(getAttrCodeActiveTab() === 0){
            if(typeof window.dialogDgrcompoIndiAttrDoSave === 'function'){ window.dialogDgrcompoIndiAttrDoSave(); }
        }else{
            if(typeof window.dialogDgrcompoAdvncProcDoSave === 'function'){ window.dialogDgrcompoAdvncProcDoSave(); }
        }
    });
    $("#attrCodeDelBtn").off("click.attrCode").on("click.attrCode", function(e){
        e.preventDefault();
        if(!isSysAdmin){ return; }
        if(getAttrCodeActiveTab() === 0){
            $("#delRowBtn").trigger("click");
        }else{
            $("#delRowAdvncProcBtn").trigger("click");
        }
    });

    $("#dialogDgrcompoAttrCodeDiv").dialog({
        title: "투자사업유형·분류항목 설정",
        autoOpen: false,
        width: 'auto',
        height: 'auto',
        modal: true,
        resizable: true,
        open: function(event, ui){
            if(typeof doDialogDgrcompoIndiAttrSearch === 'function'){ doDialogDgrcompoIndiAttrSearch(); }
            if(typeof doDialogDgrcompoAdvncProcSearch === 'function'){ doDialogDgrcompoAdvncProcSearch(); }
            setTimeout(function(){ applyAttrCodeAdminUi(); }, 400);
        },
        close: function(event, ui){
            gridScrollPosition = 0;
            comboData = jQuery.csComboAjaxCall(comboParam);
            $("#condIndiAttr", tabObj).csCreatCombo(comboData, {
                id : 'indiAttr',
                groupId : 'ALL',
                selectedValue : '',
                comboType : 'A',
                comboTypeValue : ''
            });
            $("#condAdvncProc", tabObj).csCreatCombo(comboData, {
                id : 'advncProc',
                groupId : 'ALL',
                selectedValue : '',
                comboType : 'A',
                comboTypeValue : ''
            });
            buildToolbarAdvncCombo();
            buildToolbarIndiAttrCombo();
            loadAttrCodeNmAll();
            doSearch();
        },
        buttons: {
            "닫기": function(){
                $(this).dialog("close");
            }
        }
    });

    //보고항목 관리 클릭 이벤트 (legacy)
    $("#indiAttrBtn", tabObj).click(function() {
        $("#dialogDgrcompoIndiAttrCallBackFunction", $("#dialogDgrcompoIndiAttrDiv")).val("budgetSelectDialogIndiAttrCallBack");
    	$("#dialogDgrcompoIndiAttrDiv").dialog('open');
    });
    
    budgetSelectDialogIndiAttrCallBack = function(){
    	var reportCd = '';
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        var orderYmdSeq = $("#condOrderYmdSeq option:selected", tabObj).val();
        if(fisFgMstCd == "100" && isEmpty(officeCd) == true){
            
            return;
        }
        
        gridScrollPosition = 0;

        comboData = jQuery.csComboAjaxCall(comboParam);
        loadAttrCodeNmAll();

        doSearch();
    }

    //사전절차 관리 클릭 이벤트 (legacy)
    $("#advncProcBtn", tabObj).click(function() {
    	$("#dialogDgrcompoAdvncProcCallBackFunction", $("#dialogDgrcompoAdvncProcDiv")).val("budgetSelectDialogAdvncProcCallBack");
    	$("#dialogDgrcompoAdvncProcDiv").dialog('open');
    });
    
    budgetSelectDialogAdvncProcCallBack = function(){
    	var reportCd = '';
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        var orderYmdSeq = $("#condOrderYmdSeq option:selected", tabObj).val();
        if(fisFgMstCd == "100" && isEmpty(officeCd) == true){
            
            return;
        }
        
        gridScrollPosition = 0;

        comboData = jQuery.csComboAjaxCall(comboParam);
        loadAttrCodeNmAll();

        doSearch();
    }
    
    //선택된 데이터 가져오기  -> 수정된 데이터 가져오기로 변경
    var getSelectedData = function(gridObject, gridRows){
        var selectedDatas = [];
        var selectedData = {};
        var rowId;
        var rowData;
        var cnt = 0;
        // DOM rows 전체 순회 대신 jqGrid ID 목록 사용 (적용 저장 체감속도)
        var ids = [];
        try{ ids = gridObject.jqGrid('getDataIDs') || []; }catch(eIds){ ids = []; }
        if(ids.length < 1 && gridRows){
            for(var g = 0; g < gridRows.length; g++){
                if(gridRows[g] && gridRows[g].id){ ids.push(gridRows[g].id); }
            }
        }
        for(var i = 0; i < ids.length; i++) {
            rowId = ids[i];
            var localRow = null;
            try{ localRow = gridObject.jqGrid('getLocalRow', rowId); }catch(eL0){}
            rowData = localRow || gridObject.getRowData(rowId);
            if(!rowData){ continue; }

            if(rowData.changeFlag == "Y" && rowData.teBgtCompoId != "00000000000"){
                selectedData = {};
                selectedData["fisYear"] = rowData.fisYear;
                selectedData["bgtDgr"] = rowData.bgtDgr;
                selectedData["teBgtCompoId"] = rowData.teBgtCompoId;
                selectedData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
                selectedData["sel010Yn"] = rowData.sel010Yn;
                selectedData["sel020Yn"] = rowData.sel020Yn;
                selectedData["sel040Yn"] = rowData.sel040Yn;
                selectedData["sel050Yn"] = rowData.sel050Yn;
                selectedData["sel055Yn"] = rowData.sel055Yn;
                selectedData["sel060Yn"] = rowData.sel060Yn;
                selectedData["sel090Yn"] = rowData.sel090Yn;
                selectedData["dgrcompoNm"] = rowData.dgrcompoNm;
                selectedData["dgrcompoId"] = rowData.dgrcompoId;
                selectedData["checkYn031"] = $('#checkYn031_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                selectedData["checkYn032"] = $('#checkYn032_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                selectedData["checkYn033"] = $('#checkYn033_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                selectedData["checkYn034"] = $('#checkYn034_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                selectedData["checkYn035"] = $('#checkYn035_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                selectedData["reportMstr"] = firstCode(rowData.reportMstr);
                selectedData["reportCd"] = firstCode(rowData.reportCd);
                selectedData["reportDetlCd"] = firstCode(rowData.reportDetlCd);
                selectedData["govSub"] = firstCode(rowData.govSub);
                var curIndi = (localRow && localRow.indiAttr != null) ? localRow.indiAttr : (rowData.indiAttr || "");
                var curAdvn = (localRow && localRow.advncProc != null) ? localRow.advncProc : (rowData.advncProc || "");
                var curIndiOrg = (localRow && localRow.indiAttrOrg != null) ? localRow.indiAttrOrg : rowData.indiAttrOrg;
                if (isAttrMode) {
                    // 투자사업유형/분류항목 → TB_REPORT 컬럼만 (ATTR 테이블 스킵)
                    selectedData["indiAttrOrg"] = curIndiOrg || "";
                    if(isInvestReportRow(rowData) || isInvestReportRow(localRow)){
                        selectedData["indiAttr"] = curIndi;
                    } else {
                        selectedData["indiAttr"] = "";
                    }
                    selectedData["advncProc"] = curAdvn;
                    selectedData["indiAttrSkip"] = "Y";
                } else {
                    // 조서·집계 저장: 분류항목(advncProc) 유지
                    // 대분류=투자사업 → 투자사업유형(indiAttr)은 항상 미선택("")
                    selectedData["advncProc"] = curAdvn;
                    if(isInvestReportRow(rowData) || isInvestReportRow(localRow)){
                        selectedData["indiAttr"] = "";
                        selectedData["indiAttrOrg"] = curIndiOrg || curIndi;
                        // TB_REPORT.INDI_ATTR만 갱신 (ATTR 테이블 처리 생략 → 적용 저장 속도)
                        selectedData["indiAttrSkip"] = "Y";
                    } else {
                        selectedData["indiAttr"] = curIndi;
                        selectedData["indiAttrOrg"] = curIndiOrg;
                        selectedData["indiAttrSkip"] = "Y";
                    }
                }
                
                
                selectedDatas.push(selectedData);
                cnt++;
            }
        }
        
        return selectedDatas;
    };
    
    //국고 데이터 가져오기(변경시)
    var getSelectedData030 = function(gridObject, gridRows){
        var selectedDatas = [];
        var selectedData = {};
        var rowId;
        var rowData;
        var cnt = 0;

        var checkYn031 ="";
        var checkYn032 ="";
        var checkYn033 ="";
        var checkYn034 ="";
        var checkYn035 ="";
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = gridObject.getRowData(rowId);

            if(isEmpty(rowData.dgrcompoId) == false && rowData.teBgtCompoId != "00000000000" && rowData.changeFlag == "Y"){
                checkYn031 = $('#checkYn031_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn032 = $('#checkYn032_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn033 = $('#checkYn033_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn034 = $('#checkYn034_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                checkYn035 = $('#checkYn035_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                
                if(rowData.checkYn031 != checkYn031
                        || rowData.checkYn032 != checkYn032
                        || rowData.checkYn033 != checkYn033
                        || rowData.checkYn034 != checkYn034
                        || rowData.checkYn035 != checkYn035
                        ){
                    
                    selectedData = {};
                    selectedData["fisYear"] = rowData.fisYear;
                    selectedData["bgtDgr"] = rowData.bgtDgr;
                    selectedData["teBgtCompoId"] = rowData.teBgtCompoId;
                    selectedData["teBgtCompoSeq"] = rowData.teBgtCompoSeq;
                    selectedData["reportMstr"] = rowData.reportMstr;
                    selectedData["reportCd"] = rowData.reportCd;
                    selectedData["reportDetlCd"] = rowData.reportDetlCd;
                    selectedData["govSub"] = rowData.govSub;
                    selectedData["indiAttr"] = getIndiAttrCheckVal(rowData.dgrcompoId);
                    selectedData["advncProc"] = getAdvncProcCheckVal(rowData.dgrcompoId);
                    selectedData["checkYn031"] = $('#checkYn031_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                    selectedData["checkYn031Yn"] = rowData.checkYn031 != checkYn031 ? "Y" : "N";
                    selectedData["checkYn032"] = $('#checkYn032_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                    selectedData["checkYn032Yn"] = rowData.checkYn032 != checkYn032 ? "Y" : "N";
                    selectedData["checkYn033"] = $('#checkYn033_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                    selectedData["checkYn033Yn"] = rowData.checkYn033 != checkYn033 ? "Y" : "N";
                    selectedData["checkYn034"] = $('#checkYn034_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                    selectedData["checkYn034Yn"] = rowData.checkYn034 != checkYn034 ? "Y" : "N";
                    selectedData["checkYn035"] = $('#checkYn035_'+rowId, tabObj).is(':checked') == true ? "Y" : "N";
                    selectedData["checkYn035Yn"] = rowData.checkYn035 != checkYn035 ? "Y" : "N";
                    
                    selectedDatas.push(selectedData);
                    cnt++;
                }
            }             
        }
        
        return selectedDatas;
    };

    //보고항목 체크된 코드 가져오기
    var getIndiAttrCheckVal = function(dgrcompoId){
    	var $checks = $('input[id^="chkAttr_indi_'+dgrcompoId+'_"]:checked', tabObj);
    	if($checks.length > 0 || $('input[id^="chkAttr_indi_'+dgrcompoId+'_"]', tabObj).length > 0){
    		return $checks.map(function(){ return $(this).val(); }).get().join(',');
    	}
    	var $sel = $('#selIndiAttr_' + dgrcompoId, tabObj);
    	if($sel.length > 0){
    		return $sel.find('option:selected').map(function(){ return $(this).val(); }).get().join(',');
    	}
    	var itemList = comboData['indiAttr'] || [];
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
    
    //분류항목 체크된 코드 가져오기
    var getAdvncProcCheckVal = function(dgrcompoId){
    	var $checks = $('input[id^="chkAttr_advn_'+dgrcompoId+'_"]:checked', tabObj);
    	if($checks.length > 0 || $('input[id^="chkAttr_advn_'+dgrcompoId+'_"]', tabObj).length > 0){
    		return $checks.map(function(){ return $(this).val(); }).get().join(',');
    	}
    	var $sel = $('#selAdvncProc_' + dgrcompoId, tabObj);
    	if($sel.length > 0){
    		return $sel.find('option:selected').map(function(){ return $(this).val(); }).get().join(',');
    	}
    	var itemList = comboData['advncProc'] || [];
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
    
    // 취소 직전 분류 스냅샷 (탭 닫으면 삭제) — 상단 cancelUndoSnapshot / undoMemoryAction 사용

    var clearCancelUndoMemory = function(){
        cancelUndoSnapshot = null;
        $("#undoCancelClassBtn", tabObj).hide();
        try{ $("#undoCancelClassBtn", tabObj).btnChangeState(false); }catch(e){}
    };

    var showUndoCancelBtn = function(){
        $("#undoCancelClassBtn", tabObj).show();
        $("#undoCancelClassBtn", tabObj).btnChangeState(true);
    };

    bcjisCommMainObj["tabClose_"+tabId] = function(){
        clearCancelUndoMemory();
    };

    // 분류 모드: 저장 후 전체 재조회 없이 로컬 상태만 정리 (적용/취소/되돌리기 체감 속도)
    var clearChangeFlagsAfterClassSave = function(){
        var gridRows = $("#"+gridId, tabObj)[0].rows;
        for(var i = 0; i < gridRows.length; i++){
            var rowId = gridRows[i].id;
            if(!rowId){ continue; }
            var rowData = budgetSelectGrid.getRowData(rowId);
            // 개별사업(세세목): 적용 분류 반영 후 색상 플래그 동기화
            if(String(rowData.teBgtCompoId) != "00000000000" && rowData.changeFlag == "Y"){
                syncSelFlagsFromReportCd(rowId, rowData);
                budgetSelectGrid.jqGrid("setCell", rowId, "changeFlag", "N");
            }
            // 개별사업 + 상위(세부사업 등) 체크 모두 해제
            if(isSelYnChecked(rowData.selYn)){
                uncheckSelYnRow(rowId);
            }
        }
        // DOM에 남은 체크(부모 포함) 강제 해제
        try{
            $("#"+gridId+" td[aria-describedby='"+gridId+"_selYn'] input[type=checkbox]:checked", tabObj).prop("checked", false);
            $("#cb_"+gridId, tabObj).prop("checked", false);
            $("th#"+gridId+"_selYn input[type=checkbox]", tabObj).prop("checked", false);
        }catch(e){}
        applyBizRowColors();
    };

    //저장 callback
    var doSaveCallBack = function(data){
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });
            undoMemoryAction = null;
            return;
        }
        
        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function() {
                if(undoMemoryAction === "clear"){
                    clearCancelUndoMemory();
                }else if(undoMemoryAction === "keep"){
                    showUndoCancelBtn();
                }
                undoMemoryAction = null;
                // 그리드에 이미 반영됨 → 전체 재조회 생략 (적용 저장 체감속도)
                clearChangeFlagsAfterClassSave();
            }
        });
    };
    
    //저장 실행
    var doSave = function(params){
        if(params && params.confirmData != "Y"){
            return;
        }
        
        var selectedDatas = getSelectedData(budgetSelectGrid, $("#"+gridId, tabObj)[0].rows);
        
        if(selectedDatas.length < 1){
        	$.csAlert({
                msg : '수정된 데이터가 없습니다.'
            });
        	
        	return;
        }

        if(isAttrMode){
            var noNature = 0;
            for(var si = 0; si < selectedDatas.length; si++){
                if(isEmpty(selectedDatas[si].reportCd)){ noNature++; }
            }
            if(noNature > 0 && noNature === selectedDatas.length){
                $.csAlert({
                    msg : "조서·집계 항목(대/중/소분류)이 지정된 사업만 보고항목을 적용할 수 있습니다."
                });
                return;
            }
        }
        
        saveReportParam["saveReportDatas"] = selectedDatas;
        saveReportParam["viewMode"] = viewMode;
        saveReportParam["saveReportDatas030"] = getSelectedData030(budgetSelectGrid, $("#"+gridId, tabObj)[0].rows);
        console.log('testestsetet123123');
       $.csAjaxCall({
            url : "/budget/ajaxBudgetSelectNewSaveReport.do",
            data : saveReportParam,
            async : true,
            callBack : doSaveCallBack
        });
    };

    var runSaveWithConfirm = function(confirmMsg, memoryAction){
        if(checkCloseYn(saveReportParam) == false){
            return;
        }
        undoMemoryAction = memoryAction || "clear";
        $.csConfirm({
            msg : confirmMsg,
            callBack : doSave
        });
    };
    
    // ===== attr 툴바: 분류항목(다중) / 투자사업유형(단일) =====
    // (저장 버튼 없음 — 적용/삭제 시 즉시 저장)
    var isInvestMstrToolbarSelected = function(){
        var $opt = $("#reportMstrSel option:selected", tabObj);
        var v = $opt.val() || '';
        var nm = $.trim($opt.text() || '');
        if(nm.indexOf('투자') >= 0){ return true; }
        if(v && (String(v).toUpperCase().indexOf('I') === 0)){ return true; }
        return false;
    };

    var buildToolbarAdvncCombo = function(){
        if(!isAttrMode){ return; }
        var $sel = $("#toolbarAdvncProc", tabObj);
        $sel.empty();
        $sel.append('<option value=\"\">선택</option>');
        var list = (comboData && comboData['advncProc']) ? comboData['advncProc'] : [];
        for(var i = 0; i < list.length; i++){
            $sel.append('<option value=\"'+list[i].code+'\">'+list[i].codeNm+'</option>');
        }
    };

    var getToolbarAdvncCodes = function(){
        var codes = [];
        var v = $("#toolbarAdvncProc", tabObj).val() || '';
        if(v){ codes.push(v); }
        return codes;
    };

    // 기존 분류항목 + 신규 선택 항목 합치기(중복 제거, 순서 유지)
    var mergeAdvncCodes = function(existing, newCodes){
        var seen = {};
        var out = [];
        var add = function(code){
            code = $.trim(String(code == null ? '' : code));
            if(!code || code === 'null' || seen[code]){ return; }
            seen[code] = true;
            out.push(code);
        };
        if(existing != null && existing !== ''){
            var parts = String(existing).split(/[,|]/);
            for(var i = 0; i < parts.length; i++){ add(parts[i]); }
        }
        if(newCodes && newCodes.length){
            for(var j = 0; j < newCodes.length; j++){ add(newCodes[j]); }
        }
        return out.join(',');
    };

    var buildToolbarIndiAttrCombo = function(){
        if(!isAttrMode){ return; }
        var $sel = $("#toolbarIndiAttr", tabObj);
        $sel.empty();
        // 값 ""(빈값) = 미선택 적용(투자사업유형 해제). 라벨을 실제 동작과 일치시킨다.
        $sel.append('<option value=\"\">미선택</option>');
        var list = (comboData && comboData['indiAttr']) ? comboData['indiAttr'] : [];
        for(var i = 0; i < list.length; i++){
            $sel.append('<option value=\"'+list[i].code+'\">'+list[i].codeNm+'</option>');
        }
        refreshToolbarIndiAttrState();
    };

    var refreshToolbarIndiAttrState = function(){
        if(!isAttrMode){ return; }
        var enabled = isInvestMstrToolbarSelected();
        $("#toolbarIndiAttr", tabObj).prop('disabled', !enabled);
        if(!enabled){
            $("#toolbarIndiAttr", tabObj).val('');
        }
    };

    var resetToolbarAttrControls = function(){
        $("#toolbarAdvncProc", tabObj).val('');
        $("#toolbarIndiAttr", tabObj).val('');
        refreshToolbarIndiAttrState();
    };
    // 분류항목·투자사업유형 적용 → 체크 사업 반영 후 즉시 저장
    $("#attrApplyBtn", tabObj).click(function(e){
        e.preventDefault();
        if(!isAttrMode){ return; }
        if(!$("#"+gridId, tabObj).length || !$("#"+gridId, tabObj)[0].rows){
            $.csAlert({ msg : "조회된 사업이 없습니다." });
            return;
        }
        var advCodes = getToolbarAdvncCodes();
        var indiCode = $("#toolbarIndiAttr", tabObj).val() || '';
        var indiEnabled = !$("#toolbarIndiAttr", tabObj).prop('disabled');
        // 분류항목 미선택 + 투자유형 비활성 → 적용 불가
        // 투자유형 활성 시 '전체'(빈값)=미선택 적용 가능
        if(advCodes.length < 1 && !indiEnabled){
            $.csAlert({ msg : "적용할 분류항목을 선택하거나, 대분류를 투자사업으로 조회한 뒤 투자사업유형을 선택해 주십시오." });
            return;
        }
        var gridRows = $("#"+gridId, tabObj)[0].rows;
        var targets = [];
        for(var i = 0; i < gridRows.length; i++){
            var rowId = gridRows[i].id;
            if(!rowId){ continue; }
            var rowData = budgetSelectGrid.getRowData(rowId);
            if(String(rowData.teBgtCompoId) == "00000000000"){ continue; }
            if(isSelYnChecked(rowData.selYn)){
                targets.push(rowId);
            }
        }
        if(targets.length < 1){
            $.csAlert({ msg : "적용할 사업을 좌측에서 체크하여 주십시오." });
            return;
        }
        for(var t = 0; t < targets.length; t++){
            var rid = targets[t];
            var local = null;
            try{ local = budgetSelectGrid.jqGrid('getLocalRow', rid); }catch(e1){}
            var rowSnap = local || budgetSelectGrid.getRowData(rid);
            var isInvest = isInvestReportRow(rowSnap);
            // 분류항목: 기존 값 유지 + 신규 선택 추가(중복 제외)
            if(advCodes.length > 0){
                var curAdv = (local && local.advncProc != null) ? local.advncProc
                    : (rowSnap.advncProc || '');
                var merged = mergeAdvncCodes(curAdv, advCodes);
                budgetSelectGrid.jqGrid("setCell", rid, "advncProc", merged);
                if(local){ local.advncProc = merged; }
            }
            // 투자사업유형: 투자사업만 반영. 그 외 사업은 비움
            if(isInvest){
                if(indiEnabled){
                    budgetSelectGrid.jqGrid("setCell", rid, "indiAttr", indiCode);
                    if(local){ local.indiAttr = indiCode; }
                }
            } else {
                budgetSelectGrid.jqGrid("setCell", rid, "indiAttr", "");
                if(local){ local.indiAttr = ""; }
            }
            budgetSelectGrid.jqGrid("setCell", rid, "changeFlag", "Y");
            if(local){ local.changeFlag = "Y"; }
            refreshBizInfoCell(rid, local || budgetSelectGrid.getRowData(rid));
        }
        runSaveWithConfirm("선택한 " + targets.length + "건에 적용하고 저장하시겠습니까?", "clear");
    });

    // 사업정보 옆 '삭제' → 해당 분류항목만 제거 후 즉시 저장
    $(tabObj).on('click', 'a.bizAdvnDel', function(e){
        e.preventDefault();
        e.stopPropagation();
        if(!isAttrMode){ return; }
        var dgrId = $(this).attr('data-dgr');
        var delCode = $(this).attr('data-code');
        if(!dgrId || !delCode){ return; }
        if(!$("#"+gridId, tabObj).length || !$("#"+gridId, tabObj)[0].rows){
            return;
        }
        var gridRows = $("#"+gridId, tabObj)[0].rows;
        var targetRowId = null;
        for(var i = 0; i < gridRows.length; i++){
            var rid = gridRows[i].id;
            if(!rid){ continue; }
            var rd = budgetSelectGrid.getRowData(rid);
            if(String(rd.dgrcompoId) === String(dgrId)){
                targetRowId = rid;
                break;
            }
        }
        if(!targetRowId){
            $.csAlert({ msg : "대상 사업을 찾을 수 없습니다." });
            return;
        }
        var local = null;
        try{ local = budgetSelectGrid.jqGrid('getLocalRow', targetRowId); }catch(e2){}
        var cur = (local && local.advncProc != null) ? String(local.advncProc)
                : String((budgetSelectGrid.getRowData(targetRowId).advncProc) || '');
        var kept = [];
        var parts = cur.split(/[,|]/);
        for(var p = 0; p < parts.length; p++){
            var c = $.trim(parts[p]);
            if(!c || c === String(delCode)){ continue; }
            kept.push(c);
        }
        var nextVal = kept.join(',');
        budgetSelectGrid.jqGrid("setCell", targetRowId, "advncProc", nextVal);
        if(local){ local.advncProc = nextVal; local.changeFlag = "Y"; }
        budgetSelectGrid.jqGrid("setCell", targetRowId, "changeFlag", "Y");
        refreshBizInfoCell(targetRowId, local || budgetSelectGrid.getRowData(targetRowId));
        runSaveWithConfirm("선택한 분류항목을 삭제하고 저장하시겠습니까?", "clear");
    });

    // 조회지우기: 대~국고 초기화 후 상단필터 기준 재조회 (attr는 분류/투자유형도 초기화)
    $("#attrSearchClearBtn", tabObj).click(function(e){
        e.preventDefault();
        searchDetl = '';
        $("#reportMstrSel", tabObj).val('');
        reportCdSelCreateCombo('', '');
        reportDetlCdSelCreateCombo('', '');
        $("#govSubSel", tabObj).val('');
        if(isAttrMode){
            resetToolbarAttrControls();
        }
        doSearch();
    });

    //일괄적용 버튼 클릭 → 적용 후 바로 저장
    $("#saveAllBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        var gridRows = $("#"+gridId, tabObj)[0].rows;
        
        //체크된 갯수
        var cnt = 0;
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = budgetSelectGrid.getRowData(rowId);

            if(rowData.selYn == "Y" && rowData.teBgtCompoId != "00000000000"){
            	cnt++;
            }
        }
        
        if(cnt < 1){
            $.csAlert({
            	msg : "개별사업을 선택하여 주십시오."
            });
            
            return;
        }
        
        var reportMstr = $("#reportMstrSel option:selected", tabObj).val();		//대분류
        var reportCd = $("#reportCdSel option:selected", tabObj).val();			//중분류
        var reportDetlCd = $("#reportDetlCdSel option:selected", tabObj).val();	//소분류
        var govSub = $("#govSubSel option:selected", tabObj).val();				//국고
        
        if(isEmpty(reportMstr) != true){
        	if(isEmpty(reportCd) == true || isEmpty(reportDetlCd) == true){
        		$.csAlert({
                	msg : "소분류까지 선택해주세요."
                });
                
                return;
        	}
        }
        
        if(isEmpty(reportMstr) == true && isEmpty(reportCd) == true && isEmpty(reportDetlCd) == true && isEmpty(govSub) != true){
    		$.csAlert({
            	msg : "분류를 선택해주세요."
            });
            
            return;
        }
        
        if((reportDetlCd == '012' || reportDetlCd == '0292' || reportDetlCd == '301') && isEmpty(govSub) == true){
        	$.csAlert({
            	msg : "국고보조는 필수입니다."
            });
            
            return;
        }
        
        for(var i = 0; i < gridRows.length; i++) {
            rowId = gridRows[i].id;
            rowData = budgetSelectGrid.getRowData(rowId);

            if(rowData.selYn == "Y" && rowData.teBgtCompoId != "00000000000"){
            	//데이터 수정여부
            	budgetSelectGrid.jqGrid("setCell", rowId, "changeFlag", 'Y');
            	
            	//데이터가 있으면 데이터 입력 아니면 빈값 입력
            	if(isEmpty(reportMstr) != true){
            		budgetSelectGrid.jqGrid("setCell", rowId, "reportMstrNm", reportMstr);
            		budgetSelectGrid.jqGrid("setCell", rowId, "reportMstr", reportMstr);
            	}else{
            		budgetSelectGrid.jqGrid("setCell", rowId, "reportMstrNm", null);
            		budgetSelectGrid.jqGrid("setCell", rowId, "reportMstr", null);
            	}
            	
            	if(isEmpty(reportCd) != true){
            		budgetSelectGrid.jqGrid("setCell", rowId, "reportCdNm", reportCd);
            		budgetSelectGrid.jqGrid("setCell", rowId, "reportCd", reportCd);
            	}else{
            		budgetSelectGrid.jqGrid("setCell", rowId, "reportCdNm", null);
            		budgetSelectGrid.jqGrid("setCell", rowId, "reportCd", null);
            	}
            	
            	if(isEmpty(reportDetlCd) != true){
            		budgetSelectGrid.jqGrid("setCell", rowId, "reportDetlCdNm", reportDetlCd);
            		budgetSelectGrid.jqGrid("setCell", rowId, "reportDetlCd", reportDetlCd);
            	}else{
            		budgetSelectGrid.jqGrid("setCell", rowId, "reportDetlCdNm", null);
            		budgetSelectGrid.jqGrid("setCell", rowId, "reportDetlCd", null);
            	}
            	
            	if(isEmpty(govSub) != true){
            		budgetSelectGrid.jqGrid("setCell", rowId, "govSubNm", govSub);
            		budgetSelectGrid.jqGrid("setCell", rowId, "govSub", govSub);
            		budgetGovSubchangeYn(govSub, rowData.dgrcompoId);
            	}else{
            		budgetSelectGrid.jqGrid("setCell", rowId, "govSubNm", null);
            		budgetSelectGrid.jqGrid("setCell", rowId, "govSub", null);
            		budgetGovSubchangeYn(govSub, rowData.dgrcompoId);
            	}

            	// 대분류=투자사업 → 투자사업유형은 미선택("")으로 초기화
            	var applyMstrNm = $.trim($("#reportMstrSel option:selected", tabObj).text() || '');
            	var applyIsInvest = (applyMstrNm.indexOf('투자') >= 0)
            		|| (reportMstr && String(reportMstr).toUpperCase().indexOf('I') === 0)
            		|| (reportCd === '020');
            	if(applyIsInvest){
            		budgetSelectGrid.jqGrid("setCell", rowId, "indiAttr", "");
            		try{
            			var loc = budgetSelectGrid.jqGrid('getLocalRow', rowId);
            			if(loc){ loc.indiAttr = ""; loc.changeFlag = "Y"; }
            		}catch(eInv){}
            	}
            	
            }
        }

        // 적용과 동시에 저장 (취소 되돌리기 메모리는 해제)
        runSaveWithConfirm("선택한 사업에 조서·집계 항목을 적용하고 저장하시겠습니까?", "clear");
    });

    // 선택 사업의 조서·집계 항목 선택 취소 → 저장 + 되돌리기 가능
    $("#cancelClassBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        if(isAttrMode){
            return;
        }

        var gridRows = $("#"+gridId, tabObj)[0].rows;
        var targets = [];
        for(var i = 0; i < gridRows.length; i++) {
            var rowId = gridRows[i].id;
            var rowData = budgetSelectGrid.getRowData(rowId);
            if(rowData.selYn == "Y" && rowData.teBgtCompoId != "00000000000"){
                targets.push({ rowId: rowId, rowData: rowData });
            }
        }
        if(targets.length < 1){
            $.csAlert({
                msg : "취소할 개별사업을 선택(체크)하여 주십시오."
            });
            return;
        }

        $.csConfirm({
            msg : "선택한 " + targets.length + "건 사업의 조서·집계 항목 선택을 취소하고 저장하시겠습니까?",
            callBack : function(params){
                if(params && params.confirmData != "Y"){
                    return;
                }
                var snapItems = [];
                for(var i = 0; i < targets.length; i++) {
                    var rowId = targets[i].rowId;
                    var rowData = targets[i].rowData;
                    var dgrId = rowData.dgrcompoId;
                    snapItems.push({
                        teBgtCompoId : rowData.teBgtCompoId,
                        dgrcompoId : dgrId,
                        reportMstr : firstCode(rowData.reportMstr),
                        reportCd : firstCode(rowData.reportCd),
                        reportDetlCd : firstCode(rowData.reportDetlCd),
                        govSub : firstCode(rowData.govSub),
                        checkYn031 : $('#checkYn031_'+dgrId, tabObj).is(':checked') ? "Y" : "N",
                        checkYn032 : $('#checkYn032_'+dgrId, tabObj).is(':checked') ? "Y" : "N",
                        checkYn033 : $('#checkYn033_'+dgrId, tabObj).is(':checked') ? "Y" : "N",
                        checkYn034 : $('#checkYn034_'+dgrId, tabObj).is(':checked') ? "Y" : "N",
                        checkYn035 : $('#checkYn035_'+dgrId, tabObj).is(':checked') ? "Y" : "N"
                    });
                    budgetSelectGrid.jqGrid("setCell", rowId, "changeFlag", "Y");
                    budgetSelectGrid.jqGrid("setCell", rowId, "reportMstrNm", null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "reportMstr", null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "reportCdNm", null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "reportCd", null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "reportDetlCdNm", null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "reportDetlCd", null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "govSubNm", null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "govSub", null);
                    budgetGovSubchangeYn("", dgrId);
                }
                cancelUndoSnapshot = { items: snapItems };
                if(checkCloseYn(saveReportParam) == false){
                    return;
                }
                undoMemoryAction = "keep";
                doSave({ confirmData: "Y" });
            }
        });
    });

    // 직전 취소 되돌리기 → 저장
    $("#undoCancelClassBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        if(isAttrMode || !cancelUndoSnapshot || !cancelUndoSnapshot.items || cancelUndoSnapshot.items.length < 1){
            $.csAlert({ msg : "되돌릴 취소 내역이 없습니다." });
            return;
        }

        $.csConfirm({
            msg : "직전 취소 " + cancelUndoSnapshot.items.length + "건을 취소 전 상태로 되돌리고 저장하시겠습니까?",
            callBack : function(params){
                if(params && params.confirmData != "Y"){
                    return;
                }
                var gridRows = $("#"+gridId, tabObj)[0].rows;
                var byTeId = {};
                for(var i = 0; i < cancelUndoSnapshot.items.length; i++){
                    byTeId[cancelUndoSnapshot.items[i].teBgtCompoId] = cancelUndoSnapshot.items[i];
                }
                var restored = 0;
                for(var i = 0; i < gridRows.length; i++){
                    var rowId = gridRows[i].id;
                    var rowData = budgetSelectGrid.getRowData(rowId);
                    if(rowData.teBgtCompoId == "00000000000"){
                        continue;
                    }
                    var snap = byTeId[rowData.teBgtCompoId];
                    if(!snap){
                        continue;
                    }
                    budgetSelectGrid.jqGrid("setCell", rowId, "changeFlag", "Y");
                    budgetSelectGrid.jqGrid("setCell", rowId, "reportMstrNm", snap.reportMstr || null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "reportMstr", snap.reportMstr || null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "reportCdNm", snap.reportCd || null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "reportCd", snap.reportCd || null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "reportDetlCdNm", snap.reportDetlCd || null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "reportDetlCd", snap.reportDetlCd || null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "govSubNm", snap.govSub || null);
                    budgetSelectGrid.jqGrid("setCell", rowId, "govSub", snap.govSub || null);
                    if(isEmpty(snap.govSub) != true){
                        budgetGovSubchangeYn(snap.govSub, rowData.dgrcompoId);
                    }else{
                        // 스냅샷의 국고 체크 상태 복원
                        budgetGovSubchangeYn("", rowData.dgrcompoId);
                        var dgrId = rowData.dgrcompoId;
                        if(snap.checkYn031 == "Y"){ $('#checkYn031_'+dgrId, tabObj).prop('checked', true); }
                        if(snap.checkYn032 == "Y"){ $('#checkYn032_'+dgrId, tabObj).prop('checked', true); }
                        if(snap.checkYn033 == "Y"){ $('#checkYn033_'+dgrId, tabObj).prop('checked', true); }
                        if(snap.checkYn034 == "Y"){ $('#checkYn034_'+dgrId, tabObj).prop('checked', true); }
                        if(snap.checkYn035 == "Y"){ $('#checkYn035_'+dgrId, tabObj).prop('checked', true); }
                    }
                    restored++;
                }
                if(restored < 1){
                    $.csAlert({ msg : "되돌릴 대상 사업이 현재 목록에 없습니다. 동일 조건으로 조회 후 다시 시도해 주십시오." });
                    return;
                }
                if(checkCloseYn(saveReportParam) == false){
                    return;
                }
                undoMemoryAction = "clear";
                doSave({ confirmData: "Y" });
            }
        });
    });
    
    //국고 데이터 수정시 체크박스 수정
    var budgetGovSubchangeYn = function(fg, id){
    	
    	$('#checkYn' + fg + '_' + id, tabObj).prop('checked', true);
    	
    	if(fg == '031'){
            $('#checkYn032'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn033'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn034'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn035'+'_'+id, tabObj).removeAttr('checked');
        }else if(fg == '032'){
            $('#checkYn031'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn033'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn034'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn035'+'_'+id, tabObj).removeAttr('checked');
        }else if(fg == '033'){
            $('#checkYn031'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn032'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn034'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn035'+'_'+id, tabObj).removeAttr('checked');
        }else if(fg == '034'){
            $('#checkYn031'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn032'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn033'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn035'+'_'+id, tabObj).removeAttr('checked');
        }else if(fg == '035'){
        	$('#checkYn031'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn032'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn033'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn034'+'_'+id, tabObj).removeAttr('checked');
        }else{
        	$('#checkYn031'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn032'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn033'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn034'+'_'+id, tabObj).removeAttr('checked');
            $('#checkYn035'+'_'+id, tabObj).removeAttr('checked');
        }
    }
    
  //대분류 데이터 변경시
    var doChangeReportMstrSel = function(){
        var reportMstrSel = $("#reportMstrSel option:selected", tabObj).val();
        reportCdSelCreateCombo(reportMstrSel, '');
        doChangeReportCdSel();
        refreshToolbarIndiAttrState();
    };
    
    //중분류 데이터 변경시
    var doChangeReportCdSel = function(){
    	var reportCdSel = $("#reportCdSel option:selected", tabObj).val();
    	reportDetlCdSelCreateCombo(reportCdSel, '');
    	
    };
    
    //회계년도 변경시 이벤트
    var doChangeCondFisYear = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');		//예산차수 변경		재구성
        condFisFgMstCdCreateCombo(fisYear, '');	//회계구분 마스터	재구성
        doChageCondBgtDgr();					//예산차수 번경시 실행
        doChageCondFisFgMstCd();				//회계구분 변경시 실행
        
        condFrscFgCdFrCreateCombo(fisYear, '');	//재원구분 시작	재구성
        condFrscFgCdToCreateCombo(fisYear, '');	//재원구분 종료 재구성
    };
    
    //예산차수 변경시 이벤트
    var doChageCondBgtDgr = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();		//회계년도
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();		//예산차수
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');				//실국 selectbox 재구성
        doChangeCondOfficeCd();												//실국 변경이벤트
        
        condTeMngMokCdFrCreateCombo(fisYear + '_' + bgtDgr, '');			//통계목시작 재구성
        condTeMngMokCdToCreateCombo(fisYear + '_' + bgtDgr, '');			//통계목 종료 재구성
        
        condOrderYmdSeqCreateCombo(fisYear + '_' + bgtDgr, '');			//지시일시 재구성
    };
    
    //회계구분 수정이벤트
    var doChageCondFisFgMstCd = function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        condFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');	//회계구분 재구성
    };
    
    //변경이벤트
    var doChangeCondOfficeCd = function(){
        $("#condDeptCdFr", tabObj).val("");
        $("#condDeptNmFr", tabObj).val("");
        $("#condDeptRankFr", tabObj).val("");
        $("#condDeptCdTo", tabObj).val("");
        $("#condDeptNmTo", tabObj).val("");
        $("#condDeptRankTo", tabObj).val("");
    };
    
    //대분류 변경 이벤트
    $("#reportMstrSel", tabObj).change(function(){
    	doChangeReportMstrSel();	
    });
    
    //중뷴류 변경 이벤트
    $("#reportCdSel", tabObj).change(function(){
    	doChangeReportCdSel();
    });
    
    //회계년도 변경 이벤트
    $("#condFisYear", tabObj).change(function(){
        doChangeCondFisYear();
    });
    
    //예산차수 변경 이벤트
    $("#condBgtDgr", tabObj).change(function(){
        doChageCondBgtDgr();
    });
    
    //회계구분 변경 이벤트
    $("#condFisFgMstCd", tabObj).change(function(){
        doChageCondFisFgMstCd();
    });
    
    //실국 변경 이벤트
    $("#condOfficeCd", tabObj).change(function(){
        doChangeCondOfficeCd();
    });
    
    //통계목 시작 변경 이벤트
    $("#condTeMngMokCdFr", tabObj).change(function(){
        $("#condTeMngMokCdTo", tabObj).val($("#condTeMngMokCdFr option:selected", tabObj).val());
    });
    
    //재원구분 변경 이벤트
    $("#condFrscFgCdFr", tabObj).change(function(){
        $("#condFrscFgCdTo", tabObj).val($("#condFrscFgCdFr option:selected", tabObj).val());
    });

    // 사업명 입력 후 Enter → 조회
    $("#condBizNm", tabObj).keydown(function(e){
        if(e.keyCode === 13){
            e.preventDefault();
            $("#searchBtn", tabObj).click();
        }
    });
    
    //부서선택 dialog 이벤트
    var openDialogBgtDeptSelt = function(seltFg){

        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        var officeCd = $("#condOfficeCd option:selected", tabObj).val();
        
        $("#dialogDgrDeptSeltCallBackFunction", $("#dialogDgrDeptSeltDiv")).val("budgetSelectDialogDgrDeptSeltCallBack");
        $("#dialogDgrDeptSeltFisYear", $("#dialogDgrDeptSeltDiv")).val(fisYear);
        $("#dialogDgrDeptSeltBgtDgr", $("#dialogDgrDeptSeltDiv")).val(bgtDgr);
        $("#dialogDgrDeptSeltOfficeCd", $("#dialogDgrDeptSeltDiv")).val(officeCd);
        $("#dialogDgrDeptSeltSeltFg", $("#dialogDgrDeptSeltDiv")).val(seltFg);
        $("#dialogDgrDeptSeltReportCd", $("#dialogDgrDeptSeltDiv")).val("");
        $("#dialogDgrDeptSeltUserDeptYn", $("#dialogDgrDeptSeltDiv")).val("");
        
        $("#dialogDgrDeptSeltDiv").dialog('open');
    };
    
    //부서선택 callback 이벤트
    budgetSelectDialogDgrDeptSeltCallBack = function(param){
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
    
    //부서 시작 클릭 이벤트
    $("#openDialogBgtDeptBtnFr", tabObj).click(function(){
        openDialogBgtDeptSelt(1);
    });
    
    //부서 종료 클릭 이벤트
    $("#openDialogBgtDeptBtnTo", tabObj).click(function(){
        openDialogBgtDeptSelt(2);
    });
    
    //전체선택 클릭 이벤트
    $("#selectAllBtn", tabObj).click(function(){
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $("#selectAllBtn", tabObj).hide();
        $("#unSelectAllBtn", tabObj).show();
        setGridCheckedAll(budgetSelectGrid, $("#"+gridId, tabObj)[0].rows, "Y"); 
    });
    
    //전체해제 클릭 이벤트
    $("#unSelectAllBtn", tabObj).click(function(){
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $("#unSelectAllBtn", tabObj).hide();
        $("#selectAllBtn", tabObj).show();
        setGridCheckedAll(budgetSelectGrid, $("#"+gridId, tabObj)[0].rows, "N");
    });
    
    //공통코드 파라미터
    var comboParam = [
                      {id : "reportCd", codeId : "RP011"},				//중분류
                      {id : "reportDetlCd", codeId : "RP012"},			//소분류
                      {id : "reportMstr", codeId : "RP010"},			//대분류
                      {id : "govSub", codeId : "RP013"},				//국고
                      {id : "indiAttr", codeId : "RP014"},				//보고항목
                      {id : "advncProc", codeId : "RP015"},				//사전절차
                      {id : "fisYear", subQueryId : "FisYear"},			//회계년도
                      {id : "bgtDgr", subQueryId : "BgtDgr"},			//예산차수
                      {id : "fisFgMstCd", subQueryId : "FisFgMstCd"},	//회계구분 마스터
                      {id : "fisFgCd", subQueryId : "FisFgCd"},			//회계구분 서브
                      {id : "officeCd", subQueryId : "OfficeCd"},		//실국
                      {id : "teMngMokCd", subQueryId : "TeMngMokCd"},	//통계목
                      {id : "frscFgCd", subQueryId : "FrscFgCd"},		//재원구분
                      {id : "orderYmdSeq", subQueryId : "OrderYmdSeq"}	//지시일자
                      
                    ];

    // 콤보 로드를 다음 틱으로 미뤄 탭 화면이 먼저 그려지게 함 (오픈 체감속도)
    
    //대분류 데이터에 따라 중분류 새로 세팅
    var reportCdSelCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
    	$("#reportCdSel", tabObj).csCreatCombo(comboData
    			, {id: 'reportCd'
    				, groupId: groupId
    				, selectedValue: selectedValue
    				, comboType: 'TS'
    					, comboTypeValue: ''
    	}
    	);
    };
    
    //중분류 데이터에 따라 소분류 새로 세팅
    var reportDetlCdSelCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
    	$("#reportDetlCdSel", tabObj).csCreatCombo(comboData
    			, {id: 'reportDetlCd'
    				, groupId: groupId
    				, selectedValue: selectedValue
    				, comboType: 'TS'
    					, comboTypeValue: ''
    	}
    	);
    };
    
    //예산차수 설정
    var condBgtDgrCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
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
    	if(!comboData){ return; }
        $("#condFisFgMstCd", tabObj).csCreatCombo(comboData
                , {id: 'fisFgMstCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: ''
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condFisFgCdCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
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
    	if(!comboData){ return; }
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
    	if(!comboData){ return; }
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
    	if(!comboData){ return; }
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
    	if(!comboData){ return; }
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
    	if(!comboData){ return; }
        $("#condFrscFgCdTo", tabObj).csCreatCombo(comboData
                , {id: 'frscFgCd'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'A'
                  , comboTypeValue: ''
                  }
        );
    };
    
    var condOrderYmdSeqCreateCombo = function(groupId, selectedValue){
    	if(!comboData){ return; }
        $("#condOrderYmdSeq", tabObj).csCreatCombo(comboData
                , {id: 'orderYmdSeq'
                  , groupId: groupId
                  , selectedValue: selectedValue
                  , comboType: 'S'
                  , comboTypeValue: ''
                  }
        );
    };
    
    budgetSelectDialogReport070OrderYmdModifyCallBackFunction = function(param){        
        var comboOrderParam = [
                          {id : "orderYmdSeq", subQueryId : "OrderYmdSeq"}
                        ];

        var comboOrderData = jQuery.csComboAjaxCall(comboOrderParam);
        
        if(comboData && comboOrderData){
            comboData.orderYmdSeq = comboOrderData.orderYmdSeq;
        }

        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOrderYmdSeqCreateCombo(fisYear + '_' + bgtDgr, '');
    };
    
    $("#modifyOrderYmdSeqBtn", tabObj).click(function() {
        if($(this).attr("enabledYn") != "Y"){
            return;
        }
        
        $("#dialogReport070OrderYmdModifyCallBackFunction", $("#dialogReport070OrderYmdModifyDiv")).val("budgetSelectDialogReport070OrderYmdModifyCallBackFunction");
        $("#dialogReport070OrderYmdModifyFisYear", $("#dialogReport070OrderYmdModifyDiv")).val($("#condFisYear option:selected", tabObj).val());
        $("#dialogReport070OrderYmdModifyBgtDgr", $("#dialogReport070OrderYmdModifyDiv")).val($("#condBgtDgr option:selected", tabObj).val());

        $("#dialogReport070OrderYmdModifyDiv").dialog('open');
    });
    
    // 화면 골격 표시 후 콤보 로드·조건초기화 (메뉴 오픈 체감속도 개선)
    setTimeout(function(){
        comboData = jQuery.csComboAjaxCall(comboParam);
        doCondInit();
        loadAttrCodeNmAll();
    }, 0);
});

function onChangeFlag(dgrcompoId, obj){
	var tabId = _budgetSelectTabId;
    var tabObj = $("#"+tabId);
    var gridId = "BUDGET_SELECT_NEW_GRD_" + tabId;
	var $grd = $("#"+gridId, tabObj);
	if($grd.length < 1 || !$grd[0].rows){
		return;
	}
	var gridRows = $grd[0].rows;
	var budgetSelectGrid = $grd;
	var rowId;
    var rowData;
    var viewMode = tabObj.find("[data-view-mode]").attr("data-view-mode")
        || ((typeof _budgetSelectViewMode !== 'undefined') ? _budgetSelectViewMode : 'class');
    
	for(var i = 0; i < gridRows.length; i++) {
        rowId = gridRows[i].id;
        rowData = budgetSelectGrid.getRowData(rowId);

        if(rowData.dgrcompoId == dgrcompoId){

        	var reportDetlCd = rowData.reportDetlCd;
        	// attr(보고·분류) 화면에서는 조서 분류 없이 보고/분류항목 선택 가능
        	if(viewMode !== 'attr' && isEmpty(reportDetlCd) == true){
        		if(obj){
        			if($(obj).is(':checked')){
        				$(obj).prop('checked', false);
        			}else{
        				$(obj).prop('checked', true);
        			}
        		}
        		$.csAlert({
                    msg : "분류를 먼저 적용하여 주세요."
                });
        		
        		return;
        	}
        	
        	//데이터 수정여부
        	budgetSelectGrid.jqGrid("setCell", rowId, "changeFlag", 'Y');
        }
	}
}