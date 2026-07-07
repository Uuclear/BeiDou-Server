package org.gms.util;

import org.gms.manager.ServerManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接获取工具，从 Spring 容器中的 {@link DataSource} 取得 JDBC 连接。
 *
 * @author Frz (Big Daddy)
 * @author The Real Spookster - some modifications to this beautiful code
 * @author Ronan - some connection pool to this beautiful code
 */
public class DatabaseConnection {

    /**
     * 从应用数据源获取一条 JDBC 连接。
     *
     * @return 数据库连接
     * @throws SQLException 获取连接失败时抛出
     */
    public static Connection getConnection() throws SQLException {
        return ServerManager.getApplicationContext().getBean(DataSource.class).getConnection();
    }
}
