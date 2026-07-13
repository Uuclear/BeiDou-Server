package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.gms.dao.entity.CharactersDO;

import java.util.List;

/**
 * 游戏角色数据访问Mapper接口，对应数据库表 characters。
 * 继承MyBatis-Flex的BaseMapper获得基础CRUD能力。
 *
 * @author sleep
 * @since 2024-05-24
 */
public interface CharactersMapper extends BaseMapper<CharactersDO> {
    /**
     * 更新游戏角色数据
     */
    @Update("UPDATE characters SET HasMerchant = #{value}")
    void updateAllHasMerchant(Integer value);

    /**
     * 查询游戏角色数据
     */
    @Select("SELECT id, world FROM characters WHERE accountid = #{accountId}")
    List<CharactersDO> selectIdAndWorldListByAccountId(int accountId);
}
