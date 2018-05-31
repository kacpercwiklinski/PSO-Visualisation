package sample;

import lombok.Data;
import processing.core.PApplet;
import processing.core.PVector;

@Data
public class Box {

    private PApplet p;
    private PVector pos;
    private float height;
    private int scale;

    public Box(PApplet p , int x , int y,float height,int scale){
        this.p = p ;
        this.pos = new PVector(x*scale+scale/2,y*scale+scale/2);
        this.height = height;
        this.scale = scale;
    }



    public void show(){
        float r = p.map(height,0,1000,50,255);
        p.fill(r,0,0);
        p.noStroke();
        p.rect(this.pos.x-scale/2,this.pos.y-scale/2,scale,scale);
    }



}
