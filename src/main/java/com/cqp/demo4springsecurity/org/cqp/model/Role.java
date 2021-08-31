package com.cqp.demo4springsecurity.org.cqp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author cqp
 * @version 1.0.0
 * @ClassName Role.java
 * @Description 角色
 * @createTime 2021年08月15日 14:15:00
 */

// 自动创建表
@Entity(name = "t_role")
public class Role {
    private Long id;
    private String name;
    private String nameZh;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }
}
