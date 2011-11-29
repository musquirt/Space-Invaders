import com.brackeen.javagamebook.graphics.*;
import java.awt.*;
import javax.swing.ImageIcon;

public class Enemy extends Sprite {

    private int     floorY;
    private int     screenMaxY;
    private int     screenMinY;
	private int		points;

    public Enemy(Animation anim, int screenMin, int screenMax, int p) {
        super(anim);
        screenMinY = screenMin;
        screenMaxY = screenMax;
        floorY = screenMax - getHeight();
		points = p;
    }
 
    /* moves the enemy left at the given speed */
    public void moveLeft(float customSpeed) {
    	setVelocityX(-customSpeed);
    	return;
    }
    
    /* moves the enemy right at the given speed */
    public void moveRight(float customSpeed) {
    	setVelocityX(customSpeed);
    	return;
    }
    
    public void draw(Graphics2D g) {
    	g.drawImage(getImage(),
            Math.round(getX()),
            Math.round(getY()),
            null);
    }

    /* Updates the enemy's positon and animation */
    public void update(long elapsedTime) {
        super.update(elapsedTime);
    }
	
	public int getPoints() {
		return points;
	}
	
	public int getFloorY() {
		return floorY;
	}
	
	public Point getPosition() {
		Point p = new Point();
		if (getVelocityX() < 0) {
			p.x = (int) getX();
		}
		else if (getVelocityX() > 0) {
			p.x = (int) getX() + getWidth();
		}
		else {
			p.x = (int) getX() + getWidth()/2;
		}
		p.y = (int) getY() + getHeight();
		return p;
	}


}