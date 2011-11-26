import com.brackeen.javagamebook.graphics.*;
import java.awt.*;
import javax.swing.ImageIcon;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.lang.Math;

public class Fleet {
	
	private List<Enemy> ships = new ArrayList<Enemy>();
    private int    		screenMaxY;
    private int     	screenMinY;
	private int			screenMaxX;	
	private float		speed;
	private float		bulletspeed;
	private float		vx;
	private float		vy;
	private int 		columns;
	private int 		rows;
	private int 		vertspace;
	private int 		horizspace;
	private int			yoffset;
	private int			xoffset;
	private boolean     down;
	private long		time;
	private boolean 	gameover;
	public static float startingspeed;
	private int			numships;
	private boolean 	nextlevel;
	private double 		probability;
	
	public Fleet(int screenMin, int screenMax, int screenMaxx) {
		columns = 11;
		rows = 5;
		numships = rows*columns;
		vertspace = 25;
		horizspace = 30;
		yoffset = 100;
		xoffset = 100;
		down = false;
		vy = 0f;
		gameover = false;
		speed = startingspeed;
		vx = speed;
		nextlevel = false;
		probability = 0.01;
		bulletspeed = .25f;
		
		Image small1 = new ImageIcon("../graphics/small_ship_1.png").getImage();
		Image small2 = new ImageIcon("../graphics/small_ship_2.png").getImage();
		Image med1 = new ImageIcon("../graphics/med_ship_1.png").getImage();
		Image med2 = new ImageIcon("../graphics/med_ship_2.png").getImage();
		Image big1 = new ImageIcon("../graphics/big_ship_1.png").getImage();
		Image big2 = new ImageIcon("../graphics/big_ship_2.png").getImage();
			
		screenMinY = screenMin;
        screenMaxY = screenMax;
		screenMaxX = screenMaxx;
		
		int smalloffset = 0; // Small ships need to be offset to stay centered with rest of fleet
		
		Enemy ship;
		for(int r = 0; r < rows; r++) {
			
			for(int c = 0; c < columns; c++) {
				// 1 row of small ships
				if (r == 0) {
					Animation small = new Animation();
					small.addFrame(small1, 750);
					small.addFrame(small2, 750);
					ship = new Enemy(small, screenMin, screenMax, 40);
					smalloffset = 3;
				}
				// 2 rows of medium ships
				else if ((r == 1) || (r == 2)) {
					Animation med = new Animation();
					med.addFrame(med1, 750);
					med.addFrame(med2, 750);
					ship = new Enemy(med, screenMin, screenMax, 20);
					smalloffset = 0;
				}
				// The rest are big ships
				else {
					Animation big = new Animation();
					big.addFrame(big1, 750);
					big.addFrame(big2, 750);
					ship = new Enemy(big, screenMin, screenMax, 10);
					smalloffset = 0;
				}
				
				ship.setX(c*horizspace+xoffset+smalloffset);
				ship.setY(r*vertspace+yoffset);
				ships.add(ship);
			}
		}
	}
	
	public void scaleProbability(double scalar) {
		probability = scalar * probability;
	}

	public void draw(Graphics2D g) {
    	for (int i = 0; i < ships.size(); i++) {
			if (ships.get(i) != null) {
				g.drawImage(ships.get(i).getImage(),
				Math.round(ships.get(i).getX()),
				Math.round(ships.get(i).getY()),
				null);
			}
		}
    	//if (theBullet != null) {
    	//	theBullet.draw(g);
        //}
    }
	
