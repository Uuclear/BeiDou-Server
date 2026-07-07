package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.gms.dao.entity.ExtendValueDO;

import java.sql.Date;
import java.util.List;

/**
 * `extend_value` 表 / {@link org.gms.dao.entity.ExtendValueDO} 的 MyBatis Mapper 接口。
 * <p>
 * 扩展键值存储表，以类型+键的方式保存各类业务的附加字段。
 */
public interface ExtendValueMapper extends BaseMapper<ExtendValueDO> {
    /**
     * 按扩展类型清理指定创建时间之前的过期扩展键值记录。
     */
    @Delete("delete from extend_value where extend_type = #{extendType} and create_time < #{createTime}")
    void clean(@Param("extendType") String extendType, @Param("createTime") Date createTime);
}
