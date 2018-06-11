package rmiForAutoPackage;

import org.apache.log4j.Logger;
import svnkitV1.SvnUserImplV1;
import util.CmdUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by HP on 2017/6/21.
 */
public class AutoPackageImpl extends UnicastRemoteObject implements AutoPackage {
    private static Logger logger = Logger.getLogger(AutoPackageImpl.class.getName());

    static String WorkPathOF820 = "D:\\Workspaces\\863-consumer-worker";
    //    static String WorkPathOF822 = "D:\\WorkerSpaces\\WorkerConsumer";
    static String WorkPathOF822 = "D:\\WorkerSpaces\\comsumers";
    //    static String WorkPathOF823 = "D:\\WorkSpaces\\worker-consumers";
    static String WorkPathOF823 = "D:\\WorkSpaces\\consumers";
    static ProcessBuilder builder = new ProcessBuilder();
    static Boolean IsAllPassed;

    protected AutoPackageImpl() throws RemoteException {
        IsAllPassed = false;
    }

    public boolean startKafkaPackgeInstall() throws Exception {
        autoKafkaServiceOnServer();
        return IsAllPassed;
    }


    /**
     * 停止kafka服务
     *
     * @throws Exception
     */
    public void autoKafkaServiceOnServer() throws Exception {
        ServerIP ip = getIPFromRunningComputer();
        switch (ip) {
            case IP820:
                autoRestartAndUpdateSVN820();
                break;
            case IP822:
                autoRestartAndUpdateSVN822();
                break;
            case IP823:
                autoRestartAndUpdateSVN823();
                break;
            default:
                showTheGetIPErrorInfo();
                return;
        }
        IsAllPassed = true;//用以指示该程序已经正确运行，如果错误会在default中返回运行不到这里
    }

    public void autoRestartAndUpdateSVN820() throws Exception {
        String Stop820cmd = "d: & " + WorkPathOF820 + "\\stopAllWorkers.bat";
        ExcuteCmd(Stop820cmd);

        SvnUserImplV1.getSingleInstanceFor86().updateProjectFromSvn(new File(WorkPathOF820 + "\\worker-autosolve-1"));
        SvnUserImplV1.getSingleInstanceFor86().updateProjectFromSvn(new File(WorkPathOF820 + "\\worker-autosolve-2"));
        SvnUserImplV1.getSingleInstanceFor86().updateProjectFromSvn(new File(WorkPathOF820 + "\\worker-autosolve-3"));

        String star820cmd = "d: & " + WorkPathOF820 + "\\startAllWorkers.bat";
        ExcuteCmd(star820cmd);
    }

    /**
     * 该方法涉及多线程的处理，设定了2个线程，对五个文件进行操作
     *
     * @throws Exception
     */
    private void autoRestartAndUpdateSVN822() throws Exception {
//        autoPackageThread820 Thread820 = new autoPackageThread820(latch);

//        String Stop822cmd = "d: & "+WorkPathOF822+"\\stopAllWorkers.bat";
//        ExcuteCmd(Stop822cmd);

        //之所以定义两个，是因为服务器只有两个核，渣渣服务器
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        final CountDownLatch latch = new CountDownLatch(5);

        for (int i = 3; i < 8; i++) {
            final  int index = i;
            //由于需要再线程中使用，所以需要定义另外一final值
            excutedPool(latch,index,fixedThreadPool);

        }
        latch.await();

        //如果不加下面这句话就会造成程序无法退出的现象
        fixedThreadPool.shutdown();
        logger.info("the count thread latch no is : " + latch.getCount());

        for (int i = 3; i < 8; i++) {
            CmdUtil.ExcuteCmd("d: & cd "+WorkPathOF822+"\\autosolver-"+i+"\\bin & daemon.bat");
        }
        logger.info("start mission is complete");
//
//        String star822cmd = "d: & "+WorkPathOF822+"\\startAllWorkers.bat";
//        ExcuteCmd(star822cmd);

//        String start822cmd = "d: & cd "+WorkPathOF822+"\\autosolver-3\\bin"
//                +"& "+WorkPathOF822+"\\autosolver-3\\bin\\daemon.bat"
//                +"& cd "+WorkPathOF822+"\\autosolver-4\\bin"
//                +"& " + WorkPathOF822+"\\autosolver-4\\bin\\daemon.bat"
//                +"& cd "+WorkPathOF822+"\\autosolver-5\\bin"
//                +"& " + WorkPathOF822+"\\autosolver-5\\bin\\daemon.bat"
//                +"& cd "+WorkPathOF822+"\\autosolver-6\\bin"
//                +"& " + WorkPathOF822+"\\autosolver-6\\bin\\daemon.bat"
//                +"& cd "+WorkPathOF822+"\\autosolver-7\\bin"
//                +"& " + WorkPathOF822+"\\autosolver-7\\bin\\daemon.bat";
//
//        ExcuteCmd(start822cmd);
    }

