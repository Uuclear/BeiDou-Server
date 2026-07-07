package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.client.MonsterBook;
import org.gms.dao.entity.MonsterbookDO;
import org.gms.dao.mapper.MonsterbookMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.gms.dao.entity.table.MonsterbookDOTableDef.MONSTERBOOK_D_O;

/**
 * 怪物收藏册业务服务，管理怪物图鉴条目与玩家收集进度。
 */
@Service
@AllArgsConstructor
public class MonsterBookService {
    private final MonsterbookMapper monsterbookMapper;

    /**
     * 执行 getByCharacterId 相关业务逻辑。
     *
     * @param cid cid
     * @return List<MonsterbookDO> 类型结果
     */
    public List<MonsterbookDO> getByCharacterId(int cid) {
        return monsterbookMapper.selectListByQuery(QueryWrapper.create().where(MONSTERBOOK_D_O.CHARID.eq(cid)).orderBy(MONSTERBOOK_D_O.CHARID, true));
    }
}
