package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try(Socket socket = new Socket("localhost",5000)){
            BufferedReader echoes = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter stringToEcho = new PrintWriter(socket.getOutputStream(),true);

            Scanner scanner = new Scanner(System.in);
            String response;
            String echoString = "";

            do{
                String info = "\n" + "1. Song Details" + "\n" +
                        "2. Albums by the Artist" + "\n" +
                        "3. Songs in the Album" + "\n" +
                        "4. Exit the Server" + "\n";
                System.out.println(info);
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice){
                    case 1:
                        stringToEcho.println("QuerySong");
                        System.out.println("Enter song name");
                        echoString = scanner.nextLine();
                        stringToEcho.println(echoString);
                        break;
                    case 2:
                        stringToEcho.println("QueryArtistAlbum");
                        System.out.println("Enter Artist name: ");
                        echoString = scanner.nextLine();
                        stringToEcho.println(echoString);
                        break;
                    case 3:
                        stringToEcho.println("QueryAlbumSongs");
                        System.out.println("Enter Album Name: ");
                        echoString = scanner.nextLine();
                        stringToEcho.println(echoString);
                        break;
                    case 4:
                        echoString = "exit";
                        stringToEcho.println(echoString);
                        break;
                }

                if (!echoString.equals("exit")){
                    while(!(response = echoes.readLine()).equals("\0")){
                        System.out.println(response);
                    }
                }
            }while (!echoString.equals("exit"));
        } catch (SocketTimeoutException e){
            System.out.println("Socket timed out");
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }
}

