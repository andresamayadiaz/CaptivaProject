package controllers;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;

import play.Logger;

import models.RefModel;

import controllers.PathModel.PathChangeModel;

public class JGitUtils {

	public static boolean hasCommits(Repository repository) {
		if (repository != null && repository.getDirectory().exists()) {
			return (new File(repository.getDirectory(), "objects").list().length > 2)
					|| (new File(repository.getDirectory(), "objects/pack").list().length > 0);
		}
		return false;
	}
	
	public static List<PathChangeModel> getFilesInCommit(Repository repository, RevCommit commit) {
		List<PathChangeModel> list = new ArrayList<PathChangeModel>();
		if (!hasCommits(repository)) {
			return list;
		}
		RevWalk rw = new RevWalk(repository);
		try {
			if (commit == null) {
				ObjectId object = getDefaultBranch(repository);
				commit = rw.parseCommit(object);
			}

			if (commit.getParentCount() == 0) {
				TreeWalk tw = new TreeWalk(repository);
				tw.reset();
				tw.setRecursive(true);
				tw.addTree(commit.getTree());
				while (tw.next()) {
					list.add(new PathChangeModel(tw.getPathString(), tw.getPathString(), 0, tw
							.getRawMode(0), commit.getId().getName(), ChangeType.ADD));
				}
				tw.release();
			} else {
				RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
				DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
				df.setRepository(repository);
				df.setDiffComparator(RawTextComparator.DEFAULT);
				df.setDetectRenames(true);
				List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
				for (DiffEntry diff : diffs) {
					if (diff.getChangeType().equals(ChangeType.DELETE)) {
						list.add(new PathChangeModel(diff.getOldPath(), diff.getOldPath(), 0, diff
								.getNewMode().getBits(), commit.getId().getName(), diff
								.getChangeType()));
					} else {
						list.add(new PathChangeModel(diff.getNewPath(), diff.getNewPath(), 0, diff
								.getNewMode().getBits(), commit.getId().getName(), diff
								.getChangeType()));
					}
				}
			}
		} catch (Throwable t) {
			//todo
			Logger.error(t, t.getMessage());
		} finally {
			rw.dispose();
		}
		return list;
	}
	
	/**
	 * Returns the default branch to use for a repository. Normally returns
	 * whatever branch HEAD points to, but if HEAD points to nothing it returns
	 * the most recently updated branch.
	 * 
	 * @param repository
	 * @return the objectid of a branch
	 * @throws Exception
	 */
	public static ObjectId getDefaultBranch(Repository repository) throws Exception {
		ObjectId object = repository.resolve(Constants.HEAD);
		if (object == null) {
			// no HEAD
			// perhaps non-standard repository, try local branches
			List<RefModel> branchModels = getLocalBranches(repository, true, -1);
			if (branchModels.size() > 0) {
				// use most recently updated branch
				RefModel branch = null;
				Date lastDate = new Date(0);
				for (RefModel branchModel : branchModels) {
					if (branchModel.getDate().after(lastDate)) {
						branch = branchModel;
						lastDate = branch.getDate();
					}
				}
				object = branch.getReferencedObjectId();
			}
		}
		return object;
	}
	
	/**
	 * Returns the list of local branches in the repository. If repository does
	 * not exist or is empty, an empty list is returned.
	 * 
	 * @param repository
	 * @param fullName
	 *            if true, /refs/heads/yadayadayada is returned. If false,
	 *            yadayadayada is returned.
	 * @param maxCount
	 *            if < 0, all local branches are returned
	 * @return list of local branches
	 */
	public static List<RefModel> getLocalBranches(Repository repository, boolean fullName,
			int maxCount) {
		return getRefs(repository, Constants.R_HEADS, fullName, maxCount);
	}
	
	/**
	 * Returns a list of references in the repository matching "refs". If the
	 * repository is null or empty, an empty list is returned.
	 * 
	 * @param repository
	 * @param refs
	 *            if unspecified, all refs are returned
	 * @param fullName
	 *            if true, /refs/something/yadayadayada is returned. If false,
	 *            yadayadayada is returned.
	 * @param maxCount
	 *            if < 0, all references are returned
	 * @return list of references
	 */
	private static List<RefModel> getRefs(Repository repository, String refs, boolean fullName,
			int maxCount) {
		List<RefModel> list = new ArrayList<RefModel>();
		if (maxCount == 0) {
			return list;
		}
		if (!hasCommits(repository)) {
			return list;
		}
		try {
			Map<String, Ref> map = repository.getRefDatabase().getRefs(refs);
			RevWalk rw = new RevWalk(repository);
			for (Entry<String, Ref> entry : map.entrySet()) {
				Ref ref = entry.getValue();
				RevObject object = rw.parseAny(ref.getObjectId());
				String name = entry.getKey();
				if (fullName && !(refs == null)) {
					name = refs + name;
				}
				list.add(new RefModel(name, ref, object));
			}
			rw.dispose();
			Collections.sort(list);
			Collections.reverse(list);
			if (maxCount > 0 && list.size() > maxCount) {
				list = new ArrayList<RefModel>(list.subList(0, maxCount));
			}
		} catch (IOException e) {
			//todo
			Logger.error(e, e.getMessage());
		}
		return list;
	}
	
