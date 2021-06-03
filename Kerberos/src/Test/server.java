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
            new sendMessThread().start();//���Ӳ�����socket�������÷�����Ϣ�߳�
            System.out.println(socket.getInetAddress().getHostAddress()+" SUCCESS TO CONNECT...");
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    //������Ϣ��Ӧ��Ϣ�߳�
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
    
  //�������
    public static void main(String[] args) {
    	System.out.println("*******************�����*******************");
        server B = new server(2333);
        B.start();
    }
    

}

