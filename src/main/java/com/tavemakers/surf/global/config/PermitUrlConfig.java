package com.tavemakers.surf.global.config;

import org.springframework.stereotype.Component;

@Component
public class PermitUrlConfig {

    public String[] getPublicUrl(){
        return new String[]{
                "/login/**",
                "/api/members/signup",

                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
//                "/v1/member/**",
//                "/v1/manager/**"

        };
    }

    public String[] getMemberUrl(){
        return new String[]{
                "/v1/member/**",
        };
    }

    public String[] getAdminUrl(){
        return new String[]{
                "/api/admin/**",
                "/v1/admin/**"
        };
    }

}