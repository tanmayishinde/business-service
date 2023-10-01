package com.root.businessservice.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionUtil {
    @Autowired
    private HttpServletRequest httpServletRequest;

    public String getSessionId(){
        Cookie sessionCookie = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        for(Cookie cookie : cookies){
            if("session-id".equals(cookie.getName())){
                sessionCookie = cookie;
            }
        }
        return (sessionCookie != null) ? sessionCookie.getValue() : null;
    }
}
