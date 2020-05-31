package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

//This Class is created to access DB with respect to Category entity

@Repository
public class RestaurantCategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    //To get the list of RestaurantCategoryEntity from the db by restaurant
    public List<RestaurantCategoryEntity> getCategoriesByRestaurant(RestaurantEntity restaurantEntity){
        try {
            List<RestaurantCategoryEntity> restaurantCategoryEntity = entityManager.createNamedQuery("getCategoriesByRestaurant",RestaurantCategoryEntity.class).setParameter("restaurant",restaurantEntity).getResultList();
            return restaurantCategoryEntity;
        }catch (NoResultException nre){
            return null;
        }

    }

    //To get the list of RestaurantCategoryEntity from the db by category
    public List<RestaurantCategoryEntity> getRestaurantByCategory(CategoryEntity categoryEntity) {
        try {
            List<RestaurantCategoryEntity> restaurantCategoryEntities = entityManager.createNamedQuery("getRestaurantByCategory",RestaurantCategoryEntity.class).setParameter("category",categoryEntity).getResultList();
            return restaurantCategoryEntities;
        }catch (NoResultException nre){
            return null;
        }
    }
}
