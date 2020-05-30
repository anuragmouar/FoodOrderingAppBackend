package com.upgrad.FoodOrderingApp.service.common;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the common utility class.
 */
@Component
public class Utility {

    public boolean isValidSignupCustomerRequest(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if (customerEntity.getFirstName() == null || customerEntity.getFirstName() == ""
            || customerEntity.getPassword() == null||customerEntity.getPassword() == ""
            || customerEntity.getEmail() == null||customerEntity.getEmail() == ""
            || customerEntity.getContactNumber() == null||customerEntity.getContactNumber() == "") {
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
        if(password.length() > 8 && password.matches("(?=.*[a-z]).*")
                && password.matches("(?=.*[A-Z]).*")&& password.matches("(?=.*[0-9]).*")
                && Pattern.compile("[^A-Za-z0-9 ]").matcher(password).find()) {
            return true;
        }
        return false;
    }

    public boolean isValidAuthorizationFormat(String[] decodedArray) throws AuthenticationFailedException {
        if(decodedArray.length == 2) {
            return true;
        } else {
            return false;
        }
    }
}
