package pl.edu.pwr.myapplication;

public class StepDetector
{

    private int ACCEL_RING_SIZE = 50;
    private int VEL_RING_SIZE = 10;
    private float STEP_THRESHOLD = 50f;

    private int STEP_DELAY_NS = 250000000;

    private int accelerometerRingCounter = 0;

    private float[] accelRingX = new float[ACCEL_RING_SIZE];
    private float[] accelRingY = new float[ACCEL_RING_SIZE];
    private float[] accelRingZ = new float[ACCEL_RING_SIZE];

    private int velRingCounter = 0;
    private float[] velRing = new float[VEL_RING_SIZE];
    private long lastStepTimeNs = 0;
    private float oldVelocityEstimate = 0;

    private StepListener listener;
    private SensorFilter sensorFilter;

    public StepDetector()
    {
        sensorFilter = new SensorFilter();
    }

    public void registerListener(StepListener listener)
    {
        this.listener = listener;
    }

    public void updateAccelerometer(long timeNs, float x, float y, float z)
    {
        float[] currentAccelerometer = new float[3];
        currentAccelerometer[0] = x;
        currentAccelerometer[1] = y;
        currentAccelerometer[2] = z;

        accelerometerRingCounter++;
        accelRingX[accelerometerRingCounter % ACCEL_RING_SIZE] = currentAccelerometer[0];
        accelRingY[accelerometerRingCounter % ACCEL_RING_SIZE] = currentAccelerometer[1];
        accelRingZ[accelerometerRingCounter % ACCEL_RING_SIZE] = currentAccelerometer[2];

        float[] worldZ = new float[3];

        worldZ[0] = sensorFilter.sumOfElements(accelRingX) / Math.min(accelerometerRingCounter, ACCEL_RING_SIZE);
        worldZ[1] = sensorFilter.sumOfElements(accelRingY) / Math.min(accelerometerRingCounter, ACCEL_RING_SIZE);
        worldZ[2] = sensorFilter.sumOfElements(accelRingZ) / Math.min(accelerometerRingCounter, ACCEL_RING_SIZE);

        float normalizationFactor = sensorFilter.norm(worldZ);

        worldZ[0] = worldZ[0] / normalizationFactor;
        worldZ[1] = worldZ[1] / normalizationFactor;
        worldZ[2] = worldZ[2] / normalizationFactor;

        float currentZ = sensorFilter.dot(worldZ, currentAccelerometer) - normalizationFactor;

        velRingCounter++;
        velRing[velRingCounter % VEL_RING_SIZE] = currentZ;

        float velocityEstimate = sensorFilter.sumOfElements(velRing);

        if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
                && (timeNs - lastStepTimeNs > STEP_DELAY_NS))
        {
            listener.step(timeNs);
            lastStepTimeNs = timeNs;
        }

        oldVelocityEstimate = velocityEstimate;
    }
}