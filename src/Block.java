import com.brackeen.javagamebook.graphics.*;
import java.awt.*;
import javax.swing.ImageIcon;

public class Block extends Sprite {

	private int hits;

	public Block(Animation anim) {
		super(anim);
		hits = 0;
	}
	
	public void setXY(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public void draw(Graphics2D g) {
    	g.drawImage(getImage(),
            Math.round(getX()),
            Math.round(getY()),
            null);
    }
	
	public boolean gotHit() {
		if (this.hits < 3) {
			this.hits++;
			this.update(110);
			return true;
		}
		return false;
	}

	public boolean checkCollisions(Point p) {
		if (p == null || this.hits >= 3) {
			return false;
		}
		
		if ((p.x >= (int)this.getX())
				&& (p.x <= ((int)this.getX() + this.getWidth()))
				&& (p.y <= (int)this.getY())
				&& (p.y >= ((int)this.getY() - this.getHeight())))
		{
			return this.gotHit();
		}
		
		return false;
	}
	
	public boolean shipCollision(Point p) {
		if (p == null) {	return false;	}
		
		if (((float)p.x >= this.getX()-0.5f) && ((float)p.x <= (this.getX() + this.getWidth() + 0.5f))
			&& (float) p.y >= (this.getY()+0.5f))
		{
			while (this.gotHit() == true) {
			}
			return true;
		}
		
		return false;
	}

}
