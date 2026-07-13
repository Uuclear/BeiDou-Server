package org.gms;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;

/**
 * GMS服务器应用程序主入口类
 * <p>
 * 该类是Spring Boot应用程序的启动类，负责：
 * <ul>
 *   <li>自动初始化数据库（如果数据库不存在）</li>
 *   <li>启动Spring Boot应用上下文</li>
 *   <li>扫描MyBatis Mapper接口</li>
 * </ul>
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("org.gms.dao.mapper")
@Slf4j
public class ServerApplication {

    /**
     * 应用程序主入口方法
     * <p>
     * 执行流程：
     * <ol>
     *   <li>尝试自动初始化数据库</li>
     *   <li>如果数据库初始化失败，记录错误日志并退出</li>
     *   <li>启动Spring Boot应用程序</li>
     * </ol>
     * </p>
     *
     * @param args 命令行启动参数
     */
    public static void main(String[] args) {
        try {
            initDb(args);
        } catch (Exception e) {
            log.error("自动创建数据库失败：", e);
            return;
        }
        SpringApplication.run(ServerApplication.class, args);
    }

    /**
     * 初始化数据库
     * <p>
     * 修复PreDataSourceConfig优先级不够的问题，避免在创建数据库之前获取连接导致启动失败。
     * 由于FlywayAutoConfiguration、HibernateJpaAutoConfiguration等组件在启动时会获取数据库连接，
     * 如果数据库不存在会一直报错。因此在Spring自动配置之前手动解析yml配置文件来自动创建数据库。
     * </p>
     * <p>
     * 配置文件加载优先级：
     * <ol>
     *   <li>通过spring.config.location指定的外部配置文件</li>
     *   <li>jar包内置的application.yml</li>
     * </ol>
     * </p>
     * <p>
     * 数据源参数获取优先级：
     * <ol>
     *   <li>JVM系统属性</li>
     *   <li>命令行参数（--key=value格式）</li>
     *   <li>配置文件中的配置</li>
     * </ol>
     * </p>
     *
     * @param args 命令行启动参数，用于获取spring.config.location等配置
     * @throws Exception 数据库初始化过程中可能抛出的异常，包括文件读取异常、数据库连接异常等
     */
    private static void initDb(String[] args) throws Exception {
        InputStream resource = null;

        // 尝试从启动参数获取外部配置文件路径
        String location = getStartParam(args, "spring.config.location");
        if (location != null) {
            Path path = Path.of(location);
            if (!Files.exists(path)) {
                return;
            }
            resource = Files.newInputStream(path);
        }
        if (resource == null) {
            // 解析jar包自带的yml配置文件
            resource = ServerApplication.class.getClassLoader().getResourceAsStream("application.yml");
        }

        // 解析YAML配置文件
        Yaml yaml = new Yaml();
        LinkedHashMap<String, Object> property = yaml.load(resource);
        JSONObject mybatisFlex = JSONObject.parse(JSONObject.toJSONString(property.get("mybatis-flex")));
        JSONObject datasource = mybatisFlex.getJSONObject("datasource").getJSONObject("mysql");

        // 获取数据库驱动类名，优先使用启动参数
        String driver = getStartParam(args, "mybatis-flex.datasource.mysql.driver-class-name");
        if (driver == null) driver = datasource.getString("driver-class-name");

        // 获取数据库连接URL，优先使用启动参数
        String dbUrl = getStartParam(args, "mybatis-flex.datasource.mysql.url");
        if (dbUrl == null) dbUrl = datasource.getString("url");

        // 获取数据库用户名，优先使用启动参数
        String username = getStartParam(args, "mybatis-flex.datasource.mysql.username");
        if (username == null) username = datasource.getString("username");

        // 获取数据库密码，优先使用启动参数
        String password = getStartParam(args, "mybatis-flex.datasource.mysql.password");
        if (password == null) password = datasource.getString("password");

        // 从URL中解析数据库名称和前缀URL
        String urlPrefix = dbUrl.split("\\?")[0];
        String[] dbSplit = urlPrefix.split("/");
        String dbName = dbSplit[dbSplit.length - 1];
        String dbPrefix = urlPrefix.substring(0, urlPrefix.length() - dbName.length());

        // 连接到MySQL服务器（不指定数据库），检查并创建数据库
        try (Connection connection = getConnection(driver, dbPrefix + "mysql", username, password)) {
            // 检查数据库是否已存在
            PreparedStatement preparedStatement = connection.prepareStatement("SHOW DATABASES LIKE '" + dbName + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return;
            }
            resultSet.close();
            preparedStatement.close();

            // 创建数据库，使用utf8mb4字符集和utf8mb4_general_ci排序规则
            preparedStatement = connection.prepareStatement("CREATE DATABASE " + dbName + " DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
    }

    /**
     * 获取数据库连接
     * <p>
     * 根据指定的驱动类、连接URL、用户名和密码创建并返回数据库连接。
     * </p>
     *
     * @param driver   JDBC驱动类全限定名
     * @param url      数据库连接URL
     * @param username 数据库用户名
     * @param password 数据库密码
     * @return 数据库连接对象
     * @throws Exception 驱动加载失败或数据库连接失败时抛出异常
     */
    private static Connection getConnection(String driver, String url, String username, String password) throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * 获取启动参数值
     * <p>
     * 按以下优先级顺序查找参数：
     * <ol>
     *   <li>第一优先级：JVM系统属性（通过-Dkey=value设置）</li>
     *   <li>第二优先级：Spring Boot命令行参数（通过--key=value设置）</li>
     *   <li>第三优先级：环境变量（参数名中的点号替换为下划线）</li>
     * </ol>
     * </p>
     *
     * @param args      命令行参数数组
     * @param paramName 要查找的参数名称
     * @return 参数值，如果未找到则返回null
     */
    private static String getStartParam(String[] args, String paramName) {
        // 第一优先级：JVM系统属性
        String property = System.getProperty(paramName);
        if (property != null) {
            return property;
        }
        // 第二优先级：Spring Boot命令行参数
        for (String arg : args) {
            if (arg.startsWith("--" + paramName)) {
                return arg.split("=")[1];
            }
        }
        // 第三优先级：环境变量（点号替换为下划线）
        return System.getenv(paramName.replaceAll("\\.", "_"));
    }
}
