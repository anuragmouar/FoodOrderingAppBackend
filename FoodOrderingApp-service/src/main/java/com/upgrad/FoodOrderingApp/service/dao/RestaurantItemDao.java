package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantItemEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

public class RestaurantItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantItemEntity> getItemsByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            List<RestaurantItemEntity> restaurantItemEntities = entityManager.createNamedQuery("getItemsByRestaurant",RestaurantItemEntity.class).setParameter("restaurant",restaurantEntity).getResultList();
            return restaurantItemEntities;
        }catch (NoResultException nre){
            return null;
        }
    }
}
