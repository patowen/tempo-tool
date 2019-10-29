package net.patowen.songanalyzer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
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
	
	private boolean configFileValid;
	
	public Config() {
		this(Paths.get("config.txt"));
	}
	
	public Config(Path configFile) {
		this.configFile = configFile;
		initConfigEntries();
	}
	
	public interface Keys {
		String DEFAULT_FILE = "defaultFile"; // Which file to open when launching the program
		String DEFAULT_FOLDER = "defaultFolder"; // Which folder to open by default when saving or loading
		String DEFAULT_SONG_FOLDER = "defaultSongFolder"; // Which folder to open by default when loading sound files
	}
	
	private void initConfigEntries() {
		addConfigEntryPath(Keys.DEFAULT_FILE, null);
		addConfigEntryPath(Keys.DEFAULT_FOLDER, Paths.get(""));
		addConfigEntryPath(Keys.DEFAULT_SONG_FOLDER, Paths.get(""));
	}
	
	public boolean loadConfig() {
		// This flag is used to determine whether it is safe to overwrite a
		// config file.
		configFileValid = false;
		
		// Create an empty config file if none exists. This makes sure that if
		// there's an invisible config file unrelated to the program before it
		// is launch, it refuses to mess with it.
		if (Files.notExists(configFile)) {
			try {
				Files.createFile(configFile);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		// If the config file passed to this program cannot be parsed, it may
		// belong to a different program and should not be overwritten.
		try (Scanner scanner = new Scanner(configFile)) {
			while (scanner.hasNextLine()) {
				if (!parseLine(scanner.nextLine())) {
					return false;
				}
			}
			
			configFileValid = true;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void saveConfig() {
		if (configFileValid) {
			try (BufferedWriter writer = Files.newBufferedWriter(configFile)) {
				for (String key : configEntries.keySet()) {
					String line = serializeLine(key, configEntries.get(key));
					if (line != null) {
						writer.write(line);
						writer.write(System.lineSeparator());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean parseLine(String line) {
		int colonPos = line.indexOf(':');
		if (colonPos == -1) {
			return false;
		}
		String key = line.substring(0, colonPos);
		String valueStr = line.substring(colonPos+1).trim();
		
		ConfigEntry configEntry = configEntries.get(key);
		if (key == null) {
			return false;
		}
		
		return configEntry.parseValue(valueStr);
	}
	
	private String serializeLine(String key, ConfigEntry configEntry) {
		String valueStr = configEntry.serializeValue();
		if (valueStr == null) {
			return null;
		}
		return key + ": " + valueStr;
	}
	
	public static interface ConfigEntry {
		public boolean parseValue(String valueStr);
		public String serializeValue();
	}
	
	private static class ConfigEntryPath implements ConfigEntry {
		private Path value;
		
		public ConfigEntryPath(Path defaultValue) {
			this.value = defaultValue;
		}
		
		@Override
		public boolean parseValue(String valueStr) {
			int len = valueStr.length();
			if (len < 2) {
				return false;
			}
			if (valueStr.charAt(0) != '\"' || valueStr.charAt(len-1) != '\"') {
				return false;
			}
			value = Paths.get(valueStr.substring(1, len-1));
			return true;
		}
		
		@Override
		public String serializeValue() {
			if (value == null) {
				return null;
			}
			return "\"" + value.toString() + "\"";
		}
		
		public Path getValue() {
			return value;
		}
		
		public void setValue(Path value) {
			this.value = value;
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
	
	public void setConfigEntryPath(String key, Path value) {
		configEntryPaths.get(key).setValue(value);
	}
}
