package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVO;

public interface IProductService {
    public ServiceResponse saveOrUpdateProduct(Product product);
    public ServiceResponse<String> setSaleStatus(Integer productId,Integer status);
    public ServiceResponse<ProductDetailVO> manageProductDetail(Integer productId);
    public ServiceResponse<PageInfo> getProductList(int pageNum, int pageSize);
    public ServiceResponse<PageInfo> searchProduct(String productName,Integer productId,Integer pageNum,Integer pageSize);
}
