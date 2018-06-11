package svnkit;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.internal.wc16.SVNUpdateClient16;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ResourceBundle;


/**
 * Created by CharlesYang on 2017/6/28.
 *
 */
public class SvnUserImpl {
    private static Logger logger = Logger.getLogger(SvnUserImpl.class.getName());
    private static ResourceBundle rb = ResourceBundle.getBundle("svn");
    private static String autosolveUrl = rb.getString("svn.consumer.autosolve.8.6.url");
    private static String username = rb.getString("svn.username.8.6");
    private static String password = rb.getString("svn.password.8.6");
    private static final SvnUserImpl svnUser = new SvnUserImpl();
    private static Enumeration<String> properties = rb.getKeys();

    //单纯的为了练习，而强行写的单例，哈哈，才看了effectiveJava
    private SvnUserImpl(){}

    public static SvnUserImpl getSingleInstanceFor86(){
        if(svnUser == null){
            throw new NullPointerException("not static svnUserImple instance be create");
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

    public static SVNStatus showStatus(SVNClientManager clientManager,
                                       File wcPath, boolean remote) {
        SVNStatus status = null;
        try {
            status = clientManager.getStatusClient().doStatus(wcPath, remote);
        } catch (SVNException e) {
            logger.error(e.getErrorMessage(), e);
        }
        return status;
    }

    public boolean updateProjectFromSvn(File localFile) {


        SVNClientManager clientManager = SVNUtilTest.authSvn(autosolveUrl, username, password);
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
            long checkoutVersion = SVNUtilTest.checkout(clientManager, repositoryURL, SVNRevision.HEAD, localFile, SVNDepth.INFINITY);
            logger.info("the file :"+localFile.getAbsolutePath()+" is not a VersionedDirectory," +
                    "so we checkout a copy for you , the checkVersion now is :"+checkoutVersion);
        } else {
            long updatedVersion = SVNUtilTest.update(clientManager, localFile, SVNRevision.HEAD, SVNDepth.INFINITY);
            logger.info("the file :"+localFile.getAbsolutePath()+" is already updated, the version now is :"+updatedVersion);
        }
        return true;
    }


}
