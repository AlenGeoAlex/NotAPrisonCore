package me.alenalex.notaprisoncore.paper.data;

import me.alenalex.notaprisoncore.api.data.IMineMetaDataHolder;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class MineMetaDataHolder implements IMineMetaDataHolder {
    private final DataHolder dataHolder;
    private final HashSet<IMineMeta> reservedMetaSet;

    public MineMetaDataHolder(DataHolder dataHolder) {
        this.dataHolder = dataHolder;
        this.reservedMetaSet = new HashSet<>();
    }

    public void onEnable(){
        Collection<?> joined = this.dataHolder.getPlugin().getPrisonDataStore().mineMetaStore()
                .reserveMetas()
                .handle((metaCollection, err) -> {
                    if (err != null) {
                        getDataHolder().getPlugin().getLogger().severe("Failed to reserve metas.");
                        err.printStackTrace();
                        return Collections.emptyList();
                    }

                    return metaCollection;
                }).join();

        if(joined.isEmpty()){
            getDataHolder().getPlugin().getLogger().warning("No locked metas found! Claiming new mines will be failed");
        }

        for (Object object : joined) {
            if(object == null)
                continue;

            if(!(object instanceof IMineMeta))
                continue;

            IMineMeta meta = (IMineMeta) object;
            reservedMetaSet.add(meta);
        }
        getDataHolder().getPlugin().getLogger().info("Locked "+reservedMetaSet.size()+" metas for this server instance");
    }

    public void onDisable(){
        Boolean finalResponse = this.dataHolder.getPlugin().getPrisonDataStore()
                .mineMetaStore()
                .releaseReservedMetas()
                .handle((response, err) -> {
                    if (err != null) {
                        getDataHolder().getPlugin().getLogger().severe("Failed to release existing lock for this server instance");
                        err.printStackTrace();
                        return false;
                    }

                    return response;
                }).join();

        if(finalResponse)
            getDataHolder().getPlugin().getLogger().info("Released existing lock.");
        else getDataHolder().getPlugin().getLogger().warning("Failed to release lock on plugin");
    }

    public DataHolder getDataHolder() {
        return dataHolder;
    }
}
