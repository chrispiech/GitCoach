package minions;


import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import javaEval.CharSequenceJavaFileObject;
import javaEval.ClassFileManager;
import javaEval.JavaSource;
import stanford.karel.Karel;
import stanford.karel.KarelConstants;
import stanford.karel.KarelException;
import stanford.karel.KarelState;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import util.FileSystem;
import util.Warnings;


/**
 * WARNING: This is the most complicated class to understand. Feel free to
 * contact Chris for questions...
 *
 */
public class KarelTester {

	private static final long TIMEOUT_SECONDS = 15;

	private static String className = "Submission";

	private static List<JavaFileObject> unitTestObjects = null;

	public static void setClassName(String className) {
		KarelTester.className = className;
	}
	
	public static boolean testCompile(String src, boolean silent) {
		return createCompiledUnit(src, silent) != null;
	}
	
	public static List<KarelState> getIntermediateStates(KarelState pre, String src) {
		JavaFileManager fileManager = createCompiledUnit(src, true);
		if(fileManager == null) {
			Warnings.error("Program did not compile");
		}

		// Creating an instance of our compiled unit tester and
		// running its call() method
		Karel studentInstance = null;
		try {
			String className = KarelTester.className;
			ClassLoader loader = fileManager.getClassLoader(null);
			Class<?> tester = loader.loadClass(className);
			studentInstance = (Karel) tester.newInstance();
			studentInstance.loadPrecondition_salt25041988(pre);
			executeProgram(studentInstance);
			return studentInstance.getAllStates_salt25041988();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (KarelException e) {
			return studentInstance.getAllStates_salt25041988();
		} 
		return null;
	}

	public static KarelState getPostcondition(KarelState pre, String src) {
		JavaFileManager fileManager = createCompiledUnit(src, true);
		if(fileManager == null) {
			//Warnings.error("Program did not compile");
			return null;
		}

		// Creating an instance of our compiled unit tester and
		// running its call() method
		Karel studentInstance = null;
		try {
			String className = KarelTester.className;
			ClassLoader loader = fileManager.getClassLoader(null);
			Class<?> tester = loader.loadClass(className);
			studentInstance = (Karel) tester.newInstance();
			studentInstance.loadPrecondition_salt25041988(pre);
			//studentInstance.putBeeper();
			executeProgram(studentInstance);
			return studentInstance.getState_salt25041988();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (KarelException e) {
			return studentInstance.getState_salt25041988();
		} 
		return null;
	}

	private static JavaFileManager createCompiledUnit(String src, boolean silent) {
		List<JavaSource> srcToAdd = new ArrayList<JavaSource>();

		try {
			// add the students code
			srcToAdd.add(new JavaSource(KarelTester.className, src));
		} catch(Exception e) {
			System.out.println(e);
			System.out.println("failed to create java for ast");
			throw new RuntimeException("test");
		}

		// Make the program file system
		return getCompiledUnit(srcToAdd, silent);
	}

	private static void executeProgram(final Karel studentInstance) {
		ExecutorService executor = Executors.newCachedThreadPool();
		Callable<Object> task = new Callable<Object>() {
		   public Object call() {
		      studentInstance.run();
		      return null;
		   }
		};
		Future<Object> future = executor.submit(task);
		try {
		   future.get(); 
		} catch (InterruptedException e) {
			Warnings.error("who interrupted you?");
		} catch (ExecutionException e) {
		} finally {
		   future.cancel(true); // may or may not desire this
		}
		
		executor.shutdown();

	}

	private static JavaFileManager getCompiledUnit(List<JavaSource> srcToAdd, boolean silent) {
		// We get an instance of JavaCompiler. Then
		// we create a file manager
		// (our custom implementation of it)
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		JavaFileManager fileManager = new
				ClassFileManager(compiler
						.getStandardFileManager(null, null, null));

		// Dynamic compiling requires specifying
		// a list of "files" to compile. 
		List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
		if(unitTestObjects == null) {
			setUnitTestObjects();
		}
		for(JavaFileObject javaFile : unitTestObjects) {
			jfiles.add(javaFile);
		}

		for(JavaSource javaSource : srcToAdd) {
			String src =javaSource.getSource();
			String className = javaSource.getClassName();
			jfiles.add(new CharSequenceJavaFileObject(className, src));
		}

		// We specify a task to the compiler. Compiler should use our file
		// manager and our list of "files".
		// Then we run the compilation with call()
		Writer output = null;
		if(silent) {
			output = new StringWriter();
		}
		boolean compiled = compiler.getTask(output, fileManager, null, null,
				null, jfiles).call();

		// It must compile!!! Otherwise we are going to return you null
		if(!compiled) {
			return null;
		}

		return fileManager;
	}

	private static void setUnitTestObjects() {
		unitTestObjects = new ArrayList<JavaFileObject>();

		File srcDir = new File("src");
		File blockySrcDir = new File(srcDir, "karel");

		for(File f : FileSystem.listFiles(blockySrcDir)) {
			if(FileSystem.getExtension(f).equals("java")) {
				String className = FileSystem.getNameWithoutExtension(f);
				String src = FileSystem.getFileContents(f);
				JavaFileObject javaFile = new CharSequenceJavaFileObject(className, src);
				unitTestObjects.add(javaFile);
			}
		}
	}





}
