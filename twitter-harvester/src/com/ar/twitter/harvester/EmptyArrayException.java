package com.ar.twitter.harvester;

public class EmptyArrayException extends Exception {
	public EmptyArrayException(){
		super("You forgot to add user IDs as a parameter.");
	}
}
