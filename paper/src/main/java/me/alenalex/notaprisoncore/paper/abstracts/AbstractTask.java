package me.alenalex.notaprisoncore.paper.abstracts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;

@Getter
@ToString
@EqualsAndHashCode
public abstract class AbstractTask implements Runnable{

    private final NotAPrisonCore pluginInstance;

    public AbstractTask() {
        this.pluginInstance = ((Bootstrap) Bootstrap.getJavaPlugin()).getPluginInstance();
    }


}
