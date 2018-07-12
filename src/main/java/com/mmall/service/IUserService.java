package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;

/*
 *created by dingtao
 */
public interface IUserService {
    ServiceResponse<User> login(String username, String password);
    ServiceResponse<String> register(User user);
    ServiceResponse<String> checkVaild(String str,String type);
    ServiceResponse selectQuestion(String username);
    ServiceResponse<String> checkAnswer(String username,String question,String answer);
    ServiceResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken);
    ServiceResponse<String> resetPassword(String passwordOld,String passwordNew,User user);
    ServiceResponse<User> updateInformation(User user);
    ServiceResponse<User> getInformation(Integer userId);
    public ServiceResponse checkAdminRole(User user);
}

