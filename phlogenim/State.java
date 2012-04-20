/*
 * Created on Aug 7, 2003
 */
 
package phlogenim;


/**
 * This class defines a number of unique constants that allow us easily
 * to give unique symbolic names to the different state the Breakout game can
 * be in.  These are used to set up a basic finite state machine that the
 * game travels through as it progresses, in response to various game events.
 * The basic state machine with transitions looks something like this:
 * 
 * <pre>
 * Initial State: OPEN_SCREEN
 *    This is the state we initialize to when the game starts
 * OPEN_SCREEN:
 *    Display the opening splash screen.
 *    When the user clicks the mouse, transition to LOAD_LEVEL
 * LOAD_LEVEL:
 *    Build the game world by initializing (and displaying) most of the sprites,
 *    then transition to WAIT_TO_PLAY
 * WAIT_TO_PLAY:
 *    Run the game update and animation loops.
 *    When the user clicks the mouse, transition to SERVE_BALL
 * PLAY_GAME:
 *    Run the game update and animation loops.
 *    If the ball goes out and there are lives left, transition to SERVE_BALL
 *    If the ball goes out and there are no lives left, transition to GAME_OVER
 * GAME_OVER:
 *    Display the game over screen.
 *    When the user clicks the mouse, transition to LOAD_LEVEL
 *    
 * @author Randolph M. Jones
 * @see StateClass
 * @see Manager
 * @see Interface
 */
public interface State
{
   /**
    * Display the game over screen.
    */
   public final static State GAME_OVER = new StateClass();

   /**
    * Display the opening splash screen.
    */
   public final static State OPEN_SCREEN = new StateClass();
   
   /**
    * Normal game play.
    */
   public final static State PLAY_GAME = new StateClass();
   
   /**
    * Game holds its state indefinitely.
    */
   public final static State PAUSE_GAME = new StateClass();
   
   /**
    * Build the sprites constituting the basic "Breakout World".
    */
   public final static State LOAD_LEVEL = new StateClass();
   
   /**
    * Wait for the user to click the mouse to serve the ball.
    */
   public final static State WAIT_TO_PLAY = new StateClass();

}