package svnkit;

import org.junit.Test;

import java.io.File;

/**
 * Created by HP on 2017/6/30.
 */
public class testSvn {
    @Test
    public void testUpdateConsumerFileAtLocal(){
        SvnUserImpl svnUser = SvnUserImpl.getSingleInstanceFor86();
        File localKafka = new File("E:\\测试文档_YMX\\consumer-autosolve");
        System.out.println(svnUser.updateProjectFromSvn(localKafka));
    }
}
