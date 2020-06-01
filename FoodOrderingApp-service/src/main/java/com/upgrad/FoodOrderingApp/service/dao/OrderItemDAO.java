package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * This class facilitates DB interaction to OrderItem entity.
 */
@Repository
public class OrderItemDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method gets item by order.
     *
     * @param ordersEntity
     * @return order entity list
     */
    public List<OrderItemEntity> getItemsByOrders(OrdersEntity ordersEntity) {
        try{
            List<OrderItemEntity> orderItemEntities = entityManager.createNamedQuery("getItemsByOrders", OrderItemEntity.class).setParameter("ordersEntity",ordersEntity).getResultList();
            return orderItemEntities;
        }catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method saves order item.
     *
     * @param orderItemEntity
     * @return order item
     */
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
        entityManager.persist(orderItemEntity);
        return orderItemEntity;
    }

    /**
     * This method gets order items by order.
     *
     * @param ordersEntity
     * @return list of order entity
     */
    public List<OrderItemEntity> getOrderItemsByOrder(OrdersEntity ordersEntity) {
        try {
            List<OrderItemEntity> orderItemEntities = entityManager.createNamedQuery("getOrderItemsByOrder",OrderItemEntity.class).setParameter("orders",ordersEntity).getResultList();
            return orderItemEntities;
        }catch (NoResultException nre){
            return null;
        }
    }
}
