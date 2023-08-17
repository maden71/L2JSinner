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
package handlers.playeractions;

import org.l2jmobius.gameserver.handler.IPlayerActionHandler;
import org.l2jmobius.gameserver.instancemanager.CastleManager;
import org.l2jmobius.gameserver.model.ActionDataHolder;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;

/**
 * @author Index
 */
public class WaitingAction implements IPlayerActionHandler
{
	@Override
	public void useAction(Player player, ActionDataHolder data, boolean ctrlPressed, boolean shiftPressed)
	{
		final int actionId = (player.getClan() != null) && (CastleManager.getInstance().getCastleByOwner(player.getClan()) != null) ? player.isClanLeader() ? 100 : 101 : 0;
		if (actionId == 0)
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_NOT_SET_A_LIST_OF_ACTIONS_FOR_THE_WAITING_TIME);
			return;
		}
		
		player.sendPacket(new SocialAction(player.getObjectId(), actionId));
	}
}
