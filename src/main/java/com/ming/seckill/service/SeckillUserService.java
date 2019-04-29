package com.ming.seckill.service;

import com.ming.seckill.dao.SeckillUserDAO;
import com.ming.seckill.domain.SeckillUser;
import com.ming.seckill.exception.GlobalException;
import com.ming.seckill.redis.RedisService;
import com.ming.seckill.redis.SeckillUserKey;
import com.ming.seckill.result.CodeMsg;
import com.ming.seckill.util.MD5Util;
import com.ming.seckill.util.UUIDUtil;
import com.ming.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class SeckillUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    SeckillUserDAO seckillUserDAO;

    @Autowired
    RedisService redisService;

    //优化：对象级缓存
    public SeckillUser getById(long id){
        //取缓存
        SeckillUser seckillUser = redisService.get(SeckillUserKey.getById,""+id,SeckillUser.class);
        if (seckillUser!=null){
            return seckillUser;
        }
        //查数据库
        seckillUser = seckillUserDAO.getById(id);
        if (seckillUser!=null) {
            redisService.set(SeckillUserKey.getById,""+id,seckillUser);
        }
        return seckillUser;
    }

    public boolean updatePassword(String token,long id,String formPass){
        SeckillUser seckillUser = getById(id);
        if (seckillUser==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //为什么要新建一个对象更新，不用原来的对象更新？因为只需要 更新password字段，更新的东西越多，sql越多
        SeckillUser toBeUpdate = new SeckillUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass,seckillUser.getSalt()));
        //TODO 是否要判断这些操作是否成功？
        seckillUserDAO.update(toBeUpdate);
        //更新 成功，修改缓存
        redisService.delete(SeckillUserKey.getById,""+id);
        //更新token
        seckillUser.setPassword(toBeUpdate.getPassword());
        redisService.delete(SeckillUserKey.token,""+token);
        return true;
    }

    public String login(HttpServletResponse response,LoginVo loginVo) {
        if (loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        SeckillUser seckillUser = seckillUserDAO.getById(Long.parseLong(mobile));
        if (seckillUser == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = seckillUser.getPassword();
        String dbSalt = seckillUser.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass,dbSalt);
        if (!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(seckillUser,token,response);
        return token;
    }

    public SeckillUser getByToken(HttpServletResponse response,String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        SeckillUser seckillUser = redisService.get(SeckillUserKey.token,token,SeckillUser.class);
        //延长一下有效期，延长session有效期
        if (seckillUser!=null) {
            addCookie(seckillUser,token, response);
        }
        return seckillUser;
    }

    private void addCookie(SeckillUser seckillUser,String token,HttpServletResponse response){
        //生成cookie
        //用户信息和token写到redis中
        redisService.set(SeckillUserKey.token,token,seckillUser);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        //有效期设为redis中session的有效期
        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
