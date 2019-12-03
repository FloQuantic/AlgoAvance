package algoavance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TSP extends MetaAbs {

    private int[] tournee;
    private HashMap<Integer,int[]> maMap;
    private DonneesSolution mesDonnees;

    public TSP(HashMap<Integer,int[]> maMap) {
        this.maMap = maMap;
        tournee = new int[maMap.size()];
        solutionAleatoire();
        mesDonnees = new DonneesSolution(tournee,calculerDistance(tournee),-1);
    }

    public TSP(HashMap<Integer,int[]> maMap, int[] tournee) {
        this.maMap = maMap;
        this.tournee = tournee;
        mesDonnees = new DonneesSolution(tournee,calculerDistance(tournee),-1);
    }

    public String maMapToString(HashMap<Integer, int[]> map){
        String toReturn = "";
        for (Integer x: map.keySet()) {
            toReturn += x + " :\t" + tabToString(map.get(x))+ "\n";
        }
        return toReturn;
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
        double dist=0;
        dist+=distEucl(0,0,maMap.get(sol[0])[0],maMap.get(sol[0])[1]);
        for (int i=1; i<sol.length; i++){
            dist+=distEucl(maMap.get(sol[i-1])[0],maMap.get(sol[i-1])[1],maMap.get(sol[i])[0],maMap.get(sol[i])[1]);
        }
        dist+=distEucl(maMap.get(sol[sol.length-1])[0],maMap.get(sol[sol.length-1])[1],0,0);
        return dist;
    }

    private HashMap<Integer,int[]> voisinsTournee(int[] vec){
        HashMap<Integer,int[]> mesVoisins = new HashMap<>();

        int[] temp = new int[vec.length];
        int perm;
        int ind = 0;
        for (int i=0; i<vec.length; i++){
            for (int k=i+1; k<vec.length; k++) {
                for (int j = 0; j < vec.length; j++) {
                    temp[j] = vec[j];
                }
                perm = temp[i];
                temp[i] = temp[k];
                temp[k] = perm;
                int[] t = new int[vec.length];
                for (int j = 0; j < vec.length; j++) {
                    t[j] = temp[j];
                }
                mesVoisins.put(ind, t);
                ind++;
            }
        }

        return mesVoisins;
    }

    private int[] meilleurVoisin(HashMap<Integer,int[]> voisins){
        if (voisins.isEmpty()) return tournee;
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

    public DonneesSolution steepestHillClimbing(){
        DonneesSolution mesDonneesTemp = new DonneesSolution(tournee,calculerDistance(tournee),0);
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
        } while (nbDepl!=super.NB_DEPL && !stop);

        mesDonneesTemp.setVecSolution(tournee);
        mesDonneesTemp.setResultatFonction(calculerDistance(tournee));
        mesDonneesTemp.setNbDeplacement(nbDepl);

        return mesDonneesTemp;
    }

    public void hillClimbingWithRestart(boolean withTabou){
        DonneesSolution mesDonneesTemp;
        int[] min = tournee;
        mesDonnees.setNbDeplacement(0);

        for (int i=0; i<super.NB_ESSAIS; i++){
            solutionAleatoire();
            mesDonneesTemp = (withTabou)?tabouClimbing(withTabou):steepestHillClimbing();
            if (mesDonneesTemp.getResultatFonction()<calculerDistance(min)){
                min = tournee;
                if (mesDonneesTemp.getNbDeplacement()!=0){
                    mesDonnees.setTabous(mesDonneesTemp.getTabous());
                    mesDonnees.setNbDeplacement(mesDonneesTemp.getNbDeplacement());
                }
            }
        }

        tournee = min;
        mesDonnees.setVecSolution(tournee);
        mesDonnees.setResultatFonction(calculerDistance(tournee));
    }

    private HashMap<Integer, int[]> voisinsNonTabou(int[] vec, Queue<int[]> maQueue){
        HashMap<Integer,int[]> voisins = voisinsTournee(vec);
        HashMap<Integer,int[]> mapSansTabou = new HashMap<>();

        int indST = 0;
        for (int i=0; i<voisins.size(); i++){
            if (!containsElem(maQueue,voisins.get(i))) {
                mapSansTabou.put(indST,voisins.get(i));
                indST++;
            }
        }

        return (indST>0)?mapSansTabou:null;
    }

    public DonneesSolution tabouClimbing(boolean restart){
        DonneesSolution mesDonneeesTemp = new DonneesSolution(tournee,calculerDistance(tournee),0);
        int[] min = tournee;
        int[] temp = tournee;
        Queue<int[]> listeTabou = new ConcurrentLinkedQueue<>();
        int nbDepl = 0;
        boolean stop = false;
        HashMap<Integer,int[]> voisins = maMap;

        do{
            voisins = voisinsNonTabou(tournee,listeTabou);
            if (voisins!=null){
                temp = meilleurVoisin(voisins);
            } else {
                stop = true;
            }
            if (listeTabou.size()>=super.MAX_TABOU) listeTabou.remove(listeTabou.peek());
            listeTabou.add(tournee);
            if (calculerDistance(temp)<calculerDistance(min)) min = temp;
            tournee = temp;
            nbDepl++;
        } while (nbDepl<super.NB_DEPL && !stop);

        tournee = min;
        mesDonneeesTemp.setVecSolution(tournee);
        mesDonneeesTemp.setResultatFonction(calculerDistance(tournee));
        mesDonneeesTemp.setNbDeplacement(nbDepl);
        mesDonneeesTemp.setTabous(listeTabou);
        if (!restart) afficherListeTabou(listeTabou);

        return mesDonneeesTemp;
    }

    public DonneesSolution getMesDonnees() {
        return mesDonnees;
    }
}
