package stanford.edu.gitviewer;

import java.io.*;


import javax.tools.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

import minions.KarelTester;
import stanford.karel.KarelState;

import java.util.*;


public class Runner {

	public static void run(Intermediate intermediate) {
		
		System.out.println("Commit: " +intermediate.commitIndex);
		// Get ready for a class of this classname
		String className = getClassName(intermediate.filePath);
		KarelTester.setClassName(className);

		// The code, as a string
		String src = intermediate.code;

		// Record if it compiles. If not, nothing much we can do
		
		// Just relevant to Midpoint Karel for now
		KarelState pre = KarelState.getDefaultWorld();
		KarelState post = KarelTester.getPostcondition(pre, src);
		
		intermediate.compiles = post != null;
		intermediate.run = true;

	}

	private static String getClassName(String filePath) {
		File f = new File(filePath);
		return f.getName().split("\\.")[0];
	}

}


