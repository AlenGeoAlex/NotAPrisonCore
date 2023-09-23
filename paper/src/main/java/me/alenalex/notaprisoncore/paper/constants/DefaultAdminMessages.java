package me.alenalex.notaprisoncore.paper.constants;

import org.bukkit.ChatColor;

public class DefaultAdminMessages {

    private static final String PREFIX = ChatColor.GRAY+"["+ChatColor.YELLOW+"N"+ChatColor.RED+"P"+ChatColor.WHITE+"Core"+ChatColor.GRAY+"] "+ChatColor.WHITE+"➔  "+ChatColor.GRAY;
    public static final String NO_SCHEMATIC_FOUND = ChatColor.translateAlternateColorCodes('&', PREFIX+"&cNo schematic with the provided name has been found in the cache");
    public static final String FAILED_TO_GENERATE_MINE_META = ChatColor.translateAlternateColorCodes('&', PREFIX+"&cFailed to generate the mine. Check for errors on console");
    public static final String INVALID_MINE_GENERATION_COUNT = ChatColor.translateAlternateColorCodes('&', PREFIX+"&cPlease provide a valid mine count to generate");
    public static final String INVALID_COOL_DOWN_INTERVAL = ChatColor.translateAlternateColorCodes('&', PREFIX+"&cPlease provide a cool-down of minimum 200 ticks (10 sec)");
    public static final String GENERATION_IN_PROGRESS = ChatColor.translateAlternateColorCodes('&', PREFIX+"&cA pasting is already in progress...");
    public static final String PASTE_COMPLETE = ChatColor.translateAlternateColorCodes('&', PREFIX+"The paste has been completed and the records are added onto database. Once all complete, shutdown the server and then copy the world to any other existing servers and the start the server to acquire locks");

    public static final String FAILURE_KICK_MESSAGE = ChatColor.translateAlternateColorCodes('&', "&cFailed to load your prison data! Please retry or contact admins after multiple try");
    public static class MineCommand{
        public static final String PROFILE_NOT_LOADED_SENDER = ChatColor.translateAlternateColorCodes('&', PREFIX+"&cThe profile for the provided player is not yet loaded!!");
        public static final String PROFILE_NOT_LOADED = ChatColor.translateAlternateColorCodes('&', PREFIX+"&cYour profile is not yet loaded, Please try again later or contact admins!");
        public static final String TARGET_REQUIRED_FOR_CONSOLE_COMMAND = ChatColor.translateAlternateColorCodes('&', PREFIX+"This command is now run from a console. If running from console please provide the target player as /mine [TARGET_PLAYER]");
    }
}
