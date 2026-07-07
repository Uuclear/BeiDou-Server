/**
 * 脚本类型：物品
 * 对象 ID：2430033
 * 功能描述：北斗指南书（2430033）使用脚本，集齐 7 本可兑换 100 万金币奖励。
 */
var status = -1;
var text;

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
			if (im.haveItem(2430033, 7))
			{
				im.gainMeso(1000000);
				im.sendOk("This is a reward for you, take it.");
				im.gainItem(2430033,-7);
				im.dispose();
			}
			else
			{
				im.sendOk("More book is required.");
				im.dispose();				
			}
	    }
		else
		{
			im.dispose();
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
	}
	
	if (status == -1)
	{
		cm.dispose();
		return false;
	}	
	return true;
}

