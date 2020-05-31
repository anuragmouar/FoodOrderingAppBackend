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

    public boolean isValidSignupCustomerRequest(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if (customerEntity.getFirstName() == null || customerEntity.getFirstName() == ""
                || customerEntity.getPassword() == null || customerEntity.getPassword() == ""
                || customerEntity.getEmail() == null || customerEntity.getEmail() == ""
                || customerEntity.getContactNumber() == null || customerEntity.getContactNumber() == "") {
            return false;
        }
        return true;
    }

    public boolean isValidEmailIDFormat(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public boolean isValidContactNumber(String contactNumber) {
        Pattern p = Pattern.compile("^\\d{10}$");
        Matcher m = p.matcher(contactNumber);
        return (m.find() && m.group().equals(contactNumber));
    }

    public boolean isValidPassword(String password) {
        if (password.length() > 8 && password.matches("(?=.*[a-z]).*")
                && password.matches("(?=.*[A-Z]).*") && password.matches("(?=.*[0-9]).*")
                && Pattern.compile("[^A-Za-z0-9 ]").matcher(password).find()) {
            return true;
        }
        return false;
    }

    public boolean isValidAuthorizationFormat(String[] decodedArray) throws AuthenticationFailedException {
        if (decodedArray.length == 2) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isValidUpdateCustomerDetails(String firstName) throws UpdateCustomerException {
        if (firstName == null || firstName == "") {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        return true;
    }

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

    public boolean isValidCustomerRating(String cutomerRating){
        if(cutomerRating.equals("5.0")){
            return true;
        }
        Pattern p = Pattern.compile("[1-4].[0-9]");
        Matcher m = p.matcher(cutomerRating);
        return (m.find() && m.group().equals(cutomerRating));
    }
}
