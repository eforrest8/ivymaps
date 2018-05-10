package ivy.makery.maps;

import javafx.beans.property.SimpleStringProperty;

public class Class {
	private final SimpleStringProperty subject;
	private final SimpleStringProperty number;
	private final SimpleStringProperty section;
	private final SimpleStringProperty title;
	private final SimpleStringProperty days;
	private final SimpleStringProperty startTime;
	private final SimpleStringProperty endTime;
	private final String[] instructor;
	private final String[] rooms;
	
	Class(String subject, String number, String section, String title, String[] daysArray, String[] instructor, String[] blocks, String[] rooms) {
		this.subject = new SimpleStringProperty(subject);
		this.number = new SimpleStringProperty(number);
		this.section = new SimpleStringProperty(section);
		this.title = new SimpleStringProperty(title);
		this.instructor = instructor;
		this.startTime = new SimpleStringProperty(blocks[0]);
		this.endTime = new SimpleStringProperty(blocks[blocks.length - 1]);
		this.rooms = rooms;
		String daysTemp = "";
		for (String x: daysArray) {
			daysTemp += x + ", ";
		}
		daysTemp = daysTemp.substring(0, daysTemp.length() - 2);
		this.days = new SimpleStringProperty(daysTemp);
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

	public String getDays() {
		return days.get();//might need to format this
	}

	public String getStartTime() {
		int hourNumber;
		if (startTime.get().substring(0, 2).contains(":")) {
			hourNumber = Integer.parseInt(startTime.get().substring(0, 1));
		} else {
			hourNumber = Integer.parseInt(startTime.get().substring(0, 2));
		}
		String hourNumberString = "";
		String ampm = "am";
		if (hourNumber >= 13) {
			hourNumber -= 12;//TODO: fix time formatting
			ampm = "pm";
		}
		hourNumberString = Integer.toString(hourNumber);
		//if (hourNumberString.length() <= 1) {
		//	hourNumberString = "0" + hourNumberString;
		//}
		String formattedTime;
		if (startTime.get().substring(3).length() < 2) {
			formattedTime = hourNumberString + ":0" + startTime.get().substring(3) + ampm;
		} else {
			formattedTime = hourNumberString + ":" + startTime.get().substring(3) + ampm;
		}
		return formattedTime;
	}

	public String getEndTime() {
		int hourNumber = Integer.parseInt(endTime.get().substring(0, 1));
		if (hourNumber >= 13) {
			hourNumber -= 12;
		}
		String formattedTime = Integer.toString(hourNumber) + endTime.get().substring(2, 4);
		return formattedTime;
	}

	public String getInstructor() {
		String result = "";
		
		for (String x: instructor) {
			//attempt to prettify name
			if (x.contains(", ")) {
				String lastName = x.split(",\\s*")[0];
				String firstName = x.split(",\\s*")[1];
				x = firstName + " " + lastName;
			}
			result = result.concat(x + " ");
		}
		result = result.trim();
		return result;
	}

	public String[] getRooms() {
		return rooms;
	}
}
