package org.gms.config;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import org.gms.dao.entity.GameConfigDO;
import org.gms.manager.ServerManager;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.gms.server.life.MonsterInformationProvider;
import org.gms.service.ConfigService;
import org.gms.util.Pair;

import java.util.List;
import java.util.function.Function;

/**
 * 游戏动态配置管理类
 * <p>
 * 北斗动态参数计划核心类，用于管理游戏服务器的动态配置参数。
 * 配置结构采用三级层次：configType（配置大类） -> configSubType（配置子类） -> configCode（配置项代码）。
 * </p>
 * <p>
 * 配置数据存储在数据库中，结构示例：
 * <pre>
 * {
 *   "world": {
 *     "0": {
 *       "server_message": {"clazz":"java.lang.String","value":"Welcome to Scania!"},
 *       "exp_rate": {"clazz":"java.lang.Float","value":"1.0"}
 *     }
 *   },
 *   "server": {
 *     "global": {
 *       "WORLDS": {"clazz":"java.lang.Integer","value":"1"}
 *     }
 *   }
 * }
 * </pre>
 * </p>
 * <p>
 * 支持世界（world）和服务器（server）两级配置，不同世界可以有独立的配置参数。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
public class GameConfig {

    /**
     * 单例实例
     */
    private static final GameConfig config = new GameConfig();

    /**
     * 配置属性存储对象，以JSON结构存储所有配置
     */
    private final JSONObject properties = new JSONObject();

    /**
     * 私有构造函数，初始化配置
     * <p>
     * 从Spring容器中获取ConfigService，加载数据库中的所有游戏配置并添加到内存中。
     * </p>
     */
    private GameConfig() {
        ConfigService configService = ServerManager.getApplicationContext().getBean(ConfigService.class);
        List<GameConfigDO> gameConfigDOS = configService.loadGameConfigs();
        gameConfigDOS.forEach(gameConfigDO -> add(this, gameConfigDO));
    }

    /**
     * 添加游戏配置项（公开方法）
     *
     * @param gameConfigDO 游戏配置数据对象
     */
    public static void add(GameConfigDO gameConfigDO) {
        add(config, gameConfigDO);
    }

    /**
     * 添加游戏配置项（内部实现）
     * <p>
     * 根据配置的三级层次结构（type -> subType -> code），
     * 逐级创建JSONObject并存储配置值和类型信息。
     * </p>
     *
     * @param config       GameConfig实例
     * @param gameConfigDO 游戏配置数据对象
     */
    private static void add(GameConfig config, GameConfigDO gameConfigDO) {
        JSONObject typeProp = config.properties.getJSONObject(gameConfigDO.getConfigType());
        if (typeProp == null) {
            typeProp = new JSONObject();
            config.properties.put(gameConfigDO.getConfigType(), typeProp);
        }
        JSONObject subProp = typeProp.getJSONObject(gameConfigDO.getConfigSubType());
        if (subProp == null) {
            subProp = new JSONObject();
            typeProp.put(gameConfigDO.getConfigSubType(), subProp);
        }
        JSONObject valueProp = subProp.getJSONObject(gameConfigDO.getConfigCode());
        if (valueProp == null) {
            valueProp = new JSONObject();
            subProp.put(gameConfigDO.getConfigCode(), valueProp);
        }
        valueProp.put("value", gameConfigDO.getConfigValue());
        valueProp.put("clazz", gameConfigDO.getConfigClazz());
    }

    /**
     * 删除游戏配置项
     * <p>
     * 根据配置项的三级定位删除配置，并清理空的父级节点。
     * </p>
     *
     * @param gameConfigDO 游戏配置数据对象，包含要删除的配置定位信息
     */
    public static void remove(GameConfigDO gameConfigDO) {
        JSONObject typeProp = config.properties.getJSONObject(gameConfigDO.getConfigType());
        if (typeProp == null) {
            return;
        }
        JSONObject subProp = typeProp.getJSONObject(gameConfigDO.getConfigSubType());
        if (subProp == null) {
            return;
        }
        subProp.remove(gameConfigDO.getConfigCode());
        if (subProp.isEmpty()) {
            typeProp.remove(gameConfigDO.getConfigSubType());
        }
        if (typeProp.isEmpty()) {
            config.properties.remove(gameConfigDO.getConfigType());
        }
    }

    /**
     * 更新游戏配置项
     * <p>
     * 更新配置值后，对于需要热重载的配置项立即生效：
     * <ul>
     *   <li>世界级配置：经验倍率、金币倍率、掉落倍率、服务器消息、世界标志等</li>
     *   <li>服务器级配置：如允许偷窃任务物品等</li>
     * </ul>
     * </p>
     *
     * @param gameConfigDO 游戏配置数据对象
     */
    public static void update(GameConfigDO gameConfigDO) {
        JSONObject valueProp = getValueProp(gameConfigDO.getConfigType(), gameConfigDO.getConfigSubType(), gameConfigDO.getConfigCode());
        if (valueProp == null) {
            add(gameConfigDO);
            return;
        }
        valueProp.put("value", gameConfigDO.getConfigValue());

        // 手动重载不能自动重载的世界级配置项
        if ("world".equals(gameConfigDO.getConfigType())) {
            int index = Integer.parseInt(gameConfigDO.getConfigSubType());
            World world = Server.getInstance().getWorld(index);
            switch (gameConfigDO.getConfigCode()) {
                case "exp_rate":
                    world.setExpRate(Float.parseFloat(gameConfigDO.getConfigValue()));
                    break;
                case "meso_rate":
                    world.setMesoRate(Float.parseFloat(gameConfigDO.getConfigValue()));
                    break;
                case "drop_rate":
                    world.setDropRate(Float.parseFloat(gameConfigDO.getConfigValue()));
                    break;
                case "boss_drop_rate":
                    world.setBossDropRate(Float.parseFloat(gameConfigDO.getConfigValue()));
                    break;
                case "quest_rate":
                    world.setQuestRate(Float.parseFloat(gameConfigDO.getConfigValue()));
                    break;
                case "travel_rate":
                    world.setTravelRate(Float.parseFloat(gameConfigDO.getConfigValue()));
                    break;
                case "fishing_rate":
                    world.setFishingRate(Float.parseFloat(gameConfigDO.getConfigValue()));
                    break;
                case "server_message":
                    world.setServerMessage(GameConfig.getWorldString(index, "server_message"));
                    break;
                case "event_message":
                    world.setEventMessage(GameConfig.getWorldString(index, "event_message"));
                    break;
                case "recommend_message":
                    Server.getInstance().worldRecommendedList().set(index, new Pair<>(index, GameConfig.getWorldString(index, "recommend_message")));
                    break;
                case "flag":
                    world.setFlag(GameConfig.getWorldByte(index, "flag"));
                    break;
            }
        }
        // 重载其余需要特殊处理的配置项
        switch (gameConfigDO.getConfigCode()) {
            case "allow_steal_quest_item":
                MonsterInformationProvider.getInstance().clearDrops();
                break;
        }
    }

    /**
     * 获取配置对象（根据key全局搜索）
     *
     * @param key 配置项键名
     * @return 配置对象，找不到返回null
     */
    public static Object getObject(String key) {
        return get(key, null);
    }

    /**
     * 获取配置值（根据key全局搜索，无默认值）
     *
     * @param key 配置项键名
     * @param <T> 返回值类型
     * @return 配置值，找不到返回null
     */
    public static <T> T get(String key) {
        return get(key, null);
    }

    /**
     * 获取配置值（根据key全局搜索，带默认值）
     * <p>
     * 在所有配置大类中搜索指定key，返回第一个匹配的值。
     * </p>
     *
     * @param key          配置项键名
     * @param defaultValue 默认值
     * @param <T>          返回值类型
     * @return 配置值，找不到返回默认值
     */
    public static <T> T get(String key, T defaultValue) {
        for (String type : config.properties.keySet()) {
            T obj = get(type, key, null);
            if (obj != null) {
                return obj;
            }
        }
        return defaultValue;
    }

    /**
     * 获取配置值（指定大类和key，无默认值）
     *
     * @param type 配置大类
     * @param key  配置项键名
     * @param <T>  返回值类型
     * @return 配置值，找不到返回null
     */
    public static <T> T get(String type, String key) {
        return get(type, key, null);
    }

    /**
     * 获取配置值（指定大类和key，带默认值）
     *
     * @param type      配置大类
     * @param key       配置项键名
     * @param defaultVal 默认值
     * @param <T>       返回值类型
     * @return 配置值，找不到返回默认值
     */
    public static <T> T get(String type, String key, T defaultVal) {
        JSONObject valueProp = getValueProp(type, key);
        if (valueProp == null) {
            return defaultVal;
        }
        T t = getValue(valueProp);
        return t == null ? defaultVal : t;
    }

    /**
     * 获取配置值（指定三级定位，无默认值）
     *
     * @param type    配置大类
     * @param subType 配置子类
     * @param key     配置项键名
     * @param <T>     返回值类型
     * @return 配置值，找不到返回null
     */
    public static <T> T get(String type, String subType, String key) {
        return get(type, subType, key, null);
    }

    /**
     * 获取配置值（指定三级定位，带默认值）
     *
     * @param type      配置大类
     * @param subType   配置子类
     * @param key       配置项键名
     * @param defaultVal 默认值
     * @param <T>       返回值类型
     * @return 配置值，找不到返回默认值
     */
    public static <T> T get(String type, String subType, String key, T defaultVal) {
        JSONObject valueProp = getValueProp(type, subType, key);
        if (valueProp == null) {
            return defaultVal;
        }
        T t = getValue(valueProp);
        return t == null ? defaultVal : t;
    }

    /**
     * 根据配置中存储的clazz类型转换并获取值
     * <p>
     * 首先尝试直接按类型获取，如果失败则尝试将字符串值解析为目标类型。
     * </p>
     *
     * @param valueProp 包含value和clazz的配置属性对象
     * @param <T>       返回值类型
     * @return 类型转换后的值
     * @throws RuntimeException 如果指定的类找不到
     */
    @SuppressWarnings("unchecked")
    private static <T> T getValue(JSONObject valueProp) {
        String clazz = valueProp.getString("clazz");
        Class<?> clz;
        try {
            clz = Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            return (T) valueProp.getObject("value", clz);
        } catch (JSONException e) {
            return (T) JSONObject.parseObject(valueProp.getString("value"), clz);
        }
    }

    /* ==================== 以上根据数据库的clazz类型获取，以下根据传入的类型获取 ==================== */

    /**
     * 获取配置属性JSON对象（三级精确定位）
     *
     * @param type    配置大类
     * @param subType 配置子类
     * @param key     配置项键名（自动转为小写）
     * @return 配置属性JSON对象，找不到返回null
     */
    public static JSONObject getValueProp(String type, String subType, String key) {
        JSONObject typeProp = config.properties.getJSONObject(type);
        if (typeProp == null) {
            return null;
        }
        JSONObject subProp = typeProp.getJSONObject(subType);
        if (subProp == null) {
            return null;
        }
        return subProp.getJSONObject(key.toLowerCase());
    }

    /**
     * 获取配置属性JSON对象（两级定位，在subType中搜索key）
     *
     * @param type 配置大类
     * @param key  配置项键名（自动转为小写）
     * @return 配置属性JSON对象，找不到返回null
     */
    public static JSONObject getValueProp(String type, String key) {
        JSONObject typeProp = config.properties.getJSONObject(type);
        if (typeProp == null) {
            return null;
        }
        for (String subType : typeProp.keySet()) {
            JSONObject subProp = typeProp.getJSONObject(subType);
            if (subProp == null) {
                continue;
            }
            JSONObject valueProp = subProp.getJSONObject(key.toLowerCase());
            if (valueProp != null) {
                return valueProp;
            }
        }
        return null;
    }

    /**
     * 获取Integer类型配置值（全局搜索，包装类型）
     *
     * @param key 配置项键名
     * @return Integer值，找不到返回null
     */
    public static Integer getInteger(String key) {
        return getValue(key, null, valueProp -> valueProp.getInteger("value"));
    }

    /**
     * 获取int类型配置值（全局搜索，基本类型，默认0）
     *
     * @param key 配置项键名
     * @return int值，找不到返回0
     */
    public static int getIntValue(String key) {
        return getValue(key, 0, valueProp -> valueProp.getIntValue("value"));
    }

    /**
     * 获取Long类型配置值（全局搜索，包装类型）
     *
     * @param key 配置项键名
     * @return Long值，找不到返回null
     */
    public static Long getLong(String key) {
        return getValue(key, null, valueProp -> valueProp.getLong("value"));
    }

    /**
     * 获取long类型配置值（全局搜索，基本类型，默认0L）
     *
     * @param key 配置项键名
     * @return long值，找不到返回0L
     */
    public static long getLongValue(String key) {
        return getValue(key, 0L, valueProp -> valueProp.getLongValue("value"));
    }

    /**
     * 获取Short类型配置值（全局搜索，包装类型）
     *
     * @param key 配置项键名
     * @return Short值，找不到返回null
     */
    public static Short getShort(String key) {
        return getValue(key, null, valueProp -> valueProp.getShort("value"));
    }

    /**
     * 获取short类型配置值（全局搜索，基本类型，默认0）
     *
     * @param key 配置项键名
     * @return short值，找不到返回0
     */
    public static short getShortValue(String key) {
        return getValue(key, (short) 0, valueProp -> valueProp.getShortValue("value"));
    }

    /**
     * 获取Byte类型配置值（全局搜索，包装类型）
     *
     * @param key 配置项键名
     * @return Byte值，找不到返回null
     */
    public static Byte getByte(String key) {
        return getValue(key, null, valueProp -> valueProp.getByte("value"));
    }

    /**
     * 获取byte类型配置值（全局搜索，基本类型，默认0）
     *
     * @param key 配置项键名
     * @return byte值，找不到返回0
     */
    public static byte getByteValue(String key) {
        return getValue(key, (byte) 0, valueProp -> valueProp.getByteValue("value"));
    }

    /**
     * 获取Float类型配置值（全局搜索，包装类型）
     *
     * @param key 配置项键名
     * @return Float值，找不到返回null
     */
    public static float getFloat(String key) {
        return getValue(key, null, valueProp -> valueProp.getFloat("value"));
    }

    /**
     * 获取float类型配置值（全局搜索，基本类型，默认0F）
     *
     * @param key 配置项键名
     * @return float值，找不到返回0F
     */
    public static float getFloatValue(String key) {
        return getValue(key, 0F, valueProp -> valueProp.getFloatValue("value"));
    }

    /**
     * 获取Double类型配置值（全局搜索，包装类型）
     *
     * @param key 配置项键名
     * @return Double值，找不到返回null
     */
    public static Double getDouble(String key) {
        return getValue(key, null, valueProp -> valueProp.getDouble("value"));
    }

    /**
     * 获取double类型配置值（全局搜索，基本类型，默认0D）
     *
     * @param key 配置项键名
     * @return double值，找不到返回0D
     */
    public static double getDoubleValue(String key) {
        return getValue(key, 0D, valueProp -> valueProp.getDoubleValue("value"));
    }

    /**
     * 获取Boolean类型配置值（全局搜索，包装类型）
     *
     * @param key 配置项键名
     * @return Boolean值，找不到返回null
     */
    public static Boolean getBoolean(String key) {
        return getValue(key, null, valueProp -> valueProp.getBoolean("value"));
    }

    /**
     * 获取boolean类型配置值（全局搜索，基本类型，默认false）
     *
     * @param key 配置项键名
     * @return boolean值，找不到返回false
     */
    public static boolean getBooleanValue(String key) {
        return getValue(key, false, valueProp -> valueProp.getBooleanValue("value"));
    }

    /**
     * 获取String类型配置值（全局搜索，可能为null）
     *
     * @param key 配置项键名
     * @return String值，找不到返回null
     */
    public static String getString(String key) {
        return getValue(key, null, valueProp -> valueProp.getString("value"));
    }

    /**
     * 获取String类型配置值（全局搜索，空字符串默认值）
     *
     * @param key 配置项键名
     * @return String值，找不到或为null时返回空字符串
     */
    public static String getStringValue(String key) {
        return getValue(key, "", valueProp -> valueProp.getString("value") == null ? "" : valueProp.getString("value"));
    }

    /**
     * 获取指定类型对象配置值（全局搜索）
     *
     * @param key 配置项键名
     * @param clz 目标类型Class
     * @param <T> 返回值类型
     * @return 类型转换后的对象，找不到返回null
     */
    public static <T> T getObject(String key, Class<T> clz) {
        return getValue(key, null, valueProp -> {
            try {
                return valueProp.getObject("value", clz);
            } catch (JSONException e) {
                return JSONObject.parseObject(valueProp.getString("value"), clz);
            }
        });
    }

    /**
     * 通用获取值方法（使用Function映射）
     *
     * @param key        配置项键名
     * @param defaultVal 默认值
     * @param mapper     值映射函数
     * @param <T>        返回值类型
     * @return 映射后的值，找不到返回默认值
     */
    private static <T> T getValue(String key, T defaultVal, Function<JSONObject, T> mapper) {
        for (String type : config.properties.keySet()) {
            JSONObject valueProp = getValueProp(type, key);
            if (valueProp != null) {
                return mapper.apply(valueProp);
            }
        }
        return defaultVal;
    }

    /* ==================== 以下根据参数大类获取，可以避免同一个参数在不同大区场景下获取错误 ==================== */

    /**
     * 获取世界级配置值
     *
     * @param worldId 世界ID
     * @param key     配置项键名
     * @param <T>     返回值类型
     * @return 配置值
     */
    public static <T> T getWorld(int worldId, String key) {
        return get("world", String.valueOf(worldId), key);
    }

    /**
     * 获取服务器级配置值
     *
     * @param key 配置项键名
     * @param <T> 返回值类型
     * @return 配置值
     */
    public static <T> T getServer(String key) {
        return get("server", key);
    }

    /**
     * 获取世界级int配置值
     *
     * @param worldId 世界ID
     * @param key     配置项键名
     * @return int值，找不到返回0
     */
    public static int getWorldInt(int worldId, String key) {
        JSONObject valueProp = getValueProp("world", String.valueOf(worldId), key);
        if (valueProp == null) {
            return 0;
        }
        return valueProp.getIntValue("value");
    }

    /**
     * 获取服务器级int配置值
     *
     * @param key 配置项键名
     * @return int值，找不到返回0
     */
    public static int getServerInt(String key) {
        JSONObject valueProp = getValueProp("server", key);
        if (valueProp == null) {
            return 0;
        }
        return valueProp.getIntValue("value");
    }

    /**
     * 获取世界级byte配置值
     *
     * @param worldId 世界ID
     * @param key     配置项键名
     * @return byte值，找不到返回0
     */
    public static byte getWorldByte(int worldId, String key) {
        JSONObject valueProp = getValueProp("world", String.valueOf(worldId), key);
        if (valueProp == null) {
            return (byte) 0;
        }
        return valueProp.getByteValue("value");
    }

    /**
     * 获取服务器级byte配置值
     *
     * @param key 配置项键名
     * @return byte值，找不到返回0
     */
    public static byte getServerByte(String key) {
        JSONObject valueProp = getValueProp("server", key);
        if (valueProp == null) {
            return (byte) 0;
        }
        return valueProp.getByteValue("value");
    }

    /**
     * 获取世界级long配置值
     *
     * @param worldId 世界ID
     * @param key     配置项键名
     * @return long值，找不到返回0L
     */
    public static long getWorldLong(int worldId, String key) {
        JSONObject valueProp = getValueProp("world", String.valueOf(worldId), key);
        if (valueProp == null) {
            return 0L;
        }
        return valueProp.getLongValue("value");
    }

    /**
     * 获取服务器级long配置值
     *
     * @param key 配置项键名
     * @return long值，找不到返回0L
     */
    public static long getServerLong(String key) {
        JSONObject valueProp = getValueProp("server", key);
        if (valueProp == null) {
            return 0L;
        }
        return valueProp.getLongValue("value");
    }

    /**
     * 获取世界级short配置值
     *
     * @param worldId 世界ID
     * @param key     配置项键名
     * @return short值，找不到返回0
     */
    public static short getWorldShort(int worldId, String key) {
        JSONObject valueProp = getValueProp("world", String.valueOf(worldId), key);
        if (valueProp == null) {
            return (short) 0;
        }
        return valueProp.getShortValue("value");
    }

    /**
     * 获取服务器级short配置值
     *
     * @param key 配置项键名
     * @return short值，找不到返回0
     */
    public static short getServerShort(String key) {
        JSONObject valueProp = getValueProp("server", key);
        if (valueProp == null) {
            return (short) 0;
        }
        return valueProp.getShortValue("value");
    }

    /**
     * 获取世界级float配置值
     *
     * @param worldId 世界ID
     * @param key     配置项键名
     * @return float值，找不到返回0F
     */
    public static float getWorldFloat(int worldId, String key) {
        JSONObject valueProp = getValueProp("world", String.valueOf(worldId), key);
        if (valueProp == null) {
            return 0F;
        }
        return valueProp.getFloatValue("value");
    }

    /**
     * 获取服务器级float配置值
     *
     * @param key 配置项键名
     * @return float值，找不到返回0F
     */
    public static float getServerFloat(String key) {
        JSONObject valueProp = getValueProp("server", key);
        if (valueProp == null) {
            return 0F;
        }
        return valueProp.getFloatValue("value");
    }

    /**
     * 获取世界级double配置值
     *
     * @param worldId 世界ID
     * @param key     配置项键名
     * @return double值，找不到返回0D
     */
    public static double getWorldDouble(int worldId, String key) {
        JSONObject valueProp = getValueProp("world", String.valueOf(worldId), key);
        if (valueProp == null) {
            return 0D;
        }
        return valueProp.getDoubleValue("value");
    }

    /**
     * 获取服务器级double配置值
     *
     * @param key 配置项键名
     * @return double值，找不到返回0D
     */
    public static double getServerDouble(String key) {
        JSONObject valueProp = getValueProp("server", key);
        if (valueProp == null) {
            return 0D;
        }
        return valueProp.getDoubleValue("value");
    }

    /**
     * 获取世界级String配置值
     *
     * @param worldId 世界ID
     * @param key     配置项键名
     * @return String值，找不到返回空字符串
     */
    public static String getWorldString(int worldId, String key) {
        JSONObject valueProp = getValueProp("world", String.valueOf(worldId), key);
        if (valueProp == null) {
            return "";
        }
        return valueProp.getString("value");
    }

    /**
     * 获取服务器级String配置值
     *
     * @param key 配置项键名
     * @return String值，找不到返回空字符串
     */
    public static String getServerString(String key) {
        JSONObject valueProp = getValueProp("server", key);
        if (valueProp == null) {
            return "";
        }
        return valueProp.getString("value");
    }

    /**
     * 获取世界级boolean配置值
     *
     * @param worldId 世界ID
     * @param key     配置项键名
     * @return boolean值，找不到返回false
     */
    public static boolean getWorldBoolean(int worldId, String key) {
        JSONObject valueProp = getValueProp("world", String.valueOf(worldId), key);
        if (valueProp == null) {
            return false;
        }
        return valueProp.getBooleanValue("value");
    }

    /**
     * 获取服务器级boolean配置值
     *
     * @param key 配置项键名
     * @return boolean值，找不到返回false
     */
    public static boolean getServerBoolean(String key) {
        JSONObject valueProp = getValueProp("server", key);
        if (valueProp == null) {
            return false;
        }
        return valueProp.getBooleanValue("value");
    }

    /**
     * 获取世界级指定类型对象配置值
     *
     * @param worldId 世界ID
     * @param key     配置项键名
     * @param clz     目标类型Class
     * @param <T>     返回值类型
     * @return 类型转换后的对象，找不到返回null
     */
    public static <T> T getWorldObject(int worldId, String key, Class<T> clz) {
        return getValue(false, String.valueOf(worldId), key, clz);
    }

    /**
     * 获取世界级指定类型对象配置值（带默认值）
     *
     * @param worldId    世界ID
     * @param key        配置项键名
     * @param defaultVal 默认值
     * @param <T>        返回值类型
     * @return 类型转换后的对象，找不到返回默认值
     */
    public static <T> T getWorldObject(int worldId, String key, T defaultVal) {
        T t = getValue(false, String.valueOf(worldId), key, defaultVal.getClass());
        return t == null ? defaultVal : t;
    }

    /**
     * 获取服务器级指定类型对象配置值
     *
     * @param key 配置项键名
     * @param clz 目标类型Class
     * @param <T> 返回值类型
     * @return 类型转换后的对象，找不到返回null
     */
    public static <T> T getServerObject(String key, Class<T> clz) {
        return getValue(true, null, key, clz);
    }

    /**
     * 获取服务器级指定类型对象配置值（带默认值）
     *
     * @param key        配置项键名
     * @param defaultVal 默认值
     * @param <T>        返回值类型
     * @return 类型转换后的对象，找不到返回默认值
     */
    public static <T> T getServerObject(String key, T defaultVal) {
        T t = getValue(true, null, key, defaultVal.getClass());
        return t == null ? defaultVal : t;
    }

    /**
     * 内部通用获取值方法（指定服务器/世界级别）
     *
     * @param isServer 是否为服务器级别（true=服务器级，false=世界级）
     * @param subType  配置子类（世界ID，服务器级为null）
     * @param key      配置项键名
     * @param clz      目标类型Class
     * @param <T>      返回值类型
     * @return 类型转换后的对象，找不到返回null
     */
    @SuppressWarnings("unchecked")
    private static <T> T getValue(boolean isServer, String subType, String key, Class<?> clz) {
        JSONObject valueProp;
        if (isServer) {
            valueProp = getValueProp("server", key);
        } else {
            valueProp = getValueProp("world", subType, key);
        }
        if (valueProp == null) {
            return null;
        }
        try {
            return (T) valueProp.getObject("value", clz);
        } catch (JSONException e) {
            return (T) JSONObject.parseObject(valueProp.getString("value"), clz);
        }
    }

    /**
     * 获取世界级泛型类型对象配置值（支持复杂泛型类型）
     *
     * @param worldId 世界ID
     * @param key     配置项键名
     * @param type    类型引用（用于解析泛型）
     * @param <T>     返回值类型
     * @return 类型转换后的对象，找不到返回null
     */
    public static <T> T getWorldObject(int worldId, String key, TypeReference<T> type) {
        JSONObject valueProp = getValueProp("world", String.valueOf(worldId), key);
        if (valueProp == null) {
            return null;
        }
        try {
            return valueProp.getObject("value", type);
        } catch (JSONException e) {
            return JSONObject.parseObject(valueProp.getString("value"), type);
        }
    }

    /**
     * 获取服务器级泛型类型对象配置值（支持复杂泛型类型）
     *
     * @param key  配置项键名
     * @param type 类型引用（用于解析泛型）
     * @param <T>  返回值类型
     * @return 类型转换后的对象，找不到返回null
     */
    public static <T> T getServerObject(String key, TypeReference<T> type) {
        JSONObject valueProp = getValueProp("server", key);
        if (valueProp == null) {
            return null;
        }
        try {
            return valueProp.getObject("value", type);
        } catch (JSONException e) {
            return JSONObject.parseObject(valueProp.getString("value"), type);
        }
    }

    /**
     * 获取所有配置属性JSON对象
     *
     * @return 包含所有配置的JSONObject
     */
    public static JSONObject getConfig() {
        return config.properties;
    }
}
