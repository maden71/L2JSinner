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
package custom.events.TeamVsTeam;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.Config;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.enums.CategoryType;
import org.l2jmobius.gameserver.enums.PartyDistributionType;
import org.l2jmobius.gameserver.enums.SkillFinishType;
import org.l2jmobius.gameserver.enums.Team;
import org.l2jmobius.gameserver.instancemanager.AntiFeedManager;
import org.l2jmobius.gameserver.instancemanager.InstanceManager;
import org.l2jmobius.gameserver.instancemanager.ZoneManager;
import org.l2jmobius.gameserver.model.CommandChannel;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.impl.creature.OnCreatureDeath;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogout;
import org.l2jmobius.gameserver.model.events.listeners.AbstractEventListener;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.instancezone.InstanceTemplate;
import org.l2jmobius.gameserver.model.olympiad.OlympiadManager;
import org.l2jmobius.gameserver.model.quest.Event;
import org.l2jmobius.gameserver.model.quest.QuestTimer;
import org.l2jmobius.gameserver.model.skill.CommonSkill;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.serverpackets.ExPVPMatchCCRecord;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jmobius.gameserver.util.Broadcast;
import org.l2jmobius.gameserver.util.Util;

/**
 * Team vs Team event.
 * @author Mobius
 */
public class TvT extends Event
{
	// NPC
	private static final int MANAGER = 70011;
	// Skills
	private static final SkillHolder GHOST_WALKING = new SkillHolder(100000, 1); // Custom Ghost Walking
	private static final SkillHolder KNIGHT = new SkillHolder(15648, 1); // Knight's Harmony (Adventurer)
	private static final SkillHolder WARRIOR = new SkillHolder(15649, 1); // Warrior's Harmony (Adventurer)
	private static final SkillHolder WIZARD = new SkillHolder(15650, 1); // Wizard's Harmony (Adventurer)
	private static final SkillHolder[] GROUP_BUFFS =
	{
		new SkillHolder(15642, 1), // Horn Melody (Adventurer)
		new SkillHolder(15643, 1), // Drum Melody (Adventurer)
		new SkillHolder(15644, 1), // Pipe Organ Melody (Adventurer)
		new SkillHolder(15645, 1), // Guitar Melody (Adventurer)
		new SkillHolder(15651, 1), // Prevailing Sonata (Adventurer)
		new SkillHolder(15652, 1), // Daring Sonata (Adventurer)
		new SkillHolder(15653, 1), // Refreshing Sonata (Adventurer)
	};
	// Others
	private static final int INSTANCE_ID = 3049;
	private static final int BLUE_DOOR_ID = 24190002;
	private static final int RED_DOOR_ID = 24190003;
	
	// TVT event npc spawns
	private static final Location MANAGER_SPAWN_GIRAN1 = new Location(82345, 148616, -3444, 194);
	private static final Location MANAGER_SPAWN_GIRAN2 = new Location(83224, 148840, -3372, 64521);
	private static final Location MANAGER_SPAWN_GIRAN3 = new Location(83232, 148397, -3373, 0);
	private static final Location MANAGER_SPAWN_ADEN1 = new Location(147447, 25745, -2014, 16083);
	private static final Location MANAGER_SPAWN_ADEN2 = new Location(147101, 27229, -2206, 49305);
	private static final Location MANAGER_SPAWN_ADEN3 = new Location(147808, 27229, -2206, 49305);
	private static final Location MANAGER_SPAWN_TI1 = new Location(-114455, 255045, -1531, 11547);
	private static final Location MANAGER_SPAWN_TI2 = new Location(-114266, 255047, -1532, 14385);
	private static final Location MANAGER_SPAWN_TI3 = new Location(-114366, 255907, -1513, 49151);
	private static final Location MANAGER_SPAWN_HEINE1 = new Location(111391, 219048, -3544, 15583);
	private static final Location MANAGER_SPAWN_HEINE2 = new Location(111872, 220162, -3677, 31045);
	private static final Location MANAGER_SPAWN_HEINE3 = new Location(110900, 220160, -3678, 65232);
	private static final Location MANAGER_SPAWN_HEINE4 = new Location(111384, 221147, -3544, 47944);
	private static final Location MANAGER_SPAWN_GLUDIO1 = new Location(-14372, 123793, -3120, 16706);
	private static final Location MANAGER_SPAWN_GLUDIO2 = new Location(-14377, 123458, -3123, 47750);
	private static final Location MANAGER_SPAWN_GODDARD1 = new Location(147694, -55534, -2735, 49151);
	private static final Location MANAGER_SPAWN_GODDARD2 = new Location(147441, -56470, -2782, 7082);
	private static final Location MANAGER_SPAWN_GODDARD3 = new Location(147974, -56492, -2782, 17453);
	private static final Location MANAGER_SPAWN_DION1 = new Location(15776, 142869, -2707, 15247);
	private static final Location MANAGER_SPAWN_DION2 = new Location(18276, 145121, -3065, 6411);
	private static final Location MANAGER_SPAWN_DION3 = new Location(18562, 145915, -3073, 46754);
	private static final Location MANAGER_SPAWN_RUNE1 = new Location(43714, -47713, -798, 55040);
	private static final Location MANAGER_SPAWN_RUNE2 = new Location(43854, -48474, -798, 16303);
	private static final Location MANAGER_SPAWN_RUNE3 = new Location(43471, -50116, -798, 62376);
	private static final Location MANAGER_SPAWN_OREN1 = new Location(80949, 54962, -1526, 32968);
	private static final Location MANAGER_SPAWN_OREN2 = new Location(82617, 55647, -1526, 39050);
	private static final Location MANAGER_SPAWN_OREN3 = new Location(82567, 53142, -1497, 8191);
	private static final Location MANAGER_SPAWN_FAERON1 = new Location(-80108, 248492, -3478, 42468);
	private static final Location MANAGER_SPAWN_FAERON2 = new Location(-80900, 248038, -3485, 59180);
	private static final Location MANAGER_SPAWN_FAERON3 = new Location(-80386, 247262, -3467, 13674);
	private static final Location MANAGER_SPAWN_GLUDIN1 = new Location(-80685, 149785, -3046, 18577);
	private static final Location MANAGER_SPAWN_GLUDIN2 = new Location(-82037, 150186, -3131, 17078);
	private static final Location MANAGER_SPAWN_GLUDIN3 = new Location(-83032, 150864, -3131, 0);
	private static final Location MANAGER_SPAWN_FLORAN1 = new Location(17521, 170617, -3500, 48663);
	private static final Location MANAGER_SPAWN_FLORAN2 = new Location(17608, 169758, -3499, 15622);
	private static final Location MANAGER_SPAWN_ARCAN1 = new Location(207124, 88078, -1123, 49625);
	private static final Location MANAGER_SPAWN_HUNTERS1 = new Location(116973, 77071, -2696, 41481);
	private static final Location MANAGER_SPAWN_SHUTGRD1 = new Location(87369, -143194, -1295, 16433);
	
