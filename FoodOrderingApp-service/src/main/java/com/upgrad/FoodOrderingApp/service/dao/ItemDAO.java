package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * This class facilitates DB interaction to Item entity.
 */
@Repository
public class ItemDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method gets Item by UUID.
     *
     * @param uuid
     * @return item entity
     */
    public ItemEntity getItemByUUID(String uuid) {
        try {
            ItemEntity itemEntity = entityManager.createNamedQuery("getItemByUUID",ItemEntity.class).setParameter("uuid",uuid).getSingleResult();
            return itemEntity;
        }catch (NoResultException nre){
            return null;
        }
    }
}
