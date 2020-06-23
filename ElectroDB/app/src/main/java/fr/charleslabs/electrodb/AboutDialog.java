package fr.charleslabs.electrodb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class AboutDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.aboutDialog_title)
                .setIcon(R.drawable.resultlist_qfp)
                .setMessage(R.string.aboutDialog_message)
                .setCancelable(true)
                .setPositiveButton(getText(R.string.aboutDialog_okBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setNegativeButton(getText(R.string.aboutDialog_websiteBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.charleslabs.fr"));
                        startActivity(intent);
                    }
                })
                .create();
    }

    @Override
    public void onStart(){
        if (getDialog() != null)
            getDialog().getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
        super.onStart();
    }
}
