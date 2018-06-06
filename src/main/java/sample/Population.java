package sample;

import lombok.Data;
import lombok.ToString;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;


@Data
@ToString
public class Population {
    private PApplet p;
    private int quantity;
    private List<Agent> population = new ArrayList<>();
//    private Agent pBest;
    private Random r = new Random();
    private Utils utils;

    public Population(PApplet p,int quantity,Utils utils){
        this.p = p;
        this.utils = utils;

        IntStream.range(0,quantity).forEach(value -> {
            population.add(new Agent(p,value,p.random(p.width),p.random(p.height)));
        });


//        this.pBest = population.get(r.nextInt(population.size()));
    }

    public void drawPopulation(){
        population.forEach(Agent::show);
    }

    public void updatePopulation(){
        population.forEach(Agent::update);
    }





}
