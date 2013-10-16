import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by sdickson on 8/16/13.
 */
public class NetworkManager
{
    Context context;
    Activity activity;

    public NetworkManager(Context context, Activity activity)
    {
        this.context = context;
        this.activity = activity;
    }

    public boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null)
        {
            NetworkInfo[] activeNetwork = cm.getAllNetworkInfo();
            for(int i = 0; i < activeNetwork.length; i++)
            {
                if(activeNetwork[i].getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void showNetworkErrorDialog()
    {
        if(activity != null)
        {
                lastThrown = System.currentTimeMillis();
                AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                alertDialog.setCancelable(false);
                alertDialog.setTitle("Network Error");
                alertDialog.setMessage("The Internet connection appears to be offline. Some content may not be available until a connection is made.");
                alertDialog.setButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
        }
    }
}
