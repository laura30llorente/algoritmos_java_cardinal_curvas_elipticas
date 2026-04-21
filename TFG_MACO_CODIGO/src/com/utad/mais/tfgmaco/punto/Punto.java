package com.utad.mais.tfgmaco.punto;

import java.math.BigInteger;
import com.utad.mais.tfgmaco.curva.*;

public abstract class Punto {
	// Atributos
	protected CurvaElipticaFp curva;
	
	// Constructor
	public Punto(CurvaElipticaFp curva) {
		this.curva = curva;
	}
	
	// Metodo getter para obtener la curva a la que pertenece el punto
	public CurvaElipticaFp getCurva() {
		return curva;
	}
	
	// Otros metodos
	public abstract BigInteger getX();
	public abstract BigInteger getY();
	public abstract boolean esPuntoInfinito();
}
