package pl.edu.pwr.myapplication;

public class DataTuple
{
    public String date;
    public String steps;
    public String distance;
    public String speed;
    public String id;

    public DataTuple(String date, String steps, String distance, String speed, String id)
    {
        this.date = date;
        this.steps = steps;
        this.distance = distance;
        this.speed = speed;
        this.id = id;
    }
}
