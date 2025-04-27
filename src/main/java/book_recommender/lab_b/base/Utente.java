


/**
  * La classe Utente rappresenta un utente registrato nel sistema.
 * Contiene informazioni sull'utente e gestisce la registrazione e l'autenticazione.
 * 
 * @author Alessio     	Gervasini 		Mat. 756181
 * @author Francesco 	Orsini Pio		Mat. 756954
 * @author Luca      	Borin        	Mat. 756563

 
 */

package book_recommender.lab_b.base;
import java.util.HashMap;
import java.util.Map;


public class Utente {
    private String userid; // L'ID dell'utente
    private String password; // La password dell'utente
    private String nome; // Il nome dell'utente
    private String cognome; // Il cognome dell'utente
    private String codiceFiscale; // Il codice fiscale dell'utente
    private String email; // L'email dell'utente

    // Mappa statica per memorizzare gli utenti registrati
    private static Map<String, Utente> utentiRegistrati = new HashMap<>();

    /**
     * Costruttore per creare un'istanza di Utente.
     * 
     * @param userid l'ID dell'utente.
     * @param password la password dell'utente.
     * @param nome il nome dell'utente.
     * @param cognome il cognome dell'utente.
     * @param codiceFiscale il codice fiscale dell'utente.
     * @param email l'email dell'utente.
     */
    public Utente(String userid, String password, String nome, String cognome, String codiceFiscale, String email) {
        this.userid = userid;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.email = email;
    }

    /**
     * Registra un utente nel sistema.
     * 
     * @param utente l'utente da registrare.
     */
    public static void registraUtente(Utente utente) {
        utentiRegistrati.put(utente.userid, utente);
    }

    /**
     * Ottiene un utente registrato dal sistema.
     * 
     * @param userid l'ID dell'utente da cercare.
     * @return l'utente corrispondente all'ID fornito, o null se l'utente non esiste.
     */
    public static Utente getUtente(String userid) {
        return utentiRegistrati.get(userid);
    }

    /**
     * Autentica l'utente confrontando la password fornita con quella memorizzata.
     * 
     * @param password la password da verificare.
     * @return true se la password corrisponde, false altrimenti.
     */
    public boolean autentica(String password) {
        return this.password.equals(password);
    }

    /**
     * Ottiene l'ID dell'utente.
     * 
     * @return l'ID dell'utente.
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Ottiene il nome dell'utente.
     * 
     * @return il nome dell'utente.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Ottiene il cognome dell'utente.
     * 
     * @return il cognome dell'utente.
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Ottiene il codice fiscale dell'utente.
     * 
     * @return il codice fiscale dell'utente.
     */
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    /**
     * Ottiene l'email dell'utente.
     * 
     * @return l'email dell'utente.
     */
    public String getEmail() {
        return email;
    }
}