	// event buffers
	private static final Location BLUE_BUFFER_SPAWN_LOC = new Location(147450, 46913, -3400, 49000);
	private static final Location RED_BUFFER_SPAWN_LOC = new Location(151545, 46528, -3400, 16000);
	private static final Location BLUE_SPAWN_LOC = new Location(147447, 46722, -3416);
	private static final Location RED_SPAWN_LOC = new Location(151536, 46722, -3416);
	private static final ZoneType BLUE_PEACE_ZONE = ZoneManager.getInstance().getZoneByName("colosseum_peace1");
	private static final ZoneType RED_PEACE_ZONE = ZoneManager.getInstance().getZoneByName("colosseum_peace2");
	// Settings
	private static final int REGISTRATION_TIME = 3; // Minutes
	private static final int WAIT_TIME = 1; // Minutes
	private static final int FIGHT_TIME = 5; // Minutes
	private static final int INACTIVITY_TIME = 2; // Minutes
	static final int MINIMUM_PARTICIPANT_LEVEL = 85;
	static final int MAXIMUM_PARTICIPANT_LEVEL = 200;
	private static final int MINIMUM_PARTICIPANT_COUNT = 1;
	static final int MAXIMUM_PARTICIPANT_COUNT = 200; // Scoreboard has 25 slots
	private static final int PARTY_MEMBER_COUNT = 7;
	private static final ItemHolder REWARD = new ItemHolder(57, 1000000); // Adena
	// Misc
	static final Map<Player, Integer> PLAYER_SCORES = new ConcurrentHashMap<>();
	static final Set<Player> PLAYER_LIST = ConcurrentHashMap.newKeySet();
	static final Set<Player> BLUE_TEAM = ConcurrentHashMap.newKeySet();
	static final Set<Player> RED_TEAM = ConcurrentHashMap.newKeySet();
	private static Npc MANAGER_NPC_INSTANCE1 = null;
	private static Npc MANAGER_NPC_INSTANCE2 = null;
	private static Npc MANAGER_NPC_INSTANCE3 = null;
	private static Npc MANAGER_NPC_INSTANCE4 = null;
	private static Npc MANAGER_NPC_INSTANCE5 = null;
	private static Npc MANAGER_NPC_INSTANCE6 = null;
	private static Npc MANAGER_NPC_INSTANCE7 = null;
	private static Npc MANAGER_NPC_INSTANCE8 = null;
	private static Npc MANAGER_NPC_INSTANCE9 = null;
	private static Npc MANAGER_NPC_INSTANCE10 = null;
	private static Npc MANAGER_NPC_INSTANCE11 = null;
	private static Npc MANAGER_NPC_INSTANCE12 = null;
	private static Npc MANAGER_NPC_INSTANCE13 = null;
	private static Npc MANAGER_NPC_INSTANCE14 = null;
	private static Npc MANAGER_NPC_INSTANCE15 = null;
	private static Npc MANAGER_NPC_INSTANCE16 = null;
	private static Npc MANAGER_NPC_INSTANCE17 = null;
	private static Npc MANAGER_NPC_INSTANCE18 = null;
	private static Npc MANAGER_NPC_INSTANCE19 = null;
	private static Npc MANAGER_NPC_INSTANCE20 = null;
	private static Npc MANAGER_NPC_INSTANCE21 = null;
	private static Npc MANAGER_NPC_INSTANCE22 = null;
	private static Npc MANAGER_NPC_INSTANCE23 = null;
	private static Npc MANAGER_NPC_INSTANCE24 = null;
	private static Npc MANAGER_NPC_INSTANCE25 = null;
	private static Npc MANAGER_NPC_INSTANCE26 = null;
	private static Npc MANAGER_NPC_INSTANCE27 = null;
	private static Npc MANAGER_NPC_INSTANCE28 = null;
	private static Npc MANAGER_NPC_INSTANCE29 = null;
	private static Npc MANAGER_NPC_INSTANCE30 = null;
	private static Npc MANAGER_NPC_INSTANCE31 = null;
	private static Npc MANAGER_NPC_INSTANCE32 = null;
	private static Npc MANAGER_NPC_INSTANCE33 = null;
	private static Npc MANAGER_NPC_INSTANCE34 = null;
	private static Npc MANAGER_NPC_INSTANCE35 = null;
	private static Npc MANAGER_NPC_INSTANCE36 = null;
	private static Npc MANAGER_NPC_INSTANCE37 = null;
	private static Npc MANAGER_NPC_INSTANCE38 = null;
	
	private static volatile int BLUE_SCORE;
	private static volatile int RED_SCORE;
	private static Instance PVP_WORLD = null;
	public static boolean EVENT_ACTIVE = false;
	
