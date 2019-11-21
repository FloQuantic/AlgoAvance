package algoavance;

import java.util.Random;

public class UBQP {

    private int[] X;
    private int[][] Q;

    public UBQP(int[][] Q){
        this.Q = Q;
        X = new int[Q[0].length];
        solutionAleatoire();
    }

    public void solutionAleatoire(){
        Random r = new Random();
        for (int i=0; i<X.length; i++){
            X[i] = r.nextInt(100);
        }
    }

    public int calculerf(){
        int f=0;
        for (int i=0; i<X.length; i++){
            for (int j=0; j<X.length; j++){
                f+=Q[i][j]*X[i]*X[j];
            }
        }
        return f;
    }


}
