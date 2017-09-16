package com.ourcode.models.algorithms;

import com.ourcode.models.input.OCCostUnit;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by LEOLEOl on 5/12/2017.
 */
public class Floyd {
    static public int size_max = 100;
    static public double maxC = 1000000.0;

    private double[][] matrix;
    private int[][] Trace;
    private Hashtable<Pair, Double> hashTableMatrix; // Pair of (int, int), (string, string)
    private Hashtable<Pair, String> hashTableTrace;// Pair of (string, string)
    private Hashtable<Pair, Integer> hashTableTraceInteger; // Pair of (int, int)


    //
    private int n;
    private Set<String> locationCodeSet;

    public Floyd() {
        matrix = new double[size_max][size_max];
        Trace = new int[size_max][size_max];

        hashTableMatrix = new Hashtable<>();
        hashTableTrace = new Hashtable<>();
        hashTableTraceInteger = new Hashtable<>();
    }

    public void loadGraph(String file_path) {
        try (BufferedReader br = new BufferedReader(new FileReader(file_path))) {
            String line = br.readLine();
            Scanner scanner = new Scanner(line);
            n = scanner.nextInt();

            locationCodeSet = new HashSet<>();

            // Init matrix
            for (int i = 1; i <= n; ++i) {
                locationCodeSet.add(Integer.toString(i));
                for (int j = 1; j <= n; ++j)
                    if (i == j) {
                        matrix[i][j] = 0;
                        hashTableMatrix.put(new Pair(Integer.toString(i), Integer.toString(j)), 0.0);
                    } else {
                        matrix[i][j] = maxC;
                        hashTableMatrix.put(new Pair(Integer.toString(i), Integer.toString(j)), maxC);
                    }
            }

            while ((line = br.readLine()) != null) {
                scanner = new Scanner(line);
                int a = scanner.nextInt(), b = scanner.nextInt();
                double c = scanner.nextDouble();

                matrix[a][b] = c;
                hashTableMatrix.put(new Pair(Integer.toString(a), Integer.toString(b)), c);

                // process the line.
                System.out.printf("%d %d %f\n", a, b, c);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGraph(ArrayList<OCCostUnit> costUnits, Set<String> locationCodeSet)
    {
        this.locationCodeSet = locationCodeSet;
        for (OCCostUnit costUnit: costUnits)
            hashTableMatrix.put(new Pair(costUnit.getSrcLocationCode(), costUnit.getDesLocationCode()), costUnit.getTravelTime());
    }

    public void loadGraph(Hashtable<Pair, Double> hashTableCosts, Set<String> locationCodeSet)
    {
        this.hashTableMatrix = hashTableCosts;
        this.locationCodeSet = locationCodeSet;
        for (String u: locationCodeSet) hashTableMatrix.put(new Pair(u, u), 0.0);

    }

//    public void runFloyd() {
//        for (int u = 1; u <= n; ++u)
//            for (int v = 1; v <= n; ++v) {
//                hashTableTraceInteger.put(new Pair(u, v), v);
//                Trace[u][v] = v;
//            }
//
//        for (int k = 1; k <= n; ++k)
//            for (int u = 1; u <= n; ++u)
//                for (int v = 1; v <= n; ++v) {
//                    Pair uv = new Pair(u, v), uk = new Pair(u, k), kv = new Pair(k, v);
//                    if (hashTableMatrix.get(uv) > hashTableMatrix.get(uk) + hashTableMatrix.get(kv)) {
//                        matrix[u][v] = matrix[u][k] + matrix[k][v];
//                        hashTableMatrix.put(uv, hashTableMatrix.get(uk) + hashTableMatrix.get(kv));
//                        Trace[u][v] = Trace[u][k];
//                        hashTableTraceInteger.put(uv, hashTableTraceInteger.get(uk));
//                    }
//                }
//    }

    public void runFloyd()
    {
        for (String u: locationCodeSet)
            for (String v: locationCodeSet)
                hashTableTrace.put(new Pair(u, v), v);

        for (String k: locationCodeSet)
            for (String u: locationCodeSet)
                for (String v: locationCodeSet) {
                    Pair uv = new Pair(u, v), uk = new Pair(u, k), kv = new Pair(k, v);
                    if (hashTableMatrix.get(uv) > hashTableMatrix.get(uk) + hashTableMatrix.get(kv)) {
                        hashTableMatrix.put(uv, new Double(hashTableMatrix.get(uk) + hashTableMatrix.get(kv)));
                        hashTableTrace.put(uv, hashTableTrace.get(uk));
                    }
                }
    }

    public Hashtable getHashTableMatrix()
    {
        return new Hashtable(hashTableMatrix);
    }

    public Hashtable getHashTableTrace()
    {
        return new Hashtable(hashTableTrace);
    }



    public void printResult(int s, int f) {
        double sf = hashTableMatrix.get(new Pair(s, f));
        if (sf == maxC)
            System.out.printf("There is no path from %d to %d", s, f);
        else {
            System.out.printf("Distance from %d to %d: %f\n", s, f, sf);
            do {
                System.out.printf("%d -> ", s);
                //s = Trace[s][f];
                s = hashTableTraceInteger.get(new Pair(s, f));
            } while (s != f);
            System.out.printf("%d\n", f);
        }
    }

    public void printResult(String s, String f)
    {
        Double sf = hashTableMatrix.get(new Pair(s, f));
        if (sf == maxC) {
            System.out.printf("There is no path from %s to %s", s, f);
        }
        else {
            System.out.printf("Distance from %s to %s: %f\n", s, f, sf);
            do {
                System.out.printf("%s -> ", s);
                //s = Trace[s][f];
                s = hashTableTrace.get(new Pair(s, f));
            } while (!s.equals(f));
            System.out.printf("%s\n", f);
        }
    }

    public static void main(String[] args) {
        Floyd floyd = new Floyd();
        floyd.loadGraph("E:/data.txt");
        floyd.runFloyd();
        floyd.printResult(1, 4);
    }
}
