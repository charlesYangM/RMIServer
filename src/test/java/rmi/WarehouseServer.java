package rmi;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import javax.naming.NamingException;


public class WarehouseServer
{
    public static void main(String[] args) throws RemoteException, NamingException, MalformedURLException, AlreadyBoundException
    {
        System.out.println("Constructing server implementation");
        WarehouseImpl centralWarehouse = new WarehouseImpl();

        System.out.println("Binding server implementation to registry");
        LocateRegistry.createRegistry(1099);
        Naming.bind("rmi://localhost:1099/central_warehoues",centralWarehouse);

        System.out.println("Waiting for invocations from clients ...");
    }
}