package me.alenalex.notaprisoncore.paper.commands;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.exceptions.NoSchematicFound;
import me.alenalex.notaprisoncore.api.generator.IMineGenerator;
import me.alenalex.notaprisoncore.api.provider.IMineMetaProvider;
import me.alenalex.notaprisoncore.paper.abstracts.AbstractCommand;
import me.alenalex.notaprisoncore.paper.commands.help.CommandHelpProvider;
import me.alenalex.notaprisoncore.paper.commands.help.SubcommandHelpProvider;
import me.alenalex.notaprisoncore.paper.entity.mine.Mine;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;
import me.alenalex.notaprisoncore.paper.manager.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@Command(value = "npcadmin", alias = {"npca", "npcadm"})
@Permission("npc.command.admin")
public class NPCoreAdmin extends AbstractCommand {

    public NPCoreAdmin(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    protected CommandHelpProvider helpPrompt() {
        return CommandHelpProvider.Builder.newBuilder()
                .withCommandName("npca")
                .withPermission("npc.command.admin")
                .withAliases("npca", "npcadm")
                .withDescription("The admin command section for NPCore")
                .withSubcommands(
                        SubcommandHelpProvider.Builder.newBuilder()
                                .withSubcommand("generate")
                                .withPermission("generate")
                                .withAliases("gen", "paste")
                                .withDescription("Generate/Paste mines in the default mine world with provided schematic name. If count and tick are provided, it considers as multiple pasting")
                                .withArgs("[schematicName] (count) (tick)")
                                .build()
                )
                .build();
    }

    @Command("expand")
    public void claim(CommandSender sender) {
        Optional<IMineMeta> unclaimedMeta = getCommandManager().getPrisonManagers().getPluginInstance().getDataHolder().mineMetaDataHolder().getUnclaimedMeta();
        IMineMeta meta = unclaimedMeta.orElse(null);
        if (meta == null) {
            System.out.println("Meta is null");
            return;
        }

        Player player = (Player) sender;
        player.teleport(meta.getSpawnPoint());
        System.out.println(meta.getSpawnPoint());
        CuboidRegion region = new CuboidRegion(BukkitUtil.toVector(meta.getMineSchematicLowerPoint()), BukkitUtil.toVector( meta.getMineSchematicUpperPoint()));
        System.out.println(region);
        region.expand(new Vector(4, 0, 4), new Vector(-1, -1, -1));
        System.out.println(region);
        CuboidRegion bedrock = region.clone();
        System.out.println(region);
        bedrock.expand(new Vector(1, 1, 1), new Vector(-1, -2, -1));
        Bukkit.getScheduler().runTaskLaterAsynchronously(pluginInstance().getBukkitPlugin(), new Runnable() {
            @Override
            public void run() {

                EditSession editSession = (new EditSessionBuilder(FaweAPI.getWorld(meta.getSpawnPoint().getWorld().getName()))).limitUnlimited().fastmode(Boolean.valueOf(true)).build();
                try {
                    editSession.makeWalls(bedrock, new Pattern() {
                        @Override
                        public BaseBlock apply(Vector position) {
                            System.out.println(position);
                            return new BaseBlock(0);
                        }
                    });
                } catch (WorldEditException e) {
                    e.printStackTrace();
                }finally {
                    editSession.flushQueue();
                }
            }
        }, 100);
    }
//
//        Mine mine = new Mine(UUID.randomUUID(), (MineMeta) meta);
//        mine.setDefaults(commandManager.getPrisonManagers());
//        mine.getLocalMetaDataHolder().set("WORLD_GUARD_REGION", UUID.randomUUID());
//        commandManager.getPrisonManagers().getPluginInstance().getPrisonDataStore().mineStore().claimMine(mine)
//                .whenComplete(((uuid, throwable) -> {
//                    if(throwable != null){
//                        throwable.printStackTrace();
//                        return;
//                    }
//                    UUID mineId = uuid.orElse(null);
//                    if(mineId == null){
//                        System.out.println("mine is not null");
//                        return;
//                    }
//
//                    mine.setMineId(mineId);
//                }));
//    }
//
//    @Command("test2")
//    public void onCommand2(CommandSender sender, String id){
//        UUID mineId = UUID.fromString(id);
//        commandManager.getPrisonManagers().getPluginInstance().getPrisonDataStore().mineStore().id(mineId)
//                .whenComplete(((iMine, throwable) -> {
//                    if(throwable != null){
//                        throwable.printStackTrace();
//                        return;
//                    }
//                    IMine mine = iMine.orElse(null);
//                    System.out.println(mine.getId());
//                    System.out.println(mine.getLocalMetaDataHolder().get("WORLD_GUARD_REGION"));
//                    System.out.println(mine.getBlockChoices().getChoices());
//                }));
//    }

    @Command
    public void defaultCommand(CommandSender sender){
        getCommandHelpProvider().send(sender);
    }

    @Command(value = "generate", alias = {"gen", "paste"})
    @Permission("generate")
    public void generateCommand(CommandSender sender, String schematicName, Integer count, Long tickInterval){
        boolean isMultiple = false;
        if(count != null && tickInterval != null && count > 1){
            isMultiple = true;
            if(tickInterval <= 300){
                sendFormattedMessage(sender, "Generation of multiple mine from schematics requires at-least of 300 ticks (15 sec) time interval between them. This is to ensure that the server doesn't get overloaded");
                return;
            }
        }

        IMineMetaProvider metaProvider = getCommandManager().getPrisonManagers().mineManager().metaProvider();

        if(metaProvider.isPastingInProgress()){
            sendFormattedMessage(sender, ChatColor.RED+"An existing pasting/generation is on progress, Try again later");
            return;
        }

        if(isMultiple){
            metaProvider.pasteMines(sender, schematicName, count, tickInterval);
        }else{
            metaProvider.pasteMines(sender, schematicName);
        }
    }


}
