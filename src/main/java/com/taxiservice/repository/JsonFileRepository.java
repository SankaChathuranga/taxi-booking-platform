package com.taxiservice.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public abstract class JsonFileRepository<T> implements BaseRepository<T> {
    private static final Logger logger = LoggerFactory.getLogger(JsonFileRepository.class);
    protected final ObjectMapper objectMapper;
    protected final File dataFile;
    protected final Class<T> entityClass;
    protected final TypeReference<List<T>> typeReference;

    protected JsonFileRepository(String fileName, Class<T> entityClass, TypeReference<List<T>> typeReference) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.entityClass = entityClass;
        this.typeReference = typeReference;
        this.dataFile = new File("src/main/resources/data/" + fileName);
        logger.info("Initializing repository for file: {}", dataFile.getAbsolutePath());
        initializeFile();
    }

    private void initializeFile() {
        try {
            if (!dataFile.exists()) {
                logger.info("Data file does not exist, creating new file");
                dataFile.getParentFile().mkdirs();
                objectMapper.writeValue(dataFile, new ArrayList<>());
            } else {
                logger.info("Data file exists at: {}", dataFile.getAbsolutePath());
                List<T> existingData = readAll();
                logger.info("Existing data in file: {}", existingData);
            }
        } catch (IOException e) {
            logger.error("Failed to initialize data file: {}", dataFile.getName(), e);
            throw new RuntimeException("Failed to initialize data file: " + dataFile.getName(), e);
        }
    }

    protected List<T> readAll() {
        try {
            List<T> data = objectMapper.readValue(dataFile, typeReference);
            logger.info("Read {} records from {}", data.size(), dataFile.getName());
            return data;
        } catch (IOException e) {
            logger.error("Failed to read data from file: {}", dataFile.getName(), e);
            throw new RuntimeException("Failed to read data from file: " + dataFile.getName(), e);
        }
    }

    protected void writeAll(List<T> entities) {
        try {
            logger.info("Writing {} records to {}", entities.size(), dataFile.getName());
            objectMapper.writeValue(dataFile, entities);
        } catch (IOException e) {
            logger.error("Failed to write data to file: {}", dataFile.getName(), e);
            throw new RuntimeException("Failed to write data to file: " + dataFile.getName(), e);
        }
    }

    @Override
    public T save(T entity) {
        List<T> entities = readAll();
        if (getEntityId(entity) == null) {
            setEntityId(entity, UUID.randomUUID().toString());
            entities.add(entity);
        } else {
            int index = findEntityIndex(entities, getEntityId(entity));
            if (index != -1) {
                entities.set(index, entity);
            } else {
                entities.add(entity);
            }
        }
        writeAll(entities);
        return entity;
    }

    @Override
    public Optional<T> findById(String id) {
        return readAll().stream()
                .filter(entity -> getEntityId(entity).equals(id))
                .findFirst();
    }

    @Override
    public List<T> findAll() {
        return readAll();
    }

    @Override
    public void delete(String id) {
        List<T> entities = readAll();
        entities.removeIf(entity -> getEntityId(entity).equals(id));
        writeAll(entities);
    }

    protected abstract String getEntityId(T entity);
    protected abstract void setEntityId(T entity, String id);
    protected abstract int findEntityIndex(List<T> entities, String id);
} 