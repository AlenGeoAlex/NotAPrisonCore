package me.alenalex.notaprisoncore.api.common.json;

import com.google.gson.Gson;

public interface IJsonWrapper {

    Gson get();
    Gson getPretty();
}
