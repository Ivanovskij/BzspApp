/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.ivanovskij.beans;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

@ManagedBean
@SessionScoped
public class Production implements Serializable {

    private int idProducts;
    private String name;
    private String descr;
    private String image;
    private double cost;
    private String delete;
    private String typeProduct;
    private String lang;

    // ---
    @Resource(name = "jdbc/db_bzsp")
    private DataSource ds;
    private Connection conn;

    private List<Production> productionList = new ArrayList<>();

    private String currentTypePr = "";
    private String searchText = "";

    private Production curProdForUpdate;

    public Production() {
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
    public List<Production> getProductByType() {
        return selectExecute(
                "SELECT idProducts, p.name, descr, image, cost, "
                + "isDelete, TypeProducts_idTypeProducts as typePr "
                + "FROM products as p, language, typeproducts "
                + "where isDelete = 'N' and "
                + "language.idLanguage = " + getCurrentLocale() + " and "
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
                    + "typeproducts.Language_idLanguage = " + getCurrentLocale() + ""
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
    public List<Production> getAllProductions() {
        return selectExecute(
                "SELECT idProducts, p.name, descr, image, cost, "
                + "isDelete, TypeProducts_idTypeProducts as typePr "
                + "FROM products as p, language, typeproducts "
                + "where "
                + "language.idLanguage = p.Language_idLanguage and "
                + "typeproducts.idTypeProducts = p.TypeProducts_idTypeProducts "
                + "order by idProducts"
        );
    }

    // --------------------------------------------------------
    public void searchProduct() {
        if (searchText == null
                || searchText.trim().length() == 0) {
            getAllProductions();
            return;
        }

        selectExecute(
                "SELECT idProducts, p.name, descr, image, cost, "
                + "isDelete, TypeProducts_idTypeProducts as typePr "
                + "FROM products as p, language, typeproducts "
                + "where "
                + "language.idLanguage = p.Language_idLanguage and "
                + "typeproducts.idTypeProducts = p.TypeProducts_idTypeProducts and "
                + "lower(p.name) like '%" + searchText.toLowerCase() + "%'"
        );
    }

    // --------------------------------------------------------
    public void selectTypePr() {
        Map<String, String> param
                = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        currentTypePr = (String) param.get("typePr");
        getProductByType();
    }

    // ========================== CRUD
    // -------------- CREATE -----------------
    public void createProduct() {
        String query = "INSERT INTO products (idProducts, name, descr, "
                + "image, isDelete, cost, TypeProducts_idTypeProducts, Language_idLanguage) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setInt(1, idProducts);
            pstmt.setString(2, name);
            pstmt.setString(3, descr);
            pstmt.setString(4, image);
            pstmt.setString(5, delete);
            pstmt.setDouble(6, cost);
            pstmt.setString(7, typeProduct);
            pstmt.setString(8, lang);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("add good");
                getAllProductions();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    // -------------- DELETE -----------------
    public void deleteProduct() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest req = (HttpServletRequest) fc.getExternalContext().getRequest();

        String idStr = req.getParameter("id");
        try {
            if (idStr != null) {
                int id = Integer.parseInt(req.getParameter("id"));

                String query = "UPDATE products SET isDelete = 'Y' "
                        + "WHERE idProducts = " + id;
                PreparedStatement pstmt = getConnection().prepareStatement(query);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    System.out.println("delete good");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    // -------------- UPDATE -----------------
    public void getProductById() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest req = (HttpServletRequest) fc.getExternalContext().getRequest();

        String idStr = req.getParameter("id");

        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            for (Production p : productionList) {
                if (p.getIdProducts() == id) {
                    setIdProducts(p.getIdProducts());
                    setName(p.getName());
                    setDescr(p.getDescr());
                    setCost(p.getCost());
                    setImage(p.getImage());
                    setTypeProduct(p.getTypeProduct());
                    setLang(p.getLang());
                    setDelete(p.getDelete());
                }
            }
        }

    }

    public void updateProduct() {
        String query = "UPDATE products SET name=?, descr=?, "
                + "image=?, isDelete=?, cost=?, "
                + "TypeProducts_idTypeProducts=?, "
                + "Language_idLanguage=? "
                + "WHERE idProducts=?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            getProductById();
            pstmt.setString(1, name);
            pstmt.setString(2, descr);
            pstmt.setString(3, image);
            pstmt.setString(4, delete);
            pstmt.setDouble(5, cost);
            pstmt.setInt(6, Integer.parseInt(typeProduct));
            pstmt.setInt(7, getCurrentLocale());
            pstmt.setInt(8, idProducts);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("update good");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    // ---------------------------------------------------
    public int getIdProducts() {
        return idProducts;
    }

    public void setIdProducts(int idProducts) {
        this.idProducts = idProducts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String imagePath) {
        this.image = imagePath;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public String getTypeProduct() {
        return typeProduct;
    }

    public void setTypeProduct(String typeProduct) {
        this.typeProduct = typeProduct;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    // -------------------------------------------------------
    public String getCurrentTypePr() {
        return currentTypePr;
    }

    public void setCurrentTypePr(String currentTypePr) {
        this.currentTypePr = currentTypePr;
    }

    public List<Production> getProductionList() {
        return productionList;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    // -------------------------------------------------------
    // 1 - русская локаль
    // 2 - английская локаль
    // 1 и 2 это в БД idLang
    private int getCurrentLocale() {
        Locale current = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        if (current.toString().equals("ru")) {
            return 1;
        } else {
            return 2;
        }
    }

    public Production getCurProdForUpdate() {
        return curProdForUpdate;
    }

    public void setCurProdForUpdate(Production curProdForUpdate) {
        this.curProdForUpdate = curProdForUpdate;
    }

    @Override
    public String toString() {
        return "Production{" + "idProducts=" + idProducts
                + ", name=" + name + ", descr=" + descr + ", image=" + image
                + ", cost=" + cost + ", delete=" + delete + ", typeProduct="
                + typeProduct + ", lang=" + lang + '}';
    }
}
