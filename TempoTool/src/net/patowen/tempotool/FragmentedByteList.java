package net.patowen.tempotool;

import java.util.ArrayList;

public class FragmentedByteList {
	private final ArrayList<byte[]> data;
	private final int blockSize;
	private int size;
	
	private byte[] lastBlock;
	private int sizeInBlock;
	
	public FragmentedByteList(int blockSize) {
		this.data = new ArrayList<>();
		this.blockSize = blockSize;
		this.size = 0;
		
		lastBlock = new byte[blockSize];
		data.add(lastBlock);
		sizeInBlock = 0;
	}
	
	public void add(byte item) {
		if (sizeInBlock == blockSize) {
			lastBlock = new byte[blockSize];
			data.add(lastBlock);
			sizeInBlock = 0;
		}
		
		lastBlock[sizeInBlock] = item;
		sizeInBlock++;
		size++;
	}
	
	public byte get(int index) {
		return data.get(index / blockSize)[index % blockSize];
	}
	
	public int size() {
		return size;
	}
}
