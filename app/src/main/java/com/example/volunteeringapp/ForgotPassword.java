package com.example.volunteeringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPassword extends AppCompatActivity {
    String verifyCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("ForgotPasswordPool").build();
        ExecutorService fixedThreadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 0L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), threadFactory);

        final Data app = (Data) getApplication();

        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                verifyCode = "" + randomChar() + randomChar() + randomChar() + randomChar() + randomChar();
                String subject = "Verify code (change password)";
                String content = "Here is the verify code for your password change: " + verifyCode;

                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", 587);
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.auth", true);
                MyAuthenticator auth = new MyAuthenticator();
                Session session = Session.getInstance(props, auth);

                try {
                    MimeMessage message = new MimeMessage(session);
                    Address addressFrom = new InternetAddress("qianmeng6463@gmail.com");
                    Address addressTo = new InternetAddress(Objects.requireNonNull(app.getUser().get("email")).toString());

                    message.setFrom(addressFrom);
                    message.setRecipient(Message.RecipientType.TO, addressTo);

                    message.setSubject(subject);
                    message.setText(content);

                    Transport.send(message);
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }
        });

        final EditText verifyCodeText = findViewById(R.id.verifyCode);
        Button submit = findViewById(R.id.submitVerifyCode);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyCodeText.getText().toString().equals(verifyCode)) {
                    Intent intent = new Intent(ForgotPassword.this, ChangePassword.class);
                    intent.putExtra("source", 1);
                    startActivity(intent);
                } else {
                    Toast.makeText(ForgotPassword.this, "Wrong verify code!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public char randomChar() {
        int ascii = (int) (Math.random() * 43 + 48);
        while (ascii < 48 || ascii > 57 && ascii < 65 || ascii > 90) {
            ascii = (int) (Math.random() * 43 + 48);
        }

        return (char) ascii;
    }
}

class MyAuthenticator extends Authenticator {
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        String username = "qianmeng6463@gmail.com";
        String password = "yan15937";
        return new PasswordAuthentication(username, password);
    }
}
