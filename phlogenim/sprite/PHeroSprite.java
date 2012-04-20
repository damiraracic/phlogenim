package phlogenim.sprite;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;

import phlogenim.Constant;
import phlogenim.State;

import rjones.game.engine.graphics.ImageLoader;
import rjones.game.engine.sprite.RectangularShapeSprite;
import rjones.game.engine.sprite.Sprite;
import rjones.game.engine.util.BetterRectangle;
import rjones.game.engine.util.Vector2D;


/*
 * Created on Aug 7, 2003
 */

/**
 * This class implements the ball in the phlogenim game.  Its collision box is square, even
 * though the ball draws as a circle.  The collision box is a little smaller than usual
 * in order to make the collisions look more appropriate given the round shape.  When
 * the ball hits anything, it reflects its velocity (hopefully realistically) off the
 * object it hit, according to the surface geometry of the object.
 * 
 * @author Randolph M. Jones
 */
public class PHeroSprite extends PSprite implements Sprite, Constant, State
{

   /**
    * Indicates whether the user is pressing the UP key, to levitate.
    */
   private boolean go_up;

   /**
    * Indicates whether the hero sprite is in contact with a "supportive" surface,
    * i.e., a surface with a normal vector opposite to the gravitational acceleration.
    */
   private boolean on_ground;

   /**
    * Little hack intended to help with the "velcro ball" syndrome.
    */
   private boolean tilt;
   
   private double horizontal_reflection_coefficient;
   
   /**
    * Position vector indicating the last location where the hero sprite was
    * in contact with a "supportive" surface.
    */
   private Vector2D last_position;
   
   /**
    * Two arrays of images store the animations of the hero sprite.
    * Current image to be displayed is pointed to by the index variable.
    */
   private int index;
   
   private BufferedImage[] hero_images = hero_images_right;
   
   private static BufferedImage[] hero_images_right
		= {new ImageLoader().loadImage("/phlogenim/images/heroR1.gif"),
		   new ImageLoader().loadImage("/phlogenim/images/heroR2.gif"),
		   new ImageLoader().loadImage("/phlogenim/images/heroR1.gif"),
		   new ImageLoader().loadImage("/phlogenim/images/heroR4.gif")};
   				
   private static BufferedImage[] hero_images_left
		= {new ImageLoader().loadImage("/phlogenim/images/heroL1.gif"),
		   new ImageLoader().loadImage("/phlogenim/images/heroL2.gif"),
		   new ImageLoader().loadImage("/phlogenim/images/heroL1.gif"),
		   new ImageLoader().loadImage("/phlogenim/images/heroL4.gif")};

   /**
    * Two variable to control frequency of hero sprite update.
    * Without limiting the speed of updating, hero sprite animation would look
    * unbelievable and quite comic.
    */   
   private long update_last;
   
   private static long animation_delay = 200;
   
   /**
    * Short for "delta acceleration", this is the acceleration added by the movement
    * of the cursor keys.
    */
   private Vector2D d_acceleration;
   
   private Vector2D shot_direction;
   
   /**
    * This variable keeps track of the last item the ball collided with, so the ball
    * can detect (hopefully unusual) cases where it has gotten "trapped" inside
    * another sprite.
    */
   private Sprite last_collision;

   private RectangularShapeSprite sprite_delegate;
   
   /**
    * Create a simple white filled circle to represent a bouncing Phlogenim ball
    * 
    * @param x		x coordinate of the upper left corner of the ball's bounding box
    * @param y		y coordinate of the upper left corner of the ball's bounding box
    * @param width	the ball's width in pixels
    * @param height	the ball's height in pixels
    */
/*
   public PHeroSprite(double x, double y,
		   			  double width, double height,
		   			  Color c,
		   			  double m)
   {
	   sprite_delegate =
	         new RectangularShapeSprite(x,
							            y,
							            width,
							            height,
							            true,
							            c,
							            (RectangularShape)new Ellipse2D.Double());
//	   setCollisionBoxScale(0.9);
	   mass = m;
	   d_acceleration = new Vector2D(0.0, 0.0);
	   shot_direction = new Vector2D(d_acceleration);
	   horizontal_reflection_coefficient = 1.0;
	   first_update = true;
	   on_ground = false;
	   last_position = new Vector2D(this.getCollisionBox().getCenterX(),
			   						this.getCollisionBox().getCenterY());
	   index = 0;
	   update_last = System.currentTimeMillis();
   }
*/
   public PHeroSprite(double x, double y, double m)
   {
	   sprite_delegate =
	   		 new RectangularShapeSprite(x + hero_images_left[0].getWidth(),	// not neccesary
	   									y - hero_images_left[0].getHeight(),
							            (double)hero_images_left[0].getWidth(),
							            (double)hero_images_left[0].getHeight(),
							            true,
							            Color.white,
							            (RectangularShape)new Rectangle2D.Double());
//	   setCollisionBoxScale(0.9);
	   mass = m;
	   d_acceleration = new Vector2D(0.0, 0.0);
	   shot_direction = new Vector2D(d_acceleration);
	   horizontal_reflection_coefficient = 1.0;
	   first_update = true;
	   on_ground = false;
	   last_position = new Vector2D(this.getCollisionBox().getCenterX(),
			   						this.getCollisionBox().getCenterY());
	   index = 0;
	   update_last = System.currentTimeMillis();
}

