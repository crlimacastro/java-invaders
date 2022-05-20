package org.newdawn.spaceinvaders;

/**
 * An entity representing a shot fired by the player's ship
 * 
 * @author Kevin Glass
 */
public class ShotEntity extends Entity {
	/** The game in which this entity exists */
	private Game game;
	/** True if this shot has been "used", i.e. its hit something */
	private boolean used = false;
	
	/**
	 * Create a new shot from the player
	 * 
	 * @param game The game in which the shot has been created
	 * @param sprite The sprite representing this shot
	 * @param x The initial x location of the shot
	 * @param y The initial y location of the shot
	 */
	public ShotEntity(Game game,String sprite,int x,int y, int dx, int dy) {
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
	public void move(long delta) {
		// proceed with normal move
		super.move(delta);
		
		// if we shot off the screen, remove ourselfs
		if (y < -100) {
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
		
		// if we've hit a harder alien, damage/kill it
		if (other instanceof HarderAlienEntity) {
			// remove the shot
			game.removeEntity(this);

			// damage the harder alien
			HarderAlienEntity alien = (HarderAlienEntity)other;
			alien.damage();

			// remove the alien if it's dead
			if (alien.getHealth() < 1) {
				game.removeEntity(alien);
				// notify the game that we've killed an alien
				game.notifyAlienKilled();
			}
			
			used = true;
		}
		// if we've hit an alien, kill it!
		else if (other instanceof AlienEntity) {
			// remove the affected entities
			game.removeEntity(this);
			game.removeEntity(other);
			
			// notify the game that the alien has been killed
			game.notifyAlienKilled();
			used = true;
		}
	}
}