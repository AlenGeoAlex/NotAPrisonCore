package me.alenalex.notaprisoncore.api.common;

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@ToString
public enum RedisKey {

    SERVER_SWITCH(3000, "switch"),
    MINE_DATA(10000, "mine-data");
    private final long expiry;
    private final String identifierKey;

    RedisKey(long expiry, String identifierKey) {
        this.expiry = expiry;
        this.identifierKey = identifierKey;
    }

    @NotNull
    public String keyOf(String id){
        return this.identifierKey +":"+id;
    }

    @Nullable
    public String splitKey(String id){
        if(id.length() <= this.identifierKey.length()+1)
            return null;

        return id.substring(this.identifierKey.length()+1);
    }

    @Nullable
    public static RedisKey getKeyOfString(String rawKey){
        RedisKey[] values = RedisKey.values();
        for (RedisKey eachKey : values) {
            if(rawKey.startsWith(eachKey.identifierKey))
                return eachKey;
        }

        return null;
    }
}
