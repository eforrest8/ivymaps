package ivy.makery.maps;

import javafx.beans.property.SimpleStringProperty;

public class Class { //TODO: add remaining class properties
	private final SimpleStringProperty subject;
	private final SimpleStringProperty number;
	private final SimpleStringProperty section;
	private final SimpleStringProperty title;
	private final SimpleStringProperty startTime;
	private final SimpleStringProperty instructor;
	
	Class(String subject, String number, String section, String title, String startTime, String instructor) {
		this.subject = new SimpleStringProperty(subject);
		this.number = new SimpleStringProperty(number);
		this.section = new SimpleStringProperty(section);
		this.title = new SimpleStringProperty(title);
		this.startTime = new SimpleStringProperty(startTime);
		this.instructor = new SimpleStringProperty(instructor);
	}

	public String getSubject() {
		return subject.get();
	}

	public String getNumber() {
		return number.get();
	}

	public String getSection() {
		return section.get();
	}

	public String getTitle() {
		return title.get();
	}

	public String getStartTime() {
		return startTime.get(); //TODO: Make this report nicer times
	}

	public String getInstructor() {
		return instructor.get();
	}
}
