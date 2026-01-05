package org.example.assignment.util;

public final class FileNameUtil {
    private FileNameUtil() {}

    public static String extractLastExtension(String filename) {
        if (filename == null) return "";

        String f = filename.trim();

        // 경로 제거
        f = f.replace("\\", "/");
        int slash = f.lastIndexOf('/');
        if (slash >= 0) f = f.substring(slash + 1);
        //이중 확장자 방지
        int dot = f.lastIndexOf('.');
        if (dot < 0 || dot == f.length() - 1) return "";
        //소문자로 최종 확장자 통일
        return f.substring(dot + 1).trim().toLowerCase();
    }
}
