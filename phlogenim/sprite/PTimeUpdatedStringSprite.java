/**
 * 
 */
package phlogenim.sprite;

import java.awt.Color;

import rjones.game.engine.Ticker;
import rjones.game.engine.sprite.*;

/**
 * @author Damir
 *
 */
public class PTimeUpdatedStringSprite extends PStringSprite implements Sprite {

	private Ticker ticker;
	
	public PTimeUpdatedStringSprite(double x, double y,
								 double width, double height,
								 Ticker t) {
		super(x, y, width, height);
		ticker = t;
	}

	public PTimeUpdatedStringSprite(double x, double y,
								 double width, double height,
								 Ticker t,
								 Color color) {
		this(x, y, width, height, t);
		super.setColor(color);
	}
	
	public boolean update(long dms)
	{
		this.setText("" + ticker.getFrameRate());
				
		return true;
	}

}