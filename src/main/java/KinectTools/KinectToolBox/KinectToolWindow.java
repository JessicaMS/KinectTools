package KinectTools.KinectToolBox;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.EtchedBorder;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import KinectTools.KinectToolBox.Kinect;
import KinectTools.KinectToolBox.ViewerPanel3D;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.VideoFrame;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class KinectToolWindow extends JFrame implements ActionListener, WindowListener {
	static final boolean DESIGN_MODE = false;

	Kinect myKinect;
	ViewerPanel3D mainPanel;

	public static JFrame progressFrame;
	static JProgressBar progressBar;
	static JLabel progressLabel;

	boolean showSkeletons=false;
	boolean seatedSkeleton=false;
	static final int colorStream = 1;
	static final int depthStream = 2;
	static final int depthWithTexture = 3;
	static final int IRStream = 4;
	
	private int viewModeState = 1;


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

	public void showErrorDialog(String title, String question)
	{
		JOptionPane.showMessageDialog(this, question, title, JOptionPane.ERROR_MESSAGE);
	}


	private void initializeKinect() {

		setLoadingProgress("Intitializing Kinect...",20);
		myKinect = new Kinect();

		myKinect.setDepthResolution(320, 240);
		myKinect.setColorResolution(640, 480);
		if(!myKinect.start(J4KSDK.COLOR))
		{
			showErrorDialog("ERROR", "<html><center><br>ERROR: The Kinect device could not be initialized.<br><br>1. Check if the Microsoft's Kinect SDK was succesfully installed on this computer.<br> 2. Check if the Kinect is plugged into a power outlet.<br>3. Check if the Kinect is connected to a USB port of this computer.</center>");
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

		int flags= 0;
		if (showSkeletons) flags=flags|Kinect.SKELETON;


		if (viewModeState == colorStream) {
			flags = flags|Kinect.COLOR;
			mainPanel.setDrawFlatTexture(true);
			
		} else if (viewModeState == depthStream || viewModeState == depthWithTexture) {
			flags = flags|Kinect.COLOR;
			flags = flags|Kinect.DEPTH;
			flags = flags|Kinect.XYZ;
			flags = flags|Kinect.UV;

			mainPanel.setDrawFlatTexture(false);
		} 
		
		if (viewModeState == IRStream) {
			flags = flags|Kinect.INFRARED;
			myKinect.updateTextureUsingInfrared(true);
			mainPanel.setDrawFlatTexture(true);
		} else {
			myKinect.updateTextureUsingInfrared(false);
		}
		
		if (viewModeState == depthWithTexture) {
			mainPanel.setShowVideo(true);
			myKinect.computeUV(true);
		} else {
			mainPanel.setShowVideo(false);
			myKinect.computeUV(false);
		}


		myKinect.start(flags);
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

	public void captureImage() {
		byte[] imageData =  myKinect.getVideoTexture().getData();
		System.out.println("Size of captured imageData: " + imageData.length);

		byte[] RGB = new byte[myKinect.getColorWidth()* myKinect.getColorHeight()*3];

		// OpenGL uses:  GL2.GL_BGRA, create RGB byte array
		int j=0;
		for (int i = 0;  i < imageData.length; i += 4) {
			byte b, g, r;

			b = imageData[i];
			g = imageData[i + 1];
			r = imageData[i + 2];

			RGB[j++] = b; 
			RGB[j++] = g; 
			RGB[j++] = r;
		}

		BufferedImage image = new BufferedImage(myKinect.getColorWidth(), myKinect.getColorHeight(), BufferedImage.TYPE_3BYTE_BGR);
		image.setData(Raster.createRaster(image.getSampleModel(), new DataBufferByte(RGB, RGB.length), new Point() ));
		System.out.println("Input size: " + image.getHeight());

		try {
			ImageIO.write(image, "png", new File("c:/captures/new.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void guiActionHandler(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "Overlay Skeleton":
			showSkeletons = !showSkeletons;
			resetKinect();
			break;
		case "Color Stream":
			viewModeState = colorStream;
			resetKinect();
			break;
		case "Depth Stream":
			viewModeState = depthStream;
			resetKinect();
			break;
		case "Infrared Stream":
			viewModeState = IRStream;
			resetKinect();
			break;
		case "Depth with Texture":
			viewModeState = depthWithTexture;
			resetKinect();
			break;

		default:
			System.out.println("Unhandled window event:" + e.getActionCommand());
			break;
		}


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
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{66, 74, 104, 75, 203, 61, 0};
		gbl_contentPane.rowHeights = new int[]{383, 14, 14, 27, 26, 27, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);


		final JCheckBox chckbxOverlaySkeleton = new JCheckBox("Overlay Skeleton");
		chckbxOverlaySkeleton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guiActionHandler(e);
			}
		});

		JRadioButton rdbtnTexture = new JRadioButton("Color Stream");
		rdbtnTexture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guiActionHandler(e);
			}
		});
		rdbtnTexture.setSelected(true);
		buttonGroup.add(rdbtnTexture);
		GridBagConstraints gbc_rdbtnTexture = new GridBagConstraints();
		gbc_rdbtnTexture.anchor = GridBagConstraints.WEST;
		gbc_rdbtnTexture.fill = GridBagConstraints.VERTICAL;
		gbc_rdbtnTexture.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnTexture.gridx = 1;
		gbc_rdbtnTexture.gridy = 2;
		contentPane.add(rdbtnTexture, gbc_rdbtnTexture);
		GridBagConstraints gbc_chckbxOverlaySkeleton = new GridBagConstraints();
		gbc_chckbxOverlaySkeleton.anchor = GridBagConstraints.WEST;
		gbc_chckbxOverlaySkeleton.fill = GridBagConstraints.VERTICAL;
		gbc_chckbxOverlaySkeleton.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxOverlaySkeleton.gridx = 4;
		gbc_chckbxOverlaySkeleton.gridy = 2;
		contentPane.add(chckbxOverlaySkeleton, gbc_chckbxOverlaySkeleton);



		JCheckBox chckbxSitting = new JCheckBox("Sitting Skeleton");
		chckbxSitting.addActionListener(this);

		JRadioButton rdbtnInfrared = new JRadioButton("Infrared Stream");
		rdbtnInfrared.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guiActionHandler(e);
			}
		});
		buttonGroup.add(rdbtnInfrared);
		GridBagConstraints gbc_rdbtnInfrared = new GridBagConstraints();
		gbc_rdbtnInfrared.anchor = GridBagConstraints.WEST;
		gbc_rdbtnInfrared.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnInfrared.gridx = 1;
		gbc_rdbtnInfrared.gridy = 3;
		contentPane.add(rdbtnInfrared, gbc_rdbtnInfrared);
		GridBagConstraints gbc_chckbxSitting = new GridBagConstraints();
		gbc_chckbxSitting.anchor = GridBagConstraints.WEST;
		gbc_chckbxSitting.fill = GridBagConstraints.VERTICAL;
		gbc_chckbxSitting.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxSitting.gridx = 4;
		gbc_chckbxSitting.gridy = 3;
		contentPane.add(chckbxSitting, gbc_chckbxSitting);



		progressBar.setMaximum(100);   
		setLoadingProgress("Loading ...",0);

		initializeKinect();

		JRadioButton rdbtnDepth = new JRadioButton("Depth Stream");
		rdbtnDepth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guiActionHandler(e);
			}
		});
		buttonGroup.add(rdbtnDepth);
		GridBagConstraints gbc_rdbtnDepth = new GridBagConstraints();
		gbc_rdbtnDepth.anchor = GridBagConstraints.WEST;
		gbc_rdbtnDepth.fill = GridBagConstraints.VERTICAL;
		gbc_rdbtnDepth.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnDepth.gridx = 1;
		gbc_rdbtnDepth.gridy = 4;
		contentPane.add(rdbtnDepth, gbc_rdbtnDepth);

		JRadioButton rdbtnDepthTexture = new JRadioButton("Depth with Texture");
		rdbtnDepthTexture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guiActionHandler(e);
			}
		});

		JButton btnCapture = new JButton("Capture Flat Texture");
		btnCapture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				captureImage();
			}
		});


		GridBagConstraints gbc_btnCapture = new GridBagConstraints();
		gbc_btnCapture.insets = new Insets(0, 0, 5, 5);
		gbc_btnCapture.fill = GridBagConstraints.BOTH;
		gbc_btnCapture.gridx = 4;
		gbc_btnCapture.gridy = 5;
		contentPane.add(btnCapture, gbc_btnCapture);

		JLabel lblNewLabel = new JLabel("(Accelerometer Data)");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 4;
		gbc_lblNewLabel.gridy = 6;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		myKinect.setLabel( lblNewLabel);

		buttonGroup.add(rdbtnDepthTexture);
		GridBagConstraints gbc_radioDepthTexture = new GridBagConstraints();
		gbc_radioDepthTexture.insets = new Insets(0, 0, 5, 5);
		gbc_radioDepthTexture.gridx = 1;
		gbc_radioDepthTexture.gridy = 5;
		contentPane.add(rdbtnDepthTexture, gbc_radioDepthTexture);

		setLoadingProgress("Intitializing OpenGL...",60);
		mainPanel= new ViewerPanel3D();
		mainPanel.setShowVideo(false);
		mainPanel.setDrawFlatTexture(true);
		myKinect.setViewer(mainPanel);
		mainPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		mainPanel.setBounds(0, 0, 609, 329);

		progressFrame.setVisible(false);

		JPanel panel = new JPanel();
		panel.setBackground(Color.ORANGE);

		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 6;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;

		if (DESIGN_MODE) {
			contentPane.add(panel, gbc_panel);
		} else {
			contentPane.add(mainPanel, gbc_panel.clone());	
		}

	}



	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosing(WindowEvent e) {
		progressFrame.dispose();
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

	}
}
