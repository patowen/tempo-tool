/*
   Copyright 2019 Patrick Owen

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

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
