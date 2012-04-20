/**
 * 
 */
package phlogenim.items;

/**
 * @author: Damir Aracic
 * 
 * Item is any structure on the screen that accumulates several sprites
 * (e.g. a wall comprises many bricks). These sprites must be registered
 * somehow with the sprite manager. How this is done is defined by the
 * registerItem() method.
 *
 */
public interface Item {
	
	public void registerItem();

}
