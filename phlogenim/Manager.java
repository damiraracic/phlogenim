package phlogenim;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import phlogenim.levels.*;
import phlogenim.sprite.*;


import rjones.game.engine.Animation;
import rjones.game.engine.GameUpdater;
import rjones.game.engine.SpriteManager;
import rjones.game.engine.Ticker;
import rjones.game.engine.World;
import rjones.game.engine.graphics.ImageLoader;
import rjones.game.engine.sound.SoundClip;
import rjones.game.engine.sprite.StringSprite;
import rjones.game.engine.util.Vector2D;


/*
 * Created on Aug 8, 2003
 */

/**
 * This is the main engine of the phlogenim game.  It maintains the state of the game,
 * along with associated information such as the score, how many lives are left, etc.
 * The PongManager sets up the PongViewer, which is used to display the game, and the
 * PongInterface, which is used to get input from the user.  This class also starts
 * the two game loops for running the animation and the update of the world, and
 * initializes the sprites in the game through the SpriteManager.
 * 
 * @author Randolph M. Jones
 * @see Viewer
 * @see Interface
 * @see rjones.game.engine.Animation
 * @see rjones.game.engine.World
 * @see rjones.game.engine.SpriteManager
 */
public class Manager implements State, GameUpdater
{
   /**
    * Constant representing the frame rates (in frames per second) for the
    * animation thread.  You can tinker with
    * this if you don't like the rates you get.  If you set this too high,
    * it will stop having any effect.  This is only what you <em>want</em>
    * the frame rate to be.  If the computer isn't fast enough, the actual
    * rate will be slower.
    * 
    * It doesn't normally make sense to make the animation frame rate
    * faster than the world frame rate...or at least it shouldn't ;)
    * 
    * @see #WORLD_FRAME_RATE
    */
   public static long ANIMATION_FRAME_RATE = 200;

   /**
    * Constant representing the frame rates (in frames per second) for the
    * world update thread.  You can tinker with
    * this if you don't like the rates you get.  If you set this too high,
    * it will stop having any effect. This is only what you <em>want</em>
    * the frame rate to be.  If the computer isn't fast enough, the actual
    * rate will be slower.
    * 
    * @see #ANIMATION_FRAME_RATE
    */
   public static long WORLD_FRAME_RATE = 175;

   /**
    * The user-controlled sprite.
    */
   public PHeroSprite hero;
   /**
    * Pixel width (and height) of the user sprite.
    */
   private final int hero_width = 12;
   private final int hero_height = 12;

   /**
    * Magnitude of the ball's velocity vector in pixels.
    * For our simple collision detection, we must make sure that
    * the velocity of the ball is less than the thickness of
    * anything it can hit (the paddle and the walls).
    * Otherwise, there would be a chance the ball would "pass through"
    * one of these objects without actually colliding with it.
    * The ball's velocity should be set so the game is not too easy
    * and not too hard.  A variation on this game might vary the
    * velocity in different situations.
    */
   private final double hero_velocity = 200;

   /**
    * configuration properties...
    */
   private PhlogenimProperties properties;
   
   /**
    * Special "enumerated type" for running the game's state machine.
    */
   private State game_state;

   /**
    * The thread that will run the graphics animation.
    */
   private Animation graphics_updater;
   
   /**
    * The utility object for loading in the images.
    */
   private ImageLoader image_loader;
   
   /**
    * The input handling routines for the game.
    */
   private Interface input_handler;
   
   /**
    * A text-based sprite to display the current number of lives remaining.
    */
   private StringSprite health_display;
   
   /**
    * A text-based sprite to display the current score.
    */
   private StringSprite score_display;   
   
   /**
    * The two main things we need to keep track of in the game state.
    */
   private int health, score;

   /**
    * Number of lives the player can lose before the game ends.
    */
   private final int max_health = 117000;

   /**
    * A simple image used for the game opening screen.
    */
   private BufferedImage open_splash;

   /**
    * A simple image used for the game over screen.
    */
   private BufferedImage game_over;
   
   /**
    * Recorded sounds to enhance various game events (mostly triggered by collisions).
    */
   private SoundClip hero_sound, wall_sound, score_sound, bonus_sound, lose_sound;
   
   private ArrayList<SoundClip> brick_collision_sounds;
   
   /**
    * Many games need benefit from some randomness.
    */
   private Random randomizer = new Random();
   
   /**
    * The engine that manages sprite updates and collision handling.
    */
   private SpriteManager sm;
   
   /**
    * The graphical display of the game.
    */
   private Viewer viewer;

   /**
    * Pixel width of (invisible) walls.
    */
   private final int wall_thickness = 20;

