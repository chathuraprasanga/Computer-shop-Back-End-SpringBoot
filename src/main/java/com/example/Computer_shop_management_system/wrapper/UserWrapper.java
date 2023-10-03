package com.example.Computer_shop_management_system.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWrapper {

    private Integer id;

    private String username;

    private String email;

    private String contactNumber;

    private String status;

}
