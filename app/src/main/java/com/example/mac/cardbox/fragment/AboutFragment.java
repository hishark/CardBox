package com.example.mac.cardbox.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.mac.cardbox.R;


public class AboutFragment extends Fragment {

    private ImageButton button_sendMsgTo777;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_about, container, false);

        button_sendMsgTo777 = view.findViewById(R.id.button_sendMsgTo777);
        button_sendMsgTo777.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri smsToUri = Uri.parse("smsto:13767862093");

                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

                intent.putExtra("sms_body", "");

                startActivity(intent);

            }
        });

        return view;
    }


}
