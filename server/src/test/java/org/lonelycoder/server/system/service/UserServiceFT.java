package org.lonelycoder.server.system.service;

import org.junit.Test;
import org.lonelycoder.server.system.user.entity.User;
import org.lonelycoder.server.system.user.service.UserService;
import org.lonelycoder.server.test.BaseFT;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : lihaoquan
 */
public class UserServiceFT extends BaseFT {

    @Autowired
    private UserService userService;

    @Test
    public void testMain() {

        System.out.println("hello world");

        User user = new User();
        user.setUsername("jjj");
        user.setPassword("2312312asdasd3");
        user.setEmail("sdasd@163.com");
        user.setMobilePhoneNumber("13922401777");

        userService.save(user);


        User currentUser = userService.findByUsername("jjj");

        System.out.println(">>>"+currentUser.getEmail());


    }
}
