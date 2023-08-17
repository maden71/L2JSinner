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
package events.ChuseokHarvestFestival;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.logging.Level;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.quest.LongTimeEvent;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.network.SystemMessageId;

/**
 * @URL https://l2central.info/main/events_and_promos/1459.html
 * @author CostyKiller
 */
public class ChuseokHarvestFestival extends LongTimeEvent
{
	// NPCs
	private static final int MOON_RABBIT = 34604;
	private static final int FULL_MOON = 34605;
	// Item
	private static final int WISH_TICKET = 82196;
	// Skill
	private static final SkillHolder ENERGY_BUFF = new SkillHolder(34288, 1); // Full Moon's Festive Energy
	// Misc
	private static final String CHUSEOK_HARVEST_FESTIVAL_VAR = "CHUSEOK_HARVEST_FESTIVAL_TICKET_RECEIVED";
	private static final int PLAYER_LEVEL = 105;
	// Moon Location
	private static final Location FULL_MOON_LOC = new Location(81241, 148863, -3472);
	
	public ChuseokHarvestFestival()
	{
		addStartNpc(MOON_RABBIT);
		addFirstTalkId(MOON_RABBIT, FULL_MOON);
		addTalkId(MOON_RABBIT, FULL_MOON);
		
		startQuestTimer("schedule", 1000, null, null);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34604.htm":
			case "34604-1.htm":
			case "34605.htm":
			case "34605-1.htm":
			case "34605-2.htm":
			{
				htmltext = event;
				break;
			}
			case "getTicket":
			{
				if (npc.getId() != FULL_MOON)
				{
					break;
				}
				if (player.getLevel() < PLAYER_LEVEL)
				{
					htmltext = "no-level.htm";
					break;
				}
				if (player.getVariables().getBoolean(CHUSEOK_HARVEST_FESTIVAL_VAR, false))
				{
					player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_BEEN_REWARDED_FOR_ENTERING_A_WISH_YOU_CAN_ONLY_MAKE_1_WISH_PER_CHARACTER);
					break;
				}
				
				player.getVariables().set(CHUSEOK_HARVEST_FESTIVAL_VAR, true);
				player.getVariables().storeMe();
				giveItems(player, WISH_TICKET, 1);
				break;
			}
			case "getBuff":
			{
				if (npc.getId() != FULL_MOON)
				{
					break;
				}
				if (player.getLevel() < PLAYER_LEVEL)
				{
					htmltext = "no-level.htm";
					break;
				}
				if (player.isAffectedBySkill(ENERGY_BUFF))
				{
					player.sendPacket(SystemMessageId.YOU_CANNOT_CHANGE_YOUR_WISH_ONCE_ENTERED_PROCEED);
					break;
				}
				
				SkillCaster.triggerCast(player, player, ENERGY_BUFF.getSkill());
				break;
			}
			case "moveToTheMoon":
			{
				if (npc.getId() == MOON_RABBIT)
				{
					player.teleToLocation(FULL_MOON_LOC, true);
				}
				break;
			}
			case "schedule":
			{
				final long currentTime = System.currentTimeMillis();
				final Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, 6);
				calendar.set(Calendar.MINUTE, 30);
				if (calendar.getTimeInMillis() < currentTime)
				{
					calendar.add(Calendar.DAY_OF_YEAR, 1);
				}
				cancelQuestTimers("reset");
				startQuestTimer("reset", calendar.getTimeInMillis() - currentTime, null, null);
				break;
			}
			case "reset":
			{
				if (isEventPeriod())
				{
					// Update data for offline players.
					try (Connection con = DatabaseFactory.getConnection();
						PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var=?"))
					{
						ps.setString(1, CHUSEOK_HARVEST_FESTIVAL_VAR);
						ps.executeUpdate();
					}
					catch (Exception e)
					{
						LOGGER.log(Level.SEVERE, "Could not reset Chuseok Harvest Festival Event var: ", e);
					}
					
					// Update data for online players.
					for (Player plr : World.getInstance().getPlayers())
					{
						plr.getVariables().remove(CHUSEOK_HARVEST_FESTIVAL_VAR);
						plr.getVariables().storeMe();
					}
				}
				cancelQuestTimers("schedule");
				startQuestTimer("schedule", 1000, null, null);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getId() + ".htm";
	}
	
	public static void main(String[] args)
	{
		new ChuseokHarvestFestival();
	}
}