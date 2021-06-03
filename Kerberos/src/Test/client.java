package Test;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class client extends Thread {
	 //定义一个Socket对象
    Socket socket = null;

    public client(String host, int port) {
        try {
            //需要服务器的IP地址和端口号，才能获得正确的Socket对象
            socket = new Socket(host, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    @Override
    public void run() {
        //客户端一连接就开启这个读线程
        new sendMessThread().start();
        super.run();
        try {
            // 读Sock里面的数据
        	int len = 0;
            InputStream s = socket.getInputStream();
            byte[] buf = new byte[100];
            while ((len = s.read(buf)) != -1) {
            	byte[] arrays = new byte[len];
            	System.arraycopy(buf, 0, arrays, 0, len);
            	String message = new String(arrays);
            	System.out.println(message);
            }  

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //写线程
    class sendMessThread extends Thread{
    	@Override
    	public void run() {
    		super.run();
    		//写操作
    		Scanner scanner=null;
    		OutputStream os= null;
    		try {
    			scanner=new Scanner(System.in);
    			os= socket.getOutputStream();
    			String in ="";
    			do {
    				in=scanner.nextLine();
    				os.write(in.getBytes());
    				
    			}while(!in.equals("quit"));
                    
                    
    		} catch (IOException e) {
                    e.printStackTrace();
    		}
    		scanner.close();
    		try {
                    os.close();
    		} catch (IOException e) {
                    e.printStackTrace();
    		}
    	}
    }
        
    //函数入口
    public static void main(String[] args) {
    	System.out.println("********************客户端*********************");
    	//需要服务器的正确的IP地址和端口号
    	client A=new client("127.0.0.1", 2333);
    	A.start();
    }

}

