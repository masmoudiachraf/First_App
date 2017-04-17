package com.example.masmo.first_app;

/**
 * Created by masmo on 09/04/2017.
 */

public class ClientsData {
    int id_cli;
    String name_cli;
    int numero_cli;
    int statut;
    int coiffeur;

    public ClientsData(int id_cli, String name_cli, int numero_cli, int statut, int coiffeur) {
        this.id_cli = id_cli;
        this.name_cli = name_cli;
        this.numero_cli = numero_cli;
        this.statut = statut;
        this.coiffeur = coiffeur;
    }


}
