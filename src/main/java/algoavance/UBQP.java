package algoavance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UBQP extends MetaAbs {

    private int[] X;
    private int[][] Q;
    private int tailleVector;
    private DonneesSolution mesDonnees;

    public UBQP(int[][] Q){
        this.Q = Q;
        tailleVector= Q[0].length;
        X = new int[tailleVector];
        mesDonnees = new DonneesSolution(X,0,-1);
        solutionAleatoire();
    }

    public UBQP(int[][] Q, int[] X){
        this.Q = Q;
        this.X = X;
        tailleVector= X.length;
        mesDonnees = new DonneesSolution(X,calculerResultat(X),-1);
    }

    public String QtoString(){
        String toReturn = "Q en entree :\n";
        for (int i=0; i<tailleVector; i++){
            toReturn += "[\t";
            for (int j=0; j<tailleVector; j++){
                toReturn += Q[i][j] + "\t";
            }
            toReturn += "]\n";
        }
        return toReturn;
    }

    private int calculerResultat(int[] vec){
        int f=0;
        for (int i=0; i<tailleVector; i++){
            for (int j=0; j<tailleVector; j++){
                f+=Q[i][j]*vec[i]*vec[j];
            }
        }
        return f;
    }

    public void solutionAleatoire(){
        Random r = new Random();
        for (int i=0; i<tailleVector; i++){
            X[i] = r.nextInt(2);
        }
        mesDonnees.setVecSolution(X);
        mesDonnees.setResultatFonction(calculerResultat(X));
    }

    private int[][] voisinsVecteurSolution(int[] vec){
        int[][] voisinsVecteurSolution = new int[tailleVector][tailleVector];
        for (int i=0; i<tailleVector; i++){
            for (int j=0; j<tailleVector; j++){
                if (i==j) {
                    voisinsVecteurSolution[i][j] = (vec[j] + 1) % 2;
                } else {
                    voisinsVecteurSolution[i][j] = vec[j];
                }
            }
        }
        return voisinsVecteurSolution;
    }

    private boolean contrainteRespecte(int[] vec, int constraint){
        int sum = 0;
        for (int i=0; i<tailleVector; i++){
            sum += vec[i];
        }
        return sum>=constraint;
    }

    private int[] meilleurVoisin(int[][] voisinage, int constraint){
        int[] fVoisins=  new int[voisinage.length];
        int len = 0;
        for (int i=0; i<fVoisins.length; i++){
            fVoisins[i]=calculerResultat(voisinage[i]);
        }

        ArrayList<Integer> indexList = new ArrayList<>();
        int ind = 0;
        while (ind<voisinage.length && !contrainteRespecte(voisinage[ind],constraint)) ind++;
        if (ind>=voisinage.length) return null;
        int min = fVoisins[ind];

        indexList.add(0);
        for (int i=ind; i<voisinage.length; i++){
            if (contrainteRespecte(voisinage[i],constraint)) {
                if (fVoisins[i] < min) {
                    min = fVoisins[i];
                    indexList.clear();
                    indexList.add(i);
                } else if (fVoisins[i] == min) {
                    indexList.add(i);
                }
            }
        }

        Random r = new Random();
        return voisinage[indexList.get(r.nextInt(indexList.size()))];
    }

    public DonneesSolution steepestHillClimbing(int constraint){
        DonneesSolution donneesTemp = new DonneesSolution(X,calculerResultat(X),0);
        int nbDepl = 0;
        boolean stop = false;
        int[] temp;

        do {
            temp = meilleurVoisin(voisinsVecteurSolution(X), constraint);
            if (temp==null) return donneesTemp;
            if (calculerResultat(temp)<calculerResultat(X)){
                X = temp;
            } else {
                stop = true;
            }
            nbDepl++;
        } while (nbDepl!=super.NB_DEPL && !stop);

        donneesTemp.setVecSolution(X);
        donneesTemp.setResultatFonction(calculerResultat(X));
        donneesTemp.setNbDeplacement(nbDepl);

        return donneesTemp;
    }

    public void hillClimbingWithRestart(int constraint, boolean withTabou){
        int[] min = X;
        DonneesSolution mesDonneesTemp;
        mesDonnees.setNbDeplacement(0);

        for (int i=0; i<super.NB_ESSAIS; i++){
            solutionAleatoire();
            mesDonneesTemp = (withTabou)?tabouClimbing(constraint):steepestHillClimbing(constraint);
            if (mesDonneesTemp.getResultatFonction()<calculerResultat(min)){
                min = X;
                if (mesDonneesTemp.getNbDeplacement()!=0) mesDonnees.setNbDeplacement(mesDonneesTemp.getNbDeplacement());
            }
        }

        X = min;
        mesDonnees.setVecSolution(X);
        mesDonnees.setResultatFonction(calculerResultat(X));

    }

    private int[][] voisinsNonTabou(int[] vec, Queue<int[]> maQueue){
        int[][] voisins = voisinsVecteurSolution(vec);
        int[][] voisinsSansTabou;
        HashMap<Integer,int[]> mapSansTabou = new HashMap<>();

        int indST = 0;
        for (int i=0; i<vec.length; i++){
            if (!containsElem(maQueue,voisins[i])) {
                mapSansTabou.put(indST,voisins[i]);
                indST++;
            }
        }

        voisinsSansTabou = new int[mapSansTabou.size()][vec.length];
        for (int i=0; i<mapSansTabou.size(); i++){
            voisinsSansTabou[i]=mapSansTabou.get(i);
        }

        return (indST!=0)?voisinsSansTabou:null;
    }

    public DonneesSolution tabouClimbing(int constraint){
        DonneesSolution mesDonnneesTemp = new DonneesSolution(X,calculerResultat(X),0);
        int[] min = X;
        int[] temp = X;
        Queue<int[]> listeTabou = new ConcurrentLinkedQueue<>();
        int nbDepl = 0;
        boolean stop = false;
        int[][] voisins;

        do{
            voisins = voisinsNonTabou(X,listeTabou);
            if (voisins!=null){
                temp = meilleurVoisin(voisins,constraint);
            } else {
                stop = true;
            }
            if (listeTabou.size()>=super.MAX_TABOU) listeTabou.remove(listeTabou.peek());
            listeTabou.add(X);
            if (calculerResultat(temp)<calculerResultat(min)) min = temp;
            X = temp;
            nbDepl++;
        } while (nbDepl<super.NB_DEPL && !stop);

        X = min;
        mesDonnneesTemp.setVecSolution(X);
        mesDonnneesTemp.setResultatFonction(calculerResultat(X));
        mesDonnneesTemp.setNbDeplacement(nbDepl);
        afficherListeTabou(listeTabou);

        return mesDonnneesTemp;
    }



    public DonneesSolution getMesDonnees() {
        return mesDonnees;
    }

}
