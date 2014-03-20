package net.nature.client.database;

public class ParkActivity {
	
	public ParkActivity(int id, String name){
		this.name = name;
		this.id = id;
		this.setDescription(description);
	}
	
	public ParkActivity() {
	}

	private int id;
	private String name;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String toString(){
		return "(" + id + ") " + name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
