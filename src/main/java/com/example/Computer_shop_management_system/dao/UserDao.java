package com.example.Computer_shop_management_system.dao;

import com.example.Computer_shop_management_system.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<User, Integer> {

//    find user by email
    User findByEmail(@Param("email")String email);

}
