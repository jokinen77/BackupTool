package com.mycompany.backuptool;

import com.mycompany.backuptool.configures.BackupConfigure;
import com.mycompany.backuptool.configures.BackupConfigureLoader;
import com.mycompany.backuptool.handler.BackupHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) throws Exception {
        List<String> instructions = readInstructionsFileLines();
        if (args.length < 1) {
            StringBuilder usage = new StringBuilder();
            for (String instruction : instructions) {
                usage.append("\n" + instruction);
            }
            System.out.println(usage.toString());
            return;
        }
        
        boolean askConfirm = true;
        String confFile = args[0];
        
        for (String arg : args) {
            if (arg.equalsIgnoreCase("force")) {
                askConfirm = false;
            }
        }
        
        System.out.println("Using configuration file: " + confFile);
        
        BackupConfigure conf = BackupConfigureLoader.loadConfigures(confFile);
        conf.printToLog();

        if (askConfirm) {
            Scanner scanner = new Scanner(System.in);
            System.out.print(askConfirm ? "Do you want to start the backup process? (y/n):\n> " : "");
            if (!scanner.nextLine().toLowerCase().matches("[y]|yes")) {
                System.out.println("Backup aborted!");
                return;
            }
            BackupHandler handler = new BackupHandler(conf);
            handler.backupFiles();
        } else {
            System.out.println("Found 'force' argument. Asking confirmation skipped.");
            BackupHandler handler = new BackupHandler(conf);
            handler.backupFiles();
        }
    }
    
    public static List<String> readInstructionsFileLines() throws IOException, ClassNotFoundException {
        ArrayList<String> lines = new ArrayList<>();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("usage.txt");
        InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        for (String line; (line = reader.readLine()) != null;) {
            lines.add(line);
        }
        return lines;
    }

}
