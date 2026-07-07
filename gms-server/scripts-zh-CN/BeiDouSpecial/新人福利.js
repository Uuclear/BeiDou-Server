


/**
 * 脚本类型：自定义NPC
 * 对象 ID：新人福利
 * 功能描述：新手福利礼包，每个角色限领一次随机金币与点券。
 */
var status;
var mesoQty;    // 金币数量
var cashQty;    // 点券数量

//Start
function start()
{
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection)
{
	if (CheckStatus(mode))
	{
		if (status == 0)
		{
			//第一层对话
			var strGetText = cm.getCharacterExtendValue("新人福利礼包");
			if ( strGetText == "已领取" )
			{
				cm.sendOk("您已经领取了新手奖励了。每个角色#r限领一次。#k");
				cm.dispose();
			}
			else
			{
				// 生成随机数量
				mesoQty = Math.floor(Math.random() * 501) + 500;     // 金币随机50-100万（可以根据需要调整）
				cashQty = Math.floor(Math.random() * 101) + 100;  // 点券随机100-200

				cm.sendAcceptDecline("您确定要领取新手礼包吗？一个角色#r限领一次。#k\n\n\r\n"
					+ "获得奖励：\n"
					+ "#b" + mesoQty + "#k 万金币\n"
					+ "#b" + cashQty + "#k 万点券");
			}
		}
		else if (status == 1 )
		{
			//第二层对话
			cm.saveOrUpdateCharacterExtendValue("新人福利礼包", "已领取");

			// 给金币（注意：这里mesoQty是万为单位，所以需要乘以10000）
			cm.gainMeso(mesoQty * 10000);

			// 给点券
			cm.getPlayer().getCashShop().gainCash(1, cashQty *10000);

			cm.sendOk("恭喜您获得：\n"
				+ "#b" + mesoQty + "#k 万金币\n"
				+ "#b" + cashQty + "#k 万点券\n\n"
				+ "祝您游戏愉快！");
			cm.dropMessage(5,"【新人福利】玩家 [" + cm.getPlayer() + "] 加入游戏领取开荒金币"+mesoQty+"万＋点券"+cashQty+"万！");
			cm.dispose();
		}
		else
		{
			//最后一层对话完继续循环至此，推出结束
			cm.dispose();
		}
	}

}

function CheckStatus(mode)
{
	if (mode == -1)
	{
		cm.dispose();
		return false;
	}

	if (mode == 1)
	{
		status++;
	}
	else
	{
		status--;
		// 如果用户点击"否"，直接结束
		if (status == -1)
		{
			cm.sendOk("好吧，下次再来领取吧。");
			cm.dispose();
			return false;
		}
	}

	if (status == -1)
	{
		cm.dispose();
		return false;
	}
	return true;
}