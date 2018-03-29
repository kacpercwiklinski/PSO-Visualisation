package sample;



import lombok.Data;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.HashMap;
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
        parent.ellipse(this.pos.x, this.pos.y, 8, 8);
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

    @Deprecated   // -------- Nieuzywane
    public void calculateForce(Point target) {
        PVector force = new PVector(target.getPos().x - this.pos.x, target.getPos().y - this.pos.y).normalize();
        this.applyForce(force);
    }

    @Deprecated //-----Nieuzywane
    public void calculateForce(Agent target) {
        PVector force = new PVector(target.getPos().x - this.pos.x, target.getPos().y - this.pos.y).normalize();
        this.applyForce(force);
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


    public void calculateFitness(Point target,Population p){
        this.fitness = -this.pos.dist(target.getPos());

        this.memory.put(this.fitness,this.pos);
        
//         Działający kod @Deprecated
//        if(this.fitness > p.getPBest().getFitness()){
//            p.setPBest(this);
//        }
//
//        if(p.getPBest().getFitness() > p.getUtils().getGBest().getFitness()){
//            p.getUtils().setGBest(this);
//        }
//        calculateForce(p.getUtils().getGBest());

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

        if(this.pos.dist(target.getPos()) <= 8){
            p.getUtils().setGoalReached(true);
        }
        updateParticle(p.getUtils().getGBest(),p);

        memory.clear();
    }

}
