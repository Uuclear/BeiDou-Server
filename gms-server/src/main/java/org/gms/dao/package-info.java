/**
 * 数据访问层，基于 MyBatis-Flex。
 * <p>
 * {@link org.gms.dao.entity} 存放与数据库表一一对应的 DO（Data Object）实体；
 * {@link org.gms.dao.mapper} 存放 Mapper 接口，由 MyBatis 自动生成 SQL 映射。
 * 数据库 Schema 由 Flyway 迁移脚本（{@code db/migration/}）版本化管理。
 */
package org.gms.dao;
