package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        public ResponseEntity<SignupCustomerResponse> signUpCustomer(@RequestBody SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
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
}
