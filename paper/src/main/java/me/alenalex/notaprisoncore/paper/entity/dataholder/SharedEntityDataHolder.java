package me.alenalex.notaprisoncore.paper.entity.dataholder;

import me.alenalex.notaprisoncore.api.abstracts.AbstractDataHolder;

import java.util.concurrent.ConcurrentHashMap;

public class SharedEntityDataHolder extends AbstractDataHolder {

    public SharedEntityDataHolder(ConcurrentHashMap<String, Object> dataHolder) {
        super(dataHolder);
    }

    public SharedEntityDataHolder() {
    }
}
