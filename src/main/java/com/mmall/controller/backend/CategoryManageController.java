package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /*
    添加品类的方法
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServiceResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId",defaultValue = "0") int parentId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        //校验一下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是用户管理员，进行处理分类逻辑
            return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return ServiceResponse.createByErrorMessage("用户不是管理员权限，需要管理员才可以操作");
        }
    }


    /*
    更新品类名字方法
     */
    @RequestMapping("set_categoryName.do")
    @ResponseBody
    public ServiceResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServiceResponse.createByErrorMessage("用户未登录，请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else{
            return ServiceResponse.createByErrorMessage("无操作权限，需要管理员权限");
        }
    }

    /*
    不递归查询子节点，查询平行节点
     */
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServiceResponse getChildParalleCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServiceResponse.createByErrorMessage("用户未登录，请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询子节点的category信息，并且不递归 只查询平级的
            return iCategoryService.getChildParallelCategory(categoryId);
        }else{
            return ServiceResponse.createByErrorMessage("无操作权限，需要管理员权限");
        }
    }


    /*
    不递归查询子节点，查询当前节点和递归子节点
     */
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServiceResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServiceResponse.createByErrorMessage("用户未登录，请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询子节点的category信息，不递归查询子节点，查询当前节点和递归子节点
            return iCategoryService.selectCategoryAndChilerenById(categoryId);
        }else{
            return ServiceResponse.createByErrorMessage("无操作权限，需要管理员权限");
        }
    }
}
