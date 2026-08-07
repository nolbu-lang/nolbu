package com.cs.bcjis.ai;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 관리자가 globals.properties 에 등록한 외부자료 URL을 읽어 텍스트로 변환한다.
 *
 * Globals.AiExternalSourceUrls — 쉼표 또는 줄바꿈으로 복수 URL
 * Globals.AiExternalMaxCharsPerSource — URL당 최대 문자 수 (기본 20000)
 * Globals.AiExternalFetchTimeoutMs — 연결/읽기 타임아웃 ms (기본 15000)
 */
@Component("aiExternalSourceFetcher")
public class AiExternalSourceFetcher {

    private static final Logger logger = Logger.getLogger(AiExternalSourceFetcher.class);

    private static final Pattern HTML_TAG = Pattern.compile("<[^>]+>");
    private static final Pattern HTML_SCRIPT = Pattern.compile(
            "(?is)<script[^>]*>.*?</script>|(?is)<style[^>]*>.*?</style>");
    private static final Pattern MULTI_SPACE = Pattern.compile("[ \\t\\x0B\\f\\r]+");
    private static final Pattern MULTI_NL = Pattern.compile("\\n{3,}");

    @Autowired
    @Qualifier("config")
    private Properties config;

    public List<String> getConfiguredUrls() {
        List<String> urls = new ArrayList<String>();
        String raw = config.getProperty("Globals.AiExternalSourceUrls", "");
        if (raw == null || raw.trim().length() == 0) {
            return urls;
        }
        String[] parts = raw.split("[,\\r\\n]+");
        for (int i = 0; i < parts.length; i++) {
            String u = parts[i].trim();
            if (u.length() == 0 || u.startsWith("#")) {
                continue;
            }
            urls.add(u);
        }
        return urls;
    }

