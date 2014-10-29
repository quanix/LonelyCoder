package org.lonelycoder.server.system.user.service;

import org.lonelycoder.core.service.BaseService;
import org.lonelycoder.server.system.user.entity.User;
import org.lonelycoder.server.system.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author : lihaoquan
 * Description :
 */

@Component
@Transactional
public class UserService extends BaseService<User,Long> {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);


    /**
     * 自动装载UserDao
     * @return
     */
    @Autowired
    private UserRepository getUserDao() {
        return (UserRepository) baseRepository;
    }


    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    public User findByUsername(String username) {
        return getUserDao().findByUsername(username);
    }


    /**
     * 继承父类的保存方法,对密码进行处理
     * @param user
     * @return
     */
    @Override
    public User save(User user) {
        return super.save(user);
    }
}
