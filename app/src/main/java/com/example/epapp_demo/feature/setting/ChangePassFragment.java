package com.example.epapp_demo.feature.setting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.epapp_demo.feature.login.LoginActivity;
import com.example.epapp_demo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.epapp_demo.feature.setting.SettingFragment.email;
import static com.example.epapp_demo.feature.setting.SettingFragment.pass;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class ChangePassFragment extends Fragment {
    EditText edtEmail, edtPass, edtPassNew;
    Button btnXacNhan;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference1 = firebaseDatabase.getReference("KhachHang");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public ChangePassFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_pass_fragment, container, false);
        edtEmail = view.findViewById(R.id.ed_EmailChange);
        edtPass = view.findViewById(R.id.ed_PassWord);
        edtPassNew= view.findViewById(R.id.ed_PassWordNew);
        btnXacNhan = view.findViewById(R.id.btn_ChangePass);
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email1 = edtEmail.getText().toString();
                String pass1 = edtPass.getText().toString();
                String pass2 = edtPassNew.getText().toString();
                if(email1.isEmpty() || pass1.isEmpty()){
                    Toast.makeText(getActivity(), "Không được để trống", Toast.LENGTH_SHORT).show();
                }
                else if (!email1.equals(email)){
                    Toast.makeText(getActivity(), "Email không đúng", Toast.LENGTH_SHORT).show();
                }else if( !pass1.equals(pass)){
                    Toast.makeText(getActivity(), "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View view1 = layoutInflater.inflate(R.layout.change_pass_alert_dialog,null);
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            mAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("User", "User password updated.");
                                                Toast.makeText(getActivity(), "Đăng nhập lại để tiếp tục", Toast.LENGTH_SHORT).show();
                                                FirebaseAuth.getInstance().signOut();
                                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                            }
                                        }
                                    });

                        }
                    });
                    builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setView(view1);
                    builder.show();
                }
            }
        });
        return view;
    }
}