package com.erlei.tools.file;

public class FileOperateException extends RuntimeException {
    public FileOperateException(String msg, Exception ex) {
        super(msg, ex);
    }

    public FileOperateException(String msg) {
        super(msg);
    }
}
