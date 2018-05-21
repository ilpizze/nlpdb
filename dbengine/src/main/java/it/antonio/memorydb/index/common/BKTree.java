package it.antonio.memorydb.index.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BKTree<T> {

	BKTreeNode<T> root;

	public void add(String word, T value) {

		if (root == null) {
			root = new BKTreeNode<>(word, value);
		} else {
			root.add(word, value);
		}
	}

	public Collection<T> find(String word, int maxDistance) {

		NavigableMap<Integer, List<T>> collected = new TreeMap<>();
		for (int i = 0; i <= maxDistance; i++) {
			collected.put(i, new LinkedList<>());
		}

		root.find(word, maxDistance, collected);

		return collected.values().stream().flatMap(l -> l.stream()).collect(Collectors.toList());
	}

	@SuppressWarnings("hiding")
	private class BKTreeNode<T> {
		final T value;
		final String word;
		final Map<Integer, BKTreeNode<T>> childrenByDistance = new HashMap<>();

		public BKTreeNode(String word, T value) {
			super();
			this.value = value;
			this.word = word;
		}

		public void add(String word, T value) {
			int score = distance(word, this.word);

			BKTreeNode<T> child = childrenByDistance.get(score);
			if (child != null) {
				child.add(word, value);
			} else {
				childrenByDistance.put(score, new BKTreeNode<>(word, value));
			}
		}

		public void find(String word, int maxDistance, NavigableMap<Integer, List<T>> collected) {
			int distanceAtNode = distance(word, this.word);

			if (distanceAtNode <= maxDistance) {
				collected.get(distanceAtNode).add(value);
			}

			for (int score = distanceAtNode - maxDistance; score <= maxDistance + distanceAtNode; score++) {
				BKTreeNode<T> child = childrenByDistance.get(score);
				if (child != null) {
					child.find(word, maxDistance, collected);
				}
			}
		}

	}

	private static int distance(String left, String right) {
		if (left == null || right == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		/*
		 * This implementation use two variable to record the previous cost counts, So
		 * this implementation use less memory than previous impl.
		 */

		int n = left.length(); // length of left
		int m = right.length(); // length of right

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		if (n > m) {
			// swap the input strings to consume less memory
			final String tmp = left;
			left = right;
			right = tmp;
			n = m;
			m = right.length();
		}

		final int[] p = new int[n + 1];

		// indexes into strings left and right
		int i; // iterates through left
		int j; // iterates through right
		int upperLeft;
		int upper;

		char rightJ; // jth character of right
		int cost; // cost

		for (i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (j = 1; j <= m; j++) {
			upperLeft = p[0];
			rightJ = right.charAt(j - 1);
			p[0] = j;

			for (i = 1; i <= n; i++) {
				upper = p[i];
				cost = left.charAt(i - 1) == rightJ ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
				p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
				upperLeft = upper;
			}
		}

		return p[n];
	}
}
