import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.bot.event.MessageEvent;
import org.powerbot.game.bot.event.listener.MessageListener;
import org.powerbot.game.bot.event.listener.PaintListener;

@Manifest(authors = { "BeeStar" }, name = "wjcCitadel", description = "Works at citadel", version = 2.0)
public class wjcCitadel extends ActiveScript implements PaintListener, MessageListener {

    final int[] 
    loomIDs = { 15737, 15736, 15735, 15734, 15340, 15339, 15338, 15335, 14662, 
    		14478, 14477, 14435, 15174, 15057, 15056, 15055, 15684, 15685, 15691, 15692, 15693, 14335, 14337, 14340, 14341},
    		
    rootIDs = { 17858, 17861, 17866, 17907, 17913, 17920, 18874, 18883, 18901, 18907, 18920, 18921, 19093, 19094,
    		 18406, 18496, 18503, 18662, 18856, 18859},
    		
    rockIDs = {24910, 24917, 25057, 25058, 24609, 24908, 21738, 21751, 21760, 21754, 25062, 25063, 25053, 25052, 21517, 21518, 21556, 21558, 21501, 21500, 24983, 21560,
    		21567, 21568, 25031, 25032, 28007,  28008,  28009,  28016,  28017,  28018,  28019, 28020, 28021, 28022, 28023, 28028, 28029,
    		28030, 28031, 28032, 28033, 28034, 28035, 28036, 28037, 28038, 28039, 28043, 28044, 28045, 28048, 28049, 28050, 28051, 28052,
    		28053, 28085, 28086, 28087, 28088, 28089, 28090, 28091, 28092, 28093,  28074, 28075, 28076, 28077, 28078, 28079, 28080, 28082, 28083, 28084,};
    
    final int loomAnim = 5562,
    		rootAnim = 5781,
    		rockAnim = 6051,
    		rootSetting = 2537,
    		rockSetting = 2538,
    		resourceRespawnSetting = 2273,
    		resourceSetting = 2277;
    
    int iterations = 0,
    startExp = 0,
    startResources = 0,
    resources = 0;
    long startTime = 0;
    boolean needSetup = true,
    guiInitialized = false;
    String Mode = "Crafting";
    
    SceneObject currentThing,
    thing1,
    thing2;
    

    private class craft extends Strategy implements Runnable, Condition{
		public void run() {
			SceneObject loom = SceneEntities.getNearest(loomIDs);
			if(loom != null){
				if(loom.isOnScreen()){
					if(Players.getLocal().getAnimation() != loomAnim){
						if(!Players.getLocal().isMoving()){
							sleep(153,211);
							loom.interact("Weave");
							sleep(2000, 3100);
						}
					}
				}
				else{
					Camera.turnTo(loom);
				}
			}
		}
		public boolean validate() {
			return Mode.equals("Crafting") && guiInitialized;
		}
    }
    
    private class mine extends Strategy implements Runnable, Condition{
    	public void run(){
    		if(needSetup){
    			thing1 = SceneEntities.getNearest(rockIDs);
    			thing2 = SceneEntities.getNearest(new Filter<SceneObject>(){
    		        public boolean accept(final SceneObject SceneObject){
    		                return SceneObject != null && contains(rockIDs, SceneObject.getId()) && !SceneObject.equals(thing1);
    		        }
                	});
    			currentThing = thing1;
    			needSetup = false;
    		}
    		if(Settings.get(rockSetting) != 0){
    			sleep(30, 60);
    		}
    		else{
    			if(Settings.get(resourceRespawnSetting) != 0){
    				switchObject();
    			}
    			currentThing.interact("Mine");
    		}
    	}
    	public boolean validate(){
    		return Mode.equals("Mining") && guiInitialized;
    	}
    }
    
    private class woodcut extends Strategy implements Runnable, Condition{
    	public void run(){
    		if(needSetup){
    			thing1 = SceneEntities.getNearest(rootIDs);
                thing2 = SceneEntities.getNearest(new Filter<SceneObject>(){
    		        public boolean accept(final SceneObject SceneObject){
    		                return SceneObject != null && contains(rootIDs, SceneObject.getId()) && !SceneObject.equals(thing1);
    		        }
                	});
                currentThing = thing1;
                needSetup = false;
    		}
			
			if(Settings.get(rootSetting) != 0){
				sleep(30, 60);
			}
			else{
				if(Settings.get(resourceRespawnSetting) != 0){
					switchObject();
				}
				currentThing.interact("Chop", "Root");
			}
    	}
    	public boolean validate(){
    		return Mode.equals("Woodcutting") && guiInitialized;
    	}
    }
    
