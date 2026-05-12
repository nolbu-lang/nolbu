var CS_LOG_INFO = 1;
var CS_LOG_DEBUG = 2;

var CS_LOG_LEVEL = CS_LOG_DEBUG;

var BCJIS_RETURN_CODE = "bcjisRtnCode";

String.prototype.toCamelize = function(){
    return this.toLowerCase().replace(/_+(.)?/g, function(match, chr){return chr ? chr.toUpperCase() : '';});
};

String.prototype.replaceAll = function(target, replacement){
   return this.split(target).join(replacement);
};

String.prototype.digits = function(){
   return this.replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,");
};

String.prototype.trim = function() {
    return this.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
};

String.prototype.startsWith = function (str){
      return this.indexOf(str) == 0;
};

function isEmpty(s) {
    if (s == null || s == undefined || s == "" || (""+s).trim().length == 0) {
        return true;
    }

    return false;
}

function jsonToString(obj) {
    var t = typeof (obj);
    if (t != "object" || obj === null) {
        if (t == "string")
            obj = '"' + obj + '"';
        return String(obj);
    } else {
        var v, json = [], arr = (obj && obj.constructor == Array);
        for ( var n in obj) {
            v = obj[n];
            t = typeof (v);
            if (t == "string")
                v = '"' + v + '"';
            else if (t == "object" && v !== null)
                v = jsonToString(v);
            json.push((arr ? "" : '"' + n + '":') + String(v));
        }
        return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
    }
}

function jsonToStringEncodeUrl(obj) {
    var t = typeof (obj);
    if (t != "object" || obj === null) {
        if (t == "string")
            obj = '"' + obj + '"';
        return String(obj);
    } else {
        var v, json = [], arr = (obj && obj.constructor == Array);
        for ( var n in obj) {
            v = obj[n];
            t = typeof (v);
            if (t == "string")
                v = '"' + encodeURIComponent(v) + '"';
            else if (t == "object" && v !== null)
                v = jsonToStringEncodeUrl(v);
            json.push((arr ? "" : '"' + n + '":') + String(v));
        }
        return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
    }
}

function addCommaStr(str){
    if(isEmpty(str) == true){
        str = "0";
    }
    
    str = "" + str;
    var strArr = str.split('.');
    var str1 = strArr[0];
    var str2 = strArr.length > 1 ? '.' + strArr[1] : '';
    
    return str1.replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") + str2;
}

