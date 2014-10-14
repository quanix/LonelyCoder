package org.lonelycoder.core.util;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.dialect.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Author : lihaoquan
 * Description :
 */
public class JpaDialect {

    /**
     * 获取数据库方言
     * @param dataSource
     * @return
     */
    public static String getDialect(DataSource dataSource) {
        String jdbcUrl = getJdbcUrl(dataSource);
        if (StringUtils.contains(jdbcUrl, ":h2:")) {
            return H2Dialect.class.getName();
        } else if (StringUtils.contains(jdbcUrl, ":mysql:")) {
            return MySQL5InnoDBDialect.class.getName();
        } else if (StringUtils.contains(jdbcUrl, ":oracle:")) {
            return Oracle10gDialect.class.getName();
        } else if (StringUtils.contains(jdbcUrl, ":postgresql:")) {
            return PostgreSQL82Dialect.class.getName();
        } else if (StringUtils.contains(jdbcUrl, ":sqlserver:")) {
            return SQLServer2008Dialect.class.getName();
        } else {
            throw new IllegalArgumentException("Unknown Database of " + jdbcUrl);
        }
    }


    /**
     * 获取JDBC URL
     * @param dataSource
     * @return
     */
    public static String getJdbcUrl(DataSource dataSource) {
        String jdbcURL = "";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            if(connection == null) {
                throw new IllegalStateException("Connection returned by DataSource [" + dataSource + "] was null");
            }
            return connection.getMetaData().getURL();
        }catch (Exception e) {
            throw new RuntimeException("Could not get database url", e);
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
