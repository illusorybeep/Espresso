import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

public class StringBox extends JPanel implements MouseListener, KeyListener
{		
	// The dimensions of the panel
	private int width;
	private int height;

	// Keyboard characters final
	private static final char[] CHARACTERS = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
											  'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
											  '1','2','3','4','5','6','7','8','9','0','`','~','!','@','#','$','%','^','&','*','(',')','-','_','=','+',
											  '[','{',']','}','\\','|',';',':','\'','"',',','<','.','>','/','?',' '};
	
	// Colors
	private Color backgroundColor = new Color(255, 243, 230);
	private Color boxColorNotClicked = new Color(255, 230, 204);
	private Color boxColorClicked = new Color(255, 218, 179);
	
	// Some fields for use
	private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);
	
	// Drawing the boxTerminalTerminal
	private int boxLeft; 
	private int boxRight; 
	private int boxTop;
	private int boxBottom;
	private int boxWidth;
	
	// The search box
	private BufferedImage search;
	private int searchWidth;
	private int searchHeight;
	private int searchX;
	private int searchY;
	
	// Where the text will be drawn
	private int textX; 
	private int textY;
	
	private String text = ""; // The text in the box that will be processed
	private int displayStart, displayEnd; // The indexes of text that will be displayed
	
	// Where the mouse is in the box
	private int mousePos;
	private int mouseIndex; 
	
	private boolean clicked; // Whether or not the box is clicked
	
	
	public StringBox(int width, int height)
	{
		super();
		setPreferredSize(new Dimension(width, height));
		addMouseListener(this);
		addKeyListener(this);

		this.width = width;
		this.height = height;
		displayStart = 0; displayEnd = 0;
		
		try { search = ImageIO.read(new File("glass.png")); }
		catch(IOException ex) { System.out.println("glass.png not found"); }
		
		initializeVariables();
	}
	
	private void initializeVariables()
	{
		boxLeft = width / 8;
		boxRight = width / 8 * 6; 
		boxTop = height / 10 * 3;
		boxBottom = boxTop + (height / 10);
		
		boxWidth = (boxRight) - (boxLeft + 10); 
		
		textX = boxLeft + 10;
		textY = (boxTop + boxBottom) / 2 + 5; 
		
		searchWidth = width / 20;
		searchHeight = width / 20;
		searchX = boxRight + 8;
		searchY = boxTop + 6;
	}
	
	public void addNotify() 
	{
        super.addNotify();
        requestFocus();
    }
	
	public void paint(Graphics gr)
	{
		// Clear the screen
		gr.setColor(backgroundColor);
		gr.fillRect(0, 0, width, height);
		
		// Setting the box's filling
		if(!clicked) gr.setColor(boxColorNotClicked);
		else gr.setColor(boxColorClicked);
		
		// The box filling
		int[] xPoints = new int[] { boxLeft, boxRight, boxRight, boxLeft };
		int[] yPoints = new int[] { boxTop, boxTop, boxBottom, boxBottom };
		gr.fillPolygon(xPoints, yPoints, 4);
		
		// The outline of the box
		gr.setColor(Color.BLACK);
		gr.drawLine(boxLeft, boxTop, boxRight, boxTop);
		gr.drawLine(boxRight, boxTop, boxRight, boxBottom);
		gr.drawLine(boxRight, boxBottom, boxLeft, boxBottom);
		gr.drawLine(boxLeft, boxBottom, boxLeft, boxTop);
		
		// The search bit
		gr.drawImage(search, searchX, searchY, searchWidth, searchHeight, this);
		
		gr.setFont(font);
		gr.drawString(text.substring(displayStart, displayEnd), textX, textY);
		
		if(clicked) gr.drawLine(mousePos, boxTop + 5, mousePos, boxBottom - 5);
	}
	
	private void getMousePosFromClick(int xPos)
	{
		if(displayEnd == 0 || (xPos > boxLeft && xPos < textX)) 
		{
			mousePos = textX;
			mouseIndex = 0;
		}
		else
		{
			int length = textX; 
			List<Integer> characterLengths = new ArrayList<>();
			FontMetrics metrics = getGraphics().getFontMetrics(font);
			
			for(int k = displayStart; k < displayEnd; k++)
			{
				int charLength = metrics.stringWidth(text.substring(k, k+1));
				characterLengths.add(charLength + length);
				length += charLength;
			}
			
			xPos += 5;
			for(int k = 0; k < characterLengths.size(); k++)
			{
				if(xPos > characterLengths.get(k))
				{
					mousePos = characterLengths.get(k);
					mouseIndex = k + 1;
				}
				else break; 
			}
		}
	}
	
	private void setMousePos()
	{
		mousePos = textX;
		FontMetrics metrics = getGraphics().getFontMetrics(font);
		
		for(int k = displayStart; k < displayStart + mouseIndex; k++)
		{
			mousePos += metrics.stringWidth(text.substring(k, k+1));
		}
	}
	
	public boolean isValidCharacter(char c)
	{
		for(char ch : CHARACTERS)
		{
			if(ch == c) return true;
		}
		
		return false; 
	}
	
	public boolean textFitsOnScreen(char c)
	{
		int length = 0; 
		FontMetrics metrics = getGraphics().getFontMetrics(font);
		
		length += metrics.stringWidth(c + "");
		
		for(int k = displayStart; k < displayEnd; k++)
		{
			length += metrics.stringWidth(text.substring(k, k+1));
		}
		
		return (length <= boxWidth);
	}
	
	public boolean textFitsOnScreen()
	{
		int length = 0; 
		FontMetrics metrics = getGraphics().getFontMetrics(font);
		
		for(int k = displayStart; k < displayEnd; k++)
		{
			length += metrics.stringWidth(text.substring(k, k+1));
		}
		
		return (length <= boxWidth);
	}
	
	public void mouseClicked(MouseEvent event) 
	{
		if(!clicked)
		{
			if(event.getX() > boxLeft && event.getX() < boxRight &&
			   event.getY() > boxTop && event.getY() < boxBottom)
				{
					clicked = true;
					
					// Finding what character the mouse clicked on / next to
					getMousePosFromClick(event.getX());
				}
		}
		else
		{
			if(event.getX() > boxLeft && event.getX() < boxRight &&
			   event.getY() > boxTop && event.getY() < boxBottom)
				{
					getMousePosFromClick(event.getX());
				}
			else
				clicked = false; 
		}
		
		if(event.getX() > searchX && event.getX() < searchX + searchWidth &&
		   event.getY() > searchY && event.getY() < searchY + searchHeight)
				{
					enter();
				}
		
		repaint();
	}
	public void keyPressed(KeyEvent event) 
	{
		if(clicked)
		{
			int keyCode = event.getKeyCode();
			switch(keyCode)
			{
				case KeyEvent.VK_LEFT:
					leftArrow();
					break;
				
				case KeyEvent.VK_RIGHT:
					rightArrow();
					break;
					
				case KeyEvent.VK_BACK_SPACE:
					backspace();
					break;
					
				case KeyEvent.VK_ENTER:
					enter();
					break;
			}
			
			if(isValidCharacter(event.getKeyChar()))
			{
				if(textFitsOnScreen(event.getKeyChar()))
				{
					if(mouseIndex == displayEnd)
					{
						text += event.getKeyChar();
						mouseIndex++;
						displayEnd++;
					}
					else
					{
						text = text.substring(0, mouseIndex) + event.getKeyChar() + text.substring(mouseIndex, text.length());
						mouseIndex++;
						displayEnd++;
					}
				}
				else
				{
					if(mouseIndex == displayEnd - displayStart)
					{
						text = text.substring(0, mouseIndex + displayStart) + event.getKeyChar() + text.substring(mouseIndex + displayStart, text.length());
						displayStart++;
						displayEnd++;
					}
					else
					{
						text = text.substring(0, mouseIndex + displayStart) + event.getKeyChar() + text.substring(mouseIndex + displayStart, text.length());
						mouseIndex++;
					}
				}
				
				setMousePos();                                                                                                                                                                                                                                                                                                                                                                            
			}
			
			repaint();
		}
	}
	public void enter()
	{
		System.out.println("Your search term is: " + text);
		System.exit(0);
	}
	public void leftArrow()
	{
		if(mouseIndex > 0) mouseIndex--;
		
		if(displayStart > 0 && mouseIndex == 0) 
		{
			displayStart--;
			if(!textFitsOnScreen()) displayEnd--;
		}
		
		setMousePos();
	}
	public void rightArrow()
	{	
		if(mouseIndex == displayEnd - displayStart && displayEnd < text.length()) 
		{
			displayStart++;
			displayEnd++;
		}
		
		if(mouseIndex < displayEnd - displayStart)
		{
			mouseIndex++;
		}
		
		setMousePos();
	}
	public void backspace()
	{
		if(mouseIndex == 0 && displayStart != 0)
		{
			System.out.println(1);
			text = text.substring(0, displayStart + mouseIndex-1) + text.substring(displayStart + mouseIndex, text.length());
			displayStart--;
			displayEnd--;
		}
		else if(mouseIndex != 0 && displayEnd < text.length())
		{
			System.out.println(2);
			text = text.substring(0, displayStart + mouseIndex-1) + text.substring(displayStart + mouseIndex, text.length());
			mouseIndex--;
		}
		else if(mouseIndex != 0)
		{
			System.out.println(3);
			text = text.substring(0, displayStart + mouseIndex-1) + text.substring(displayStart + mouseIndex, text.length());
			displayEnd--;
			mouseIndex--;
		}
		
		setMousePos();
	}
	public void mouseEntered(MouseEvent event) { }
	public void mouseExited(MouseEvent event) { }
	public void mousePressed(MouseEvent event) { }
	public void mouseReleased(MouseEvent event) { }
	public void keyTyped(KeyEvent event)  { }
	public void keyReleased(KeyEvent event) { }
}
