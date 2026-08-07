package com.cs.bcjis.bizdesc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * 구형 HWP(OLE Compound)에서 본문 텍스트를 문단 단위로 추출한다.
 * <p>
 * 시 보안솔루션 해제 후 확장자만 .hwpx이고 내부는 HWP 5.x OLE인 경우가 많다.
 */
public class HwpOleTextExtractor {

    private static final int HWPTAG_BEGIN = 0x10;
    private static final int PARA_HEADER = HWPTAG_BEGIN + 50; // 66
    private static final int PARA_TEXT = HWPTAG_BEGIN + 51; // 67

    public List<String> extract(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        try {
            return extract(fis);
        } finally {
            fis.close();
        }
    }

    public List<String> extract(InputStream in) throws Exception {
        POIFSFileSystem fs = new POIFSFileSystem(in);
        try {
            boolean compressed = isCompressed(fs.getRoot());
            List<String> sectionPaths = new ArrayList<String>();
            walkSections(fs.getRoot(), "", sectionPaths);
            Collections.sort(sectionPaths);

            List<String> all = new ArrayList<String>();
            for (String sp : sectionPaths) {
                DocumentEntry de = resolve(fs.getRoot(), sp);
                DocumentInputStream dis = new DocumentInputStream(de);
                byte[] raw;
                try {
                    raw = readAll(dis);
                } finally {
                    dis.close();
                }
                byte[] data = compressed ? inflateRaw(raw) : raw;
                List<String> paras = extractSection(data);
                for (String p : paras) {
                    String t = p == null ? "" : p.replace('\u00a0', ' ').replaceAll("\\s+", " ").trim();
                    if (t.length() > 0) {
                        all.add(t);
                    }
                }
            }
            return all;
        } finally {
            // POI 3.9 POIFSFileSystem has no close(); stream closed by caller
        }
    }

    private boolean isCompressed(DirectoryEntry root) throws IOException {
        if (!root.hasEntry("FileHeader")) {
            return true;
        }
        DocumentEntry de = (DocumentEntry) root.getEntry("FileHeader");
        DocumentInputStream dis = new DocumentInputStream(de);
        byte[] fh;
        try {
            fh = readAll(dis);
        } finally {
            dis.close();
        }
        if (fh.length < 40) {
            return true;
        }
        int flags = (fh[36] & 0xff) | ((fh[37] & 0xff) << 8)
                | ((fh[38] & 0xff) << 16) | ((fh[39] & 0xff) << 24);
        return (flags & 0x01) != 0;
    }

    private void walkSections(DirectoryEntry dir, String path, List<String> sectionPaths) throws IOException {
        for (Iterator<?> it = dir.getEntries(); it.hasNext();) {
            Entry e = (Entry) it.next();
            String p = path + "/" + e.getName();
            if (e.isDirectoryEntry()) {
                walkSections((DirectoryEntry) e, p, sectionPaths);
            } else if (p.indexOf("BodyText") >= 0 && e.getName().startsWith("Section")) {
                sectionPaths.add(p);
            }
        }
    }

    private DocumentEntry resolve(DirectoryEntry root, String fullPath) throws IOException {
        String[] parts = fullPath.split("/");
        DirectoryEntry cur = root;
        for (int i = 1; i < parts.length - 1; i++) {
            if (parts[i].length() == 0) {
                continue;
            }
            cur = (DirectoryEntry) cur.getEntry(parts[i]);
        }
        return (DocumentEntry) cur.getEntry(parts[parts.length - 1]);
    }

    private List<String> extractSection(byte[] sectionBytes) {
        List<String> paras = new ArrayList<String>();
        StringBuilder cur = null;
        int p = 0;
        int n = sectionBytes.length;
        while (p + 4 <= n) {
            int header = (sectionBytes[p] & 0xff)
                    | ((sectionBytes[p + 1] & 0xff) << 8)
                    | ((sectionBytes[p + 2] & 0xff) << 16)
                    | ((sectionBytes[p + 3] & 0xff) << 24);
            p += 4;
            int tag = header & 0x3FF;
            int size = (header >> 20) & 0xFFF;
            if (size == 0xFFF) {
                if (p + 4 > n) {
                    break;
                }
                size = (sectionBytes[p] & 0xff)
                        | ((sectionBytes[p + 1] & 0xff) << 8)
                        | ((sectionBytes[p + 2] & 0xff) << 16)
                        | ((sectionBytes[p + 3] & 0xff) << 24);
                p += 4;
            }
            if (size < 0 || p + size > n) {
                break;
            }
            if (tag == PARA_HEADER) {
                if (cur != null) {
                    paras.add(cur.toString());
                }
                cur = new StringBuilder();
            } else if (tag == PARA_TEXT) {
                if (cur == null) {
                    cur = new StringBuilder();
                }
                cur.append(parseParaText(sectionBytes, p, size));
            }
            p += size;
        }
        if (cur != null) {
            paras.add(cur.toString());
        }
        return paras;
    }

    private String parseParaText(byte[] data, int off, int len) {
        StringBuilder sb = new StringBuilder();
        int end = off + len;
        int p = off;
        while (p + 1 < end) {
            int code = (data[p] & 0xff) | ((data[p + 1] & 0xff) << 8);
            p += 2;
            if (code < 32) {
                int span = controlSpan(code);
                int skip = (span - 1) * 2;
                if (p + skip <= end) {
                    p += skip;
                } else {
                    p = end;
                }
                continue;
            }
            sb.append((char) code);
        }
        return sb.toString();
    }

    private int controlSpan(int code) {
        switch (code) {
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
        case 11:
        case 12:
        case 14:
        case 15:
        case 16:
        case 17:
        case 18:
        case 19:
        case 20:
        case 21:
        case 22:
        case 23:
            return 8;
        default:
            return 1;
        }
    }

    private byte[] inflateRaw(byte[] compressed) throws IOException {
        Inflater inflater = new Inflater(true);
        InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(compressed), inflater);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        try {
            while ((n = iis.read(buf)) >= 0) {
                bos.write(buf, 0, n);
            }
        } catch (IOException e) {
            if (bos.size() == 0) {
                throw e;
            }
        } finally {
            try {
                iis.close();
            } catch (Exception ignore) {
            }
            inflater.end();
        }
        return bos.toByteArray();
    }

    private byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) >= 0) {
            bos.write(buf, 0, n);
        }
        return bos.toByteArray();
    }
}
