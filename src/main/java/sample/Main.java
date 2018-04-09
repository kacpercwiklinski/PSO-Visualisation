package sample;

import processing.core.PApplet;

import java.util.Random;

public class Main extends PApplet {

    Boolean wIterations = true;  //------ Wybór wersji
    int maxIt = 100;              //------ Maksymalna ilosc iteracji
    int nPop = 20;                //------ Ilosc czasteczek w populacji


    public Point point;
    boolean temp = true;
    int i = 0;
    Population population;
    Utils utils = new Utils(false);
    Random r = new Random();


    public static void main(String[] args) {
        PApplet.main("sample.Main");
    }

    public void settings() {
        size(1024, 600);
//        fullScreen();
    }

    public void setup() {
        background(51);
        point = new Point(this, width/2, height/2);
        initializeSwarm();
    }

    public void draw() {
        background(51);
        point.show();
        population.drawPopulation();

        if (wIterations) {
            PSOwIterations();
        } else {
            PSO();
        }

        if (mousePressed) {
            if (wIterations) {
                initializeSwarm();
            }
            point = new Point(this, mouseX, mouseY);
        }

    }

    private void initializeSwarm() {
        population = new Population(this, nPop, utils);
        utils.setGBest(population.getPopulation().get(r.nextInt(population.getPopulation().size())));
        utils.setGoalReached(false);
        i = 0;
        temp = true;
    }

    private void PSO() {
        population.updatePopulation();
        population.getPopulation().parallelStream().forEach(agent -> {
            agent.calculateFitness(point, population);
        });
    }

    private void PSOwIterations() {
        if (population != null && i < maxIt) {
            population.updatePopulation();
            if (utils.getGoalReached()) {
                System.out.println("Znaleziono rozwiazanie w : " + i + " iteracji.");
                utils.setGoalReached(true);
//                population.getPopulation().forEach(agent -> {
//                    agent.getAcc().mult(0);
//                });
                i = maxIt + 1;
                temp = false;
            } else {
                population.getPopulation().forEach(agent -> {
                    agent.calculateFitness(point, population);
                });
            }
            i++;
        } else {
            if (temp == true)
                System.out.println("Nie znaleziono rozwiazania.");
            temp = false;
        }
    }


}
