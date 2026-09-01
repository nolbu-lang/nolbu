$(document).ready(function() {
    var tabId = _rlkCheckTabId;
    var tabObj = $("#"+tabId);

    var deptGrid = $("#RLK_CHECK_DEPT_GRD", tabObj);
    var compoGrid = $("#RLK_CHECK_DGRCOMPO_GRD", tabObj);

    var deptColNames = ['', '실국', '부서명', '세부사업수',
                         'dgrcompoId', 'upDgrcompoId', 'level', 'isLeaf', 'expanded', 'loaded',
                         'fisYear', 'bgtDgr', 'officeCd', 'deptCd', 'dbizCnt'
                        ];

    var deptColModel = [
                        {name : 'radioSel', index : 'radioSel', width : 30, align : 'center', sortable : false, fixed : true,
                            formatter : function(cellValue, options, rowObject){
                                return '<input type="radio" name="rlkCheckDeptRadio" />';
                            }
                        },
                        {name : 'officeNm', index : 'officeNm', width : 120, sortable : false, fixed : true, align : 'left'},
                        {name : 'deptNm', index : 'deptNm', width : 160, sortable : false, fixed : true, align : 'left'},
                        {name : 'dbizCnt', index : 'dbizCnt', width : 70, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key : true},
                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, hidden : true},
                        {name : 'level', index : 'level', width : 0, sortable : false, hidden : true},
                        {name : 'isLeaf', index : 'isLeaf', width : 0, sortable : false, hidden : true},
                        {name : 'expanded', index : 'expanded', width : 0, sortable : false, hidden : true},
                        {name : 'loaded', index : 'loaded', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'officeCd', index : 'officeCd', width : 0, sortable : false, hidden : true},
                        {name : 'deptCd', index : 'deptCd', width : 0, sortable : false, hidden : true},
                        {name : 'dbizCnt', index : 'dbizCnt', width : 0, sortable : false, hidden : true}
                       ];

    var compoColNames = ['', '구분(회계-실국-부서-세부사업-개별사업)', '기정액', '증감액', '예산액',
                          'dgrcompoId', 'upDgrcompoId', 'fisYear', 'bgtDgr', 'dgrLevel', 'deptCd', 'dbizCd', 'teBgtCompoId', 'teBgtCompoSeq', 'isLeaf', 'reportCnt'
                         ];

    var reportCntFormatter = function(cellValue, options, rowObject){
        var rVal = rowObject.dgrcompoNm;
        if(Number(rowObject.reportCnt) > 0){
            rVal = '<span class="ui-icon ui-icon-pencil" style="display:inline-block;vertical-align:middle;"></span>' + rVal;
        }

        return rVal;
    };

    var compoColModel = [
                        {name : 'selYn', index : 'selYn', width : 30, align : 'center', sortable : false, fixed : true, formatter : 'checkbox', editoptions : {value : 'Y:N'}, formatoptions : {disabled : false}},
                        {name : 'dgrcompoNm', index : 'dgrcompoNm', width : 340, sortable : false, fixed : true, align : 'left', formatter : reportCntFormatter},
                        {name : 'preAmt', index : 'preAmt', width : 90, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'demandDiffAmt', index : 'demandDiffAmt', width : 90, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'demandBgtAmt', index : 'demandBgtAmt', width : 90, sortable : false, fixed : true, align : 'right', formatter : 'integer', formatoptions : {thousandsSeparator : ","}},
                        {name : 'dgrcompoId', index : 'dgrcompoId', width : 0, sortable : false, hidden : true, key : true},
                        {name : 'upDgrcompoId', index : 'upDgrcompoId', width : 0, sortable : false, hidden : true},
                        {name : 'fisYear', index : 'fisYear', width : 0, sortable : false, hidden : true},
                        {name : 'bgtDgr', index : 'bgtDgr', width : 0, sortable : false, hidden : true},
                        {name : 'dgrLevel', index : 'dgrLevel', width : 0, sortable : false, hidden : true},
                        {name : 'deptCd', index : 'deptCd', width : 0, sortable : false, hidden : true},
                        {name : 'dbizCd', index : 'dbizCd', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoId', index : 'teBgtCompoId', width : 0, sortable : false, hidden : true},
                        {name : 'teBgtCompoSeq', index : 'teBgtCompoSeq', width : 0, sortable : false, hidden : true},
                        {name : 'isLeaf', index : 'isLeaf', width : 0, sortable : false, hidden : true},
                        {name : 'reportCnt', index : 'reportCnt', width : 0, sortable : false, hidden : true}
                       ];

    var getGridHeight = function(){
        var height = $("#mainCenter", tabObj).height() - 90 > 200 ? $("#mainCenter", tabObj).height() - 90 : 200;
        if(isEmpty($("#RLK_CHECK_DEPT_GRD", tabObj)) == false){
            $("#RLK_CHECK_DEPT_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", height - 30);
        }
        if(isEmpty($("#RLK_CHECK_DGRCOMPO_GRD", tabObj)) == false){
            $("#RLK_CHECK_DGRCOMPO_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", height - 30);
        }
        return height;
    };

    var mainBodyResize = function(){
        $("#subMainBody", tabObj).width($("#mainCenter", tabObj).width());
        $("#subMainBody", tabObj).layout().resizeAll();
    };

    var subMainBodyResize = function(){
        if(isEmpty($("#RLK_CHECK_DEPT_GRD", tabObj)) == false){
            $("#RLK_CHECK_DEPT_GRD", tabObj).setGridHeight(getGridHeight());
            $("#RLK_CHECK_DEPT_GRD", tabObj).setGridWidth($("#subMainWest", tabObj).width());
        }
        if(isEmpty($("#RLK_CHECK_DGRCOMPO_GRD", tabObj)) == false){
            $("#RLK_CHECK_DGRCOMPO_GRD", tabObj).setGridHeight(getGridHeight());
            $("#RLK_CHECK_DGRCOMPO_GRD", tabObj).setGridWidth($("#subMainCenter", tabObj).width());
        }
    };

    bcjisCommMainObj["mainBodyResize_"+tabId] = mainBodyResize;

    $("#mainBody", tabObj).layout({
        north__size : 150,
        center__onresize : mainBodyResize
    });

    $("#subMainBody", tabObj).layout({
        west__size : "35%",
        center__onresize : subMainBodyResize
    });

    var getSearchParam = function(){
        return {
            fisYear : $("#condFisYear option:selected", tabObj).val(),
            bgtDgr : $("#condBgtDgr option:selected", tabObj).val(),
            fisFgMstCd : $("#condFisFgMstCd option:selected", tabObj).val(),
            fisFgCd : $("#condFisFgCd option:selected", tabObj).val(),
            officeCd : $("#condOfficeCd option:selected", tabObj).val(),
            srchNm : $("#condSrchNm", tabObj).val(),
            amtUnit : $("#condAmtUnit", tabObj).val()
        };
    };

    var doSearchDeptCallBack = function(data){
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });

            return;
        }

        $("#RLK_CHECK_DEPT_GRD", tabObj).GridUnload();
        deptGrid = $("#RLK_CHECK_DEPT_GRD", tabObj);
        deptGrid.csTreeGrid({
            datastr : data,
            height : getGridHeight(),
            colNames : deptColNames,
            colModel : deptColModel,
            ExpandColumn : "deptNm",
            jsonReader : {
                repeatitems : false,
                root : "dataList"
            }
        });

        $("#RLK_CHECK_DEPT_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", getGridHeight() - 30);

        data = null;
    };

    var doSearchDept = function(){
        $.csAjaxCall({
            url : "/budget/ajaxRlkCheckDeptList.do",
            data : getSearchParam(),
            async : true,
            callBack : doSearchDeptCallBack
        });
    };

    var doSearchCompoCallBack = function(data){
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });

            return;
        }

        $("#RLK_CHECK_DGRCOMPO_GRD", tabObj).jqGrid('GridUnload');
        compoGrid = $("#RLK_CHECK_DGRCOMPO_GRD", tabObj);
        compoGrid.csTreeGrid({
            datastr : data,
            height : getGridHeight(),
            colNames : compoColNames,
            colModel : compoColModel,
            ExpandColumn : "dgrcompoNm",
            jsonReader : {
                repeatitems : false,
                root : "dataList"
            },
            loadComplete : function(){
                var iColSelYn = getColumnIndexByName($(this), 'selYn');
                var rows = this.rows;
                for(var i = 0; i < rows.length; i++){
                    $(rows[i].cells[iColSelYn]).click(function(e){
                        setTreeGridChecked(e, compoGrid, $("#RLK_CHECK_DGRCOMPO_GRD", tabObj)[0].rows, 'dgrLevel');
                    });
                }
            }
        });

        $("#RLK_CHECK_DGRCOMPO_GRD", tabObj).closest(".ui-jqgrid-bdiv").css("max-height", getGridHeight() - 30);

        $("#selectAllBtn", tabObj).btnChangeState(true);
        $("#unSelectAllBtn", tabObj).btnChangeState(true);
        $("#applyDatasBtn", tabObj).btnChangeState(true);
        $("#selectAllBtn", tabObj).show();
        $("#unSelectAllBtn", tabObj).hide();

        data = null;
    };

    var doSearchCompo = function(){
        $.csAjaxCall({
            url : "/budget/ajaxRlkCheckDgrcompoList.do",
            data : getSearchParam(),
            async : true,
            callBack : doSearchCompoCallBack
        });
    };

    var doSearch = function(){
        doSearchDept();
        doSearchCompo();
    };

    var doCondInit = function(){
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
    };

    var comboParam = [
                      {id : "fisYear", subQueryId : "FisYear"},
                      {id : "bgtDgr", subQueryId : "BgtDgr"},
                      {id : "fisFgMstCd", subQueryId : "FisFgMstCd"},
                      {id : "fisFgCd", subQueryId : "FisFgCd"},
                      {id : "officeCd", subQueryId : "OfficeCd"}
                    ];

    var comboData = jQuery.csComboAjaxCall(comboParam);

    var condBgtDgrCreateCombo = function(groupId, selectedValue){
        $("#condBgtDgr", tabObj).csCreatCombo(comboData
                , {id : 'bgtDgr'
                  , groupId : groupId
                  , selectedValue : selectedValue
                  , comboType : ''
                  , comboTypeValue : ''
                  }
        );
    };

    var condFisFgMstCdCreateCombo = function(groupId, selectedValue){
        $("#condFisFgMstCd", tabObj).csCreatCombo(comboData
                , {id : 'fisFgMstCd'
                  , groupId : groupId
                  , selectedValue : selectedValue
                  , comboType : ''
                  , comboTypeValue : ''
                  }
        );
    };

    var condFisFgCdCreateCombo = function(groupId, selectedValue){
        $("#condFisFgCd", tabObj).csCreatCombo(comboData
                , {id : 'fisFgCd'
                  , groupId : groupId
                  , selectedValue : selectedValue
                  , comboType : 'A'
                  , comboTypeValue : ''
                  }
        );
    };

    var condOfficeCdCreateCombo = function(groupId, selectedValue){
        $("#condOfficeCd", tabObj).csCreatCombo(comboData
                , {id : 'officeCd'
                  , groupId : groupId
                  , selectedValue : selectedValue
                  , comboType : 'A'
                  , comboTypeValue : ''
                  }
        );
    };

    $("#condFisYear", tabObj).change(function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        condBgtDgrCreateCombo(fisYear, '');
        condFisFgMstCdCreateCombo(fisYear, '');

        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');

        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        condFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');
    });

    $("#condBgtDgr", tabObj).change(function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var bgtDgr = $("#condBgtDgr option:selected", tabObj).val();
        condOfficeCdCreateCombo(fisYear + '_' + bgtDgr, '');
    });

    $("#condFisFgMstCd", tabObj).change(function(){
        var fisYear = $("#condFisYear option:selected", tabObj).val();
        var fisFgMstCd = $("#condFisFgMstCd option:selected", tabObj).val();
        condFisFgCdCreateCombo(fisYear + '_' + fisFgMstCd, '');
    });

    $("#searchBtn", tabObj).click(function(){
        doSearch();
    });

    $("#condInitBtn", tabObj).click(function(){
        $("#condSrchNm", tabObj).val("");
        doCondInit();
        doSearch();
    });

    $("#selectAllBtn", tabObj).click(function(){
        if($(this).attr("enabledYn") != "Y"){
            return;
        }

        $("#selectAllBtn", tabObj).hide();
        $("#unSelectAllBtn", tabObj).show();
        setGridCheckedAll(compoGrid, $("#RLK_CHECK_DGRCOMPO_GRD", tabObj)[0].rows, "Y");
    });

    $("#unSelectAllBtn", tabObj).click(function(){
        if($(this).attr("enabledYn") != "Y"){
            return;
        }

        $("#unSelectAllBtn", tabObj).hide();
        $("#selectAllBtn", tabObj).show();
        setGridCheckedAll(compoGrid, $("#RLK_CHECK_DGRCOMPO_GRD", tabObj)[0].rows, "N");
    });

    var getSelectedDeptRow = function(){
        var $selRadio = $('input[name=rlkCheckDeptRadio]:checked', tabObj);
        if($selRadio.length < 1){
            return null;
        }

        var rowId = $selRadio.closest('tr').attr('id');
        return deptGrid.getRowData(rowId);
    };

    var getCheckedCompoDatas = function(){
        var gridRows = $("#RLK_CHECK_DGRCOMPO_GRD", tabObj)[0].rows;
        var checkedDatas = [];
        var rowId, rowData;

        for(var i = 0; i < gridRows.length; i++){
            rowId = gridRows[i].id;
            rowData = compoGrid.getRowData(rowId);

            if(rowData.selYn == "Y" && rowData.isLeaf == "true" && rowData.teBgtCompoId != "00000000000"){
                checkedDatas.push({
                    teBgtCompoId : rowData.teBgtCompoId
                });
            }
        }

        return checkedDatas;
    };

    var doApplyCallBack = function(data){
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });

            return;
        }

        $.csAlert({
            msg : data.bcjisMessage,
            callBack : function(){
                doSearch();
            }
        });
    };

    var doApply = function(params){
        if(params.confirmData != "Y"){
            return;
        }

        var deptRow = getSelectedDeptRow();
        var checkedDatas = getCheckedCompoDatas();

        $.csAjaxCall({
            url : "/budget/ajaxRlkCheckApplyDatas.do",
            data : {
                fisYear : deptRow.fisYear,
                bgtDgr : deptRow.bgtDgr,
                deptCd : deptRow.deptCd,
                deptNm : deptRow.deptNm,
                checkedDatas : checkedDatas
            },
            async : true,
            callBack : doApplyCallBack
        });
    };

    $("#applyDatasBtn", tabObj).click(function(){
        if($(this).attr("enabledYn") != "Y"){
            return;
        }

        var deptRow = getSelectedDeptRow();
        if(isEmpty(deptRow) == true){
            $.csAlert({
                msg : "좌측에서 적용할 부서를 선택하여 주십시오."
            });

            return;
        }

        var checkedDatas = getCheckedCompoDatas();
        if(isEmpty(checkedDatas) == true || checkedDatas.length < 1){
            $.csAlert({
                msg : "우측에서 적용할 세부사업을 선택하여 주십시오."
            });

            return;
        }

        $.csConfirm({
            msg : "선택한 " + checkedDatas.length + "건의 소속 부서를 '" + deptRow.deptNm + "'(으)로 변경하여 적용하시겠습니까?",
            callBack : doApply
        });
    });

    doCondInit();
    doSearch();
});
