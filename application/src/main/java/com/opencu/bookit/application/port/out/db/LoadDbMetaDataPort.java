package com.opencu.bookit.application.port.out.db;

import com.opencu.bookit.application.config.DatabaseMetadataProperties;

import java.util.List;

public interface LoadDbMetaDataPort {
    DatabaseMetadataProperties getMetadataProperties();
    List<DatabaseMetadataProperties.TableMetadata> getTableMetadata();
}
