package pl.edu.pwr.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

@SuppressLint("Pedometer")
class MessageReceiver extends ResultReceiver
{
    private HomeActivity.Message message;

    public MessageReceiver(HomeActivity.Message message)
    {
        super(new Handler());
        this.message = message;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData)
    {
        message.displayMessage(resultCode, resultData);

    }
}
