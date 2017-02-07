package com.moebuff.discord.io;

import com.moebuff.discord.reflect.FieldKit;
import com.moebuff.discord.reflect.MemberUtils;
import com.moebuff.discord.utils.UnhandledException;
import org.apache.commons.io.FilenameUtils;

import java.lang.reflect.Field;

/**
 * File Folder
 *
 * @author muto
 */
public class FF {
    public static FileHandle CACHE;
    public static FileHandle SONGS;

    public static final FileHandle ROOT;

    static {
        ROOT = new FileHandle(FileKit.RUNTIMEDIR, ".csga-bot");

        for (Field f : FF.class.getFields()) {
            if (MemberUtils.isFinal(f)) continue;

            FileHandle dir = ROOT.child(f.getName().toLowerCase());
            if (!dir.exists()) {
                UnhandledException.validate(dir.mkdirs(), "mkdirs失败，无法创建 %s", dir.path());
            }
            //在运行android时会多出$change属性，这个报错会让你一脸蒙蔽，据说只会出现在IDEA中。目前已知的解决办法
            //是关掉Instant Run，具体位置在File->Settings->Build,Execution,Deployment->Instant Run
            FieldKit.writeField(f, null, dir);
        }
    }

    /**
     * @return 根目录规范化的绝对路径名字符串
     */
    public static String getRootCanonicalPath() {
        return FilenameUtils.getFullPathNoEndSeparator(FileKit.getAbsolutePath(ROOT));
    }
}
