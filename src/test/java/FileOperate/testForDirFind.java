package FileOperate;

import java.io.File;

/**
 * Created by HP on 2017/7/20.
 */
public class testForDirFind {

    public static void main(String[] args) {
        getDir("E:\\测试文档_YMX");

    }
    public static void getDir(String strPath) {
        File f = new File(strPath);
        if (f.isDirectory()) {
            File[] fList = f.listFiles();
            for (int j = 0; j < fList.length; j++) {
                if (fList[j].isDirectory())
                    System.out.println(fList[j].getAbsolutePath());
            }
        }
    }

}
