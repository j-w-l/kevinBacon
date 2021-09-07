import java.io.*;
import java.util.*;

/**
 * Jonathan Lee.
 * Winter 2020. Revised 6/18/21.
 */

public class Bacon {
    // instance variables
    protected Map<Integer, String> movies;
    protected Map<Integer, String> actors;
    protected Map<Integer, List<Integer>> actorToMovie;
    protected Map<Integer, List<Integer>> movieToActor;

    // Constructor -- reads in test files.
    public Bacon() {
        // Read in map of movies.
        try {
            BufferedReader input = new BufferedReader(new FileReader("inputs/movies.txt"));
            String line;
            movies = new HashMap<Integer, String>();
            // Until you read in all the movies.
            while ((line = input.readLine()) != null) {
                String [] pieces = line.split("\\|");
                if (!movies.containsKey(Integer.parseInt(pieces[0])));
                    movies.put(Integer.parseInt(pieces[0]), pieces[1]);
            }
            input.close();
        }

        catch (Exception e) {
            System.out.println("Error opening file!");
        }

        // Read in map of actors.
        try {
            BufferedReader input = new BufferedReader(new FileReader("inputs/actors.txt"));
            String line;
            actors = new HashMap<Integer, String>();
            // until you read in all the actors/
            while ((line = input.readLine()) != null) {
                String [] pieces = line.split("\\|");
                if (!actors.containsKey(Integer.parseInt(pieces[0])));
                    actors.put(Integer.parseInt(pieces[0]), pieces[1]);
            }

            input.close();
        }

        catch (Exception e) {
            System.out.println("Error opening file!");
        }

        // Read in map of actors to movies.
        try {
            BufferedReader input = new BufferedReader(new FileReader("inputs/movie-actors.txt"));
            String line;
            actorToMovie = new HashMap<Integer, List<Integer>>();
            while ((line = input.readLine()) != null) {
                String [] pieces = line.split("\\|");
                // If map doesn't already contain key
                if (!actorToMovie.containsKey(Integer.parseInt(pieces[1]))) {
                    actorToMovie.put(Integer.parseInt(pieces[1]), new ArrayList<Integer>());
                    actorToMovie.get(Integer.parseInt(pieces[1])).add(Integer.parseInt(pieces[0]));
                }
                // Else if it already contains the key.
                else {
                    actorToMovie.get(Integer.parseInt(pieces[1])).add(Integer.parseInt(pieces[0]));
                }
            }

            input.close();
        }

        catch (Exception e) {
            System.out.println("Error opening file!");
        }

        // Read in map of movies to actors.
        try {
            BufferedReader input = new BufferedReader(new FileReader("inputs/movie-actors.txt"));
            String line;
            movieToActor = new HashMap<Integer, List<Integer>>();
            // Same concept as above, but in reverse: movies to actors.
            while ((line = input.readLine()) != null) {
                String [] pieces = line.split("\\|");
                if (!movieToActor.containsKey(Integer.parseInt(pieces[0]))) {
                    movieToActor.put(Integer.parseInt(pieces[0]), new ArrayList<Integer>());
                    movieToActor.get(Integer.parseInt(pieces[0])).add(Integer.parseInt(pieces[1]));
                }
                else {
                    movieToActor.get(Integer.parseInt(pieces[0])).add(Integer.parseInt(pieces[1]));
                }
            }

            input.close();
        }

        catch (Exception e) {
            System.out.println("Error opening file!");
        }
    }

