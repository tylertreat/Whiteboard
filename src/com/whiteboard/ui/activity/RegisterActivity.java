package com.whiteboard.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.clarionmedia.infinitum.activity.InfinitumActivity;
import com.clarionmedia.infinitum.activity.annotation.Bind;
import com.clarionmedia.infinitum.activity.annotation.InjectLayout;
import com.clarionmedia.infinitum.activity.annotation.InjectView;
import com.clarionmedia.infinitum.di.annotation.Autowired;
import com.whiteboard.R;
import com.whiteboard.auth.SessionManager;
import com.whiteboard.model.User;
import com.whiteboard.service.UserService;

@InjectLayout(R.layout.activity_register)
public class RegisterActivity extends InfinitumActivity {

    @InjectView(R.id.email_field)
    private EditText mEmailField;

    @InjectView(R.id.name_field)
    private EditText mNameField;

    @InjectView(R.id.password_field)
    private EditText mPasswordField;

    @InjectView(R.id.confirm_password_field)
    private EditText mConfirmPasswordField;

    @InjectView(R.id.register_button)
    @Bind("registerClicked")
    private Button mRegisterButton;

    @InjectView(R.id.cancel_button)
    @Bind("cancelClicked")
    private Button mCancelButton;

    @Autowired
    private UserService mUserService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void registerClicked(View view) {
        String email = mEmailField.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_LONG).show();
            return;
        }
        String name = mNameField.getText().toString();
        String password = mPasswordField.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_LONG).show();
            return;
        }
        String confirmPassword = mConfirmPasswordField.getText().toString().trim();
        if (!confirmPassword.equals(password)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }
        User check = mUserService.getUserByEmail(email);
        if (check != null) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_LONG).show();
            return;
        }
        User user = new User();
        user.setEmail(email);
        if (TextUtils.isEmpty(name))
            user.setName(name);
        user.setPassword(password);
        mUserService.saveUser(user);
        SessionManager.setUser(user);
        setResult(RESULT_OK);
        finish();
    }

    private void cancelClicked(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

}