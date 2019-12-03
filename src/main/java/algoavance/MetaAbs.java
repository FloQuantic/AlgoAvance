package algoavance;

import java.util.Queue;

public abstract class MetaAbs {

    static final int NB_DEPL = 1000;
    static final int NB_ESSAIS = 10;
    static final int MAX_TABOU = 1000;

    public static String tabToString(int[] tab){
        String toReturn = "[\t";
        for (int i=0; i<tab.length; i++){
            toReturn+= tab[i] + "\t";
        }
        toReturn += "]";
        return toReturn;
    }

    static void afficherListeTabou(Queue<int[]> listeTabou){
        String toPrint = "Liste tabous : \n";
        for (int[] tabou: listeTabou) {
            toPrint += tabToString(tabou) + "\n";
        }
        System.out.println(toPrint);
    }

    boolean containsElem(Queue<int[]> maQueue, int[] voisin) {
        boolean stop;
        int i;
        for (int[] vec : maQueue) {
            i = 0;
            stop = false;
            while (i<vec.length && !stop){
                if (vec[i]!=voisin[i]) stop = true;
                i++;
            }
            if (!stop) return true;
        }
        return false;
    }


}
