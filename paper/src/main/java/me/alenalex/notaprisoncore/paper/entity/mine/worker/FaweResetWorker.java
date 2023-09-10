package me.alenalex.notaprisoncore.paper.entity.mine.worker;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.api.config.options.ResetterConfiguration;
import me.alenalex.notaprisoncore.api.entity.mine.IMineResetter;
import me.alenalex.notaprisoncore.api.provider.IRandomProvider;
import me.alenalex.notaprisoncore.paper.abstracts.AbstractResetWorker;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
public class FaweResetWorker extends AbstractResetWorker {

    private EditSession editSession;
    public FaweResetWorker(IMineResetter resetter) {
        super(resetter);
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    protected CompletableFuture<Long> work() {
        CompletableFuture<Long> future = new CompletableFuture<>();
        World world = this.getMineResetter().getMineMeta().getSpawnPoint().getWorld();
        this.editSession = (new EditSessionBuilder(FaweAPI.getWorld(world.getName()))).limitUnlimited().fastmode(Boolean.valueOf(true)).build();

        Bukkit.getScheduler().runTaskAsynchronously(Bootstrap.getJavaPlugin(), new Runnable() {
            @Override
            public void run() {
                try {
                    CuboidRegion region = new CuboidRegion(BukkitUtil.toVector(getMineResetter().getMineMeta().getLowerMiningPoint()), BukkitUtil.toVector(getMineResetter().getMineMeta().getUpperMiningPoint()));
                    List<BlockEntry> choices = getMineResetter().getBlockChoices().getChoices();
                    List<BaseBlock> blocks = new ArrayList<>();
                    for (BlockEntry choice : choices) {
                        Material type = choice.getMaterialType();
                        if(choice.getData() == -1)
                            blocks.add(new BaseBlock(type.getId()));
                        else blocks.add(new BaseBlock(type.getId(), choice.getData()));
                    }
                    int blockChange = editSession.setBlocks(region, new Pattern() {
                        @Override
                        public BaseBlock apply(Vector position) {
                            BaseBlock random = IRandomProvider.getRandomFromList(blocks);
                            return random;
                        }
                    });

                    editSession.flushQueue();
                    future.complete((long) blockChange);
                }catch (Exception e){
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
    }
}
