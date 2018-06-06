package sample;



import lombok.*;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public @Data
@ToString
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
    boolean isWinner = false;

    float currentHeight = 0;

    public Agent(PApplet p, int number, float x, float y ) {
        this.parent = p;
        this.number = number;
        this.pos = new PVector(x,y);
        this.vel = new PVector(parent.random(-100,100),parent.random(-100,100));
        acc = new PVector(0,0);
        this.pBest = this;
    }

    public Agent(Agent agent){
        this.parent = agent.parent;
        this.number = agent.number;
        this.pos = agent.pos;
        this.vel = agent.vel;
        this.fitness = agent.fitness;
    }

    public void show() {
        int size ;
        if(isWinner){
            parent.fill(255, 255, 0);
            size = 16;
        }else{
            parent.fill(0, 255, 0);
            size = 4;
        }
        parent.strokeWeight(1);
        parent.ellipse(this.pos.x, this.pos.y, size, size);
    }

    public void update() {
        this.vel.add(acc);
        pos.add(this.vel);
        vel.limit(1);
        acc.mult(0);
    }

    private void applyForce(PVector force){
        acc.add(force);
    }

    private void updateParticle(Population p){
        float c1 = 2.05f,c2 = 2f;
        float k = 1f;

        float o = c1 + c2;

//        float w = ((float) ((2 * k) / (Math.abs(2 - o - Math.sqrt(Math.pow(o, 2) - (4 * o))))));
            float w = 1.2f;

        PVector t1 = this.vel.mult(w);
        PVector t2 = new PVector(this.pBest.getPos().x - this.getPos().x,this.pBest.getPos().y-this.getPos().y).mult(c1* p.getP().random(0,1));
        PVector t3 = new PVector(p.getUtils().getGBest().getPos().x - this.getPos().x,p.getUtils().getGBest().getPos().y-this.getPos().y).mult(c2* p.getP().random(0,1));
        PVector vel = t1.add(t2).add(t3);

        this.pos.add(vel);
    }


    public void calculateFitness(List<Box> points, Population p){

        float currentHeight = 0;

        for (Box point : points){
            if(this.pos.dist(point.getPos()) < point.getScale()/2){
                currentHeight = point.getHeight();
            }
        }

        this.fitness = currentHeight;

        this.memory.put(this.fitness,this.pos);

        if(this.fitness > this.pBest.getFitness()){
            this.memory.put(this.fitness,this.pos);
        }

        Optional<Float> fit = memory.keySet().stream().max((o1, o2) -> o1 > o2 ? 1 : o1.equals(o2) ? 0 : -1);

        fit.ifPresent(aFloat -> {
            this.pBest.setFitness(aFloat);
            this.pBest.setCurrentHeight(this.currentHeight);
            this.pBest.setPos(memory.get(fit.get()));
            this.pBest.setVel(this.vel);
        });



        if(this.pBest.getFitness() > p.getUtils().getGBest().getFitness()){
            p.getUtils().setGBest(this.pBest);
        }

        updateParticle(p);

        memory.clear();
    }

    public String toString(){
        return "Fitness: " + this.fitness + ", Height: " + this.currentHeight;
    }

}