function addCommaMinusStr(str){
    if(isEmpty(str) == true){
        str = "0";
    }
    
    str = "" + str;
    var strArr = str.split('.');
    var str1 = strArr[0];
    var str2 = strArr.length > 1 ? '.' + strArr[1] : '';
    
    return str1.replace(/^[+-]?(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") + str2;
}

function toDateFormat(str){
    if(isEmpty(str) == true){
        return "";
    }
    
    str = str.replace(/[^a-zA-Z0-9]/g, '');
    
    if(str.length < 5){
        return str;
    }
    
    if(str.length < 7){
        return str.substring(0, 4) + "-" + str.substring(4, 6);
    }
    
    return str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8);
};

function beforeFill(str, fillStr, size){
    str = "" + str;
    if(isEmpty(str) == true){
        str = "";
    }
    
    var tempFill = "";
    for(var i = 0; i < size - str.length; i++){
        if(str.length == 11){
            alert("i:" + i + "size:" + size);
        }
        tempFill += fillStr;
    }

    return tempFill + str;
}

function beforeZeroFill(str, size){
    return beforeFill(str, "0", size);
}

function bcjisPrintPage(){
    window.print();
}

function bcjisMovePage(url, data) {

    var formId = 'bcjisMovePageForm';

    var form = $('<form action="' + ctx + url + '" method="post" name="' + formId + '" id="' + formId + '"></form>');
    $(form).appendTo('body');

    if (data) {
        for ( var i in data) {
            $('<input type="hidden" name="' + i + '" value="' + encodeURIComponent(data[i]) + '" />').appendTo(form);
        }
    }

    form.submit();
}

function bcjisMovePage2(url, data) {

    var formId = 'bcjisMovePageForm';

    var form = $('<form action="' + url + '" method="post" name="' + formId + '" id="' + formId + '"></form>');
    $(form).appendTo('body');

    if (data) {
        for ( var i in data) {
            $('<input type="hidden" name="' + i + '" value="' + encodeURIComponent(data[i]) + '" />').appendTo(form);
        }
    }

    form.submit();
}

function getElementValueToString(eleValue) {
    var strValue = "";

    if (eleValue == "[object]") {
        strValue = eleValue.value;
    } else {
        strValue = eleValue;
    }
    return strValue;
}

function isRequired(eleValue) {
    var strValue = getElementValueToString(eleValue);

    if (strValue.replace(/^(\s+)|(\s+)$/g, "").length == 0) {
        return false;
    } else {
        return true;
    }
}

function fnLimitLength(str, length) {
    str = str + "";
    if (getLengthByteUTF8(str) > length) {
        return false;
    }

    return true;
}

function getLengthByteUTF8(input) {
    var byteLength = 0;

    for ( var inx = 0; inx < input.length; inx++) {
        var oneChar = input.charAt(inx);
        byteLength += charByteSize(oneChar);
    }

    return byteLength;
}

function charByteSize(ch) {
    if (ch == null || ch.length == 0) {
        return 0;
    }
    var charCode = ch.charCodeAt(0);
    if (charCode <= 0x00007F) {
        return 1;
    } else if (charCode <= 0x0007FF) {
        return 2;
    } else if (charCode <= 0x00FFFF) {
        return 3;
    } else {
        return 4;
    }
}

function isValidURL(url) {
    var RegExp = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;

    if (RegExp.test(url)) {
        return true;
    } else {
        return false;
    }
}

function setTreeGridChecked(checkedEvent, gridObjeck, gridRows, rowLevelNm){
    var checkedRowId = $(checkedEvent.target).closest('tr')[0].id;
    var isChecked = $(checkedEvent.target).is(':checked');
    
    var rowLevel = Number(gridObjeck.jqGrid('getCell', checkedRowId, rowLevelNm));

    var tempId;
    var startFlag = false;
    var tempRowLevel = 0;
    for(var k = 0; k < gridRows.length; k++) {
        tempId = gridRows[k].id;
        
        if(checkedRowId === tempId){
            startFlag = true;
            continue;
        }
        
        if(startFlag != true){
            continue;
        }
        
        tempRowLevel = Number(gridObjeck.jqGrid('getCell', tempId, rowLevelNm));
        if(rowLevel < tempRowLevel){
            gridObjeck.jqGrid('setCell', tempId, 'selYn', isChecked);
        }else{
            break;
        }
    }
}

function setGridCheckedAll(gridObjeck, gridRows, isChecked){
    var tempId;
    for(var k = 0; k < gridRows.length; k++) {
        tempId = gridRows[k].id;
        gridObjeck.jqGrid('setCell', tempId, 'selYn', isChecked);
    }
}

function setUpTreeGridCheck(gridObjeck, rowid, upIdNm, rowLevelNm){
    if(isEmpty(rowLevelNm) == true){
        rowLevelNm = "dgrLevel";
    }
    
    var selYn = gridObjeck.jqGrid('getCell', rowid, "selYn");
    var rowLevel = Number(gridObjeck.jqGrid('getCell', rowid, rowLevelNm));

    if(selYn !== "Y"){
        return;
    }
    
    if(rowLevel <= 5){
        return;
    }
    
    var pRowId = gridObjeck.jqGrid('getCell', rowid, upIdNm); 
    gridObjeck.jqGrid('setCell', pRowId, "selYn", "Y");
    
    setUpTreeGridCheck(gridObjeck, pRowId, upIdNm);
}

function checkCloseYn(param){    
    var data = $.csAjaxCall({
        url : "/comm/ajaxSelectIsCloseYn.do",
        data : param
    });
    
    if(data.data.closeYn != "N"){
        $.csAlert({
            msg : "마감된 자료입니다."
        });
        
        return false;
    }
/*
    if(param.reportCheckYn == "Y" && data.data.userReportYn == "Y" && data.data.reportYn != "Y"){
        $.csAlert({
            msg : "조서 수정 권한이 존재하지 않습니다."
        });
        
        return false;
    }
*/    
    return true;
}