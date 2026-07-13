package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.gms.dao.entity.AccountsDO;

/**
 * 账号信息数据访问Mapper接口，对应数据库表 accounts。
 * 继承MyBatis-Flex的BaseMapper获得基础CRUD能力。
 *
 * @author sleep
 * @since 2024-05-24
 */
public interface AccountsMapper extends BaseMapper<AccountsDO> {
    /**
     * 更新账号信息数据
     */
    @Update("UPDATE accounts SET loggedin = #{value}")
    void updateAllLoggedIn(Integer value);
    
    /**
     * 查询账号信息数据
     */
    @Select("SELECT * FROM accounts WHERE name = #{name}")
    AccountsDO selectOneByName(String name);
    
    /**
     * 新增账号信息数据
     */
    @Insert("INSERT INTO accounts(name, password, birthday, tempban, language) VALUES (#{name}, #{password}, #{birthday}, #{tempban}, #{language})")
    void addAccount(AccountsDO accountsDO);
}
