/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.ivanovskij.dao.models;

import com.github.ivanovskij.beans.Production;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@ManagedBean(eager = true)
@ApplicationScoped
public class ProductionDao {
    
    @Resource(name = "jdbc/db_bzsp")
    private DataSource ds;
    private Connection conn;

    private List<Production> productionList = new ArrayList<>();
    
    private String currentTypePr = "";


    public ProductionDao() {
        try {
            Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/db_bzsp");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    
    // --------------------------------------------------------
    private Connection getConnection() throws SQLException {
        if (ds == null) {
            throw new SQLException("No data source");
        }
        conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("No connection");
        }
        return conn;
    }
    
    // --------------------------------------------------------
    public List selectExecute(String query) {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);

            productionList.clear();
            while (rs.next()) {
                Production production = new Production();
                production.setIdProducts(rs.getInt("idProducts"));
                production.setName(rs.getString("name"));
                production.setDescr(rs.getString("descr"));
                production.setImage(rs.getString("image"));
                production.setDelete(rs.getString("isDelete"));
                production.setCost(rs.getDouble("cost"));
                production.setTypeProduct(rs.getString("typePr"));
                production.setLang(rs.getString("typePr"));
                productionList.add(production);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
        return productionList;
    }

    // --------------------------------------------------------
    private void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    // --------------------------------------------------------
    public List<Production> getProductByType() {
        return selectExecute(
            "SELECT idProducts, p.name, descr, image, cost, " 
            + "isDelete, TypeProducts_idTypeProducts as typePr "
            + "FROM products as p, language, typeproducts "
            + "where isDelete = 'N' and "
            + "language.idLanguage = 1 and "
            + "language.idLanguage = p.Language_idLanguage and "        
            + "typeproducts.idTypeProducts = p.TypeProducts_idTypeProducts and "
            + "typeproducts.name = '" + currentTypePr + "'"
        );   
    }
    
    // --------------------------------------------------------
    public List<Production> getAllTypeProducts() {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(
                    "select distinct typeproducts.name as typePr from products, typeproducts "
                    + "where products.TypeProducts_idTypeProducts = typeproducts.idTypeProducts and "
                    + "typeproducts.Language_idLanguage = 1"
            );

            productionList.clear();
            while (rs.next()) {
                Production production = new Production();
                production.setTypeProduct(rs.getString("typePr"));
                productionList.add(production);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
        return productionList;
    }
    
    // --------------------------------------------------------
    public void selectTypePr() {
        Map<String, String> param = 
                FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        currentTypePr = (String)param.get("typePr");
        getProductByType();
    }

    // -------------------------------------------------------
    public String getCurrentTypePr() {
        return currentTypePr;
    }

    public void setCurrentTypePr(String currentTypePr) {
        this.currentTypePr = currentTypePr;
    }
}