	/**
	 * Returns a RefModel for a specific branch name in the repository. If the
	 * branch can not be found, null is returned.
	 * 
	 * @param repository
	 * @return a refmodel for the branch or null
	 */
	public static RefModel getBranch(Repository repository, String name) {
		RefModel branch = null;
		try {
			// search for the branch in local heads
			for (RefModel ref : JGitUtils.getLocalBranches(repository, false, -1)) {
				if (ref.displayName.endsWith(name)) {
					branch = ref;
					break;
				}
			}

			// search for the branch in remote heads
			if (branch == null) {
				for (RefModel ref : JGitUtils.getRemoteBranches(repository, false, -1)) {
					if (ref.displayName.endsWith(name)) {
						branch = ref;
						break;
					}
				}
			}
		} catch (Throwable t) {
			//todo
			Logger.error(t, t.getMessage());
		}
		return branch;
	}
	
	/**
	 * Returns the list of remote branches in the repository. If repository does
	 * not exist or is empty, an empty list is returned.
	 * 
	 * @param repository
	 * @param fullName
	 *            if true, /refs/remotes/yadayadayada is returned. If false,
	 *            yadayadayada is returned.
	 * @param maxCount
	 *            if < 0, all remote branches are returned
	 * @return list of remote branches
	 */
	public static List<RefModel> getRemoteBranches(Repository repository, boolean fullName,
			int maxCount) {
		return getRefs(repository, Constants.R_REMOTES, fullName, maxCount);
	}
	
	/**
	 * Returns the specified commit from the repository. If the repository does
	 * not exist or is empty, null is returned.
	 * 
	 * @param repository
	 * @param objectId
	 *            if unspecified, HEAD is assumed.
	 * @return RevCommit
	 */
	public static RevCommit getCommit(Repository repository, String objectId) {
		if (!hasCommits(repository)) {
			return null;
		}
		RevCommit commit = null;
		try {
			// resolve object id
			ObjectId branchObject;
			if (objectId == null) {
				branchObject = getDefaultBranch(repository);
			} else {
				branchObject = repository.resolve(objectId);
			}
			RevWalk walk = new RevWalk(repository);
			RevCommit rev = walk.parseCommit(branchObject);
			commit = rev;
			walk.dispose();
		} catch (Throwable t) {
			//todo
			Logger.error(t, t.getMessage());
		}
		return commit;
	}
	
	/**
	 * Returns a list of commits for the repository or a path within the
	 * repository. Caller may specify ending revision with objectId. Caller may
	 * specify offset and maxCount to achieve pagination of results. If the
	 * repository does not exist or is empty, an empty list is returned.
	 * 
	 * @param repository
	 * @param objectId
	 *            if unspecified, HEAD is assumed.
	 * @param path
	 *            if unspecified, commits for repository are returned. If
	 *            specified, commits for the path are returned.
	 * @param offset
	 * @param maxCount
	 *            if < 0, all commits are returned.
	 * @return a paged list of commits
	 */
	public static List<RevCommit> getRevLog(Repository repository, String objectId, String path,
			int offset, int maxCount) {
		List<RevCommit> list = new ArrayList<RevCommit>();
		if (maxCount == 0) {
			return list;
		}
		if (!hasCommits(repository)) {
			return list;
		}
		try {
			// resolve branch
			ObjectId branchObject;
			if (objectId == null) {
				branchObject = getDefaultBranch(repository);
			} else {
				branchObject = repository.resolve(objectId);
			}

			RevWalk rw = new RevWalk(repository);
			rw.markStart(rw.parseCommit(branchObject));
			if (!(path == null)) {
				TreeFilter filter = AndTreeFilter.create(
						PathFilterGroup.createFromStrings(Collections.singleton(path)),
						TreeFilter.ANY_DIFF);
				rw.setTreeFilter(filter);
			}
			Iterable<RevCommit> revlog = rw;
			if (offset > 0) {
				int count = 0;
				for (RevCommit rev : revlog) {
					count++;
					if (count > offset) {
						list.add(rev);
						if (maxCount > 0 && list.size() == maxCount) {
							break;
						}
					}
				}
			} else {
				for (RevCommit rev : revlog) {
					list.add(rev);
					if (maxCount > 0 && list.size() == maxCount) {
						break;
					}
				}
			}
			rw.dispose();
		} catch (Throwable t) {
			//todo
			Logger.error(t, t.getMessage());
		}
		return list;
	}
	
