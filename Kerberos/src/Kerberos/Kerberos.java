package Kerberos;

import RSA.RSA;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Date;
import java.util.Random;

import DES.DES;

public class Kerberos {

    String ID_as = "00";
    String ID_tgs = "01";
    String ID_v = "02";
    String register = "0000";
    String K_TGS = "tgsmima";
    String K_V = "vvvmima";

    String ID_as_N = "13437348013206713011776153015230929232275142817544473901779622638713964556654757389258697305272298230812548643581242705983325011038307842461961432768279203877965082199907212517594145633949362437385253554141142472891146024887883490345056601985592050961160923771002647072077973006928639919590414713426331600378895549387703651544877722302949645632333230162887167173666465346006486189481652060954198820673985924611126552957272699060568036444193024486315435969548092076311181406929291804329522086582271187414843689958113161246015868093222834647317060724019451851518581086867739687259690919551979067134606990094794229793401";
    String ID_as_PK = "65537";//��Կe
    String ID_as_SK = "6901667324908847928489205893551555745420351593471848513172770765243309290043117452428963943921309042943393806423672287808485331897408796330501606549171403661079735598685882487064353513044074171915483160442879545600025425999832860346139589536246914375141337794768147811056603893773363875876725356190300868212565685844233981770145413171885929101382291989203265881631353655297461791497418329519708520781888123775664909246548323883189840834535363201501369611282553774926283844301230570643032765662366251688084662186752037468895782488625871734784832944650741834099051003653834790497388310983459874121897497736981794902485";//˽Կd
    Date TS;
    String Lifetime = "0000000001000";

    // C ------>>>>>>  AS

    public String client_to_as(String ID_c, String ID_tgs,Date TS1){
        String message;
        message = ID_c + ID_tgs + TS1.getTime();
        return  message;
    }

    public String[] as_parse_client(String message){
        String []result;
        if(message.length()==4){//����ע��
            result = new String[1];
        }
        else {//ע���ж�messageΪ�ա������ǲ��ǵ��ڹ涨�ı��ĳ���
            result = new String[3];
            String ID_tgs = message.substring(4,6);
            String TS1 = message.substring(6,19);
            result[1] = ID_tgs;
            result[2] = TS1;
        }
        String ID_c = message.substring(0,4);
        result[0] = ID_c;
        return result;
    }

    public String as_to_client(String K_c, String K_c_tgs,String ID_tgs,Date TS_2,String Lifetime_2,String Ticket_tgs){
        String message;

        TS_2 = new Date();
        message = K_c_tgs + ID_tgs + TS_2.getTime() + Lifetime_2 + Ticket_tgs;
        DES des = new DES(K_c);//K_c

        System.out.println("AS����Client�������ǣ�"+message);
        message = des.encrypt_string(message);
        return  message;
    }

    public String get_Ticket_tgs( String K_tgs,String K_c_tgs,String ID_c,String AD_c,String ID_tgs,Date TS_2,String Lifetime_2 ){
        String message;
        K_tgs = "tgsmima";
        DES des = new DES(K_tgs);
        message = des.encrypt_string(K_c_tgs + ID_c + AD_c + ID_tgs + TS_2.getTime() + Lifetime);
        return message;
    }

    /**
     * ����AS����Client�ı���message
     * ��Message��������ڲ���K_c_tgs || ID_tgs || TS_2 || Lifetime_2 || Ticket_tgs
     */
    public String[] client_parse_as(String K_C, String message){//ע���ж�messageΪ�ա����ȴ���0   ע����ܺ�ı��ĳ��Ȳ��̶�
        if(message.length() == 4){// AS�ش�0000 ��ʾ���ݿ�û�鵽
            String[] result = new String[1];
            result[0] = message;
            return result;
        }
        String parse[] = new String[5];
        DES des = new DES(K_C);  //K_C
        message = des.decrypt_string(message);
        System.out.println("Client����AS�����ı���:K_c_tgs || ID_tgs || TS_2 || Lifetime_2 || Ticket_tgs"+ message);
        parse[0] = message.substring(0,7);
        parse[1] = message.substring(7,9);
        if(!parse[1].equals(ID_tgs)){
            String[] result = new String[1];
            result[0] = K_C;
            return result;
        }
        parse[2] = message.substring(9,22);
        parse[3] = message.substring(22,35);
        parse[4] = message.substring(35,message.length());
        DES des1 = new DES(K_TGS);
        String enTicket = message.substring(35,message.length());
        System.out.println("����ǰ��ticket_tgs is:"+enTicket);
        String Ticket = des1.decrypt_string(enTicket);
        System.out.println("���ܺ��ticket_tgs is:"+Ticket);
        return parse;
    }

