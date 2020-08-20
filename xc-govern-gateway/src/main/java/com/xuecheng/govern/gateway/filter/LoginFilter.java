package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.protocol.RequestContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/18 16:36
 */
@Component
public class LoginFilter extends ZuulFilter {
    @Autowired
    AuthService authService;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        HttpServletResponse response = requestContext.getResponse();
        String tokenFormCookie = authService.getTokenFormCookie(request);
        if(StringUtils.isEmpty(tokenFormCookie)){
            //拒绝访问
            access_denied();
            return null;
        }
        String jwtFormHeards = authService.getJwtFormHeards(request);
        if(StringUtils.isEmpty(jwtFormHeards)){
            access_denied();
            return null;
        }
        long expire = authService.getExpire(tokenFormCookie);
        if(expire<0){
            access_denied();
            return null;
        }
        return null;
    }

    //拒绝访问
    private void access_denied(){
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        requestContext.setSendZuulResponse(false);
        requestContext.setResponseStatusCode(200);
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        String jsonString = JSON.toJSONString(responseResult);
        requestContext.setResponseBody(jsonString);
        response.setContentType("application/json;charset=utf-8");
    }
}
