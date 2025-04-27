/**
 * Classe per la gestione della registrazione e del login degli utenti.
 * 
 * @author Alessio     	Gervasini 		Mat. 756181
 * @author Francesco 	Orsini Pio		Mat. 756954
 * @author Luca      	Borin        	Mat. 756563

 
 */
package book_recommender.lab_b.base;
import book_recommender.lab_b.nonservira.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Pattern;




public class RegistrazioneUtente {
    public static final String RESET = "\033[0m";  // Resetta il colore
    public static final String ROSSO = "\033[0;31m";
    public static final String VERDE = "\033[0;32m";
    public static final String BLU = "\033[0;34m";


    public static final String V = "✓"; // Carattere  per il segno di spunta
    public static final String X = "x ";

    public String userID; // L'ID dell'utente

    /**
     * Metodo per registrare un nuovo utente.
     * Richiede l'inserimento di dati personali e li salva in un file CSV.
     */
    public static void registrazione() {
                       try {
            // Imposta la codifica dell'output della console su UTF-8
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        @SuppressWarnings("resource")
        Scanner input = new Scanner(System.in);

        System.out.println("\n"+VERDE+V+" Benvenuto! Per favore, inserisci i seguenti dati:"+RESET);

        String nomeCognome;
        do {
            System.out.print("\nNome e Cognome: ");
            nomeCognome = input.nextLine().trim();
            if (nomeCognome.isEmpty()) {
                System.out.println("\n"+ROSSO+X+"Errore: Il nome e cognome non possono essere vuoti."+RESET);
            }
        } while (nomeCognome.isEmpty());

        String userID;
        do {
            System.out.print("\nUserID (massimo 8 caratteri): ");
            userID = input.nextLine().trim();
            if (userID.isEmpty()) {
                System.out.println("\n"+ROSSO+X+"Errore: L'UserID non può essere vuoto."+RESET);
            } else if (!validaUserID(userID)) {
                System.out.println("\n"+ROSSO+X+"Errore: l'UserID deve essere al massimo di 8 caratteri."+RESET);
            }
        } while (userID.isEmpty() || !validaUserID(userID));

        try {
            creaFileSeNonEsiste(Main.reg);

            if (isUserIDExists(userID)) {
                System.out.println("\n"+ROSSO+X+"UserID già esistente. Avvio login..."+RESET);
                if (login(userID)) {
                    System.out.println("\n"+VERDE+V+"Login effettuato con successo!"+RESET);
                    Main.menuUtenteRegistrato(userID);
                    return; // Termina la registrazione se l'utente esiste già e ha fatto login
                } else {
                    System.out.println("\n"+ROSSO+X+"Login fallito. Riprova la registrazione."+RESET);
                    Main.menu();
                    return; // Termina la registrazione se il login fallisce
                }
            }
        } catch (IOException e) {
            Main.menu();
            return; // Termina la registrazione se c'è un errore durante la verifica dell'UserID
        }

        String codiceFiscale;
        do {
            System.out.print("\nCodice Fiscale (XXXXXX00X00X000X): ");
            codiceFiscale = input.nextLine().trim().toUpperCase();
            if (!validaCodiceFiscale(codiceFiscale)) {
                System.out.println("\n"+ROSSO+X+"Errore: il codice fiscale deve seguire il formato XXXXXX00X00X000X, riprova."+RESET);
            }
        } while (!validaCodiceFiscale(codiceFiscale));

        String email;
        do {
            System.out.print("\nIndirizzo di Posta Elettronica: ");
            email = input.nextLine().trim().toLowerCase();
            if (!validaEmail(email)) {
                System.out.println("\n"+ROSSO+X+"Errore: formato email non valido."+RESET);
            }
        } while (!validaEmail(email));

        String password;
        do {
            System.out.print("\nPassword (minimo 8 caratteri, almeno una maiuscola, una minuscola, un numero e un carattere speciale): ");
            password = input.nextLine().trim();
            if (!validaPassword(password)) {
                System.out.println("\n"+ROSSO+X+"Errore: la password deve contenere almeno 8 caratteri, includere almeno una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale." +RESET);
            }
        } while (!validaPassword(password));

        // Creazione della stringa da scrivere sul file
        String datiUtente = nomeCognome + "," + codiceFiscale + "," + email + "," + userID + "," + password;

        // Salvataggio dei dati su file
        salvaSuFile(datiUtente);

        System.out.print("\n"+VERDE+V+"Registrazione completata con successo!\n"+RESET);
    }

    /**
     * Valida il formato del codice fiscale.
     *
     * @param codiceFiscale Il codice fiscale da validare.
     * @return true se il codice fiscale è valido, false altrimenti.
     */
    public static boolean validaCodiceFiscale(String codiceFiscale) {
        // Regex per validare il formato del codice fiscale
        String cfRegex = "^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$";
        return Pattern.matches(cfRegex, codiceFiscale);
    }

    /**
     * Valida il formato dell'email.
     *
     * @param email L'email da validare.
     * @return true se l'email è valida, false altrimenti.
     */
    public static boolean validaEmail(String email) {
        // Semplice regex per validare l'email
        String emailRegex = "^[\\w-\\.]+@[\\w-]+\\.(it|com)$";
        return Pattern.matches(emailRegex, email);
    }

    /**
     * Verifica se un userID esiste già nel file.
     *
     * @param userID L'userID da verificare.
     * @return true se l'userID esiste, false altrimenti.
     * @throws IOException se si verifica un errore durante la lettura del file.
     */
    public static boolean isUserIDExists(String userID) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(Main.reg))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length > 3 && userData[3].equals(userID)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Valida l'userID.
     *
     * @param userID L'userID da validare.
     * @return true se l'userID è valido, false altrimenti.
     */
    public static boolean validaUserID(String userID) {
        return userID.length() <= 8;
    }

