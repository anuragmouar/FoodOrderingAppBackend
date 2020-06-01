package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ItemService itemService;

    @Autowired
    CustomerService customerService;

    /**
     * Retrieve all the restaurants in order of their ratings and display the response in a JSON format with the corresponding HTTP status.
     * Within each restaurant, the list of categories should be displayed in a categories string in alphabetical order of their category name and items shouldn’t be displayed.
     */
    @RequestMapping(method = RequestMethod.GET, path = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {
        List<RestaurantEntity> restaurantEntities = restaurantService.restaurantsByRating();
        List<RestaurantList> restaurantLists = getRestaurantList(restaurantEntities);
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantLists);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    /**
     * Retrieve all restaurants by given restaurant name.  It is not necessary for the restaurant name to exactly match that in the database.
     * Even if there is a partial match, all the restaurants corresponding to that name should be returned in alphabetical order of their names
     * @param restaurantName
     * @return list of restaurants by given restaurant name. If there are no restaurants by the name entered by the customer, return an empty list with corresponding HTTP status
     * @throws RestaurantNotFoundException when restaurant name field entered by the customer is empty
     */
    @RequestMapping(method = RequestMethod.GET, path = "/name/{restaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByName(@PathVariable(value = "restaurant_name") final String restaurantName) throws RestaurantNotFoundException {

        List<RestaurantEntity> restaurantEntities = restaurantService.restaurantsByName(restaurantName);

        if (!restaurantEntities.isEmpty()) {
            List<RestaurantList> restaurantLists = getRestaurantList(restaurantEntities);
            RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantLists);
            return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<RestaurantListResponse>(new RestaurantListResponse(), HttpStatus.OK);
        }

    }

    /**
     * Retrieve all restaurants by given category id
     *
     * @param categoryId
     * @return all restaurants by given category id. If there are no restaurants under the category entered by the customer, return an empty list with corresponding HTTP status.
     * @throws CategoryNotFoundException if the category id field entered by the customer is empty or if there is no category by the uuid entered by the customer
     */
    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByCategoryId(@PathVariable(value = "category_id") String categoryId) throws CategoryNotFoundException {

        List<RestaurantEntity> restaurantEntities = restaurantService.restaurantByCategory(categoryId);

        List<RestaurantList> restaurantLists = getRestaurantList(restaurantEntities);
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantLists);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    /**
     * Retrieve restaurant details by given restaurant id
     * @param restaurantUuid
     * @return restaurant’s details
     * @throws RestaurantNotFoundException if the restaurant id field entered by the customer is empty or if there is no restaurant by the uuid entered by the customer
     */
    @RequestMapping(method = RequestMethod.GET, path = "/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantByRestaurantId(@PathVariable(value = "restaurant_id") final String restaurantUuid) throws RestaurantNotFoundException {

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantUuid);

        List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantUuid);

        List<CategoryList> categoryLists = new LinkedList<>();
        for (CategoryEntity categoryEntity : categoryEntities) {

            List<ItemEntity> itemEntities = itemService.getItemsByCategoryAndRestaurant(restaurantUuid, categoryEntity.getUuid());
            List<ItemList> itemLists = new LinkedList<>();
            itemEntities.forEach(itemEntity -> {
                ItemList itemList = new ItemList()
                    .id(UUID.fromString(itemEntity.getUuid()))
                    .itemName(itemEntity.getitemName())
                    .price(itemEntity.getPrice())
                    .itemType(ItemList.ItemTypeEnum.valueOf(itemEntity.getType().getValue()));

                itemLists.add(itemList);
            });

            CategoryList categoryList = new CategoryList()
                .itemList(itemLists)
                .id(UUID.fromString(categoryEntity.getUuid()))
                .categoryName(categoryEntity.getCategoryName());

            categoryLists.add(categoryList);
        }

        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState()
            .id(UUID.fromString(restaurantEntity.getAddress().getState().getStateUuid()))
            .stateName(restaurantEntity.getAddress().getState().getStateName());

        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress()
            .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
            .city(restaurantEntity.getAddress().getCity())
            .flatBuildingName(restaurantEntity.getAddress().getFlatBuilNo())
            .locality(restaurantEntity.getAddress().getLocality())
            .pincode(restaurantEntity.getAddress().getPincode())
            .state(restaurantDetailsResponseAddressState);

        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse()
            .restaurantName(restaurantEntity.getRestaurantName())
            .address(restaurantDetailsResponseAddress)
            .averagePrice(restaurantEntity.getAvgPrice())
            .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
            .numberCustomersRated(restaurantEntity.getNumberCustomersRated())
            .id(UUID.fromString(restaurantEntity.getUuid()))
            .photoURL(restaurantEntity.getPhotoUrl())
            .categories(categoryLists);

        return new ResponseEntity<RestaurantDetailsResponse>(restaurantDetailsResponse, HttpStatus.OK);
    }

    /**
     * Updates restaurant details for given restaurant id.
     * @param authorization
     * @param restaurantUuid
     * @param customerRating
     * @return the uuid of the restaurant updated and message “RESTAURANT RATING UPDATED SUCCESSFULLY”
     * @throws AuthorizationFailedException if the access token provided by the customer does not exist in the database or if the access token provided by the customer exists in the database, but the customer has already logged out
     * @throws RestaurantNotFoundException if the restaurant id field entered by the customer is empty or If there is no restaurant by the uuid entered by the customer
     * @throws InvalidRatingException If the customer rating field entered by the customer is empty or is not in the range of 1 to 5
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/{restaurant_id}", params = "customer_rating", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(@RequestHeader("authorization") final String authorization, @PathVariable(value = "restaurant_id") final String restaurantUuid, @RequestParam(value = "customer_rating") final Double customerRating) throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {

        final String accessToken = authorization.split("Bearer ")[1];

        customerService.getCustomer(accessToken);

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantUuid);

        restaurantService.updateRestaurantRating(restaurantEntity, customerRating);

        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse()
            .id(UUID.fromString(restaurantUuid))
            .status("RESTAURANT RATING UPDATED SUCCESSFULLY");

        return new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse, HttpStatus.OK);
    }

    private List<RestaurantList> getRestaurantList(List<RestaurantEntity> restaurantEntities) {
        List<RestaurantList> restaurantLists = new LinkedList<>();

        for (RestaurantEntity restaurantEntity : restaurantEntities) {

            List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());
            String categories = new String();
            ListIterator<CategoryEntity> listIterator = categoryEntities.listIterator();
            while (listIterator.hasNext()) {
                categories = categories + listIterator.next().getCategoryName();
                if (listIterator.hasNext()) {
                    categories = categories + ", ";
                }
            }

            RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState()
                .id(UUID.fromString(restaurantEntity.getAddress().getState().getStateUuid()))
                .stateName(restaurantEntity.getAddress().getState().getStateName());

            RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress()
                .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                .city(restaurantEntity.getAddress().getCity())
                .flatBuildingName(restaurantEntity.getAddress().getFlatBuilNo())
                .locality(restaurantEntity.getAddress().getLocality())
                .pincode(restaurantEntity.getAddress().getPincode())
                .state(restaurantDetailsResponseAddressState);

            RestaurantList restaurantList = new RestaurantList()
                .id(UUID.fromString(restaurantEntity.getUuid()))
                .restaurantName(restaurantEntity.getRestaurantName())
                .averagePrice(restaurantEntity.getAvgPrice())
                .categories(categories)
                .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                .numberCustomersRated(restaurantEntity.getNumberCustomersRated())
                .photoURL(restaurantEntity.getPhotoUrl())
                .address(restaurantDetailsResponseAddress);

            restaurantLists.add(restaurantList);

        }
        return restaurantLists;
    }
}
