package pl.edu.pwr.myapplication;

public class SensorFilter {

    public SensorFilter() {}

    public float sumOfElements(float[] array)
    {
        float returnValue = 0;
        for (int i = 0; i < array.length; ++i)
        {
            returnValue += array[i];
        }

        return returnValue;
    }

    public float norm(float[] array)
    {
        float returnValue = 0;
        for (int i = 0; i < array.length; ++i)
        {
            returnValue += Math.pow(array[i],2);
        }

        return (float) Math.sqrt(returnValue);
    }

    public float dot(float[] a, float[] b)
    {
        float returnValue = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
        return returnValue;
    }

}