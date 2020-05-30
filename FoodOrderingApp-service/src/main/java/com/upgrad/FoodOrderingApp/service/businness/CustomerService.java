package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.Utility;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDAO;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDAO;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * This class provides service to customer endpoints.
 */
@Service
public class CustomerService {

    @Autowired
    private CustomerDAO customerDAO;
    @Autowired
    private CustomerAuthDAO customerAuthDAO;
    @Autowired
    private Utility utility;
    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * This service method provides service to customer signup endpoint.
     *
     * @param customerEntity
     * @return new customer entity
     * @throws SignUpRestrictedException
     */
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        validateCustomerEntity(customerEntity);

        String[] encryptedPassword = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedPassword[0]);
        customerEntity.setPassword(encryptedPassword[1]);
        CustomerEntity newCustomerEntity = customerDAO.createCustomer(customerEntity);

        return newCustomerEntity;
    }

    private void validateCustomerEntity(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if(customerEntity.getContactNumber() != null || customerEntity.getContactNumber() != "") {
            CustomerEntity existingCustomerEntity = customerDAO.getCustomerByContactNumber(customerEntity.getContactNumber());
            if (existingCustomerEntity != null) {
                throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number");
            }
        }
        if (!utility.isValidSignupCustomerRequest(customerEntity)) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
        if (!utility.isValidEmailIDFormat(customerEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }
        if (!utility.isValidContactNumber(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }
        if (!utility.isValidPassword(customerEntity.getPassword())) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }
    }

    /**
     *  This service method provides service to customer login endpoint.
     *
     * @param decodedArray
     * @return authorized customer entity
     * @throws AuthenticationFailedException
     */
    public CustomerAuthEntity authenticate(String[] decodedArray) throws AuthenticationFailedException {
        if(utility.isValidAuthorizationFormat(decodedArray)) {
            String contactNumber = decodedArray[0];
            String password = decodedArray[1];

            CustomerEntity customerEntity = customerDAO.getCustomerByContactNumber(contactNumber);
            if (customerEntity == null) {
                throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
            }

            String encryptedPassword = passwordCryptographyProvider.encrypt(password, customerEntity.getSalt());
            if (encryptedPassword.equals(customerEntity.getPassword())) {
                JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
                CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
                customerAuthEntity.setCustomer(customerEntity);

                final ZonedDateTime now = ZonedDateTime.now();
                final ZonedDateTime expiresAt = now.plusHours(8);

                customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
                customerAuthEntity.setLoginAt(now);
                customerAuthEntity.setExpiresAt(expiresAt);
                customerAuthEntity.setUuid(UUID.randomUUID().toString());

                CustomerAuthEntity newCustomerAuthEntity = customerAuthDAO.createCustomerAuth(customerAuthEntity);
                return newCustomerAuthEntity;
            } else {
                throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
            }
        } else {
            throw new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        }
    }

    /**
     * This service method provides service to customer logout endpoint.
     *
     * @param accessToken
     * @return logged out customerAuth entity
     * @throws AuthorizationFailedException
     */
    public CustomerAuthEntity logout(String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDAO.getCustomerAuthByAccessToken(accessToken);
        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();
        if (customerAuthEntity.getExpiresAt().compareTo(now) < 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        customerAuthEntity.setLogoutAt(ZonedDateTime.now());
        CustomerAuthEntity updatedCustomerAuthEntity = customerAuthDAO.customerLogout(customerAuthEntity);
        return updatedCustomerAuthEntity;
    }
}