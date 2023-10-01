package com.example.Computer_shop_management_system.serviceImple;

import com.example.Computer_shop_management_system.JWT.CustomerUserDetailsService;
import com.example.Computer_shop_management_system.JWT.JwtUtil;
import com.example.Computer_shop_management_system.POJO.User;
import com.example.Computer_shop_management_system.constents.AppConstants;
import com.example.Computer_shop_management_system.dao.UserDao;
import com.example.Computer_shop_management_system.service.UserService;
import com.example.Computer_shop_management_system.utis.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;


    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signUp {}", requestMap);
        try {
            if(validateSignUpMap(requestMap)){
                User user = userDao.findByEmail(requestMap.get("email"));   // check if there are any user in same email
                if(user == null){
                    userDao.save(getUserFromMap(requestMap));
                    return AppUtils.getResponseEntity("Successfully registered.",HttpStatus.OK);
                }else {
                    return AppUtils.getResponseEntity("Email already exists.",HttpStatus.BAD_REQUEST);
                }
            }else {
                return AppUtils.getResponseEntity(AppConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return AppUtils.getResponseEntity(AppConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    check the received map have correct keys
    private boolean validateSignUpMap(Map<String,String> requestMap){
        if (requestMap.containsKey("username") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password")){
            return true;
        }
        return false;
    }

//    convert map to user object
    private User getUserFromMap(Map<String,String> requestMap){
        User user = new User();
        user.setUserName(requestMap.get("username"));
        user.setContactNumber((requestMap.get("contactNumber")));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setUserStatus("false");
        user.setUserRole("user");
        return user;
    }


    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try{
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
            );
            if (auth.isAuthenticated()){
                if (customerUserDetailsService.getUserDetail().getUserStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\""+
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getUserRole()) + "\"}",
                    HttpStatus.OK);
                }else {
                    return new ResponseEntity<String>("(\"message\":\""+"Wait for Admin Approval."+"\"}",
                            HttpStatus.BAD_REQUEST);
                }
            }
        }catch (Exception ex){
            log.error("{}",ex);
        }
        return new ResponseEntity<String>("(\"message\":\""+"Bad Credentials."+"\"}",
                HttpStatus.BAD_REQUEST);
    }
}
