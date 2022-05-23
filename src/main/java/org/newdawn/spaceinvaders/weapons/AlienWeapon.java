package org.newdawn.spaceinvaders.weapons;

import java.util.Random;

import org.newdawn.spaceinvaders.Entity;
import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.ShotEntity;

public class AlienWeapon implements IWeapon {
    private Game game;
    private Entity ship;
    private float harderAlienSpawnRate;

    private Random random = new Random();

    public AlienWeapon(Game game, Entity ship, float harderAlienSpawnRate) {
        this.game = game;
        this.ship = ship;
        this.harderAlienSpawnRate = harderAlienSpawnRate;
    }

    @Override
    public float getFireInterval() {
        return 500;
    }

    @Override
    public void fire() {
        String sprite = random.nextFloat() <= harderAlienSpawnRate ? "sprites/harderAlien.gif" : "sprites/alien.gif";
        ShotEntity shot = new ShotEntity(game, sprite, ship.getX() + 10, ship.getY() - 30, 0, -300);
		game.addEntity(shot);
    }

}