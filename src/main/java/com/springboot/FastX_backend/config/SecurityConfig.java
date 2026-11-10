package com.springboot.FastX_backend.config;

import com.springboot.FastX_backend.service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final MyUserDetailsService myUserDetailsService;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize)->authorize
                        //passenger
                        .requestMatchers(HttpMethod.POST,"/api/passenger/add").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/passenger/update").hasAuthority("PASSENGER")
                        .requestMatchers(HttpMethod.GET,"/api/passenger/all").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/passenger/profile").hasAuthority("PASSENGER")
                        .requestMatchers(HttpMethod.GET,"/api/passenger/notifications").hasAuthority("PASSENGER")
                        .requestMatchers(HttpMethod.PUT,"/api/passenger/notification/read/{notificationId}").hasAuthority("PASSENGER")
                        .requestMatchers(HttpMethod.GET,"/api/passenger/{passengerId}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/passenger/delete/{id}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/passenger/change-password").hasAuthority("PASSENGER")

                        //login
                        .requestMatchers(HttpMethod.GET,"/api/auth/login").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/auth/user-details").authenticated()

                        //admin
                        //add admin
                        .requestMatchers(HttpMethod.POST,"/api/admin/add").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/admin/dashboard").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/operators").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/routes").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/bookings").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/monthly-bookings").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/booking-status").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/process/refund/{refundId}").hasAuthority("BUS_OPERATOR")
                        //add bus operator by admin
                        .requestMatchers(HttpMethod.POST,"/api/busoperator/add").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/busOperator/profile").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/busOperator/change-password").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET, "/api/admin/dashboard").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/operators").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/busoperator/add").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/busOperator/delete/{id}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/operators/{operatorId}/status").hasAuthority("ADMIN")

                        //bus
                        .requestMatchers(HttpMethod.POST,"/api/add/bus").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET,"/api/bus/dynamicFilter").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.PUT,"/api/bus/update/{id}").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET,"/api/bus/all").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/operator/bus/count").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET, "/api/operator/cancellations").hasAuthority("BUS_OPERATOR")

                        //amenity
                        .requestMatchers(HttpMethod.POST,"/api/add/amenity").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET,"/api/amenity/all").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET,"/api/amenity/{id}").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.PUT,"/api/amenity/update/description/{id}").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.DELETE,"/api/amenity/delete/{id}").hasAuthority("BUS_OPERATOR")

                        //Bus Amenity
                        .requestMatchers(HttpMethod.POST,"/api/add/bus/amenity/{busId}/{amenityId}").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET,"/api/bus/amenity/all").hasAnyAuthority("BUS_OPERATOR","ADMIN","PASSENGER")
                        .requestMatchers(HttpMethod.GET,"/api/bus/amenity/{busId}").hasAnyAuthority("BUS_OPERATOR","ADMIN","PASSENGER")
                        .requestMatchers(HttpMethod.DELETE,"/api/delete/amenity/bus/{busId}/{amenityId}").hasAnyAuthority("BUS_OPERATOR")

                        //route
                        .requestMatchers(HttpMethod.POST,"/api/add/route").hasAnyAuthority("BUS_OPERATOR","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/get/routes").hasAnyAuthority("BUS_OPERATOR","ADMIN","PASSENGER")
                        .requestMatchers(HttpMethod.GET,"/api/get/active/route").hasAnyAuthority("BUS_OPERATOR","ADMIN","PASSENGER")
                        .requestMatchers(HttpMethod.PUT,"/api/update/status/{id}").hasAnyAuthority("BUS_OPERATOR","ADMIN")

                        //routeStop
                        .requestMatchers(HttpMethod.POST,"/api/add/routestop/{routeId}").hasAnyAuthority("BUS_OPERATOR","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/get/routestop/{routeId}").hasAnyAuthority("BUS_OPERATOR","ADMIN","PASSENGER")
                        .requestMatchers(HttpMethod.GET,"/api/get/routestop/stoporder/{routeId}/{stopOrder}").hasAnyAuthority("BUS_OPERATOR","ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/update/routeStop/{routeId}/{routeStopId}").hasAnyAuthority("BUS_OPERATOR","ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/add/routestop/bulk/{routeId}").hasAnyAuthority("BUS_OPERATOR","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/get/routestop/{routeId}/cities").hasAnyAuthority("BUS_OPERATOR","ADMIN","PASSENGER")

                        //seat
                        .requestMatchers(HttpMethod.POST,"/api/add/seat/{busId}").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.POST,"/api/add/seat/bulk/{busId}").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET,"/api/get/seat/{busId}").hasAnyAuthority("BUS_OPERATOR","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/get/all/seats/bus/{busId}").hasAnyAuthority("BUS_OPERATOR","ADMIN","PASSENGER")
                        .requestMatchers(HttpMethod.PUT,"/api/seat/update/{seatId}/{busId}").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.DELETE,"/api/seat/delete/{seatId}/{busId}").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET,"/api/seat/filter/bySeatType/{busId}").hasAnyAuthority("BUS_OPERATOR","ADMIN","PASSENGER")
                        .requestMatchers(HttpMethod.GET,"/api/seats/bus/summary/{busId}").hasAnyAuthority("BUS_OPERATOR","ADMIN")

                        //schedule
                        .requestMatchers(HttpMethod.POST,"/api/add/schedule").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET,"/api/get/all/schedule").hasAnyAuthority("BUS_OPERATOR","ADMIN","PASSENGER")
                        .requestMatchers(HttpMethod.GET,"/api/get/schedule/operator").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET,"/api/schedule/dynamic/filter").hasAnyAuthority("BUS_OPERATOR","ADMIN","PASSENGER")
                        .requestMatchers(HttpMethod.GET,"/api/operator/schedule/count").hasAuthority("BUS_OPERATOR")




                        //booking
                        .requestMatchers(HttpMethod.POST,"/api/add/booking").hasAuthority("PASSENGER")
                        .requestMatchers(HttpMethod.GET,"/api/get/booking").hasAuthority("PASSENGER")
                        .requestMatchers(HttpMethod.GET,"/api/get/ticket/{bookingId}").hasAuthority("PASSENGER")
                        .requestMatchers(HttpMethod.GET,"/api/operator/booking/status").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET,"/api/operator/booking/count").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET, "/api/operator/bookings").hasAuthority("BUS_OPERATOR")

                        //booking seat
                        .requestMatchers(HttpMethod.GET,"/api/get/booking/details/{bookingId}").hasAnyAuthority("BUS_OPERATOR","ADMIN","PASSENGER")
                        .requestMatchers(HttpMethod.POST,"/api/upload/id-document").hasAuthority("PASSENGER")

                        //payment
                        .requestMatchers(HttpMethod.POST,"/api/initiate/payment/{id}").hasAuthority("PASSENGER")
                        .requestMatchers(HttpMethod.GET,"/api/get/payment/{paymentId}").hasAuthority("PASSENGER")

                        //cancellation
                        .requestMatchers(HttpMethod.POST,"/api/request/cancellation/{id}").hasAuthority("PASSENGER")

                        //review
                        .requestMatchers(HttpMethod.GET,"/api/operator/reviews").hasAuthority("BUS_OPERATOR")
                        .requestMatchers(HttpMethod.GET,"/api/operator/reviews/summary").hasAuthority("BUS_OPERATOR")

                        .anyRequest().permitAll()

                );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }
    @Bean
    public AuthenticationProvider getProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(myUserDetailsService);
        authenticationProvider.setPasswordEncoder(getEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder getEncoder(){
        return new BCryptPasswordEncoder();
    }

}
