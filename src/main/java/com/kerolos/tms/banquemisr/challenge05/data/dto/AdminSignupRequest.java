package com.kerolos.tms.banquemisr.challenge05.data.dto;

import com.kerolos.tms.banquemisr.challenge05.data.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminSignupRequest extends SignupRequest implements Serializable {

    private UserRole role;

    public AdminSignupRequest(SignupRequest signupRequest) {
        this.setFullName(signupRequest.getFullName());
        this.setEmail(signupRequest.getEmail());
        this.setPassword(signupRequest.getPassword());
        this.setDateOfBirth(signupRequest.getDateOfBirth());
        this.role = UserRole.USER;
    }

}