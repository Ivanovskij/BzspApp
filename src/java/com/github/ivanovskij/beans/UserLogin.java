/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.ivanovskij.beans;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
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
public class UserLogin implements Serializable {
    
    private int id;
    private String name;
    private String password;
    private String phone;
    
    public UserLogin() {
    }
    
    public UserLogin(int id, String name, String password, String phone) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.phone = phone;
    }
    
    // ---------------------------------------------------------
    public String login() {
        try {
            HttpServletRequest request = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest());
            request.login(name, password);
            request.getSession().setAttribute("AccessInfo", "true");
            return "admin-index";
        } catch (ServletException se) {
            ResourceBundle bundle = ResourceBundle.getBundle("com.github.ivanovskij.nls.messages", 
                    FacesContext.getCurrentInstance().getViewRoot().getLocale());
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(bundle.getString("login_access_error"));
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage("form-login:username", message);
        }
        return "login";
    }  

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
