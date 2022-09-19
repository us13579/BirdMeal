package com.backend.birdmeal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long userSeq;
    private String userEmail;
    private boolean userRole; //0이면 일반사용자, 1이면 child
    private String userNickname;
    private String userEoa;
    private String userTel;
    private String userAdd;
    private boolean userChargeState;
    private String userCreateDate;
    private String userUpdateDate;
}
