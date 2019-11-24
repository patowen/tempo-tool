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

package net.patowen.tempotool.userinput;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputDictionary {
	private Map<InputType, List<InputMapping>> dictionary;
	private List<InputMapping> mappings;
	
	public InputDictionary() {
		mappings = new ArrayList<>();
	}
	
	public void addInputMapping(InputMapping inputMapping) {
		mappings.add(inputMapping);
	}
	
	public void constructDictionary() {
		dictionary = new HashMap<>();
		
		for (InputMapping mapping : mappings) {
			dictionary.putIfAbsent(mapping.getInputType(), new ArrayList<>());
			dictionary.get(mapping.getInputType()).add(mapping);
		}
	}
	
	public InputAction applyInput(InputType inputType, Point pos, double value) {
		List<InputMapping> candidateMappings = dictionary.get(inputType);
		
		if (candidateMappings == null) {
			return null;
		}
		
		for (InputMapping candidateMapping : candidateMappings) {
			if (candidateMapping.applyAction(pos, value)) {
				return candidateMapping.getInputAction();
			}
		}
		return null;
	}
}
