package ex3;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BrushManager extends Remote {
    void draw(Graphics2D g) throws RemoteException;

    void addBrush(Brush brush) throws RemoteException;

    void removeBrush(Brush brush) throws RemoteException;
}
