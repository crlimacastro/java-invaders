package org.newdawn.spaceinvaders;

import java.util.Timer;
import java.util.TimerTask;

import org.newdawn.spaceinvaders.weapons.DefaultWeapon;
import org.newdawn.spaceinvaders.weapons.IWeapon;

/**
 * The entity that represents the players ship
 * 
 * @author Kevin Glass
 */
public class ShipEntity extends Entity {
	/** The game in which the ship exists */
	private Game game;

	/** Current health of the ship */
	private int health = 3;
	/** Are iframes active right now? */
	private boolean invinsible = false;
	/** How long the ship will be invinsible for after being hit */
	private long iframes = 3000;
	/** Timer to schedule removing iframes */
    private Timer timer = new Timer();
	/** The weapon that the ship is currently using */
	private IWeapon weapon;

	/**
	 * Create a new entity to represent the players ship
	 *  
	 * @param game The game in which the ship is being created
	 * @param ref The reference to the sprite to show for the ship
	 * @param x The initial x location of the player's ship
	 * @param y The initial y location of the player's ship
	 */
	public ShipEntity(Game game,String ref,int x,int y) {
		super(ref,x,y);
		
		this.game = game;
		this.weapon = new DefaultWeapon(game, this);
	}

	/**
	 * @return Current ship health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @param health New ship health
	 */
	public void setHealth(int value) {
		health = value;
	}

	/**
	 * Decreases ship health by 1 and notifies Game that the ship has been hit
	 */
	public void damage() {
		if (invinsible) return;

		health--;
		invinsible = true;

		sprite = SpriteStore.get().getSprite("sprites/invinsibleShip.gif");

		// Task that disables invinsibility
		TimerTask iframesTask = new TimerTask() {
			@Override
			public void run() {
				invinsible = false;
				sprite = SpriteStore.get().getSprite("sprites/ship.gif");
			}
		};
		timer.schedule(iframesTask, iframes);

		game.notifyShipDamaged();

	}
	
	/**
	 * Request that the ship move itself based on an elapsed ammount of
	 * time
	 * 
	 * @param delta The time that has elapsed since last move (ms)
	 */
	public void move(long delta) {
		// if we're moving left and have reached the left hand side
		// of the screen, don't move
		if ((dx < 0) && (x < 10)) {
			return;
		}
		// if we're moving right and have reached the right hand side
		// of the screen, don't move
		if ((dx > 0) && (x > 750)) {
			return;
		}
		
		super.move(delta);
	}

	/**
	 * Request that the ship fire with the current weapon
	 */
	public void fireWeapon() {
		weapon.fire();
	}

	/**
	 * @param weapon The weapon to use
	 */
	public void setWeapon(IWeapon weapon) {
		if (this.weapon != null) {
			this.weapon.onUnequip();
		}
		this.weapon = weapon;
		this.weapon.onEquip();
	}
	
	/**
	 * Notification that the player's ship has collided with something
	 * 
	 * @param other The entity with which the ship has collided
	 */
	public void collidedWith(Entity other) {
		// if its an alien, notify the game that the player
		// is dead
		if (other instanceof AlienEntity) {
			game.notifyDeath();
		}
	}
}