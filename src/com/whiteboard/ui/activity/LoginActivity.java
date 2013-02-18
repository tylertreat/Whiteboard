package com.whiteboard.ui.activity;

import android.content.Intent;
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
import com.clarionmedia.infinitum.orm.Session;
import com.clarionmedia.infinitum.orm.context.InfinitumOrmContext;
import com.clarionmedia.infinitum.orm.context.InfinitumOrmContext.SessionType;
import com.clarionmedia.infinitum.orm.criteria.criterion.Conditions;
import com.whiteboard.R;
import com.whiteboard.auth.SessionManager;
import com.whiteboard.model.User;
import com.whiteboard.util.NetworkUtils;

@InjectLayout(R.layout.activity_login)
public class LoginActivity extends InfinitumActivity {

    private static final int REQUEST_REGISTER = 1;

    @InjectView(R.id.email_field)
    private EditText mEmailField;

    @InjectView(R.id.password_field)
    private EditText mPasswordField;

    @InjectView(R.id.login_button)
    @Bind("loginClicked")
    private Button mLoginButton;

    @InjectView(R.id.register_button)
    @Bind("registerClicked")
    private Button mRegisterButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkUtils.getLocalIpAddress();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_REGISTER) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, WhiteboardActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void loginClicked(View view) {
        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email))
            return;
        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password))
            return;
        Session session = getInfinitumContext(InfinitumOrmContext.class).getSession(SessionType.SQLITE);
        session.open();
        User user = session.createCriteria(User.class).add(Conditions.eq("mEmail", email)).unique();
        if (!user.getPassword().equals(password)) {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_LONG).show();
            return;
        }
        SessionManager.setUser(user);
        Intent intent = new Intent(this, WhiteboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerClicked(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, REQUEST_REGISTER);
    }

}