package phlogenim.sprite;


import java.awt.Color;
import java.awt.Graphics2D;

import phlogenim.State;

import rjones.game.engine.sprite.GenericSprite;
import rjones.game.engine.sprite.RectangularShapeSprite;
import rjones.game.engine.sprite.Sprite;
import rjones.game.engine.util.BetterRectangle;
import rjones.game.engine.util.Vector2D;


/*
 * Created on Aug 8, 2003
 */

/**
 * A general sprite that allows you to display strings of text on the screen.
 * This uses Java's built-in methods for drawing text on a graphics context.
 * You just have to specify what the text is and its color.  An improved
 * version of this class would also allow you to set font attributes and things
 * like that.
 * 
 * @author rjones
 */
public class PStringSprite extends PSprite implements Sprite, State
{
   /**
    * The color in which the text should be drawn
    */
   private Color color;
   /**
    * A delegate to provide basic sprite functions
    */
   private RectangularShapeSprite sprite_delegate;
   /**
    * The text that should be displayed
    */
   private String text;

   
   public PStringSprite(double x, double y, double width, double height)
   {
	  sprite_delegate = new RectangularShapeSprite(x, y, width, height);
	  this.color = Color.white;
   }

   public PStringSprite(double x,
					    double y,
					    double width,
					    double height,
					    Color color)
   {
      this(x, y, width, height);
      this.color = color;
   }

   /**
    * A default method that does nothing in the case of a collision.
    * 
    * @param s	The sprite that wants to collide with this one.
    */
   public void collideWith(Sprite s)
   {
	   if (s instanceof PHeroSprite)
	   {
		   manager.setGameState(LOAD_LEVEL);
	   }
   }

   public BetterRectangle getCollisionBox()
   {
      return sprite_delegate.getCollisionBox();
   }
   
   public Vector2D getVelocity()
   {
      return sprite_delegate.getVelocity();
   }

   /**
    * A default computation for the surface normal.  But since this sprite is never supposed
    * to be involved in collisions, this method is meaningless here.
    * This just invokes the getSurfaceNormal() method on this object's GenericSprite delegate.
    * 
    */
   public double getSurfaceNormal(double theta)
   {
      return sprite_delegate.getSurfaceNormal(theta);
   }

   /**
    * Paint the current text string on the specified graphics context.
    * 
    * @param g	The graphics context to draw the string on.
    */
   public void paint(Graphics2D g)
   {
      g.setColor(color);
      g.drawString(
         text,
         (int)sprite_delegate.getCollisionBox().getX(),
         (int)sprite_delegate.getCollisionBox().getY());
   }

   /**
    * Change the color of the text that this sprite displays.
    * 
    * @param color	New value of the color to use for the display.
    */
   public void setColor(Color color)
   {
      this.color = color;
   }

   /**
    * Change the text that this sprite displays on the screen.
    * 
    * @param string	Value of the new string to display.
    */
   public void setText(String string)
   {
      this.text = string;
   }

   /**
    * A default update method that does nothing.
    * The idea here is that this type of sprite is for display purposes only, and
    * doesn't move or have any other influence on the game world.
    * 
    * @return	False always.
    */
   public boolean update(long d_ms)
   {
      return false;
   }
}
