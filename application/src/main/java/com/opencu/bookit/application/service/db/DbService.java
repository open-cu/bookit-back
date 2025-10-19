package com.opencu.bookit.application.service.db;

import com.opencu.bookit.application.config.DatabaseMetadataProperties;
import com.opencu.bookit.application.port.out.db.LoadDbMetaDataPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbService {
    private final LoadDbMetaDataPort loadDbMetaDataPort;

    public DbService(LoadDbMetaDataPort loadDbMetaDataPort) {
        this.loadDbMetaDataPort = loadDbMetaDataPort;
    }


    public DatabaseMetadataProperties getDatabaseMetadataProperties() {
        return loadDbMetaDataPort.getMetadataProperties();
    }

    public List<DatabaseMetadataProperties.TableMetadata> getTableMetadata() {
        return loadDbMetaDataPort.getTableMetadata();
    }
}
