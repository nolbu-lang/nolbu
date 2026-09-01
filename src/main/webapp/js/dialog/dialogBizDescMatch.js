/**
 * 사업설명서 HWPX/HWP 업로드/삭제 (회계년도+예산차수+실국 단위)
 */
$(document).ready(function () {
    var dialogObj = $("#dialogBizDescMatchDiv");
    if (dialogObj.length === 0) {
        return;
    }

    var currentCtx = {
        fisYear: "",
        bgtDgr: "",
        reportCd: "",
        officeCd: "",
        officeNm: ""
    };

    dialogObj.dialog({
        autoOpen: false,
        modal: true,
        width: 760,
        height: 540,
        resizable: true
    });

    var readFilterFromReport = function () {
        var $panel = $(".ui-tabs-panel:visible");
        var getVal = function (sel) {
            var el = $panel.find(sel).get(0) || $(sel).filter(":visible").get(0) || $(sel).get(0);
            if (!el) { return ""; }
            var v = "";
            if (el.options && el.selectedIndex >= 0) {
                v = el.options[el.selectedIndex].value;
            } else {
                v = el.value || "";
            }
            v = (v == null ? "" : String(v)).replace(/^\s+|\s+$/g, "");
            if (v === "null" || v === "undefined") { return ""; }
            return v;
        };
        var getText = function (sel) {
            var el = $panel.find(sel).get(0) || $(sel).filter(":visible").get(0) || $(sel).get(0);
            if (!el || !el.options || el.selectedIndex < 0) { return ""; }
            return String(el.options[el.selectedIndex].text || "").replace(/^\s+|\s+$/g, "");
        };

        var officeCd = getVal("#condOfficeCd");
        var officeNm = getText("#condOfficeCd");
        if (officeCd === "" || officeNm === "전체") {
            officeCd = "";
        }
        return {
            fisYear: getVal("#condFisYear"),
            bgtDgr: getVal("#condBgtDgr"),
            officeCd: officeCd,
            officeNm: officeNm
        };
    };

    var syncHiddenFromCtx = function () {
        $("#dialogBizDescMatchFisYear").val(currentCtx.fisYear || "");
        $("#dialogBizDescMatchBgtDgr").val(currentCtx.bgtDgr || "");
        $("#dialogBizDescMatchReportCd").val(currentCtx.reportCd || "");
        $("#dialogBizDescMatchOfficeCd").val(currentCtx.officeCd || "");
        $("#dialogBizDescMatchOfficeNm").val(currentCtx.officeNm || "");
        if (currentCtx.fisYear && currentCtx.bgtDgr && currentCtx.officeCd) {
            $("#dialogBizDescMatchOfficeLabel").text(
                currentCtx.fisYear + "년 / " + currentCtx.bgtDgr + "차 / "
                + currentCtx.officeNm + " (" + currentCtx.officeCd + ")"
            );
            $("#dialogBizDescMatchOfficeRow").show();
        } else {
            $("#dialogBizDescMatchOfficeLabel").text("-");
            $("#dialogBizDescMatchOfficeRow").hide();
        }
    };

    var assertCtxReady = function () {
        var missing = [];
        if (!currentCtx.fisYear) { missing.push("회계년도"); }
        if (!currentCtx.bgtDgr) { missing.push("예산차수"); }
        if (!currentCtx.officeCd) { missing.push("실국"); }
        if (missing.length) {
            $.csAlert({
                msg: "조회조건 '" + missing.join("', '") + "'을(를) 선택한 뒤 다시 열어 주세요."
            });
            return false;
        }
        return true;
    };

    var loadList = function () {
        syncHiddenFromCtx();
        if (!assertCtxReady()) { return; }
        $("#dialogBizDescMatchStatus", dialogObj).text("조회 중...");
        $.csAjaxCall({
            url: "/bizdesc/ajaxBizDescFileList.do",
            data: {
                fisYear: currentCtx.fisYear,
                bgtDgr: currentCtx.bgtDgr,
                officeCd: currentCtx.officeCd,
                reportCd: currentCtx.reportCd
            },
            async: true,
            callBack: function (data) {
                $("#dialogBizDescMatchStatus", dialogObj).text("");
                if (isEmpty(data) || data[BCJIS_RETURN_CODE] != "SUCC") {
                    $.csAlert({ msg: (data && data.bcjisMessage) ? data.bcjisMessage : "목록 조회에 실패하였습니다." });
                    return;
                }
                var list = data.dataList || [];
                var $body = $("#dialogBizDescMatchFileBody", dialogObj).empty();
                if (!list.length) {
                    $body.append('<tr><td colspan="6">해당 회계년도·예산차수·실국에 업로드된 사업설명서가 없습니다. 아래에서 파일을 업로드해 주세요.</td></tr>');
                    return;
                }
                for (var i = 0; i < list.length; i++) {
                    var row = list[i];
                    var fileId = row.bizdescFileId || row.bizdesc_file_id || "";
                    var orgNm = row.orgFileNm || row.org_file_nm || "";
                    var reportCd = row.reportCd || row.report_cd || "";
                    var reportLabel = reportCd === "010" ? "경상" : (reportCd === "020" ? "투자" : reportCd);
                    var bizCnt = row.bizCount != null ? row.bizCount : (row.biz_count || 0);
                    var regiDate = row.regiDate || row.regi_date || "";
                    var regiId = row.regiId || row.regi_id || "";
                    var tr = $("<tr></tr>");
                    tr.append($("<td></td>").text(orgNm));
                    tr.append($("<td></td>").text(reportLabel));
                    tr.append($("<td></td>").text(bizCnt));
                    tr.append($("<td></td>").text(regiDate));
                    tr.append($("<td></td>").text(regiId));
                    var $del = $('<a href="#" class="btnClass">삭제</a>');
                    $del.data("fileId", fileId).data("orgNm", orgNm);
                    tr.append($("<td></td>").append($del));
                    $body.append(tr);
                }
            }
        });
    };

    $("#dialogBizDescMatchFileBody", dialogObj).on("click", "a", function (e) {
        e.preventDefault();
        var fileId = $(this).data("fileId");
        var orgNm = $(this).data("orgNm");
        $.csConfirm({
            msg: "[" + orgNm + "] 파일을 삭제하시겠습니까?\n(관련 매칭도 함께 삭제됩니다)",
            callBack: function (p) {
                if (p.confirmData != "Y") { return; }
                $.csAjaxCall({
                    url: "/bizdesc/ajaxBizDescFileDelete.do",
                    data: {
                        bizdescFileId: fileId,
                        officeCd: currentCtx.officeCd
                    },
                    async: true,
                    callBack: function (data) {
                        if (isEmpty(data) || data[BCJIS_RETURN_CODE] != "SUCC") {
                            $.csAlert({ msg: (data && data.bcjisMessage) ? data.bcjisMessage : "삭제 실패" });
                            return;
                        }
                        loadList();
                    }
                });
            }
        });
    });

    $("#dialogBizDescMatchUploadBtn", dialogObj).click(function (e) {
        e.preventDefault();
        var live = readFilterFromReport();
        if (live.fisYear) { currentCtx.fisYear = live.fisYear; }
        if (live.bgtDgr) { currentCtx.bgtDgr = live.bgtDgr; }
        if (live.officeCd) {
            currentCtx.officeCd = live.officeCd;
            currentCtx.officeNm = live.officeNm;
        }
        syncHiddenFromCtx();
        if (!assertCtxReady()) { return; }
        var fileInput = $("#dialogBizDescMatchFile");
        if (!fileInput.val()) {
            $.csAlert({ msg: "HWPX/HWP 파일을 선택해 주세요." });
            return;
        }
        $("#dialogBizDescMatchStatus", dialogObj).text("업로드 중... (파일 크기에 따라 수 분 소요될 수 있습니다)");

        var uploadUrl = "/bizdesc/ajaxBizDescFileUpload.do"
            + "?fisYear=" + encodeURIComponent(currentCtx.fisYear || "")
            + "&bgtDgr=" + encodeURIComponent(currentCtx.bgtDgr || "")
            + "&reportCd=" + encodeURIComponent(currentCtx.reportCd || "")
            + "&officeCd=" + encodeURIComponent(currentCtx.officeCd || "")
            + "&officeNm=" + encodeURIComponent(currentCtx.officeNm || "");

        $.bcjisFileAjaxCall({
            url: uploadUrl,
            fileElementId: "dialogBizDescMatchFile",
            dataType: "json",
            data: {
                fisYear: currentCtx.fisYear,
                bgtDgr: currentCtx.bgtDgr,
                reportCd: currentCtx.reportCd,
                officeCd: currentCtx.officeCd,
                officeNm: currentCtx.officeNm
            },
            timeout: 600000,
            callBack: function (data) {
                $("#dialogBizDescMatchStatus", dialogObj).text("");
                try {
                    $("#dialogBizDescMatchFile").val("");
                } catch (ignore) {}
                if (isEmpty(data) || data[BCJIS_RETURN_CODE] != "SUCC") {
                    $.csAlert({
                        msg: (data && data.bcjisMessage) ? data.bcjisMessage : "업로드에 실패하였습니다."
                    });
                    return;
                }
                var cnt = (data.data && data.data.bizCount != null) ? data.data.bizCount : "";
                $.csAlert({
                    msg: (data.bcjisMessage || "업로드되었습니다.") + (cnt !== "" ? " (사업 " + cnt + "건)" : "")
                });
                loadList();
            },
            error: function () {
                $("#dialogBizDescMatchStatus", dialogObj).text("");
                $.csAlert({ msg: "업로드 중 오류가 발생하였습니다." });
            }
        });
    });

    $("#dialogBizDescMatchRefreshBtn", dialogObj).click(function (e) {
        e.preventDefault();
        loadList();
    });

    var downloadJsonFile = function (fileName, contentObj) {
        var text = typeof contentObj === "string" ? contentObj : JSON.stringify(contentObj, null, 2);
        var blob = new Blob([text], { type: "application/json;charset=utf-8" });
        var url = (window.URL || window.webkitURL).createObjectURL(blob);
        var a = document.createElement("a");
        a.href = url;
        a.download = fileName || "bizdesc.json";
        document.body.appendChild(a);
        a.click();
        setTimeout(function () {
            document.body.removeChild(a);
            try { (window.URL || window.webkitURL).revokeObjectURL(url); } catch (ignore) {}
        }, 100);
    };

    $("#dialogBizDescMatchExportBtn", dialogObj).click(function (e) {
        e.preventDefault();
        if (!assertCtxReady()) { return; }
        $("#dialogBizDescMatchStatus", dialogObj).text("JSON 내보내기 중...");
        var exportHttpFailed = false;
        $.csAjaxCall({
            url: "/bizdesc/ajaxBizDescExportJson.do",
            data: {
                fisYear: currentCtx.fisYear,
                bgtDgr: currentCtx.bgtDgr,
                officeCd: currentCtx.officeCd,
                officeNm: currentCtx.officeNm,
                reportCd: currentCtx.reportCd
            },
            async: true,
            callBack: function (data) {
                $("#dialogBizDescMatchStatus", dialogObj).text("");
                if (exportHttpFailed) { return; }
                if (isEmpty(data) || data[BCJIS_RETURN_CODE] != "SUCC") {
                    var msg = (data && data.bcjisMessage) ? data.bcjisMessage
                        : "JSON 내보내기에 실패하였습니다.<br>Tomcat 재시작 후 다시 시도해 주세요.";
                    $.csAlert({ msg: msg });
                    return;
                }
                var payload = data.data || {};
                var fileName = payload.fileName || "사업설명서.json";
                var content = payload.content;
                if (!content) {
                    $.csAlert({ msg: "내보낼 내용이 없습니다." });
                    return;
                }
                try {
                    downloadJsonFile(fileName, content);
                } catch (err) {
                    $.csAlert({ msg: "파일 저장 중 오류가 발생하였습니다." });
                    return;
                }
                var fc = payload.fileCount != null ? payload.fileCount : "";
                var bc = payload.bizCount != null ? payload.bizCount : "";
                $.csAlert({
                    msg: "JSON 파일을 PC에 저장했습니다.<br>"
                        + "파일: " + fileName
                        + (fc !== "" ? "<br>업로드파일 " + fc + "개" : "")
                        + (bc !== "" ? " / 사업 " + bc + "건" : "")
                });
            },
            error: function (xhr, st, err) {
                exportHttpFailed = true;
                $("#dialogBizDescMatchStatus", dialogObj).text("");
                var detail = (xhr && xhr.status) ? ("HTTP " + xhr.status) : (st || err || "");
                $.csAlert({
                    msg: "JSON 내보내기 요청에 실패하였습니다.<br>"
                        + detail
                        + "<br>Tomcat 재시작 후 다시 시도해 주세요."
                });
            }
        });
    });

    $("#dialogBizDescMatchCloseBtn", dialogObj).click(function (e) {
        e.preventDefault();
        dialogObj.dialog("close");
    });

    /**
     * 사업설명서 업로드 창 열기.
     * params 가 없어도 조서 화면 조회조건에서 회계년도·예산차수·실국을 다시 읽는다.
     */
    window.openDialogBizDescMatch = function (params) {
        params = params || {};
        var live = readFilterFromReport();

        var fisYear = params.fisYear || live.fisYear || "";
        var bgtDgr = params.bgtDgr || live.bgtDgr || "";
        var officeCd = params.officeCd == null ? "" : String(params.officeCd).replace(/^\s+|\s+$/g, "");
        var officeNm = params.officeNm == null ? "" : String(params.officeNm).replace(/^\s+|\s+$/g, "");

        if (live.officeCd) {
            officeCd = live.officeCd;
            officeNm = live.officeNm;
        }
        if (live.fisYear) { fisYear = live.fisYear; }
        if (live.bgtDgr) { bgtDgr = live.bgtDgr; }

        if (!officeCd) {
            var $hid = $(".ui-tabs-panel:visible #bizDescOfficeCd");
            if ($hid.length && $hid.val()) {
                officeCd = String($hid.val()).replace(/^\s+|\s+$/g, "");
                officeNm = String($(".ui-tabs-panel:visible #bizDescOfficeNm").val() || officeCd).replace(/^\s+|\s+$/g, "");
            }
        }

        var missing = [];
        if (!fisYear) { missing.push("회계년도"); }
        if (!bgtDgr) { missing.push("예산차수"); }
        if (!officeCd || officeCd === "null" || officeCd === "undefined" || officeNm === "전체") {
            missing.push("실국");
            officeCd = "";
        }
        if (missing.length) {
            $.csAlert({
                msg: "조회조건 '" + missing.join("', '") + "'을(를) 선택한 뒤 사업설명서를 불러와 주세요."
            });
            return;
        }

        currentCtx.fisYear = fisYear;
        currentCtx.bgtDgr = bgtDgr;
        currentCtx.reportCd = params.reportCd || "";
        currentCtx.officeCd = officeCd;
        currentCtx.officeNm = officeNm;

        syncHiddenFromCtx();
        $("#dialogBizDescMatchFile").val("");
        $("#dialogBizDescMatchStatus", dialogObj).text("");
        dialogObj.dialog("option", "title",
            "사업설명서불러오기 - " + fisYear + "/" + bgtDgr + "차/" + officeNm);
        dialogObj.dialog("open");
        loadList();
    };
});
