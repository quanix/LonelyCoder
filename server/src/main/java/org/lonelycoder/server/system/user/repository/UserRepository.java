package org.lonelycoder.server.system.user.repository;

import org.lonelycoder.core.repository.BaseRepository;
import org.lonelycoder.server.system.user.entity.User;

/**
 * Author : lihaoquan
 * Description :
 */
public interface UserRepository extends BaseRepository<User,Long> {

    public User findByUsername(String username);
}
