package com.mmall.service.impl;
/**
 *created by dingtao
 */
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICarService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVO;
import com.mmall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
@Service("iCartService")
public class ICartServciceImpl implements ICarService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    //添加购物车
    public ServiceResponse<CartVO> add(Integer userId,Integer productId,Integer count){

        if(productId == null ||count == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart =cartMapper.selectByUserIdProductId(userId,productId);
        if(cart == null){
            //说明这个产品不再购物车内，需要新增该产品的记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else{
            //产品已经存在，两者数量相加，再跟新
            count = cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        CartVO cartVO = this.getCartVoLimit(userId);
        return ServiceResponse.createBySuccess(cartVO);

    }


    private CartVO getCartVoLimit(Integer userId){
        CartVO cartVO = new CartVO();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVO>  cartProductVOList=Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");

        if(!CollectionUtils.isEmpty(cartList)){
            for(Cart cartItem: cartList){
                CartProductVO cartProductVO = new CartProductVO();
                cartProductVO.setId(cartItem.getId());
                cartProductVO.setUserId(cartItem.getUserId());
                cartProductVO.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    cartProductVO.setProductMainImage(product.getMainImage());
                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductSubtitle(product.getSubtitle());
                    cartProductVO.setProductStatus(product.getStatus());
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount=0; //初始化可以购买产品的库存
                    if(product.getStock() >= cartItem.getQuantity()){
                        //产品的库存，要大于它所设定的数量
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVO.setLimitQuantntity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        //放入有效库存,库存不充足的时候
                        buyLimitCount = product.getStock(); //最大值就是商品的库存，无法再添加了
                        cartProductVO.setLimitQuantntity(Const.Cart.LIMIT_NUM_FAIL);
                       //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVO.setQuantity(buyLimitCount);
                    //计算总价，购物车中对应的当前的产品的数量和单价的总价值
                    cartProductVO.setProductTotalPrice(BigDecimalUtil.sub(product.getPrice().doubleValue(),cartProductVO.getQuantity()));
                    cartProductVO.setProductChecked(cartItem.getChecked());
                }
                //判断是否被勾选，如果是勾选的计算整个购物车的总价
                if(cartItem.getChecked() == Const.Cart.CHECKED){
                    //增加到总价当中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
                }
                cartProductVOList.add(cartProductVO);

            }
        }
        cartVO.setCartTotalPrice(cartTotalPrice);
        cartVO.setCartProductVOList(cartProductVOList);
        cartVO.setAllChecked(this.getAllCheckedStatus(userId));
        cartVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVO;
    }

    //哦判断是否权限
    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }else{
            return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
        }
    }

    //update方法
    public ServiceResponse<CartVO> update(Integer userId,Integer productId,Integer count){
        if(productId == null ||count == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
        if(cart != null){
            //更新购物车中的产品数量
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        CartVO cartVO = this.getCartVoLimit(userId);
        return ServiceResponse.createBySuccess(cartVO);
    }

    //delete购物车中产品
    public  ServiceResponse<CartVO> deleteProduct(Integer userId,String productIds){
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        //重新从DB中获取最新的，并获得返回
        CartVO cartVO = this.getCartVoLimit(userId);
        return ServiceResponse.createBySuccess(cartVO);
    }

    //查询接口
    public ServiceResponse<CartVO> list(Integer userId){
        CartVO cartVO = this.getCartVoLimit(userId);
        return ServiceResponse.createBySuccess(cartVO);

    }

    //全选或者全反选,或者单独选、反选
    public ServiceResponse<CartVO> selectOrUnSelect(Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrUnCheckedProduct(userId,productId,checked);
        return this.list(userId);
    }

    //获取购物车总的数量
    public ServiceResponse<Integer> getCartProductCount(Integer userId){
        if(userId == null){
            return ServiceResponse.createBySuccess(0);
        }
        return ServiceResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

}
