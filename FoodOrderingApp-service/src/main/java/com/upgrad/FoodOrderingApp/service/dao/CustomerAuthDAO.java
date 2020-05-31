package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * This class facilitates DB interaction to CustomerAuth entity.
 */
@Repository
public class CustomerAuthDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This DAO method creates new customerAuth entity.
     *
     * @param customerAuthEntity
     * @return customerAuthEntity
     */
    public CustomerAuthEntity createCustomerAuth(CustomerAuthEntity customerAuthEntity) {
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }

    /**
     * This DAO method gets CustomerAuthEntity based on access-token.
     *
     * @param accessToken
     * @return customerAuthEntity
     */
    public CustomerAuthEntity getCustomerAuthByAccessToken(String accessToken) {
        try {
            CustomerAuthEntity customerAuthEntity = entityManager.createNamedQuery("getCustomerAuthByAccessToken", CustomerAuthEntity.class).setParameter("access_Token", accessToken).getSingleResult();
            return customerAuthEntity;
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * This DAO method updates existing customerAuth entity.
     *
     * @param customerAuthEntity
     * @return customerAuthEntity
     */
    public CustomerAuthEntity customerLogout(CustomerAuthEntity customerAuthEntity) {
        entityManager.merge(customerAuthEntity);
        return customerAuthEntity;
    }
}
