package com.taxiservice.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.taxiservice.model.Booking;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BookingRepository extends JsonFileRepository<Booking> {
    
    public BookingRepository() {
        super("bookings.json", Booking.class, new TypeReference<List<Booking>>() {});
    }

    @Override
    protected String getEntityId(Booking entity) {
        return entity.getId();
    }

    @Override
    protected void setEntityId(Booking entity, String id) {
        entity.setId(id);
    }

    @Override
    protected int findEntityIndex(List<Booking> entities, String id) {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public List<Booking> findByUserId(String userId) {
        return readAll().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Booking> findByDriverId(String driverId) {
        return readAll().stream()
                .filter(booking -> driverId != null && driverId.equals(booking.getDriverId()))
                .collect(Collectors.toList());
    }
} 