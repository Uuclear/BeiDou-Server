package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.PetsDO;

/**
 * 宠物数据访问Mapper接口，对应数据库表 pets。
 * 继承MyBatis-Flex的BaseMapper获得基础CRUD能力。
 *
 * @author sleep
 * @since 2024-05-24
 */
public interface PetsMapper extends BaseMapper<PetsDO> {

}
