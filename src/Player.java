import com.brackeen.javagamebook.graphics.*;
import java.awt.*;
import javax.swing.ImageIcon;

/* The Player class extends the Sprite class */
public class Player extends Sprite {
	// high for debugging purposes
    private static final float Speed = .20f;

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
    	theBullet = new Bullet(anim, getX()+getWidth()/2, getY()-getHeight()/2,
    								true, screenMaxY, screenMinY);
    	return;
    }

    /* Sets the location of "floor" */
    public void setFloorY(int floorY) {
        this.floorY = floorY;
        setY(floorY);
    }
	
	public int getFloorY() {
		return this.floorY;
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
    public void moveLeft(float customSpeed) {
    	setVelocityX(-customSpeed);
    	return;
    }
    
    /* responsible for moving the player right */
    public void moveRight() {
    	setVelocityX(Player.Speed);
    	return;
    }
    public void moveRight(float customSpeed) {
    	setVelocityX(customSpeed);
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
    	if (theBullet != null && theBullet.getLive() == true) {
			return theBullet.getBulletLocation();
		} else {
			return null;
		}
    }
	
	public boolean checkCollisions(Point p) {
		if ((p.x >= this.getX()) && (p.x <= this.getX() + this.getWidth())
			&& (p.y >= this.getY()) && (p.y <= (this.getY() + this.getHeight())))
		{	
			return true;
		}
		return false;		
	}
    
    public void BulletCollided() {
    	theBullet.killBullet();
    }
}
