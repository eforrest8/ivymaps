package ivy.makery.maps;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

import javafx.beans.property.SimpleStringProperty;

public class Advisor {
	private final SimpleStringProperty name;
	private final Boolean in;
	private final String room;
	private final String[] blocks;
	private final String[] days;
	
	Advisor(String name, String[] daysArray, String[] blocks, String room) {
		this.name = new SimpleStringProperty(name);
		this.blocks = blocks;
		this.room = room;
		this.days = daysArray;
		this.in = defineIn();
	}
	
	private Boolean defineIn() {
		//do stuff
		Boolean result = false;
		String today = DayOfWeek.from(LocalDate.now()).name();
		int hour = LocalTime.now().getHour();
		int minute = LocalTime.now().getMinute();
		int blocksIteration = 0;
		for (String x: days) {
			if (x.equalsIgnoreCase(today)) {
				continue;
			} else {
				blocksIteration++;
			}
		}
		for (String x: blocks) {
			int xhour = Integer.parseInt(x.substring(0, 1));
			int xminute = Integer.parseInt(x.substring(3));
			int oldhour = 0;
			if (blocksIteration == 0) {
				//we are on the right day
				if (xhour == hour) {
					if ((xminute - minute) < 0) {
						result = true;
						continue;
					}
				}
			} else {
				//we are not on the right day
				if (oldhour > xhour) {
					blocksIteration--;
				}
				oldhour = xhour;
			}
		}
		return result;
	}
	
	public String getName() {
		return name.get();
	}

	public String getIn() {
		String result;
		if (in) {
			result = "Yes";
		} else {
			result = "No";
		}
		return result;
	}

	public String getRoom() {
		return room;
	}
}
