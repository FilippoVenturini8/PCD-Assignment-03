package ex3;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

public class PixelArtClient {
    public static void main(String[] args){

        String host = (args.length < 1) ? null : args[0];
        try{
            Registry registry = LocateRegistry.getRegistry(host);

            BrushManager brushManager = (BrushManager) registry.lookup("brushManager");
            PixelGrid pixelGrid = (PixelGrid) registry.lookup("pixelGrid");

            Brush localBrush = new BrushImpl(0, 0, new Random().nextInt(256 * 256 * 256));
            brushManager.addBrush(localBrush);

            PixelGridView view = new PixelGridView(pixelGrid, brushManager, 800, 800);

            view.addMouseMovedListener((x, y) -> {
                localBrush.updatePosition(x, y);
                view.refresh();
            });

            view.addPixelGridEventListener((x, y) -> {
                pixelGrid.set(x, y, localBrush.getColor());
                view.refresh();
            });

            view.addColorChangedListener(localBrush::setColor);

            view.display();
        }catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
