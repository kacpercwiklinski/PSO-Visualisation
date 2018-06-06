package sample;

import lombok.Data;

import java.util.Random;

@Data
public class Utils {
    Random r = new Random();
    private Agent gBest;
    private Boolean goalReached;

    public Utils(Boolean reached){
        this.goalReached = reached;
    }




}
