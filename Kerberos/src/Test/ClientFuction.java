package Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;


public class ClientFuction {
	private  BigInteger[] pubkey = new BigInteger[2];//AS的公钥
	
	private static final String AS_IP = "192.168.43.215";
    private static final String TGS_IP = "192.168.43.215";
    private static final String V_IP = "192.168.43.215";
    private static final int AS_PORT = 8888;
    private static final int TGS_PORT = 7777;
    private static final int SERVER_PORT = 6666;
    
    static Socket socket = null;
    static DataOutputStream output = null;
    static DataInputStream input = null;
    
    //private static final Logger log = LogManager.getLogger(BackgroundClient.class);
    
    //初始化用户进程
    public void init() throws IOException {
        pubkey[0]=new BigInteger("25210376174222502674597043575587868433359845921729777429392593376801244718803287391093663295857829689208086882496347648829616115541377572451813241929958704483188200039335156767743378800207443537960952226191738855785981692041259305829486869081914154060612653741010933367421479008336912956487753130634749042193278479439430114897156732011964917535436211234837825681273904534007141953227436227382619843005754349476204056079297744670168472659413655833978852141561891871277772065229804420765889528128945186167113014241572555603967433653284618158672392942089685732576941187113403406438710612351646692434368229939197562886053");
        pubkey[1]=new BigInteger("65537");
    }
    
    //kerberos认证
    public boolean verify(){
    	try {
    		socket = new Socket(AS_IP, AS_PORT);
    		output = new DataOutputStream(socket.getOutputStream());
    		input = new DataInputStream(socket.getInputStream());
        
    		String ID_c = "22345678";      //用户id
    		String AD_c = "192.168.43.216";  //用户ip
    		String K_c = "12345678";       //用户口令
        
    		kerberos kerberos = new kerberos();
        
    		//AS认证
    		String message1 = kerberos.Client_CToAS(ID_c, TGS_IP);
    		output.writeUTF(message1);                 //发送消息
    		System.out.println("向AS发送认证报文："+message1);
    		String receive1 = input.readUTF();         //接受信息
    		System.out.println("收到AS发来的密文："+receive1);
    		String ps = kerberos.generateHash(K_c);   //口令的hash值
    		String []message2 = kerberos.Client_parseAS(ps, receive1); //解密报文
    		for(int i=0;i<message2.length;i++) {                       //输出
    			System.out.println(message2[i]);
    		}
    		String Kctgs = message2[0];                 //用于c与v会话的session key
            String Ticket_tgs = message2[4];            //tgs生成用于访问v服务器的票据
    		
    		//tgs认证
    		Socket socket2 = new Socket(TGS_IP, TGS_PORT);
            DataOutputStream output2 = new DataOutputStream(socket2.getOutputStream());
            DataInputStream input2 = new DataInputStream(socket2.getInputStream());
            String message3  = kerberos.CtoTGS(V_IP, Ticket_tgs, ID_c, AD_c, Kctgs);
            output2.writeUTF(message3);                 //发送消息
            System.out.println("向TGS发送认证报文："+message3);
            String receive2 = input2.readUTF();         //接受信息
            System.out.println("收到TGS发来的密文："+receive2);
            String []message4 = kerberos.GetTGStoC(receive2, Kctgs);
            for(int i=0;i<message4.length;i++) {       //输出
    			System.out.println(message4[i]);
    		}
            String Kcv = message4[0];                 //用于c与v会话的session key
            String Ticket_v = message4[3];            //tgs生成用于访问v服务器的票据
            
            //v认证
            Socket socket3 = new Socket(V_IP, SERVER_PORT);
            DataOutputStream output3 = new DataOutputStream(socket3.getOutputStream());
            DataInputStream input3 = new DataInputStream(socket3.getInputStream());
            String message5 = kerberos.CtoV(ID_c, Kcv, AD_c, Ticket_v);
            output3.writeUTF(message5);                 //发送消息
            System.out.println("向V发送认证报文："+message5);
            String receive3 = input3.readUTF();         //接受信息
            System.out.println("收到TGS发来的密文："+receive3);
            String []message6 = kerberos.GetVtoC(receive3, Kcv);
            for(int i=0;i<message6.length;i++) {       //输出
    			System.out.println(message6[i]);
    		}
            
            
            System.out.println("认证成功！");
    		
    	} catch (IOException e) {
    		e.printStackTrace();
        }finally {
            try {
                input.close();
                output.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        
        
        
    	return true;
    	
    }
    
    //注册
    public boolean register(){
    	try {
    		socket = new Socket(AS_IP, AS_PORT);
    		output = new DataOutputStream(socket.getOutputStream());
    		input = new DataInputStream(socket.getInputStream());
        
    		String ID_c = "22345678";      //用户id
    		String K_c = "12345678";       //用户口令
        
    		kerberos kerberos = new kerberos();
    		String message1 = kerberos.CtoAS(ID_c, K_c);
    		output.writeUTF(message1);//发送消息
    		System.out.println("向AS发送注册报文："+message1);
    		String receive1 = input.readUTF();  //接受信息
    		System.out.println("收到AS发来的密文："+receive1);
    		String []message2 = kerberos.GetAStoC(receive1);
    		for(int i=0;i<message2.length;i++) {
    			System.out.println(message2[i]);
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
        }finally {
            try {
                input.close();
                output.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    	
        return true;
    }
    
    public static void main(String[] args){
    	ClientFuction c = new ClientFuction();
        //c.register();
        c.verify();
    }
    

}
