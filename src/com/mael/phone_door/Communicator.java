/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mael.phone_door;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeUnit;

public class Communicator implements SerialPortEventListener
{

    //for containing the ports that will be found
    @SuppressWarnings("rawtypes")
	private Enumeration ports = null;
    //map the port names to CommPortIdentifiers
    @SuppressWarnings("rawtypes")
	private HashMap portMap = new HashMap();

    //this is the object that contains the opened port
    private CommPortIdentifier selectedPortIdentifier = null;
    private SerialPort serialPort = null;

    //input and output streams for sending and receiving data, buffer for reading
    private InputStream input = null;
    private OutputStream output = null;
    private byte[] readBuffer = new byte[10240]; //10k buffer with 8k effective
    private int buffersize = 10240; //10k buffer with 8k effective
    private int HW_inputbuffer = 16384; //16k HW buffer, used for Beaglebone Black, set it lower depending your use
    private int buffertoread = 0;
    private BufferedInputStream is;
    private BufferedReader reader;

    //just a boolean flag that i use for enabling
    //and disabling buttons depending on whether the program
    //is connected to a serial port or not
    private boolean bConnected = false;

    //the timeout value for connecting with the port
    final static int TIMEOUT = 2000;

    //some ascii values for for certain things
    final static int SPACE_ASCII = 32;
    final static int DASH_ASCII = 45;
    final static int NEW_LINE_ASCII = 10;

    //a string for recording what goes on in the program
    //this string is written to the GUI
    String logText = "";

    // add constructor without GUI
    public Communicator()
    {
    }
    //search for all the serial ports
    //pre: none
    //post: adds all the found ports to a combo box on the GUI
    @SuppressWarnings("unchecked")
	public void searchForPorts()
    {
        ports = CommPortIdentifier.getPortIdentifiers();

        while (ports.hasMoreElements())
        {
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();

            //get only serial ports
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
       //         window.cboxPorts.addItem(curPort.getName());
                portMap.put(curPort.getName(), curPort);
            }
        }
    }

    // adding connection function with COM port as a parameter and settings
    //connect to the selected port in the combo box
    //pre: ports are already found by using the searchForPorts method
    //post: the connected comm port is stored in commPort, otherwise,
    //an exception is generated
    public void connect(String port,int baudrate)
    { 
    	 
        String selectedPort = port;
        selectedPortIdentifier = (CommPortIdentifier)portMap.get(selectedPort);
     //   System.out.println("selectedPortIdentifier : " + selectedPortIdentifier); 
//      update connect function
        
        CommPort commPort = null;

        try
        {
            //the method below returns an object of type CommPort
            commPort = selectedPortIdentifier.open("COM", TIMEOUT);
            //the CommPort object can be casted to a SerialPort object
            serialPort = (SerialPort)commPort;
            serialPort.setSerialPortParams(baudrate,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
            serialPort.setInputBufferSize(buffersize);
            //for controlling GUI elements
            setConnected(true);
            //logging
            logText = selectedPort + " opened successfully.";
        }
        catch (PortInUseException e)
        {
            logText = selectedPort + " is in use. (" + e.toString() + ")";            
        }
        catch (Exception e)
        {
            logText = "Failed to open " + selectedPort + "(" + e.toString() + ")";
        }
    }
    
    //open the input and output streams
    public boolean initIOStream()
    {
        //return value for whather opening the streams is successful or not
        boolean successful = false;
        try {
            //
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
            is = new BufferedInputStream(is);
            reader = new BufferedReader(new InputStreamReader(is));
            serialPort.setInputBufferSize( HW_inputbuffer ); 
            successful = true;
            return successful;
        }
        catch (IOException e) {
            logText = "I/O Streams failed to open. (" + e.toString() + ")";
   
            return successful;
        }
    }

    //starts the event listener that knows whenever data is available to be read
    //pre: an open serial port
    //post: an event listener for the serial port that knows when data is recieved
    public void initListener()
    {
        try
        {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true); //disable event listener
            
        }
        catch (TooManyListenersException e)
        {
            logText = "Too many listeners. (" + e.toString() + ")";
        }
        
    }

    //disconnect the serial port
    public void disconnect()
    {
        try
        {
            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            is.close();
            reader.close();
            setConnected(false);
    
            logText = "Disconnected.";
        }
        catch (Exception e)
        {
            logText = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
       }
    }

    final public boolean getConnected()
    {
        return bConnected;
    }

    public void setConnected(boolean bConnected)
    {
        this.bConnected = bConnected;
    }

    //what happens when data is received
    //pre: serial event is triggered
    //post: processing on the data it reads
    public void serialEvent(SerialPortEvent evt) {
        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
        //	readSerial();
        }
   
    }

    //method that can be called to send data
    //pre: open serial port
    //post: data sent to the other device
    public void writeData(int leftThrottle, int rightThrottle)
    {
        try
        {
            output.write(leftThrottle);
            output.flush();
            //this is a delimiter for the data
            output.write(DASH_ASCII);
            output.flush();
            
            output.write(rightThrottle);
            output.flush();
            //will be read as a byte so it is a space key
            output.write(SPACE_ASCII);
            output.flush();
        }
        catch (Exception e)
        {
            logText = "Failed to write data. (" + e.toString() + ")";

        }
    }

    // add byte sending capacity
    //method that can be called to send data
    public void writebytes(byte[] data,int returnlentgh)
    {
    	buffertoread = returnlentgh;
        try
        {
        	for (int i=0; i < data.length; i++){
        		output.write(data[i]);
        		output.flush();
//        		System.out.println("sent step " + i);
        	}	
        }
        catch (Exception e)
        {
            logText = "Failed to write data. (" + e.toString() + ")";

        }
    }
 
    @SuppressWarnings("resource")

    String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
      
    byte[] readSerial() {
    	
        		try {
        	//sleep(2); // let the time to the camera to answer, otherwise the  buffer will be field with bad data
        	waitForBytes(buffertoread);
            
                // Read the serial port
                input.read(readBuffer, 0, buffertoread);
                
                // Print it out
      //          System.out.println("in read function                     " + new String(readBuffer, 0, buffertoread));
                return readBuffer;
            
        } catch (IOException e) {
        }
        return readBuffer;
    }
    
    private void waitForBytes(int numBytes) {
    	try {
			while (( is.available()) <  buffertoread) sleep(1);
		} catch (IOException e) {
	//		System.out.println("waitForBytes error " + e);;
		}
    }
    
    public int available() throws Throwable  {
       

    	try {
            return is.available();
        } catch (IOException e) {
            throw new Exception(e);
        }
    }
    
    public void sleep(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
        	System.out.println("Sleep interrupted " + e);
        }
    }
       
}