   /**
    * "Normal" wall sprites.
    */
   private PWallSprite north_wall, east_wall, south_wall, west_wall;

   /**
    * Data structure containing all non-user items currently on screen.
    */
   private ArrayList<Level> levels = new ArrayList<Level>(5);

   private int current_level = 0;

   /**
    * The pixel width and height of the gameplay area,
    * as specified by the constructor parameters.
    */
   private int width;
   private int height;
   
   /**
    * The thread that will run the game loop that updates the world.
    */
   private World world_updater;

   private Ticker single_thread_master = null;
   
   /**
    * Create a new controller for the hlogenim game.
    * The gameplay area will have a width and height defined by the parameters.
    * 
    * @param width	The width in pixels of the gameplay area.
    * @param height	The height in pixels of the gameplay area.
    */
   public Manager(int width, int height)
   {
      this.width = width;
      this.height = height;
     
      properties = PhlogenimProperties.getProperties();

      loadImages();
      loadSounds();

      startViewer();
      startInterface();
      
      sm = new SpriteManager();
      
      createHero();
      createLevels();
   }

   /**
    * A "significant game event" that can be triggered by external game objects.
    * This method allows other objects to increase the game's score.
    * Any kind of game event that should increase the player's score can use
    * this method to do so.  Note that this method always increases the score
    * by just one point. A variation of this game might allow a parameter
    * to increase the score by various amounts.
    */
   
   public void bumpScore(int points)
   {
//	  int BONUS_DISPLAY_TIME = 30;
	   
	  score += points;
//	  current_run += points;
//	  if (current_run >= BONUS) {
//		  score += 2 * BONUS;
//		  current_run = 0;
		  
//	      int bonus_offset = viewer.getGraphics().getFontMetrics(
//	  							viewer.getGraphics().getFont()).stringWidth("B O N U S !!!") / 2;
//	      PTemporaryStringSprite bonus_display = new PTemporaryStringSprite(
//													width / 2 - bonus_offset,
//													height / 2,
//													0,
//													0,
//													System.currentTimeMillis(),
//													BONUS_DISPLAY_TIME,
//													sm,
//													Color.red);
//	      bonus_display.setText("B O N U S   " + 2 * BONUS + " !!!");
//	      bonus_sound.play();
//	      sm.add(bonus_display);
//	  }
	  score_display.setText("Score: " + score);
   }
   
//   public void resetCurrentRun(int points)
//   {
//	  current_run = points;
//   }
   
   /**
    * When we're done, make sure we throw things away and exit gracefully.
    */
   public void endGame()
   {
      world_updater.stopTicker();
      graphics_updater.stopTicker();
      viewer.setVisible(false);
      viewer.dispose();
      System.exit(0);
   }
   
/*   public void pauseGame() {
	   synchronized (world_updater) {
		   world_updater.pauseTicker();
	   }
   }

   public void continueGame() {
	   synchronized (world_updater) {
		   world_updater.unpauseTicker();
		   world_updater.notify();
	   }
   }
*/   
   /**
    * Public access method to allow other objects
    * to be sensitive to the state of the game world.
    * 
    * @return	The current game state value
    * @see State
    */
   public State getGameState() {
      return game_state;
   }
   
   public Viewer getViewer() {
	   return viewer;
   }

   /**
    * Allow other objects to have access to the SpriteManager.
    * Mostly, this is to allow the PongViewer object to send the SpriteManager
    * requests to paint the sprites.
    * 
    * @return	The current SpriteManager for the game.
    * @see Viewer
    * @see rjones.game.engine.SpriteManager
    */
   public SpriteManager getSpriteManager()
   {
      return sm;
   }

   /**
    * Load in any images that this game will need to use.
    * We do this up front because it takes time, and we don't want
    * any delays in the middle of the game.
    */
   public void loadImages()
   {
      System.out.println("Loading images...");
      image_loader = new ImageLoader();
      open_splash =
         image_loader.loadImage("/phlogenim/images/green_splash.gif");
      game_over =
         image_loader.loadImage("/phlogenim/images/game_over.gif");
      System.out.println("Finished loading images...");
   }

   /**
    * Load in any sound clips that this game will need to use.
    * We do this up front because it takes time, and we don't want
    * any delays in the middle of the game.
    */
   public void loadSounds()
   {
      System.out.println("Loading sounds...");
      wall_sound = new SoundClip("/phlogenim/sounds/snap.wav");
      score_sound = new SoundClip("/phlogenim/sounds/snap-keys.wav");
      lose_sound = new SoundClip("/phlogenim/sounds/plplpl-fade.wav");
      brick_collision_sounds = new ArrayList<SoundClip>();
      brick_collision_sounds.add(new SoundClip("/phlogenim/sounds/space.wav"));
      brick_collision_sounds.add(new SoundClip("/phlogenim/sounds/plop.wav"));
      hero_sound = new SoundClip("/phlogenim/sounds/space.wav");
      System.out.println("Finished loading sounds...");
   }

