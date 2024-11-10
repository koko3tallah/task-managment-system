package com.kerolos.tms.banquemisr.challenge05.data.dto;

import com.kerolos.tms.banquemisr.challenge05.data.enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse implements Serializable {

    private Long id;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private UserRole role;
    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
