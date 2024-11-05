package com.fet.venus.db.dao.impl;

import com.fet.venus.db.dao.IOperations;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractHibernateDaoImpl<T extends Serializable> implements IOperations<T> {

    @Override
    public T findOne(long id) {
        return null;
    }

    @Override
    public T findOne(String id) {
        return null;
    }

    @Override
    public List<T> findAll() {
        return List.of();
    }

    @Override
    public T create(T entity) {
        return null;
    }

    @Override
    public T update(T entity) {
        return null;
    }

    @Override
    public void delete(T entity) {

    }
}
