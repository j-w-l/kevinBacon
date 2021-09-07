import java.util.*;

/**
 * Jonathan Lee.
 * Scaffold for library created by CBK.
 * Winter 2020. Revised 6/18/21.
 * */

public class GraphLibrary <V,E> {

    // Breadth first search.
    public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source){
        Graph<V,E> tree= new AdjacencyMapGraph<V,E>();
        tree.insertVertex(source);
        Set<V> visited = new HashSet<V>(); //Set to track which vertices have already been visited
        Queue<V> queue = new LinkedList<V>(); //queue to implement BFS

        queue.add(source); //enqueue start vertex
        visited.add(source); //add start to visited Set
        while (!queue.isEmpty()) { //loop until no more vertices
            V u = queue.remove(); //dequeue
            for (V v : g.outNeighbors(u)) { //loop over out neighbors
                if (!visited.contains(v)) { //if neighbor not visited, then neighbor is discovered from this vertex
                    visited.add(v); //add neighbor to visited Set
                    queue.add(v); //enqueue neighbor
                    tree.insertVertex(v);
                    tree.insertDirected(v, u, g.getLabel(v, u)); //save that this vertex was discovered from prior vertex
                }
            }
        }
        return tree;
    }

    // Gets path and returns it as an arraylist.
    public static <V,E> List<V> getPath(Graph<V,E> tree, V v){
        V temp = v;
        List<V> result = new ArrayList<V>();
        result.add(temp);

        // No path (obviously).
        if(!tree.hasVertex(v)) {
            result.add(v);
            return result;
        }

        while(tree.outDegree(temp) > 0) {
            for(V neighbor: tree.outNeighbors(temp)) {
                result.add(neighbor);
                temp = neighbor;
            }
        }

        return result;
    }

    // Returns a set of missing pertices.
    public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph){
        // temporary sets.
        Set<V> result = new HashSet<>();
        Set<V> temp = new HashSet<>();

        // fill temp set with each subgraph vertex.
        for (V v: subgraph.vertices()){
            temp.add(v);
        }

        // compare what in the graph isn't in the subgraph, and store that in result
        for (V v: graph.vertices()) {
            if (!temp.contains(v))
                result.add(v);
        }

        // return what's in the graph but not in the subgraph.
        return result;
    }

    // Return the average separation (path length) recursively.
    public static <V,E> double averageSeparation(Graph<V,E> tree, V root){
        int count = tree.numVertices();
        double total = 0;

        // uses recursive helper method instead of enumerating.
        total = helper(tree, root, 1);

        // return mathematical average.
        return total/count;
    }

    // Recursive helper for averageSeparation.
    private static <V,E> double helper(Graph<V,E> tree, V root, int path) {
        double total = 0;
        // Recursively accumulates.
        for(V vertex: tree.inNeighbors(root)) {
            total += path;
            total += helper(tree, vertex, path+1);
        }
        return total;

    }
}
