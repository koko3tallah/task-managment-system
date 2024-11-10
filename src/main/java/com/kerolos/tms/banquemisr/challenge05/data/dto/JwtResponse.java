package com.kerolos.tms.banquemisr.challenge05.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse implements Serializable {

    private String accessToken;
    private String refreshToken;

}
