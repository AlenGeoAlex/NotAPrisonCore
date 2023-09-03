package me.alenalex.notaprisoncore.paper.commands;

import dev.triumphteam.cmd.core.annotations.Command;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.paper.entity.mine.Mine;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;
import me.alenalex.notaprisoncore.paper.manager.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.UUID;

@Command("test")
public class NPCoreAdmin {

    private final CommandManager commandManager;

    public NPCoreAdmin(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Command
    public void claim(CommandSender sender){
        Optional<IMineMeta> unclaimedMeta = commandManager.getPrisonManagers().getPluginInstance().getDataHolder().mineMetaDataHolder().getUnclaimedMeta();
        IMineMeta meta = unclaimedMeta.orElse(null);
        if(meta == null){
            System.out.println("Meta is null");
            return;
        }

        Mine mine = new Mine(UUID.randomUUID(), (MineMeta) meta);
        mine.setDefaults(commandManager.getPrisonManagers());
        mine.getLocalMetaDataHolder().set("WORLD_GUARD_REGION", UUID.randomUUID());
        commandManager.getPrisonManagers().getPluginInstance().getPrisonDataStore().mineStore().claimMine(mine)
                .whenComplete(((uuid, throwable) -> {
                    if(throwable != null){
                        throwable.printStackTrace();
                        return;
                    }
                    UUID mineId = uuid.orElse(null);
                    if(mineId == null){
                        System.out.println("mine is not null");
                        return;
                    }

                    mine.setMineId(mineId);
                }));
    }

    @Command("test2")
    public void onCommand2(CommandSender sender, String id){
        UUID mineId = UUID.fromString(id);
        commandManager.getPrisonManagers().getPluginInstance().getPrisonDataStore().mineStore().id(mineId)
                .whenComplete(((iMine, throwable) -> {
                    if(throwable != null){
                        throwable.printStackTrace();
                        return;
                    }
                    IMine mine = iMine.orElse(null);
                    System.out.println(mine.getId());
                    System.out.println(mine.getLocalMetaDataHolder().get("WORLD_GUARD_REGION"));
                    System.out.println(mine.getBlockChoices().getChoices());
                }));
    }
}
