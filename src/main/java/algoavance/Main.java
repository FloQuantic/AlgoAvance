package algoavance;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main
{

    private static int constraint;

    private static int[][] chargerQ(String file){
        int[][] monQ = new int[0][];
        try{
            InputStream flux=new FileInputStream(file);
            InputStreamReader lecture=new InputStreamReader(flux);
            BufferedReader buff=new BufferedReader(lecture);
            String ligne;
            while ((ligne=buff.readLine())!=null){
                monQ = ligneTokenizer(ligne);
            }
            buff.close();
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
        return monQ;
    }

    private static int[][] ligneTokenizer(String ligne) {
        int[][] monQ;
        StringTokenizer st =  new StringTokenizer(ligne);
        int len = Integer.parseInt(st.nextToken(" "));
        monQ = new int[len][len];
        constraint = Integer.parseInt(st.nextToken(" "));
        for (int i=0; i<len; i++){
            for (int j=0; j<len; j++){
                monQ[i][j]=Integer.parseInt(st.nextToken(" "));
            }
        }
        return monQ;
    }

    private static HashMap<Integer,int[]> chargerMap(String file) {
        HashMap<Integer,int[]> maMap = new HashMap<>();
        try{
            InputStream flux=new FileInputStream(file);
            InputStreamReader lecture=new InputStreamReader(flux);
            BufferedReader buff=new BufferedReader(lecture);
            String ligne=buff.readLine();
            while ((ligne=buff.readLine())!=null){
                StringTokenizer st =  new StringTokenizer(ligne);
                int key = Integer.parseInt(st.nextToken("\t"));
                int[] coordonnees = {Integer.parseInt(st.nextToken("\t")), Integer.parseInt(st.nextToken("\t"))};
                maMap.put(key,coordonnees);
            }
            buff.close();
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
        return maMap;
    }

    public static void main( String[] args )
    {
        /*int[][] Q = chargerQ("graphe12345.txt");

        UBQP myUBQP = new UBQP(Q);
        System.out.println(myUBQP.QtoString());
        System.out.println(myUBQP.tabToString(myUBQP.hillClimbingWithRestart(constraint)) + "\nf=" + myUBQP.getF()+"\n");
        */
        HashMap<Integer,int[]> mapVille = chargerMap("tsp101.txt");

        TSP monTSP = new TSP(mapVille);
        System.out.println(monTSP.maMapToString());
        System.out.println(monTSP.tourneeToString());
        System.out.println(monTSP.tabouClimbing());
        System.out.println(monTSP.tourneeToString());
        System.out.println(monTSP.getDist());

    }

}
