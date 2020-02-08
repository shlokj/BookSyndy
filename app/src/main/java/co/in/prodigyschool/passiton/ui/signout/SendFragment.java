package co.in.prodigyschool.passiton.ui.signout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;

import co.in.prodigyschool.passiton.R;
import co.in.prodigyschool.passiton.SignInActivity;

public class SendFragment extends Fragment {

    private SendViewModel sendViewModel;
    private TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);
        textView = root.findViewById(R.id.text_send);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sign out");
        builder.setMessage("Sign out of BookSyndy?");
        builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                signOut();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //return to home frag
                //this solution shows signout as selected
              //  FragmentTransaction t = getFragmentManager().beginTransaction();
              //  t.replace(R.id.nav_host_fragment, new HomeFragment());
             //   t.commit();
                startActivity(getActivity().getIntent());
                getActivity().finish();
            }
        });
/*        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                startActivity(getActivity().getIntent());
                getActivity().finish();
            }
        });*/
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                startActivity(getActivity().getIntent());
                getActivity().finish();
            }
        });
        builder.show();


        return root;
    }
    public void signOut(){

        Intent signout = new Intent(getActivity(), SignInActivity.class);
        signout.putExtra("CLOSE_APP",true);
        signout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        FirebaseAuth.getInstance().signOut();
        startActivity(signout);
        getActivity().finish();
        sendViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
    }
}