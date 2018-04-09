package stanford.karel;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import util.FileSystem;

public class KarelCore implements KarelConstants {

	public int col;
	public int row;
	public int direction;
	public int beepersInBag;
	public KarelWorld world;
	public int status;
	public int instructionCount;

	protected String cmdString = "";
	protected Stack<KarelState> preconditions;
	protected Map<String, List<PrePost>> prePostMap; //before and after methods
	protected List<KarelState> allStates;
	protected File worldFile;

	public static final java.awt.Color BLACK = Color.BLACK;
	public static final java.awt.Color BLUE = Color.BLUE;
	public static final java.awt.Color CYAN = Color.CYAN;
	public static final java.awt.Color DARK_GRAY = Color.DARK_GRAY;
	public static final java.awt.Color GRAY = Color.GRAY;
	public static final java.awt.Color GREEN = Color.GREEN;
	public static final java.awt.Color LIGHT_GRAY = Color.LIGHT_GRAY;
	public static final java.awt.Color MAGENTA = Color.MAGENTA;
	public static final java.awt.Color ORANGE = Color.ORANGE;
	public static final java.awt.Color PINK = Color.PINK;
	public static final java.awt.Color RED = Color.RED;
	public static final java.awt.Color WHITE = Color.WHITE;
	public static final java.awt.Color YELLOW = Color.YELLOW;

	public KarelCore(){
		world = new KarelWorld();
		preconditions = new Stack<KarelState>();
		status = RUNNING;
		instructionCount=0;
		beepersInBag =INFINITY;
		prePostMap = new HashMap<String, List<PrePost>>();
		allStates = new LinkedList<KarelState>();
	}
	
	//----------------------------------------
	//          KAREL API
	//----------------------------------------
	public void move_salt04251988(){
		if(!checkRunning_salt25041988()) return;
		int nX = col + dx_salt04251988(direction);
		int nY = row + dy_salt04251988(direction);
		if(world.w[col][row].walls[direction] || nX<0 || nX >= world.numCols || nY<0 || nY>=world.numRows){
			moveError_salt04251988();
			return;
		}
		col=nX;
		row=nY;
		//		System.out.println(""+X+" "+Y+" "+B);
		cmdString += "M";
		allStates.add(new KarelState(this, worldFile));
	}

	public void turnLeft_salt04251988(){
		if(!checkRunning_salt25041988()) return;
		direction = (direction+3)%4;
		cmdString += "L";
		allStates.add(new KarelState(this, worldFile));
	}

	public void turnRight_salt04251988(){
		if(!checkRunning_salt25041988()) return;
		direction = (direction+1)%4;
		cmdString += "R";
		allStates.add(new KarelState(this, worldFile));
	}

	public void turnAround_salt04251988(){
		if(!checkRunning_salt25041988()) return;
		direction = (direction+2)%4;
		cmdString += "RR";
		allStates.add(new KarelState(this, worldFile));
	}

	public boolean frontIsClear_salt04251988(){
		cmdString += "F";
		if(!checkRunning_salt25041988()) return false;
		return !world.w[col][row].walls[direction];
	}

	public boolean frontIsBlocked_salt04251988(){
		cmdString += "F";
		if(!checkRunning_salt25041988()) return false;
		return world.w[col][row].walls[direction];
	}

	public boolean leftIsClear_salt04251988(){
		cmdString += "Q";
		if(!checkRunning_salt25041988()) return false;
		return !world.w[col][row].walls[(direction+3)%4];
	}

	public boolean rightIsClear_salt04251988(){
		cmdString += "P";
		if(!checkRunning_salt25041988()) return false;
		return !world.w[col][row].walls[(direction+1)%4];
	}

	public boolean leftIsBlocked_salt04251988(){
		cmdString += "Q";
		if(!checkRunning_salt25041988()) return false;
		return world.w[col][row].walls[(direction+3)%4];
	}

	public boolean rightIsBlocked_salt04251988(){
		cmdString += "P";
		if(!checkRunning_salt25041988()) return false;
		return world.w[col][row].walls[(direction+1)%4];
	}

	public boolean beepersPresent_salt04251988(){
		cmdString += "B";
		if(!checkRunning_salt25041988()) return false;
		return world.w[col][row].nBeepers>0;
	}

