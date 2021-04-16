package com.example.douglasbookstore.ui.help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.douglasbookstore.adapters.HelpImgAdapter;
import com.example.douglasbookstore.MapsActivity;
import com.example.douglasbookstore.R;

public class HelpFragment extends Fragment {

    private HelpViewModel helpViewModel;
    View view;
    GridView imgGrid;
    ImageView imgView;
    HelpImgAdapter adapter;
    Button btnMaps;
    TextView txtViewPhone;
    TextView txtViewEmail;
    TextView txtViewMap;
    TextView txtViewWeb;

    int[] imgBookStore = {R.drawable.bookstore1, R.drawable.bookstore2, R.drawable.bookstore3, R.drawable.bookstore4};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        helpViewModel =
                ViewModelProviders.of(this).get(HelpViewModel.class);
        view = inflater.inflate(R.layout.fragment_help, container, false);

        addImgInGrid();
        linkPhone();
        linkEmail();
        linkMaps();
        linkWeb();


        return view;
    }

    private void linkWeb() {
        txtViewWeb = view.findViewById(R.id.helpTxtViewWeb);

        txtViewWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse("https://bookstore.douglascollege.ca");
                Intent intent1 = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent1);
            }
        });

    }

    private void linkMaps() {

        txtViewMap = view.findViewById(R.id.helpTxtViewLoca);

        txtViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMap =new Intent(getActivity(), MapsActivity.class);
                getActivity().startActivity(intentMap);
            }
        });
    }

    private void linkEmail() {

        txtViewEmail = view.findViewById(R.id.helpTxtViewPhone);
        txtViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:bookstore@douglascollege.ca"));
                startActivity(Intent.createChooser(emailIntent, "Email"));
            }
        });
    }

    private void linkPhone() {
        txtViewPhone = view.findViewById(R.id.helpTxtViewEmail);
        txtViewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:6045275015"));
                startActivity(callIntent);
            }
        });

    }

    private void addImgInGrid() {

        imgView = view.findViewById(R.id.helpImgView);
        imgGrid = view.findViewById(R.id.helpGridView);

        adapter = new HelpImgAdapter(getActivity(), imgBookStore);
        imgGrid.setAdapter(adapter);
        imgView.setImageResource(imgBookStore[0]);
        imgGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imgView.setImageResource(imgBookStore[position]);
            }
        });

    }


}