package custom.events.TeamVsTeam;


import org.l2jmobius.gameserver.handler.IVoicedCommandHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.olympiad.OlympiadManager;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static custom.events.TeamVsTeam.TvT.*;

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
    public boolean useVoicedCommand(String command, Player player, String params) {
        if(command.startsWith("reg") || command.startsWith("join")) {
            if(canRegister(player)) {
//            final NpcHtmlMessage msg = new NpcHtmlMessage();
//            final StringBuilder html = new StringBuilder();
//            final String filePath = "C:\\Games\\L2\\l2masterclass3serv\\serv\\L2J_Mobius_10.3_MasterClass\\dist\\game\\data\\scripts\\custom\\events\\TeamVsTeam\\manager-register.html";
//            htmlFileRead(player, msg, html, filePath);
//                final String manager = HtmCache.getInstance().getHtm(player, "data/scripts/custom/events/TeamVsTeam/manager-register.html");
//                player.sendPacket(new NpcHtmlMessage(manager));
                player.teleToLocation(82437,148613,-3470,32646);

            }
        }else if (command.startsWith("unreg") || command.startsWith("leave")) {

//            final NpcHtmlMessage msg = new NpcHtmlMessage(70011);
//            final StringBuilder html = new StringBuilder();
//            final String filePath = "C:\\Games\\L2\\l2masterclass3serv\\serv\\L2J_Mobius_10.3_MasterClass\\dist\\game\\data\\scripts\\custom\\events\\TeamVsTeam\\manager-cancel.html";
//            htmlFileRead(player, msg, html, filePath);
            if (canRegister(player)) {
                player.teleToLocation(82437, 148613, -3470, 32646);
            }
        }
        return false;
    }

    private void htmlFileRead(Player player, NpcHtmlMessage msg, StringBuilder html, String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                html.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        msg.setHtml(html.toString());
        player.sendPacket(msg);
    }

    private boolean canRegister(Player player)
    {
        if(!EVENT_ACTIVE) {
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
        if(player.isInCombat()) {
            player.sendMessage("You cannot register while in combat.");
            return false;
        }
        return true;
    }



    @Override
    public String[] getVoicedCommandList() {
        return COMMANDS;
    }
}