    private void autoRestartAndUpdateSVN823() throws Exception {


        SvnUserImplV1.getSingleInstanceFor86().updateProjectFromSvn(new File(WorkPathOF823 + "\\autosolve-8\\application"));
        SvnUserImplV1.getSingleInstanceFor86().updateProjectFromSvn(new File(WorkPathOF823 + "\\autosolve-9\\application"));
        SvnUserImplV1.getSingleInstanceFor86().updateProjectFromSvn(new File(WorkPathOF823 + "\\autosolve-10\\application"));
        SvnUserImplV1.getSingleInstanceFor86().updateProjectFromSvn(new File(WorkPathOF823 + "\\autosolve-44\\application"));
        SvnUserImplV1.getSingleInstanceFor86().updateProjectFromSvn(new File(WorkPathOF823 + "\\autosolve-45\\application"));
//        SvnUserImplV1.getSingleInstanceFor86().updateProjectFromSvn(new File(WorkPathOF823+"\\worker-autosolve-2"));
//        SvnUserImplV1.getSingleInstanceFor86().updateProjectFromSvn(new File(WorkPathOF823+"\\worker-autosolve-3"));
        String start823cmd = "d: & cd " + WorkPathOF823 + "\\autosolve-8\\bin"

                + "& " + WorkPathOF823 + "\\autosolve-8\\bin\\daemon.bat"
                + "& cd " + WorkPathOF823 + "\\autosolve-9\\bin"
                + "& " + WorkPathOF823 + "\\autosolve-9\\bin\\daemon.bat"
                + "& cd " + WorkPathOF823 + "\\autosolve-10\\bin"
                + "& " + WorkPathOF823 + "\\autosolve-10\\bin\\daemon.bat"
                + "& cd " + WorkPathOF823 + "\\autosolve-45\\bin"
                + "& " + WorkPathOF823 + "\\autosolve-45\\bin\\daemon.bat"
                + "& cd " + WorkPathOF823 + "\\autosolve-44\\bin"
                + "& " + WorkPathOF823 + "\\autosolve-44\\bin\\daemon.bat";
        ExcuteCmd(start823cmd);

//        String star823cmd = "d: & "+WorkPathOF823+"\\startAllWorkers.bat";
//        ExcuteCmd(star823cmd);
    }

    /**
     * 运行cmd命令
     *
     * @param cmd 产生的运行指令都在这里面
     * @throws Exception
     */
    public void ExcuteCmd(String cmd) throws Exception {
        builder.command(
                "cmd.exe", "/c", cmd
        );
        builder.redirectErrorStream(true);
        //start() 执行命令
        Process p = builder.start();

        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), "gbk"));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) {
                break;
            }
            logger.info(line);
            if (line.contains("error:")) {
                showTheErrorStopInfo();
                System.exit(-1);
            }
        }

    }

    /**
     * 给出一个windows弹出框提示程序出错
     *
     * @throws Exception
     */
    public void showTheErrorStopInfo() throws Exception {
        String showError = "@mshta vbscript:msgbox(\"程序出错请检查\",64,\"提示框Title\")(window.close)";
        ExcuteCmd(showError);
    }

    /**
     * 给出一个windows弹出框提示这是一个非Kafka IP
     *
     * @throws Exception
     */
    public void showTheGetIPErrorInfo() throws Exception {
        String showError = "@mshta vbscript:msgbox(\"不是Kafka服务器IP,自动打包RMI请求终止\",64,\"提示框Title\")(window.close)";
        ExcuteCmd(showError);
    }

    /**
     * 获得运行本代码的计算机IP
     *
     * @return ServerIP 一个枚举用于switch语句中
     * @throws SocketException
     */
    public static ServerIP getIPFromRunningComputer() throws SocketException {
        Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
//            System.out.println(netInterface.getName());
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address) {
                    if (ip.getHostAddress().equals("172.16.8.20")) {
                        return ServerIP.IP820;
                    } else if (ip.getHostAddress().equals("172.16.8.22")) {
                        return ServerIP.IP822;
                    } else if (ip.getHostAddress().equals("172.16.8.23")) {
                        return ServerIP.IP823;
                    }

                }
            }
        }
        return ServerIP.Notkafka;
    }

    /**
     * 线程池的执行
     * @param latch 用于同步的计数器
     * @param index 用于指示当前更新的消费者名称
     */
    public static void excutedPool(final CountDownLatch latch,final int index,ExecutorService fixedThreadPool){

        //使用线程池执行
        fixedThreadPool.execute(new Runnable() {
            public void run() {
                try {
//                    CmdUtil.killProcessByName("cmd.exe");
//                    CmdUtil.killProcessByName(index + ".exe");
                    if (!SvnUserImplV1.getSingleInstanceFor86().updateProjectFromSvn(new File(WorkPathOF822 + "\\autosolver-" + index + "\\application"))) {
                        logger.error("can not update " + index + " application");
                    }
                    //下面的代码用于对主线程通知，await等待通知
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
