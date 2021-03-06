package kinectToolBox;

import java.awt.EventQueue;

import javax.swing.JFrame;

class JFrameThread implements Runnable {
	private JFrame frame;
	
	JFrameThread(JFrame frame) {
		setJFrame(frame);
	}
	
	public void setJFrame(JFrame frame) {
		this.frame = frame;
	}
	
	public void run() {
		try {
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

public class KinectToolBox {
	
	public static void main(String[] args) {
		
		KinectToolWindow frame = new KinectToolWindow();

		JFrameThread windowThread = new JFrameThread(frame);

		EventQueue.invokeLater(windowThread);
		

	}
}
