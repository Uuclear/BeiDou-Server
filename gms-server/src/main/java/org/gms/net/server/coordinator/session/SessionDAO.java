package org.gms.net.server.coordinator.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * HWID 账号关联数据访问对象，负责 hwidaccounts 表的增删改查。
 */
public class SessionDAO {
    private static final Logger log = LoggerFactory.getLogger(SessionDAO.class);

    /** 删除数据库中已过期的 HWID 账号关联记录。 */
    public static void deleteExpiredHwidAccounts() {
        final String query = "DELETE FROM hwidaccounts WHERE expiresat < CURRENT_TIMESTAMP";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            log.warn("Failed to delete expired hwidaccounts", e);
        }
    }

    /**
     * 查询指定账号关联的所有 HWID。
     *
     * @param con       数据库连接
     * @param accountId 账号 ID
     * @return HWID 列表
     * @throws SQLException 数据库异常
     */
    public static List<Hwid> getHwidsForAccount(Connection con, int accountId) throws SQLException {
        final List<Hwid> hwids = new ArrayList<>();

        final String query = "SELECT hwid FROM hwidaccounts WHERE accountid = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hwids.add(new Hwid(rs.getString("hwid")));
                }
            }
        }

        return hwids;
    }

    /**
     * 注册账号与 HWID 的访问关联及过期时间。
     *
     * @param con       数据库连接
     * @param accountId 账号 ID
     * @param hwid      硬件标识，不可为 null
     * @param expiry    过期时间
     * @throws SQLException 数据库异常
     */
    public static void registerAccountAccess(Connection con, int accountId, Hwid hwid, Instant expiry)
            throws SQLException {
        if (hwid == null) {
            throw new IllegalArgumentException("Hwid must not be null");
        }

        final String query = "INSERT INTO hwidaccounts (accountid, hwid, expiresat) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, accountId);
            ps.setString(2, hwid.hwid());
            ps.setTimestamp(3, Timestamp.from(expiry));

            ps.executeUpdate();
        }
    }

    /**
     * 查询指定账号下各 HWID 的相关度记录。
     *
     * @param con       数据库连接
     * @param accountId 账号 ID
     * @return HWID 相关度列表
     * @throws SQLException 数据库异常
     */
    public static List<HwidRelevance> getHwidRelevance(Connection con, int accountId) throws SQLException {
        final List<HwidRelevance> hwidRelevances = new ArrayList<>();

        final String query = "SELECT * FROM hwidaccounts WHERE accountid = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String hwid = rs.getString("hwid");
                    int relevance = rs.getInt("relevance");
                    hwidRelevances.add(new HwidRelevance(hwid, relevance));
                }
            }
        }

        return hwidRelevances;
    }

    /**
     * 更新账号与 HWID 关联的相关度与过期时间。
     *
     * @param con            数据库连接
     * @param hwid           硬件标识
     * @param accountId      账号 ID
     * @param expiry         新过期时间
     * @param loginRelevance 登录相关度
     * @throws SQLException 数据库异常
     */
    public static void updateAccountAccess(Connection con, Hwid hwid, int accountId, Instant expiry, int loginRelevance)
            throws SQLException {
        final String query = "UPDATE hwidaccounts SET relevance = ?, expiresat = ? WHERE accountid = ? AND hwid LIKE ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, loginRelevance);
            ps.setTimestamp(2, Timestamp.from(expiry));
            ps.setInt(3, accountId);
            ps.setString(4, hwid.hwid());

            ps.executeUpdate();
        }
    }
}
