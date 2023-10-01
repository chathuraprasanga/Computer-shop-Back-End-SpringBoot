package com.example.Computer_shop_management_system.JWT;

import com.example.Computer_shop_management_system.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

//    Developer ID = Chathura Prasanga
//    Date = 18.04.2023

@Slf4j
@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    private com.example.Computer_shop_management_system.POJO.User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername {}",username);
        userDetail = userDao.findByEmail(username);
        if(!Objects.isNull(userDetail))
            return  new User(userDetail.getEmail(),userDetail.getPassword(),new ArrayList<>());
        else
            throw new UsernameNotFoundException("User not fountd.");
    }

    public com.example.Computer_shop_management_system.POJO.User getUserDetail(){
        return userDetail;
    }

}
