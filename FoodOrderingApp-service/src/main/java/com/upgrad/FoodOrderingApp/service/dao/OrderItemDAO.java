package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
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
     * @param orderEntity
     * @return order entity list
     */
    public List<OrderItemEntity> getItemsByOrders(OrderEntity orderEntity) {
        try{
            List<OrderItemEntity> orderItemEntities = entityManager.createNamedQuery("getItemsByOrders", OrderItemEntity.class).setParameter("orderEntity", orderEntity).getResultList();
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
     * @param orderEntity
     * @return list of order entity
     */
    public List<OrderItemEntity> getOrderItemsByOrder(OrderEntity orderEntity) {
        try {
            List<OrderItemEntity> orderItemEntities = entityManager.createNamedQuery("getOrderItemsByOrder",OrderItemEntity.class).setParameter("orders", orderEntity).getResultList();
            return orderItemEntities;
        }catch (NoResultException nre){
            return null;
        }
    }
}
