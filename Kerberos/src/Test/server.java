package Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class server extends Thread {
	ServerSocket server = null;
    Socket socket = null;
    public server(int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run(){

        super.run();
        try{
            System.out.println("wait client connect...");
            while(true) {
            socket = server.accept();
            new sendMessThread().start();//连接并返回socket后，再启用发送消息线程
            System.out.println(socket.getInetAddress().getHostAddress()+" SUCCESS TO CONNECT...");
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    //接受消息响应消息线程
    class sendMessThread extends Thread{
        @Override
        public void run(){
            super.run();
            Scanner scanner=null;
            OutputStream out = null;
            try{
                if(socket != null){
                    scanner = new Scanner(System.in);
                    out = socket.getOutputStream();
                    String in = "";
                    do {
                    	in=scanner.nextLine();
                    	out.write(in.getBytes());
                    }while (!in.equals("quit"));
                    scanner.close();
                    try{
                        out.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    
                }
            }catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    
  //函数入口
    public static void main(String[] args) {
    	System.out.println("*******************服务端*******************");
        server B = new server(2333);
        B.start();
    }
    

}

