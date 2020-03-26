package com.example.covid19alerts.ui.send;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.covid19alerts.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SendFragment extends Fragment {

//    private SendViewModel sendViewModel;
    private EditText name, phone, need, city, detail;
    private Context _context;
    private TextView loc;
    private Button submit;
    private BroadcastReceiver broadcastReceiver;

    private AwesomeValidation awesomeValidation;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        sendViewModel =
//                ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);


        // Assign variables
        loc = root.findViewById(R.id._loc);
        name = root.findViewById(R.id._name);
        phone = root.findViewById(R.id._phone);
        need = root.findViewById(R.id._need);
        city = root.findViewById(R.id._city);
        detail = root.findViewById(R.id._details);
        submit = root.findViewById(R.id.btn_submit);

        setup();

        return root;
    }

    private void uploadData(final Activity a) {
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("name", name.toString())
                .add("phone", phone.toString())
                .add("city", city.toString())
                .add("need", need.toString())
                .add("details", detail.toString())
                .add("loc", loc.getText().toString())
                .build();

        String url = "https://covid19alerts-949a.restdb.io/rest/requests";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-apikey", "a1d9893a3d0c5a7d8574a0b4d0dc85ae5910e")
                .post(formBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.i(TAG, e.getMessage());

                a.runOnUiThread(new Runnable() {
                    public void run() {
                        final Toast toast = Toast.makeText(_context, "Failed", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                a.runOnUiThread(new Runnable() {
                    public void run() {
                        final Toast toast = Toast.makeText(_context, "Uploaded", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                Log.i(TAG, response.body().string());
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        validate(getActivity());
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check validation
                if (awesomeValidation.validate()) {
                    // TODO: upload all data to DB
                    uploadData(getActivity());
                    Toast.makeText(_context,
                            "Request Submitted Successfully", Toast.LENGTH_SHORT).show();
                    // clear form
                    name.getText().clear();
                    phone.getText().clear();
                    need.getText().clear();
                    city.getText().clear();
                    detail.getText().clear();
                } else {
                    Toast.makeText(_context,
                            "Please fill in the details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        _context = context;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        _context.unregisterReceiver(broadcastReceiver);
    }
    private void setup() {
        if (broadcastReceiver ==  null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //use coordinates
                    loc.setText(" "+intent.getExtras().get("coordinates"));
                }
            };
        }
        _context.registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    private void validate(Activity a) {

        // Init validation
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        // Add validation
        awesomeValidation.addValidation(a, R.id._name,
                RegexTemplate.NOT_EMPTY, R.string.invalid_name);
        awesomeValidation.addValidation(a, R.id._phone,
                "[5-9]{1}[0-9]{9}$", R.string.invalid_mobile);
        awesomeValidation.addValidation(a, R.id._need,
                RegexTemplate.NOT_EMPTY, R.string.invalid_entry);
        awesomeValidation.addValidation(a, R.id._details,
                RegexTemplate.NOT_EMPTY, R.string.invalid_entry);
        awesomeValidation.addValidation(a, R.id._city,
                RegexTemplate.NOT_EMPTY, R.string.invalid_entry);
    }
}