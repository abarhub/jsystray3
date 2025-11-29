package org.jsystray;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        if (!SystemTray.isSupported()) {
            System.err.println("System tray is not supported!");
            return;
        }

        try {
            // Load properties from resources
            Map<String, String> props = loadProperties(args);

            // Load tray icon image
            Image image = Toolkit.getDefaultToolkit().getImage(Main.class.getClassLoader().getResource("tray_icon.png"));

            // Create popup menu
            PopupMenu popup = new PopupMenu();

            for (String key : props.keySet()) {
                if(key.startsWith("separator")){
                    popup.addSeparator();
                } else {
                    String command = props.get(key);
                    MenuItem item = new MenuItem(key);
                    item.addActionListener((ActionEvent e) -> runCommand(command));
                    popup.add(item);
                }
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

    private static Map<String, String> loadProperties(String[] args) throws IOException {
        if (args.length > 1 && Objects.equals(args[0], "--config")) {
            String configFile = args[1];
            try (InputStream input = Files.newInputStream(Path.of(configFile))) {
                return loadProperties(input);
            }
        }

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("commands.properties")) {
            return loadProperties(input);
        }
    }

    private static void runCommand(String command) {
        try {
            String[] tab = SplitUtils.split(command);
            Runtime.getRuntime().exec(tab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> loadProperties(InputStream input) {
        var buffer = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8));
        try (var lines = buffer.lines()) {
            return lines
                    .filter(x -> x != null && !x.isBlank())
                    .filter(x -> !x.trim().startsWith("#"))
                    .map(Main::split)
                    .filter(x -> x.size() == 2)
                    .collect(Collectors.toMap(List::getFirst,
                            List::getLast,
                            (e1, e2) -> {
                                throw new RuntimeException("Same key : " + e1);
                            },
                            LinkedHashMap::new));
        }
    }

    private static List<String> split(String s) {
        if (s == null || !s.contains("=")) {
            return List.of();
        }
        var pos = s.indexOf("=");
        if (pos == -1) {
            return List.of();
        }
        return List.of(s.substring(0, pos), s.substring(pos + 1));
    }
}
