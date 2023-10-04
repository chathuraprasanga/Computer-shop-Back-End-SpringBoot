package com.example.Computer_shop_management_system.dao;

import com.example.Computer_shop_management_system.POJO.User;
import com.example.Computer_shop_management_system.wrapper.UserWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface UserDao extends JpaRepository<User, Integer> {

//    find user by email
    User findByEmail(@Param("email")String email);

    List<UserWrapper> getAllUser();

    List<String> getAllAdmin();

    @Transactional
    @Modifying
    Integer updateStatus(@Param("userStatus") String userStatus, @Param("id") Integer id);

//    find user by user email
//    User findByEmail(String email);

}
