package com.example.Computer_shop_management_system.serviceImple;

import com.example.Computer_shop_management_system.JWT.CustomerUserDetailsService;
import com.example.Computer_shop_management_system.JWT.JwtFilter;
import com.example.Computer_shop_management_system.JWT.JwtUtil;
import com.example.Computer_shop_management_system.POJO.User;
import com.example.Computer_shop_management_system.constents.AppConstants;
import com.example.Computer_shop_management_system.dao.UserDao;
import com.example.Computer_shop_management_system.service.UserService;
import com.example.Computer_shop_management_system.utils.AppUtils;
import com.example.Computer_shop_management_system.utils.EmailUtils;
import com.example.Computer_shop_management_system.wrapper.UserWrapper;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    private EmailUtils emailUtils;


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

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try{
            if (jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new  ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try{
            if (jwtFilter.isAdmin()){
                Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if (optional.isPresent()){
                    userDao.updateStatus(requestMap.get("userStatus"), Integer.parseInt(requestMap.get("id")));
//                    send mails to all all admins
                    sendMailtoAllAdmin(requestMap.get("userStatus"), optional.get().getEmail(), userDao.getAllAdmin());
                    return AppUtils.getResponseEntity("User status updated successfully.", HttpStatus.OK);
                }else {
                    AppUtils.getResponseEntity("User id doesn't exist.", HttpStatus.OK);
                }
            }else {
                return AppUtils.getResponseEntity(AppConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return AppUtils.getResponseEntity(AppConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private void sendMailtoAllAdmin(String userStatus, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if (userStatus != null && userStatus.equalsIgnoreCase("true")){
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account approved", "USER:- "+ user +"\nis approved by \nADMIN:- "+jwtFilter.getCurrentUser(), allAdmin);
        }else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account disabled", "USER:- "+ user +"\nis approved by \nADMIN:- "+jwtFilter.getCurrentUser(), allAdmin);
        }
    }


    @Override
    public ResponseEntity<String> checkToken() {
        return AppUtils.getResponseEntity("true", HttpStatus.OK);
    }


    @Override
    public ResponseEntity<String> chanagePassword(Map<String, String> requestMap) {
        try{
            User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
            if (!userObj.equals(null)){
                if (userObj.getPassword().equals(requestMap.get("oldPassword"))){
                    userObj.setPassword(requestMap.get("newPassword"));
                    userDao.save(userObj);
                    return AppUtils.getResponseEntity("Password updated successfully.", HttpStatus.OK);
                }
                return AppUtils.getResponseEntity("Incorrect Password.", HttpStatus.BAD_REQUEST);
            }
            return AppUtils.getResponseEntity(AppConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return AppUtils.getResponseEntity(AppConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try{
            User user = userDao.findByEmail(requestMap.get("email"));
            if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail()))
                emailUtils.forgotMail(user.getEmail(), "Credential by FIXCOM Management system.", user.getPassword());
            return AppUtils.getResponseEntity("Check your mail for credentials.", HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return AppUtils.getResponseEntity(AppConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
