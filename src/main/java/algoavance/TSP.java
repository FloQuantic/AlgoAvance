package algoavance;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TSP {

    private static final int NB_DEPL = 1000;
    private static final int NB_ESSAIS = 10000;
    private static final int MAX_TABOU = 10;
    private int[] tournee;
    private HashMap<Integer,int[]> maMap;
    private double dist;

    public TSP(HashMap<Integer,int[]> maMap) {
        this.maMap = maMap;
        tournee = new int[maMap.size()];
        solutionAleatoire();
        dist = calculerDistance(tournee);
    }

    public String tourneeToString(){
        return UBQP.tabToString(tournee);
    }

    public String maMapToString(){
        String toReturn = "";
        for (Integer x: maMap.keySet()) {
            toReturn += x + " :\t" + UBQP.tabToString(maMap.get(x))+ "\n";
        }
        return toReturn;
    }

    public double getDist(){
        calculerDistance(tournee);
        return dist;
    }

    public void solutionAleatoire(){
        ArrayList<Integer> maListe = new ArrayList<>();
        for (int i=0; i<maMap.size(); i++){
            maListe.add(i+1);
        }

        Random r = new Random();
        int i=0;
        while (maListe.size()>0){
            int j =  r.nextInt(maListe.size());
            tournee[i] = maListe.get(j);
            maListe.remove(maListe.get(j));
            i++;
        }
    }

    private double distEucl(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    private double calculerDistance(int[] sol) {
        dist=0;
        dist+=distEucl(0,0,maMap.get(sol[0])[0],maMap.get(sol[0])[1]);
        for (int i=1; i<sol.length; i++){
            dist+=distEucl(maMap.get(sol[i-1])[0],maMap.get(sol[i-1])[1],maMap.get(sol[i])[0],maMap.get(sol[i])[1]);
        }
        dist+=distEucl(maMap.get(sol[sol.length-1])[0],maMap.get(sol[sol.length-1])[1],0,0);
        return dist;
    }

    private HashMap<Integer,int[]> voisinsTournee(int[] vec){
        HashMap<Integer,int[]> mesVoisins = new HashMap<>();

        int[] temp;
        int perm;
        for (int i=0; i<vec.length; i++){
            temp=vec;
            perm=temp[i];
            temp[i]=temp[(i+1)%vec.length];
            temp[(i+1)%vec.length]=perm;
            mesVoisins.put(i,temp);
        }

        return mesVoisins;
    }

    private int[] meilleurVoisin(HashMap<Integer,int[]> voisins){
        double[] distVoisins = new double[voisins.size()];
        for (int i=0; i<voisins.size(); i++){
            distVoisins[i]=calculerDistance(voisins.get(i));
        }

        ArrayList<Integer> indexList = new ArrayList<>();
        indexList.add(0);
        double min = distVoisins[0];
        for (int i=0; i<distVoisins.length; i++){
            if (distVoisins[i] < min) {
                min = distVoisins[i];
                indexList.clear();
                indexList.add(i);
            } else if (distVoisins[i] == min) {
                indexList.add(i);
            }
        }

        Random r = new Random();
        return voisins.get(indexList.get(r.nextInt(indexList.size())));
    }

    public int[] steepestHillClimbing(){
        int nbDepl = 0;
        boolean stop = false;
        int[] temp;

        do {
            temp = meilleurVoisin(voisinsTournee(tournee));
            if (calculerDistance(temp)<calculerDistance(tournee)){
                tournee = temp;
            } else {
                stop = true;
            }
            nbDepl++;
        } while (nbDepl!=NB_DEPL && !stop);

        return tournee;
    }

    public int[] hillClimbingWithRestart(){
        int[] min = tournee;

        for (int i=0; i<NB_ESSAIS; i++){
            solutionAleatoire();
            if (calculerDistance(steepestHillClimbing())<calculerDistance(min)){
                min = tournee;
            }
        }

        tournee = min;

        return tournee;
    }

    private HashMap<Integer, int[]> voisinsNonTabou(int[] vec, Queue<int[]> maQueue){
        HashMap<Integer,int[]> voisins = voisinsTournee(vec);
        HashMap<Integer,int[]> mapSansTabou = new HashMap<>();

        int indST = 0;
        for (int i=0; i<vec.length; i++){
            if (!maQueue.contains(voisins.get(i))) {
                mapSansTabou.put(indST,voisins.get(i));
                indST++;
            }
        }


        return (indST!=0)?mapSansTabou:null;
    }

    public int[] tabouClimbing(){
        int[] min = tournee;
        int[] temp = tournee;
        Queue<int[]> listeTabou = new ConcurrentLinkedQueue<>();
        int nbDepl = 0;
        boolean stop = false;
        HashMap<Integer,int[]> voisins;

        do{
            voisins = voisinsNonTabou(tournee,listeTabou);
            if (voisins!=null){
                temp = meilleurVoisin(voisins);
            } else {
                stop = true;
            }
            if (listeTabou.size()>=MAX_TABOU) listeTabou.remove(listeTabou.peek());
            listeTabou.add(tournee);
            if (calculerDistance(temp)<calculerDistance(min)) min = temp;
            tournee = temp;
            nbDepl++;
        } while (nbDepl<NB_DEPL && !stop);

        tournee = min;
        return tournee;
    }

}
