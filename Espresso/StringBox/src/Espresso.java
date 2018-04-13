import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;

public class Espresso extends JFrame
{
	private int width = 600;
	private int height = 400;

	StringBox box;

	public Espresso()
	{
		super("Espresso");
		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		add(new StringBox(600, 400));
		
		revalidate();
		setVisible(true);
	}

	public void switchPanels(String text)
	{
		remove(box);
		revalidate();
		repaint();
	}

	public static void main(String[] args)
	{
		new Espresso();
	}
}
