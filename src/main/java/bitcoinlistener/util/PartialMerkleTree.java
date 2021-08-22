/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.util;

import bitcoinlistener.datatypes.SHA256Hash;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Partial merkle tree to proof that the given set of transactions is indeed in the block.
 */
public class PartialMerkleTree {

	// Input values ================================================================================
	private List<SHA256Hash> hashes;
	private List<Boolean> flags;
	private long totalTransactions; // uint32_t
	private int height;

	// Computed values =============================================================================
	private TreeNode root;
	private List<SHA256Hash> matchedTxIds;
	private SHA256Hash merkleRoot;

	// =============================================================================================
	// CONSTRUCTORS
	// =============================================================================================

	public PartialMerkleTree(List<SHA256Hash> hashes, long totalTransactions,
							 List<Boolean> flags) {
		this.hashes = new LinkedList<>(hashes);
		this.totalTransactions = totalTransactions;
		this.flags = flags;
		this.height = (int) Math.ceil((Math.log(totalTransactions) / Math.log(2)));
	}

	// =============================================================================================
	// OPERATIONS
	// =============================================================================================

	public SHA256Hash getMerkleRoot() {
		if (merkleRoot == null) {
			merkleRoot = root.data.getInverted();
		}
		return merkleRoot;
	}

	public List<SHA256Hash> getMatchedTxIds() {
		return matchedTxIds;
	}


	/**
	 * Build and validate the merkle tree
	 */
	public void build() {
		this.merkleRoot = null;
		this.root = new TreeNode();
		this.matchedTxIds = new ArrayList<>();
		List<TreeNode> stack = new LinkedList<>();
		stack.add(root);
		while (!stack.isEmpty()) {
			boolean flag = flags.remove(0);
			TreeNode node = stack.remove(0);
			if (flag) { // flag = 1
				if (node.height <= height - 1) { // internal
					int nodeIndexLeft = (node.currentIndex * 2);
					boolean hasRight = getTotalNodesAt(node.height + 1) > nodeIndexLeft + 1;
					node.left = new TreeNode(null, node, null, null, node.height + 1,
											 nodeIndexLeft);
					if (hasRight) {
						node.right = new TreeNode(null, node, null, null, node.height + 1,
												  nodeIndexLeft + 1);
						stack.add(0, node.right);
					}
					stack.add(0, node.left);
				} else { // flag 1 and leaf node
					node.data = hashes.remove(0);
					matchedTxIds.add(node.data.getInverted());
				}
			} else if (!flag) { // flag = 0
				node.data = hashes.remove(0);
			}
		}
		if (hashes.size() != 0) {
			throw new RuntimeException("invalid");
		}
		if (flags.stream().anyMatch(s -> s == true)) {
			throw new RuntimeException("remaining flags");
		}

		fillMissingHashes(root);
	}

	// =============================================================================================
	// AUXILIARY METHODS
	// =============================================================================================

	private int getTotalNodesAt(int height) {
		return (int) Math.ceil(
				(double) this.totalTransactions / Math.pow(2, (this.height - height)));
	}

	private void fillMissingHashes(TreeNode root) {
		// FIXME do it iteratively
		if (root == null) {
			return;
		}
		fillMissingHashes(root.left);
		fillMissingHashes(root.right);

		if (!root.isLeaf() && root.data == null) {
			SHA256Hash rightHash;
			if (root.right == null) {
				rightHash = root.left.data;
			} else if (root.right.data.equals(root.left.data)) {
				throw new RuntimeException("Invalid merkle tree. Left = Right");
			} else {
				rightHash = root.right.data;
			}
			SHA256Hash leftHash = root.left.data;
			root.data = new SHA256Hash(HashUtil.sha256(HashUtil.sha256(leftHash.getHash(), rightHash.getHash())));
		}
	}


	private class TreeNode {
		public SHA256Hash data;
		public TreeNode parent;
		public TreeNode left;
		public TreeNode right;
		public int currentIndex;
		int height;

		public TreeNode() {
		}

		public TreeNode(SHA256Hash data, TreeNode parent, TreeNode left,
						TreeNode right, int height,
						int currentIndex) {
			this();
			this.data = data;
			this.left = left;
			this.right = right;
			this.height = height;
			this.parent = parent;
			this.currentIndex = currentIndex;
		}

		public boolean isLeft() {
			if (this.parent == null) {
				return false;
			}
			return (this.parent.left == this);
		}

		public boolean isLeaf() {
			return (this.left == null && this.right == null);
		}

		public String getHashAsStr() {
			return data.getHashAsStr();
		}
	}
}