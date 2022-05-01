import java.util.LinkedList;
import java.util.ArrayList;


/**
 * CISC 380 Algorithms Assignment 5
 *
 * Represents a graph of nodes and edges in adjacency list format. This program has the functionality to retrieve node data and add new nodes to the graph.
 * The two way directed graph contains a set of nodes which are connected via uphill and downhill paths.
 * The goal of the user is to find a path from the starting point "home" to the ending point "work" by first going only uphill, then second going only downhill.
 * In this assignment, the isUphillDownhillPath method evaluates the nodes and their edges to see if there is an uphill and downhill path from home to work.
 *
 * @author Joseph Lambrecht Due Date: 04/25/21
 */

public class TwoWayDirectedGraph {

    private ArrayList<TwoWayDirectedGraphNode> nodes;

    public TwoWayDirectedGraph(boolean[][] adjacencyMatrixUphill, boolean[][] adjacencyMatrixDownhill) {
        this.nodes = new ArrayList<TwoWayDirectedGraphNode>();

		// populate the graph with nodes.
		for (int i = 0; i < adjacencyMatrixUphill.length; i++) {
			this.nodes.add(new TwoWayDirectedGraphNode(i));
        }

		// connect the nodes based on the adjacency matrix
		for (int i = 0; i < adjacencyMatrixUphill.length; i++) {
			for (int j = 0; j < adjacencyMatrixUphill[i].length; j++) {
				if (adjacencyMatrixUphill[i][j]) {
					this.connect(i, j, true);
				}
			}
        }

        // connect the nodes based on the adjacency matrix
		for (int i = 0; i < adjacencyMatrixDownhill.length; i++) {
			for (int j = 0; j < adjacencyMatrixDownhill[i].length; j++) {
				if (adjacencyMatrixDownhill[i][j]) {
					this.connect(i, j, false);
				}
			}
		}
    }

    public int getGraphSize() {
		return this.nodes.size();
	}// getGraphSize

    private void connect(int root, int other, boolean isUphill) {
		//check if the two roots to be connected exist
		if (0 > root || root >= this.getGraphSize()) {
			throw new ArrayIndexOutOfBoundsException("Cannot connect nonexistent root with value: " + root
					+ ". Valid Nodes are between 0 and " + (this.nodes.size() - 1) + ".");
		}
		if (0 > other || other >= this.getGraphSize()) {
			throw new ArrayIndexOutOfBoundsException("Cannot connect nonexistent root with value: " + other
					+ ". Valid Nodes are between 0 and " + (this.nodes.size() - 1) + ".");

		}

		//define the nodes to be connected
		TwoWayDirectedGraphNode rootNode = findNode(root);
		TwoWayDirectedGraphNode otherNode = findNode(other);

		//connect the nodes based on whether the root node is uphill or downhill of the other node.
        if (isUphill) {
            rootNode.addUphillNodes(otherNode);
        }
        else {
            rootNode.addDownhillNodes(otherNode);
        }


	}// connect

	//given an index, return the node at the index or null if it does not exist
    private TwoWayDirectedGraphNode findNode(int data) {
		if(0 <= data && data < this.nodes.size()){
			return nodes.get(data);
		}else{
			return null;
		}


	}// findNode

	//return nodes in graph
    public ArrayList<TwoWayDirectedGraphNode> getNodes() {
        return this.nodes;
    }

    /**
	 * Returns a string representation of all the nodes in the graph. The string
	 * displays the nodes data, and a list of all of its outgoing Nodes.
	 *
	 * @return a string representation of the graph.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();

        // for every node
		for (int i = 0; i < this.nodes.size(); i++) {
			// append the string representation to the result.
			TwoWayDirectedGraphNode current = this.nodes.get(i);
			sb.append(String.format("Node: %-8s Uphill Edges: %-3d Downhill Edges: %-3d Uphill Nodes: %-3s Downhill Nodes: %-3s\n", current.data, current.getOutgoingNodesUphill().size(), current.getOutgoingNodesDownhill().size(), this.getArrayData(current.getOutgoingNodesUphill()), this.getArrayData(current.getOutgoingNodesDownhill())));
		}
		return sb.toString();
    }// toString

    private String getArrayData(LinkedList<TwoWayDirectedGraphNode> output) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < output.size(); i++) {
            sb.append(output.get(i).data + ", ");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method evaluates the nodes and their edges to see if there is an uphill and downhill path from home to work
     * @param homeNode - the home node (starting point)
     * @param workNode - the work node (ending point)
     * @return will return true if there is such a path, and false if there is no such path
     */
    public boolean isValidUphillDownhillPath(int homeNode, int workNode) {

		Integer visitedTable[] = new Integer[this.getGraphSize()];
		for (int i =0; i<visitedTable.length; i++){
			visitedTable[i]=0;
		}

        // solve uphill problem first by throwing away downhill edges
        int maxvisited=1;
       	DFS(homeNode, visitedTable, maxvisited);

		//flip the edges
		ArrayList<TwoWayDirectedGraphNode> flipNodes;

		flipNodes = new ArrayList<TwoWayDirectedGraphNode>();

		// populate the graph with nodes.
		for (int i = 0; i < visitedTable.length; i++) {
			flipNodes.add(new TwoWayDirectedGraphNode(i));
		}

		//reverse the edges using a modified DFS
		boolean visited[] = new boolean[visitedTable.length];
		for (int i = 0; i < visitedTable.length; i++) {
			reverse(i, flipNodes, visited);
		}
		this.nodes = flipNodes;

        // solve the downhill problem next
        maxvisited++;
		DFS(workNode, visitedTable, maxvisited);

        //check the table to see if any node was visited in both the uphill and the downhill search
		for (int i =0; i < visitedTable.length; i++){
			if (visitedTable[i] == 2){ return true;}
		}

        return false;
    }//isValidUphillDownhillPath

