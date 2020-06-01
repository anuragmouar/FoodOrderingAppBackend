package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.Utility;
import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
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

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    RestaurantItemDao restaurantItemDao;

    @Autowired
    CategoryItemDao categoryItemDao;

    /**
     * This service method serves get items by popularity endpoint.
     *
     * @param restaurantEntity
     * @return list of item entity
     */
    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
        List <OrderEntity> orderEntityList = orderDAO.getOrdersByRestaurant(restaurantEntity);
        List <ItemEntity> itemEntityList = new LinkedList<>();
        orderEntityList.forEach(ordersEntity -> {
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

    /**
     * This service method serves item endpoint.
     *
     * @param restaurantUuid
     * @param categoryUuid
     * @return Item Entity
     */
    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantUuid, String categoryUuid) {

        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);

        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryUuid);

        List<RestaurantItemEntity> restaurantItemEntities = restaurantItemDao.getItemsByRestaurant(restaurantEntity);

        List<CategoryItemEntity> categoryItemEntities = categoryItemDao.getItemsByCategory(categoryEntity);

        List<ItemEntity> itemEntities = new LinkedList<>();

        restaurantItemEntities.forEach(restaurantItemEntity -> {
            categoryItemEntities.forEach(categoryItemEntity -> {
                if(restaurantItemEntity.getItem().equals(categoryItemEntity.getItem())){
                    itemEntities.add(restaurantItemEntity.getItem());
                }
            });
        });

        return itemEntities;
    }

    /**
     *  This method serves Item endpoint.
     *
     * @param itemUuid
     * @return item entity
     * @throws ItemNotFoundException
     */
    public ItemEntity getItemByUUID(String itemUuid) throws ItemNotFoundException {
        ItemEntity itemEntity = itemDAO.getItemByUUID(itemUuid);
        if(itemEntity == null){
            throw new ItemNotFoundException("INF-003","No item by this id exist");
        }
        return itemEntity;
    }
}
