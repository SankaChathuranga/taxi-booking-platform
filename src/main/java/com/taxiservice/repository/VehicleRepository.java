package com.taxiservice.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.taxiservice.model.Vehicle;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class VehicleRepository extends JsonFileRepository<Vehicle> {
    
    public VehicleRepository() {
        super("vehicles.json", Vehicle.class, new TypeReference<List<Vehicle>>() {});
    }

    @Override
    protected String getEntityId(Vehicle entity) {
        return entity.getId();
    }

    @Override
    protected void setEntityId(Vehicle entity, String id) {
        entity.setId(id);
    }

    @Override
    protected int findEntityIndex(List<Vehicle> entities, String id) {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public List<Vehicle> findByDriverId(String driverId) {
        return readAll().stream()
                .filter(vehicle -> vehicle.getDriverId().equals(driverId))
                .collect(Collectors.toList());
    }
} 