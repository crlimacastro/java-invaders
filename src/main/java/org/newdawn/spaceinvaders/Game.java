package org.newdawn.spaceinvaders;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.newdawn.spaceinvaders.weapons.DefaultWeapon;
import org.newdawn.spaceinvaders.weapons.RapidFireWeapon;
import org.newdawn.spaceinvaders.resources.Sound;
import org.newdawn.spaceinvaders.resources.Sprite;
import org.newdawn.spaceinvaders.weapons.AlienWeapon;
import org.newdawn.spaceinvaders.weapons.SpreadShotWeapon;

/**
 * The main hook of our game. This class with both act as a manager
 * for the display and central mediator for the game logic.
 * 
 * Display management will consist of a loop that cycles round all
 * entities in the game asking them to move and then drawing them
 * in the appropriate place. With the help of an inner class it
 * will also allow the player to control the main ship.
 * 
 * As a mediator it will be informed when entities within our game
 * detect events (e.g. alient killed, played died) and will take
 * appropriate game actions.
 * 
 * @author Kevin Glass
 */
public class Game extends Canvas {
	/** The stragey that allows us to use accelerate page flipping */
	private BufferStrategy strategy;
	/** True if the game is currently "running", i.e. the game loop is looping */
	private boolean gameRunning = true;
	/** The list of all the entities that exist in our game */
	private ArrayList entities = new ArrayList();
	/** The list of entities that need to be added to the game this loop */
	private ArrayList addList = new ArrayList();
	/** The list of entities that need to be removed from the game this loop */
	private ArrayList removeList = new ArrayList();
	/** The entity representing the player */
	private Entity ship;
	/** The speed at which the player's ship should move (pixels/sec) */
	private double moveSpeed = 300;
	/** The time at which last fired a shot */
	private long lastFire = 0;
	/** The number of aliens left on the screen */
	private int alienCount;
	/** The percentage of aliens that will spawn as a harder alien */
	private float harderAlienSpawnRate = 0.1f;

	/** The message to display which waiting for a key press */
	private String message = "";
	/** True if we're holding up game play until a key has been pressed */
	private boolean waitingForKeyPress = true;
	/** True if the left cursor key is currently pressed */
	private boolean leftPressed = false;
	/** True if the right cursor key is currently pressed */
	private boolean rightPressed = false;
	/** True if we are firing */
	private boolean firePressed = false;
	/**
	 * True if game logic needs to be applied this loop, normally as a result of a
	 * game event
	 */
	private boolean logicRequiredThisLoop = false;
	/** Weapon selected UI logic */
	private int weaponSelected = 0;
	private Sprite[] weaponSprites = {
		ResourceStore.getInstance().getResource("sprites/defaultshotico.gif", Sprite::new),
		ResourceStore.getInstance().getResource("sprites/spreadshotico.gif", Sprite::new),
		ResourceStore.getInstance().getResource("sprites/rapidshotico.gif", Sprite::new),
		ResourceStore.getInstance().getResource("sprites/alienshotico.gif", Sprite::new),
	};
	AudioPlayer audioPlayer = new AudioPlayer();

	/**
	 * Construct our game and set it running.
	 */
	public Game() {
		// create a frame to contain our game
		JFrame container = new JFrame("Space Invaders 101");

		// get hold the content of the frame and set up the resolution of the game
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setLayout(null);

		// setup our canvas size and put it into the content of the frame
		setBounds(0, 0, 800, 600);
		panel.add(this);

		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		setIgnoreRepaint(true);

		// finally make the window visible
		container.pack();
		container.setResizable(false);
		container.setVisible(true);

		// add a listener to respond to the user closing the window. If they
		// do we'd like to exit the game
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// add a key input system (defined below) to our canvas
		// so we can respond to key pressed
		addKeyListener(new KeyInputHandler(this));

		// request the focus so key events come to us
		requestFocus();

		// create the buffering strategy which will allow AWT
		// to manage our accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		// initialise the entities in our game so there's something
		// to see at startup
		initEntities();
	}

