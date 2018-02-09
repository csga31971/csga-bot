package com.moebuff.discord.tuling123;

import com.moebuff.discord.Settings;
import com.moebuff.discord.reflect.ClassKit;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;

/**
 * 图灵工厂
 *
 * @author muto
 */
public class TuringFactory {
    private static final List<Class> API_CLASSES = new ArrayList<>();

    static {
        Class[] classes = ClassKit.getClasses("com.moebuff.discord.tuling123");
        for (Class c : classes) {
            if (c.getSuperclass() == BaseApi.class) API_CLASSES.add(c);
        }
    }

    public static Issue getIssue(IUser user) {
        return new BaseIssue(user.getStringID());
    }

    public static ITuring getApi() {
        //noinspection unchecked
        for (Class<? extends BaseApi> clazz : API_CLASSES) {
            BaseApi api = ClassKit.newInstance(clazz,
                    Settings.TL_APIKEY, Settings.TL_SECRET);
            if (api.isAvailable()) return api;
        }

        throw new NullPointerException("尚无可用的图灵接口。");
    }
}
