package com.opencu.bookit.adapter.out.security.spring.payload.response;

public record TBankUserInfoResponse(
        String sub,
        String name,
        String gender,
        String birthdate,
        String family_name,
        String given_name,
        String middle_name,
        String phone_number,
        Boolean phone_number_verified
) {}