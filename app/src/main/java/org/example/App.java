package org.example;

import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class App {
    public static void main(String[] args) {

        // Shared map for storing logs for each command
        ConcurrentMap<String, StringBuilder> logs = new ConcurrentHashMap<>();

        // Commands and their runnable actions
        Map<String, Runnable> commands = new HashMap<>();

        commands.put("help", () -> {
            System.out.println("Available commands:");
            System.out.println("  help (h) - Show this help message");
            System.out.println("  exit (e) - Exit the application");
            System.out.println("  logs <command> - View logs for specific command");
            System.out.println("  <command> & - Run a command in the background");
            System.out.println("  test1 <&> - Simulate a long-running command (5 seconds) in the background");
            System.out.println("  test2 <&> - Simulate a long-running command (20 seconds) in the background");
        });
        commands.put("h", commands.get("help"));

        commands.put("exit", () -> {
            System.out.println("Exiting the application.");
            System.exit(0);
        });
        commands.put("e", commands.get("exit"));

        // Test1 command (5 seconds sleep)
        commands.put("test1", () -> {
            String command = "test1";
            logs.put(command, new StringBuilder());
            try {
                Thread.sleep(5000);
                logs.get(command).append("test1 command complete after 5 seconds.\n");
            } catch (InterruptedException e) {
                logs.get(command).append("test1 command was interrupted.\n");
            }
        });

        // Test2 command (20 seconds sleep)
        commands.put("test2", () -> {
            String command = "test2";
            logs.put(command, new StringBuilder());
            try {
                Thread.sleep(20000);
                logs.get(command).append("test2 command complete after 20 seconds.\n");
            } catch (InterruptedException e) {
                logs.get(command).append("test2 command was interrupted.\n");
            }
        });

        // Interactive loop
        Scanner scanner = new Scanner(System.in);
        System.out.println("Application started. Type 'help' for commands or 'exit' to quit.");

        // Loop to accept user input
        while (true) {
            System.out.print("cli> ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.isEmpty()) {
                continue;
            }

            if (input.endsWith("&")) {
                String command = input.substring(0, input.length() - 1).trim();
                if (command.isEmpty()) {
                    System.out.println("Error: No command provided to run in the background.");
                } else {
                    if (commands.containsKey(command)) {
                        new Thread(commands.get(command)).start();
                        System.out.println("Command '" + command + "' is now running in the background.");
                    } else {
                        System.out.println("Unrecognized command: " + command);
                    }
                }
            } else if (input.startsWith("logs ")) {
                String command = input.substring("logs ".length()).trim();

                if (logs.containsKey(command)) {
                    System.out.println("Logs for command '" + command + "':");
                    System.out.println(logs.get(command).toString());
                } else {
                    System.out.println("No logs found for command: " + command);
                }
            } else {
                if (commands.containsKey(input)) {
                    commands.get(input).run();
                } else {
                    System.out.println("Unrecognized command: " + input);
                    System.out.println("Type 'help' to see the list of available commands.");
                }
            }
        }
    }
}
