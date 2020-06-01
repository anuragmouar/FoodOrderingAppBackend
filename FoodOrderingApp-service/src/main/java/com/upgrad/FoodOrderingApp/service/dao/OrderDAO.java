package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * This class facilitates DB interaction to Order entity.
 */
@Repository
public class OrderDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method gets order by restaurant.
     *
     * @param restaurantEntity
     * @return order entity list
     */
    public List<OrdersEntity> getOrdersByRestaurant(RestaurantEntity restaurantEntity) {
        try{
            List<OrdersEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByRestaurant",OrdersEntity.class).setParameter("restaurant",restaurantEntity).getResultList();
            return ordersEntities;
        }catch (NoResultException nre){
            return null;
        }
    }

    //To get all the order corresponding to the address
    public List<OrdersEntity> getOrdersByAddress(AddressEntity addressEntity) {
        try{
            List<OrdersEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByAddress",OrdersEntity.class).setParameter("address",addressEntity).getResultList();
            return ordersEntities;
        }catch (NoResultException nre) {
            return null;
        }
    }
}
