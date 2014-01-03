package com.mael.phone_door;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.binary.Base64;

//import gnu.io.SerialPort;


public class stream_cam {
static InputStream zob;
static int frameptr  = 0;
static int jpglen = 0;

//Communicator object
//Communicator communicator = null;
 static Communicator camera = new Communicator();

public static void main(String args[]) {
	
	@SuppressWarnings("unused")
	String port = "COM3";  /*	if (args[1] != null) {	port = args[1];} 	else {port ="COM3" ;}
	*/
	camera.searchForPorts(); // if removed the port number below is unknown on the function "connect"...
	camera.connect("COM3",115200);
	
//	System.out.println("connected" + camera.getConnected());
      if (camera.getConnected() == true)
      { //System.out.println("connected");
          if (camera.initIOStream() == true)
          { //System.out.println("init io");
        	  camera.initListener();
        	//  System.out.println("listening ");
          }
      }
  //  reset();// to put when app is killed
    sleep(20);
     boolean conn_115200 = getVersion();  
  //    System.out.println("get version at 115200 bds result " + conn_115200 );
     
        
  if (!conn_115200) {
    	  camera.disconnect();
      sleep(20);
      camera.connect("COM3",38400);
  	
  //	System.out.println("connected" + camera.getConnected());
        if (camera.getConnected() == true)
        { //System.out.println("connected");
            if (camera.initIOStream() == true)
            { //System.out.println("init io");
          	  camera.initListener();
          //	  System.out.println("listening but listener disabled ;-)");
            }
        }
      sleep(20);
      boolean conn_38400 = getVersion(); // check 115bds a faire
  //    System.out.println("getversion result  at 38400 bds" + conn_38400 );
      camera.sleep(10);
      
      ChangeBaudRate(115200);
  //  	  System.out.println("Changed BaudRate " );
    	  sleep(5);
    	  camera.disconnect();
      sleep(100);
      camera.connect("COM3",115200);
  	
//  	System.out.println("connected" + camera.getConnected());
        if (camera.getConnected() == true)
        { //System.out.println("connected");
            if (camera.initIOStream() == true)
            {// System.out.println("init io");
          	  camera.initListener();
          	  //System.out.println("listening but listener disabled ;-)");
            }
        }
        conn_115200 = getVersion();
        camera.sleep(1);
 //       System.out.println("get version at 115200 bds result " + conn_115200 );
      } 

/********* end of init phase *******************/        
        
/***********  settings phase *******************/
// Set the picture size - you can choose one of 640x480, 320x240 or 160x120 
     // Remember that bigger pictures take longer to transmit!
     //setImageSize(VC0706_640x480);        // biggest
     //        
 //       boolean b = setImageSize(VC0706_320x240);// medium
        	//	setImageSize(VC0706_160x120);          // small
  //      camera.sleep(100); //sleep cannot be included into set image size as it be after the command
  //      System.out.println("setImageSize result " + b); // Doesn't work, so bypassed       
/*        int imgsize = getImageSize();
        
        System.out.println("getImageSize vu dans stream_cam " + imgsize);
  */           
 
     
      
     
     
      TVoff();
      camera.sleep(1);
      boolean comp = setCompression((byte) 0x99); // quite high but in line with requirements, may be to put as an rg ?
 //     System.out.println("compression " + comp);
      camera.sleep(1);
      resumeVideo(); 
        
      
/***********  END of settings phase ************/        
  		int nb_image = 1;
  		while (nb_image >0){
  			nb_image++;

  		/*
  			byte[] a = new byte[0];
  			a = CaptureBase64();
  		*/	//PrintStream ps = new PrintStream(a);
  			String a = CaptureBase64();
  			System.out.print(a);
			System.out.flush();
  			
          
          camera.disconnect();
          sleep(300);
          camera.connect("COM3",115200);
 //    		System.out.println("connected" + camera.getConnected());
      		if (camera.getConnected() == true)
     			{//  System.out.println("connected");
            if (camera.initIOStream() == true)
          	{ //  System.out.println("init io");
          	  	camera.initListener();
       //   	  	System.out.println("listening ");
            }
        }
    
    		sleep(10);
       conn_115200 = getVersion();  
       sleep(10);
      //  System.out.println("get version at 115200 bds result " + conn_115200 );
   
  		}
  		
}
/**************** Functions *******/

static boolean connect() {
	camera.connect("COM3",115200);
	return true;
}


static boolean disconnect() {
	camera.disconnect();
	return true;
}
static void captureAndSave(int numimage) {
	//	int nb_image = numimage;
      	long time = System.currentTimeMillis();
      	jpglen = 30000;
  		
      	do {
     	     camera.sleep(2);
 			resumeVideo();
 			camera.sleep(2);
 			//System.out.println("resuming "+ resume);
   
 			takePicture();
 			camera.sleep(2);
 			jpglen = frameLength();
     	} while (jpglen > 20000);
      	
//  		System.out.println("Picture taken!");
//  		System.out.println("jpglen  " + jpglen); 
        byte[] bufferfile = {(byte) 0x00};// 

        bufferfile = readImageData(jpglen,8192); //never reach above, thus it is a compromise 
   /*      camera.sleep(2);
        
        FileOutputStream fileOuputStream;
        
  		try { 
  		    //convert array of bytes into file
  		   fileOuputStream = new FileOutputStream("K:\\mes_docs\\Mael\\android_app_sdk\\eclise workspace\\TurtleMeatSimpleHttpServer\\temp\\cupajoe.jpg");  //"C:\\tmp\\image"+nb_image+".jpg");
  		    fileOuputStream.write(bufferfile);
  		    fileOuputStream.close();
 // 		   System.out.println("written");
 
		} catch (Exception e) {
			e.printStackTrace();
		}
  		  time = time - System.currentTimeMillis();
         System.out.println("done! time");
          System.out.println(time + " ms");
          System.out.println("Stored ");
          System.out.println(bufferfile.length);
          System.out.println(" byte image.");
          
   */     System.out.print("dataAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA5555555555555555555555"); 
   		  System.out.flush();
          System.out.print(bufferfile);
          System.out.flush();
          System.out.print("stop_data");
          System.out.flush();
}

static byte[]  Capture() {
	//	int nb_image = numimage;
      	
      	jpglen = 30000;
  		
      	do {
     	     camera.sleep(2);
 			resumeVideo();
 			camera.sleep(2);
 			takePicture();
 			camera.sleep(2);
 			jpglen = frameLength();
     	} while (jpglen > 20000);

        byte[] bufferfile = {(byte) 0x00};// 

        bufferfile = readImageData(jpglen,8192); //never reach above, thus it is a compromise 
        camera.sleep(2);
        ByteArrayOutputStream  image = new ByteArrayOutputStream(); 
        try {
			image.write(bufferfile);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
        return bufferfile;
     
}


static String CaptureBase64() {
	//	int nb_image = numimage;
      	
      	jpglen = 30000;
  		
      	do {
     	     camera.sleep(2);
 			resumeVideo();
 			camera.sleep(2);
 			takePicture();
 			camera.sleep(2);
 			jpglen = frameLength();
     	} while (jpglen > 20000);

        byte[] bufferfile = {(byte) 0x00};// 

        bufferfile = readImageData(jpglen,8192); //never reach above, thus it is a compromise 
        camera.sleep(2);
        ByteArrayOutputStream  image = new ByteArrayOutputStream(); 
        try {
			image.write(bufferfile);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   /*     byte[] Base64bufferfile =  org.apache.commons.codec.binary.Base64.encodeBase64(bufferfile);
        String Base64output = new String(Base64bufferfile);
     */ return  Base64.encodeBase64URLSafeString(bufferfile);
    //    return Base64output;
     
}







static boolean ChangeBaudRate(int baudrate) {
	
	byte arg_buffer1 = 0;
	byte arg_buffer2 = 0;
	
	switch (baudrate) 
	{ 
	case 9600   : arg_buffer1=(byte) 0xAE; arg_buffer2 = (byte) 0xC8; break; 
	case 19200  : arg_buffer1=(byte) 0x56; arg_buffer2 = (byte) 0xE4; break; 
	case 38400  : arg_buffer1=(byte) 0x2A; arg_buffer2 = (byte) 0xF2; break; 
	case 57600  : arg_buffer1=(byte) 0x1C; arg_buffer2 = (byte) 0x4C; break; 
	case 115200 : arg_buffer1=(byte) 0x0D; arg_buffer2 = (byte) 0xA6; break; 
	
	}
	
	byte[] args  = {(byte) 0x03, (byte) 0x01,(byte)   arg_buffer1 ,(byte) arg_buffer2 };
	                    
	runCommand(VC0706_SET_BAUDRATE, args,  20); //data often starts at adress != 0

//	  System.out.println("frameptr = : " + frameptr);
	  camera.sleep(10); // let the cam answer
	  boolean result = false; 
	  byte[] answer = null;
	  
  	  while (!result) {
  			  answer = camera.readSerial();
  			  for (int i =0; i < (answer.length - 4); i++ ){
  				  if ((answer[i] == (byte) 0x76) ||
	  				  (answer[i+1] == (byte) 0x00) ||  //looking for camera delay
  					  (answer[i+2] == (byte) 0x24) ||
	  				  (answer[i+3] == (byte) 0x00))
  				  {
  					  result = true;
  					  break;
  				  }
  			  }
  	  }		  
	return result;
	
}

public static void sleep(long millis) {
    try {
        TimeUnit.MILLISECONDS.sleep(millis);
    } catch (InterruptedException e) {
    	System.out.println("Sleep interrupted " + e);
    }
}

static int frameLength() {
	  byte[] args = new byte[]{(byte) 0x01,(byte) 0x00};
	  runCommand(VC0706_GET_FBUF_LEN, args, 40);
	  int len, offset = 0;
	  camera.sleep(10);
	  byte[] answer = null; 
	  boolean result = false; 
  	  while (!result) {
  		  answer = camera.readSerial();
		offset = 0;
		  for (int i =0; i < (answer.length - 4); i++ ){
			  if ((answer[i] == (byte) 0x76) ||
			      (answer[i+1] == (byte) 0x00) ||
			      (answer[i+2] == (byte) VC0706_GET_FBUF_LEN) ||
			      (answer[i+3] == (byte) 0x00))
			  {
	//			  System.out.println("sgot it, i = " + i );
				  offset = i+12;
//				  System.out.println("so, offset = " + offset );
				  result = true;
	//			  System.out.println("at the end i =  " + i );
				  break;
			  }
		  }
  	  }
//  	System.out.println("converting " + answer[offset] + "and " + answer[offset+1] + "and " + answer[offset+2] + "and " + answer[offset+3] );
  	byte[] arr = { answer[offset], answer[offset+1],answer[offset+2],answer[offset+3]};
  	ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default
  	len = wrapped.getInt(); // 1
  	 if ( len  > 50000){//sometimes the offset is 2 ahead, so files are not > 50k
 // 		System.out.println("image lentgh problem = " + len); 
  		byte[]arr1 = { answer[offset-2], answer[offset-1],answer[offset],answer[offset+1]};
  	  	wrapped = ByteBuffer.wrap(arr1); // big-endian by default
  	  	len = wrapped.getInt(); 
//  	 	System.out.println("real lentgh = " + len); 
	}
    return len;
	}

	static boolean takePicture() {
		frameptr = 0;
		return cameraFrameBuffCtrl(VC0706_STOPCURRENTFRAME);
	}
	
	static boolean resumeVideo() {
	  return cameraFrameBuffCtrl(VC0706_STEPFRAME);//  VC0706_RESUMEFRAME
	}
	
	static boolean cameraFrameBuffCtrl(byte command) {
		  byte[] args = {(byte) 0x1,(byte) command};
		  runCommand(VC0706_FBUF_CTRL, args, 20);
		  byte[] answer = null; 
		  boolean result = false;
	  	  while (!result) {
	  		  answer = camera.readSerial();
			  for (int i =0; i < (answer.length - 4); i++ ){
				  if ((answer[i] == (byte) 0x76) ||
				      (answer[i+1] == (byte) 0x00) ||
				      (answer[i+2] == (byte) VC0706_FBUF_CTRL) ||
				      (answer[i+3] == (byte) 0x00))
				  {
	//				  System.out.println("sgot fbuf result OK, i = " + i );
					  result = true;					 
					  break;
				  }
			  }
	  	  }
	  		return result;
		}

	static boolean getVersion(){
		byte[] args = new byte[] { (byte) 0x01,  (byte) 0x56,  (byte) 0x57 };  
		runCommand(VC0706_GEN_VERSION, args, 50);
		camera.sleep(10); // let the cam answer
		boolean result = false;
		@SuppressWarnings("unused")
		byte[] answer = camera.readSerial();
   
		return result;
 	}
	
	static boolean reset() {
	  byte[] args = {(byte) 0x00};

	  return runCommand(VC0706_RESET, args, 15);
	}

	static boolean setImageSize(byte x) {
	  byte[] args = new byte[]{(byte) 0x12, (byte) 0x09, (byte) 0x01,(byte) 0x00, (byte) 0x19,(byte) 0x11 };  
	  runCommand(VC0706_WRITE_DATA, args, 100);
	  camera.sleep(1000);
	  
	  boolean result = false; 
	    	while (!result) {
	    	try {
	    	byte[] answer = camera.readSerial();
			String fileString = new String(answer,"UTF-8");
//			System.out.println("set imagesize result en string" + fileString);
			result = fileString.contains("Init en");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    	}
	   
	    return result;
	  
}

	static int getImageSize() {
	  byte[] args = {(byte)0x40,(byte) 0x40, (byte)0x10,(byte) 0x00, (byte)0x19};
	  runCommand(VC0706_READ_DATA, args, 100);
	    
	  camera.sleep(100); // let the cam answer
	  byte[] answer = null;
	  boolean verify_response = false;
	  while (!verify_response) {
	    	answer = camera.readSerial();
//			String fileString = new String(answer,"UTF-8");
//			System.out.println("get image result en string" + fileString);			
	    	}
	  
	  return answer[5];
	}

	static boolean setCompression(byte c) {
	   byte[] arg = {(byte)0x05, (byte)0x01, (byte)0x01, (byte)0x12, (byte)0x04,(byte) c};
	  return runCommand(VC0706_WRITE_DATA, arg, 10);
	}

	static int getCompression() {
		 byte[] args = {(byte)0x4, (byte) 0x1, (byte) 0x1, (byte) 0x12, (byte) 0x04};
	  runCommand(VC0706_READ_DATA, args, 20);
	  byte[] answer = camera.readSerial();
	  return answer[5];
	}
	

	static boolean TVon() {
		byte[] args = {(byte) 0x1, (byte) 0x1};
			  return runCommand(VC0706_TVOUT_CTRL, args, 5);
			}
	
	static boolean TVoff() {
		byte[] args = {(byte) 0x1, (byte) 0x00};
			  return runCommand(VC0706_TVOUT_CTRL, args, 5);
			}


	
	public static byte[] readImageData(int jpglen, int buffersize){
	
		int wCount = 0; // For counting # of writes
		byte[] globalbuffer = new byte[0];
      
		while (jpglen > 0) {
 

			int bytesToRead = Math.min(buffersize, jpglen);
			byte[] buffer = readPicture(bytesToRead,buffersize);	
  
			if(buffer.length<buffersize) {   
	//			System.out.println("BUFFER LENTGH < "+ buffersize + " :" + buffer.length);
			}
				
			globalbuffer = concat(globalbuffer, buffer);
		
			jpglen -= bytesToRead;
			wCount++;
		}
    
//		System.out.println("done in " + wCount + " iteration");
		return globalbuffer;
}

	
	public static byte[] readPicture(int bytetoRead,int buffersize) {
	
		byte arg_buffer1=0;
		byte arg_buffer2=0;
		int sleeptime = 0; //based on tests at 38400 bauds
		switch (buffersize) 
		{ 
		case 32  : arg_buffer1=(byte) 0x00; arg_buffer2 = (byte) 0x20; sleeptime = 12; break; 
		case 64  : arg_buffer1=(byte) 0x00; arg_buffer2 = (byte) 0x40; sleeptime = 15; break; 
		case 96  : arg_buffer1=(byte) 0x00; arg_buffer2 = (byte) 0x60; sleeptime = 20; break; 
		case 128 : arg_buffer1=(byte) 0x00; arg_buffer2 = (byte) 0x80; sleeptime = 22; break; 
		case 256 : arg_buffer1=(byte) 0x01; arg_buffer2 = (byte) 0x00; sleeptime = 33; break; 
		case 512 : arg_buffer1=(byte) 0x02; arg_buffer2 = (byte) 0x00; sleeptime = 55; break; 
		case 1024 : arg_buffer1=(byte) 0x04; arg_buffer2 = (byte) 0x00; sleeptime = 100; break;
		case 2048 : arg_buffer1=(byte) 0x08; arg_buffer2 = (byte) 0x00; sleeptime = 200; break;
		case 4096 : arg_buffer1=(byte) 0x10; arg_buffer2 = (byte) 0x00; sleeptime = 390; break;
		case 8192 : arg_buffer1=(byte) 0x20; arg_buffer2 = (byte) 0x00; sleeptime = 480; break;
		}
	/* best result seen
	case 32  : sleeptime = 20@38400 (10,3 s/12k) 12@115200 (2,9 s/5k)
	case 64  : sleeptime = 26@38400 (7,4s/12k)
	case 96  : sleeptime = 45@38400 (6,6s/12k)
	case 128 : sleeptime = 45@38400 (5s/12k) 	22@115200 (1,1 s/5k)
	case 256 : sleeptime = 80@38400 (4,2s/12k) 	33@115200 (1,0 s/5k)
	case 512 : sleeptime = 140@38400 (3,6s/12k) 55@115200 (0,6 s/5k)
	case 1024 :sleeptime =  					100 @115200 (0,5 s/5k)
	case 2048 :sleeptime = 						200 @115200 (0,6 s/5k)
	case 4096 :sleeptime = 						390 @115200 (0,8 s/5k)
	case 8192 :sleeptime = 						480 @115200 (0,5 s/5k)
	*/
   
	 byte[] args  = {(byte) 0x0C, (byte) 0x00, (byte) 0x0A, // last was 0x0A
				(byte) 0x00, (byte) 0x00, (byte) (frameptr >> 8), (byte) (frameptr & 0xFF), 
				(byte) 0x00, (byte) 0x00, (byte) arg_buffer1, (byte) arg_buffer2, 
		                    (byte) 0x00, (byte) 0x06};
		runCommand(VC0706_READ_FBUF, args,  buffersize + 64); //data often starts at adress != 0

	//	  System.out.println("frameptr = : " + frameptr);
		  camera.sleep(sleeptime); // let the cam answer
		  boolean result = false; 
		  byte[] answer = null;
		  @SuppressWarnings("unused")
		int offset = 0, it=0;
	  	  while (!result) {
	  		// try {
//	  		System.out.println("iteration  " + it);
	  			  answer = camera.readSerial();
	  			offset = 0;
	  			  for (int i =0; i < (answer.length - 4); i++ ){
	  				  if ((answer[i] == (byte) 0x00) ||
		  				  (answer[i+1] == (byte) 0x00) ||  //looking for camera delay
	  					  (answer[i+2] == (byte) 0xFF) ||
		  				  (answer[i+3] == (byte) 0xD8))
	  				  {
//	  					  System.out.println("got FFD8, i = " + i );
	  					  offset = i;
	  //					  System.out.println("so, offset = " + offset );
	  					  result = true;
	  	//				  System.out.println("at the end i =  " + i );
	  					  break;
	  				  }
	  			  }
	  			it++;
	  	  }		  
		  
		  byte[] resultat = new byte[bytetoRead];
//		  System.out.println("result length " + resultat.length );
		  frameptr += bytetoRead;
		  for (int k=0;k<bytetoRead;k++){
			  resultat[k] = answer[k+5];
		  }
		  
		  return resultat;
	
		}
	
/**************** low level commands */
	static boolean runCommand(byte cmd, byte[] args, int resplen) {

sendCommand(cmd, args, resplen);

return true; //logic to be be implemented
}

static void sendCommand(byte cmd, byte[] args, int resplen) {
	
	if (resplen ==0) resplen =5;
	int args_len = args.length;
	byte[] tosend = new byte[3 + args_len];
	tosend[0] = (byte) 0x56;
	tosend[1] = (byte) 0x00;
	tosend[2] = cmd;
	System.arraycopy(args, 0, tosend, 3, args_len);
	
    camera.writebytes(tosend,resplen);  
}

static byte[] concat(byte[] A, byte[] B) {
	   int aLen = A.length;
	   int bLen = B.length;
	   byte[] C= new byte[aLen+bLen];
	   System.arraycopy(A, 0, C, 0, aLen);
	   System.arraycopy(B, 0, C, aLen, bLen);
	   return C;
	}

/************* constants ***********/
 static byte VC0706_RESET = 0x26;
 static byte VC0706_GEN_VERSION = 0x11;
 static byte VC0706_SET_BAUDRATE = 0x24;
 static byte VC0706_READ_FBUF = 0x32;
 static byte VC0706_GET_FBUF_LEN = 0x34;
 static byte VC0706_FBUF_CTRL = 0x36;
 static byte VC0706_DOWNSIZE_CTRL = 0x54;
 static byte VC0706_DOWNSIZE_STATUS = 0x55;
 static byte VC0706_READ_DATA = 0x30;
 static byte VC0706_WRITE_DATA = 0x31;
 static byte VC0706_COMM_MOTION_CTRL = 0x37;
 static byte VC0706_COMM_MOTION_STATUS = 0x38;
 static byte VC0706_COMM_MOTION_DETECTED = 0x39;
 static byte VC0706_MOTION_CTRL = 0x42;
 static byte VC0706_MOTION_STATUS = 0x43;
 static byte VC0706_TVOUT_CTRL = 0x44;
 static byte VC0706_OSD_ADD_CHAR = 0x45;

 static byte VC0706_STOPCURRENTFRAME = 0x0;
 static byte VC0706_STOPNEXTFRAME = 0x1;
 static byte VC0706_RESUMEFRAME = 0x3;
 static byte VC0706_STEPFRAME = 0x2;

 static byte VC0706_640x480 = 0x00;
 static byte VC0706_320x240 = 0x11;
 static byte VC0706_160x120 = 0x22;

 static byte VC0706_MOTIONCONTROL = 0x0;
 static byte VC0706_UARTMOTION = 0x01;
 static byte VC0706_ACTIVATEMOTION = 0x01;

 static byte VC0706_SET_ZOOM = 0x52;
 static byte VC0706_GET_ZOOM = 0x53;

 static int CAMERABUFFSIZ = 100;
 static int CAMERADELAY = 10;
}


