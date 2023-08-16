package me.alenalex.notaprisoncore.api.enums;

import lombok.Getter;

import java.io.InputStream;

@Getter
public enum ConfigType {
    LOCALE("Paper Locale File", "default.yml"),
    IDENTIFIER("Block Identifier Configuration", "identifiers.yml"),
    PLUGIN("Paper Plugin Configuration", "config.yml");

    private final String name;
    private final String fileName;

    ConfigType(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
    }

    public InputStream getFileStream(){
        return ConfigType.class.getClassLoader().getResourceAsStream(this.fileName);
    }

}
