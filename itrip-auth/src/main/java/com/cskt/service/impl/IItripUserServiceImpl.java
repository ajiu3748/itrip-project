package com.cskt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cskt.common.constants.ErrorCodeEnum;
import com.cskt.common.exception.ServiceException;
import com.cskt.entity.ItripUser;
import com.cskt.mapper.ItripUserMapper;
import com.cskt.service.IItripEmailService;
import com.cskt.service.IItripUserService;
import com.cskt.service.SmsService;
import com.cskt.util.MD5;
import com.cskt.vo.SmsMessageVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: Mr.阿九
 * @createTime: 2021-01-09 09:40
 **/
@Service
public class IItripUserServiceImpl extends ServiceImpl<ItripUserMapper, ItripUser> implements IItripUserService {
    @Resource
    private ItripUserMapper userMapper;
    @Resource
    private SmsService smsService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Value(value = "${email.send.enable}")
    private boolean emailEnable;
    @Value(value = "${email.send.url}")
    private String emailUrl;
    @Value(value = "${sms.send.enable}")
    private boolean smsEnable;
    @Resource
    private IItripEmailService emailService;
    /**
     * 邮箱用户注册
     * @param user
     * @return
     */
    @Override
    public Boolean userRegisterMail(ItripUser user) {
        //对密码进行加密
        user.setUserPassword(MD5.getMd5(user.getUserPassword(),32));
        //新增用户信息
        userMapper.insert(user);
        //发送邮件链接
        this.sendEmail(user.getUserCode());
        return true;
    }

    /**
     * 发送邮件链接
     * @param code
     * @return
     */
    @Override
    public Boolean sendEmail(String code) {
        //生成验证码
        String activeCode= UUID.randomUUID().toString();
        String path=emailUrl+"/auth/api/activate?code="+activeCode;
        //是否发送邮件
        if(emailEnable){
            emailService.sendActivationMail(code,path);
        }
        //设置30分钟的缓存时间
        stringRedisTemplate.opsForValue().set(activeCode,code,30, TimeUnit.MINUTES);
        return true;
    }

    /**
     *  更新激活状态
     * @param code 用户邮箱
     * @return
     */
    @Override
    public Boolean activeEmail(String code) {
        //从缓存中根据验证码获取需要激活的用户邮箱
        String userCode=stringRedisTemplate.opsForValue().get(code);
        QueryWrapper<ItripUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", userCode);
        ItripUser user = userMapper.selectOne(queryWrapper);
        //如果用户信息为空或缓存失效
        if(user==null){
            throw  new ServiceException(ErrorCodeEnum.AUTH_ACTIVE_OVERTIME);
        }
        user.setActivated(1);
        //更新激活状态
        userMapper.updateById(user);
        return true;
    }

    /**
     * 手机号注册
     * @param user
     * @return
     */
    @Override
    public Boolean userRegisterSms(ItripUser user,String code) {
        //根据手机号+用户输入的验证码从缓存中获取验证码
        String userCode=stringRedisTemplate.opsForValue().get(user.getUserCode()+code);
        //如果未获取到值，说明验证码错误
        if(userCode==null){
            throw  new ServiceException(ErrorCodeEnum.AUTH_ACTIVE_VERIFITION);
        }
        //对密码进行加密
        user.setUserPassword(MD5.getMd5(user.getUserPassword(),32));
        //新增用户信息
        user.setActivated(1);
        userMapper.insert(user);
        return true;
    }

    /**
     *发送手机号验证码
     * @param phone
     * @return
     */
    @Override
    public Boolean sendSms(String phone) throws JsonProcessingException {
        //四位的随机数验证码
        Integer userCode=new Random().nextInt(9999);
        //手机号+验证码作为redis的key，手机号作为值
        String key=phone+userCode;
        //是否发送验证码
        if(smsEnable){
            SmsMessageVO messageVO=new SmsMessageVO(phone,userCode.toString());
            smsService.sendMsg(messageVO);
        }
        //设置30分钟的缓存时间
        stringRedisTemplate.opsForValue().set(key,phone,30, TimeUnit.MINUTES);
        return true;
    }


}
