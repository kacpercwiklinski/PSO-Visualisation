package sample;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends PApplet {

    private final Boolean wIterations = true;         //------ Wybór wersji
    private final int MAX_IT = 2000;              //------ Maksymalna ilosc iteracji
    private final int N_POP = 100;                //------ Ilosc czasteczek w populacji
    private final int FRAMERATE = 60;


    private final int cols = 1200;
    private final int rows = 600;
    private final int scale = 20;
    private float yoff = 0;
    private Box highest;


    private final List<Box> boxes = new ArrayList<>();

    private final float[][] heights = new float[1200 / scale][600 / scale];


    private boolean temp = true;
    private int i = 0;
    private Population population;
    private final Utils utils = new Utils(false);
    private final Random r = new Random();


    public static void main(String[] args) {
        PApplet.main("sample.Main");
    }

    public void settings() {
        size(1200, 600);
    }

    public void setup() {
        frameRate(FRAMERATE);
        background(51);
        randomizeMap();
        initializeSwarm();
    }

    public void draw() {
        checkGoal();
        background(51);
        drawMap();
        population.drawPopulation();

        if (wIterations) {
            PSOwIterations();
        } else {
            PSO();
        }

        if (mousePressed) {
            if (wIterations && (mouseButton == LEFT)) {
                initializeSwarm();
            }
            if (mouseButton == RIGHT) {
                randomizeMap();
            }
        }
    }

    private void randomizeMap() {
        boxes.clear();
        for (int x = 0; x < cols / scale; x++) {
            float xoff = 0;
            for (int y = 0; y < rows / scale; y++) {
                heights[x][y] = noise(xoff, yoff);
                boxes.add(new Box(this, x, y, map(heights[x][y], 0, 1, -300, 1000), scale));
                xoff += 0.05f;
            }
            yoff += 0.05f;
        }
        highest = boxes.parallelStream().max((o1, o2) -> o1.getHeight() > o2.getHeight() ? 1 : o1.equals(o2)? 0 : -1).get();
        drawMap();
    }

    private void drawMap() {
        boxes.forEach(Box::show);
    }

    private void checkGoal() {
        if (utils.getGBest().getFitness() == highest.getHeight()) {
            utils.setGoalReached(true);
        }
    }

    private void initializeSwarm() {
        population = new Population(this, N_POP, utils);
        utils.setGBest(population.getPopulation().get(r.nextInt(population.getPopulation().size())));
        utils.setGoalReached(false);
        i = 0;
        temp = true;
    }

    private void PSO() {
        population.updatePopulation();
        population.getPopulation().parallelStream().forEach(agent -> agent.calculateFitness(boxes, population));
    }

    private void PSOwIterations (){
        if (population != null && i < MAX_IT) {
            population.updatePopulation();
            if (utils.getGoalReached()) {
                utils.getGBest().setWinner(true);
                System.out.println("Znaleziono rozwiazanie w : " + i + " iteracji.");
                System.out.println("Najwyzsze miejsce :" + utils.getGBest().getFitness());
                System.out.println("Najwyzszy box :" + highest.getHeight());

                utils.setGoalReached(true);
                i = MAX_IT + 1;
                temp = false;
            } else {
                population.getPopulation().forEach(agent -> agent.calculateFitness(boxes, population));

            }
            i++;
        } else {
            if (temp)
                System.out.println("Nie znaleziono rozwiazania.");
            temp = false;
        }
    }


}
