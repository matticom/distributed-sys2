// Autor: 			Matthias Kugler
// Erstelldatum: 	17.10.2017
// 
// Funktion der Klasse:
// - validiert die Benutzereingaben in die Textfelder Name/Telefonnummer
// - checkt, ob nur whitespace oder gar nichts oder etwas gültiges eingegeben wurde



import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControlsValidator {
	
	protected FieldsStatus nameStatus;
	protected FieldsStatus phoneNumberStatus;
	protected String name;
	protected String phoneNumber;
	protected String feedbackText;
	protected boolean isFeedbackVisible;
	protected boolean isReturn;

	public void checkFields(String name, String phoneNumber){
		feedbackText = "";
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.nameStatus = checkForWhitespaces(name, "nameControl");
		this.phoneNumberStatus = checkForWhitespaces(phoneNumber, "phoneNumberControl");
		setFeedbackParam();
	}
	
	protected FieldsStatus checkForWhitespaces(String text, String control){
		Pattern pattern = Pattern.compile("\\s+");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find() && matcher.group().length() == text.length()){
			if (control.equals("nameControl")) {
				return FieldsStatus.NameSpaces;
			} else {
				return FieldsStatus.PhoneSpaces;
			}
		} else if (text.isEmpty()){
			if (control.equals("nameControl")) {
				return FieldsStatus.NameBlank;
			} else {
				return FieldsStatus.PhoneBlank;
			}
		} else {
			if (control.equals("nameControl")) {
				return FieldsStatus.NameOK;
			} else {
				return FieldsStatus.PhoneOK;
			}
		}
	}
	
	protected void setFeedbackParam(){
		
		switch (nameStatus) {
		
		case NameSpaces:
			
			switch (phoneNumberStatus) {
			
			case PhoneSpaces:
				feedbackText = "Leerzeichen in beiden Feldern!";
				isFeedbackVisible = true;
				isReturn = true;
				break;
			case PhoneBlank:
				feedbackText = "Leerzeichen im Namensfeld!";
				isFeedbackVisible = true;
				isReturn = true;
				break;
			case PhoneOK:
				feedbackText = "Leerzeichen im Namensfeld!";
				isFeedbackVisible = true;
				isReturn = false;
				break;
				
			}
			break;
			
		case NameBlank:
			
			switch (phoneNumberStatus) {

			case PhoneSpaces:
				feedbackText = "Leerzeichen im Telefonnummerfeld!";
				isFeedbackVisible = true;
				isReturn = true;
				break;
			case PhoneBlank:
				feedbackText = "Die Felder dürfen nicht leer sein!";
				isFeedbackVisible = true;
				isReturn = true;
				break;
			case PhoneOK:
				isFeedbackVisible = false;
				isReturn = false;
				break;
			}
			break;
			
		case NameOK:	
			
			switch (phoneNumberStatus) {

			case PhoneSpaces:
				feedbackText = "Leerzeichen im Telefonnummerfeld!";
				isFeedbackVisible = true;
				isReturn = false;
				break;
			case PhoneBlank:
				isFeedbackVisible = false;
				isReturn = false;
				break;
			case PhoneOK:
				isFeedbackVisible = false;
				isReturn = false;
				break;
				
			}
			break;
		}
	}

	public String getFeedbackText() {
		return feedbackText;
	}

	public boolean isFeedbackVisible() {
		return isFeedbackVisible;
	}

	public boolean inputIsInvalid() {
		return isReturn;
	}

	public FieldsStatus getNameStatus() {
		return nameStatus;
	}

	public FieldsStatus getPhoneNumberStatus() {
		return phoneNumberStatus;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
}
