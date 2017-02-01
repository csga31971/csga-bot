package com.moebuff.discord.tuling123;

import com.moebuff.discord.Settings;
import sx.blah.discord.handle.obj.IUser;

/**
 * 图灵工厂
 *
 * @author muto
 */
public class TuringFactory {
    public static Issue getIssue(IUser user) {
        return new BaseIssue(user.getID());
    }

    public static ITuring getApi() {
        return new OpenApi2(Settings.TL_APIKEY, Settings.TL_SECRET);
    }
}
