import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.bot.event.MessageEvent;
import org.powerbot.game.bot.event.listener.MessageListener;
import org.powerbot.game.bot.event.listener.PaintListener;


@Manifest(authors = { "BeeStar" }, name = "wjcPenguinAid", description = "Helps you find penguins", version = 1.1)
public class wjcPenguinAid extends ActiveScript implements PaintListener, MessageListener {
	//??? - Bush - ??? - Cactus - Crate - Rock - Toadstool
	public final int[] penguins = {8104, 8105, 8106, 8107, 8108, 8109, 8110};
	final int animation = 10355, bear = 43099;
	int penguinsFound = 0;
	
	public void drawTile(Graphics g, Tile tile, Color col) {
        for (Polygon poly : tile.getBounds()) {
            boolean drawThisOne = true;
            for (int i = 0; i < poly.npoints; i++) {
                Point p = new Point(poly.xpoints[i], poly.ypoints[i]);
                if (!Calculations.isOnScreen(p)) {
                    drawThisOne = false;
                }
            }
            if (drawThisOne) {
                Color col2 = new Color(col.getRed(), col.getGreen(), col.getBlue(), 80);
                g.setColor(col2);
                g.fillPolygon(poly);
                g.setColor(col);
                g.drawPolygon(poly);
            }
        }
    }
	
	@Override
	public void onRepaint(Graphics draw) {
		draw.setColor(Color.cyan);
		NPC Penguin = NPCs.getNearest(penguins);
		if(Penguin != null){
	    	for(Polygon p : Penguin.getBounds()){
				draw.fillPolygon(p);
			}
			
			drawTile(draw, Penguin.getLocation(), Color.cyan);
			final Point p = Penguin.getLocation().getMapPoint();
			final Point player = Players.getLocal().getLocation().getMapPoint();
			draw.fillOval(p.x - 5, p.y - 5, 10, 10);
			draw.drawLine(player.x, player.y, p.x, p.y);
			draw.drawLine(player.x + 1, player.y + 1, p.x + 1, p.y + 1);
			draw.drawLine(player.x - 1, player.y - 1, p.x - 1, p.y - 1);
		}
		
		SceneObject Bear = SceneEntities.getNearest(bear);
		if(Bear != null){
			for(Polygon p : Bear.getBounds()){
				draw.fillPolygon(p);
			}
			
			final Point p = Bear.getLocation().getMapPoint();
			draw.fillOval(p.x - 5, p.y - 5, 10, 10);
		}
		
		draw.setColor(Color.BLACK);
		draw.drawString("Penguins/bears found: "+ penguinsFound, 280, 369);
	}

	@Override
	protected void setup() {
	}

	@Override
	public void messageReceived(MessageEvent m) {
		if(m.getMessage().contains("ou spy on the penguin")){
			penguinsFound++;
		}
	}

}