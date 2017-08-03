package huffman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

class Node {
	String info;
	double percent;
	Node left;
	Node right;

	public Node(String info, double percent, Node left, Node right) {
		super();
		this.info = info;
		this.percent = percent;
		this.left = left;
		this.right = right;
	}

	public Node(String info, double percent) {
		this(info, percent, null, null);
	}

	public Node() {
		super();
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	@Override
	public String toString() {
		return "Key : " + info + ", Percent: " + percent + " ";
	}

}

class HuffmanTree {
	Node root;

	public HuffmanTree() {
		super();
	}

	public boolean isLeaf(Node p) {
		return (p.left == null && p.right == null);
	}

	public String breadthFirst() {
		StringBuilder sb = new StringBuilder();
		Queue<Node> queue = new LinkedList<>();
		Queue<Integer> level = new LinkedList<>();
		if (root != null) {
			queue.offer(root);
			level.offer(1);
		}
		Node p;
		Integer i, currentLevel = 1;
		while (!queue.isEmpty()) {
			p = queue.poll();
			i = level.poll();
			if (i > currentLevel) {
				sb.append(System.lineSeparator());
				currentLevel = i;
			}
			sb.append(p);
			if (p.left != null) {
				queue.add(p.left);
				level.add(i + 1);
			}
			if (p.right != null) {
				queue.add(p.right);
				level.add(i + 1);
			}
		}
		return sb + "";
	}

	public void depthFirst(Node p, String path, HashMap<String, String> result, boolean isOneOnRight) {
		if (p != null) {
			if (isLeaf(p)) {
				result.put(p.info, new StringBuffer(path).toString());
			}
			if (isOneOnRight)
				path += 0;
			else
				path += 1;
			depthFirst(p.left, path, result, isOneOnRight);
			path = path.substring(0, path.length() - 1);
			if (isOneOnRight)
				path += 1;
			else
				path += 0;
			depthFirst(p.right, path, result, isOneOnRight);
			path = path.substring(0, path.length() - 1);
		}
	}
}

public class Huffman {

	public boolean higherPercentInLeft;
	public boolean isOneOnRight;
	HuffmanTree tree;
	HashMap<String, String> result;
	private String encodingString;

	public Huffman() {
		super();
	}

	public Huffman(String encodingString, boolean higherPercentInLeft, boolean isOneOnRight) {
		tree = new HuffmanTree();
		this.encodingString = encodingString;
		this.higherPercentInLeft = higherPercentInLeft;
		this.isOneOnRight = isOneOnRight;
		List<Node> list = satistic(encodingString);
		buildHuffmanTree(list);
		result = new HashMap<>();
		String path = "";
		tree.depthFirst(tree.root, path, result, isOneOnRight);
	}

	public String getDecoding() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < encodingString.length(); i++) {
			sb.append(result.get(encodingString.charAt(i) + ""));
		}
		return sb + "";
	}

	public String getResult() {
		StringBuilder sb = new StringBuilder();
		for (String key : result.keySet()) {
			sb.append(key + " : " + result.get(key) + System.lineSeparator());
		}
		return sb + "";
	}

	public String getEncoding(String decodingString) {
		StringBuilder sb = new StringBuilder(), s = new StringBuilder();
		int i = 0;
		String str = null;
		while (i < decodingString.length()) {
			boolean check = false;
			while (i < decodingString.length() && !check) {
				s.append(decodingString.charAt(i));
				for (String key : result.keySet()) {
					if (result.get(key).equalsIgnoreCase(s.toString().trim())) {
						check = true;
						str = new String(key);
						break;
					}
				}
				i++;
			}
			while (i < decodingString.length() && check) {
				s.append(decodingString.charAt(i));
				i++;
				boolean bcheck = false;
				for (String key : result.keySet()) {
					if (result.get(key).equalsIgnoreCase(s.toString().trim())) {
						bcheck = true;
						str = new String(key);
						break;
					}
				}
				if (!bcheck) {
					sb.append(str);
					s = new StringBuilder();
					str = null;
					i--;
					break;
				}
			}
		}
		if (str != null)
			sb.append(str);
		return sb + "";
	}

	List<Node> satistic(String encodingString) {
		int size = encodingString.length();
		List<Node> list = new ArrayList<>();
		HashMap<Character, AtomicInteger> hasExisted = new HashMap<>();
		for (int i = 0; i < encodingString.length(); i++) {
			if (!hasExisted.containsKey(encodingString.charAt(i))) {
				hasExisted.put(encodingString.charAt(i), new AtomicInteger(1));
			} else {
				hasExisted.get(encodingString.charAt(i)).set(hasExisted.get(encodingString.charAt(i)).intValue() + 1);
			}
		}
		for (Character key : hasExisted.keySet()) {
			list.add(new Node(key + "", hasExisted.get(key).intValue() * 1.0 / size));
		}
		return list;
	}

	void buildHuffmanTree(List<Node> list) {
		while (list.size() > 1) {
			Node firstNode = null, secondNode = null;
			for (Node node : list) {
				if (firstNode == null)
					firstNode = node;
				else if (firstNode.percent > node.percent)
					firstNode = node;
			}
			list.remove(firstNode);

			for (Node node : list) {
				if (secondNode == null)
					secondNode = node;
				else if (secondNode.percent > node.percent)
					secondNode = node;
			}
			list.remove(secondNode);

			Node newNode = new Node();
			newNode.percent = firstNode.percent + secondNode.percent;
			if (higherPercentInLeft) {
				newNode.info = secondNode.info + firstNode.info;
				newNode.left = secondNode;
				newNode.right = firstNode;
			} else {
				newNode.info = firstNode.info + secondNode.info;
				newNode.left = firstNode;
				newNode.right = secondNode;
			}
			list.add(newNode);
		}
		tree.root = list.get(0);
	}

}