	/**
	 * Start a fresh game, this should clear out any old data and
	 * create a new set.
	 */
	private void startGame() {
		weaponSelected = 0;

		// clear out any existing entities and intialise a new set
		entities.clear();
		initEntities();

		// blank out any keyboard settings we might currently have
		leftPressed = false;
		rightPressed = false;
		firePressed = false;
	}

	/**
	 * Initialise the starting state of the entities (ship and aliens). Each
	 * entitiy will be added to the overall list of entities in the game.
	 */
	private void initEntities() {
		// create the player ship and place it roughly in the center of the screen
		ship = new ShipEntity(this, "sprites/ship.gif", 370, 550);
		entities.add(ship);

		// create a block of aliens (5 rows, by 12 aliens, spaced evenly)
		alienCount = 0;
		Random random = new Random();
		for (int row = 0; row < 5; row++) {
			for (int x = 0; x < 12; x++) {
				int xPos = 100 + (x * 50);
				int yPos = (50) + row * 30;

				// determine what kind of alien to create
				Entity alien;
				if (random.nextFloat() <= harderAlienSpawnRate) {
					alien = new HarderAlienEntity(this, "sprites/harderAlien.gif", xPos, yPos);
				} else {
					alien = new AlienEntity(this, "sprites/alien.gif", xPos, yPos);
				}

				entities.add(alien);
				alienCount++;
			}
		}
	}

	private void playClip(String path) {
		audioPlayer.playSound(ResourceStore.getInstance().getResource(path, Sound::new));
	}

	/**
	 * Notification from a game entity that the logic of the game
	 * should be run at the next opportunity (normally as a result of some
	 * game event)
	 */
	public void updateLogic() {
		logicRequiredThisLoop = true;
	}

	/**
	 * Add an entity to the game.
	 * 
	 * @param entity The entity that should be added
	 */
	public void addEntity(Entity entity) {
		addList.add(entity);
	}

	/**
	 * Remove an entity from the game. The entity removed will
	 * no longer move or be drawn.
	 * 
	 * @param entity The entity that should be removed
	 */
	public void removeEntity(Entity entity) {
		removeList.add(entity);
	}

	/**
	 * Notification that the player has died.
	 */
	public void notifyDeath() {
		playClip("sounds/defeat.wav");
		message = "Oh no! They got you, try again?";
		waitingForKeyPress = true;
	}

	/**
	 * Notification that the player has won since all the aliens
	 * are dead.
	 */
	public void notifyWin() {
		playClip("sounds/victory.wav");
		message = "Well done! You Win!";
		waitingForKeyPress = true;
	}

