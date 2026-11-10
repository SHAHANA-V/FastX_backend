package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.request.BookingSeatDto;
import com.springboot.FastX_backend.model.BookingSeat;
import org.springframework.stereotype.Component;

@Component
public class BookingSeatMapper {
    public static BookingSeat convertDtoToModel(BookingSeatDto bookingSeatDto) {
        BookingSeat bookingSeat = new BookingSeat();

        bookingSeat.setPassengerName(bookingSeatDto.passengerName());
        bookingSeat.setAge(bookingSeatDto.age());
        bookingSeat.setGender(bookingSeatDto.gender());
        bookingSeat.setIdType(bookingSeatDto.idType());
        bookingSeat.setIdDocument(bookingSeatDto.idDocument());

        return bookingSeat;
    }
}
