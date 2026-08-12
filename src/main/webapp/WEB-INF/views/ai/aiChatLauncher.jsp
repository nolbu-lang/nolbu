<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%--
  AI 예산편성 도우미 시작 버튼
  - 항상 표시 (숨기지 않음)
  - 파란 상태 BAR / 로그아웃 버튼 위치에 맞춤
--%>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/ai/aiChat.css?v=20260806d" />
<style type="text/css">
.ui-layout-pane-north,
.ui-layout-north,
#mainNorth {
  overflow: visible !important;
}
.always_top:after { display:block; content:""; clear:both; }
.always_top .right { white-space: nowrap; }

/* BAR 안 원본은 자리 확보만 (투명) */
#aiChatOpenBtnSlot,
#globalAbortBtnSlot {
  display: inline-block !important;
  visibility: hidden !important;
  pointer-events: none !important;
  margin: 0 8px !important;
  padding: 4px 14px !important;
  border: 1px solid transparent !important;
  font-size: 12px !important;
  font-weight: bold !important;
  line-height: 1.35 !important;
  white-space: nowrap !important;
}

/* 실제 클릭 버튼 — 기본 보이도록 */
#aiChatOpenBtn.ai-chat-fab {
  display: inline-block !important;
  visibility: visible !important;
  opacity: 1 !important;
  position: fixed !important;
  top: 18px;
  right: 110px;
  left: auto;
  z-index: 2147483000 !important;
  margin: 0 !important;
  padding: 4px 14px !important;
  background: #e67e22 !important;
  color: #fff !important;
  border: 1px solid #f0a030 !important;
  border-radius: 4px !important;
  font-size: 12px !important;
  font-weight: bold !important;
  line-height: 1.35 !important;
  text-decoration: none !important;
  white-space: nowrap !important;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,.22);
  box-sizing: border-box;
}
#aiChatOpenBtn.ai-chat-fab:hover {
  background: #d35400 !important;
  border-color: #d35400 !important;
}
</style>
<script src="${pageContext.request.contextPath}/js/ai/aiChat.js?v=20260812b"></script>
<script type="text/javascript">
(function () {
  var SLOT_ID = "aiChatOpenBtnSlot";
  var ABORT_SLOT_ID = "globalAbortBtnSlot";

  function ensureFab() {
    if (!window.jQuery) { return null; }

    var $fab = jQuery("a#aiChatOpenBtn.ai-chat-fab");
    if ($fab.length) {
      return $fab.first();
    }

    // body에 이미 있으면 fab로 사용
    var $bodyBtn = jQuery("body > #aiChatOpenBtn");
    if ($bodyBtn.length) {
      $bodyBtn.addClass("ai-chat-fab");
      return $bodyBtn.first();
    }

    var $orig = jQuery("#aiChatOpenBtn").first();
    if ($orig.length && $orig.parent()[0] !== document.body) {
      $orig.attr("id", SLOT_ID).attr("aria-hidden", "true");
    }

    var $clone = jQuery(
      '<a href="javascript:void(0);" id="aiChatOpenBtn" class="ai-chat-fab" title="AI 예산편성 도우미">AI 예산도우미</a>'
    );
    jQuery(document.body).append($clone);
    return $clone;
  }

  function ensureAbortFab() {
    if (!window.jQuery) { return null; }

    var $fab = jQuery("a#globalAbortBtn.abort-fab");
    if ($fab.length) {
      return $fab.first();
    }

    var $bodyBtn = jQuery("body > #globalAbortBtn");
    if ($bodyBtn.length) {
      $bodyBtn.addClass("abort-fab btnClass");
      return $bodyBtn.first();
    }

    var $orig = jQuery("#globalAbortBtn").first();
    if ($orig.length && $orig.parent()[0] !== document.body) {
      $orig.attr("id", ABORT_SLOT_ID).attr("aria-hidden", "true");
    }

    var $clone = jQuery(
      '<a href="javascript:void(0);" id="globalAbortBtn" class="abort-fab btnClass" title="진행 중인 조회/저장 작업을 중단합니다">실행중단</a>'
    );
    $clone.on("click", function (e) {
      e.preventDefault();
      if (typeof jQuery.csAbortAllAjax === "function") {
        jQuery.csAbortAllAjax();
      }
    });
    jQuery(document.body).append($clone);
    return $clone;
  }

  function pickAnchor() {
    // 1) 로그아웃 버튼 (가장 확실)
    var logout = document.getElementById("mainNorthLogoutBtn");
    if (logout) {
      var lr = logout.getBoundingClientRect();
      if (lr.width > 0 && lr.height > 0) {
        return { type: "logout", rect: lr, el: logout };
      }
    }
    // 2) 슬롯
    var slot = document.getElementById(SLOT_ID);
    if (slot) {
      var sr = slot.getBoundingClientRect();
      if (sr.width > 0) {
        return { type: "slot", rect: sr, el: slot };
      }
    }
    // 3) 상태 BAR
    var bars = document.querySelectorAll(".always_top");
    for (var i = 0; i < bars.length; i++) {
      var br = bars[i].getBoundingClientRect();
      if (br.width > 10 && br.height > 5) {
        return { type: "bar", rect: br, el: bars[i] };
      }
    }
    return null;
  }

  function placeFixedBtn($btn, top, left, right) {
    if (!$btn || !$btn.length) { return; }
    var fabW = $btn.outerWidth() || 90;
    if (left !== null) {
      if (left < 8) { left = 8; }
      var maxL = (window.innerWidth || 1200) - fabW - 8;
      if (left > maxL) { left = maxL; }
    }
    if (top < 4) { top = 4; }
    var css = {
      display: "inline-block",
      visibility: "visible",
      opacity: 1,
      position: "fixed",
      top: top + "px",
      zIndex: 2147483000
    };
    if (left !== null) {
      css.left = left + "px";
      css.right = "auto";
    } else {
      css.right = right + "px";
      css.left = "auto";
    }
    $btn.css(css);
  }

  function syncAiBtnToBar() {
    if (!window.jQuery) { return; }
    var $fab = ensureFab();
    var $abort = ensureAbortFab();
    if (!$fab || !$fab.length) { return; }

    var fabW = $fab.outerWidth() || 110;
    var fabH = $fab.outerHeight() || 26;
    var abortW = ($abort && $abort.length) ? ($abort.outerWidth() || 80) : 0;
    var gap = 8;
    var top = 18;
    var left = null;
    var right = 110;

    var anchor = pickAnchor();
    if (anchor) {
      var r = anchor.rect;
      if (anchor.type === "logout") {
        top = Math.round(r.top + (r.height - fabH) / 2);
        left = Math.round(r.left - fabW - gap);
        right = null;
      } else if (anchor.type === "slot") {
        top = Math.round(r.top + (r.height - fabH) / 2);
        left = Math.round(r.left);
        right = null;
      } else if (anchor.type === "bar") {
        top = Math.round(r.top + (r.height - fabH) / 2);
        left = Math.round(r.right - fabW - 16);
        right = null;
      }
    }

    placeFixedBtn($fab, top, left, right);

    // 실행중단: AI 도우미 왼쪽 (가려지지 않도록 fixed)
    if ($abort && $abort.length) {
      var abortLeft = null;
      var abortRight = null;
      if (left !== null) {
        abortLeft = left - abortW - gap;
      } else {
        abortRight = (right || 110) + fabW + gap;
      }
      placeFixedBtn($abort, top, abortLeft, abortRight);
    }
  }

  function bind() {
    if (!window.jQuery) { return; }
    ensureFab();
    ensureAbortFab();
    syncAiBtnToBar();

    // FAB 클릭은 전역 openAiBudgetHelper 로만 연다 (기존 창 focus / 대화 유지)
    jQuery(document).off("click.aifabopen");
    jQuery(document).on("click.aifabopen", "#aiChatOpenBtn, a.ai-chat-fab", function (e) {
      e.preventDefault();
      e.stopPropagation();
      if (typeof window.openAiBudgetHelper === "function") {
        window.openAiBudgetHelper();
      }
      return false;
    });

    // 심사정보시스템 종료/이동 시 AI 창도 종료
    jQuery(window).off("beforeunload.aifabclose unload.aifabclose pagehide.aifabclose");
    jQuery(window).on("beforeunload.aifabclose unload.aifabclose pagehide.aifabclose", function () {
      try {
        var w = window.__aiChatPopupWin;
        if (w && !w.closed) { w.close(); }
      } catch (ignore) {}
    });
    jQuery(document).off("click.aifablogout");
    jQuery(document).on("click.aifablogout", "#mainNorthLogoutBtn", function () {
      try {
        var w = window.__aiChatPopupWin;
        if (w && !w.closed) { w.close(); }
      } catch (ignore2) {}
    });

    jQuery(window).on("resize.aifab load.aifab", syncAiBtnToBar);
    jQuery(document).on(
      "click.aifab tabsactivate tabsselect mouseup.aifab",
      "#mainTabs01, #tab_slide_ul, .tab_wrap, .ui-layout-resizer, .ui-layout-toggler",
      function () { setTimeout(syncAiBtnToBar, 50); }
    );

    // layout 초기화·탭 로딩 동안 몇 초간 자주 맞춤
    var n = 0;
    var t1 = setInterval(function () {
      syncAiBtnToBar();
      if (++n > 60) { clearInterval(t1); }
    }, 200);
    // 이후에도 BAR 위치 추적
    setInterval(syncAiBtnToBar, 500);
  }

  if (window.jQuery) {
    jQuery(bind);
  } else {
    document.addEventListener("DOMContentLoaded", function () {
      var w = setInterval(function () {
        if (window.jQuery) {
          clearInterval(w);
          jQuery(bind);
        }
      }, 50);
    });
  }
})();
</script>
