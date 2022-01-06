import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static java.time.Clock.system;
import static java.time.InstantSource.system;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.Timer;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sarra
 */
public class Player {
    
    int x;
    int y;
    int width;
    int height;
    double xspeed; 
    double yspeed;
    
    AudioInputStream ais;
    Clip clip;
    
    private BufferedImage img = null;
    String name;
    
    Rectangle hitBox;
    
    boolean keyLeft;
    boolean keyRight;
    boolean keyUp;
    boolean keyDown;
    
    game_over GO;
    JFrame parent;
    GamePanel panel; 
    int score;
    
    EnterName nickname;
    
    Font pixelmix;
       
    public Player(int x, int y, GamePanel panel) throws UnsupportedAudioFileException, LineUnavailableException,IOException, FontFormatException{
        try {
            this.nickname = new EnterName();
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.panel = panel;
        this.x = x;
        this.y = y;
        
        width = 50;
        height = 100;
        
        hitBox = new Rectangle(x,y,width,height);
    
         //death sound 
            File file = new File("src\\\\wav\\\\death.wav");
            try {
                ais = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(ais);
               
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
            } 

    }
    
    public void set() throws IOException, InterruptedException{
        
        if (keyLeft == keyRight) {
            xspeed *= 0.8;
        }
        else if (keyLeft && !keyRight) {
            xspeed --;
        } 
        else if (keyRight && !keyLeft) {
            xspeed ++;
        }  
        if (xspeed > 0 && xspeed < 0.75) {
            xspeed = 0;
        } 
        if (xspeed < 0 && xspeed > -0.75) {
            xspeed = 0;
        }
        
        if (xspeed >7) {
            xspeed = 7;
        }
        if (xspeed < -7) {
            xspeed = -7;
        }
        
        if (keyUp) {
            
            hitBox.y ++; 
            for (wall wall: panel.walls) {
                if(wall.hitBox.intersects(hitBox)){
                    yspeed = -13;
                }
            }
            hitBox.y --;
            
        } 
        
        yspeed += 0.6;
        
        //Horizontal Collision 
        hitBox.x += xspeed;
    for(int i=0;i<panel.walls.size();i++){
        if (panel.walls.get(i).id != 0){
        if (hitBox.intersects(panel.walls.get(i).hitBox)) {
        hitBox.x -= xspeed;
        while (!panel.walls.get(i).hitBox.intersects(hitBox)) {
        hitBox.x += Math.signum(xspeed); // if xspeed> 0 math.signum = 1 , if xspeed <0 math.signum = -1
        } 
        hitBox.x -= Math.signum(xspeed);
        panel.cameraX += x - hitBox.x;
        xspeed = 0;
        hitBox.x = x;
            }
        }
        else {
             if (hitBox.intersects(panel.walls.get(i).hitBox)) {
                 //death sound 
                     clip.start();
                     panel.sleep(1000);
                 parent = (JFrame) panel.getTopLevelAncestor();             
                 parent.dispose();
                 //stop music
                panel.ST.stop();
                    
        GO = new game_over();
        GO.setLocationRelativeTo(null);
        GO.setResizable(false);
        GO.setVisible(true);
        GO.setTitle("GAME OVER");
                GO.show();
             }
        }
    }
        
        //Vertical Collision
        hitBox.y += yspeed;
        for(int i=0;i<panel.walls.size();i++){
            if (panel.walls.get(i).id != 0){
            if (hitBox.intersects(panel.walls.get(i).hitBox)) {
                hitBox.y -= yspeed;
                while (!panel.walls.get(i).hitBox.intersects(hitBox)) {
                    hitBox.y += Math.signum(yspeed); // if yspeed> 0 math.signum = 1 , if yspeed <0 math.signum = -1
                }
                hitBox.y -= Math.signum(yspeed);
                yspeed = 0;
                y = hitBox.y;
            }
            } 
            else {
                 if (hitBox.intersects(panel.walls.get(i).hitBox)) {
                    //death sound 
                     clip.start();
                    panel.sleep(1000);
                    parent = (JFrame) panel.getTopLevelAncestor();
                    parent.dispose();
                    //stop music
                    panel.ST.stop();
                        
        GO = new game_over();
        GO.setLocationRelativeTo(null);
        GO.setResizable(false);
        GO.setVisible(true);
        GO.setTitle("GAME OVER");
                    GO.show();
                 }
            }
        }
        
        
        
        panel.cameraX -= xspeed;
        y += yspeed;
        
        /* so that the hitbox follows the player */
        hitBox.x = x;
        hitBox.y = y; 
        
        //Death code
        if (y > 600) {
            //death sound 
            clip.start();
            panel.sleep(1000);
            parent = (JFrame) panel.getTopLevelAncestor();
            parent.dispose();
            //stop music
            panel.ST.stop();
                
        GO = new game_over();
        GO.setLocationRelativeTo(null);
        GO.setResizable(false);
        GO.setVisible(true);
        GO.setTitle("GAME OVER");
            GO.show();
            
            /* GO = new game_over();
            GO.setLocationRelativeTo(null); //to start in the middle instead of the left corner
            GO.setResizable(false);
            GO.setVisible(true);
            GO.setTitle("Enter your Name");        
            GO.setDefaultCloseOperation(EXIT_ON_CLOSE);*/
           
        }
        
        
    }
    
    public void draw(Graphics2D gtd) throws SQLException, IOException{
        //gtd.setColor(Color.BLACK);
        
        img = ImageIO.read( new File("src\\\\assets\\\\pngcharacter.png" ));
        gtd.drawImage(img,x,y, null);
        
        try {
            pixelmix = Font.createFont(Font.TRUETYPE_FONT, new File("src\\\\fonts\\\\pixelmix_bold.ttf")).deriveFont(25f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,new File("src\\\\fonts\\\\pixelmix_bold.ttf")));
        } catch(IOException | FontFormatException e){
            
        }
        //Font f= new Font("Arial",Font.BOLD,40);
        gtd.setFont(pixelmix);
        try {
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/sample","app","app");
            Statement stmt = con.createStatement();
           ResultSet rs = stmt.executeQuery("SELECT * FROM APP.CURRENT_PLAYER");
            if (rs.next()) {
            name = rs.getString("username");
            } 
            else {
                System.out.print("db empty");
            }
            } catch (SQLException ex) {
            Logger.getLogger(EnterName.class.getName()).log(Level.SEVERE, null, ex);
            } 
        gtd.drawString(name,x-30,y);
        gtd.drawString(" Score "+Integer.toString(panel.score), 50, 100);
        score = panel.score;
         
        
         try {
               Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/sample","app","app");
            Statement stmt = con.createStatement();
            stmt.executeUpdate("UPDATE APP.SCOREBOARD SET score="+score+" WHERE USERNAME='"+name+"'");
            } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(EnterName.class.getName()).log(Level.SEVERE, null, ex);
            } 
         
    }
}