   /**
    * A "significant game event" that can be triggered by external game objects
    * (such as a collision between the ball and the "bottom wall").
    * This decrements the number of lives, removes the ball from the game,
    * and tells the game to wait for the next serve.  Alternatively, if there
    * are no lives left, we switch into the GAME_OVER state.
    */
   public void wound(double mass, Vector2D velocity)
   {
	  int damage = (int)(mass * velocity.magnitude());
	  System.err.println("Damage: " + damage);
      health -= damage;
      health_display.setText("Health: " + health);
      if (health <= 0)
      {
         setGameState(GAME_OVER);
      }
      else
      {
    	 /**
    	  * Repell the ball according to made-up 'momentum conservation' that
    	  * combines ratios of masses and velocities of the colliding objects.
    	  */
//    	 System.err.println("Masses: " + hero.getMass() + ", " + mass);
//   	 System.err.println("Velocities: " + hero.getVelocity() + ", " + velocity);
//    	 double x = (hero.getMass() / mass) * (hero.getVelocity().getX() / velocity.getX());
//    	 double y = (hero.getMass() / mass) * (hero.getVelocity().getY() / velocity.getY());
//    	 Vector2D v = new Vector2D(x, y);
//    	 System.err.println("New velocity: " + v);
//    	 hero.setVelocity(v);
      }
   }

   /**
    * Give the player some more health
    *
    */
   public void addHealth()
   {
	  health += health / 5;
	  health_display.setText("Health: " + health);	   
   }

   /**
    * Public mutator method that allows external objects to change the state of the game world.
    * 
    * @param x	The new game state to change to.
    * @see State
    */
   public void setGameState(State x)
   {
      game_state = x;
   }

   /**
    * A "significant game event" that can be triggered by external game objects.
    * Normally, this method will be called when the mouse moves in a horizontal
    * direction, so that the game knows it should update the position of the paddle.
    * Note that this method could have an improved implementation.  It doesn't
    * cleanly handle situations where the mouse moves very quickly out of the playing area.
    * 
    * @param x	New x coordinate for the center of the paddle.
    */
   public void accelerateByXY(Vector2D a)
   {
//      if ((x
//         > (west_wall.getCollisionBox().getWidth()
//            + paddle.getCollisionBox().getWidth() / 2))
//         && (x
//            < (width
//               - east_wall.getCollisionBox().getWidth()
//              - paddle.getCollisionBox().getWidth() / 2)))
//     {
       hero.accelerateByXY(new Vector2D(a));
//     }
   }
   
   public void jump()
   {
	   hero.jump();
   }

   public void tilt()
   {
	   hero.setTilt();
   }
   
   public void shoot(double mass)
   {
	   hero.shoot(mass);
   }
   
   /**
    * Start the game by starting the animation and world update threads.
    */
   public void startGame()
   {
      setGameState(OPEN_SCREEN);
      graphics_updater = new Animation(viewer, ANIMATION_FRAME_RATE);
      graphics_updater.start();
      world_updater = new World(this, WORLD_FRAME_RATE);
      world_updater.start();
   }

   /**
    * Start the game.  Instead of starting the world update and animation 
    * loops as seperate threads, run them in a single master thread explicitly
    * calling their tick() methods in rigid lockstep.
    */
   public void singleThreadStart()
   {	
	   	setGameState(OPEN_SCREEN);
	    graphics_updater = new Animation(viewer, ANIMATION_FRAME_RATE);
	    world_updater = new World(this, WORLD_FRAME_RATE);
	
	    single_thread_master = new Ticker(Math.max( WORLD_FRAME_RATE, ANIMATION_FRAME_RATE )) {
	    	public void tick()
	    	{
    	    	world_updater.tick();
    	    	graphics_updater.tick();   		
	    	}
	    };
	    single_thread_master.start();
   }

   /**
    * Start the input handlers for the game.
    */
   public void startInterface()
   {
      input_handler = new Interface(this);
      viewer.addMouseListener(input_handler);
      viewer.addMouseMotionListener(input_handler);
      viewer.addWindowListener(input_handler);
      viewer.addKeyListener(input_handler);
   }

