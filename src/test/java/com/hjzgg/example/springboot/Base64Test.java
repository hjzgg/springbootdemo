package com.hjzgg.example.springboot;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * @author hujunzheng
 * @create 2019-04-08 12:59
 **/
public class Base64Test {
    public static void main(String[] args) throws Exception {
//        pngToBase64();
        base64ToPng();
    }

    public static void base64ToPng() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Lenovo\\Desktop\\yy.txt"));
             OutputStream out = new FileOutputStream("C:\\Users\\Lenovo\\Downloads\\hehe.png")) {
            String base64Content = reader.readLine();
            byte[] pngContent = Base64.decodeBase64(base64Content);
            for (int i = 0; i < pngContent.length; ++i) {
                if (pngContent[i] < 0) {//调整异常数据
                    pngContent[i] += 256;
                }
            }
            out.write(pngContent);
            out.flush();
        }
    }


    public static void pngToBase64() throws Exception {
        try (FileInputStream fis = new FileInputStream("C:\\Users\\Lenovo\\Downloads\\tt.png")) {
            byte[] pngContent = new byte[fis.available()];
            fis.read(pngContent);
            IOUtils.write(Base64.encodeBase64String(pngContent), new FileWriter("C:\\Users\\Lenovo\\Desktop\\tt.txt"));
        }
    }
}