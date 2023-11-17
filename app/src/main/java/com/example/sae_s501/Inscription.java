package com.example.sae_s501;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import com.example.sae_s501.model.Utilisateur;
import com.example.sae_s501.retrofit.RetrofitService;
import com.example.sae_s501.retrofit.UserService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Inscription extends AppCompatActivity {


    private EditText editTextPseudo;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmationPassword;
    private Button buttonEnvoyer;

    private UserService userService;
    private RetrofitService retrofitService = new RetrofitService();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);
        editTextPseudo = findViewById(R.id.editpseudo);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmationPassword = findViewById(R.id.editTextPasswordConfirm);
        buttonEnvoyer = findViewById(R.id.btnInscription);


        /* creation requete */
        userService = retrofitService.getRetrofit().create(UserService.class);
        buttonEnvoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Récupération des données saisies dans les champs de texte
                String pseudo = editTextPseudo.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmationPassword = editTextConfirmationPassword.getText().toString();

                // mot de passe critères

                // Vérifier si le mot de passe et le mot de passe de confirmation correspondent
                if (!password.equals(confirmationPassword)) {
                    showToast("Les mots de passe ne correspondent pas.");
                    return;
                }

                if (password.length() < 8) {
                    showToast("Le mot de passe doit contenir au moins 8 caractères.");
                    return;
                }

                if (!MDPCharacterSpe(password)) {
                    showToast("Le mot de passe doit contenir au moins un caractère spécial.");
                    return;
                }

                if (!MotDePasseMaj(password)) {
                    showToast("Le mot de passe doit contenir au moins une majuscule.");
                    return;
                }
                if (!MDPChiffre(password)) {
                    showToast("Le mot de passe doit contenir au moins un chiffre.");
                    return;
                }



                /*Utilisateur user = new Utilisateur(pseudo, email, password);
                Call<Utilisateur> call = userService.registerUser(user);*/

                /* envoie requete */
                Call<Utilisateur> call = userService.registerUser(pseudo, email, password);

                call.enqueue(new Callback<Utilisateur>() {
                    /* resultat de la requete */
                    @Override
                    public void onResponse(Call<Utilisateur> call, Response<Utilisateur> response) {
                        if (response.isSuccessful()) {
                            showToast("Inscription réussie !");
                            Intent intent = new Intent(Inscription.this, Connexion.class);
                            startActivity(intent);
                        } else {
                            showToast("Échec de l'inscription !");
                            recreate();
                        }
                    }
                    @Override
                    public void onFailure(Call<Utilisateur> call, Throwable t) {
                        showToast("Erreur : " + t.getMessage());
                    }
                });
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Vérifier si le mot de passe contient au moins un caractère spécial
    private boolean MDPCharacterSpe(String password) {
        String specialCharacters = "!@#$%^&*()-_=+[]{}|;:'\",.<>/?";
        for (char specialChar : specialCharacters.toCharArray()) {
            if (password.contains(String.valueOf(specialChar))) {
                return true;
            }
        }
        return false;
    }

    // Vérifier si le mot de passe contient une majuscule
    private boolean MotDePasseMaj(String password) {
        for (char character : password.toCharArray()) {
            if (Character.isUpperCase(character)) {
                return true;
            }
        }
        return false;
    }
    // Vérifier si le mot de passe contient au moins un chiffre
    private boolean MDPChiffre(String password) {
        for (char character : password.toCharArray()) {
            if (Character.isDigit(character)) {
                return true;
            }
        }
        return false;
    }
}
