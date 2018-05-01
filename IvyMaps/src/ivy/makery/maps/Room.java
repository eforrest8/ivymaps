package ivy.makery.maps;

public class Room {
	private final int x;
	private final int y;
	private final String roomNumber;
	
	Room(int x, int y, String roomNumber) {
		this.x = x;
		this.y = y;
		this.roomNumber = roomNumber;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getRoomNumber() {
		return roomNumber;
	}
}
