/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gms.server.quest.actions;

import org.gms.client.Character;
import org.gms.client.Job;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.provider.Data;
import org.gms.provider.DataTool;
import org.gms.server.quest.Quest;
import org.gms.server.quest.QuestActionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务奖励技能动作。
 */
public class SkillAction extends AbstractQuestAction {
    int itemEffect;
    Map<Integer, SkillData> skillData = new HashMap<>();

    /**
     * 构造 SkillAction 实例。
     * @param quest 任务
     * @param data WZ 数据节点
     */
    public SkillAction(Quest quest, Data data) {
        super(QuestActionType.SKILL, quest);
        processData(data);
    }


    /**
     * 处理数据。
     * @param data WZ 数据节点
     */
    @Override
    public void processData(Data data) {
        for (Data sEntry : data) {
            byte skillLevel = 0;
            int skillid = DataTool.getInt(sEntry.getChildByPath("id"));
            Data skillLevelData = sEntry.getChildByPath("skillLevel");
            if (skillLevelData != null) {
                skillLevel = (byte) DataTool.getInt(skillLevelData);
            }
            int masterLevel = DataTool.getInt(sEntry.getChildByPath("masterLevel"));
            List<Integer> jobs = new ArrayList<>();

            Data applicableJobs = sEntry.getChildByPath("job");
            if (applicableJobs != null) {
                for (Data applicableJob : applicableJobs.getChildren()) {
                    jobs.add(DataTool.getInt(applicableJob));
                }
            }

            skillData.put(skillid, new SkillData(skillid, skillLevel, masterLevel, jobs));
        }
    }

    /**
     * 执行动作逻辑。
     * @param chr 角色
     * @param extSelection 扩展选项
     */
    @Override
    public void run(Character chr, Integer extSelection) {
        for (SkillData skill : skillData.values()) {
            Skill skillObject = SkillFactory.getSkill(skill.getId());
            if (skillObject == null) {
                continue;
            }

            boolean shouldLearn = skill.jobsContains(chr.getJob()) || skillObject.isBeginnerSkill();

            byte skillLevel = (byte) Math.max(skill.getLevel(), chr.getSkillLevel(skillObject));
            int masterLevel = Math.max(skill.getMasterLevel(), chr.getMasterLevel(skillObject));
            if (shouldLearn) {
                chr.changeSkillLevel(skillObject, skillLevel, masterLevel, -1);
            }

        }
    }

    private class SkillData {
        protected int id, level, masterLevel;
        List<Integer> jobs = new ArrayList<>();

        /**
         * 执行 技能数据 操作。
         * @param id ID
         * @param level level
         * @param masterLevel masterLevel
         * @param jobs jobs（Integer 列表/集合）
         * @return SkillData 类型结果
         */
        public SkillData(int id, int level, int masterLevel, List<Integer> jobs) {
            this.id = id;
            this.level = level;
            this.masterLevel = masterLevel;
            this.jobs = jobs;
        }

        /**
         * 获取ID。
         * @return int 类型结果
         */
        public int getId() {
            return id;
        }

        /**
         * 获取等级。
         * @return int 类型结果
         */
        public int getLevel() {
            return level;
        }

        /**
         * 获取Master、等级。
         * @return int 类型结果
         */
        public int getMasterLevel() {
            return masterLevel;
        }

        /**
         * 执行 jobs、Contains 操作。
         * @param job job
         * @return boolean 类型结果
         */
        public boolean jobsContains(Job job) {
            return jobs.contains(job.getId());
        }


    }
} 