    // buildGraph. Constructs our directed "Bacon" grpah.
    public Graph<String, Set<String>> buildGraph() {
        // Our directed graph -- will return this.
        Graph<String, Set<String>> result = new AdjacencyMapGraph<>();

        // Shared actors. Stores vertex 1, and a map with vertex 2 and a set of shared actor names.
        Map<Integer, Map<Integer, Set<String>>> sharedActors = new HashMap<>();

        // for each actor
        for (Integer actor : actors.keySet()) {
            // for each movie
            for (Integer am : actorToMovie.get(actor)) {
                // for each movie's cast of actors
                for (Integer ma : movieToActor.get(am)) {
                    if (sharedActors.containsKey(actor) && actor != ma) {
                        // If the map of maps already contains it.
                        if (sharedActors.get(actor).containsKey(ma)) {
                            sharedActors.get(actor).get(ma).add(movies.get(am));
                        }
                        // If hte map of maps doesn't already contain it.
                        else {
                            sharedActors.get(actor).put(ma, new HashSet<>());
                            sharedActors.get(actor).get(ma).add(movies.get(am));
                        }
                    }

                    // Not a repeat key but not already contained in the map of maps.
                    else if (actor != ma) {
                        sharedActors.put(actor, new HashMap<>());
                        sharedActors.get(actor).put(ma, new HashSet<>());
                        sharedActors.get(actor).get(ma).add(movies.get(am));
                    }
                }
            }
        }

        // Set graph's vertices to the actors.
        for (Integer each : actors.keySet())
            result.insertVertex(actors.get(each));

        // Insert directed edge between relevant actor-actors.
        for (Integer x : sharedActors.keySet()) {
            for (Integer y : sharedActors.get(x).keySet()) {
                if (!actors.get(x).equals(actors.get(y))) { // weed out identical keys
                    result.insertDirected(actors.get(x), actors.get(y), sharedActors.get(x).get(y));
                }
            }
        }

        return result;
    }


