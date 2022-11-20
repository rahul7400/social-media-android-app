package in.macro.codes.Kncok.Service;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.IBinder;

public class StoryJob extends JobService {

    private boolean mRunning = false;
    public StoryJob() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {



        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }


}
