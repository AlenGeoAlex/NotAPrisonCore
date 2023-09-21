package me.alenalex.notaprisoncore.message.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
public class Sample {


    @AllArgsConstructor
    @Getter
    @ToString
    public static final class SampleRequest{
        private final String name;
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static final class SampleResponse{
        private final String fromName;
        private final String modifiedName;
    }

}
