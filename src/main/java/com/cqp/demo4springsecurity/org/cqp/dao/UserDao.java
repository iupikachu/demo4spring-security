package com.cqp.demo4springsecurity.org.cqp.dao;

import com.cqp.demo4springsecurity.org.cqp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author cqp
 * @version 1.0.0
 * @ClassName UserDao.java
 * @Description TODO
 * @createTime 2021年08月15日 15:29:00
 */
@Repository
public interface UserDao extends JpaRepository<User,Long> {
    User findUserByUsername(String username);
}
