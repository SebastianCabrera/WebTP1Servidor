package ucr.ac.cr.ci2413;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.io.File;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Arrays;


class Servidor extends Thread
{
    private Socket socket;
    Servidor(Socket sokk)
    {
        this.socket = sokk;
        this.start();
    }
    @Override
    public void run() {
        try
        {
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String recibe = br.readLine();
            String [] dir = recibe.split(" "); //Linea recibida dividida por espacios
            String [] accept=null; //Se guardan los accept recibidos en la solicitud
            String referer=""; //Se pone la direccion, en este caso localhost y el puerto
            //Talvez cambiar esto-V
            String mensajeRetorno= "HTTP/1.1 501 No implementado"; //El mensaje de retorno
            String servidor= "MariSebasServer"; //Lo que se va a presentar como servidor
            String path=""; //FALTA DECIR QUE ES
            String absolute= new File("").getAbsolutePath();
            File file= null; //Se va a guardar el archivo html que se va a devolver
            long length=0; //FALTA DCIR QUE ES
            Calendar calendario = Calendar.getInstance(); //Esto es para poder devolver la hora

            //Si dir[1] no tiene un archivo, se tiene que cargar uno por default
            if(dir[1].length() < 2)
            {
                dir[1]="\\index.html";
            }


            String linea = "";

            int tam = -1;
            while((linea=br.readLine())!= null&&(linea.length()!=0))
            {
                //Se obtiene el tamano del contenido
                if (linea.contains("Content-Length:"))  //linea.indexOf("Content-Length:")>-1
                {
                    tam = Integer.parseInt(linea.substring(linea.indexOf("Content-Length:")+ 16,linea.length()));
                }
                //Se llena el vector de accepts
                if (linea.contains("Accept:"))
                {
                    accept=linea.substring(linea.indexOf("Accept:")+ 8,linea.length()).split(",|;");
                }
            }

            // Si es un post
            String post = "";
            if(tam>0)
            {
                char[] charPost = new char[tam];
                br.read(charPost,0,tam); //Acá algo
                post = new String(charPost); //Este se ocuparia en 'Datos' en la bitacora
            }


            try{
                file = new File(absolute + "\\501.html"); //html de 'no implementado'
                length = file.length();
                System.out.println("metodo: "+ dir[0]);
                if(dir[0].equals("GET") || dir[0].equals("get")  ||
                        dir[0].equals("head") || dir[0].equals("HEAD") ||
                        dir[0].equals("--head") || dir[0].equals("--HEAD")
                        || dir[0].equals("POST") || dir[0].equals("post"))
                {
                    file = new File(absolute+dir[1]); //Se carga el file solicitado
                    referer="http://locallhost:9999/";
                    System.out.println("Path: "+absolute+dir[1]);
                    if(file.exists()){
                        mensajeRetorno="HTTP/1.1 200 OK";
                    }else{
                        mensajeRetorno="HTTP/1.1 404 No fue encontrado";
                        file = new File(absolute + "\\404.html");//html de 'no encontrado'
                        referer="";
                    }
                    length = file.length();

                }

            }catch(Exception e){
                e.printStackTrace();
            }

            //Para revisar el tipo de dato que es
            String mime = Files.probeContentType(file.toPath());

            path = file.getPath();

            System.out.println("Valor de path: " + path);

            if(Arrays.asList(accept).contains(mime) || Arrays.asList(accept).contains("*/*"))
            {
                //Acomodar de otra forma?
            }
            else
            {
                mensajeRetorno="HTTP/1.1 406 No Aceptable";
                file = new File(absolute + "\\406.html");  // html de 'no aceptado'
                length = file.length();
                referer="";
            }

            OutputStream out = socket.getOutputStream();
            boolean b1=false;
            //Si es head
            if(dir[0].equals("head") || dir[0].equals("HEAD") || dir[0].equals("--head") || dir[0].equals("--HEAD")){
                b1=true;
                length=0;
            }
            String res = mensajeRetorno+"\r\n"
                    +"Accept: text/html,application/xhtml+xml,application/xml\r\n"
                    + "Server: "+servidor+"\r\n"
                    + "Host: locallhost\r\n"
                    + "Referer: "+referer+"\r\n"
                    + "Date: "+calendario.getTime()+"\r\n"
                    + "Content-Length: "+length+"\r\n"
                    + "Content-type: "+ mime+"; charset=UTF-8\r\n\r\n";
            out.write(res.getBytes());

            //Se hace el envío
            if(!b1){
                FileInputStream fis = new FileInputStream(file);
                byte[] r = new byte[1024];
                int read;
                while((read=fis.read(r)) >= 0) {
                    out.write(r, 0, read);
                }
                fis.close();
            }
        }

        //FALTA BITACORA
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}