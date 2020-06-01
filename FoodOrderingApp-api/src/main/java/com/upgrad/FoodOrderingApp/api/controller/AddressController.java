package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/")
public class AddressController {

    @Autowired
    AddressService addressService;

    @Autowired
    CustomerService customerService;

    /**
     * Method to save address provided.
     *
     * @param authorization
     * @param saveAddressRequest
     * @return uuid of the address saved and message “ADDRESS SUCCESSFULLY REGISTERED”
     * @throws AuthorizationFailedException if the access token provided by the customer does not exist in the database
     *                                      or if the access token provided by the customer exists in the database, but the customer has already logged out or session has expired
     * @throws AddressNotFoundException     if the state uuid entered does not exist in the database
     * @throws SaveAddressException         if the pincode entered is invalid
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization") final String authorization, @RequestBody(required = false) SaveAddressRequest saveAddressRequest) throws AuthorizationFailedException, AddressNotFoundException, SaveAddressException {

        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        AddressEntity addressEntity = new AddressEntity();

        addressEntity.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setUuid(UUID.randomUUID().toString());

        StateEntity stateEntity = addressService.getStateByUUID(saveAddressRequest.getStateUuid());

        AddressEntity savedAddress = addressService.saveAddress(addressEntity, stateEntity);

        CustomerAddressEntity customerAddressEntity = addressService.saveCustomerAddressEntity(customerEntity, savedAddress);

        SaveAddressResponse saveAddressResponse = new SaveAddressResponse()
            .id(savedAddress.getUuid())
            .status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
    }

    /**
     * Retrive the list of saved address in descending order of their saved time
     *
     * @param authorization
     * @return address list
     * @throws AuthorizationFailedException if the access token provided by the customer does not exist in the database
     *                                      or if the access token provided by the customer exists in the database, but the customer has already logged out
     *                                      or session has expired
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/address/customer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllSavedAddress(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        List<AddressEntity> addressEntities = addressService.getAllAddress(customerEntity);
        Collections.reverse(addressEntities);

        List<AddressList> addressLists = new LinkedList<>();
        addressEntities.forEach(addressEntity -> {
            AddressListState addressListState = new AddressListState()
                .stateName(addressEntity.getState().getStateName())
                .id(UUID.fromString(addressEntity.getState().getStateUuid()));
            AddressList addressList = new AddressList()
                .id(UUID.fromString(addressEntity.getUuid()))
                .city(addressEntity.getCity())
                .flatBuildingName(addressEntity.getFlatBuilNo())
                .locality(addressEntity.getLocality())
                .pincode(addressEntity.getPincode())
                .state(addressListState);
            addressLists.add(addressList);
        });

        AddressListResponse addressListResponse = new AddressListResponse().addresses(addressLists);
        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);
    }

    /**
     * Delete address of given address id
     *
     * @param authorization
     * @param addressUuid
     * @return uuid of the address deleted and message “ADDRESS DELETED SUCCESSFULLY”
     * @throws AuthorizationFailedException if the access token provided by the customer does not exist in the database
     *                                      or if the access token provided by the customer exists in the database, but the customer has already logged out
     *                                      or session has expired
     * @throws AddressNotFoundException     if address id field is empty or if address id entered is incorrect
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteSavedAddress(@RequestHeader("authorization") final String authorization, @PathVariable(value = "address_id") final String addressUuid) throws AuthorizationFailedException, AddressNotFoundException {

        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        AddressEntity addressEntity = addressService.getAddressByUUID(addressUuid, customerEntity);

        AddressEntity deletedAddressEntity = addressService.deleteAddress(addressEntity);

        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse()
            .id(UUID.fromString(deletedAddressEntity.getUuid()))
            .status("ADDRESS DELETED SUCCESSFULLY");

        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);

    }

    /**
     * Retrieve all the states present in the database
     *
     * @return list of states
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/states", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getAllStates() {

        List<StateEntity> stateEntities = addressService.getAllStates();

        if (!stateEntities.isEmpty()) {
            List<StatesList> statesLists = new LinkedList<>();
            stateEntities.forEach(stateEntity -> {
                StatesList statesList = new StatesList()
                    .id(UUID.fromString(stateEntity.getStateUuid()))
                    .stateName(stateEntity.getStateName());
                statesLists.add(statesList);
            });

            StatesListResponse statesListResponse = new StatesListResponse().states(statesLists);
            return new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);
        } else
            return new ResponseEntity<StatesListResponse>(new StatesListResponse(), HttpStatus.OK);
    }

}
