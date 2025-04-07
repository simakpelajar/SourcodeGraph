import java.util.*;

public class GraphSearch {
  private int jumlahVertex;
  private String[] labelVertex;
  private Map<Integer, List<Node>> adjacencyList;

  class Node {
    int vertex, biaya;
    Node(int vertex, int biaya) { this.vertex = vertex; this.biaya = biaya; }
  }

  GraphSearch(int jumlahVertex, String[] labelVertex) {
    this.jumlahVertex = jumlahVertex;
    this.labelVertex = labelVertex;
    adjacencyList = new HashMap<>();
    for (int i = 0; i < jumlahVertex; i++) adjacencyList.put(i, new ArrayList<>());
  }

  void tambahEdge(int dari, int ke, int biaya) {
    adjacencyList.get(dari).add(new Node(ke, biaya));
    adjacencyList.get(ke).add(new Node(dari, biaya)); // undirected graph
  }

  // Cetak hasil pencarian
  private void printResult(String algorithm, List<String> path) {
    System.out.print(algorithm + ": " + String.join(" ", path) + "\n");
  }

  void BFS(int mulai) {
    boolean[] dikunjungi = new boolean[jumlahVertex];
    Queue<Integer> antrean = new LinkedList<>();
    List<String> result = new ArrayList<>();
    
    dikunjungi[mulai] = true;
    antrean.add(mulai);

    while (!antrean.isEmpty()) {
      int saatIni = antrean.poll();
      result.add(labelVertex[saatIni]);
      
      for (Node tetangga : adjacencyList.get(saatIni)) {
        if (!dikunjungi[tetangga.vertex]) {
          dikunjungi[tetangga.vertex] = true;
          antrean.add(tetangga.vertex);
        }
      }
    }
    printResult("BFS", result);
  }

  void DFS(int mulai) {
    boolean[] dikunjungi = new boolean[jumlahVertex];
    List<String> result = new ArrayList<>();
    dfsHelper(mulai, dikunjungi, result);
    printResult("DFS", result);
  }

  private void dfsHelper(int v, boolean[] dikunjungi, List<String> result) {
    dikunjungi[v] = true;
    result.add(labelVertex[v]);
    
    for (Node tetangga : adjacencyList.get(v)) {
      if (!dikunjungi[tetangga.vertex]) {
        dfsHelper(tetangga.vertex, dikunjungi, result);
      }
    }
  }

