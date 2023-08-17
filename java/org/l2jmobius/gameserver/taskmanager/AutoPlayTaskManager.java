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
package org.l2jmobius.gameserver.taskmanager;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.Config;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.geoengine.GeoEngine;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.item.Weapon;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.serverpackets.autoplay.ExAutoPlayDoMacro;
import org.l2jmobius.gameserver.util.Util;

/**
 * @author Mobius
 */
public class AutoPlayTaskManager
{
	private static final Set<Set<Player>> POOLS = ConcurrentHashMap.newKeySet();
	private static final int POOL_SIZE = 300;
	private static final int TASK_DELAY = 300;
	
	protected AutoPlayTaskManager()
	{
	}
	
	private class AutoPlay implements Runnable
	{
		private final Set<Player> _players;
		
		public AutoPlay(Set<Player> players)
		{
			_players = players;
		}
		
		@Override
		public void run()
		{
			PLAY: for (Player player : _players)
			{
				if (!player.isOnline() || player.isInOfflineMode() || !Config.ENABLE_AUTO_PLAY)
				{
					stopAutoPlay(player);
					continue PLAY;
				}
				
				if (player.isCastingNow() || (player.getQueuedSkill() != null))
				{
					continue PLAY;
				}
				
				// Next target mode.
				final int targetMode = player.getAutoPlaySettings().getNextTargetMode();
				
				// Skip thinking.
				final WorldObject target = player.getTarget();
				if ((target != null) && target.isCreature())
				{
					final Creature creature = (Creature) target;
					if (creature.isAlikeDead() || !isTargetModeValid(targetMode, player, creature))
					{
						player.setTarget(null);
					}
					else if ((creature.getTarget() == player) || (creature.getTarget() == null))
					{
						// We take granted that mage classes do not auto hit.
						if (isMageCaster(player))
						{
							continue PLAY;
						}
						
						// Check if actually attacking.
						if (player.hasAI() && !player.isAttackingNow() && !player.isCastingNow() && !player.isMoving() && !player.isDisabled())
						{
							if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
							{
								if (creature.isAutoAttackable(player))
								{
									// GeoEngine can see target check.
									if (!GeoEngine.getInstance().canSeeTarget(player, creature))
									{
										player.setTarget(null);
										continue PLAY;
									}
									
									player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, creature);
								}
							}
							else if (creature.hasAI() && !creature.getAI().isAutoAttacking())
							{
								final Weapon weapon = player.getActiveWeaponItem();
								if (weapon != null)
								{
									final boolean ranged = weapon.getItemType().isRanged();
									final double angle = Util.calculateHeadingFrom(player, creature);
									final double radian = Math.toRadians(angle);
									final double course = Math.toRadians(180);
									final double distance = (ranged ? player.getCollisionRadius() : player.getCollisionRadius() + creature.getCollisionRadius()) * 2;
									final int x1 = (int) (Math.cos(Math.PI + radian + course) * distance);
									final int y1 = (int) (Math.sin(Math.PI + radian + course) * distance);
									final Location location;
									if (ranged)
									{
										location = new Location(player.getX() + x1, player.getY() + y1, player.getZ());
									}
									else
									{
										location = new Location(creature.getX() + x1, creature.getY() + y1, player.getZ());
									}
									player.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, location);
								}
							}
						}
						continue PLAY;
					}
				}
				
				// Pickup.
				if (player.getAutoPlaySettings().doPickup())
				{
					PICKUP: for (Item droppedItem : World.getInstance().getVisibleObjectsInRange(player, Item.class, 200))
					{
						// Check if item is reachable.
						if ((droppedItem == null) //
							|| (!droppedItem.isSpawned()) //
							|| !GeoEngine.getInstance().canMoveToTarget(player.getX(), player.getY(), player.getZ(), droppedItem.getX(), droppedItem.getY(), droppedItem.getZ(), player.getInstanceWorld()))
						{
							continue PICKUP;
						}
						
						// Move to item.
						if (player.calculateDistance2D(droppedItem) > 70)
						{
							if (!player.isMoving())
							{
								player.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, droppedItem);
							}
							continue PLAY;
						}
						
						// Try to pick it up.
						if (!droppedItem.isProtected() || (droppedItem.getOwnerId() == player.getObjectId()))
						{
							player.doPickupItem(droppedItem);
							continue PLAY; // Avoid pickup being skipped.
						}
					}
				}
				
