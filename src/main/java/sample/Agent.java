package sample;


import lombok.Data;
import lombok.ToString;
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
    private HashMap<Float, PVector> memory = new HashMap<>();
    float w;

    float currentHeight = 0;

    public Agent(PApplet p, int number, float x, float y, float z, float inertialWeight) {
        this.parent = p;
        this.number = number;
        this.pos = new PVector(x, y, z);
        this.vel = new PVector(parent.random(-100, 100), parent.random(-100, 100), parent.random(-100, 100));
        acc = new PVector(0, 0);
        this.pBest = this;
        this.w = inertialWeight;
        this.fitness = 0;
    }

    public void show() {
        parent.strokeWeight(4);
        parent.point(this.pos.x, this.pos.y, this.pos.z);
    }

    public void update() {
        this.vel.add(acc);
        pos.add(this.vel);
        vel.limit(2);
        acc.mult(0);
    }

    private void applyForce(PVector force) {
        acc.add(force);
    }

    private void updateParticle(Population p) {
        float c1 = 1.7f, c2 = 1.7f;

        PVector t1 = this.vel.mult(w);
        PVector t2 = new PVector(this.pBest.getPos().x - this.getPos().x, this.pBest.getPos().y - this.getPos().y, this.pBest.getPos().z - this.getPos().z).mult(c1 * p.getP().random(0, 1));
        PVector t3 = new PVector(p.getUtils().getGBest().getPos().x - this.getPos().x, p.getUtils().getGBest().getPos().y - this.getPos().y, p.getUtils().getGBest().getPos().z - this.getPos().z).mult(c2 * p.getP().random(0, 1));
        PVector vel = t1.add(t2).add(t3);

        this.pos.add(vel);
    }


    public void calculateFitness(List<Point> points, Population p) {

        float currentHeight = 0;

        for (Point point : points) {
                if (this.pos.dist(point.getPos()) < point.getSize()) {
                    currentHeight = point.getPos().z;
                }
        }

        this.fitness = currentHeight ;

        if (this.fitness > this.pBest.getFitness()) {
            this.memory.put(this.fitness, this.pos);
        }
        Optional<Float> fit = memory.keySet().stream().max((o1, o2) -> o1 > o2 ? 1 : o1.equals(o2) ? 0 : -1);


        fit.ifPresent(aFloat -> {
            this.pBest.setFitness(aFloat);
            this.pBest.setCurrentHeight(this.currentHeight);
            this.pBest.setPos(memory.get(fit.get()));
            this.pBest.setVel(this.vel);
        });


        if (this.pBest.getFitness() > p.getUtils().getGBest().getFitness()) {
            p.getUtils().setGBest(this.pBest);
        }

        updateParticle(p);

        memory.clear();

        if (this.w > 0.4f) {
            this.w -= 0.001f;
        }
    }

    public String toString() {
        return "Fitness: " + this.fitness + ", Height: " + this.currentHeight;
    }

}
