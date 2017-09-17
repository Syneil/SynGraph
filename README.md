# SynGraph

This project is an attempt to build an easy-to-use graph data-structure library.

# Usage

Creating graphs is straightforward:

    Graph<String, Integer> flightPaths = new GraphImpl<>(); // empty graph
    g.addEdge("LHR", 530, "EDI", false); // 2 vertices, one undirected edge
    g.addEdge("LHR", 5556, "JFK", false); // 3 vertices, two undirected edges
    g.addEdge("EDI", 5250, "JFK", false); // 3 vertices, two undirected edges

Simple stats are built into the interface:

    long airportCount = flightPaths.numberOfVertices(); // 3
    long pathCount = flightPaths.numberOfEdges(); // 3

Graph elements are available as streams:

    Stream<String> airports = flightPaths.vertices();
    Stream<Edge<String, Integer>> paths = flightPaths.edges();

Exploratory methods are available:

    if (flightPaths.hasVertex("LHR")) {
        Set<String> vacationOptions = flightPaths.neighbours("LHR");
    }
    if (flightPaths.hasEdge("LHR", "JFK")) {
        Set<Edge<String, Integer>> routesToJFK = flightPaths.getEdges("LHR", "JFK");
    }

Graph analysis is available through a utility class:

    if (GraphAnalyser.disjointed(flightPaths)) { // false
        // flight network consists of (at least!) 2 non-internavigable groups 
    }
    if (GraphAnalyser.isBridge(flightPaths, heathrowJFKPath)) { // false
        // the Heathrow-JFK flight is the only connection between two
        // otherwise non-internavigable groups
    }
    if (GraphAnalyser.hasArticulationPoint(flightPaths)) { // false
        // the flight network has a bottleneck! (or something)
    }

TODO (-cument):
* Constraints architecture
* Graph generation
* Graph reading (RDF)
* "Getting started" section in this readme
