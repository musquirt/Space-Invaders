import com.brackeen.javagamebook.graphics.*;
import java.awt.*;
import javax.swing.ImageIcon;
import java.util.List;
import java.util.ArrayList;

public class Blockades {

	int screenHeight;
	int screenWidth;
	int top;
	int count;
	List<Block> blocks;
	List<Sprite> barriers;


	public Blockades(int c, int H, int W, Player p) {
		screenHeight = H;
		screenWidth = W;
		count = c;

		blocks = new ArrayList<Block>();
		barriers = new ArrayList<Sprite>();

		int divides = screenWidth/(count+1);

		Image barrier = new ImageIcon("../graphics/barrier_full.png").getImage();
		Animation anim = new Animation();
		anim.addFrame(barrier, 1000);

		Image hit0 = new ImageIcon("../graphics/0hit.png").getImage();
		Image hit1 = new ImageIcon("../graphics/1hit.png").getImage();
		Image hit2 = new ImageIcon("../graphics/2hit.png").getImage();
		Image hit3 = new ImageIcon("../graphics/3hit.png").getImage();

		for(int i = 0; i < count; i++) {
			Sprite b = new Sprite(anim);
			b.setY(p.getY()-b.getHeight()-10);
			b.setX((i+1)*divides-b.getWidth()/2);
			barriers.add(b);
			top = (int) b.getY();

			for(int j = 0; j < 4; j++) {
				for(int k = 0; k < 6; k++) {
					if ((j == 3) && ((k == 2) || (k == 3))) {
						blocks.add(null);
					}
					else {
						Animation ba = new Animation();
						ba.addFrame(hit0, 1000);
						ba.addFrame(hit1, 1000);
						ba.addFrame(hit2, 1000);
						ba.addFrame(hit3, 1000);
						Block bl = new Block(ba);
						bl.setXY((int) (i+1)*divides-b.getWidth()/2 + k*bl.getWidth(), (int) p.getY()-b.getHeight()-10 + j*bl.getHeight());
						blocks.add(bl);
					}
				}
			}

		}
	}

	public int getTop() {
		return top;
	}

	public void update(long elapsedtime) {
		for(int i = 0; i < barriers.size(); i++) {
			barriers.get(i).update(elapsedtime);
		}
		for(int i = 0; i < blocks.size(); i++) {
			if (blocks.get(i) != null) {
				blocks.get(i).update(0);
			}
		}
	}

	public void draw(Graphics2D g) {
		for(int i = 0; i < barriers.size(); i++) {
			g.drawImage(barriers.get(i).getImage(),
            Math.round(barriers.get(i).getX()),
            Math.round(barriers.get(i).getY()),
            null);
		}
		for(int i = 0; i < blocks.size(); i++) {
			if (blocks.get(i) != null) {
				blocks.get(i).draw(g);
			}
		}
	}
	
	public boolean checkPlayerCollisions(Player p, Block b) {
		if (b.checkCollisions(p.getBulletLocation()) == true) {
			p.BulletCollided();
			return true;
		}
		return false;
	}
	
	public void checkShipCollisions(List<Enemy> ships, Block b) {
		for(int k = 0; k < ships.size(); k++) {
			if (ships.get(k) != null) {
				b.shipCollision(ships.get(k).getPositionM());
				b.shipCollision(ships.get(k).getPositionL());
				b.shipCollision(ships.get(k).getPositionR());
			}
		}
	}
	
	public void checkBulletCollisions(List<Bullet> missiles, int num) {
		if (blocks.get(blocks.size()-1-num) != null) {
			int j = 0;
			while (j < missiles.size()) {
				if (blocks.get(blocks.size()-1-num).checkCollisions(missiles.get(j).getBulletLocation()) == true) {
					missiles.remove(j);
				}
				else {
					j++;
				}
			}
		}
	}

	public void checkCollisions(Player p, List<Bullet> missiles, List<Enemy> ships) {
		boolean playerBullet = false;
		for(int i = blocks.size() - 1; i >= 0; i--) {
			if (blocks.get(i) != null) {
				if (playerBullet == false) {
					playerBullet = checkPlayerCollisions(p, blocks.get(i));
				}
				checkShipCollisions(ships, blocks.get(i));
			}
			checkBulletCollisions(missiles, i);
		}
	}

}