    /**
     * Valida il formato della password.
     *
     * @param password La password da validare.
     * @return true se la password è valida, false altrimenti.
     */
    public static boolean validaPassword(String password) {
        // Regex per validare la password (minimo 8 caratteri, almeno una maiuscola, una minuscola, un numero e un carattere speciale)
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return Pattern.matches(passwordRegex, password);
    }

    /**
     * Salva i dati dell'utente nel file.
     *
     * @param dati La stringa dei dati da salvare.
     */
    public static void salvaSuFile(String dati) {
        String filename = Main.reg;

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)))) {
            writer.println(dati);
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio dei dati su file: " + e.getMessage());
        }
    }

    /**
     * Effettua il login di un utente.
     *
     * @param existingUserID L'userID dell'utente che vuole effettuare il login.
     * @return true se il login ha successo, false altrimenti.
     */
    public static boolean login(String existingUserID) {
        try {
            // Imposta la codifica dell'output della console su UTF-8
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        @SuppressWarnings("resource")
        Scanner input = new Scanner(System.in);
        String password;

        while (true) {
            System.out.print("\nPassword (o digita 'back' per tornare al menu principale): ");
            password = input.nextLine().trim();

            if (password.equalsIgnoreCase("back")) {
                return false; // L'utente ha scelto di tornare al menu principale
            }

            if (controllaUtente(existingUserID, password)) {
                return true; // Login riuscito
            } else {
                System.out.println("\n"+ROSSO+X+"ID o password errata. Riprova."+RESET);
            }
        }
    }

    /**
     * Controlla se l'userID e la password corrispondono a un utente registrato.
     *
     * @param userID   L'userID dell'utente.
     * @param password La password dell'utente.
     * @return true se le credenziali sono valide, false altrimenti.
     */
    public static boolean controllaUtente(String userID, String password) {
        String filePath = Main.reg;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String dbentry;
            while ((dbentry = reader.readLine()) != null) {
                String[] userData = dbentry.split(",");
                if (userData.length > 4 && userData[3].equals(userID) && userData[4].equals(password)) {
                    return true;
                }
            }
        } catch(IOException e) {
            // Gestione dell’eccezione
            }
            return false;
        }
        
        /**
         * Crea un file se non esiste.
         *
         * @param filename Il nome del file da creare.
         * @throws IOException se si verifica un errore durante la creazione del file.
         */
        public static void creaFileSeNonEsiste(String filename) throws IOException {
            File file = new File(filename);
            if (!file.exists()) {
                try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
                    writer.println("Nome e Cognome,Codice Fiscale,Email,UserID,Password");
                } catch (IOException e) {
                    System.err.println("Errore durante la creazione del file: " + e.getMessage());
                    throw e;
                }
            }
        }

        public static void ricercalibrologin2(String userid, Libreria libreria) {
            // Controlla se il file CSV dei libri esiste, altrimenti lo genera
            if (!new File(Main.LIBRI_FILE_PATH).exists()) {
                Libricsv.generaFileLibri();
            }
        
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
        
            while (true) {
                System.out.println(Main.VIOLA + "\n\n* Menu di ricerca libri:\n" + RESET);
                System.out.println("1. Cerca per titolo");
                System.out.println("2. Cerca per autore");
                System.out.println("3. Cerca per autore e anno");
                System.out.println(ROSSO + "4. Esci e salva la libreria" + RESET);
                System.out.print("\nInserisci la tua scelta: ");
        
                int scelta = scanner.nextInt();
                scanner.nextLine(); // Consuma la newline
        
                switch (scelta) {
                    case 1:
                        while (true) {
                            System.out.print("\nInserisci il titolo da cercare (digita 'back' per tornare al menu ricerca): ");
                            String titolo = scanner.nextLine();
                            if (titolo.equalsIgnoreCase("back")) {
                                break;
                            } else if (titolo.isEmpty()) {
                                System.out.println(ROSSO + X + "Il titolo non può essere vuoto." + RESET);
                                continue;
                            } else if (Libreria.libroEsisteInDataCsv(titolo)) {
                                System.out.println(ROSSO + X + "Il libro non esiste in biblioteca." + RESET);
                                continue;
                            }
        
                            String[] datiLibro = Libreria.recuperaDatiLibro(titolo);
                            if (datiLibro != null) {
                                if (libreria.aggiungiLibro(datiLibro[0])) {
                                    System.out.println(VERDE + V + "Libro aggiunto con successo alla libreria!" + RESET);
                                    break; // Torna al menu di ricerca libri
                                } else {
                                    System.out.println(ROSSO + X + "Il libro è già presente nella libreria." + RESET);
                                }
                            } else {
                                System.out.println(ROSSO + X + "Libro non trovato." + RESET);
                            }
                        }
                        break;
        
                    case 2:
                        while (true) {
                            System.out.print("\nInserisci l'autore da cercare (digita 'back' per tornare al menu ricerca): ");
                            String autore = scanner.nextLine();
                            if (autore.equalsIgnoreCase("back")) {
                                break;
                            } else if (autore.isEmpty()) {
                                System.out.println(ROSSO + X + "L'autore non può essere vuoto." + RESET);
                                continue;
                            } else if (!Libreria.esisteAutore(autore)) {
                                System.out.println(ROSSO + X + "Non esiste nessun autore con questo nome in biblioteca." + RESET);
                                continue;
                            }
        
                            String[] datiLibro = Libreria.cercaLibroPerAutore(autore);
                            if (datiLibro != null) {
                                if (libreria.aggiungiLibro(datiLibro[0])) {
                                    System.out.println(VERDE + V + "Libro aggiunto con successo alla libreria!" + RESET);
                                    break; // Torna al menu di ricerca libri
                                } else {
                                    System.out.println(ROSSO + X + "Il libro è già presente nella libreria." + RESET);
                                }
                            } else {
                                System.out.println(ROSSO + X + "Libro non trovato." + RESET);
                            }
                        }
                        break;
        
                    case 3:
                        System.out.print("\nInserisci l'autore: ");
                        String autoreAnno = scanner.nextLine();
                        System.out.print("Inserisci l'anno di pubblicazione: ");
                        int anno = scanner.nextInt();
                        scanner.nextLine(); // Consuma la newline
        
                        if (!Libreria.esisteAnnoPerAutore(autoreAnno, anno)) {
                            System.out.println(ROSSO + X + "Non esiste nessun libro per questo autore e anno." + RESET);
                            break;
                        }
        
                        String[] datiLibro = Libreria.recuperaDatiLibroPerAutoreEAnno(autoreAnno, anno);
                        if (datiLibro != null) {
                            if (libreria.aggiungiLibro(datiLibro[0])) {
                                System.out.println(VERDE + V + "Libro aggiunto con successo alla libreria!" + RESET);
                                break; // Torna al menu di ricerca libri
                            } else {
                                System.out.println(ROSSO + X + "Il libro è già presente nella libreria." + RESET);
                            }
                        } else {
                            System.out.println(ROSSO + X + "Libro non trovato." + RESET);
                        }
                        break;
        
                    case 4:
                        Libreria.registraLibreria(userid, libreria);
                        return;
        
                    default:
                        System.out.println(ROSSO + X + "Scelta non valida. Riprova." + RESET);
                }
            }
        }

    }