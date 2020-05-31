package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

//This Class represents the RestaurantCategory table in the DB

@Entity
@Table(name = "restaurant_category")
@NamedQueries({

        @NamedQuery(name = "getCategoriesByRestaurant",query = "SELECT r FROM RestaurantCategoryEntity r WHERE r.restaurant= :restaurant ORDER BY r.category.categoryName ASC "),
        @NamedQuery(name = "getRestaurantByCategory",query = "SELECT r FROM RestaurantCategoryEntity r WHERE r.category = :category ORDER BY r.restaurant.customerRating DESC "),
})
public class RestaurantCategoryEntity implements Serializable {


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private RestaurantEntity restaurant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private CategoryEntity category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}
