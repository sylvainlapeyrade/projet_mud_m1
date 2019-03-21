import java.rmi.Remote;
import java.rmi.RemoteException;

/******************************************************************************
 * file     : src/ServeurPersistance.java
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
public interface ServeurPersistance extends Remote {

    void sauvegarderPersonnage(Personnage personnage) throws RemoteException;

    Personnage recuperePersonnage(String nomPersonnage) throws RemoteException;

    void supprimerPersonnage(String nomPersonnage) throws RemoteException;

}
