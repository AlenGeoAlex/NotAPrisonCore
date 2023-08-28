package me.alenalex.notaprisoncore.paper.commands;

import dev.triumphteam.cmd.core.annotations.Command;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.paper.manager.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.Optional;

@Command("test")
public class NPCoreAdmin {

    private final CommandManager commandManager;

    public NPCoreAdmin(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Command
    public void claim(CommandSender sender){
        Optional<IMineMeta> unclaimedMeta = commandManager.getPrisonManagers().getPluginInstance().getDataHolder().mineMetaDataHolder().getUnclaimedMeta();
        unclaimedMeta.ifPresent(meta -> {
            System.out.println(meta.getMetaId());
            commandManager.getPrisonManagers().getPluginInstance().getDataHolder().mineMetaDataHolder().claimMeta(meta);
        });
    }
}
