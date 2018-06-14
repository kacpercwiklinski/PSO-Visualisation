package sample;

import lombok.Data;
import processing.core.PApplet;
import processing.core.PVector;

@Data
public class Point {
    private PApplet parent;
    private PVector pos;
    private int size = 10;

    public Point(PApplet parent, PVector pos) {
        this.parent = parent;
        this.pos = pos;
    }

    public Point() {
    }

    public void show(){
        parent.fill(255,255,0);
        parent.strokeWeight(size);
        parent.point(this.pos.x,this.pos.y,this.pos.z);
    }
}
