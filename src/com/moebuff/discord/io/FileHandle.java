package com.moebuff.discord.io;

import com.moebuff.discord.reflect.FieldKit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;

/**
 * 简易的文件处理器
 *
 * @author muto
 */
public class FileHandle extends File {
    private static int DEFAULT_BUFFER_SIZE = 8192;

    static {
        DEFAULT_BUFFER_SIZE = (int) FieldKit.readStaticField(
                BufferedInputStream.class,
                "DEFAULT_BUFFER_SIZE");
    }

    public FileHandle(String pathname) {
        super(pathname);
    }

    public FileHandle(String parent, String child) {
        super(parent, child);
    }

    public FileHandle(File parent, String child) {
        super(parent, child);
    }

    public FileHandle(URI uri) {
        super(uri);
    }

    /**
     * @return 文件的路径名，反斜杠将被左斜杠代替。
     */
    public String path() {
        return getPath().replace('\\', '/');
    }

    /**
     * Returns a handle to the child with the specified name.
     */
    public FileHandle child(String name) {
        return new FileHandle(this, name);
    }

    /**
     * Returns a stream for reading this file as bytes.
     */
    public InputStream read() {
        return FileKit.openInputStream(this);
    }

    /**
     * Returns a buffered stream for reading this file as bytes.
     * If {@code size <= 0} then use the {@code DEFAULT_BUFFER_SIZE} in this stream.
     */
    public BufferedInputStream read(int size) {
        return new BufferedInputStream(read(), size <= 0 ? DEFAULT_BUFFER_SIZE : size);
    }
}
