package algoavance;

import java.util.ArrayList;
import java.util.Random;

public class UBQP {

    private final static int NB_DEPL = 100;
    private final static int NB_ESSAIS = 100;
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

    private int[][] voisinsX(){
        int[][] voisinsX = new int[X.length][X.length];
        for (int i=0; i<X.length; i++){
            for (int j=0; j<X.length; j++){
                if (i==j) {
                    voisinsX[i][j] = (X[j] + 1) % 2;
                } else {
                    voisinsX[i][j] = X[j];
                }
            }
        }
        return voisinsX;
    }

    private int[] meilleurVoisin(){
        int[][] voisins = voisinsX();
        int[] fVoisins=  new int[X.length];
        for (int i=0; i<X.length; i++){
            fVoisins[i]=calculerf(voisins[i]);
        }

        ArrayList<Integer> indexList = new ArrayList<>();
        int min = fVoisins[0];
        indexList.add(0);
        for (int i=1; i<X.length; i++){
            if (fVoisins[i]<min){
                min = fVoisins[i];
                indexList.clear();
                indexList.add(i);
            } else if (fVoisins[i]==min){
                indexList.add(i);
            }
        }

        Random r = new Random();
        return voisins[indexList.get(r.nextInt(indexList.size()))];
    }

    private int[] steepestHillClimbing(){
        int nbDepl = 0;
        boolean stop = false;
        int[] temp = X;

        do {
            temp = meilleurVoisin();
            if (calculerf(temp)<calculerf(X)){
                X = temp;
            } else {
                stop = true;
            }
            nbDepl++;
        } while (nbDepl!=NB_DEPL && !stop);

        return X;
    }

    public int[] hillClimbingWithRestart(){
        int[] min = X;

        for (int i=0; i<NB_ESSAIS; i++){
            solutionAleatoire();
            if (calculerf(steepestHillClimbing())<calculerf(min)){
                min = X;
            }
        }

        X = min;

        return X;
    }


}
