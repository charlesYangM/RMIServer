package svnkitV1;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.internal.wc16.SVNCommitClient16;
import org.tmatesoft.svn.core.internal.wc16.SVNUpdateClient16;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ResourceBundle;


/**
 * Created by CharlesYang on 2017/6/28.
 *
 */
public class SvnUserImplV1 {
    private static Logger logger = Logger.getLogger(SvnUserImplV1.class.getName());
    private static ResourceBundle rb = ResourceBundle.getBundle("svn");
    private static String autosolveUrl = rb.getString("svn.consumer.autosolve.8.6.url");
    private static String username = rb.getString("svn.username.8.6");
    private static String password = rb.getString("svn.password.8.6");
    private static final String workspace = rb.getString("svn.workCopy.workerConsumer.localhost");
    private static final SvnUserImplV1 svnUser = new SvnUserImplV1();
    private static Enumeration<String> properties = rb.getKeys();

    //单纯的为了练习，而强行写的单例，哈哈，才看了effectiveJava
    private SvnUserImplV1(){}

    public static SvnUserImplV1 getSingleInstanceFor86(){
        if(svnUser == null){
            throw new NullPointerException("not static svnUserImple instance be created");
        }
        return svnUser;
    }

    public static void main(String[] args) {


//        String url = "http://172.16.3.10:18080/svn/autosolve/trunk/autosolve-test-v2";
//        String name = "yangmingxiong@tsinghuabigdata.com";
//        String password = "ARrgr4NDRF";

//        File workCopy = new File("D:\\Charles_863_V2_2016-3-28\\autosolve-test-v2");
//        SVNRepository repository = null;
//
//        try {
//            repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
//            ISVNAuthenticationManager authManager = SVNUtilTest.authSvn()
//            repository.setAuthenticationManager(authManager);
//
//            DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
//            SVNClientManager clientManager = SVNClientManager.newInstance(options,
//                    authManager);
//
//            logger.info(showStatus(clientManager, workCopy, true).getAuthor());
//
//            System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
//            System.out.println("Repository UUID: " + repository.getRepositoryUUID(true));
//
//            SVNNodeKind nodeKind = repository.checkPath("", -1);
//            if (nodeKind == SVNNodeKind.NONE) {
//                System.err.println("There is no entry at '" + autosolveUrl + "'.");
//                System.exit(1);
//            } else if (nodeKind == SVNNodeKind.FILE) {
//                System.err.println("The entry at '" + autosolveUrl + "' is a file while a directory was expected.");
//                System.exit(1);
//            }
//
////            listEntries( repository , "" );
//
//            Long latestRevision = repository.getLatestRevision( );
//            System.out.println( "Repository latest revision: " + latestRevision );
////            repository.update(latestRevision,);
//        } catch ( SVNException svne ) {
//        //handle exception
//    }

    }

    /**
     * 列出指定仓库目录下的所有文件
     * @param repository
     * @param path
     * @throws SVNException
     */
    public static void listEntries(SVNRepository repository, String path) throws SVNException {
        Collection entries = repository.getDir(path, -1, null, (Collection) null);
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            SVNDirEntry entry = (SVNDirEntry) iterator.next();
            System.out.println("/" + (path.equals("") ? "" : path + "/") + entry.getName() +
                    " ( author: '" + entry.getAuthor() + "'; revision: " + entry.getRevision() +
                    "; date: " + entry.getDate() + ")");
            if (entry.getKind() == SVNNodeKind.DIR) {
                listEntries(repository, (path.equals("")) ? entry.getName() : path + "/" + entry.getName());
            }
        }
    }

    /**
     * 更新本地目录
     * @param localFile 我们想要更新的svn的本地目录
     * @return
     *
     *
     */

    public boolean updateProjectFromSvn(File localFile) {
        //todo 这个方法中有一个问题，如果，更新的时候，发现svn需要进行clean up的操作，会发生更新操作失败，以后还需针对clean up的情况进行调整

        SVNClientManager clientManager = SVNUtilV1.authSvn(autosolveUrl, username, password);
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        if (null == clientManager) {
            logger.error("SVN login error! >>> url:" + autosolveUrl
                    + " username:" + username + " password:" + password);
            return false;
        }

        // 注册一个更新事件处理器,这个还不知道具体有什么用，删除了应该也没有影响
        clientManager.getCommitClient().setEventHandler(new SVNUpdateClient16(clientManager, options));

        SVNURL repositoryURL = null;
        try {
            // eg: http://svn.ambow.com/wlpt/bsp
//            repositoryURL = SVNURL.parseURIEncoded(project.getSvnUrl()).appendPath("trunk/"+project.getName(), false);
            repositoryURL = SVNURL.parseURIEncoded(autosolveUrl);
        } catch (SVNException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        if (!SVNWCUtil.isVersionedDirectory(localFile)) {
            long checkoutVersion = SVNUtilV1.checkout(clientManager, repositoryURL, SVNRevision.HEAD, localFile, SVNDepth.INFINITY);
            logger.info("the file :"+localFile.getAbsolutePath()+" is not a VersionedDirectory," +
                    "so we checkout a copy for you , the checkVersion now is :"+checkoutVersion);
        } else {
            long updatedVersion = SVNUtilV1.update(clientManager, localFile, SVNRevision.HEAD, SVNDepth.INFINITY);
            logger.info("the file :"+localFile.getAbsolutePath()+" is already updated, the version now is :"+updatedVersion);
        }
        return true;
    }

    /**
     * 提交项目到SVN
     * @param workspace 我们想要更新的本地地址
     * @return
     */
    public boolean commitProjectToSvn(String  workspace) {
        SVNClientManager clientManager = SVNUtilV1.authSvn(autosolveUrl, username, password);
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);

        clientManager.getCommitClient().setEventHandler(new SVNCommitClient16(clientManager,options));

        File wc_project = new File( workspace );

        checkVersiondDirectory(clientManager,wc_project);

        try {
            SVNUtilV1.commit(clientManager, wc_project, false, getIPFromRunningComputer());
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 递归检查不在版本控制的文件，并add到svn
     * @param clientManager
     * @param wc
     */
    private void checkVersiondDirectory(SVNClientManager clientManager,File wc){
        if(!SVNWCUtil.isVersionedDirectory(wc)){
            SVNUtilV1.addEntry(clientManager, wc);
        }
        if(wc.isDirectory()){
            for(File sub:wc.listFiles()){
                if(sub.isDirectory() && sub.getName().equals(".svn")){
                    continue;
                }
                checkVersiondDirectory(clientManager,sub);
            }
        }
    }

    /**
     * 获得本机的IP 用在日志信息中，
     * @return
     * @throws SocketException
     */
    public static String getIPFromRunningComputer() throws SocketException {
        Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements())
        {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
//            System.out.println(netInterface.getName());
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements())
            {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address)
                {
                    if (!ip.getHostAddress().equals("127.0.0.1")){
//                        System.out.println("本机的IP = " + ip.getHostAddress());
                        return ip.getHostAddress().toString();
                    }
                }
            }
        }
        return "IP CAN'T GET";
    }
}
