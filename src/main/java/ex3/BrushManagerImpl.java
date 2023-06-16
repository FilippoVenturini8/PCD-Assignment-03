package ex3;


import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

public class BrushManagerImpl implements BrushManager {
    private List<Brush> brushes = new java.util.LinkedList<>();

    @Override
    public synchronized List<Brush> getBrushes() throws RemoteException{
        return new LinkedList<>(brushes);
    }

    @Override
    public synchronized void addBrush(final Brush brush) throws RemoteException{
        brushes.add(brush);
    }

    @Override
    public synchronized void removeBrush(final int brushId) throws RemoteException{
        brushes.remove(this.getBrush(brushId));
    }

    @Override
    public synchronized void updateBrushPosition(int brushId, int x, int y) throws RemoteException {
        this.getBrush(brushId).updatePosition(x, y);
    }

    @Override
    public synchronized void changeBrushColor(int brushId, int color) throws RemoteException{
        this.getBrush(brushId).setColor(color);
    }

    @Override
    public synchronized Brush getBrush(int id) throws RemoteException{
        for(Brush b: brushes){
            if(b.getId() == id){
                return b;
            }
        }
        return null;
    }
}