    /**
     * 등록된 URL 본문을 합쳐 LLM 컨텍스트 문자열을 만든다.
     * 실패 URL은 메모만 남긴다.
     */
    public String buildContextForLlm() {
        List<String> urls = getConfiguredUrls();
        if (urls.isEmpty()) {
            return "";
        }
        int maxChars = getIntProp("Globals.AiExternalMaxCharsPerSource", 20000);
        StringBuilder sb = new StringBuilder();
        int ok = 0;
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            sb.append("\n===== 외부자료 ").append(i + 1).append(": ").append(url).append(" =====\n");
            try {
                FetchResult fr = fetch(url, maxChars);
                if (fr.text != null && fr.text.length() > 0) {
                    sb.append(fr.text);
                    if (fr.truncated) {
                        sb.append("\n...(이하 생략)...\n");
                    }
                    ok++;
                } else {
                    sb.append("(본문을 추출하지 못했습니다");
                    if (fr.note != null && fr.note.length() > 0) {
                        sb.append(": ").append(fr.note);
                    }
                    sb.append(")\n");
                }
            } catch (Exception e) {
                logger.warn("외부자료 fetch 실패 url=" + url + " : " + e.getMessage());
                sb.append("(가져오기 실패: ").append(e.getMessage()).append(")\n");
            }
        }
        if (ok == 0) {
            return "";
        }
        return sb.toString();
    }

    private FetchResult fetch(String urlStr, int maxChars) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int timeout = getIntProp("Globals.AiExternalFetchTimeoutMs", 15000);
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setRequestProperty("Accept", "*/*");
            conn.setInstanceFollowRedirects(true);

            int code = conn.getResponseCode();
            if (code >= 400) {
                throw new RuntimeException("HTTP " + code);
            }

            String contentType = conn.getContentType();
            if (contentType == null) {
                contentType = "";
            }
            String ctLower = contentType.toLowerCase(Locale.ENGLISH);
            String pathLower = url.getPath() == null ? "" : url.getPath().toLowerCase(Locale.ENGLISH);

            InputStream is = conn.getInputStream();
            if (ctLower.indexOf("pdf") >= 0 || pathLower.endsWith(".pdf")) {
                FetchResult pdf = tryExtractPdf(is, maxChars);
                if (pdf.text != null && pdf.text.length() > 0) {
                    return pdf;
                }
                FetchResult empty = new FetchResult();
                empty.note = "PDF 텍스트 추출 불가(라이브러리 없음). URL만 참고하세요.";
                return empty;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, resolveCharset(contentType)));
            StringBuilder raw = new StringBuilder();
            char[] buf = new char[4096];
            int n;
            int softLimit = Math.max(maxChars * 4, 80000);
            while ((n = reader.read(buf)) >= 0) {
                raw.append(buf, 0, n);
                if (raw.length() >= softLimit) {
                    break;
                }
            }
            reader.close();

            String text = toPlainText(raw.toString(), ctLower);
            FetchResult fr = new FetchResult();
            if (text.length() > maxChars) {
                fr.text = text.substring(0, maxChars);
                fr.truncated = true;
            } else {
                fr.text = text;
            }
            return fr;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private FetchResult tryExtractPdf(InputStream is, int maxChars) {
        FetchResult fr = new FetchResult();
        try {
            // 선택적 PDFBox 사용 (classpath에 있을 때만)
            Class<?> pdDocCls = Class.forName("org.apache.pdfbox.pdmodel.PDDocument");
            Class<?> stripperCls = Class.forName("org.apache.pdfbox.text.PDFTextStripper");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            int limit = 8 * 1024 * 1024;
            while ((n = is.read(buf)) >= 0) {
                bos.write(buf, 0, n);
                if (bos.size() >= limit) {
                    break;
                }
            }
            Object doc = pdDocCls.getMethod("load", byte[].class).invoke(null, new Object[] { bos.toByteArray() });
            try {
                Object stripper = stripperCls.newInstance();
                String text = (String) stripperCls.getMethod("getText", pdDocCls).invoke(stripper, doc);
                if (text == null) {
                    text = "";
                }
                text = MULTI_NL.matcher(text.replace('\r', '\n')).replaceAll("\n\n").trim();
                if (text.length() > maxChars) {
                    fr.text = text.substring(0, maxChars);
                    fr.truncated = true;
                } else {
                    fr.text = text;
                }
            } finally {
                pdDocCls.getMethod("close").invoke(doc);
            }
        } catch (ClassNotFoundException e) {
            fr.note = "PDFBox 미포함";
        } catch (Exception e) {
            fr.note = e.getMessage();
            logger.warn("PDF 추출 실패: " + e.getMessage());
        }
        return fr;
    }

    private String toPlainText(String raw, String contentTypeLower) {
        if (raw == null) {
            return "";
        }
        String s = raw;
        if (contentTypeLower.indexOf("html") >= 0 || s.indexOf("<html") >= 0 || s.indexOf("<HTML") >= 0
                || s.indexOf("<body") >= 0 || s.indexOf("<div") >= 0) {
            s = HTML_SCRIPT.matcher(s).replaceAll(" ");
            s = HTML_TAG.matcher(s).replaceAll(" ");
            s = s.replace("&nbsp;", " ").replace("&amp;", "&").replace("&lt;", "<")
                    .replace("&gt;", ">").replace("&quot;", "\"");
        }
        s = s.replace('\r', '\n');
        s = MULTI_SPACE.matcher(s).replaceAll(" ");
        s = MULTI_NL.matcher(s).replaceAll("\n\n");
        return s.trim();
    }

    private String resolveCharset(String contentType) {
        if (contentType != null) {
            String lower = contentType.toLowerCase(Locale.ENGLISH);
            int idx = lower.indexOf("charset=");
            if (idx >= 0) {
                String cs = contentType.substring(idx + 8).trim();
                int semi = cs.indexOf(';');
                if (semi > 0) {
                    cs = cs.substring(0, semi).trim();
                }
                cs = cs.replace("\"", "");
                if (cs.length() > 0) {
                    return cs;
                }
            }
        }
        return "UTF-8";
    }

    private int getIntProp(String key, int defaultValue) {
        try {
            String v = config.getProperty(key);
            if (v != null && v.trim().length() > 0) {
                return Integer.parseInt(v.trim());
            }
        } catch (Exception e) {
            // ignore
        }
        return defaultValue;
    }

    private static class FetchResult {
        String text = "";
        String note = "";
        boolean truncated;
    }
}