	public void update(long elapsedTime) {
		if (gameover == true) {
			return;
		}
		
		boolean reverse = false;
		for (int i = 0; i < ships.size(); i++) {
			if (ships.get(i) != null) {
				ships.get(i).setVelocityX(vx);
				ships.get(i).setVelocityY(vy);
				ships.get(i).update(elapsedTime);
				if ((ships.get(i).getX() < 0) || (ships.get(i).getX() > (screenMaxX-ships.get(i).getWidth()))) {
					reverse = true;
				}
			}
		}
		if ((reverse == true) && (down == false)) {
			// Make sure ships are not off the screen
			if (speed < 0) {
				float minx = 0f;
				for (int i = 0; i < ships.size(); i++) {
					if (ships.get(i) != null) {
						if (ships.get(i).getX() < minx) {
							minx = ships.get(i).getX();
						}
					}
				}
				for (int i = 0; i < ships.size(); i++) {
					if (ships.get(i) != null) {
						ships.get(i).setX(ships.get(i).getX()-minx);
					}
				}
			}
			else {
				float maxx = 0f;
				for (int i = 0; i < ships.size(); i++) {
					if (ships.get(i) != null) {
						if ((screenMaxX-ships.get(i).getWidth())-ships.get(i).getX() < maxx) {
							maxx = screenMaxX-ships.get(i).getWidth()-ships.get(i).getX();
						}
					}
				}
				for (int i = 0; i < ships.size(); i++) {
					if (ships.get(i) != null) {
						ships.get(i).setX(ships.get(i).getX()+maxx);
					}
				}
			}
			
			// Increase speed
			if ((speed > -1f) && (speed < 1f)) {
				speed = speed * 1.05f;
			}
			else if (speed < 0) {
				speed = -1f;
			}
			else {
				speed = 1f;
			}
			speed = -speed;
			
			down = true;
			vx = 0;
			vy = .08f;
			time = Calendar.getInstance().getTimeInMillis();
		}
		
		if (down == true) {
			if ((Calendar.getInstance().getTimeInMillis()-time) > vertspace/vy) {
				down = false;
				vx = speed;
				vy = 0;
			}
			for(int i = 0; i < ships.size(); i++) {
				if (ships.get(i) != null) {
					if (ships.get(i).getY() >= ships.get(i).getFloorY()) {
						gameover = true;
						vx = 0;
						vy = 0;
					}
				}
			}
		}
		
		
    }
	
	public Enemy checkCollisions(Point p) {
		if (p == null) {
			return null;
		}
		
		for (int i = 0; i < ships.size(); i++) {
			if (ships.get(i) != null) {
				if ((p.x >= ships.get(i).getX()) && (p.x <= (ships.get(i).getX() + ships.get(i).getWidth()))
					&& (p.y >= ships.get(i).getY()) && (p.y <= (ships.get(i).getY() + ships.get(i).getHeight())))
				{	
					Enemy hit = ships.get(i);
					ships.set(i, null);
					numships--;
					if (numships == 0) {
						nextlevel = true;
					}
					return hit;
				}
			}
		}
		
		return null;
	}
	
	public void shoot(List<Bullet> missiles) {
		double shot = Math.random();
		if (shot < probability) {
			int where = (int) Math.ceil(Math.random()*columns);
			Enemy shooter = null;
			for (int i = rows-1; i >= 0; i--) {
				if (ships.get(i*columns+where-1) != null) {
					shooter = ships.get(i*columns+where-1);
					break;
				}
			}
			
			if (shooter == null) {
				return;
			}
			
			double type = Math.floor(Math.random()*2);
			Animation bullet = new Animation();
			if (type == 0) {
				Image b1 = new ImageIcon("../graphics/enemy_bullet_1_1.png").getImage();
				Image b2 = new ImageIcon("../graphics/enemy_bullet_1_2.png").getImage();
				bullet.addFrame(b1, 210);
				bullet.addFrame(b2, 210);
			}
			else {
				Image b1 = new ImageIcon("../graphics/enemy_bullet_2_1.png").getImage();
				Image b2 = new ImageIcon("../graphics/enemy_bullet_2_2.png").getImage();
				Image b3 = new ImageIcon("../graphics/enemy_bullet_2_3.png").getImage();
				Image b4 = new ImageIcon("../graphics/enemy_bullet_2_4.png").getImage();
				Image b5 = new ImageIcon("../graphics/enemy_bullet_2_5.png").getImage();
				Image b6 = new ImageIcon("../graphics/enemy_bullet_2_6.png").getImage();
				bullet.addFrame(b6, 70);
				bullet.addFrame(b5, 70);
				bullet.addFrame(b4, 70);
				bullet.addFrame(b3, 70);
				bullet.addFrame(b2, 70);
				bullet.addFrame(b1, 70);
			}
			
			
			Bullet bill = new Bullet(bullet, shooter.getX(), shooter.getY(), false, screenMaxY, screenMinY, bulletspeed);
			missiles.add(bill);
		}
	}
	
	public boolean checkGameOver() {
		return gameover;
	}
	
	public boolean checkNextLevel() {
		return nextlevel;
	}
	
}
