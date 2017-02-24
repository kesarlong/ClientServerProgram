/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cntclientprogram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Billy
 */
public class ClientSideThread extends Thread {

    private String serverCommand;
    private String host;
    private long startingTime;
    private long timeElapsed;
    private long totalTime;
    private int port;

    public ClientSideThread(String host, int port, String serverCommand) {
        this.host = host;
        this.port = port;
        this.serverCommand = serverCommand;
        this.timeElapsed = 0;
        this.totalTime = 0;

    }

    public String getServerCommand() {
        return this.serverCommand;
    }

    public double getElaspedTime() {
        return this.timeElapsed;
    }

    public double getTotalTime() {
        return this.totalTime;
    }

    private void beginTimer() {
        this.startingTime = System.nanoTime();
    }

    private void stopTimer() {
        this.timeElapsed = System.nanoTime() - this.startingTime;
    }
    

    private void updateTotalTime() {
        this.totalTime += this.timeElapsed;
        
    }

    public void run() {

        while (true) {
            try {

                String string = null;
                Socket socket = new Socket(this.host, this.port);
                PrintWriter outputStream = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                
                totalTime = 0;
                
                //send command to server
                outputStream.println(this.serverCommand);
                beginTimer();
                //print output from server until done message recieved, do timer stuff
                while (true) {
                    string = inputStream.readLine();
                    if (!string.equals("done")) {
                        stopTimer();
                        updateTotalTime();
                        timeElapsed = 0;
                        System.out.println(string);
                        beginTimer();
                    } else {
                        break;
                    }
                }
                stopTimer();
                updateTotalTime();
                totalTime = TimeUnit.NANOSECONDS.toMillis(totalTime);
                inputStream.close();
                outputStream.close();
                
                System.out.printf("%n");
                System.out.printf("%n");
                socket.close();

                break;
            } catch (UnknownHostException i) {
                System.out.printf("Error, the host is unknown...t%n");
                i.printStackTrace();
            } catch (IOException i) {
                i.printStackTrace();
            } catch (Exception i) {
                System.out.printf("There was an error, an exception has been thrown%n");
            }
        }

    }

}
