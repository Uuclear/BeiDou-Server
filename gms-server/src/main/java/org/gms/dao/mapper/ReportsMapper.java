package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.ReportsDO;

/**
 * `reports` 表 / {@link org.gms.dao.entity.ReportsDO} 的 MyBatis Mapper 接口。
 * <p>
 * 玩家举报记录表，保存被举报者、举报原因及处理状态。
 */
public interface ReportsMapper extends BaseMapper<ReportsDO> {

}
