package me.alenalex.notaprisoncore.paper.manager;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.BukkitCommandOptions;
import me.alenalex.notaprisoncore.paper.commands.NPCoreAdmin;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

public final class CommandManager {

    private final PrisonManagers prisonManagers;

    private final BukkitCommandManager<CommandSender> triumphCommandManager;
    public CommandManager(PrisonManagers prisonManagers) {
        this.prisonManagers = prisonManagers;
        this.triumphCommandManager = BukkitCommandManager.create(
                this.prisonManagers.getPluginInstance().getBukkitPlugin()
        );
    }

    public void registerCommand(){
        this.triumphCommandManager.registerCommand(new NPCoreAdmin(this));
    }

    public PrisonManagers getPrisonManagers() {
        return prisonManagers;
    }
}
