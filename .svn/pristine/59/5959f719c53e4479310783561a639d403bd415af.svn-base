<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    
    var dialogObj = $("#dialogDgrcompoSeperateDiv");
    
    //layoutResize
    var sepBodyResize = function () {
       if(isEmpty($("#DIALOG_DGR_COMPO_SEPERATE_FRSC_GRD", dialogObj)) == false){
            $("#DIALOG_DGR_COMPO_SEPERATE_FRSC_GRD", dialogObj).setGridWidth($("#sepWestDiv2", dialogObj).width());
        }
        
        if(isEmpty($("#DIALOG_DGR_COMPO_SEPERATE_CHAR_GRD", dialogObj)) == false){
            $("#DIALOG_DGR_COMPO_SEPERATE_CHAR_GRD", dialogObj).setGridWidth($("#sepWestDiv3", dialogObj).width());
        }
    };

    
    var dialogDgrcompoSeperateClose = function(){
        $("#dialogDgrcompoSeperateDiv").dialog("close");
    };
    
    function openDialog(){
        
    	$("#sepBody", dialogObj).layout({
            west__size : "50%",
            center__onresize: sepBodyResize
        });
    	
    	sepBodyResize();
    }

    $("#dialogDgrcompoSeperateDiv").dialog({
        title: "예산분리",
        autoOpen: false,
        width: 1500,
        height: 800,
        modal: true,
        resizable: true,
        open: function(event, ui){
            openDialog();
        },
        buttons : {
            "저장" : function() {
            },
            "닫기" : function() {
                dialogDgrcompoSeperateClose();
            }
        }
    });
    

    var sepTab = $("#tabTest").tabs();
    
	$("#addBtn", dialogObj).click(function() {
    	
        var tabCnt = parseInt($('#dialogDgrcompoSeperateTabCnt').val());
        
        var id = 'tab_' + tabCnt;
        var label = '탭' + tabCnt;
        var tabTemplate = "<li><a href='!{href}'>!{label}</a> <span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>";
        var li = $(tabTemplate.replace(/!\{href\}/g, "#" + id).replace(/!\{label\}/g, label));

        sepTab.find(".ui-tabs-nav").append(li);
        sepTab.append('<div id="' + id + '" style="height:100%;">' + getSepHtml(tabCnt) + '</div>');
        
        sepTab.tabs("refresh");
        
        $('#dialogDgrcompoSeperateTabCnt').val(tabCnt + 1);
        
        $("[href='#"+id+"']", dialogObj).trigger("click");
    });
	
	
	function getSepHtml(cnt){
		var sepHtml = '<div id="sepWestDiv1' + cnt + '"  style="padding-bottom:20px;">';
		sepHtml += '<div id="dialogDgrcompoSeperateAmtUnitDiv' + cnt + '" class="unitDiv" style="top: 0px;">';
		sepHtml += '(단위:천원)';
		sepHtml += '</div>';
		sepHtml += '<div class="viewDiv' + cnt + '" style="width:690px;">';
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
		sepHtml += '<th colspan="2">사업-통계목</th>';
		sepHtml += '<td colspan="5">';
		sepHtml += '<input type="hidden" id="dialogDgrcompoSeperateTeBgtCompoSeq' + cnt + '"/>';
		sepHtml += '<input type="text" id="dialogDgrcompoSeperateDgrcompoNm' + cnt + '" class="readonly" style="width:100%;" readonly/>';
		sepHtml += '</td>';
		sepHtml += '</tr>';
		sepHtml += '<tr>';
		sepHtml += '<th colspan="2">사업명</th>';
		sepHtml += '<td colspan="5">';
		sepHtml += '<input type="text" id="dialogDgrcompoSeperateCompGround' + cnt + '" style="width:100%;"/>';
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
		sepHtml += '<div id="DIALOG_DGR_COMPO_SEPERATE_FRSC_DIV' + cnt + '" class="csGrid">';
		sepHtml += '<table id="DIALOG_DGR_COMPO_SEPERATE_FRSC_GRD' + cnt + '"  style="border:0px;height:100%;"></table>';
		sepHtml += '</div>';
		sepHtml += '</div>';
		sepHtml += '<div id="sepWestDiv3' + cnt + '">';
		sepHtml += '<div class="ui-widget-header">';
		sepHtml += '성격별 예산액';
		sepHtml += '</div>';
		sepHtml += '<div id="DIALOG_DGR_COMPO_SEPERATE_CHAR_DIV' + cnt + '" class="csGrid">';
		sepHtml += '<table id="DIALOG_DGR_COMPO_SEPERATE_CHAR_GRD' + cnt + '" ></table>';
		sepHtml += '</div>';
		sepHtml += '</div>';
	}
});




</script>
<div id="dialogDgrcompoSeperateDiv" class="dialog" style="display:none;">
  <input type="hidden" id="dialogDgrcompoSeperateCallBackFunction"/>
  <input type="hidden" id="dialogDgrcompoSeperateDgrcompoId"/>
  <input type="hidden" id="dialogDgrcompoSeperateFisYear"/>
  <input type="hidden" id="dialogDgrcompoSeperateBgtDgr"/>
  <input type="hidden" id="dialogDgrcompoSeperateTeBgtCompoId"/>
  <input type="hidden" id="dialogDgrcompoSeperateIsLeaf"/>
  <input type="hidden" id="dialogDgrcompoSeperateCompoLevel"/>
  <input type="hidden" id="dialogDgrcompoSeperateAmtUnit" value="1"/>
  <input type="hidden" id="dialogDgrcompoSeperateTabCnt" value="0"/>  
  <br>
  
  <div id="sepBody"  style="height:97%;border:0px;overflow:hidden;">
        <div id="sepCenter" class="ui-layout-center" style="border:0px;overflow:auto;">
        	<div class="btn">
            	<!-- <div class="btnL">
              		<a id="selectAllRlkBtn" class="btnDisabledClass" href="#" enabledYn="N">전체선택</a>
              		<a id="unSelectAllRlkBtn" class="btnDisabledClass" href="#" enabledYn="N" style="display:none;">선택해제</a>
            	</div> -->
            	<div class="btnR">
              		<a id="addBtn" class="btnDisabledClass" href="#none" enabledYn="Y">추가</a>
            	</div>
          	</div>
			<div id="tabTest" class="ui-layout-center">
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
			          <th colspan="2">사업-통계목</th>
			          <td colspan="5">
			            <input type="hidden" id="dialogDgrcompoSeperateTeBgtCompoSeq"/>
			            <input type="text" id="dialogDgrcompoSeperateDgrcompoNm" class="readonly" style="width:100%;" readonly/>
			          </td>
			        </tr>
			        <tr>
			          <th colspan="2">사업명</th>
			          <td colspan="5">
			            <input type="text" id="dialogDgrcompoSeperateCompGround" style="width:100%;"/>
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

<script type="text/javaScript" language="javascript" defer="defer">

$(document).ready(function (){
	console.log('22 : ' + $("#sepBody").width());
});
</script>