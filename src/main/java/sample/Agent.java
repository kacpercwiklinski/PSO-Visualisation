package sample;



import lombok.Data;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public @Data
class Agent {

    private PApplet parent;
    private int number;
    private PVector pos;
    private PVector vel;
    private PVector acc;
    private float speed;
    private float fitness;
    private Agent pBest;
    private HashMap<Float,PVector> memory = new HashMap<>();

    public Agent(PApplet p, int number, float x, float y) {
        this.parent = p;
        this.number = number;
        this.pos = new PVector(x,y);
        this.vel = new PVector(0,0);
        acc = new PVector(0,0);
        this.pBest = this;
    }

    public void show() {
        parent.fill(0, 255, 0);
        parent.strokeWeight(1);
        parent.ellipse(this.pos.x, this.pos.y, 4, 4);
    }

    public void update() {
        this.vel.add(acc);
        pos.add(this.vel);
        vel.limit(5);
        acc.mult(0);
    }

    private void applyForce(PVector force){
        acc.add(force);
    }

    private void updateParticle(Agent target,Population p){
        PVector dir = new PVector(target.getPos().x - this.pos.x, target.getPos().y - this.pos.y).normalize();
        float c1 = 2f,c2 = 2f;
        float w = 1.2f;

        PVector t1 = dir.mult(w);
        PVector t2 = new PVector(this.pBest.getPos().x - this.getPos().x,this.pBest.getPos().y-this.getPos().y).mult(c1* p.getP().random(0,1));
        PVector t3 = new PVector(p.getUtils().getGBest().getPos().x - this.getPos().x,p.getUtils().getGBest().getPos().y-this.getPos().y).mult(c2* p.getP().random(0,1));
        PVector vel = t1.add(t2).add(t3);

        this.applyForce(vel);
    }


    public void calculateFitness(List<Box> points, Population p){

        float currentHeight = 0;
        Box highest = points.stream().max((o1, o2) -> o1.getHeight() > o2.getHeight()? 1 : -1).get();


        for (Box point : points){
            if(this.pos.dist(point.getPos()) < point.getScale()/2){
                currentHeight = point.getHeight();
            }
        }

        this.fitness = currentHeight;

        System.out.println("Aktualna wysokosc: " + currentHeight);


        this.memory.put(this.fitness,this.pos);

        Optional<Float> fit = memory.keySet().stream().max((o1, o2) -> o1 > o2 ? 1 : o1 == o2 ? 0 : -1);
        fit.ifPresent(aFloat -> {
            this.pBest.setFitness(aFloat);
            this.pBest.setPos(memory.get(fit.get()));
        });


        if(this.fitness > this.pBest.getFitness()){
            this.memory.put(this.fitness,this.pos);
        }

        if(this.pBest.getFitness() >= p.getUtils().getGBest().getFitness()){
            p.getUtils().setGBest(this.pBest);
        }

        if(this.pos.dist(highest.getPos()) <= 4){
            p.getUtils().setGoalReached(true);
        }


        updateParticle(p.getUtils().getGBest(),p);

        memory.clear();
    }

}
