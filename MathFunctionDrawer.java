
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;


/**
 * This is Math Function drawing tool that asks 
 * the user to enter a function and draws it  
 * 
 */
public class MathFunctionDrawer extends Frame {

    private Color curColor = Color.RED;//Color
    //Menu
    private MenuBar menuBar;
    private Menu testMenu;
    private MenuItem colorItem, EquationItem, resetItem;
    //Insets 
    private static Insets ins;
    //x and y values
    private int x, y;
	Double c;
	//user input
    private String input;
    private Double userY;
	private double rangeReal, originReal, xRange, rangeImaginary, originImaginary, originx, xValue, originy, aspectRatio, delta, yImaginary;
    private static int height, width;			
	private int Wwindow, Hwindow;
	private int x_init, y_init, x_cur, y_cur;
	private boolean rubberbanding;
	private boolean changeCordinate = true;
    private ExpressionParser expressionParser;

    //Constructor
    MathFunctionDrawer() {
    	//Call the menu
        createMenu();
      
        //Enables the closing of the window.
        addWindowListener(new MyFinishWindow());
        addMouseListener(
				new MouseAdapter()
				{
					public void mousePressed(MouseEvent evt)
					{
						// rubberbanding starts with the first mouse move
						rubberbanding = false;
						
						// remember the coordinates
						x_init = evt.getX();
						y_init = evt.getY();
					}
					public void mouseReleased(MouseEvent evt)
					{
						
						// done with rubberbanding
						rubberbanding = false;
						
						// coordinates at release point
						x_cur = evt.getX();
						y_cur = evt.getY();
						//Width
						int w = (int) Math.abs(x_cur - x_init);
						//Height
						int h = (int) Math.abs(y_cur - y_init);
						//Minimum
						int xmin = Math.min(x_cur, x_init) - ins.left;
						int ymin = Math.min(y_cur, y_init) - ins.top;
						//Maximum
						int xmax = Math.max(x_cur, x_init) - ins.left;
						int ymax = Math.max(y_cur, y_init) - ins.top;

						//Conversion
						double xConv = screenXtological(xmin);
						double xConv1 = screenXtological(xmax);
						double yConv = screenYtoLogical(ymin);
						double yConv1 = screenYtoLogical(ymax);
						
						//Calculate the range
						double x = (xConv1 - xConv);	//width
						System.out.println(x);
						double y = (yConv - yConv1);	//height
						System.out.println(y);
						
						//Calculate the origin
						double xOrigin = (xConv + xConv1)/2;
						double yOrigin = (yConv + yConv1)/2;
						double range;
						if((x/y)>= aspectRatio)
						{
							range = x;
							
						}
						else
						{
							range = aspectRatio * y;
							
						}
						
						setupCoordinateSystem(range, xOrigin, yOrigin);
						//drawSimpleCoordinateSystem(500, 400, g2d);
						repaint();
					}
				}
				);
        addMouseMotionListener(
				new MouseAdapter()
				{
					public void mouseDragged(MouseEvent evt)
					{
						Graphics2D g2d = (Graphics2D)getGraphics();

						// rubber banding with XOR drawing:
						// drawing into the canvas once will display a rectangle
						// drawing over the same location will restore the previous state
						g2d.setXORMode(Color.black);
						g2d.setColor(Color.white);
						// have we already drawn a rectangle? if yes, draw over it to make it disappear
						if(rubberbanding == true)
						{
							drawRect(g2d);
						}
						// update
						x_cur = evt.getX();
						y_cur = evt.getY();
						drawRect(g2d);
						// after we drew at least once
						rubberbanding = true;
					}
				}
				);
    }

