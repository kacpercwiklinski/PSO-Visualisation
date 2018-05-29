package sample;

import lombok.Data;
import processing.core.PApplet;
import processing.core.PVector;



public @Data
class Point {
    private PApplet p;
    private PVector pos;
    private float height;


    public Point(PApplet p , int x , int y,float height){
        this.p = p ;
        this.pos = new PVector(x,y);
        this.height = height;
    }

    public void show(){
        float color = PApplet.map(this.height,100,500,100,255);
        p.fill(color*0.25f,0,0);
        p.stroke(0);
        p.strokeWeight(1);
        p.ellipse(this.pos.x,this.pos.y,128,128);
        p.fill(color*0.5f,0,0);
        p.ellipse(this.pos.x,this.pos.y,64,64);
        p.fill(color,0,0);
        p.ellipse(this.pos.x,this.pos.y,32,32);
        p.textSize(16);
        p.text(this.height,this.pos.x + 64,this.pos.y);
    }

}
