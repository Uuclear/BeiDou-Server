package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.gms.dao.entity.CharactersDO;

import java.util.List;

/**
 * `characters` 表 / {@link org.gms.dao.entity.CharactersDO} 的 MyBatis Mapper 接口。
 * <p>
 * 角色主表，持久化等级、属性、地图位置、公会、人气、背包栏位等角色核心状态。
 */
public interface CharactersMapper extends BaseMapper<CharactersDO> {
    /**
     * 批量更新所有角色的 HasMerchant 雇佣商人标志。
     */
    @Update("UPDATE characters SET HasMerchant = #{value}")
    void updateAllHasMerchant(Integer value);

    /**
     * 按账号 ID 查询其下全部角色的 ID 与世界编号。
     */
    @Select("SELECT id, world FROM characters WHERE accountid = #{accountId}")
    List<CharactersDO> selectIdAndWorldListByAccountId(int accountId);
}
