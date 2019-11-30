package algoavance;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main
{

    private static int constraint;

    public static int[][] chargerQ(String file){
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

    public static void main( String[] args )
    {
        int[][] Q = chargerQ("graphe12345.txt");

        UBQP myUBQP = new UBQP(Q);
        System.out.println(myUBQP.QtoString());
        System.out.println(myUBQP.tabToString(myUBQP.hillClimbingWithRestart()) + "\nf=" + myUBQP.getF());
    }
}