				// Find target.
				Creature creature = null;
				double closestDistance = Double.MAX_VALUE;
				TARGET: for (Creature nearby : World.getInstance().getVisibleObjectsInRange(player, Creature.class, player.getAutoPlaySettings().isShortRange() && (targetMode != 2 /* Characters */) ? 600 : 1400))
				{
					// Skip unavailable creatures.
					if ((nearby == null) || nearby.isAlikeDead())
					{
						continue TARGET;
					}
					// Check creature target.
					if (player.getAutoPlaySettings().isRespectfulHunting() && !nearby.isPlayable() && (nearby.getTarget() != null) && (nearby.getTarget() != player) && !player.getServitors().containsKey(nearby.getTarget().getObjectId()))
					{
						continue TARGET;
					}
					// Check next target mode.
					if (!isTargetModeValid(targetMode, player, nearby))
					{
						continue TARGET;
					}
					// Check if creature is reachable.
					if (GeoEngine.getInstance().canSeeTarget(player, nearby) && GeoEngine.getInstance().canMoveToTarget(player.getX(), player.getY(), player.getZ(), nearby.getX(), nearby.getY(), nearby.getZ(), player.getInstanceWorld()))
					{
						final double creatureDistance = player.calculateDistance2D(nearby);
						if (creatureDistance < closestDistance)
						{
							creature = nearby;
							closestDistance = creatureDistance;
						}
					}
				}
				
				// New target was assigned.
				if (creature != null)
				{
					player.setTarget(creature);
					
					// We take granted that mage classes do not auto hit.
					if (isMageCaster(player))
					{
						continue PLAY;
					}
					
					player.sendPacket(ExAutoPlayDoMacro.STATIC_PACKET);
				}
			}
		}
		
		private boolean isMageCaster(Player player)
		{
			// Iss classes considered fighters.
			final int classId = player.getActiveClass();
			if ((classId > 170) && (classId < 176))
			{
				return false;
			}
			
			return player.isMageClass() && (player.getRace() != Race.ORC);
		}
		
		private boolean isTargetModeValid(int mode, Player player, Creature creature)
		{
			switch (mode)
			{
				case 1: // Monster
				{
					return creature.isMonster() && creature.isAutoAttackable(player);
				}
				case 2: // Characters
				{
					return creature.isPlayable() && creature.isAutoAttackable(player);
				}
				case 3: // NPC
				{
					return creature.isNpc() && !creature.isMonster() && !creature.isInsideZone(ZoneId.PEACE);
				}
				default: // Any Target
				{
					return (creature.isNpc() && !creature.isInsideZone(ZoneId.PEACE)) || (creature.isPlayable() && creature.isAutoAttackable(player));
				}
			}
		}
	}
	
	public synchronized void doAutoPlay(Player player)
	{
		for (Set<Player> pool : POOLS)
		{
			if (pool.contains(player))
			{
				return;
			}
		}
		
		for (Set<Player> pool : POOLS)
		{
			if (pool.size() < POOL_SIZE)
			{
				player.onActionRequest();
				pool.add(player);
				return;
			}
		}
		
		final Set<Player> pool = ConcurrentHashMap.newKeySet(POOL_SIZE);
		player.onActionRequest();
		pool.add(player);
		ThreadPool.scheduleAtFixedRate(new AutoPlay(pool), TASK_DELAY, TASK_DELAY);
		POOLS.add(pool);
	}
	
	public void stopAutoPlay(Player player)
	{
		for (Set<Player> pool : POOLS)
		{
			if (pool.remove(player))
			{
				// Pets must follow their owner.
				if (player.hasServitors())
				{
					for (Summon summon : player.getServitors().values())
					{
						summon.followOwner();
					}
				}
				if (player.hasPet())
				{
					player.getPet().followOwner();
				}
				return;
			}
		}
	}
	
	public boolean isAutoPlay(Player player)
	{
		for (Set<Player> pool : POOLS)
		{
			if (pool.contains(player))
			{
				return true;
			}
		}
		return false;
	}
	
	public static AutoPlayTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AutoPlayTaskManager INSTANCE = new AutoPlayTaskManager();
	}
}