   /**
    * Here's where we do most of the setup for the game
    * by populating the world with most of the sprites and initializing
    * the game information, like score and lives left.
    */
   public void startSprites()
   {
	   
	  Color wall_color = new Color(Integer.parseInt(properties.get("wall.color","ff0000"),16));
	  Color fg_color = new Color(Integer.parseInt(properties.get("foreground.color", "0000ff"),16));

      // Vertical wall taking up the whole left side
      west_wall =
         new PWallSprite(
             0.0,
             0.0,
            (double)wall_thickness,
            (double)height,
            wall_color);
      west_wall.setBallCollision(wall_sound);
//      west_wall.setName("West Wall");

      // Vertical wall taking up the whole right side
      east_wall =
         new PWallSprite(
            (double) (width - wall_thickness),
             0.0,
            (double)wall_thickness,
            (double)height,
            wall_color);
      east_wall.setBallCollision(wall_sound);
//      east_wall.setName("East Wall");

      // Top horizontal wall squeezed between west and east walls
      // (we don't want them to overlap so the walls don't "collide"
      //  every game tick)
      north_wall =
         new PWallSprite(
            (double)wall_thickness,
             0.0,
            (double) (width - (wall_thickness * 2)),
            (double)wall_thickness,
            wall_color);
      north_wall.setBallCollision(wall_sound);
//      north_wall.setName("North Wall");

      // Bottom horizontal, invisible "wall", so we can detect when
      // the ball has escaped
      south_wall =
         new PWallSprite(
            (double)wall_thickness,
            (double) (height - wall_thickness),
            (double) (width - (wall_thickness * 2)),
            (double)wall_thickness,
            wall_color);
      south_wall.setBallCollision(wall_sound);
//      south_wall.setName("South Wall");

      health = max_health;
      health_display =
         new StringSprite(
            1.5 * wall_thickness,
            2 * wall_thickness,
            0,
            0,
            fg_color);
      health_display.setText("Health: " + health);

      score = 0;
      int score_offset =
         viewer.getGraphics().getFontMetrics(
            viewer.getGraphics().getFont()).stringWidth(
            "Score: 8888");
      score_display =
         new StringSprite(
            width - 2 * wall_thickness - score_offset,
            height - wall_thickness,
            0,
            0,
            fg_color);
      score_display.setText("Score: " + score);

      // Now that we have created all the sprites, we have to actually add them
      // to the game world, using the SpriteManager.
      // Note that the only sprite we haven't created yet is the ball.  We won't
      // do that until the game really starts.
      sm.add(west_wall);
      sm.add(east_wall);
      sm.add(north_wall);
      sm.add(south_wall);
      sm.add(health_display);
      sm.add(score_display);
   }
   
   public void createHero()
   {
	   hero = new PHeroSprite(wall_thickness,
					 		  height - wall_thickness,
					 		  150.00);
	   hero.setManager(this);
	   hero.setCollisionSound(hero_sound);
   }
   
   public void createLevels()
   {
	   levels.add(new LevelOne(this, hero, wall_thickness));
	   levels.add(new LevelTwo(this, hero, wall_thickness));
	   levels.add(new LevelThree(this, hero, wall_thickness));
	   levels.add(new LevelFinal(this, hero, wall_thickness));
   }
   
   public void loadLevel()
   {
	   hero.place(new Vector2D(wall_thickness + 2 * hero.getCollisionBox().getWidth(),
			   				   height - wall_thickness));
	   levels.get(current_level).initialize();
	   current_level++;
   }

   /**
    * Create the graphical interface for the game, using the specified width and height.
    * Also, configure it with the images it will periodically need to paint to the screen.
    */
   public void startViewer()
   {
      System.out.println("Creating viewer...");
      
      viewer = new Viewer(this, width, height);
      viewer.setBackground( new Color(Integer.parseInt(properties.get("background.color", "000000"),16)) );
      viewer.setSplashImage(open_splash);
      viewer.setClosingImage(game_over);
   }

   /**
    * This is the main update method for the game world.
    * This method gets called every tick of the World update thread.
    * It implements a simple state machine that changes states based
    * on user input or significant events in the game (like the ball
    * leaving the playing area).
    * @see rjones.game.engine.World
    * @see State
    */
   // Notice that not all the states are represented here.  Some of them get
   // changed in other places.  Most notably in the PongInterface when particular
   // user inputs are received.  For example, the system will wait in the
   // WAIT_TO_PLAY state until the user clicks the mouse, which changes the
   // state to SERVE_BALL, which again gets handled the next time updateGame()
   // gets invoked.
   public void updateGame( )
   {
      State cur = getGameState();
      if (cur == LOAD_LEVEL)
      {
    	 sm.removeAll();
         startSprites();
         loadLevel();
         setGameState(WAIT_TO_PLAY);
      } else if (cur == PLAY_GAME)
      {
    	 sm.update();
      }
   }
   
   public void resetGame()
   {
	  sm.removeAll();
      current_level = 0;
      setGameState(LOAD_LEVEL);
   }
   
}