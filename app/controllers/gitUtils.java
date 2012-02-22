package controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import junit.framework.Assert;

import org.eclipse.jgit.revwalk.DepthWalk.Commit;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.lib.*;
import static org.eclipse.jgit.lib.Constants.*;

public class gitUtils {

	public static Collection<RevCommit> getBranches(final Repository repository) throws Exception {
		if (repository == null)
			throw new Exception(new Exception("Null Repository"));

		final Collection<RevCommit> commits = new HashSet<RevCommit>();
		final RevWalk walk = new RevWalk(repository);
		final RefDatabase refDb = repository.getRefDatabase();
		try {
			getRefCommits(walk, refDb, R_HEADS, commits);
			getRefCommits(walk, refDb, R_REMOTES, commits);
		} catch (IOException e) {
			throw new Exception(e);
		} finally {
			walk.release();
		}
		return commits;
	}
	
	private static void getRefCommits(final RevWalk walk,
			final RefDatabase refDb, final String prefix,
			final Collection<RevCommit> commits) throws IOException {
		for (Ref ref : refDb.getRefs(prefix).values()) {
			final RevCommit commit = getRef(walk, ref);
			if (commit != null)
				commits.add(commit);
		}
	}
	
	private static RevCommit getRef(final RevWalk walk, final Ref ref)
			throws IOException {
		ObjectId id = ref.getPeeledObjectId();
		if (id == null)
			id = ref.getObjectId();
		return id != null ? walk.parseCommit(id) : null;
	}
	
}
