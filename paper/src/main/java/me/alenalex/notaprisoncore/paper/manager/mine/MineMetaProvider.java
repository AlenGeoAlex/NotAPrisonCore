package me.alenalex.notaprisoncore.paper.manager.mine;

import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.exceptions.NoSchematicFound;
import me.alenalex.notaprisoncore.api.provider.IMineMetaProvider;
import me.alenalex.notaprisoncore.paper.constants.DefaultAdminMessages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class MineMetaProvider implements IMineMetaProvider {

    private final MineManager mineManager;
    private final AtomicBoolean pasting;

    public MineMetaProvider(MineManager mineManager) {
        this.mineManager = mineManager;
        this.pasting = new AtomicBoolean(false);
    }

    @Override
    public boolean isPastingInProgress() {
        return pasting.get();
    }

    @Override
    public void pasteMines(CommandSender sender, String schematicName, int count, long coolDownInterval) {
        if(pasting.get()){
            sender.sendMessage(DefaultAdminMessages.GENERATION_IN_PROGRESS);
            return;
        }
        pasting.set(true);
        if(count <= 0)
        {
            sender.sendMessage(DefaultAdminMessages.INVALID_MINE_GENERATION_COUNT);
            return;
        }
        if(coolDownInterval <= 300){
            sender.sendMessage(DefaultAdminMessages.INVALID_COOL_DOWN_INTERVAL);
            return;
        }

        Collection<IMineMeta> metas = null;
        this.mineManager.generator().generateMines(sender, schematicName, count, coolDownInterval)
                .whenComplete((metaCollection, err) -> {
                    pasting.set(false);
                    if(err != null){
                        mineManager.getPlugin().getLogger().severe("An unknown error occurred while generating mines, Please check stacktrace below if present");
                        if(err instanceof NoSchematicFound){
                            sender.sendMessage(DefaultAdminMessages.NO_SCHEMATIC_FOUND);
                        }
                        err.printStackTrace();
                        return;
                    }

                    int completed = 0;
                    mineManager.getPlugin().getLogger().info("Starting to save "+metaCollection.size()+" generated metas to database");
                    for (IMineMeta meta : metaCollection) {
                        if(meta == null){
                            sender.sendMessage(DefaultAdminMessages.FAILED_TO_GENERATE_MINE_META);
                            continue;
                        }
                        Boolean response = mineManager.registerMineMeta(meta)
                                .handle((res, insertErr) -> {
                                    if(insertErr != null){
                                        insertErr.printStackTrace();
                                        return false;
                                    }

                                    return true;
                                }).join();
                        if(response == null){
                            sender.sendMessage(DefaultAdminMessages.FAILED_TO_GENERATE_MINE_META);
                            continue;
                        }

                        if(!response){
                            sender.sendMessage(DefaultAdminMessages.FAILED_TO_GENERATE_MINE_META);
                            continue;
                        }
                        completed++;
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSuccessfully generated mine of ["+completed+"/"+metaCollection.size()+"] meta with id "+ meta.getMetaId()));
                        sender.sendMessage(DefaultAdminMessages.PASTE_COMPLETE);
                    }
                });
    }

    @Override
    public void pasteMines(CommandSender sender, String schematicName) {
        if(pasting.get()){
            sender.sendMessage(DefaultAdminMessages.GENERATION_IN_PROGRESS);
            return;
        }
        pasting.set(true);
        try {
            IMineMeta meta = null;
            try {
                Optional<IMineMeta> metaOptional = this.mineManager.generator().generateMine(sender, schematicName);
                if(!metaOptional.isPresent()){
                    sender.sendMessage(DefaultAdminMessages.FAILED_TO_GENERATE_MINE_META);
                    return;
                }
                meta = metaOptional.get();
            } catch (NoSchematicFound e) {
                e.printStackTrace();
                sender.sendMessage(DefaultAdminMessages.NO_SCHEMATIC_FOUND);
            }

            if(meta == null){
                sender.sendMessage(DefaultAdminMessages.FAILED_TO_GENERATE_MINE_META);
                return;
            }

            IMineMeta finalMeta = meta;
            this.mineManager.registerMineMeta(meta).whenComplete((mineMeta, err) -> {
                if(err != null){
                    err.printStackTrace();
                    sender.sendMessage(DefaultAdminMessages.FAILED_TO_GENERATE_MINE_META);
                    return;
                }

                if(!mineMeta){
                    sender.sendMessage(DefaultAdminMessages.FAILED_TO_GENERATE_MINE_META);
                    return;
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSuccessfully generated mine meta with id "+ finalMeta.getMetaId()));
                sender.sendMessage(DefaultAdminMessages.PASTE_COMPLETE);
            });
        }finally {
            pasting.set(false);
        }
    }
}
