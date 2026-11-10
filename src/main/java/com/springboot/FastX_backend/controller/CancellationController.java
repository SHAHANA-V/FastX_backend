package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.request.CancellationDto;
import com.springboot.FastX_backend.dto.response.CancellationRespDto;
import com.springboot.FastX_backend.enums.CancellationStatus;
import com.springboot.FastX_backend.service.CancellationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class CancellationController {
    private final CancellationService cancellationService;

    @PostMapping("/api/request/cancellation/{id}")
    public void requestCancellation(@PathVariable long id, @Valid @RequestBody CancellationDto cancellationDto, Principal principal){
        cancellationService.requestCancellation(id,principal.getName(),cancellationDto);
    }

    @PutMapping("/update/{cancellationId}")
    public void updateCancellation(@PathVariable long cancellationId,
                                   @RequestParam CancellationStatus status,
                                   Principal principal){
        cancellationService.updateCancellation(cancellationId,status,principal.getName());
    }

    @GetMapping("/api/operator/cancellations")
    public List<CancellationRespDto> getOperatorCancellations(Principal principal) {
        return cancellationService.getOperatorCancellations(principal.getName());
    }
}
