package ex3;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Arrays;

public class PixelGridImpl implements PixelGrid {
	private final int nRows;
	private final int nColumns;
	private final int[][] grid;
	
	public PixelGridImpl(final int nRows, final int nColumns) {
		this.nRows = nRows;
		this.nColumns = nColumns;
		grid = new int[nRows][nColumns];
	}

	@Override
	public synchronized void clear() throws RemoteException{
		for (int i = 0; i < nRows; i++) {
			Arrays.fill(grid[i], 0);
		}
	}
	
	@Override
	public synchronized void set(final int x, final int y, final int color) throws RemoteException {
		grid[y][x] = color;
	}
	
	@Override
	public synchronized int get(int x, int y) throws RemoteException {
		return grid[y][x];
	}
	
	@Override
	public synchronized int getNumRows() throws RemoteException {
		return this.nRows;
	}
	

	@Override
	public synchronized int getNumColumns() throws RemoteException {
		return this.nColumns;
	}
	
}
