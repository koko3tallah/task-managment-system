package com.kerolos.tms.banquemisr.challenge05.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RefreshTokenRequest implements Serializable  {

    private String refreshToken;

}
