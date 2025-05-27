package com.example.appreserva.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SucessoAgendamentoActivity extends AppCompatActivity {

    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sucesso_agendamento);

        btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminAgendamentosActivity.class);
            startActivity(intent);
        });
    }
}