	private TvT()
	{
		addTalkId(MANAGER);
		addFirstTalkId(MANAGER);
		addExitZoneId(BLUE_PEACE_ZONE.getId(), RED_PEACE_ZONE.getId());
		addEnterZoneId(BLUE_PEACE_ZONE.getId(), RED_PEACE_ZONE.getId());
		
		// Daily task to start event at 20:00.
		final Calendar calendar = Calendar.getInstance();
		for (int hour = 1; hour <= 24; hour++)
		{
			eventScheduler(calendar, hour);
		}
		
		eventScheduler2(calendar);
	}
	
	private void eventScheduler2(Calendar calendar)
	{
	}
	
	private void eventScheduler(Calendar calendar, int hour)
	{
		if ((calendar.get(Calendar.HOUR_OF_DAY) >= hour) && (calendar.get(Calendar.MINUTE) >= 0))
		{
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		ThreadPool.scheduleAtFixedRate(() -> eventStart(null), calendar.getTimeInMillis() - System.currentTimeMillis(), 86400000);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		
		if (!EVENT_ACTIVE)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "Participate":
			{
				
				if (canRegister(player))
				{
					if ((Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP == 0) || AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.L2EVENT_ID, player, Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP))
					{
						PLAYER_LIST.add(player);
						PLAYER_SCORES.put(player, 0);
						player.setRegisteredOnEvent(true);
						addLogoutListener(player);
						htmltext = "registration-success.html";
					}
					else
					{
						htmltext = "registration-ip.html";
					}
				}
				else
				{
					htmltext = "registration-failed.html";
				}
				break;
			}
			case "CancelParticipation":
			{
				if (player.isOnEvent())
				{
					return null;
				}
				// Remove the player from the IP count
				if (Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP > 0)
				{
					AntiFeedManager.getInstance().removePlayer(AntiFeedManager.L2EVENT_ID, player);
				}
				PLAYER_LIST.remove(player);
				PLAYER_SCORES.remove(player);
				removeListeners(player);
				player.setRegisteredOnEvent(false);
				htmltext = "registration-canceled.html";
				break;
			}
			case "BuffHeal":
			{
				if (player.isOnEvent() || player.isGM())
				{
					if (player.isInCombat())
					{
						htmltext = "manager-combat.html";
					}
					else
					{
						for (SkillHolder holder : GROUP_BUFFS)
						{
							SkillCaster.triggerCast(npc, player, holder.getSkill());
						}
						if (player.isMageClass())
						{
							SkillCaster.triggerCast(npc, player, WIZARD.getSkill());
						}
						else if (player.isInCategory(CategoryType.KNIGHT_GROUP))
						{
							SkillCaster.triggerCast(npc, player, KNIGHT.getSkill());
						}
						else
						{
							SkillCaster.triggerCast(npc, player, WARRIOR.getSkill());
						}
						player.setCurrentHp(player.getMaxHp());
						player.setCurrentMp(player.getMaxMp());
						player.setCurrentCp(player.getMaxCp());
					}
				}
				break;
			}
			case "TeleportToArena":
			{
				Broadcast.toAllOnlinePlayersOnScreen("TVT Event has Started!");
				// Remove offline players.
				for (Player participant : PLAYER_LIST)
				{
					if ((participant == null) || (participant.isOnlineInt() != 1))
					{
						PLAYER_LIST.remove(participant);
						PLAYER_SCORES.remove(participant);
					}
				}
				// Check if there are enough players to start the event.
				if (PLAYER_LIST.size() < MINIMUM_PARTICIPANT_COUNT)
				{
					Broadcast.toAllOnlinePlayers("TvT Event: Event was canceled, not enough participants.");
					for (Player participant : PLAYER_LIST)
					{
						removeListeners(participant);
						participant.setRegisteredOnEvent(false);
					}
					EVENT_ACTIVE = false;
					return null;
				}
				// Create the instance.
				final InstanceManager manager = InstanceManager.getInstance();
				final InstanceTemplate template = manager.getInstanceTemplate(INSTANCE_ID);
				PVP_WORLD = manager.createInstance(template, null);
				// Randomize player list and separate teams.
				final List<Player> playerList = new ArrayList<>(PLAYER_LIST.size());
				playerList.addAll(PLAYER_LIST);
				Collections.shuffle(playerList);
				PLAYER_LIST.clear();
				PLAYER_LIST.addAll(playerList);
				boolean team = getRandomBoolean(); // If teams are not even, randomize where extra player goes.
				for (Player participant : PLAYER_LIST)
				{
					participant.setOnEvent(true);
					participant.setRegisteredOnEvent(false);
					if (team)
					{
						BLUE_TEAM.add(participant);
						PVP_WORLD.addAllowed(participant);
						participant.leaveParty();
						participant.setTeam(Team.BLUE);
						participant.teleToLocation(BLUE_SPAWN_LOC, PVP_WORLD);
						team = false;
					}
					else
					{
						RED_TEAM.add(participant);
						PVP_WORLD.addAllowed(participant);
						participant.leaveParty();
						participant.setTeam(Team.RED);
						participant.teleToLocation(RED_SPAWN_LOC, PVP_WORLD);
						team = true;
					}
					addDeathListener(participant);
				}
				// Make Blue CC.
				if (BLUE_TEAM.size() > 1)
				{
					CommandChannel blueCC = null;
					Party lastBlueParty = null;
					int blueParticipantCounter = 0;
					for (Player participant : BLUE_TEAM)
					{
						blueParticipantCounter++;
						if (blueParticipantCounter == 1)
						{
							lastBlueParty = new Party(participant, PartyDistributionType.FINDERS_KEEPERS);
							participant.joinParty(lastBlueParty);
							if (BLUE_TEAM.size() > PARTY_MEMBER_COUNT)
							{
								if (blueCC == null)
								{
									blueCC = new CommandChannel(participant);
								}
								else
								{
									blueCC.addParty(lastBlueParty);
								}
							}
						}
						else
						{
							participant.joinParty(lastBlueParty);
						}
						if (blueParticipantCounter == PARTY_MEMBER_COUNT)
						{
							blueParticipantCounter = 0;
						}
					}
				}
				// Make Red CC.
				if (RED_TEAM.size() > 1)
				{
					CommandChannel redCC = null;
					Party lastRedParty = null;
					int redParticipantCounter = 0;
					for (Player participant : RED_TEAM)
					{
						redParticipantCounter++;
						if (redParticipantCounter == 1)
						{
							lastRedParty = new Party(participant, PartyDistributionType.FINDERS_KEEPERS);
							participant.joinParty(lastRedParty);
							if (RED_TEAM.size() > PARTY_MEMBER_COUNT)
							{
								if (redCC == null)
								{
									redCC = new CommandChannel(participant);
								}
								else
								{
									redCC.addParty(lastRedParty);
								}
							}
						}
						else
						{
							participant.joinParty(lastRedParty);
						}
						if (redParticipantCounter == PARTY_MEMBER_COUNT)
						{
							redParticipantCounter = 0;
						}
					}
				}
				// Spawn managers.
				addSpawn(MANAGER, BLUE_BUFFER_SPAWN_LOC, false, (WAIT_TIME + FIGHT_TIME) * 60000, false, PVP_WORLD.getId());
				addSpawn(MANAGER, RED_BUFFER_SPAWN_LOC, false, (WAIT_TIME + FIGHT_TIME) * 60000, false, PVP_WORLD.getId());
				// Initialize scores.
				BLUE_SCORE = 0;
				RED_SCORE = 0;
				// Initialize scoreboard.
				PVP_WORLD.broadcastPacket(new ExPVPMatchCCRecord(ExPVPMatchCCRecord.INITIALIZE, Util.sortByValue(PLAYER_SCORES, true)));
				// Schedule start.
				startQuestTimer("5", (WAIT_TIME * 60000) - 5000, null, null);
				startQuestTimer("4", (WAIT_TIME * 60000) - 4000, null, null);
				startQuestTimer("3", (WAIT_TIME * 60000) - 3000, null, null);
				startQuestTimer("2", (WAIT_TIME * 60000) - 2000, null, null);
				startQuestTimer("1", (WAIT_TIME * 60000) - 1000, null, null);
				startQuestTimer("StartFight", WAIT_TIME * 60000, null, null);
				break;
			}
			case "minuteReg":
			{
				Broadcast.toAllOnlinePlayers("TVT Event: 1 minute left to register!");
				startQuestTimer("30 seconds", 30000, null, null);
				startQuestTimer("10 seconds", 50000, null, null);
				startQuestTimer("5 seconds", 55000, null, null);
				startQuestTimer("4 seconds", 56000, null, null);
				startQuestTimer("3 seconds", 57000, null, null);
				startQuestTimer("2 seconds", 58000, null, null);
				startQuestTimer("1 second", 59000, null, null);
				break;
			}
			case "30 seconds":
			case "10 seconds":
			case "5 seconds":
			case "4 seconds":
			case "3 seconds":
			case "2 seconds":
			case "1 second":
			{
				Broadcast.toAllOnlinePlayers(String.format("TVT Event: %s left to register!", event));
				break;
			}
			case "StartFight":
			{
				// Open doors.
				openDoor(BLUE_DOOR_ID, PVP_WORLD.getId());
				openDoor(RED_DOOR_ID, PVP_WORLD.getId());
				// Send message.
				broadcastScreenMessageWithEffect("The fight has began!", 5);
				// Schedule finish.
				startQuestTimer("10", (FIGHT_TIME * 60000) - 10000, null, null);
				startQuestTimer("9", (FIGHT_TIME * 60000) - 9000, null, null);
				startQuestTimer("8", (FIGHT_TIME * 60000) - 8000, null, null);
				startQuestTimer("7", (FIGHT_TIME * 60000) - 7000, null, null);
				startQuestTimer("6", (FIGHT_TIME * 60000) - 6000, null, null);
				startQuestTimer("5", (FIGHT_TIME * 60000) - 5000, null, null);
				startQuestTimer("4", (FIGHT_TIME * 60000) - 4000, null, null);
				startQuestTimer("3", (FIGHT_TIME * 60000) - 3000, null, null);
				startQuestTimer("2", (FIGHT_TIME * 60000) - 2000, null, null);
				startQuestTimer("1", (FIGHT_TIME * 60000) - 1000, null, null);
				startQuestTimer("EndFight", FIGHT_TIME * 60000, null, null);
				break;
			}
			case "EndFight":
			{
				// Close doors.
				closeDoor(BLUE_DOOR_ID, PVP_WORLD.getId());
				closeDoor(RED_DOOR_ID, PVP_WORLD.getId());
				// Disable players.
				for (Player participant : PLAYER_LIST)
				{
					participant.setInvul(true);
					participant.setImmobilized(true);
					participant.disableAllSkills();
					for (Summon summon : participant.getServitors().values())
					{
						summon.setInvul(true);
						summon.setImmobilized(true);
						summon.disableAllSkills();
					}
				}
				// Make sure noone is dead.
				for (Player participant : PLAYER_LIST)
				{
					if (participant.isDead())
					{
						participant.doRevive();
					}
				}
				// Team Blue wins.
				if (BLUE_SCORE > RED_SCORE)
				{
					final Skill skill = CommonSkill.FIREWORK.getSkill();
					broadcastScreenMessageWithEffect("Team Blue won the event!", 7);
					for (Player participant : BLUE_TEAM)
					{
						if ((participant != null) && (participant.getInstanceWorld() == PVP_WORLD))
						{
							participant.broadcastPacket(new MagicSkillUse(participant, participant, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()));
							participant.broadcastSocialAction(3);
							giveItems(participant, REWARD);
						}
					}
				}
				// Team Red wins.
				else if (RED_SCORE > BLUE_SCORE)
				{
					final Skill skill = CommonSkill.FIREWORK.getSkill();
					broadcastScreenMessageWithEffect("Team Red won the event!", 7);
					for (Player participant : RED_TEAM)
					{
						if ((participant != null) && (participant.getInstanceWorld() == PVP_WORLD))
						{
							participant.broadcastPacket(new MagicSkillUse(participant, participant, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()));
							participant.broadcastSocialAction(3);
							giveItems(participant, REWARD);
						}
					}
				}
				// Tie.
				else
				{
					broadcastScreenMessageWithEffect("The event ended with a tie!", 7);
					for (Player participant : PLAYER_LIST)
					{
						participant.broadcastSocialAction(13);
					}
				}
				startQuestTimer("ScoreBoard", 3500, null, null);
				startQuestTimer("TeleportOut", 7000, null, null);
				break;
			}
			case "ScoreBoard":
			{
				PVP_WORLD.broadcastPacket(new ExPVPMatchCCRecord(ExPVPMatchCCRecord.FINISH, Util.sortByValue(PLAYER_SCORES, true)));
				break;
			}
			case "TeleportOut":
			{
				// Remove event listeners.
				for (Player participant : PLAYER_LIST)
				{
					removeListeners(participant);
					participant.setTeam(Team.NONE);
					participant.setOnEvent(false);
					participant.leaveParty();
				}
				// Destroy world.
				if (PVP_WORLD != null)
				{
					PVP_WORLD.destroy();
					PVP_WORLD = null;
				}
				// Enable players.
				for (Player participant : PLAYER_LIST)
				{
					participant.setInvul(false);
					participant.setImmobilized(false);
					participant.enableAllSkills();
					for (Summon summon : participant.getServitors().values())
					{
						summon.setInvul(true);
						summon.setImmobilized(true);
						summon.disableAllSkills();
					}
				}
				EVENT_ACTIVE = false;
				break;
			}
			case "ResurrectPlayer":
			{
				if (player.isDead() && player.isOnEvent())
				{
					if (BLUE_TEAM.contains(player))
					{
						player.setIsPendingRevive(true);
						player.teleToLocation(BLUE_SPAWN_LOC, false, PVP_WORLD);
						// Make player invulnerable for 30 seconds.
						GHOST_WALKING.getSkill().applyEffects(player, player);
						// Reset existing activity timers.
						resetActivityTimers(player); // In case player died in peace zone.
					}
					else if (RED_TEAM.contains(player))
					{
						player.setIsPendingRevive(true);
						player.teleToLocation(RED_SPAWN_LOC, false, PVP_WORLD);
						// Make player invulnerable for 30 seconds.
						GHOST_WALKING.getSkill().applyEffects(player, player);
						// Reset existing activity timers.
						resetActivityTimers(player); // In case player died in peace zone.
					}
				}
				break;
			}
			case "10":
			case "9":
			case "8":
			case "7":
			case "6":
			case "5":
			case "4":
			case "3":
			case "2":
			case "1":
			{
				broadcastScreenMessage(event, 4);
				break;
			}
		}
		// Activity timer.
		if (event.startsWith("KickPlayer") && (player != null) && (player.getInstanceWorld() == PVP_WORLD))
		{
			if (event.contains("Warning"))
			{
				sendScreenMessage(player, "You have been marked as inactive!", 10);
			}
			else
			{
				player.setTeam(Team.NONE);
				PVP_WORLD.ejectPlayer(player);
				PLAYER_LIST.remove(player);
				PLAYER_SCORES.remove(player);
				BLUE_TEAM.remove(player);
				RED_TEAM.remove(player);
				player.setOnEvent(false);
				removeListeners(player);
				player.sendMessage("You have been kicked for been inactive.");
				if (PVP_WORLD != null)
				{
					// Manage forfeit.
					if ((BLUE_TEAM.isEmpty() && !RED_TEAM.isEmpty()) || //
						(RED_TEAM.isEmpty() && !BLUE_TEAM.isEmpty()))
					{
						manageForfeit();
					}
					else
					{
						broadcastScreenMessageWithEffect("Player " + player.getName() + " was kicked for been inactive!", 7);
					}
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		// Event not active.
		if (!EVENT_ACTIVE)
		{
			return null;
		}
		
		// Player has already registered.
		if (PLAYER_LIST.contains(player))
		{
			// Npc is in instance.
			if (npc.getInstanceWorld() != null)
			{
				return "manager-buffheal.html";
			}
			return "manager-cancel.html";
		}
		// Player is not registered.
		return "manager-register.html";
	}
	
	@Override
	public String onEnterZone(Creature creature, ZoneType zone)
	{
		if (creature.isPlayable() && creature.getActingPlayer().isOnEvent())
		{
			// Kick enemy players.
			if ((zone == BLUE_PEACE_ZONE) && (creature.getTeam() == Team.RED))
			{
				creature.teleToLocation(RED_SPAWN_LOC, PVP_WORLD);
				sendScreenMessage(creature.getActingPlayer(), "Entering the enemy headquarters is prohibited!", 10);
			}
			if ((zone == RED_PEACE_ZONE) && (creature.getTeam() == Team.BLUE))
			{
				creature.teleToLocation(BLUE_SPAWN_LOC, PVP_WORLD);
				sendScreenMessage(creature.getActingPlayer(), "Entering the enemy headquarters is prohibited!", 10);
			}
			// Start inactivity check.
			if (creature.isPlayer() && //
				(((zone == BLUE_PEACE_ZONE) && (creature.getTeam() == Team.BLUE)) || //
					((zone == RED_PEACE_ZONE) && (creature.getTeam() == Team.RED))))
			{
				resetActivityTimers(creature.getActingPlayer());
			}
		}
		return null;
	}
	
	@Override
	public String onExitZone(Creature creature, ZoneType zone)
	{
		if (creature.isPlayer() && creature.getActingPlayer().isOnEvent())
		{
			final Player player = creature.getActingPlayer();
			cancelQuestTimer("KickPlayer" + creature.getObjectId(), null, player);
			cancelQuestTimer("KickPlayerWarning" + creature.getObjectId(), null, player);
			// Removed invulnerability shield.
			if (player.isAffectedBySkill(GHOST_WALKING))
			{
				player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, GHOST_WALKING.getSkill());
			}
		}
		return super.onExitZone(creature, zone);
	}
	
	private boolean canRegister(Player player)
	{
		if (PLAYER_LIST.contains(player))
		{
			player.sendMessage("You are already registered on this event.");
			return false;
		}
		if (player.getLevel() < MINIMUM_PARTICIPANT_LEVEL)
		{
			player.sendMessage("Your level is too low to participate.");
			return false;
		}
		if (player.getLevel() > MAXIMUM_PARTICIPANT_LEVEL)
		{
			player.sendMessage("Your level is too high to participate.");
			return false;
		}
		if (player.isRegisteredOnEvent() || (player.getBlockCheckerArena() > -1))
		{
			player.sendMessage("You are already registered on an event.");
			return false;
		}
		if (PLAYER_LIST.size() >= MAXIMUM_PARTICIPANT_COUNT)
		{
			player.sendMessage("There are too many players registered on the event.");
			return false;
		}
		if (player.isFlyingMounted())
		{
			player.sendMessage("You cannot register on the event while flying.");
			return false;
		}
		if (player.isTransformed())
		{
			player.sendMessage("You cannot register on the event while on a transformed state.");
			return false;
		}
		if (!player.isInventoryUnder80(false))
		{
			player.sendMessage("There are too many items in your inventory.");
			player.sendMessage("Try removing some items.");
			return false;
		}
		if ((player.getWeightPenalty() != 0))
		{
			player.sendMessage("Your invetory weight has exceeded the normal limit.");
			player.sendMessage("Try removing some items.");
			return false;
		}
		if (player.isCursedWeaponEquipped() || (player.getReputation() < 0))
		{
			player.sendMessage("People with bad reputation can't register.");
			return false;
		}
		if (player.isInDuel())
		{
			player.sendMessage("You cannot register while on a duel.");
			return false;
		}
		if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			player.sendMessage("You cannot participate while registered on the Olympiad.");
			return false;
		}
		if (player.isInInstance())
		{
			player.sendMessage("You cannot register while in an instance.");
			return false;
		}
		if (player.isInSiege() || player.isInsideZone(ZoneId.SIEGE))
		{
			player.sendMessage("You cannot register while on a siege.");
			return false;
		}
		if (player.isFishing())
		{
			player.sendMessage("You cannot register while fishing.");
			return false;
		}
		return true;
	}
	
