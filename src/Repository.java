// Autor: 			Matthias Kugler
// Erstelldatum: 	17.10.2017
// 
// Funktion der Klasse:
// - stellt eine Liste mit Telefonbucheinträgen zur Verfügung



import java.util.ArrayList;
import java.util.List;

public class Repository {

	List<PN_Entry> phoneBookList;
	
	public Repository() {
		phoneBookList = new ArrayList<PN_Entry>();
		phoneBookList.add(new PN_Entry("Meier", 3333));
		phoneBookList.add(new PN_Entry("Walter", 3333));
		phoneBookList.add(new PN_Entry("Walter", 5555));
		phoneBookList.add(new PN_Entry("Ernst", 4444));
		phoneBookList.add(new PN_Entry("von Witt", 3333));
		phoneBookList.add(new PN_Entry("Cäsar", 9999));
		phoneBookList.add(new PN_Entry("Klein", 3333));
		phoneBookList.add(new PN_Entry("Zanan", 1111));
		phoneBookList.add(new PN_Entry("Cäsar", 4444));
		phoneBookList.add(new PN_Entry("Hüpp", 3333));
	}

	public List<PN_Entry> getPhoneBookList() {
		return phoneBookList;
	}
}
