package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/*
create by dingtao
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;
    /*
    用户登录
     */
    @RequestMapping(value="login.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> login(String username,String password,HttpSession session){
        ServiceResponse<User> response =  iUserService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /*
    登出功能
     */
    @RequestMapping(value="logout.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySuccess();
    }

    @RequestMapping(value="register.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value="check_vaild.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> checkValid(String str,String type){
        return iUserService.checkVaild(str,type);
    }

    @RequestMapping(value="get_user_info.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServiceResponse.createByErrorMessage("用户未登陆，无法获取当前信息");
        }else{
            return ServiceResponse.createBySuccess(user);
        }
    }

    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetRestPassword(username, passwordNew, forgetToken);
    }

    //登录状态下，重置密码
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    //更新个人用户信息

    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> update_information(HttpSession session,User user){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        //todo:为什么这里需要setId ，难道从session里面获取到的User没有Id ？ ---这里是为了防止用户获取信息时越权获取，所以先从session里面拿到Id，丢给User,防止ID被变化
        user.setId(currentUser.getId());
        ServiceResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            //todo:这里也是同样如此,username绝对不可变，所以也需要setName。全部从登陆信息中获取
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    //获取用户的详细信息
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> get_information(HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，需要强制登陆");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
