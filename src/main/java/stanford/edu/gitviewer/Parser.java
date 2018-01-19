package stanford.edu.gitviewer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class Parser {

	public static void parse(Intermediate intermediate) {
		String code = intermediate.code;
		InputStream in = null;
		CompilationUnit cu = null;
		try{
			in = new ByteArrayInputStream(code.getBytes());
			cu = JavaParser.parse(in);
			
			addTotalComments(intermediate, cu);
			cu.accept(new MethodVisitor(), null);
			
			in.close();
		} catch(ParseException x){
			System.out.println("Did not parse");
		} catch (IOException e) {
			throw new RuntimeException(e);
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
        		
            System.out.println(n.getBody());
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

}
