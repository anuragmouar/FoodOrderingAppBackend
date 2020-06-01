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
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUuid(UUID.randomUUID().toString());
        orderEntity.setBill(saveOrderRequest.getBill().floatValue());
        orderEntity.setDate(timestamp);
        orderEntity.setCustomer(customerEntity);
        orderEntity.setDiscount(saveOrderRequest.getDiscount().doubleValue());
        orderEntity.setPayment(paymentEntity);
        orderEntity.setAddress(addressEntity);
        orderEntity.setRestaurant(restaurantEntity);
        orderEntity.setCoupon(couponEntity);
        OrderEntity savedOrderEntity = orderService.saveOrder(orderEntity);
        List<ItemQuantity> itemQuantities = saveOrderRequest.getItemQuantities();
        for (ItemQuantity itemQuantity : itemQuantities) {
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            ItemEntity itemEntity = itemService.getItemByUUID(itemQuantity.getItemId().toString());
            orderItemEntity.setItem(itemEntity);
            orderItemEntity.setOrder(orderEntity);
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
        List<OrderEntity> ordersEntities = orderService.getOrdersByCustomers(customerEntity.getUuid());
        List<OrderList> orderLists = new LinkedList<OrderList>();

        if (ordersEntities != null) {
            for (OrderEntity orderEntity : ordersEntities) {
                List<OrderItemEntity> orderItemEntities = orderService.getOrderItemsByOrder(orderEntity);
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
                        .id(UUID.fromString(orderEntity.getAddress().getState().getStateUuid()))
                        .stateName(orderEntity.getAddress().getState().getStateName());
                OrderListAddress orderListAddress = new OrderListAddress()
                        .id(UUID.fromString(orderEntity.getAddress().getUuid()))
                        .flatBuildingName(orderEntity.getAddress().getFlatBuilNo())
                        .locality(orderEntity.getAddress().getLocality())
                        .city(orderEntity.getAddress().getCity())
                        .pincode(orderEntity.getAddress().getPincode())
                        .state(orderListAddressState);
                OrderListCoupon orderListCoupon = new OrderListCoupon()
                        .couponName(orderEntity.getCoupon().getCouponName())
                        .id(UUID.fromString(orderEntity.getCoupon().getUuid()))
                        .percent(orderEntity.getCoupon().getPercent());
                OrderListCustomer orderListCustomer = new OrderListCustomer()
                        .id(UUID.fromString(orderEntity.getCustomer().getUuid()))
                        .firstName(orderEntity.getCustomer().getFirstName())
                        .lastName(orderEntity.getCustomer().getLastName())
                        .emailAddress(orderEntity.getCustomer().getEmail())
                        .contactNumber(orderEntity.getCustomer().getContactNumber());
                OrderListPayment orderListPayment = new OrderListPayment()
                        .id(UUID.fromString(orderEntity.getPayment().getUuid()))
                        .paymentName(orderEntity.getPayment().getPaymentName());
                OrderList orderList = new OrderList()
                        .id(UUID.fromString(orderEntity.getUuid()))
                        .itemQuantities(itemQuantityResponseList)
                        .address(orderListAddress)
                        .bill(BigDecimal.valueOf(orderEntity.getBill()))
                        .date(String.valueOf(orderEntity.getDate()))
                        .discount(BigDecimal.valueOf(orderEntity.getDiscount()))
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
