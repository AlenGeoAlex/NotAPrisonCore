package me.alenalex.notaprisoncore.api.common.serializer;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import org.bukkit.Material;

import java.io.IOException;

public class BlockEntrySerializer extends TypeAdapter<BlockEntry> {
    @Override
    public void write(JsonWriter out, BlockEntry value) throws IOException {
        out.beginObject();
        out.name("material");
        out.value(value.getMaterialType().name());
        if(value.getData() >= 0){
            out.name("data");
            out.value(value.getData());
        }
        out.endObject();
    }

    @Override
    public BlockEntry read(JsonReader in) throws IOException {
        String materialRaw = null;
        Material material;
        byte data = -1;
        String fieldName = null;

        in.beginObject();
        while (in.hasNext()){
            JsonToken token = in.peek();
            if(token.equals(JsonToken.NAME)){
                fieldName = in.nextName();
            }

            if("material".equals(fieldName)){
                token = in.peek();
                materialRaw = in.nextString();
            }

            if("data".equals(fieldName)){
                token = in.peek();
                data = (byte) in.nextInt();
            }
        }
        in.endObject();

        if(materialRaw == null || materialRaw.isEmpty())
            return null;

        material = Material.getMaterial(materialRaw);
        if(data >= 0)
            return new BlockEntry(material, data);

        return new BlockEntry(material);
    }
}
