package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/*
created by dingtao
 */
@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServiceResponse<ProductDetailVO> detail(Integer productId){
        //要判断产品在不在线
       return iProductService.getProductDetail(productId);
    }

    //返回product列表
    @RequestMapping("list.do")
    @ResponseBody
    //require=false  表示可选参数
    public ServiceResponse<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword,
                                          @RequestParam(value = "catergoryId",required = false)Integer categoryId,
                                          @RequestParam(value = "pageNum",defaultValue ="1") int pageNum,
                                          @RequestParam(value ="pageSize",defaultValue = "10") int pageSize,
                                          @RequestParam(value ="orderBy",defaultValue = "") String orderBy){

        //要判断产品在不在线,写在方 法内部
        return iProductService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }
}
