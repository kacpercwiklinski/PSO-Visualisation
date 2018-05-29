package sample;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends PApplet {

    Boolean wIterations = true;  //------ Wybór wersji
    final int MAX_IT = 100;              //------ Maksymalna ilosc iteracji
    final int N_POP = 100;                //------ Ilosc czasteczek w populacji
    final int NUM_OF_POINTS = 10;
    final int MAX_HEIGHT = 1000;


    public Point point;
    public List<Point> points = new ArrayList<>();
    
    
    
    boolean temp = true;
    int i = 0;
    Population population;
    Utils utils = new Utils(false);
    Random r = new Random();


    public static void main(String[] args) {
        PApplet.main("sample.Main");
    }

    public void settings() {
        size(1366, 720);
//        fullScreen();
    }

    public void setup() {
        frameRate(30);
        background(51);

        for(int i = 0; i < NUM_OF_POINTS ; i++){
            Point p = new Point(this, ((int) random(128,width-128)), ((int) random(128,height-128)),random(MAX_HEIGHT));
            points.add(p);
        }
        
        initializeSwarm();
    }

    public void draw() {
        background(51);
        points.forEach(Point::show);
        population.drawPopulation();

        if (wIterations) {
            PSOwIterations();
        } else {
            PSO();
        }

        if (mousePressed) {
            if (wIterations) {
                initializeSwarm();
            }else{
                randomizeHeights();

            }
        }

        if(mousePressed && (mouseButton == RIGHT)){
            randomizePositions();
        }

    }

    private void randomizeHeights(){
        points.forEach(point1 -> {
            point1.setHeight(random(MAX_HEIGHT));
        });
    }

    private void randomizePositions(){
        points.forEach(point1 -> {
            point1.setPos(new PVector(random(128,width-128),random(128,height-128)));
        });
    }

    private void initializeSwarm() {
        population = new Population(this, N_POP, utils);
        utils.setGBest(population.getPopulation().get(r.nextInt(population.getPopulation().size())));
        utils.setGoalReached(false);
        i = 0;
        temp = true;
        randomizeHeights();
    }

    private void PSO() {
        population.updatePopulation();
        population.getPopulation().parallelStream().forEach(agent -> {
            agent.calculateFitness(points, population);
        });
    }

    private void PSOwIterations() {
        if (population != null && i < MAX_IT) {
            population.updatePopulation();
            if (utils.getGoalReached()) {
                System.out.println("Znaleziono rozwiazanie w : " + i + " iteracji.");
                utils.setGoalReached(true);
                i = MAX_IT + 1;
                temp = false;
            } else {
                population.getPopulation().forEach(agent -> {
                    agent.calculateFitness(points, population);
                });

            }
            i++;
        } else {
            if (temp)
                System.out.println("Nie znaleziono rozwiazania.");
            temp = false;
        }
    }


}
