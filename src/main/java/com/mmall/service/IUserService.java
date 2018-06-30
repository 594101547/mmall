package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;

/*
 *created by dingtao
 */
public interface IUserService {
    ServiceResponse<User> login(String username, String password);
    ServiceResponse<String> register(User user);
    public ServiceResponse<String> checkVaild(String str,String type);
}

