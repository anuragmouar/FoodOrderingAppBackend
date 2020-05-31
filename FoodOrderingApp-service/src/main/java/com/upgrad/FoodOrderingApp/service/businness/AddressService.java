package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//This Class handles all service related to the address

@Service
public class AddressService {
    @Autowired
    AddressDao addressDao; //Handles all data related to the addressEntity

    /* This method is to saveAddress.Takes the Address and state entity and saves the Address to the DB.
    If error throws exception with error code and error message.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity addressEntity,StateEntity stateEntity)throws SaveAddressException{

        //Checking if any field is empty in the address entity.
        if (addressEntity.getCity() == null || addressEntity.getFlatBuilNo() == null || addressEntity.getPincode() == null || addressEntity.getLocality() == null){
            throw new SaveAddressException("SAR-001","No field can be empty");
        }

        //Setting state to the address
        addressEntity.setState(stateEntity);

        //Passing the addressEntity to addressDao saveAddress method which returns saved address.
        AddressEntity savedAddress = addressDao.saveAddress(addressEntity);

        //returning SavedAddress
        return savedAddress;

    }
}
