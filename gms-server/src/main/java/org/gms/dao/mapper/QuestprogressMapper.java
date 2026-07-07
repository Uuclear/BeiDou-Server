package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.QuestprogressDO;

/**
 * `questprogress` 表 / {@link org.gms.dao.entity.QuestprogressDO} 的 MyBatis Mapper 接口。
 * <p>
 * 任务进度表，存储任务进行中的计数器、收集进度等中间状态。
 */
public interface QuestprogressMapper extends BaseMapper<QuestprogressDO> {

}
