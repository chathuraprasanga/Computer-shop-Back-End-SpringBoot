package com.example.Computer_shop_management_system.serviceImple;

import com.example.Computer_shop_management_system.JWT.JwtFilter;
import com.example.Computer_shop_management_system.POJO.Category;
import com.example.Computer_shop_management_system.constents.AppConstants;
import com.example.Computer_shop_management_system.dao.CategoryDao;
import com.example.Computer_shop_management_system.service.CategoryService;
import com.example.Computer_shop_management_system.utils.AppUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try{
            if (jwtFilter.isAdmin()){
                if (validateCategoryMap(requestMap, false)){
                    categoryDao.save(getCategoryFromMap(requestMap, false));
                    return AppUtils.getResponseEntity("Category added successfully", HttpStatus.OK);
                }
            }else {
                return AppUtils.getResponseEntity(AppConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return AppUtils.getResponseEntity(AppConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id") && validateId){
                return true;
            }else if(!validateId){
                return true;
            }
        }
        return false;
    }


    private Category getCategoryFromMap(Map<String, String> requestMap, Boolean isAdd){
        Category category = new Category();
        if (isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }


    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
                log.info("InsideIf");
                return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(), HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryDao.findAll(),HttpStatus.OK);
        }catch (Exception ex){

        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{
            if (jwtFilter.isAdmin()){
                log.info("adminUpdate");
                if (validateCategoryMap(requestMap, true)){
                    Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (optional.isPresent()){
                        categoryDao.save(getCategoryFromMap(requestMap,true));
                        return AppUtils.getResponseEntity("Category updated successfully.", HttpStatus.OK);
                    }else {
                        return AppUtils.getResponseEntity("Category id doesn't exists.",HttpStatus.OK);
                    }
                }
                return AppUtils.getResponseEntity(AppConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else {
                return AppUtils.getResponseEntity(AppConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return AppUtils.getResponseEntity(AppConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
