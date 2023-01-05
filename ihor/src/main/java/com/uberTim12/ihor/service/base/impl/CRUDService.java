package com.uberTim12.ihor.service.base.impl;

import com.uberTim12.ihor.exception.NotFoundException;
import com.uberTim12.ihor.service.base.interfaces.ICRUDService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class CRUDService<T> implements ICRUDService<T> {

    protected abstract JpaRepository<T, Integer> getEntityRepository();

    @Override
    public List<T> getAll() {
        return getEntityRepository().findAll();
    }

    @Override
    public T get(Integer id) throws EntityNotFoundException {
        return findEntityChecked(id);
    }

    @Override
    public T save(T entity) {
        return getEntityRepository().save(entity);
    }

    @Override
    public T update(T entity) {
        return save(entity);
    }

    @Override
    public void delete(Integer id) {
        getEntityRepository().deleteById(id);
//        var entity = findEntityChecked(id);
//        entity.setActive(false);
    }

    private T findEntityChecked(Integer id) throws EntityNotFoundException {
//        if (Boolean.TRUE.equals(entity.getActive())) {
//            return entity;
//        }
//        throw new NotFoundException("Cannot find entity with id: " + id);

        return getEntityRepository().findById(id).orElseThrow(() -> new NotFoundException("Cannot find entity with id: " + id));
    }
}
