package KinectTools.testdll;

import edu.ufl.digitalworlds.j4k.J4K1;

public class Kinect1 extends J4K1
{
	//private J4KSDK callback;

	//		J4K1SDKImpl(J4KSDK callback)
	//		{
	//			super();
	//			this.callback=callback;
	//		}
	//		
	//		J4K1SDKImpl(J4KSDK callback,int id)
	//		{
	//			super(id);
	//			this.callback=callback;
	//		}

	@Override
	public void onDepthFrameEvent(short[] packed_depth, float[] XYZ, float[] UV) {
		//			callback.onDepthFrameEventFromNative(packed_depth, null, XYZ, UV);
	}

	@Override
	public void onSkeletonFrameEvent(boolean[] flags, float[] joint_positions, byte[] joint_state) {
		//			callback.onSkeletonFrameEventFromNative(flags, joint_positions, null, joint_state);
	}

	@Override
	public void onVideoFrameEvent(byte[] video_data) {
		//			callback.onColorFrameEventFromNative(video_data);
	}	

	@Override
	public void onInfraredFrameEvent(short[] infrared_data) {
		//			callback.onInfraredFrameEventFromNative(infrared_data);
	}

	public void finalize() {
		System.out.println("A garbage collected");
	}
}

