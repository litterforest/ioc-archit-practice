package com.softd.test.springframework.ioc.archit.service.impl;

import com.softd.test.springframework.ioc.archit.annotation.MyComponent;
import com.softd.test.springframework.ioc.archit.service.UserService;

/**
 * 功能描述
 *
 * @author cobee
 * @since 2020-09-20
 */
@MyComponent("userService")
public class UserServiceImpl implements UserService {
    @Override
    public String getUser(Long userId) {
        return "User["+userId+"]，User Info";
    }
}
