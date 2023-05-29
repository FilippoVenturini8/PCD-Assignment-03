package ex3;

import java.rmi.RemoteException;

public interface ColorChangeListener {
    void colorChanged(int color) throws RemoteException;
}
