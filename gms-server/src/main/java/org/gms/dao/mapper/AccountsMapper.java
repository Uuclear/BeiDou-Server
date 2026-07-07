package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.gms.dao.entity.AccountsDO;

/**
 * `accounts` 表 / {@link org.gms.dao.entity.AccountsDO} 的 MyBatis Mapper 接口。
 * <p>
 * 游戏账号表，存储登录名、密码哈希、封禁状态、NX点数、角色栏位及账号级元数据。
 */
public interface AccountsMapper extends BaseMapper<AccountsDO> {
    /**
     * 批量更新所有账号的 loggedin 登录状态标志。
     */
    @Update("UPDATE accounts SET loggedin = #{value}")
    void updateAllLoggedIn(Integer value);
    
    /**
     * 按登录名查询唯一账号记录。
     */
    @Select("SELECT * FROM accounts WHERE name = #{name}")
    AccountsDO selectOneByName(String name);
    
    /**
     * 插入新账号记录（仅写入基础注册字段）。
     */
    @Insert("INSERT INTO accounts(name, password, birthday, tempban, language) VALUES (#{name}, #{password}, #{birthday}, #{tempban}, #{language})")
    void addAccount(AccountsDO accountsDO);
}
