package me.alenalex.notaprisoncore.paper.abstracts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.locale.LocaleKey;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.commands.help.CommandHelpProvider;
import me.alenalex.notaprisoncore.paper.constants.Permission;
import me.alenalex.notaprisoncore.paper.data.DataHolder;
import me.alenalex.notaprisoncore.paper.entity.mine.Mine;
import me.alenalex.notaprisoncore.paper.entity.profile.PrisonUserProfile;
import me.alenalex.notaprisoncore.paper.manager.CommandManager;
import me.alenalex.notaprisoncore.paper.manager.ConfigurationManager;
import me.alenalex.notaprisoncore.paper.store.PrisonDataStore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@ToString
public abstract class AbstractCommand {

    private final CommandManager commandManager;

    private final CommandHelpProvider commandHelpProvider;

    public AbstractCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.commandHelpProvider = helpPrompt();
    }

    protected NotAPrisonCore pluginInstance(){
        return this.commandManager.getPrisonManagers().getPluginInstance();
    }
    protected abstract CommandHelpProvider helpPrompt();

    protected void sendLocaleMessage(LocaleKey key){

    }

    protected void sendFormattedMessage(CommandSender sender, String message){
        sender.sendMessage(ChatColor.GRAY+"["+ChatColor.YELLOW+"N"+ChatColor.RED+"P"+ChatColor.WHITE+"Core"+ChatColor.GRAY+"] "+ChatColor.WHITE+"➔  "+ChatColor.GRAY+message);
    }

    protected boolean canRunAdminCommand(CommandSender sender){
        return Permission.anyOf(sender, Permission.ADMIN, Permission.ADMIN_COMMAND);
    }

    protected boolean isAdmin(CommandSender sender){
        return Permission.ADMIN.validate(sender);
    }

    protected boolean canRunAdminCommand(Player player){
        return Permission.anyOf(player, Permission.ADMIN, Permission.ADMIN_COMMAND);
    }

    protected boolean isAdmin(Player player){
        return Permission.ADMIN.validate(player);
    }

    protected boolean canRunAdminCommand(IPrisonUserProfile profile){
        return Permission.anyOf(profile, Permission.ADMIN, Permission.ADMIN_COMMAND);
    }

    protected boolean isAdmin(IPrisonUserProfile profile){
        return Permission.ADMIN.validate(profile);
    }

    protected boolean hasPermission(Player player, Permission permission){
        return permission.validate(player);
    }

    protected boolean hasPermission(CommandSender sender, Permission permission){
        return permission.validate(sender);
    }

    protected boolean hasPermission(IPrisonUserProfile profile, Permission permission){
        return permission.validate(profile);
    }

    protected boolean hasPermission(Player player, String permission){
        return player.hasPermission(permission);
    }

    protected boolean hasPermission(CommandSender sender, String permission){
        return sender.hasPermission(permission);
    }

    protected boolean hasPermission(IPrisonUserProfile profile, String permission){
        Player player = Bukkit.getPlayer(profile.getUserId());
        if(player == null)
            return false;

        return hasPermission(player, permission);
    }

    protected Player asPlayer(CommandSender sender){
        try {
            return (Player) sender;
        }catch (Exception ignored){
            return null;
        }
    }

    protected boolean isPlayer(CommandSender sender){
        return (sender instanceof Player);
    }

    protected boolean isConsole(CommandSender sender){
        return !isPlayer(sender);
    }

    public DataHolder getDataHolder(){
        return this.commandManager.getPrisonManagers().getPluginInstance().getDataHolder();
    }

    @Nullable
    protected Mine getMine(UUID uuid){
        return (Mine) getDataHolder().getMineDataHolder().get(uuid);
    }

    @Nullable
    protected Mine getMineOf(Player player){
        return getMineOf(player.getUniqueId());
    }

    @Nullable
    protected Mine getMineOf(IPrisonUserProfile profile){
        if(!profile.hasMine())
            return null;

        return getMine(profile.getMineId());
    }

    protected Mine getMineOf(UUID playerUid){
        PrisonUserProfile profile = getProfile(playerUid);
        if(profile == null)
            return null;

        return getMineOf(profile);
    }


    protected boolean isPlayerDataLoaded(@NotNull Player player){
        return isPlayerDataLoaded(player.getUniqueId());
    }

    protected boolean isPlayerDataLoaded(UUID playerUid){
        return getDataHolder().getProfileDataHolder().isLoaded(playerUid);
    }

    protected PrisonUserProfile getProfile(@NotNull Player player){
        return getProfile(player.getUniqueId());
    }

    protected PrisonUserProfile getProfile(@NotNull UUID playerUid){
        return (PrisonUserProfile) getDataHolder().getProfileDataHolder().get(playerUid);
    }

    protected ConfigurationManager getConfigurationManager(){
        return (ConfigurationManager) this.commandManager.getPrisonManagers().getConfigurationManager();
    }

    protected PrisonDataStore getDataStore(){
        return this.commandManager.getPrisonManagers().getPluginInstance().getPrisonDataStore();
    }
}
