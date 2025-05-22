package com.taxiservice.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.taxiservice.model.User;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class UserRepository extends JsonFileRepository<User> {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public UserRepository() {
        super("users.json", User.class, new TypeReference<List<User>>() {});
    }

    @Override
    protected String getEntityId(User entity) {
        return entity.getId();
    }

    @Override
    protected void setEntityId(User entity, String id) {
        entity.setId(id);
    }

    @Override
    protected int findEntityIndex(List<User> entities, String id) {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public Optional<User> findByEmail(String email) {
        List<User> users = readAll();
        logger.info("Searching for user with email: {}", email);
        logger.info("Total users in database: {}", users.size());
        users.forEach(user -> logger.info("User in DB: {}", user.getEmail()));
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
}