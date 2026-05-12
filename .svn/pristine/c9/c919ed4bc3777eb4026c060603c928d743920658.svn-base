$(document).ready(function() {
    bcjisCommMainObj = {};
    
    mainLayout = $('#wrap').layout({
        north__size : 70,
        west__size : 270,
        south__size : 72
    });
   
    //상단 자동 토글
    mainLayout.toggle('north');
    //하단 자동 토글
    mainLayout.toggle('south');
});

//textArea 글자수 제한(객체, 최대가로, 최대줄)
function chkAreaLen(obj, maxWidth, maxLine){
	var str = obj.value;
	var strArr = str.split('\n');
	var rtnStr = "";
	var flag = true;
	for(var i=0 ; i<strArr.length ; i++){
		var tmpStr = strArr[i];
		var len = tmpStr.length;
		
		if(i < maxLine){
			if(len > maxWidth && maxWidth != 0){
				rtnStr += tmpStr.substring(0, maxWidth) + '\n';
				flag = false;
			}else{
				if(i == (maxLine - 1)){
					rtnStr += tmpStr;
				}else{
					rtnStr += tmpStr + '\n';
				}
				
			}
		}else{
			flag = false;
		}
	}
	
	if(!flag){
		obj.value = rtnStr;
	}
}

//textArea 글자수 제한(객체, 최대줄)
function chkAreaLenLine(obj, maxLine){
	var str = obj.value;
	var strArr = str.split('\n');
	var rtnStr = "";
	var flag = true;
	for(var i=0 ; i<strArr.length ; i++){
		var tmpStr = strArr[i];
		var len = tmpStr.length;
		
		if(i < maxLine){
			if(i == (maxLine - 1)){
				rtnStr += tmpStr;
			}else{
				rtnStr += tmpStr + '\n';
			}
		}else{
			flag = false;
		}
	}
	
	if(!flag){
		obj.value = rtnStr;
	}
}

function commifyKey(s) {
	var n = decommifyKey($(s).val());
	var reg = /(^[+-]?\d+)(\d{3})/;   // 정규식
	n += '';                          // 숫자를 문자열로 변환

	while (reg.test(n)){
	    n = n.replace(reg, '$1' + ',' + '$2');
	}
	
	var stripNum = decommifyKey(n);

	if((n!="-" && isNaN(stripNum)===true) || stripNum.replace(/\-/g,'').length > 15){
		 //alert('금액을 확인해주세요');
		 n = "";
	}
	
	//document.write_form.amt.value = n;
	$(s).val(n);
	console.log(n);
	//return n;
}

function decommifyKey(n){
	var num = n.toString();
	if(num=="" || num==null){
		return "";
	}
	re =/^\$|,/g;
	 
	num = num.replace(re,"");
	 
	return num;
}