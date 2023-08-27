package me.alenalex.notaprisoncore.paper.manager.mine;

import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.exceptions.NoSchematicFound;
import me.alenalex.notaprisoncore.api.provider.IMineMetaProvider;
import me.alenalex.notaprisoncore.paper.constants.DefaultAdminMessages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Optional;

public class MineMetaProvider implements IMineMetaProvider {

    private final MineManager mineManager;

    public MineMetaProvider(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    @Override
    public void pasteMines(CommandSender sender, String schematicName, int count, long coolDownInterval) {
        if(count <= 0)
        {
            sender.sendMessage(DefaultAdminMessages.INVALID_MINE_GENERATION_COUNT);
            return;
        }
        if(coolDownInterval <= 200){
            sender.sendMessage(DefaultAdminMessages.INVALID_COOL_DOWN_INTERVAL);
            return;
        }

        Collection<IMineMeta> metas = null;
        this.mineManager.generator().generateMines(sender, schematicName, count, coolDownInterval)
                .whenComplete((metaCollection, err) -> {
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
                    }
                });
    }

    @Override
    public void pasteMines(CommandSender sender, String schematicName) {
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

                return;
            }

            if(!mineMeta){
                sender.sendMessage(DefaultAdminMessages.FAILED_TO_GENERATE_MINE_META);
                return;
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSuccessfully generated mine meta with id "+ finalMeta.getMetaId()));
        });
    }
}
