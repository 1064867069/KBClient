package Test;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class client extends Thread {
	 //����һ��Socket����
    Socket socket = null;

    public client(String host, int port) {
        try {
            //��Ҫ��������IP��ַ�Ͷ˿ںţ����ܻ����ȷ��Socket����
            socket = new Socket(host, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    @Override
    public void run() {
        //�ͻ���һ���ӾͿ���������߳�
        new sendMessThread().start();
        super.run();
        try {
            // ��Sock���������
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
    
    //д�߳�
    class sendMessThread extends Thread{
    	@Override
    	public void run() {
    		super.run();
    		//д����
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
        
    //�������
    public static void main(String[] args) {
    	System.out.println("********************�ͻ���*********************");
    	//��Ҫ����������ȷ��IP��ַ�Ͷ˿ں�
    	client A=new client("127.0.0.1", 2333);
    	A.start();
    }

}

