
//The version implement user interface with Swing
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class Snake extends JFrame implements ActionListener, KeyListener,Runnable
{
    //Directions
    public final static int MOVE_UP=1;
    public final static int MOVE_DOWN=2;
    public final static int MOVE_LEFT=3;
    public final static int MOVE_RIGHT=4;
    public final static int STEP_SIZE = 10;
    
    private Toolkit toolkit;
    private JMenuBar menuBar;
    private JMenu gameMenu, diffMenu, scoreMenu, About;
    private JMenuItem beginItem,exitItem, scoreItem, authorItem, easyItem, hardItem;
    private int snakeDirection, foodX, foodY, bombX, bombY,time;
    
    private int snakeBodyX[]=new int[50];// the snake
    private int snakeBodyY[]=new int[50];
    private Thread myThread = null;
    private int curScore = 0;
    private int food = 0;
    private int maxScore=0;
    private int myLength = 6; //snake length
    public Snake(){
        time = 500;
        setVisible(true);  //frame can be set visible
        menuBar = new JMenuBar();
        toolkit=getToolkit();
        //initialize all menus and items
        gameMenu = new JMenu("Game Menu");
        beginItem = new JMenuItem("Start Game");
        exitItem = new JMenuItem("Exit Game");
        
        diffMenu = new JMenu("Difficulty");
        easyItem = new JMenuItem("Easy");
        hardItem = new JMenuItem("Hard");
        
        scoreMenu = new JMenu("Score");
        scoreItem = new JMenuItem("Highest score");
        
        About = new JMenu("About");
        authorItem = new JMenuItem("Author");
        
        //add item to menu
        gameMenu.add(beginItem);
        gameMenu.add(exitItem);
        
        diffMenu.add(easyItem);
        diffMenu.add(hardItem);
        
        scoreMenu.add(scoreItem);
        About.add(authorItem);
        
        //add menu to menuItem
        menuBar.add(gameMenu);
        menuBar.add(diffMenu);
        menuBar.add(scoreMenu);
        menuBar.add(About);
        
        //add interactive user interface, click
        beginItem.addActionListener(this); //if clicked, causes thread created and started, and run() to be runed
        exitItem.addActionListener(this);
        scoreItem.addActionListener(this);
        authorItem.addActionListener(this);
        
        //arrow keys
        addKeyListener(this);
        //begin and exit the shortcut probably on menu bar
        KeyStroke keyOpen = KeyStroke.getKeyStroke('O',InputEvent.CTRL_DOWN_MASK);
        beginItem.setAccelerator(keyOpen);
        KeyStroke keyExit = KeyStroke.getKeyStroke('X',InputEvent.CTRL_DOWN_MASK);
        exitItem.setAccelerator(keyExit);
        
        setJMenuBar(menuBar);
        setResizable(false);
        setTitle("GreedySnake");
        setBounds(300,200,400,400); //bounds of the frame
        validate();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.repaint();
    }
    
    
    public static void main(String args[])
    {
        new Snake();
    }
    
    
    @Override
    public void run() {
        //initial snake position
        for(int i=0; i<=myLength-1;i++)
        {
            snakeBodyX[i]=90-i*10; // every step is 10 wide
            snakeBodyY[i]=60;
        }
        //starting position
        snakeDirection=4;
        addFoodAndEnlengthSnake();
        while(myThread!=null)  // the main game loop that keeps running
        {
            step();
            try
            {
                myThread.sleep(time);
            }
            catch(Exception ee)
            {
                System.out.println(snakeDirection+"");
            }
        }
    }
    
    public void step(){
        //movefirst, checkdead, then checkfood
        if(myThread != null){
            if(snakeBodyX[0] == foodX && snakeBodyY[0] == foodY){ //check weather eat anything first before move it
                toolkit.beep();
                food++;
                addFoodAndEnlengthSnake();
            }
            moveToNextStep(); //only moveToNextStep() moves along coordinates
            if(!isDead()) 	this.repaint(); //redudant code removed here
        }
    }
    
    public void addFoodAndEnlengthSnake(){
        //replace bomb
        bombX = (int)Math.floor(Math.random()*39)*10;
        bombY = (int)Math.floor(Math.random()*39)*10;
        
        foodX=(int)Math.floor(Math.random()*39)*10; //Math.random() creates number bettween (0,1)
        foodY=(int)Math.floor(Math.random()*29)*10+50;
        if(food!=0){
            time=time-5;
            myLength += 1;
            curScore += 10;
            if(maxScore < curScore)	maxScore = curScore;
        }
    }
    
    public boolean isDead(){ //range[0,390]
        //bump into wall
        if(snakeBodyX[0] < 0 || snakeBodyX[0] > 390 || snakeBodyY[0] < 50 || snakeBodyY[0] >390){
            myThread = null;
            JOptionPane.showMessageDialog(this, "Game Over: You hit the wall!");
            return true;
        }
        //step on bomb
        if(snakeBodyX[0] == bombX && snakeBodyY[0] == bombY){
            myThread = null;
            JOptionPane.showMessageDialog(this, "Game Over: You hit the Bomb!");
            return true;
        }
        
        //bump into itself
        for(int i = 1; i < myLength; i++){
            if(snakeBodyX[0] == snakeBodyX[i] && snakeBodyY[0] == snakeBodyY[i])	{
                myThread = null;
                JOptionPane.showMessageDialog(this, "Game Over: You hit yourself!");
                return true;
            }
        }
        
        
        return false;
    }
    
    public void moveToNextStep(){
        //shift each location
        for(int i = myLength -1; i > 0; i--){
            snakeBodyX[i] = snakeBodyX[i-1];
            snakeBodyY[i] = snakeBodyY[i-1];
        }
        //move the head
        if(snakeDirection == Snake.MOVE_DOWN)	        snakeBodyY[0] += Snake.STEP_SIZE;
        else if(snakeDirection == Snake.MOVE_UP)		snakeBodyY[0] -= Snake.STEP_SIZE;
        else if(snakeDirection == Snake.MOVE_LEFT)		snakeBodyX[0] -= Snake.STEP_SIZE;
        else if(snakeDirection == Snake.MOVE_RIGHT)		snakeBodyX[0] += Snake.STEP_SIZE;
    }
    
    
    @Override //keys be pressed left, right, up and down
    public void keyPressed(KeyEvent e) { //method in keyListener
        if(myThread!=null){
            if(e.getKeyCode()==KeyEvent.VK_UP){ //VK_UP: static instance variable in class KeyEvent
                if(snakeDirection!=Snake.MOVE_DOWN){
                    snakeDirection=Snake.MOVE_UP;
                    //add step and it will be much more sensitive
                    step();// enlength snake, collison
                    
                }
            }
            else if(e.getKeyCode()==KeyEvent.VK_DOWN){
                if(snakeDirection != Snake.MOVE_UP){
                    snakeDirection = Snake.MOVE_DOWN;
                    step();
                }
            }
            else if(e.getKeyCode() == KeyEvent.VK_LEFT){
                if(snakeDirection != Snake.MOVE_RIGHT){
                    snakeDirection = Snake.MOVE_LEFT;
                    step();
                }
            }
            else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                if(snakeDirection!= Snake.MOVE_LEFT){
                    snakeDirection = Snake.MOVE_RIGHT;
                    step();
                }
            }
        }
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) { //method in actionlistener: implements interactivity: Click
        Object obj = e.getSource();
        if(obj == beginItem){
            //start game, clear necessary parameters
            myLength = 6;
            curScore = 0;
            food = 0;
            //restart a new game weather its already launched
            myThread = null;
            myThread = new Thread(this); //Runnable(this) 
            myThread.start();
        }
        else if(obj == exitItem){
            System.exit(0);
        }
        else if(obj == easyItem)	 time = 500;
        else if(obj == hardItem)	 time = 200;
        else if(obj == authorItem){
            JOptionPane.showMessageDialog(this, "written by Yongjiao Yu");
        }
        else if(obj == scoreItem){
            JOptionPane.showMessageDialog(this, "Highest Score:"+ maxScore +"");
        }
    }
    
    
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.white); 
        g.fillRect(0,50,400,400); //the size of window is 400 X 400 in coordinates 
        //draw the food
        g.setColor(Color.green); 
        g.fillRect(foodX,foodY,10,10);
        //draw the bomb
        g.setColor(Color.red); 
        g.fillRect(bombX,bombY,10,10);
        //draw the snake
        g.setColor(Color.pink); 
        for(int i=0; i <= myLength-1; i++) 
            g.fillRect(snakeBodyX[i],snakeBodyY[i],10,10);  //each snake block is 10x10
        //display the score
        g.setColor(Color.black); 
        g.drawString("Score: "+ curScore,6,60); 
        g.drawString("Food eatten: " + food,6,72); 
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    // not needed 		
    @Override
    public void keyReleased(KeyEvent e) {}
}