package com.example.masmo.first_app;

/**
 * Created by masmo on 01/05/2017.
 */

public class DataClient {
    int id;
    String name;
    String numTelClient;
    int statut;
    int idcoiffeur;

    public DataClient(int id, String name, String numTelClient, int statut, int idcoiffeur) {
        this.id = id;
        this.name = name;
        this.numTelClient = numTelClient;
        this.statut = statut;
        this.idcoiffeur = idcoiffeur;
    }
}
