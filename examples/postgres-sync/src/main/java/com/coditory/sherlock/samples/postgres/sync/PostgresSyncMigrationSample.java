package com.coditory.sherlock.samples.postgres.sync;

import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.migrator.SherlockMigrator;
import com.coditory.sherlock.sql.SqlSherlock;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class PostgresSyncMigrationSample {
    private static final Logger logger = LoggerFactory.getLogger(PostgresSyncMigrationSample.class);

    private static DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/test");
        config.setUsername("postgres");
        config.setPassword("postgres");
        return new HikariDataSource(config);
    }

    public static void main(String[] args) {
        Sherlock sherlock = SqlSherlock.create(dataSource());
        // first commit - all migrations are executed
        SherlockMigrator.builder(sherlock)
            .addChangeSet("change-set-1", () -> logger.info("Change-set 1"))
            .addChangeSet("change-set-2", () -> logger.info("Change-set 2"))
            .migrate();
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
            .addChangeSet("change-set-1", () -> logger.info("Change-set 1"))
            .addChangeSet("change-set-2", () -> logger.info("Change-set 2"))
            .addChangeSet("change-set-3", () -> logger.info("Change-set 3"))
            .migrate();
    }
}
