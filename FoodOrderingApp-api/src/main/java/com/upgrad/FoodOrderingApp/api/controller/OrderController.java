package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * This class will handle all Order endpoint.
 */
@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    CustomerService customerService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    AddressService addressService;
    @Autowired
    RestaurantService restaurantService;
    @Autowired
    ItemService itemService;

    /**
     * This is Coupon endpoint
     *
     * @param authorization
     * @param couponName
     * @return coupon details
     * @throws AuthorizationFailedException
     * @throws CouponNotFoundException
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByCouponName(@RequestHeader(value = "authorization") final String authorization, @PathVariable(value = "coupon_name") final String couponName) throws AuthorizationFailedException, CouponNotFoundException {
        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        CouponEntity couponEntity = orderService.getCouponByCouponName(couponName);
        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse()
                .couponName(couponEntity.getCouponName())
                .id(UUID.fromString(couponEntity.getUuid()))
                .percent(couponEntity.getPercent());
        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);
    }

    /**
     *  This is Order endpoint.
     *
     * @param authorization
     * @param saveOrderRequest
     * @return order
     * @throws AuthorizationFailedException
     * @throws PaymentMethodNotFoundException
     * @throws AddressNotFoundException
     * @throws RestaurantNotFoundException
     * @throws CouponNotFoundException
     * @throws ItemNotFoundException
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader(value = "authorization") final String authorization, @RequestBody(required = false) final SaveOrderRequest saveOrderRequest) throws AuthorizationFailedException, PaymentMethodNotFoundException, AddressNotFoundException, RestaurantNotFoundException, CouponNotFoundException, ItemNotFoundException {
        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        CouponEntity couponEntity = orderService.getCouponByCouponId(saveOrderRequest.getCouponId().toString());
        PaymentEntity paymentEntity = paymentService.getPaymentByUUID(saveOrderRequest.getPaymentId().toString());
        AddressEntity addressEntity = addressService.getAddressByUUID(saveOrderRequest.getAddressId(), customerEntity);
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(saveOrderRequest.getRestaurantId().toString());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        OrdersEntity ordersEntity = new OrdersEntity();
        ordersEntity.setUuid(UUID.randomUUID().toString());
        ordersEntity.setBill(saveOrderRequest.getBill().floatValue());
        ordersEntity.setDate(timestamp);
        ordersEntity.setCustomer(customerEntity);
        ordersEntity.setDiscount(saveOrderRequest.getDiscount().doubleValue());
        ordersEntity.setPayment(paymentEntity);
        ordersEntity.setAddress(addressEntity);
        ordersEntity.setRestaurant(restaurantEntity);
        ordersEntity.setCoupon(couponEntity);
        OrdersEntity savedOrderEntity = orderService.saveOrder(ordersEntity);
        List<ItemQuantity> itemQuantities = saveOrderRequest.getItemQuantities();
        for (ItemQuantity itemQuantity : itemQuantities) {
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            ItemEntity itemEntity = itemService.getItemByUUID(itemQuantity.getItemId().toString());
            orderItemEntity.setItem(itemEntity);
            orderItemEntity.setOrder(ordersEntity);
            orderItemEntity.setPrice(itemQuantity.getPrice());
            orderItemEntity.setQuantity(itemQuantity.getQuantity());
            OrderItemEntity savedOrderItem = orderService.saveOrderItem(orderItemEntity);
        }
        SaveOrderResponse saveOrderResponse = new SaveOrderResponse()
                .id(savedOrderEntity.getUuid())
                .status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<SaveOrderResponse>(saveOrderResponse, HttpStatus.CREATED);
    }

    /**
     *  This is Customer order endpoint.
     *
     * @param authorization
     * @return Customer order
     * @throws AuthorizationFailedException
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getPastOrderOfUser(@RequestHeader(value = "authorization") final String authorization) throws AuthorizationFailedException {
        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        List<OrdersEntity> ordersEntities = orderService.getOrdersByCustomers(customerEntity.getUuid());
        List<OrderList> orderLists = new LinkedList<OrderList>();

        if (ordersEntities != null) {
            for (OrdersEntity ordersEntity : ordersEntities) {
                List<OrderItemEntity> orderItemEntities = orderService.getOrderItemsByOrder(ordersEntity);
                List<ItemQuantityResponse> itemQuantityResponseList = new LinkedList<>();
                orderItemEntities.forEach(orderItemEntity -> {
                    ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem()
                            .itemName(orderItemEntity.getItem().getitemName())
                            .itemPrice(orderItemEntity.getItem().getPrice())
                            .id(UUID.fromString(orderItemEntity.getItem().getUuid()))
                            .type(ItemQuantityResponseItem.TypeEnum.valueOf(orderItemEntity.getItem().getType().getValue()));
                    ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse()
                            .item(itemQuantityResponseItem)
                            .quantity(orderItemEntity.getQuantity())
                            .price(orderItemEntity.getPrice());
                    itemQuantityResponseList.add(itemQuantityResponse);
                });
                OrderListAddressState orderListAddressState = new OrderListAddressState()
                        .id(UUID.fromString(ordersEntity.getAddress().getState().getStateUuid()))
                        .stateName(ordersEntity.getAddress().getState().getStateName());
                OrderListAddress orderListAddress = new OrderListAddress()
                        .id(UUID.fromString(ordersEntity.getAddress().getUuid()))
                        .flatBuildingName(ordersEntity.getAddress().getFlatBuilNo())
                        .locality(ordersEntity.getAddress().getLocality())
                        .city(ordersEntity.getAddress().getCity())
                        .pincode(ordersEntity.getAddress().getPincode())
                        .state(orderListAddressState);
                OrderListCoupon orderListCoupon = new OrderListCoupon()
                        .couponName(ordersEntity.getCoupon().getCouponName())
                        .id(UUID.fromString(ordersEntity.getCoupon().getUuid()))
                        .percent(ordersEntity.getCoupon().getPercent());
                OrderListCustomer orderListCustomer = new OrderListCustomer()
                        .id(UUID.fromString(ordersEntity.getCustomer().getUuid()))
                        .firstName(ordersEntity.getCustomer().getFirstName())
                        .lastName(ordersEntity.getCustomer().getLastName())
                        .emailAddress(ordersEntity.getCustomer().getEmail())
                        .contactNumber(ordersEntity.getCustomer().getContactNumber());
                OrderListPayment orderListPayment = new OrderListPayment()
                        .id(UUID.fromString(ordersEntity.getPayment().getUuid()))
                        .paymentName(ordersEntity.getPayment().getPaymentName());
                OrderList orderList = new OrderList()
                        .id(UUID.fromString(ordersEntity.getUuid()))
                        .itemQuantities(itemQuantityResponseList)
                        .address(orderListAddress)
                        .bill(BigDecimal.valueOf(ordersEntity.getBill()))
                        .date(String.valueOf(ordersEntity.getDate()))
                        .discount(BigDecimal.valueOf(ordersEntity.getDiscount()))
                        .coupon(orderListCoupon)
                        .customer(orderListCustomer)
                        .payment(orderListPayment);
                orderLists.add(orderList);
            }
            CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse()
                    .orders(orderLists);
            return new ResponseEntity<CustomerOrderResponse>(customerOrderResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<CustomerOrderResponse>(new CustomerOrderResponse(), HttpStatus.OK);
        }
    }
}
