package com.opencu.bookit.adapter.in.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencu.bookit.application.config.DatabaseMetadataProperties;
import com.opencu.bookit.application.service.db.DatabaseSchemaService;
import com.opencu.bookit.application.service.db.DbService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@Profile("dev")  // Только для dev среды
@RequestMapping("/api/dev/database-metadata")
public class DatabaseMetadataController {

    private final DbService dbService;
    private final DatabaseSchemaService databaseSchemaService;

    public DatabaseMetadataController(DbService dbService, DatabaseSchemaService databaseSchemaService) {
        this.dbService = dbService;
        this.databaseSchemaService = databaseSchemaService;
    }


    @GetMapping
    public List<Map<String, Object>> getMetadata() throws JsonProcessingException, SQLException {
        return databaseSchemaService.getTables();
    }

    @GetMapping("/tables")
    public List<DatabaseMetadataProperties.TableMetadata> getTables() {
        return dbService.getTableMetadata();
    }
}
