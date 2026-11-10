package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.request.PaymentDto;
import com.springboot.FastX_backend.dto.response.PaymentRespDto;
import com.springboot.FastX_backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/api/initiate/payment/{id}")
    public void initiatePayment(@PathVariable long id,
                                @Valid @RequestBody PaymentDto paymentDto,
                                Principal principal){
        paymentService.initiatePayment(id,paymentDto,principal.getName());
    }

    @GetMapping("/api/get/payment/{id}")
    public PaymentRespDto getPayment(@PathVariable long id, Principal principal){
        return paymentService.getPayment(id,principal.getName());
    }
}
