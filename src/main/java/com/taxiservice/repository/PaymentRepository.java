package com.taxiservice.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.taxiservice.model.Payment;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PaymentRepository extends JsonFileRepository<Payment> {
    
    public PaymentRepository() {
        super("payments.json", Payment.class, new TypeReference<List<Payment>>() {});
    }

    @Override
    protected String getEntityId(Payment entity) {
        return entity.getId();
    }

    @Override
    protected void setEntityId(Payment entity, String id) {
        entity.setId(id);
    }

    @Override
    protected int findEntityIndex(List<Payment> entities, String id) {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public List<Payment> findByUserId(String userId) {
        return readAll().stream()
                .filter(payment -> payment.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Payment> findByDriverId(String driverId) {
        return readAll().stream()
                .filter(payment -> payment.getDriverId() != null && payment.getDriverId().equals(driverId))
                .collect(Collectors.toList());
    }

    public List<Payment> findByBookingId(String bookingId) {
        return readAll().stream()
                .filter(payment -> payment.getBookingId().equals(bookingId))
                .collect(Collectors.toList());
    }

    public List<Payment> findByStatus(String status) {
        return readAll().stream()
                .filter(payment -> payment.getStatus().equals(status))
                .collect(Collectors.toList());
    }
}
