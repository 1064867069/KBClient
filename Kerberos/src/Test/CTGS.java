package Test;

import java.util.Arrays;
import java.util.Date;

import DES.DES;

public class CTGS {
	String getlength(int length) {//����ת��
		String len="";
		if(length<10) {
			len ="000"+String.valueOf(length);
		}else if(length<100) {
			len ="00"+String.valueOf(length);
		}else if(length<1000) {
			len ="0"+String.valueOf(length);
		}else {
			len = String.valueOf(length);
		}
		return len;
	}
	//c->tgs
	String CtoTGS(String IDv,String Tickettgs,String IDc,String ADc,String EKctgs) {
		//������֤
		Date TS_3 = new Date();
		DES des = new DES(EKctgs);
		String Authenticatortgs= des.encrypt_string(IDc+" "+ADc+" "+TS_3.getTime());
		
		String message = IDv+" "+Tickettgs+" "+Authenticatortgs;
		int length = message.length();
		String length1 = getlength(length);
		String cpackage = "1005"+length1+message;
		return cpackage;
	}
	//tgs��ȡc->tgs�İ�
	String[] GetCtoTGS(String cpackage,String EKtgs) {
		String []result;
		char[]ss=cpackage.toCharArray();
		String message="";
		for(int i=8;i<cpackage.length();i++) {
			message=message+ss[i];
		}
		result = message.split(" ");
		//���ܱ�ǩ
		DES des = new DES(EKtgs);
		String []Tickettgs = des.decrypt_string(result[1]).split(" ");
		//������֤
		DES des1 = new DES(Tickettgs[0]);
		String []Authenticatortgs =des1.decrypt_string(result[2]).split(" ");
		//�ϲ�����
		int l = Tickettgs.length;
		int l1 = Authenticatortgs.length;
		result = Arrays.copyOf(result, l+1+l1);// ��������
        System.arraycopy(Tickettgs, 0, result, 1, l);
        System.arraycopy(Authenticatortgs, 0, result, 1+l, l1);	
		return result;
	}
	//TGS->c
	String TGStoC(String Kcv,String EKctgs,String EKv,String IDv,String IDc,String ADc) {
		//����Ʊ��
		String Ticketv1,Ticketv2;
		Date TS_4 = new Date();
		TS_4.getTime();
		long lifetime = 10000000;
		Ticketv1 = Kcv+" "+IDc+" "+ADc+" "+IDv+" "+TS_4.getTime()+" "+lifetime;
		DES des = new DES(EKv);
		Ticketv2 = des.encrypt_string(Ticketv1);
		//����message
		String message1=Kcv+" "+IDv+" "+TS_4.getTime()+" "+Ticketv2;
		DES des1 = new DES(EKctgs);
		String message2=des1.encrypt_string(message1);
		//���ɰ�		
		int length = message2.length();
		String length1 = getlength(length);
		String cpackage = "1006"+length1+message2;
		return cpackage;
	}
	//c��ȡtgs->c�İ�
		String[] GetTGStoC(String cpackage,String EKctgs) {
			String []result;
			char[]ss=cpackage.toCharArray();
			String message1="";
			for(int i=8;i<cpackage.length();i++) {
				message1=message1+ss[i];
			}
			//�������ݰ�
			DES des1 = new DES(EKctgs);
			String message2=des1.decrypt_string(message1);
			result = message2.split(" ");
			return result;
		}
	public static void main(String[] args) {
		CTGS ct = new CTGS();

		String s1 = ct.CtoTGS("127.12.312.211", "jyak1ZjvCWHWA4/G2C4qX456NfR847I9Gm8F+x/DBFJysLv0bZQgAM5aSGtIwtRiuzZ5eVL7TCi0WJg+BX7fgQ==", "asasasda","127.234.234.23","shxrgerf");
		System.out.println(s1);
		//String[] s2 = ct.GetCtoTGS(s1,"shxrgerf","qwertyui");
		//for(int i=0;i<s2.length;i++) {
			//System.out.println(s2[i]);
		//}
		/*
		String m1 = ct.TGStoC("qwertyui", "asdfghjk", "zxcvbnml", "127.125.123.12", "439999", "145.124.245.3");
		System.out.println(m1);
		String[] s2 = ct.GetTGStoC(m1,"asdfghjk");
		for(int i=0;i<s2.length;i++) {
			System.out.println(s2[i]);
		}
		*/
	}

}
