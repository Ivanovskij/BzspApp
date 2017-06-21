/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.ivanovskij.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author IOAdmin
 */
public class AccessCheckerFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;        
        HttpServletResponse res = (HttpServletResponse) response;        
        String contextPath = req.getContextPath();
        HttpSession session = req.getSession();
        
        if((session!=null) && (session.getAttribute("AccessInfo")!=null)) {
            chain.doFilter(request, response);    
        } else {    
            if(req.getRequestURI().equals(contextPath+"/")||
               req.getRequestURI().equals(contextPath+"/faces/login.xhtml")
              ) {                
                chain.doFilter(request, response);            // страницы доступные без авторизации
            } else {    
                res.sendRedirect(contextPath + "/faces/login.xhtml");    // для остальных редиректим на логин
            }    
        }
    }

    @Override
    public void destroy() {
    }
    
}
