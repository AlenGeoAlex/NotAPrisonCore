package me.alenalex.notaprisoncore.paper.database;

import com.zaxxer.hikari.HikariConfig;
import me.alenalex.notaprisoncore.api.database.SQLDatabase;

import java.util.logging.Logger;


public class PrisonSqlDatabase extends SQLDatabase {

    public PrisonSqlDatabase(HikariConfig hikariConfig, Logger logger) {
        super(hikariConfig, logger);
    }



}