    public void runGame(String source, Graph<String, Set<String>> graph) {
        // Prints out the commands for user reference.
        System.out.println("Commands:");
        System.out.println("a: average separation (path length)");
        System.out.println("n: find the number of actors who have a path (connected by some number of steps) to the current center");
        System.out.println("c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation");
        System.out.println("d <low> <high>: list actors sorted by degree, with degree between low and high");
        System.out.println("i: list actors with infinite separation from teh current center");
        System.out.println("p <name>: find path from <name> to current center o the universe");
        System.out.println("s <low> <high>: list actors sorted by noninfinite separation from the current center, with separation between low and high");
        System.out.println("u <name>: make <name> the center of the universe");
        System.out.println("q: quit game/n");

        // Print which actor is our center, how many connections, and average separation.
        System.out.println(source + " is now the center of the acting universe, connected to [#]/" + (GraphLibrary.bfs(graph, source).numVertices() - 1) + "actors with average separation [#]/" + GraphLibrary.averageSeparation(GraphLibrary.bfs(graph, source), source));
        Scanner input = new Scanner(System.in);
        String user = "";
        String center = source;

        // While the user hasn't quit the program:
        while (!user.equals("q") && !user.equals("Q")) {
            // Read in user's input on what game feature they want to play.
            System.out.println(center + " game > ");
            user = input.next();

            // Average separation feature.
            if (user.equals("a") || user.equals("A")) {
                // Just call the GraphLibrary function.
                System.out.println("Average separation for " + center + " is "  + GraphLibrary.averageSeparation(GraphLibrary.bfs(graph, center), center));
            }

            // "How many actors can find a path" feature.
            else if (user.equals("n") || user.equals("N")) {
                // Just use numVertices, much like in the "center of the acting universe" statement.
                System.out.println((GraphLibrary.bfs(graph, center).numVertices() - 1) + " actors have a path to " + center);
            }

            // Top centers of the universe -- supports both positive and negative inputs.
            else if (user.equals("c") || user.equals("C")) {
                Graph<String, Set<String>> temp = GraphLibrary.bfs(graph, center);

                // User input: number of centers in the list of top centers.
                System.out.println("How many actors: ");
                int howManyActors = input.nextInt();

                // Prevents invalid input case.
                while (howManyActors > temp.numVertices()) {
                    System.out.println("Invalid: how many actors: ");
                    howManyActors = input.nextInt();
                }

                // Maps each center to its averageSeparation.
                Map<String, Double> centerToSeparation = new HashMap<>();
                for (String each : temp.vertices())
                    centerToSeparation.put(each, GraphLibrary.averageSeparation(GraphLibrary.bfs(graph, each), each));

                // For testing purposes -- seeing the averageSeparation of each center, side by side.
//                for (String each : centerToSeparation.keySet())
//                    System.out.println(each + " : " + centerToSeparation.get(each));

                // Positive input case.
                if (howManyActors > 0) {
                    // Using a priority queue:
                    Comparator<String> QueueComparator = (String d1, String d2) -> (int)((centerToSeparation.get(d1)-centerToSeparation.get(d2))*99999); // Needed because of the (int) cast.
                    PriorityQueue<String> queue = new PriorityQueue<String>(QueueComparator);
                    queue.addAll(centerToSeparation.keySet());
                    System.out.print("{");
                    for (int i = 0; i < howManyActors; i ++) {
                        Double value = centerToSeparation.get(queue.peek());
                        String s = queue.remove();
                        System.out.print(s + " - " + value);
                        if (i < howManyActors - 1)
                            System.out.print(", ");
                    }
                    System.out.print("}\n");
                }

                // Negative input case.
                else if (howManyActors < 0) {
                    // Using an ArrayList, to spice things up:
                    Comparator<String> ListComparator = (String d1, String d2) -> (int)((centerToSeparation.get(d2)-centerToSeparation.get(d1))*99999); // min queue
                    List<String> list = new ArrayList<String>(); // Max PQ (reverse ordered)
                    list.addAll(centerToSeparation.keySet());
                    list.sort(ListComparator);
                    System.out.print("{");
                    for (int i = 0; i < Math.abs(howManyActors); i ++) {
                        System.out.print(list.get(i) + " - " + centerToSeparation.get(list.get(i)));
                        if (i < howManyActors - 1)
                            System.out.print(", ");
                    }
                    System.out.print("}\n");
                }
            }

            // Sort by degree.
            else if (user.equals("d") || user.equals("D")) {
                // Get user input.
                System.out.println("Low index: ");
                int low = input.nextInt();

                System.out.println();

                System.out.println("High index: ");
                int high = input.nextInt();

                System.out.println();

                // Prevent invalid case where low is higher than high
                if (low > high) {
                    System.out.println("Error: mixed low and high values!");
                    System.out.print("\n");
                }

                else {
                    // Store the BFS from center.
                    Graph<String, Set<String>> tempGraph = GraphLibrary.bfs(graph, center);
                    // List of vertices -- necessary for sorting
                    List<String> vertices = new ArrayList<>();

                    for (String v : tempGraph.vertices()) {
                        vertices.add(v);
                    }

                    // Compares by degree (in reverse)
                    Comparator<String> vertexComp = (String v1, String v2) -> tempGraph.inDegree(v2) - tempGraph.inDegree(v1);
                    vertices.sort(vertexComp); // sorts the list accordingly

                    System.out.print("{");
                    for (int i = 0; i < vertices.size(); i++) {
                        // Makes sure that you remain with bounds set by user (low, high).
                        if (low <= tempGraph.inDegree(vertices.get(i)) && (high >= tempGraph.inDegree(vertices.get(i)))) {
                            System.out.print(vertices.get(i) + " : " + tempGraph.inDegree(vertices.get(i)));
                            if (i < vertices.size() - 1)
                                System.out.print(", ");
                        }
                    }
                    System.out.print("}\n");
                }
            }

            // Missing Vertices feature.
            else if (user.equals("i") || user.equals("I")) {
                // just calls the graphLibrary method.
                System.out.println(GraphLibrary.missingVertices(graph, GraphLibrary.bfs(graph, center)));
            }

            // Path function...just prints the path of how two actors are connected, IF it exists.
            else if (user.equals("p") || user.equals("P")) {
                String name = input.nextLine();

                // Prevents input that doesn't exist in the grpah.
                while (!graph.hasVertex(name)) {
                    System.out.print("Try again: ");
                    name = input.nextLine();
                }

                System.out.println();

                // tempGraph returns the BFS graph.
                Graph<String, Set<String>> tempGraph = GraphLibrary.bfs(graph, center);

                if (tempGraph.hasVertex(name)) {
                    List<String> path = GraphLibrary.getPath(tempGraph, name);
                    for (int i = 0; i < path.size() - 1; i++)
                        System.out.println(path.get(i) + " was in " + tempGraph.getLabel(path.get(i), path.get(i + 1)) + " with " + path.get(i + 1));
                }

                else { System.out.println(name + " not connected to " + center + "\n"); }

            }

            // Noninfinite separation sorting.
            else if (user.equals("s") || user.equals("S")) {
                // Get user input.
                System.out.println("Low index: ");
                int low = input.nextInt();

                System.out.println();

                System.out.println("High index: ");
                int high = input.nextInt();

                System.out.println();

                // Prevents invalid case where the low value is higher than the high one...
                if (low > high) {
                    System.out.println("Error: mixed low and high values!");
                    System.out.print("\n");
                }

                else {
                    // Stores BFS on the graph from the center
                    Graph<String, Set<String>> tempGraph = GraphLibrary.bfs(graph, center);
                    // Stroes a map of vertices to the size of their path.
                    Map<String, Integer> verticesToDegree = new HashMap<>();
                    for (String each : tempGraph.vertices())
                        verticesToDegree.put(each, GraphLibrary.getPath(tempGraph, each).size() - 1);

                    // Functionally just comparator.comparingint...
                    Comparator<String> vertexComp = (String v1, String v2) -> verticesToDegree.get(v1) - verticesToDegree.get(v2);

                    // List of vertices -- store with the vertices from the graph (from keySet of verticestodegree)
                    // List is key data structure, like degree sorting, because it enables sorting!
                    List<String> list = new ArrayList<>();
                    list.addAll(verticesToDegree.keySet());
                    list.sort(vertexComp);

                    // Indicates if we've found a path. If this remains false, then there's no actors between the degrees the user has entered.
                    boolean contains = false;

                    for (int i = low; i < high; i++) {
                        if (verticesToDegree.containsValue(i)) {
                            contains = true;
                        }
                    }

                    if (!contains) {
                        System.out.println("Try again: no actors between your bounds.");
                    }

                    else if (contains) { // same as else, always true...
                        System.out.print("{");
                        for (int i = 0; i < list.size(); i++) {
                            // Same concept as degree sorting...
                            if ((low <= verticesToDegree.get(list.get(i))) && (high >= verticesToDegree.get(list.get(i)))) {
                                System.out.print(list.get(i) + " : " + verticesToDegree.get(list.get(i)));
                                if (i < list.size() - 1)
                                    System.out.print(", ");
                            }
                        }
                        System.out.print("}\n");
                    }
                }
            }

            // Replace the current center method.
            else if (user.equals("u") || user.equals("U")) {
                String name = input.nextLine();

                // Insists on an input that actually a legit actor.
                while (!graph.hasVertex(name)) {
                    System.out.print("Try again: ");
                    name = input.nextLine();
                }

                // Once valid, reset our center of the universe to that actor.
                center = name;

                // Announce it like you did at the beginning (name, # of connections, average separation)
                System.out.println(center + " is now the center of the acting universe, connected to [#]/" + (GraphLibrary.bfs(graph, center).numVertices() - 1) + "actors with average separation [#]/" + GraphLibrary.averageSeparation(GraphLibrary.bfs(graph, center), center));
            }
        }

        // Close the file; you're done with it!
        input.close();
    }


    // Test run our graph.
    public static void main(String[] args) {
        // Test run our graph with "Kevin Bacon" as the starting center of the universe.
        Bacon game = new Bacon();
        game.runGame("Kevin Bacon", game.buildGraph());
    }
}
