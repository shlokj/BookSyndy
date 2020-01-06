package co.in.prodigyschool.passiton.ui.bookRequests;

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

public class ReqFragment extends Fragment {

    private ReqViewModel sendViewModel;
    private TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(ReqViewModel.class);
        View root = inflater.inflate(R.layout.fragment_book_requests, container, false);

        return root;
    }
}