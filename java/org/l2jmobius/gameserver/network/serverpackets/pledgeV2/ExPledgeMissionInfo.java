/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.serverpackets.pledgeV2;

import java.util.Collection;
import java.util.Collections;

import org.l2jmobius.gameserver.data.xml.DailyMissionData;
import org.l2jmobius.gameserver.model.DailyMissionDataHolder;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExPledgeMissionInfo extends ServerPacket
{
	private final Player _player;
	private final Collection<DailyMissionDataHolder> _rewards;
	
	public ExPledgeMissionInfo(Player player)
	{
		_player = player;
		_rewards = DailyMissionData.getInstance().getDailyMissionData(player);
	}
	
	public ExPledgeMissionInfo(Player player, DailyMissionDataHolder holder)
	{
		_player = player;
		_rewards = Collections.singletonList(holder);
	}
	
	@Override
	public void write()
	{
		if (!DailyMissionData.getInstance().isAvailable() || (_player.getClan() == null))
		{
			return;
		}
		
		ServerPackets.EX_PLEDGE_MISSION_INFO.writeId(this);
		writeInt(_rewards.size());
		for (DailyMissionDataHolder reward : _rewards)
		{
			int progress = reward.getProgress(_player);
			int status = reward.getStatus(_player);
			// TODO: Figure out this.
			if (reward.isLevelUpMission())
			{
				if (status == 2)
				{
					status = reward.getRequiredCompletions() > _player.getLevel() ? 1 : 3;
				}
				else if ((status == 3) && (progress == 3))
				{
					status = 0;
				}
				else
				{
					status = reward.isRecentlyCompleted(_player) ? 0 : 3;
				}
				progress = 1;
			}
			else if (status == 1)
			{
				status = 3;
			}
			else if (status == 3)
			{
				status = 2;
			}
			writeInt(reward.getId());
			writeInt(progress);
			writeByte(status);
		}
	}
}
