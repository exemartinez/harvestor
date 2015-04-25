package com.ar.twitter.harvester;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDAO {

	private static final String LOCALHOST = "localhost";
	private MongoDatabase database;
	private MongoClient mongoClient;

	/**
	 * Performs connection to the database - MongoDB
	 */
	public void connect(String databasename) {
		// To directly connect to a single MongoDB server

		mongoClient = new MongoClient(LOCALHOST, 27017);
		database = mongoClient.getDatabase(databasename);

	}

	public void insertDocuments(String collectionName, ArrayList<Document> documents) {
		MongoCollection<Document> collection = getCollection(collectionName);

		// Insert many documents in a given collection
		collection.insertMany(documents);
	}

	/**
	 * Gets a Mongo collection for further processing. (or simple querying)
	 * @param collectionName
	 * @return
	 */
	public MongoCollection<Document> getCollection(String collectionName) {
		// Getting a new collection
		MongoCollection<Document> collection = database.getCollection("mycoll");
		return collection;
	}

	/**
	 * Creates a document of the collection AlreadyFollowedUsers
	 * 
	 * @param userid
	 * @param username
	 * @param description
	 */
	public Document createUserAlreadyFollowedUsersDocument(Long userid,
			String username, String description) {
		// create the document
		return (new Document("twitter_id", userid).append("twitter_username",
				username).append("twitter_description", description));

	}

	/**
	 * Closes all MongoDB connections
	 */
	public void closeConnections() {
		// Closing the connection
		mongoClient.close();
	}

}
