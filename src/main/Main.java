/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Ivo
 */
public class Main {

    private static String jarRoot;
    private static List<Webhook> hooks;
    private static Window window = null;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        hooks = new ArrayList<>();
        jarRoot = "";
        try {
            jarRoot = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            if(!jarRoot.endsWith("/")) jarRoot += "/";
        } catch (URISyntaxException ex) {
            ex.printStackTrace();   
            jarRoot = "";
        }
       // Webhook webhook = new Webhook("Aynueg6J");
       // webhook.setDispatchTo("http://localhost:8080/bestbit-platform/api/ps/notificacao-gerencianet");
       // webhook.connect();
        //hooks.add(webhook);
        createTrayIcon();
        openWindow();
    }
    
    public static void openWindow(){

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        if(window == null){
            window = new Window();
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> window.setVisible(true));
    }
    
    private static TrayIcon trayIcon = null;
    private static void createTrayIcon(){
        if(!SystemTray.isSupported()) return;
        final PopupMenu popup = new PopupMenu();
        
        trayIcon =
                new TrayIcon(getIconImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        final SystemTray tray = SystemTray.getSystemTray();
       
        MenuItem openWindowItem = new MenuItem("Open window");
        openWindowItem.addActionListener((ev) -> openWindow());
        MenuItem stopItem = new MenuItem("Stop Webhook Inbox -> localhost");
        stopItem.addActionListener((e) -> System.exit(0));
        popup.add(openWindowItem);
        popup.addSeparator();
        popup.add(stopItem);
        trayIcon.setPopupMenu(popup);
        trayIcon.setToolTip("Webhook Inbox -> localhost is running.");
        
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
        }
        
    }
    
    public static Image getIconImage(){
        
        System.out.println(jarRoot);
        return Toolkit.getDefaultToolkit().getImage(jarRoot+"tray-icon.png");
    }

    public static List<Webhook> getHooks() {
        return hooks;
    }
    
    public static Webhook getHook(int idx){
        if(idx < 0 || idx >= hooks.size()) return new Webhook(true);
        return hooks.get(idx);
    }
    
}
