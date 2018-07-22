package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Category;

import java.util.List;

/*
create by dingtao
 */
public interface ICategoryService {
    public ServiceResponse addCategory(String categoryName, Integer parentId);
    public ServiceResponse updateCategoryName(Integer categoryId,String categoryName);
    public ServiceResponse<List<Category>> getChildParallelCategory(Integer categoryId);
    public ServiceResponse<List<Integer>> selectCategoryAndChilerenById(Integer categoryId);
}
