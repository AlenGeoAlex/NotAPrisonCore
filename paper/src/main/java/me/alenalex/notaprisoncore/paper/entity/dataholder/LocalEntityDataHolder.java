package me.alenalex.notaprisoncore.paper.entity.dataholder;

import me.alenalex.notaprisoncore.api.abstracts.AbstractDataHolder;

import java.util.concurrent.ConcurrentHashMap;

public class LocalEntityDataHolder extends AbstractDataHolder {

    public LocalEntityDataHolder(ConcurrentHashMap<String, Object> dataHolder) {
        super(dataHolder);
    }

    public LocalEntityDataHolder() {
    }
}
