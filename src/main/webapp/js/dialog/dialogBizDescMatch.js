/**

 * 사업설명서 HWPX/HWP 업로드/삭제 (회계년도+예산차수+실국/전체 단위)

 */

$(document).ready(function () {

    var dialogObj = $("#dialogBizDescMatchDiv");

    if (dialogObj.length === 0) {

        return;

    }



    var OFFICE_CD_ALL = "ALL";



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



    var normalizeOfficeFromFilter = function (officeCd, officeNm) {

        officeCd = (officeCd == null ? "" : String(officeCd)).replace(/^\s+|\s+$/g, "");

        officeNm = (officeNm == null ? "" : String(officeNm)).replace(/^\s+|\s+$/g, "");

        if (officeCd === "null" || officeCd === "undefined") { officeCd = ""; }

        if (officeCd === "" || officeCd === OFFICE_CD_ALL || officeNm === "전체") {

            return { officeCd: OFFICE_CD_ALL, officeNm: "전체", allOffice: true };

        }

        return { officeCd: officeCd, officeNm: officeNm, allOffice: false };

    };



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



        var office = normalizeOfficeFromFilter(getVal("#condOfficeCd"), getText("#condOfficeCd"));

        return {

            fisYear: getVal("#condFisYear"),

            bgtDgr: getVal("#condBgtDgr"),

            officeCd: office.officeCd,

            officeNm: office.officeNm,

            allOffice: office.allOffice

        };

    };



    var syncHiddenFromCtx = function () {

        $("#dialogBizDescMatchFisYear").val(currentCtx.fisYear || "");

        $("#dialogBizDescMatchBgtDgr").val(currentCtx.bgtDgr || "");

        $("#dialogBizDescMatchReportCd").val(currentCtx.reportCd || "");

        $("#dialogBizDescMatchOfficeCd").val(currentCtx.officeCd || "");

        $("#dialogBizDescMatchOfficeNm").val(currentCtx.officeNm || "");

        if (currentCtx.fisYear && currentCtx.bgtDgr && currentCtx.officeCd) {

            var scopeLabel = (currentCtx.officeCd === OFFICE_CD_ALL)

                ? "전체 (모든 실국 공유)"

                : (currentCtx.officeNm + " (" + currentCtx.officeCd + ")");

            $("#dialogBizDescMatchOfficeLabel").text(

                currentCtx.fisYear + "년 / " + currentCtx.bgtDgr + "차 / " + scopeLabel

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



    var syncCheckAllState = function () {

        var $checks = $("#dialogBizDescMatchFileBody input.bizdesc-row-check", dialogObj);

        var total = $checks.length;

        var checked = $checks.filter(":checked").length;

        $("#dialogBizDescMatchCheckAll", dialogObj).prop("checked", total > 0 && total === checked);

    };



    var getSelectedRows = function () {

        var rows = [];

        $("#dialogBizDescMatchFileBody tr", dialogObj).each(function () {

            var $cb = $(this).find("input.bizdesc-row-check");

            if (!$cb.length || !$cb.prop("checked")) { return; }

            rows.push({

                fileId: $cb.data("fileId") || "",

                orgNm: $cb.data("orgNm") || "",

                fileOfficeCd: $cb.data("fileOfficeCd") || ""

            });

        });

        return rows;

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

                officeNm: currentCtx.officeNm,

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

                var emptyMsg = (currentCtx.officeCd === OFFICE_CD_ALL)

                    ? "해당 회계년도·예산차수에 '전체' 공유로 업로드된 사업설명서가 없습니다."

                    : "해당 회계년도·예산차수·실국에 업로드된 사업설명서가 없습니다. (전체 공유 파일 포함)";

                if (!list.length) {

                    $("#dialogBizDescMatchCheckAll", dialogObj).prop("checked", false);

                    $body.append('<tr><td colspan="6">' + emptyMsg + ' 아래에서 파일을 업로드해 주세요.</td></tr>');

                    return;

                }

                for (var i = 0; i < list.length; i++) {

                    var row = list[i];

                    var fileId = row.bizdescFileId || row.bizdesc_file_id || "";

                    var orgNm = row.orgFileNm || row.org_file_nm || "";

                    var rowOfficeCd = row.officeCd || row.office_cd || "";

                    var displayNm = orgNm;

                    if (rowOfficeCd === OFFICE_CD_ALL) {

                        displayNm = "[전체공유] " + orgNm;

                    }

                    var reportCd = row.reportCd || row.report_cd || "";

                    var reportLabel = reportCd === "010" ? "경상" : (reportCd === "020" ? "투자" : reportCd);

                    var bizCnt = row.bizCount != null ? row.bizCount : (row.biz_count || 0);

                    var regiDate = row.regiDate || row.regi_date || "";

                    var regiId = row.regiId || row.regi_id || "";

                    var tr = $("<tr></tr>");

                    var $cb = $('<input type="checkbox" class="bizdesc-row-check"/>');

                    $cb.data("fileId", fileId).data("orgNm", displayNm).data("fileOfficeCd", rowOfficeCd);

                    if (list.length === 1) {

                        $cb.prop("checked", true);

                    }

                    tr.append($("<td style=\"text-align:center;\"></td>").append($cb));

                    tr.append($("<td></td>").text(displayNm));

                    tr.append($("<td></td>").text(reportLabel));

                    tr.append($("<td></td>").text(bizCnt));

                    tr.append($("<td></td>").text(regiDate));

                    tr.append($("<td></td>").text(regiId));

                    $body.append(tr);

                }

                syncCheckAllState();

            }

        });

    };



    $("#dialogBizDescMatchCheckAll", dialogObj).change(function () {

        var checked = $(this).prop("checked");

        $("#dialogBizDescMatchFileBody input.bizdesc-row-check", dialogObj).prop("checked", checked);

    });



    $("#dialogBizDescMatchFileBody", dialogObj).on("change", "input.bizdesc-row-check", function () {

        syncCheckAllState();

    });



    $("#dialogBizDescMatchDeleteBtn", dialogObj).click(function (e) {

        e.preventDefault();

        if (!assertCtxReady()) { return; }

        var selected = getSelectedRows();

        if (!selected.length) {

            $.csAlert({ msg: "삭제할 파일을 목록에서 선택해 주세요." });

            return;

        }

        for (var i = 0; i < selected.length; i++) {

            if (selected[i].fileOfficeCd === OFFICE_CD_ALL && currentCtx.officeCd !== OFFICE_CD_ALL) {

                $.csAlert({

                    msg: "전체 공유 사업설명서는 조회조건 '실국=전체'에서만 삭제할 수 있습니다."

                });

                return;

            }

        }

        var names = [];

        for (var j = 0; j < selected.length; j++) {

            names.push(selected[j].orgNm);

        }

        $.csConfirm({

            msg: "선택한 " + selected.length + "개 파일을 삭제하시겠습니까?\n"

                + names.join("\n")

                + "\n(관련 매칭도 함께 삭제됩니다)",

            callBack: function (p) {

                if (p.confirmData != "Y") { return; }

                var failMsg = "";

                for (var k = 0; k < selected.length; k++) {

                    var data = $.csAjaxCall({

                        url: "/bizdesc/ajaxBizDescFileDelete.do",

                        data: {

                            bizdescFileId: selected[k].fileId,

                            officeCd: currentCtx.officeCd,

                            officeNm: currentCtx.officeNm

                        },

                        async: false

                    });

                    if (isEmpty(data) || data[BCJIS_RETURN_CODE] != "SUCC") {

                        failMsg = (data && data.bcjisMessage) ? data.bcjisMessage : "삭제 실패";

                        break;

                    }

                }

                if (failMsg) {

                    $.csAlert({ msg: failMsg });

                    return;

                }

                loadList();

            }

        });

    });



    $("#dialogBizDescMatchUploadBtn", dialogObj).click(function (e) {

        e.preventDefault();

        var live = readFilterFromReport();

        if (live.fisYear) { currentCtx.fisYear = live.fisYear; }

        if (live.bgtDgr) { currentCtx.bgtDgr = live.bgtDgr; }

        currentCtx.officeCd = live.officeCd;

        currentCtx.officeNm = live.officeNm;

        syncHiddenFromCtx();

        if (!assertCtxReady()) { return; }

        var fileEl = document.getElementById("dialogBizDescMatchFile");

        if (!fileEl || !fileEl.files || fileEl.files.length === 0) {

            $.csAlert({ msg: "HWPX/HWP 파일을 선택해 주세요." });

            return;

        }

        var file = fileEl.files[0];

        var name = (file.name || "").toLowerCase();

        if (name && !(name.indexOf(".hwpx") >= 0 || name.indexOf(".hwp") >= 0)) {

            $.csAlert({

                msg: "사업설명서는 .hwpx 또는 .hwp 파일만 업로드할 수 있습니다.\n선택 파일: " + file.name

            });

            return;

        }



        $("#dialogBizDescMatchStatus", dialogObj).text(

            "업로드 중... (" + file.name + ", " + Math.max(1, Math.round(file.size / 1024 / 1024)) + "MB)"

        );



        var formData = new FormData();

        formData.append("hwpxFile", file, file.name);

        formData.append("fisYear", currentCtx.fisYear || "");

        formData.append("bgtDgr", currentCtx.bgtDgr || "");

        formData.append("reportCd", currentCtx.reportCd || "");

        formData.append("officeCd", currentCtx.officeCd || "");

        formData.append("officeNm", currentCtx.officeNm || "");



        var uploadUrl = (typeof ctx === "string" ? ctx : "")

            + "/bizdesc/ajaxBizDescFileUpload.do"

            + "?fisYear=" + encodeURIComponent(currentCtx.fisYear || "")

            + "&bgtDgr=" + encodeURIComponent(currentCtx.bgtDgr || "")

            + "&reportCd=" + encodeURIComponent(currentCtx.reportCd || "")

            + "&officeCd=" + encodeURIComponent(currentCtx.officeCd || "")

            + "&officeNm=" + encodeURIComponent(currentCtx.officeNm || "");



        var xhr = new XMLHttpRequest();

        xhr.open("POST", uploadUrl, true);

        xhr.timeout = 600000;

        xhr.onload = function () {

            $("#dialogBizDescMatchStatus", dialogObj).text("");

            try { fileEl.value = ""; } catch (ignore) {}

            var data = null;

            try {

                var text = (xhr.responseText || "").replace(/^\uFEFF/, "").replace(/^[\s\u00A0]+/, "");

                var m = text.match(/\{[\s\S]*\}/);

                data = JSON.parse(m ? m[0] : text);

            } catch (parseErr) {

                $.csAlert({

                    msg: "업로드 응답을 해석하지 못했습니다. (HTTP " + xhr.status + ")\n"

                        + "다시 시도하거나 파일을 HWPX로 저장해 올려 주세요."

                });

                return;

            }

            if (isEmpty(data) || data[BCJIS_RETURN_CODE] != "SUCC") {

                $.csAlert({

                    msg: (data && data.bcjisMessage) ? data.bcjisMessage : "업로드에 실패하였습니다."

                });

                return;

            }

            var cnt = (data.data && data.data.bizCount != null) ? data.data.bizCount : "";

            var scopeHint = (currentCtx.officeCd === OFFICE_CD_ALL)

                ? "\n(모든 실국에서 사용 가능한 전체 공유 파일입니다.)" : "";

            $.csAlert({

                msg: (data.bcjisMessage || "업로드되었습니다.") + (cnt !== "" ? " (사업 " + cnt + "건)" : "") + scopeHint

            });

            loadList();

        };

        xhr.onerror = function () {

            $("#dialogBizDescMatchStatus", dialogObj).text("");

            $.csAlert({ msg: "업로드 중 네트워크 오류가 발생하였습니다." });

        };

        xhr.ontimeout = function () {

            $("#dialogBizDescMatchStatus", dialogObj).text("");

            $.csAlert({ msg: "업로드 시간이 초과되었습니다. 파일 크기·네트워크를 확인 후 다시 시도해 주세요." });

        };

        xhr.upload.onprogress = function (ev) {

            if (!ev.lengthComputable) { return; }

            var pct = Math.min(99, Math.round((ev.loaded / ev.total) * 100));

            $("#dialogBizDescMatchStatus", dialogObj).text(

                "업로드 중... " + pct + "% (" + file.name + ")"

            );

        };

        xhr.send(formData);

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

        var selected = getSelectedRows();

        if (!selected.length) {

            $.csAlert({ msg: "보낼 파일을 목록에서 선택해 주세요." });

            return;

        }

        var fileIds = [];

        for (var i = 0; i < selected.length; i++) {

            fileIds.push(selected[i].fileId);

        }

        $("#dialogBizDescMatchStatus", dialogObj).text("JSON 보내기 중...");

        var exportHttpFailed = false;

        $.csAjaxCall({

            url: "/bizdesc/ajaxBizDescExportJson.do",

            data: {

                fisYear: currentCtx.fisYear,

                bgtDgr: currentCtx.bgtDgr,

                officeCd: currentCtx.officeCd,

                officeNm: currentCtx.officeNm,

                reportCd: currentCtx.reportCd,

                bizdescFileIds: fileIds

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

                        + (fc !== "" ? "<br>선택파일 " + fc + "개" : "")

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

     * params 가 없어도 조서 화면 조회조건에서 회계년도·예산차수·실국(전체 포함)을 다시 읽는다.

     */

    window.openDialogBizDescMatch = function (params) {

        params = params || {};

        var live = readFilterFromReport();



        var fisYear = params.fisYear || live.fisYear || "";

        var bgtDgr = params.bgtDgr || live.bgtDgr || "";

        var office = normalizeOfficeFromFilter(

            params.officeCd != null ? params.officeCd : live.officeCd,

            params.officeNm != null ? params.officeNm : live.officeNm

        );



        if (live.fisYear) { fisYear = live.fisYear; }

        if (live.bgtDgr) { bgtDgr = live.bgtDgr; }

        if (live.officeCd) {

            office.officeCd = live.officeCd;

            office.officeNm = live.officeNm;

        }



        if (!office.officeCd) {

            var $hid = $(".ui-tabs-panel:visible #bizDescOfficeCd");

            if ($hid.length && $hid.val()) {

                office = normalizeOfficeFromFilter(

                    String($hid.val()).replace(/^\s+|\s+$/g, ""),

                    String($(".ui-tabs-panel:visible #bizDescOfficeNm").val() || $hid.val()).replace(/^\s+|\s+$/g, "")

                );

            }

        }



        var missing = [];

        if (!fisYear) { missing.push("회계년도"); }

        if (!bgtDgr) { missing.push("예산차수"); }

        if (!office.officeCd) { missing.push("실국"); }

        if (missing.length) {

            $.csAlert({

                msg: "조회조건 '" + missing.join("', '") + "'을(를) 선택한 뒤 사업설명서를 불러와 주세요."

            });

            return;

        }



        currentCtx.fisYear = fisYear;

        currentCtx.bgtDgr = bgtDgr;

        currentCtx.reportCd = params.reportCd || "";

        currentCtx.officeCd = office.officeCd;

        currentCtx.officeNm = office.officeNm;



        syncHiddenFromCtx();

        $("#dialogBizDescMatchFile").val("");

        $("#dialogBizDescMatchStatus", dialogObj).text("");

        var titleOffice = (office.officeCd === OFFICE_CD_ALL) ? "전체" : office.officeNm;

        dialogObj.dialog("option", "title",

            "사업설명서불러오기 - " + fisYear + "/" + bgtDgr + "차/" + titleOffice);

        dialogObj.dialog("open");

        loadList();

    };

});


