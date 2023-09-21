package me.alenalex.notaprisoncore.paper.message;

import me.alenalex.notaprisoncore.api.abstracts.AbstractMessageService;
import me.alenalex.notaprisoncore.api.config.options.ServerConfiguration;
import me.alenalex.notaprisoncore.api.exceptions.message.MessageBusExistsException;
import me.alenalex.notaprisoncore.message.AbstractMessageBuilder;
import me.alenalex.notaprisoncore.message.models.OnlineAnnouncementMessage;
import me.alenalex.notaprisoncore.message.service.IMessageServiceBus;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.message.bus.OnlineAnnouncementMessageBus;
import me.alenalex.notaprisoncore.paper.wrapper.GsonWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PrisonMessageService extends AbstractMessageService {

    private final NotAPrisonCore pluginInstance;
    private IMessageServiceBus<OnlineAnnouncementMessage.OnlineAnnouncementRequest> heartbeatService;

    public PrisonMessageService(NotAPrisonCore plugin) {
        super(plugin.getDatabaseProvider(), GsonWrapper.singleton());
        this.pluginInstance = plugin;
    }

    @Override
    public void onEnable() throws MessageBusExistsException {
        new AbstractMessageBuilder(){
            private final ServerConfiguration configuration = ((Bootstrap) (Bootstrap.getJavaPlugin())).getPluginInstance().getPrisonManagers().configurationManager().getPluginConfiguration().serverConfiguration();
            private final String serverAddress = this.buildAddress();
            @Override
            public String sourceName() {
                return configuration.getServerName();
            }
            @Override
            public String sourceAddress() {
                return serverAddress;
            }

            private String getAddress() {
                StringBuilder response = new StringBuilder();

                try {
                    // Create a URL object with the target URL
                    URL url = new URL("http://checkip.amazonaws.com");

                    // Open a connection to the URL
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // Set the HTTP request method (GET by default)
                    connection.setRequestMethod("GET");

                    // Get the response code
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read the response content
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;

                        while ((inputLine = reader.readLine()) != null) {
                            response.append(inputLine);
                        }
                        reader.close();
                    } else {
                        System.err.println("HTTP request failed with status code: " + responseCode);
                    }

                    // Close the connection
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return response.toString();
            }

            private String buildAddress(){
                String ip = ((Bootstrap) Bootstrap.getJavaPlugin()).getServer().getIp();
                if(ip.equals("0.0.0.0")){
                    ip = getAddress();
                }
                String port = String.valueOf(((Bootstrap) Bootstrap.getJavaPlugin()).getServer().getPort());
                return ip + ":" + port;
            }

        }.build();
        this.heartbeatService = new OnlineAnnouncementMessageBus(getProvider().getRedisDatabase(), getJsonWrapper(), this.pluginInstance.getPrisonManagers().configurationManager().getPluginConfiguration().serverConfiguration(), AbstractMessageBuilder.get().sourceAddress());
        this.registerMessageBus(this.heartbeatService);
    }

    @Override
    public void onDisable() {

    }

    @Override
    @Nullable
    public IMessageServiceBus<?> getService(String messageChannel) {
        return this.messageBusHashMap.get(messageChannel);
    }

    @Override
    @NotNull
    public IMessageServiceBus<OnlineAnnouncementMessage.OnlineAnnouncementRequest> getHeartbeatService() {
        return this.heartbeatService;
    }
}
