package KinectTools.testdll;

import com.jogamp.opengl.GL2;

import edu.ufl.digitalworlds.j4k.Skeleton;



public class TestDLL {

	private static void takethAway() {
		Kinect1 myGarbage = new Kinect1();

	}

	public static void main(String args[]) {
		System.out.println("Hello frind");

		takethAway();
		System.out.println("Is it gone??");
	}

}
