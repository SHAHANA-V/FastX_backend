package com.springboot.FastX_backend.config;

import com.springboot.FastX_backend.dto.response.ErrorDto;
import com.springboot.FastX_backend.exception.AmenityAlreadyExistException;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.module.ResolutionException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFoundException(ResourceNotFoundException e){
        return ResponseEntity.badRequest()
                .body(new ErrorDto(e.getMessage(),"Id is not available in db", Instant.now()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String ,String >> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        // to avoid big message when we use get errordto ,we use map to simplify message
        Map<String ,String> map = new HashMap<>();

        //it will get all the result like errors(it may have many error in same fields)
        BindingResult bindingResult = e.getBindingResult();

        //get every field error
        List<FieldError> list = bindingResult.getFieldErrors();

        //put them in map and get the field and its default error message
        list.forEach(fieldError->map.put(fieldError.getField(),fieldError.getDefaultMessage()));
        return ResponseEntity.badRequest().body(map);

    }

    @ExceptionHandler(OwnerShipException.class)
    public ResponseEntity<ErrorDto> handleOwnerShipException(OwnerShipException e){
        return ResponseEntity.badRequest().body(new ErrorDto(
                e.getMessage(),
                "You do not have permission to modify this bus",
                Instant.now()
        ));
    }

    @ExceptionHandler(AmenityAlreadyExistException.class)
    public ResponseEntity<ErrorDto> handleAmenityAlreadyExistException(AmenityAlreadyExistException e){
        return ResponseEntity.badRequest().body(new ErrorDto(
                e.getMessage(),
                "Already exists",
                Instant.now()
        ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleRuntimeException(RuntimeException e){
        return ResponseEntity.badRequest().body(new ErrorDto(
                e.getMessage(),
                "RunTime Exception occurred",
                Instant.now()
        ));
    }


}
