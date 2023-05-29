package ex3;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class BrushImpl implements Brush, Serializable {
    private int x, y;
    private int color;

    public BrushImpl(final int x, final int y, final int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public synchronized void updatePosition(final int x, final int y) throws RemoteException {
        this.x = x;
        this.y = y;
    }
    @Override
    public synchronized int getX(){
        return this.x;
    }
    @Override
    public synchronized int getY(){
        return this.y;
    }
    @Override
    public synchronized int getColor() throws RemoteException{
        return this.color;
    }
    @Override
    public synchronized void setColor(int color) throws RemoteException{
        this.color = color;
    }
}