	/**
	 * Notification that an alien has been killed
	 */
	public void notifyAlienKilled() {
		playClip("sounds/explosion.wav");

		// reduce the alient count, if there are none left, the player has won!
		alienCount--;

		if (alienCount == 0) {
			notifyWin();
		}

		// if there are still some aliens left then they all need to get faster, so
		// speed up all the existing aliens
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = (Entity) entities.get(i);

			if (entity instanceof AlienEntity) {
				// speed up by 2%
				entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
			}
		}
	}

	public void notifyShipDamaged() {
		playClip("sounds/explosion.wav");

		ShipEntity shipCasted = (ShipEntity) ship;
		if (shipCasted.getHealth() <= 0) {
			notifyDeath();
		}
	}

	/**
	 * Attempt to fire a shot from the player. Its called "try"
	 * since we must first check that the player can fire at this
	 * point, i.e. has he/she waited long enough between shots
	 */
	public void tryToFire() {
		// check that we have waiting long enough to fire
		if (System.currentTimeMillis() - lastFire < ((ShipEntity) ship).getWeapon().getFireInterval()) {
			return;
		}

		// if we waited long enough, create the shot entity, and record the time.
		lastFire = System.currentTimeMillis();
		((ShipEntity) ship).fireWeapon();
		playClip("sounds/shoot.wav");
	}

	/**
	 * The main game loop. This loop is running during all game
	 * play as is responsible for the following activities:
	 * <p>
	 * - Working out the speed of the game loop to update moves
	 * - Moving the game entities
	 * - Drawing the screen contents (entities, text)
	 * - Updating game events
	 * - Checking Input
	 * <p>
	 */
	public void gameLoop() {
		long lastLoopTime = System.currentTimeMillis();

		// keep looping round til the game ends
		while (gameRunning) {
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();

			// Get hold of a graphics context for the accelerated
			// surface and blank it out
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, 800, 600);

			// cycle round asking each entity to move itself
			if (!waitingForKeyPress) {
				for (int i = 0; i < entities.size(); i++) {
					Entity entity = (Entity) entities.get(i);

					entity.move(delta);
				}
			}

			// draw ship health
			g.setColor(Color.white);
			g.drawString("Health: ", 16, 16 + g.getFontMetrics().getHeight() / 2);
			ShipEntity shipCasted = (ShipEntity) ship;
			Sprite shipSprite = ResourceStore.getInstance().getResource("sprites/ship.gif", Sprite::new);
			for (int i = 0; i < shipCasted.getHealth(); i++) {
				int x = 16 + g.getFontMetrics().stringWidth("Health: ") + 8 + shipSprite.getWidth() * i;
				int y = 8;
				shipSprite.draw(g, x, y);
			}

			// draw weapon select
			int weaponSpriteWidth = weaponSprites[0].getWidth();
			int weaponSpriteHeight = weaponSprites[0].getHeight();
			for (int i = 0; i < weaponSprites.length; i++) {
				weaponSprites[i].draw(g, 200 + i * (weaponSpriteWidth + 4), 4);	
			}
			g.drawRect(200 - 2 + weaponSelected * (weaponSpriteWidth + 4), 0, weaponSpriteWidth + 4, weaponSpriteHeight + 4);
			
			// draw mute instructions
			g.drawString("Press M to mute audio", 512, 16 + g.getFontMetrics().getHeight() / 2);

			// cycle round drawing all the entities we have in the game
			for (int i = 0; i < entities.size(); i++) {
				Entity entity = (Entity) entities.get(i);

				entity.draw(g);
			}

			// brute force collisions, compare every entity against
			// every other entity. If any of them collide notify
			// both entities that the collision has occured
			for (int p = 0; p < entities.size(); p++) {
				for (int s = p + 1; s < entities.size(); s++) {
					Entity me = (Entity) entities.get(p);
					Entity him = (Entity) entities.get(s);

					if (me.collidesWith(him)) {
						me.collidedWith(him);
						him.collidedWith(me);
					}
				}
			}

			// add any entity that has been marked for adding
			entities.addAll(addList);
			addList.clear();

			// remove any entity that has been marked for clear up
			entities.removeAll(removeList);
			removeList.clear();

			// if a game event has indicated that game logic should
			// be resolved, cycle round every entity requesting that
			// their personal logic should be considered.
			if (logicRequiredThisLoop) {
				for (int i = 0; i < entities.size(); i++) {
					Entity entity = (Entity) entities.get(i);
					entity.doLogic();
				}

				logicRequiredThisLoop = false;
			}

			// if we're waiting for an "any key" press then draw the
			// current message
			if (waitingForKeyPress) {
				g.setColor(Color.white);
				g.drawString(message, (800 - g.getFontMetrics().stringWidth(message)) / 2, 250);
				g.drawString("Press any key", (800 - g.getFontMetrics().stringWidth("Press any key")) / 2, 300);
			}

			// finally, we've completed drawing so clear up the graphics
			// and flip the buffer over
			g.dispose();
			strategy.show();

			// resolve the movement of the ship. First assume the ship
			// isn't moving. If either cursor key is pressed then
			// update the movement appropraitely
			ship.setHorizontalMovement(0);

			if ((leftPressed) && (!rightPressed)) {
				ship.setHorizontalMovement(-moveSpeed);
			} else if ((rightPressed) && (!leftPressed)) {
				ship.setHorizontalMovement(moveSpeed);
			}

			// if we're pressing fire, attempt to fire
			if (firePressed) {
				tryToFire();
			}

			// finally pause for a bit. Note: this should run us at about
			// 100 fps but on windows this might vary each loop due to
			// a bad implementation of timer
			try {
				Thread.sleep(10);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * A class to handle keyboard input from the user. The class
	 * handles both dynamic input during game play, i.e. left/right
	 * and shoot, and more static type input (i.e. press any key to
	 * continue)
	 * 
	 * This has been implemented as an inner class more through
	 * habbit then anything else. Its perfectly normal to implement
	 * this as seperate class if slight less convienient.
	 * 
	 * @author Kevin Glass
	 */
	private class KeyInputHandler extends KeyAdapter {
		/** The number of key presses we've had while waiting for an "any key" press */
		private int pressCount = 1;

		private Game game;

		public KeyInputHandler(Game game) {
			this.game = game;
		}

		/**
		 * Notification from AWT that a key has been pressed. Note that
		 * a key being pressed is equal to being pushed down but *NOT*
		 * released. Thats where keyTyped() comes in.
		 *
		 * @param e The details of the key that was pressed
		 */
		public void keyPressed(KeyEvent e) {
			// if we're waiting for an "any key" typed then we don't
			// want to do anything with just a "press"
			if (waitingForKeyPress) {
				return;
			}

			if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
				leftPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
				rightPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = true;
			}
		}

		/**
		 * Notification from AWT that a key has been released.
		 *
		 * @param e The details of the key that was released
		 */
		public void keyReleased(KeyEvent e) {
			// if we're waiting for an "any key" typed then we don't
			// want to do anything with just a "released"
			if (waitingForKeyPress) {
				return;
			}

			if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
				leftPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
				rightPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = false;
			}
		}

		/**
		 * Notification from AWT that a key has been typed. Note that
		 * typing a key means to both press and then release it.
		 *
		 * @param e The details of the key that was typed.
		 */
		public void keyTyped(KeyEvent e) {
			// if we hit escape, then quit the game
			if (e.getKeyChar() == 27) {
				System.exit(0);
			}

			if (!waitingForKeyPress) {
				if (e.getKeyChar() == KeyEvent.VK_1) {
					weaponSelected = 0;
					((ShipEntity) ship).setWeapon(new DefaultWeapon(game, ship));
				}
				if (e.getKeyChar() == KeyEvent.VK_2) {
					weaponSelected = 1;
					((ShipEntity) ship).setWeapon(new SpreadShotWeapon(game, ship));
				}
				if (e.getKeyChar() == KeyEvent.VK_3) {
					weaponSelected = 2;
					((ShipEntity) ship).setWeapon(new RapidFireWeapon(game, ship));
				}
				if (e.getKeyChar() == KeyEvent.VK_4) {
					weaponSelected = 3;
					((ShipEntity) ship).setWeapon(new AlienWeapon(game, ship, harderAlienSpawnRate));
				}
				if (e.getKeyChar() == 'm') {
					// Toggle Mute
					audioPlayer.setMuted(!audioPlayer.isMuted());
				}
				if (e.getKeyChar() == '+') {
					audioPlayer.setVolume(audioPlayer.getVolume() + 0.1f);
				}
				if (e.getKeyChar() == '-') {
					audioPlayer.setVolume(audioPlayer.getVolume() - 0.1f);
				}
			}

			// if we're waiting for a "any key" type then
			// check if we've recieved any recently. We may
			// have had a keyType() event from the user releasing
			// the shoot or move keys, hence the use of the "pressCount"
			// counter.
			if (waitingForKeyPress) {
				if (pressCount == 1) {
					// since we've now recieved our key typed
					// event we can mark it as such and start
					// our new game
					waitingForKeyPress = false;
					startGame();
					playClip("sounds/start.wav");
					pressCount = 0;
				} else {
					pressCount++;
				}
			}
		}
	}

	/**
	 * The entry point into the game. We'll simply create an
	 * instance of class which will start the display and game
	 * loop.
	 * 
	 * @param argv The arguments that are passed into our game
	 */
	public static void main(String argv[]) {
		Game g = new Game();

		// Start the main game loop, note: this method will not
		// return until the game has finished running. Hence we are
		// using the actual main thread to run the game.
		g.gameLoop();
	}
}
