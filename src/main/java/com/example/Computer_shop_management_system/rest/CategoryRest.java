package com.example.Computer_shop_management_system.rest;

import com.example.Computer_shop_management_system.POJO.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/category")
public interface CategoryRest {

    @PostMapping(path = "/add")
    ResponseEntity<String> addNewCategory(@RequestBody(required=true) Map<String,String> requestMap);

    @RequestMapping(path = "/get")
    ResponseEntity<List<Category>> getAllCategory(@RequestParam(required = false) String filterValue);

    @RequestMapping(path = "/update")
    ResponseEntity<String> updateCategory(@RequestBody(required = true) Map<String, String> requestMap);

}
