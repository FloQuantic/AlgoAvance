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
        /* UBQP ------------------------------------------------------------------ */

        //Chargement de la matrice en entree
        int[][] Q = chargerQ("graphe12345.txt");

        //Init : pick a constructor
        int[] X = null;
        UBQP myUBQP = (X==null) ? new UBQP(Q) : new UBQP(Q,X);

        //Affichage donnees en entrees
        System.out.println(myUBQP.QtoString());
        System.out.println("X en entree : " + myUBQP.tabToString(myUBQP.getMesDonnees().getVecSolution()) + "\n");

        //Affichage resultats fonctions parametrees
        boolean contrainte = false;
        boolean tabou = false;

        System.out.println("Resultats donnees en entree :");
        myUBQP.getMesDonnees().afficherDonneesResultats();

        System.out.println("Resultats steepestHillClimbing :");
        myUBQP.steepestHillClimbing((contrainte)?constraint:0).afficherDonneesResultats();

        System.out.println("Resultats tabouClimbing :");
        myUBQP.tabouClimbing((contrainte)?constraint:0).afficherDonneesResultats();

        System.out.println("Resultats hillClimbingWithRestart :");
        myUBQP.hillClimbingWithRestart((contrainte)?constraint:0,tabou);
        myUBQP.getMesDonnees().afficherDonneesResultats();

        /* TSP -------------------------------------------------------------------- */

        //Chargement de la map en entree
        HashMap<Integer,int[]> mapVille = chargerMap("tsp101.txt");

        //Init : pick a constructor
        int[] T = null;
        TSP monTSP = (T==null) ? new TSP(mapVille) : new TSP(mapVille,T);

        //Affichage donnees en entrees
        System.out.println("Map en entree :\n" + monTSP.maMapToString(mapVille));
        System.out.println("Tournee en entree : " + monTSP.tabToString(monTSP.getMesDonnees().getVecSolution()) + "\n");

        //Affichage resultats fonctions parametrees
        boolean tabouTSP = true;

        System.out.println("Resultats donnees en entree :");
        monTSP.getMesDonnees().afficherDonneesResultats();

        System.out.println("Resultats steepestHillClimbing :");
        monTSP.steepestHillClimbing().afficherDonneesResultats();

        System.out.println("Resultats tabouClimbing :");
        monTSP.tabouClimbing(false).afficherDonneesResultats();

        System.out.println("Resultats hillClimbingWithRestart :");
        monTSP.hillClimbingWithRestart(tabouTSP);
        monTSP.getMesDonnees().afficherDonneesResultats();
        monTSP.getMesDonnees().afficherTabou();

    }

}
