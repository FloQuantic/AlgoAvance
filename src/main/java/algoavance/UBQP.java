package algoavance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UBQP {

    private final static int NB_DEPL = 100;
    private final static int NB_ESSAIS = 100;
    private final static int MAX_TABOU = 6;
    private int[] X;
    private int[][] Q;
    private int f;
    private int qLenght;

    public UBQP(int[][] Q){
        this.Q = Q;
        qLenght= Q[0].length;
        X = new int[qLenght];
        solutionAleatoire();
    }

    public String XtoString(){
        String toReturn = "X:\n";
        toReturn += tabToString(X);
        return toReturn;
    }

    public String QtoString(){
        String toReturn = "Q:\n";
        toReturn += matToString(Q);
        return toReturn;
    }

    public String tabToString(int[] tab){
        String toReturn = "[\t";
        for (int i=0; i<tab.length; i++){
            toReturn+= tab[i] + "\t";
        }
        toReturn += "]";
        return toReturn;
    }

    public String matToString(int[][] mat){
        String toReturn = "";
        for (int i=0; i<mat[0].length; i++){
            toReturn += "[\t";
            for (int j=0; j<mat[0].length; j++){
                toReturn += mat[i][j] + "\t";
            }
            toReturn += "]\n";
        }
        return toReturn;
    }

    public int getF(){
        f = calculerf(X);
        return f;
    }

    private int calculerf(int[] vec){
        int f=0;
        for (int i=0; i<vec.length; i++){
            for (int j=0; j<vec.length; j++){
                f+=Q[i][j]*vec[i]*vec[j];
            }
        }
        return f;
    }

    public void solutionAleatoire(){
        Random r = new Random();
        for (int i=0; i<X.length; i++){
            X[i] = r.nextInt(2);
        }
        f=calculerf(X);
    }

    private int[][] voisinsX(int[] vec){
        int[][] voisinsX = new int[vec.length][vec.length];
        for (int i=0; i<vec.length; i++){
            for (int j=0; j<vec.length; j++){
                if (i==j) {
                    voisinsX[i][j] = (vec[j] + 1) % 2;
                } else {
                    voisinsX[i][j] = vec[j];
                }
            }
        }
        return voisinsX;
    }

    private boolean contrainteRespecte(int[] vec, int constraint){
        int sum = 0;
        for (int i=0; i<vec.length; i++){
            sum += vec[i];
        }
        return sum>=constraint;
    }

    private int[] meilleurVoisin(int[][] voisinage, int constraint){
        int[][] voisins = voisinage;
        int[] fVoisins=  new int[voisinage[0].length];
        for (int i=0; i<voisinage[0].length; i++){
            fVoisins[i]=calculerf(voisins[i]);
        }

        ArrayList<Integer> indexList = new ArrayList<>();
        int ind = 0;
        while (ind<voisinage[0].length && !contrainteRespecte(voisins[ind],constraint)) ind++;
        if (ind>=voisinage[0].length) return null;
        int min = fVoisins[ind];

        indexList.add(0);
        for (int i=ind; i<voisinage[0].length; i++){
            if (contrainteRespecte(voisins[i],constraint)) {
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
        return voisins[indexList.get(r.nextInt(indexList.size()))];
    }

    public int[] steepestHillClimbing(int constraint){
        int nbDepl = 0;
        boolean stop = false;
        int[] temp = X;

        do {
            temp = meilleurVoisin(voisinsX(X), constraint);
            if (temp==null) return X;
            if (calculerf(temp)<calculerf(X)){
                X = temp;
            } else {
                stop = true;
            }
            nbDepl++;
        } while (nbDepl!=NB_DEPL && !stop);

        return X;
    }

    public int[] hillClimbingWithRestart(int constraint){
        int[] min = X;

        for (int i=0; i<NB_ESSAIS; i++){
            solutionAleatoire();
            if (calculerf(steepestHillClimbing(constraint))<calculerf(min)){
                min = X;
            }
        }

        X = min;

        return X;
    }

    private int[][] voisinsNonTabou(int[] vec, Queue<int[]> maQueue){
        int[][] voisins = voisinsX(vec);
        int[][] voisinsSansTabou;
        HashMap<Integer,int[]> mapSansTabou = new HashMap<>();

        int indST = 0;
        for (int i=0; i<vec.length; i++){
            if (!maQueue.contains(voisins[i])) {
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

    public int[] tabouClimbing(int constraint){
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
            if (listeTabou.size()>=MAX_TABOU) listeTabou.remove(listeTabou.peek());
            listeTabou.add(X);
            if (calculerf(temp)<calculerf(min)) min = temp;
            X = temp;
            nbDepl++;
        } while (nbDepl<NB_DEPL && !stop);

        X = min;
        return X;
    }

}
