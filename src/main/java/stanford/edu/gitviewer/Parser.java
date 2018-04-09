package stanford.edu.gitviewer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.TokenMgrError;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import minions.KarelParser;
import minions.KarelTester;

public class Parser {

	public static void parse(Intermediate intermediate) {
		String code = intermediate.code;

		// first collect indentation errors
		try {
			KarelParser parser = new KarelParser();
			parser.parse(code);
			intermediate.indentationErrors = parser.getIndentationErrors();
		} catch(Exception e) {
			intermediate.indentationErrors = -1;
		}
		InputStream in = null;
		CompilationUnit cu = null;
		in = new ByteArrayInputStream(code.getBytes());
		try{
			cu = JavaParser.parse(in);
			addTotalComments(intermediate, cu);
			in.close();
		}  catch (IOException e) {
			intermediate.parses = false;
		} catch (TokenMgrError e) {
			intermediate.parses = false;
		} catch(ParseException e) {
			intermediate.parses = false;
		}
	}

	/**
	 * Simple visitor implementation for visiting MethodDeclaration nodes.
	 */
	private static class MethodVisitor extends VoidVisitorAdapter<Void> {
		@Override
		public void visit(MethodDeclaration n, Void arg) {
			/* here you can access the attributes of the method.
             this method will be called for all methods in this 
             CompilationUnit, including inner class methods */

			//System.out.println(n.getBody());
			super.visit(n, arg);
		}
	}

	private static void addTotalComments(Intermediate intermediate, CompilationUnit cu) {
		int totalComments = 0;
		for(Comment cmt : cu.getComments()) {
			String content = cmt.getContent();
			totalComments += content.length();
		}
		intermediate.totalComments = totalComments;
		intermediate.nonComments = intermediate.code.length() - totalComments;
	}

	private static String getClassName(String filePath) {
		File f = new File(filePath);
		return f.getName().split("\\.")[0];
	}


}
