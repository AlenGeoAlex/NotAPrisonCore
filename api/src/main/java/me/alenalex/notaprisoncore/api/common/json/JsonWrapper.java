package me.alenalex.notaprisoncore.api.common.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonWrapper implements IJsonWrapper {

    public static final IJsonWrapper WRAPPER;

    static {
        try {
            WRAPPER = new JsonWrapper();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final Gson gson;
    private final Gson prettyJson;
    private JsonWrapper() throws IllegalAccessException {
        if(WRAPPER != null)
            throw new IllegalAccessException("JsonWrapper is a singleton, please use the JsonWrapper#WRAPPER to access the wrapper");
        this.gson = new GsonBuilder()
                .serializeNulls()
                .create();
        this.prettyJson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    @Override
    public Gson get() {
        return gson;
    }

    @Override
    public Gson getPretty() {
        return prettyJson;
    }
}
