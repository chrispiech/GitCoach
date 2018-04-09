package stanford.karel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class KarelSquare implements KarelConstants{

	public int nBeepers;
	public boolean[] walls;
	public Color c;

	public KarelSquare(){
		nBeepers=0;
		walls = new boolean[4];
		for(int i=NORTH;i<=WEST;i++) walls[i]=false;
		c=null;
	}

	public KarelSquare(KarelSquare toCopy) {
		this.nBeepers = toCopy.nBeepers;
		this.c = toCopy.c;
		this.walls = new boolean[toCopy.walls.length];
		System.arraycopy(toCopy.walls, 0, this.walls, 0, toCopy.walls.length);
	}

	public KarelSquare(JSONObject json) {
		nBeepers=0;
		walls = new boolean[4];
		for(int i=NORTH;i<=WEST;i++) walls[i]=false;
		c=null;
		if(json.has("nBeepers")) {
			this.nBeepers = json.getInt("nBeepers");
		}
		if(json.has("color")) {
			int rgb = json.getInt("color");
			this.c = new Color(rgb);
		}
	}

	public JSONObject getJson() {
		JSONObject root = new JSONObject();
		if(nBeepers > 0) {
			root.put("nBeepers", nBeepers);
		}
		if(c != null) {
			root.put("color", c.getRGB());
		}
		return root;
	}

	public boolean isEmpty() {
		if(c != null) return false;
		if(nBeepers != 0) return false;
		return true;
	}
	
	public void setDynamicElements(KarelSquare other) {
		this.nBeepers = other.nBeepers;
		this.c = other.c;
	}

	@Override
	public int hashCode() {
		List<Integer> hashes = new ArrayList<Integer>();
		hashes.add(nBeepers);
		if(c != null) {
			hashes.add(c.getRGB());
		}
		return hashes.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		KarelSquare other = (KarelSquare)o;
		if(other.nBeepers != nBeepers) return false;
		if(c == null) {
			if(other.c != null) return false;
		} else {
			if(other.c.getRGB() != c.getRGB()) return false;
		}
		return true;
	}

}