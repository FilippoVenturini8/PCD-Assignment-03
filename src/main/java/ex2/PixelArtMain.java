package ex2;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class PixelArtMain {



	public static void main(String[] args) {
		try{
			var brushManager = new BrushManager();

			PixelGrid grid = new PixelGrid(40,40);
			PixelGridView view = new PixelGridView(grid, brushManager, 800, 800);

			ConnectionHandler connectionHandler = new ConnectionHandler(brushManager, grid, view);

			view.addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					try {
						connectionHandler.disconnect();
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				}
			});

			view.addMouseMovedListener(connectionHandler::sendNewPosition);

			view.addPixelGridEventListener(connectionHandler::sendPaintedPixel);

			view.addColorChangedListener(connectionHandler::sendNewColor);

			connectionHandler.connect();

			view.display();
		}catch (IOException | TimeoutException e) {
			throw new RuntimeException(e);
		}

	}

}
