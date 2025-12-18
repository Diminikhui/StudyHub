import java.util.*;

class Edge implements Comparable<Edge> {
    int from, to, weight;

    Edge(int f, int t, int w) {
        from = f;
        to = t;
        weight = w;
    }

    public int compareTo(Edge e) {
        return Integer.compare(this.weight, e.weight);
    }
}

class DSU {
    int[] parent, rank;

    DSU(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    int find(int x) {
        if (parent[x] != x)
            parent[x] = find(parent[x]);
        return parent[x];
    }

    void union(int a, int b) {
        a = find(a);
        b = find(b);
        if (a != b) {
            if (rank[a] < rank[b]) parent[a] = b;
            else if (rank[a] > rank[b]) parent[b] = a;
            else {
                parent[b] = a;
                rank[a]++;
            }
        }
    }
}

public class MST_Kruskal {

    public static List<Edge> mst(int n, List<Edge> edges) {
        Collections.sort(edges);

        DSU dsu = new DSU(n);
        List<Edge> result = new ArrayList<>();

        for (Edge e : edges) {
            if (dsu.find(e.from) != dsu.find(e.to)) {
                dsu.union(e.from, e.to);
                result.add(e);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int n = 5;

        List<Edge> edges = Arrays.asList(
                new Edge(0, 1, 10),
                new Edge(0, 2, 6),
                new Edge(0, 3, 5),
                new Edge(1, 3, 15),
                new Edge(2, 3, 4)
        );

        List<Edge> mst = mst(n, edges);

        System.out.println("Минимальное остовное дерево:");
        for (Edge e : mst) {
            System.out.println(e.from + " — " + e.to + " : " + e.weight);
        }
    }
}
