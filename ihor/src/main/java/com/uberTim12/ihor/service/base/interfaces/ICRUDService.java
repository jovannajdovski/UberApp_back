package com.uberTim12.ihor.service.base.interfaces;

import java.util.List;

public interface ICRUDService<T> {
    List<T> getAll();
    T get(Integer id);
    T save(T entity);
    T update(T entity);
    void delete(Integer id);
}
