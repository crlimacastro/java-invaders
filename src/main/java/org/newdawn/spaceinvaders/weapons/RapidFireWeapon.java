package org.newdawn.spaceinvaders.weapons;

import org.newdawn.spaceinvaders.Entity;
import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.ShotEntity;

public class RapidFireWeapon implements IWeapon {
    private Game game;
    private Entity ship;

    public RapidFireWeapon(Game game, Entity ship) {
        this.game = game;
        this.ship = ship;
    }

    @Override
    public void onEquip() {
        game.setFiringInterval(100);
    }

    @Override
    public void onUnequip() {
        game.setFiringInterval(500);
    }

    @Override
    public void fire() {
        ShotEntity shot = new ShotEntity(game, "sprites/shot.gif", ship.getX() + 10, ship.getY() - 30, 0, -300);
		game.addEntity(shot);
    }

}