package org.lonelycoder.server.system.service;

import org.junit.Test;
import org.lonelycoder.core.entity.search.SearchRequest;
import org.lonelycoder.core.entity.search.Searchable;
import org.lonelycoder.core.entity.search.filter.SearchFilterHelper;
import org.lonelycoder.server.system.user.entity.User;
import org.lonelycoder.server.system.user.service.UserService;
import org.lonelycoder.server.test.BaseFT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<User> userList = userService.findAll();

        System.out.println("size ==="+userList.size());


        Map<String, Object> searchParams = new HashMap<String, Object>();
        searchParams.put("username_eq", "jjj");
        SearchRequest search = new SearchRequest(searchParams);

        search.or(SearchFilterHelper.newCondition("email_eq","sdasd@163.com"), SearchFilterHelper.newCondition("email_eq","sdassd@163.com"));

        Page<User> userPage = userService.findAll(search);
        System.out.println(userPage.getTotalPages());

    }
}
