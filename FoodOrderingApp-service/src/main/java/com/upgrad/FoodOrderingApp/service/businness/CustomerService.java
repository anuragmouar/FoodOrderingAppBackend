package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.Utility;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDAO;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDAO;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        CustomerEntity existingCustomerEntity = customerDAO.getCustomerByContactNumber(customerEntity.getContactNumber());

        if (existingCustomerEntity != null) {//Checking if Customer already Exists if yes throws exception.
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number");
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
        String[] encryptedPassword = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedPassword[0]);
        customerEntity.setPassword(encryptedPassword[1]);
        CustomerEntity newCustomerEntity = customerDAO.createCustomer(customerEntity);

        return newCustomerEntity;
    }

    /**
     * This service method provides service to customer login endpoint.
     *
     * @param contactNumber,password
     * @return authorized customer entity
     * @throws AuthenticationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(String contactNumber, String password) throws AuthenticationFailedException {

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

    }

    /**
     * This service method provides service to customer logout endpoint.
     *
     * @param accessToken
     * @return logged out customerAuth entity
     * @throws AuthorizationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
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

    /**
     * This service method provides service to update customer endpoints.
     *
     * @param accessToken
     * @return customer entity
     * @throws AuthorizationFailedException
     */
    public CustomerEntity getCustomer(String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = null;
        customerAuthEntity = customerAuthDAO.getCustomerAuthByAccessToken(accessToken);
        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();
        if (customerAuthEntity.getExpiresAt().compareTo(now) <= 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        return customerAuthEntity.getCustomer();
    }

    /**
     * This service method provides service for update customer endpoint.
     *
     * @param customerEntity
     * @return updated customer entity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(CustomerEntity customerEntity) {
        CustomerEntity existingCustomer = customerDAO.getCustomerByUuid(customerEntity.getUuid());
        existingCustomer.setFirstName(customerEntity.getFirstName());
        existingCustomer.setLastName(customerEntity.getLastName());
        CustomerEntity updatedCustomer = customerDAO.updateCustomer(customerEntity);
        return updatedCustomer;
    }

    /**
     * This service method provides service for update customer password endpoint.
     *
     * @param oldPassword
     * @param newPassword
     * @param customerEntity
     * @return customerEntity
     * @throws UpdateCustomerException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(String oldPassword, String newPassword, CustomerEntity customerEntity) throws UpdateCustomerException {
        if (utility.isValidPassword(newPassword)) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }
        String encryptedOldPassword = passwordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());
        if (encryptedOldPassword.equals(customerEntity.getPassword())) {
            CustomerEntity existingCustomerEntity = customerDAO.getCustomerByUuid(customerEntity.getUuid());
            String[] encryptedPassword = passwordCryptographyProvider.encrypt(newPassword);
            existingCustomerEntity.setSalt(encryptedPassword[0]);
            existingCustomerEntity.setPassword(encryptedPassword[1]);
            CustomerEntity updatedCustomerEntity = customerDAO.updateCustomer(existingCustomerEntity);
            return updatedCustomerEntity;
        } else {
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }
    }
}