    /**
     * This method recursively performs a Depth First Search
     * @param homeNode - the home node (starting point)
     * @param visitedTable - integer array tracking how many times a node has been visited
     * @param max visited - count of the most times a node could have been visited so far
     */
    private void DFS(int homeNode, Integer visitedTable[], int maxvisited){
		visitedTable[homeNode]++;
		TwoWayDirectedGraphNode current = this.nodes.get(homeNode);
		for (TwoWayDirectedGraphNode node : current.getOutgoingNodesUphill()){
			int data = node.getData();
			if (visitedTable[data] < maxvisited){ DFS(data, visitedTable, maxvisited);}
		}
	}

    /**
     * This method recursively performs a Depth First Search
     * @param homeNode - the home node (starting point)
     * @param visitedTable - integer array tracking how many times a node has been visited
     * @param max visited - count of the most times a node could have been visited so far
     */
	private void reverse(int homeNode, ArrayList<TwoWayDirectedGraphNode> flipNodes, boolean visited[]){
		visited[homeNode] = true;
		TwoWayDirectedGraphNode current = this.nodes.get(homeNode);
		for (TwoWayDirectedGraphNode node : current.getOutgoingNodesDownhill()){
			int data = node.getData();
			this.flipConnect(data,homeNode,true,flipNodes);
		}
	}

    private void flipConnect(int root, int other, boolean isUphill, ArrayList<TwoWayDirectedGraphNode> flipNodes) {

		if (0 > root || root >= this.getGraphSize()) {
			throw new ArrayIndexOutOfBoundsException("Cannot connect nonexistent root with value: " + root
					+ ". Valid Nodes are between 0 and " + (flipNodes.size() - 1) + ".");
		}

		if (0 > other || other >= this.getGraphSize()) {
			throw new ArrayIndexOutOfBoundsException("Cannot connect nonexistent root with value: " + other
					+ ". Valid Nodes are between 0 and " + (flipNodes.size() - 1) + ".");

		}

		TwoWayDirectedGraphNode rootNode = flipNodes.get(root);
		TwoWayDirectedGraphNode otherNode = flipNodes.get(other);

        if (isUphill) {
            rootNode.addUphillNodes(otherNode);
		}
        else {
            rootNode.addDownhillNodes(otherNode);
        }


	}// flipConnect


    /**
     * This class represents each specific node in the graph.  Each node can have a LinkedList of uphill and downhill nodes to make
     * it a two-way directed graph node.
     */
    private static class TwoWayDirectedGraphNode {

        private int data;

        private LinkedList<TwoWayDirectedGraphNode> outgoingNodesUphill;
        private LinkedList<TwoWayDirectedGraphNode> outgoingNodesDownhill;

        public TwoWayDirectedGraphNode(int data) {

            this.data = data;
            this.outgoingNodesUphill = new LinkedList<TwoWayDirectedGraphNode>();
            this.outgoingNodesDownhill = new LinkedList<TwoWayDirectedGraphNode>();

        }

        public void addUphillNodes(TwoWayDirectedGraphNode newNode) {
            this.outgoingNodesUphill.add(newNode);
        }

        public void addDownhillNodes(TwoWayDirectedGraphNode newNode) {
            this.outgoingNodesDownhill.add(newNode);
        }

        public LinkedList<TwoWayDirectedGraphNode> getOutgoingNodesUphill() {
            return this.outgoingNodesUphill;
        }

        public LinkedList<TwoWayDirectedGraphNode> getOutgoingNodesDownhill() {
            return this.outgoingNodesDownhill;
        }

        public int getData(){return this.data;}

    }

}
