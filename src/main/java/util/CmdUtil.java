package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by HP on 2017/7/20.
 */
public class CmdUtil {
    static ProcessBuilder builder = new ProcessBuilder();
    /**
     * 运行cmd命令
     * @param svnCommand 产生的运行指令都在这里面
     * @throws Exception
     */
    public static boolean  ExcuteCmd(String svnCommand) throws Exception {

        builder.command("cmd.exe", "/c",svnCommand);
        builder.redirectErrorStream(true);
        //start() 执行命令
        Process p = builder.start();

        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(),"gbk"));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) {
                break;
            }

            System.out.println(line);
            if (line.contains("[ERROR]")){
                showTheErrorStopInfo();
                System.exit(-1);
            }
        }
        p.destroy();
        return true;
    }

    /**
     * 给出一个windows弹出框提示程序出错
     * @throws Exception
     */
    private static void showTheErrorStopInfo() throws Exception {
        ExcuteCmd("@mshta vbscript:msgbox(\"程序出错请检查\",64,\"提示框Title\")(window.close)");
    }

    /**
     * 根据进程名 关闭进程 针对windows
     * @param processName 进程名
     * @throws Exception
     */
    public static void killProcessByName(String processName) throws Exception {
        ExcuteCmd("start wmic process where name='"+processName+"' call terminate");
    }
}
