// Autor: 			Matthias Kugler
// Erstelldatum: 	17.10.2017
// 
// Funktion der Klasse:
// - Modelklasse für einen Telefonbucheintrag



public class PN_Entry {
	protected String name;
	protected int phoneNumber;
	
	public PN_Entry(String name, int phoneNumber) {
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	public String getName() {
		return name;
	}

	public int getPhoneNumber() {
		return phoneNumber;
	}
}
