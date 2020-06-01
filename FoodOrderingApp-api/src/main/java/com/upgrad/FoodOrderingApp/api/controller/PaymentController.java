package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @RequestMapping(method = RequestMethod.GET, path = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> getAllPaymentMethods() {

        List<PaymentEntity> paymentEntities = paymentService.getAllPaymentMethods();

        List<PaymentResponse> paymentResponses = new LinkedList<>();
        paymentEntities.forEach(paymentEntity -> {
            PaymentResponse paymentResponse = new PaymentResponse()
                .paymentName(paymentEntity.getPaymentName())
                .id(UUID.fromString(paymentEntity.getUuid()));
            paymentResponses.add(paymentResponse);
        });

        PaymentListResponse paymentListResponse = new PaymentListResponse()
            .paymentMethods(paymentResponses);
        return new ResponseEntity<PaymentListResponse>(paymentListResponse, HttpStatus.OK);

    }
}