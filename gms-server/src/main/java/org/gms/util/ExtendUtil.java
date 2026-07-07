package org.gms.util;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.ExtendValueDO;
import org.gms.dao.mapper.ExtendValueMapper;
import org.gms.manager.ServerManager;

import java.sql.Date;

import static org.gms.dao.entity.table.ExtendValueDOTableDef.EXTEND_VALUE_D_O;

/**
 * 扩展属性（extend_value 表）读写工具，按扩展 ID、类型、名称查询或保存键值对。
 */
public class ExtendUtil {
    private static final ExtendValueMapper extendValueMapper = ServerManager.getApplicationContext().getBean(ExtendValueMapper.class);

    /**
     * 按三元组键查询扩展属性记录。
     *
     * @param extendId   扩展实体 ID
     * @param extendType 扩展类型
     * @param extendName 扩展字段名
     * @return 匹配的记录，不存在时返回 {@code null}
     */
    public static ExtendValueDO getExtendValue(String extendId, String extendType, String extendName) {
        return extendValueMapper.selectOneByQuery(QueryWrapper.create()
                .where(EXTEND_VALUE_D_O.EXTEND_ID.eq(extendId))
                .and(EXTEND_VALUE_D_O.EXTEND_TYPE.eq(extendType))
                .and(EXTEND_VALUE_D_O.EXTEND_NAME.eq(extendName)));

    }

    /**
     * 保存或更新扩展属性值；记录不存在时插入，存在时更新值与更新时间。
     *
     * @param extendId    扩展实体 ID
     * @param extendType  扩展类型
     * @param extendName  扩展字段名
     * @param extendValue 扩展字段值
     */
    public static void saveOrUpdateExtendValue(String extendId, String extendType, String extendName, String extendValue) {
        ExtendValueDO extendValueDO = getExtendValue(extendId, extendType, extendName);
        if (extendValueDO == null) {
            extendValueMapper.insertSelective(ExtendValueDO.builder()
                    .extendId(extendId)
                    .extendType(extendType)
                    .extendName(extendName)
                    .extendValue(extendValue)
                    .createTime(new Date(System.currentTimeMillis()))
                    .build());
        } else {
            extendValueDO.setCreateTime(null);
            extendValueDO.setUpdateTime(new Date(System.currentTimeMillis()));
            extendValueDO.setExtendValue(extendValue);
            extendValueMapper.update(extendValueDO);
        }
    }
}
