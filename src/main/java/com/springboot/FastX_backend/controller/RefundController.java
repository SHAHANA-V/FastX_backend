
package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.model.Refund;
import com.springboot.FastX_backend.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class RefundController {

    private final RefundService refundService;

    @GetMapping("/api/refund/{refundId}")
    public Refund getRefund(@PathVariable long refundId, Principal principal) {
        return refundService.getRefund(refundId, principal.getName());
    }

    @PutMapping("/api/process/refund/{refundId}")
    public void processRefund(@PathVariable long refundId, Principal principal) {
        refundService.processRefund(refundId, principal.getName());
    }
}
