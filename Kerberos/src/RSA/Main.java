package RSA;

public class Main {
	public static void main(String[]argc)
    {
		System.out.println("1");
		RSA test=new RSA();
        String plian="niha";
        System.out.println("2");
        String first=test.encrypt_string(plian);
        System.out.println(test.encrypt_string(plian).length());
        String second=test.decrypt_string(first);
        System.out.print(second);
        
    }
}
