package com.example.appreserva.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SolicitacaoDetalheActivity extends AppCompatActivity {

    Button btnAprovar, btnRecusar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solicitacao_detalhe);

        btnAprovar = findViewById(R.id.btnAprovar);
        btnRecusar = findViewById(R.id.btnRecusar);

        btnAprovar.setOnClickListener(v -> {
            Intent intent = new Intent(this, SucessoAgendamentoActivity.class);
            startActivity(intent);
        });

        btnRecusar.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgendamentoExcluidoActivity.class);
            startActivity(intent);
        });
    }
}