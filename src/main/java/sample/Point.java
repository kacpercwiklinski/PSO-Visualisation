package sample;

import lombok.Data;
import processing.core.PApplet;
import processing.core.PVector;


public @Data
class Point {
    private PApplet p;
    private PVector pos;

    public Point(PApplet p , int x , int y){
        this.p = p ;
        this.pos = new PVector(x,y);
    }

    public void show(){
        p.fill(0,255,255);
        p.ellipse(this.pos.x,this.pos.y,16,16);
    }

}