	public boolean noBeepersPresent_salt04251988(){
		cmdString += "B";
		if(!checkRunning_salt25041988()) return false;
		return world.w[col][row].nBeepers==0;
	}

	public void pickBeeper_salt04251988(){
		if(!checkRunning_salt25041988()) return;
		if(world.w[col][row].nBeepers==0){
			pickBeeperError_salt04251988();
			return ;
		}
		world.w[col][row].nBeepers--;
		beepersInBag++;
		cmdString += "T";
		allStates.add(new KarelState(this, worldFile));
	}

	public void putBeeper_salt04251988(){
		if(!checkRunning_salt25041988()) return;
		if(beepersInBag==0) {
			putBeeperError_salt04251988();
			return;
		}
		world.w[col][row].nBeepers++;
		beepersInBag--;
		cmdString += "A";
		allStates.add(new KarelState(this, worldFile));
	}

	public boolean beepersInBag_salt04251988() {
		if(!checkRunning_salt25041988()) return false;
		return beepersInBag>0;	
	}

	public boolean noBeepersInBag_salt04251988() {
		if(!checkRunning_salt25041988()) return false;
		return beepersInBag==0;	
	}

	public boolean facingNorth_salt04251988() {
		cmdString += "1";
		if(!checkRunning_salt25041988()) return false;
		return direction==NORTH;	
	}

	public boolean facingEast_salt04251988() {
		cmdString += "2";
		if(!checkRunning_salt25041988()) return false;
		return direction==EAST;	
	}

	public boolean facingSouth_salt04251988() {
		cmdString += "3";
		if(!checkRunning_salt25041988()) return false;
		return direction==SOUTH;	
	}

	public boolean facingWest_salt04251988() {
		cmdString += "4";
		if(!checkRunning_salt25041988()) return false;
		return direction==WEST;	
	}

	public boolean notFacingNorth_salt04251988() { 
		cmdString += "1";
		if(!checkRunning_salt25041988()) return false;
		return direction!=NORTH;	
	}

	public boolean notFacingEast_salt04251988() {
		cmdString += "2";
		if(!checkRunning_salt25041988()) return false;
		return direction!=EAST;	
	}	

	public boolean notFacingSouth_salt04251988() {
		cmdString += "3";
		if(!checkRunning_salt25041988()) return false;
		return direction!=SOUTH;
	}

	public boolean notFacingWest_salt04251988() {
		cmdString += "4";
		if(!checkRunning_salt25041988()) return false;
		return direction!=WEST;	
	}

	public boolean cornerColorIs_salt04251988(Color c){
		if(!checkRunning_salt25041988()) return false;
		return c==world.w[col][row].c;
	}

	public void paintCorner_salt04251988(Color c){
		if(!checkRunning_salt25041988()) return;
		world.w[col][row].c=c;
	}
	
	//----------------------------------------
	//          SALTED PUBLIC METHODS        
	//----------------------------------------

	public void run_salt25041988() {
		
	}
	
	public List<KarelState> getAllStates_salt25041988() {
		return allStates;
	}
	
	public Map<String, List<PrePost>> getPrePostMap_salt25041988() {
		return prePostMap;
	}

	public String getCmdString_salt25041988() {
		return cmdString;
	}
	
	public int getNumInstructions_salt25041988() {
		return instructionCount;
	}

	public void load_salt25041988(KarelState pre){
		if(pre.getWorldFile() != null) {
			loadWorldFile_salt25041988(pre.getWorldFile());
		} else {
			reset_salt04251988();
			this.world = new KarelWorld(pre.getWorld());
		}
		
		
	}

	public void loadWorldFile_salt25041988(File worldFile) {
		this.worldFile = worldFile;
		try{
			reset_salt04251988();
			BufferedReader rd = new BufferedReader(new FileReader(worldFile));
			while(true){
				String line = rd.readLine();
				if(line==null) break;
				String cmd = line.substring(0,line.indexOf(':')).toUpperCase();
				processCommand_salt04251988(cmd,line.substring(cmd.length()+2));
			}
			rd.close();
		} catch(IOException e){
			String worldPath = worldFile.getPath();
			System.out.println("Karel crashed trying to load the world from file "+worldPath+".");
		}
	}

	public void beforeMethod_salt25041988(String name) {
		KarelState precondition = new KarelState(this, worldFile, name);
		preconditions.push(precondition);
	}

