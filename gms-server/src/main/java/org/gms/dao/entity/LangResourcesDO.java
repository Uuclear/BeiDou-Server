package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 数据库表 `lang_resources` 的实体类（DO）。
 * <p>
 * 多语言资源表，存储界面与提示文本的国际化键值对。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("lang_resources")
public class LangResourcesDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 语言类型，zh-CN，en-US
     */
    private String langType;

    /**
     * 预留，当存在2个一样的code，不一样的value，需要用base来区分
     */
    private String langBase;

    /**
     * i18n编码
     */
    private String langCode;

    /**
     * i18n值
     */
    private String langValue;

    /**
     * 预留扩展字段
     */
    private String langExtend;

}
