
/*
 * Cameron Massey 2/16/2016 Java GUI chat room server application
 * 
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;


 public class PersonServerMulti {


   private static ArrayList<Person> array = new ArrayList<>();
      
   public static ArrayList<Person> getArray(){
       return array;
   }

   // Listen for incoming connections and handle them
   public static void main(String[] args) {
     int i=0;
      
     	
     
    try{

          System.out.print("Enter port #: ");
          Scanner sc = new Scanner(System.in);
          int in = sc.nextInt();     
          
          ServerSocket listener = new ServerSocket(in);
          InetAddress IP=InetAddress.getLocalHost();
          System.out.println(IP.getHostAddress());
          Socket client;
          
          while(true){
          
            try{
               Thread.sleep(0);//don't remember why you need this but you do
            }
            catch (Exception e)
            {e.printStackTrace();}   
                   
            //limits the number of users to 10       
            while(array.size() < 10){
                        
                   client = listener.accept();
                   Person p = new Person(client, new PrintStream(client.getOutputStream()));

                   array.add(p);
                    	
                   doListen listen= new doListen(p);
                   
                   Thread t = new Thread(listen);
                   
                   t.start();
            }    //end while
         }//end while
      }//end try 
      catch (IOException ioe) {
         System.out.println("IOException on socket listen: " + ioe);
         ioe.printStackTrace();
      }//end catch
   }
   
   public static int getEmpty(Person [] array2){
   
      int i = 0;
      for(Person x : array2){
         if(x == null){
            return i;
         }
         i++; 
      }
      
      return 500;
   }
   
   
   public static boolean hasEmpty(Person [] array2){
   
      boolean x = false;
      
      for(Person p: array2){
         if(p == null)
            return true;
      }
      
      return x;
   
   }//end hasEmpty
   
   public static void writeTo(String input, Person p1){
		
   	try{
         
         
         Person p = null;
   		for(Person x : array)
                {
                     
                        //all messages have a "code" attached to the front of the string which decides what action to do based on that string. Ex: *removePerson, *main, *personset
                        if(x != null && x.getIsConnected() == true)
                        {
                            //if the code is in the form of: 'name of intended user' + 'message'
                            if(input.length() > 0 && input.substring(0, x.getName().length()).equals(x.getName())){
                              
                              x.printOut("Secret from " + p1.getName() + ": " + input.substring(x.getName().length()));
                            }
                            
                            
                            if(input.length() > 4 && input.substring(0,5).equals("*main") && p1.getName() != null){
                              x.printOut(input.substring(0,5) + p1.getName() + ": " + input.substring(5));
                            }
                                                                                               
                            for(Person y : array){
                              if(y != null && y.getIsConnected() == false){
                                 x.printOut("*removePerson" + y.getName());  
                              }
                              
                              if(y != null && y.getName() != null && y.getIsConnected() == true){
                                 x.printOut("*personSet" + y.getName());
                              }    
                            }//end inner for loop
                        }
                        
                        if(x != null && x.getIsConnected() == false){
                           p = x;
                        }
                     
                        
   		}//end for loop
         
         
         if(p != null){
            array.remove(p);
   	      array.trimToSize();
         }
   	}catch (Exception e) {
            System.out.println("Exception " + e + " Here!");
            }
   }


 
 
 
   
 }
//read from client
 class doListen implements Runnable {
   private Socket client;
   private String line,input;
   private Person p1;
   

   doListen(Person p1 ) {
      
      this.p1 = p1;
      this.client = p1.getSocket();
   	System.out.println(client);
   }

     public void run () {

       input="";

	
	
       try {        
         
           BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
         
         //sets name if not already set
         if(p1.getName() == null){
            
            Boolean b = false;
                                 
            do{
               String str = "Please enter a unique name: ";
               p1.printOut(str);
               input = reader.readLine().substring(5);
               
               b = check(input,PersonServerMulti.getArray());
            }while(b == false);
            
            p1.setName(input);

            
         }
         PersonServerMulti.writeTo("", p1);

	         
          //gets client input and prints it
          while((line = reader.readLine()) != null) {
           	input= line;
		      System.out.println("I got:" + input);
	   	   PersonServerMulti.writeTo(input, p1);	
        
         
      }
       } catch (Exception e) {
         //if someone disconnects set that Persons isConnected value to false
         System.out.println("Disconnected " + p1.getName());
         p1.setIsConnected(false);
         p1.closeOutstream();
         
         //have to send a message so the list updates after disconnection.
         PersonServerMulti.writeTo("", p1);
         
       }
     }//end run
     

     
     public  Boolean check(String name, ArrayList<Person> array){
         
         boolean uniqueName = true;
         
         for(Person p : array){
            if(p != null){
               if(name.equals(p.getName())){
                  uniqueName = false;
               }
            }
         }
         
      return uniqueName;
   }
}
