package ex3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

public class PixelArtClient {

    public static void main(String[] args) {
        int myBrushId = new Random().nextInt();
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1");
            BrushManager brushManager = (BrushManager) registry.lookup("brushManager");
            PixelGrid grid = (PixelGrid) registry.lookup("pixelGrid");

            brushManager.addBrush(new BrushImpl(0, 0, myBrushId, new Random().nextInt(256 * 256 * 256)));

            PixelGridView view = new PixelGridView(grid, brushManager, 800, 800);

            view.addMouseMovedListener((x, y) -> {
                brushManager.updateBrushPosition(myBrushId, x, y);
                view.refresh();
            });

            view.addPixelGridEventListener((x, y) -> {
                grid.set(x, y, brushManager.getBrush(myBrushId).getColor());
                view.refresh();
            });

            new javax.swing.Timer(33, e -> {
                view.refresh();
            }).start();

            view.addColorChangedListener(color -> {
                brushManager.changeBrushColor(myBrushId, color);
            });

            view.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    try {
                        brushManager.removeBrush(myBrushId);
                        System.exit(0);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            view.display();

        } catch (RemoteException | NotBoundException e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }
}