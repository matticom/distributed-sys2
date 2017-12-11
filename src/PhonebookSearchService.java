// Autor: 			Matthias Kugler
// Erstelldatum: 	17.10.2017
// 
// Funktion der Klasse:
// - Suchservice nach Namen und Telefonnummern
// - dekodiert Query string
// - erstellt zwei autonome Suche Threads
// - gibt eine Liste mit Ergebnissen zurück
// - gibt Feedback mit Erfolgsstatus der Suchen



import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhonebookSearchService implements SearchService {

	protected String name;
	protected int phoneNumber;
	protected Repository repo;

	protected boolean nameSearchNeeded;
	protected boolean phoneNumberSearchNeeded;

	protected Thread nameSearchThread;
	protected Thread phoneNumberSearchThread;

	protected List<PN_Entry> foundNameEntries;
	protected List<PN_Entry> foundPhoneNumberEntries;
	
	protected Map<String, Boolean> emptinessFeedback;
	protected final String NAME_MAP_KEY = "NAME";
	protected final String PHONENUMBER_MAP_KEY = "PHONE";

	public PhonebookSearchService(Repository repo) {
		this.repo = repo;
		this.foundNameEntries = new ArrayList<PN_Entry>();
		this.foundPhoneNumberEntries = new ArrayList<PN_Entry>();
	}

	// zweite Methode aus dem searchService Interface
	// Feedback Map, der Erfolgsstatus beinhaltet
	@Override
	public Map<String, Boolean> getFeedBack() {
		return emptinessFeedback;
	}
	
	// erste Methode aus dem searchService Interface
	@Override
	public List<PN_Entry> searchWithParams(String params) {
		initializeFeedbackMap();
		parseParams(params);
		analyzeTasks();
		startThreads();
		List<PN_Entry> allfoundEntries = mergeResults();
		clearResources();
		return allfoundEntries;
	}
	
	// Feedback Map, der Erfolgsstatus beinhaltet, wird initialisiert
	protected void initializeFeedbackMap() {
		emptinessFeedback = new HashMap<String, Boolean>();
		emptinessFeedback.put(NAME_MAP_KEY, true);
		emptinessFeedback.put(PHONENUMBER_MAP_KEY, true);
	}
	
	// aus query string werden die Suchparameter extrahiert
	protected void parseParams(String params) {
		int startIdxName = params.indexOf("=") + 1;
		int endIdxName = params.indexOf("&");
		int startIdxPhoneNumber = params.indexOf("=", startIdxName) + 1;
		int endIdxPhoneNumber = params.length();
		try {
			name = URLDecoder.decode(params.substring(startIdxName, endIdxName).trim(), "UTF-8");
			phoneNumber = Integer.parseInt(params.substring(startIdxPhoneNumber, endIdxPhoneNumber).trim());
		} catch (NumberFormatException e) {
			phoneNumber = 0;
			System.out.println("Keine Zahl!");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	protected void analyzeTasks() {
		// entsprechend der Suchparameter werden Flags gesetzt
		nameSearchNeeded = !name.isEmpty();
		phoneNumberSearchNeeded = (phoneNumber > 0);
	}
	
	protected void startThreads() {
		try {
			if (nameSearchNeeded && phoneNumberSearchNeeded) {
				nameSearchThread = new Thread(() -> searchNames(name));
				phoneNumberSearchThread = new Thread(() -> searchPhoneNumbers(phoneNumber));
				nameSearchThread.start();
				phoneNumberSearchThread.start();
				nameSearchThread.join();
				phoneNumberSearchThread.join();
			} else {
				if (nameSearchNeeded) {
					nameSearchThread = new Thread(() -> searchNames(name));
					nameSearchThread.start();
					nameSearchThread.join();
				}
				if (phoneNumberSearchNeeded) {
					phoneNumberSearchThread = new Thread(() -> searchPhoneNumbers(phoneNumber));
					phoneNumberSearchThread.start();
					phoneNumberSearchThread.join();
				}
			}
		} catch (InterruptedException e) {
			System.out.println("Thread wurde abgebrochen: " + e.getMessage());
		}
	}

	// Namens-Thread Methode:
	// durchsucht nur die Namen des Telefonbuchs
	// wenn eine Übereinstimmung gefunden wird, wird sie eine List geschrieben
	// am Ende wird ein Feedback über den Erfolg in den Map geschrieben
	protected void searchNames(String name) {
		List<PN_Entry> foundEntry = new ArrayList<PN_Entry>();
		for (PN_Entry entry : repo.getPhoneBookList()) {
			if (entry.getName().equalsIgnoreCase(name)) {
				foundEntry.add(entry);
			}
		}
		foundNameEntries = foundEntry;
		if (!foundNameEntries.isEmpty()) {
			emptinessFeedback.put(NAME_MAP_KEY, false);
		}
	}

	// Telefonnummer-Thread Methode:
	// durchsucht nur die Telefonnummern des Telefonbuchs
	// wenn eine Übereinstimmung gefunden wird, wird sie eine List geschrieben
	// am Ende wird ein Feedback über den Erfolg in den Map geschrieben
	protected void searchPhoneNumbers(int phoneNumber) {
		List<PN_Entry> foundEntry = new ArrayList<PN_Entry>();
		for (PN_Entry entry : repo.getPhoneBookList()) {
			if (entry.getPhoneNumber() == phoneNumber) {
				foundEntry.add(entry);
			}
		}
		foundPhoneNumberEntries = foundEntry;
		if (!foundPhoneNumberEntries.isEmpty()) {
			emptinessFeedback.put(PHONENUMBER_MAP_KEY, false);
		}
	}
	
	// die getrennten Liste von Namen/Telefonnummer Thread werden
	// zusammengeführt in eine Liste
	protected List<PN_Entry> mergeResults() {
		List<PN_Entry> allfoundEntries = new ArrayList<PN_Entry>();
		if (!foundNameEntries.isEmpty()) {
			allfoundEntries.addAll(foundNameEntries);
			if (!foundPhoneNumberEntries.isEmpty()) {
				allfoundEntries.addAll(foundPhoneNumberEntries);
			}
		} else {
			if (!foundPhoneNumberEntries.isEmpty()) {
				allfoundEntries.addAll(foundPhoneNumberEntries);
			}
		}
		return allfoundEntries;
	}

	// Aufräumarbeiten bevor der SearchService wieder einsatzbereit ist
	protected void clearResources() {
		foundNameEntries.clear();
		foundPhoneNumberEntries.clear();
		nameSearchThread = null;
		phoneNumberSearchThread = null;
	}
}