	private void sendScreenMessage(Player player, String message, int duration)
	{
		player.sendPacket(new ExShowScreenMessage(message, ExShowScreenMessage.TOP_CENTER, duration * 1000, 0, true, false));
	}
	
	private void broadcastScreenMessage(String message, int duration)
	{
		PVP_WORLD.broadcastPacket(new ExShowScreenMessage(message, ExShowScreenMessage.TOP_CENTER, duration * 1000, 0, true, false));
	}
	
	private void broadcastScreenMessageWithEffect(String message, int duration)
	{
		PVP_WORLD.broadcastPacket(new ExShowScreenMessage(message, ExShowScreenMessage.TOP_CENTER, duration * 1000, 0, true, true));
	}
	
	private void broadcastScoreMessage()
	{
		PVP_WORLD.broadcastPacket(new ExShowScreenMessage("Blue: " + BLUE_SCORE + " - Red: " + RED_SCORE, ExShowScreenMessage.BOTTOM_RIGHT, 15000, 0, true, false));
	}
	
	private void addLogoutListener(Player player)
	{
		player.addListener(new ConsumerEventListener(player, EventType.ON_PLAYER_LOGOUT, (OnPlayerLogout event) -> onPlayerLogout(event), this));
	}
	
	private void addDeathListener(Player player)
	{
		player.addListener(new ConsumerEventListener(player, EventType.ON_CREATURE_DEATH, (OnCreatureDeath event) -> onPlayerDeath(event), this));
	}
	
