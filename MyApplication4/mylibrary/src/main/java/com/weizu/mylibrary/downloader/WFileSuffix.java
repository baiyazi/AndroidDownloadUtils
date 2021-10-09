package com.weizu.mylibrary.downloader;

/**
 * 下载支持的文件类型后缀
 */
public enum WFileSuffix{
    EXE("exe"),
    ZIP("zip"),
    JPEG("jpeg"),
    PNG("png"),
    GIF("gif"),
    MP4("mp4"),
    MP3("mp3"),
    PDF("pdf");

    private String value; // 实际值
    WFileSuffix(String value) {
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
