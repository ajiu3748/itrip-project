package com.cskt.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cskt.entity.ItripUser;
import com.cskt.mapper.ItripUserMapper;
import com.cskt.service.ItripUserService;
@Service
public class ItripUserServiceImpl extends ServiceImpl<ItripUserMapper, ItripUser> implements ItripUserService{

}
