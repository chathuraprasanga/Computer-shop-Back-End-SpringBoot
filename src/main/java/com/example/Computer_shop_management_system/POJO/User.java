package com.example.Computer_shop_management_system.POJO;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

//named query here
//find user by email for login purpose
@NamedQuery(name = "User.findByEmail", query = "select u from User u where u.email=:email")

@NamedQuery(name = "User.getAllUser", query = "select new com.example.Computer_shop_management_system.wrapper.UserWrapper(u.id,u.userName,u.email,u.contactNumber,u.userStatus) from User u where u.userRole='user'")

@NamedQuery(name = "User.updateStatus", query = "update User u set u.userStatus=:userStatus where u.id=:id")

@NamedQuery(name = "User.getAllAdmin", query = "select u.email from User u where u.userRole='admin'")

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userID")
    private Integer userID;

    @Column(name = "userName")
    private String userName;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "userRole")
    private String userRole;

    @Column(name = "userStatus")
    private String userStatus;



}
