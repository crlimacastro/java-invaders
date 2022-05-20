package org.newdawn.spaceinvaders.weapons;

import org.newdawn.spaceinvaders.Entity;
import org.newdawn.spaceinvaders.Game;
import org.newdawn.spaceinvaders.ShotEntity;

public class DefaultWeapon implements IWeapon {
    private Game game;
    private Entity ship;

    public DefaultWeapon(Game game, Entity ship) {
        this.game = game;
        this.ship = ship;
    }

    @Override
    public void onEquip() {
        /** Nothing happens */
    }

    @Override
    public void onUnequip() {
        /** Nothing happens */
    }

    @Override
    public void fire() {
        ShotEntity shot = new ShotEntity(game, "sprites/shot.gif", ship.getX() + 10, ship.getY() - 30, 0, -300);
		game.addEntity(shot);
    }

}