package com.example.epapp_demo.feature.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.epapp_demo.R;
import com.example.epapp_demo.feature.admin.BottomNavigationAdmin;
import com.example.epapp_demo.feature.cuahang.BottomNavigationStore;
import com.example.epapp_demo.feature.home.BottomNavigation;
import com.example.epapp_demo.feature.register.SignUpActivity;
import com.example.epapp_demo.model.local.modul.Store;
import com.example.epapp_demo.model.local.modul.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button btnLogin;
    TextView txtSignUp;
    TextInputLayout inputEmail,inputPass;
    TextInputEditText edtemail, edtpassword;
    DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    ProgressBar pb;
    LocationManager locationManager;
    boolean GpsStatus;
    int PERMISSION_ALL = 1;
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mAuth = FirebaseAuth.getInstance();
        init();
        edtemail.addTextChangedListener(new LoginActivity.ValidationTextWatcher(edtemail));
        edtpassword.addTextChangedListener(new LoginActivity.ValidationTextWatcher(edtpassword));

        //cấp quyền
        String[] PERMISSIONS = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        final ProgressDialog dialog=new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Vui lòng đợi");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        //dialog.show();


        if (mAuth.getCurrentUser() != null){
            final String userID= fAuth.getCurrentUser().getUid();
            try {
                mData.child("KhachHang").child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        try {
                            Customer user = dataSnapshot.getValue(Customer.class);
                            Log.d("abcxyz", String.valueOf(user));
                            int phanquyen = user.getPhanQuyen();
                            if (phanquyen == 0) {
                                Intent i = new Intent(LoginActivity.this, BottomNavigation.class);
                                startActivity(i);
                                finish();
                                dialog.hide();
                            }
                        } catch (Exception e) {
                            Intent i = new Intent(LoginActivity.this, BottomNavigationStore.class);
                            startActivity(i);
                            finish();
                            dialog.hide();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }catch (Exception e){
                mData.child("CuaHang").child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Store user = dataSnapshot.getValue(Store.class);
                        Log.d("abcxyz", String.valueOf(user));
                        Intent i = new Intent(LoginActivity.this, BottomNavigationStore.class);
                        startActivity(i);
                        finish();
                        dialog.hide();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

        }else {
            dialog.hide();
        }

        btnLogin.setOnClickListener(v -> {
            if(validateEmail() & validatePassword()){
                final String email1 = edtemail.getText().toString();
                final String pass1 = edtpassword.getText().toString();
                pb.setVisibility(View.VISIBLE);
                if (email1.equals("admin@gmail.com")&&pass1.equals("admin1")){
                    Intent i = new Intent(LoginActivity.this, BottomNavigationAdmin.class);
                    startActivity(i);
                    finish();
                }else {


                    mAuth.signInWithEmailAndPassword(email1, pass1)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công",
                                                Toast.LENGTH_SHORT).show();
                                        final String userId = fAuth.getCurrentUser().getUid();
                                        try {
                                            mData.child("KhachHang").child(userId).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    try {
                                                        Customer user = dataSnapshot.getValue(Customer.class);
                                                        Log.d("abcxyz", String.valueOf(user));
                                                        int phanquyen = user.getPhanQuyen();
                                                        if (phanquyen == 0) {
                                                            CheckGpsStatus();
                                                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                            DatabaseReference databaseReference1 = firebaseDatabase.getReference("KhachHang");
                                                            databaseReference1.child(userId).child("userPass").setValue(pass1);
                                                            Intent i = new Intent(LoginActivity.this, BottomNavigation.class);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                    } catch (Exception e) {
                                                        Intent i = new Intent(LoginActivity.this, BottomNavigationStore.class);
                                                        startActivity(i);
                                                        finish();
                                                    }


                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        } catch (Exception e) {
                                            mData.child("CuaHang").child(userId).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    Store user = dataSnapshot.getValue(Store.class);
                                                    Log.d("abcxyz", String.valueOf(user));
                                                    Intent i = new Intent(LoginActivity.this, BottomNavigationStore.class);
                                                    startActivity(i);
                                                    finish();

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }


                                    } else {
                                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại",
                                                Toast.LENGTH_SHORT).show();
                                        pb.setVisibility(View.GONE);
                                    }
                                }
                            });
                }

            }
        });
        txtSignUp.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(i);
        });
    }
    //check GPS
    public void CheckGpsStatus() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (GpsStatus) {
            Intent intent = new Intent(LoginActivity.this, BottomNavigation.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Vui lòng bật vị trí thiết bị để tiếp tục ứng dụng!", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.INVISIBLE);
        }
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
        private boolean validatePassword() {
            if (edtpassword.getText().toString().trim().isEmpty()) {
                inputPass.setError("Bắt buộc nhập mật khẩu");
                requestFocus(edtpassword);
                return false;
            }else if(edtpassword.getText().toString().length() < 6){
                inputPass.setError("Mật khẩu phải là 6 ký tự");
                requestFocus(edtpassword);
                return false;
            }else {
                inputPass.setErrorEnabled(false);
            }
            return true;
        }
        private boolean validateEmail() {
            if (edtemail.getText().toString().trim().isEmpty()) {
                inputEmail.setError("Bắt buộc nhập mật Email");
                requestFocus(edtemail);
                return false;
            } else {
                String emailId = edtemail.getText().toString();
                Boolean  isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
                if (!isValid) {
                    inputEmail.setError("Sai định dạng Email, ex: abc@example.com");
                    requestFocus(edtemail);
                    return false;
                } else {
                    inputEmail.setErrorEnabled(false);
                }
            }
            return true;
        }
    private class ValidationTextWatcher implements TextWatcher {

        private View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edtEmail:
                    validateEmail();
                    break;
                case R.id.edtPassword:
                    validatePassword();
                    break;
            }
        }
    }
    public void init(){
        btnLogin = findViewById(R.id.btnSignIn);
        txtSignUp = findViewById(R.id.txtSignUp);
        edtemail = findViewById(R.id.edtEmail);
        edtpassword = findViewById(R.id.edtPassword);
        inputEmail = findViewById(R.id.inputEmail);
        inputPass = findViewById(R.id.inputPass);
        pb = findViewById(R.id.pbLogin);
    }
}