package me.alenalex.notaprisoncore.api.provider;

import org.bukkit.command.CommandSender;

public interface IMineMetaProvider {

    boolean isPastingInProgress();
    void pasteMines(CommandSender sender, String schematicName, int count, long coolDownInterval);

    void pasteMines(CommandSender sender, String schematicName);

}
