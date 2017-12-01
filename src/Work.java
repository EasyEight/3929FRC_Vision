import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.*;
import javax.swing.*;

import org.opencv.core.*;
import org.opencv.videoio.*;
import org.opencv.video.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.*;
import org.opencv.objdetect.Objdetect;

public class Work extends JPanel
{
   public static void main(String[]args) throws IOException
   {
	  boolean capping = true;
	  System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
      JFrame frame = new JFrame();
      
	  VideoCapture cap = new VideoCapture("http://root:root@10.39.29.67/axis-cgi/mjpg/video.cgi?date=1&clock=1&resolution=320x240");
      //VideoCapture cap = new VideoCapture();
      //cap.open(0);
	  //cap.open(0);//?dummy=video.mjpg");"10.39.29.67:3218/view/viewer_index.shtml?id=108"
	  //cap.open("http://10.39.29.67/view/viewer_index.shtml?id=0");
	  Mat raw = new Mat();
      Mat hsv = new Mat();
      Mat mask = new Mat();
      Mat edge = new Mat();
      //Mat blur = new Mat();
      Mat hierarchy = new Mat();
      ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
      BufferedImage img;

      Scalar lower = new Scalar(30, 100, 100);
      Scalar upper = new Scalar(60, 255, 255); //255 is max
     // Size kSize = new Size(25, 25);

      double area1;
      double area2;
      
      MatOfPoint contour1 = new MatOfPoint();
      MatOfPoint contour2 = new MatOfPoint();
      
	  while(capping){
		  
	      boolean found = false;
	      
	      cap.read(raw);
	      Imgproc.cvtColor(raw, hsv, Imgproc.COLOR_RGB2HSV_FULL);
	      Core.inRange(hsv, lower, upper, mask);
	      //Imgproc.GaussianBlur(mask, blur, kSize, 1, 1);
	      Imgproc.Canny(mask, edge, .5f, 1f);
	      Imgproc.findContours(edge, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
	      
	      if(contours.size() > 5){
	    	  int w = 0;
	    	  double max = 0;
	    	  for(int x = 0; x < contours.size() - 1; x++){
	    		 if(Imgproc.contourArea(contours.get(x)) > max){
	    			 max = Imgproc.contourArea(contours.get(x));
	    			 contour1 = contours.get(x);
	    			 w = x;
	    		 }
	    	  }
	    	  contours.remove(w);
	    	  max = 0;
	    	  for(int x = 0; x < contours.size() - 1; x++){
	    		 if(Imgproc.contourArea(contours.get(x)) > max){
	    			 max = Imgproc.contourArea(contours.get(x));
	    			 contour2 = contours.get(x);
	    		 }
	    	  }
	    	  
	    	  area1 = Imgproc.contourArea(contour1);
	    	  area2 = Imgproc.contourArea(contour2);
	    	  
	    	  if(area1 > area2 && area2 * 1.5 > area1 && area1 + area2 > 100){
	    		  found = true;
	    	  }
	    	  else if(area2 > area1 && area1 * 1.5 > area2 && area1 + area2 > 100){
	    		  found = true;
	    	  }
	    	  if(found){
	    		  //find extremas from area1 and area2
	    		  
	    		  int extrema1L = 0; int extrema1R = 0;
	    		  int extrema2L = 0; int extrema2R = 0;
	    		  
	    		  int extremaLeft;
	    		  int extremaRight;

	    		  if(extrema1L < extrema2L){
	    			  extremaLeft = extrema1L;
	    			  extremaRight = extrema2R;
	    		  }
	    		  else{
	    			  extremaLeft = extrema2L;
	    			  extremaRight = extrema1R;
	    		  }
	    		  /*
	    		  if(extremaLeft > (Videoio.CAP_PROP_FRAME_WIDTH - extremaRight) + 20){
	    			  System.out.println("Turn left!");
	    		  }
	    		  else if(extremaLeft + 20 < (Videoio.CAP_PROP_FRAME_WIDTH - extremaRight)){
	    			  System.out.println("Turn right!");
	    		  }
	    		  else{
	    			  System.out.println("Go forward!");
	    			  if(extremaLeft < 20 && Videoio.CAP_PROP_FRAME_WIDTH - extremaRight < 20){
	    				  System.out.println("place");
	    			  }
	    		  }
	    		  */
	    	  }
	      }
	      img = mat2Img(edge);
	      frame.getContentPane().add(new ImagePanel(img));
	      frame.setSize((int) cap.get(Videoio.CAP_PROP_FRAME_WIDTH), (int) cap.get(Videoio.CAP_PROP_FRAME_HEIGHT));
	      
	      frame.setVisible(true);
      }
      //mat = im.imread("green.jpg", mat);
	
      
      cap.release();
   }
   
   public static BufferedImage mat2Img(Mat in)
   {
       BufferedImage out;
       byte[] data = new byte[320 * 240 * (int)in.elemSize()];
       int type;
       in.get(0, 0, data);
       
       if(in.channels() == 1)
           type = BufferedImage.TYPE_BYTE_GRAY;
       else
           type = BufferedImage.TYPE_3BYTE_BGR;
       out = new BufferedImage(320, 240, type);

       out.getRaster().setDataElements(0, 0, 320, 240, data);
       return out;
   }
}