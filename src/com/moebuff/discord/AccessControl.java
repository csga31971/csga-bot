package com.moebuff.discord;

import com.moebuff.discord.utils.ExceptionKit;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.EnumSet;

/**
 * 权限控制
 *
 * @author muto
 */
public class AccessControl {
    /**
     * 获取当前工会下，指定用户的所有权限
     *
     * @param user  用户
     * @param guild 工会
     * @return 已启用的权限集
     */
    public static EnumSet<Permissions> getPermissions(IUser user, IGuild guild) {
        EnumSet<Permissions> set = EnumSet.noneOf(Permissions.class);
        user.getRolesForGuild(guild).forEach(
                t -> set.addAll(t.getPermissions())
        );
        return set;
    }

    private IUser user;

    public AccessControl(IUser user) {
        this.user = user;
    }

    /**
     * 验证用户在当前工会下是否拥有指定的权限，若用户没有该权限，则抛出异常。
     *
     * @param guild 当前所在工会
     * @param p     需要验证的权限
     * @throws PermissionException 无权访问
     */
    public void contains(IGuild guild, Permissions p) throws PermissionException {
        for (IRole r : user.getRolesForGuild(guild)) {
            if (r.getPermissions().contains(p)) return;
        }
        throw getException(p);
    }

    private PermissionException getException(Permissions p) {
        return ExceptionKit.format(PermissionException.class, "No %s permission.", p);
    }
}
