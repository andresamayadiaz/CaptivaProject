package controllers;

import java.io.Serializable;

import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.FileMode;

/**
 * PathModel is a serializable model class that represents a file or a folder,
 * including all its metadata and associated commit id.
 * 
 * @author James Moger
 * 
 */
public class PathModel implements Serializable, Comparable<PathModel> {

	private static final long serialVersionUID = 1L;

	public final String name;
	public final String path;
	public final long size;
	public final int mode;
	public final String commitId;
	public boolean isParentPath;

	public PathModel(String name, String path, long size, int mode, String commitId) {
		this.name = name;
		this.path = path;
		this.size = size;
		this.mode = mode;
		this.commitId = commitId;
	}

	public boolean isTree() {
		return FileMode.TREE.equals(mode);
	}

	@Override
	public int hashCode() {
		return commitId.hashCode() + path.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PathModel) {
			PathModel other = (PathModel) o;
			return this.path.equals(other.path);
		}
		return super.equals(o);
	}

	@Override
	public int compareTo(PathModel o) {
		boolean isTree = isTree();
		boolean otherTree = o.isTree();
		if (isTree && otherTree) {
			return path.compareTo(o.path);
		} else if (!isTree && !otherTree) {
			return path.compareTo(o.path);
		} else if (isTree && !otherTree) {
			return -1;
		}
		return 1;
	}

	/**
	 * PathChangeModel is a serializable class that represents a file changed in
	 * a commit.
	 * 
	 * @author James Moger
	 * 
	 */
	public static class PathChangeModel extends PathModel {

		private static final long serialVersionUID = 1L;

		public final ChangeType changeType;

		public PathChangeModel(String name, String path, long size, int mode, String commitId,
				ChangeType type) {
			super(name, path, size, mode, commitId);
			this.changeType = type;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return super.equals(o);
		}
	}
}

