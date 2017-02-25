package kinect;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

import com.jogamp.opengl.GL2;

import edu.ufl.digitalworlds.opengl.OpenGLPanel;
import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.Skeleton;
import edu.ufl.digitalworlds.j4k.VideoFrame;

/*
 * Copyright 2011-2014, Digital Worlds Institute, University of 
 * Florida, Angelos Barmpoutis.
 * All rights reserved.
 *
 * When this program is used for academic or research purposes, 
 * please cite the following article that introduced this Java library: 
 * 
 * A. Barmpoutis. "Tensor Body: Real-time Reconstruction of the Human Body 
 * and Avatar Synthesis from RGB-D', IEEE Transactions on Cybernetics, 
 * October 2013, Vol. 43(5), Pages: 1347-1356. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain this copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce this
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
@SuppressWarnings("serial")
public class ViewerPanel3D extends OpenGLPanel
{
	private float view_rotx = 0.0f, view_roty = 0.0f, view_rotz = 0.0f;
	private int prevMouseX, prevMouseY;
	
	private DepthMap map=null;
//	private boolean is_playing=false;
	private boolean show_video=false;
	private boolean drawFlatTexture=false;
	
	public void setShowVideo(boolean flag){show_video=flag;}
	
	public void setDrawFlatTexture(boolean flag) {
		drawFlatTexture=flag;
		if (flag == true) {
			map = null;
		}
	}
	
	VideoFrame videoTexture;
	
	Skeleton skeletons[];
	
	public void setup()
	{
		
		//OPENGL SPECIFIC INITIALIZATION (OPTIONAL)
		    GL2 gl=getGL2();
		    gl.glEnable(GL2.GL_CULL_FACE);
		    float light_model_ambient[] = {0.3f, 0.3f, 0.3f, 1.0f};
		    float light0_diffuse[] = {0.9f, 0.9f, 0.9f, 0.9f};   
		    float light0_direction[] = {0.0f, -0.4f, 1.0f, 0.0f};
			gl.glEnable(GL2.GL_NORMALIZE);
		    gl.glShadeModel(GL2.GL_SMOOTH);
		    
		    gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL2.GL_FALSE);
		    gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_FALSE);    
		    gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, light_model_ambient,0);
		    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light0_diffuse,0);
		    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0_direction,0);
		    gl.glEnable(GL2.GL_LIGHT0);
			
		    gl.glEnable(GL2.GL_COLOR_MATERIAL);
			gl.glEnable(GL2.GL_LIGHTING);
			gl.glColor3f(0.9f,0.9f,0.9f);
		    
			
			skeletons=new Skeleton[6];
			
			videoTexture=new VideoFrame();
			
		    background(0, 0, 0);	
	}	
	
	
	public void draw() {
		
		GL2 gl=getGL2();
		
		
		pushMatrix();
	    
		translate(0,0,-2);
	    rotate(view_rotx, 1.0, 0.0, 0.0);
	    rotate(view_roty, 0.0, 1.0, 0.0);
	    rotate(view_rotz, 0.0, 0.0, 1.0);
	    translate(0,0,2);        
	    
	    if(drawFlatTexture) {
	    	//pushMatrix();
			
		    gl.glDisable(GL2.GL_LIGHTING);
		    gl.glEnable(GL2.GL_TEXTURE_2D);
		    gl.glColor3f(1f,1f,1f);
		    videoTexture.use(gl);
		    translate(0,0,-2.2);
		    rotateZ(180);
		    image(8.0/3.0,2);
		    
		    popMatrix();
	    }
	    
	    if(getMap()!=null) 
	    {
	    	if(show_video)
	    	{
	    		gl.glDisable(GL2.GL_LIGHTING);
	    		gl.glEnable(GL2.GL_TEXTURE_2D);
	    		gl.glColor3f(1f,1f,1f);
	    		videoTexture.use(gl);
	    		getMap().drawTexture(gl);
	    		gl.glDisable(GL2.GL_TEXTURE_2D);
	    	}
	    	else
	    	{
	    		gl.glEnable(GL2.GL_LIGHTING);
	    		gl.glDisable(GL2.GL_TEXTURE_2D);
	    		gl.glColor3f(0.9f,0.9f,0.9f);
	    		getMap().drawNormals(gl);
	    	}
	    } //else System.out.println("map is null");
	    
	    gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
	    
	    gl.glDisable(GL2.GL_LIGHTING);
	    gl.glLineWidth(2);
	    gl.glColor3f(1f,0f,0f);
	    for(int i=0;i<skeletons.length;i++)
	    	if(skeletons[i]!=null) 
	    	{
	    		if(skeletons[i].getTimesDrawn()<=10 && skeletons[i].isTracked())
	    		{
	    			skeletons[i].draw(gl);
	    			skeletons[i].increaseTimesDrawn();
	    		}
	    	}
		
	    popMatrix();
	}
	
	
	public void mouseDragged(int x, int y, MouseEvent e) {

	    Dimension size = e.getComponent().getSize();

	    
	    if(isMouseButtonPressed(3)||isMouseButtonPressed(1))
	    {
	    	float thetaY = 360.0f * ( (float)(x-prevMouseX)/(float)size.width);
	    	float thetaX = 360.0f * ( (float)(prevMouseY-y)/(float)size.height);
	    	view_rotx -= thetaX;
	    	view_roty += thetaY;		
	    }
	    
	    prevMouseX = x;
	    prevMouseY = y;

	}

	public void mousePressed(int x, int y, MouseEvent e) {
		prevMouseX = x;
	    prevMouseY = y;
	
	}
	
	
	public DepthMap getMap() {
		return map;
	}

	public void setMap(DepthMap map) {
		this.map = map;
	}

	public VideoFrame getVideoFrame() {
	
		try {
			return videoTexture.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
