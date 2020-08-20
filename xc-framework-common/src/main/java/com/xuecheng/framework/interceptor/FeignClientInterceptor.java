package com.xuecheng.framework.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/19 14:50
 */
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null){
            HttpServletRequest request = requestAttributes.getRequest();
            //取出header，找到jwt令牌
            Enumeration<String> headerNames = request.getHeaderNames();
            if(headerNames != null){
                while (headerNames.hasMoreElements()){
                    String headerName = headerNames.nextElement();
                    String headerValue = request.getHeader(headerName);
                    //将jwt令牌向下传
                    requestTemplate.header(headerName,headerValue);
                }
            }
        }

    }
}
