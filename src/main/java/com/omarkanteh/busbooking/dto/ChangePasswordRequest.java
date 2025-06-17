package com.omarkanteh.busbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
