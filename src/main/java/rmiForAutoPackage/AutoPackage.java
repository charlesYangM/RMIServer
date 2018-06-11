package rmiForAutoPackage;

import java.rmi.Remote;


/**
 * Created by HP on 2017/6/21.
 */
public interface AutoPackage extends Remote{
    public boolean startKafkaPackgeInstall() throws Exception;
}
