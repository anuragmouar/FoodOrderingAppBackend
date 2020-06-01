package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDAO;
import com.upgrad.FoodOrderingApp.service.dao.OrderDAO;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDAO;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *  This service class serves Order endpoint.
 */
@Service
public class OrderService {

    @Autowired
    OrderDAO orderDao;
    @Autowired
    CouponDao couponDao;
    @Autowired
    OrderItemDAO orderItemDao;
    @Autowired
    CustomerDAO customerDao;

    /**
     * This method serves coupon endpoint to get coupon by coupon name.
     *
     * @param couponName
     * @return coupon
     * @throws CouponNotFoundException
     */
    public CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException {
        if(couponName == null||couponName == ""){
            throw new CouponNotFoundException("CPF-002","Coupon name field should not be empty");
        }
        CouponEntity couponEntity = couponDao.getCouponByCouponName(couponName);
        if(couponEntity == null){
            throw new CouponNotFoundException("CPF-001","No coupon by this name");
        }
        return couponEntity;
    }

    /**
     *  This method serves Coupon endpoint to get coupon by uuid.
     *
     * @param couponUuid
     * @return coupon
     * @throws CouponNotFoundException
     */
    public CouponEntity getCouponByCouponId(String couponUuid) throws CouponNotFoundException {
        CouponEntity couponEntity = couponDao.getCouponByCouponId(couponUuid);
        if(couponEntity == null){
            throw new CouponNotFoundException("CPF-002","No coupon by this id");
        }
        return couponEntity;
    }

    /**
     * This method serves Order endpoint to save order.
     *
     * @param ordersEntity
     * @return Order
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrdersEntity saveOrder(OrdersEntity ordersEntity) {
        OrdersEntity savedOrderEntity = orderDao.saveOrder(ordersEntity);
        return savedOrderEntity;
    }

    /**
     * This method serves Order item endpoint.
     *
     * @param orderItemEntity
     * @return Order item
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
        OrderItemEntity savedOrderItemEntity = orderItemDao.saveOrderItem(orderItemEntity);
        return savedOrderItemEntity;
    }

    /**
     * This method serves Order by customer endpoint.
     *
     * @param uuid
     * @return List of order
     */
    public List<OrdersEntity> getOrdersByCustomers(String uuid) {
        CustomerEntity customerEntity = customerDao.getCustomerByUuid(uuid);
        List<OrdersEntity> ordersEntities = orderDao.getOrdersByCustomers(customerEntity);
        return ordersEntities;
    }

    /**
     * This method serves Order item endpoint.
     *
     * @param ordersEntity
     * @return list of order entity.
     */
    public List<OrderItemEntity> getOrderItemsByOrder(OrdersEntity ordersEntity) {
        List<OrderItemEntity> orderItemEntities = orderItemDao.getOrderItemsByOrder(ordersEntity);
        return orderItemEntities;
    }
}
