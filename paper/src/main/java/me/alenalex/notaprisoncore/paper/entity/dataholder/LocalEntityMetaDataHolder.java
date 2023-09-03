package me.alenalex.notaprisoncore.paper.entity.dataholder;

import me.alenalex.notaprisoncore.api.abstracts.AbstractMetaDataHolder;

import java.util.concurrent.ConcurrentHashMap;

public class LocalEntityMetaDataHolder extends AbstractMetaDataHolder {

    public LocalEntityMetaDataHolder(ConcurrentHashMap<String, Object> dataHolder) {
        super(dataHolder);
    }

    public LocalEntityMetaDataHolder() {
    }
}
