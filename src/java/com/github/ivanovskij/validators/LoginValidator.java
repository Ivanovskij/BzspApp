/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.ivanovskij.validators;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("com.github.ivanovskij.validators.LoginValidator")
public class LoginValidator implements Validator {
    
    private final String pathMessages = "com.github.ivanovskij.nls.messages";

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object o) throws ValidatorException {
        ResourceBundle bundle = ResourceBundle.getBundle(pathMessages, 
                FacesContext.getCurrentInstance().getViewRoot().getLocale());
        
        try {
            String value = o.toString();
            
            if (value.length() < 6) {
                throw new IllegalArgumentException(bundle.getString("pass_length_error"));
            }
        } catch (IllegalArgumentException e) {
            FacesMessage message = new FacesMessage(e.getMessage());
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }
    
}
