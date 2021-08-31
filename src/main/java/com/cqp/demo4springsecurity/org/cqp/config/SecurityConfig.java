package com.cqp.demo4springsecurity.org.cqp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.PrintWriter;

/**
 * @author cqp
 * @version 1.0.0
 * @ClassName SecurityConfig.java
 * @Description TODO
 * @createTime 2021年07月29日 00:21:00
 */

//@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {



    // 过期的解决密码明文问题方法
    @Bean
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    // 重载基于内存的验证 在java代码里直接写账户密码权限
    // sring 5 之后不支持 明文密码
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("cqp").password("123").roles("admin")
                .and()
                .withUser("iu").password("123").roles("user");
    }

    // HttpSecurity 配置
    // 使用默认登录界面
    /*
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasAnyRole("admin","user")
                .anyRequest().authenticated()     // 其余端口只需要登录就行
                .and()
                .formLogin()
                .loginProcessingUrl("/doLogin")  // 表单登录的url
                .permitAll()       // 和登录相关的接口全部允许
                .and()
                .csrf().disable();   // 为了postman测试，所以不开启csrf防护
    }
    */

    // HttpSecurity 配置 (前后端不分离)
    // 自定义页面登录配置
    /*
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")  // 不只是设置了登录页面 也设置了登录接口/login.html (get请求页面 post传递参数) 默认参数为 username password form表单中的 <input>name 需要与参数统一
                //.successForwardUrl("/success") // 服务端跳转
                .defaultSuccessUrl("/success") // 客户端重定向
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout") // 默认注销地址是 logout
                //.logoutRequestMatcher(new AntPathRequestMatcher("/logout","POST")) // 默认注销是get请求，改为post请求
                .logoutSuccessUrl("/login.html")
                .permitAll()
                .and()
                .csrf().disable();

    }*/

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
