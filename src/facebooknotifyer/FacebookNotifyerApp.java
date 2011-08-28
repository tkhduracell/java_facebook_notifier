package facebooknotifyer;

import java.awt.SplashScreen;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @see the sees
 * @author Filip
 */
public class FacebookNotifyerApp {
    
    private static final File saveFile = new File("data.dat");

    public static void main(String[] args) throws InterruptedException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        Logger.getLogger(FacebookChecker.class.getName()).log(Level.FINE, "Initializing...");
        ArrayList<FacebookAccount> list = new ArrayList<FacebookAccount>();
        Logger.getLogger(FacebookChecker.class.getName()).log(Level.FINE, "Loading accounts");
        fetchAccounts(list);
        Logger.getLogger(FacebookChecker.class.getName()).log(Level.FINE, "Accounts loaded");
        final FacebookChecker checker = new FacebookChecker(list);

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable(){
            public void run() {
                Logger.getLogger(FacebookChecker.class.getName()).log(Level.FINE, "Initializing GUI class...");
                new MainDialog(checker).setVisible(false);
                if(SplashScreen.getSplashScreen()!=null){
                    SplashScreen.getSplashScreen().close();
                }
            }
        });
        
    }
    
    public static boolean fetchAccounts(List<FacebookAccount> list){
        ObjectInputStream ois = null;
        boolean add = false;
        try {
            ois = new ObjectInputStream(new FileInputStream(FacebookNotifyerApp.saveFile));
            while(true){
                FacebookAccount fa = (FacebookAccount)ois.readObject();
                fa.setLastUpdated(null);
                list.add(fa);
                add = true;
            }
        } catch (EOFException eofex) {
            
        } catch(Exception ex) {
            Logger.getLogger(FacebookNotifyerApp.class.getName()).log(Level.FINE, "Error loading older accounts", ex);       
        } finally {
            try {
                ois.close();
            } catch (Exception ex) {
                Logger.getLogger(FacebookNotifyerApp.class.getName()).log(Level.FINE, "Error closing stream", ex);
            }
        }
        
        return add;
    }

    public static void saveSettings(ArrayList<FacebookAccount> accounts){
        ObjectOutputStream oos = null;  
        try {
            oos = new ObjectOutputStream(new FileOutputStream(saveFile));
        } catch (IOException ex) {
            Logger.getLogger(FacebookNotifyerApp.class.getName()).log(Level.FINE, "Couldn't open outputstream to "+saveFile.getName(), ex);
        }
        for(FacebookAccount fa : accounts){
            try {
                oos.writeObject(fa);
            } catch (IOException ex) {
                Logger.getLogger(FacebookNotifyerApp.class.getName()).log(Level.FINE, "Error saving accounts", ex);
            }
        }
        try {
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(FacebookNotifyerApp.class.getName()).log(Level.FINE, "Close stream fail", ex);
        }
    }
    
    
}
