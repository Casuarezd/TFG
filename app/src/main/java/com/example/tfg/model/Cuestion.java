package com.example.tfg.model;

import java.util.ArrayList;

public class Cuestion {

	private String pregunta;
	private ArrayList<String> respuestas;
	private String correcta;
	private boolean contestada;
	
	
	
	public Cuestion(String pregunta, String correcta) {
		this.pregunta = pregunta;
		this.respuestas = new ArrayList<String>();
		this.correcta = correcta;
	}

	public Cuestion() {

		pregunta = " ";
		respuestas = new ArrayList<String>();
		correcta = " ";
	}

	public String getPregunta() {
		return pregunta;
	}

	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}

	public ArrayList<String> getRespuestas() {
		return respuestas;
	}

	public void setRespuestas(ArrayList<String> respuestas) {
		this.respuestas = respuestas;
	}

	public String getCorrecta() {
		return correcta;
	}

	public void setCorrecta(String correcta) {
		this.correcta = correcta;
	}

	public Boolean getContestada() {
		return contestada;
	}

	public void setContestada(Boolean contestada) {
		this.contestada = contestada;
	}

	@Override
	public String toString() {
		return "Cuestion [pregunta=" + pregunta + ", respuestas=" + respuestas + ", correcta=" + correcta + ", contestada= "+ contestada + "]";
	}
	
	
	
	
	
}