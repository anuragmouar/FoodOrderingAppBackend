package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

/**
 *  This class will handle all API#001 Customers endpoint.
 *
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * This is customer signup endpoint.
     *
     * @param signupCustomerRequest
     * @return responseEntity with Customer uuid and status
     * @throws SignUpRestrictedException
     */
    @RequestMapping(method = RequestMethod.POST,path = "/signup",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signUpCustomer(@RequestBody final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setUuid(UUID.randomUUID().toString());

        CustomerEntity signedupCustomer = customerService.saveCustomer(customerEntity);
        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse().id(signedupCustomer.getUuid())
                                                                                        .status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse, HttpStatus.CREATED);
    }

    /**
     *  This is customer login endpoint.
     *
     * @param authorization
     * @return response entity with customer details and access-token.
     * @throws AuthenticationFailedException
     */
    @RequestMapping(method = RequestMethod.POST,path = "/login",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> loginCustomer(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decoded = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedAuth = new String(decoded);
        String[] decodedArray = decodedAuth.split(":");

        CustomerAuthEntity customerAuthEntity = customerService.authenticate(decodedArray);

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", customerAuthEntity.getAccessToken());

        LoginResponse loginResponse = new LoginResponse().id(customerAuthEntity.getCustomer().getUuid())
                                                            .contactNumber(customerAuthEntity.getCustomer().getContactNumber())
                                                            .emailAddress(customerAuthEntity.getCustomer().getEmail())
                                                            .firstName(customerAuthEntity.getCustomer().getFirstName())
                                                            .lastName(customerAuthEntity.getCustomer().getLastName())
                                                            .message("LOGGED IN SUCCESSFULLY");
        return new ResponseEntity<LoginResponse>(loginResponse,headers,HttpStatus.OK);
    }

    /**
     * This is customer logout endpoint.
     *
     * @param authorization
     * @return response entity with Customer uuid and status
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.POST,path = "/logout",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logoutCustomer(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        String accessToken = authorization.split("Bearer ")[1];
        CustomerAuthEntity customerAuthEntity =  customerService.logout(accessToken);
        LogoutResponse logoutResponse = new LogoutResponse().id(customerAuthEntity.getCustomer().getUuid())
                                                               .message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(logoutResponse,HttpStatus.OK);
    }
}