package me.alenalex.notaprisoncore.paper.serializer;

import com.google.common.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.alenalex.notaprisoncore.api.abstracts.AbstractMetaDataHolder;
import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.enums.MineAccess;
import me.alenalex.notaprisoncore.api.exceptions.meta.FailedMetaDataInitialization;
import me.alenalex.notaprisoncore.paper.entity.dataholder.SharedEntityMetaDataHolder;
import me.alenalex.notaprisoncore.paper.entity.mine.BlockChoices;
import me.alenalex.notaprisoncore.paper.entity.mine.Mine;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;
import me.alenalex.notaprisoncore.paper.wrapper.GsonWrapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MineSerializer extends TypeAdapter<IMine> {

    public static final Type BLOCK_ENTRY_TYPE = new TypeToken<ArrayList<BlockEntry>>(){}.getType();
    @Override
    public void write(JsonWriter out, IMine value) throws IOException {
        Mine mine = (Mine) value;
        out.beginObject();
        out.name("id");
        if(value.getId() == null)
            out.nullValue();
        else out.value(value.getId().toString());
        out.name("owner-id");
        out.value(value.getOwnerId().toString());
        out.name("reset-block-choices");
        ((BlockChoices) value.getBlockChoices()).toJsonWriter(out);
        out.name("vault-balance");
        out.value(value.getVault().getBalance().toString());
        out.name("access");
        out.value(value.access().name());
        out.name("meta");
        GsonWrapper.singleton().gson().toJson((MineMeta) value.getMeta(), MineMeta.class, out);
        out.name("shared-meta-data");
        out.value(((AbstractMetaDataHolder) value.getSharedMetaDataHolder()).encode());
        out.endObject();
    }

    @Override
    public IMine read(JsonReader in) throws IOException {
        UUID mineId = null;
        MineMeta meta = null;
        UUID ownerId = null;
        MineAccess access;
        List<BlockEntry> blockEntries = new ArrayList<>();
        BigDecimal amount = null;
        SharedEntityMetaDataHolder sharedEntityMetaDataHolder = new SharedEntityMetaDataHolder();

        in.beginObject();
        while(in.hasNext()){
            String propName = in.nextName();
            switch (propName){
                case "id":
                    String nextString = in.nextString();
                    if(nextString == null || nextString.isEmpty())
                        break;
                    mineId = UUID.fromString(nextString);
                    break;
                case "owner-id":
                    ownerId = UUID.fromString(in.nextString());
                    break;
                case "meta":
                    meta = GsonWrapper.singleton().gson().fromJson(in, MineMeta.class);
                    break;
                case "reset-block-choices":
                    blockEntries = GsonWrapper.singleton().gson().fromJson(in, BLOCK_ENTRY_TYPE);
                    break;
                case "vault-balance":
                    amount = new BigDecimal(in.nextString());
                    break;
                case "access":
                    access = MineAccess.valueOf(in.nextString());
                    break;
                case "shared-meta-data":
                    String encodedValue = in.nextString();
                    if(encodedValue == null || encodedValue.isEmpty()){
                        sharedEntityMetaDataHolder = new SharedEntityMetaDataHolder();
                        break;
                    }
                    try {
                        sharedEntityMetaDataHolder = AbstractMetaDataHolder.decode(encodedValue, SharedEntityMetaDataHolder.class);
                    } catch (FailedMetaDataInitialization e) {
                        e.printStackTrace();
                        throw new IOException("Failed to decode shared data of the player",e);
                    }
                    break;
                default:
                    // Handle unknown fields if needed
                    in.skipValue();
            }
        }
        in.endObject();
        if(meta == null)
            throw new IOException("Failed to deserialize the mine meta for mine of owner "+ownerId);

        return new Mine(ownerId, mineId, meta, blockEntries, amount, sharedEntityMetaDataHolder);
    }
}
