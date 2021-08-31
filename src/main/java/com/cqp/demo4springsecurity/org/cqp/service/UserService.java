package com.cqp.demo4springsecurity.org.cqp.service;

import com.cqp.demo4springsecurity.org.cqp.dao.UserDao;
import com.cqp.demo4springsecurity.org.cqp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author cqp
 * @version 1.0.0
 * @ClassName UserService.java
 * @Description TODO
 * @createTime 2021年08月15日 15:32:00
 */

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findUserByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        return user;
    }
}