    //����֤�飬AS�Ĺ�Կ��������˽Կ�������ǩ��
    public String get_Certification(){
        System.out.println("hash(PK):"+BigInteger.valueOf((ID_as_PK+" "+ID_as_N).hashCode()).toString());
        System.out.println("Sig[hash(PK)]:"+Sig_PK(  ( BigInteger.valueOf(  ( ID_as_PK+" "+ID_as_N ).hashCode()    )  ).toString()));
        String Certification = ID_as + ID_as_PK +" " + ID_as_N + " "+Sig_PK(ID_as_PK+" "+ID_as_N);
        return  Certification;
    }

    public String Sig_PK(String AS_PK){
        BigInteger []bigInteger_sk = new BigInteger[2];
        BigInteger []bigInteger_pk = new BigInteger[2];
        bigInteger_sk[0] = new BigInteger(ID_as_N);
        bigInteger_sk[1] = new BigInteger(ID_as_SK);
        RSA rsa = new RSA(bigInteger_pk,bigInteger_sk);
        String C = rsa.sign_string(AS_PK);
        //ǩ��
        return C;
    }

    /**
     * ��֤֤��
     * @param Certification
     * @return ��Կ e d
     */
    public String parse_Certification(String Certification){
        String []result = new String[3];
        result = Certification.split(" ");
        System.out.println("IDtgs+e:"+result[0]);
        System.out.println("N:"+result[1]);
        System.out.println("Sig[hash(PK)]:"+result[2]);
        if(!result[0].substring(0,2).equals(ID_as)) {
            return null;
        }
        BigInteger []bigInteger_sk = new BigInteger[2];
        BigInteger []bigInteger_pk = new BigInteger[2];
        bigInteger_pk[0] = new BigInteger(ID_as_N);
        bigInteger_pk[1] = new BigInteger(ID_as_PK);
        RSA rsa = new RSA(bigInteger_pk,bigInteger_sk);
        //�����ǩ����֤
        System.out.println("hash:"+(result[0].substring(2)+" "+result[1]).hashCode());
        String verify = rsa.verify_string(result[2]);
        System.out.println("verify:"+verify);
        if(!verify.equals(  String.valueOf  (  (  result[0].substring(2)+" "+result[1]  ).hashCode())  )  ){
            System.out.println("verify false!");

            return null;
        }
        else {
            System.out.println("verify successfully!");

            return result[0].substring(2)+" "+result[1];
        }
    }

    //�����û�ID���û�����Լ���֤ͨ����PK���Լ���ID����������֤,Pk���ÿո�ָ�����N��E
    public String client_id_key(String ID_c,String ID_key,String PK){
        String []result = PK.split(" ");
        BigInteger []bigInteger_sk = new BigInteger[2];
        BigInteger []bigInteger_pk = new BigInteger[2];
        bigInteger_pk[0] = new BigInteger(result[1]);
        bigInteger_pk[1] = new BigInteger(result[0]);
        RSA rsa = new RSA(bigInteger_pk,bigInteger_sk);

        System.out.println("N is:"+result[1]);
        System.out.println("E is:"+result[0]);

        String c = rsa.encrypt_string(ID_c + ID_key);
        System.out.println("c:"+c);
        return c;   //rsa����
    }

    //�����û������û�����
    public String[] parse_client_id_key(String message){

        System.out.println("message:"+message);

        BigInteger []bigInteger_sk = new BigInteger[2];
        BigInteger []bigInteger_pk = new BigInteger[2];
        bigInteger_sk[0] = new BigInteger(ID_as_N);
        bigInteger_sk[1] = new BigInteger(ID_as_SK);
        RSA rsa = new RSA(bigInteger_pk,bigInteger_sk);

        String decrpt_information = rsa.decrypt_string(message);

        String []user = new String[2];
        user[0] = decrpt_information.substring(0,4);
        user[1] =  decrpt_information.substring(4);
        return user;
    }

    // C ------>>>>>>  TGS

    /**
     * Client ��TGS���� ��ȡ�������Ʊ��
     */
    public String client_to_tgs(String ID_v,String Ticket_tgs,String Authenticator_c){
        String message;
        message = ID_v +" " + Ticket_tgs +" "+ Authenticator_c;
        return  message;
    }

    /**
     * TGS ���� ��Client��������������
     * ��Message������Stirng[], String����Ԫ��Ϊ��ID_v || Ticket_tgs || Authenticator_c
     */
    public String[] tgs_parse_client(String message){
        String []result;
        if(message.length()>0){
            result = message.split(" ");
        } else {
            result = new String[1];
        }
        return result;
    }