	private void removeListeners(Player player)
	{
		for (AbstractEventListener listener : player.getListeners(EventType.ON_PLAYER_LOGOUT))
		{
			if (listener.getOwner() == this)
			{
				listener.unregisterMe();
			}
		}
		for (AbstractEventListener listener : player.getListeners(EventType.ON_CREATURE_DEATH))
		{
			if (listener.getOwner() == this)
			{
				listener.unregisterMe();
			}
		}
	}
	
	private void resetActivityTimers(Player player)
	{
		cancelQuestTimer("KickPlayer" + player.getObjectId(), null, player);
		cancelQuestTimer("KickPlayerWarning" + player.getObjectId(), null, player);
		startQuestTimer("KickPlayer" + player.getObjectId(), PVP_WORLD.getDoor(BLUE_DOOR_ID).isOpen() ? INACTIVITY_TIME * 60000 : (INACTIVITY_TIME * 60000) + (WAIT_TIME * 60000), null, player);
		startQuestTimer("KickPlayerWarning" + player.getObjectId(), PVP_WORLD.getDoor(BLUE_DOOR_ID).isOpen() ? (INACTIVITY_TIME / 2) * 60000 : ((INACTIVITY_TIME / 2) * 60000) + (WAIT_TIME * 60000), null, player);
	}
	
