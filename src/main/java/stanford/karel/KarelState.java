package stanford.karel;



import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;


public class KarelState {

	private KarelWorld world = null;
	private int karelCol;
	private int karelRow;
	private int direction;
	private int status;
	private File worldFile;
	
	// something unexpected
	private String currentMethod;
	
	public static KarelState getDefaultWorld() {
		KarelState defaultWorld = new KarelState();
		return defaultWorld;
	}
	
	public KarelState(File worldFile) {
		KarelCore core = new KarelCore();
		core.loadWorldFile_salt25041988(worldFile);
		init(core, worldFile, null);
	}
	
	public KarelState() {
		this.world = new KarelWorld(8, 8);
		this.karelCol = 0;
		this.karelRow = 0;
		this.direction = Karel.EAST;
		this.status = Karel.RUNNING;
		this.worldFile = null;
		this.currentMethod = null;
	}
	
	public KarelState(KarelCore k, File worldFile) {
		this(k, worldFile, null);
	}
	
	public KarelState(KarelCore k, File worldFile, String currentMethod) {
		init(k, worldFile, currentMethod);
	}

	private void init(KarelCore k, File worldFile, String currentMethod) {
		this.world = new KarelWorld(k.world);
		this.karelCol = k.col;
		this.karelRow = k.row;
		this.direction = k.direction;
		this.status = k.status;
		this.worldFile = worldFile;
		this.currentMethod = currentMethod;
	}
	
	public KarelState(JSONObject json) {
		this.karelCol = json.getInt("col");
		this.karelRow = json.getInt("row");
		this.direction = json.getInt("direction");
		this.status = json.getInt("status");
		String worldPath = json.getString("worldPath");
		this.worldFile = new File(worldPath);
		this.world = new KarelWorld(json.getJSONObject("world"));
	}

	public JSONObject getJson() {
		JSONObject root = new JSONObject();
		//root.put("beepersInBag", beepersInBag);
		root.put("col", karelCol);
		root.put("row", karelRow);
		root.put("direction", direction);
		root.put("status", status);
		//root.put("worldPath", getWorldPath());
		root.put("world", world.getJson());
		return root;
	}
	
	public String getWorldPath() {
		return worldFile.getName();
	}
	
	public File getWorldFile() {
		return worldFile;
	}
	
	@Override 
	public int hashCode() {
		List<Integer> intValues = new ArrayList<Integer>();
		intValues.add(karelCol);
		intValues.add(karelRow);
		intValues.add(status);
		intValues.add(direction);
		intValues.add(world.hashCode());
		intValues.add(getWorldPath().hashCode());
		return intValues.hashCode();
	}
	
	public KarelWorld getWorld() {
		return world;
	}

	public int getKarelCol() {
		return karelCol;
	}

	public int getKarelRow() {
		return karelRow;
	}

	public int getDirection() {
		return direction;
	}

	public int getStatus() {
		return status;
	}

	@Override
	public boolean equals(Object o) {
		KarelState other = (KarelState)o;
		if(karelCol != other.karelCol) return false;
		if(karelRow != other.karelRow) return false;
		if(status != other.status) return false;
		if(direction != other.direction) return false;
		if(!getWorldPath().equals(other.getWorldPath())) return false;
		if(!world.equals(other.world)) return false;
		return true;
	}
	
	@Override
	public String toString() {
		return getJson().toString(2);
	}

	public String getCurrentMethod() {
		return currentMethod;
	}

	
}
