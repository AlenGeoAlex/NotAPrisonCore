package me.alenalex.notaprisoncore.paper.entity.dataholder;

import me.alenalex.notaprisoncore.api.abstracts.AbstractMetaDataHolder;

import java.util.concurrent.ConcurrentHashMap;

public class SharedEntityMetaDataHolder extends AbstractMetaDataHolder {

    public SharedEntityMetaDataHolder(ConcurrentHashMap<String, Object> dataHolder) {
        super(dataHolder);
    }

    public SharedEntityMetaDataHolder() {
    }
}
