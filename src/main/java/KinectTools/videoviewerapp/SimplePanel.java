package KinectTools.videoviewerapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public abstract class SimplePanel extends JPanel implements ItemListener, ActionListener
{
    private static String most_recent_path="";
    public static String getMostRecentPath(){return most_recent_path;}
    public static void setMostRecentPath(String path){most_recent_path=path;}
    
    public static JFrame app_frame;
    public static String frame_title;
    public static BufferedImage frame_icon;
    
    private JButton systeminfo_Button;
    public static SimplePanel app;
    static JProgressBar progressBar;
    static JLabel progressLabel;

    public void setLoadingProgress(String msg,int value)
    {
    	progressLabel.setText(msg);
        progressBar.setValue(value);
    }
    
    public abstract void GUIsetup(JPanel p_root);

    public void GUIclosing(){};
    
    public static boolean showConfirmDialog(String title, String question)
    {
    	if(JOptionPane.showConfirmDialog(app_frame, question, title, JOptionPane.YES_NO_OPTION)==0) return true;
    	else return false;
    }
    
    public static void showErrorDialog(String title, String question)
    {
    	JOptionPane.showMessageDialog(app_frame, question, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showInformationDialog(String title, String question)
    {
    	JOptionPane.showMessageDialog(app_frame, question, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static boolean showConfirmDialog(Component parent, String title, String question)
    {
    	if(JOptionPane.showConfirmDialog(parent, question, title, JOptionPane.YES_NO_OPTION)==0) return true;
    	else return false;
    }
    
    public static void showErrorDialog(Component parent,String title, String question)
    {
    	JOptionPane.showMessageDialog(parent, question, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showInformationDialog(Component parent, String title, String question)
    {
    	JOptionPane.showMessageDialog(parent, question, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void destructor(){// You can still stop closing if you want to
            // dispose method issues the WINDOW_CLOSED event
            app_frame.dispose();
            GUIclosing();
            System.exit(0);
    }
 
    public SimplePanel() {
        setLayout(new BorderLayout());
        setBorder(new EtchedBorder());

        // hard coding 14 = 11 demo dirs + images + fonts + Intro
        progressBar.setMaximum(100);   
        
        setLoadingProgress("Loading ...",0);
        
        UIManager.put("Button.margin", new Insets(0,0,0,0));
       
        JPanel p_root=new JPanel(new BorderLayout());
        GUIsetup(p_root);
        add(p_root,BorderLayout.CENTER);
        
    }

    public void MenuGUIsetup(JMenuBar menuBar){}
 
    public JPanel createWindowSettingsPanel()
    {
    	return createWindowSettingsPanel(Color.BLACK);
    }
    
    public JPanel createWindowSettingsPanel(Color clr)
    {
    	JPanel windowPanel=new JPanel(new GridBagLayout());
  		windowPanel.setOpaque(false);
  		TitledBorder tb=new TitledBorder(new EtchedBorder(), "Window");
		tb.setTitleColor(clr);
  		windowPanel.setBorder(tb);
  		
  		JButton detach_button2=new JButton("Detach window 2");
		detach_button2.addActionListener(this);
		systeminfo_Button=new JButton("System info");
		systeminfo_Button.addActionListener(this);
		
		return windowPanel;
    }
    
    public void GUIactionPerformed(ActionEvent e){}
    public void updateLookAndFeel(){}
    
    public void onWindowStateChanged(){}
    
    public void actionPerformed(ActionEvent e) {
        GUIactionPerformed(e);
    }

    public void GUIitemStateChanged(ItemEvent e){};
    public void itemStateChanged(ItemEvent e) {
        GUIitemStateChanged(e);
    	revalidate();
    }

    public void start() {}

    public void stop() {GUIclosing();System.exit(0);}

    
    public static String FormatDecimal(String in,int decimals)
    {
    	return in.substring(0, Math.min(in.indexOf(".")+decimals+1, in.length()));
    }
       
    public static void createMainFrame(String title)
    {
    	SimplePanel.frame_title=title;
    	app_frame=new JFrame(title);
    	JFrame frame = app_frame;
        frame.getAccessibleContext().setAccessibleDescription("DW Engine");
        int WIDTH = 400, HEIGHT = 200;
        frame.setSize(WIDTH, HEIGHT);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(d.width/2 - WIDTH/2, d.height/2 - HEIGHT/2);
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        WindowAdapter adapter=new WindowAdapter() {
            public void windowClosing(WindowEvent e) { app.destructor();}
            public void windowDeiconified(WindowEvent e) { 
                if (app != null) { /*app.start();*/ }
            }
            public void windowIconified(WindowEvent e) { 
                if (app != null) { /*app.stop();*/ }
            }
            public void windowStateChanged(WindowEvent e)
            {
            	if(app!=null)app.onWindowStateChanged();
            }
        };
        frame.addWindowListener(adapter);
        frame.addWindowStateListener(adapter);
        JOptionPane.setRootFrame(frame);

        JPanel progressPanel = new JPanel() {
            public Insets getInsets() {
                return new Insets(40,30,20,30);
            }
        };
        
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        frame.getContentPane().add(progressPanel, BorderLayout.CENTER);

        Dimension labelSize = new Dimension(400, 20);
        progressLabel = new JLabel("Loading, please wait...");
        progressLabel.setAlignmentX(CENTER_ALIGNMENT);
        progressLabel.setMaximumSize(labelSize);
        progressLabel.setPreferredSize(labelSize);
        
        progressPanel.add(progressLabel);
        progressPanel.add(Box.createRigidArea(new Dimension(1,20)));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressLabel.setLabelFor(progressBar);
        progressBar.setAlignmentX(CENTER_ALIGNMENT);
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        
        progressBar.getAccessibleContext().setAccessibleName("loading progress");
        progressPanel.add(progressBar);

        frame.setIconImage(DWIcon());
        frame.setVisible(true);

    }
    
    public static Image DWIcon()
    {
    	BufferedImage img=new BufferedImage(16, 16, 5);
    	img.setRGB(0,0,-1);
    	img.setRGB(0,1,-5982238);
    	img.setRGB(0,2,-9661979);
    	img.setRGB(0,3,-2630425);
    	img.setRGB(0,4,-1);
    	img.setRGB(0,5,-1);
    	img.setRGB(0,6,-1);
    	img.setRGB(0,7,-3419681);
    	img.setRGB(0,8,-8873758);
    	img.setRGB(0,9,-12027154);
    	img.setRGB(0,10,-14523400);
    	img.setRGB(0,11,-16034306);
    	img.setRGB(0,12,-16034306);
    	img.setRGB(0,13,-14523400);
    	img.setRGB(0,14,-9661979);
    	img.setRGB(0,15,-197637);
    	img.setRGB(1,0,-9661979);
    	img.setRGB(1,1,-16034306);
    	img.setRGB(1,2,-16034306);
    	img.setRGB(1,3,-15705602);
    	img.setRGB(1,4,-8873758);
    	img.setRGB(1,5,-8873758);
    	img.setRGB(1,6,-13669387);
    	img.setRGB(1,7,-16034306);
    	img.setRGB(1,8,-16034306);
    	img.setRGB(1,9,-16034306);
    	img.setRGB(1,10,-16034306);
    	img.setRGB(1,11,-16034306);
    	img.setRGB(1,12,-16034306);
    	img.setRGB(1,13,-16034306);
    	img.setRGB(1,14,-16034306);
    	img.setRGB(1,15,-10450456);
    	img.setRGB(2,0,-14523400);
    	img.setRGB(2,1,-16034306);
    	img.setRGB(2,2,-16034306);
    	img.setRGB(2,3,-16034306);
    	img.setRGB(2,4,-16034306);
    	img.setRGB(2,5,-15246085);
    	img.setRGB(2,6,-12027154);
    	img.setRGB(2,7,-9661979);
    	img.setRGB(2,8,-9661979);
    	img.setRGB(2,9,-9661979);
    	img.setRGB(2,10,-14523400);
    	img.setRGB(2,11,-16034306);
    	img.setRGB(2,12,-16034306);
    	img.setRGB(2,13,-16034306);
    	img.setRGB(2,14,-16034306);
    	img.setRGB(2,15,-15246085);
    	img.setRGB(3,0,-16034306);
    	img.setRGB(3,1,-16034306);
    	img.setRGB(3,2,-16034306);
    	img.setRGB(3,3,-16034306);
    	img.setRGB(3,4,-16034306);
    	img.setRGB(3,5,-15246085);
    	img.setRGB(3,6,-2696218);
    	img.setRGB(3,7,-1);
    	img.setRGB(3,8,-65794);
    	img.setRGB(3,9,-65794);
    	img.setRGB(3,10,-1907480);
    	img.setRGB(3,11,-15246085);
    	img.setRGB(3,12,-16034306);
    	img.setRGB(3,13,-16034306);
    	img.setRGB(3,14,-16034306);
    	img.setRGB(3,15,-13669387);
    	img.setRGB(4,0,-15246085);
    	img.setRGB(4,1,-16034306);
    	img.setRGB(4,2,-16034306);
    	img.setRGB(4,3,-16034306);
    	img.setRGB(4,4,-16034306);
    	img.setRGB(4,5,-16034306);
    	img.setRGB(4,6,-12880911);
    	img.setRGB(4,7,-65793);
    	img.setRGB(4,8,-65794);
    	img.setRGB(4,9,-65794);
    	img.setRGB(4,10,-1907479);
    	img.setRGB(4,11,-14523400);
    	img.setRGB(4,12,-16034306);
    	img.setRGB(4,13,-16034306);
    	img.setRGB(4,14,-15114757);
    	img.setRGB(4,15,-2432789);
    	img.setRGB(5,0,-12880911);
    	img.setRGB(5,1,-16034306);
    	img.setRGB(5,2,-16034306);
    	img.setRGB(5,3,-4601623);
    	img.setRGB(5,4,-4273435);
    	img.setRGB(5,5,-13669387);
    	img.setRGB(5,6,-16034306);
    	img.setRGB(5,7,-8873758);
    	img.setRGB(5,8,-65794);
    	img.setRGB(5,9,-2499358);
    	img.setRGB(5,10,-11304469);
    	img.setRGB(5,11,-16034306);
    	img.setRGB(5,12,-16034306);
    	img.setRGB(5,13,-16034306);
    	img.setRGB(5,14,-3747352);
    	img.setRGB(5,15,-263173);
    	img.setRGB(6,0,-9661979);
    	img.setRGB(6,1,-16034306);
    	img.setRGB(6,2,-12880911);
    	img.setRGB(6,3,-1);
    	img.setRGB(6,4,-1);
    	img.setRGB(6,5,-3353888);
    	img.setRGB(6,6,-15246085);
    	img.setRGB(6,7,-15246085);
    	img.setRGB(6,8,-7362596);
    	img.setRGB(6,9,-13669387);
    	img.setRGB(6,10,-16034306);
    	img.setRGB(6,11,-16034306);
    	img.setRGB(6,12,-10450456);
    	img.setRGB(6,13,-16034306);
    	img.setRGB(6,14,-7690779);
    	img.setRGB(6,15,-197637);
    	img.setRGB(7,0,-4536866);
    	img.setRGB(7,1,-16034306);
    	img.setRGB(7,2,-12880911);
    	img.setRGB(7,3,-1);
    	img.setRGB(7,4,-1);
    	img.setRGB(7,5,-1);
    	img.setRGB(7,6,-8873758);
    	img.setRGB(7,7,-16034306);
    	img.setRGB(7,8,-16034306);
    	img.setRGB(7,9,-16034306);
    	img.setRGB(7,10,-11304469);
    	img.setRGB(7,11,-2696218);
    	img.setRGB(7,12,-197123);
    	img.setRGB(7,13,-12880911);
    	img.setRGB(7,14,-14523400);
    	img.setRGB(7,15,-263173);
    	img.setRGB(8,0,-1);
    	img.setRGB(8,1,-14523400);
    	img.setRGB(8,2,-13669387);
    	img.setRGB(8,3,-1);
    	img.setRGB(8,4,-2301980);
    	img.setRGB(8,5,-11304469);
    	img.setRGB(8,6,-16034306);
    	img.setRGB(8,7,-16034306);
    	img.setRGB(8,8,-16034306);
    	img.setRGB(8,9,-9661979);
    	img.setRGB(8,10,-131587);
    	img.setRGB(8,11,-66051);
    	img.setRGB(8,12,-197380);
    	img.setRGB(8,13,-12880911);
    	img.setRGB(8,14,-16034306);
    	img.setRGB(8,15,-3090201);
    	img.setRGB(9,0,-1);
    	img.setRGB(9,1,-8347933);
    	img.setRGB(9,2,-16034306);
    	img.setRGB(9,3,-9661979);
    	img.setRGB(9,4,-15246085);
    	img.setRGB(9,5,-16034306);
    	img.setRGB(9,6,-14523400);
    	img.setRGB(9,7,-5982238);
    	img.setRGB(9,8,-15246085);
    	img.setRGB(9,9,-15246085);
    	img.setRGB(9,10,-3550492);
    	img.setRGB(9,11,-131587);
    	img.setRGB(9,12,-131844);
    	img.setRGB(9,13,-12880911);
    	img.setRGB(9,14,-16034306);
    	img.setRGB(9,15,-10450456);
    	img.setRGB(10,0,-1);
    	img.setRGB(10,1,-3682076);
    	img.setRGB(10,2,-16034306);
    	img.setRGB(10,3,-16034306);
    	img.setRGB(10,4,-16034306);
    	img.setRGB(10,5,-13669387);
    	img.setRGB(10,6,-1512979);
    	img.setRGB(10,7,-65794);
    	img.setRGB(10,8,-6310687);
    	img.setRGB(10,9,-16034306);
    	img.setRGB(10,10,-15246085);
    	img.setRGB(10,11,-5916445);
    	img.setRGB(10,12,-7099161);
    	img.setRGB(10,13,-16034306);
    	img.setRGB(10,14,-16034306);
    	img.setRGB(10,15,-12880911);
    	img.setRGB(11,0,-3617058);
    	img.setRGB(11,1,-15246085);
    	img.setRGB(11,2,-16034306);
    	img.setRGB(11,3,-16034306);
    	img.setRGB(11,4,-16034306);
    	img.setRGB(11,5,-2039065);
    	img.setRGB(11,6,-1);
    	img.setRGB(11,7,-1);
    	img.setRGB(11,8,-65794);
    	img.setRGB(11,9,-12027154);
    	img.setRGB(11,10,-16034306);
    	img.setRGB(11,11,-16034306);
    	img.setRGB(11,12,-16034306);
    	img.setRGB(11,13,-16034306);
    	img.setRGB(11,14,-16034306);
    	img.setRGB(11,15,-16034306);
    	img.setRGB(12,0,-11304469);
    	img.setRGB(12,1,-16034306);
    	img.setRGB(12,2,-16034306);
    	img.setRGB(12,3,-16034306);
    	img.setRGB(12,4,-16034306);
    	img.setRGB(12,5,-2696218);
    	img.setRGB(12,6,-1);
    	img.setRGB(12,7,-65538);
    	img.setRGB(12,8,-65538);
    	img.setRGB(12,9,-2302236);
    	img.setRGB(12,10,-15246085);
    	img.setRGB(12,11,-16034306);
    	img.setRGB(12,12,-16034306);
    	img.setRGB(12,13,-16034306);
    	img.setRGB(12,14,-16034306);
    	img.setRGB(12,15,-16034306);
    	img.setRGB(13,0,-12880911);
    	img.setRGB(13,1,-16034306);
    	img.setRGB(13,2,-16034306);
    	img.setRGB(13,3,-16034306);
    	img.setRGB(13,4,-16034306);
    	img.setRGB(13,5,-14523400);
    	img.setRGB(13,6,-9661979);
    	img.setRGB(13,7,-9661979);
    	img.setRGB(13,8,-9661979);
    	img.setRGB(13,9,-11304469);
    	img.setRGB(13,10,-15246085);
    	img.setRGB(13,11,-16034306);
    	img.setRGB(13,12,-16034306);
    	img.setRGB(13,13,-16034306);
    	img.setRGB(13,14,-16034306);
    	img.setRGB(13,15,-14523400);
    	img.setRGB(14,0,-9661979);
    	img.setRGB(14,1,-16034306);
    	img.setRGB(14,2,-16034306);
    	img.setRGB(14,3,-16034306);
    	img.setRGB(14,4,-16034306);
    	img.setRGB(14,5,-16034306);
    	img.setRGB(14,6,-16034306);
    	img.setRGB(14,7,-16034306);
    	img.setRGB(14,8,-16034306);
    	img.setRGB(14,9,-14523400);
    	img.setRGB(14,10,-9661979);
    	img.setRGB(14,11,-8873758);
    	img.setRGB(14,12,-15246085);
    	img.setRGB(14,13,-16034306);
    	img.setRGB(14,14,-16034306);
    	img.setRGB(14,15,-4733209);
    	img.setRGB(15,0,-1);
    	img.setRGB(15,1,-9661979);
    	img.setRGB(15,2,-13669387);
    	img.setRGB(15,3,-16034306);
    	img.setRGB(15,4,-16034306);
    	img.setRGB(15,5,-14523400);
    	img.setRGB(15,6,-12027154);
    	img.setRGB(15,7,-8873758);
    	img.setRGB(15,8,-3090456);
    	img.setRGB(15,9,-65794);
    	img.setRGB(15,10,-65794);
    	img.setRGB(15,11,-131587);
    	img.setRGB(15,12,-2893596);
    	img.setRGB(15,13,-8873758);
    	img.setRGB(15,14,-4667678);
    	img.setRGB(15,15,-197637);
    	return img;
    }

    public static void setFrameSize(int WIDTH,int HEIGHT, InputStream iconfile)
    {
    	JFrame frame=app_frame;
    	frame.getContentPane().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(app, BorderLayout.CENTER);
        
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(d.width/2 - WIDTH/2, d.height/2 - HEIGHT/2);
        frame.setSize(WIDTH, HEIGHT);
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if(iconfile!=null)
        {	
        	try{SimplePanel.frame_icon=ImageIO.read(iconfile);
        	    frame.setIconImage(SimplePanel.frame_icon);
        	}catch(IOException e){}
        }
        else
        {
        	SimplePanel.frame_icon=(BufferedImage) SimplePanel.DWIcon();
        	frame.setIconImage(SimplePanel.frame_icon);
        }
    	
        frame.validate();
        frame.repaint();
        
        try{
        frame.getFocusTraversalPolicy()
             .getDefaultComponent(frame)
             .requestFocus();
        }catch(NullPointerException e){}
        
        app.start();


    }
    
    public static void addToGridBag(JPanel panel, Component comp,
            int x, int y, int w, int h, double weightx, double weighty) {

        GridBagLayout gbl = (GridBagLayout) panel.getLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = w;
        c.gridheight = h;
        c.weightx = weightx;
        c.weighty = weighty;
        panel.add(comp);
        gbl.setConstraints(comp, c);
    }

    
}