    private class antiban extends Strategy implements Runnable, Condition{
    	public void run(){
    		int rand = Random.nextInt(0, 300);
    		switch(rand){
    			case 0:
    				Mouse.move(Random.nextInt(37, 392), Random.nextInt(343, 6578));
    				sleep(20, 150);
    				break;
    			case 1:
    				Camera.setAngle(Random.nextInt(96, 483));
    				sleep(20, 150);
    				break;
    			case 2: 
    				Camera.setAngle(Random.nextInt(21, 358));
    				sleep(20, 150);
    				break;
    			case 3:
                    int dx = Random.nextInt(-30, 30);
                    int dy = Random.nextInt(-30, 30);
                    Mouse.move(Mouse.getX() + dx, Mouse.getY() + dy);
                    sleep(20, 150);
                    break;
    		}
    	}
    	public boolean validate(){
    		return true;
    	}
    }

    public void switchObject(){
    	if(currentThing.equals(thing1)){
    		currentThing = thing2;
    	}
    	else{
    		currentThing = thing1;
    	}
    }
    
    public boolean contains(int[] array, int id){
    	for(int x = 0; x < array.length; x++){
    		if(array[x] == id){
    			return true;
    		}
    	}
    	return false;
    }
    
    public void sleep(int s1, int s2) {
            Time.sleep(Random.nextInt(s1, s2));
    }
    
    public int getExp(){
    	return Skills.getExperience(Skills.MINING) + Skills.getExperience(Skills.WOODCUTTING) + Skills.getExperience(Skills.CRAFTING);
    }
    
    public void messageReceived(MessageEvent m) {
    	if(m.getMessage().contains("your resource cap for this week")){
    		Game.logout(true);
    		stop();
    	}
    }    

    public void onRepaint(Graphics g1) {

            long millis = System.currentTimeMillis() - startTime;

            long hours = millis / (1000 * 60 * 60);
            millis -= hours * (1000 * 60 * 60);

            long minutes = millis /(1000 *60);
            millis -= minutes * (1000 * 60);

            long seconds = millis / 1000;
            
            int xpGained = getExp() - startExp;
            resources = (Settings.get(resourceSetting) - startResources) / 100;
            float xphour = 0;
            if ((minutes > 0 || hours > 0 || seconds > 0) && xpGained > 0) {
                    xphour = ((float) xpGained)/(float)(seconds + (minutes*60) + (hours*60*60)) * 3600;
            }

            final Color color1 = new Color(0, 255, 51);
            final Color color2 = new Color(0, 0, 0);

            final BasicStroke stroke1 = new BasicStroke(1);

            final Font font1 = new Font("Arial", 1, 15);
            final Font font2 = new Font("Arial", 0, 11);

            Graphics2D g = (Graphics2D)g1;
            g.setColor(color1);
            g.fillRoundRect(25, 25, 125, 150, 16, 16);
            g.setColor(color2);
            g.setStroke(stroke1);
            g.drawRoundRect(25, 25, 125, 150, 16, 16);
            g.setFont(font1);
            g.drawString("wjcCitadel", 52, 48);
            g.setFont(font2);
            g.drawString("Resources: " + resources, 39, 71);
            g.drawString("Exp gained: " + xpGained, 39, 101);
            g.drawString("Exp/hour: " + xphour, 39, 131);
            g.drawString("Runtime: " + hours + ":" + minutes + ":" + seconds, 39, 161);

            MouseTrail.add(Mouse.getLocation());
            MouseTrail.draw(g1);
    }
    
