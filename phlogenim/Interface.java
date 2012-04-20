package phlogenim;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import rjones.game.engine.util.Vector2D;

/*
 * Created on Aug 8, 2003
 */
 
/**
 * This class provides the user interaction components for the phlogenim game.
 * Mostly, that involves catching mouse clicks and movement, and using those
 * to invoke appropriate messages to the PongManager.
 * 
 * @author Randolph M. Jones
 * @see Manager
 */
public class Interface implements MouseListener, MouseMotionListener,
								  WindowListener, KeyListener, State {
	/**
	 * A reference back to the PongManager, which is the primary model that the
	 * interface needs to communicate with.
	 */
	private Manager manager;
	   
	/**
	 * Store a mouse point when mouse button is clicked.
	 */
	private Point mouse_press_point;
   
	/**
	 * Set up the user input and interaction methods,
	 * with a reference back to the PongManager.
	 * 
	 * @param manager The manager for the phlogenim game. This is where
	 * the interface will send its messages.
	 * 
	 * @see Manager
	 */
	public Interface(Manager pm)
	{
		this.manager = pm;
	}

	/**
	 * Handle mouse clicks for the phlogenim game.
	 * This uses a simple state machine to interpret clicks differently at different points
	 * of the game.  A mouse click can start the game after it has ended (or upon startup), and it will cause
	 * the game to serve the ball while the game is running.
	 * 
	 * @param e	The event representation to handle.
	 * 
	 * @see State
	 */
	public void mouseClicked(MouseEvent e)
	{
	    State cur = manager.getGameState();
	    if (cur == OPEN_SCREEN)
	    {
	    	manager.setGameState(LOAD_LEVEL);
	    }
	    else if (cur == WAIT_TO_PLAY)
	    {
	    	manager.setGameState(PLAY_GAME);
	    }
	    else if (cur == GAME_OVER)
	    {
	    	manager.resetGame();
	    }
	}

	/**
	 * This game currently does nothing with mouseDragged events.
	 * 
	 * @param e	The event representation to handle.
	 */
	public void mouseDragged(MouseEvent e)
	{
	}

	/**
     * This game currently does not do anything with mouseEntered events.
     * 
     * @param e	The event representation to handle.
     */
	public void mouseEntered(MouseEvent e)
    {
    }

	/**
	 * This game currently does not do anything with mouseExited events
	 * 
	 * @param e	The event representation to handle.
	 */
	public void mouseExited(MouseEvent e)
	{
	}

	/**
	 * This game currently does not do anything with mouseMoved events.
	 * 
	 * @param e	The event representation to handle.
	 */
	public void mouseMoved(MouseEvent e)
	{
	}

	/**
	 * This game currently does not do anything with mousePressed events.
	 * 
	 * @param e	The event representation to handle.
	 */
	public void mousePressed(MouseEvent e)
	{
		mouse_press_point = new Point(e.getPoint());
	}

	/**
	 * This game currently does not do anything with mouseReleased events.
	 * 
	 * @param e	The event representation to handle.
	 */
	public void mouseReleased(MouseEvent e)
	{
	}

	/**
	 * This game currently does not do anything with windowActivated events.
	 * 
	 * @param e	The event representation to handle.
	 */
	public void windowActivated(WindowEvent e)
	{
	}

	/**
	 * This game currently does not do anything with windowClosed events.
	 * 
	 * @param e	The event representation to handle.
	 */
	public void windowClosed(WindowEvent e)
	{
	}

	/**
	 * When the window is closing (someone has clicked the "destroy" button), tell
	 * the game controller to clean up the game and end.
	 * 
	 * @param e	The event representation to handle.
	 */
	public void windowClosing(WindowEvent e)
	{
		manager.endGame();
	}

	/**
	 * This game currently does not do anything with windowDeactivated events.
	 * 
	 * @param e	The event representation to handle.
	 */
	public void windowDeactivated(WindowEvent e)
	{
	}

	/**
	 * This game currently does not do anything with windowDeiconified events.
	 * 
	 * @param e	The event representation to handle.
	 */
	public void windowDeiconified(WindowEvent e)
	{
	}

	/**
	 * This game currently does not do anything with windowIconified events.
	 * 
	 * @param e	The event representation to handle.
	 */
	public void windowIconified(WindowEvent e)
	{
	}

	/**
	 * This game currently does not do anything with windowOpened events
	 * 
	 * @param e	The event representation to handle.
	 */
	public void windowOpened(WindowEvent e)
	{
	}
	
	/**
	 * Listen for key events. In particular listen for:
	 * 1) the escape key to exit the game,
	 * 2) the "p" key to pause the game and
	 * 3) cursor keys to move the user sprite
	 *
	 * @param e The event representation to handle.
	 */
	public void keyPressed(KeyEvent e)
	{
		State current = manager.getGameState();
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			manager.endGame();
		}
		if (e.getKeyCode() == KeyEvent.VK_P)
		{
//			manager.pauseGame();
		}
		if (current == PLAY_GAME)
		{
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
			{
				manager.accelerateByXY(new Vector2D(-5, 0));
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			{
				manager.accelerateByXY(new Vector2D(5, 0));
			}
			if (e.getKeyCode() == KeyEvent.VK_UP)
			{
				manager.jump();
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				manager.accelerateByXY(new Vector2D(0, 5));
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE)
			{
				manager.shoot(10.00);
			}
/*			if (e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				manager.tilt();
				System.err.println("Tilt!!!");
			}
*/		}
	}

	/**
	 * See keyPressed().
	 */
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_P)
		{
//			manager.continueGame();
		}
	}

	/**
	 * stub for KeyListener interface, do nothing currently
	 */
	public void keyTyped(KeyEvent arg0)
	{
	}

}