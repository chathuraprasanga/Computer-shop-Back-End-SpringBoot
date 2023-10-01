package com.example.Computer_shop_management_system.restImpl;

import com.example.Computer_shop_management_system.constents.AppConstants;
import com.example.Computer_shop_management_system.rest.UserRest;
import com.example.Computer_shop_management_system.service.UserService;
import com.example.Computer_shop_management_system.utis.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserRestImpl implements UserRest {

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
       try {
           return userService.signUp(requestMap);
       }catch (Exception ex){
           ex.printStackTrace();
       }
       return AppUtils.getResponseEntity(AppConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try{
            return userService.login(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return AppUtils.getResponseEntity(AppConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
