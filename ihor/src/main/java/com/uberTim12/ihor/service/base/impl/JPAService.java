package com.uberTim12.ihor.service.base.impl;

import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Transactional
public abstract class JPAService<T> extends CRUDService<T> implements IJPAService<T> {
    @Override
    public Iterable<T> getAll(Sort sorter) {
        return getEntityRepository().findAll(sorter);
    }

    @Override
    public Page<T> getAll(Pageable page) {
        return getEntityRepository().findAll(page);
    }
}
