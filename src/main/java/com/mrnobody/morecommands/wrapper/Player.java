package com.mrnobody.morecommands.wrapper;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.Achievement;
import net.minecraft.util.BlockPos;

/**
 * A wrapper for the {@link EntityPlayerMP} class
 * 
 * @author MrNobody98
 */
public class Player extends com.mrnobody.morecommands.wrapper.Entity {
	private final EntityPlayerMP player;
	
	public Player(EntityPlayerMP player) {
		super(player);
		this.player = player;
	}

	/**
	 * @return the players rotation yaw
	 */
	public float getYaw() {
		return player.rotationYaw;
	}
   
	/**
	 * Sets the players rotation yaw
	 */
	public void setYaw(float yaw) {
		player.rotationYaw = yaw;
	}
   
	/**
	 * @return the players rotation pitch
	 */
	public float getPitch() {
		return player.rotationPitch;
	}
   
	/**
	 * Sets the players rotation pitch
	 */
	public void setPitch(float pitch) {
		player.rotationPitch = pitch;
	}
	
	/**
	 * Gives an item to the player
	 */
	public void givePlayerItem(Item item) {
		givePlayerItem(item, new ItemStack(item).stackSize);
	}
   
	/**
	 * Gives an amount of items to the player
	 */
	public void givePlayerItem(Item item, int quantity) {
		givePlayerItem(item, quantity, 0);
	}
   
	/**
	 * Gives an amount of items with metadata to the player
	 */
	public void givePlayerItem(Item item, int quantity, int meta) {
		ItemStack itemStack = new ItemStack(item, quantity, meta);
		if (!player.inventory.addItemStackToInventory(itemStack)) {
			player.dropItem(item, quantity);
		}
	}
   
	/**
	 * @return the players health
	 */
	public float getHealth() {
		return player.getHealth();
	}
   
	/**
	 * Sets the players health
	 */
	public void setHealth(float health) {
		player.setHealth(health);
	}
   
	/**
	 * Heals the player by a certain amount
	 */
	public void heal(float quantity) {
		setHealth(getHealth() + quantity);
	}
   
	/**
	 * @return the players hunger
	 */
	public int getHunger() {
		return player.getFoodStats().getFoodLevel();
	}
   
	/**
	 * Sets the players hunger
	 */
	public void setHunger(int food) {
		player.getFoodStats().setFoodLevel(food);
	}
   
	/**
	 * @return whether player damage is enabled
	 */
	public boolean getDamage() {
		return !player.capabilities.disableDamage;
	}
   
	/**
	 * Sets whether player damage is enabled
	 */
	public void setDamage(boolean damage) {
		player.capabilities.disableDamage = !damage;
	}
   
	/**
	 * Sets an inventory slot
	 */
	public boolean setInventorySlot(int slot, int id, int quantity, int damage) {
		if (slot < 0 || slot >= player.inventory.mainInventory.length) {
			return false;
		} else if ((Item) Item.itemRegistry.getObjectById(id) == null) {
			if (id == 0) {
				player.inventory.mainInventory[slot] = null;
				return true;
			}
		return false;
		}
		player.inventory.mainInventory[slot] = new ItemStack(Item.getItemById(id), quantity, damage);
		return true;
	}
   
	/**
	 * @return the players name
	 */
	public String getPlayerName() {
		return player.getName();
	}
   
	/**
	 * @return the players current item
	 */
	public Item getCurrentItem() {
		return player.getCurrentEquippedItem().getItem();
	}
   
	/**
	 * @return the players current slot
	 */
	public int getCurrentSlot() {
		return player.inventory.currentItem;
	}
	
	/**
	 * Sets the players current slot
	 */
	public void setCurrentSlot(int index, ItemStack item) {
		player.inventory.setInventorySlotContents(index, item);
	}
	
	/**
	 * removes enchantments from the current item
	 */
	public void removeEnchantment() {
		ItemStack stack = player.getCurrentEquippedItem();
		int damage = stack.getItemDamage();
		
		ItemStack newItem = new ItemStack(stack.getItem());
		setCurrentSlot(getCurrentSlot(), newItem);
		newItem.damageItem(stack.getItemDamage(), player);
	}
	
	/**
	 * adds an enchantment from the current item
	 */
	public void addEnchantment(Enchantment enchantment, int level) {
		player.getCurrentEquippedItem().addEnchantment(enchantment, level);
	}
   
	/**
	 * @return the players forward movement
	 */
	public float getMovementForward() {
		return player.moveForward;
	}
   
	/**
	 * @return the players strafe movement
	 */
	public float getMovementStrafe() {
		return player.moveStrafing;
	}
   
	/**
	 * Sets the players step height
	 */
	public void setStepHeight(float height) {
		player.stepHeight = height;
	}
   
	/**
	 * @return the players step height
	 */
	public float getStepHeight() {
		return player.stepHeight;
	}
   
	/**
	 * @return the {@link EntityPlayer} object
	 */
	public EntityPlayerMP getMinecraftPlayer() {
		return player;
	}
   
	/**
	 * Removes a potion effect
	 */
	public void removePotionEffect(int potion) {
		player.removePotionEffect(potion);
	}
   
	/**
	 * Removes all potion effects
	 */
	public void removeAllPotionEffects() {
		player.clearActivePotions();
	}
   
	/**
	 * Adds a potion effect
	 */
	public void addPotionEffect(int id, int duration, int strength) {
		player.addPotionEffect(new PotionEffect(id, duration, strength));
	}
   
	/**
	 * Changes the players dimension
	 */
	public void changeDimension(int dimension) {
		player.travelToDimension(dimension);
	}
   
	/**
	 * Sets the players air
	 */
	public void setAir(int air) {
		player.setAir(air);
	}
   
	/**
	 * triggers an achievement
	 */
	public boolean addAchievement(String name) {
		Achievement ach = Achievements.getAchievementByName(name);
		Achievement requirement = Achievements.getAchievementByName(Achievements.getAchievementRequirement(name));
		
		if (player instanceof EntityPlayerMP) {
			if (requirement != null) {
				if (((EntityPlayerMP) player).getStatFile().hasAchievementUnlocked(requirement)) {
					player.triggerAchievement(ach); return true;
				}
				else {return false;}
			}
			else {
				player.triggerAchievement(ach); return true;
			}
		}
		else {return false;}
	}
	
	/**
	 * @return the player's spawn point
	 */
	public BlockPos getSpawn() {
		return this.player.getBedLocation();
	}
	
	/**
	 * Sets the players spawn point
	 */
	public void setSpawn(BlockPos coord) {
		this.player.setSpawnChunk(coord, true, player.dimension);
	}
   
	/**
	 * @return whether player is allowed to fly
	 */
	public boolean getAllowFlying() {
		return player.capabilities.allowFlying;
	}
   
	/**
	 * Sets whether player is allowed to fly
	 */
	public void setAllowFlying(boolean allow) {
		player.capabilities.allowFlying = allow;
		player.capabilities.isFlying = allow;
		((EntityPlayerMP) player).sendPlayerAbilities();
	}
   
	/**
	 * @return whether player is in creative mode
	 */
	public boolean isCreativeMode() {
		return player.capabilities.isCreativeMode;
	}
}
