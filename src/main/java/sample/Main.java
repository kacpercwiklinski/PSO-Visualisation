package sample;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends PApplet {

    private final Boolean wIterations = true;


    private List<Point> points = new ArrayList();

    private int FITNESS_FUNCTION = 1; //    1 --- Sphere Function   ,  2 --- Booth function , 3 --- Himmelblau's function
    private final int MAX_IT = 2000;              //------ Maksymalna ilosc iteracji
    private final int N_POP = 100;                //------ Ilosc czasteczek w populacji

    private final int SCOPE = 5;

    private final int FRAMERATE = 30;

    static int scl = 10;
    static int w = 1000;
    static int h = 1000;
    static int cols = w / scl;
    static int rows = h / scl;


    Boolean pointsAdded = false;

    private float highest;
    private float lowest;
    private float functionWeight;

    private boolean temp = true;
    private int i = 0;
    private Population population;
    private final Utils utils = new Utils(false);
    private final Random r = new Random();


    public static void main(String[] args) {
        PApplet.main("sample.Main");
    }

    public void settings() {
        size(1200, 600, P3D);
    }

    private float sphereFunction(float x, float y) {
        float sum = x * x;
        sum += y * y;
        functionWeight = 0.1f;
        return sum;
    }

    private double boothFunction(float x, float y) {
        functionWeight = 0.01f;
        return Math.pow((x + 2 * y + 7), 2) + Math.pow((2 * x + y - 5), 2);
    }

    private double himmelblaus(float x, float y) {
        functionWeight = 0.00005f;
        return Math.pow((x * x + y - 11), 2) + Math.pow((x + y * y - 7), 2);
    }


    public void setup() {
        frameRate(FRAMERATE);
        background(51);
        initializeSwarm();

        drawShape();

        highest = points.parallelStream().max((o1, o2) -> o1.getPos().z > o2.getPos().z ? 1 : o1.equals(o2) ? 0 : -1).get().getPos().z;
        lowest = points.parallelStream().min((o1, o2) -> o1.getPos().z > o2.getPos().z ? 1 : o1.equals(o2) ? 0 : -1).get().getPos().z;
    }

    public void draw() {
        background(20);
        translate(width / 2, (height / 2), -1200);
        rotateX(PI / 3);
        translate(0, height / 2 + 200);


        rotateZ(mouseX * 0.01f);

        drawShape();

        population.drawPopulation();

        checkVersion();

        handleMouse();


        checkGoal();
    }

    private void checkVersion() {
        if (wIterations) {
            PSOwIterations();
        } else {
            PSO();
        }
    }

    private void handleMouse() {
        if (mousePressed) {
            if (wIterations && (mouseButton == LEFT)) {
                initializeSwarm();
            }
        }
    }


    private void drawShape() {
        strokeWeight(0.3f);
        stroke(255);

        for (int y = -rows / 2; y < rows / 2; y++) {
            beginShape(TRIANGLE_STRIP);
            for (int x = -cols / 2; x < cols / 2; x++) {
                double zF1;
                double zF2;
                float xF1 = map(x * scl, -cols / 2, cols / 2, -SCOPE, SCOPE);
                float yF1 = map(y * scl, -rows / 2, rows / 2, -SCOPE, SCOPE);
                float yF2 = map((y + 1) * scl, -rows / 2, rows / 2, -SCOPE, SCOPE);


                switch (FITNESS_FUNCTION) {
                    case 1:
                        zF1 = sphereFunction(xF1, yF1) * functionWeight;
                        zF2 = sphereFunction(xF1, yF2) * functionWeight;
                        break;
                    case 2:
                        zF1 = boothFunction(xF1, yF1) * functionWeight;
                        zF2 = boothFunction(xF1, yF2) * functionWeight;
                        break;
                    case 3:
                        zF1 = himmelblaus(xF1, yF1) * functionWeight;
                        zF2 = himmelblaus(xF1, yF2) * functionWeight;
                        break;
                    default:
                        zF1 = sphereFunction(xF1, yF1) * functionWeight;
                        zF2 = sphereFunction(xF1, yF2) * functionWeight;
                        break;
                }


                pushStyle();
                colorMode(HSB, 1, 1, 1);

                double color = zF1 * 2;

                color = map((float) color, 640, 0, 1f, 0.2f);

                fill((float) color, 0.9f, 1f);
                noStroke();
                vertex(x * scl, y * scl, (float) zF1);
                vertex(x * scl, (y + 1) * scl, (float) zF2);

                popStyle();


                if (!pointsAdded) {
                    points.add(new Point(this, new PVector(x * scl, y * scl, (float) zF1)));
                }
            }
            endShape();
        }

        pointsAdded = true;
    }

    private void checkGoal() {
        if (utils.getGBest().getFitness() > highest - 20) {
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
        population.getPopulation().parallelStream().forEach(agent -> agent.calculateFitness(points, population));
    }

    private void PSOwIterations() {
        if (population != null && i < MAX_IT) {
            population.updatePopulation();
            if (utils.getGoalReached()) {
                utils.setGoalReached(true);
                System.out.println("Znaleziono najwyzszy punkt w "+ i+" iteracji.");
                i = MAX_IT + 1;
                temp = false;
            } else {
                population.getPopulation().forEach(agent -> agent.calculateFitness(points, population));

            }
            i++;
        } else {
            if (temp)
                System.out.println("Nie znaleziono rozwiazania.");
            temp = false;
        }
    }


}
