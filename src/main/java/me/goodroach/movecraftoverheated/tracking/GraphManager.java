package me.goodroach.movecraftoverheated.tracking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class GraphManager {
    public GraphManager() {

    }

    // Perform BFS and return a list of connected components (forest)
    public List<List<DispenserWeapon>> getForest(DispenserGraph graph) {
        graph.makeEdges();
        Map<DispenserWeapon, List<DispenserWeapon>> adjList = graph.getAdjList();

        Set<DispenserWeapon> visited = new HashSet<>();
        List<List<DispenserWeapon>> forest = new ArrayList<>();

        // Traverse each node in the adjacency list
        for (DispenserWeapon dispenser : adjList.keySet()) {
            if (!visited.contains(dispenser)) {
                List<DispenserWeapon> connectedComponent = bfs(dispenser, visited, adjList);
                forest.add(connectedComponent);
            }
        }

        return forest;
    }

    // BFS algorithm to explore the graph and return the connected component
    private List<DispenserWeapon> bfs(
        DispenserWeapon start,
        Set<DispenserWeapon> visited,
        Map<DispenserWeapon, List<DispenserWeapon>> adjList
    ) {
        List<DispenserWeapon> component = new ArrayList<>();
        Queue<DispenserWeapon> queue = new LinkedList<>();
        queue.offer(start);
        visited.add(start);

        // Perform BFS
        while (!queue.isEmpty()) {
            DispenserWeapon current = queue.poll();
            component.add(current);

            // Traverse neighbors of the current node
            for (DispenserWeapon neighbor : adjList.get(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }

        return component;
    }
}

