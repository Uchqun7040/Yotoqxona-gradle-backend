package com.example.Yotoqxonagradle.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GeneralService<Entity,IdType>{
    public Page<Entity> getAll(Pageable pageable);
    public Entity create(Entity data);
    public Entity update(Entity data);
    public void delete(Entity data);
    public void deleteById(IdType id);
    Optional<Entity> getById(IdType id);
}
