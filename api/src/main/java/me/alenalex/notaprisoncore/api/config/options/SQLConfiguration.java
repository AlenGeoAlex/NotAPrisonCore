package me.alenalex.notaprisoncore.api.config.options;

import com.zaxxer.hikari.HikariConfig;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfigurationOption;

import java.util.HashMap;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class SQLConfiguration extends AbstractConfigurationOption {

    private String hostName;
    private int port;
    private String userName;
    private String password;
    private String database;
    private boolean doAutoReconnect;
    private boolean useSSL;
    private final SQLConfiguration.HikariProperties hikariProperties;

    public SQLConfiguration(Section section) {
        super(section);
        this.hikariProperties = new HikariProperties(getSection().getSection("hikari-properties"));
    }

    @Override
    public void load() {
        this.hostName = getSection().getString("host");
        this.port = getSection().getInt("port");
        this.userName = getSection().getString("username");
        this.password = getSection().getString("password");
        this.database = getSection().getString("database");
        this.doAutoReconnect = getSection().getBoolean("auto-reconnect");
        this.useSSL = getSection().getBoolean("use-ssl");
        this.hikariProperties.load();
    }

    @Override
    public ValidationResponse validate() {
        ValidationResponse.Builder builder = ValidationResponse.Builder.builder();
        if(hostName == null || hostName.isEmpty()){
            builder.withErrors("Host address for SQL connection is required");
        }

        if(port <= 0){
            builder.withWarnings("Invalid port range found, defaulting to 3306");
            this.port = 3306;
        }

        if(userName == null || userName.isEmpty()){
            builder.withErrors("Username is required for SQL connection");
        }

        if(database == null || database.isEmpty()){
            builder.withErrors("Database is required for SQL Connection");
        }

        return builder.build();
    }

    public final String asJdbcUrl(String driver){
        final String JDBC_REMOTE_URL = "jdbc:"
                + "%s" //Type of DB (postgresql,mysql)
                + "://"
                + "%s" // host
                + ":"
                + "%d" // port
                + "/"
                + "%s" // database
                + "?autoReconnect="
                + "%s" // auto reconnect
                + "&"
                + "useSSL="
                + "%s" // use ssl
                ;

        return  String.format(
                JDBC_REMOTE_URL,
                driver,
                getHostName(),
                getPort(),
                getDatabase(),
                isDoAutoReconnect(),
                isUseSSL()
        );
    }

    public final HikariConfig asHikariConfig(String driver){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(asJdbcUrl(driver));
        config.setUsername(getUserName());
        config.setPassword(getPassword());

        config.setPoolName(this.hikariProperties.getConnectionPoolName());
        this.hikariProperties.getCustomHikariProperties().forEach(config::addDataSourceProperty);
        return config;
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class HikariProperties extends AbstractConfigurationOption{

        private String connectionPoolName;
        private HashMap<String, String> customHikariProperties;
        public HikariProperties(Section section) {
            super(section);
            this.customHikariProperties = new HashMap<>();
        }

        @Override
        public void load() {
            this.connectionPoolName = super.getSection().getString("pool-name");
            Section customHikariPropertySection = super.getSection().getSection("custom-hikari-properties");
            Set<String> customPropertiesKey = customHikariPropertySection.getRoutesAsStrings(false);
            customPropertiesKey.forEach(eachProp -> {
                customHikariProperties.put(eachProp, customHikariPropertySection.getString(eachProp));
            });
        }
    }

}
