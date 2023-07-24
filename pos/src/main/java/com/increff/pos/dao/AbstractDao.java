package com.increff.pos.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;


public abstract class AbstractDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void insert(Object obj) {
        entityManager.persist(obj);
    }

    public <T> T getById(Class<T> entityClass, Integer entityId) {
        return entityManager.find(entityClass, entityId);
    }

    public <T> List<T> getAll(Class<T> entityClass) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);

        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    protected EntityManager em() {
        return entityManager;
    }
}


