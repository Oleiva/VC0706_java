import gnu.io.SerialPort;


public class stream_cam {

//Communicator object
//Communicator communicator = null;
 static Communicator camera = new Communicator();

public static void main(String args[]) {
	String port = "COM3"; 
/*	if (args[1] != null) {	port = args[1];}
	else {port ="COM3" ;}
	*/
	camera.searchForPorts(); // if removed the port number below is unknown on the function "connect"...
	
	camera.connect("COM3",38400);
	
	System.out.println("connected" + camera.getConnected());
      if (camera.getConnected() == true)
      { System.out.println("connected");
          if (camera.initIOStream() == true)
          { System.out.println("init io");
        	  camera.initListener();
        	  System.out.println("listening");
          }
      }
      try {
		Thread.sleep(200);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     byte[] data = new byte[] { (byte) 0x56,  (byte) 0x00, VC0706_GEN_VERSION,  (byte) 0x01,  (byte) 0x56,  (byte) 0x57 };
   //   byte[] data = new byte[] { (byte) 0x4D, (byte) 0x4D};
      camera.writebytes(data);
 
   
     
}
//constants
 static byte VC0706_RESET = 0x26;
 static byte VC0706_GEN_VERSION = 0x11;
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

 int CAMERABUFFSIZ = 100;
 int CAMERADELAY = 10;


}