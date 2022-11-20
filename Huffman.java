/*
  This is a Huffman encoding class that implements PrefixCode interface.
  String and text files are encoded to return compressed files.
  Characters are assigned binary representation based on frequency.
  More frequent characters are represented by fewer bits.

  Assignment10
  Adam Rogers
 */

import java.util.*;
import java.io.*;

public class Huffman implements PrefixCode {

    private Node root;
    private final HashMap<Integer,Integer> freqMap;
    private final PriorityQueue<Node> q;
    private final HashMap<Integer,String> codeMap;

    public Huffman() {
        root = null;
        freqMap = new HashMap<>();
        q = new PriorityQueue<>();
        codeMap = new HashMap<>();
    }

    public void generateCode(InputStream in) {
        try {
            // read text by character
            int b = in.read();
            while (b != -1) {
                int freq = 1;
                // if it's already in map, increase frequency and replace
                if (freqMap.containsKey(b)) {
                    freq =  1 + freqMap.get(b);
                }
                // otherwise add it
                freqMap.put(b, freq);
                b = in.read();
            }
        }
        catch (IOException io) {
            System.exit(1);
        }
        // iterate through map
        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            // build priority queue of characters with frequency as key
            Node nd = new Node();
            nd.ch = entry.getKey();
            nd.weight = entry.getValue();
            q.add(nd);
        }
        // iterate through priority queue
        while (q.size() > 1) {
            // build tree using Huffman algorithm
            Node nd0 = q.poll();
            Node nd1 = q.poll();
            Node nd2 = new Node();
            nd2.left = nd0;
            nd2.right = nd1;
            nd2.weight = nd0.weight + nd1.weight;
            nd2.ch = -1;
            q.add(nd2);
        }
        root = q.poll();
        // if text file has of one distinct character, assign code to "0"
        if (root.ch != -1) codeMap.put(root.ch, "0");
        else encode(root, "");
    }

    public String getCodeword(char ch) {
        // return binary code
        return codeMap.getOrDefault((int) ch, "");
    }

    public int getChar(String codeword) {
        Node nd = root;
        char[] ch = codeword.toCharArray();
        // iterate through binary String
        for (char c : ch) {
            if (c == '0') nd = nd.left;
            if (c == '1') nd = nd.right;
        }
        //return character leaf found
        return nd.ch;
    }

    public String encode(String str) {
        StringBuilder code = new StringBuilder();
        char[] ch = str.toCharArray();
        // iterate through character array
        for (char c : ch) code.append(codeMap.get((int) c));
        return code.toString();
    }

    public String decode(String str) {
        Node nd = root;
        StringBuilder code = new StringBuilder();
        char[] ch = str.toCharArray();
        // iterate through binary String
        for (char c : ch) {
            if (c == '0') nd = nd.left;
            if (c == '1') nd = nd.right;
            // if character is found, append to String
            if (nd.ch != -1) {
                code.append((char) (nd.ch));
                nd = root;
            }
        }
        return code.toString();
    }

    public int originalSize() {
        return root.weight;
    }

    public int compressedSize() {
        int sumBits = 0;
        // iterate through map
        for (Map.Entry<Integer, String> entry : codeMap.entrySet()) {
            char[] ch = (entry.getValue()).toCharArray();
            int numBits = ch.length;
            int numOccur = freqMap.get(entry.getKey());
            sumBits += numBits * numOccur;
        }
        return sumBits / 8;
    }

    private void encode(Node nd, String code) {
        // recursive method to build encoding map
        if (nd.left == null || nd.right == null) {
            codeMap.put(nd.ch, code);
            return;
        }
        encode(nd.left, code + "0");
        encode(nd.right, code + "1");
    }

    protected static class Node implements Comparable<Node> {
        Node left;
        Node right;
        int ch;
        int weight;

        public int compareTo(Node nd) {
            // Node class comparable by weight
            // lower frequencies have more priority
            return Integer.compare(weight, nd.weight);
        }
    }
}

