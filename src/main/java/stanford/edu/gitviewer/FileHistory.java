package stanford.edu.gitviewer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

public class FileHistory {
	
	private static final int BREAK_MINS = 10;

	public static ArrayList<String> getFiles(String repoPath) {
		try {
			Git git = Git.init().setDirectory(new File(repoPath) ).call();
			Repository repo = git.getRepository();
			ObjectId from = repo.resolve("refs/heads/master");
			RevWalk walk = new RevWalk(repo);
			RevCommit commit = walk.parseCommit(from);
			RevTree tree = commit.getTree();
			TreeWalk treeWalk = new TreeWalk(repo);
			treeWalk.addTree(tree);
			treeWalk.setRecursive(false);
			ArrayList<String> paths = new ArrayList<String>();
			while (treeWalk.next()) {
				if (treeWalk.isSubtree()) {
					System.out.println("dir: " + treeWalk.getPathString());
					treeWalk.enterSubtree();
				} else {
					paths.add(treeWalk.getPathString());

				}
			}
			walk.close();
			treeWalk.close();
			return paths;
		} catch (Exception e) {
			throw(new RuntimeException(e));
		}
	}
	
	public static ArrayList<Intermediate> getHistory(String repoPath, String filePath) {
		ArrayList<Intermediate> history = getRawIntermediate(repoPath, filePath); 
		Collections.reverse(history);
		addIntermediateTiming(history);
		parseIntermediateCode(history);
		return history;
	}

	private static ArrayList<Intermediate> getRawIntermediate(String repoPath, String filePath) {
		ArrayList<Intermediate> history = new ArrayList<Intermediate>();
		try {
			Git git = Git.init().setDirectory(new File(repoPath) ).call();
			Repository repo = git.getRepository();

			RevWalk walk = new RevWalk(repo);
			ObjectId from = repo.resolve("refs/heads/master");
			walk.markStart(walk.parseCommit(from));

			String lastText = "";
			for (RevCommit commit : walk) {
				String currText = getFile(repo, commit, filePath);
				if(!currText.isEmpty() && !currText.equals(lastText)) {
					Intermediate intr = new Intermediate();
					intr.code = currText;
					intr.timeStamp = commit.getCommitTime();
					history.add(intr);
					lastText = currText;
				}
			}
			walk.close();
			
		} catch (Exception e) {
			throw(new RuntimeException(e));
		}
		return history;
	}

	private static void parseIntermediateCode(ArrayList<Intermediate> history) {
		for(Intermediate intermediate : history) {
			Parser.parse(intermediate);
		}
	}

	private static void addIntermediateTiming(ArrayList<Intermediate> history) {
		int startTime = 0;
		int workingTimeSeconds = 0;
		int lastTime = 0;
		if(!history.isEmpty()) {
			startTime = history.get(0).timeStamp;
			lastTime = startTime;
		}
		for (Intermediate intermediate : history) {
			int time = intermediate.timeStamp;
			boolean tookBreak = false;
			int deltaSeconds = time - lastTime;
			if(deltaSeconds > BREAK_MINS * 60) {
				deltaSeconds = 0;
				tookBreak = true;
			}
			workingTimeSeconds += deltaSeconds;
			double workingMins = workingTimeSeconds / 60.0;
			double workingHours = workingMins / 60.0;
			intermediate.workingHours = workingHours;
			if(tookBreak) {
				double breakSeconds = time - lastTime;
				double breakMins = breakSeconds / 60.0;
				double breakHours = breakMins / 60.0;
				intermediate.breakHours = breakHours;
			} else {
				intermediate.breakHours = null;
			}
			
			lastTime = time;
		}

	}

	private static String getFile(Repository repo, RevCommit commit, String path) throws IOException {
		// Makes it simpler to release the allocated resources in one go
		ObjectReader reader = repo.newObjectReader();

		// Get the revision's file tree
		RevTree tree = commit.getTree();
		// .. and narrow it down to the single file's path
		TreeWalk treewalk = TreeWalk.forPath(reader, path, tree);

		if (treewalk != null) {
			// use the blob id to read the file's data
			byte[] data = reader.open(treewalk.getObjectId(0)).getBytes();
			return new String(data, "utf-8").trim();
		} else {
			return "";
		}
	}



}
