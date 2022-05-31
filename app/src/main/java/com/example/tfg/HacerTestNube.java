package com.example.tfg;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.model.Cuestion;
import com.example.tfg.model.putPDF;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.io.util.FileUtil;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class HacerTestNube extends AppCompatActivity {
    private TextView tView;
    private ArrayList<Cuestion> test = new ArrayList<Cuestion>();
    private ArrayList<Cuestion> incorrectas;
    private Cuestion[] preguntas;
    private ArrayList<String> lineas;
    private int preguntaActual = 0;
    private int[] respuestasUser;

    private int bien;
    private int mal;
    private int nc;

    private int lineafallo=0;

    RadioGroup rgroup;
    LinearLayout ll1;
    LinearLayout ll2;
    Button btnComenzar;
    TextView tvp;
    Button btnAtras;
    Button btnSiguiente;
    Button btnVerificar;

    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_test2);

//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);

        Bundle extras = getIntent().getExtras();
        String ruta = extras.getString("ruta");
        String imagen = extras.getString("imagen");
        String titulo = extras.getString("titulo");

        rgroup = (RadioGroup) findViewById(R.id.radioGrupo);
        ll1 = (LinearLayout) findViewById(R.id.linearLayout1);
        ll2 = (LinearLayout) findViewById(R.id.linearLayout2);
        btnComenzar = (Button) findViewById(R.id.btn_Comenzar);
        tvp = (TextView) findViewById(R.id.id_pregunta);
        btnAtras = (Button) findViewById(R.id.btn_Atras);
        btnSiguiente = (Button) findViewById(R.id.btn_Siguiente);
        btnVerificar = (Button) findViewById(R.id.btn_Verificar);

        ll1.setVisibility(View.INVISIBLE);
        ll2.setVisibility(View.INVISIBLE);

        // tView.setText(titulo);
        /*
        try {
           pruebas(ruta);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tView.setText(lineas.toString());

         */
        String texto = "";
        String textFromPage = "";
        try {
            File archivo = new File(ruta);
            InputStream input = new FileInputStream(archivo);


            PdfReader reader = new PdfReader(input);

            int nPag = reader.getNumberOfPages();
            System.out.println("NUMERO DE PAGINAS: " + nPag);
            for(int i=1; i<=nPag;i++){
                if(i!=1){
                    textFromPage = textFromPage +"\n" + PdfTextExtractor.getTextFromPage(reader, i);
                }else{
                    textFromPage = textFromPage + PdfTextExtractor.getTextFromPage(reader, i);
                }

                System.out.println("CONTENIDO PAGINA " + i +": "+ textFromPage);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Excepcion cargando pdf" + e.getStackTrace());
        }

        System.out.println(textFromPage);

        String[] lista = textFromPage.split("\n");
        for (int i = 0; i < lista.length; i++) {
            System.out.println("Numero " + i + ": " + lista[i]);
        }

        if(comprobarFormato1(lista)){
            pasarArrayVector(test);
        }else{
            Toast.makeText(this, "Error de formato en linea "+ lineafallo, Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    public void pasarArrayVector(ArrayList<Cuestion> array) {
        int nPreguntas = 0;
        preguntas = new Cuestion[array.size()];
        Collections.shuffle(array);
        Iterator<Cuestion> it = array.iterator();
        while (it.hasNext()) {
            Cuestion aux = (Cuestion) it.next();
            preguntas[nPreguntas] = aux;
            nPreguntas++;
        }
        respuestasUser = new int[preguntas.length];
        for (int i = 0; i < respuestasUser.length; i++) {
            respuestasUser[i] = -1;
        }
    }


    public void comenzarTest(View view) {
        ll1.setVisibility(View.VISIBLE);
        ll2.setVisibility(View.VISIBLE);
        btnComenzar.setVisibility(View.INVISIBLE);
        btnComenzar.setEnabled(false);

        bien=0;
        mal=0;
        nc=preguntas.length;

        cambiarPregunta();
    }

    public void cambiarPregunta() {
        tvp.setText(preguntas[preguntaActual].getPregunta());

        rgroup.removeAllViews();

        for (int i = 0; i < preguntas[preguntaActual].getRespuestas().size(); i++) {
            RadioButton aux = new RadioButton(this);
            aux.setId(i);
            aux.setText(preguntas[preguntaActual].getRespuestas().get(i));
            aux.setTextSize(20);
            rgroup.addView(aux);
        }

        if (preguntaActual == 0) {
            btnAtras.setVisibility(View.INVISIBLE);
            btnAtras.setEnabled(false);
        } else {
            btnAtras.setVisibility(View.VISIBLE);
            btnAtras.setEnabled(true);
        }

        if (preguntaActual == preguntas.length - 1) {
            btnSiguiente.setVisibility(View.INVISIBLE);
            btnSiguiente.setEnabled(false);
        } else {
            btnSiguiente.setVisibility(View.VISIBLE);
            btnSiguiente.setEnabled(true);
        }

        //Comprobamos si la pregunta actual ya ha sido respondida o no
        if (preguntas[preguntaActual].getContestada()) {
            String aux = preguntas[preguntaActual].getCorrecta();
            String auxBueno = aux.replace(" ", "");
            int respuesta = LetraNumero(auxBueno);
            for (int i = 0; i < preguntas[preguntaActual].getRespuestas().size(); i++) {
                RadioButton auxiliar = (RadioButton) rgroup.getChildAt(i);
                auxiliar.setEnabled(false);
                //Coloreamos de rojo la respuesta del user
                if (i == respuestasUser[preguntaActual]) {
                    auxiliar.setBackgroundColor(-65536);
                }// Coloreamos de verde la respuesta correcta, si es la del user se sobreescribe.
                if (i == respuesta) {
                    auxiliar.setBackgroundColor(-16711936);
                }
            }
            btnVerificar.setEnabled(false);
            btnVerificar.setVisibility(View.INVISIBLE);
        } else {
            btnVerificar.setEnabled(true);
            btnVerificar.setVisibility(View.VISIBLE);
        }

    }

    public void verificarPregunta(View view) {
        int respuesta = -1;
        String text = "";
        boolean contestada = false;
        //Comprobamos si hay algun radio button activado
        for (int i = 0; i < preguntas[preguntaActual].getRespuestas().size(); i++) {
            RadioButton aux = (RadioButton) rgroup.getChildAt(i);
            if (aux.isChecked()) {
                contestada = true;
                respuesta = i;
                respuestasUser[preguntaActual] = respuesta;
                preguntas[preguntaActual].setContestada(true);
            }
        }
        String aux = preguntas[preguntaActual].getCorrecta();
        String auxBueno = aux.replace(" ", "");
        int numeroCorrecta = LetraNumero(auxBueno);
        boolean acierto = false;
        if (contestada) {
            if (respuesta == numeroCorrecta) {
                acierto = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(HacerTestNube.this);
                builder.setTitle("Respuesta correcta");
                builder.setMessage(preguntas[preguntaActual].getRespuestas().get(respuesta))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).show();
                bien++;
                nc--;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(HacerTestNube.this);
                builder.setTitle("Respuesta incorrecta");
                builder.setMessage("La respuesta correcta es : \n" + preguntas[preguntaActual].getRespuestas().get(numeroCorrecta))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).show();
                mal++;
                nc--;
            }
        } else {

        }

        //RECORREMOS LOS RADIOBUTTONS Y CAMBIAMOS DE COLOR EN BASE A LA RESPUESTA
        for (int i = 0; i < preguntas[preguntaActual].getRespuestas().size(); i++) {
            RadioButton auxiliar = (RadioButton) rgroup.getChildAt(i);
            auxiliar.setEnabled(false);
            if (auxiliar.isChecked() && acierto) {
                auxiliar.setBackgroundColor(-16711936);
            } else if (auxiliar.isChecked() && !acierto) {
                auxiliar.setBackgroundColor(-65536);
            }
            if (i == numeroCorrecta) {
                auxiliar.setBackgroundColor(-16711936);
            }
        }
        btnVerificar.setEnabled(false);
        btnVerificar.setVisibility(View.INVISIBLE);
    }


    public void siguientePregunta(View view) {
        preguntaActual++;
        cambiarPregunta();
    }

    public void anteriorPregunta(View view) {
        preguntaActual--;
        cambiarPregunta();
    }

    public void terminarTest(View view) {
        Intent intent = new Intent(this, Resultados.class);
        intent.putExtra("total", preguntas.length);
        intent.putExtra("correctas", bien);
        intent.putExtra("incorrectas", mal);
        intent.putExtra("noContestadas", nc);
        startActivity(intent);
    }

    public int LetraNumero(String respuesta) {
        int r = -1;
        switch (respuesta) {
            case "a":
                r = 0;
                break;

            case "b":
                r = 1;
                break;

            case "c":
                r = 2;
                break;
            case "d":
                r = 3;
                break;
            case "e":
                r = 4;
                break;
            case "f":
                r = 5;
                break;
            case "g":
                r = 6;
                break;
            case "h":
                r = 7;
                break;
            default:
                r = Integer.parseInt(respuesta);
                break;
        }
        return r;
    }


    public boolean comprobarFormato1(String[] list){
        int i = 0;
        while (i < list.length) {
            Cuestion c = new Cuestion();
            boolean fin = false;

            while (!fin) {

                if(list[i] == null || list[i]=="" || list[i].length()<3){
                    lineafallo=i+1;
                    return false;
                }
                //Compruebo si el primer caracter es un guion
                //Compruebo si las respuestas empiezan por una letra valida
                //Compruebo si la respueta empieza por R y es valida
                if(i==0 && list[i].charAt(0)!='-'){
                    lineafallo=i+1;
                    return false;
                }

                if(list[i].charAt(0)=='-'){
                    c.setPregunta(list[i]);
                }
                else if(list[i].charAt(0) == 'R') {
                    String reducido = list[i].substring(2);
                    char letra = reducido.charAt(0);
                    if(isletter(letra)){
                        c.setCorrecta(reducido);
                        fin = true;
                    }else{
                        lineafallo=i+1;
                        return false;
                    }
                } else {
                    String reducido = list[i].substring(2);
                    char letra = list[i].charAt(0);
                    if(isletter(letra)){
                        c.getRespuestas().add(reducido);
                    }else{
                        lineafallo=i+1;
                        return false;
                    }

                }
                i++;
            }
            c.setContestada(false);
            test.add(c);
        }

        return true;
    }

    public boolean comprobarFormato2(String[] list){
        int i = 0;
        while (i < list.length) {
            if(list[i] == null || list[i]==""){
                return false;
            }
            Cuestion c = new Cuestion();
            boolean fin = false;
            boolean valido=true;

            while (!fin) {

                //Compruebo si el primer caracter es un guion
                //Compruebo si las respuestas empiezan por una letra valida
                //Compruebo si la respueta empieza por R y es valida
                if(i==0 && list[i].charAt(0)!='-'){
                    return false;
                }

                if(list[i].charAt(0)=='-'){
                    c.setPregunta(list[i]);
                }
                else if(list[i].charAt(0) == 'R') {
                    String reducido = list[i].substring(2);
                    char letra = reducido.charAt(0);
                    if(isletter(letra)){
                        c.setCorrecta(reducido);
                        fin = true;
                    }else{
                        return false;
                    }
                } else {
                    if(list[i].charAt(0)==' '){
                        valido=false;
                    }else {
                        String reducido = list[i].substring(2);
                        char letra = list[i].charAt(0);
                        if (isletter(letra)) {
                            c.getRespuestas().add(reducido);
                        } else {
                            return false;
                        }
                    }

                }
                i++;
            }
            if(valido){
                c.setContestada(false);
                test.add(c);
            }
        }

        return true;
    }

    public boolean isletter(char aux){
        if(aux == 'a'){
            return true;
        }
        if(aux == 'b'){
            return true;
        }
        if(aux == 'c'){
            return true;
        }
        if(aux == 'd'){
            return true;
        }
        if(aux == 'e'){
            return true;
        }
        if(aux == 'f'){
            return true;
        }
        if(aux == 'g'){
            return true;
        }
        return false;
    }
}
