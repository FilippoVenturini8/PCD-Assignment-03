package ex3;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Objects;

public class BrushImpl implements Brush, Serializable {
    private int x, y;
    private int color;
    private final int id;

    public BrushImpl(final int x, final int y, final int id, final int color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.id = id;
    }

    @Override
    public synchronized void updatePosition(final int x, final int y) throws RemoteException {
        this.x = x;
        this.y = y;
    }
    // write after this getter and setters
    @Override
    public synchronized int getX() throws RemoteException{
        return this.x;
    }
    @Override
    public synchronized int getY() throws RemoteException{
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

    @Override
    public int getId() throws RemoteException{
        return this.id;
    }
}