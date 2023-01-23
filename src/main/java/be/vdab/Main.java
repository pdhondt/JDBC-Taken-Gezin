package be.vdab;

import be.vdab.domain.Gezin;
import be.vdab.repositories.PersoonRepository;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        /*var scanner = new Scanner(System.in);
        System.out.print("Voornaam papa: ");
        var papa = scanner.nextLine();
        System.out.print("Voornaam mama: ");
        var mama = scanner.nextLine();
        var gezin = new Gezin(papa, mama);
        System.out.print("Namen van de kinderen (STOP om te stoppen): ");
        *//*for (String kind; !"STOP".equals(kind = scanner.nextLine()); ) {
            gezin.addKind(kind);
        }*//*
        var kind = scanner.nextLine();
        while (!"STOP".equals(kind)) {
            gezin.addKind(kind);
            System.out.print("Namen van de kinderen (STOP om te stoppen): ");
            kind = scanner.nextLine();
        }
        var repository = new PersoonRepository();
        try {
            repository.create(gezin);
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }*/
        /*var repository = new PersoonRepository();
        try {
            System.out.println("Lijst van personen met het grootste vermogen:");
            repository.findGrootsteVermogen().forEach(System.out::println);
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }*/
        var repository = new PersoonRepository();
        try {
            System.out.println("Lijst van personen met papa en mama:");
            repository.findPersonenMetPapaEnMama().forEach(System.out::println);
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}