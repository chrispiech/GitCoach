package stanford.karel;


public interface KarelConstants {

	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;

	public static final int RUNNING = 0;
	public static final int WALL_CRASH = 1;
	public static final int PUT_CRASH = 2;
	public static final int PICK_CRASH = 3;
	public static final int INFINITE_LOOP = 4;

	public static final int MAX_INSTRUCTIONS = 10000;
	public static final int INFINITY = 999999999;

}
