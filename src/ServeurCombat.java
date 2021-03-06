import java.rmi.Remote;
import java.rmi.RemoteException;

/******************************************************************************
 * file     : src/ServeurCombat.java
 * @author  : OLIVIER Thomas
 *            BOURAKADI Reda
 *            LAPEYRADE Sylvain
 * @version : 4.0
 * location : UPSSITECH - University Paul Sabatier
 * date     : 25 Mars 2019
 * licence  :              This work is licensed under a
 *              Creative Commons Attribution 4.0 International License.
 *                                    (CC BY)
 *****************************************************************************/
public interface ServeurCombat extends Remote {

    void lancerCombatMonstre(Personnage personnage) throws RemoteException;

    void lancerCombat(EtreVivant attaquant, EtreVivant attaque) throws RemoteException;

    Combat getCombatEtre(EtreVivant etreVivant) throws RemoteException;

    void fuirCombat(EtreVivant etreVivant) throws RemoteException;

    boolean estEnCombat(EtreVivant etreVivant) throws RemoteException;


}