	private void manageForfeit()
	{
		cancelQuestTimer("10", null, null);
		cancelQuestTimer("9", null, null);
		cancelQuestTimer("8", null, null);
		cancelQuestTimer("7", null, null);
		cancelQuestTimer("6", null, null);
		cancelQuestTimer("5", null, null);
		cancelQuestTimer("4", null, null);
		cancelQuestTimer("3", null, null);
		cancelQuestTimer("2", null, null);
		cancelQuestTimer("1", null, null);
		cancelQuestTimer("EndFight", null, null);
		startQuestTimer("EndFight", 10000, null, null);
		broadcastScreenMessageWithEffect("Enemy team forfeit!", 7);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGOUT)
	private void onPlayerLogout(OnPlayerLogout event)
	{
		final Player player = event.getPlayer();
		// Remove player from lists.
		PLAYER_LIST.remove(player);
		PLAYER_SCORES.remove(player);
		BLUE_TEAM.remove(player);
		RED_TEAM.remove(player);
		// Manage forfeit.
		if ((BLUE_TEAM.isEmpty() && !RED_TEAM.isEmpty()) || //
			(RED_TEAM.isEmpty() && !BLUE_TEAM.isEmpty()))
		{
			manageForfeit();
		}
	}
	
	@RegisterEvent(EventType.ON_CREATURE_DEATH)
	public void onPlayerDeath(OnCreatureDeath event)
	{
		if (event.getTarget().isPlayer())
		{
			final Player killedPlayer = event.getTarget().getActingPlayer();
			final Player killer = event.getAttacker().getActingPlayer();
			// Confirm Blue team kill.
			if ((killer.getTeam() == Team.BLUE) && (killedPlayer.getTeam() == Team.RED))
			{
				PLAYER_SCORES.put(killer, PLAYER_SCORES.get(killer) + 1);
				BLUE_SCORE++;
				broadcastScoreMessage();
				PVP_WORLD.broadcastPacket(new ExPVPMatchCCRecord(ExPVPMatchCCRecord.UPDATE, Util.sortByValue(PLAYER_SCORES, true)));
			}
			// Confirm Red team kill.
			if ((killer.getTeam() == Team.RED) && (killedPlayer.getTeam() == Team.BLUE))
			{
				PLAYER_SCORES.put(killer, PLAYER_SCORES.get(killer) + 1);
				RED_SCORE++;
				broadcastScoreMessage();
				PVP_WORLD.broadcastPacket(new ExPVPMatchCCRecord(ExPVPMatchCCRecord.UPDATE, Util.sortByValue(PLAYER_SCORES, true)));
			}
			// Auto release after 10 seconds.
			startQuestTimer("ResurrectPlayer", 10000, null, killedPlayer);
		}
	}
	
