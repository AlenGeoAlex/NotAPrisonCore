package me.alenalex.notaprisoncore.paper.constants;

import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@ToString
public enum Permission {

    CLAIM_COMMAND_OTHERS(PermissionKeys.CLAIM_OTHERS),
    ADMIN_COMMAND(PermissionKeys.ADMIN_COMMAND),
    ADMIN(PermissionKeys.ADMIN);

    public static class PermissionKeys {
        public static final String CLAIM_OTHERS = "npc.command.claim.others";
        public static final String ADMIN = "npcore.admin";
        public static final String ADMIN_COMMAND = "npcore.command.admin";
    }

    private final String permissionKey;

    Permission(String permissionKey) {
        this.permissionKey = permissionKey;
    }

    public boolean validate(@NotNull Player player){
        return player.hasPermission(this.permissionKey);
    }

    public boolean validate(@NotNull CommandSender sender){
        if(sender instanceof Player)
            return validate((Player) sender);
        else return true;
    }

    public boolean validate(@NotNull IPrisonUserProfile profile){
        UUID userId = profile.getUserId();
        Player player = Bukkit.getPlayer(userId);
        if(player == null)
            return false;

        return validate(player);
    }

    public static boolean anyOf(@NotNull Player player, Permission... permissions){
        if(permissions == null || permissions.length == 0)
            return true;

        for (Permission permission : permissions) {
            if(permission.validate(player))
                return true;
        }

        return false;
    }

    public static boolean anyOf(@NotNull CommandSender sender, Permission... permissions){
        if(permissions == null || permissions.length == 0)
            return true;

        for (Permission permission : permissions) {
            if(permission.validate(sender))
                return true;
        }

        return false;
    }

    public static boolean anyOf(@NotNull IPrisonUserProfile profile, Permission... permissions){
        if(permissions == null || permissions.length == 0)
            return true;

        for (Permission permission : permissions) {
            if(permission.validate(profile))
                return true;
        }

        return false;
    }

    public static boolean allOf(@NotNull Player player, Permission... permissions){
        if(permissions == null)
            return true;

        for (Permission permission : permissions) {
            if(!permission.validate(player))
                return false;
        }

        return true;
    }

    public static boolean allOf(@NotNull CommandSender sender, Permission... permissions){
        if(permissions == null)
            return true;

        for (Permission permission : permissions) {
            if(!permission.validate(sender))
                return false;
        }

        return true;
    }

    public static boolean allOf(@NotNull IPrisonUserProfile profile, Permission... permissions){
        if(permissions == null)
            return true;

        for (Permission permission : permissions) {
            if(!permission.validate(profile))
                return false;
        }

        return true;
    }

    public static boolean anyOf(@NotNull Player player, String... permissions){
        if(permissions == null || permissions.length == 0)
            return true;

        for (String permission : permissions) {
            if(player.hasPermission(permission))
                return true;
        }

        return false;
    }

    public static boolean anyOf(@NotNull CommandSender sender, String... permissions){
        if(!(sender instanceof Player))
            return true;

        return anyOf((Player) sender, permissions);
    }

    public static boolean anyOf(@NotNull IPrisonUserProfile profile, String... permissions){
        Player player = Bukkit.getPlayer(profile.getUserId());
        if(player == null)
            return false;

        return anyOf(player, permissions);
    }

    public static boolean allOf(@NotNull Player player, String... permissions){
        if(permissions == null)
            return true;

        for (String permission : permissions) {
            if(!player.hasPermission(permission))
                return false;
        }

        return true;
    }

    public static boolean allOf(@NotNull CommandSender sender, String... permissions){
        if(!(sender instanceof Player))
            return true;

        return allOf((Player) sender, permissions);
    }

    public static boolean allOf(@NotNull IPrisonUserProfile profile, String... permissions){
        Player player = Bukkit.getPlayer(profile.getUserId());
        if(player == null)
            return false;

        return allOf(player, permissions);
    }


}
