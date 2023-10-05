package com.example.Computer_shop_management_system.dao;

import com.example.Computer_shop_management_system.POJO.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillDao extends JpaRepository<Bill, Integer> {
}
