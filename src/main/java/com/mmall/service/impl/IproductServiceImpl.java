package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.controller.backend.ProductManageController;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DataTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;
import com.mmall.vo.ProductListVO;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

/*
create by dingtao
 */
public class IproductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    public ServiceResponse saveOrUpdateProduct(Product product){
        if(product !=null){

            if(StringUtils.isBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length>0){
                    product.setMainImage(subImageArray[0]);
                }
            }

            if(product.getId() != null){
                int rowCount = productMapper.updateByPrimaryKey(product);
                if(rowCount>0){
                    return ServiceResponse.createBySuccess("更新产品成功");
                }
                return ServiceResponse.createByErrorMessage("更新产品失败");
            }else{
                int rowCount = productMapper.insert(product);
                if(rowCount>0){
                    return ServiceResponse.createBySuccess("更新产品成功");
                }
                return ServiceResponse.createByErrorMessage("新增产品成功");
            }

        }
        return ServiceResponse.createByErrorMessage("新增或者更新产品参数不正确");
    }

    public ServiceResponse<String> setSaleStatus(Integer productId,Integer status){
        if(productId == null || status ==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0){
            return ServiceResponse.createBySuccess("修改产品消失状态成功");
        }
        return ServiceResponse.createByErrorMessage("修改产品参数状态失败");
    }

    //使用VO
    public ServiceResponse<ProductDetailVO> manageProductDetail(Integer productId){
        if(productId == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServiceResponse.createByErrorMessage("产品已下架，找不到对应ID的产品");
        }
        //VO对象--value object
        //pojo->bo(business object) -> vo(view object)
        //使用VO来组装我们的对象
        ProductDetailVO productDetailVO = assmbleProductDetailVO(product);
        return ServiceResponse.createBySuccess(productDetailVO);
    }

    private ProductDetailVO assmbleProductDetailVO(Product product){
        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setId(product.getId());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setName(product.getName());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());

        //分离成为配置
        //imageHost
        //parmentCatagoryId
        productDetailVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVO.setCategoryId(0);//默认根节点
        }else{
            productDetailVO.setParentCategoryId(category.getParentId());
        }
        //createTime
        //updateTime
        productDetailVO.setCreateTime(DataTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVO.setUpdateTime(DataTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVO;
    }


    //获取产品列表
    public ServiceResponse<PageInfo> getProductList(int pageNum,int pageSize){
        //startpage-->start
        //填充自己的sql逻辑
        //pageHelper--收尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVO> productListVOList = Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVO productListVO = assembleProductListVO(productItem);
            productListVOList.add(productListVO);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVOList);
        return ServiceResponse.createBySuccess(pageResult);
    }

    private ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVO = new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setName(product.getName());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVO.setMainImage(product.getMainImage());
        productListVO.setPrice(product.getPrice());
        productListVO.setSubtitle(product.getSubtitle());
        productListVO.setStatus(product.getStatus());
        return productListVO;
    }

    //产品搜索
    public ServiceResponse<PageInfo> searchProduct(String productName,Integer productId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product>  productList = Lists.newArrayList();
        List<ProductListVO> productListVOList = Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVO productListVO = assembleProductListVO(productItem);
            productListVOList.add(productListVO);
        }
        //开始分页
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVOList);
        return ServiceResponse.createBySuccess(pageResult);
    }
}
