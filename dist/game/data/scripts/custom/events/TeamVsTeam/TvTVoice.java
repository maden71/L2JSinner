package custom.events.TeamVsTeam;

import static custom.events.TeamVsTeam.TvT.BLUE_TEAM;
import static custom.events.TeamVsTeam.TvT.EVENT_ACTIVE;
import static custom.events.TeamVsTeam.TvT.MAXIMUM_PARTICIPANT_COUNT;
import static custom.events.TeamVsTeam.TvT.MAXIMUM_PARTICIPANT_LEVEL;
import static custom.events.TeamVsTeam.TvT.MINIMUM_PARTICIPANT_LEVEL;
import static custom.events.TeamVsTeam.TvT.PLAYER_LIST;
import static custom.events.TeamVsTeam.TvT.PLAYER_SCORES;
import static custom.events.TeamVsTeam.TvT.RED_TEAM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.handler.IVoicedCommandHandler;
import org.l2jmobius.gameserver.instancemanager.AntiFeedManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogout;
import org.l2jmobius.gameserver.model.events.listeners.AbstractEventListener;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jmobius.gameserver.model.olympiad.OlympiadManager;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

public class TvTVoice implements IVoicedCommandHandler
{
	
	private static final String[] COMMANDS =
	{
		"reg",
		"unreg",
		"join",
		"leave",
	
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String params)
	{
		if (command.startsWith("reg") || command.startsWith("join"))
		{
			if (canRegister(player))
			{
				playerRegister(player);
				
			}
		}
		else if (command.startsWith("unreg") || command.startsWith("leave"))
		{
			playerUnRegister(player);
			
		}
		return false;
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
	
	public boolean playerRegister(Player player)
	{
		if (canRegister(player))
		{
			if ((Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP == 0) || AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.L2EVENT_ID, player, Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP))
			{
				PLAYER_LIST.add(player);
				PLAYER_SCORES.put(player, 0);
				
				player.setRegisteredOnEvent(true);
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
	
	private void addLogoutListener(Player player)
	{
		player.addListener(new ConsumerEventListener(player, EventType.ON_PLAYER_LOGOUT, (OnPlayerLogout event) -> onPlayerLogout(event), this));
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
		
	}
	
	public boolean playerUnRegister(Player player)
	{
		if (player.isOnEvent())
		{
			return false;
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
		player.sendMessage("You unregistered for the event!");
		return false;
	}
	
	private void htmlFileRead(Player player, NpcHtmlMessage msg, StringBuilder html, String filePath)
	{
		try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				html.append(line).append("\n");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		msg.setHtml(html.toString());
		player.sendPacket(msg);
	}
	
	private boolean canRegister(Player player)
	{
		if (!EVENT_ACTIVE)
		{
			player.sendMessage("There is no active events at the moment.");
			return false;
		}
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
		if (player.isInCombat())
		{
			player.sendMessage("You cannot register while in combat.");
			return false;
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}
