# Java-Server-Client-Gui
A simple chat program that uses a java swing GUI. This zip file contains the server: PersonServerMulti.java and the client: NetworkJFrame.java.

To run the program first run PersonServerMulti.java and enter into the prompt the intended port # for hosting.
After a port # is entered the server will print out it's ip address.

Next run NetworkJFrame.java(the client). It will prompt you to enter in the port #. 
After entering in the correct port # It will prompt you: "Y for local N for external: "
Enter "Y" if you're running the client on the same machine, "N" if you're not.
If you enter N it will prompt you for the IP address. Enter in the IP address that was printed by the server.

Now a java swing GUI will appear. First type in your name into the input bar and hit send. This will add you to the list of users. You can 
select a user in the list of users and press select. This will send whatever text you've entered into the input bar to that user as a "secret". To disconnect the client simply exit the GUI.

WARNING: This was my first coding project. As a result of that the actual code is messy and unpleasant. I would recommend not looking past the surface of this thing. I'm planning on refactoring the whole thing at some point.
