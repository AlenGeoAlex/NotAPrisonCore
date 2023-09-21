package me.alenalex.notaprisoncore.paper.message;

import me.alenalex.notaprisoncore.api.abstracts.AbstractMessageService;
import me.alenalex.notaprisoncore.api.config.options.ServerConfiguration;
import me.alenalex.notaprisoncore.api.exceptions.message.MessageBusExistsException;
import me.alenalex.notaprisoncore.message.AbstractMessageBuilder;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.message.bus.SampleBus;
import me.alenalex.notaprisoncore.paper.message.bus.SampleOneWay;
import me.alenalex.notaprisoncore.paper.wrapper.GsonWrapper;

public class PrisonMessageService extends AbstractMessageService {

    private final NotAPrisonCore pluginInstance;
    private SampleBus sampleBus;
    private SampleOneWay oneWayMessageBus;
    public PrisonMessageService(NotAPrisonCore plugin) {
        super(plugin.getDatabaseProvider(), GsonWrapper.singleton());
        this.pluginInstance = plugin;
    }

    @Override
    public void onEnable() throws MessageBusExistsException {
        new AbstractMessageBuilder(){
            private final ServerConfiguration configuration = ((Bootstrap) (Bootstrap.getJavaPlugin())).getPluginInstance().getPrisonManagers().configurationManager().getPluginConfiguration().serverConfiguration();
            @Override
            public String sourceName() {
                return configuration.getServerName();
            }
        }.build();
        this.sampleBus = new SampleBus(pluginInstance.getDatabaseProvider().getRedisDatabase(), GsonWrapper.singleton(), pluginInstance.getPrisonManagers().configurationManager().getPluginConfiguration().serverConfiguration());
        this.registerMessageBus(sampleBus);
        oneWayMessageBus = new SampleOneWay(pluginInstance.getDatabaseProvider().getRedisDatabase(), GsonWrapper.singleton(), pluginInstance.getPrisonManagers().configurationManager().getPluginConfiguration().serverConfiguration());
        this.registerMessageBus(oneWayMessageBus);
    }

    @Override
    public void onDisable() {

    }

    public SampleBus getSampleBus() {
        return sampleBus;
    }

    public SampleOneWay getOneWayMessageBus() {
        return oneWayMessageBus;
    }
}
