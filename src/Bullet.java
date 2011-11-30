import com.brackeen.javagamebook.graphics.*;
import java.awt.*;

/* The Bullet class extends the Sprite class */
public class Bullet extends Sprite {

    private static final float Speed = .35f;
    private boolean live;
    private int min;
    private int max;

    public Bullet(Animation anim, float startPosX, float startPosY, boolean dir, int screenMax, int screenMin) {
        super(anim);
        live = true;
        setX(startPosX);
        setY(startPosY);
        max = screenMax;
        min = screenMin;
        if (dir == true) {
        	setVelocityY(-Bullet.Speed);
        } else {
        	setVelocityY(Bullet.Speed);
        }
    }
	
	public Bullet(Animation anim, float startPosX, float startPosY, boolean dir, int screenMax, int screenMin, float speed) {
        super(anim);
        live = true;
        setX(startPosX);
        setY(startPosY);
        max = screenMax;
        min = screenMin;
        if (dir == true) {
        	setVelocityY(-speed);
        } else {
        	setVelocityY(speed);
        }
    }

    /* Updates the bullet's positon and animation */
    public void update(long elapsedTime) {
        // move bullet
        super.update(elapsedTime);
        // check to see if we're still on screen
        if (getY() > max || getY() < min) {
        	live = false;
        	setVelocityY(0);
        }
        
        return;
    }
    
    // for when a bullet collides with player/enemy/barrier
    public void killBullet() {
    	live = false;
    }
    
	public Point getBulletLocation() {
		Point bPoint = new Point();
		bPoint.x = (int) (this.getX()+this.getWidth()/2);
		bPoint.y = (int) (this.getY());
		return bPoint;
	}
	
    public void draw(Graphics2D g) {
   		if (live == true) {
			g.drawImage(getImage(),
				    Math.round(getX()),
				    Math.round(getY()),
				    null);
		}
		return;
    }
    
    public boolean getLive() {
    	return live;
    }
}
