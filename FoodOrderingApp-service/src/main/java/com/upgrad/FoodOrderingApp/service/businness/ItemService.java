package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.Utility;
import com.upgrad.FoodOrderingApp.service.dao.ItemDAO;
import com.upgrad.FoodOrderingApp.service.dao.OrderDAO;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDAO;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class provides service to Item endpoint.
 */
@Service
public class ItemService {

    @Autowired
    OrderDAO orderDAO;
    @Autowired
    Utility utility;
    @Autowired
    ItemDAO itemDAO;
    @Autowired
    RestaurantDao restaurantDao;
    @Autowired
    OrderItemDAO orderItemDAO;

    /**
     * This service method serves get items by popularity endpoint.
     *
     * @param restaurantEntity
     * @return list of item entity
     */
    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
        List <OrdersEntity> ordersEntityList = orderDAO.getOrdersByRestaurant(restaurantEntity);
        List <ItemEntity> itemEntityList = new LinkedList<>();
        ordersEntityList.forEach(ordersEntity -> {
            List <OrderItemEntity> orderItemEntityList = orderItemDAO.getItemsByOrders(ordersEntity);
            orderItemEntityList.forEach(orderItemEntity -> {
                itemEntityList.add(orderItemEntity.getItem());
            });
        });
        Map<String,Integer> itemCountMap = new HashMap<String,Integer>();
        itemEntityList.forEach(itemEntity -> {
            Integer count = itemCountMap.get(itemEntity.getUuid());
            itemCountMap.put(itemEntity.getUuid(),(count == null) ? 1 : count+1);
        });
        Map<String,Integer> sortedItemCountMap = utility.sortMapByValues(itemCountMap);
        List<ItemEntity> sortedItemEntityList = new LinkedList<>();
        Integer count = 0;
        for(Map.Entry<String,Integer> item:sortedItemCountMap.entrySet()){
            if(count < 5) {
                sortedItemEntityList.add(itemDAO.getItemByUUID(item.getKey()));
                count = count+1;
            }else{
                break;
            }
        }
        return sortedItemEntityList;
    }
}
