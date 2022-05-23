package org.newdawn.spaceinvaders;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.newdawn.spaceinvaders.resources.Sprite;

public class HarderAlienEntity extends AlienEntity {
    // How many shots left to kill the alien
    private int health = 2;
    // Random to determine if the alien should fire and when
    private Random random = new Random();
    // Timer to schedule firing
    private Timer timer = new Timer();

    /**
     * Create a new harder alien entity
     * 
     * @param game The game in which this entity is being created
     * @param ref  The sprite which should be displayed for this alien
     * @param x    The intial x location of this alien
     * @param y    The intial y location of this alient
     */
    public HarderAlienEntity(Game game, String ref, int x, int y) {
        super(game, ref, x, y);
    }

    /**
     * @return Current health of the alien
     */
    public int getHealth() {
        return health;
    }

    /**
     * Reduces health of alien by 1 and sets its sprite to the damaged version
     */
    public void damage() {
        health--;
        sprite = ResourceStore.getInstance().getResource("sprites/damagedHarderAlien.gif", Sprite::new);
    }

    /**
     * Shoot sometimes and do regular alien logic
     */
    @Override
    public void doLogic() {
        // Determine whether the alien will shoot this logic update
        if (random.nextBoolean()) {
            // Determine when the alien will shoot (in milliseconds)
            long delay = random.nextLong() & 1001 + 1000;

            // Task that fires the shot and adds the entity to the game
            TimerTask fireTask = new TimerTask() {
                @Override
                public void run() {
                    AlienShotEntity shot = new AlienShotEntity(game, "sprites/alienShot.gif", getX() + 10, getY() + 30, 0, 300);
                    game.addEntity(shot);
                }
            };

            // Schedule the task to fire the shot
            timer.schedule(fireTask, delay);
        }
        super.doLogic();
    }
}
