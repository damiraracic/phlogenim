package phlogenim;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import phlogenim.State;

import rjones.game.engine.GameViewer;
import rjones.game.engine.graphics.DoubleBufferedViewer;


/*
 * Created on Aug 7, 2003
 */

/**
 * The graphical interface for the phlogenim game.
 * This is essentially just an AWT frame on which to draw the various
 * game objects.  The game animation loop instructs this object to
 * paint itself once per frame.
 * 
 * @author Randolph M. Jones
 * @see rjones.game.engine.Animation
 */
public class Viewer implements State, DoubleBufferedViewer
{
   /**
    * A special image to display when the game is over.
    */
   private BufferedImage closing_image;
   /**
   * The delegate GameViewer that actually provides the display area.
   */
   private GameViewer display;
   /**
    * We need a reference to the PongManager so we can communicate with
    * the SpriteManager for painting, and so we can make some of the
    * viewing context dependent on the game state.
    */
   private Manager pm;
   /**
    * A special image to display during the game opening.
    */
   private BufferedImage splash_image;

   /**
    * Create a Panel with a Canvas inside it for drawing the Phlogenim game.
    * 
    * @param manager	A reference to the game manager that holds game state information.
    * @param w			The width of the game canvas in pixels.
    * @param h			The height of the game canvas in pixels.
    */
   public Viewer(Manager manager, int w, int h)
   {
      display = new GameViewer("Phlogenim", w, h, false);
      this.pm = manager;
   }

   /**
    * Set up an input handler for mouse events associated with this viewer.
    * 
    * @param x	A reference to the input handler.
    * @see rjones.game.engine.GameViewer
    */
   public void addMouseListener(MouseListener x)
   {
      display.addMouseListener(x);
   }

   /**
    * Set up an input handler for mouse motion events associated with this viewer.
    * 
    * @param x	A reference to the input handler.
    * @see rjones.game.engine.GameViewer
    */
   public void addMouseMotionListener(MouseMotionListener x)
   {
      display.addMouseMotionListener(x);
   }

   /**
    * Set up an input handler for window events associated with this viewer.
    * 
    * @param x	A reference to the input handler.
    * @see rjones.game.engine.GameViewer
    */
   public void addWindowListener(WindowListener x)
   {
      display.addWindowListener(x);
   }

   /**
    * Set up an input handler for keyboard events associated with this viewer.
    * 
    * @param x	A reference to the input handler.
    * @see rjones.game.engine.GameViewer
    */
   public void addKeyListener(KeyListener x)
   {
      display.addKeyListener(x);
   }

   /**
    * Display the current game state, which should already have been drawn to
    * a graphics buffer for the game area.
    * This issues a request to the GameViewer delegate to repaint itself. 
    * 
    * @see rjones.game.engine.Animation
    * @see rjones.game.engine.GameViewer#repaintRequest()
    */
   public synchronized void displayBuffer()
   {
      display.repaintRequest();
   }

   /**
    * Pass this message along to the GameViewer delegate
    * 
    * @see rjones.game.engine.GameViewer
    */
   public void dispose()
   {
      display.dispose();
   }

   /**
    * Return the GameViewer delegate; Manager needs it.
    * 
    */
   public GameViewer getDisplay()
   {
	   return display;
   }
   
	/**
	 * Pass this message along to the GameViewer delegate
	 * 
	 * @see rjones.game.engine.GameViewer
	 */
	public Color getBackground()
	{
		return display.getBackground();
	}

	/**
	 * Pass this message along to the GameViewer delegate
	 * 
	 * @see rjones.game.engine.GameViewer
	 */
	public Graphics getGraphics()
	{
		return display.getGraphics();
	}

   /**
    * Draw the current state of the world onto the graphics buffer.
    * This method gets called once per animation frame by the animation thread.
    * This method draws different things depending on the current state of the game.
    * It either draws the splash screen, the game over screen, or the game sprites.
    * 
    * @see rjones.game.engine.Animation
    */
   public void paintBuffer()
   {
      // Grab the next free buffer from the double-buffering object
      Graphics2D buffer_graphics = display.getGameGraphics();
      // The buffer might be null if the game area is not yet displayed.  If that's
      // the case, just don't try to draw anything.
      if (buffer_graphics != null)
      {
         State cur = pm.getGameState();
         if (cur == OPEN_SCREEN)
         {
            buffer_graphics.drawImage(
               splash_image,
               0,
               0,
               display.getGameWidth(),
               display.getGameHeight(),
               null);
         } else if (cur == WAIT_TO_PLAY || cur == PLAY_GAME)
         {
            // Erase the buffer, then draw the sprites to it.
            buffer_graphics.setColor(display.getBackground());
            buffer_graphics.fillRect(
               0,
               0,
               display.getWidth(),
               display.getHeight());
            pm.getSpriteManager().paint(buffer_graphics);
         } else if (cur == GAME_OVER)
         {
            // Erase the buffer, draw the sprites to it, then superimpose the game over screen
            buffer_graphics.setColor(display.getBackground());
            buffer_graphics.fillRect(
               0,
               0,
               display.getWidth(),
               display.getHeight());
            pm.getSpriteManager().paint(buffer_graphics);
            buffer_graphics.drawImage(
               closing_image,
               0,
               0,
               display.getWidth(),
               display.getHeight(),
               null);
         }
         // Clean up after we're done drawing
         //buffer_graphics.dispose();
      }
   }

	/**
	 * Pass this message along to the GameViewer delegate
	 * 
	 * @see rjones.game.engine.GameViewer
	 */
	public void setBackground(Color c)
	{
		display.setBackground(c);
	}

   /**
    * Set the image to show when in the GAME_OVER state
    * 
    * @param image	The desired GAME_OVER image.
    */
   public void setClosingImage(BufferedImage image)
   {
      this.closing_image = image;
   }

   /**
    * Set the image to show when in the OPEN_SCREEN state
    * 
    * @param image	The desired OPEN_SCREEN image
    */
   public void setSplashImage(BufferedImage image)
   {
      this.splash_image = image;
   }

   /**
    * Pass this message along to the GameViewer delegate
    * 
    * @see rjones.game.engine.GameViewer
    */
   public void setVisible(boolean v)
   {
      display.setVisible(v);
   }

}
