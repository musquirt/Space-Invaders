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
        	setVelocityY(-Bullet.Speed);
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