    protected void setup() {
    	SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                    GUI g = new GUI();
                    g.setVisible(true);
            }
    	});
    		
    	startExp = getExp();
        startTime = System.currentTimeMillis();
        startResources = Settings.get(resourceSetting);
           
        provide(new craft());
        provide(new woodcut());
        provide(new mine());
        provide(new antiban());
    }
    
    private static class MouseTrail {
		private final static int SIZE = 50;
		private final static float rainbowStep = (float) (1.0/SIZE);
		private final static double alphaStep = (255.0/SIZE);
	
		private static Point[] points = new Point[SIZE];
		private static int index = 0;
		private static float offSet = 0.05f;
		private static float start = 0;
	
		public static void add(Point p) {
			points[index++] = p;
			index %= SIZE;
		}
	
		public static void draw(Graphics graphics) {
			Graphics2D g2D = (Graphics2D) graphics;
			g2D.setStroke(new BasicStroke(2F));
			double alpha = 0;
			float rainbow = start;
	
			start += offSet;
			if (start > 1) {
				start -= 1;
			}
	
			for (int i = index; i != (index == 0 ? SIZE-1 : index-1); i = (i+1)%SIZE) {
				if (points[i] != null && points[(i+1)%SIZE] != null) {
					int rgb = Color.HSBtoRGB(rainbow, 0.9f, 0.9f);
					rainbow += rainbowStep;
	
					if (rainbow > 1) {
						rainbow -= 1;
					}
					g2D.setColor(new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, (int)alpha));
					g2D.drawLine(points[i].x, points[i].y, points[(i+1)%SIZE].x, points[(i+1)%SIZE].y);
					alpha += alphaStep;
				}
			}
		}
	}
    
    @SuppressWarnings("serial")
	class GUI extends JFrame {
    	public GUI() {
    		initComponents();
    	}

    	private void modeBoxActionPerformed(ActionEvent e) {

    	}

    	private void initComponents() {
    		label1 = new JLabel();
    		label2 = new JLabel();
    		modeBox = new JComboBox<>();
    		button1 = new JButton();

    		Container contentPane = getContentPane();

    		label1.setText("wjcCitadel by BeeStar");

    		label2.setText("Which Plot?");

    		modeBox.setModel(new DefaultComboBoxModel<>(new String[] {
    			"Crafting",
    			"Mining",
    			"Woodcutting"
    		}));

    		button1.setText("Start");
    		button1.addActionListener(new ActionListener() {
    			@Override
    			public void actionPerformed(ActionEvent e) {
    	    		String input = modeBox.getSelectedItem().toString();
    	    		if(input.equals("Mining")){
    	    			Mode = "Mining";
    	    		} else if(input.equals("Woodcutting")){
    	    			Mode = "Woodcutting";
    	    		} else{
    	    			Mode = "Crafting";
    	    		}
    	    		guiInitialized = true;
    	    		setVisible(false);
    	    		dispose();
    			}
    		});

    		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
    		contentPane.setLayout(contentPaneLayout);
    		contentPaneLayout.setHorizontalGroup(
    			contentPaneLayout.createParallelGroup()
    				.addGroup(contentPaneLayout.createSequentialGroup()
    					.addGroup(contentPaneLayout.createParallelGroup()
    						.addGroup(contentPaneLayout.createSequentialGroup()
    							.addContainerGap()
    							.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
    								.addComponent(label1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    								.addComponent(modeBox)
    								.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
    									.addComponent(label2)
    									.addGap(20, 20, 20))))
    						.addGroup(contentPaneLayout.createSequentialGroup()
    							.addGap(43, 43, 43)
    							.addComponent(button1)))
    					.addContainerGap(12, Short.MAX_VALUE))
    		);
    		contentPaneLayout.setVerticalGroup(
    			contentPaneLayout.createParallelGroup()
    				.addGroup(contentPaneLayout.createSequentialGroup()
    					.addContainerGap()
    					.addComponent(label1)
    					.addGap(18, 18, 18)
    					.addComponent(label2)
    					.addGap(18, 18, 18)
    					.addComponent(modeBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    					.addGap(18, 18, 18)
    					.addComponent(button1)
    					.addContainerGap(15, Short.MAX_VALUE))
    		);
    		pack();
    		setLocationRelativeTo(getOwner());
    	}
    	
    	private JLabel label1;
    	private JLabel label2;
    	private JComboBox<String> modeBox;
    	private JButton button1;
    }

}