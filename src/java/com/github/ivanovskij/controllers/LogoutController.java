/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.ivanovskij.controllers;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author IOAdmin
 */
@ManagedBean
@SessionScoped
public class LogoutController implements Serializable {

    /**
     * Creates a new instance of LogoutController
     */
    public LogoutController() {
    }
    
    public String logout() {
        try {
            HttpServletRequest request = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest());
            if (request.getSession() != null) {
                request.logout();
                FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
                request.getSession().setAttribute("AccessInfo", null);
            }        
        } catch (ServletException se) {
            // NOP
        }
        return "logout";
    }
    
}
