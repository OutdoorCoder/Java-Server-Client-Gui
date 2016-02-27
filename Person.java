

import java.io.*;
import java.net.*;

public class Person{


   private String name;
   private Socket socket;
   private Boolean isConnected;
   private PrintStream outStream;
   
   public Person(Socket socket, PrintStream stream ){
      
      this.isConnected = true;
      this.socket = socket;
      this.outStream = stream;
   }

    public PrintStream getOutStream() {
        return outStream;
    }

    public void setOutStream(PrintStream outStream) {
        this.outStream = outStream;
    }
    
    public void printOut(String input){
        outStream.println(input);
    }


   public void setSocket(Socket socket){ this.socket = socket;}
   
   public Socket getSocket(){ return this.socket;}
   
   public void setName(String name){ this.name = name;}
   
   public String getName(){ return this.name;}
   
   public boolean getIsConnected(){return this.isConnected;}
   
   public void setIsConnected(Boolean bool){ this.isConnected = bool;}
   
   public void closeOutstream(){ this.outStream.close();}

   


}