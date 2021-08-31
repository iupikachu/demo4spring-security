package com.cqp.demo4springsecurity.org.cqp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;
import java.io.PrintWriter;

/**
 * @author cqp
 * @version 1.0.0
 * @ClassName SecurityConfigWithDB.java
 * @Description JDBC UserDetailsManager 把数据存到数据库进行认证授权
 *              表的结构无法自己定义 需要使用(全局搜索) users.ddl 手动去数据库里建表
 *              不够灵活 spring-security自带的
 * @createTime 2021年08月15日 13:30:00
 */

//@Configuration
public class SecurityConfigWithDB extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    // 过期的解决密码明文问题方法
    @Bean
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        if(!manager.userExists("cqp")){
            manager.createUser(User.withUsername("cqp").password("123").roles("admin").build());
        }
        if(!manager.userExists("iu")){
            manager.createUser(User.withUsername("iu").password("123").roles("user").build());
        }
        return manager;
    }

    // 放行静态资源
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**","/css/**","/images/**");
    }

    // 角色继承  admin 拥有 user 权限
    @Bean
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return hierarchy;
    }

    // HttpSecurity 配置 (前后端分离)
    // json 格式交互
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/doLogin")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler((req,resp,authentication) -> {
                    resp.setContentType("application/json; charset=UTF-8");
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(authentication.getPrincipal()));  // 登录成功的回调 http://localhost:8080/doLogin?name=cqp&password=123
                    out.flush();
                    out.close();
                })
                .failureHandler((req,resp,exception) -> {
                    resp.setContentType("application/json; charset=UTF-8");
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(exception.getMessage()));  // 登录失败的回调 http://localhost:8080/doLogin?name=ppp&password=123
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout") // 默认注销地址是 logout
                .logoutSuccessHandler((req,resp,authentication) -> {
                    resp.setContentType("application/json; charset=UTF-8");
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString("注销登录成功！"));
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint((req,resp,exception) -> {
                    resp.setContentType("application/json; charset=UTF-8");
                    PrintWriter out = resp.getWriter();
                    resp.setStatus(401);  // 401: Unauthorized
                    out.write(new ObjectMapper().writeValueAsString("尚未登录，请登录！"));  // 登录失败的回调 http://localhost:8080/doLogin?name=ppp&password=123
                    out.flush();
                    out.close();
                });

    }
}