  void bestFirstSearch(int mulai, int tujuan) {
    boolean[] dikunjungi = new boolean[jumlahVertex];
    PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.biaya));
    List<String> result = new ArrayList<>();
    
    pq.add(new Node(mulai, 0));
    dikunjungi[mulai] = true;

    while (!pq.isEmpty()) {
      Node saatIni = pq.poll();
      int v = saatIni.vertex;
      result.add(labelVertex[v]);
      
      if (v == tujuan) break;

      for (Node tetangga : adjacencyList.get(v)) {
        if (!dikunjungi[tetangga.vertex]) {
          dikunjungi[tetangga.vertex] = true;
          pq.add(tetangga);
        }
      }
    }
    printResult("Best-First Search", result);
  }

  void hillClimbing(int mulai, int tujuan) {
    boolean[] dikunjungi = new boolean[jumlahVertex];
    List<String> result = new ArrayList<>();
    int sekarang = mulai;
    
    dikunjungi[mulai] = true;
    result.add(labelVertex[sekarang]);
    
    while (sekarang != tujuan) {
      Node terbaik = null;
      for (Node tetangga : adjacencyList.get(sekarang)) {
        if (!dikunjungi[tetangga.vertex]) {
          if (terbaik == null || tetangga.biaya < terbaik.biaya) {
            terbaik = tetangga;
          }
        }
      }
      if (terbaik == null) break;
      sekarang = terbaik.vertex;
      dikunjungi[sekarang] = true;
      result.add(labelVertex[sekarang]);
    }
    printResult("Hill Climbing", result);
  }

  void branchAndBound(int mulai, int tujuan) {
    boolean[] dikunjungi = new boolean[jumlahVertex];
    PriorityQueue<List<Node>> pq = new PriorityQueue<>(
        Comparator.comparingInt(jalur -> jalur.stream().mapToInt(n -> n.biaya).sum()));
    
    List<Node> jalurAwal = new ArrayList<>();
    jalurAwal.add(new Node(mulai, 0));
    pq.add(jalurAwal);

    while (!pq.isEmpty()) {
      List<Node> jalur = pq.poll();
      Node terakhir = jalur.get(jalur.size() - 1);
      
      if (dikunjungi[terakhir.vertex]) continue;
      dikunjungi[terakhir.vertex] = true;

      if (terakhir.vertex == tujuan) {
        List<String> result = new ArrayList<>();
        for (Node n : jalur) result.add(labelVertex[n.vertex]);
        printResult("Branch and Bound", result);
        return;
      }

      for (Node tetangga : adjacencyList.get(terakhir.vertex)) {
        if (!dikunjungi[tetangga.vertex]) {
          List<Node> jalurBaru = new ArrayList<>(jalur);
          jalurBaru.add(new Node(tetangga.vertex, tetangga.biaya));
          pq.add(jalurBaru);
        }
      }
    }
  }

  void dynamicProgamming(int mulai, int tujuan) {
    int[] jarak = new int[jumlahVertex];
    int[] prev = new int[jumlahVertex];
    Arrays.fill(jarak, Integer.MAX_VALUE);
    Arrays.fill(prev, -1);
    jarak[mulai] = 0;

    PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.biaya));
    pq.add(new Node(mulai, 0));

    while (!pq.isEmpty()) {
      Node saatIni = pq.poll();
      for (Node tetangga : adjacencyList.get(saatIni.vertex)) {
        int jarakBaru = jarak[saatIni.vertex] + tetangga.biaya;
        if (jarakBaru < jarak[tetangga.vertex]) {
          jarak[tetangga.vertex] = jarakBaru;
          prev[tetangga.vertex] = saatIni.vertex;
          pq.add(new Node(tetangga.vertex, jarakBaru));
        }
      }
    }

    List<String> path = new ArrayList<>();
    for (int at = tujuan; at != -1; at = prev[at]) {
      path.add(labelVertex[at]);
    }
    Collections.reverse(path);
    
    printResult("Shortest Path (Dijkstra)", path);
    System.out.println("Shortest Path Cost (Dijkstra): " + jarak[tujuan]);
  }

  public static void main(String[] args) {
    // Graf 1
    System.out.println("=== Graf 1: S -> Z ===");
    String[] labels1 = {"S", "A", "B", "C", "D", "E", "F", "Z"};
    GraphSearch graf1 = new GraphSearch(8, labels1);
    
    int[][] edges1 = {{0,1,4}, {0,2,3}, {1,2,5}, {1,4,5}, {2,3,3}, 
                      {4,3,5}, {4,6,3}, {3,5,2}, {5,7,2}};
    for (int[] e : edges1) graf1.tambahEdge(e[0], e[1], e[2]);

    runAllAlgorithms(graf1);

    // Graf 2
    System.out.println("\n=== Graf 2: A -> Z ===");
    String[] labels2 = {"A", "B", "C", "D", "E", "F", "G", "Z"};
    GraphSearch graf2 = new GraphSearch(8, labels2);
    
    int[][] edges2 = {{0,1,3}, {0,2,5}, {1,2,6}, {1,3,4}, {2,4,3}, 
                      {3,4,5}, {3,5,3}, {4,6,2}, {5,6,6}, {5,7,2}, {6,7,3}};
    for (int[] e : edges2) graf2.tambahEdge(e[0], e[1], e[2]);

    runAllAlgorithms(graf2);
  }

  private static void runAllAlgorithms(GraphSearch graph) {
    graph.BFS(0);
    graph.DFS(0);
    graph.bestFirstSearch(0, 7);
    graph.hillClimbing(0, 7);
    graph.branchAndBound(0, 7);
    graph.dynamicProgamming(0, 7);
  }
}