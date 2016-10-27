/*
 * Cameron Massey 2/16/2016 Java GUI chat room client application
 * 
 */
import java.io.*;
import java.util.Scanner;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Cameron
 */
 //read from client
class getInput implements Runnable {

    private Socket server;
    private String line;
    private BlockingQueue<String> queue;

    getInput(Socket server, BlockingQueue<String> queue) {
        this.server = server;
        this.queue = queue;
    }

    public void run() {

        try {
            // Get input from the client
            BufferedReader d = new BufferedReader(new InputStreamReader(server.getInputStream()));

            while ((line = d.readLine()) != null) {
                String s = line;

                try {
                    queue.put(s);
                } catch (InterruptedException ex) {
                    Logger.getLogger(getInput.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            server.close();
        } catch (IOException ioe) {
            System.out.println("IOException on socket listen: " + ioe);
        }
    }
}

public class NetworkJFrame extends javax.swing.JFrame {

    /**
     * Creates new form NetworkJFrame
     */
    public NetworkJFrame() {
        initComponents();
        Thread t1;
        Thread t2;
        String yorN = null;
            
        try {

            System.out.print("Enter port #: ");
            Scanner sc = new Scanner(System.in);
            int in = sc.nextInt();
            
            System.out.print("Y for local N for external: ");
            sc.nextLine();
            yorN = sc.nextLine();

            
            if(yorN.equals("Y")){
               host = InetAddress.getLocalHost();
            }
            else{
               System.out.print("Enter ip address: ");
               host = InetAddress.getByName(sc.nextLine());
            }  
            

            
            
            //change the value host below to the ip of the server computer, currently it is set for using on the same computer
            server = new Socket(host, in);//host instead of ip for local
            System.out.println(server);
            
            getInput input = new getInput(server, queue);
            updateText upT = new updateText();

            t1 = new Thread(input);
            t2 = new Thread(upT);
            
            
            //I know its unsafe to make threads in a constructor but I can't figure out a different way to do it
            t1.start();
            t2.start();

        } catch (IOException ioe) {
            System.out.println("IOException on socket listen: " + ioe);
        }
        
    }
    
    private void InputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InputButtonActionPerformed
        PrintStream out2 = null;
        try {

            // TODO add your handling code here:
            String str;
            str = InputTextArea.getText();
            InputTextArea.setText("");
            out2 = new PrintStream(server.getOutputStream());
            out2.println("*main" +str);
        } catch (IOException ex) {
            Logger.getLogger(NetworkJFrame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }


    }//GEN-LAST:event_InputButtonActionPerformed

    private void MainTextAreaComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_MainTextAreaComponentHidden

    }//GEN-LAST:event_MainTextAreaComponentHidden

    private void UsersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UsersButtonActionPerformed
        // TODO add your handling code here:
                PrintStream out2 = null;
        try {

            // TODO add your handling code here:
            String str;
            
            List<String> list = jList1.getSelectedValuesList();
            out2 = new PrintStream(server.getOutputStream());
            ListIterator<String> it = list.listIterator();
            while(it.hasNext()){
               String s = it.next();
               out2.println(s + InputTextArea.getText());

            }
            InputTextArea.setText("");
            
        } catch (IOException ex) {
            Logger.getLogger(NetworkJFrame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }
    }//GEN-LAST:event_UsersButtonActionPerformed

    class updateText implements Runnable {

        //this checks the incoming string from the server, the first part of the string is a code that identifies what should be done with the rest of the string
        //*main puts the  on the main view
        //*personSet adds the name to the list of users as long as that user doesn't already exist
        //*removePerson removes a user
        //Else it puts the string on the main view.
         @Override
         public void run(){  
            while (true) {
                String str;
                while ((str = queue.poll()) != null) {
                    
                    if(str.length() > 4 && str.substring(0, 5).equals("*main")){
                        MainTextArea.append(str.substring(5) + "\n");
                    }
                    else if(str.length() > 9 && str.substring(0, 10).equals("*personSet")){
                        
                        if(!array.contains(str.substring(10))){
                            array.add(str.substring(10));
                        }
                        
                        
                        DefaultListModel model = new DefaultListModel();                        
                        for(String s : array){
                            model.addElement(s);
                        }
                        jList1.setModel(model);
                        
                        
                        
                    }
                    else if(str.length() > 12 && str.substring(0,13).equals("*removePerson")){
                        array.remove(str.substring(13));
                        
                        DefaultListModel model = new DefaultListModel();                        
                        for(String s : array){
                            model.addElement(s);
                        }
                        jList1.setModel(model);
                        
                        
                        
                    }
                    else{
                        MainTextArea.append(str + "\n");
                    }
                    

                }

            }
         }  
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NetworkJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NetworkJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NetworkJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NetworkJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new NetworkJFrame().setVisible(true);

            }
        });
        
        

    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jDialog2 = new javax.swing.JDialog();
        jScrollPane1 = new javax.swing.JScrollPane();
        MainTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        InputTextArea = new javax.swing.JTextArea();
        UsersLabel = new javax.swing.JLabel();
        InputLabel = new javax.swing.JLabel();
        UsersButton = new javax.swing.JButton();
        InputButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        MainTextArea.setColumns(20);
        MainTextArea.setRows(5);
        MainTextArea.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                MainTextAreaComponentHidden(evt);
            }
        });
        jScrollPane1.setViewportView(MainTextArea);

        InputTextArea.setColumns(20);
        InputTextArea.setRows(5);
        jScrollPane2.setViewportView(InputTextArea);

        UsersLabel.setText("Users");

        InputLabel.setText("Input");

        UsersButton.setText("Select");
        UsersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UsersButtonActionPerformed(evt);
            }
        });

        InputButton.setText("Send");
        InputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InputButtonActionPerformed(evt);
            }
        });

        jList1.setModel(new javax.swing.DefaultListModel<String>() {
            String[] strings = new String[10];
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(UsersLabel)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(InputLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(InputButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UsersButton))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jScrollPane1, jScrollPane2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(UsersLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(InputLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(InputButton, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UsersButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

        
    private ArrayList<String> array = new ArrayList<>();
    private InetAddress host;
    private static Socket server;
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton InputButton;
    private javax.swing.JLabel InputLabel;
    private javax.swing.JTextArea InputTextArea;
    private javax.swing.JTextArea MainTextArea;
    private javax.swing.JButton UsersButton;
    private javax.swing.JLabel UsersLabel;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables

}




