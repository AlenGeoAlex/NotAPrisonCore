package me.alenalex.notaprisoncore.paper.entity.dataholder;

import me.alenalex.notaprisoncore.api.abstracts.AbstractMetaDataHolder;
import me.alenalex.notaprisoncore.api.exceptions.meta.IllegalMetaData;
import org.apache.commons.lang3.SerializationUtils;

import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class LocalEntityMetaDataHolder extends AbstractMetaDataHolder {

    public LocalEntityMetaDataHolder(ConcurrentHashMap<String, Object> dataHolder) {
        super(dataHolder);
    }

    public LocalEntityMetaDataHolder() {
    }

    public void setHolderData(String base64){
        byte[] decodedData = Base64.getDecoder().decode(base64);
        ConcurrentHashMap<String, Object> decodedMap = null;
        try {
            decodedMap = (ConcurrentHashMap<String, Object>) SerializationUtils.deserialize(decodedData);
        }catch (Exception e){
            e.printStackTrace();
            throw new IllegalMetaData("Failed to deserialize/convert the meta data", e);
        }

        if(decodedMap == null)
            throw new IllegalMetaData("Failed to deserialize/convert the meta data");

        this.dataHolder.clear();
        this.dataHolder.putAll(decodedMap);
    }
}