   /**
    * If the ball hits anything, it must "bounce" accurately off the surface
    * it hit.  To do this, we first reverse the ball's velocity to "back it out"
    * of the object it hit.  This is so it (hopefully) doesn't get "trapped inside"
    * the object it hit.  Then we reflect the ball based on the geometry of the
    * surface of the object at the point the ball hit it, to get an accurate
    * transfer of momentum (although this particular formula assumes the object
    * the ball hit is immovable, so the ball retains all of the momentum).  There
    * is also an extra check, just to be safe, to make sure the ball doesn't
    * appear to have collided with the same object twice in a row, because that
    * shouldn't happen in this game.  If such a case is detected the ball will
    * try to keep moving without bouncing, in order to avoid the "trap".
    * 
    * @param s	The sprite that this Ball sprite has collided with
    */
   public void collideWith(Sprite s)
   {
//	  System.err.println("Collision! (" + was_collision + ")");
      Vector2D vel;
      double theta;
      if (!(   s instanceof PHeroProjectileSprite
    		|| s instanceof PEnemyProjectileSprite))
      {
//     	 System.err.println("In collideWith() method...");
         last_collision = s;
//       System.err.println(s.getCollisionBox().getMinX() + ", " + s.getCollisionBox().getMaxY());
         // First, back the ball out of whatever it hit.  This should
         // help us avoid problems with the ball getting trapped inside
         // a wall, etc.  This is pretty simple-minded, and might do
         // funny things sometimes.
         vel = this.getVelocity();
         vel.negate();
         vel = vel.makeUnitVector();
         while (this.getCollisionBox().intersects(s.getCollisionBox()))
         {
//        	 System.err.println("Intersecting!!!");
//        	 if (tilt == true)
//       	 {
//        		 AffineTransform af = new AffineTransform();
//        		 af.scale(-5.0, -5.0);
//        		 af.transform(vel, vel);
//        		 tilt = false;
//        	 }
        	 this.translate(vel);
         }
         // Next compute the new velocity, by reflecting the old velocity
         // around the angle tangent to whatever we just hit

         // First measure the angle from the center of the other object to
         // the center of this ball
         theta =
            new Vector2D(s.getCollisionBox().getCenter()).angleTo(
               this.getCollisionBox().getCenter());
//         System.err.println("Pre-theta: " + theta);
         // Use that angle to find the surface normal of the object we hit
         theta = s.getSurfaceNormal(theta);
//         System.err.println("Theta: " + theta);
         // Little test to determine whether hero is "standing" on a surface.
         if (   theta <= -Math.PI / 2 + angle_tollerance
        	 && theta >= -Math.PI / 2 - angle_tollerance)
         {
        	 on_ground = true;
             last_position = new Vector2D(this.getCollisionBox().getCenterX(),
		                                  this.getCollisionBox().getCenterY());
         }
         else
         {
        	 on_ground = false;
             horizontal_reflection_coefficient = vertical_reflection_coefficient;
         }
                  
         // Rotate the surfaceNormal to give us a tangent line
         theta += Math.PI / 2;
         // Finally, rotate our velocity around the tangent line
//         System.out.println("Velocity (before): " + this.getVelocity());
         vel = this.getVelocity();
         vel.reflect(theta);
         // Dull the vertical bounce by some factor.
         AffineTransform af = new AffineTransform();
	   	 af.scale(horizontal_reflection_coefficient, 1.0);
	   	 af.transform(vel, vel);
	   	 if (on_ground)
	   	 {
	   		 af = new AffineTransform();
	   		 af.scale(1.0, vertical_reflection_coefficient);
	   		 af.transform(vel, vel);
	   	 }
         this.setVelocity(vel);
//         System.out.println("Velocity (after): " + this.getVelocity());
         horizontal_reflection_coefficient = 1.0;
      }
   }

   /**
    * Invoke the getCollisionBox() method on this object's RectangularShapeSprite delegate.
    * 
    * @see RectangularShapeSprite#getCollisionBox()
    */
   public BetterRectangle getCollisionBox()
   {
      return sprite_delegate.getCollisionBox();
   }

   /**
    * The surface normal for a circle always sticks straight out from the circle,
    * perpendicular to the surface of the circle at every point.
    * 
    * @param theta
    * @see Sprite#getSurfaceNormal(double)
    */
   public double getSurfaceNormal(double theta)
   {
      return theta;
   }

   /**
    * Invoke the getVelocity() method on this object's RectangularShapeSprite delegate.
    * 
   * @see RectangularShapeSprite#getVelocity()
   */
   public Vector2D getVelocity()
   {
      return sprite_delegate.getVelocity();
   }

