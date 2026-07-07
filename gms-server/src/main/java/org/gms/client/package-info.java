/**
 * 玩家会话与角色状态管理。
 * <p>
 * 核心类：
 * <ul>
 *   <li>{@link org.gms.client.Client} — Netty 连接会话，持有加密状态与发包通道</li>
 *   <li>{@link org.gms.client.Character} — 玩家角色完整状态（属性、背包、技能、任务进度等）</li>
 * </ul>
 * 子包 {@code inventory}、{@code command}、{@code processor} 分别处理物品操作、GM 命令与 NPC 交互处理。
 */
package org.gms.client;
