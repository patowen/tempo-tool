package net.patowen.songanalyzer;

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
