package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService{
    @Autowired
    private UserMapper userMapper;

    //登陆方法
    @Override
    public ServiceResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServiceResponse.createByErrorMessage("用户名不存在~");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if(user ==null){
            return ServiceResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return  ServiceResponse.createBySuccess("登录成功",user);
    }

    //注册方法
    public ServiceResponse<String> register(User user){

        ServiceResponse vaildResponse = this.checkVaild(user.getUsername(),Const.USERNAME);
        if(!vaildResponse.isSuccess()){
            return vaildResponse;
        }

//        int resultCount = userMapper.checkUsername(user.getUsername());
//        if(resultCount > 0){
//            return ServiceResponse.createByErrorMessage("用户名已存在~");
//        }

        vaildResponse = this.checkVaild(user.getEmail(),Const.EMAIL);
        if(!vaildResponse.isSuccess()){
            return vaildResponse;
        }

//        resultCount = userMapper.checkEmail(user.getEmail());
//        if(resultCount > 0){
//            return ServiceResponse.createByErrorMessage("用户名已存在~");
//        }
        user.setRole(Const.Role.ROLE_CUSTER);
        //MD5加密，对密码进行加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if(resultCount ==0){
            return ServiceResponse.createByErrorMessage("注册失败");
        }
        return ServiceResponse.createBySuccess("注册成功");
    }

    /*
    校验参数是否无效，即是否在数据库中存在
    type :username、email
     */
    public ServiceResponse<String> checkVaild(String str,String type){
        if(StringUtils.isNotBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount >0){
                    return ServiceResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServiceResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }else{
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        return ServiceResponse.createBySuccess("校验成功");
    }

    //忘记密码时通过username获取用户对应的密码问题
    public ServiceResponse selectQuestion(String username){
        ServiceResponse vaildResponse = this.checkVaild(username,Const.USERNAME);
        if(vaildResponse.isSuccess()){
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServiceResponse.createBySuccess(question);
        }
        return ServiceResponse.createByErrorMessage("找回密码的问题为空~");
    }

    //对忘记密码对应的问题进行校验判断
    public ServiceResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //说明问题及问题答案是属于这个用户，且全部正确
            //生成一个Token,使用UUID的randomUUID来实现
            String forgetToken = UUID.randomUUID().toString();
            //把forgetToken放置在本地，并设置有效期
            TokenCache.setKey("token_"+username,forgetToken);
            return ServiceResponse.createBySuccessMessage(forgetToken);
        }
        return ServiceResponse.createByErrorMessage("问题的答案错误");
    }

    public ServiceResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServiceResponse.createByErrorMessage("参数错误，Token为空");
        }

        //对username进行校验 因为token的key是token_{username}。如果username为空。那就一直是token_
        ServiceResponse vaildResponse = this.checkVaild(username,Const.USERNAME);
        if(vaildResponse.isSuccess()){
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServiceResponse.createByErrorMessage("Token无效，或者已过期");
        }

        if(StringUtils.equals(forgetToken,token)){
            String md5Password= MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount=userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount>0){
                return ServiceResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServiceResponse.createByErrorMessage("Token错误，请重新获取重置密码的token");
        }
        return ServiceResponse.createByErrorMessage("修改密码失败哦");



    }

    public ServiceResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        //防止横向越权，要校验一下这个旧密码是否属于这个User,因为我们会查询count(1)，如果不指定id，那么结果肯定>0，为ture
        int resultCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount ==0){
            return ServiceResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount =userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServiceResponse.createBySuccessMessage("更新密码成功");
        }
        return ServiceResponse.createByErrorMessage("更新密码失败");
    }

    public ServiceResponse<User> updateInformation(User user){
        //username不能被更新
        //email要做校验，是否这个email已经存在，同时如果这个email存在，则不能是当前这个用户的。
        int resultCount = userMapper.checkEmailById(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServiceResponse.createByErrorMessage("邮箱已存在，请重新输入邮箱");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return ServiceResponse.createBySuccess("个人信息更新成功",updateUser);
        }
        return ServiceResponse.createByErrorMessage("个人信息更新失败");
    }

    public ServiceResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServiceResponse.createByErrorMessage("找不到当前用户");
        }
        //将密码置空，因为这里不需要返回密码，保护用户安全隐私
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess(user);
    }

    //backend
    public ServiceResponse checkAdminRole(User user){
        if(user !=null && user.getRole() == Const.Role.ROLE_ADMIN){
            return ServiceResponse.createBySuccess();
        }else{
            return ServiceResponse.createByErrorMessage("用户不是管路员");
        }
    }
}
