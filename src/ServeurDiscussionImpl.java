import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/******************************************************************************
 * file     : src/ServeurDiscussionImpl.java
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
public class ServeurDiscussionImpl extends UnicastRemoteObject implements ServeurDiscussion {

    private Donjon donjon;

    /**
     * Constructeur de la classe ServeurDiscussionImpl.
     *
     * @param donjon Base de données Donjon pour le serveur.
     * @throws RemoteException Exception déclenchée si ServeurDiscussionImpl ne crée pas l'objet.
     */
    ServeurDiscussionImpl(Donjon donjon) throws RemoteException {
        super();
        this.donjon = donjon;
    }

    /**
     * Envoyer un message à un personnage disponible dans listePersonnage qui se trouve aussi dans la même pièce.
     *
     * @param personnage personnage qui envoie le message.
     * @param message    chaine de caractère à envoyer.
     */
    public void discuter(Personnage personnage, String message) {
        for (EtreVivant etreVivantCourant : this.donjon.getEtreVivantMemePiece(personnage.getPieceActuelle())) {
            try {
                if (etreVivantCourant instanceof Personnage) {
                    ((Personnage) etreVivantCourant).getServeurNotification()
                            .notifier(personnage.getNom() + ": " + message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
