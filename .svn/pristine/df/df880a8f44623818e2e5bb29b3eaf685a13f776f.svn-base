<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html lang="ko">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />

<link rel="stylesheet" media="all" href="/css/jquery.layout.css"/>

<style>
*{margin:0; padding:0;}
body{color:#222; font-size:14px; line-height:1.3em; letter-spacing:-0.06em; font-family: 'Open Sans', 'Malgun Gothic', sans-serif;}
a{text-decoration:none; color:inherit;}
ul, ol{list-style:none;}
img{border:none; vertical-align:top;}
input, select, button{vertical-align:top; border:none;}

fieldset{border:none;}
input:focus{outline:none;}
legend{display:none;}

button, .pnt{cursor:pointer;}

table{border-collapse:collapse;}

hr {clear:both;border:none;}



body{padding:20px;}
h1{line-height: normal; letter-spacing: -0.1em; width:100%;}
p{padding:10px 0; width:100%;}
.classify_table{width:1200px;}
	.classify_table thead .bd-L, .classify_table tbody .bd-L{border-left:2px solid #222;}
	.classify_table thead .bd-R, .classify_table tbody .bd-R{border-right:2px solid #222;}
	.classify_table thead .bd-T, .classify_table tbody .bd-T{border-top:2px solid #222;}
	.classify_table thead .bd-B, .classify_table tbody .bd-B{border-bottom:2px solid #222;}
.classify_table th, .classify_table td{border:1px solid #ddd; padding:5px; font-size:14px;}
.classify_table thead{border-top:2px solid #ccc; border-bottom:2px solid #ccc;}
.classify_table thead th{background-color:#fafafa;}
.classify_table tbody td{text-align:center;}
	.classify_table tbody td.essential{background-color:#f3f3f3; font-weight:bold;}
	.classify_table tbody td.bg_type01{background-color:#e6f0ff;}
	.classify_table tbody td.bg_type02{background-color:#fdeaed;}
	.classify_table tbody td.bg_type03{background-color:#fdfbea;}
	.classify_table tbody td.bg_type04{background-color:#f7f2ed;}
	.classify_table tbody td.bg_type05{background-color:#ecfeed;}
	.classify_table tbody td.bg_type06{background-color:#e7efe9;}
	.classify_table tbody td.bg_type07{background-color:#f7ecfe;}
	.classify_table tbody td.bg_type08{background-color:#e0fcf7;}
	.classify_table tbody td.bg_type09{background-color:#ffe291;}
	.classify_table tbody td.smallf{font-size:12px; padding:5px 0;}
	.classify_table tbody td.text_type01{color:#4365ff;}
	.classify_table tbody td.text_type02, .text_type02{color:#59a824;}
ul.paddingleft li{padding-left: 20px; line-height: 22px; width:calc(100% - 20px);}
</style>
</head>
<body>

<h1>분류표</h1>
<p>※ 업무담당자는 본 표를 참고하여 개별사업을 분류하시기 바랍니다.</p>

	<table class="classify_table">
		<colgroup>
			<col width="4%">
			<col width="10%">
			<col width="10%">
			<col width="10%">
			<col width="10%">
			<col width="12%">
			<col width="12%">
			<col width="10%">
			<col width="10%">
			<col width="12%">
		</colgroup>

		<thead>
			<tr>
				<th rowspan="2">번호</th>
				<th colspan="3" class="bd-L bd-T bd-R">신규 심사조서 분류</th>
				<th class="bd-T bd-R">별도선택</th>
				<th colspan="2">기존 심사조서 분류</th>
				<th colspan="3" class="bd-L bd-T bd-R">집계표 서식대응(시비)</th>
			</tr>
			<tr>
				<th class="bd-L">대분류</th>
				<th>중분류</th>
				<th class="bd-R">소분류</th>
				<th class="bd-R">국고보조</th>
				<th>해당조서</th>
				<th>세부조서</th>
				<th class="bd-L">대분류</th>
				<th>중분류</th>
				<th class="bd-R">소분류</th>
			</tr>
		</thead>

		<tbody>
			<tr>
				<td>1</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type01">경상사업비</td>
				<td>자체경상</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type01">경상사업심사조서</td>
				<td>경상사업심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">그 외 경상비</td>
			</tr>
			<tr>
				<td>2</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type01">경상사업비</td>
				<td>국고경상</td>
				<td class="essential bd-L bd-R">필수선택</td>
				<td class="bg_type01">경상사업심사조서</td>
				<td>-</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01 smallf">경상국비매칭시비</td>
				<td class="text_type01 bd-R">경상국비매칭시비</td>
			</tr>
			<tr>
				<td>2-1</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type01">경상사업비</td>
				<td>국고경상</td>
				<td class="essential bd-L bd-R">국고-일반</td>
				<td class="bg_type08">국고보조심사조서</td>
				<td>일반회계-국고</td>
				<td class="text_type02 bd-L">국고보조사업</td>
				<td class="text_type02">국비+균특+기금</td>
				<td class="text_type02 bd-R">국고보조금-국비</td>
			</tr>
			<tr>
				<td>2-2</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type01">경상사업비</td>
				<td>국고경상</td>
				<td class="essential bd-L bd-R">국고-균특</td>
				<td class="bg_type08">국고보조심사조서</td>
				<td>일반회계-균특</td>
				<td class="text_type02 bd-L">국고보조사업</td>
				<td class="text_type02">국비+균특+기금</td>
				<td class="text_type02 bd-R">균특보조금-국비</td>
			</tr>
			<tr>
				<td>2-3</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type01">경상사업비</td>
				<td>국고경상</td>
				<td class="essential bd-L bd-R">국고-기금</td>
				<td class="bg_type08">국고보조심사조서</td>
				<td>일반회계-기금</td>
				<td class="text_type02 bd-L">국고보조사업</td>
				<td class="text_type02">국비+균특+기금</td>
				<td class="text_type02 bd-R">기금보조금-국비</td>
			</tr>
			<tr>
				<td>2-4</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type01">경상사업비</td>
				<td>국고경상</td>
				<td class="essential bd-L bd-R">국고-기타특별</td>
				<td class="bg_type08">국고보조심사조서</td>
				<td>기타특별회계</td>
				<td class="bd-L">-</td>
				<td>-</td>
				<td class="bd-R">-</td>
			</tr>
			<tr>
				<td>2-5</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type01">경상사업비</td>
				<td>국고경상</td>
				<td class="essential bd-L bd-R">국고-매칭펀드</td>
				<td>-</td>
				<td>-</td>
				<td class="bd-L">-</td>
				<td>-</td>
				<td class="bd-R">-</td>
			</tr>
			<tr>
				<td>3</td>
				<td class="bd-L">투자사업비</td>
				<td class="bg_type02">투자사업비</td>
				<td>자체투자</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type02">투자사업심사조서</td>
				<td>시비</td>
				<td class="text_type01 bd-L">투자사업비</td>
				<td class="text_type01">자체사업</td>
				<td class="text_type01 bd-R">자체사업</td>
			</tr>
			<tr>
				<td>4</td>
				<td class="bd-L">투자사업비</td>
				<td class="bg_type02">투자사업비</td>
				<td>국고투자</td>
				<td class="essential bd-L bd-R">필수선택</td>
				<td class="bg_type02">투자사업심사조서</td>
				<td>국고</td>
				<td class="bd-L">-</td>
				<td>-</td>
				<td class="bd-R">-</td>
			</tr>
			<tr>
				<td>4-1</td>
				<td class="bd-L">투자사업비</td>
				<td class="bg_type02">투자사업비</td>
				<td>국고투자</td>
				<td class="essential bd-L bd-R">국고-일반</td>
				<td class="bg_type02">투자사업심사조서</td>
				<td>일반회계-국고</td>
				<td class="text_type01 bd-L">투자사업비</td>
				<td class="text_type01 smallf">국고투자매칭시비</td>
				<td class="text_type01 bd-R">국고투자매칭시비</td>
			</tr>
			<tr>
				<td>4-2</td>
				<td class="bd-L">투자사업비</td>
				<td class="bg_type02">투자사업비</td>
				<td>국고투자</td>
				<td class="essential bd-L bd-R">국고-균특</td>
				<td class="bg_type02">투자사업심사조서</td>
				<td>일반회계-균특</td>
				<td class="text_type01 bd-L">투자사업비</td>
				<td class="text_type01 smallf">국고투자매칭시비</td>
				<td class="text_type01 bd-R">국고투자매칭시비</td>
			</tr>
			<tr>
				<td>4-3</td>
				<td class="bd-L">투자사업비</td>
				<td class="bg_type02">투자사업비</td>
				<td>국고투자</td>
				<td class="essential bd-L bd-R">국고-기금</td>
				<td class="bg_type02">투자사업심사조서</td>
				<td>일반회계-기금</td>
				<td class="text_type01 bd-L">투자사업비</td>
				<td class="text_type01 smallf">국고투자매칭시비</td>
				<td class="text_type01 bd-R">국고투자매칭시비</td>
			</tr>
			<tr>
				<td>4-4</td>
				<td class="bd-L">투자사업비</td>
				<td class="bg_type02">투자사업비</td>
				<td>국고투자</td>
				<td class="essential bd-L bd-R">국고-기타특별</td>
				<td class="bg_type02">투자사업심사조서</td>
				<td>기타특별회계</td>
				<td class="bd-L">-</td>
				<td>-</td>
				<td class="bd-R">-</td>
			</tr>
			<tr>
				<td>4-5</td>
				<td class="bd-L">투자사업비</td>
				<td class="bg_type02">투자사업비</td>
				<td>국고투자</td>
				<td class="essential bd-L bd-R">국고-매칭펀드</td>
				<td class="bg_type02">투자사업심사조서</td>
				<td>-</td>
				<td class="text_type01 bd-L">투자사업비</td>
				<td class="text_type01 smallf">국비직접(매칭펀드)</td>
				<td class="text_type01 bd-R">국비직접(매칭펀드)</td>
			</tr>
			<tr>
				<td>5</td>
				<td class="bd-L">투자사업비</td>
				<td class="bg_type02">투자사업비</td>
				<td>기타특별회계</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type02">투자사업심사조서</td>
				<td>기타특별회계</td>
				<td class="bd-L">-</td>
				<td>-</td>
				<td class="bd-R">-</td>
			</tr>
			<tr>
				<td>6</td>
				<td class="bd-L">투자사업비</td>
				<td class="bg_type02">투자사업비</td>
				<td>투자비삭감</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type02">투자사업심사조서</td>
				<td>투자비 삭감액</td>
				<td class="text_type01 bd-L">투자사업비</td>
				<td class="text_type01">투자비삭감액</td>
				<td class="text_type01 bd-R">투자비삭감액</td>
			</tr>
			<tr>
				<td>7</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type03">공통경비</td>
				<td>기간제근로자</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type03">공통심사조서</td>
				<td class="smallf">기간제근로자심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">기간제근로자</td>
			</tr>
			<tr>
				<td>8</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type03">공통경비</td>
				<td>포상금</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type03">공통심사조서</td>
				<td>포상금심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">포상금</td>
			</tr>
			<tr>
				<td>9</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type03">공통경비</td>
				<td class="smallf">사회복무요원보상</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type03">공통심사조서</td>
				<td class="smallf">사회복무요원심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">사회복무요원</td>
			</tr>
			<tr>
				<td>10</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type03">공통경비</td>
				<td>위원회수당</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type03">공통심사조서</td>
				<td>위원회수당심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">위원회수당</td>
			</tr>
			<tr>
				<td>11</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type03">공통경비</td>
				<td>워크숍경비</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type03">공통심사조서</td>
				<td>워크숍경비심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">워크숍경비</td>
			</tr>
			<tr>
				<td>12</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type03">공통경비</td>
				<td>시간제임기제</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type03">공통심사조서</td>
				<td class="smallf">시간제임기제심사조서</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">기준인건비</td>
				<td class="text_type01 bd-R">시간제임기제</td>
			</tr>
			<tr>
				<td>13</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type04">기본경비</td>
				<td>기본수용비</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type04">기본경비심사조서</td>
				<td>기본수용비심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 smallf bd-R">사무관리비-기본수용비</td>
			</tr>
			<tr>
				<td>14</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type04">기본경비</td>
				<td>급량비</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type04">기본경비심사조서</td>
				<td>급량비심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">사무관리비-급량비</td>
			</tr>
			<tr>
				<td>15</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type04">기본경비</td>
				<td>당직수당</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type04">기본경비심사조서</td>
				<td>당직수당심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">사무관리비-당직수당</td>
			</tr>
			<tr>
				<td>16</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type04">기본경비</td>
				<td>국내여비</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type04">기본경비심사조서</td>
				<td>국내여비심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">출장여비</td>
			</tr>
			<tr>
				<td>17</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type04">기본경비</td>
				<td>월액여비</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type04">기본경비심사조서</td>
				<td>월액여비심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">월액여비</td>
			</tr>
			<tr>
				<td>18</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type05">시책업무추진비</td>
				<td>시책업무추진비</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type05">시책추진심사조서</td>
				<td class="smallf">시책업무추진비심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">시책업무추진비<br>(시책업추비)</td>
			</tr>
			<tr>
				<td>19</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type06">국외여비</td>
				<td>국외업무여비</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type06">국외여비심사조서</td>
				<td class="smallf">국외업무여비심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">국외업무여비</td>
			</tr>
			<tr>
				<td>20</td>
				<td class="bd-L">경상사업비</td>
				<td class="bg_type06">국외여비</td>
				<td>국제화여비</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type06">국외여비심사조서</td>
				<td>국제화여비심사조서</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">국제화여비</td>
			</tr>
			<tr>
				<td>21</td>
				<td class="bd-L">기준인건비</td>
				<td class="bg_type07">공무직보수</td>
				<td>공무직보수</td>
				<td class="bd-L bd-R">-</td>
				<td class="bg_type07">무기계약심사조서</td>
				<td class="smallf">무기계약근로자심사조서</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">공무직보수</td>
				<td class="text_type01 bd-R">공무직보수</td>
			</tr>
			<tr>
				<td>22</td>
				<td class="bd-L">경상사업비</td>
				<td>예산삭감</td>
				<td>예산삭감</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">경상사업비</td>
				<td class="text_type01">기타경상경비</td>
				<td class="text_type01 bd-R">경상비삭감액</td>
			</tr>
			<tr>
				<td>23</td>
				<td class="bd-L">기준인건비</td>
				<td>일반직</td>
				<td>보수</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>24</td>
				<td class="bd-L">기준인건비</td>
				<td>일반직</td>
				<td>직급보조비</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>25</td>
				<td class="bd-L">기준인건비</td>
				<td>일반직</td>
				<td>성과상여금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>26</td>
				<td class="bd-L">기준인건비</td>
				<td>일반직</td>
				<td>연금부담금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>27</td>
				<td class="bd-L">기준인건비</td>
				<td>일반직</td>
				<td>국민건강보험금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>28</td>
				<td class="bd-L">기준인건비</td>
				<td>전문직</td>
				<td>보수</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>29</td>
				<td class="bd-L">기준인건비</td>
				<td>전문직</td>
				<td>직급보조비</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>30</td>
				<td class="bd-L">기준인건비</td>
				<td>전문직</td>
				<td>성과상여금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>31</td>
				<td class="bd-L">기준인건비</td>
				<td>전문직</td>
				<td>연금부담금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>32</td>
				<td class="bd-L">기준인건비</td>
				<td>전문직</td>
				<td>국민건강보험금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>33</td>
				<td class="bd-L">기준인건비</td>
				<td>청경</td>
				<td>보수</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>34</td>
				<td class="bd-L">기준인건비</td>
				<td>청경</td>
				<td>직급보조비</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>35</td>
				<td class="bd-L">기준인건비</td>
				<td>청경</td>
				<td>성과상여금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>36</td>
				<td class="bd-L">기준인건비</td>
				<td>청경</td>
				<td>연금부담금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>37</td>
				<td class="bd-L">기준인건비</td>
				<td>청경</td>
				<td>국민건강보험금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">기준인건비</td>
				<td class="text_type01">일반직+전문직<br>+청원경찰</td>
				<td class="text_type01 bd-R">일반직+전문직<br>+청원경찰</td>
			</tr>
			<tr>
				<td>38</td>
				<td class="bd-L">채무상환</td>
				<td>지방채상환</td>
				<td>지방채상환</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">채무상환</td>
				<td class="text_type01">지방채상환</td>
				<td class="text_type01 bd-R">지방채상환</td>
			</tr>
			<tr>
				<td>39</td>
				<td class="bd-L">채무상환</td>
				<td>채무부담상환</td>
				<td>채무부담상환</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">채무상환</td>
				<td class="text_type01">채무부담상환</td>
				<td class="text_type01 bd-R">채무부담상환</td>
			</tr>
			<tr>
				<td>40</td>
				<td class="bd-L">채무상환</td>
				<td class="smallf">통합관리기금상환</td>
				<td class="smallf">통합관리기금상환</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">채무상환</td>
				<td class="text_type01 smallf">통합관리기금상환</td>
				<td class="text_type01 bd-R">통합관리기금상환</td>
			</tr>
			<tr>
				<td>41</td>
				<td class="bd-L">예비비</td>
				<td>예비비</td>
				<td>예비비</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">예비비</td>
				<td class="text_type01">예비비</td>
				<td class="text_type01 bd-R">예비비</td>
			</tr>
			<tr>
				<td>42</td>
				<td class="bd-L">교육청전출금</td>
				<td>교육청전출금</td>
				<td>교육청전출금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">법정경비</td>
				<td class="text_type01">교육비특별회계<br>전출금</td>
				<td class="text_type01 bd-R">교육비특별회계<br>전출금</td>
			</tr>
			<tr>
				<td>43</td>
				<td class="bd-L">구군교부금</td>
				<td>징수교부금</td>
				<td>징수교부금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">법정경비</td>
				<td class="text_type01">자치구&middot;군 교부금</td>
				<td class="text_type01 bd-R">징수교부금</td>
			</tr>
			<tr>
				<td>44</td>
				<td class="bd-L">구군교부금</td>
				<td>조정교부금</td>
				<td class="smallf">자치구조정교부금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">법정경비</td>
				<td class="text_type01">자치구&middot;군 교부금</td>
				<td class="text_type01 bd-R">자치구 조정교부금</td>
			</tr>
			<tr>
				<td>45</td>
				<td class="bd-L">구군교부금</td>
				<td>조정교부금</td>
				<td>시&middot;군조정교부금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">법정경비</td>
				<td class="text_type01">자치구&middot;군 교부금</td>
				<td class="text_type01 bd-R">기장군 조정교부금</td>
			</tr>
			<tr>
				<td>46</td>
				<td class="bd-L">기타회계전출금</td>
				<td>기타회계전출금</td>
				<td>기타회계전출금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">법정경비</td>
				<td class="text_type01">전출금(법정)</td>
				<td class="text_type01 bd-R">기타회계전출금</td>
			</tr>
			<tr>
				<td>47</td>
				<td class="bd-L">기금전출금</td>
				<td>기금전출금</td>
				<td>기금전출금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">법정경비</td>
				<td class="text_type01">전출금(법정)</td>
				<td class="text_type01 bd-R">기금전출금</td>
			</tr>
			<tr>
				<td>48</td>
				<td class="bd-L">운수업체보조금</td>
				<td>운수업체보조금</td>
				<td>운수업체보조금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">용도지정경비</td>
				<td class="text_type01">운수업계보조금</td>
				<td class="text_type01 bd-R">운수업계보조금</td>
			</tr>
			<tr>
				<td>49</td>
				<td class="bd-L">국고대여장학금<br>지방세연구원<br>출연금</td>
				<td>국고대여장학금<br>지방세연구원<br>출연금</td>
				<td>국고대여장학금<br>지방세연구원<br>출연금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">법정경비</td>
				<td class="text_type01">전출금(법정)</td>
				<td class="text_type01 bd-R">국고대여장학금<br>지방세연구원 출연금</td>
			</tr>
			<tr>
				<td>50</td>
				<td class="bd-L">용도지정경비</td>
				<td>국고보조반환금</td>
				<td>국고보조반환금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">용도지정경비</td>
				<td class="text_type01">국고보조반환금</td>
				<td class="text_type01 bd-R">국고보조반환금</td>
			</tr>
			<tr>
				<td>51</td>
				<td class="bd-L">용도지정경비</td>
				<td>교육비전입금</td>
				<td>교육비전입금</td>
				<td class="bd-L bd-R">-</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bd-L">용도지정경비</td>
				<td class="text_type01">교육비전입금</td>
				<td class="text_type01 bd-R">교육비전입금</td>
			</tr>
			<tr>
				<td>52</td>
				<td class="bd-L bd-B">일반국비</td>
				<td class="bd-B">일반국비</td>
				<td class="essential bd-B">일반국비</td>
				<td class="essential bd-L bd-R">필수선택</td>
				<td>-</td>
				<td>-</td>
				<td class="bd-L">-</td>
				<td>-</td>
				<td class="bd-R">-</td>
			</tr>
			<tr>
				<td>52-1</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td class="bg_type08">국고보조 선택1</td>
				<td class="essential bd-L bd-R">국고-일반</td>
				<td>국고보조심사조서</td>
				<td>일반회계-국고</td>
				<td class="text_type01 bg_type09 bd-L">국고보조사업</td>
				<td class="text_type01 bg_type09 smallf">일반국비매칭시비</td>
				<td class="text_type01 bg_type09 bd-R">국고보조금-시비</td>
			</tr>
			<tr>
				<td>52-2</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td class="bg_type08">국고보조 선택2</td>
				<td class="essential bd-L bd-R">국고-균특</td>
				<td>국고보조심사조서</td>
				<td>일반회계-균특</td>
				<td class="text_type01 bg_type09 bd-L">국고보조사업</td>
				<td class="text_type01 bg_type09 smallf">일반국비매칭시비</td>
				<td class="text_type01 bg_type09 bd-R">균특보조금-시비</td>
			</tr>
			<tr>
				<td>52-3</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td class="bg_type08">국고보조 선택3</td>
				<td class="essential bd-L bd-R">국고-기금</td>
				<td>국고보조심사조서</td>
				<td>일반회계-기금</td>
				<td class="text_type01 bg_type09 bd-L">국고보조사업</td>
				<td class="text_type01 bg_type09 smallf">일반국비매칭시비</td>
				<td class="text_type01 bg_type09 bd-R">기금보조금-시비</td>
			</tr>
			<tr>
				<td>52-4</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td class="bg_type08">국고보조 선택4</td>
				<td class="essential bd-L bd-R">국고-기타특별</td>
				<td>국고보조심사조서</td>
				<td>기타특별회계</td>
				<td class="text_type01 bg_type09 bd-L">-</td>
				<td class="text_type01 bg_type09">-</td>
				<td class="text_type01 bg_type09 bd-R">-</td>
			</tr>
			<tr>
				<td>52-5</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td class="bg_type08">국고보조 선택5</td>
				<td class="essential bd-L bd-R bd-B">국고-매칭펀드</td>
				<td>-</td>
				<td>-</td>
				<td class="text_type01 bg_type09 bd-L bd-B">투자사업비</td>
				<td class="text_type01 bg_type09 smallf bd-B">국비직접(매칭펀드)</td>
				<td class="text_type01 bg_type09 bd-B bd-R">국비직접(매칭펀드)</td>
			</tr>
		</tbody>
	</table>
<br>
<p>※ 22번-52번의 경우 기준인건비,법정경비,용도지정경비 등으로 예산투입에 대해 심사가 불필요하여 심사조서 서식을 작성하지 않음</p>
<p>※ 국고보조 집계표 대응 조건</p>
<ul class="paddingleft">
	<li>1) 국고-일반 선택시 = 재원별예산액(국고보조금) = <span class="text_type02">국고보조금-국비</span></li>
	<li>2) 국고-균특 선택시 = 재원별예산액(균특보조금) = <span class="text_type02">균특보조금-균특</span></li>
	<li>3) 국비-기금 선택시 = 재원별예산액(기금보조금) = <span class="text_type02">기금보조금-기금</span></li>
	<li>4) 국고-기타특별 선택시 = 집계표 미노출</li>
	<li>5) 국고-매칭펀드 선택시 = 집계표 미노출</li>
</ul>

</body>
</html>