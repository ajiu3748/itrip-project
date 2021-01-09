package com.cskt.itripauth;

import com.cskt.service.ItripUserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ItripAuthApplicationTests {
    @Resource
    private ItripUserService userService;

    @Test
    void contextLoads() {
        userService.removeById(12);
    }

}
