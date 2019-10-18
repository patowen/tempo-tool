package net.patowen.songanalyzer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Config {
	private Map<String, ConfigEntryPath> configEntryPaths = new HashMap<>();
	private Map<String, ConfigEntry> configEntries = new TreeMap<>();
	
	private Path configFile;
	
	public Config() {
		this(Paths.get("config.txt"));
	}
	
	public Config(Path configFile) {
		this.configFile = configFile;
		initConfigEntries();
	}
	
	private interface Keys {
		String DEFAULT_FILE = "defaultFile"; // Which file to open when launching the program
		String DEFAULT_FOLDER = "defaultFolder"; // Which folder to open when launching the program
	}
	
	private void initConfigEntries() {
		addConfigEntryPath(Keys.DEFAULT_FILE, null);
		addConfigEntryPath(Keys.DEFAULT_FOLDER, Paths.get(""));
	}
	
	public void loadConfig() {
		try (Scanner scanner = new Scanner(configFile)) {
			while (scanner.hasNextLine()) {
				parseLine(scanner.nextLine());
			}
		} catch (IOException e) {}
	}
	
	public void saveConfig() {
		for (String key : configEntries.keySet()) {
			serializeLine(key, configEntries.get(key));
		}
	}
	
	// Ignores lines that cannot be parsed
	private void parseLine(String line) {
		int colonPos = line.indexOf(':');
		if (colonPos == -1) {
			return;
		}
		String key = line.substring(0, colonPos);
		String valueStr = line.substring(colonPos+1);
		
		ConfigEntry configEntry = configEntries.get(key);
		if (key == null) {
			return;
		}
		
		configEntry.parseValue(valueStr);
	}
	
	private String serializeLine(String key, ConfigEntry configEntry) {
		return key + ":" + configEntry.serializeValue();
	}
	
	public static interface ConfigEntry {
		public void parseValue(String valueStr);
		public String serializeValue();
	}
	
	private static class ConfigEntryPath implements ConfigEntry {
		private Path value;
		
		public ConfigEntryPath(Path defaultValue) {
			this.value = defaultValue;
		}
		
		public void parseValue(String valueStr) {
			value = Paths.get(valueStr);
		}
		
		public String serializeValue() {
			return value.toString();
		}
		
		public Path getValue() {
			return value;
		}
	}
	
	private void addConfigEntryPath(String key, Path defaultValue) {
		ConfigEntryPath configPath = new ConfigEntryPath(defaultValue);
		configEntries.put(key, configPath);
		configEntryPaths.put(key, configPath);
	}
	
	public Path getConfigEntryPath(String key) {
		return configEntryPaths.get(key).getValue();
	}
}
