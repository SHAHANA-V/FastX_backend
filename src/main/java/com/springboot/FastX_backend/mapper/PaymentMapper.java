package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.request.PaymentDto;
import com.springboot.FastX_backend.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public static Payment dtoToModel(PaymentDto dto) {
        Payment payment = new Payment();
        payment.setPaymentMethod(dto.paymentMethod());
        return payment;
    }
}
