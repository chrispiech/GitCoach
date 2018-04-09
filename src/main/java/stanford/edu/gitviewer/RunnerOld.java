package stanford.edu.gitviewer;

import java.io.*;


import javax.tools.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.util.*;


public class RunnerOld {

	public static void run(Intermediate intermediate) {
		String code = intermediate.code;
		String className = getClassName(intermediate.filePath);

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		JavaFileObject compilationUnit =
				new StringJavaFileObject(className, code);

		SimpleJavaFileManager fileManager =
				new SimpleJavaFileManager(compiler.getStandardFileManager(null, null, null));

		JavaCompiler.CompilationTask compilationTask = compiler.getTask(
				null, fileManager, null, null, null, Arrays.asList(compilationUnit));

		compilationTask.call();

		CompiledClassLoader classLoader =
				new CompiledClassLoader(fileManager.getGeneratedOutputFiles());

		Class<?> karelTest;
		try {
			karelTest = classLoader.loadClass(className);
			Class<?>[] cArg = new Class<?>[0];
			Method run = karelTest.getMethod("main", String[].class);
			System.out.println(run);
			run.invoke(null, new Object[]{null});
			System.out.println("worked");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getClassName(String filePath) {
		File f = new File(filePath);
		return f.getName().split("\\.")[0];
	}

	private static class StringJavaFileObject extends SimpleJavaFileObject {
		private final String code;

		public StringJavaFileObject(String name, String code) {
			super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
					Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
			return code;
		}
	}

	private static class ClassJavaFileObject extends SimpleJavaFileObject {
		private final ByteArrayOutputStream outputStream;
		private final String className;

		protected ClassJavaFileObject(String className, Kind kind) {
			super(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind);
			this.className = className;
			outputStream = new ByteArrayOutputStream();
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			return outputStream;
		}

		public byte[] getBytes() {
			return outputStream.toByteArray();
		}

		public String getClassName() {
			return className;
		}
	}

	private static class SimpleJavaFileManager extends ForwardingJavaFileManager {
		private final List<ClassJavaFileObject> outputFiles;

		protected SimpleJavaFileManager(JavaFileManager fileManager) {
			super(fileManager);
			outputFiles = new ArrayList<ClassJavaFileObject>();
		}

		@Override
		public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
			ClassJavaFileObject file = new ClassJavaFileObject(className, kind);
			outputFiles.add(file);
			return file;
		}

		public List<ClassJavaFileObject> getGeneratedOutputFiles() {
			return outputFiles;
		}
	}

	private static class CompiledClassLoader extends ClassLoader {
		private final List<ClassJavaFileObject> files;

		private CompiledClassLoader(List<ClassJavaFileObject> files) {
			this.files = files;
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			Iterator<ClassJavaFileObject> itr = files.iterator();
			while (itr.hasNext()) {
				ClassJavaFileObject file = itr.next();
				if (file.getClassName().equals(name)) {
					itr.remove();
					byte[] bytes = file.getBytes();
					return super.defineClass(name, bytes, 0, bytes.length);
				}
			}
			return super.findClass(name);
		}
	}
}