	@Override
	public boolean eventStart(Player eventMaker)
	{
		
		if (EVENT_ACTIVE)
		{
			return false;
		}
		EVENT_ACTIVE = true;
		
		// Cancel timers. (In case event started immediately after another event was canceled.)
		for (List<QuestTimer> timers : getQuestTimers().values())
		{
			for (QuestTimer timer : timers) {
				timer.cancel();
			}
		}
		// Register the event at AntiFeedManager and clean it for just in case if the event is already registered
		if (Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP > 0)
		{
			AntiFeedManager.getInstance().registerEvent(AntiFeedManager.L2EVENT_ID);
			AntiFeedManager.getInstance().clear(AntiFeedManager.L2EVENT_ID);
		}
		// Clear player lists.
		PLAYER_LIST.clear();
		PLAYER_SCORES.clear();
		BLUE_TEAM.clear();
		RED_TEAM.clear();
		// Spawn event manager.
		MANAGER_NPC_INSTANCE1 = addSpawn(MANAGER, MANAGER_SPAWN_GIRAN1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE2 = addSpawn(MANAGER, MANAGER_SPAWN_GIRAN2, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE3 = addSpawn(MANAGER, MANAGER_SPAWN_GIRAN3, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE4 = addSpawn(MANAGER, MANAGER_SPAWN_ADEN1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE5 = addSpawn(MANAGER, MANAGER_SPAWN_ADEN2, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE6 = addSpawn(MANAGER, MANAGER_SPAWN_ADEN3, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE7 = addSpawn(MANAGER, MANAGER_SPAWN_TI1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE8 = addSpawn(MANAGER, MANAGER_SPAWN_TI2, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE9 = addSpawn(MANAGER, MANAGER_SPAWN_TI3, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE10 = addSpawn(MANAGER, MANAGER_SPAWN_HEINE1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE11 = addSpawn(MANAGER, MANAGER_SPAWN_HEINE2, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE12 = addSpawn(MANAGER, MANAGER_SPAWN_HEINE3, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE13 = addSpawn(MANAGER, MANAGER_SPAWN_HEINE4, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE14 = addSpawn(MANAGER, MANAGER_SPAWN_GLUDIO1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE15 = addSpawn(MANAGER, MANAGER_SPAWN_GLUDIO2, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE16 = addSpawn(MANAGER, MANAGER_SPAWN_GODDARD1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE17 = addSpawn(MANAGER, MANAGER_SPAWN_GODDARD2, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE18 = addSpawn(MANAGER, MANAGER_SPAWN_GODDARD3, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE19 = addSpawn(MANAGER, MANAGER_SPAWN_DION1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE20 = addSpawn(MANAGER, MANAGER_SPAWN_DION2, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE21 = addSpawn(MANAGER, MANAGER_SPAWN_DION3, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE22 = addSpawn(MANAGER, MANAGER_SPAWN_RUNE1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE23 = addSpawn(MANAGER, MANAGER_SPAWN_RUNE2, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE24 = addSpawn(MANAGER, MANAGER_SPAWN_RUNE3, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE25 = addSpawn(MANAGER, MANAGER_SPAWN_OREN1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE26 = addSpawn(MANAGER, MANAGER_SPAWN_OREN2, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE27 = addSpawn(MANAGER, MANAGER_SPAWN_OREN3, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE28 = addSpawn(MANAGER, MANAGER_SPAWN_FAERON1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE29 = addSpawn(MANAGER, MANAGER_SPAWN_FAERON2, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE30 = addSpawn(MANAGER, MANAGER_SPAWN_FAERON3, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE31 = addSpawn(MANAGER, MANAGER_SPAWN_GLUDIN1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE32 = addSpawn(MANAGER, MANAGER_SPAWN_GLUDIN2, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE33 = addSpawn(MANAGER, MANAGER_SPAWN_GLUDIN3, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE34 = addSpawn(MANAGER, MANAGER_SPAWN_FLORAN1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE35 = addSpawn(MANAGER, MANAGER_SPAWN_FLORAN2, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE36 = addSpawn(MANAGER, MANAGER_SPAWN_ARCAN1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE37 = addSpawn(MANAGER, MANAGER_SPAWN_HUNTERS1, false, REGISTRATION_TIME * 60000);
		MANAGER_NPC_INSTANCE38 = addSpawn(MANAGER, MANAGER_SPAWN_SHUTGRD1, false, REGISTRATION_TIME * 60000);
		
		startQuestTimer("TeleportToArena", REGISTRATION_TIME * 60000, null, null);
		// Send message to players.
		Broadcast.toAllOnlinePlayers("TvT Event: Registration opened for " + REGISTRATION_TIME + " minutes.");
		Broadcast.toAllOnlinePlayers("TvT Event: You can register in every town at TvT Event Manager.");
		startQuestTimer("minuteReg", 120000, null, null);
		return true;
	}
	
	public boolean playerRegister(Player player)
	{
		if (canRegister(player))
		{
			if ((Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP == 0) || AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.L2EVENT_ID, player, Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP))
			{
				PLAYER_LIST.add(player);
				PLAYER_SCORES.put(player, 0);
				player.setRegisteredOnEvent(true);
				addLogoutListener(player);
				player.sendMessage("You registered for the event!");
			}
			else
			{
				player.sendMessage("You cant register more than 1 character!");
			}
		}
		else
		{
			player.sendMessage("Registretion failed!");
		}
		return false;
	}
	
	public void playerUnReg(Player player)
	{
		startQuestTimer("CancelParticipation", 1, null, player);
	}
	
	@Override
	public boolean eventStop()
	{
		if (!EVENT_ACTIVE)
		{
			return false;
		}
		EVENT_ACTIVE = false;
		
		// Despawn event manager.
		MANAGER_NPC_INSTANCE1.deleteMe();
		MANAGER_NPC_INSTANCE2.deleteMe();
		MANAGER_NPC_INSTANCE3.deleteMe();
		MANAGER_NPC_INSTANCE4.deleteMe();
		MANAGER_NPC_INSTANCE5.deleteMe();
		MANAGER_NPC_INSTANCE6.deleteMe();
		MANAGER_NPC_INSTANCE7.deleteMe();
		MANAGER_NPC_INSTANCE8.deleteMe();
		MANAGER_NPC_INSTANCE9.deleteMe();
		MANAGER_NPC_INSTANCE10.deleteMe();
		MANAGER_NPC_INSTANCE11.deleteMe();
		MANAGER_NPC_INSTANCE12.deleteMe();
		MANAGER_NPC_INSTANCE13.deleteMe();
		MANAGER_NPC_INSTANCE14.deleteMe();
		MANAGER_NPC_INSTANCE15.deleteMe();
		MANAGER_NPC_INSTANCE16.deleteMe();
		MANAGER_NPC_INSTANCE17.deleteMe();
		MANAGER_NPC_INSTANCE18.deleteMe();
		MANAGER_NPC_INSTANCE19.deleteMe();
		MANAGER_NPC_INSTANCE20.deleteMe();
		MANAGER_NPC_INSTANCE21.deleteMe();
		MANAGER_NPC_INSTANCE22.deleteMe();
		MANAGER_NPC_INSTANCE23.deleteMe();
		MANAGER_NPC_INSTANCE24.deleteMe();
		MANAGER_NPC_INSTANCE25.deleteMe();
		MANAGER_NPC_INSTANCE26.deleteMe();
		MANAGER_NPC_INSTANCE27.deleteMe();
		MANAGER_NPC_INSTANCE28.deleteMe();
		MANAGER_NPC_INSTANCE29.deleteMe();
		MANAGER_NPC_INSTANCE30.deleteMe();
		MANAGER_NPC_INSTANCE31.deleteMe();
		MANAGER_NPC_INSTANCE32.deleteMe();
		MANAGER_NPC_INSTANCE33.deleteMe();
		MANAGER_NPC_INSTANCE34.deleteMe();
		MANAGER_NPC_INSTANCE35.deleteMe();
		MANAGER_NPC_INSTANCE36.deleteMe();
		MANAGER_NPC_INSTANCE37.deleteMe();
		MANAGER_NPC_INSTANCE38.deleteMe();
		
		// Cancel timers.
		for (List<QuestTimer> timers : getQuestTimers().values())
		{
			for (QuestTimer timer : timers)
			{
				timer.cancel();
			}
		}
		// Remove participants.
		for (Player participant : PLAYER_LIST)
		{
			removeListeners(participant);
			participant.setTeam(Team.NONE);
			participant.setRegisteredOnEvent(false);
			participant.setOnEvent(false);
		}
		if (PVP_WORLD != null)
		{
			PVP_WORLD.destroy();
			PVP_WORLD = null;
		}
		// Send message to players.
		Broadcast.toAllOnlinePlayers("TvT Event: Event was canceled.");
		return true;
	}
	
	@Override
	public boolean eventBypass(Player player, String bypass)
	{
		
		return false;
	}
	
	public static void main(String[] args)
	{
		new TvT();
	}
	
}
