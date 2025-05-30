package com.example.reservaplusapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminAgendamentosActivity extends AppCompatActivity {

    RecyclerView solicitacoesRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_agendamentos);

        solicitacoesRecycler = findViewById(R.id.solicitacoesRecycler);
        solicitacoesRecycler.setLayoutManager(new LinearLayoutManager(this));
        // Exemplo de chamada de detalhe (poderia usar um adapter)
        solicitacoesRecycler.setOnClickListener(v -> {
            Intent intent = new Intent(this, SolicitacaoDetalheActivity.class);
            startActivity(intent);
        });
    }
}