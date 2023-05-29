package ex3;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class PixelArtServer {

	public static int randomColor() {
		Random rand = new Random();
		return rand.nextInt(256 * 256 * 256);
	}

	public static void main(String[] args) {

		try {
			BrushManager brushManager = new BrushManagerImpl();
			BrushManager brushManagerStub = (BrushManager) UnicastRemoteObject.exportObject(brushManager, 0);

			Registry registry = LocateRegistry.getRegistry();

			PixelGrid grid = new PixelGridImpl(40,40);
			PixelGrid gridStub = (PixelGrid) UnicastRemoteObject.exportObject(grid, 0);

			registry.rebind("brushManager", brushManagerStub);
			registry.rebind("pixelGrid", gridStub);

			System.out.println("Server online");
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

}