	/**
	 * Returns a list of commits starting from HEAD and working backwards.
	 * 
	 * @param repository
	 * @param maxCount
	 *            if < 0, all commits for the repository are returned.
	 * @return list of commits
	 */
	public static List<RevCommit> getRevLog(Repository repository, int maxCount) {
		return getRevLog(repository, null, 0, maxCount);
	}
	
	/**
	 * Returns a list of commits starting from the specified objectId using an
	 * offset and maxCount for paging. This is similar to LIMIT n OFFSET p in
	 * SQL. If the repository does not exist or is empty, an empty list is
	 * returned.
	 * 
	 * @param repository
	 * @param objectId
	 *            if unspecified, HEAD is assumed.
	 * @param offset
	 * @param maxCount
	 *            if < 0, all commits are returned.
	 * @return a paged list of commits
	 */
	public static List<RevCommit> getRevLog(Repository repository, String objectId, int offset,
			int maxCount) {
		return getRevLog(repository, objectId, null, offset, maxCount);
	}
	
	/**
	 * Returns a list of commits since the minimum date starting from the
	 * specified object id.
	 * 
	 * @param repository
	 * @param objectId
	 *            if unspecified, HEAD is assumed.
	 * @param minimumDate
	 * @return list of commits
	 */
	public static List<RevCommit> getRevLog(Repository repository, String objectId, Date minimumDate) {
		List<RevCommit> list = new ArrayList<RevCommit>();
		if (!hasCommits(repository)) {
			return list;
		}
		try {
			// resolve branch
			ObjectId branchObject;
			if ( objectId == null ) {
				branchObject = getDefaultBranch(repository);
			} else {
				branchObject = repository.resolve(objectId);
			}

			RevWalk rw = new RevWalk(repository);
			rw.markStart(rw.parseCommit(branchObject));
			rw.setRevFilter(CommitTimeRevFilter.after(minimumDate));
			Iterable<RevCommit> revlog = rw;
			for (RevCommit rev : revlog) {
				list.add(rev);
			}
			rw.dispose();
		} catch (Throwable t) {
			//todo
			Logger.error(t, t.getMessage());
		}
		return list;
	}
	
	/**
	 * Returns a path model of the current file in the treewalk.
	 * 
	 * @param tw
	 * @param basePath
	 * @param commit
	 * @return a path model of the current file in the treewalk
	 */
	private static PathModel getPathModel(TreeWalk tw, String basePath, RevCommit commit) {
		String name;
		long size = 0;
		if (basePath == null) {
			name = tw.getPathString();
		} else {
			name = tw.getPathString().substring(basePath.length() + 1);
		}
		try {
			if (!tw.isSubtree()) {
				size = tw.getObjectReader().getObjectSize(tw.getObjectId(0), Constants.OBJ_BLOB);
			}
		} catch (Throwable t) {
			//error(t, null, "failed to retrieve blob size for " + tw.getPathString());
			Logger.error(t, t.getMessage());
		}
		return new PathModel(name, tw.getPathString(), size, tw.getFileMode(0).getBits(),
				commit.getName());
	}
	
	/**
	 * Returns the list of files in the specified folder at the specified
	 * commit. If the repository does not exist or is empty, an empty list is
	 * returned.
	 * 
	 * @param repository
	 * @param path
	 *            if unspecified, root folder is assumed.
	 * @param commit
	 *            if null, HEAD is assumed.
	 * @return list of files in specified path
	 */
	public static List<PathModel> getFilesInPath(Repository repository, String path,
			RevCommit commit) {
		List<PathModel> list = new ArrayList<PathModel>();
		if (!hasCommits(repository)) {
			return list;
		}
		if (commit == null) {
			commit = getCommit(repository, null);
		}
		final TreeWalk tw = new TreeWalk(repository);
		try {
			tw.addTree(commit.getTree());
			if (!(path == null)) {
				PathFilter f = PathFilter.create(path);
				tw.setFilter(f);
				tw.setRecursive(false);
				boolean foundFolder = false;
				while (tw.next()) {
					if (!foundFolder && tw.isSubtree()) {
						tw.enterSubtree();
					}
					if (tw.getPathString().equals(path)) {
						foundFolder = true;
						continue;
					}
					if (foundFolder) {
						list.add(getPathModel(tw, path, commit));
					}
				}
			} else {
				tw.setRecursive(false);
				while (tw.next()) {
					list.add(getPathModel(tw, null, commit));
				}
			}
		} catch (IOException e) {
			//error(e, repository, "{0} failed to get files for commit {1}", commit.getName());
			Logger.error(e, e.getMessage());
		} finally {
			tw.release();
		}
		Collections.sort(list);
		return list;
	}
	
	
}