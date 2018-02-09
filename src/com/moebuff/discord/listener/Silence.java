package com.moebuff.discord.listener;

import sx.blah.discord.handle.impl.obj.Guild;
import sx.blah.discord.handle.obj.*;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 *
 * 由于是利用channel对role的权限进行控制的，所以首先要做的
 * 就是设置狗管理的role和被禁言的role。好蛋疼啊
 *
 */
public class Silence {

    private static IRole dog_admin = null;
    private static IRole silenced = null;
    private static IChannel freeChannel = null;

    static void setDogRole(IGuild guild, IChannel channel, IUser author, List<IRole> mentionedRoles){
        if(!author.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)){
            channel.sendMessage("only Administrator could manage dog-admins.");
            return;
        }
        if(mentionedRoles.size() > 1){
            channel.sendMessage("please mention **only one** role.");
            return;
        }
        if(mentionedRoles == null || mentionedRoles.size() == 0){
            channel.sendMessage("removed `" + dog_admin.getName() + "` role from dog-admins.");
            dog_admin = null;
            return;
        }
        dog_admin = mentionedRoles.get(0);
        if(dog_admin.isEveryoneRole()){
            channel.sendMessage("wait, what do you want to do ? :poop");
            return;
        }
        channel.sendMessage("role `" + dog_admin.getName() + "` is dog-admin now.");
    }

    static void updog(IGuild guild, IChannel channel, IUser author, List<IUser> mentionedUsers){
        //上狗，好恶俗
        if(dog_admin == null){
            channel.sendMessage("use `%setdogrole [@role]` to set a role for dog-admins plz.");
            return;
        }
        if(!author.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)){
            channel.sendMessage("only guild owner could manage dog-admins.");
            return;
        }
        for(IUser u:mentionedUsers){
            u.addRole(dog_admin);
        }
    }

    static void downdog(IGuild guild, IChannel channel, IUser author, List<IUser> mentionedUsers){
        if(!author.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)
                || !(author == guild.getOwner())){
            channel.sendMessage("only Administrator could manage dog-admins.");
            return;
        }
        if(dog_admin == null){
            channel.sendMessage("use `%setdogrole [@role]` to set a role for dog-admins plz.");
            return;
        }
        for(IUser u:mentionedUsers){
            if(u.getRolesForGuild(guild).contains(dog_admin))
                u.removeRole(dog_admin);
            else
                channel.sendMessage("`" + u.getName() + "` is not dog-admin.");
        }
    }

    static void setSilenceRole(IGuild guild, IChannel channel, IUser author, List<IRole> mentionedRoles){
        if(!author.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)){
            channel.sendMessage("only Administrator could manage silenced role.");
            return;
        }
        if(mentionedRoles.size() > 1){
            channel.sendMessage("please mention **only one** role.");
            return;
        }
        silenced = mentionedRoles.get(0);
        if(silenced == null){
            channel.sendMessage("the silenced role seems so empty :( please mention one role after your command.");
            return;
        }
        if(silenced.isEveryoneRole()){
            channel.sendMessage("wait, what do you want to do ? :poop:");
            return;
        }
        channel.sendMessage("role `" + silenced.getName() + "` is silenced role now.");
    }

    static void silence(IGuild guild, IChannel channel, IUser author, List<IUser> mentionedUsers){
        //需要先设置silenced role才行，好烦啊
        //dog-admin，administrator，频道创建者拥有禁言权限
        if(!(author.getRolesForGuild(guild).contains(dog_admin)
                || author.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)
                || author == guild.getOwner())){
            channel.sendMessage("you need to be a dog-admin or admin to sleep others.");
            return;
        }
        if(silenced == null){
            channel.sendMessage("use `%setsilencerole [@role]` to set a role for silenced users plz.");
            return;
        }
        List<IChannel> channelList = guild.getChannels();
        EnumSet<Permissions> permissions = EnumSet.noneOf(Permissions.class);
        permissions.add(Permissions.SEND_MESSAGES);
        for(IChannel ic:channelList){
            ic.overrideRolePermissions(silenced, null, permissions);
            if(freeChannel != null)
                freeChannel.overrideRolePermissions(silenced, permissions, null);
        }
        for(IUser u : mentionedUsers){
            u.addRole(silenced);
        }
        channel.sendMessage("silenced.");
    }

    static void wake(IGuild guild, IChannel channel, IUser author, List<IUser> mentionedUsers){
        //dog-admin，administrator，频道创建者拥有解禁言权限
        if(!(author.getRolesForGuild(guild).contains(dog_admin)
                || author.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)
                || author == guild.getOwner())){
            channel.sendMessage("you need to be a dog-admin or admin to wake others.");
            return;
        }

        for(IUser u : mentionedUsers){
            u.removeRole(silenced);
        }
        channel.sendMessage("waked.");
    }

    static void setFreeChannel(IGuild guild, IChannel channel, IUser author){
        //被禁言的人只能在freeChannel说话，好可怜啊
        if(!author.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)){
            channel.sendMessage("only Administrator could manage free channels.");
            return;
        }
        if(silenced == null){
            channel.sendMessage("use `%setsilencerole [@role]` to set a role for silenced users plz.");
            return;
        }
        freeChannel = channel;
        freeChannel.overrideRolePermissions(silenced, guild.getEveryoneRole().getPermissions(), silenced.getPermissions());
        channel.sendMessage(channel.getName() + " is now free channel.");
    }

    static void reset(IGuild guild, IChannel channel){
        //取消所有狗管理，解除所有禁言，调试用
        //需要先设置freeChannel和两个role
        List<IChannel> channelList = guild.getChannels();
        IRole silenced = guild.getRolesByName("Silenced").get(0);
        EnumSet<Permissions> permissions = guild.getEveryoneRole().getPermissions();
        permissions.remove(Permissions.SEND_MESSAGES);
        permissions.remove(Permissions.VOICE_CONNECT);
        IMessage msg = channel.sendMessage("resetting...");
        for(IChannel ic:channelList){
            ic.overrideRolePermissions(silenced, guild.getEveryoneRole().getPermissions(), null);
        }
        List<IUser> userList = guild.getUsers();
        for(IUser u : userList){
            if(silenced!=null && u.getRolesForGuild(guild).contains(silenced))
                u.removeRole(silenced);
            if(dog_admin!=null && u.getRolesForGuild(guild).contains(dog_admin))
                u.removeRole(dog_admin);
        }
        msg.edit("reset done.");
    }
}
