package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * This class facilitates DB interaction for Customer Entity.
 */
@Repository
public class CustomerDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     *  This DAO method gets customer entity by contact number if any.
     *
     * @param contactNumber
     * @return existing customer entity if any
     */
    public CustomerEntity getCustomerByContactNumber(String contactNumber) {
        try {
            CustomerEntity customerEntity = entityManager.createNamedQuery("customerByContactNumber", CustomerEntity.class).setParameter("contact_number", contactNumber).getSingleResult();
            return customerEntity;
        }catch(NoResultException ex) {
            return null;
        }
    }

    /**
     *  This DAO method creates new customer entity.
     *
     * @param customerEntity
     * @return new customer entity
     */
    public CustomerEntity createCustomer(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }
}
