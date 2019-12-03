package algoavance;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DonneesSolution {

    private int[] vecSolution;

    private double resultatFonction;

    private int nbDeplacement;

    private Queue<int[]> tabous = new ConcurrentLinkedQueue<>();

    public DonneesSolution(int[] vec, double res, int depl){
        vecSolution = vec;
        resultatFonction = res;
        nbDeplacement = depl;
    }


    public int[] getVecSolution() {
        return vecSolution;
    }

    public void setVecSolution(int[] vecSolution) {
        this.vecSolution = vecSolution;
    }

    public double getResultatFonction() {
        return resultatFonction;
    }

    public void setResultatFonction(double resultatFonction) {
        this.resultatFonction = resultatFonction;
    }

    public int getNbDeplacement() {
        return nbDeplacement;
    }

    public void setNbDeplacement(int nbDeplacement) {
        this.nbDeplacement = nbDeplacement;
    }

    public void afficherDonneesResultats(){
        System.out.println("Vecteur solution : " + MetaAbs.tabToString(vecSolution) + "\nResultat associe : " + resultatFonction + "\tNombre de deplacements : " + nbDeplacement + "\n");
    }

    public void setTabous(Queue<int[]> tabous) {
        this.tabous = tabous;
    }

    public Queue<int[]> getTabous() {
        return tabous;
    }
    public void afficherTabou() {
        MetaAbs.afficherListeTabou(tabous);
    }

}
