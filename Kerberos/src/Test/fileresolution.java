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

    //文件分割
    public static void getSplitFile(String file1,int count1) {
        String file = file1; //要分割的文件路径
 
        int count = count1; //文件分割的份数
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(new File(file), "r");
            long length = raf.length();
            long maxSize = length / count;
            
            //初始化偏移量
            long offSet = 0L;
            for (int i = 0; i < count - 1; i++) { 
                long begin = offSet;
                long end = (i + 1) * maxSize;
                offSet = getWrite(file, i, begin, end);
            }
            if (length - offSet > 0) {//最后一块单独处理
                getWrite(file, count-1, offSet, length);
            }
 
        } catch (FileNotFoundException e) {
            System.out.println("没有找到文件");
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
    
    //分块的文件数据写入临时文件
    public static long getWrite(String file,int index,long begin,long end){
        String a=file.split(".jpg")[0];
        long endPointer = 0L;
        try {
            //源文件
            RandomAccessFile in = new RandomAccessFile(new File(file), "r");
            //定义二进制临时文件存储
            RandomAccessFile out = new RandomAccessFile(new File(a + "_" + index + ".tmp"), "rw");
 
            //申明具体每一文件的字节数组
            byte[] b = new byte[1024];
            int n = 0;
            //从指定位置读取文件字节流
            in.seek(begin);
            //判断文件流读取的边界
            while(in.getFilePointer() <= end && (n = in.read(b)) != -1){
                out.write(b, 0, n);
            }
            //定义当前读取文件的指针
            endPointer = in.getFilePointer();
            //关闭输入流
            in.close();
            //关闭输出流
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return endPointer;
    }
   
    //文件合并
    public static void merge(String file,String tempFile,int tempCount) {
        String a=tempFile.split(".jpg")[0];
        RandomAccessFile raf = null;
        try {
            //定义要输出的文件指针
            raf = new RandomAccessFile(new File(file), "rw");
            //开始合并文件
            for (int i = 0; i < tempCount; i++) {
                //读取切片文件
                RandomAccessFile reader = new RandomAccessFile(new File(a + "_" + i + ".tmp"), "r");
                byte[] b = new byte[1024];
                int n = 0;
                //先读后写
                while ((n = reader.read(b)) != -1) {//读
                    raf.write(b, 0, n);//写
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
