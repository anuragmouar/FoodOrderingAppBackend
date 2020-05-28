package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.Utility;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDAO;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides service to customer endpoints.
 */
@Service
public class CustomerService {

    @Autowired
    private CustomerDAO customerDAO;
    @Autowired
    private Utility utility;

    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if(customerEntity.getContactNumber() != null || customerEntity.getContactNumber() != "") {
            CustomerEntity existingCustomerEntity = customerDAO.getCustomerByContactNumber(customerEntity.getContactNumber());
            if (existingCustomerEntity != null) {
                throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number");
            }
        }
        if (!utility.isValidSignupCustomerRequest(customerEntity)) {//Checking if is Valid Signup Request.
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
        if (!utility.isValidEmailIDFormat(customerEntity.getEmail())) {//Checking if email is valid
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }
        if (!utility.isValidContactNumber(customerEntity.getContactNumber())) {//Checking if Contact is valid
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }
        if (!utility.isValidPassword(customerEntity.getPassword())) {//Checking if Password is valid.
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }
        return null;
    }

}
