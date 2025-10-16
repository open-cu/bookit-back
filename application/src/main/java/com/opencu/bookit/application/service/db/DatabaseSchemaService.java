package com.opencu.bookit.application.service.db;


import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;



@Service
public class DatabaseSchemaService {

    private final DataSource dataSource;

    public DatabaseSchemaService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Map<String, Object>> getTables() throws SQLException {
        List<Map<String, Object>> tables = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String schema = "public";

            try (ResultSet tablesRs = metaData.getTables(conn.getCatalog(), schema, "%", new String[]{"TABLE"})) {
                while (tablesRs.next()) {
                    String tableName = tablesRs.getString("TABLE_NAME");
                    String tableSchema = tablesRs.getString("TABLE_SCHEM");

                    if (!"public".equalsIgnoreCase(tableSchema)) {
                        continue;
                    }

                    Map<String, Object> tableInfo = new HashMap<>();
                    tableInfo.put("name", tableName);
                    tableInfo.put("schema", tableSchema);

                    List<Map<String, Object>> columns = new ArrayList<>();
                    try (ResultSet cols = metaData.getColumns(conn.getCatalog(), schema, tableName, "%")) {
                        while (cols.next()) {
                            Map<String, Object> col = new HashMap<>();
                            col.put("name", cols.getString("COLUMN_NAME"));
                            col.put("type", cols.getString("TYPE_NAME"));
                            col.put("nullable", cols.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                            col.put("size", cols.getInt("COLUMN_SIZE"));
                            columns.add(col);
                        }
                    }

                    tableInfo.put("columns", columns);
                    tables.add(tableInfo);
                }
            }
        }
        return tables;
    }

}