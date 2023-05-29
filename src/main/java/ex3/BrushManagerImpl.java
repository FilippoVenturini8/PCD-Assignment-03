package ex3;


import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class BrushManagerImpl implements BrushManager {
    private static final int BRUSH_SIZE = 10;
    private static final int STROKE_SIZE = 2;
    private List<Brush> brushes = new java.util.ArrayList<>();

    @Override
    public synchronized void draw(final Graphics2D g) throws RemoteException{
        brushes.forEach(brush -> {
            try{
                g.setColor(new Color(brush.getColor()));
                var circle = new java.awt.geom.Ellipse2D.Double(brush.getX() - BRUSH_SIZE / 2.0, brush.getY() - BRUSH_SIZE / 2.0, BRUSH_SIZE, BRUSH_SIZE);
                // draw the polygon
                g.fill(circle);
                g.setStroke(new BasicStroke(STROKE_SIZE));
                g.setColor(Color.BLACK);
                g.draw(circle);
            }catch (RemoteException ex){
                System.err.println("Client exception: " + ex.toString());
                ex.printStackTrace();
            }
        });
    }

    @Override
    public synchronized void  addBrush(final Brush brush) throws RemoteException{
        brushes.add(brush);
    }

    @Override
    public synchronized void removeBrush(final Brush brush) throws RemoteException {
        brushes.remove(brush);
    }

}
