package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.application.config.DatabaseMetadataProperties;
import com.opencu.bookit.application.port.out.db.LoadDbMetaDataPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DbMetadataAdapter implements LoadDbMetaDataPort {

    private final DatabaseMetadataProperties metadataProperties;

    /**
     * @return 
     */
    @Override
    public DatabaseMetadataProperties getMetadataProperties() {
        return metadataProperties;
    }

    /**
     * @return 
     */
    @Override
    public List<DatabaseMetadataProperties.TableMetadata> getTableMetadata() {
        return metadataProperties.getTables();
    }
}
