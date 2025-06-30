package org.jsystray;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        if (!SystemTray.isSupported()) {
            System.err.println("System tray is not supported!");
            return;
        }

        try {
            // Load properties from resources
            Properties props = new Properties();
            boolean configTrouve = false;
            if (args.length > 1 && Objects.equals(args[0], "--config")) {
                String configFile = args[1];
                try (InputStream input = Files.newInputStream(Path.of(configFile))) {
                    props.load(input);
                    configTrouve = true;
                }
            }

            if (!configTrouve) {
                try (InputStream input = Main.class.getClassLoader().getResourceAsStream("commands.properties")) {
                    props.load(input);
                }
            }

            // Load tray icon image
            Image image = Toolkit.getDefaultToolkit().getImage(Main.class.getClassLoader().getResource("tray_icon.png"));

            // Create popup menu
            PopupMenu popup = new PopupMenu();

            for (String key : props.stringPropertyNames()) {
                String command = props.getProperty(key);
                MenuItem item = new MenuItem(key);
                item.addActionListener((ActionEvent e) -> runCommand(command));
                popup.add(item);
            }

            // Exit menu item
            popup.addSeparator();
            MenuItem exitItem = new MenuItem("Quit");
            exitItem.addActionListener(e -> System.exit(0));
            popup.add(exitItem);

            // Create TrayIcon and add to SystemTray
            TrayIcon trayIcon = new TrayIcon(image, "Tray App", popup);
            trayIcon.setImageAutoSize(true);
            SystemTray.getSystemTray().add(trayIcon);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runCommand(String command) {
        try {
            String[] tab=command.split(" ");
            Runtime.getRuntime().exec(tab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