    /**
     * TGS ��Client���ͷ������Ʊ�ݱ���
     */
    public String tgs_to_client(String K_c_tgs, String K_c_v,String ID_v,Date TS_4,String Ticket_v){
        String message;
        TS_4 = new Date();
        message = K_c_v + ID_v + TS_4.getTime() + Ticket_v;
        DES des = new DES(K_c_tgs);
        System.out.println("TGS����Client�������ǣ�"+message);
        message = des.encrypt_string(message);
        return  message;
    }

    /**
     * Client ��TGS���صı����н������������Ʊ��
     * ��Message������Stirng[], String����Ԫ��Ϊ��K_c_v || IDv || TS_4 || Ticket_v
     */
    public String[] client_parse_tgs(String K_c_tgs, String message){
        String parse[] = new String[5];
        DES des = new DES(K_c_tgs);
        message = des.decrypt_string(message);
        System.out.println("Client����TGS�����ı���:K_c_v || IDv || TS_4 || Ticket_v "+ message);
        parse[0] = message.substring(0,7);
        parse[1] = message.substring(7,9);
        parse[2] = message.substring(9,22);
        parse[3] = message.substring(22,message.length());
        DES des1 = new DES(K_V);
        String enTicket = message.substring(22,message.length());
        System.out.println("����ǰ��ticket_v is:"+enTicket);
        String Ticket = des1.decrypt_string(enTicket);
        System.out.println("���ܺ��ticket_v is:"+Ticket);
        return parse;
    }

    /**
     * ���ɷ������Ʊ��Ticket_v
     */
    public String get_Ticket_v(String K_v,String K_c_v,String ID_c,String AD_c,String ID_v,Date TS_4,String Lifetime_4){
        String message;
        DES des = new DES(K_V);
        TS_4 = new Date();
        message = des.encrypt_string(K_c_v + ID_c + AD_c + ID_v + TS_4.getTime() + Lifetime);
        return message;
    }

    /**
     * Client����AS������֤��ϢAuthenticatorC(TS_3)   Client����TGS ��K_c_v ������֤��ϢAuthenticatorC(TS_5)
     */
    public String get_Authenticator_c(String K_c_tgs,String ID_c,String AD_c,Date TS_3){
        String message;
        DES des = new DES(K_c_tgs);
        message = des.encrypt_string(ID_c + AD_c + TS_3.getTime());
        return message;
    }

    // C ------>>>>>>  Server V

    /**
     * Client ��Server V ��ȡ����
     */
    public String client_to_v(String Ticket_v,String Authenticator_c){
        String message;
        message = ID_v + " "+ Ticket_v +" "+ Authenticator_c;
        return  message;
    }

    /**
     * Server V ���� ��Client��������������
     * ��Message������Stirng[], String����Ԫ��Ϊ��Ticket_v || Authenticator_c
     */
    public String[] v_parse_client(String message){
        String []result;
        if(message.length()>0){
            result = message.split(" ");
        } else {
            result = new String[1];
        }
        return result;
    }

    /**
     * Server V ��Client�����໥��֤����
     */
    public String v_to_client(String K_c_v,String TS_5){
        String message;
        message = String.valueOf(Long.parseLong(TS_5)+1);//TS_5+1
        DES des = new DES(K_c_v);
        System.out.println("V����Client������TS_5+1�ǣ�"+message);
        message = des.encrypt_string(message);
        return  message;
    }

    /**
     * Client ��Server V���صı����н������໥��֤����
     * ��Message������Stirng[], String����Ԫ��Ϊ��TS_5 + 1
     */
    public String[] client_parse_v(String k_c_v, String message){
        String parse[] = new String[1];
        DES des = new DES(k_c_v);
        message = des.decrypt_string(message);
        System.out.println("Client����V�����ı���:TS_5 + 1:"+ message);
        parse[0] = message;
        return parse;
    }

    /**
     * Client����TGS ��K_c_v������֤��ϢAuthenticatorC(TS_5)
     */
    public String get_Authenticator_c_sig(String K_c_tgs,String ID_c,String AD_c,Date TS_5,String N,String SK){
        String message;
        DES des = new DES(K_c_tgs);
        message = des.encrypt_string(ID_c + AD_c + TS_5.getTime() + N + " " + SK);
        return message;
    }

    /**
     * �������7λDES���� ����k_c_tgs k_c_v
     */
    public String create_sessionkey(){
        String sessionKey = new String();
        StringBuilder stringBuilder = new StringBuilder();
        int[] temp = new int[7];  //ascll ��ֵ ���49 -- 122
        Random random = new Random();
        for(int i=0; i<temp.length; i++){
            temp[i] = random.nextInt(122) % (122 - 49 + 1) + 49;
            stringBuilder.append((char)temp[i]);
        }
        return stringBuilder.toString();
    }

}

