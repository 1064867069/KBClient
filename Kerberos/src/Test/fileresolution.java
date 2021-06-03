package Test;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile; 

public class fileresolution {
    public static void main(String[] args) {
    	String file = "E:\\project\\eclipse\\File_Share\\png\\1.png";
    	String outfile = "E:\\project\\eclipse\\File_Share\\png\\out.png";
    	int count = 10;
        getSplitFile(file,count);
        merge(outfile,file,10);
    }

    //�ļ��ָ�
    public static void getSplitFile(String file1,int count1) {
        String file = file1; //Ҫ�ָ���ļ�·��
 
        int count = count1; //�ļ��ָ�ķ���
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(new File(file), "r");
            long length = raf.length();
            long maxSize = length / count;
            
            //��ʼ��ƫ����
            long offSet = 0L;
            for (int i = 0; i < count - 1; i++) { 
                long begin = offSet;
                long end = (i + 1) * maxSize;
                offSet = getWrite(file, i, begin, end);
            }
            if (length - offSet > 0) {//���һ�鵥������
                getWrite(file, count-1, offSet, length);
            }
 
        } catch (FileNotFoundException e) {
            System.out.println("û���ҵ��ļ�");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    //�ֿ���ļ�����д����ʱ�ļ�
    public static long getWrite(String file,int index,long begin,long end){
        String a=file.split(".jpg")[0];
        long endPointer = 0L;
        try {
            //Դ�ļ�
            RandomAccessFile in = new RandomAccessFile(new File(file), "r");
            //�����������ʱ�ļ��洢
            RandomAccessFile out = new RandomAccessFile(new File(a + "_" + index + ".tmp"), "rw");
 
            //��������ÿһ�ļ����ֽ�����
            byte[] b = new byte[1024];
            int n = 0;
            //��ָ��λ�ö�ȡ�ļ��ֽ���
            in.seek(begin);
            //�ж��ļ�����ȡ�ı߽�
            while(in.getFilePointer() <= end && (n = in.read(b)) != -1){
                out.write(b, 0, n);
            }
            //���嵱ǰ��ȡ�ļ���ָ��
            endPointer = in.getFilePointer();
            //�ر�������
            in.close();
            //�ر������
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return endPointer;
    }
   
    //�ļ��ϲ�
    public static void merge(String file,String tempFile,int tempCount) {
        String a=tempFile.split(".jpg")[0];
        RandomAccessFile raf = null;
        try {
            //����Ҫ������ļ�ָ��
            raf = new RandomAccessFile(new File(file), "rw");
            //��ʼ�ϲ��ļ�
            for (int i = 0; i < tempCount; i++) {
                //��ȡ��Ƭ�ļ�
                RandomAccessFile reader = new RandomAccessFile(new File(a + "_" + i + ".tmp"), "r");
                byte[] b = new byte[1024];
                int n = 0;
                //�ȶ���д
                while ((n = reader.read(b)) != -1) {//��
                    raf.write(b, 0, n);//д
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
