package me.alenalex.notaprisoncore.api.abstracts;

import me.alenalex.notaprisoncore.api.abstracts.message.AbstractMessageBus;
import me.alenalex.notaprisoncore.api.abstracts.message.AbstractOneWayMessageBus;
import me.alenalex.notaprisoncore.api.abstracts.message.AbstractTwoWayMessageBus;
import me.alenalex.notaprisoncore.api.abstracts.message.IMessageService;
import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.database.IDatabaseProvider;
import me.alenalex.notaprisoncore.api.exceptions.database.redis.RedisDatabaseNotAvailableException;
import me.alenalex.notaprisoncore.api.exceptions.message.MessageBusExistsException;
import me.alenalex.notaprisoncore.message.MessageRequestModel;
import me.alenalex.notaprisoncore.message.MessageResponseModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Set;

public abstract class AbstractMessageService implements IMessageService {

    private final HashMap<String, AbstractMessageBus<?>> messageBusHashMap;
    private final IDatabaseProvider provider;
    private final IJsonWrapper jsonWrapper;
    private Thread jedisThread;
    private final JedisPubSub pubSub;
    private boolean initialized;

    public AbstractMessageService(IDatabaseProvider provider, IJsonWrapper jsonWrapper) {
        this.provider = provider;
        this.messageBusHashMap = new HashMap<>();
        this.jedisThread = null;
        this.pubSub = new Subscriber(this);
        this.jsonWrapper = jsonWrapper;
        this.initialized = false;
    }

    @Override
    public boolean isListening() {
        return initialized;
    }

    public abstract void onEnable() throws MessageBusExistsException;

    public abstract void onDisable();

    @Override
    public void registerMessageBus(AbstractOneWayMessageBus<?> oneWayMessageBus) throws MessageBusExistsException {
        String channelName = oneWayMessageBus.channelName();
        if(messageBusHashMap.containsKey(channelName))
            throw new MessageBusExistsException("A message bus with channel name "+channelName+" already exists!");

        this.messageBusHashMap.put(channelName, oneWayMessageBus);
        if(initialized)
            this.pubSub.subscribe(channelName);
    }

    @Override
    public void registerMessageBus(AbstractTwoWayMessageBus<?, ?> twoWayMessageBus) throws MessageBusExistsException {
        String requestChannel = twoWayMessageBus.channelName();
        String responseChannel = twoWayMessageBus.responseChannelName();

        if(messageBusHashMap.containsKey(requestChannel))
            throw new MessageBusExistsException("A message bus with channel name "+requestChannel+" already exists!");

        if(messageBusHashMap.containsKey(responseChannel))
            throw new MessageBusExistsException("A response message bus with channel name "+responseChannel+" already exists!");

        this.messageBusHashMap.put(requestChannel, twoWayMessageBus);
        this.messageBusHashMap.put(responseChannel, twoWayMessageBus);
        if(initialized)
            this.pubSub.subscribe(requestChannel, responseChannel);
    }

    @Override
    public void unregisterMessageBus(@NotNull String channelName) {
        AbstractMessageBus<?> messageBus = getServiceBusOf(channelName);

        if(messageBus == null)
            return;

        if(messageBus instanceof AbstractTwoWayMessageBus){
            AbstractTwoWayMessageBus<?, ?> bus = (AbstractTwoWayMessageBus<?, ?>) messageBus;
            this.messageBusHashMap.remove(bus.channelName());
            this.messageBusHashMap.remove(bus.responseChannelName());
            if(initialized)
                this.pubSub.unsubscribe(bus.channelName(), bus.responseChannelName());
        }else{
            this.messageBusHashMap.remove(messageBus.channelName());
            if(initialized)
                this.pubSub.unsubscribe(messageBus.channelName());
        }
    }

    @Nullable
    protected AbstractMessageBus<?> getServiceBusOf(String channelName){
        return messageBusHashMap.get(channelName);
    }


    public boolean listen() throws Exception {
        if(!provider.getRedisDatabase().isConnected())
            throw new RedisDatabaseNotAvailableException();
        JedisPooled connection = provider.getRedisDatabase().getConnection();
        if(connection == null)
            return false;

        this.close();
        this.jedisThread = new Thread(){
            @Override
            public void run() {
                connection.subscribe(pubSub, messageBusHashMap.keySet().toArray(new String[0]));
                initialized = true;
            }
        };
        this.jedisThread.start();
        return true;
    }

    public void close(){
        if(this.pubSub != null && this.pubSub.isSubscribed()){
            this.pubSub.unsubscribe();
            this.initialized = false;
        }

        if(this.jedisThread != null){
            this.jedisThread.stop();
            this.jedisThread = null;
        }
    }

    public Set<String> getChannels(){
        return this.messageBusHashMap.keySet();
    }


    private static final class Subscriber extends JedisPubSub {

        private final AbstractMessageService service;

        public Subscriber(AbstractMessageService service) {
            this.service = service;
        }

        @Override
        public void onMessage(String channel, String message) {
            if(channel == null || message == null)
                return;

            AbstractMessageBus<?> messageBus = this.service.getServiceBusOf(channel);

            if(messageBus instanceof AbstractTwoWayMessageBus){
                try {
                    AbstractTwoWayMessageBus<?, ?> twoWayMessageBus = (AbstractTwoWayMessageBus<?, ?>) messageBus;
                    if(twoWayMessageBus.responseChannelName().equals(channel)) {
                        MessageResponseModel<?> model = service.jsonWrapper.fromString(message, twoWayMessageBus.responseModelClazz().getType());
                        twoWayMessageBus.receiveResponse(model);
                    }else{
                        MessageRequestModel<?> requestModel = service.jsonWrapper.fromString(message, twoWayMessageBus.requestModelClazz().getType());
                        twoWayMessageBus.receive(requestModel);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(messageBus instanceof AbstractOneWayMessageBus){
                try {
                    AbstractOneWayMessageBus<?> oneWayMessageBus = (AbstractOneWayMessageBus<?>) messageBus;
                    MessageRequestModel<?> requestModel = service.jsonWrapper.fromString(message, oneWayMessageBus.requestClazz().getType());
                    oneWayMessageBus.receive(requestModel);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                throw new IllegalStateException("No known message bus provider found!");
            }
        }
    }

    protected IDatabaseProvider getProvider() {
        return provider;
    }

    protected IJsonWrapper getJsonWrapper() {
        return jsonWrapper;
    }
}
