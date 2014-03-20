package net.nature.client.database;

import android.graphics.Rect;

public class Landmark {
	
	public Landmark(Rect rect, int id, String name){
		this.name = name;
		this.rect = rect;
		this.id = id;
		this.setDescription(description);
	}
	
	public Landmark() {
	}

	private int id;
	private String name;
	private Rect rect;
	private String description;
	private String[] targets;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Rect getRect() {
		return rect;
	}

	public void setRect(Rect rect) {
		this.rect = rect;
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

	public String[] getTargets() {
		return targets;
	}

	public void setTargets(String[] targets) {
		this.targets = targets;
	}
}
