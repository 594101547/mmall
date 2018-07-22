package com.mmall.service;
/**
 * created by dingtao
 */
import com.mmall.common.ServiceResponse;
import com.mmall.vo.CartVO;

public interface ICarService {
    ServiceResponse<CartVO> add(Integer userId, Integer productId, Integer count);
    ServiceResponse<CartVO> update(Integer userId,Integer productId,Integer count);
    ServiceResponse<CartVO> deleteProduct(Integer userId,String productIds);
    ServiceResponse<CartVO> list(Integer userId);
    ServiceResponse<CartVO> selectOrUnSelect(Integer userId,Integer productId,Integer checked);
    ServiceResponse<Integer> getCartProductCount(Integer userId);

}
