package ex3;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BrushManager extends Remote {

    List<Brush> getBrushes() throws RemoteException;

    void addBrush(Brush brush) throws RemoteException;

    void removeBrush(int brushId) throws RemoteException;

    void updateBrushPosition(int brushId, int x, int y) throws RemoteException;

    void changeBrushColor(int brushId, int color) throws RemoteException;

    Brush getBrush(int id) throws RemoteException;
}