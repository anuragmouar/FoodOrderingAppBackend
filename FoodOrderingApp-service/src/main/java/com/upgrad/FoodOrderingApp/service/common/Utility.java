package com.upgrad.FoodOrderingApp.service.common;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the common utility class.
 */
@Component
public class Utility {

    /**
     * Validates signup request for customer
     * @param customerEntity
     * @return true if validation passes else false
     * @throws SignUpRestrictedException if firstname/password/email/contact number is null or blank
     */
    public boolean isValidSignupCustomerRequest(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if (customerEntity.getFirstName() == null || customerEntity.getFirstName() == ""){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        if(customerEntity.getPassword() == null||customerEntity.getPassword() == ""){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        if (customerEntity.getEmail() == null||customerEntity.getEmail() == ""){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        if (customerEntity.getContactNumber() == null||customerEntity.getContactNumber() == ""){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        return true;
    }

    /**
     * Validates email id
     * @param email
     * @return true if validation passes else false
     */
    public boolean isValidEmailIDFormat(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    /**
     * Validates contact number
     * @param contactNumber
     * @return true if validation passes else false
     */
    public boolean isValidContactNumber(String contactNumber) {
        Pattern p = Pattern.compile("^\\d{10}$");
        Matcher m = p.matcher(contactNumber);
        return (m.find() && m.group().equals(contactNumber));
    }

    /**
     * Validates password provided
     * @param password
     * @return true if validation passes else false
     */
    public boolean isValidPassword(String password) {
        if (password.length() > 8 && password.matches("(?=.*[a-z]).*")
                && password.matches("(?=.*[A-Z]).*") && password.matches("(?=.*[0-9]).*")
                && Pattern.compile("[^A-Za-z0-9 ]").matcher(password).find()) {
            return true;
        }
        return false;
    }

    public boolean isValidAuthorizationFormat(String authorization) throws AuthenticationFailedException {
        try {
            byte[] decoded = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedAuth = new String(decoded);
            String[] decodedArray = decodedAuth.split(":");
            String username = decodedArray[0];
            String password = decodedArray[1];
            return true;
        }catch (ArrayIndexOutOfBoundsException exc){
            throw new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        }
    }

    /**
     * Validates customer details provided
     * @param firstName
     * @return true if validation passes else false
     * @throws UpdateCustomerException
     */
    public boolean isValidUpdateCustomerDetails(String firstName) throws UpdateCustomerException {
        if (firstName == null || firstName == "") {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        return true;
    }

    /**
     * Validates password entered by customer
     * @param oldPassword
     * @param newPassword
     * @return true if validation passes else false
     * @throws UpdateCustomerException if old/new password is null or blank
     */
    public boolean isValidCustomerPassword(String oldPassword, String newPassword) throws UpdateCustomerException {
        if (oldPassword == null || oldPassword == "") {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        if (newPassword == null || newPassword == "") {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        return true;
    }

    public Map<String, Integer> sortMapByValues(Map<String, Integer> itemCountMap) {
        List<Map.Entry<String,Integer>> list = new LinkedList<Map.Entry<String, Integer>>(itemCountMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue().compareTo(o1.getValue()));
            }
        });
        Map<String, Integer> sortedByValueMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> item : list) {
            sortedByValueMap.put(item.getKey(), item.getValue());
        }
        return sortedByValueMap;
    }

    /**
     * Method to validate if customer rating
     * @param cutomerRating
     * @return true if validation passes else false
     */
    public boolean isValidCustomerRating(String cutomerRating){
        if(cutomerRating.equals("5.0")){
            return true;
        }
        Pattern p = Pattern.compile("[1-4].[0-9]");
        Matcher m = p.matcher(cutomerRating);
        return (m.find() && m.group().equals(cutomerRating));
    }

    /**
     * Method to validate pincode
     * @param pincode
     * @return true if validation passes else false
     */
    public boolean isPincodeValid(String pincode){
        Pattern p = Pattern.compile("\\d{6}\\b");
        Matcher m = p.matcher(pincode);
        return (m.find() && m.group().equals(pincode));
    }
}
