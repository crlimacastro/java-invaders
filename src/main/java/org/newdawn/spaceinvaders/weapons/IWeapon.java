package org.newdawn.spaceinvaders.weapons;

public interface IWeapon {
	/** Callback of what should change when weapon is equipped */
	void onEquip();
	/** Callback of what should change when weapon is unequipped */
	void onUnequip();
	/** Definition of how the weapon spawns ShotEntities */
	void fire();
}
