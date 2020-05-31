package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantEntity> restaurantsByRating(){
        try{
            List<RestaurantEntity> restaurantEntities = entityManager.createNamedQuery("restaurantsByRating",RestaurantEntity.class).getResultList();
            return restaurantEntities;
        }catch (NoResultException nre){
            return null;
        }
    }

    public RestaurantEntity getRestaurantByUuid(String uuid) {
        try {
            RestaurantEntity restaurantEntity = entityManager.createNamedQuery("getRestaurantByUuid",RestaurantEntity.class).setParameter("uuid",uuid).getSingleResult();
            return restaurantEntity;
        }catch (NoResultException nre){
            return null;
        }

    }

    public List<RestaurantEntity> restaurantsByName(String restaurantName) {
        try {
            String restaurantNameLow = "%"+restaurantName.toLowerCase()+"%";
            List<RestaurantEntity> restaurantEntities = entityManager.createNamedQuery("restaurantsByName", RestaurantEntity.class).setParameter("restaurant_name_low",restaurantNameLow).getResultList();
            return restaurantEntities;
        }catch (NoResultException nre){
            return null;
        }

    }

    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity) {
        entityManager.merge(restaurantEntity);
        return restaurantEntity;
    }
}