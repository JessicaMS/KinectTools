package KinectTools.KinectToolBox;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.EtchedBorder;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.event.ChangeListener;

import KinectTools.KinectToolBox.Kinect;
import KinectTools.KinectToolBox.ViewerPanel3D;
import edu.ufl.digitalworlds.gui.DWApp;
import edu.ufl.digitalworlds.j4k.J4K1;
import edu.ufl.digitalworlds.j4k.J4KSDK;

import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;

public class KinectToolWindow extends JFrame implements ActionListener, WindowListener {
	Kinect myKinect;
	ViewerPanel3D mainPanel;

	public static JFrame progressFrame;
	static JProgressBar progressBar;
	static JLabel progressLabel;
	
	boolean showSkeletons=false;
	boolean seatedSkeleton=false;
	

	public void setLoadingProgress(String msg,int value)
	{
		progressLabel.setText(msg);
		progressBar.setValue(value);
	}


	private JPanel contentPane;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * Launch the application.
	 */



	private void initializeKinect() {

		setLoadingProgress("Intitializing Kinect...",20);
		myKinect = new Kinect();

		if(!myKinect.start(J4KSDK.COLOR))
		{
			DWApp.showErrorDialog("ERROR", "<html><center><br>ERROR: The Kinect device could not be initialized.<br><br>1. Check if the Microsoft's Kinect SDK was succesfully installed on this computer.<br> 2. Check if the Kinect is plugged into a power outlet.<br>3. Check if the Kinect is connected to a USB port of this computer.</center>");
			//System.exit(0); 
		} else System.out.println("Kinect Started");
	}
	private void resetKinect()
	{
		//if(turn_off.getText().compareTo("Turn on")==0) return;
		
		myKinect.stop();
//		int depth_res=J4K1.NUI_IMAGE_RESOLUTION_INVALID;
//		if(depth_resolution.getSelectedIndex()==0) myKinect.setDepthResolution(80, 60);//  depth_res=J4K1.NUI_IMAGE_RESOLUTION_80x60;
//		else if(depth_resolution.getSelectedIndex()==1) myKinect.setDepthResolution(320, 240);//depth_res=J4K1.NUI_IMAGE_RESOLUTION_320x240;
//		else if(depth_resolution.getSelectedIndex()==2) myKinect.setDepthResolution(640, 480);//depth_res=J4K1.NUI_IMAGE_RESOLUTION_640x480;
		
//		int video_res=J4K1.NUI_IMAGE_RESOLUTION_INVALID;
//		if(video_resolution.getSelectedIndex()==0) myKinect.setColorResolution(640, 480);//video_res=J4K1.NUI_IMAGE_RESOLUTION_640x480;
//		else if(video_resolution.getSelectedIndex()==1) myKinect.setDepthResolution(1280, 960);//video_res=J4K1.NUI_IMAGE_RESOLUTION_1280x960;
		
		int flags= Kinect.COLOR;
		
		if (showSkeletons) flags=flags|Kinect.SKELETON;
		//flags=flags|Kinect.DEPTH;
		//flags=flags|Kinect.XYZ;
		//if(show_infrared.isSelected()) {flags=flags|Kinect.INFRARED; myKinect.updateTextureUsingInfrared(true);}
		//else myKinect.updateTextureUsingInfrared(false);
			
		myKinect.start(flags);
//		if(show_video.isSelected())myKinect.computeUV(true);
//		else myKinect.computeUV(false);
//		if(seated_skeleton.isSelected())myKinect.setSeatedSkeletonTracking(true);
//		if(near_mode.isSelected()) myKinect.setNearMode(true);
	}
	
	/**
	 * Create Progress Frame
	 */
	public void createProgressFrame() {
		progressFrame = new JFrame("Progress");
		progressFrame.getAccessibleContext().setAccessibleDescription("Kinect Tool Window");
        int WIDTH = 400, HEIGHT = 200;
        progressFrame.setSize(WIDTH, HEIGHT);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        progressFrame.setLocation(d.width/2 - WIDTH/2, d.height/2 - HEIGHT/2);
        progressFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        progressFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        WindowAdapter adapter=new WindowAdapter() {
            public void windowClosing(WindowEvent e) { this.destructor();}

			private void destructor() {
				// TODO Auto-generated method stub
			}

			public void windowDeiconified(WindowEvent e) { 
                if (this != null) { /*app.start();*/ }
            }
            public void windowIconified(WindowEvent e) { 
                if (this != null) { /*app.stop();*/ }
            }
            public void windowStateChanged(WindowEvent e)
            {
            	if(this!=null)this.onWindowStateChanged();
            }

			private void onWindowStateChanged() {
				// TODO Auto-generated method stub
				
			}

        };
        progressFrame.addWindowListener(adapter);
        progressFrame.addWindowStateListener(adapter);
        JOptionPane.setRootFrame(progressFrame);

        JPanel progressPanel = new JPanel() {
            public Insets getInsets() {
                return new Insets(40,30,20,30);
            }
        };
        
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressFrame.getContentPane().add(progressPanel, BorderLayout.CENTER);

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

        progressFrame.setVisible(true);

	}

	/**
	 * Create the frame.
	 */
	public KinectToolWindow() {
		createProgressFrame();
       
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 625, 645);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JRadioButton rdbtnNewRadioButton = new JRadioButton("Texture Stream");
		rdbtnNewRadioButton.setSelected(true);
		buttonGroup.add(rdbtnNewRadioButton);
		rdbtnNewRadioButton.setBounds(0, 336, 109, 31);
		contentPane.add(rdbtnNewRadioButton);

		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Depth Stream");
		buttonGroup.add(rdbtnNewRadioButton_1);
		rdbtnNewRadioButton_1.setBounds(0, 371, 109, 23);
		contentPane.add(rdbtnNewRadioButton_1);

		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBounds(438, 371, 46, 23);
		contentPane.add(lblNewLabel);

		final JCheckBox chckbxOverlaySkeleton = new JCheckBox("Overlay Skeleton");
		chckbxOverlaySkeleton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxOverlaySkeleton.isSelected()) {
					showSkeletons = true;
				} else showSkeletons = false;
				resetKinect();
			}
		});
		chckbxOverlaySkeleton.setBounds(276, 340, 126, 23);
		contentPane.add(chckbxOverlaySkeleton);

		JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 405, 589, 158);
		contentPane.add(textArea);

		JCheckBox chckbxLogEvents = new JCheckBox("Log Texture Events");
		chckbxLogEvents.addActionListener(this);
		chckbxLogEvents.setSelected(true);


		chckbxLogEvents.setBounds(125, 340, 134, 23);
		contentPane.add(chckbxLogEvents);


		progressBar.setMaximum(100);   
        setLoadingProgress("Loading ...",0);
		
		initializeKinect();

		setLoadingProgress("Intitializing OpenGL...",60);
		mainPanel=new ViewerPanel3D();
		mainPanel.setShowVideo(false);
		mainPanel.setShowColorStream(true);
		myKinect.setViewer(mainPanel);
		mainPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		mainPanel.setBounds(0, 0, 609, 329);
		
		progressFrame.setVisible(false);
		
		contentPane.add(mainPanel);
		

	}

	

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosing(WindowEvent e) {
		myKinect.stop();
		
	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