	public void afterMethod_salt25041988(String name) {
		// Get the corresponding precondition
		KarelState postcondition = new KarelState(this, worldFile, name);
		KarelState precondition = preconditions.pop();
		String preMethod = precondition.getCurrentMethod();
		if(preMethod != name) {
			throw new RuntimeException("names must match");
		}

		// Add the pre/post combination to the pre/post map
		if(!prePostMap.containsKey(name)) {
			prePostMap.put(name, new LinkedList<PrePost>());
		}
		PrePost newPrePost = new PrePost(precondition, postcondition);
		prePostMap.get(name).add(newPrePost);
	}

	public boolean checkRunning_salt25041988(){

		instructionCount++;
		if(instructionCount>MAX_INSTRUCTIONS){
			infiniteLoopError_salt04251988();
		}
		return true;
	}
	
	public void loadPrecondition_salt25041988(KarelState pre) {
		// load static elements
		load_salt25041988(pre);

		status = pre.getStatus();
		col = pre.getKarelCol();
		row = pre.getKarelRow();
		direction = pre.getDirection();
		world.setDynamicElements(pre);
	}
	
	public KarelState getState_salt25041988() {
		return new KarelState(this, worldFile);
	}

	//----------------------------------------
	//          PRIVATE SALTED METHODS        
	//----------------------------------------

	protected void processCommand_salt04251988(String cmd,String line){
		if(cmd.equals("BEEPER")) world.beeperCommand(toPoint_salt04251988(line.substring(0,line.indexOf(')')+1)),getInt_salt04251988(line.substring(line.indexOf(')')+2)));
		else if(cmd.equals("WALL")) world.wallCommand(toPoint_salt04251988(line.substring(0,line.indexOf(')')+1)),toDir_salt04251988(line.substring(line.indexOf(')')+2).toUpperCase()));
		else if(cmd.equals("DIMENSION")) world.dimensionCommand(toPoint_salt04251988(line.substring(0,line.indexOf(')')+1)));
		else if(cmd.equals("BEEPERBAG")) bagCommand_salt04251988(getInt_salt04251988(line));
		else if(cmd.equals("KAREL")) karelCommand_salt04251988(toPoint_salt04251988(line.substring(0,line.indexOf(')')+1)),toDir_salt04251988(line.substring(line.indexOf(')')+2).toUpperCase()));
	}

	protected int getInt_salt04251988(String s){
		if(s.toUpperCase().startsWith("INF")) return INFINITY;
		return Integer.valueOf(s).intValue();
	}

	protected Point toPoint_salt04251988(String s){
		return new Point(Integer.valueOf(s.substring(1,s.indexOf(','))).intValue()-1,Integer.valueOf(s.substring(s.indexOf(' ')+1,s.indexOf(')'))).intValue()-1);
	}

	protected void karelCommand_salt04251988(Point p,int d){
		col = p.x;
		row = p.y;
		direction = d;
	}

	protected void bagCommand_salt04251988(int n){
		beepersInBag = n;
	}

	protected int dx_salt04251988(int d){
		if(d==NORTH||d==SOUTH) return 0;
		if(d==EAST) return 1;
		return -1;
	}

	protected int dy_salt04251988(int d){
		if(d==EAST||d==WEST) return 0;
		if(d==NORTH) return 1;
		return -1;
	}

	protected int toDir_salt04251988(String dir){
		if(dir.equals("NORTH")) return NORTH;
		if(dir.equals("SOUTH")) return SOUTH;
		if(dir.equals("EAST")) return EAST;
		return WEST;
	}

	protected void reset_salt04251988(){
		status=RUNNING;
		beepersInBag=INFINITY;
		col=0;
		row=0;
		direction=EAST;
		instructionCount=0;
		cmdString = "";
	}

	protected void moveError_salt04251988(){
		status=WALL_CRASH;
		cmdString += "E";
		throw new KarelException();
	}

	protected void pickBeeperError_salt04251988(){
		status=PICK_CRASH;
		cmdString += "E";
		throw new KarelException();
	}

	protected void putBeeperError_salt04251988(){
		status=PUT_CRASH;
		cmdString += "E";
		throw new KarelException();
	}

	protected void infiniteLoopError_salt04251988(){
		status=INFINITE_LOOP;
		cmdString += "I";
		throw new KarelException();
	}
}
