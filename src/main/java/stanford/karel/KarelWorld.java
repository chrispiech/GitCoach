package stanford.karel;


import java.awt.Color;
import java.awt.Point;
import java.io.*;
import java.util.*;

import org.json.JSONObject;

public class KarelWorld implements KarelConstants {
	
	public int numCols,numRows;
	
	// IMPORTANT NOTE: Matrix is in col major order :/
	// IMPORTANT NOTE: Row 0 is the bottom row :/
	public KarelSquare[][] w;
	

	public KarelWorld() {
		// TODO Auto-generated constructor stub
	}
	
	public KarelWorld(int rows, int cols) {
		this.numCols = cols;
		this.numRows = rows;
		this.w = new KarelSquare[numCols][numRows];
		for(int x = 0; x < numCols; x++) {
			for(int y = 0; y < numRows; y++){
				w[x][y] = new KarelSquare();
			}
		}
	}
	
	public KarelWorld(KarelWorld toCopy){
		this.numCols = toCopy.numCols;
		this.numRows = toCopy.numRows;
		this.w = new KarelSquare[numCols][numRows];
		for(int x = 0; x < numCols; x++) {
			for(int y = 0; y < numRows; y++){
				w[x][y] = new KarelSquare(toCopy.w[x][y]);
			}
		}
	}
	
	public KarelWorld(JSONObject json) {
		this.numCols = json.getInt("numCols");
		this.numRows = json.getInt("numRows");
		this.w = new KarelSquare[numCols][numRows];
		JSONObject squares = json.getJSONObject("squares");
		for(int x = 0; x < numCols; x++) {
			for(int y = 0; y < numRows; y++) {
				String squareKey = getSquareKey(x, y);
				if(squares.has(squareKey)) {
					JSONObject squareJson = squares.getJSONObject(squareKey);
					this.w[x][y] = new KarelSquare(squareJson);
				} else {
					this.w[x][y] = new KarelSquare();
				}
			}
		}
	}

	private static String getSquareKey(int x, int y) {
		return x + "," + y;
	}
	
	public JSONObject getJson() {
		JSONObject root = new JSONObject();
		root.put("numCols", this.numCols);
		root.put("numRows", this.numRows);
		JSONObject squares = new JSONObject();
		for(int x = 0; x < numCols; x++) {
			for(int y = 0; y < numRows; y++) {
				KarelSquare sq = w[x][y];
				if(!sq.isEmpty()) {
					String key = getSquareKey(x, y);
					squares.put(key, sq.getJson());
				}
			}
		}
		root.put("squares", squares);
		return root;
	}

	public void initializeBlankWorld(int myX,int myY){
		numCols=myX;
		numRows=myY;
		w = new KarelSquare[numCols][numRows];
		for(int x=0;x<numCols;x++){
			for(int y=0;y<numRows;y++){
				w[x][y] = new KarelSquare();
			}
		}
		for(int x=0;x<numCols;x++){
			w[x][0].walls[SOUTH]=true;
			w[x][numRows-1].walls[NORTH]=true;
		}
		for(int y=0;y<numRows;y++){
			w[0][y].walls[WEST]=true;
			w[numCols-1][y].walls[EAST]=true;
		}
	}

	public void beeperCommand(Point p,int n){
		w[p.x][p.y].nBeepers=n;
	}

	public void wallCommand(Point p,int d){
		w[p.x][p.y].walls[d]=true;
		w[p.x+dx(d)][p.y+dy(d)].walls[(d+2)%4]=true;
	}

	private int dx(int d){
		if(d==NORTH||d==SOUTH) return 0;
		if(d==EAST) return 1;
		return -1;
	}

	private int dy(int d){
		if(d==EAST||d==WEST) return 0;
		if(d==NORTH) return 1;
		return -1;
	}


	public void dimensionCommand(Point p){
		initializeBlankWorld(p.x+1,p.y+1);
	}

	public void print(){
		for(int y=numRows-1;y>=0;y--){
			String line=" ";
			for(int x=0;x<numCols;x++){
				if(w[x][y].walls[NORTH]) line+="-";
				else line+=" ";
				line+="  ";
			}
			System.out.println(line);
			line = "";
			for(int x=0;x<numCols;x++){
				if(w[x][y].walls[WEST]) line+="|";
				else line+=" ";
				line += ""+w[x][y].nBeepers;
				if(w[x][y].walls[EAST]) line+="|";
				else line+=" ";
			}
			System.out.println(line);
			line=" ";
			for(int x=0;x<numCols;x++){
				if(w[x][y].walls[SOUTH]) line+="-";
				else line+=" ";
				line+="  ";
			}

			System.out.println(line);
		}
	}
	
	@Override
	public String toString() {
		return getJson().toString(3);
	}
	
	@Override
	public int hashCode() {
		List<Integer> hashes = new ArrayList<Integer>();
		for(int x = 0; x < numCols; x++) {
			for(int y = 0; y < numRows; y++) {
				hashes.add(w[x][y].hashCode());
			}
		}
		return hashes.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		KarelWorld other = (KarelWorld) o;
		for(int x = 0; x < numCols; x++) {
			for(int y = 0; y < numRows; y++) {
				if(!w[x][y].equals(other.w[x][y])) {
					return false;
				}
			}
		}
		return true;
	}

	public void setDynamicElements(KarelState pre) {
		KarelWorld preWorld = pre.getWorld();
		for(int x = 0; x < numCols; x++) {
			for(int y = 0; y < numRows; y++) {
				KarelSquare preWorldSquare = preWorld.w[x][y];
				w[x][y].setDynamicElements(preWorldSquare);
			}
		}
		
	}

	public int getNumBeepers(int col, int row) {
		return w[col][row].nBeepers;
	}
	
}
