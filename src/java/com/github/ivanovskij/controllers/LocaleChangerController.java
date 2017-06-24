/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.ivanovskij.controllers;

import java.io.Serializable;
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author IOAdmin
 */
@ManagedBean(name="localeChanger")
@SessionScoped
public class LocaleChangerController implements Serializable {

     private Locale currentLocale = new Locale("ru");
    
    public LocaleChangerController() {
        if (FacesContext.getCurrentInstance().isReleased()) {
            currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        }
    }

    // --------------------------------------------------
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(String lang) {
        this.currentLocale = new Locale(lang);
    }
    
}
