package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/17 20:37
 */
public interface XcUserRepository extends JpaRepository<XcUser,String> {
    XcUser findByUsername(String username);
}
