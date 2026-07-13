package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.gms.dao.entity.ExtendValueDO;

import java.sql.Date;
import java.util.List;

/**
 * 扩展字段表 映射层。
 *
 * @author CN
 * @since 2024-07-08
 */
/**
 * extendvalue数据访问Mapper接口，对应数据库表 extendvalue。
 * 继承MyBatis-Flex的BaseMapper获得基础CRUD能力。
 *
 * @author sleep
 * @since 2024-05-24
 */
public interface ExtendValueMapper extends BaseMapper<ExtendValueDO> {
    /**
     * 删除extendvalue数据
     */
    @Delete("delete from extend_value where extend_type = #{extendType} and create_time < #{createTime}")
    void clean(@Param("extendType") String extendType, @Param("createTime") Date createTime);
}
