package snow.prog.fhbgds;

public class Timer
{
    float ticksPerSecond;
    public int freq = 0;
    private double lastHighResTime;
    public int elapsedFullTicks;
    public float timerSpeed = 1.0F;
    public float timeSinceLastTick;
    private long lastSysClockSyncTime;
    private long lastSyncHighResClock;
    private long field_74285_i;
    private double timeSyncAdjustment = 1.0D;

    public Timer(float par1)
    {
        this.ticksPerSecond = par1;
        this.lastSysClockSyncTime = Snow.getSystemTime();
        this.lastSyncHighResClock = System.nanoTime() / 1000000L;
    }

    public void updateTimer()
    {
    	freq++;
        long currentTime = Snow.getSystemTime();
        long timeSinceLastSync = currentTime - this.lastSysClockSyncTime;
        long highResTime = System.nanoTime() / 1000000L;
        double timeInSeconds = (double)highResTime / 1000.0D;

        if (timeSinceLastSync <= 1000L && timeSinceLastSync >= 0L)
        {
            this.field_74285_i += timeSinceLastSync;

            if (this.field_74285_i > 1000L)
            {
                long timeSinceHighResSync = highResTime - this.lastSyncHighResClock;
                double d1 = (double)this.field_74285_i / (double)timeSinceHighResSync;
                this.timeSyncAdjustment += (d1 - this.timeSyncAdjustment) * 0.20000000298023224D;
                this.lastSyncHighResClock = highResTime;
                this.field_74285_i = 0L;
            }

            if (this.field_74285_i < 0L)
            {
                this.lastSyncHighResClock = highResTime;
            }
        }
        else
        {
            this.lastHighResTime = timeInSeconds;
        }

        this.lastSysClockSyncTime = currentTime;
        double d2 = (timeInSeconds - this.lastHighResTime) * this.timeSyncAdjustment;
        this.lastHighResTime = timeInSeconds;

        if (d2 < 0.0D)
        {
            d2 = 0.0D;
        }

        if (d2 > 1.0D)
        {
            d2 = 1.0D;
        }

        this.timeSinceLastTick = (float)((double)this.timeSinceLastTick + d2 * (double)this.timerSpeed * (double)this.ticksPerSecond);
        this.elapsedFullTicks = (int)this.timeSinceLastTick;
        this.timeSinceLastTick -= (float)this.elapsedFullTicks;

        if (this.elapsedFullTicks > 10){
            this.elapsedFullTicks = 10;
        }
        if(this.freq >= Snow.frequency && !Snow.isPaused){
        	Snow.instance.createFlake();
        	freq = 0;
        }
    }
}