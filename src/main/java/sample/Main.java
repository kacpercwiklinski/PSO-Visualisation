package sample;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends PApplet {

    Boolean wIterations = false;  //------ Wybór wersji
    final int MAX_IT = 100;              //------ Maksymalna ilosc iteracji
    final int N_POP = 2000;                //------ Ilosc czasteczek w populacji
    final int NUM_OF_POINTS = 10;

    final int MAX_HEIGHT = 1000;
    int cols = 1200;
    int rows = 600;
    int scale = 10;


    public Point point;
    public List<Point> points = new ArrayList<>();

    public List<Box> boxes = new ArrayList<>();

    public float[][] heights = new float[1200/scale][600/scale];
    
    
    
    boolean temp = true;
    int i = 0;
    Population population;
    Utils utils = new Utils(false);
    Random r = new Random();


    public static void main(String[] args) {
        PApplet.main("sample.Main");
    }

    public void settings() {
        size(1200, 600);
//        fullScreen();
    }

    public void setup() {
//        frameRate(1);
        background(51);
        float yoff=0;
        for (int x = 0 ; x < cols/scale ; x++){
            float xoff=0;
            for (int y = 0 ; y < rows/scale;y++){
                heights[x][y] = noise(xoff,yoff);
                boxes.add(new Box(this,x,y,map(heights[x][y],0,1,-300,1000),scale));
                xoff+=0.5f;
            }
            yoff+=0.5f;
        }

        drawMap();
        initializeSwarm();
    }

    public void draw() {
        background(51);
        drawMap();
        System.out.println("Najwyzszy: " + boxes.parallelStream().max((o1, o2) -> o1.getHeight() > o2.getHeight() ? 1 : -1));
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
            }
//            else{
//                randomizeHeights();
//            }
        }

        if(mousePressed && (mouseButton == RIGHT)){
            randomizePositions();
        }

    }

    private void drawMap(){
        boxes.forEach(Box::show);
    }

    private void randomizeHeights(){
//        boxes.forEach();
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
            agent.calculateFitness(boxes, population);
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
                    agent.calculateFitness(boxes, population);
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
