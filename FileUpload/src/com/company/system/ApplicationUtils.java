package com.company.system;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ApplicationUtils {



     public static void main(String[] args){
         Desktop dt = Desktop.getDesktop();
         try {
             dt.open(new File("/Users/mathew/Downloads/Mathew A 16993699168.pdf"));
         } catch (IOException e) {
             e.printStackTrace();
         }

     }
}
