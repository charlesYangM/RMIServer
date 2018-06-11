package rmiForAutoPackage;

import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Created by Charles on 2017/6/21.
 */
public class AutoPackageServer {
    private static Logger logger = Logger.getLogger(AutoPackageServer.class.getName());

    public static void main(String[] args) throws RemoteException, AlreadyBoundException, MalformedURLException {
        AutoPackage AutoPackage = new AutoPackageImpl();
        LocateRegistry.createRegistry(1099);
        Naming.bind("rmi://localhost:1099/AutoPackage", AutoPackage);

        logger.info("Waiting for invocations from clients ...");
    }
}
