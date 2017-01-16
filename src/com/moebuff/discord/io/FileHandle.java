package com.moebuff.discord.io;

import java.io.File;
import java.net.URI;

/**
 * 简易的文件处理器
 *
 * @author muto
 */
public class FileHandle extends File {
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

    public String path() {
        return getPath().replace('\\', '/');
    }

    public FileHandle child(String name) {
        return new FileHandle(this, name);
    }
}
