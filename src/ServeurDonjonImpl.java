import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/******************************************************************************
 * file     : src/ServeurDonjonImpl.java
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

public class ServeurDonjonImpl extends UnicastRemoteObject implements ServeurDonjon {

    private Donjon donjon;

    /**
     * Constructeur de la classe ServeurDonjonImpl.
     *
     * @param donjon Base de données Donjon pour le serveur.
     * @throws RemoteException Exception déclenchée si ServeurDonjonImpl ne crée pas l'objet.
     */
    ServeurDonjonImpl(Donjon donjon) throws RemoteException {
        super();
        this.donjon = donjon;
    }

    /**
     * Crée un personnage, l'enregistre dans le serveur et renvoie le personnage.
     *
     * @param nomPersonnage Nom du personnage.
     * @return Renvoie le nouveau personnage crée.
     */
    public synchronized Personnage seConnecter(String nomPersonnage) {
        Personnage personnage = new Personnage(nomPersonnage);
        this.donjon.ajouterEtreVivant(personnage);
        return personnage;
    }

    /**
     * Enregistre dans le serveur un personnage et renvoie le personnage.
     *
     * @param personnage Personnage.
     * @return Renvoie le nouveau personnage crée.
     */
    public synchronized Personnage seConnecter(Personnage personnage) {
        this.donjon.ajouterEtreVivant(personnage);
        return personnage;
    }

    /**
     * Déplace un personnage dans le donjon. Met à jour la pièce du joueur et renvoie le nouveau joueur.
     *
     * @param personnage Personnage qui se déplace.
     * @param direction  Direction vers lequel le personnage se déplace.
     * @return le personnage mis a jour
     */
    public Personnage seDeplacer(Personnage personnage, String direction) {
        Personnage personnageListe = (Personnage) this.donjon.recupereEtreVivant(personnage.getNom());
        Piece pieceSuivante = this.donjon.getPieceDepart();
        if (personnageListe.getPieceActuelle() == null) {
            personnageListe.setPieceActuelle(pieceSuivante);
        }
        switch (direction.toUpperCase()) {
            case "N":
                pieceSuivante = this.donjon.getPieceSuivante(personnageListe.getPieceActuelle(), "Nord");
                break;
            case "E":
                pieceSuivante = this.donjon.getPieceSuivante(personnageListe.getPieceActuelle(), "Est");
                break;
            case "S":
                pieceSuivante = this.donjon.getPieceSuivante(personnageListe.getPieceActuelle(), "Sud");
                break;
            case "O":
                pieceSuivante = this.donjon.getPieceSuivante(personnageListe.getPieceActuelle(), "Ouest");
                break;
            default:
                pieceSuivante = personnageListe.getPieceActuelle();
                break;
        }
        if (pieceSuivante == null) {
            try {
                personnageListe.getServeurNotification().notifier("Impossible d'aller dans cette direction.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (!direction.equals("")) {
                this.donjon.prevenirJoueurMemePiece(personnageListe, personnage.getNom() + " a quitté la pièce.");
            }
            personnageListe.setPieceActuelle(pieceSuivante);
        }
        return personnageListe;
    }

    /**
     * Préviens les autres joueurs de l'arrivé d'un personnage dans la pièce et déclenche
     * un combat avec un monstre si ce n'est pas la pièce de départ.
     *
     * @param personnage    Personnage qui se déplace.
     * @param direction     Direction vers lequel le personnage se déplace.
     * @param serveurCombat utilisé pour qu'un montre puisse attaquer le personnage
     */
    public void entrerNouvellePiece(Personnage personnage, String direction, ServeurCombat serveurCombat) {
        try {
            personnage.getServeurNotification().notifier("Vous arrivez dans la pièce : "
                    + personnage.getPieceActuelle());
            this.afficherEtreVivantPiece(personnage);
            this.donjon.prevenirJoueurMemePiece(personnage, personnage.getNom()
                    + " est entré dans la pièce: " + personnage.getPieceActuelle());
            if (!direction.equals("")) {
                serveurCombat.lancerCombatMonstre(personnage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Déconnecte un personnage du donjon. Il le supprime de la liste des personnage.
     *
     * @param personnage à déconnecter.
     */
    public void deconnecter(Personnage personnage) {
        try {
            this.donjon.prevenirJoueurMemePiece(personnage, personnage.getNom() + " est maintenant déconnecté.");
            this.donjon.supprimerEtreVivant(personnage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche tous les EtreVivant qui se trouvent dans la même pièce qu'un Personnage.
     *
     * @param personnage Personnage pour récupérer la pièce et pour le notifier.
     */
    public void afficherEtreVivantPiece(Personnage personnage) {
        String notification = "Êtres dans la pièce: ";
        for (EtreVivant etreVivantCourant : this.donjon.getEtreVivantMemePiece(personnage.getPieceActuelle())) {
            notification = notification.concat("[" + etreVivantCourant.getNom() + "|" + etreVivantCourant.getPointDeVieActuel() + "pdv] ");
        }
        try {
            personnage.getServeurNotification().notifier(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche tous les combats qui se trouvent dans la même pièce qu'un personnage.
     *
     * @param personnage pour récupérer la pièce et pour le notifier.
     */
    public void afficherCombatPiece(Personnage personnage) {
        String notification = "Combats dans la pièce: ";
        for (Combat combat : this.donjon.getCombatMemePiece(personnage.getPieceActuelle())) {
            notification = notification.concat("[" + combat.getEtreVivantAttaquant().getNom() + "|vs|" + combat.getEtreVivantAttaque().getNom() + "] ");
        }
        if (notification.equals("Combats dans la pièce: ")) {
            notification = "Il n'y a pas de combat dans la pièce.";
        }
        try {
            personnage.getServeurNotification().notifier(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Associe un serveur de notification à un personnage.
     *
     * @param personnage          auquel on associe un serveur notification.
     * @param serveurNotification qui sera associé au personnage.
     */
    public void enregistrerNotification(Personnage personnage, ServeurNotification serveurNotification) {
        this.donjon.associerServeurNotificationPersonnage(personnage, serveurNotification);
    }

    /**
     * Vérifie si un personnage est dans la liste de personnage du donjon.
     *
     * @param nomEtreVivant que l'on cherche dans la liste.
     * @return Renvoie la valeur true si le personnage existe, false sinon.
     */
    public boolean existeNomEtreVivant(String nomEtreVivant) {
        return this.donjon.recupereEtreVivant(nomEtreVivant) != null;
    }

    /**
     * Retourne un personnage en fonction du nom passé en parametre
     *
     * @param nomPersonnage nom du  personnage recherché
     * @return personnage si trouvé, null sinon
     */
    public Personnage getPersonnage(String nomPersonnage) {
        EtreVivant etreVivant = this.donjon.recupereEtreVivant(nomPersonnage);
            if (etreVivant.getClass().equals(Personnage.class)) {
            return (Personnage) etreVivant;
        }
        return null;
    }

    /**
     * Retourne un monstre en fonction du nom passé en parametre
     *
     * @param nomMonstre du monstre recherché
     * @return Monstre si trouvé sinon null
     */
    public Monstre getMonstre(String nomMonstre) {
        EtreVivant etreVivant = this.donjon.recupereEtreVivant(nomMonstre);
        if (etreVivant.getClass().equals(Monstre.class)) {
            return (Monstre) etreVivant;
        }
        return null;
    }

}
