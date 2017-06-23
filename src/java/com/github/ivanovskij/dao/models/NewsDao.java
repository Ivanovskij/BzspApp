/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.ivanovskij.dao.models;

import com.github.ivanovskij.beans.News;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class NewsDao {

    private static final String CURRENT_DATE = "2017";

    @Resource(name = "jdbc/db_bzsp")
    private DataSource ds;
    private Connection conn;

    private List<News> newsList = new ArrayList<>();

    private String selectedDate = CURRENT_DATE;

    public NewsDao() {
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

            newsList.clear();
            while (rs.next()) {
                News news = new News();
                news.setIdNews(rs.getInt("idNews"));
                news.setName(rs.getString("name"));
                news.setDescr(rs.getString("descr"));
                news.setLang(rs.getString("lang"));
                news.setDate(rs.getString("date"));
                newsList.add(news);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
        return newsList;
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
    public List<News> getAllDates() {
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT DATE_FORMAT(news.date, '%Y') as dateFormat FROM news");

            newsList.clear();
            while (rs.next()) {
                News news = new News();
                news.setIdNews(0);
                news.setName(null);
                news.setDescr(null);
                news.setLang(null);
                news.setDate(rs.getString("dateFormat"));
                newsList.add(news);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(NewsDao.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return newsList;
    }

    // --------------------------------------------------------
    public List<News> getNewsByDate() {
        return selectExecute(
            "SELECT DISTINCT idNews, name, descr, Language_idLanguage as lang, date "
            + "FROM news " 
            + "where "
            + "Language_idLanguage = 1 and "
            + "DATE_FORMAT(news.date, '%Y') = '" + selectedDate + "'"
        );
            
    }
    
    // --------------------------------------------------------
    public void selectDate() {
        Map<String, String> param = 
                FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedDate = param.get("date");
        getNewsByDate();
    }
    
    // --------------------------------------------------------
    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

}
