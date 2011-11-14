import com.brackeen.javagamebook.graphics.*;
import java.awt.*;
import javax.swing.ImageIcon;

/* The Player class extends the Sprite class */
public class Player extends Sprite {
	// high for debugging purposes
	// .15 seems to be a good speed
    private static final float Speed = .35f;

    private int     floorY;
    private int     screenMaxY;
    private int     screenMinY;
    private Bullet  theBullet = null;

    public Player(Animation anim, int screenMin, int screenMax) {
        super(anim);
        screenMinY = screenMin;
        screenMaxY = screenMax;
        setFloorY(screenMax - getHeight());
    }
    
    private void createBullet() {
    	Image bulletImg = new 
    				ImageIcon("../graphics/playerBullet.png").getImage();
    	Animation anim = new Animation();
    	anim.addFrame(bulletImg, 1000);
    	theBullet = new Bullet(anim, getX()+getWidth()/2, getY()-getHeight(),
    								true, screenMaxY, screenMinY);
    	return;
    }

    /* Sets the location of "floor" */
    public void setFloorY(int floorY) {
        this.floorY = floorY;
        setY(floorY);
    }
    
    /* fires a bullet, if possible  */
    public void shoot() {
    	// one shot on the screen at a time
    	if (theBullet == null || theBullet.getLive() != true) {
    		theBullet = null;
    		createBullet();
    	}
    }
    
    /* responsible for moving the player left */
    public void moveLeft() {
    	setVelocityX(-Player.Speed);
    	return;
    }
    
    /* responsible for moving the player right */
    public void moveRight() {
    	setVelocityX(Player.Speed);
    	return;
    }
    
    public void draw(Graphics2D g) {
    	g.drawImage(getImage(),
            Math.round(getX()),
            Math.round(getY()),
            null);
    	if (theBullet != null) {
    		theBullet.draw(g);
        }
    }

    /* Updates the player's positon and animation */
    public void update(long elapsedTime) {
        // move player
        super.update(elapsedTime);
        if (theBullet != null) {
        	theBullet.update(elapsedTime);
        }
    }
    
    public Point getBulletLocation() {
    	if (theBullet != null) {
			Point bPoint = new Point();
			bPoint.x = (int)theBullet.getX();
			bPoint.y = (int)theBullet.getY();
			return bPoint;
		} else {
			return null;
		}
    }
}