   /**
    * Invoke the paint() method on this object's RectangularShapeSprite delegate.
    * 
    * @see RectangularShapeSprite#paint(java.awt.Graphics2D)
    */
   public void paint(Graphics2D g)
   {
	   BetterRectangle br = getCollisionBox();
	   g.drawImage(hero_images[index], new AffineTransform(
					   						1.0, 0.0,
					   						0.0, 1.0,
					   						sprite_delegate.getCollisionBox().getMinX(),
					   						sprite_delegate.getCollisionBox().getMinY()),
					   						null);
//	   System.err.println("Index: " + index);
   }

   /**
    * Invoke the setAcceleration() method on this object's RectangularShapeSprite delegate.
    * 
   * @see RectangularShapeSprite#setAcceleration(Vector2D)
   */
   public void accelerateByXY(Vector2D a)
   {
	  d_acceleration = new Vector2D(a);

	  if (d_acceleration.getX() > 0.0)
	  {
		  hero_images = hero_images_right;
	  }
	  else
	  {
		  hero_images = hero_images_left;
	  }
   }

   /**
    * Invoke the setCollisionBox() method on this object's RectangularShapeSprite delegate.
    * 
   * @see rjones.game.engine.sprite.RectangularShapeSprite#setCollisionBoxScale(double)
   */
   public void setCollisionBoxScale(double s)
   {
      sprite_delegate.setCollisionBoxScale(s);
   }

   /**
    * Invoke the setVelocity() method on this object's RectangularShapeSprite delegate.
    * 
   * @see RectangularShapeSprite#setVelocity(Vector2D)
   */
   public void setVelocity(Vector2D v)
   {
      sprite_delegate.setVelocity(v);
   }

   public void jump()
   {
	   if (on_ground == true)
	   {
//		   System.err.println("Velocity (before): " + this.getVelocity());
		   Vector2D v = this.getVelocity();
//		   v.subtract(new Vector2D(0.0, Math.abs(0.5 * v.getX())));
		   v.add(new Vector2D(0.0, 4.0));
		   this.setVelocity(v);
//		   System.err.println("Velocity (after): " + this.getVelocity());
	   }
   }
   
   public void setTilt()
   {
	   tilt = true;
   }
   
   public void shoot(double mass)
   {
	   PHeroProjectileSprite pps =
		   new PHeroProjectileSprite(this.getCollisionBox().getCenterX() - 5.0,
							     this.getCollisionBox().getCenterY() - 5.0,
							     8.0, 12.0,
							     Color.orange,
							     mass,
							     this);
	   pps.setManager(manager);
	   pps.setCollisionSound(collision_sound);
	   collision_sound.play();
	   Vector2D velocity = new Vector2D(0.0, 0.0);
	   AffineTransform af = new AffineTransform();
	   af.scale(50.0, 50.0);
//	   System.err.println("Shoot!!! + " + shot_direction);
	   velocity = new Vector2D(af.transform(shot_direction, velocity));
	   pps.setVelocity(velocity);
	   manager.getSpriteManager().add(pps);
   }
   
   /**
    * Invoke the translate() method on this object's RectangularShapeSprite delegate.
    * 
    * @see RectangularShapeSprite#translate(Vector2D)
    */
   public void translate(Vector2D v)
   {
      sprite_delegate.translate(v);
   }
   
   public void place(Vector2D v)
   {
	  sprite_delegate.place(new Vector2D(v.getX() - hero_images_left[0].getWidth(),
										 v.getY() - hero_images_left[0].getHeight()));
   }

   /**
    * Invoke the update() method on this object's RectangularShapeSprite delegate.
    * 
    * @see RectangularShapeSprite#update()
    */
   public boolean update(long d_ms)
   {
	   if (on_ground == true)
	   {
		   on_ground = isStillOnGround();
	   }

	   if (!first_update)
	   {
		   Vector2D dv = new Vector2D(d_acceleration.getX() * d_ms / 1000.0,
		   					 		  (d_acceleration.getY() + g) * d_ms / 1000.0);
		   Vector2D v = this.getVelocity();
		   v.add(dv);
		   this.setVelocity(v);
		   if (!d_acceleration.isZero())
		   {
			   shot_direction = new Vector2D(d_acceleration);
//			   System.err.println("Shot: " + shot_direction);
			   d_acceleration = new Vector2D(0.0, 0.0);
		   }
		   go_up = false;
	   }
	   else
	   {
		   first_update = false;
	   }
	   
	   if (Math.abs(this.getVelocity().getX()) > velocity_tollerance)
	   {
		  if (System.currentTimeMillis() - update_last >= animation_delay)
		  {
			  index = (index + 1) % hero_images.length;
			  update_last = System.currentTimeMillis();
		  }
//		  System.err.println("Index: " + index);   
	   }

//	   System.err.println("I am now at " + (this.getCollisionBox().getCenterX() + 10.0)
//			   				 	  + ", " + (this.getCollisionBox().getCenterY()));
	   	   	   	   
	   return sprite_delegate.update(-1);
   }

   private boolean isStillOnGround()
   {
	   if (   this.getCollisionBox().getCenterY() < (last_position.getY() - position_tollerance)
		   || this.getCollisionBox().getCenterY() > (last_position.getY() + position_tollerance))
	   {
		   return false;
	   }
	   else
	   {
		   return true;
	   }
   }

}