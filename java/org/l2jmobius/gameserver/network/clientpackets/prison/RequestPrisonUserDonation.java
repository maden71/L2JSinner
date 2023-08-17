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
package org.l2jmobius.gameserver.network.clientpackets.prison;

import org.l2jmobius.gameserver.instancemanager.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.zone.type.ScriptZone;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.prison.ExPrisonUserDonation;

/**
 * @author Fakee
 */
public class RequestPrisonUserDonation implements ClientPacket
{
	private static final ScriptZone PRISON_ZONE_1 = ZoneManager.getInstance().getZoneById(26010, ScriptZone.class);
	private static final ScriptZone PRISON_ZONE_2 = ZoneManager.getInstance().getZoneById(26011, ScriptZone.class);
	private static final ScriptZone PRISON_ZONE_3 = ZoneManager.getInstance().getZoneById(26012, ScriptZone.class);
	private static final Location EXIT_LOCATION1 = new Location(61072, -43395, -2992);
	private static final Location EXIT_LOCATION2 = new Location(59317, -43502, -2992);
	private static final Location EXIT_LOCATION3 = new Location(60026, -44630, -2992);
	private static final long PRISON_ZONE_1_DONATION = 1000000000;
	private static final long PRISON_ZONE_2_DONATION = 1500000000;
	private static final long PRISON_ZONE_3_DONATION = 2000000000;
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (PRISON_ZONE_3.isCharacterInZone(player))
		{
			if (!player.reduceAdena("Prison Donation", PRISON_ZONE_3_DONATION, player, true))
			{
				player.sendPacket(new ExPrisonUserDonation(false));
			}
			else
			{
				player.sendPacket(new ExPrisonUserDonation(true));
				player.teleToLocation(EXIT_LOCATION3);
			}
		}
		else if (PRISON_ZONE_2.isCharacterInZone(player))
		{
			if (!player.reduceAdena("Prison Donation", PRISON_ZONE_2_DONATION, player, true))
			{
				player.sendPacket(new ExPrisonUserDonation(false));
			}
			else
			{
				player.sendPacket(new ExPrisonUserDonation(true));
				player.teleToLocation(EXIT_LOCATION2);
			}
		}
		else if (PRISON_ZONE_1.isCharacterInZone(player))
		{
			if (!player.reduceAdena("Prison Donation", PRISON_ZONE_1_DONATION, player, true))
			{
				player.sendPacket(new ExPrisonUserDonation(false));
			}
			else
			{
				player.sendPacket(new ExPrisonUserDonation(true));
				player.teleToLocation(EXIT_LOCATION1);
			}
		}
	}
}
