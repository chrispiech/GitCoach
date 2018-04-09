package stanford.karel;


import java.awt.Color;
import java.awt.Point;
import java.io.*;
import java.util.*;

import util.FileSystem;

public class Karel extends KarelCore {
	
	
	
	//----------------------------------------
	//          Karel API       
	//----------------------------------------

	public void run() {
	}
	
	public void move(){
		move_salt04251988();
	}

	public void turnLeft(){
		turnLeft_salt04251988();
	}

	public void turnRight(){
		turnRight_salt04251988();
	}

	public void turnAround(){
		turnAround_salt04251988();
	}

	public boolean frontIsClear(){
		return frontIsClear_salt04251988();
	}

	public boolean frontIsBlocked(){
		return frontIsBlocked_salt04251988();
	}

	public boolean leftIsClear(){
		return leftIsClear_salt04251988();
	}

	public boolean rightIsClear(){
		return rightIsClear_salt04251988();
	}

	public boolean leftIsBlocked(){
		return leftIsBlocked_salt04251988();
	}

	public boolean rightIsBlocked(){
		return rightIsBlocked_salt04251988();
	}

	public boolean beepersPresent(){
		return beepersPresent_salt04251988();
	}

	public boolean noBeepersPresent(){
		return noBeepersPresent_salt04251988();
	}

	public void pickBeeper(){
		pickBeeper_salt04251988();
	}

	public void putBeeper(){
		putBeeper_salt04251988();
	}

	public boolean beepersInBag() {
		return beepersInBag_salt04251988();
	}

	public boolean noBeepersInBag() {
		return noBeepersInBag_salt04251988();
	}

	public boolean facingNorth() {
		return facingNorth_salt04251988();
	}

	public boolean facingEast() {
		return facingEast_salt04251988();
	}

	public boolean facingSouth() {
		return facingSouth_salt04251988();
	}

	public boolean facingWest() {
		return facingWest_salt04251988();
	}

	public boolean notFacingNorth() { 
		return notFacingNorth_salt04251988();	
	}

	public boolean notFacingEast() {
		return notFacingEast_salt04251988();
	}	

	public boolean notFacingSouth() {
		return notFacingSouth_salt04251988();
	}

	public boolean notFacingWest() {
		return notFacingWest_salt04251988();	
	}
	
}
