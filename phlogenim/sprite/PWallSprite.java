package phlogenim.sprite;


import java.awt.Color;
import java.awt.Graphics2D;

import rjones.game.engine.sound.SoundClip;
import rjones.game.engine.sprite.RectangleSprite;
import rjones.game.engine.sprite.Sprite;
import rjones.game.engine.util.BetterRectangle;
import rjones.game.engine.util.Vector2D;


/*
 * Created on Aug 8, 2003
 */
 
/**
 * This sprite provides "normal" walls for the Phlogenim game.
 * In this game, instances of this sprite provide the left and right walls.
 * These wall are just there for the ball to bounce off of (and make a noise).
 * They don't do anything else special.
 * 
 * @author rjones
 */
public class PWallSprite extends PSprite implements Sprite
{
   /**
    * A sound clip to play when the ball collides with this sprite.
    */
   private SoundClip ball_collision;
   /**
    * A delegate to provide default behaviors for this sprite.
    */
   private RectangleSprite sprite_delegate;

   /**
    * Create a normal wall for the Phlogenim game, with the specified location, dimensions, and color.
    * @param x	X coordinate of the left edge of the wall.
    * @param y	Y coordinate of the top edge of the wall.
    * @param w	Width of the wall in double-precision pixels.
    * @param h	Height of the wall in double-precision pixels.
    * @param c	Color of the wall.
    */
   public PWallSprite(double x, double y, double w, double h, Color c)
   {
      sprite_delegate = new RectangleSprite(x, y, w, h, c);
   }

	/**
	 * Handle collisions with this sprite.
	 * In this case, we only care about collisions with the ball.  In such cases,
	 * we just play the appropriate sound.  The ball sprite handles the computations
	 * for bouncing the ball off of the wall.
	 * 
	 * @param s	The sprite that has collided with this sprite.
	 */
   public void collideWith(Sprite s)
   {
//      if (s instanceof PHeroSprite && ball_collision != null)
//      {
//         ball_collision.play();
//      }
   }

	/**
	 * Invoke the getCollisionBox() method on this object's RectangleSprite delegate.
	 * 
	 * @see RectangleSprite#getCollisionBox()
	 */
   public BetterRectangle getCollisionBox()
   {
      return sprite_delegate.getCollisionBox();
   }

	/**
	 * Invoke the getSurfaceNormal() method on this object's RectangleSprite delegate.
	 * 
	 * @see RectangleSprite#getSurfaceNormal(double)
	 */
   public double getSurfaceNormal(double theta)
   {
      return sprite_delegate.getSurfaceNormal(theta);
   }

	/**
	 * Invoke the paint() method on this object's RectangleSprite delegate.
	 * 
	 * @see RectangleSprite#paint(java.awt.Graphics2D)
	 */
   public void paint(Graphics2D g)
   {
      sprite_delegate.paint(g);
   }

   /**
    * Set the sound clip to play when the ball collides with this sprite.
    * 
    * @param clip	The sound clip to play.
    */
   public void setBallCollision(SoundClip clip)
   {
      this.ball_collision = clip;
   }

	/**
	 * Invoke the update() method on this object's RectangleSprite delegate.
	 * 
	 * @see RectangleSprite#update()
	 */
   public boolean update(long dms)
   {
      return sprite_delegate.update( dms );
   }
   
   public Vector2D getVelocity()
   {
	  return sprite_delegate.getVelocity();
   }

}
