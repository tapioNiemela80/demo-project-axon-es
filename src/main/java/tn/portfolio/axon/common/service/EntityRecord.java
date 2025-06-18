package tn.portfolio.axon.common.service;

import jakarta.persistence.Tuple;

import java.util.UUID;

public class EntityRecord {
    private final Tuple data;

    public EntityRecord(Tuple data) {
        this.data = data;
    }

    public String getString(String key){
        return data.get(key, String.class);
    }

    public UUID getUUID(String key){
        return data.get(key, UUID.class);
    }

    public Integer getInteger(String key){
        return data.get(key, Integer.class);
    }

}