    private void createMenu()
	{
		// build a menu
		menuBar = new MenuBar();
		// main menu bar
		setMenuBar(menuBar);
		
		// a menu
		testMenu = new Menu("Test");
        // The Function
		menuBar.add(testMenu);
		EquationItem = new MenuItem("Equation");
		testMenu.add(EquationItem);
		
		EquationItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						//Ask the user to prompt the function
						input = JOptionPane.showInputDialog("Equation");
						//parse the function
						expressionParser = new ExpressionParser();
						repaint();
					}
				}
				);	
		
		colorItem = new MenuItem("Color");
		testMenu.add(colorItem);
		// the color dialog
		colorItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						Color c = JColorChooser.showDialog(null, "Select drawing color", curColor);

						curColor = c;
						repaint();
					}
				}
				);	
		
		testMenu = new Menu("Reset");
	      
		menuBar.add(testMenu);
		resetItem = new MenuItem("ResetItem");
		testMenu.add(resetItem);
		// reset the window to its origin zoom level
		resetItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						
						setupCoordinateSystem(30, 0, 0);
						repaint();
					}
				}
				);
				
	}
    //Paint Method
    public void paint(Graphics g) {
      

        Graphics2D g2d = (Graphics2D) g;
        ins = getInsets();
        //changeCordinate = true;
        //Height of window
        Hwindow = getHeight();
      	//width of window
      	Wwindow = getWidth();

      	
        //drawable Area
        width = (Wwindow - ins.left - ins.right);
      	height = (Hwindow - ins.top - ins.bottom);
      	//x-axis
        g.drawLine(0, (ins.top + height/2), width, (ins.top + height/2));
        //y-axis
        g.drawLine((ins.left + width/2), ins.top, (ins.left + width/2), height+ ins.top);
        // draw labels
        drawSimpleCoordinateSystem(500, 400, g2d);
        g2d.setColor(curColor);
     

        Polygon p = new Polygon();
        double xPoint = 0;
        int yPoint;
        String result = "";
        //check if the input is null
        if (isInvalidInput(this.input)) {
        	return;
        }
   
        if(changeCordinate == true)
        {
        	setupCoordinateSystem(30, 0, 0);
        	changeCordinate = false;
        	
        }
        //x values 
        for (int x = 0; x < width; x = x + 5) {
            
        	//convert x from pixels 
            xPoint = screenXtological(x);
            //solve the function for y
            result = evaluateExpression("x", xPoint, this.input);
            System.out.println(result);
            try {
            	//Y function values
            	
                userY = expressionParser.evaluate(result);
                if(Double.isNaN(userY))
                {
                	userY = (double) 0;
                	//setupCoordinateSystem(y, 0, 0);
                }
                
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            //assign Y values to c
            c =  userY;
            
            //revert y to pixels and assign them to yPoint
            yPoint = (reverse());
            
            
            System.out.println(xPoint + ",  " + c);

           //add points 
            p.addPoint(x + ins.left, yPoint + ins.top);            
        }
        //Draw the graph
        g.drawPolyline(p.xpoints, p.ypoints,p.npoints);
        
        
    }
    // draw a rectangle
    private void drawRect(Graphics2D g)
	{
		g.drawRect(Math.min(x_init,  x_cur), Math.min(y_init,  y_cur),
				   Math.abs(x_init - x_cur), Math.abs(y_init - y_cur));
	}
    private int reverse()
  	{
  		
  		double y = -((c - originy - (rangeImaginary /2))/ delta);
  		return (int) y;
  	}
	private void setupCoordinateSystem(double rangeReal, double originReal, double originImaginary)
	{
		this.rangeReal = rangeReal;
		this.originReal = originReal;
		this.originImaginary = originImaginary;
		rangeImaginary = height * rangeReal / width;
		aspectRatio = rangeReal/rangeImaginary;
		delta =  rangeReal / width;	
	}
    
    //Convert from x to Xreal
    private double screenXtological(int x)
	{
   	 	xRange = originReal - (rangeReal/2) + delta * x;
   	 	return xRange;
	}
    
  	
  	//convert y
  	private double screenYtoLogical(int y)
  	{
  		yImaginary = originImaginary + (rangeImaginary /2) - delta * y;
  		return yImaginary;
  	}
  	
    
    public static void drawSimpleCoordinateSystem(int xmax, int ymax, Graphics2D g2d)
	{
    	
		int xOffset = (ins.left + width/2);
		int yOffset = (ins.top + height/2);
		int step = 40;
		int fontSize =9;
		int y = (ins.top + height/2);
		int z = (ins.left + width/2);
		
		//x-axis
		
		Font fontCoordSys = new Font("serif",Font.PLAIN,fontSize);
		g2d.setFont(fontCoordSys);
		
		//Marks and labels for the x-axis.
		for (int i= z; i>0; i=i-step)
		{
			g2d.drawLine(i ,yOffset-2,i,yOffset+2);
			g2d.drawString(String.valueOf((i - z)/20), i - 3, yOffset - 10);
		}
		for (int i=z; i<=width; i=i+step)
		{
			g2d.drawLine(i,yOffset-2,i,yOffset+2);
			g2d.drawString(String.valueOf((i - z)/20), i - 3, yOffset - 10);
		}
	
		//y-axis
		//Marks and labels for the y-axis.
		
		for (int i=y; i>0; i=i-step)
		{
		g2d.drawLine(xOffset-2,i,xOffset+2,i);
		
		g2d.drawString(String.valueOf((i - y)/20),xOffset-13,(int) (i));
		
		}
		for (int i=y; i<=width; i=i+step)
		{
		g2d.drawLine(xOffset-2,i,xOffset+2,i);
		g2d.drawString(String.valueOf((i - y)/20),xOffset-13,(int) (i));
		}
	}

    public boolean isInvalidInput(String expression) {
    	return expression == null || expression.length() == 0;
    }

    public String evaluateExpression(String variable, double value, String expression) {
    	 expression = expression.replace("sin", "Math.sin")
                .replace("cos", "Math.cos")
                .replace("tan", "Math.tan")
                .replace("sqrt", "Math.sqrt")
                .replace("sqr", "Math.pow")
                .replace("log", "Math.log");
    	
    	 return expression.replaceAll(variable, String.valueOf(value));
    }

    public class ExpressionParser {

        ScriptEngine engine;

        ExpressionParser() {
            ScriptEngineManager manager = new ScriptEngineManager();
            engine = manager.getEngineByName("JavaScript");//setup the engine
        }
        
        
        public double evaluate(String expression) throws ScriptException {
            String result = engine.eval(expression).toString();
            
            return new Double(result);
        }
    }

    public static void main(String[] argv) {
        //Generate the window.
        MathFunctionDrawer f = new MathFunctionDrawer();

        //Define a title for the window.
        f.setTitle("Graphing Calculator");
        //Definition of the window size in pixels
        f.setSize(600, 600);
        //Show the window on the screen.
        f.setVisible(true);
        f.setResizable(false);
    }
}
