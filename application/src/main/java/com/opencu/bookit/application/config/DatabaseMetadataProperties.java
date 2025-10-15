package com.opencu.bookit.application.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.database.metadata")
@Getter
@Setter
@Component
public class DatabaseMetadataProperties {
    private boolean enabled = true;
    private String productName;
    private String productVersion;
    private List<TableMetadata> tables = new ArrayList<>();
    private List<String> enumTypes = new ArrayList<>();

    @Getter
    @Setter
    public static class TableMetadata {
        private String name;
        private String schema;
        private List<ColumnMetadata> columns = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class ColumnMetadata {
        private String name;
        private String type;
        private int size;
        private boolean nullable;
        private String defaultValue;
    }
}
