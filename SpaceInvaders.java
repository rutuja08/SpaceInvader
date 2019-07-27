

//Space Invader 
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.applet.*;
import java.sql.*;
import java.applet.AudioClip;

/*<applet code="SpaceInvaders.class" archive="/root/project/jar_files/postgresql-9.4.1212.jre6.jar" width="1500" height="600" align=MIDDLE vspace=100 hspace=100>
</applet>*/
public class SpaceInvaders extends Applet implements MouseListener, MouseMotionListener, KeyListener
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2652298165440569829L;
	AudioClip sndFondo, sndMisil1, sndMisil2, sndExplosion;
    Image dbImage;
    Font myFont,myFont1;
    Graphics dbg;
    static  int score=0;
    Thread th;
    int speed = 15;
    Image playerI,imgExplosion;
    Image enemyI[];
    float enemy[][];
    float player[];
    int lives;
    boolean left, right;
    int gameState;
    int enemyD;
    int time;
Connection con=null;
ResultSet rs=null;
PreparedStatement ps;
String query;
Statement stmt;

    String name=JOptionPane.showInputDialog("Enter Yor Name", "player");
   
    
    
    boolean wall[];
    int bullet[][];
    int no_of_bar,no_of_aliens;

    public void paint (Graphics g)
    {
		if (gameState == 0)
		{
			g.setColor (Color.white);
			g.setFont(myFont);
			g.drawString ("Health: " + lives, 950, 150);
			g.drawString("Score: "+score,950,100);
			g.drawString("Player: "+name,950,50);
			g.setFont(myFont1);
			g.drawString("HELP",950,200);
			g.drawString("LEFT Arrow Key ",950,230);
			g.drawString("RIGHT Arrow Key",950,270);
			g.drawString("Space Bar Key ",950,310);
			g.drawString(": move left ",1300,230);
                        g.drawString(": move right ",1300,270);
                        g.drawString(": shoot ",1300,310);
			g.fillRect(900,5,5,560);
			g.fillRect(5,5,900,5);
			g.fillRect(5,5,5,560);
			g.fillRect(5,560,900,5);
			updateEnemys (g);
			updateWalls (g);
			updatePlayer (g);
			updateBullets (g);
			try 
			{
				Thread.sleep(20);
			}
			catch (Exception e) {}
		}
		else
		{  
			
			g.setColor(Color.white);
			g.drawString("Oops you lose....",100,100);
	
			g.drawString(name+" your score is: "+score,100,150);
			sndFondo.stop();
	
			sndExplosion.play();
		}
    }

    public void init ()
    { 
	 String name=null;
	myFont=new Font("TimesRoman", Font.BOLD, 40);
	myFont1=new Font("TimesRoman", Font.BOLD, 28);
	addMouseListener(this);
	addMouseMotionListener(this);
	addKeyListener(this);

		enemyI = new Image[3];
		enemy = new float[90][4];
		player = new float[2];
		wall = new boolean[100];
		bullet = new int[100][3];

		lives = 3;
		gameState = 0;
		enemyD = 1;
		time = 0;
		no_of_bar=5;
	       imgExplosion=this.getImage(this.getCodeBase(),"DATA/Explosion.png");
		sndFondo =this.getAudioClip(this.getCodeBase(),"DATA/Fondo.wav");

       		 sndMisil2 =this.getAudioClip(this.getCodeBase(),"DATA/Misil1.wav");

        	sndMisil1 =this.getAudioClip(this.getCodeBase(),"DATA/laser.wav");
		


        	sndExplosion=this.getAudioClip(this.getCodeBase(),"DATA/Explosion.wav");
		playerI = getImage (getCodeBase (), "DATA/PLAYER.PNG");
		enemyI[0] = getImage (getCodeBase (), "DATA/ENEMY1.PNG");
		enemyI[1] = getImage (getCodeBase (), "DATA/ENEMY2.PNG");
		enemyI[2] = getImage (getCodeBase (), "DATA/ENEMY3.PNG");

		player[0] = 230;
		player[1] = 400;

		for (int i = 0 ; i < 6 ; i++)
		{
			for (int j = 0 ; j < 20 ; j++)
			{
			enemy [i * 10 + j] [0] = 50 + j *40;
			enemy [i * 10 + j] [1] = 20 + i * 30;
			if (i == 0)
				enemy [i * 10 + j] [2] = 2;
			else if (i == 1 || i == 2)
				enemy [i * 10 + j] [2] = 1;
			else
				enemy [i * 10 + j] [2] = 0;
			}
		}

		for (int i = 0 ; i < 100 ; i++)
			wall [i] = true;

		for (int i = 0 ; i < 100 ; i++)
			bullet [i] [0] = -100;

		try{

        Class.forName("org.postgresql.Driver");
        con=DriverManager.getConnection("jdbc:postgresql://localhost/typroject","modern","spaceinvader");
       	if(con==null)
   	System.out.println("connection failed");    	
	else{	System.out.println("connection successfull");    }
		}
	catch(Exception e)
	{
		System.out.println(e);
	}
	
		
    }

    public void updateEnemys (Graphics g)
    {
no_of_bar=5;

		boolean won = true;
		for (int i = 0 ; i < 60 ; i++)//increases the no of blue aliens
		{
			if (enemy[i][0] >= 0)
			{
				won = false;
				g.drawImage (enemyI[(int)enemy[i][2]], (int)enemy[i][0], (int)enemy[i][1], this);
			
				if (enemy[i][0]+enemyI[(int)enemy[i][2]].getWidth(this) > 900|| enemy[i][0] < 5)
				{
					enemyD *= -1;
					for (int j=0;j<60;j++)
					{
						enemy[j][0] += enemyD * 5;
						enemy[j][1] += 10;
					}
				}
			enemy[i][0] += enemyD * 3 * 3.5 / speed;
				
				if (Math.random() < (1.0 / speed) * 0.02)
					shoot((int)enemy[i][0] + enemyI[(int)enemy[i][2]].getWidth(this) / 8, (int)enemy[i][1] + 16,1);
			}
		}
		
		if (won)
		{
			try 
			{
				Thread.sleep(1000);
			} catch (Exception e) {}
			
			init();
			if (speed >= 5)
				speed -= 5;
		}
    }

    public void updateWalls (Graphics g)

    {
		g.setColor (Color.blue);
		for (int k = 0 ; k < no_of_bar; k++)//k no of barriers
			for (int i = 0 ; i < 4; i++)//i verticle pos of small rects
				for (int j = 0 ; j < 5 ; j++)//j for horizontal position of small rects
					if (wall [(i * 5 + j) + (k * 20)])  
						g.fillRect(j * 16 + 50 + (k * 150), i * 12 + 330, 16, 20);//vertical position
    }

    public void updatePlayer (Graphics g)
    {
		time --;
		g.drawImage(playerI, (int)player[0], (int)player[1], this);
		if (left && player [0] > 5)
			player[0] -= 5;
		if (right && player [0] + 32 < 900)//player horizontal movement
			player[0] += 5;

		if (lives < 0)
		{
			g.setColor(Color.red);
			gameState = 1;
			/*try{
				query="select * from spaceinvaders";
				//ps=con.prepareStatement(query);
				System.out.println("Here");
				stmt=con.createStatement();
				rs=stmt.executeQuery(query);
				while(rs.next())
					{
					String nm=rs.getString("name");
					int scr=rs.getInt("score");
					System.out.println(name+"\t"+score);
					}
				
			//ps=con.prepareStatement("insert into spaceinvaders values(?,?)");
		//ps.setString(1,name);
		//ps.setInt(2,score);
		//rs=ps.executeQuery();
				}
			catch(Exception e)
			{
				System.out.println(e);
			}   */         
			sndExplosion.play();
		}
    }

    public void updateBullets (Graphics g)
    {
		for (int i = 0 ; i < 100 ; i++)
		{
			if (bullet[i][0] >= 0)
			{
				bullet[i][1] += bullet[i][2] * 9 + 20.0 / speed;
				g.setColor(Color.white);

				g.drawLine(bullet [i] [0], bullet [i] [1] - 5, bullet [i] [0], bullet [i] [1] + 5);
				g.drawLine(bullet [i] [0] + 1, bullet [i] [1] - 5, bullet [i] [0] + 1, bullet [i] [1] + 5);
				if (bullet[i][1] < -5 || bullet[i][1] > 455)
					bullet[i][0] = -100;

				for (int k = 0 ; k < no_of_bar ; k++)
					for (int l = 0 ; l < 4 ; l++)
						for (int j = 0 ; j < 5 ; j++)
							if (wall [(l * 5 + j) + (k * 20)] &&
								bullet [i] [0] >= j * 16 + 50 + (k * 150) &&
								bullet [i] [0] <= j * 16 + 50 + (k * 150) + 16 &&
								bullet [i] [1] >= l * 12 + 330 &&
								bullet [i] [1] <= l * 12 + 330 + 12)
							{
								wall[(l * 5 + j) + (k * 20)] = false;
								bullet[i][0] = -100;
								break;
							}

				for (int z = 0 ; z < 60 ; z++)
					if (bullet[i][2] == -1 && enemy[z][0] >= 0)
						if (bullet[i][0] >= enemy[z][0] && bullet[i][0] <= enemy[z][0] + enemyI[(int)enemy[z][2]].getWidth(this) &&
							bullet[i][1] >= enemy[z][1] && bullet[i][1] <= enemy[z][1] + enemyI[(int)enemy[z][2]].getHeight(this))
						{
							enemy [z] [0] = -1000;
							score=score+5;	
							bullet [i] [0] = -100;
							break;
						}

				if (bullet[i][0] >= player[0] && bullet[i][0] <= player[0] + playerI.getWidth(this))
					if (bullet[i][1] >= player[1] && bullet[i][1] <= player[1] + playerI.getHeight(this))
					{
						
						lives--;
						

					
						bullet[i][0] = -100;
					}
			}
		}
    }

    public void shoot (int x, int y, int dir)
    {
		for (int i = 0 ; i < 100 ; i++)
		{
			if (dir == -1)
				time = 15;
			if (bullet[i][0] < 0)
			{
				bullet[i][0] = x;
				bullet[i][1] = y;
				bullet[i][2] = dir;
				sndMisil1.play();
				break;
			}
		}
    }

    public void start ()
    {
		th = new Thread();
		th.start();
		sndFondo.loop();
    }

    public void run ()
    {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		while (true)
		{
			repaint ();
			try
			{
				Thread.sleep (20);
			}
			catch (InterruptedException ex) { }
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		}
    }

    public void update (Graphics g)
    {
		if (dbImage == null)
		{
			dbImage = createImage (this.getSize ().width, this.getSize ().height);
			dbg = dbImage.getGraphics ();
		}
		try
		{	
			Thread.sleep (speed);
		}
		catch (Exception e) { }
		
		dbg.setColor (Color.black);
		dbg.fillRect (0, 0, this.getSize().width, this.getSize().height);
		dbg.setColor (Color.black);
		paint (dbg);
		g.drawImage (dbImage, 0, 0, this);
		repaint ();
    }

	@Override
	public void mouseClicked(MouseEvent arg0) {
		shoot ((int)player[0] + 16, (int)player[1], -1);
		sndMisil2.play();
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if (player [0] > 5 &&  (player [0] + 32) < 900)
			player[0] =arg0.getX();
		else
			player[0]=400;
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		int key=arg0.getKeyCode();
		switch(key)
		{
		case KeyEvent.VK_LEFT :
				if(player[0]>5)
					player[0]-=5;
				break;
		case KeyEvent.VK_RIGHT :
				if(player[0]+32<900)
					player[0]+=5;
				break;
		case KeyEvent.VK_SPACE :
			shoot ((int)player[0] + 16, (int)player[1], -1);
			break;
				
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
