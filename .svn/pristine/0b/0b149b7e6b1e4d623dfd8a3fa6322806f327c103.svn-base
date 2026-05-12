(function($) {
    jQuery.csAjaxCall = function(params) {
        var rtnData = null;
        ajaxDefaultParams = {
            type : "POST",
            async : false,
            error : function(xhr, st, err) {
                $.csAlert({
                    msg : "st:" + st + "<BR>xhr" + xhr + "<BR>err:" + err,
                    logLabel : CS_LOG_DEBUG
                });
            },
            dataType : "JSON",
            paramType : "JSON",
            beforeSend : function() {
            },
            complete : function() {
                if (params.async == true && typeof params.callBack == "function") {
                    params.callBack(rtnData);
                }
            },
            success : function(data, st) {
                rtnData = data;
            }
        };

        $.extend(ajaxDefaultParams, params);

        if (ajaxDefaultParams.paramType == "JSON") {
            ajaxDefaultParams.data = jsonToStringEncodeUrl(params.data);
        }

        var bcjisLoading = $("#bcjisLoading");
        if (bcjisLoading != null && bcjisLoading != undefined && bcjisLoading != "") {
            $(document).ajaxStart(function() {
                $("#bcjisLoading").fadeIn(200);
            }).ajaxStop(function() {
                $("#bcjisLoading").fadeOut(300);
            });
        }

        ajaxDefaultParams.url = ctx + ajaxDefaultParams.url;
        
        $.ajax(ajaxDefaultParams);

        return rtnData;
    };
    
    jQuery.csComboAjaxCall = function(params){
        var comboData = $.csAjaxCall({
            url : "/comm/ajaxCommComboList.do"
          , data: {comboParam: params}
        });
        
        return comboData;
    };
    
    jQuery.bcjisFileAjaxCall = function(params){
        var rtnData = null;
        ajaxDefaultParams = {
            type : "POST",
            async: false,
            error : function(xhr,st,err){
                $.csAlert({
                    msg : "st:" + st + "<BR>xhr" + xhr + "<BR>err:" + err,
                    logLabel : CS_LOG_DEBUG
                });
            },
            dataType: "json",
            complete : function(){
                if(typeof params.callBack == "function"){
                    params.callBack(rtnData);
                }
            },
            success : function(data, st){
                rtnData = data;
            }
        };
        
        $.extend(ajaxDefaultParams, params);
        
        var bcjisLoading = $("#bcjisLoading");
        if(bcjisLoading != null && bcjisLoading != undefined && bcjisLoading != ""){
            $(document).ajaxStart(function() {
                $("#bcjisLoading").fadeIn(200);
            }).ajaxStop(function() {
                $("#bcjisLoading").fadeOut(300);
            });
        }
        
        ajaxDefaultParams.url = ctx + ajaxDefaultParams.url;
        
        $.ajaxFileUpload(ajaxDefaultParams);
        
        return rtnData;
    };
    
    bcjisExeclDown = function(url, data) {

        var formId = 'bcjisExcelDownForm';

        if ($("#" + formId) != null && $("#" + formId).attr("id") != null) {
            $("#" + formId).remove();
        }

        var form = $('<form action="' + url + '" method="post" name="' + formId + '" id="' + formId + '"></form>');
        $(form).appendTo('body');

        if (data) {
            for ( var i in data) {
                $('<input type="hidden" name="' + i + '" value="'+ encodeURIComponent(data[i]) + '" />').appendTo(form);
            }
        }

        form.submit();
    };
    
    jQuery.bcjisExcelAjaxCall = function(params){
        var rtnData = null;
        
        ajaxEcelDefaultParams = {
            async: true,
            complete : function(){
                if(isEmpty(rtnData) == true || rtnData[BCJIS_RETURN_CODE] != "SUCC"){
                    $.csAlert({
                        msg : rtnData.bcjisMessage
                    });
                    
                    return;   
                }
                
                if(rtnData.fileDeleteYn != "N"){
                    rtnData["fileDeleteYn"] = "Y";
                }

                bcjisExeclDown(ctx + "/comm/excelFileDownload.do", rtnData);
                
                if(typeof params.callBack == "function"){
                    params.callBack(rtnData);
                }
            },
            success : function(data, st){
                rtnData = data;
            }
        };
        
        $.extend(ajaxEcelDefaultParams, params);
        
        if(isEmpty(ajaxEcelDefaultParams.data.excelFileName) == true){
            ajaxEcelDefaultParams.data.excelFileName = "예산편성심사정보시스템";
        }
        
        $.csAjaxCall(ajaxEcelDefaultParams);
    };
    
    jQuery.csGrid = function(params){
        if(params.id == null || params.id == undefined || params.id == ""){
            return;
        }
        
        if(params.divId == null || params.divId == undefined || params.divId == ""){
            params.divId = params.id + "_DIV";
        }
        
        if(params.tableId == null || params.tableId == undefined || params.tableId == ""){
            params.tableId = params.id + "_GRD";
        }
        
        if(params.pagerId == null || params.pagerId == undefined || params.pagerId == ""){
            params.pagerId = params.id + "_PGR";
        }
        
        var datatype = "json";
        if(params.datatype != null && params.datatype != undefined && params.datatype != ""){
            datatype = params.datatype;
            params.datatype = "";
        }
        
        var csDefaultGrid = $('#' + params.tableId, '#' + params.divId);
        imbsDefaultJsonReader = { 
            page: "page", 
            total: "totalPages",
            root: "dataList",
            id: "id",
            records: "totalCount",
            cell: "cell",
            repeatitems: false
        };
        
        $.extend(imbsDefaultJsonReader, params.jsonReader);
        
        csDefaultGridParams = {
            multiselect: false,
            width: "788",
            height: "auto",
            sortable: false,
            viewrecords: true,
            datatype: "local",
            rowNum: 10,
            defaultRows: 10,
            shrinkToFix: false,
            viewsortcols: true,
            minHeight: "250",
            emptyDataText: "조회된 자료가 존재하지 않습니다.",
            loadError: function(xhr, st, err){
                $.csAlert({
                    msg : "st:" + st + "<BR>xhr" + xhr + "<BR>err:" + err,
                    logLabel : CS_LOG_DEBUG
                });
            }
        };
        
        $.extend(csDefaultGridParams, params);
        
        csDefaultGridParams.jsonReader = imbsDefaultJsonReader;

        csDefaultGrid.jqGrid(csDefaultGridParams);
        
        csDefaultGrid.jqGrid('setGridParam', {datatype: datatype});
        
        var emptyData = {};
        emptyData[imbsDefaultJsonReader.page] = 0;
        emptyData[imbsDefaultJsonReader.total] = 0;
        emptyData[imbsDefaultJsonReader.records] = 0;
        emptyData[imbsDefaultJsonReader.root] = [];

        csDefaultGrid.addCsJsonData(emptyData);
        
        return csDefaultGrid;
    };
    
    jQuery.fn.insertData = function(pos, data){
        if(pos == null || pos == undefined || pos == "" || pos == 0){
            pos = 1;
        }
        
        try{
            this.jqGrid('addRowData', pos, data);
        }catch(err){
            $.csAlert({
                msg : err + "",
                logLabel : CS_LOG_DEBUG
            });
        }
    };
    
    jQuery.fn.addCsJsonData = function(data){
        try{
            if(data.rowNum != null && data.rowNum != undefined && data.rowNum != ""){
                this.jqGrid('setGridParam', {rowNum: data.rowNum});
            }
            
            this.jqGrid('setGridParam', {datatype: "json"});
            this[0].addJSONData(data);
            var dataList = data.dataList;
            var length = dataList.length;
            var defaultRows = this.getGridParam("defaultRows");
            var colModels = this.getGridParam("colModel");
            var colModelCnt = 0;
            for(var i = 0; i < colModels.length; i++){
                colModel = colModels[i];
                if(colModels[i].hidden == false){
                    colModelCnt ++;
                }
            }
            
            colNameLength = colModelCnt;
            
            var msg = "&nbsp;";
            for(var i = 0; i < defaultRows - length; i++){
                if((i+length+1) == 1){
                    msg = this.getGridParam("emptyDataText");
                }else{
                    msg = "&nbsp;";
                }
                this.append('<tr id="' + (i+length+1) + '" tabindex="-1" role="row" class="ui-widget-content jqgrow ui-row-ltr"><td colspan="'+colNameLength+'" role="gridcell" style="text-align:center;width: 98%;cursor:default;" aria-describedby="CS001_GRD_cb">' + msg + '</td></tr>');
            }
            
            this.jqGrid('setGridParam', {datatype: "local"});
        }catch(err){
            $.csAlert({
                msg : err,
                logLabel : CS_LOG_DEBUG
            });
        }
    };

    getColumnIndexByName = function(grid, columnName) {
        var cm = grid.jqGrid('getGridParam', 'colModel'), i, l;
        for (i = 0, l = cm.length; i < l; i += 1) {
            if (cm[i].name === columnName) {
                return i;
            }
        }
        return -1;
    };
    
    jQuery.fn.setCsParam = function(param){
        this.jqGrid("setGridParam", param);
    };
    
    jQuery.fn.getCsParam = function(param){
        return this.jqGrid("getGridParam", param);
    };
    
    jQuery.fn.addPagingData = function(data, f){
        try{
            var page = data.page;
            var totalPages = data.totalPages;

            if(f == null || f == undefined){
                f = "";
            }
            
            var prevPage1 = page < 2 ? 1 : page - 1;
            var prevPage2 = page <= 10 ? 1 : (page % 10) == 0 ? page-19 : page - (page % 10) - 10 + 1;
            var nextPage1 = page + 1 > totalPages ? totalPages : page + 1;
            var nextPage2 = page - (page % 10) + 10 + 1 > totalPages ? totalPages : page - (page % 10) + 10 + 1;
            
            var sHtml1 = '<a href="javascript:'+f+'('+prevPage2+')" class="img"><img src="' + ctx + '/images/btn/btn_prev02.gif" alt=""/></a>'
                       + '<a href="javascript:'+f+'('+prevPage1+')" class="img"><img src="' + ctx + '/images/btn/btn_prev01.gif" alt=""/></a>';
            var sHtml = '';
            
            var startPage = page <= 10 ? 1 : (page % 10) == 0 ? page - 9 : page - (page % 10) + 1;
            var endPage = (page % 10) == 0 ? page : page - (page % 10) + 10 > totalPages ? totalPages : page - (page % 10) + 10;

            for(var i = startPage; i <= endPage; i++){
                if(i == page){
                    sHtml += '<span style="color:#666666;font-weight:bold;">'+i+'</span>';
                }else{
                    sHtml += '<a href="javascript:'+f+'('+i+')"><span style="text-decoration:underline;">'+i+'</span></a>';
                }
            }
            
            var sHtml2 = '<a href="javascript:'+f+'('+nextPage1+')" class="img"><img src="' + ctx + '/images/btn/btn_next01.gif" alt=""/></a>'
                       + '<a href="javascript:'+f+'('+nextPage2+')" class="img"><img src="' + ctx + '/images/btn/btn_next02.gif" alt=""/></a>';
            
            this.html('');
            this.html(sHtml1 + sHtml + sHtml2);
        }catch(err){
            $.csAlert({
                msg : err + "",
                logLabel : CS_LOG_DEBUG
            });
        }
    };
    
    jQuery.fn.csTreeGrid = function(params){
        csDefaultGridParams = {
                datatype : "jsonstring",
                sortable: false,
                height : "auto",
                width : "auto",
                loadui : "disable",
                treeGrid : true,
                treeGridModel : "adjacency",
                autowidth : true,
                rowNum : 10000,
                ExpandColClick : true,
                treeIcons : {
                    leaf : 'ui-icon-document-b'
                }
        };
    
        $.extend(csDefaultGridParams, params);
    
        this.jqGrid(csDefaultGridParams);
    };

    getSelectedStr = function(str1, str2){
        if(str1 == str2){
            return "selected";
        }
        
        return "";
    };
    
    jQuery.fn.csCreatCombo = function(data, options){
        if(options.id == null || options.id == undefined || options.id == ""){
            return;
        }

        this.empty();
        if(options.comboType == "A"){
            this.append("<option value='" + options.comboTypeValue + "' " + getSelectedStr(options.selectedValue, options.comboTypeValue) + ">전체</option>");
        }else if(options.comboType == "S"){
            this.append("<option value='" + options.comboTypeValue + "' " + getSelectedStr(options.selectedValue, options.comboTypeValue) + ">선택</option>");
        }else if(options.comboType == "TS"){
        	var title = this.attr('title');
        	if(title){
        		this.append("<option value='" + options.comboTypeValue + "' " + getSelectedStr(options.selectedValue, options.comboTypeValue) + ">" + title + " 선택</option>");
        	}else{
        		this.append("<option value='" + options.comboTypeValue + "' " + getSelectedStr(options.selectedValue, options.comboTypeValue) + ">선택</option>");
        	}
        }
        
        var tempData = {};
        
        var addList = options.beforeAdd;
        if(addList != null && addList != undefined && addList != ""){
            for(var i = 0; i < addList.length; i++){
                tempData = addList[i];
                if(options.groupId == "ALL" || tempData.groupId == options.groupId){
                    this.append("<option value='" + tempData.code + "' " + getSelectedStr(options.selectedValue, tempData.code) + ">" + tempData.codeNm + "</option>");
                }
            }
        }
        
        var tempComboData = data[options.id];
        if(tempComboData != null && tempComboData != undefined && tempComboData != ""){
            for(var i = 0; i < tempComboData.length; i++){
                tempData = tempComboData[i];
                if(options.groupId == "ALL" || tempData.groupId == options.groupId){
                    this.append("<option value='" + tempData.code + "' " + getSelectedStr(options.selectedValue, tempData.code) + ">" + tempData.codeNm + "</option>");
                }
            }
        }
        
        addList = options.afterAdd;
        if(addList != null && addList != undefined && addList != ""){
            for(var i = 0; i < addList.length; i++){
                tempData = addList[i];
                if(options.groupId == "ALL" || tempData.groupId == options.groupId){
                    this.append("<option value='" + tempData.code + "' " + getSelectedStr(options.selectedValue, tempData.code) + ">" + tempData.codeNm + "</option>");
                }
            }
        }
        
        try{
            if(options.selectedValue == null || options.selectedValue == undefined || options.selectedValue == ""){
                if(options.defaultSelectedYn != "N"){
                    $(this).val($("#" + $(this).attr("id") + " option:first", $(this)).val());
                }
            }
        }catch(err){
            alert(err);
        }
    };
    
    getCsComboStr = function(data, options){
        var rVal = "";
        if(options.id == null || options.id == undefined || options.id == ""){
            return rVal;
        }
        
        var tempData = {};
        
        var addList = options.beforeAdd;
        if(addList != null && addList != undefined && addList != ""){
            for(var i = 0; i < addList.length; i++){
                tempData = addList[i];
                if(options.groupId == "ALL" || tempData.groupId == options.groupId){
                    rVal +="<option value='" + tempData.code + "' " + getSelectedStr(options.selectedValue, tempData.code) + ">" + tempData.codeNm + "</option>";
                }
            }
        }
        var tempComboData = data[options.id];
        if(tempComboData != null && tempComboData != undefined && tempComboData != ""){
            for(var i = 0; i < tempComboData.length; i++){
                tempData = tempComboData[i];
                if(options.groupId == "ALL" || tempData.groupId == options.groupId){
                    rVal +="<option value='" + tempData.code + "' " + getSelectedStr(options.selectedValue, tempData.code) + ">" + tempData.codeNm + "</option>";
                }
            }
        }
        
        addList = options.afterAdd;
        if(addList != null && addList != undefined && addList != ""){
            for(var i = 0; i < addList.length; i++){
                tempData = addList[i];
                if(options.groupId == "ALL" || tempData.groupId == options.groupId){
                    rVal +="<option value='" + tempData.code + "' " + getSelectedStr(options.selectedValue, tempData.code) + ">" + tempData.codeNm + "</option>";
                }
            }
        }

        var rValH = "";
        try{
            if(options.selectedValue == null || options.selectedValue == undefined || options.selectedValue == ""){
                if(options.defaultSelectedYn != "N"){           
                    if(options.comboType == "A"){
                        rValH +="<option value='" + options.comboTypeValue + "' selected>전체</option>";
                    }else if(options.comboType == "S"){
                        rValH +="<option value='" + options.comboTypeValue + "' selected>선택</option>";
                    }else if(options.comboType == "C"){//추가
                        rValH +="<option value='" + options.comboTypeValue + "' selected></option>";
                    }
                }
            }else{
                if(options.comboType == "A"){
                    rValH +="<option value='" + options.comboTypeValue + "' " + getSelectedStr(options.selectedValue, options.comboTypeValue) + ">전체</option>";
                }else if(options.comboType == "S"){
                    rValH +="<option value='" + options.comboTypeValue + "' " + getSelectedStr(options.selectedValue, options.comboTypeValue) + ">선택</option>";
                }else if(options.comboType == "C"){
                    rValH +="<option value='" + options.comboTypeValue + "' selected></option>";
                }
            }
        }catch(err){
            
        }
        
        return rValH + rVal;
    };
    
    jQuery.fn.csDatepicker = function(options){
        var csDefaultCalendarParams = {
                monthNamesShort: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
                dayNamesMin: ['일','월','화','수','목','금','토'],
                weekHeader: 'Wk',
                dateFormat: 'yy-mm-dd',
                changeMonth: true,
                changeYear: true,
                showMonthAfterYear: true,
                buttonImageOnly: true,
                buttonImage: ctx+"/images/btn/btn_calendar.gif",
                showOn: "both",
                yearRange: '2010:c+1>'
        };
        
        
        $.extend(csDefaultCalendarParams, options);
        
        this.datepicker(csDefaultCalendarParams);
        
        this.isDate();
    };
    
    jQuery.fn.isNumberic = function(){
        this.keyup(function(){
            $(this).val($(this).val().replace(/[^\d]+/g, ''));
        });
        
        this.blur(function(){
            $(this).val($(this).val().replace(/[^\d]+/g, ''));
        });
    };
    
    jQuery.fn.isNumberic2 = function(){
        this.keyup(function(){
            $(this).val($(this).val().replace(/[^0-9-]/g, ''));
        });
        
        this.blur(function(){
            $(this).val($(this).val().replace(/[^0-9-]/g, ''));
        });
    };
    
    jQuery.fn.isNumberic3 = function(){
        this.keyup(function(){
            $(this).val($(this).val().replace(/[^0-9.]/g, ''));
        });
        
        this.blur(function(){
            $(this).val($(this).val().replace(/[^0-9.]/g, ''));
        });
    };
    
    jQuery.fn.isAlphaNumberic2 = function(){
        this.keyup(function(){
            $(this).val($(this).val().replace(/[^a-zA-Z0-9_]/g, ''));
        });
        
        this.blur(function(){
            $(this).val($(this).val().replace(/[^a-zA-Z0-9_]/g, ''));
        });
    };
    
    jQuery.fn.isDate = function(){
        this.keyup(function(){
            $(this).val(toDateFormat($(this).val()));
            
        });
        
        this.blur(function(){
            $(this).val(toDateFormat($(this).val()));
        });
    };
    
    jQuery.fn.btnChangeState = function(flag){
        var tFlag = flag;
        if(_mainNorthPowGrCd == "BC002"){
            tFlag = false;
        }
        
        if(tFlag){
            $(this).removeClass("btnDisabledClass");
            $(this).addClass("btnClass");
            $(this).attr("enabledYn", "Y");
        }else{
            $(this).removeClass("btnClass");
            $(this).addClass("btnDisabledClass");
            $(this).attr("enabledYn", "N");
            
        }
    };
    
    attchFileDownload = function(atchFileId, fileSn) {
        var url = ctx + "/comm/fileDown.do";
        var formId = 'bcjisAttchFileDownForm';

        if ($("#" + formId) != null && $("#" + formId).attr("id") != null) {
            $("#" + formId).remove();
        }

        var form = $('<form action="' + url + '" method="post" name="' + formId + '" id="' + formId + '"></form>');
        $(form).appendTo('body');

        $('<input type="hidden" name="atchFileId" value="'+ encodeURIComponent(atchFileId) + '" />').appendTo(form);
        $('<input type="hidden" name="fileSn" value="'+ encodeURIComponent(fileSn) + '" />').appendTo(form);

        form.submit();
    };
    
    getAtchFileHtml = function(fileList) {
        if(isEmpty(fileList) == true || fileList.length < 1){
            return "";
        }
        
        var tHtml = "";
        for(var i = 0; i < fileList.length; i++){
            tHtml = (i == 0 ? '' : '<br/>') 
                  + '<a id="atchFileDn_'+i+'" href="javascript:attchFileDownload(\''+fileList[i].atchFileId+'\', \''+fileList[i].fileSn+'\');">'
                  + fileList[i].orignlFileNm + '</a>';
        }
        
        return tHtml;
    };
    
    getPopupLinkHtml = function(url, name) {
        if(isEmpty(url) == true ){
            return "";
        }
        
        if(isEmpty(name) == true){
            name = "_blank";
        }
        
        var tHtml = '<a id="popupLinkHtml_0" href="javascript:window.open(\''+url+'\', \''+name+'\');">' + url + '</a>';
        
        return tHtml;
    };
    
    jQuery.csFullCalendar = function(params){
        if(params.id == null || params.id == undefined || params.id == ""){
            return;
        }

        csFullCalendarParams = {
            theme: true,
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'month,agendaWeek,agendaDay'
            },
            editable: false,
            titleFormat : {
                month : "yyyy년 MMMM",
                week : "[yyyy] MMM d일{ [yyyy] MMM d일}",
                day : "yyyy년 MMM d일 dddd"
            },
            monthNames : [ "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월" ],
            monthNamesShort : [ "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월" ],
            dayNames : [ "일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일" ],
            dayNamesShort : [ "일", "월", "화", "수", "목", "금", "토" ],
            buttonText : {
                today : "오늘",
                month : "월별",
                week : "주별",
                day : "일별"
            }
        };
        
        $.extend(csFullCalendarParams, params);
        
        return $('#calendar').fullCalendar(csFullCalendarParams);
    };
    
    jQuery.popupWindow = function(params){
        var popupWindowParams = {
            centerBrowser:0, // center window over browser window? {1 (YES) or 0 (NO)}. overrides top and left
            centerScreen:0, // center window over entire screen? {1 (YES) or 0 (NO)}. overrides top and left
            height:500, // sets the height in pixels of the window.
            left:0, // left position when the window appears.
            location:0, // determines whether the address bar is displayed {1 (YES) or 0 (NO)}.
            menubar:0, // determines whether the menu bar is displayed {1 (YES) or 0 (NO)}.
            resizable:0, // whether the window can be resized {1 (YES) or 0 (NO)}. Can also be overloaded using resizable.
            scrollbars:0, // determines whether scrollbars appear on the window {1 (YES) or 0 (NO)}.
            status:0, // whether a status line appears at the bottom of the window {1 (YES) or 0 (NO)}.
            width:500, // sets the width in pixels of the window.
            windowName:null, // name of window set from the name attribute of the element that invokes the click
            windowURL:null, // url used for the popup
            top:0, // top position when the window appears.
            toolbar:0 // determines whether a toolbar (includes the forward and back buttons) is displayed {1 (YES) or 0 (NO)}.
        };
        
        settings = $.extend({}, popupWindowParams, params || {});
        
        var windowFeatures = 'height=' + settings.height
                           + ',width=' + settings.width
                           + ',toolbar=' + settings.toolbar
                           + ',scrollbars=' + settings.scrollbars
                           + ',status=' + settings.status
                           + ',resizable=' + settings.resizable
                           + ',location=' + settings.location
                           + ',menuBar=' + settings.menubar;
        var centeredY;
        var centeredX;
        
        if(settings.centerBrowser){
            if (navigator.userAgent.match(/msie /i)) {//hacked together for IE browsers
                centeredY = (window.screenTop - 120) + ((((document.documentElement.clientHeight + 120)/2) - (settings.height/2)));
                centeredX = window.screenLeft + ((((document.body.offsetWidth + 20)/2) - (settings.width/2)));
            }else{
                centeredY = window.screenY + (((window.outerHeight/2) - (settings.height/2)));
                centeredX = window.screenX + (((window.outerWidth/2) - (settings.width/2)));
            }
            window.open(ctx+settings.windowURL, settings.windowName, windowFeatures+',left=' + centeredX +',top=' + centeredY).focus();
        }else if(settings.centerScreen){
            centeredY = (screen.height - settings.height)/2;
            centeredX = (screen.width - settings.width)/2;
            window.open(ctx+settings.windowURL, settings.windowName, windowFeatures+',left=' + centeredX +',top=' + centeredY).focus();
        }else{
            window.open(ctx+settings.windowURL, settings.windowName, windowFeatures+',left=' + settings.left +',top=' + settings.top).focus();  
        }
        
    };
    
    jQuery.csAlert = function(params) {
        
        alertParams = {
            resizable : false,
            height : "auto",
            width : "auto",
            minWidth: 200,
            modal : true,
            title : "확인",
            buttons : {
                "확인" : function(event) {
                    $(this).dialog("close");
                    if(isEmpty(params.callBack) == false){
                        params.callBack(params);
                    }
                }
            }
        };

        $.extend(alertParams, params);

        if (alertParams.logLabel != null && alertParams.logLabel != undefined && alertParams.logLabel != "") {
            if (alertParams.logLabel > CS_LOG_LEVEL) {
                return;
            }
        }

        $("#bcjisDialogMsg").dialog(alertParams);
        $("#bcjisDialogMsgDiv").html(alertParams.msg);
        
        if (navigator.userAgent.match(/msie [6]/i) || navigator.userAgent.match(/msie [7]/i) ) {
            var w = $("#bcjisDialogMsg #bcjisDialogMsgDiv")[0].clientWidth + 20;
            $("#bcjisDialogMsg").dialog('option', {'width':w});
        }
        
        if($("#bcjisDialogMsg").width() < alertParams.minWidth){
            $("#bcjisDialogMsg").width(alertParams.minWidth);
        }

        try{
            $("#bcjisLoading").fadeOut(300);
        }catch(err){
        }
    };

    jQuery.csConfirm = function(params, callback) {
        confirmParams = {
            resizable : false,
            height : "auto",
            width: "auto",
            minWidth: 200,
            modal : true,
            title : "확인",
            buttons : {
                "확인" : function() {
                    $(this).dialog("close");
                    params["confirmData"] = "Y";
                    if(isEmpty(params.callBack) == false){
                        params.callBack(params);
                    }
                },
                "취소" : function() {
                    $(this).dialog("close");
                    params["confirmData"] = "N";
                    if(isEmpty(params.callBack) == false){
                        params.callBack(params);
                    }
                }
            }
        };

        $.extend(confirmParams, params);

        $("#bcjisDialogMsg").dialog(confirmParams);
        $("#bcjisDialogMsgDiv").html(confirmParams.msg);
        
        if (navigator.userAgent.match(/msie [6]/i) || navigator.userAgent.match(/msie [7]/i) ) {
            var w = $("#bcjisDialogMsg #bcjisDialogMsgDiv")[0].clientWidth + 20;
            $("#bcjisDialogMsg").dialog('option', {'width':w});
        }
        
        if($("#bcjisDialogMsg").width() < confirmParams.minWidth){
            $("#bcjisDialogMsg").width(confirmParams.minWidth);
        }
   
    };

})(jQuery);