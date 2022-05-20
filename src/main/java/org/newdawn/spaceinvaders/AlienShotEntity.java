package org.newdawn.spaceinvaders;

public class AlienShotEntity extends Entity {
	/** The game in which this entity exists */
	private Game game;
	/** True if this shot has been "used", i.e. its hit something */
	private boolean used = false;

    /**
	 * Create a new shot from the alien
	 * 
	 * @param game The game in which the shot has been created
	 * @param sprite The sprite representing this shot
	 * @param x The initial x location of the shot
	 * @param y The initial y location of the shot
	 */
	public AlienShotEntity(Game game,String sprite,int x,int y, int dx, int dy) {
		super(sprite,x,y);
		
		this.game = game;
		
		this.dx = dx;
		this.dy = dy;
	}

	/**
	 * Request that this shot moved based on time elapsed
	 * 
	 * @param delta The time that has elapsed since last move
	 */
	@Override
	public void move(long delta) {
		// proceed with normal move
		super.move(delta);
		
		// if we shot off the screen, remove ourselfs
		if (y > 700) {
			game.removeEntity(this);
		}
	}
	
	/**
	 * Notification that this shot has collided with another
	 * entity
	 * 
	 * @parma other The other entity with which we've collided
	 */
	public void collidedWith(Entity other) {
		// prevents double kills, if we've already hit something,
		// don't collide
		if (used) {
			return;
		}
		
		// if we've hit the player
		if (other instanceof ShipEntity) {
			// remove the shot
			game.removeEntity(this);

			// damage the ship
			((ShipEntity)other).damage();
			used = true;
		}
    }
}
