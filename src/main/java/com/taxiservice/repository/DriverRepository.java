package com.taxiservice.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.taxiservice.model.Driver;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DriverRepository extends JsonFileRepository<Driver> {
    
    public DriverRepository() {
        super("drivers.json", Driver.class, new TypeReference<List<Driver>>() {});
    }

    @Override
    protected String getEntityId(Driver entity) {
        return entity.getId();
    }

    @Override
    protected void setEntityId(Driver entity, String id) {
        entity.setId(id);
    }

    @Override
    protected int findEntityIndex(List<Driver> entities, String id) {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public List<Driver> findAvailableDrivers() {
        return readAll().stream()
                .filter(Driver::isAvailability)
                .collect(Collectors.toList());
    }
} 