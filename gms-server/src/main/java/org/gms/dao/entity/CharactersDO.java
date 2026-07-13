package org.gms.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 游戏角色实体类，对应数据库表 characters。
 * 存储游戏角色详细属性。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("characters")
public class CharactersDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 唯一ID
     */
    private Integer id;

    /**
     * 账号ID
     */
    private Integer accountid;

    /**
     * 服务器世界ID
     */
    private Integer world;

    /**
     * 名称
     */
    private String name;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 经验值
     */
    private Integer exp;

    /**
     * 转蛋经验值
     */
    private Integer gachaexp;

    @Column("str")
    /**
     * 力量属性STR
     */
    private Integer attrStr;

    @Column("dex")
    /**
     * 敏捷属性DEX
     */
    private Integer attrDex;

    @Column("luk")
    /**
     * 运气属性LUK
     */
    private Integer attrLuk;

    @Column("int")
    /**
     * 智力属性INT
     */
    private Integer attrInt;

    /**
     * 当前HP
     */
    private Integer hp;

    /**
     * 当前MP
     */
    private Integer mp;

    /**
     * 最大HP
     */
    private Integer maxhp;

    /**
     * 最大MP
     */
    private Integer maxmp;

    /**
     * 金币数量
     */
    private Integer meso;

    @Column("hpMpUsed")
    /**
     * HP/MP已使用量
     */
    private Integer hpMpUsed;

    /**
     * 职业ID
     */
    private Integer job;

    /**
     * 皮肤颜色
     */
    private Integer skincolor;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 人气值
     */
    private Integer fame;

    /**
     * fquest
     */
    private Integer fquest;

    /**
     * 发型ID
     */
    private Integer hair;

    /**
     * 脸型ID
     */
    private Integer face;

    /**
     * 能力点AP
     */
    private Integer ap;

    /**
     * 技能点SP分配
     */
    private String sp;

    /**
     * 当前地图ID
     */
    private Integer map;

    /**
     * 出生点ID
     */
    private Integer spawnpoint;

    /**
     * GM权限等级
     */
    private Integer gm;

    /**
     * 组队ID
     */
    private Integer party;

    @Column("buddyCapacity")
    /**
     * 好友容量上限
     */
    private Integer buddyCapacity;

    /**
     * 角色创建时间
     */
    private Timestamp createdate;

    /**
     * 世界排名
     */
    private Integer rank;

    @Column("rankMove")
    /**
     * 排名变动值
     */
    private Integer rankMove;

    @Column("jobRank")
    /**
     * 职业排名
     */
    private Integer jobRank;

    @Column("jobRankMove")
    /**
     * 职业排名变动值
     */
    private Integer jobRankMove;

    /**
     * 家族ID
     */
    private Integer guildid;

    /**
     * 家族职位等级
     */
    private Integer guildrank;

    /**
     * 聊天群ID
     */
    private Integer messengerid;

    /**
     * 聊天群职位
     */
    private Integer messengerposition;

    /**
     * 坐骑等级
     */
    private Integer mountlevel;

    /**
     * 坐骑经验值
     */
    private Integer mountexp;

    /**
     * 坐骑疲劳度
     */
    private Integer mounttiredness;

    /**
     * omokwins
     */
    private Integer omokwins;

    /**
     * omoklosses
     */
    private Integer omoklosses;

    /**
     * omokties
     */
    private Integer omokties;

    /**
     * matchcardwins
     */
    private Integer matchcardwins;

    /**
     * matchcardlosses
     */
    private Integer matchcardlosses;

    /**
     * matchcardties
     */
    private Integer matchcardties;

    /**
     * merchantmesos
     */
    private Integer merchantmesos;

    /**
     * hasmerchant
     */
    private Boolean hasmerchant;

    /**
     * equipslots
     */
    private Integer equipslots;

    /**
     * useslots
     */
    private Integer useslots;

    /**
     * setupslots
     */
    private Integer setupslots;

    /**
     * etcslots
     */
    private Integer etcslots;

    @Column("familyId")
    /**
     * familyId
     */
    private Integer familyId;

    /**
     * monsterbookcover
     */
    private Integer monsterbookcover;

    @Column("allianceRank")
    /**
     * allianceRank
     */
    private Integer allianceRank;

    @Column("vanquisherStage")
    /**
     * vanquisherStage
     */
    private Integer vanquisherStage;

    @Column("ariantPoints")
    /**
     * ariantPoints
     */
    private Integer ariantPoints;

    @Column("dojoPoints")
    /**
     * dojoPoints
     */
    private Integer dojoPoints;

    @Column("lastDojoStage")
    /**
     * lastDojoStage
     */
    private Integer lastDojoStage;

    @Column("finishedDojoTutorial")
    /**
     * finishedDojoTutorial
     */
    private Integer finishedDojoTutorial;

    @Column("vanquisherKills")
    /**
     * vanquisherKills
     */
    private Integer vanquisherKills;

    @Column("summonValue")
    /**
     * summonValue
     */
    private Long summonValue;

    @Column("partnerId")
    /**
     * partnerId
     */
    private Integer partnerId;

    @Column("marriageItemId")
    /**
     * marriageItemId
     */
    private Integer marriageItemId;

    /**
     * reborns
     */
    private Integer reborns;

    /**
     * pqpoints
     */
    private Integer pqpoints;

    @Column("dataString")
    /**
     * dataString
     */
    private String dataString;

    @Column("lastLogoutTime")
    /**
     * lastLogoutTime
     */
    private Timestamp lastLogoutTime;

    @Column("lastExpGainTime")
    /**
     * lastExpGainTime
     */
    private Timestamp lastExpGainTime;

    @Column("partySearch")
    /**
     * partySearch
     */
    private Boolean partySearch;

    /**
     * jailexpire
     */
    private Long jailexpire;

}
