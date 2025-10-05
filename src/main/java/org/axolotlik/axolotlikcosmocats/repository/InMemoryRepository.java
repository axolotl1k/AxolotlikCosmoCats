package org.axolotlik.axolotlikcosmocats.repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public abstract class InMemoryRepository<T> {

    protected final Map<Long, T> storage = new HashMap<>();
    protected final AtomicLong idGenerator = new AtomicLong(1);

    public Long generateId() {
        return idGenerator.getAndIncrement();
    }

    public T save(Long id, T entity) {
        storage.put(id, entity);
        return entity;
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
