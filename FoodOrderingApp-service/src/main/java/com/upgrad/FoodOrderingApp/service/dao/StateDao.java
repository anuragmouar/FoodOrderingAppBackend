package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class StateDao {

    @PersistenceContext
    private EntityManager entityManager;

    public StateEntity getStateByUuid(String uuid) {
        try {
            StateEntity stateEntity = entityManager.createNamedQuery("getStateByUuid", StateEntity.class).setParameter("uuid", uuid).getSingleResult();
            return stateEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<StateEntity> getAllStates() {
        try {
            List<StateEntity> stateEntities = entityManager.createNamedQuery("getAllStates", StateEntity.class).getResultList();
            return stateEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }
}
