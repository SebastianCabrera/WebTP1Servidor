package ucr.ac.cr.ci2413;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Main
{
    private static  int puerto = 6666;
    public static void main(String[] args) {
        try {
            System.out.println("Servidor con puerto numero " + puerto);
            ServerSocket server = new ServerSocket(puerto);
            while(true){
                new Servidor(server.accept());
            }
        } catch (Exception e) {
        }
    }
}