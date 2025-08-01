package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.adapter.in.web.dto.response.MeResponse;
import com.opencu.bookit.domain.model.user.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MeResponseMapper {

    @org.mapstruct.Mapping(target = "username", expression = "java(extractUsername(userModel.getUsername()))")
    MeResponse toResponse(UserModel userModel);

    default String extractUsername(String username) {
        if (username == null) return null;
        int idx = username.indexOf(';');
        if (idx == 0) return null;
        return idx >= 0 ? username.substring(0, idx) : username;
    }
}