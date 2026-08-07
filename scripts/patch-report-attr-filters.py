# -*- coding: utf-8 -*-
"""Add advncProc filter to ReportWrite010 list + indiAttr filters to ReportWrite020."""
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
XML010 = ROOT / "src/main/resources/csframework/sqlmap/cubrid/report/ReportWrite010.xml"
XML020 = ROOT / "src/main/resources/csframework/sqlmap/cubrid/report/ReportWrite020.xml"

ADVNC_BLOCK = (
    '                                 <isNotEmpty prepend="" property="advncProc">\n'
    "                                   AND (SELECT COUNT(*) FROM TB_REPORT WHERE 1=1 AND FIS_YEAR = A.FIS_YEAR"
    " AND BGT_DGR = A.BGT_DGR AND TE_BGT_COMPO_ID = A.TE_BGT_COMPO_ID"
    " AND ADVNC_PROC LIKE '%' || #advncProc# || '%') <![CDATA[>]]> 0\n"
    "                                 </isNotEmpty>\n"
)

INDI_BLOCK = (
    '                                         <isNotEmpty prepend="" property="indiAttr">\n'
    "\t\t\t\t\t\t\t          \tAND (SELECT COUNT(*) FROM TB_REPORT WHERE 1=1 AND FIS_YEAR = A.FIS_YEAR"
    " AND BGT_DGR = A.BGT_DGR AND TE_BGT_COMPO_ID = A.TE_BGT_COMPO_ID"
    " AND INDI_ATTR LIKE '%' || #indiAttr# || '%') <![CDATA[>]]> 0\n"
    "\t\t\t\t\t\t\t         </isNotEmpty>\n"
)


def patch_010():
    text = XML010.read_text(encoding="utf-8")
    marker = 'id="ReportWrite010.selectReport010List"'
    idx = text.find(marker)
    if idx < 0:
        raise SystemExit("selectReport010List not found")
    end = text.find("<select ", idx + 10)
    if end < 0:
        end = text.find("</sqlMap>", idx)
    chunk = text[idx:end]
    if 'property="advncProc"' in chunk:
        print("010 selectReport010List: advncProc already present")
        return
    needle = (
        "                                 </isEqual>\n"
        "                                 GROUP BY DECODE(H.CT, 1, A.FIS_YEAR || '_' || TO_CHAR(A.BGT_DGR, '000')"
        " || '_' || E.REPORT_DETL_CD || '_' || '0000' || '_' || '0000000' || '_' || '0000000000000000'"
        " || '_' || '00000000000'"
    )
    if needle not in chunk:
        raise SystemExit("010 needle not found in selectReport010List")
    insert = "                                 </isEqual>\n" + ADVNC_BLOCK + needle.split("\n", 1)[1]
    # rebuild carefully
    insert = (
        "                                 </isEqual>\n"
        + ADVNC_BLOCK
        + "                                 GROUP BY DECODE(H.CT, 1, A.FIS_YEAR || '_' || TO_CHAR(A.BGT_DGR, '000')"
        " || '_' || E.REPORT_DETL_CD || '_' || '0000' || '_' || '0000000' || '_' || '0000000000000000'"
        " || '_' || '00000000000'"
    )
    chunk2 = chunk.replace(needle, insert, 1)
    XML010.write_text(text[:idx] + chunk2 + text[end:], encoding="utf-8")
    print("010: injected advncProc into selectReport010List")


def patch_020():
    text = XML020.read_text(encoding="utf-8")
    # Match advncProc isNotEmpty blocks (various indentations)
    pat = re.compile(
        r'(<isNotEmpty prepend="" property="advncProc">\s*'
        r'AND \(SELECT COUNT\(\*\) FROM TB_REPORT WHERE 1=1 AND FIS_YEAR = A\.FIS_YEAR'
        r' AND BGT_DGR = A\.BGT_DGR AND TE_BGT_COMPO_ID = A\.TE_BGT_COMPO_ID'
        r" AND ADVNC_PROC LIKE '%' \|\| #advncProc# \|\| '%'\) <!\[CDATA\[>\]\]> 0\s*"
        r"</isNotEmpty>)"
        r'(?!\s*<isNotEmpty prepend="" property="indiAttr">)',
        re.M,
    )

    def repl(m):
        return m.group(1) + "\n" + INDI_BLOCK

    text2, cnt = pat.subn(repl, text)
    XML020.write_text(text2, encoding="utf-8")
    print("020: injected indiAttr after advncProc blocks:", cnt)


if __name__ == "__main__":
    patch_010()
    patch_020()
