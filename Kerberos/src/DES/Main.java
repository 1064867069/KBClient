package DES;

import RSA.RSA;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.Date;
import java.util.Random;

import Kerberos.Kerberos;
public class Main {
	
	
	
	public static void main(String[]argc)
    {
		String K_tgs = "tgsmime";
		String plain = "testeeerg888999999999999";
		DES des = new DES(K_tgs);
        String message = des.encrypt_string(plain);
        System.out.println(message.length());
        System.out.println(message);
        String resultString =des.decrypt_string(message);
        System.out.println(resultString);
    }
}
