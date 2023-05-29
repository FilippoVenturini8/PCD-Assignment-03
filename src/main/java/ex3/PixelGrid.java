package ex3;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PixelGrid extends Remote {
    void clear() throws RemoteException;

    void set(int x, int y, int color) throws RemoteException;

    int get(int x, int y) throws RemoteException;

    int getNumRows() throws RemoteException;

    int getNumColumns() throws RemoteException;
}
